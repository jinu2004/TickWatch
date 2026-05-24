package agent.route

import agent.batch.AgentBatchRequest
import agent.services.IngestionService
import database.projects.ProjectDataService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class AgentRoute(private val projectDataService: ProjectDataService, private val ingestionService: IngestionService) {

    fun Route.ingestRoute() {
        post("/ingest") {
            val apikey = call.request.headers["tw-api-key"] ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            val project = projectDataService.getProjectByApiKey(apikey) ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }


            val request = call.receiveNullable<AgentBatchRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            ingestionService.process(projectId = project.id, request)


        }


    }


}