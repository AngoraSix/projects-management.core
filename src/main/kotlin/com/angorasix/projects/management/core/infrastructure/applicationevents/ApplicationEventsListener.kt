package com.angorasix.projects.management.core.infrastructure.applicationevents

import com.angorasix.commons.infrastructure.intercommunication.projectmanagement.ManagementTasksClosed
import com.angorasix.commons.infrastructure.intercommunication.projectmanagement.ProjectManagementContributorRegistered
import com.angorasix.commons.infrastructure.intercommunication.projectmanagement.ProjectManagementCreated
import com.angorasix.projects.management.core.messaging.publisher.MessagePublisher
import org.springframework.context.event.EventListener

class ApplicationEventsListener(
    private val messagePublisher: MessagePublisher,
) {
    @EventListener
    fun handleProjectManagementCreated(evt: ProjectManagementCreatedApplicationEvent) =
        evt.newProjectManagement.id?.let {
            messagePublisher.publishProjectManagementCreated(
                projectManagementCreated =
                    ProjectManagementCreated(
                        projectManagementId = evt.newProjectManagement.id,
                        creatorContributor = evt.requestingContributor,
                    ),
                requestingContributor = evt.requestingContributor,
            )
        }

    @EventListener
    fun handleContributorRegistered(evt: ContributorRegisteredApplicationEvent) =
        evt.projectManagement.id?.let {
            messagePublisher.publishContributorRegistered(
                projectManagementContributorRegistered =
                    ProjectManagementContributorRegistered(
                        projectManagementId = evt.projectManagement.id,
                        registeredContributorId = evt.registeredContributorId,
                        ownershipCurrency = evt.ownershipCurrency,
                        managementFinancialCurrencies = evt.managementFinancialCurrencies,
                    ),
                requestingContributor = evt.requestingContributor,
            )
        }

    @EventListener
    fun handleContributorRegistered(evt: ManagementTasksClosedApplicationEvent) =
        evt.projectManagement.id?.let {
            messagePublisher.publishManagementTasksClosed(
                managementTasksClosed =
                    ManagementTasksClosed(
                        projectManagementId = evt.projectManagement.id,
                        collection =
                            evt.collection.map {
                                ManagementTasksClosed.ManagementTaskClosed(
                                    taskId = it.taskId,
                                    caps = it.caps,
                                    moneyPayment = it.moneyPayment,
                                    assigneeContributorIds = it.assigneeContributorIds,
                                    doneInstant = it.doneInstant,
                                )
                            },
                        ownershipCurrency = evt.ownershipCurrency,
                        managementFinancialCurrencies = evt.managementFinancialCurrencies,
                        currencyDistributionRules = evt.currencyDistributionRules.toTasksDistributionRules(),
                    ),
                requestingContributor = evt.requestingContributor,
            )
        }
}

private fun Map<String, ManagementTasksClosedApplicationEvent.ApplicationEventTasksDistributionRules>.toTasksDistributionRules() =
    this.mapValues {
        ManagementTasksClosed.TasksDistributionRules(
            startupDefaultDuration = it.value.startupDefaultDuration,
            regularDefaultDuration = it.value.regularDefaultDuration,
        )
    }
