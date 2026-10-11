package com.pocketskilltrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "answer_options")
public class AnswerOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private boolean isCorrect = false;

    @Column(name = "option_order", nullable = false)
    private int optionOrder = 1;

    public AnswerOption() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public int getOptionOrder() { return optionOrder; }
    public void setOptionOrder(int optionOrder) { this.optionOrder = optionOrder; }
}
