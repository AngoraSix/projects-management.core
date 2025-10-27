package com.angorasix.projects.management.core.infrastructure.persistence.repository

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
class ProjectManagementFilterRepositoryImpl(
    private val mongoOps: ReactiveMongoOperations,
) : ProjectManagementFilterRepository {
    override fun findUsingFilter(
        filter: ListProjectsManagementFilter,
        requestingContributor: A6Contributor?,
    ): Flow<ProjectManagement> = mongoOps.find(filter.toQuery(), ProjectManagement::class.java).asFlow()

    override suspend fun findSingleUsingFilter(
        filter: ListProjectsManagementFilter,
        requestingContributor: A6Contributor?,
    ): ProjectManagement? =
        mongoOps
            .find(filter.toQuery(requestingContributor), ProjectManagement::class.java)
            .awaitFirstOrNull()
}

private fun ListProjectsManagementFilter.toQuery(requestingContributor: A6Contributor? = null): Query {
    val query = Query()

    ids?.let { query.addCriteria(where("_id").`in`(it as Collection<Any>)) }
    projectIds?.let { query.addCriteria(where("projectId").`in`(it as Collection<Any>)) }

    if (adminId != null && requestingContributor != null) {
        query.addCriteria(where("admins.contributorId").`in`(adminId + requestingContributor.contributorId))
    }

    return query
}
