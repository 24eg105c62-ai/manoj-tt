package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.QuizAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserIdOrderByCompletedTimeDesc(Long userId);
    Page<QuizAttempt> findByUserIdOrderByCompletedTimeDesc(Long userId, Pageable pageable);

    List<QuizAttempt> findByUserIdAndTopicIdOrderByCompletedTimeDesc(Long userId, Long topicId);
    long countByUserIdAndTopicId(Long userId, Long topicId);

    long countByUserId(Long userId);
    long countByUserIdAndPassedTrue(Long userId);

    @Query("SELECT AVG(qa.percentage) FROM QuizAttempt qa WHERE qa.user.id = :userId")
    Double getAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.user.id = :userId ORDER BY qa.completedTime ASC")
    List<QuizAttempt> findAllByUserIdChronological(@Param("userId") Long userId);

    @Query("SELECT COUNT(qa) FROM QuizAttempt qa")
    long countTotalAttempts();

    @Query("SELECT AVG(qa.percentage) FROM QuizAttempt qa")
    Double getGlobalAverageScore();
}
