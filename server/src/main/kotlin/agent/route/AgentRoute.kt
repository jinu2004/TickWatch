package agent.route

import agent.batch.AgentBatchRequest
import agent.batch.enums.AggregationType
import agent.batch.enums.Severity
import agent.query.MetricFilter
import agent.query.SuspicionFilter
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
import io.ktor.util.collections.setValue

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
            val filter =
                SuspicionFilter(
                    severity = call.parameters["severity"]?.let { Severity.valueOf(it) },

                    entityId = call.parameters["entityId"],

                    serverId = call.parameters["serverId"],

                    resolved = call.parameters["resolved"]?.toBoolean(),

                    limit = call.parameters["limit"]?.toIntOrNull() ?: 50,

                    offset = call.parameters["offset"]?.toLongOrNull() ?: 0
                )


            val project = projectDataRepository.getProjectById(requestProjectID) ?: return@post

            val result = suspicionDataRepository.getFiltered(project.id,filter)
            call.respond(result)

        }
    }

    fun Route.metricsData(){
        post("/dashboard/metrics") {

            val requestProjectID = call.request.headers["tw_project_id"]?:run {
                HttpStatusCode.BadGateway
                return@post
            }

            val filter =
                MetricFilter(

                    metric = call.parameters["metric"],
                    entityId = call.parameters["entityId"],
                    entityType = call.parameters["entityType"],
                    serverId = call.parameters["serverId"],
                    aggregationType = call.parameters["aggregationType"]?.let { AggregationType.valueOf(it) },
                    fromTimestamp = call.parameters["fromTimestamp"]?.toLongOrNull(),
                    toTimestamp = call.parameters["toTimestamp"]?.toLongOrNull(),
                    limit = call.parameters["limit"]?.toIntOrNull() ?: 100
                )
            val project = projectDataRepository.getProjectById(requestProjectID) ?: return@post
            val result = metricDataRepository.getMetrics(project.id, filter)
            call.respond(result)
        }
    }







}