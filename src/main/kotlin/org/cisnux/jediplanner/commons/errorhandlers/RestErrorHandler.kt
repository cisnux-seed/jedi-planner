package org.cisnux.jediplanner.commons.errorhandlers

import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.applications.controllers.dtos.MetaResponseDTO
import org.cisnux.jediplanner.applications.controllers.dtos.WebResponseDTO
import org.cisnux.jediplanner.commons.exceptions.APIException
import org.cisnux.jediplanner.commons.logger.Loggable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestErrorHandler : Loggable {
    @ExceptionHandler(ConstraintViolationException::class)
    suspend fun constraintViolationException(constraintViolationException: ConstraintViolationException): ResponseEntity<WebResponseDTO<String?>?>? =
        withContext(Dispatchers.IO) {
            log.warn("Error", constraintViolationException)
            val message = java.lang.String.join(
                ", ",
                constraintViolationException.constraintViolations.stream()
                    .map<String?> { obj: ConstraintViolation<*>? -> obj!!.message }.toList()
            )
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                WebResponseDTO(
                    meta = MetaResponseDTO(
                        code = HttpStatus.BAD_REQUEST.value().toString(), message = message
                    )
                )
            )
        }

    @ExceptionHandler(Exception::class)
    suspend fun globalException(exception: Exception): ResponseEntity<WebResponseDTO<String?>?>? = withContext(
        Dispatchers.IO
    ) {
        log.warn("Error", exception)
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            WebResponseDTO(
                meta = MetaResponseDTO(
                    code = HttpStatus.INTERNAL_SERVER_ERROR.value().toString(),
                    message = exception.message
                )
            )
        )
    }

    @ExceptionHandler(APIException::class)
    suspend fun apiException(apiException: APIException): ResponseEntity<WebResponseDTO<String?>?>? = withContext(
        Dispatchers.IO
    ) {
        log.warn("Error", apiException)

        ResponseEntity.status(apiException.statusCode).body(
            WebResponseDTO(
                meta = MetaResponseDTO(
                    code = apiException.statusCode.toString(),
                    message = apiException.message
                )
            )
        )
    }
}