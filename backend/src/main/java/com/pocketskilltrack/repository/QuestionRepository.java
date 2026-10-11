package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.Difficulty;
import com.pocketskilltrack.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTopicIdAndActiveTrue(Long topicId);

    @Query("SELECT q FROM Question q WHERE q.topic.id = :topicId AND q.active = true AND (:diff IS NULL OR q.difficulty = :diff)")
    List<Question> findByTopicIdAndDifficulty(@Param("topicId") Long topicId, @Param("diff") Difficulty diff);

    long countByTopicId(Long topicId);
    Page<Question> findByTopicId(Long topicId, Pageable pageable);
}
