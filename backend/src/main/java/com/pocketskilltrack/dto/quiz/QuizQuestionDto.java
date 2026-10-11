package com.pocketskilltrack.dto.quiz;

import com.pocketskilltrack.entity.Difficulty;
import java.util.ArrayList;
import java.util.List;

public class QuizQuestionDto {

    private Long id;
    private String questionText;
    private Difficulty difficulty;
    private int points;
    private List<AnswerOptionDto> options = new ArrayList<>();

    public QuizQuestionDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public List<AnswerOptionDto> getOptions() { return options; }
    public void setOptions(List<AnswerOptionDto> options) { this.options = options; }
}
