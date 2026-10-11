package com.pocketskilltrack.dto.admin;

import java.time.LocalDateTime;
import java.util.Set;

public class UserAdminDto {

    private Long id;
    private String studentId;
    private String email;
    private String fullName;
    private String college;
    private String department;
    private String studyYear;
    private boolean enabled;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private long enrolledCoursesCount;
    private long quizAttemptsCount;

    public UserAdminDto() {}

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

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public long getEnrolledCoursesCount() { return enrolledCoursesCount; }
    public void setEnrolledCoursesCount(long enrolledCoursesCount) { this.enrolledCoursesCount = enrolledCoursesCount; }

    public long getQuizAttemptsCount() { return quizAttemptsCount; }
    public void setQuizAttemptsCount(long quizAttemptsCount) { this.quizAttemptsCount = quizAttemptsCount; }
}
