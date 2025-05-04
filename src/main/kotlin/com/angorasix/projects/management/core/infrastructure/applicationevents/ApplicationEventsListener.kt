package com.angorasix.projects.management.core.infrastructure.applicationevents

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
                        participatesInOwnership = evt.participatesInOwnership,
                        managementFinancialCurrencies = evt.managementFinancialCurrencies,
                    ),
                requestingContributor = evt.requestingContributor,
            )
        }
}
