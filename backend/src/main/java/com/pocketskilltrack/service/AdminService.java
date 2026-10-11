package com.pocketskilltrack.service;

import com.pocketskilltrack.dto.admin.*;
import com.pocketskilltrack.dto.common.PagedResponse;
import com.pocketskilltrack.dto.course.CourseDetailDto;
import com.pocketskilltrack.entity.*;
import com.pocketskilltrack.exception.BadRequestException;
import com.pocketskilltrack.exception.ResourceNotFoundException;
import com.pocketskilltrack.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository optionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final PdfDocumentRepository pdfDocumentRepository;
    private final AchievementRepository achievementRepository;
    private final AuditLogRepository auditLogRepository;
    private final AuditLogService auditLogService;

    public AdminService(
            UserRepository userRepository,
            CourseRepository courseRepository,
            CourseModuleRepository moduleRepository,
            TopicRepository topicRepository,
            QuestionRepository questionRepository,
            AnswerOptionRepository optionRepository,
            EnrollmentRepository enrollmentRepository,
            QuizAttemptRepository quizAttemptRepository,
            PdfDocumentRepository pdfDocumentRepository,
            AchievementRepository achievementRepository,
            AuditLogRepository auditLogRepository,
            AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.pdfDocumentRepository = pdfDocumentRepository;
        this.achievementRepository = achievementRepository;
        this.auditLogRepository = auditLogRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public AdminOverviewDto getOverview() {
        AdminOverviewDto dto = new AdminOverviewDto();
        dto.setTotalStudents(userRepository.count());
        dto.setTotalCourses(courseRepository.count());
        dto.setTotalTopics(topicRepository.count());
        dto.setTotalQuestions(questionRepository.count());
        dto.setTotalEnrollments(enrollmentRepository.count());
        dto.setTotalQuizAttempts(quizAttemptRepository.countTotalAttempts());
        Double globalAvg = quizAttemptRepository.getGlobalAverageScore();
        dto.setGlobalAverageScore(globalAvg != null ? Math.round(globalAvg * 10.0) / 10.0 : 0.0);
        dto.setTotalPdfsUploaded(pdfDocumentRepository.count());

        Page<AuditLog> logs = auditLogRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 10));
        dto.setRecentAuditLogs(logs.getContent().stream().map(this::mapToAuditLogDto).collect(Collectors.toList()));

        return dto;
    }

    @Transactional(readOnly = true)
    public PagedResponse<UserAdminDto> getUsers(String query, Pageable pageable) {
        Page<User> users;
        if (query != null && !query.trim().isEmpty()) {
            users = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrStudentIdContainingIgnoreCase(
                    query.trim(), query.trim(), query.trim(), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        List<UserAdminDto> dtos = users.getContent().stream().map(u -> {
            UserAdminDto dto = new UserAdminDto();
            dto.setId(u.getId());
            dto.setStudentId(u.getStudentId());
            dto.setEmail(u.getEmail());
            dto.setFullName(u.getFullName());
            dto.setCollege(u.getCollege());
            dto.setDepartment(u.getDepartment());
            dto.setStudyYear(u.getStudyYear());
            dto.setEnabled(u.isEnabled());
            dto.setRoles(u.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()));
            dto.setCreatedAt(u.getCreatedAt());
            dto.setEnrolledCoursesCount(enrollmentRepository.countByUserId(u.getId()));
            dto.setQuizAttemptsCount(quizAttemptRepository.countByUserId(u.getId()));
            return dto;
        }).collect(Collectors.toList());

        return new PagedResponse<>(dtos, users.getNumber(), users.getSize(), users.getTotalElements(), users.getTotalPages(), users.isLast());
    }

    @Transactional
    public void toggleUserStatus(Long userId, Long adminId, String adminEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);

        auditLogService.log(adminId, adminEmail, "TOGGLE_USER_STATUS", "USER", userId.toString(), null,
                "Toggled user enabled status to " + user.isEnabled());
    }

    @Transactional
    public Course createCourse(CourseCreateRequest request, Long adminId, String adminEmail) {
        if (courseRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new BadRequestException("A course with slug '" + request.getSlug() + "' already exists");
        }

        Course course = new Course();
        course.setTitle(request.getTitle().trim());
        course.setSlug(request.getSlug().trim().toLowerCase());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory().trim());
        course.setDifficultyLevel(request.getDifficultyLevel());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPublished(request.isPublished());

        Course saved = courseRepository.save(course);

        auditLogService.log(adminId, adminEmail, "CREATE_COURSE", "COURSE", saved.getId().toString(), null,
                "Created course " + saved.getTitle());

        return saved;
    }

    @Transactional
    public Course updateCourse(Long courseId, CourseCreateRequest request, Long adminId, String adminEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        course.setTitle(request.getTitle().trim());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory().trim());
        course.setDifficultyLevel(request.getDifficultyLevel());
        course.setThumbnailUrl(request.getThumbnailUrl());
        course.setPublished(request.isPublished());

        Course saved = courseRepository.save(course);

        auditLogService.log(adminId, adminEmail, "UPDATE_COURSE", "COURSE", saved.getId().toString(), null,
                "Updated course " + saved.getTitle());

        return saved;
    }

    @Transactional
    public void deleteCourse(Long courseId, Long adminId, String adminEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        courseRepository.delete(course);

        auditLogService.log(adminId, adminEmail, "DELETE_COURSE", "COURSE", courseId.toString(), null,
                "Deleted course " + course.getTitle());
    }

    @Transactional
    public CourseModule addModule(Long courseId, String title, int moduleOrder, Long adminId, String adminEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        CourseModule module = new CourseModule();
        module.setCourse(course);
        module.setTitle(title.trim());
        module.setModuleOrder(moduleOrder);

        CourseModule saved = moduleRepository.save(module);

        auditLogService.log(adminId, adminEmail, "ADD_MODULE", "MODULE", saved.getId().toString(), null,
                "Added module " + saved.getTitle() + " to course " + course.getTitle());

        return saved;
    }

    @Transactional
    public Topic addTopic(Long moduleId, String title, String content, int readingTimeMinutes, int topicOrder, Long adminId, String adminEmail) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        Topic topic = new Topic();
        topic.setModule(module);
        topic.setTitle(title.trim());
        topic.setContent(content);
        topic.setReadingTimeMinutes(readingTimeMinutes);
        topic.setTopicOrder(topicOrder);

        Topic saved = topicRepository.save(topic);

        auditLogService.log(adminId, adminEmail, "ADD_TOPIC", "TOPIC", saved.getId().toString(), null,
                "Added topic " + saved.getTitle() + " to module " + module.getTitle());

        return saved;
    }

    @Transactional
    public Question createQuestion(QuestionCreateRequest request, Long adminId, String adminEmail) {
        Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        Question question = new Question();
        question.setTopic(topic);
        question.setQuestionText(request.getQuestionText().trim());
        question.setExplanation(request.getExplanation());
        question.setDifficulty(request.getDifficulty());
        question.setPoints(request.getPoints());
        question.setActive(true);

        List<AnswerOption> options = new ArrayList<>();
        boolean hasCorrect = false;

        for (QuestionCreateRequest.OptionCreateRequest optReq : request.getOptions()) {
            AnswerOption opt = new AnswerOption();
            opt.setQuestion(question);
            opt.setOptionText(optReq.getOptionText().trim());
            opt.setCorrect(optReq.isCorrect());
            opt.setOptionOrder(optReq.getOptionOrder());
            if (optReq.isCorrect()) hasCorrect = true;
            options.add(opt);
        }

        if (!hasCorrect) {
            throw new BadRequestException("At least one answer option must be marked as correct");
        }

        question.setOptions(options);
        Question saved = questionRepository.save(question);

        auditLogService.log(adminId, adminEmail, "CREATE_QUESTION", "QUESTION", saved.getId().toString(), null,
                "Created question for topic " + topic.getTitle());

        return saved;
    }

    @Transactional
    public void deleteQuestion(Long questionId, Long adminId, String adminEmail) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        questionRepository.delete(q);

        auditLogService.log(adminId, adminEmail, "DELETE_QUESTION", "QUESTION", questionId.toString(), null,
                "Deleted question " + questionId);
    }

    @Transactional
    public Achievement createAchievement(AchievementCreateRequest request, Long adminId, String adminEmail) {
        if (achievementRepository.findByBadgeCode(request.getBadgeCode()).isPresent()) {
            throw new BadRequestException("Badge code '" + request.getBadgeCode() + "' already exists");
        }

        Achievement ach = new Achievement(
                request.getBadgeCode().trim().toUpperCase(),
                request.getTitle().trim(),
                request.getDescription().trim(),
                request.getIconName().trim(),
                request.getCriteriaType(),
                request.getThresholdValue(),
                request.getCategory().trim().toUpperCase()
        );

        Achievement saved = achievementRepository.save(ach);

        auditLogService.log(adminId, adminEmail, "CREATE_ACHIEVEMENT", "ACHIEVEMENT", saved.getId().toString(), null,
                "Created badge " + saved.getBadgeCode());

        return saved;
    }

    @Transactional(readOnly = true)
    public PagedResponse<AuditLogDto> getAuditLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        List<AuditLogDto> dtos = page.getContent().stream().map(this::mapToAuditLogDto).collect(Collectors.toList());
        return new PagedResponse<>(dtos, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    private AuditLogDto mapToAuditLogDto(AuditLog log) {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(log.getId());
        dto.setActorId(log.getActorId());
        dto.setActorEmail(log.getActorEmail());
        dto.setAction(log.getAction());
        dto.setResourceType(log.getResourceType());
        dto.setResourceId(log.getResourceId());
        dto.setIpAddress(log.getIpAddress());
        dto.setDetailsJson(log.getDetailsJson());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }
}
