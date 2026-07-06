package com.tdd.tddmock.taskmanager;

import java.util.List;

public class TaskManager {
    private TaskService taskService;

    public TaskManager(TaskService taskService) {
        this.taskService = taskService;
    }

    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    public void addTask(Task task) {
        taskService.addTask(task);
    }

    // Other methods for task management...
}
