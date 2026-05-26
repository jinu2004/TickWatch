package agent.services

import agent.batch.AgentBatchRequest
import dashboard.RealtimeEventBus
import dashboard.query.RealtimeEvent
import dashboard.query.RealtimeType
import database.agent.repository.*
import database.agent.tables.ProcessingStatus
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.util.*

class IngestionSource(
    private val rawBatchDataRepository: RawBatchDataRepository,
    private val metricDataRepository: MetricDataRepository,
    private val eventsDataRepository: EventsDataRepository,
    private val suspicionDataRepository: SuspicionDataRepository,
    private val logsDataRepository: LogsDataRepository,
    private val realtimeEventBus: RealtimeEventBus
) : IngestionService {

    override suspend fun process(projectId: String, request: AgentBatchRequest) {
        val batchId: UUID = rawBatchDataRepository.save(projectId, request)
        runCatching {
            supervisorScope {
                launch {
                    metricDataRepository.saveBatch(
                        batchId,
                        projectId,
                        request.serverId,
                        request.metrics
                    )

                    request.metrics.forEach { data ->

                        realtimeEventBus.publish(RealtimeEvent(
                            type = RealtimeType.METRIC,
                            payload = Json.encodeToJsonElement(data),
                            serverId = request.serverId,
                            projectId = request.projectId,
                            entityId = data.entityId,
                            entityType = data.entityType,
                            metric = data.metric,
                            timestamp = System.currentTimeMillis(),
                        )
                        )
                    }

                }
                launch {
                    eventsDataRepository.saveBatch(
                        batchId,
                        projectId,
                        request.serverId,
                        request.events
                    )

                    request.events.forEach { data->

                        realtimeEventBus.publish(RealtimeEvent(RealtimeType.EVENT,
                            payload = Json.encodeToJsonElement(data),
                            serverId = request.serverId,
                            projectId = request.projectId,
                            entityId = data.entityId,
                            event = data.event,
                            entityType = data.entityType,
                            timestamp = System.currentTimeMillis()
                        ))
                    }
                }
                launch {
                    suspicionDataRepository.saveBatch(
                        batchId,
                        projectId,
                        request.serverId,
                        request.suspicionEvent
                    )

                    request.suspicionEvent.forEach { data->

                        realtimeEventBus.publish(RealtimeEvent(RealtimeType.SUSPICION,
                            payload = Json.encodeToJsonElement(data),
                            serverId = request.serverId,
                            projectId = request.projectId,
                            entityId = data.entityId,
                            entityType = data.entityType,
                            severity = data.severity,
                            timestamp = System.currentTimeMillis()))
                    }

                }
                launch {
                    logsDataRepository.saveBatch(
                        batchId,
                        projectId,
                        request.serverId,
                        request.logs
                    )

                    request.logs.forEach { data->

                        realtimeEventBus.publish(RealtimeEvent(RealtimeType.LOG,
                            payload = Json.encodeToJsonElement(data),
                            serverId = request.serverId,
                            projectId = request.projectId,
                            timestamp = System.currentTimeMillis()))
                    }

                }
            }

        }.onSuccess {
            rawBatchDataRepository.updateStatus(batchId, ProcessingStatus.COMPLETED)
        }.onFailure {
            rawBatchDataRepository.updateStatus(batchId, ProcessingStatus.FAILED)
        }

    }

}