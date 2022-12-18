package com.angorasix.projects.management.core.presentation.dto

import com.angorasix.projects.management.core.domain.management.ManagementStatus
import org.springframework.hateoas.RepresentationModel

/**
 *
 *
 * @author rozagerardo
 */
data class ProjectManagementDto(
    val projectId: String? = null,
    val constitution: ManagementConstitutionDto? = null,
    val status: ManagementStatus? = null,
    val id: String? = null,
) : RepresentationModel<ProjectManagementDto>()

data class ManagementConstitutionDto(
    val bylaws: Collection<BylawDto>? = emptyList(),
)

data class BylawDto(
    val scope: String,
    val definition: Any,
)
