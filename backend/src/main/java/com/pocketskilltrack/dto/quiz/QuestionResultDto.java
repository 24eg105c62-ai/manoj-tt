package com.pocketskilltrack.dto.quiz;

import java.util.List;

public class QuestionResultDto {

    private Long questionId;
    private String questionText;
    private Long selectedOptionId;
    private Long correctOptionId;
    private boolean correct;
    private int pointsAwarded;
    private int totalPoints;
    private String explanation;
    private List<AnswerOptionResultDto> options;

    public QuestionResultDto() {}

    public Long getQuestionId() { return questionId; }
    public void setQuestionId(Long questionId) { this.questionId = questionId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public Long getSelectedOptionId() { return selectedOptionId; }
    public void setSelectedOptionId(Long selectedOptionId) { this.selectedOptionId = selectedOptionId; }

    public Long getCorrectOptionId() { return correctOptionId; }
    public void setCorrectOptionId(Long correctOptionId) { this.correctOptionId = correctOptionId; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public int getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(int pointsAwarded) { this.pointsAwarded = pointsAwarded; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<AnswerOptionResultDto> getOptions() { return options; }
    public void setOptions(List<AnswerOptionResultDto> options) { this.options = options; }

    public static class AnswerOptionResultDto {
        private Long id;
        private String optionText;
        private boolean correct;
        private boolean selected;

        public AnswerOptionResultDto() {}

        public AnswerOptionResultDto(Long id, String optionText, boolean correct, boolean selected) {
            this.id = id;
            this.optionText = optionText;
            this.correct = correct;
            this.selected = selected;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getOptionText() { return optionText; }
        public void setOptionText(String optionText) { this.optionText = optionText; }
        public boolean isCorrect() { return correct; }
        public void setCorrect(boolean correct) { this.correct = correct; }
        public boolean isSelected() { return selected; }
        public void setSelected(boolean selected) { this.selected = selected; }
    }
}
