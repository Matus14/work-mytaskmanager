package com.portfolio.mytaskmanager.controller;


import com.portfolio.mytaskmanager.dto.ProjectRequestDTO;
import com.portfolio.mytaskmanager.dto.ProjectResponseDTO;
import com.portfolio.mytaskmanager.entity.Project;
import com.portfolio.mytaskmanager.service.ProjectService;
import jakarta.validation.Valid;
import org.hibernate.dialect.unique.CreateTableUniqueDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*") // Allow frontend from any domain to access this API
public class ProjectController {

    @Autowired
    private ProjectService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseDTO create(@Valid @RequestBody ProjectRequestDTO request){
        return service.create(request);
    }


    // GET all projects from database
    @GetMapping
    public List<ProjectResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProjectResponseDTO findById(@PathVariable Long id){
        return service.findById(id);
    }


    // PUT – update existing project by ID
    @PutMapping("/{id}")
    public ProjectResponseDTO update(@PathVariable Long id,
                                     @Valid @RequestBody ProjectRequestDTO request){
        return service.update(id,request);
    }

    // DELETE project by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
