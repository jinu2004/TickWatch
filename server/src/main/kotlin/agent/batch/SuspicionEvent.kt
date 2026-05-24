package agent.batch

import agent.batch.enums.Severity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SuspicionEvent(
    val id: String,

    val entityId: String,
    val entityType: String,

    val ruleName: String,
    val reason: String,

    val value: Double? = null,
    val threshold: Double? = null,

    val severity: Severity,

    val metadata: Map<String, JsonElement> = emptyMap(),

    val timestamp: Long
)