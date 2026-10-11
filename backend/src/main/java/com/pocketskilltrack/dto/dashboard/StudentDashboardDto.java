package com.pocketskilltrack.dto.dashboard;

import com.pocketskilltrack.dto.achievement.StudentAchievementDto;
import com.pocketskilltrack.dto.course.EnrollmentDto;
import com.pocketskilltrack.dto.prediction.ScorePredictionDto;
import com.pocketskilltrack.dto.quiz.QuizAttemptDto;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardDto {

    private long enrolledCoursesCount;
    private long completedCoursesCount;
    private double averageProgressPercent;
    private long totalQuizzesTaken;
    private long passedQuizzesCount;
    private double averageQuizScore;
    private int currentStreakDays;
    private long achievementsEarnedCount;

    private List<EnrollmentDto> activeEnrollments = new ArrayList<>();
    private List<QuizAttemptDto> recentQuizAttempts = new ArrayList<>();
    private ScorePredictionDto scorePrediction;
    private List<StudentAchievementDto> recentAchievements = new ArrayList<>();
    private List<ActionRecommendation> recommendedActions = new ArrayList<>();

    public StudentDashboardDto() {}

    public long getEnrolledCoursesCount() { return enrolledCoursesCount; }
    public void setEnrolledCoursesCount(long enrolledCoursesCount) { this.enrolledCoursesCount = enrolledCoursesCount; }

    public long getCompletedCoursesCount() { return completedCoursesCount; }
    public void setCompletedCoursesCount(long completedCoursesCount) { this.completedCoursesCount = completedCoursesCount; }

    public double getAverageProgressPercent() { return averageProgressPercent; }
    public void setAverageProgressPercent(double averageProgressPercent) { this.averageProgressPercent = averageProgressPercent; }

    public long getTotalQuizzesTaken() { return totalQuizzesTaken; }
    public void setTotalQuizzesTaken(long totalQuizzesTaken) { this.totalQuizzesTaken = totalQuizzesTaken; }

    public long getPassedQuizzesCount() { return passedQuizzesCount; }
    public void setPassedQuizzesCount(long passedQuizzesCount) { this.passedQuizzesCount = passedQuizzesCount; }

    public double getAverageQuizScore() { return averageQuizScore; }
    public void setAverageQuizScore(double averageQuizScore) { this.averageQuizScore = averageQuizScore; }

    public int getCurrentStreakDays() { return currentStreakDays; }
    public void setCurrentStreakDays(int currentStreakDays) { this.currentStreakDays = currentStreakDays; }

    public long getAchievementsEarnedCount() { return achievementsEarnedCount; }
    public void setAchievementsEarnedCount(long achievementsEarnedCount) { this.achievementsEarnedCount = achievementsEarnedCount; }

    public List<EnrollmentDto> getActiveEnrollments() { return activeEnrollments; }
    public void setActiveEnrollments(List<EnrollmentDto> activeEnrollments) { this.activeEnrollments = activeEnrollments; }

    public List<QuizAttemptDto> getRecentQuizAttempts() { return recentQuizAttempts; }
    public void setRecentQuizAttempts(List<QuizAttemptDto> recentQuizAttempts) { this.recentQuizAttempts = recentQuizAttempts; }

    public ScorePredictionDto getScorePrediction() { return scorePrediction; }
    public void setScorePrediction(ScorePredictionDto scorePrediction) { this.scorePrediction = scorePrediction; }

    public List<StudentAchievementDto> getRecentAchievements() { return recentAchievements; }
    public void setRecentAchievements(List<StudentAchievementDto> recentAchievements) { this.recentAchievements = recentAchievements; }

    public List<ActionRecommendation> getRecommendedActions() { return recommendedActions; }
    public void setRecommendedActions(List<ActionRecommendation> recommendedActions) { this.recommendedActions = recommendedActions; }

    public static class ActionRecommendation {
        private String title;
        private String description;
        private String actionType; // "COURSE", "QUIZ", "PDF", "ACHIEVEMENT"
        private String targetUrl;
        private String priority; // "HIGH", "MEDIUM"

        public ActionRecommendation() {}

        public ActionRecommendation(String title, String description, String actionType, String targetUrl, String priority) {
            this.title = title;
            this.description = description;
            this.actionType = actionType;
            this.targetUrl = targetUrl;
            this.priority = priority;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getActionType() { return actionType; }
        public void setActionType(String actionType) { this.actionType = actionType; }
        public String getTargetUrl() { return targetUrl; }
        public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }
}
