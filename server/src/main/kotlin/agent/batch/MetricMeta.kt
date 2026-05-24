package agent.batch

import kotlinx.serialization.Serializable

@Serializable
data class MetricMeta(
    val visual: String? = null,
    val min: Double? = null,
    val max: Double? = null,
    val unit: String? = null,
    val flags: List<String> = emptyList()
)