package agent.batch

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class EventPayload(
    val event: String,
    val timestamp: Long,
    val entityId: String? = null,
    val entityType: String? = null,
    val tags: Map<String, String> = emptyMap(),
    val data: Map<String, JsonElement> = emptyMap()
)