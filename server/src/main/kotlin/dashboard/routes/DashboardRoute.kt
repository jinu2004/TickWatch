package dashboard.routes

import agent.batch.enums.AggregationType
import agent.batch.enums.LogLevel
import agent.batch.enums.Severity
import dashboard.query.EventFilter
import dashboard.query.LogFilter
import dashboard.query.MetricFilter
import dashboard.query.SuspicionFilter
import database.agent.repository.*
import database.projects.ProjectDataRepository
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class DashboardRoute(
    private val projectDataRepository: ProjectDataRepository,
    private val suspicionDataRepository: SuspicionDataRepository,
    private val rawBatchDataRepository: RawBatchDataRepository,
    private val metricDataRepository: MetricDataRepository,
    private val eventsDataRepository: EventsDataRepository,
    private val logsDataRepository: LogsDataRepository
) {
    fun Route.suspicionData(){
        post("/dashboard/suspicion"){

            val requestProjectID = call.request.headers["tw_project_id"]?:run {
                HttpStatusCode.BadGateway
                return@post
            }
            val project = projectDataRepository.getProjectById(requestProjectID) ?: return@post
            val filter =
                SuspicionFilter(
                    severity = call.parameters["severity"]?.let { Severity.valueOf(it) },

                    entityId = call.parameters["entityId"],

                    serverId = call.parameters["serverId"],

                    resolved = call.parameters["resolved"]?.toBoolean(),

                    limit = call.parameters["limit"]?.toIntOrNull() ?: 50,

                    offset = call.parameters["offset"]?.toLongOrNull() ?: 0
                )

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
            val project = projectDataRepository.getProjectById(requestProjectID) ?: return@post
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

            val result = metricDataRepository.getMetrics(project.id, filter)
            call.respond(result)
        }
    }

    fun Route.eventData(){
        post("/dashboard/events"){

            val requestProjectID = call.request.headers["tw_project_id"]?:run {
                HttpStatusCode.BadGateway
                return@post
            }
            val project = projectDataRepository.getProjectById(requestProjectID) ?: return@post

            val filter =
                EventFilter(
                    event = call.parameters["event"],
                    entityId = call.parameters["entityId"],
                    entityType = call.parameters["entityType"],
                    serverId = call.parameters["serverId"],
                    fromTimestamp =call.parameters["fromTimestamp"]?.toLongOrNull(),
                    toTimestamp = call.parameters["toTimestamp"]?.toLongOrNull(),
                    limit = call.parameters["limit"]?.toIntOrNull() ?: 100,
                    offset = call.parameters["offset"]?.toLongOrNull() ?: 0
                )
            val result = eventsDataRepository.getEvents(project.id,filter)
            call.respond(result)

        }
    }

    fun Route.logData(){
        post("/dashboard/logs"){
            val requestProjectID = call.request.headers["tw_project_id"]?:run {
                HttpStatusCode.BadGateway
                return@post
            }
            val project = projectDataRepository.getProjectById(requestProjectID) ?: return@post

            val filter =
                LogFilter(
                    level = call.parameters["level"]?.let { LogLevel.valueOf(it) },
                    serverId = call.parameters["serverId"],
                    search = call.parameters["search"],
                    fromTimestamp = call.parameters["fromTimestamp"]?.toLongOrNull(),
                    toTimestamp = call.parameters["toTimestamp"]?.toLongOrNull(),
                    limit = call.parameters["limit"]?.toIntOrNull() ?: 100,
                    offset = call.parameters["offset"]?.toLongOrNull() ?: 0
                )
            val result = logsDataRepository.getLogs(project.id,filter)
            call.respond(result)
        }
    }

}