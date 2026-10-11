package com.pocketskilltrack.service;

import com.pocketskilltrack.entity.AuditLog;
import com.pocketskilltrack.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Long actorId, String actorEmail, String action, String resourceType, String resourceId, String ipAddress, String detailsJson) {
        AuditLog log = new AuditLog(actorId, actorEmail, action, resourceType, resourceId, ipAddress, detailsJson);
        auditLogRepository.save(log);
    }
}
