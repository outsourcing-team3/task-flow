package com.example.outsourcingproject.domain.comment.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    private Long taskId;       // 댓글이 달린 태스크 ID
    private String content;    // 댓글 내용
}
