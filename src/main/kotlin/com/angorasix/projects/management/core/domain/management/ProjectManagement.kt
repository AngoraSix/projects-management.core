package com.angorasix.projects.management.core.domain.management

import com.angorasix.commons.domain.A6Contributor
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
data class ProjectManagement
    @PersistenceCreator
    private constructor(
        @field:Id val id: String?,
        @field:Indexed(unique = true) val projectId: String,
        val admins: Set<A6Contributor> = emptySet(),
        val constitution: ManagementConstitution,
        var status: ManagementStatus,
    ) {
        constructor(
            projectId: String,
            admins: Set<A6Contributor>,
            constitution: ManagementConstitution,
            status: ManagementStatus,
        ) : this(
            null,
            projectId,
            admins,
            constitution,
            status,
        )

        fun isAdministeredBy(simpleContributor: A6Contributor): Boolean =
            admins.any {
                it.contributorId ==
                    simpleContributor.contributorId
            }
    }
