package com.portfolio.mytaskmanager.controller;


import com.portfolio.mytaskmanager.entity.Task;

import com.portfolio.mytaskmanager.service.ProjectService;
import com.portfolio.mytaskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/tasks")
@CrossOrigin(origins = "*") // Allow frontend to access from different origin
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectService projectService;

    // GET all tasks
    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    // GET all tasks that belong to specific project by project ID
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Task>> getTasksByProject(@PathVariable Long projectId) {
        return projectService.getProjectById(projectId)
                .map(project -> ResponseEntity.ok(taskService.getTasksByProject(project)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST a new task to database
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.saveTask(task));
    }

    // PUT – update existing task by ID
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        try {
            return ResponseEntity.ok(taskService.updateTask(id, task));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE task by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
