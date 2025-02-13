package com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.http.HttpMethod

/**
 * <p>
 *  Base file containing all Service configurations.
 * </p>
 *
 * @author rozagerardo
 */
@ConfigurationProperties(prefix = "configs.api")
data class ApiConfigs(
    @NestedConfigurationProperty
    var routes: RoutesConfigs,

    @NestedConfigurationProperty
    var basePaths: BasePathConfigs,
)

data class BasePathConfigs(
    val projectsManagement: String,
    val baseListCrudRoute: String,
    val baseByIdCrudRoute: String,
    val baseByProjectIdCrudRoute: String,
)

data class RoutesConfigs(
    val createProjectManagement: Route,
    val createProjectManagementByProjectId: Route,
    val updateProjectManagement: Route,
    val validateAdminUser: Route,
    val getProjectManagement: Route,
    val listProjectManagements: Route,
    val getProjectManagementByProjectId: Route,
)

data class Route(
    val name: String,
    val basePaths: List<String>,
    val method: HttpMethod,
    val path: String,
) {
    fun resolvePath(): String = basePaths.joinToString("").plus(path)
}
