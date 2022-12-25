package com.angorasix.projects.management.core.domain.management

import com.angorasix.projects.management.core.infrastructure.persistence.repository.ProjectManagementFilterRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

/**
 *
 *
 * @author rozagerardo
 */
interface ProjectManagementRepository :
    CoroutineCrudRepository<ProjectManagement, String>,
    CoroutineSortingRepository<ProjectManagement, String>,
    ProjectManagementFilterRepository {
    suspend fun findByProjectId(projectId: String): ProjectManagement?
}
