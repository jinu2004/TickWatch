package agent.batch.enums

import kotlinx.serialization.Serializable

@Serializable
enum class Severity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}