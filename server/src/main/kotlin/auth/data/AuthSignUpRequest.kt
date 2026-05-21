package auth.data

import kotlinx.serialization.Serializable

@Serializable
data class AuthSignUpRequest(val email: String, val password: String, val username: String)
