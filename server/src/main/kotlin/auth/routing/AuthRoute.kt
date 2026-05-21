package auth.routing

import auth.security.hashing.HashingService
import auth.security.hashing.SaltedHash
import auth.security.token.*
import auth.data.AuthResponse
import auth.data.AuthSignInRequest
import auth.data.AuthSignUpRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*



class AuthRoute(
    private val tokenConfig: TokenConfig,
    private val tokenService: TokenService,
    private val hashingService: HashingService,
    private val userDataSource: com.nxblackstudio.infrastructure.database.auth_data.UserDataSource,
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
                val user = _root_ide_package_.com.nxblackstudio.infrastructure.database.auth_data.user.UserData(
                    id = ObjectId().toString(),
                    email = request.email,
                    password = saltedHash.hash,
                    salt = saltedHash.salt,
                    username = request.username,
                )

                val wasAcknowledged = userDataSource.insertUser(user)

                if (wasAcknowledged) {
                    val accessToken = tokenService.generate(
                        config = tokenConfig,
                        claims = arrayOf(
                            TokenClaims(
                                name = TokenName.ID,
                                value = user.id
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
                                value = user.id
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
                        value = user.id
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
                        value = user.id
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
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim("id", String::class)
                val type = principal?.getClaim("type", String::class)
                if (type != "access") {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token type")
                    return@post
                }

                val user = userDataSource.getUserById(userId)

                if (user == null) {
                    call.respond(HttpStatusCode.Unauthorized, "User not Found")
                    return@post
                }

                call.respond(HttpStatusCode.OK)
            }
        }
    }

    fun Route.getUserId() {
        authenticate {
            post("/userid") {

                val principal = call.principal<JWTPrincipal>()

                val userId = principal?.getClaim("id", String::class)
                val type = principal?.getClaim("type", String::class)

                if (type != "access") {
                    call.respond(HttpStatusCode.Unauthorized, "Invalid token type")
                    return@post
                }

                call.respond(HttpStatusCode.OK, userId ?: "No userId")
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
