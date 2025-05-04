package com.angorasix.projects.management.core.messaging.listener.handler

import com.angorasix.commons.domain.clubs.WellKnownClubTypes
import com.angorasix.commons.infrastructure.intercommunication.A6DomainResource
import com.angorasix.commons.infrastructure.intercommunication.A6InfraTopics
import com.angorasix.commons.infrastructure.intercommunication.club.ClubMemberJoined
import com.angorasix.commons.infrastructure.intercommunication.messaging.A6InfraMessageDto
import com.angorasix.projects.management.core.application.ProjectsManagementService
import kotlinx.coroutines.runBlocking

class MessagingHandler(
    private val service: ProjectsManagementService,
) {
    fun processManagementMemberJoined(message: A6InfraMessageDto<ClubMemberJoined>) =
        runBlocking {
            if (message.topic == A6InfraTopics.MANAGEMENT_CLUB_MEMBER_JOINED.value &&
                message.targetType == A6DomainResource.PROJECT_MANAGEMENT &&
                message.messageData.club.clubType == WellKnownClubTypes.PROJECT_MANAGEMENT_MEMBERS.name
            ) {
                val managementId = message.messageData.club.managementId
                requireNotNull(managementId)
                service.processManagementMemberJoined(
                    projectManagementId = managementId,
                    joinedMemberContributorId = message.messageData.joinedMemberContributorId,
                    requestingContributor = message.requestingContributor,
                )
            }
        }
}
