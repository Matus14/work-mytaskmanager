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

    // Optional: works with cases where ID is not found, and instead breaking down program, it throws exception later in another block of code.
    public Optional<Task> getTaskById(Long id) {
        return taskRepo.findById(id);
    }

    public Task saveTask(Task task) {
        return taskRepo.save(task);
    }


    /*  This method updates an existing task in a database. At first calling method .findById() to make sure that
        data is there. If the data is found, the next block (.map) of the code kick off to update each collum.
        At the end after updates, the new data is again stored via .save() function.
        If there is a problem, the orElseThrow() is initialized.
     */
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
