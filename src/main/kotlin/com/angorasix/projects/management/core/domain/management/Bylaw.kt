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
    val category: String? = null,
)

/**
 * <p>
 *     All well-known bylaw scopes (used as the map key), their types and categories.
 * </p>
 *
 * @author rozagerardo
 */
enum class BylawWellknownScope {
    // (definitionType: KClass<out Any>) {
    // GOVERNANCE / OWNERSHIP
    OWNERSHIP_IS_A6MANAGED, // (Boolean::class, BylawWellknownCateogries.GOVERNANCE),
    FINANCIAL_CURRENCIES, // (Set::class, BylawWellknownCateogries.FINANCIAL),

    // POTENTIAL / UNUSED YET
    OPERATION_CORE_RETRIBUTION_MODEL, // (String::class),
    DECISION_MECHANISM, // ((String::class),
    OWNERSHIP_MECHANISM, // ((String::class),
    STARTUP_MIN_RELEASE_DATE, // ((Instant::class),
    STARTUP_MAX_RELEASE_DATE, // ((Instant::class),
    STARTUP_RETRIBUTION_PERIOD, // ((Duration::class),
    STARTUP_RETRIBUTION_MODEL, // ((String::class),
}
