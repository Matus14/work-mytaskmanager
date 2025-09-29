package com.portfolio.mytaskmanager.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProjectRequestDTO {

    @NotBlank
    @Column(nullable = false, length = 30)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;
}
