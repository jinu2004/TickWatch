package dashboard.routes

import agent.batch.enums.AggregationType
import agent.batch.enums.LogLevel
import agent.batch.enums.Severity
import dashboard.RealtimeEventBus
import dashboard.query.EventFilter
import dashboard.query.LiveFilter
import dashboard.query.LogFilter
import dashboard.query.MetricFilter
import dashboard.query.RealtimeEvent
import dashboard.query.RealtimeType
import dashboard.query.SuspicionFilter
import database.agent.repository.*
import database.projects.ProjectDataRepository
import io.ktor.http.*
import io.ktor.server.application.log
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.sse
import io.ktor.sse.ServerSentEvent
import kotlinx.serialization.json.Json

class DashboardRoute(
    private val projectDataRepository: ProjectDataRepository,
    private val suspicionDataRepository: SuspicionDataRepository,
    private val rawBatchDataRepository: RawBatchDataRepository,
    private val metricDataRepository: MetricDataRepository,
    private val eventsDataRepository: EventsDataRepository,
    private val logsDataRepository: LogsDataRepository,
    private val realtimeEventBus: RealtimeEventBus
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

    fun Route.liveData(){
            sse("/dashboard/live") {
                val requestProjectID = call.parameters["tw_project_id"] ?: run {
                    HttpStatusCode.BadGateway
                    return@sse
                }

                val project = projectDataRepository.getProjectById(requestProjectID) ?:run {
                    HttpStatusCode.Unauthorized
                    return@sse
                }

                val filter =
                    LiveFilter(
                        type = call.parameters["type"]?.let { RealtimeType.valueOf(it) },
                        serverId = call.parameters["serverId"],
                        entityId = call.parameters["entityId"],
                        severity = call.parameters["severity"]?.let { Severity.valueOf(it)},
                        metric = call.parameters["metric"],
                        event = call.parameters["event"]
                    )


                send(
                    ServerSentEvent(
                        data = "connected"
                    )
                )

                println(
                    realtimeEventBus.hashCode()
                )


                realtimeEventBus.events.collect { rtEvents ->
                    println(rtEvents.projectId)
                    println(project.id)

                    if (rtEvents.projectId != project.id) return@collect
                    if (!matchesFilter(rtEvents, filter)) return@collect
                    send(
                        ServerSentEvent(data = Json.encodeToString(rtEvents))
                    )


            }

        }
    }










    private fun matchesFilter(event: RealtimeEvent, filter: LiveFilter): Boolean {
        if (
            filter.type != null &&
            event.type != filter.type
        ) return false

        if (
            filter.serverId != null &&
            event.serverId !=
            filter.serverId
        ) return false

        if (
            filter.entityId != null &&
            event.entityId !=
            filter.entityId
        ) return false

        if (
            filter.severity != null &&
            event.severity !=
            filter.severity
        ) return false

        if (
            filter.metric != null &&
            event.metric !=
            filter.metric
        ) return false

        if (
            filter.event != null &&
            event.event !=
            filter.event
        ) return false

        return true
    }


}