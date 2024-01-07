package com.angorasix.projects.management.core.presentation.handler

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.infrastructure.constants.AngoraSixInfrastructure
import com.angorasix.commons.reactive.presentation.error.resolveBadRequest
import com.angorasix.commons.reactive.presentation.error.resolveNotFound
import com.angorasix.projects.management.core.application.ProjectsManagementService
import com.angorasix.projects.management.core.domain.management.Bylaw
import com.angorasix.projects.management.core.domain.management.ManagementConstitution
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import com.angorasix.projects.management.core.presentation.dto.BylawDto
import com.angorasix.projects.management.core.presentation.dto.ManagementConstitutionDto
import com.angorasix.projects.management.core.presentation.dto.ProjectManagementDto
import com.angorasix.projects.management.core.presentation.dto.ProjectsManagementQueryParams
import kotlinx.coroutines.flow.map
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.Link
import org.springframework.hateoas.Links
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.mediatype.Affordances
import org.springframework.http.HttpMethod
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.util.UriComponentsBuilder
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
        val requestingContributor = request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        return service.findProjectManagements(request.queryParams().toQueryFilter()).map {
            it.convertToDto(requestingContributor as? SimpleContributor, apiConfigs, request)
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
        val requestingContributor = request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        val projectManagementId = request.pathVariable("id")
        service.findSingleProjectManagement(projectManagementId)?.let {
            val outputProjectManagement =
                it.convertToDto(
                    requestingContributor as? SimpleContributor,
                    apiConfigs,
                    request,
                )
            return ok().contentType(MediaTypes.HAL_FORMS_JSON)
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
        val simpleContributor =
            request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY] as? SimpleContributor
        val projectId = request.pathVariable("projectId")
        service.findSingleProjectManagementByProjectId(projectId)?.let {
            val outputProjectManagement =
                it.convertToDto(
                    simpleContributor,
                    apiConfigs,
                    request,
                )
            return ok().contentType(MediaTypes.HAL_FORMS_JSON)
                .bodyValueAndAwait(outputProjectManagement)
        }

        return resolveNotFound(
            "Can't find Project Management using projectId",
            "Project Management",
            resolveCreateByProjectIdLink(projectId, simpleContributor, apiConfigs, request),
        )
    }

    /**
     * Handler for the Create ProjectManagements endpoint, to create a new ProjectManagement entity.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun createProjectManagement(request: ServerRequest): ServerResponse {
        val requestingContributor = request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        return if (requestingContributor is SimpleContributor) {
            val project = try {
                request.awaitBody<ProjectManagementDto>()
                    .convertToDomain(setOf(SimpleContributor(requestingContributor.contributorId, emptySet())))
            } catch (e: IllegalArgumentException) {
                return resolveBadRequest(
                    e.message ?: "Incorrect Project Management body",
                    "Project Management",
                )
            }
            val outputProjectManagement = service.createProjectManagement(project)
                .convertToDto(requestingContributor, apiConfigs, request)
            val selfLink =
                outputProjectManagement.links.getRequiredLink(IanaLinkRelations.SELF).href
            return created(URI.create(selfLink)).contentType(MediaTypes.HAL_FORMS_JSON)
                .bodyValueAndAwait(outputProjectManagement)
        } else {
            return resolveBadRequest("Invalid Contributor Header", "Contributor Header")
        }
    }

    /**
     * Handler for the Create ProjectManagements endpoint, to create a new ProjectManagement entity.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun createProjectManagementByProjectId(request: ServerRequest): ServerResponse {
        val requestingContributor = request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        val projectId = request.pathVariable("projectId")
        return if (requestingContributor is SimpleContributor) {
            val project = try {
                request.awaitBody<ProjectManagementDto>()
                    .convertToDomain(setOf(SimpleContributor(requestingContributor.contributorId, emptySet())), projectId)
            } catch (e: IllegalArgumentException) {
                return resolveBadRequest(
                    e.message ?: "Incorrect Project Management body",
                    "Project Management",
                )
            }
            val outputProjectManagement = service.createProjectManagement(project)
                .convertToDto(requestingContributor, apiConfigs, request)
            val selfLink =
                outputProjectManagement.links.getRequiredLink(IanaLinkRelations.SELF).href
            created(URI.create(selfLink)).contentType(MediaTypes.HAL_FORMS_JSON)
                .bodyValueAndAwait(outputProjectManagement)
        } else {
            resolveBadRequest("Invalid Contributor Header", "Contributor Header")
        }
    }

    /**
     * Handler for the Update ProjectManagement endpoint, retrieving a Mono with the updated ProjectManagement.
     *
     * @param request - HTTP `ServerRequest` object
     * @return the `ServerResponse`
     */
    suspend fun updateProjectManagement(request: ServerRequest): ServerResponse {
        val requestingContributor = request.attributes()[AngoraSixInfrastructure.REQUEST_ATTRIBUTE_CONTRIBUTOR_KEY]
        val projectId = request.pathVariable("id")
        val updateProjectManagementData = try {
            request.awaitBody<ProjectManagementDto>()
                .let { it.convertToDomain(it.admins ?: emptySet()) }
        } catch (e: IllegalArgumentException) {
            return resolveBadRequest(
                e.message ?: "Incorrect Project Management body",
                "Project Management",
            )
        }
        return service.updateProjectManagement(
                projectId,
                updateProjectManagementData,
                requestingContributor as SimpleContributor
        )?.let {
            val outputProjectManagement =
                it.convertToDto(
                    requestingContributor as? SimpleContributor,
                    apiConfigs,
                    request,
                )
            ok().contentType(MediaTypes.HAL_FORMS_JSON).bodyValueAndAwait(outputProjectManagement)
        } ?: resolveNotFound("Can't update this project management", "Project Management")
    }
}

private fun ProjectManagement.convertToDto(): ProjectManagementDto =
    ProjectManagementDto(projectId, admins, constitution.convertToDto(), status, id)

private fun ProjectManagement.convertToDto(
    simpleContributor: SimpleContributor?,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): ProjectManagementDto =
    convertToDto().resolveHypermedia(simpleContributor, apiConfigs, request)

private fun ProjectManagementDto.convertToDomain(admins: Set<SimpleContributor>, paramProjectId: String? = null): ProjectManagement {
    if (projectId != null && paramProjectId != null && projectId != paramProjectId) throw IllegalArgumentException(
        "Mismatching projectId parameters",
    )
    val domainProjectId = paramProjectId ?: projectId
    return ProjectManagement(
        domainProjectId ?: throw IllegalArgumentException("ProjectManagement projectId expected"),
            admins,
        constitution?.convertToDomain()
            ?: throw IllegalArgumentException("ProjectManagement constitution expected"),
        status ?: throw IllegalArgumentException("ProjectManagement status expected"),
    )
}

private fun ManagementConstitution.convertToDto(): ManagementConstitutionDto {
    return ManagementConstitutionDto(bylaws.map { it.convertToDto() })
}

private fun ManagementConstitutionDto.convertToDomain(): ManagementConstitution {
    return ManagementConstitution(
        bylaws?.map { it.convertToDomain() } ?: emptyList(),
    )
}

private fun Bylaw<Any>.convertToDto(): BylawDto {
    return BylawDto(scope, definition)
}

private fun BylawDto.convertToDomain(): Bylaw<Any> {
    return Bylaw(scope, definition)
}

private fun ProjectManagementDto.resolveHypermedia(
    simpleContributor: SimpleContributor?,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): ProjectManagementDto {
    val getSingleRoute = apiConfigs.routes.getProjectManagement
    // self
    val selfLink =
        Link.of(uriBuilder(request).path(getSingleRoute.resolvePath()).build().toUriString())
            .withRel(getSingleRoute.name).expand(id).withSelfRel()
    val selfLinkWithDefaultAffordance =
        Affordances.of(selfLink).afford(HttpMethod.OPTIONS).withName("default").toLink()
    add(selfLinkWithDefaultAffordance)

    val getByProjectIdRoute = apiConfigs.routes.getProjectManagementByProjectId
    val getByProjectIdLink =
        Link.of(
            uriBuilder(request).path(getByProjectIdRoute.resolvePath())
                .build().toUriString(),
        ).withTitle(getByProjectIdRoute.name)
            .withName(getByProjectIdRoute.name)
            .withRel(getByProjectIdRoute.name).expand(projectId)
    val getByProjectIdAffordanceLink =
        Affordances.of(getByProjectIdLink).afford(HttpMethod.GET)
            .withName(getByProjectIdRoute.name).toLink()
    add(getByProjectIdAffordanceLink)

    // edit ProjectManagement
    if (simpleContributor != null && admins != null) {
        if (admins?.map { it.contributorId }?.contains(simpleContributor.contributorId) == true) {
            val editProjectManagementRoute = apiConfigs.routes.updateProjectManagement
            val editProjectManagementLink =
                    Link.of(
                            uriBuilder(request).path(editProjectManagementRoute.resolvePath())
                                    .build().toUriString(),
                    ).withTitle(editProjectManagementRoute.name)
                            .withName(editProjectManagementRoute.name)
                            .withRel(editProjectManagementRoute.name).expand(id)
            val editProjectManagementAffordanceLink =
                    Affordances.of(editProjectManagementLink).afford(HttpMethod.PUT)
                            .withName(editProjectManagementRoute.name).toLink()
            add(editProjectManagementAffordanceLink)
        }
    }
    return this
}

private fun resolveCreateByProjectIdLink(
    projectId: String,
    simpleContributor: SimpleContributor?,
    apiConfigs: ApiConfigs,
    request: ServerRequest,
): Links {
    val getSingleRoute = apiConfigs.routes.getProjectManagement
    // self (by projectId
    val getByProjectIdRoute = apiConfigs.routes.getProjectManagementByProjectId
    val getByProjectIdLink =
        Link.of(
            uriBuilder(request).path(getByProjectIdRoute.resolvePath())
                .build().toUriString(),
        ).withTitle(getByProjectIdRoute.name)
            .withName(getByProjectIdRoute.name)
            .withRel(getByProjectIdRoute.name).expand(projectId).withSelfRel()
    val getByProjectIdAffordanceLink =
        Affordances.of(getByProjectIdLink).afford(HttpMethod.GET)
            .withName(getByProjectIdRoute.name).toLink()

    // create
    val createRoute = apiConfigs.routes.createProjectManagementByProjectId
    if (simpleContributor != null && simpleContributor.isAdminHint == true) {
        val createLink =
            Link.of(uriBuilder(request).path(createRoute.resolvePath()).build().toUriString())
                .withRel(createRoute.name).expand(projectId)
        val createAffordanceLink =
            Affordances.of(createLink).afford(HttpMethod.POST)
                .withName(createRoute.name).toLink()
        return Links.of(
            getByProjectIdAffordanceLink,
            createAffordanceLink,
        )
    }

    return Links.of(
        getByProjectIdAffordanceLink,
    )
}

private fun uriBuilder(request: ServerRequest) = request.requestPath().contextPath().let {
    UriComponentsBuilder.fromHttpRequest(request.exchange().request).replacePath(it.toString()) //
        .replaceQuery("")
}

private fun MultiValueMap<String, String>.toQueryFilter(): ListProjectsManagementFilter {
    return ListProjectsManagementFilter(
        get(ProjectsManagementQueryParams.PROJECT_IDS.param)?.flatMap { it.split(",") },
    )
}
