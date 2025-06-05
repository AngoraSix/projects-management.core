package com.angorasix.projects.management.core.infrastructure.applicationevents

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.projects.management.core.domain.management.ProjectManagement

data class ContributorRegisteredApplicationEvent(
    val projectManagement: ProjectManagement,
    val ownershipCurrency: String?,
    val managementFinancialCurrencies: Set<String>,
    val registeredContributorId: String,
    val requestingContributor: A6Contributor,
)
