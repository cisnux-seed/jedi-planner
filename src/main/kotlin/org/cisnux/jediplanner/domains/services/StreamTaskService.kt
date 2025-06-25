package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.flow.SharedFlow
import org.cisnux.jediplanner.domains.entities.Task

interface StreamTaskService {
    suspend fun getTasks(owner: Long): SharedFlow<List<Task>>
}