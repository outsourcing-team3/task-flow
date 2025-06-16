package com.example.outsourcingproject.domain.task.repository;

import com.example.outsourcingproject.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
