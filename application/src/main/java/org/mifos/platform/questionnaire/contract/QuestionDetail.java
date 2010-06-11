package org.mifos.platform.questionnaire.contract;

public class QuestionDetail {
    private Integer id;

    private String text;

    private String shortName;

    private QuestionType type;

    public QuestionDetail(Integer id, String text, String shortName, QuestionType type) {
        this.id = id;
        this.text = text;
        this.shortName = shortName;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getShortName() {
        return shortName;
    }


    public QuestionType getType() {
        return type;
    }
}
