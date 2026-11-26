package com.universidad.taskmanager.controller;

import com.universidad.taskmanager.exception.GlobalExceptionHandler;
import com.universidad.taskmanager.model.Task;
import com.universidad.taskmanager.model.TaskStatus;
import com.universidad.taskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void createTask_withValidBody_returnsCreated() throws Exception {
        Task created = new Task(1L, "Estudiar CI/CD", "Pipeline", TaskStatus.PENDING);
        when(taskService.create(any(Task.class))).thenReturn(created);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Estudiar CI/CD",
                                  "description": "Pipeline",
                                  "status": "PENDING"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Estudiar CI/CD"));
    }

    @Test
    void createTask_withoutTitle_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "description": "Sin titulo"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("title")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("obligatorio")));
    }

    @Test
    void getAllTasks_returnsList() throws Exception {
        when(taskService.findAll()).thenReturn(List.of(
                new Task(1L, "Tarea 1", "Desc", TaskStatus.PENDING)
        ));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Tarea 1"));
    }

    @Test
    void getTaskById_existing_returnsTask() throws Exception {
        when(taskService.findById(1L)).thenReturn(
                new Task(1L, "Tarea existente", "Desc", TaskStatus.PENDING)
        );

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Tarea existente"));
    }

    @Test
    void updateTask_changesStatus() throws Exception {
        when(taskService.update(any(Long.class), any(Task.class))).thenReturn(
                new Task(1L, "Tarea", "Desc", TaskStatus.COMPLETED)
        );

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Tarea",
                                  "description": "Desc",
                                  "status": "COMPLETED"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deleteTask_returnsNoContent() throws Exception {
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
