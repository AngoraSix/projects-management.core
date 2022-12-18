package com.angorasix.projects.management.core.infrastructure.queryfilters

/**
 * <p>
 *     Classes containing different Request Query Filters.
 * </p>
 *
 * @author rozagerardo
 */
data class ListProjectsManagementFilter(
    val projectIds: Collection<String>? = null,
)
