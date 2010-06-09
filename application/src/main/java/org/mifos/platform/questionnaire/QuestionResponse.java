package org.mifos.platform.questionnaire;

public class QuestionResponse {
    private Integer questionId;

    public QuestionResponse(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getQuestionId() {
        return questionId;
    }
}
