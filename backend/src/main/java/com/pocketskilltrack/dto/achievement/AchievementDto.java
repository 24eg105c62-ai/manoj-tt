package com.pocketskilltrack.dto.achievement;

import com.pocketskilltrack.entity.CriteriaType;
import java.time.LocalDateTime;

public class AchievementDto {

    private Long id;
    private String badgeCode;
    private String title;
    private String description;
    private String iconName;
    private CriteriaType criteriaType;
    private int thresholdValue;
    private String category;
    private boolean unlocked;
    private LocalDateTime unlockedAt;

    public AchievementDto() {}

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

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }

    public LocalDateTime getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(LocalDateTime unlockedAt) { this.unlockedAt = unlockedAt; }
}
