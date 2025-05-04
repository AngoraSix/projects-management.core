package com.angorasix.projects.management.core.presentation.dto

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.projects.management.core.domain.management.Bylaw
import com.angorasix.projects.management.core.domain.management.ManagementStatus
import org.springframework.hateoas.RepresentationModel

/**
 *
 *
 * @author rozagerardo
 */
data class ProjectManagementDto(
    val projectId: String? = null,
    var admins: Set<A6Contributor>? = mutableSetOf(),
    val constitution: ManagementConstitutionDto? = null,
    val status: ManagementStatus? = null,
    val id: String? = null,
) : RepresentationModel<ProjectManagementDto>()

data class ManagementConstitutionDto(
    val bylaws: Map<String, BylawDto>? = emptyMap(),
)

data class BylawDto(
    val definition: Any,
    val category: String? = null,
)

fun Bylaw<Any>.convertToDto(): BylawDto = BylawDto(definition, category)

fun BylawDto.convertToDomain(): Bylaw<Any> = Bylaw(definition, category)
