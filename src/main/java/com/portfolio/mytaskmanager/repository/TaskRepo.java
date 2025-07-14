package com.portfolio.mytaskmanager.repository;

import com.portfolio.mytaskmanager.entity.Project;
import com.portfolio.mytaskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


// JPA repository with selected attribute for ID - Long
@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {


    /*  Method name 'findByProject' is automatically parsed by Spring Data JPA
        and translated into a SQL query like:
        SELECT * FROM task WHERE project_id = ?

        The 'project' parameter is an instance of the Project entity.
        Spring extracts the project ID internally to match it in the database.

        The return type is List<Task> because a single project can have
        multiple associated tasks.
    */
    List<Task> findByProject(Project project);
}
