package agent.batch

import agent.batch.enums.AggregationType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class SystemMetrics(
    val cpuUsage: Double? = null,
    val ramUsedMb: Long? = null,
    val diskPercent: Double? = null,
    val networkInKbps: Double? = null,
    val networkOutKbps: Double? = null,
    val uptimeSeconds: Long? = null,
    val custom: Map<String, JsonElement> = emptyMap()
)

