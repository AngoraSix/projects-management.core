package com.angorasix.projects.management.core.presentation.dto

import com.angorasix.commons.domain.SimpleContributor
import com.angorasix.commons.presentation.dto.projectmanagement.BylawDto
import com.angorasix.projects.management.core.domain.management.ManagementStatus
import org.springframework.hateoas.RepresentationModel

/**
 *
 *
 * @author rozagerardo
 */
data class ProjectManagementDto(
    val projectId: String? = null,
    var admins: Set<SimpleContributor>? = mutableSetOf(),
    val constitution: ManagementConstitutionDto? = null,
    val status: ManagementStatus? = null,
    val id: String? = null,
) : RepresentationModel<ProjectManagementDto>()

data class ManagementConstitutionDto(
    val bylaws: Collection<BylawDto>? = emptyList(),
)
