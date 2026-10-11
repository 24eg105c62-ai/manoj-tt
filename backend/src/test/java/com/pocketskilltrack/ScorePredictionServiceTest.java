package com.pocketskilltrack;

import com.pocketskilltrack.dto.prediction.ScorePredictionDto;
import com.pocketskilltrack.entity.QuizAttempt;
import com.pocketskilltrack.entity.User;
import com.pocketskilltrack.repository.QuizAttemptRepository;
import com.pocketskilltrack.repository.ScorePredictionRepository;
import com.pocketskilltrack.repository.UserRepository;
import com.pocketskilltrack.service.ScorePredictionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ScorePredictionServiceTest {

    @Mock
    private QuizAttemptRepository attemptRepository;
    @Mock
    private ScorePredictionRepository predictionRepository;
    @Mock
    private UserRepository userRepository;

    private ScorePredictionService predictionService;

    @BeforeEach
    void setUp() {
        predictionService = new ScorePredictionService(attemptRepository, predictionRepository, userRepository);
    }

    @Test
    void testPredictionWithNoQuizzes_ReturnsBaselineWithDisclaimer() {
        User user = new User();
        user.setId(5L);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(attemptRepository.findAllByUserIdChronological(5L)).thenReturn(Collections.emptyList());

        ScorePredictionDto dto = predictionService.calculateStudentPrediction(5L);

        assertNotNull(dto);
        assertEquals("INSUFFICIENT_DATA", dto.getConfidenceLevel());
        assertEquals(0, dto.getSampleSize());
        assertTrue(dto.getPredictedScoreMin() < dto.getPredictedScoreMax());
        assertNotNull(dto.getLimitationsDisclaimer());
        assertTrue(dto.getLimitationsDisclaimer().contains("probabilistic performance baselines"));
    }

    @Test
    void testPredictionWithMultipleQuizzes_CalculatesStatisticalInterval() {
        User user = new User();
        user.setId(5L);

        QuizAttempt a1 = new QuizAttempt(); a1.setPercentage(70.0);
        QuizAttempt a2 = new QuizAttempt(); a2.setPercentage(80.0);
        QuizAttempt a3 = new QuizAttempt(); a3.setPercentage(85.0);
        QuizAttempt a4 = new QuizAttempt(); a4.setPercentage(90.0);
        QuizAttempt a5 = new QuizAttempt(); a5.setPercentage(95.0);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(attemptRepository.findAllByUserIdChronological(5L)).thenReturn(Arrays.asList(a1, a2, a3, a4, a5));

        ScorePredictionDto dto = predictionService.calculateStudentPrediction(5L);

        assertNotNull(dto);
        assertEquals(5, dto.getSampleSize());
        assertEquals("HIGH", dto.getConfidenceLevel());
        assertTrue(dto.getPredictedScoreMin() <= dto.getPredictedScoreMid());
        assertTrue(dto.getPredictedScoreMid() <= dto.getPredictedScoreMax());
        assertFalse(dto.getPrimaryFactors().isEmpty());
        assertTrue(dto.getPredictedScoreMax() <= 100.0);
        verify(predictionRepository, times(1)).save(any());
    }
}
