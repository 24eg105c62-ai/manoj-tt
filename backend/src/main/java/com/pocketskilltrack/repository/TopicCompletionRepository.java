package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.TopicCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicCompletionRepository extends JpaRepository<TopicCompletion, Long> {
    Optional<TopicCompletion> findByUserIdAndTopicId(Long userId, Long topicId);
    boolean existsByUserIdAndTopicId(Long userId, Long topicId);
    long countByUserId(Long userId);

    @Query("SELECT tc.topic.id FROM TopicCompletion tc WHERE tc.user.id = :userId")
    List<Long> findCompletedTopicIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(tc) FROM TopicCompletion tc WHERE tc.user.id = :userId AND tc.topic.module.course.id = :courseId")
    long countCompletedTopicsByCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
