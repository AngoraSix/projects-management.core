package com.angorasix.projects.management.core.domain.management

import com.angorasix.commons.domain.projectmanagement.core.Bylaw

/**
 * <p>
 *     Foundation defining the basic management of a project.
 * </p>
 *
 * @author rozagerardo
 */
data class ManagementConstitution constructor(
    val bylaws: Collection<Bylaw<Any>>,
)
