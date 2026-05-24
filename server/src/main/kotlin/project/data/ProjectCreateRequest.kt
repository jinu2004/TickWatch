package project.data

import kotlinx.serialization.Serializable

@Serializable
data class ProjectCreateRequest(val name: String)
