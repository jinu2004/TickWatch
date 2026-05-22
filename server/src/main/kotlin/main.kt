import di.configureDI
import di.configureMonitoring
import di.configureRoute
import di.configureSecurity
import di.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}
