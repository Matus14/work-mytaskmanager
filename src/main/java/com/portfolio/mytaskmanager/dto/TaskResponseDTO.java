package com.portfolio.mytaskmanager.dto;


import com.portfolio.mytaskmanager.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private Status status;
    private Long projectId;

}
