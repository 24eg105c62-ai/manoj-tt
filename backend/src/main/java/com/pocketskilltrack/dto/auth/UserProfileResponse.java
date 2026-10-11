package com.pocketskilltrack.dto.auth;

import java.time.LocalDateTime;
import java.util.Set;

public class UserProfileResponse {

    private Long id;
    private String studentId;
    private String email;
    private String fullName;
    private String college;
    private String department;
    private String studyYear;
    private String bio;
    private Set<String> roles;
    private LocalDateTime createdAt;

    public UserProfileResponse() {}

    public UserProfileResponse(Long id, String studentId, String email, String fullName, String college, String department, String studyYear, String bio, Set<String> roles, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.email = email;
        this.fullName = fullName;
        this.college = college;
        this.department = department;
        this.studyYear = studyYear;
        this.bio = bio;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getStudyYear() { return studyYear; }
    public void setStudyYear(String studyYear) { this.studyYear = studyYear; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
