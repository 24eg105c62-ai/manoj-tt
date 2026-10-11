package com.pocketskilltrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "badge_code", unique = true, nullable = false, length = 60)
    private String badgeCode;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "icon_name", nullable = false, length = 60)
    private String iconName;

    @Enumerated(EnumType.STRING)
    @Column(name = "criteria_type", nullable = false, length = 30)
    private CriteriaType criteriaType;

    @Column(name = "threshold_value", nullable = false)
    private int thresholdValue = 1;

    @Column(length = 60)
    private String category = "GENERAL";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Achievement() {}

    public Achievement(String badgeCode, String title, String description, String iconName, CriteriaType criteriaType, int thresholdValue, String category) {
        this.badgeCode = badgeCode;
        this.title = title;
        this.description = description;
        this.iconName = iconName;
        this.criteriaType = criteriaType;
        this.thresholdValue = thresholdValue;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBadgeCode() { return badgeCode; }
    public void setBadgeCode(String badgeCode) { this.badgeCode = badgeCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public CriteriaType getCriteriaType() { return criteriaType; }
    public void setCriteriaType(CriteriaType criteriaType) { this.criteriaType = criteriaType; }

    public int getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(int thresholdValue) { this.thresholdValue = thresholdValue; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
