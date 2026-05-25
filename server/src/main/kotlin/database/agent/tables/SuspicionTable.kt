package database.agent.tables

import agent.batch.enums.Severity
import org.jetbrains.exposed.v1.core.dao.id.java.UUIDTable
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)

object SuspicionTable :
    UUIDTable("suspicion_alerts") {

    val projectId =
        varchar("project_id",128)

    val serverId =
        varchar("server_id", 100)

    val batchId =
        uuid("batch_id")

    val eventId =
        varchar("event_id", 100)

    val entityId =
        varchar("entity_id", 100)

    val entityType =
        varchar("entity_type", 50)

    val ruleName =
        varchar("rule_name", 100)

    val reason =
        text("reason")

    val value =
        double("value")
            .nullable()

    val threshold =
        double("threshold")
            .nullable()

    val severity =
        enumerationByName(
            "severity",
            20,
            Severity::class
        )

    val metadata =
        text("metadata")

    val timestamp =
        long("timestamp")

    val resolved =
        bool("resolved")
            .default(false)

    init {
        index(false, projectId)
        index(false, severity)
        index(false, timestamp)
        index(false, entityId)
    }
}