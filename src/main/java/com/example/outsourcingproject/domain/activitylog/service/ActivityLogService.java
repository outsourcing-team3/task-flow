package com.example.outsourcingproject.domain.activitylog.service;

import com.example.outsourcingproject.domain.activitylog.domain.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;


}
