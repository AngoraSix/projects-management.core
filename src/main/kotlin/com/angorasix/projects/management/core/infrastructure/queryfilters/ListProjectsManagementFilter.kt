package com.angorasix.projects.management.core.infrastructure.queryfilters

import com.angorasix.commons.domain.SimpleContributor

/**
 * <p>
 *     Classes containing different Request Query Filters.
 * </p>
 *
 * @author rozagerardo
 */
data class ListProjectsManagementFilter(
    val projectIds: Collection<String>? = null,
    val adminId: Set<String>? = null,
    val ids: Collection<String>? = null, // mgmt id
)
