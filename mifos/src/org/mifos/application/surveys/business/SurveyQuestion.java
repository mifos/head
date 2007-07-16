package org.mifos.application.surveys.business;

import java.io.Serializable;

public class SurveyQuestion implements Serializable {
	private int surveyQuestionId;
	
	private int mandatory;
	
	private Question question;
	
	private int order;
	
	public SurveyQuestion() {
	}

	public SurveyQuestion(int id, boolean mandatory) {
		surveyQuestionId = id;
		setMandatory(mandatory);
	}

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
	
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory ? 1 : 0;
	}

	public int getSurveyQuestionId() {
		return surveyQuestionId;
	}

	public void setSurveyQuestionId(int surveyQuestionId) {
		this.surveyQuestionId = surveyQuestionId;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}
}
