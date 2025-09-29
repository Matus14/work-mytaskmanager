package com.portfolio.mytaskmanager.repository;

import com.portfolio.mytaskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// JPA repository with selected attribute for ID - Long
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByNameIgnoreCase(String name);
}
