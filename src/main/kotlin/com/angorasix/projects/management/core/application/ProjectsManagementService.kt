package com.angorasix.projects.management.core.application

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.commons.domain.projectmanagement.accounting.A6_OWNERSHIP_CAPS_CURRENCY_ID
import com.angorasix.commons.infrastructure.intercommunication.tasks.TasksClosed
import com.angorasix.projects.management.core.domain.management.BylawWellknownScope
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.domain.management.ProjectManagementRepository
import com.angorasix.projects.management.core.infrastructure.applicationevents.ContributorRegisteredApplicationEvent
import com.angorasix.projects.management.core.infrastructure.applicationevents.ManagementTasksClosedApplicationEvent
import com.angorasix.projects.management.core.infrastructure.applicationevents.ProjectManagementCreatedApplicationEvent
import com.angorasix.projects.management.core.infrastructure.domain.DEFAULT_BYLAW_VALUE_TASKS_DISTRIBUTION_RULES_REGULAR_DEFAULT_DURATION
import com.angorasix.projects.management.core.infrastructure.domain.DEFAULT_BYLAW_VALUE_TASKS_DISTRIBUTION_RULES_STARTUP_DEFAULT_DURATION
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import kotlinx.coroutines.flow.Flow
import org.springframework.context.ApplicationEventPublisher
import reactor.core.publisher.Mono
import java.time.Duration

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

    fun findProjectManagements(
        filter: ListProjectsManagementFilter,
        requestingContributor: A6Contributor? = null,
    ): Flow<ProjectManagement> = repository.findUsingFilter(filter, requestingContributor)

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
            repository.findSingleUsingFilter(
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
        repository.findSingleUsingFilter(
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
            it.constitution.bylaws[BylawWellknownScope.OWNERSHIP_IS_A6MANAGED.value]?.definition == true ||
                (it.constitution.bylaws[BylawWellknownScope.FINANCIAL_CURRENCIES.value]?.definition as Collection<String>).isNotEmpty()
        }?.let {
            applicationEventPublisher.publishEvent(
                ContributorRegisteredApplicationEvent(
                    projectManagement = it,
                    registeredContributorId = joinedMemberContributorId,
                    ownershipCurrency = extractOwnershipCurrency(it),
                    managementFinancialCurrencies =
                        (it.constitution.bylaws[BylawWellknownScope.FINANCIAL_CURRENCIES.value]?.definition as List<String>?)?.toSet()
                            ?: emptySet(),
                    requestingContributor = requestingContributor,
                ),
            )
        }

    private fun extractOwnershipCurrency(it: ProjectManagement) =
        if (it.constitution.bylaws[BylawWellknownScope.OWNERSHIP_IS_A6MANAGED.value]?.definition as Boolean? == true) {
            A6_OWNERSHIP_CAPS_CURRENCY_ID
        } else {
            null
        }

    suspend fun processManagementTasksClosed(
        projectManagementId: String,
        closedTasks: List<TasksClosed.TaskClosed>,
        requestingContributor: A6Contributor,
    ) = repository
        .findById(projectManagementId)
        ?.let { pm ->
            val ownershipBylawDef =
                pm.constitution.bylaws[BylawWellknownScope.OWNERSHIP_IS_A6MANAGED.value]?.definition
                    as? Boolean

            val financialCurrenciesBylawDef =
                pm.constitution.bylaws[BylawWellknownScope.FINANCIAL_CURRENCIES.value]?.definition
                    as? Collection<String>

            // only proceed if either “ownership” is enabled or there is at least one financial currency
            if (ownershipBylawDef == true || !(financialCurrenciesBylawDef.orEmpty().isEmpty())) {
                // compute ownershipCurrency (or null)
                val ownershipCurrency: String? =
                    ownershipBylawDef
                        .takeIf { it == true }
                        ?.let { A6_OWNERSHIP_CAPS_CURRENCY_ID }

                // turn whatever was in the “financial currencies” bylaw into a Set<String>
                val financialCurrencies: Set<String> =
                    (financialCurrenciesBylawDef ?: emptyList()).toSet()

                // now build a single set of “all currencies that need rules,” omitting null
                val allCurrencies: Set<String> =
                    financialCurrencies +
                        setOfNotNull(ownershipCurrency)

                // build one map from currency → TasksDistributionRules
                val currencyDistributionRules: Map<String, ManagementTasksClosedApplicationEvent.ApplicationEventTasksDistributionRules> =
                    allCurrencies.associateWith { currency ->
                        ManagementTasksClosedApplicationEvent.ApplicationEventTasksDistributionRules(
                            startupDefaultDuration = extractStartupCurrencyDistributionRule(pm, currency),
                            regularDefaultDuration = extractRegularCurrencyDistributionRule(pm, currency),
                        )
                    }

                applicationEventPublisher.publishEvent(
                    ManagementTasksClosedApplicationEvent(
                        projectManagement = pm,
                        collection = closedTasks,
                        ownershipCurrency = ownershipCurrency,
                        managementFinancialCurrencies = financialCurrencies,
                        requestingContributor = requestingContributor,
                        currencyDistributionRules = currencyDistributionRules,
                    ),
                )
            }
        }
}

private fun extractStartupCurrencyDistributionRule(
    projectManagement: ProjectManagement,
    currency: String,
): Duration =
    Duration.parse(
        projectManagement.constitution.bylaws[
            BylawWellknownScope.CURRENCYBASED_STARTUP_RETRIBUTION_PERIOD.value.replace(
                "{CURRENCY}",
                currency,
            ),
        ]?.definition as String? ?: DEFAULT_BYLAW_VALUE_TASKS_DISTRIBUTION_RULES_STARTUP_DEFAULT_DURATION,
    )

private fun extractRegularCurrencyDistributionRule(
    projectManagement: ProjectManagement,
    currency: String,
): Duration =
    Duration.parse(
        projectManagement.constitution.bylaws[
            BylawWellknownScope.CURRENCYBASED_REGULAR_RETRIBUTION_PERIOD.value.replace(
                "{CURRENCY}",
                currency,
            ),
        ]?.definition as String? ?: DEFAULT_BYLAW_VALUE_TASKS_DISTRIBUTION_RULES_REGULAR_DEFAULT_DURATION,
    )
