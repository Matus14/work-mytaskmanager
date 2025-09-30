package com.portfolio.mytaskmanager.service;


import com.portfolio.mytaskmanager.dto.TaskRequestDTO;
import com.portfolio.mytaskmanager.dto.TaskResponseDTO;
import com.portfolio.mytaskmanager.entity.Project;
import com.portfolio.mytaskmanager.entity.Status;
import com.portfolio.mytaskmanager.entity.Task;
import com.portfolio.mytaskmanager.repository.ProjectRepository;
import com.portfolio.mytaskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private ProjectRepository projectRepository;

    public TaskResponseDTO create(TaskRequestDTO request){
        validate(request);

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        Task entity = Task.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .status(request.getStatus())
                .dueDate(request.getDueDate())
                .project(project)
                .build();

        Task saved = repository.save(entity);
        return toDto(saved);
    }

    public List<TaskResponseDTO> findAll(){
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public TaskResponseDTO findById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        return toDto(task);
    }

    public TaskResponseDTO update(Long id, TaskRequestDTO request) {
        validate(request);

        Task t = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        t.setTitle(request.getTitle().trim());
        t.setDescription(request.getDescription().trim());
        t.setDueDate(request.getDueDate());
        t.setStatus(request.getStatus() != null ? request.getStatus() : Status.TODO);
        t.setProject(project);

        Task saved = repository.save(t);
        return toDto(saved);
    }


    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found for delete");
        }
        repository.deleteById(id);
    }

    private TaskResponseDTO toDto(Task t){
        return new TaskResponseDTO(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getDueDate(),
                t.getStatus(),
                t.getProject() != null ? t.getProject().getId() : null
        );
    }

    private void validate(TaskRequestDTO t) {

        if (t.getTitle() == null || t.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title cannot be blank");
        }
        if (t.getDescription() == null || t.getDescription().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description cannot be blank");
        }
        if (t.getProjectId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "projectId is required");
        }
        if (t.getDueDate() != null && t.getDueDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dueDate cannot be in the past");
        }
    }
}
