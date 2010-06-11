package org.mifos.platform.questionnaire.contract;

public class QuestionDetail {
    private Integer questionId;

    private String questionText;

    private String shortName;

    public QuestionDetail(Integer questionId, String questionText, String shortName) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.shortName = shortName;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getShortName() {
        return shortName;
    }


}
