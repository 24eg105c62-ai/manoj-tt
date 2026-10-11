package com.pocketskilltrack.service;

import com.pocketskilltrack.dto.achievement.*;
import com.pocketskilltrack.entity.*;
import com.pocketskilltrack.exception.BadRequestException;
import com.pocketskilltrack.exception.ResourceNotFoundException;
import com.pocketskilltrack.exception.UnauthorizedException;
import com.pocketskilltrack.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final StudentAchievementRepository studentAchievementRepository;
    private final AchievementReactionRepository reactionRepository;
    private final AchievementCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogService auditLogService;

    public AchievementService(
            AchievementRepository achievementRepository,
            StudentAchievementRepository studentAchievementRepository,
            AchievementReactionRepository reactionRepository,
            AchievementCommentRepository commentRepository,
            UserRepository userRepository,
            NotificationRepository notificationRepository,
            AuditLogService auditLogService) {
        this.achievementRepository = achievementRepository;
        this.studentAchievementRepository = studentAchievementRepository;
        this.reactionRepository = reactionRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void evaluateAndAward(User user, CriteriaType criteriaType, int currentValue) {
        List<Achievement> matching = achievementRepository.findByCriteriaType(criteriaType);

        for (Achievement ach : matching) {
            if (currentValue >= ach.getThresholdValue()) {
                if (!studentAchievementRepository.existsByUserIdAndAchievementId(user.getId(), ach.getId())) {
                    StudentAchievement sa = new StudentAchievement(user, ach);
                    studentAchievementRepository.save(sa);

                    notificationRepository.save(new Notification(
                            user,
                            "Badge Unlocked: " + ach.getTitle(),
                            "Congratulations! You unlocked the '" + ach.getTitle() + "' badge: " + ach.getDescription(),
                            "ACHIEVEMENT"
                    ));

                    auditLogService.log(user.getId(), user.getEmail(), "UNLOCK_ACHIEVEMENT", "ACHIEVEMENT", ach.getId().toString(), null, "Unlocked badge " + ach.getBadgeCode());
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<AchievementDto> getAllAchievementsForUser(Long userId) {
        List<Achievement> all = achievementRepository.findAll();
        List<StudentAchievement> earned = studentAchievementRepository.findByUserIdOrderByUnlockedAtDesc(userId);
        Map<Long, StudentAchievement> earnedMap = earned.stream()
                .collect(Collectors.toMap(sa -> sa.getAchievement().getId(), sa -> sa));

        return all.stream().map(a -> {
            AchievementDto dto = new AchievementDto();
            dto.setId(a.getId());
            dto.setBadgeCode(a.getBadgeCode());
            dto.setTitle(a.getTitle());
            dto.setDescription(a.getDescription());
            dto.setIconName(a.getIconName());
            dto.setCriteriaType(a.getCriteriaType());
            dto.setThresholdValue(a.getThresholdValue());
            dto.setCategory(a.getCategory());

            if (earnedMap.containsKey(a.getId())) {
                dto.setUnlocked(true);
                dto.setUnlockedAt(earnedMap.get(a.getId()).getUnlockedAt());
            } else {
                dto.setUnlocked(false);
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<StudentAchievementDto> getSocialFeed(Long currentUserId, Pageable pageable) {
        Page<StudentAchievement> page = studentAchievementRepository.findRecentFeed(pageable);
        return page.map(sa -> mapToStudentAchievementDto(sa, currentUserId));
    }

    @Transactional
    public StudentAchievementDto reactToAchievement(Long studentAchievementId, Long userId, ReactionType reactionType) {
        StudentAchievement sa = studentAchievementRepository.findById(studentAchievementId)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<AchievementReaction> existingOpt = reactionRepository.findByStudentAchievementIdAndUserId(studentAchievementId, userId);

        if (existingOpt.isPresent()) {
            AchievementReaction existing = existingOpt.get();
            if (existing.getReactionType() == reactionType) {
                // Clicking same reaction toggles it off
                reactionRepository.delete(existing);
            } else {
                // Switching reaction type
                existing.setReactionType(reactionType);
                reactionRepository.save(existing);
            }
        } else {
            // New reaction (enforced 1 reaction per user per achievement)
            AchievementReaction reaction = new AchievementReaction(sa, user, reactionType);
            reactionRepository.save(reaction);
        }

        return mapToStudentAchievementDto(sa, userId);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long studentAchievementId, Long currentUserId) {
        if (!studentAchievementRepository.existsById(studentAchievementId)) {
            throw new ResourceNotFoundException("Achievement post not found");
        }

        List<AchievementComment> comments = commentRepository.findByStudentAchievementIdAndStatusOrderByCreatedAtDesc(studentAchievementId, "ACTIVE");
        return comments.stream().map(c -> {
            CommentDto dto = new CommentDto();
            dto.setId(c.getId());
            dto.setStudentAchievementId(c.getStudentAchievement().getId());
            dto.setUserId(c.getUser().getId());
            dto.setUserName(c.getUser().getFullName());
            dto.setCommentText(c.getCommentText());
            dto.setCreatedAt(c.getCreatedAt());
            dto.setOwnComment(c.getUser().getId().equals(currentUserId));
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addComment(Long studentAchievementId, Long userId, String commentText) {
        StudentAchievement sa = studentAchievementRepository.findById(studentAchievementId)
                .orElseThrow(() -> new ResourceNotFoundException("Achievement post not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AchievementComment comment = new AchievementComment(sa, user, commentText.trim());
        AchievementComment saved = commentRepository.save(comment);

        CommentDto dto = new CommentDto();
        dto.setId(saved.getId());
        dto.setStudentAchievementId(sa.getId());
        dto.setUserId(user.getId());
        dto.setUserName(user.getFullName());
        dto.setCommentText(saved.getCommentText());
        dto.setCreatedAt(saved.getCreatedAt());
        dto.setOwnComment(true);

        return dto;
    }

    @Transactional
    public void deleteComment(Long commentId, Long currentUserId, boolean isAdmin) {
        AchievementComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Moderation & ownership check: must be owner or admin
        if (!comment.getUser().getId().equals(currentUserId) && !isAdmin) {
            throw new UnauthorizedException("You are not authorized to delete this comment");
        }

        comment.setStatus("REMOVED");
        commentRepository.save(comment);
    }

    public StudentAchievementDto mapToStudentAchievementDto(StudentAchievement sa, Long currentUserId) {
        StudentAchievementDto dto = new StudentAchievementDto();
        dto.setId(sa.getId());
        dto.setAchievementId(sa.getAchievement().getId());
        dto.setBadgeCode(sa.getAchievement().getBadgeCode());
        dto.setTitle(sa.getAchievement().getTitle());
        dto.setDescription(sa.getAchievement().getDescription());
        dto.setIconName(sa.getAchievement().getIconName());
        dto.setUnlockedAt(sa.getUnlockedAt());
        dto.setUserId(sa.getUser().getId());
        dto.setStudentName(sa.getUser().getFullName());
        dto.setStudentId(sa.getUser().getStudentId());

        List<AchievementReaction> reactions = reactionRepository.findByStudentAchievementId(sa.getId());
        dto.setTotalReactions(reactions.size());

        Map<String, Long> counts = new HashMap<>();
        for (ReactionType rt : ReactionType.values()) {
            counts.put(rt.name(), 0L);
        }
        ReactionType userReact = null;
        for (AchievementReaction r : reactions) {
            counts.put(r.getReactionType().name(), counts.get(r.getReactionType().name()) + 1);
            if (currentUserId != null && r.getUser().getId().equals(currentUserId)) {
                userReact = r.getReactionType();
            }
        }
        dto.setReactionCounts(counts);
        dto.setUserReaction(userReact);
        dto.setCommentCount(commentRepository.countByStudentAchievementIdAndStatus(sa.getId(), "ACTIVE"));

        return dto;
    }
}
