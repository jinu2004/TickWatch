package di

import auth.routing.AuthRoute
import auth.security.hashing.HashingService
import auth.security.hashing.SHA256Hashing
import auth.security.token.JwtTokenService
import auth.security.token.TokenConfig
import auth.security.token.TokenService
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import database.user.UserDatabaseService
import database.user.UserDatabaseSource
import database.user.UserTable
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.path
import io.ktor.server.routing.routing
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.transactions.transactionScope
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.transactions.transactionManager
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level


private fun module(environment: ApplicationEnvironment) = module{
    single {
        R2dbcDatabase.connect(
            url = "r2dbc:postgresql://localhost:5432/test",
            driver = "postgresql",
            user = "postgres",
            password = ""
        )
    }

    single<UserDatabaseService>{ UserDatabaseSource(get()) }
    single {
        TokenConfig(
            issuer = environment.config.propertyOrNull("jwt.issuer")?.getString() ?: "http://0.0.0.0:8080",
            audience = environment.config.propertyOrNull("jwt.audience")?.getString() ?: "http://0.0.0.0:8080",
            expiresIn = 365L * 1000L * 60L * 60L * 24L,
            secret = System.getenv("JWT_SECRET") ?: "fuck you"
        )
    }
    single<TokenService> { JwtTokenService() }
    single<HashingService> { SHA256Hashing() }

    single { AuthRoute(get(),get(),get(),get()) }

}

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

fun Application.configureDatabase(){
    val database by inject<R2dbcDatabase>()

    runBlocking{
            suspendTransaction(database){
                SchemaUtils.create(UserTable)
            }
        }
}


fun Application.configureSecurity() {
    val tokenConfig by inject<TokenConfig>()
    install(Authentication) {
        jwt {
            realm = "ktor_sample_app"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(tokenConfig.secret))
                    .withAudience(tokenConfig.audience)
                    .withIssuer(tokenConfig.issuer)
                    .acceptExpiresAt(tokenConfig.expiresIn)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(tokenConfig.audience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}

fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(module(environment = environment))
    }

}

fun Application.configureRoute(){
    val authRoute by inject<AuthRoute>()
    routing {
        authRoute.apply {
            signIn()
            signUp()
            refreshToken()
            authenticate()
            getUserId()
        }
    }

}












