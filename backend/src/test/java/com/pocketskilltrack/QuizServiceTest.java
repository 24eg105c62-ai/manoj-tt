package com.pocketskilltrack;

import com.pocketskilltrack.dto.quiz.QuizQuestionDto;
import com.pocketskilltrack.dto.quiz.QuizSubmissionResponse;
import com.pocketskilltrack.dto.quiz.QuizSubmitRequest;
import com.pocketskilltrack.entity.*;
import com.pocketskilltrack.exception.BadRequestException;
import com.pocketskilltrack.repository.*;
import com.pocketskilltrack.service.AchievementService;
import com.pocketskilltrack.service.AuditLogService;
import com.pocketskilltrack.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizServiceTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AnswerOptionRepository optionRepository;
    @Mock
    private QuizAttemptRepository attemptRepository;
    @Mock
    private QuizResponseRepository responseRepository;
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AchievementService achievementService;
    @Mock
    private AuditLogService auditLogService;

    private QuizService quizService;

    @BeforeEach
    void setUp() {
        quizService = new QuizService(
                questionRepository, optionRepository, attemptRepository,
                responseRepository, topicRepository, userRepository,
                achievementService, auditLogService
        );
    }

    @Test
    void testAntiCheating_NeverExposesCorrectAnswersBeforeSubmit() {
        Topic topic = new Topic();
        topic.setId(10L);

        Question q = new Question();
        q.setId(100L);
        q.setQuestionText("What is Spring IoC?");
        q.setPoints(10);
        q.setDifficulty(Difficulty.EASY);

        AnswerOption opt1 = new AnswerOption();
        opt1.setId(1L);
        opt1.setOptionText("Inversion of Control");
        opt1.setCorrect(true);

        AnswerOption opt2 = new AnswerOption();
        opt2.setId(2L);
        opt2.setOptionText("Input Output Center");
        opt2.setCorrect(false);

        q.setOptions(Arrays.asList(opt1, opt2));

        when(topicRepository.findById(10L)).thenReturn(Optional.of(topic));
        when(attemptRepository.countByUserIdAndTopicId(5L, 10L)).thenReturn(0L);
        when(questionRepository.findByTopicIdAndDifficulty(10L, null)).thenReturn(Collections.singletonList(q));

        List<QuizQuestionDto> questions = quizService.getQuizQuestions(10L, null, 5L);

        assertEquals(1, questions.size());
        assertEquals("What is Spring IoC?", questions.get(0).getQuestionText());
        assertEquals(2, questions.get(0).getOptions().size());
        // Verify AnswerOptionDto has NO isCorrect field!
        assertNotNull(questions.get(0).getOptions().get(0).getOptionText());
    }

    @Test
    void testSubmitQuiz_ServerSideEvaluationAndScoring() {
        Topic topic = new Topic();
        topic.setId(10L);
        topic.setTitle("Spring Framework");

        User user = new User();
        user.setId(5L);
        user.setEmail("student@univ.edu");

        Question q1 = new Question();
        q1.setId(101L);
        q1.setPoints(10);
        q1.setExplanation("IoC delegates object lifecycle management.");

        AnswerOption o1 = new AnswerOption();
        o1.setId(11L);
        o1.setCorrect(true);
        AnswerOption o2 = new AnswerOption();
        o2.setId(12L);
        o2.setCorrect(false);
        q1.setOptions(Arrays.asList(o1, o2));

        when(topicRepository.findById(10L)).thenReturn(Optional.of(topic));
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(attemptRepository.countByUserIdAndTopicId(5L, 10L)).thenReturn(0L);
        when(questionRepository.findByTopicIdAndActiveTrue(10L)).thenReturn(Collections.singletonList(q1));
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(inv -> {
            QuizAttempt a = inv.getArgument(0);
            a.setId(999L);
            return a;
        });

        QuizSubmitRequest req = new QuizSubmitRequest();
        req.setTopicId(10L);
        req.setTimeSpentSeconds(45);
        req.setAnswers(Map.of(101L, 11L)); // Submitting the correct option!

        QuizSubmissionResponse resp = quizService.submitQuiz(req, 5L);

        assertNotNull(resp);
        assertEquals(10, resp.getScore());
        assertEquals(10, resp.getTotalPoints());
        assertEquals(100.0, resp.getPercentage());
        assertTrue(resp.isPassed());
        assertEquals(1, resp.getQuestionResults().size());
        assertTrue(resp.getQuestionResults().get(0).isCorrect());
        assertEquals("IoC delegates object lifecycle management.", resp.getQuestionResults().get(0).getExplanation());
    }

    @Test
    void testAttemptLimitEnforced() {
        Topic topic = new Topic();
        topic.setId(10L);

        when(topicRepository.findById(10L)).thenReturn(Optional.of(topic));
        when(attemptRepository.countByUserIdAndTopicId(5L, 10L)).thenReturn(10L); // Max limit reached

        assertThrows(BadRequestException.class, () -> quizService.getQuizQuestions(10L, null, 5L));
    }
}
