package agent.services

import agent.batch.AgentBatchRequest

interface IngestionService {
    suspend fun process(projectId: String, request: AgentBatchRequest)
}