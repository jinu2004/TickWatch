package database.agent.tables

import kotlinx.serialization.Serializable

@Serializable
enum class ProcessingStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}