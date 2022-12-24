package com.angorasix.projects.management.core.presentation.router

import com.angorasix.commons.presentation.filter.checkRequestingContributor
import com.angorasix.commons.presentation.filter.extractRequestingContributor
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.projects.management.core.presentation.handler.ProjectsManagementHandler
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.coRouter

/**
 * Router for all ProjectManagement related endpoints.
 *
 * @author rozagerardo
 */
class ProjectsManagementRouter(
    private val handler: ProjectsManagementHandler,
    private val objectMapper: ObjectMapper,
    private val apiConfigs: ApiConfigs,
) {

    /**
     * Main RouterFunction configuration for all endpoints related to ProjectManagements.
     *
     * @return the [RouterFunction] with all the routes for ProjectManagements
     */
    fun projectRouterFunction() = coRouter {
        apiConfigs.basePaths.projectsManagement.nest {
            filter { request, next ->
                extractRequestingContributor(
                    request,
                    next,
                    apiConfigs.headers.contributor,
                    objectMapper,
                )
            }
            apiConfigs.routes.baseByProjectIdCrudRoute.nest {
                method(apiConfigs.routes.getProjectManagementByProjectId.method).nest {
                    method(
                        apiConfigs.routes.getProjectManagementByProjectId.method,
                        handler::getProjectManagementByProjectId,
                    )
                }
            }
            apiConfigs.routes.baseByIdCrudRoute.nest {
                method(apiConfigs.routes.updateProjectManagement.method).nest {
                    filter { request, next ->
                        checkRequestingContributor(
                            request,
                            next,
                            apiConfigs.headers.contributor,
                        )
                    }
                    method(
                        apiConfigs.routes.updateProjectManagement.method,
                        handler::updateProjectManagement,
                    )
                }
                method(apiConfigs.routes.getProjectManagement.method).nest {
                    method(
                        apiConfigs.routes.getProjectManagement.method,
                        handler::getProjectManagement,
                    )
                }
            }
            apiConfigs.routes.baseListCrudRoute.nest {
                path(apiConfigs.routes.baseListCrudRoute).nest {
                    method(apiConfigs.routes.createProjectManagement.method).nest {
                        filter { request, next ->
                            checkRequestingContributor(
                                request,
                                next,
                                apiConfigs.headers.contributor,
                            )
                        }
                        method(
                            apiConfigs.routes.createProjectManagement.method,
                            handler::createProjectManagement,
                        )
                    }
                    method(apiConfigs.routes.listProjectManagements.method).nest {
                        method(
                            apiConfigs.routes.listProjectManagements.method,
                            handler::listProjectManagements,
                        )
                    }
                }
            }
        }
    }
}
