package agent.batch.enums

import kotlinx.serialization.Serializable

@Serializable
enum class VisualTypes {
    NUMBER, TABLE, LINE_CHART, PIE_CHART, STATUS
}