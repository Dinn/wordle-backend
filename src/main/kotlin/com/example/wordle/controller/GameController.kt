package com.example.wordle.controller

import com.example.wordle.service.GameService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/game")
class GameController(
    private val gameService: GameService
) {

    @GetMapping("/new")
    fun getAnswer(): String {
        return gameService.createNewGame()
    }
}
