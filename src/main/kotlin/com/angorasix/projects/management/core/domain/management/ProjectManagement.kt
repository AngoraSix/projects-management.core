package com.angorasix.projects.management.core.domain.management

import com.angorasix.commons.domain.SimpleContributor
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceCreator
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * <p>
 *     Root entity defining the Project Management data.
 * </p>
 *
 * @author rozagerardo
 */
@Document
data class ProjectManagement @PersistenceCreator private constructor(
    @field:Id val id: String?,
    @field:Indexed(unique = true) val projectId: String,
    val admins: Set<SimpleContributor> = emptySet(),
    val constitution: ManagementConstitution,
    var status: ManagementStatus,
) {
    constructor(
        projectId: String,
        admins: Set<SimpleContributor>,
        constitution: ManagementConstitution,
        status: ManagementStatus,
    ) : this(
        null,
        projectId,
        admins,
        constitution,
        status,
    )
}
