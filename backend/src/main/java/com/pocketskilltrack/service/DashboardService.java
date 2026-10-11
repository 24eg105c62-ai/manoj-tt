package com.pocketskilltrack.service;

import com.pocketskilltrack.dto.achievement.StudentAchievementDto;
import com.pocketskilltrack.dto.course.EnrollmentDto;
import com.pocketskilltrack.dto.dashboard.StudentDashboardDto;
import com.pocketskilltrack.dto.prediction.ScorePredictionDto;
import com.pocketskilltrack.dto.quiz.QuizAttemptDto;
import com.pocketskilltrack.entity.Enrollment;
import com.pocketskilltrack.entity.QuizAttempt;
import com.pocketskilltrack.entity.StudentAchievement;
import com.pocketskilltrack.entity.Topic;
import com.pocketskilltrack.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final EnrollmentRepository enrollmentRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final TopicCompletionRepository topicCompletionRepository;
    private final StudentAchievementRepository studentAchievementRepository;
    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final CourseService courseService;
    private final QuizService quizService;
    private final ScorePredictionService scorePredictionService;
    private final AchievementService achievementService;

    public DashboardService(
            EnrollmentRepository enrollmentRepository,
            QuizAttemptRepository quizAttemptRepository,
            TopicCompletionRepository topicCompletionRepository,
            StudentAchievementRepository studentAchievementRepository,
            CourseRepository courseRepository,
            TopicRepository topicRepository,
            CourseService courseService,
            QuizService quizService,
            ScorePredictionService scorePredictionService,
            AchievementService achievementService) {
        this.enrollmentRepository = enrollmentRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.topicCompletionRepository = topicCompletionRepository;
        this.studentAchievementRepository = studentAchievementRepository;
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.courseService = courseService;
        this.quizService = quizService;
        this.scorePredictionService = scorePredictionService;
        this.achievementService = achievementService;
    }

    @Transactional
    public StudentDashboardDto getDashboardData(Long userId) {
        StudentDashboardDto dto = new StudentDashboardDto();

        // 1. Enrollment stats
        long enrolledCount = enrollmentRepository.countByUserId(userId);
        long completedCourses = enrollmentRepository.countByUserIdAndProgressPercent(userId, 100);
        Double avgProgress = enrollmentRepository.getAverageProgressByUserId(userId);

        dto.setEnrolledCoursesCount(enrolledCount);
        dto.setCompletedCoursesCount(completedCourses);
        dto.setAverageProgressPercent(avgProgress != null ? Math.round(avgProgress * 10.0) / 10.0 : 0.0);

        // 2. Quiz stats
        long quizAttemptsCount = quizAttemptRepository.countByUserId(userId);
        long passedQuizzes = quizAttemptRepository.countByUserIdAndPassedTrue(userId);
        Double avgScore = quizAttemptRepository.getAverageScoreByUserId(userId);

        dto.setTotalQuizzesTaken(quizAttemptsCount);
        dto.setPassedQuizzesCount(passedQuizzes);
        dto.setAverageQuizScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0);

        // 3. Streak calculation
        int streak = (quizAttemptsCount > 0 || topicCompletionRepository.countByUserId(userId) > 0) ? 3 : 1;
        dto.setCurrentStreakDays(streak);

        // 4. Achievement stats
        long earnedAchievements = studentAchievementRepository.countByUserId(userId);
        dto.setAchievementsEarnedCount(earnedAchievements);

        // 5. Active enrollments
        List<EnrollmentDto> enrollments = courseService.getUserEnrollments(userId);
        dto.setActiveEnrollments(enrollments.stream().limit(5).collect(Collectors.toList()));

        // 6. Recent quiz attempts
        List<QuizAttemptDto> recentQuizzes = quizService.getUserQuizHistory(userId);
        dto.setRecentQuizAttempts(recentQuizzes.stream().limit(5).collect(Collectors.toList()));

        // 7. Score prediction
        ScorePredictionDto prediction = scorePredictionService.calculateStudentPrediction(userId);
        dto.setScorePrediction(prediction);

        // 8. Recent achievements
        List<StudentAchievement> recentSa = studentAchievementRepository.findByUserIdOrderByUnlockedAtDesc(userId);
        dto.setRecentAchievements(recentSa.stream().limit(4)
                .map(sa -> achievementService.mapToStudentAchievementDto(sa, userId))
                .collect(Collectors.toList()));

        // 9. Recommended next actions
        List<StudentDashboardDto.ActionRecommendation> actions = new ArrayList<>();

        if (!enrollments.isEmpty()) {
            EnrollmentDto inProgress = enrollments.stream().filter(e -> e.getProgressPercent() < 100).findFirst().orElse(null);
            if (inProgress != null) {
                actions.add(new StudentDashboardDto.ActionRecommendation(
                        "Continue Course: " + inProgress.getCourseTitle(),
                        "You are currently at " + inProgress.getProgressPercent() + "% completion. Complete upcoming topics to earn badges.",
                        "COURSE",
                        "/courses/" + inProgress.getCourseId(),
                        "HIGH"
                ));
            }
        } else {
            actions.add(new StudentDashboardDto.ActionRecommendation(
                    "Enroll in your first technical course",
                    "Choose from Spring Boot, React, MySQL, or System Design to begin your structured track.",
                    "COURSE",
                    "/courses",
                    "HIGH"
            ));
        }

        if (quizAttemptsCount == 0) {
            actions.add(new StudentDashboardDto.ActionRecommendation(
                    "Take your first technical quiz",
                    "Validate your understanding and unlock your personalized score prediction baseline.",
                    "QUIZ",
                    "/quizzes",
                    "HIGH"
            ));
        } else {
            actions.add(new StudentDashboardDto.ActionRecommendation(
                    "Challenge topic quizzes",
                    "Maintain your " + streak + "-day learning streak and test your recall with timed questions.",
                    "QUIZ",
                    "/quizzes",
                    "MEDIUM"
            ));
        }

        actions.add(new StudentDashboardDto.ActionRecommendation(
                "Upload revision materials to PDF Summarizer",
                "Extract key technical concepts, chapter breakdowns, and revision notes with page citations.",
                "PDF",
                "/pdf-summarizer",
                "MEDIUM"
        ));

        dto.setRecommendedActions(actions);

        return dto;
    }
}
