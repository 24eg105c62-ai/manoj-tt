package com.pocketskilltrack.config;

import com.pocketskilltrack.entity.*;
import com.pocketskilltrack.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseModuleRepository moduleRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository optionRepository;
    private final AchievementRepository achievementRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            CourseModuleRepository moduleRepository,
            TopicRepository topicRepository,
            QuestionRepository questionRepository,
            AnswerOptionRepository optionRepository,
            AchievementRepository achievementRepository,
            PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.moduleRepository = moduleRepository;
        this.topicRepository = topicRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.achievementRepository = achievementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Checking database initialization...");

        // 1. Roles
        Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_STUDENT)));
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN)));

        // 2. Provision Admin User securely if not present
        if (!userRepository.existsByEmail("admin@pocketskilltrack.edu")) {
            User admin = new User();
            admin.setStudentId("ADMIN001");
            admin.setEmail("admin@pocketskilltrack.edu");
            admin.setPasswordHash(passwordEncoder.encode("AdminPassword123!"));
            admin.setFullName("Platform Administrator");
            admin.setCollege("Department of Academic Operations");
            admin.setDepartment("System Administration");
            admin.setStudyYear("Faculty / Staff");
            admin.setBio("Chief system administrator and platform moderator.");
            admin.setEnabled(true);
            admin.setRoles(new HashSet<>(Arrays.asList(adminRole, studentRole)));
            userRepository.save(admin);
            logger.info("Default administrator provisioned: admin@pocketskilltrack.edu / AdminPassword123!");
        }

        // 3. Demo Student
        if (!userRepository.existsByEmail("student@pocketskilltrack.edu")) {
            User student = new User();
            student.setStudentId("STU2026001");
            student.setEmail("student@pocketskilltrack.edu");
            student.setPasswordHash(passwordEncoder.encode("StudentPass123!"));
            student.setFullName("Alex Rivera");
            student.setCollege("Institute of Engineering & Technology");
            student.setDepartment("Computer Science");
            student.setStudyYear("Year 3");
            student.setBio("Full-stack enthusiast passionate about distributed systems and reactive architectures.");
            student.setEnabled(true);
            student.setRoles(Collections.singleton(studentRole));
            userRepository.save(student);
            logger.info("Demo student provisioned: student@pocketskilltrack.edu / StudentPass123!");
        }

        // 4. Badges / Achievements
        initAchievements();

        // 5. Technical Courses & Quizzes
        if (courseRepository.count() == 0) {
            initCoursesAndQuizzes();
        }

        logger.info("Database initialization completed successfully.");
    }

    private void initAchievements() {
        createBadgeIfAbsent("FIRST_STEP", "First Step", "Completed your very first technical topic", "Award", CriteriaType.FIRST_TOPIC, 1, "LEARNING");
        createBadgeIfAbsent("COURSE_CHAMP", "Course Conqueror", "Graduated and completed an entire technical course", "GraduationCap", CriteriaType.COURSE_COMPLETION, 1, "MILESTONE");
        createBadgeIfAbsent("QUIZ_ACE", "Score Ace", "Scored 90% or higher on a technical quiz", "Sparkles", CriteriaType.QUIZ_SCORE, 90, "MASTERY");
        createBadgeIfAbsent("QUIZ_DEVOTEE", "Practice Master", "Completed 5 topic-level quizzes", "Brain", CriteriaType.QUIZ_COUNT, 5, "EFFORT");
        createBadgeIfAbsent("PERSISTENT", "Streak Champion", "Maintained an active study streak", "Flame", CriteriaType.STREAK_DAYS, 3, "CONSISTENCY");
    }

    private void createBadgeIfAbsent(String code, String title, String desc, String icon, CriteriaType type, int threshold, String category) {
        if (achievementRepository.findByBadgeCode(code).isEmpty()) {
            achievementRepository.save(new Achievement(code, title, desc, icon, type, threshold, category));
        }
    }

    private void initCoursesAndQuizzes() {
        // COURSE 1: Spring Boot 3
        Course course1 = new Course();
        course1.setTitle("Spring Boot 3 & Microservices Architecture");
        course1.setSlug("spring-boot-3-microservices");
        course1.setDescription("Master enterprise Java backend engineering with Spring Boot 3, Spring Data JPA, RESTful APIs, and cloud-ready patterns.");
        course1.setCategory("Backend Engineering");
        course1.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
        course1.setThumbnailUrl("https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=600&auto=format&fit=crop&q=80");
        course1.setPublished(true);
        Course savedC1 = courseRepository.save(course1);

        CourseModule c1m1 = createModule(savedC1, "Module 1: Core Framework & Autoconfiguration", 1);
        Topic c1t1 = createTopic(c1m1, "Spring Boot Autoconfiguration & Starters",
                "## Spring Boot Autoconfiguration\n\nSpring Boot radically simplifies Java application setup using convention over configuration.\n\n### Key Concepts\n- `@EnableAutoConfiguration` scans classpaths and registers matching beans.\n- Starter dependencies aggregate transitively needed libraries.\n- Properties are bound via `@ConfigurationProperties`.\n\n### Best Practices\nAlways verify autoconfigured beans using `/actuator/beans` or startup logs.",
                7, 1);
        Topic c1t2 = createTopic(c1m1, "Dependency Injection & IoC Container",
                "## Inversion of Control & Beans\n\nThe Spring IoC container manages component lifecycles and injects dependencies.\n\n### Mechanisms\n- Constructor injection is strongly recommended for immutability.\n- Component scanning identifies `@Component`, `@Service`, and `@Repository`.\n- Bean scopes include Singleton (default) and Prototype.",
                6, 2);

        CourseModule c1m2 = createModule(savedC1, "Module 2: Spring Data JPA & Persistence", 2);
        Topic c1t3 = createTopic(c1m2, "Spring Data JPA & Entity Mappings",
                "## Object-Relational Mapping with JPA\n\nHibernate serves as the default JPA provider in Spring Boot.\n\n### Core Annotations\n- `@Entity` and `@Table` declare relational tables.\n- `@Id` and `@GeneratedValue` specify primary keys.\n- `@OneToMany`, `@ManyToOne`, and `@JoinColumn` model associations.\n\n### Query Generation\nRepository interfaces derive SQL queries automatically from method naming conventions.",
                8, 1);

        // Add Questions to Topic 1
        createQuestion(c1t1, "Which annotation activates Spring Boot's autoconfiguration mechanism?",
                "The `@EnableAutoConfiguration` (or `@SpringBootApplication` which includes it) tells Spring to guess beans based on classpath dependencies.",
                Difficulty.EASY, 10,
                List.of(
                        new OptionData("@EnableAutoConfiguration", true),
                        new OptionData("@EnableWebSecurity", false),
                        new OptionData("@ComponentScan", false),
                        new OptionData("@Configurable", false)
                ));

        createQuestion(c1t1, "Why is constructor injection preferred over field injection with @Autowired?",
                "Constructor injection guarantees required dependencies cannot be null, promotes immutability, and simplifies unit testing with mock instances.",
                Difficulty.MEDIUM, 10,
                List.of(
                        new OptionData("It ensures immutability and eases unit testing without reflection", true),
                        new OptionData("It executes faster at JVM runtime", false),
                        new OptionData("It allows circular dependencies without warnings", false),
                        new OptionData("It requires fewer lines of Java code", false)
                ));

        createQuestion(c1t1, "What is the primary role of a starter dependency in Spring Boot pom.xml?",
                "Starters bundle relevant dependencies and version descriptors into a single cohesive artifact coordinate.",
                Difficulty.EASY, 10,
                List.of(
                        new OptionData("To provide a curated set of dependencies and transitive libraries", true),
                        new OptionData("To compile bytecode directly into machine instructions", false),
                        new OptionData("To automatically host the application on the cloud", false),
                        new OptionData("To encrypt application properties files", false)
                ));

        // Add Questions to Topic 3 (Spring Data JPA)
        createQuestion(c1t3, "Which fetch type is the default for @ManyToOne relationships in JPA?",
                "In JPA, single-valued associations (@ManyToOne, @OneToOne) default to FetchType.EAGER, while collection associations default to FetchType.LAZY.",
                Difficulty.MEDIUM, 10,
                List.of(
                        new OptionData("FetchType.EAGER", true),
                        new OptionData("FetchType.LAZY", false),
                        new OptionData("FetchType.ASYNC", false),
                        new OptionData("FetchType.STREAMING", false)
                ));

        createQuestion(c1t3, "How does Spring Data JPA derive queries from interface method names?",
                "Spring Data inspects query method names parsing keywords like findBy, And, Or, OrderBy and synthesizes JPQL.",
                Difficulty.EASY, 10,
                List.of(
                        new OptionData("It parses method names into JPQL queries automatically", true),
                        new OptionData("It executes raw native shell scripts", false),
                        new OptionData("It calls external HTTP endpoints", false),
                        new OptionData("It requires an explicit SQL string on every method", false)
                ));

        // COURSE 2: React 18
        Course course2 = new Course();
        course2.setTitle("React 18 & Modern Web Engineering");
        course2.setSlug("react-18-modern-web-engineering");
        course2.setDescription("Build resilient, accessible user interfaces using React 18, Hooks, state patterns, Vite, and component composition.");
        course2.setCategory("Frontend Development");
        course2.setDifficultyLevel(DifficultyLevel.BEGINNER);
        course2.setThumbnailUrl("https://images.unsplash.com/photo-1633356122544-f134324a6cee?w=600&auto=format&fit=crop&q=80");
        course2.setPublished(true);
        Course savedC2 = courseRepository.save(course2);

        CourseModule c2m1 = createModule(savedC2, "Module 1: Functional Components & Hooks", 1);
        Topic c2t1 = createTopic(c2m1, "useState, useEffect, and Lifecycle Fundamentals",
                "## React Hooks Essentials\n\nHooks enable functional components to maintain state and synchronize side effects.\n\n### State Hooks\n- `useState` manages localized component state.\n- `useEffect` synchronizes with external systems.\n\n### Cleanup Phase\nReturn a cleanup callback inside `useEffect` to abort fetch requests or remove listeners.",
                5, 1);
        Topic c2t2 = createTopic(c2m1, "Component Composition & Props",
                "## Component Composition\n\nReact favors composition over inheritance. Pass children and props to build modular hierarchies.",
                6, 2);

        createQuestion(c2t1, "When does a useEffect cleanup function execute?",
                "The cleanup function runs before the component unmounts and before re-running the effect due to updated dependencies.",
                Difficulty.MEDIUM, 10,
                List.of(
                        new OptionData("Before unmounting and prior to re-running the effect on dependency change", true),
                        new OptionData("Only once when the browser window closes", false),
                        new OptionData("Immediately before the initial render completes", false),
                        new OptionData("Only when an error is thrown in rendering", false)
                ));

        createQuestion(c2t1, "What rule must be followed when invoking React Hooks?",
                "Hooks must be called at the top level of React functions, never inside loops, conditions, or nested functions.",
                Difficulty.EASY, 10,
                List.of(
                        new OptionData("Only call hooks at the top level of React functions", true),
                        new OptionData("Hooks must always be called inside a try/catch block", false),
                        new OptionData("Hooks must only be used in JavaScript class constructors", false),
                        new OptionData("Hooks can only be called from HTML onclick handlers", false)
                ));

        // COURSE 3: MySQL 8 & Database Design
        Course course3 = new Course();
        course3.setTitle("High-Performance MySQL 8 & Relational Modeling");
        course3.setSlug("high-performance-mysql-8");
        course3.setDescription("Design normalized relational databases, configure B-Tree indexes, optimize query plans, and enforce referential integrity.");
        course3.setCategory("Database Engineering");
        course3.setDifficultyLevel(DifficultyLevel.ADVANCED);
        course3.setThumbnailUrl("https://images.unsplash.com/photo-1544383835-bda2bc66a55d?w=600&auto=format&fit=crop&q=80");
        course3.setPublished(true);
        Course savedC3 = courseRepository.save(course3);

        CourseModule c3m1 = createModule(savedC3, "Module 1: Relational Schema & Indexing", 1);
        Topic c3t1 = createTopic(c3m1, "B-Tree Indexes & Query Execution Plans",
                "## Database Indexing Principles\n\nB-Tree indexes speed up lookups, range scans, and sorting at the cost of write overhead.\n\n### EXPLAIN Output\nAnalyze type (const, ref, range, index, ALL) and key usage to avoid full table scans.",
                8, 1);

        createQuestion(c3t1, "Which EXPLAIN join type indicates the worst performance (full table scan)?",
                "In MySQL EXPLAIN output, type 'ALL' means a full table scan was performed across every row.",
                Difficulty.MEDIUM, 10,
                List.of(
                        new OptionData("ALL", true),
                        new OptionData("ref", false),
                        new OptionData("const", false),
                        new OptionData("range", false)
                ));
    }

    private CourseModule createModule(Course course, String title, int order) {
        CourseModule m = new CourseModule();
        m.setCourse(course);
        m.setTitle(title);
        m.setModuleOrder(order);
        return moduleRepository.save(m);
    }

    private Topic createTopic(CourseModule module, String title, String content, int readTime, int order) {
        Topic t = new Topic();
        t.setModule(module);
        t.setTitle(title);
        t.setContent(content);
        t.setReadingTimeMinutes(readTime);
        t.setTopicOrder(order);
        return topicRepository.save(t);
    }

    private void createQuestion(Topic topic, String text, String explanation, Difficulty diff, int points, List<OptionData> options) {
        Question q = new Question();
        q.setTopic(topic);
        q.setQuestionText(text);
        q.setExplanation(explanation);
        q.setDifficulty(diff);
        q.setPoints(points);
        q.setActive(true);

        List<AnswerOption> optList = new ArrayList<>();
        int order = 1;
        for (OptionData od : options) {
            AnswerOption ao = new AnswerOption();
            ao.setQuestion(q);
            ao.setOptionText(od.text);
            ao.setCorrect(od.correct);
            ao.setOptionOrder(order++);
            optList.add(ao);
        }
        q.setOptions(optList);
        questionRepository.save(q);
    }

    private static class OptionData {
        String text;
        boolean correct;
        OptionData(String text, boolean correct) {
            this.text = text;
            this.correct = correct;
        }
    }
}
