package jwt_token.hashing

data class SaltedHash(val hash: String, val salt: String)
