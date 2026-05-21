package auth.security.token

interface TokenService {
    fun generate(config: TokenConfig, vararg claims: TokenClaims): String
    fun verify(token: String, config: TokenConfig): Boolean
}