/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.surveys.business;

/**
 * The superclass of surveys in MifOS. There are two types of surveys managed by MifOS:
 * <ul>
 * <li> Custom surveys that a client can define.
 * <li> PPI surveys that are associated with a PPI Scorecard for a country
 * </ul>
 * This class serves a dual purpose:
 * <ul>
 * <li> It defines the state and behavior common to both types of surveys.
 * <li> Instances of the class represent custom surveys, since there is no subclass
 * for custom surveys
 * </ul>
 * PPI surveys are represented as instances of the subclass {@link PpiSurvey}.
 * <p>
 * TODO: this needs to be fixed. There should be a subclass for custom surveys, to avoid
 * awkward coding such as appliesTo.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.exceptions.PersistenceException;
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
		questions = new ArrayList<SurveyQuestion>();
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
	
	/**
	 * implements a dispatch pattern so that a client will get the correct
	 * class of survey without having to invoke instanceOf(). Since this class
	 * represents custom surveys, the client gets a (custom) SurveyInstance instance.
	 * <p>
	 * @see {@link PpiSurvey.createInstance()}
	 */
	public SurveyInstance createInstance() {
		return new SurveyInstance();
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
	
	public SurveyQuestion getSurveyQuestionById(int id) {
		for (SurveyQuestion surveyQuestion : this.getQuestions()) {
			if (surveyQuestion.getSurveyQuestionId() == id)
				return surveyQuestion;
		}
		throw new IllegalArgumentException("Survey does not contain a question with id: " + id);
	}
	
	public Question getQuestionById(int id) {
		for (SurveyQuestion surveyQuestion : this.getQuestions()) {
			Question question = surveyQuestion.getQuestion();
			if (question.getQuestionId() == id)
				return question;
		}
		return null;
	}
	
	public String getQuestionText(int i) {
		return getQuestion(i).getQuestionText();
	}
	
	public SurveyQuestion addQuestion(Question question, boolean mandatory) {
		SurveyQuestion surveyQuestion = new SurveyQuestion();
		surveyQuestion.setMandatory(mandatory ? 1 : 0);
		surveyQuestion.setQuestion(question);
		surveyQuestion.setOrder(getQuestions().size());
		surveyQuestion.setSurvey(this);
		if (surveyQuestion.getOrder() == null)
			surveyQuestion.setOrder(getQuestions().size());
		getQuestions().add(surveyQuestion);
		
		return surveyQuestion;
	}
	
	
	@Override
	public String toString() {
		return "<Survey " + getName() + " " + appliesTo.getValue() + ">" ;
	}

}
