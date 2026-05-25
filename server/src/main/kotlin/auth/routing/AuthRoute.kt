package auth.routing

import jwt_token.hashing.HashingService
import jwt_token.hashing.SaltedHash
import auth.data.AuthResponse
import auth.data.AuthSignInRequest
import auth.data.AuthSignUpRequest
import auth.data.requireUser
import database.user.User
import database.user.UserDatabaseRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.konform.validation.Validation
import io.konform.validation.constraints.maxLength
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.pattern
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jwt_token.token.TokenClaims
import jwt_token.token.TokenConfig
import jwt_token.token.TokenName
import jwt_token.token.TokenService
import jwt_token.token.TokenType
import java.util.UUID


class AuthRoute(
    private val tokenConfig: TokenConfig,
    private val tokenService: TokenService,
    private val hashingService: HashingService,
    private val userDataSource: UserDatabaseRepository
) {

    fun Route.signUp() {
        post("/signup") {
            val request = call.receiveNullable<AuthSignUpRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val validateUser = Validation {
                AuthSignUpRequest::password  {
                    minLength(8)
                    pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!#%*?&])[A-Za-z\\d@$!%#*?&]{8,}$")
                }
                AuthSignUpRequest::username {
                    minLength(4)
                    maxLength(200)
                }
            }.validate(request)

            validateUser.errors.forEach { it ->
                it.message.let { call.respond(HttpStatusCode.BadRequest, it) }
            }

            if(validateUser.isValid){
                val user = userDataSource.getUserByEmail(request.email)
                if (user != null) {
                    call.respond(HttpStatusCode.Conflict,"This email is already existing")
                    return@post
                }
            }

            if (validateUser.isValid) {
                val saltedHash = hashingService.generateSaltedHash(request.password)
                val user = User(
                    email = request.email,
                    password = saltedHash.hash,
                    salt = saltedHash.salt,
                    username = request.username,
                    userid = UUID.randomUUID().toString(),
                )

                val wasAcknowledged = userDataSource.insertNewUser(user)

                if (wasAcknowledged) {
                    val accessToken = tokenService.generate(
                        config = tokenConfig,
                        claims = arrayOf(
                            TokenClaims(
                                name = TokenName.ID,
                                value = user.userid
                            ),
                            TokenClaims(
                                name = TokenName.TYPE,
                                value = TokenType.ACCESS_TOKEN.value
                            )
                        )
                    )

                    val refreshToken = tokenService.generate(
                        config = tokenConfig,
                        claims = arrayOf(
                            TokenClaims(
                                name = TokenName.ID,
                                value = user.userid
                            ),
                            TokenClaims(
                                name = TokenName.TYPE,
                                value = TokenType.REFRESH_TOKEN.value
                            )
                        )
                    )
                    call.respond(
                        HttpStatusCode.OK, message = AuthResponse(
                            accessToken,
                            refreshToken
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            }


        }
    }

    fun Route.signIn() {
        post("/signing") {
            val request = call.receiveNullable<AuthSignInRequest>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val user = userDataSource.getUserByEmail(request.email)

            application.log.info(user.toString())
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, "Invalid email or password")
                return@post
            }

            val isValidPassword = hashingService.verify(
                request.password,
                SaltedHash(user.password, user.salt)
            )

            if (!isValidPassword) {
                call.respond(HttpStatusCode.BadGateway, message = "Invalid password")
                return@post
            }

            val accessToken = tokenService.generate(
                config = tokenConfig,
                claims = arrayOf(
                    TokenClaims(
                        name = TokenName.ID,
                        value = user.userid
                    ),
                    TokenClaims(
                        name = TokenName.TYPE,
                        value = TokenType.ACCESS_TOKEN.value
                    )
                )
            )

            val refreshToken = tokenService.generate(
                config = tokenConfig,
                claims = arrayOf(
                    TokenClaims(
                        name = TokenName.ID,
                        value = user.userid
                    ),
                    TokenClaims(
                        name = TokenName.TYPE,
                        value = TokenType.REFRESH_TOKEN.value
                    )
                )
            )




            call.respond(
                HttpStatusCode.OK, message = AuthResponse(
                    accessToken,
                    refreshToken
                )
            )


        }

    }

    fun Route.authenticate() {
        authenticate {
            post("/authenticate") {
                val user = call.requireUser(userDataSource)
                call.respond(HttpStatusCode.OK)
            }
        }
    }

    fun Route.getUserId() {
        authenticate {
            post("/userid") {
                val user = call.requireUser(userDataSource)
                call.respond(HttpStatusCode.OK, user.userid)
            }
        }
    }

    fun Route.refreshToken() {
        authenticate {
            post("/refresh") {

                val principal = call.principal<JWTPrincipal>()

                val userId =
                    principal?.getClaim(TokenName.ID.value, String::class)
                val type =
                    principal?.getClaim(TokenName.TYPE.value, String::class)

                if (type != TokenType.REFRESH_TOKEN.value) {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token type")
                    return@post
                }

                val newAccessToken = tokenService.generate(
                    config = tokenConfig,
                    claims = arrayOf(
                        TokenClaims(
                            TokenName.ID,
                            userId!!
                        ),
                        TokenClaims(
                            TokenName.TYPE,
                            TokenType.ACCESS_TOKEN.value
                        )
                    )
                )

                call.respond(
                    HttpStatusCode.OK,
                    mapOf("accessToken" to newAccessToken)
                )
            }
        }
    }

}
