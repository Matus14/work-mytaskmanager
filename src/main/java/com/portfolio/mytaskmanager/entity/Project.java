package com.portfolio.mytaskmanager.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Project {

    // this generates primary key for each object in my SQL database >project1_taskmanager<
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    /*  1:N represents one project has many tasks
        cascade - operations (such as save,delete...) are automatically applicable in tasks
        orphanRemoval - if a task is removed from the list it will be as well removed from a database
    */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

}
