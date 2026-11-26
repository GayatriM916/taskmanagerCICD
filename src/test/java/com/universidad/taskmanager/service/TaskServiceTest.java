package com.universidad.taskmanager.service;

import com.universidad.taskmanager.exception.TaskNotFoundException;
import com.universidad.taskmanager.model.Task;
import com.universidad.taskmanager.model.TaskStatus;
import com.universidad.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_successfully() {
        Task input = new Task(null, "Estudiar CI/CD", "Pipeline GitHub Actions", null);
        Task saved = new Task(1L, "Estudiar CI/CD", "Pipeline GitHub Actions", TaskStatus.PENDING);

        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        Task result = taskService.create(input);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Estudiar CI/CD", result.getTitle());
        assertEquals(TaskStatus.PENDING, result.getStatus());
        assertTrue(false, "Fallo controlado para validar el pipeline CI/CD");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void findAllTasks_returnsList() {
        Task task = new Task(1L, "Tarea 1", "Descripcion", TaskStatus.PENDING);
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<Task> result = taskService.findAll();

        assertEquals(1, result.size());
        assertEquals("Tarea 1", result.get(0).getTitle());
        verify(taskRepository).findAll();
    }

    @Test
    void findById_existingTask_returnsTask() {
        Task task = new Task(1L, "Tarea existente", "Descripcion", TaskStatus.PENDING);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Tarea existente", result.getTitle());
        verify(taskRepository).findById(1L);
    }

    @Test
    void findById_missingTask_throwsException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.findById(99L));
        verify(taskRepository).findById(99L);
    }

    @Test
    void updateTask_changesStatus() {
        Task existing = new Task(1L, "Tarea", "Descripcion", TaskStatus.PENDING);
        Task update = new Task(null, "Tarea", "Descripcion", TaskStatus.COMPLETED);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.update(1L, update);

        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        verify(taskRepository).save(existing);
    }

    @Test
    void deleteTask_existingTask_deletesSuccessfully() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_missingTask_throwsException() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.delete(99L));
        verify(taskRepository, never()).deleteById(99L);
    }
}
