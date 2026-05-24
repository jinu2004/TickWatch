package auth.data

import database.user.User
import database.user.UserDatabaseService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond

suspend fun ApplicationCall.authenticateUser(userDatabaseService: UserDatabaseService): User?{
    val principal = principal<JWTPrincipal>()
        ?: return null

    val type = principal.getClaim(
        "type",
        String::class
    )

    if (type != "access") {
        return null
    }

    val userId = principal.getClaim(
        "id",
        String::class
    ) ?: return null

    return userDatabaseService
        .getUserByUserId(userId)
}

suspend fun ApplicationCall.requireUser(
    userDatabaseService: UserDatabaseService
): User {

    return authenticateUser(
        userDatabaseService
    ) ?: run {
        respond(
            HttpStatusCode.Unauthorized,
            "Unauthorized"
        )
        throw IllegalStateException()
    }
}