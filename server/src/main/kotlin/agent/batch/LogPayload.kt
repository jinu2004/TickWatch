package agent.batch

import kotlinx.serialization.Serializable

@Serializable
data class LogPayload(
    val level: String,
    val message: String,
    val timestamp: Long
)