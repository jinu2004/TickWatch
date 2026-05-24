package agent.batch.enums

import kotlinx.serialization.Serializable

@Serializable
enum class MetricPriority {
    NORMAL,
    INVESTIGATION,
    TEMPORARY,
    CRITICAL
}