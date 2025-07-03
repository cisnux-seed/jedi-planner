package org.cisnux.jediplanner.applications.controllers

import org.cisnux.jediplanner.commons.exceptions.APIException
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.storages.FileManager
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/files")
class FileController(private val fileManager: FileManager): Loggable {

    @GetMapping("/{filename:.+}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @ResponseStatus(HttpStatus.OK)
    suspend fun getFile(@PathVariable filename: String): ResponseEntity<Resource> {
        try {
            val resource = fileManager.getResource(filename)
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.filename + "\"")
                .body(resource)
        } catch (e: Exception){
            log.error("File not found: $filename, message: ${e.message}, stacktrace: ${e.stackTraceToString()}")
            throw APIException.NotFoundResourceException(
                statusCode = HttpStatus.NOT_FOUND.value(),
                message = "file not found: $filename"
            )
        }
    }
}