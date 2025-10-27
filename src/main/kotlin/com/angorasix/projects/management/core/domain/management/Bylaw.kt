package com.angorasix.projects.management.core.domain.management

/**
 * <p>
 *     Describing a particular bylaw / rule of the Project Management.
 * </p>
 *
 * @author rozagerardo
 */
class Bylaw<T>(
    val definition: T,
    val category: String,
)

/**
 * <p>
 *     All well-known bylaw scopes (used as the map key), their types and categories.
 * </p>
 *
 * @author rozagerardo
 */
enum class BylawWellknownScope(
    val value: String,
) {
    // (definitionType: KClass<out Any>) {
    // GOVERNANCE / OWNERSHIP
    OWNERSHIP_IS_A6MANAGED("OWNERSHIP_IS_A6MANAGED"), // (Boolean::class, BylawWellknownCateogries.GOVERNANCE),
    FINANCIAL_CURRENCIES("FINANCIAL_CURRENCIES"), // (Set::class, BylawWellknownCateogries.FINANCIAL),

    // TASKS DISTRIBUTION RULES -- PER CURRENCY(?)
    CURRENCYBASED_STARTUP_RETRIBUTION_PERIOD("{CURRENCY}--STARTUP_RETRIBUTION_PERIOD"), // ((Duration::class),
    CURRENCYBASED_REGULAR_RETRIBUTION_PERIOD("{CURRENCY}--REGULAR_RETRIBUTION_PERIOD"), // ((Duration::class),

    // POTENTIAL / UNUSED YET
    OPERATION_CORE_RETRIBUTION_MODEL("OPERATION_CORE_RETRIBUTION_MODEL"), // (String::class),
    OWNERSHIP_MECHANISM("OWNERSHIP_MECHANISM"), // ((String::class),
}
