package com.example.outsourcingproject.domain.comment.controller;

import com.example.outsourcingproject.domain.comment.dto.CommentRequestDto;
import com.example.outsourcingproject.domain.comment.dto.CommentResponseDto;
import com.example.outsourcingproject.domain.comment.service.CommentService;
import com.example.outsourcingproject.global.aop.annotation.ActivityLog;
import com.example.outsourcingproject.global.enums.ActivityType;
import com.example.outsourcingproject.global.enums.TargetType;
import com.example.outsourcingproject.global.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 작성
    @ActivityLog(type = ActivityType.COMMENT_CREATED, target = TargetType.COMMENT)
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long taskId,
            @RequestBody CommentRequestDto requestDto,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getId();
        CommentResponseDto responseDto = commentService.createComment(taskId, userId, requestDto);
        return ResponseEntity.ok(responseDto);    }

    //댓글 수정
    @ActivityLog(type = ActivityType.COMMENT_UPDATED, target = TargetType.COMMENT)
    @PutMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto) {
        CommentResponseDto responseDto = commentService.updateComment(taskId, commentId, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //댓글 삭제(Soft Delete)
    @ActivityLog(type = ActivityType.COMMENT_DELETED, target = TargetType.COMMENT)
    @DeleteMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId) {
        commentService.deleteComment(taskId, commentId);
        return ResponseEntity.noContent().build();
    }

    //특정 Task의 댓글 전체 조회(최신순)
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByTask(@PathVariable Long taskId) {
        List<CommentResponseDto> comments = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(comments);
    }
}
