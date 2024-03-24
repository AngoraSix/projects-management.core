package com.angorasix.projects.management.core.domain.management

/**
 * <p>
 *     Describing a particular bylaw / rule of the Project Management.
 * </p>
 *
 * @author rozagerardo
 */
class Bylaw<T>(
    val scope: String,
    val definition: T,
)

/**
 * <p>
 *     All well-known bylaw scopes, and their types.
 * </p>
 *
 * @author rozagerardo
 */
enum class BylawWellknownScope { // (definitionType: KClass<out Any>) {
    OPERATION_CORE_RETRIBUTION_MODEL, // (String::class),
    DECISION_MECHANISM, // ((String::class),
    OWNERSHIP_MECHANISM, // ((String::class),
    STARTUP_MIN_RELEASE_DATE, // ((Instant::class),
    STARTUP_MAX_RELEASE_DATE, // ((Instant::class),
    STARTUP_RETRIBUTION_PERIOD, // ((Duration::class),
    STARTUP_RETRIBUTION_MODEL, // ((String::class),
}
