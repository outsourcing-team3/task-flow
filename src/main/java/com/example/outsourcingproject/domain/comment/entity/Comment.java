package com.example.outsourcingproject.domain.comment.entity;

import com.example.outsourcingproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.scheduling.config.Task;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "comments")
@SQLDelete(sql = "UPDATE comments SET is_deleted = true, deleted_at = NOW() WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 댓글 내용
    @Column(nullable = false, length = 255)
    private String content;

    // 연결된 Task
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    // Soft Delete
    @Column(nullable = false)
    private final boolean isDeleted = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    // 댓글 내용 수정
    public void updateContent(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}
