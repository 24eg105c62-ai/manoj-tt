package com.pocketskilltrack.dto.quiz;

public class AnswerOptionDto {

    private Long id;
    private String optionText;
    private int optionOrder;

    public AnswerOptionDto() {}

    public AnswerOptionDto(Long id, String optionText, int optionOrder) {
        this.id = id;
        this.optionText = optionText;
        this.optionOrder = optionOrder;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOptionText() { return optionText; }
    public void setOptionText(String optionText) { this.optionText = optionText; }

    public int getOptionOrder() { return optionOrder; }
    public void setOptionOrder(int optionOrder) { this.optionOrder = optionOrder; }
}
