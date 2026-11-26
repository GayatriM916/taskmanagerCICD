package com.universidad.taskmanager.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Tarea no encontrada con id: " + id);
    }
}
