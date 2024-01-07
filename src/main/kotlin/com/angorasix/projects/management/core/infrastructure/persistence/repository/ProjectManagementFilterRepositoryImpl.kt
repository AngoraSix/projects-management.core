package com.angorasix.projects.management.core.infrastructure.persistence.repository

import com.angorasix.commons.domain.SimpleContributor
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
class ProjectManagementFilterRepositoryImpl(val mongoOps: ReactiveMongoOperations) :
    ProjectManagementFilterRepository {

    override fun findUsingFilter(filter: ListProjectsManagementFilter): Flow<ProjectManagement> {
        return mongoOps.find(filter.toQuery(), ProjectManagement::class.java).asFlow()
    }

    override suspend fun findByIdForContributor(filter: ListProjectsManagementFilter,
                                                simpleContributor: SimpleContributor?): ProjectManagement? {
        return mongoOps.find(filter.toQuery(simpleContributor), ProjectManagement::class.java)
                .awaitFirstOrNull()
    }
}

private fun ListProjectsManagementFilter.toQuery(simpleContributor: SimpleContributor? = null): Query {
    val query = Query()

    val requestingOthers = adminId == null || !adminId.contains(simpleContributor?.contributorId)
    val requestingOwn =
            simpleContributor != null && (adminId.isNullOrEmpty() || adminId.contains(simpleContributor.contributorId))

    ids?.let { query.addCriteria(where("_id").`in`(it)) }
    projectIds?.let { query.addCriteria(where("projectId").`in`(it)) }

    if (requestingOthers) {
        return query
    }

    if (requestingOwn) {
        query.addCriteria(where("admins").elemMatch(where("contributorId").`is`(simpleContributor?.contributorId)))
    }

    return query
}
