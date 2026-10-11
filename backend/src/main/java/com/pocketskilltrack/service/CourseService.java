package com.pocketskilltrack.service;

import com.pocketskilltrack.dto.common.PagedResponse;
import com.pocketskilltrack.dto.course.*;
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
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final TopicRepository topicRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TopicCompletionRepository topicCompletionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AchievementService achievementService;
    private final AuditLogService auditLogService;

    public CourseService(
            CourseRepository courseRepository,
            CourseModuleRepository moduleRepository,
            TopicRepository topicRepository,
            EnrollmentRepository enrollmentRepository,
            TopicCompletionRepository topicCompletionRepository,
            BookmarkRepository bookmarkRepository,
            QuestionRepository questionRepository,
            UserRepository userRepository,
            AchievementService achievementService,
            AuditLogService auditLogService) {
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.topicRepository = topicRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.topicCompletionRepository = topicCompletionRepository;
        this.bookmarkRepository = bookmarkRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.achievementService = achievementService;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public PagedResponse<CourseDto> getCourses(String query, String category, DifficultyLevel difficulty, Long userId, Pageable pageable) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        String cleanCategory = (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase("ALL")) ? category.trim() : null;

        Page<Course> coursePage = courseRepository.searchCourses(cleanQuery, cleanCategory, difficulty, pageable);

        Set<Long> enrolledCourseIds = new HashSet<>();
        Map<Long, Integer> progressMap = new HashMap<>();
        Set<Long> bookmarkedCourseIds = new HashSet<>();

        if (userId != null) {
            List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
            for (Enrollment e : enrollments) {
                enrolledCourseIds.add(e.getCourse().getId());
                progressMap.put(e.getCourse().getId(), e.getProgressPercent());
            }
            bookmarkedCourseIds.addAll(bookmarkRepository.findBookmarkedCourseIdsByUserId(userId));
        }

        List<CourseDto> dtos = coursePage.getContent().stream().map(course -> {
            CourseDto dto = mapToCourseDto(course);
            dto.setEnrolled(enrolledCourseIds.contains(course.getId()));
            dto.setProgressPercent(progressMap.getOrDefault(course.getId(), 0));
            dto.setBookmarked(bookmarkedCourseIds.contains(course.getId()));
            return dto;
        }).collect(Collectors.toList());

        return new PagedResponse<>(dtos, coursePage.getNumber(), coursePage.getSize(),
                coursePage.getTotalElements(), coursePage.getTotalPages(), coursePage.isLast());
    }

    @Transactional(readOnly = true)
    public List<String> getCategories() {
        return courseRepository.findDistinctCategories();
    }

    @Transactional(readOnly = true)
    public CourseDetailDto getCourseDetail(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        Set<Long> completedTopicIds = new HashSet<>();
        boolean isEnrolled = false;
        int progress = 0;
        boolean isBookmarked = false;

        if (userId != null) {
            completedTopicIds.addAll(topicCompletionRepository.findCompletedTopicIdsByUserId(userId));
            Optional<Enrollment> enr = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
            if (enr.isPresent()) {
                isEnrolled = true;
                progress = enr.get().getProgressPercent();
            }
            isBookmarked = bookmarkRepository.existsByUserIdAndCourseId(userId, courseId);
        }

        CourseDetailDto detailDto = new CourseDetailDto();
        detailDto.setId(course.getId());
        detailDto.setTitle(course.getTitle());
        detailDto.setSlug(course.getSlug());
        detailDto.setDescription(course.getDescription());
        detailDto.setCategory(course.getCategory());
        detailDto.setDifficultyLevel(course.getDifficultyLevel());
        detailDto.setThumbnailUrl(course.getThumbnailUrl());
        detailDto.setEnrolled(isEnrolled);
        detailDto.setProgressPercent(progress);
        detailDto.setBookmarked(isBookmarked);

        List<CourseModule> modules = moduleRepository.findByCourseIdOrderByModuleOrderAsc(courseId);
        List<CourseModuleDto> moduleDtos = new ArrayList<>();
        int totalTopics = 0;
        int completedCount = 0;

        for (CourseModule mod : modules) {
            CourseModuleDto modDto = new CourseModuleDto();
            modDto.setId(mod.getId());
            modDto.setTitle(mod.getTitle());
            modDto.setModuleOrder(mod.getModuleOrder());

            List<Topic> topics = topicRepository.findByModuleIdOrderByTopicOrderAsc(mod.getId());
            List<TopicDto> topicDtos = new ArrayList<>();

            for (Topic top : topics) {
                totalTopics++;
                boolean completed = completedTopicIds.contains(top.getId());
                if (completed) completedCount++;

                TopicDto tDto = new TopicDto();
                tDto.setId(top.getId());
                tDto.setTitle(top.getTitle());
                tDto.setReadingTimeMinutes(top.getReadingTimeMinutes());
                tDto.setTopicOrder(top.getTopicOrder());
                tDto.setCompleted(completed);
                tDto.setQuizAvailable(!top.getQuestions().isEmpty() || questionRepository.countByTopicId(top.getId()) > 0);

                topicDtos.add(tDto);
            }
            modDto.setTopics(topicDtos);
            moduleDtos.add(modDto);
        }

        detailDto.setTotalTopics(totalTopics);
        detailDto.setCompletedTopics(completedCount);
        detailDto.setModules(moduleDtos);

        return detailDto;
    }

    @Transactional
    public EnrollmentDto enroll(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseGet(() -> enrollmentRepository.save(new Enrollment(user, course)));

        auditLogService.log(userId, user.getEmail(), "ENROLL_COURSE", "COURSE", courseId.toString(), null, "Enrolled in " + course.getTitle());

        return mapToEnrollmentDto(enrollment);
    }

    @Transactional
    public boolean toggleBookmark(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<Bookmark> existing = bookmarkRepository.findByUserIdAndCourseId(userId, courseId);
        if (existing.isPresent()) {
            bookmarkRepository.delete(existing.get());
            return false;
        } else {
            bookmarkRepository.save(new Bookmark(user, course));
            return true;
        }
    }

    @Transactional
    public TopicDetailDto getTopicDetail(Long topicId, Long userId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));

        boolean completed = false;
        if (userId != null) {
            completed = topicCompletionRepository.existsByUserIdAndTopicId(userId, topicId);
        }

        Course course = topic.getModule().getCourse();
        List<Topic> allCourseTopics = topicRepository.findAllByCourseId(course.getId());

        Long prevId = null;
        Long nextId = null;
        for (int i = 0; i < allCourseTopics.size(); i++) {
            if (allCourseTopics.get(i).getId().equals(topicId)) {
                if (i > 0) prevId = allCourseTopics.get(i - 1).getId();
                if (i < allCourseTopics.size() - 1) nextId = allCourseTopics.get(i + 1).getId();
                break;
            }
        }

        TopicDetailDto dto = new TopicDetailDto();
        dto.setId(topic.getId());
        dto.setTitle(topic.getTitle());
        dto.setContent(topic.getContent());
        dto.setReadingTimeMinutes(topic.getReadingTimeMinutes());
        dto.setTopicOrder(topic.getTopicOrder());
        dto.setCompleted(completed);
        dto.setQuizAvailable(questionRepository.countByTopicId(topic.getId()) > 0);
        dto.setCourseId(course.getId());
        dto.setCourseTitle(course.getTitle());
        dto.setModuleId(topic.getModule().getId());
        dto.setModuleTitle(topic.getModule().getTitle());
        dto.setPrevTopicId(prevId);
        dto.setNextTopicId(nextId);

        return dto;
    }

    @Transactional
    public int completeTopic(Long topicId, Long userId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Course course = topic.getModule().getCourse();

        // Ensure user is enrolled
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, course.getId())
                .orElseGet(() -> enrollmentRepository.save(new Enrollment(user, course)));

        if (!topicCompletionRepository.existsByUserIdAndTopicId(userId, topicId)) {
            topicCompletionRepository.save(new TopicCompletion(user, topic));

            // Check first topic achievement
            long totalCompletedOverall = topicCompletionRepository.countByUserId(userId);
            if (totalCompletedOverall == 1) {
                achievementService.evaluateAndAward(user, CriteriaType.FIRST_TOPIC, 1);
            }
        }

        // Recalculate progress %
        long totalTopics = topicRepository.countByCourseId(course.getId());
        long completedInCourse = topicCompletionRepository.countCompletedTopicsByCourse(userId, course.getId());

        int progress = 0;
        if (totalTopics > 0) {
            progress = (int) Math.round(((double) completedInCourse / totalTopics) * 100);
        }
        if (progress > 100) progress = 100;

        enrollment.setProgressPercent(progress);
        if (progress >= 100 && enrollment.getCompletedAt() == null) {
            enrollment.setCompletedAt(LocalDateTime.now());
            long totalCompletedCourses = enrollmentRepository.countByUserIdAndProgressPercent(userId, 100);
            achievementService.evaluateAndAward(user, CriteriaType.COURSE_COMPLETION, (int) totalCompletedCourses);
        }
        enrollmentRepository.save(enrollment);

        auditLogService.log(userId, user.getEmail(), "COMPLETE_TOPIC", "TOPIC", topicId.toString(), null, "Completed topic " + topic.getTitle());

        return progress;
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDto> getUserEnrollments(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return enrollments.stream().map(this::mapToEnrollmentDto).collect(Collectors.toList());
    }

    private CourseDto mapToCourseDto(Course course) {
        CourseDto dto = new CourseDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setSlug(course.getSlug());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory());
        dto.setDifficultyLevel(course.getDifficultyLevel());
        dto.setThumbnailUrl(course.getThumbnailUrl());
        dto.setModuleCount(course.getModules().size());
        int topicCount = 0;
        for (CourseModule m : course.getModules()) {
            topicCount += m.getTopics().size();
        }
        dto.setTopicCount(topicCount);
        return dto;
    }

    private EnrollmentDto mapToEnrollmentDto(Enrollment enrollment) {
        EnrollmentDto dto = new EnrollmentDto();
        dto.setId(enrollment.getId());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setCourseTitle(enrollment.getCourse().getTitle());
        dto.setCourseCategory(enrollment.getCourse().getCategory());
        dto.setCourseThumbnailUrl(enrollment.getCourse().getThumbnailUrl());
        dto.setProgressPercent(enrollment.getProgressPercent());
        dto.setEnrolledAt(enrollment.getEnrolledAt());
        dto.setCompletedAt(enrollment.getCompletedAt());
        return dto;
    }
}
