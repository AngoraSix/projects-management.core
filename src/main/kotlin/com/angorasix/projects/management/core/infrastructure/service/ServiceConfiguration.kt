package com.angorasix.projects.management.core.infrastructure.service

import com.angorasix.projects.management.core.application.ProjectsManagementService
import com.angorasix.projects.management.core.domain.management.ProjectManagementRepository
import com.angorasix.projects.management.core.infrastructure.applicationevents.ApplicationEventsListener
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.amqp.AmqpConfigurations
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.projects.management.core.messaging.publisher.MessagePublisher
import com.angorasix.projects.management.core.presentation.handler.ProjectsManagementHandler
import com.angorasix.projects.management.core.presentation.router.ProjectsManagementRouter
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServiceConfiguration {
    @Bean
    fun projectService(
        repository: ProjectManagementRepository,
        applicationEventPublisher: ApplicationEventPublisher,
    ) = ProjectsManagementService(repository, applicationEventPublisher)

    @Bean
    fun projectHandler(
        service: ProjectsManagementService,
        apiConfigs: ApiConfigs,
    ) = ProjectsManagementHandler(service, apiConfigs)

    @Bean
    fun projectRouter(
        handler: ProjectsManagementHandler,
        apiConfigs: ApiConfigs,
    ) = ProjectsManagementRouter(handler, apiConfigs).managementRouterFunction()

    @Bean
    fun messagePublisher(
        streamBridge: StreamBridge,
        amqpConfigs: AmqpConfigurations,
    ) = MessagePublisher(streamBridge, amqpConfigs)

    @Bean
    fun applicationEventsListener(messagePublisher: MessagePublisher) = ApplicationEventsListener(messagePublisher)
}
