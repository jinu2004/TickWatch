package agent.batch.enums

import kotlinx.serialization.Serializable

@Serializable
enum class LogLevel {
    INFO,
    WARN,
    ERROR,
    DEBUG
}