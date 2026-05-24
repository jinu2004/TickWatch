package project

import java.security.SecureRandom
import java.util.Base64

object ApiTokenGenerator {

    private val secureRandom = SecureRandom()

    fun generate(): String {
        val bytes = ByteArray(32)
        secureRandom.nextBytes(bytes)

        val token = Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(bytes)

        return "tw_live_$token"
    }
}