package auth.security.token

data class TokenClaims(
    val name: TokenName,
    val value: String,
)
