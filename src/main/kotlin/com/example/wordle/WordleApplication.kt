package com.example.wordle

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Spring Boot 실행 진입점.
 * build 실패 원인이 이 파일이 없거나 mainClass 가 설정되지 않았기 때문입니다.
 */
@SpringBootApplication
class WordleApplication

fun main(args: Array<String>) {
    runApplication<WordleApplication>(*args)
}