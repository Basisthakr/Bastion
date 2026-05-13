package com.Basisttha.Bastion.Schedules;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.Basisttha.Bastion.Repository.RevokedTokenRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final RevokedTokenRepository revokedTokenRepository;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        log.info("[ScheduledTasks] Cleaning expired revoked tokens at {}", now);
        revokedTokenRepository.deleteAllExpiredTokens(now);
        log.info("[ScheduledTasks] Cleanup complete");
    }
}