package com.example.outsourcingproject.domain.comment.controller;

import com.example.outsourcingproject.domain.comment.dto.CommentRequestDto;
import com.example.outsourcingproject.domain.comment.dto.CommentResponseDto;
import com.example.outsourcingproject.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //댓글 작성
    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentRequestDto requestDto) {
        CommentResponseDto responseDto = commentService.createComment(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //댓글 수정
    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long id,
            @RequestBody CommentRequestDto requestDto) {
        CommentResponseDto responseDto = commentService.updateComment(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    //댓글 삭제(Soft Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    // 특정 Task의 댓글 전체 조회(최신순)
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<CommentResponseDto>> getCommentsByTask(@PathVariable Long taskId) {
        List<CommentResponseDto> comments = commentService.getCommentsByTask(taskId);
        return ResponseEntity.ok(comments);
    }

    //특정 Task 내에서 키워드 검색
    @GetMapping("/task/{taskId}/search")
    public ResponseEntity<List<CommentResponseDto>> searchCommentsByContent(
            @PathVariable Long taskId,
            @RequestParam String keyword) {
        List<CommentResponseDto> comments = commentService.searchComments(taskId, keyword);
        return ResponseEntity.ok(comments);
    }
}
