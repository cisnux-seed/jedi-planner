package org.cisnux.jediplanner.domains.services

interface ValidationService {
    suspend fun <T> validateObject(`object`: T)
}