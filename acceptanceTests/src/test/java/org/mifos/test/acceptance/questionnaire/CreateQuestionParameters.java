package org.mifos.test.acceptance.questionnaire;

import java.util.List;

public class CreateQuestionParameters {
    private String title;

    private String type;
    private List<String> choices;
    private Integer numericMin;
    private Integer numericMax;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public void setNumericMin(Integer numericMin) {
        this.numericMin = numericMin;
    }

    public void setNumericMax(Integer numericMax) {
        this.numericMax = numericMax;
    }

    public Integer getNumericMin() {
        return numericMin;
    }

    public Integer getNumericMax() {
        return numericMax;
    }

    boolean isNumericQuestionType() {
        return "Number".equals(getType());
    }

    boolean questionHasAnswerChoices() {
        return "Multi Select".equals(getType()) || "Single Select".equals(getType());
    }
}
