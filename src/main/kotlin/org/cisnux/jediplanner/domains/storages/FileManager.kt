package org.cisnux.jediplanner.domains.storages

import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart

interface FileManager {
    suspend fun exportFilePath(file: FilePart): String
    suspend fun getResource(filename: String): Resource
}