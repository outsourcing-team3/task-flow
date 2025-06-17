package com.example.outsourcingproject.domain.activitylog.controller;

import com.example.outsourcingproject.domain.activitylog.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;


}
