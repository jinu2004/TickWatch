package jwt_token.token

enum class TokenType(val value: String) {
    ACCESS_TOKEN("access"),REFRESH_TOKEN("refresh"),API("api")
}

enum class TokenName(val value: String){
    ID("id"),TYPE("type")
}
