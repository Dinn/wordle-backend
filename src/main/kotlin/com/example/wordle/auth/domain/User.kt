package com.example.wordle.auth.domain

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Id
    val id: UUID = UUID.randomUUID(),
    
    @Column(unique = true, nullable = false, length = 20)
    private val username: String,
    
    @Column(nullable = false, length = 60)
    private val password: String,
    
    @Column(unique = true, nullable = false, length = 100)
    val email: String,
    
    @Column(nullable = false)
    val enabled: Boolean = true,
    
    @Enumerated(EnumType.STRING)
    val role: Role = Role.USER
) : UserDetails {
    
    override fun getUsername(): String = username
    override fun getPassword(): String = password
    override fun isEnabled(): Boolean = enabled
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
    }
}

enum class Role {
    USER, ADMIN
}
