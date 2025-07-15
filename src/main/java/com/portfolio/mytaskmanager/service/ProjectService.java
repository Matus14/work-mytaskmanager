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

    // Optional: works with cases where ID is not found, and instead breaking down program, it throws exception later in another block of code.
    public List<Project> getAllProjects() {
        return projectRepo.findAll();
    }

    public Optional<Project> getProjectById(Long id) {
        return projectRepo.findById(id);
    }

    public Project createProject(Project project) {
        return projectRepo.save(project);
    }

    /*  This method updates an existing project in a database. At first calling method .findById() to make sure that
        data is there. If the data is found, the next block (.map) of the code kick off to update each collum.
        At the end after updates, the new data is again stored via .save() function.
        If there is a problem, the orElseThrow() is initialized.
     */


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
