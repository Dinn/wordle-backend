package com.example.wordle.auth.service

import com.example.wordle.auth.domain.User
import com.example.wordle.auth.dto.SignupRequest
import com.example.wordle.auth.dto.SignupResponse
import com.example.wordle.auth.repository.UserRepository
import com.example.wordle.common.exception.DuplicateResourceException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    
    @Transactional
    fun signup(request: SignupRequest): SignupResponse {
        // 중복 검사
        if (userRepository.findByUsername(request.username) != null) {
            throw DuplicateResourceException("이미 존재하는 사용자명입니다")
        }
        
        if (userRepository.findByEmail(request.email) != null) {
            throw DuplicateResourceException("이미 존재하는 이메일입니다")
        }
        
        // 사용자 생성
        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            email = request.email
        )
        
        val savedUser = userRepository.save(user)
        
        return SignupResponse(
            id = savedUser.id,
            username = savedUser.username,
            email = savedUser.email
        )
    }
}
