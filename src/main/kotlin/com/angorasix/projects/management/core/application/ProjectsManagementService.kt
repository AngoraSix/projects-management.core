package com.angorasix.projects.management.core.application

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.projects.management.core.domain.management.BylawWellknownScope
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.domain.management.ProjectManagementRepository
import com.angorasix.projects.management.core.infrastructure.applicationevents.ContributorRegisteredApplicationEvent
import com.angorasix.projects.management.core.infrastructure.applicationevents.ProjectManagementCreatedApplicationEvent
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import kotlinx.coroutines.flow.Flow
import org.springframework.context.ApplicationEventPublisher
import reactor.core.publisher.Mono

/**
 *
 *
 * @author rozagerardo
 */
class ProjectsManagementService(
    private val repository: ProjectManagementRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    suspend fun findSingleProjectManagement(id: String): ProjectManagement? = repository.findById(id)

    suspend fun findSingleProjectManagementByProjectId(projectId: String): ProjectManagement? = repository.findByProjectId(projectId)

    fun findProjectManagements(filter: ListProjectsManagementFilter): Flow<ProjectManagement> = repository.findUsingFilter(filter)

    suspend fun createProjectManagement(
        projectManagement: ProjectManagement,
        requestingContributor: A6Contributor,
    ): ProjectManagement =
        repository.save(projectManagement).also { saved ->
            applicationEventPublisher.publishEvent(ProjectManagementCreatedApplicationEvent(saved, requestingContributor))
        }

    suspend fun updateProjectManagement(
        id: String,
        updateData: ProjectManagement,
        requestingContributor: A6Contributor,
    ): ProjectManagement? {
        val projectManagementToUpdate =
            repository.findForContributorUsingFilter(
                ListProjectsManagementFilter(
                    listOf(updateData.projectId),
                    setOf(requestingContributor.contributorId),
                    listOf(id),
                ),
                requestingContributor,
            )

        return projectManagementToUpdate?.updateWithData(updateData)?.let { repository.save(it) }
    }

    private fun ProjectManagement.updateWithData(other: ProjectManagement): ProjectManagement {
        this.status = other.status
        return this
    }

    /**
     * Method to check if the contributor is admin of a single [ProjectManagement] from an id.
     *
     * @param projectManagementId [ProjectManagement] id
     * @return a [Mono] with the persisted [ProjectManagement]
     */
    suspend fun administeredProjectManagement(
        projectManagementId: String,
        simpleContributor: A6Contributor,
    ): ProjectManagement? =
        repository.findForContributorUsingFilter(
            ListProjectsManagementFilter(
                null,
                setOf(simpleContributor.contributorId),
                setOf(projectManagementId),
            ),
            simpleContributor,
        )

    suspend fun processManagementMemberJoined(
        projectManagementId: String,
        joinedMemberContributorId: String,
        requestingContributor: A6Contributor,
    ) = repository
        .findById(projectManagementId)
        ?.takeIf {
            it.constitution.bylaws[BylawWellknownScope.OWNERSHIP_IS_A6MANAGED.name]?.definition == true ||
                (it.constitution.bylaws[BylawWellknownScope.FINANCIAL_CURRENCIES.name]?.definition as Collection<String>).isNotEmpty()
        }?.let {
            applicationEventPublisher.publishEvent(
                ContributorRegisteredApplicationEvent(
                    projectManagement = it,
                    registeredContributorId = joinedMemberContributorId,
                    participatesInOwnership =
                        it.constitution.bylaws[BylawWellknownScope.OWNERSHIP_IS_A6MANAGED.name]?.definition as Boolean?
                            ?: false,
                    managementFinancialCurrencies =
                        (it.constitution.bylaws[BylawWellknownScope.FINANCIAL_CURRENCIES.name]?.definition as List<String>?)?.toSet()
                            ?: emptySet(),
                    requestingContributor = requestingContributor,
                ),
            )
        }
}
