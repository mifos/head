package org.mifos.application.surveys.business;

public class QuestionChoice {
	private int choiceId;
	
	private String choiceText;
	
	// defining the null constructor avoids some harmless hibernate error 
	// messages during testing
	public QuestionChoice() {}
	
	public QuestionChoice(String text) {
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
