package com.example.wordle.auth.dto

import java.util.*

data class SignupResponse(
    val id: UUID,
    val username: String,
    val email: String,
    val message: String = "회원가입이 완료되었습니다"
)
