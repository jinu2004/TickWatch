package agent.services

import agent.batch.AgentBatchRequest
import database.agent.repository.*
import database.agent.tables.ProcessingStatus
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

class IngestionSource(
    private val rawBatchDataRepository: RawBatchDataRepository,
    private val metricDataRepository: MetricDataRepository,
    private val eventsDataRepository: EventsDataRepository,
    private val suspicionDataRepository: SuspicionDataRepository,
    private val logsDataRepository: LogsDataRepository,
) : IngestionService {

    override suspend fun process(projectId: String, request: AgentBatchRequest) {
        val batchId: UUID = rawBatchDataRepository.save(projectId, request)
        runCatching {
            coroutineScope {
                launch {
                    metricDataRepository.saveBatch(
                        batchId,
                        projectId,
                        request.serverId,
                        request.metrics
                    )
                }
                launch {
                    eventsDataRepository.saveBatch(
                        batchId,
                        projectId,
                        request.serverId,
                        request.events
                    )
                }
                launch {
                    suspicionDataRepository.saveBatch(
                        batchId,
                        projectId,
                        request.serverId,
                        request.suspicionEvent
                    )
                }
                launch {
                    logsDataRepository.saveBatch(
                        batchId,
                        projectId,
                        request.serverId,
                        request.logs
                    )
                }
            }

        }.onSuccess {
            rawBatchDataRepository.updateStatus(batchId, ProcessingStatus.COMPLETED)
        }.onFailure {
            rawBatchDataRepository.updateStatus(batchId, ProcessingStatus.FAILED)
        }
    }

}