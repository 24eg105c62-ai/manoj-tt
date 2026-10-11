package com.pocketskilltrack.service;

import com.pocketskilltrack.dto.common.PagedResponse;
import com.pocketskilltrack.dto.quiz.*;
import com.pocketskilltrack.entity.*;
import com.pocketskilltrack.exception.BadRequestException;
import com.pocketskilltrack.exception.ResourceNotFoundException;
import com.pocketskilltrack.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private static final int MAX_ATTEMPTS_PER_TOPIC = 10;
    private static final double PASS_THRESHOLD_PERCENT = 70.0;

    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository optionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizResponseRepository responseRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final AchievementService achievementService;
    private final AuditLogService auditLogService;

    public QuizService(
            QuestionRepository questionRepository,
            AnswerOptionRepository optionRepository,
            QuizAttemptRepository attemptRepository,
            QuizResponseRepository responseRepository,
            TopicRepository topicRepository,
            UserRepository userRepository,
            AchievementService achievementService,
            AuditLogService auditLogService) {
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.attemptRepository = attemptRepository;
        this.responseRepository = responseRepository;
        this.topicRepository = topicRepository;
        this.userRepository = userRepository;
        this.achievementService = achievementService;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<QuizQuestionDto> getQuizQuestions(Long topicId, Difficulty difficulty, Long userId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));

        if (userId != null) {
            long existingAttempts = attemptRepository.countByUserIdAndTopicId(userId, topicId);
            if (existingAttempts >= MAX_ATTEMPTS_PER_TOPIC) {
                throw new BadRequestException("You have reached the maximum allowed attempts (" + MAX_ATTEMPTS_PER_TOPIC + ") for this quiz topic");
            }
        }

        List<Question> questions = questionRepository.findByTopicIdAndDifficulty(topicId, difficulty);
        if (questions.isEmpty()) {
            questions = questionRepository.findByTopicIdAndActiveTrue(topicId);
        }

        // Anti-cheating: Never send correct answers or explanations to the client before submission!
        return questions.stream().map(q -> {
            QuizQuestionDto dto = new QuizQuestionDto();
            dto.setId(q.getId());
            dto.setQuestionText(q.getQuestionText());
            dto.setDifficulty(q.getDifficulty());
            dto.setPoints(q.getPoints());

            List<AnswerOptionDto> options = q.getOptions().stream().map(opt ->
                    new AnswerOptionDto(opt.getId(), opt.getOptionText(), opt.getOptionOrder())
            ).collect(Collectors.toList());

            dto.setOptions(options);
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public QuizSubmissionResponse submitQuiz(QuizSubmitRequest request, Long userId) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        long existingAttempts = attemptRepository.countByUserIdAndTopicId(userId, topic.getId());
        if (existingAttempts >= MAX_ATTEMPTS_PER_TOPIC) {
            throw new BadRequestException("Attempt limit exceeded for this quiz");
        }

        List<Question> questions = questionRepository.findByTopicIdAndActiveTrue(topic.getId());
        if (questions.isEmpty()) {
            throw new BadRequestException("No active questions available for this quiz");
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(user);
        attempt.setTopic(topic);
        attempt.setStartTime(LocalDateTime.now().minusSeconds(Math.max(1, request.getTimeSpentSeconds())));
        attempt.setCompletedTime(LocalDateTime.now());
        attempt.setTimeSpentSeconds(request.getTimeSpentSeconds());

        int earnedPoints = 0;
        int totalPossiblePoints = 0;
        List<QuizResponse> responses = new ArrayList<>();
        List<QuestionResultDto> resultDtos = new ArrayList<>();

        Map<Long, Long> submittedAnswers = (request.getAnswers() != null) ? request.getAnswers() : Collections.emptyMap();

        for (Question q : questions) {
            totalPossiblePoints += q.getPoints();
            Long selectedOptId = submittedAnswers.get(q.getId());

            AnswerOption selectedOpt = null;
            AnswerOption correctOpt = null;

            for (AnswerOption opt : q.getOptions()) {
                if (opt.isCorrect()) {
                    correctOpt = opt;
                }
                if (selectedOptId != null && opt.getId().equals(selectedOptId)) {
                    selectedOpt = opt;
                }
            }

            boolean isCorrect = (selectedOpt != null && selectedOpt.isCorrect());
            int pts = isCorrect ? q.getPoints() : 0;
            earnedPoints += pts;

            QuizResponse resp = new QuizResponse();
            resp.setAttempt(attempt);
            resp.setQuestion(q);
            resp.setSelectedOption(selectedOpt);
            resp.setCorrect(isCorrect);
            resp.setPointsAwarded(pts);
            responses.add(resp);

            // Build result revealing correct answer and explanation now that quiz is submitted
            QuestionResultDto resDto = new QuestionResultDto();
            resDto.setQuestionId(q.getId());
            resDto.setQuestionText(q.getQuestionText());
            resDto.setSelectedOptionId(selectedOptId);
            resDto.setCorrectOptionId(correctOpt != null ? correctOpt.getId() : null);
            resDto.setCorrect(isCorrect);
            resDto.setPointsAwarded(pts);
            resDto.setTotalPoints(q.getPoints());
            resDto.setExplanation(q.getExplanation());

            List<QuestionResultDto.AnswerOptionResultDto> optResults = q.getOptions().stream().map(opt ->
                    new QuestionResultDto.AnswerOptionResultDto(
                            opt.getId(),
                            opt.getOptionText(),
                            opt.isCorrect(),
                            selectedOptId != null && opt.getId().equals(selectedOptId)
                    )
            ).collect(Collectors.toList());
            resDto.setOptions(optResults);

            resultDtos.add(resDto);
        }

        double percentage = (totalPossiblePoints > 0) ? ((double) earnedPoints / totalPossiblePoints) * 100.0 : 0.0;
        boolean passed = percentage >= PASS_THRESHOLD_PERCENT;

        attempt.setScore(earnedPoints);
        attempt.setTotalPoints(totalPossiblePoints);
        attempt.setPercentage(Math.round(percentage * 10.0) / 10.0);
        attempt.setPassed(passed);
        attempt.setResponses(responses);

        QuizAttempt savedAttempt = attemptRepository.save(attempt);

        // Achievement triggers
        long totalAttemptsCount = attemptRepository.countByUserId(userId);
        achievementService.evaluateAndAward(user, CriteriaType.QUIZ_COUNT, (int) totalAttemptsCount);

        if (percentage >= 90.0) {
            achievementService.evaluateAndAward(user, CriteriaType.QUIZ_SCORE, (int) Math.round(percentage));
        }

        auditLogService.log(userId, user.getEmail(), "SUBMIT_QUIZ", "QUIZ", savedAttempt.getId().toString(), null,
                "Scored " + savedAttempt.getPercentage() + "% on topic " + topic.getTitle());

        QuizSubmissionResponse response = new QuizSubmissionResponse();
        response.setAttemptId(savedAttempt.getId());
        response.setTopicId(topic.getId());
        response.setTopicTitle(topic.getTitle());
        response.setScore(earnedPoints);
        response.setTotalPoints(totalPossiblePoints);
        response.setPercentage(savedAttempt.getPercentage());
        response.setPassed(passed);
        response.setTimeSpentSeconds(request.getTimeSpentSeconds());
        response.setCompletedTime(savedAttempt.getCompletedTime());
        response.setQuestionResults(resultDtos);

        return response;
    }

    @Transactional(readOnly = true)
    public List<QuizAttemptDto> getUserQuizHistory(Long userId) {
        List<QuizAttempt> attempts = attemptRepository.findByUserIdOrderByCompletedTimeDesc(userId);
        return attempts.stream().map(this::mapToAttemptDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagedResponse<QuizAttemptDto> getUserQuizHistoryPaged(Long userId, Pageable pageable) {
        Page<QuizAttempt> page = attemptRepository.findByUserIdOrderByCompletedTimeDesc(userId, pageable);
        List<QuizAttemptDto> dtos = page.getContent().stream().map(this::mapToAttemptDto).collect(Collectors.toList());
        return new PagedResponse<>(dtos, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    private QuizAttemptDto mapToAttemptDto(QuizAttempt attempt) {
        QuizAttemptDto dto = new QuizAttemptDto();
        dto.setId(attempt.getId());
        dto.setTopicId(attempt.getTopic().getId());
        dto.setTopicTitle(attempt.getTopic().getTitle());
        dto.setCourseTitle(attempt.getTopic().getModule().getCourse().getTitle());
        dto.setScore(attempt.getScore());
        dto.setTotalPoints(attempt.getTotalPoints());
        dto.setPercentage(attempt.getPercentage());
        dto.setPassed(attempt.isPassed());
        dto.setTimeSpentSeconds(attempt.getTimeSpentSeconds());
        dto.setCompletedTime(attempt.getCompletedTime());
        return dto;
    }
}
