package com.pocketskilltrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "quiz_responses")
public class QuizResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    @JsonIgnore
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private AnswerOption selectedOption;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    @Column(name = "points_awarded", nullable = false)
    private int pointsAwarded = 0;

    public QuizResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public QuizAttempt getAttempt() { return attempt; }
    public void setAttempt(QuizAttempt attempt) { this.attempt = attempt; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public AnswerOption getSelectedOption() { return selectedOption; }
    public void setSelectedOption(AnswerOption selectedOption) { this.selectedOption = selectedOption; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public int getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(int pointsAwarded) { this.pointsAwarded = pointsAwarded; }
}
