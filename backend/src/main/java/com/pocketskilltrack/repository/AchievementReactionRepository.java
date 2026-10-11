package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.AchievementReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementReactionRepository extends JpaRepository<AchievementReaction, Long> {
    Optional<AchievementReaction> findByStudentAchievementIdAndUserId(Long studentAchievementId, Long userId);
    boolean existsByStudentAchievementIdAndUserId(Long studentAchievementId, Long userId);

    long countByStudentAchievementId(Long studentAchievementId);

    @Query("SELECT r FROM AchievementReaction r WHERE r.studentAchievement.id = :saId")
    List<AchievementReaction> findByStudentAchievementId(@Param("saId") Long saId);
}
