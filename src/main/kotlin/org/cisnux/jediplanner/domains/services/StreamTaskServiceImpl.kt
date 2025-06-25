package org.cisnux.jediplanner.domains.services

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import org.cisnux.jediplanner.domains.entities.Task
import org.cisnux.jediplanner.domains.repositories.TaskRepository
import org.springframework.stereotype.Service

@Service
class StreamTaskServiceImpl(private val taskRepository: TaskRepository) : StreamTaskService {
    private val tasks = MutableStateFlow<List<Task>>(emptyList())

    override suspend fun getTasks(owner: Long): SharedFlow<List<Task>> = tasks.apply {
            value = taskRepository.findAll(owner).toList()
        }.stateIn(
            scope = TaskCoroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    companion object {
        private val TaskCoroutineScope = CoroutineScope(Dispatchers.Default)
    }
}