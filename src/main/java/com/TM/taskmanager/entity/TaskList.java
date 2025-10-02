package com.TM.taskmanager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "task_lists")
public class TaskList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description" , nullable = true)
    private String description;

    @OneToMany(mappedBy = "taskList")
    private List<Task> tasks;

    @Column(name = "created" , nullable = false)
    private LocalDateTime created;

    @Column(name = "updated" , nullable = false)
    private LocalDateTime updated;
}
