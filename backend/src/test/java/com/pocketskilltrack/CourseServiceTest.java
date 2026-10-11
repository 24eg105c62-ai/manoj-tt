package com.pocketskilltrack;

import com.pocketskilltrack.dto.course.CourseDetailDto;
import com.pocketskilltrack.dto.course.EnrollmentDto;
import com.pocketskilltrack.entity.*;
import com.pocketskilltrack.repository.*;
import com.pocketskilltrack.service.AchievementService;
import com.pocketskilltrack.service.AuditLogService;
import com.pocketskilltrack.service.CourseService;
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
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseModuleRepository moduleRepository;
    @Mock
    private TopicRepository topicRepository;
    @Mock
    private EnrollmentRepository enrollmentRepository;
    @Mock
    private TopicCompletionRepository topicCompletionRepository;
    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AchievementService achievementService;
    @Mock
    private AuditLogService auditLogService;

    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseService = new CourseService(
                courseRepository, moduleRepository, topicRepository,
                enrollmentRepository, topicCompletionRepository, bookmarkRepository,
                questionRepository, userRepository, achievementService, auditLogService
        );
    }

    @Test
    void testEnrollStudent_Success() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Spring Boot 3");

        User user = new User();
        user.setId(5L);
        user.setEmail("student@univ.edu");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(enrollmentRepository.findByUserIdAndCourseId(5L, 1L)).thenReturn(Optional.empty());

        Enrollment savedEnrollment = new Enrollment(user, course);
        savedEnrollment.setId(100L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(savedEnrollment);

        EnrollmentDto dto = courseService.enroll(1L, 5L);

        assertNotNull(dto);
        assertEquals(1L, dto.getCourseId());
        assertEquals("Spring Boot 3", dto.getCourseTitle());
        assertEquals(0, dto.getProgressPercent());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void testCompleteTopic_RecalculatesCourseProgress() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("React 18");

        CourseModule module = new CourseModule();
        module.setId(10L);
        module.setCourse(course);

        Topic topic = new Topic();
        topic.setId(20L);
        topic.setTitle("Hooks Intro");
        topic.setModule(module);

        User user = new User();
        user.setId(5L);

        Enrollment enrollment = new Enrollment(user, course);
        enrollment.setProgressPercent(0);

        when(topicRepository.findById(20L)).thenReturn(Optional.of(topic));
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(enrollmentRepository.findByUserIdAndCourseId(5L, 1L)).thenReturn(Optional.of(enrollment));
        when(topicCompletionRepository.existsByUserIdAndTopicId(5L, 20L)).thenReturn(false);
        when(topicRepository.countByCourseId(1L)).thenReturn(4L);
        when(topicCompletionRepository.countCompletedTopicsByCourse(5L, 1L)).thenReturn(2L); // 2 out of 4 = 50%

        int progress = courseService.completeTopic(20L, 5L);

        assertEquals(50, progress);
        assertEquals(50, enrollment.getProgressPercent());
        verify(enrollmentRepository, times(1)).save(enrollment);
    }
}
