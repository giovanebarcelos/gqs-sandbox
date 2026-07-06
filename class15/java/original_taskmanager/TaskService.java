package com.tdd.tddmock.taskmanager;

import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();
    void addTask(Task task);
    // Other methods for task service...
}
