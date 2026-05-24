package agent.batch

import kotlinx.serialization.Serializable


@Serializable
data class AgentBatchRequest(
    val projectId: String,
    val serverId: String,
    val timestamp: Long,
    val agent: AgentInfo,
    val systemMetrics: SystemMetrics? = null,
    val metrics: List<MetricPayload> = emptyList(),
    val suspicionEvent: List<SuspicionEvent> = emptyList(),
    val events: List<EventPayload> = emptyList(),
    val logs: List<LogPayload> = emptyList()
)