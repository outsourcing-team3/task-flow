package com.example.outsourcingproject.domain.task.service;

import com.example.outsourcingproject.domain.task.dto.*;
import com.example.outsourcingproject.domain.task.entity.Task;
import com.example.outsourcingproject.domain.task.enums.TaskStatus;
import com.example.outsourcingproject.domain.task.repository.TaskRepository;
import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    // Task 생성
    @Transactional
    public TaskCreateResponseDto createTask (Long currentUserId, TaskCreateRequestDto requestDto) {

        // Task 생성자 = 로그인 유저 검증
        User creator = userRepository.findById(currentUserId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + currentUserId));

        // 요청 데이터에서 assigneeName 값이 없을 경우 기본값으로 creator 로 처리
        String assigneeName = Optional.ofNullable(requestDto.getAssigneeName()).filter(name -> !name.isBlank()).orElse(creator.getName());

        User assignee = userRepository.findAll().stream()
                .filter(user -> user.getName().equals(assigneeName))
                .findFirst()
                .orElseThrow(() -> { log.warn("유저 이름 '{}'에 해당하는 ID 없음", assigneeName);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee name not found: " + assigneeName);
        });

        // description 기본값 처리
        String description = Optional.ofNullable(requestDto.getDescription()).filter(desc -> !desc.isBlank()).orElse("No description provided.");

        // priority 기본값 처리
        String priority = Optional.ofNullable(requestDto.getPriority()).filter(pr -> !pr.isBlank()).orElse("MEDIUM");

        // status 기본값 처리
        TaskStatus status = TaskStatus.TODO;

        // deadline 기본값 처리
        LocalDateTime deadline = Optional.ofNullable(requestDto.getDeadline()).orElse(LocalDate.now().plusDays(7).atStartOfDay());

        // started 날짜 기본값 처리
        LocalDateTime startedAt = Optional.ofNullable(requestDto.getStartedAt()).orElse(LocalDateTime.now());

        Task newTask = new Task(
                requestDto.getTitle(),
                description,
                priority,
                assignee,
                creator,
                status,
                deadline,
                startedAt
        );

        taskRepository.save((newTask));

        return TaskCreateResponseDto.toDto(newTask);
    }

    // Task 조회 - 전체
    public List<TaskReadResponseDto> getAllTasks() {
        return taskRepository.findAllByIsDeletedFalse().stream().map(TaskReadResponseDto::toDto).toList();
    }

    // Task 조회 - 단 건
    public TaskReadResponseDto getTaskById(Long taskId) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id = " + taskId));

        return TaskReadResponseDto.toDto(task);
    }


    // 특정 Task 수정 - 내용 수정
    @Transactional
    public TaskReadResponseDto updateTask(Long currentUserId, Long taskId, TaskUpdateRequestDto requestDto) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id = " + taskId));

        User assignee = Optional.ofNullable(requestDto.getAssigneeName())
                .map(this::findUserName)
                .orElse(task.getAssignee());

        // 새 값이 있을 경우만 업데이트하기
        String title = Optional.ofNullable(requestDto.getTitle()).orElse(task.getTitle());
        String description = Optional.ofNullable(requestDto.getDescription()).orElse(task.getDescription());
        String priority = Optional.ofNullable(requestDto.getPriority()).orElse(task.getPriority());
        LocalDateTime deadline = Optional.ofNullable(requestDto.getDeadline()).orElse(task.getDeadline());
        LocalDateTime startedAt = Optional.ofNullable(requestDto.getStartedAt()).orElse(task.getStartedAt());

        Task updateTask = new Task(
                title,
                description,
                priority,
                assignee,
                task.getCreator(),
                task.getStatus(),
                deadline,
                startedAt
        );

        taskRepository.save(updateTask);

        return TaskReadResponseDto.toDto(updateTask);
    }

    // Task 상태 수정
    @Transactional
    public TaskReadResponseDto updateTaskStatus(Long currentUserId, Long taskId, TaskStatusUpdateRequestDto requestDto) {
        Task task = findTaskById(taskId);

        // 로그인 유저의 권한 확인
//        if (!isUserAdmin(currentUserId)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한을 보유한 유저만 이용할 수 있습니다.");
//        }

        TaskStatus updateStatus;

        // status 값 - enum 의 status 값 검증
        try {
            updateStatus = TaskStatus.valueOf(requestDto.getStatus());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "변경할 상태 값이 알맞지 않습니다: " + requestDto.getStatus());
        }

        // 수정한 상태 저장
        task.setStatus(updateStatus);
        taskRepository.save(task);

        return TaskReadResponseDto.toDto(task);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        // Task 조회
        Task task = findTaskById(taskId);

        // Soft Delete
        task.delete();

        // task 객체 저장 -> 변경 사항 DB 반영
        taskRepository.save(task);
    }


    // name 으로 User 찾기
    private User findUserName(String assigneeName) {
        return userRepository.findAll().stream()
                .filter(user -> user.getName().equals(assigneeName))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("유저 이름 '{}'에 해당하는 ID 없음", assigneeName);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee name not found: " + assigneeName);
                });
    }

    // taskId 로 Task 찾기
    private Task findTaskById(Long taskId) {
        return taskRepository.findByIdAndIsDeletedFalse(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id = " + taskId));
    }

    // 유저가 보유한 권한 확인 (Admin? User?)
//    private boolean isUserAdmin(Long userId) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
//        return user.getRole().equals(Role.ADMIN);
//    }

}
