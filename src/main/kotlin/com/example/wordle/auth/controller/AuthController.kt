package com.example.wordle.auth.controller

import com.example.wordle.auth.dto.SignupRequest
import com.example.wordle.auth.dto.SignupResponse
import com.example.wordle.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(@Valid @RequestBody request: SignupRequest): SignupResponse {
        return authService.signup(request)
    }
}
