package auth.security.token

enum class TokenType(val value: String) {
    ACCESS_TOKEN("access"),REFRESH_TOKEN("refresh")
}

enum class TokenName(val value: String){
    ID("id"),TYPE("type")
}
