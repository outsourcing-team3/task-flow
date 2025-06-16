package com.example.outsourcingproject.domain.comment.repository;

import com.example.outsourcingproject.domain.comment.entity.Comment;
import com.example.outsourcingproject.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.config.Task;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Optional<Comment> findByIdAndIsDeletedFalse(Long id);

    List<Comment> findAllByTaskOrderByCreatedAtDesc(Task task);

    List<Comment> findAllByTaskAndContentContainingIgnoreCaseOrderByCreatedAtDesc(Task task, String keyword);

    List<Comment> findAllByUser(User user);

    List<Comment> findByTaskIdAndIsDeletedFalseOrderByCreatedAtDesc(Long taskId);

    List<Comment> findByContentContainingAndIsDeletedFalseOrderByCreatedAtDesc(String keyword);
}
