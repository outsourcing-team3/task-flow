package com.example.outsourcingproject.global.enums;

public enum ActivityType {
    TASK_CREATED("새로운 작업이 생성되었습니다."),
    TASK_UPDATED("작업이 수정되었습니다."),
    TASK_DELETED("작업이 삭제되었습니다."),
    TASK_STATUS_CHANGED("작업 상태가 TODO에서 IN_PROGRESS로 변경되었습니다."),
    COMMENT_CREATED("댓글이 작성되었습니다."),
    COMMENT_UPDATED("댓글이 수정되었습니다."),
    COMMENT_DELETED("댓글이 삭제되었습니다."),
    USER_LOGGED_IN("사용자가 로그인했습니다."),
    USER_LOGGED_OUT("사용자가 로그아웃했습니다.");

    private final String message;

    ActivityType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
