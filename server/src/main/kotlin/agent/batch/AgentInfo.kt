package agent.batch

import kotlinx.serialization.Serializable

@Serializable
data class AgentInfo(
    val serverName: String,
    val agentVersion: String,
    val os: String,
    val arch: String
)
