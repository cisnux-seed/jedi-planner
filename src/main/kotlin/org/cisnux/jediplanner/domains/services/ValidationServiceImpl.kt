package org.cisnux.jediplanner.domains.services

import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.commons.logger.Loggable
import org.springframework.stereotype.Service

@Service
class ValidationServiceImpl(val validator: Validator) : ValidationService, Loggable {
    override suspend fun <T> validateObject(`object`: T) = withContext(Dispatchers.IO) {
        log.debug("Validating object: {}", `object`)
        val constraintViolations = validator.validate(`object`)
        if (!constraintViolations.isEmpty())
            throw ConstraintViolationException(constraintViolations)
    }
}