package agent.batch

import agent.batch.enums.LogLevel
import kotlinx.serialization.Serializable

@Serializable
data class LogPayload(
    val level: LogLevel,
    val message: String,
    val timestamp: Long
)