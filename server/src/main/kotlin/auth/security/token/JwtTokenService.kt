package auth.security.token

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

class JwtTokenService : TokenService {
    override fun generate(
        config: TokenConfig,
        vararg claims: TokenClaims
    ): String {
        val tokenType = claims.find { it.name == TokenName.TYPE }?.value
            ?: TokenType.ACCESS_TOKEN.value

        val expiry = when (tokenType) {
            TokenType.ACCESS_TOKEN.value ->
                Date(System.currentTimeMillis() + 60 * 60 * 1000)

            TokenType.REFRESH_TOKEN.value ->
                Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000L)

            else -> throw IllegalArgumentException("Invalid token type")
        }

        val builder = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(expiry)

        claims.forEach { claim ->
            builder.withClaim(claim.name.value, claim.value)
        }

        return builder.sign(Algorithm.HMAC256(config.secret))
    }

    override fun verify(
        token: String,
        config: TokenConfig
    ): Boolean {
        return  try {
            JWT.require(Algorithm.HMAC256(config.secret))
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build().verify(token)
            true
        } catch (e: Exception){
            false
        }

    }
}