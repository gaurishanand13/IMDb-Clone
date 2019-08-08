package com.example.myproject.loginDetails

data class tokenDataClass(
    val expires_at: String,
    val request_token: String,
    val success: Boolean
)

data class loginValidityCheck(
    val username: String,
    val password: String,
    val request_token: String

)


data class answerOfLoginDetailsValidity(
    val expires_at: String,
    val request_token: String,
    val success: Boolean
)

data class sessionIdDetails(
    val success: Boolean,
    val session_id: String
)

data class sessionIDDelete(
    val success : Boolean
)
