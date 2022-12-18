package com.angorasix.projects.management.core.domain.management

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor

/**
 * <p>
 *     Root entity defining the Project Management data.
 * </p>
 *
 * @author rozagerardo
 */
data class ProjectManagement @PersistenceConstructor private constructor(
    @field:Id val id: String?,
    val projectId: String,
    val constitution: ManagementConstitution,
    var status: ManagementStatus
) {
    constructor(
        projectId: String,
        constitution: ManagementConstitution,
        status: ManagementStatus
    ) : this(
        null,
        projectId,
        constitution,
        status
    )
}
