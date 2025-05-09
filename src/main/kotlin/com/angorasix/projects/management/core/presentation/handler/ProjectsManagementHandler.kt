package com.angorasix.projects.management.core.presentation.handler

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.commons.infrastructure.constants.AngoraSixInfrastructure
import com.angorasix.commons.presentation.dto.IsAdminDto
import com.angorasix.commons.reactive.presentation.error.resolveBadRequest
import com.angorasix.commons.reactive.presentation.error.resolveNotFound
import com.angorasix.projects.management.core.application.ProjectsManagementService
import com.angorasix.projects.management.core.domain.management.ManagementConstitution
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import com.angorasix.projects.management.core.presentation.dto.ManagementConstitutionDto
import com.angorasix.projects.management.core.presentation.dto.ProjectManagementDto
import com.angorasix.projects.management.core.presentation.dto.ProjectsManagementQueryParams
import com.angorasix.projects.management.core.presentation.dto.convertToDomain
import com.angorasix.projects.management.core.presentation.dto.convertToDto
import kotlinx.coroutines.flow.map
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.MediaTypes
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.net.URI

/**
 * ProjectManagement Handler (Controller) containing all handler functions related to ProjectManagement endpoints.
 *
 * @author rozagerardo
 */
class ProjectsManagementHandler(
    private val service: ProjectsManagementService,
    private val apiConfigs: ApiConfigs,
) {
    /**
     * Handler for the List ProjectManagements endpoint,
     * retrieving a Flux including all persisted ProjectManagements.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun listProjectManagements(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        return service
            .findProjectManagements(request.queryParams().toQueryFilter())
            .map {
                it.convertToDto(requestingContributor as? A6Contributor, apiConfigs, request)
            }.let {
                ok().contentType(MediaTypes.HAL_FORMS_JSON).bodyAndAwait(it)
            }
    }

    /**
     * Handler for the Get Single ProjectManagement endpoint,
     * retrieving a Mono with the requested ProjectManagement.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun getProjectManagement(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        val projectManagementId = request.pathVariable("id")
        service.findSingleProjectManagement(projectManagementId)?.let {
            val outputProjectManagement =
                it.convertToDto(
                    requestingContributor as? A6Contributor,
                    apiConfigs,
                    request,
                )
            return ok()
                .contentType(MediaTypes.HAL_FORMS_JSON)
                .bodyValueAndAwait(outputProjectManagement)
        }
        return resolveNotFound("Can't find Project Management", "Project Management")
    }

    /**
     * Handler for the Get Single ProjectManagement endpoint,
     * retrieving a Mono with the requested ProjectManagement.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun getProjectManagementByProjectId(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY] as? A6Contributor
        val projectId = request.pathVariable("projectId")
        service.findSingleProjectManagementByProjectId(projectId)?.let {
            val outputProjectManagement =
                it.convertToDto(
                    requestingContributor,
                    apiConfigs,
                    request,
                )
            return ok()
                .contentType(MediaTypes.HAL_FORMS_JSON)
                .bodyValueAndAwait(outputProjectManagement)
        }

        return resolveNotFound(
            "Can't find Project Management using projectId",
            "Project Management",
            resolveCreateByProjectIdLink(projectId, requestingContributor, apiConfigs, request),
        )
    }

    /**
     * Handler for the Create ProjectManagements endpoint, to create a new ProjectManagement entity.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun createProjectManagement(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]

        return if (requestingContributor is A6Contributor) {
            val project =
                try {
                    request
                        .awaitBody<ProjectManagementDto>()
                        .convertToDomain(
                            setOf(
                                A6Contributor(
                                    requestingContributor.contributorId,
                                ),
                            ),
                        )
                } catch (e: IllegalArgumentException) {
                    return resolveBadRequest(
                        e.message ?: "Incorrect Project Management body",
                        "Project Management",
                    )
                }

            val outputProjectManagement =
                service
                    .createProjectManagement(project, requestingContributor)
                    .convertToDto(requestingContributor, apiConfigs, request)

            val selfLink =
                outputProjectManagement.links.getRequiredLink(IanaLinkRelations.SELF).href

            created(URI.create(selfLink))
                .contentType(MediaTypes.HAL_FORMS_JSON)
                .bodyValueAndAwait(outputProjectManagement)
        } else {
            resolveBadRequest("Invalid Contributor Token", "Contributor Token")
        }
    }

    /**
     * Handler for the Create ProjectManagements endpoint, to create a new ProjectManagement entity.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun createProjectManagementByProjectId(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]

        return if (requestingContributor is A6Contributor) {
            val projectId = request.pathVariable("projectId")

            val project =
                try {
                    request
                        .awaitBody<ProjectManagementDto>()
                        .convertToDomain(
                            setOf(
                                A6Contributor(
                                    requestingContributor.contributorId,
                                ),
                            ),
                            projectId,
                        )
                } catch (e: IllegalArgumentException) {
                    return resolveBadRequest(
                        e.message ?: "Incorrect Project Management body",
                        "Project Management",
                    )
                }

            val outputProjectManagement =
                service
                    .createProjectManagement(project, requestingContributor)
                    .convertToDto(requestingContributor, apiConfigs, request)

            val selfLink =
                outputProjectManagement.links.getRequiredLink(IanaLinkRelations.SELF).href

            created(URI.create(selfLink))
                .contentType(MediaTypes.HAL_FORMS_JSON)
                .bodyValueAndAwait(outputProjectManagement)
        } else {
            resolveBadRequest("Invalid Contributor Token", "Contributor Token")
        }
    }

    /**
     * Handler for the Update ProjectManagement endpoint, retrieving a Mono with the updated ProjectManagement.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun updateProjectManagement(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]

        return if (requestingContributor is A6Contributor) {
            val projectId = request.pathVariable("id")

            val updateProjectManagementData =
                try {
                    request
                        .awaitBody<ProjectManagementDto>()
                        .let { it.convertToDomain(it.admins ?: emptySet()) }
                } catch (e: IllegalArgumentException) {
                    return resolveBadRequest(
                        e.message ?: "Incorrect Project Management body",
                        "Project Management",
                    )
                }

            service
                .updateProjectManagement(
                    projectId,
                    updateProjectManagementData,
                    requestingContributor,
                )?.let {
                    val outputProjectManagement =
                        it.convertToDto(
                            requestingContributor,
                            apiConfigs,
                            request,
                        )
                    ok()
                        .contentType(MediaTypes.HAL_FORMS_JSON)
                        .bodyValueAndAwait(outputProjectManagement)
                } ?: resolveNotFound("Can't update this project management", "Project Management")
        } else {
            resolveBadRequest("Invalid Contributor Token", "Contributor Token")
        }
    }

    /**
     * Handler for the Is Admin check endpoint,
     * retrieving a Mono indicating whether the user is admin of the Project Management.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun validateAdminUser(request: ServerRequest): ServerResponse {
        val requestingContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        val projectManagementId = request.pathVariable("id")
        return if (requestingContributor is A6Contributor) {
            service.administeredProjectManagement(projectManagementId, requestingContributor)?.let {
                val result = it.isAdministeredBy(requestingContributor)
                ok().contentType(MediaTypes.HAL_FORMS_JSON).bodyValueAndAwait(IsAdminDto(result))
            } ?: resolveNotFound("Can't find project management", "Project Management")
        } else {
            resolveBadRequest("Invalid Contributor Authentication", "Authentication")
        }
    }
}

private fun ProjectManagement.convertToDto(): ProjectManagementDto =
    ProjectManagementDto(projectId, admins, constitution.convertToDto(), status, id)

private fun ProjectManagement.convertToDto(
    requestingContributor: A6Contributor?,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): ProjectManagementDto = convertToDto().resolveHypermedia(requestingContributor, apiConfigs, request)

private fun ProjectManagementDto.convertToDomain(
    admins: Set<A6Contributor>,
    paramProjectId: String? = null,
): ProjectManagement {
    if (projectId != null && paramProjectId != null && projectId != paramProjectId) {
        throw IllegalArgumentException(
            "Mismatching projectId parameters",
        )
    }
    val domainProjectId = paramProjectId ?: projectId
    val constitutionDomainModel = constitution?.convertToDomain()
    if (domainProjectId == null || constitutionDomainModel == null || status == null) {
        throw IllegalArgumentException(
            "Invalid ProjectManagement -" +
                "domainProjectId: $domainProjectId -" +
                "constitution: ${constitution?.convertToDomain()} -" +
                "status: $status",
        )
    }
    return ProjectManagement(
        domainProjectId,
        admins,
        constitutionDomainModel,
        status,
    )
}

private fun ManagementConstitution.convertToDto(): ManagementConstitutionDto =
    ManagementConstitutionDto(bylaws.mapValues { (_, b) -> b.convertToDto() })

private fun ManagementConstitutionDto.convertToDomain(): ManagementConstitution =
    ManagementConstitution(
        bylaws?.mapValues { (_, b) -> b.convertToDomain() } ?: emptyMap(),
    )

private fun MultiValueMap<String, String>.toQueryFilter(): ListProjectsManagementFilter =
    ListProjectsManagementFilter(
        get(ProjectsManagementQueryParams.PROJECT_IDS.param)?.flatMap { it.split(",") },
    )
