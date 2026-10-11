package com.pocketskilltrack.dto.prediction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ScorePredictionDto {

    private String categoryOrTopic;
    private double predictedScoreMin;
    private double predictedScoreMax;
    private double predictedScoreMid;
    private String confidenceLevel;
    private int sampleSize;
    private List<String> primaryFactors = new ArrayList<>();
    private String limitationsDisclaimer;
    private List<Double> recentQuizScores = new ArrayList<>();
    private LocalDateTime calculatedAt;

    public ScorePredictionDto() {}

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

    public List<String> getPrimaryFactors() { return primaryFactors; }
    public void setPrimaryFactors(List<String> primaryFactors) { this.primaryFactors = primaryFactors; }

    public String getLimitationsDisclaimer() { return limitationsDisclaimer; }
    public void setLimitationsDisclaimer(String limitationsDisclaimer) { this.limitationsDisclaimer = limitationsDisclaimer; }

    public List<Double> getRecentQuizScores() { return recentQuizScores; }
    public void setRecentQuizScores(List<Double> recentQuizScores) { this.recentQuizScores = recentQuizScores; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
