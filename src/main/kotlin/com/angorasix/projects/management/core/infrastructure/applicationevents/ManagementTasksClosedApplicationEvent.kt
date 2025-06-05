package com.angorasix.projects.management.core.infrastructure.applicationevents

import com.angorasix.commons.domain.A6Contributor
import com.angorasix.commons.infrastructure.intercommunication.tasks.TasksClosed
import com.angorasix.projects.management.core.domain.management.ProjectManagement
import java.time.Duration

data class ManagementTasksClosedApplicationEvent(
    val projectManagement: ProjectManagement,
    val collection: List<TasksClosed.TaskClosed>,
    val ownershipCurrency: String?,
    val managementFinancialCurrencies: Set<String>,
    val currencyDistributionRules: Map<String, ApplicationEventTasksDistributionRules>, // different rules for each currency
    val requestingContributor: A6Contributor,
) {
    data class ApplicationEventTasksDistributionRules(
        val startupDefaultDuration: Duration,
        val regularDefaultDuration: Duration,
    )
}
