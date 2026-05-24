package jwt_token.token

data class TokenClaims(
    val name: TokenName,
    val value: String,
)
