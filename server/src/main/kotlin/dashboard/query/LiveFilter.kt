package dashboard.query

import agent.batch.enums.Severity

data class LiveFilter(
    val type: RealtimeType? = null,
    val serverId: String? = null,
    val entityId: String? = null,
    val severity: Severity? = null,
    val metric: String? = null,
    val event: String? = null
)
