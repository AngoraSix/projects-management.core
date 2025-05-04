package com.angorasix.projects.management.core.infrastructure.config.configurationproperty.amqp

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

@ConfigurationProperties(prefix = "configs.amqp")
data class AmqpConfigurations(
    @NestedConfigurationProperty
    var bindings: BindingConfigs,
)

class BindingConfigs(
    val projectManagementCreated: String,
    val managementContributorRegistered: String,
)
