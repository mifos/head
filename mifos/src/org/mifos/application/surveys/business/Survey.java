package org.mifos.application.surveys.business;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.framework.util.helpers.DateUtils;

public class Survey {
	
	private int surveyId;
	
	private String name;
	
	private String appliesTo;
	
	private Date dateOfCreation;
	
	private SurveyState state;
	
	private List<SurveyQuestion> questions;
	
	public Survey() {
		dateOfCreation = DateUtils.getCurrentDateWithoutTimeStamp();
		state = SurveyState.INACTIVE;
		questions = new LinkedList<SurveyQuestion>();
	}
	
	public Survey(String name, SurveyState state, String appliesTo) {
		this();
		this.name = name;
		this.state = state;
		this.appliesTo = appliesTo;
	}

	public String getAppliesTo() {
		return appliesTo;
	}

	public void setAppliesTo(String appliesTo) {
		this.appliesTo = appliesTo;
	}

	public Date getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Date dateofCreation) {
		this.dateOfCreation = dateofCreation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SurveyState getStateAsEnum() {
		return state;
	}
	
	int getState() {
		return state.getValue();
	}

	public void setState(SurveyState state) {
		this.state = state;
	}
	
	public void setState(int state) {
		this.state = SurveyState.fromInt(state);
	}

	public int getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}

	public List<SurveyQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<SurveyQuestion> questions) {
		this.questions = questions;
	}
	
	public Question getQuestion(int i) {
		return getQuestions().get(i).getQuestion();
	}
	
	public String getQuestionText(int i) {
		return getQuestion(i).getQuestionText();
	}
	
	public SurveyQuestion addQuestion(Question question, boolean mandatory) {
		SurveyQuestion surveyQuestion = new SurveyQuestion();
		surveyQuestion.setMandatory(mandatory ? 1 : 0);
		surveyQuestion.setQuestion(question);
		getQuestions().add(surveyQuestion);
		return surveyQuestion;
	}

}
