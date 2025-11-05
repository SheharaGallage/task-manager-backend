package com.TM.taskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskListDTO {
    private Long id;
    private String title;
    private String description;
    private List<TaskDTO> tasks;
    private Integer count;
    private double progress;
}
