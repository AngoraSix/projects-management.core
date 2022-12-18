package com.angorasix.projects.management.core.infrastructure.persistence.repository

import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
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
}

private fun ListProjectsManagementFilter.toQuery(): Query {
    val query = Query()
    projectIds?.let { query.addCriteria(where("projectId").`in`(it)) }
    return query
}
