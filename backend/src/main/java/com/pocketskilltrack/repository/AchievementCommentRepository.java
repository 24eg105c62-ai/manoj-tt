package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.AchievementComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementCommentRepository extends JpaRepository<AchievementComment, Long> {
    List<AchievementComment> findByStudentAchievementIdAndStatusOrderByCreatedAtDesc(Long studentAchievementId, String status);
    Page<AchievementComment> findByStudentAchievementIdAndStatusOrderByCreatedAtDesc(Long studentAchievementId, String status, Pageable pageable);
    long countByStudentAchievementIdAndStatus(Long studentAchievementId, String status);
    Page<AchievementComment> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);
}
