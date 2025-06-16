package com.example.outsourcingproject.domain.comment.service;

import com.example.outsourcingproject.domain.comment.dto.CommentRequestDto;
import com.example.outsourcingproject.domain.comment.dto.CommentResponseDto;
import com.example.outsourcingproject.domain.comment.entity.Comment;
import com.example.outsourcingproject.domain.comment.repository.CommentRepository;

import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public CommentResponseDto createComment(CommentRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "유저를 찾을 수 없습니다"));

        Task task = taskRepository.findById(requestDto.getTaskId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "태스크를 찾을 수 없습니다"));

        Comment comment = Comment.builder()
                .user(user)
                .task(task)
                .content(requestDto.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        Comment saved = commentRepository.save(comment);
        return new CommentResponseDto(saved);
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "댓글을 찾을 수 없습니다"));

        comment.updateContent(requestDto.getContent());
        return new CommentResponseDto(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "댓글을 찾을 수 없습니다"));

        comment.softDelete();
    }

    public List<CommentResponseDto> getCommentsByTask(Long taskId) {
        return commentRepository.findByTaskIdAndIsDeletedFalseOrderByCreatedAtDesc(taskId).stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<CommentResponseDto> searchComments(Long taskId, String keyword) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "태스크를 찾을 수 없습니다"));

        return commentRepository.findAllByTaskAndContentContainingIgnoreCaseOrderByCreatedAtDesc(task, keyword).stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }
}
