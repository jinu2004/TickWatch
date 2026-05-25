package dashboard.query

import agent.batch.enums.LogLevel
import kotlinx.serialization.Serializable

@Serializable
data class LogFilter(
    val level: LogLevel? = null,
    val serverId: String? = null,
    val search: String? = null,
    val fromTimestamp: Long? = null,
    val toTimestamp: Long? = null,
    val limit: Int = 100,
    val offset: Long = 0
)