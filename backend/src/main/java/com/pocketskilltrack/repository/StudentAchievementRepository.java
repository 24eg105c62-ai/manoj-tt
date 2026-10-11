package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.StudentAchievement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAchievementRepository extends JpaRepository<StudentAchievement, Long> {
    Optional<StudentAchievement> findByUserIdAndAchievementId(Long userId, Long achievementId);
    boolean existsByUserIdAndAchievementId(Long userId, Long achievementId);

    List<StudentAchievement> findByUserIdOrderByUnlockedAtDesc(Long userId);

    @Query("SELECT sa.achievement.id FROM StudentAchievement sa WHERE sa.user.id = :userId")
    List<Long> findUnlockedAchievementIdsByUserId(@Param("userId") Long userId);

    long countByUserId(Long userId);

    @Query("SELECT sa FROM StudentAchievement sa ORDER BY sa.unlockedAt DESC")
    Page<StudentAchievement> findRecentFeed(Pageable pageable);
}
