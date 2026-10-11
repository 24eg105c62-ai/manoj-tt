package com.pocketskilltrack;

import com.pocketskilltrack.dto.achievement.CommentDto;
import com.pocketskilltrack.dto.achievement.StudentAchievementDto;
import com.pocketskilltrack.entity.*;
import com.pocketskilltrack.exception.UnauthorizedException;
import com.pocketskilltrack.repository.*;
import com.pocketskilltrack.service.AchievementService;
import com.pocketskilltrack.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AchievementServiceTest {

    @Mock
    private AchievementRepository achievementRepository;
    @Mock
    private StudentAchievementRepository studentAchievementRepository;
    @Mock
    private AchievementReactionRepository reactionRepository;
    @Mock
    private AchievementCommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private AuditLogService auditLogService;

    private AchievementService achievementService;

    @BeforeEach
    void setUp() {
        achievementService = new AchievementService(
                achievementRepository, studentAchievementRepository,
                reactionRepository, commentRepository, userRepository,
                notificationRepository, auditLogService
        );
    }

    @Test
    void testReactToAchievement_OneReactionPerUserEnforced() {
        Achievement ach = new Achievement();
        ach.setId(1L);
        ach.setBadgeCode("TEST_BADGE");
        ach.setTitle("Test Badge");

        User author = new User();
        author.setId(10L);
        author.setFullName("Author User");

        StudentAchievement sa = new StudentAchievement(author, ach);
        sa.setId(100L);

        User reactingUser = new User();
        reactingUser.setId(20L);

        when(studentAchievementRepository.findById(100L)).thenReturn(Optional.of(sa));
        when(userRepository.findById(20L)).thenReturn(Optional.of(reactingUser));
        when(reactionRepository.findByStudentAchievementIdAndUserId(100L, 20L)).thenReturn(Optional.empty());

        StudentAchievementDto dto = achievementService.reactToAchievement(100L, 20L, ReactionType.LIKE);

        assertNotNull(dto);
        verify(reactionRepository, times(1)).save(any(AchievementReaction.class));
    }

    @Test
    void testDeleteComment_OwnershipCheck_ThrowsUnauthorizedIfNotOwnerOrAdmin() {
        User commenter = new User();
        commenter.setId(30L);

        AchievementComment comment = new AchievementComment();
        comment.setId(500L);
        comment.setUser(commenter);

        when(commentRepository.findById(500L)).thenReturn(Optional.of(comment));

        // Different user (userId 40L) attempting to delete without admin rights
        assertThrows(UnauthorizedException.class, () ->
                achievementService.deleteComment(500L, 40L, false)
        );

        // Same user (owner) can delete
        achievementService.deleteComment(500L, 30L, false);
        assertEquals("REMOVED", comment.getStatus());

        // Admin can delete any comment
        achievementService.deleteComment(500L, 999L, true);
        verify(commentRepository, times(2)).save(comment);
    }
}
