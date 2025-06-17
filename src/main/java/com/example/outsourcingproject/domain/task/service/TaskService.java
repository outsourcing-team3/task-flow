package com.example.outsourcingproject.domain.task.service;

import com.example.outsourcingproject.domain.task.dto.TaskCreateRequestDto;
import com.example.outsourcingproject.domain.task.dto.TaskCreateResponseDto;
import com.example.outsourcingproject.domain.task.dto.TaskReadResponseDto;
import com.example.outsourcingproject.domain.task.dto.TaskUpdateRequestDto;
import com.example.outsourcingproject.domain.task.entity.Task;
import com.example.outsourcingproject.domain.task.repository.TaskRepository;
import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
                "TODO",
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
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        return TaskReadResponseDto.toDto(task);
    }


    // 특정 Task 수정 - 내용 수정
    public TaskReadResponseDto updateTask(Long taskId, TaskUpdateRequestDto requestDto, Long currentUserId) {
        Task task = taskRepository.findByIdAndIsDeletedFalse(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + currentUserId));

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
}
