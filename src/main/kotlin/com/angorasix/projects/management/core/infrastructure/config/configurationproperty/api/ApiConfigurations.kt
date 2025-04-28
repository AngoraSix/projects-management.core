package com.angorasix.projects.management.core.infrastructure.config.configurationproperty.api

import com.angorasix.commons.infrastructure.config.configurationproperty.api.Route
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

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
    @NestedConfigurationProperty
    var managementActions: ManagementActions,
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

data class ManagementActions(
    val getProjectManagementByProjectId: String,
    val updateProjectManagement: String,
    val createProjectManagementByProjectId: String,
)
