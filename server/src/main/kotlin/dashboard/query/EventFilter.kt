package dashboard.query

import kotlinx.serialization.Serializable

@Serializable
data class EventFilter(
    val event: String? = null,
    val entityId: String? = null,
    val entityType: String? = null,
    val serverId: String? = null,
    val fromTimestamp: Long? = null,
    val toTimestamp: Long? = null,
    val limit: Int = 100,
    val offset: Long = 0
)