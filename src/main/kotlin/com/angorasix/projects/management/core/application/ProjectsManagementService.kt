package com.angorasix.projects.management.core.application

import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.domain.management.ProjectManagementRepository
import com.angorasix.projects.management.core.infrastructure.queryfilters.ListProjectsManagementFilter
import kotlinx.coroutines.flow.Flow

/**
 *
 *
 * @author rozagerardo
 */
class ProjectsManagementService(private val repository: ProjectManagementRepository) {

    suspend fun findSingleProjectManagement(id: String): ProjectManagement? =
        repository.findById(id)

    fun findProjectManagements(filter: ListProjectsManagementFilter): Flow<ProjectManagement> =
        repository.findUsingFilter(filter)

    suspend fun createProjectManagement(projectManagement: ProjectManagement): ProjectManagement =
        repository.save(projectManagement)

    suspend fun updateProjectManagement(
        id: String,
        updateData: ProjectManagement,
    ): ProjectManagement? {
        val projectManagementToUpdate =
            repository.findById(id).takeIf { it?.projectId == updateData.projectId }
                ?: throw IllegalArgumentException("Provided 'projectId' doesn't match the Project Management one")
        return projectManagementToUpdate.updateWithData(updateData).let { repository.save(it) }
    }

    private fun ProjectManagement.updateWithData(other: ProjectManagement): ProjectManagement {
        this.status = other.status
        return this
    }
}
