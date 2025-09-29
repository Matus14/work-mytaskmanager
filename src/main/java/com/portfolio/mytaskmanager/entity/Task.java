package com.portfolio.mytaskmanager.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data // Annotation automatically works with getters/setters/toString.... - Lombok dependency
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    // This generates primary key for each object in my SQL database >project1_taskmanager<
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String description;

    // Saves the enum as a string text("TODO")
    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate dueDate;


    /*  This part represents the connection - many tasks to one project
        JoinColumn makes a foreign key in Task table with name "project_id" pointing at ID in table Project
        at last there is a reference variable we need to make to mark connection with entity
    */
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

}
