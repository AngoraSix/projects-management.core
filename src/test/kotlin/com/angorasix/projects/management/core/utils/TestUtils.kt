package com.angorasix.projects.management.core.utils

import com.angorasix.projects.management.core.domain.management.Bylaw
import com.angorasix.projects.management.core.domain.management.BylawWellknownScope
import com.angorasix.projects.management.core.domain.management.ManagementConstitution
import com.angorasix.projects.management.core.domain.management.ManagementStatus
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import com.angorasix.projects.management.core.presentation.dto.BylawDto
import com.angorasix.projects.management.core.presentation.dto.ManagementConstitutionDto
import com.angorasix.projects.management.core.presentation.dto.ProjectManagementDto
import java.util.*

/**
 * <p>
 * </p>
 *
 * @author rozagerardo
 */
fun mockProjectManagement(modifier: String = ""): ProjectManagement =
    ProjectManagement(
        "mockedProjectId$modifier",
        mockConstitution(),
        ManagementStatus.STARTUP,
    )

fun mockConstitution(): ManagementConstitution = ManagementConstitution(
    listOf(
        Bylaw(
            BylawWellknownScope.OPERATION_CORE_RETRIBUTION_MODEL.name,
            "CAPS",
        ),
        Bylaw(BylawWellknownScope.OWNERSHIP_MECHANISM.name, "CAPS-BASED"),
    ),
)

fun mockProjectManagementDto(modifier: String = ""): ProjectManagementDto =
    ProjectManagementDto(
        "mockedProjectId$modifier",
        mockConstitutionDto(),
        ManagementStatus.STARTUP,
    )

fun mockConstitutionDto(): ManagementConstitutionDto = ManagementConstitutionDto(
    listOf(
        BylawDto(
            BylawWellknownScope.OPERATION_CORE_RETRIBUTION_MODEL.name,
            "CAPS",
        ),
        BylawDto(BylawWellknownScope.OWNERSHIP_MECHANISM.name, "CAPS-BASED"),
    ),
)

fun mockRequestingContributorHeader(asAdmin: Boolean = false): String {
    val requestingContributorJson = """
            {
              "contributorId": "mockedContributorId1",
              "projectAdmin": $asAdmin
            }
        """.trimIndent()
    return Base64.getUrlEncoder().encodeToString(requestingContributorJson.toByteArray())
}