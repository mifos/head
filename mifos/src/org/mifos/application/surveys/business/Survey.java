package org.mifos.application.surveys.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.DateUtils;

public class Survey extends PersistentObject {
	private int surveyId;
	
	private String surveyName;
	
	private String surveyAppliesTo;
	
	private int state;
	
	private Date dateOfCreation;
	
	private List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();

	public List<SurveyQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<SurveyQuestion> questions) {
		this.questions = questions;
	}

	public Survey() {
		dateOfCreation = DateUtils.getCurrentDateWithoutTimeStamp();
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getSurveyAppliesTo() {
		return surveyAppliesTo;
	}

	public void setSurveyAppliesTo(String surveyAppliesTo) {
		this.surveyAppliesTo = surveyAppliesTo;
	}

	public int getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(int surveyId) {
		this.surveyId = surveyId;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public Date getDateOfCreation() {
		return dateOfCreation;
	}

	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}
}
