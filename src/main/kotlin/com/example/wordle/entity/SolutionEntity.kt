package com.example.wordle.entity

import jakarta.persistence.*

@Entity
@Table(name = "solutions")
data class SolutionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false, unique = true, length = 5)
    val word: String,
    
    @Column(nullable = false)
    val isActive: Boolean = true
)
