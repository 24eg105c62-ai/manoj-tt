package com.pocketskilltrack.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pocketskilltrack.dto.prediction.ScorePredictionDto;
import com.pocketskilltrack.entity.QuizAttempt;
import com.pocketskilltrack.entity.ScorePrediction;
import com.pocketskilltrack.entity.User;
import com.pocketskilltrack.exception.ResourceNotFoundException;
import com.pocketskilltrack.repository.QuizAttemptRepository;
import com.pocketskilltrack.repository.ScorePredictionRepository;
import com.pocketskilltrack.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScorePredictionService {

    private static final String DISCLAIMER =
            "Statistical Notice: Predictions represent probabilistic performance baselines derived from empirical quiz accuracy and recent momentum. They are diagnostic estimations designed for goal setting and study prioritization, not a contractual score guarantee.";

    private final QuizAttemptRepository attemptRepository;
    private final ScorePredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScorePredictionService(
            QuizAttemptRepository attemptRepository,
            ScorePredictionRepository predictionRepository,
            UserRepository userRepository) {
        this.attemptRepository = attemptRepository;
        this.predictionRepository = predictionRepository;
        this.userRepository = userRepository;
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public ScorePredictionDto calculateStudentPrediction(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<QuizAttempt> attempts = attemptRepository.findAllByUserIdChronological(userId);
        List<Double> scores = attempts.stream().map(QuizAttempt::getPercentage).collect(Collectors.toList());
        int n = scores.size();

        ScorePredictionDto dto = new ScorePredictionDto();
        dto.setCategoryOrTopic("Overall Technical Mastery");
        dto.setLimitationsDisclaimer(DISCLAIMER);
        dto.setRecentQuizScores(scores.size() > 10 ? scores.subList(scores.size() - 10, scores.size()) : scores);

        if (n == 0) {
            dto.setPredictedScoreMin(60.0);
            dto.setPredictedScoreMax(75.0);
            dto.setPredictedScoreMid(67.5);
            dto.setConfidenceLevel("INSUFFICIENT_DATA");
            dto.setSampleSize(0);
            dto.setPrimaryFactors(Arrays.asList(
                    "No completed quizzes recorded yet",
                    "Default baseline applied based on initial curriculum benchmark",
                    "Take at least 2 quizzes to unlock tailored personalized predictions"
            ));
            dto.setCalculatedAt(LocalDateTime.now());
            return dto;
        }

        // 1. Mean and Variance
        double sum = 0.0;
        for (double s : scores) sum += s;
        double mean = sum / n;

        double sumSq = 0.0;
        for (double s : scores) sumSq += Math.pow(s - mean, 2);
        double stdDev = Math.sqrt(sumSq / n);

        // 2. Recent performance weight (last 3 attempts or all if fewer)
        int recentCount = Math.min(3, n);
        List<Double> recentScores = scores.subList(n - recentCount, n);
        double recentSum = 0.0;
        for (double s : recentScores) recentSum += s;
        double recentMean = recentSum / recentCount;

        double momentumDelta = recentMean - mean;

        // 3. Midpoint computation (60% weight to recent trajectory, 40% to overall average)
        double rawMid = (recentMean * 0.6) + (mean * 0.4);
        double boundedMid = Math.min(98.0, Math.max(25.0, rawMid));

        // 4. Spread margin calculation
        double margin = Math.max(4.0, Math.min(14.0, (stdDev * 0.9) / Math.sqrt(Math.max(1, n)) + 3.5));
        double minScore = Math.max(15.0, Math.round((boundedMid - margin) * 10.0) / 10.0);
        double maxScore = Math.min(100.0, Math.round((boundedMid + margin) * 10.0) / 10.0);
        double finalMid = Math.round(((minScore + maxScore) / 2.0) * 10.0) / 10.0;

        // 5. Confidence determination
        String confidence;
        if (n >= 5 && stdDev < 15.0) {
            confidence = "HIGH";
        } else if (n >= 3) {
            confidence = "MODERATE";
        } else {
            confidence = "LOW";
        }

        // 6. Factors breakdown
        List<String> factors = new ArrayList<>();
        factors.add(String.format(Locale.US, "Historical average accuracy across %d attempt(s): %.1f%%", n, mean));
        if (momentumDelta >= 0) {
            factors.add(String.format(Locale.US, "Recent quiz momentum: +%.1f%% over last %d attempt(s)", momentumDelta, recentCount));
        } else {
            factors.add(String.format(Locale.US, "Recent quiz trend adjustment: %.1f%% over last %d attempt(s)", momentumDelta, recentCount));
        }
        factors.add(String.format(Locale.US, "Consistency volatility indicator (standard deviation): ±%.1f%%", stdDev));
        factors.add(String.format("Calculated confidence level: %s based on empirical sample size", confidence));

        dto.setPredictedScoreMin(minScore);
        dto.setPredictedScoreMax(maxScore);
        dto.setPredictedScoreMid(finalMid);
        dto.setConfidenceLevel(confidence);
        dto.setSampleSize(n);
        dto.setPrimaryFactors(factors);
        dto.setCalculatedAt(LocalDateTime.now());

        // Persist prediction for historical tracking
        try {
            ScorePrediction entity = new ScorePrediction();
            entity.setUser(user);
            entity.setCategoryOrTopic(dto.getCategoryOrTopic());
            entity.setPredictedScoreMin(minScore);
            entity.setPredictedScoreMax(maxScore);
            entity.setPredictedScoreMid(finalMid);
            entity.setConfidenceLevel(confidence);
            entity.setSampleSize(n);
            entity.setPrimaryFactorsJson(objectMapper.writeValueAsString(factors));
            entity.setCalculatedAt(dto.getCalculatedAt());
            predictionRepository.save(entity);
        } catch (Exception ignored) {}

        return dto;
    }
}
