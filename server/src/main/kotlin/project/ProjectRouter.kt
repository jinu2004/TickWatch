package project

import auth.data.requireUser
import database.projects.Project
import database.projects.ProjectDataRepository
import database.user.UserDatabaseRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import project.data.ProjectCreateRequest
import project.data.ProjectList
import project.data.ProjectResponse
import java.time.Instant
import java.util.*

class ProjectRouter(
    private  val userDatabaseRepository: UserDatabaseRepository,
    private  val projectDataRepository: ProjectDataRepository)
{
    fun Route.createProject(){
        authenticate {
            post("/createNewProject") {
                val user = call.requireUser(userDatabaseRepository)
                val request = call.receiveNullable<ProjectCreateRequest>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }
                val api = ApiTokenGenerator.generate()
                val project = Project(
                    id = UUID.randomUUID().toString(), userId = user.userid, projectName = request.name, apiToken = api,
                    createdAt = Instant.now()
                )
                projectDataRepository.createNewProject(project)
                call.respond(
                    HttpStatusCode.Created,
                    ProjectResponse(
                        id = project.id,
                        name = project.projectName,
                        token = api
                    )
                )
            }
        }

    }

    fun Route.getAllProject(){
        authenticate {
            get("list_projects"){
                val user = call.requireUser(userDatabaseRepository)
                val list = projectDataRepository.getAllProjectByUserId(user.userid)
                call.respond(HttpStatusCode.OK, ProjectList(list))



            }
        }
    }




}