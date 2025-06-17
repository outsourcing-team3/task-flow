package com.example.outsourcingproject.domain.task.repository;

import com.example.outsourcingproject.domain.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // 삭제되지 않은(Task.isDeleted = false) Task 를 모두 조회
    List<Task> findAllByIsDeletedFalse();

    // 삭제되지 않은(Task.isDeleted = false) Task 중 taskId 로 Task 를 조회
    Optional<Task> findByIdAndIsDeletedFalse(Long taskId);

}
