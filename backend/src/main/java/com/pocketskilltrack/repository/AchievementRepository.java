package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.Achievement;
import com.pocketskilltrack.entity.CriteriaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Optional<Achievement> findByBadgeCode(String badgeCode);
    List<Achievement> findByCriteriaType(CriteriaType criteriaType);
}
