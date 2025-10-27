package com.angorasix.projects.management.core.infrastructure.persistence.repository

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import kotlinx.coroutines.flow.Flow

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
interface ProjectManagementFilterRepository {
    fun findUsingFilter(
        filter: ListProjectsManagementFilter,
        requestingContributor: A6Contributor? = null,
    ): Flow<ProjectManagement>

    suspend fun findSingleUsingFilter(
        filter: ListProjectsManagementFilter,
        requestingContributor: A6Contributor? = null,
    ): ProjectManagement?
}
