package org.cisnux.jediplanner.infrastructures.storages

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.withContext
import org.cisnux.jediplanner.commons.exceptions.APIException
import org.cisnux.jediplanner.commons.logger.Loggable
import org.cisnux.jediplanner.domains.storages.FileManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.nio.file.Files
import java.nio.file.Paths

@Component
class FileManagerImpl(
    @Value("\${file.upload-dir}")
    private var uploadDir: String,
) : FileManager, Loggable {
    override suspend fun exportFilePath(file: FilePart): String = withContext(Dispatchers.IO) {
        try {
            val uploadPath = Paths.get(uploadDir)
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath)
            }

            val filename = StringUtils.cleanPath(file.filename())
            val filePath = uploadPath.resolve(filename).normalize()

            file.transferTo(filePath).awaitFirstOrNull()

            filename
        } catch (e: Exception) {
            log.error("Failed to export file: ${e.message}")
            throw e
        }
    }

    override suspend fun getResource(filename: String): Resource = withContext(Dispatchers.IO) {
        val filePath = Paths.get(uploadDir).resolve(filename).normalize()
        val resource = UrlResource(filePath.toUri())
        resource
    }
}