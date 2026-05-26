package dashboard.query

import agent.batch.enums.Severity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RealtimeEvent(

    val type: RealtimeType,
    val projectId: String,
    val serverId: String? = null,
    val entityId: String? = null,
    val entityType: String? = null,
    val severity: Severity? = null,
    val metric: String? = null,
    val event: String? = null,
    val payload: JsonElement,
    val timestamp: Long =
        System.currentTimeMillis()
)