package com.pocketskilltrack.repository;

import com.pocketskilltrack.entity.ScorePrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScorePredictionRepository extends JpaRepository<ScorePrediction, Long> {
    List<ScorePrediction> findByUserIdOrderByCalculatedAtDesc(Long userId);
    Optional<ScorePrediction> findTopByUserIdOrderByCalculatedAtDesc(Long userId);
}
