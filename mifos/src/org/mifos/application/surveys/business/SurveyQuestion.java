package org.mifos.application.surveys.business;

import org.mifos.framework.business.PersistentObject;

public class SurveyQuestion extends PersistentObject {
	
	private int sqId;
	
	private Survey survey;
	
	private int surveyId;
	
	private Question question;
	
	private int mandatory;
	
	private int questionOrder;

	public int getMandatory() {
		return mandatory;
	}

	public void setMandatory(int mandatory) {
		this.mandatory = mandatory;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public int getQuestionOrder() {
		return questionOrder;
	}

	public void setQuestionOrder(int questionOrder) {
		this.questionOrder = questionOrder;
	}

	public Survey getSurvey() {
		return survey;
	}

	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public int getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}

	public int getSqId() {
		return sqId;
	}

	public void setSqId(int sqId) {
		this.sqId = sqId;
	}

}
