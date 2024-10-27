package com.angorasix.projects.management.core.application

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.domain.management.ProjectManagementRepository
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import kotlinx.coroutines.flow.Flow
import reactor.core.publisher.Mono

/**
 *
 *
 * @author rozagerardo
 */
class ProjectsManagementService(private val repository: ProjectManagementRepository) {

    suspend fun findSingleProjectManagement(id: String): ProjectManagement? =
        repository.findById(id)

    suspend fun findSingleProjectManagementByProjectId(projectId: String): ProjectManagement? =
        repository.findByProjectId(projectId)

    fun findProjectManagements(filter: ListProjectsManagementFilter): Flow<ProjectManagement> =
        repository.findUsingFilter(filter)

    suspend fun createProjectManagement(projectManagement: ProjectManagement): ProjectManagement =
        repository.save(projectManagement)

    suspend fun updateProjectManagement(
        id: String,
        updateData: ProjectManagement,
        requestingContributor: SimpleContributor,
    ): ProjectManagement? {
        val projectManagementToUpdate = repository.findForContributorUsingFilter(
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
        simpleContributor: SimpleContributor,
    ): ProjectManagement? = repository.findForContributorUsingFilter(
        ListProjectsManagementFilter(
            null,
            setOf(simpleContributor.contributorId),
            setOf(projectManagementId)
        ),
        simpleContributor,
    )
}
