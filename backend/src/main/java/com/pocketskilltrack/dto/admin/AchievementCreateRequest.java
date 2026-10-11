package com.pocketskilltrack.dto.admin;

import com.pocketskilltrack.entity.CriteriaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AchievementCreateRequest {

    @NotBlank(message = "Badge code is required")
    private String badgeCode;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Icon name is required")
    private String iconName;

    @NotNull(message = "Criteria type is required")
    private CriteriaType criteriaType;

    private int thresholdValue = 1;
    private String category = "GENERAL";

    public AchievementCreateRequest() {}

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
}
