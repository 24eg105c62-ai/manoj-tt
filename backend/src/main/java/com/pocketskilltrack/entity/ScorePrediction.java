package com.pocketskilltrack.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "score_predictions", indexes = {
    @Index(name = "idx_prediction_user", columnList = "user_id")
})
public class ScorePrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "category_or_topic", nullable = false, length = 100)
    private String categoryOrTopic;

    @Column(name = "predicted_score_min", nullable = false)
    private double predictedScoreMin;

    @Column(name = "predicted_score_max", nullable = false)
    private double predictedScoreMax;

    @Column(name = "predicted_score_mid", nullable = false)
    private double predictedScoreMid;

    @Column(name = "confidence_level", nullable = false, length = 30)
    private String confidenceLevel;

    @Column(name = "sample_size", nullable = false)
    private int sampleSize;

    @Column(name = "primary_factors_json", columnDefinition = "TEXT")
    private String primaryFactorsJson;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt = LocalDateTime.now();

    public ScorePrediction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getCategoryOrTopic() { return categoryOrTopic; }
    public void setCategoryOrTopic(String categoryOrTopic) { this.categoryOrTopic = categoryOrTopic; }

    public double getPredictedScoreMin() { return predictedScoreMin; }
    public void setPredictedScoreMin(double predictedScoreMin) { this.predictedScoreMin = predictedScoreMin; }

    public double getPredictedScoreMax() { return predictedScoreMax; }
    public void setPredictedScoreMax(double predictedScoreMax) { this.predictedScoreMax = predictedScoreMax; }

    public double getPredictedScoreMid() { return predictedScoreMid; }
    public void setPredictedScoreMid(double predictedScoreMid) { this.predictedScoreMid = predictedScoreMid; }

    public String getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; }

    public int getSampleSize() { return sampleSize; }
    public void setSampleSize(int sampleSize) { this.sampleSize = sampleSize; }

    public String getPrimaryFactorsJson() { return primaryFactorsJson; }
    public void setPrimaryFactorsJson(String primaryFactorsJson) { this.primaryFactorsJson = primaryFactorsJson; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
