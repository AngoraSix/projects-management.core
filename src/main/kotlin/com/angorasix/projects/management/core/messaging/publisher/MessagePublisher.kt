package com.angorasix.projects.management.core.messaging.publisher

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.commons.infrastructure.intercommunication.A6DomainResource
import com.angorasix.commons.infrastructure.intercommunication.A6InfraTopics
import com.angorasix.commons.infrastructure.intercommunication.A6_INFRA_BULK_ID
import com.angorasix.commons.infrastructure.intercommunication.messaging.A6InfraMessageDto
import com.angorasix.commons.infrastructure.intercommunication.projectmanagement.ManagementTasksClosed
import com.angorasix.commons.infrastructure.intercommunication.projectmanagement.ProjectManagementContributorRegistered
import com.angorasix.commons.infrastructure.intercommunication.projectmanagement.ProjectManagementCreated
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.amqp.AmqpConfigurations
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.MessageBuilder

class MessagePublisher(
    private val streamBridge: StreamBridge,
    private val amqpConfigs: AmqpConfigurations,
) {
    fun publishProjectManagementCreated(
        projectManagementCreated: ProjectManagementCreated,
        requestingContributor: A6Contributor,
    ) {
        streamBridge.send(
            amqpConfigs.bindings.projectManagementCreated,
            MessageBuilder
                .withPayload(
                    A6InfraMessageDto(
                        targetId = projectManagementCreated.projectManagementId,
                        targetType = A6DomainResource.PROJECT_MANAGEMENT,
                        objectId = projectManagementCreated.projectManagementId,
                        objectType = A6DomainResource.PROJECT_MANAGEMENT.value,
                        topic = A6InfraTopics.PROJECT_MANAGEMENT_CREATED.value,
                        requestingContributor = requestingContributor,
                        messageData = projectManagementCreated,
                    ),
                ).build(),
        )
    }

    fun publishContributorRegistered(
        projectManagementContributorRegistered: ProjectManagementContributorRegistered,
        requestingContributor: A6Contributor,
    ) {
        streamBridge.send(
            amqpConfigs.bindings.managementContributorRegistered,
            MessageBuilder
                .withPayload(
                    A6InfraMessageDto(
                        targetId = projectManagementContributorRegistered.projectManagementId,
                        targetType = A6DomainResource.PROJECT_MANAGEMENT,
                        objectId = projectManagementContributorRegistered.registeredContributorId,
                        objectType = A6DomainResource.CONTRIBUTOR.value,
                        topic = A6InfraTopics.PROJECT_MANAGEMENT_CONTRIBUTOR_REGISTERED.value,
                        requestingContributor = requestingContributor,
                        messageData = projectManagementContributorRegistered,
                    ),
                ).build(),
        )
    }

    fun publishManagementTasksClosed(
        managementTasksClosed: ManagementTasksClosed,
        requestingContributor: A6Contributor,
    ) {
        streamBridge.send(
            amqpConfigs.bindings.managementTasksClosed,
            MessageBuilder
                .withPayload(
                    A6InfraMessageDto(
                        targetId = managementTasksClosed.projectManagementId,
                        targetType = A6DomainResource.PROJECT_MANAGEMENT,
                        objectId = A6_INFRA_BULK_ID,
                        objectType = A6DomainResource.TASK.value,
                        topic = A6InfraTopics.PROJECT_MANAGEMENT_TASKS_CLOSED.value,
                        requestingContributor = requestingContributor,
                        messageData = managementTasksClosed,
                    ),
                ).build(),
        )
    }
}
