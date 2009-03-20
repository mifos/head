/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import java.io.Serializable;

public class SurveyQuestion implements Serializable, Comparable<SurveyQuestion> {
	private int surveyQuestionId;
	
	private int mandatory;
	
	private Question question;
	
	private Survey survey;
	
	private Integer order;
	
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
	
	public void setSurvey(Survey survey) {
		this.survey = survey;
	}

	public Survey getSurvey() {
		return survey;
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
		this.order = new Integer(order);
	}

	public Integer getOrder() {
		return order;
	}
	
	public int compareTo(SurveyQuestion o) {
		return getOrder() - o.getOrder();
	}
}
