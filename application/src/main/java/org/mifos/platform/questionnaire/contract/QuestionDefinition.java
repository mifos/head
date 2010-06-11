package org.mifos.platform.questionnaire.contract;

public class QuestionDefinition {
    private String title;

    private QuestionType type;

    public QuestionDefinition(String title, QuestionType type) {
        this.title = title;
        this.type = type;
    }

    public QuestionType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }
}
