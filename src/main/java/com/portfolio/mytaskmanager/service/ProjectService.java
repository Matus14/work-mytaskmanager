package com.portfolio.mytaskmanager.service;


import com.portfolio.mytaskmanager.dto.ProjectRequestDTO;
import com.portfolio.mytaskmanager.dto.ProjectResponseDTO;
import com.portfolio.mytaskmanager.entity.Project;
import com.portfolio.mytaskmanager.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository repository;

    public ProjectResponseDTO create(ProjectRequestDTO request){
        validate(request);

        if(repository.existsByNameIgnoreCase(request.getName().trim())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Project name already exists");
        }

        Project entity = Project.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        Project saved = repository.save(entity);
        return toDto(saved);

    }

    public List<ProjectResponseDTO> findAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    public ProjectResponseDTO update(Long id, ProjectRequestDTO request){
        validate(request);

        Project project = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        project.setName(request.getName().trim());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        Project saved = repository.save(project);
        return  toDto(saved);
    }


    public ProjectResponseDTO findById(Long id) {
        Project project = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id not found"));
        return toDto(project);

    }

    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Not found for delete");
        }
        repository.deleteById(id);
    }

    private void validate(ProjectRequestDTO p){
        if(p.getName() == null || p.getName().trim().isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must be filled in");
        }
        if (p.getStartDate() != null && p.getEndDate() != null && !p.getEndDate().isAfter(p.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "endDate must be after startDate");
        }
    }

    private ProjectResponseDTO toDto(Project p){
        return new ProjectResponseDTO(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getStartDate(),
                p.getEndDate()
        );
    }
}
