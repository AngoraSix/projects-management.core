package com.angorasix.projects.management.core.utils

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.projects.management.core.domain.management.Bylaw
import com.angorasix.projects.management.core.domain.management.BylawWellknownScope
import com.angorasix.projects.management.core.domain.management.ManagementConstitution
import com.angorasix.projects.management.core.domain.management.ManagementStatus
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.presentation.dto.BylawDto
import com.angorasix.projects.management.core.presentation.dto.ManagementConstitutionDto
import com.angorasix.projects.management.core.presentation.dto.ProjectManagementDto

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
fun mockProjectManagement(
    modifier: String = "",
    admins: Set<A6Contributor> = emptySet(),
): ProjectManagement =
    ProjectManagement(
        "mockedProjectId$modifier",
        admins,
        mockConstitution(),
        ManagementStatus.STARTUP,
    )

fun mockConstitution(): ManagementConstitution =
    ManagementConstitution(
        mapOf(
            BylawWellknownScope.OPERATION_CORE_RETRIBUTION_MODEL.name to
                Bylaw(
                    "CAPS",
                ),
            BylawWellknownScope.OWNERSHIP_MECHANISM.name to Bylaw("CAPS-BASED"),
        ),
    )

fun mockProjectManagementDto(modifier: String = getRandomString(5)): ProjectManagementDto =
    ProjectManagementDto(
        "mockedProjectId$modifier",
        emptySet(),
        mockConstitutionDto(),
        ManagementStatus.STARTUP,
    )

private fun getRandomString(length: Int): String {
    val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
    return (1..length).map { allowedChars.random() }.joinToString("")
}

fun mockConstitutionDto(): ManagementConstitutionDto =
    ManagementConstitutionDto(
        mapOf(
            BylawWellknownScope.OPERATION_CORE_RETRIBUTION_MODEL.name to
                BylawDto(
                    "CAPS",
                ),
            BylawWellknownScope.OWNERSHIP_MECHANISM.name to BylawDto("CAPS-BASED"),
        ),
    )
