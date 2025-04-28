package com.angorasix.projects.management.core.infrastructure.applicationevents

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.projects.management.core.domain.management.ProjectManagement

data class ProjectManagementCreatedApplicationEvent(
    val newProjectManagement: ProjectManagement,
    val requestingContributor: SimpleContributor,
)
