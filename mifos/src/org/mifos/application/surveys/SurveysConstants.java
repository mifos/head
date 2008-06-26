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
package org.mifos.application.surveys;

public class SurveysConstants {
	
	public static final String KEY_CUSTOMERS_SURVEYS_LIST="customerSurveysList";
	public static final String KEY_ACCOUNTS_SURVEYS_LIST="accountsSurveysList";
	
	public static final String KEY_CLIENT_SURVEYS_LIST="clientSurveysList";
	public static final String KEY_CENTER_SURVEYS_LIST="centerSurveysList";
	public static final String KEY_GROUP_SURVEYS_LIST="groupSurveysList";
	public static final String KEY_LOAN_SURVEYS_LIST="loanSurveysList";
	public static final String KEY_SAVINGS_SURVEYS_LIST="savingsSurveysList";
	
	public static final String KEY_QUESTION="question";
	public static final String KEY_SURVEY="retrievedSurvey";
	public static final String KEY_INSTANCE="retrievedInstance";
	public static final String KEY_NEW_QUESTION_CHOICES="newQuestionChoices";
	public static final String KEY_ANSWER_TYPES = "answerTypes";
	public static final String KEY_NEW_QUESTIONS = "newQuestions";
	public static final String KEY_QUESTIONS_LIST = "questionsList";
	public static final String KEY_ADDED_QUESTIONS = "addedQuestions";
	public static final String KEY_SURVEY_ACTIONFORM = "surveyActionForm";
	
	// generic var that can be used to hold list lengths
	public static final String KEY_ITEM_OFFSET = "itemOffset";
	public static final String KEY_ITEM_COUNT = "itemCount"; 
	public static final String KEY_SURVEY_TYPES = "surveyTypes";
	public static final String KEY_SURVEYS_LIST = "surveysList";
	public static final String KEY_MANDATORY_MAP = "mandatoryMap";
	public static final String KEY_BUSINESS_OBJECT_NAME = "businessObjectName";
	public static final String KEY_VALIDATED_VALUES = "validatedValues";
	public static final String KEY_NEW_SURVEY_ID = "newSurveyId";
	public static final String KEY_BUSINESS_TYPE = "businessObjectType";
	public static final String KEY_GLOBAL_NUM = "globalNum";
	public static final String KEY_INSTANCE_RESPONSES = "instanceResponses";
	public static final String KEY_REDIRECT_URL = "returnUrl";
	public static final String KEY_CONTINUE = "continue";
	
	// Errors messages
	public static final String INVALID_DATE_SURVEYED = "errors.invaleddate";
	public static final String MANDATORY_DATE_SURVEYED = "errors.mandatorydate";
	public static final String EMPTY_FIELD = "errors.emptyfield";
	public static final String INVALID_OFFICER="errors.invalidofficer";
	public static final String NAME_EXISTS="errors.nameexists";
    public static final String INVALID_NUMBER_OF_CHOICES="erros.invalidnumberofchoices";
    
    public static final int QUESTION_TYPE_GENERAL = 0;
    public static final int QUESTION_TYPE_PPI = 1;
	
}
