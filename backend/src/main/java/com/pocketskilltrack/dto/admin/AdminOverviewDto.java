package com.pocketskilltrack.dto.admin;

import java.util.ArrayList;
import java.util.List;

public class AdminOverviewDto {

    private long totalStudents;
    private long totalCourses;
    private long totalTopics;
    private long totalQuestions;
    private long totalEnrollments;
    private long totalQuizAttempts;
    private double globalAverageScore;
    private long totalPdfsUploaded;
    private List<AuditLogDto> recentAuditLogs = new ArrayList<>();

    public AdminOverviewDto() {}

    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }

    public long getTotalCourses() { return totalCourses; }
    public void setTotalCourses(long totalCourses) { this.totalCourses = totalCourses; }

    public long getTotalTopics() { return totalTopics; }
    public void setTotalTopics(long totalTopics) { this.totalTopics = totalTopics; }

    public long getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(long totalQuestions) { this.totalQuestions = totalQuestions; }

    public long getTotalEnrollments() { return totalEnrollments; }
    public void setTotalEnrollments(long totalEnrollments) { this.totalEnrollments = totalEnrollments; }

    public long getTotalQuizAttempts() { return totalQuizAttempts; }
    public void setTotalQuizAttempts(long totalQuizAttempts) { this.totalQuizAttempts = totalQuizAttempts; }

    public double getGlobalAverageScore() { return globalAverageScore; }
    public void setGlobalAverageScore(double globalAverageScore) { this.globalAverageScore = globalAverageScore; }

    public long getTotalPdfsUploaded() { return totalPdfsUploaded; }
    public void setTotalPdfsUploaded(long totalPdfsUploaded) { this.totalPdfsUploaded = totalPdfsUploaded; }

    public List<AuditLogDto> getRecentAuditLogs() { return recentAuditLogs; }
    public void setRecentAuditLogs(List<AuditLogDto> recentAuditLogs) { this.recentAuditLogs = recentAuditLogs; }
}
