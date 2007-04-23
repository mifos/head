package org.mifos.application.surveys.business;

public class SurveyQuestion {
	private int surveyQuestionId;
	
	private int mandatory;
	
	private Question question;

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public int getMandatory() {
		return mandatory;
	}

	public void setMandatory(int mandatory) {
		this.mandatory = mandatory;
	}

	public int getSurveyQuestionId() {
		return surveyQuestionId;
	}

	public void setSurveyQuestionId(int surveyQuestionId) {
		this.surveyQuestionId = surveyQuestionId;
	}
}
