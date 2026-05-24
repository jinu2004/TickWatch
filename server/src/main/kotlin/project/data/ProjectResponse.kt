package project.data

import kotlinx.serialization.Serializable

@Serializable
data class ProjectResponse(val id:String,val name: String, val token: String)