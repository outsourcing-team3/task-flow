package com.example.outsourcingproject.domain.task.service;

import com.example.outsourcingproject.domain.task.dto.TaskCreateRequestDto;
import com.example.outsourcingproject.domain.task.dto.TaskCreateResponseDto;
import com.example.outsourcingproject.domain.task.entity.Task;
import com.example.outsourcingproject.domain.task.repository.TaskRepository;
import com.example.outsourcingproject.domain.user.entity.User;
import com.example.outsourcingproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskCreateResponseDto createTask (Long currentUserId, TaskCreateRequestDto requestDto) {

        User assignee = userRepository.findById(requestDto.getAssignee().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found = " + requestDto.getAssignee().getId()));

        User creator = userRepository.findById(currentUserId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Does not exist id = " + currentUserId));

        LocalDateTime deadline = (requestDto.getDeadline() != null) ? requestDto.getDeadline() : LocalDate.now().plusDays(7).atStartOfDay();

        Task newTask = new Task(
                requestDto.getTitle(),
                requestDto.getDescription(),
                requestDto.getPriority(),
                assignee,
                creator,
                "TODO",
                deadline
        );

        taskRepository.save((newTask));

        return TaskCreateResponseDto.toDto(newTask);
    }


}
