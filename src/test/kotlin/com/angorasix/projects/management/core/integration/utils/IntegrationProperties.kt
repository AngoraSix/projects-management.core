package com.angorasix.projects.management.core.integration.utils

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 *
 * @author rozagerardo
 */
@ConfigurationProperties("integration")
class IntegrationProperties(
    val mongodb: MongodbProperties,
) {
    data class MongodbProperties(
        val baseJsonFile: String,
    )
}
