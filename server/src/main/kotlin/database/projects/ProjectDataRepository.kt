package database.projects

interface ProjectDataRepository {
    suspend fun createNewProject(project: Project): Boolean
    suspend fun updateProject(project: Project): Boolean
    suspend fun deleteProject(projectId: String): Boolean
    suspend fun getAllProjectByUserId(userId: String): List<Project>?

    suspend fun getProjectByApiKey(key: String): Project?
    suspend fun getProjectById(key: String): Project?


}