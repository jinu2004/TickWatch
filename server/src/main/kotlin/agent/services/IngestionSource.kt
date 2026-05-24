package agent.services

import agent.batch.AgentBatchRequest

class IngestionSource: IngestionService {
    override suspend fun process(projectId: String, request: AgentBatchRequest) {
        println(
            "Project: $projectId"
        )

        println(
            "Metrics: ${request.metrics.size}"
        )

        println(
            "Events: ${request.events.size}"
        )

        println(
            "Suspicion: ${request.suspicionEvent.size}"
        )

        println(
            "Logs: ${request.logs.size}"
        )
    }
}