package com.angorasix.projects.management.core.presentation.router

import com.angorasix.commons.reactive.presentation.filter.extractRequestingContributor
import com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api.ApiConfigs
import com.angorasix.projects.management.core.presentation.handler.ProjectsManagementHandler
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.coRouter

/**
 * Router for all ProjectManagement related endpoints.
 *
 * @author rozagerardo
 */
class ProjectsManagementRouter(
    private val handler: ProjectsManagementHandler,
    private val apiConfigs: ApiConfigs,
) {
    /**
     * Main RouterFunction configuration for all endpoints related to ProjectManagements.
     *
     * @return the [RouterFunction] with all the routes for ProjectManagements
     */
    fun managementRouterFunction() =
        coRouter {
            apiConfigs.basePaths.projectsManagement.nest {
                filter { request, next ->
                    extractRequestingContributor(
                        request,
                        next,
                    )
                }
                apiConfigs.basePaths.baseByProjectIdCrudRoute.nest {
                    method(
                        apiConfigs.routes.createProjectManagementByProjectId.method,
                        handler::createProjectManagementByProjectId,
                    )
                    method(
                        apiConfigs.routes.getProjectManagementByProjectId.method,
                        handler::getProjectManagementByProjectId,
                    )
                }
                apiConfigs.basePaths.baseByIdCrudRoute.nest {
                    path(apiConfigs.routes.validateAdminUser.path).nest {
                        method(
                            apiConfigs.routes.validateAdminUser.method,
                            handler::validateAdminUser,
                        )
                    }
                    method(
                        apiConfigs.routes.updateProjectManagement.method,
                        handler::updateProjectManagement,
                    )
                    method(
                        apiConfigs.routes.getProjectManagement.method,
                        handler::getProjectManagement,
                    )
                }
                apiConfigs.basePaths.baseListCrudRoute.nest {
                    method(
                        apiConfigs.routes.createProjectManagement.method,
                        handler::createProjectManagement,
                    )
                    method(
                        apiConfigs.routes.listProjectManagements.method,
                        handler::listProjectManagements,
                    )
                }
            }
        }
}
