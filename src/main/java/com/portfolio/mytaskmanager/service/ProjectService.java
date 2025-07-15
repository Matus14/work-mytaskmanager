package com.portfolio.mytaskmanager.service;


import com.portfolio.mytaskmanager.entity.Project;
import com.portfolio.mytaskmanager.repository.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepo projectRepo;

    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepo.findById(id);
    }

    public Project createProject(Project project) {
        return projectRepo.save(project);
    }

    public Project updateProject(Long id, Project updateProject) {
        return projectRepo.findById(id)
                .map(p -> {
                    p.setName(updateProject.getName());
                    p.setDescription(updateProject.getDescription());
                    p.setStartDate(updateProject.getStartDate());
                    p.setEndDate(updateProject.getEndDate());
                    return projectRepo.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));
    }

    public void deleteProject(Long id) {
        projectRepo.deleteById(id);
    }
}
