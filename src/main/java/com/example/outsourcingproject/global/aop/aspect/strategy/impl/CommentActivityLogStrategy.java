package com.example.outsourcingproject.global.aop.aspect.strategy.impl;

import com.example.outsourcingproject.global.aop.aspect.strategy.AbstractActivityLogStrategy;
import com.example.outsourcingproject.global.enums.TargetType;
import org.springframework.stereotype.Component;

@Component
public class CommentActivityLogStrategy extends AbstractActivityLogStrategy {

    @Override
    public TargetType getTargetType() {
        return TargetType.COMMENT;
    }

    @Override
    public Long getTargetId() {
        return 0L;
    }
}
