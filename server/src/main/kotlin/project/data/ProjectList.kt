package project.data

import database.projects.Project
import kotlinx.serialization.Serializable

@Serializable
data class ProjectList(val list: List<Project>?)
