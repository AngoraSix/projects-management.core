package com.angorasix.projects.management.core.infrastructure.applicationevents

import com.angorasix.commons.infrastructure.intercommunication.dto.projectmanagement.ProjectManagementCreated
import com.angorasix.projects.management.core.messaging.publisher.MessagePublisher
import org.springframework.context.event.EventListener

class ApplicationEventsListener(
    private val messagePublisher: MessagePublisher,
) {
    @EventListener
    fun handleUpdatedAssets(projectManagementCreatedEvent: ProjectManagementCreatedApplicationEvent) =
        projectManagementCreatedEvent.newProjectManagement.id?.let {
            messagePublisher.publishProjectManagementCreated(
                projectManagementCreated =
                    ProjectManagementCreated(
                        projectManagementId = projectManagementCreatedEvent.newProjectManagement.id,
                        creatorContributor = projectManagementCreatedEvent.requestingContributor,
                    ),
                requestingContributor = projectManagementCreatedEvent.requestingContributor,
            )
        }
}
