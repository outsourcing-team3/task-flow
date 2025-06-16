package com.example.outsourcingproject.domain.comment.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private Long userId;
    private String content;
    private Long taskId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Comment 엔티티에서 DTO 변환 생성자
    public CommentResponseDto(com.example.outsourcingproject.domain.comment.entity.Comment comment) {
        this.id = comment.getId();
        this.userId = comment.getUser().getId();
        this.content = comment.getContent();
//        this.taskId = comment.getTask().getId();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}
