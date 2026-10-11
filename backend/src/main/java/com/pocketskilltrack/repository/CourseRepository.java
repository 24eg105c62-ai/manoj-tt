package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.Course;
import com.pocketskilltrack.entity.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findBySlug(String slug);

    Page<Course> findByPublishedTrue(Pageable pageable);

    @Query("SELECT c FROM Course c WHERE c.published = true AND " +
           "(:category IS NULL OR LOWER(c.category) = LOWER(:category)) AND " +
           "(:difficulty IS NULL OR c.difficultyLevel = :difficulty) AND " +
           "(:query IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Course> searchCourses(
            @Param("query") String query,
            @Param("category") String category,
            @Param("difficulty") DifficultyLevel difficulty,
            Pageable pageable);

    @Query("SELECT DISTINCT c.category FROM Course c WHERE c.published = true")
    List<String> findDistinctCategories();
}
