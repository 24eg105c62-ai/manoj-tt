package com.pocketskilltrack.dto.admin;

import com.pocketskilltrack.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class QuestionCreateRequest {

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotBlank(message = "Question text is required")
    private String questionText;

    private String explanation;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty = Difficulty.MEDIUM;

    private int points = 10;

    @NotNull(message = "Options are required")
    @Size(min = 2, message = "At least 2 options are required")
    private List<OptionCreateRequest> options = new ArrayList<>();

    public QuestionCreateRequest() {}

    public Long getTopicId() { return topicId; }
    public void setTopicId(Long topicId) { this.topicId = topicId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public List<OptionCreateRequest> getOptions() { return options; }
    public void setOptions(List<OptionCreateRequest> options) { this.options = options; }

    public static class OptionCreateRequest {
        @NotBlank(message = "Option text cannot be blank")
        private String optionText;

        private boolean correct = false;
        private int optionOrder = 1;

        public OptionCreateRequest() {}

        public OptionCreateRequest(String optionText, boolean correct, int optionOrder) {
            this.optionText = optionText;
            this.correct = correct;
            this.optionOrder = optionOrder;
        }

        public String getOptionText() { return optionText; }
        public void setOptionText(String optionText) { this.optionText = optionText; }
        public boolean isCorrect() { return correct; }
        public void setCorrect(boolean correct) { this.correct = correct; }
        public int getOptionOrder() { return optionOrder; }
        public void setOptionOrder(int optionOrder) { this.optionOrder = optionOrder; }
    }
}
