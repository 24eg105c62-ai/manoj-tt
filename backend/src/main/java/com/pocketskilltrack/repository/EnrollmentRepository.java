package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    List<Enrollment> findByUserId(Long userId);
    Page<Enrollment> findByUserId(Long userId, Pageable pageable);

    long countByUserId(Long userId);
    long countByUserIdAndProgressPercent(Long userId, int progressPercent);

    @Query("SELECT AVG(e.progressPercent) FROM Enrollment e WHERE e.user.id = :userId")
    Double getAverageProgressByUserId(@Param("userId") Long userId);
}
