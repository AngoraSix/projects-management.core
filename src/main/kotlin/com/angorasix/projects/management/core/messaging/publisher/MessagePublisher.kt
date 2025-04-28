package com.angorasix.projects.management.core.messaging.publisher

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.infrastructure.intercommunication.dto.A6DomainResource
import com.angorasix.commons.infrastructure.intercommunication.dto.A6InfraTopics
import com.angorasix.commons.infrastructure.intercommunication.dto.messaging.A6InfraMessageDto
import com.angorasix.commons.infrastructure.intercommunication.dto.projectmanagement.ProjectManagementCreated
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.amqp.AmqpConfigurations
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.messaging.support.MessageBuilder

class MessagePublisher(
    private val streamBridge: StreamBridge,
    private val amqpConfigs: AmqpConfigurations,
) {
    fun publishProjectManagementCreated(
        projectManagementCreated: ProjectManagementCreated,
        requestingContributor: SimpleContributor,
    ) {
        streamBridge.send(
            amqpConfigs.bindings.projectManagementCreated,
            MessageBuilder
                .withPayload(
                    A6InfraMessageDto(
                        targetId = projectManagementCreated.projectManagementId,
                        targetType = A6DomainResource.ProjectManagement,
                        objectId = projectManagementCreated.projectManagementId,
                        objectType = A6DomainResource.ProjectManagement.value,
                        topic = A6InfraTopics.PROJECT_MANAGEMENT_CREATED.value,
                        requestingContributor = requestingContributor,
                        messageData = projectManagementCreated,
                    ),
                ).build(),
        )
    }
}
