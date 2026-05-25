package agent.query

import agent.batch.enums.Severity
import kotlinx.serialization.Serializable

@Serializable
data class SuspicionFilter(
    val severity: Severity? = null,
    val entityId: String? = null,
    val serverId: String? = null,
    val resolved: Boolean? = null,
    val limit: Int = 50,
    val offset: Long = 0
)