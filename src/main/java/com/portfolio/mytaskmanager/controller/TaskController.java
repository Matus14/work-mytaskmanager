package com.portfolio.mytaskmanager.controller;


import com.portfolio.mytaskmanager.dto.TaskRequestDTO;
import com.portfolio.mytaskmanager.dto.TaskResponseDTO;
import com.portfolio.mytaskmanager.entity.Task;

import com.portfolio.mytaskmanager.service.ProjectService;
import com.portfolio.mytaskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // Allow frontend to access from different origin
public class TaskController {

    @Autowired
    private TaskService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO create(@Valid @RequestBody TaskRequestDTO request){
        return service.create(request);
    }

    @GetMapping
    public List<TaskResponseDTO> findAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TaskResponseDTO findById(@PathVariable Long id){
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public TaskResponseDTO update(@PathVariable Long id,
                                  @Valid @RequestBody TaskRequestDTO request) {
        return service.update(id,request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return  ResponseEntity.noContent().build();
    }

}
