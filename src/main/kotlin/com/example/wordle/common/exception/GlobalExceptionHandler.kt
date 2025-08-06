package com.example.wordle.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDuplicateResource(
        ex: DuplicateResourceException,
        request: WebRequest
    ): ErrorResponse {
        return ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Duplicate Resource",
            message = ex.message ?: "리소스가 중복됩니다",
            path = request.getDescription(false).removePrefix("uri=")
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidation(
        ex: MethodArgumentNotValidException,
        request: WebRequest
    ): ErrorResponse {
        val validationErrors = ex.bindingResult.fieldErrors.associate { 
            it.field to (it.defaultMessage ?: "유효하지 않은 값입니다")
        }
        
        return ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Failed",
            message = "입력값 검증에 실패했습니다",
            path = request.getDescription(false).removePrefix("uri="),
            validationErrors = validationErrors
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneral(
        ex: Exception,
        request: WebRequest
    ): ErrorResponse {
        return ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = "서버 내부 오류가 발생했습니다",
            path = request.getDescription(false).removePrefix("uri=")
        )
    }
}
