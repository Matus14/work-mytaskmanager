package com.portfolio.mytaskmanager.service;


import com.portfolio.mytaskmanager.entity.Task;
import com.portfolio.mytaskmanager.repository.TaskRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;


    public List<Task> getAllTasks() {
        return taskRepo.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepo.findById(id);
    }

    public Task saveTask(Task task) {
        return taskRepo.save(task);
    }

    public Task updateTask(Long id, Task updateTAsk) {
        return taskRepo.findById(id)
                .map(p -> {
                    p.setTitle(updateTAsk.getTitle());
                    p.setDescription(updateTAsk.getDescription());
                    p.setProject(updateTAsk.getProject());
                    p.setStatus(updateTAsk.getStatus());
                    p.setDueDate(updateTAsk.getDueDate());
                    return taskRepo.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public void deleteTask(Long id) {
        taskRepo.deleteById(id);
    }
}
