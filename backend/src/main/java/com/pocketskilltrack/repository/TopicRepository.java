package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    List<Topic> findByModuleIdOrderByTopicOrderAsc(Long moduleId);

    @Query("SELECT t FROM Topic t WHERE t.module.course.id = :courseId ORDER BY t.module.moduleOrder ASC, t.topicOrder ASC")
    List<Topic> findAllByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(t) FROM Topic t WHERE t.module.course.id = :courseId")
    long countByCourseId(@Param("courseId") Long courseId);
}
