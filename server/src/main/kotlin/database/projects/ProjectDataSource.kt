package database.projects

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update

class ProjectDataSource(private val database: R2dbcDatabase): ProjectDataService {
    override suspend fun createNewProject(project: Project): Boolean {
        return suspendTransaction(database){

            ProjectTable.insert {
                it[id] = project.id
                it[userId] = project.userId
                it[projectName] = project.projectName
                it[apiToken] = project.apiToken
                it[createdAt] = project.createdAt
            }.insertedCount > 0
        }
    }

    override suspend fun updateProject(project: Project): Boolean {
        return suspendTransaction(database) {
            ProjectTable.update({ ProjectTable.id eq project.id})
            {
                it[id] = project.id
                it[userId] = project.userId
                it[projectName] = project.projectName
                it[apiToken] = project.apiToken
            } > 0
        }
    }

    override suspend fun deleteProject(projectId: String): Boolean {

        return suspendTransaction(database) {
            ProjectTable.deleteWhere { ProjectTable.id eq projectId } > 0
        }
    }

    override suspend fun getAllProjectByUserId(userId: String): List<Project> {
        return suspendTransaction(database) {
            ProjectTable.selectAll()
                .where{ ProjectTable.userId eq userId }
                .map { it->
                    Project(
                        id = it[ProjectTable.id],
                        userId = it[ProjectTable.userId],
                        projectName = it[ProjectTable.projectName],
                        apiToken = it[ProjectTable.apiToken],
                        createdAt = it[ProjectTable.createdAt]
                    )
                }.toList()
        }
    }

    override suspend fun getProjectByApiKey(key: String): Project?{
        return suspendTransaction(database) {
            ProjectTable.selectAll()
                .where{ ProjectTable.apiToken eq key}
                .map { it->
                    Project(
                        id = it[ProjectTable.id],
                        userId = it[ProjectTable.userId],
                        projectName = it[ProjectTable.projectName],
                        apiToken = it[ProjectTable.apiToken],
                        createdAt = it[ProjectTable.createdAt]
                    )
                }.singleOrNull()
        }
    }
}