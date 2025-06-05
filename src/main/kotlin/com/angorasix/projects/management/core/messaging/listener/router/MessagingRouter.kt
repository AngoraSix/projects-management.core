package com.angorasix.projects.management.core.messaging.listener.router

import com.angorasix.commons.infrastructure.intercommunication.club.ClubMemberJoined
import com.angorasix.commons.infrastructure.intercommunication.messaging.A6InfraMessageDto
import com.angorasix.commons.infrastructure.intercommunication.tasks.TasksClosed
import com.angorasix.projects.management.core.messaging.listener.handler.MessagingHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingRouter(
    val handler: MessagingHandler,
) {
    // Revert this when this is GA: https://github.com/spring-cloud/spring-cloud-function/issues/124
    @Bean
    fun processManagementMemberJoined(): java.util.function.Function<A6InfraMessageDto<ClubMemberJoined>, Unit> =
        java.util.function.Function { handler.processManagementMemberJoined(it) }

    @Bean
    fun processTasksClosed(): java.util.function.Function<A6InfraMessageDto<TasksClosed>, Unit> =
        java.util.function.Function { handler.processTasksClosed(it) }
}
