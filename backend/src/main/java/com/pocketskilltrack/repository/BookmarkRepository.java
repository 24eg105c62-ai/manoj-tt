package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserIdAndCourseId(Long userId, Long courseId);
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);
    void deleteByUserIdAndCourseId(Long userId, Long courseId);

    @Query("SELECT b.course.id FROM Bookmark b WHERE b.user.id = :userId")
    List<Long> findBookmarkedCourseIdsByUserId(@Param("userId") Long userId);
}
