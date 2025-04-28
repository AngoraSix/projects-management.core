package com.angorasix.projects.management.core.presentation.handler

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.reactive.presentation.mappings.addLink
import com.angorasix.commons.reactive.presentation.mappings.addSelfLink
import com.angorasix.commons.reactive.presentation.mappings.generateLink
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.projects.management.core.presentation.dto.ProjectManagementDto
import org.springframework.hateoas.Links
import org.springframework.web.reactive.function.server.ServerRequest

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */

fun ProjectManagementDto.resolveHypermedia(
    requestingContributor: SimpleContributor?,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): ProjectManagementDto {
    val getSingleRoute = apiConfigs.routes.getProjectManagement
    // self
    requireNotNull(id)
    addSelfLink(getSingleRoute, request, listOf(id))

    requireNotNull(projectId)
    addLink(
        apiConfigs.routes.getProjectManagementByProjectId,
        apiConfigs.managementActions.getProjectManagementByProjectId,
        request,
        listOf(projectId),
    )

    // edit ProjectManagement
    if (requestingContributor != null && admins != null) {
        if (admins?.map { it.contributorId }?.contains(requestingContributor.contributorId) == true) {
            addLink(
                apiConfigs.routes.updateProjectManagement,
                apiConfigs.managementActions.updateProjectManagement,
                request,
                listOf(id),
            )
        }
    }
    return this
}

fun resolveCreateByProjectIdLink(
    projectId: String,
    requestingContributor: SimpleContributor?,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): Links {
    val getByProjectIdAffordanceLink =
        generateLink(
            apiConfigs.routes.getProjectManagement,
            apiConfigs.managementActions.getProjectManagementByProjectId,
            request,
            listOf(projectId),
        )

    // create
    if (requestingContributor != null && requestingContributor.isAdminHint == true) {
        val createAffordanceLink =
            generateLink(
                apiConfigs.routes.createProjectManagementByProjectId,
                apiConfigs.managementActions.createProjectManagementByProjectId,
                request,
                listOf(projectId),
            )
        return Links.of(
            getByProjectIdAffordanceLink,
            createAffordanceLink,
        )
    }

    return Links.of(
        getByProjectIdAffordanceLink,
    )
}
