package org.mifos.platform.questionnaire.domain;

public class QuestionChoiceEntity {
    private int choiceId;

    private String choiceText;

    // TODO: Can be protected? copy-paste from org.mifos.customers.surveys.business.QuestionChoice
    // defining the null constructor avoids some harmless hibernate error
    // messages during testing
    @SuppressWarnings("PMD.UncommentedEmptyConstructor")
    public QuestionChoiceEntity() {
    }

    public QuestionChoiceEntity(String text) {
        choiceText = text;
    }

    public int getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(int choiceId) {
        this.choiceId = choiceId;
    }

    public String getChoiceText() {
        return choiceText;
    }

    public void setChoiceText(String choiceText) {
        this.choiceText = choiceText;
    }
}
