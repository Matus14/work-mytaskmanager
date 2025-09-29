package com.portfolio.mytaskmanager.dto;

import com.portfolio.mytaskmanager.entity.Status;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequestDTO {


    @NotBlank
    @Size(max = 100)
    private String title;


    @NotBlank
    @Size(max = 1000)
    private String description;


    private LocalDate dueDate;

    @NotNull
    private Status status;

    @NotNull
    private Long projectId;
}
