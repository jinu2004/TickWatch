package dashboard.query

import kotlinx.serialization.Serializable

@Serializable
enum class RealtimeType {
    METRIC,
    EVENT,
    SUSPICION,
    LOG
}