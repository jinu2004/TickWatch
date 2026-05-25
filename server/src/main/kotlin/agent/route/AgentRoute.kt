package agent.route

import agent.batch.AgentBatchRequest
import agent.services.IngestionService
import database.agent.repository.EventsDataRepository
import database.agent.repository.LogsDataRepository
import database.agent.repository.MetricDataRepository
import database.agent.repository.RawBatchDataRepository
import database.agent.repository.SuspicionDataRepository
import database.projects.ProjectDataRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class AgentRoute(
    private val projectDataRepository: ProjectDataRepository,
    private val ingestionService: IngestionService,
    private val suspicionDataRepository: SuspicionDataRepository,
    private val rawBatchDataRepository: RawBatchDataRepository,
    private val metricDataRepository: MetricDataRepository,
    private val eventsDataRepository: EventsDataRepository,
    private val logsDataRepository: LogsDataRepository

) {

    fun Route.ingestRoute() {
        post("/ingest") {
            val apikey = call.request.headers["tw-api-key"] ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            val project = projectDataRepository.getProjectByApiKey(apikey) ?: run {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }


            val request = call.receiveNullable<AgentBatchRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            ingestionService.process(projectId = project.id, request)
            call.respond(HttpStatusCode.OK)


        }
    }

    fun Route.suspicionData(){
        post("/dashboard/suspicion"){

            val requestProjectID = call.request.headers["tw_project_id"]?:run {
                HttpStatusCode.BadGateway
                return@post
            }

            val project = projectDataRepository.getProjectById(requestProjectID) ?: return@post


            val result = suspicionDataRepository.getLatest(project.id)
            call.respond(result)

        }
    }


}