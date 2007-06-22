package org.mifos.application.surveys.business;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.framework.util.helpers.DateUtils;

public class Survey implements Serializable {
	
	private int surveyId;
	
	private String name;
	
	private SurveyType appliesTo;
	
	private Date dateOfCreation;
	
	private SurveyState state;
	
	private List<SurveyQuestion> questions;
	
	public Survey() {
		dateOfCreation = DateUtils.getCurrentDateWithoutTimeStamp();
		state = SurveyState.INACTIVE;
		questions = new LinkedList<SurveyQuestion>();
	}
	
	public Survey(String name, SurveyState state, SurveyType appliesTo) {
		this();
		this.name = name;
		this.state = state;
		this.appliesTo = appliesTo;
	}

	// for hibernate and jsp
	public String getAppliesTo() {
		return appliesTo.getValue();
	}
	
	public SurveyType getAppliesToAsEnum() {
		return appliesTo;
	}

	public void setAppliesTo(String appliesTo) {
		this.appliesTo = SurveyType.fromString(appliesTo);
	}
	
	public void setAppliesTo(SurveyType appliesTo) {
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
	
	// Called from jsp as well as hibernate
	public int getState() {
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
	
	public Question getQuestionById(int id) {
		for (SurveyQuestion surveyQuestion : this.getQuestions()) {
			Question question = surveyQuestion.getQuestion();
			if (question.getQuestionId() == id)
				return question;
		}
		throw new IllegalArgumentException("Survey does not contain a question with id: " + id);
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
	
	
	@Override
	public String toString() {
		return "<Survey " + getName() + " " + appliesTo.getValue() + ">" ;
	}

}
