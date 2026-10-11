package com.pocketskilltrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_created", columnList = "created_at"),
    @Index(name = "idx_audit_actor", columnList = "actor_id")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor_id")
    private Long actorId;

    @Column(name = "actor_email", length = 100)
    private String actorEmail;

    @Column(nullable = false, length = 60)
    private String action;

    @Column(name = "resource_type", nullable = false, length = 60)
    private String resourceType;

    @Column(name = "resource_id", length = 60)
    private String resourceId;

    @Column(name = "ip_address", length = 60)
    private String ipAddress;

    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public AuditLog() {}

    public AuditLog(Long actorId, String actorEmail, String action, String resourceType, String resourceId, String ipAddress, String detailsJson) {
        this.actorId = actorId;
        this.actorEmail = actorEmail;
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.ipAddress = ipAddress;
        this.detailsJson = detailsJson;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
