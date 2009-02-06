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

package org.mifos.application.surveys.struts.action;

import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.NameType;
import org.mifos.application.customer.client.persistence.ClientPersistence;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.ppi.business.PPISurveyInstance;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.ppi.persistence.PPIPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.business.TestSurvey;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.InstanceStatus;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.GeneralConfig;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSurveyInstanceAction extends MifosMockStrutsTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		UserContext userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}

	@Override
	protected void tearDown() throws Exception {
		// FIXME: Why is resetting the database necessary? Is there a more
		// elegant way to clean up after particular tests have run?
		TestDatabase.resetMySQLDatabase();
		super.tearDown();
	}

	private ClientBO createClient() throws PersistenceException {
		ClientBO client = null;
		try {
			OfficeBO office = new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
			PersonnelBO formedBy = new PersonnelPersistence().getPersonnel(PersonnelConstants.SYSTEM_USER);
			ClientNameDetailView clientNameDetailView = new ClientNameDetailView(
					NameType.MAYBE_CLIENT, TestObjectFactory.SAMPLE_SALUTATION, "Test Client ",
					"middle", "Test Client ", "secondLast");
			ClientNameDetailView spouseNameDetailView = new ClientNameDetailView(
					NameType.SPOUSE, TestObjectFactory.SAMPLE_SALUTATION, "Test Client ",
					"middle", "Test Client ", "secondLast");
			ClientDetailView clientDetailView = new ClientDetailView(1, 1, 1,
					1, 1, 1, Short.valueOf("1"), Short.valueOf("1"), Short
							.valueOf("41"));
			client = new ClientBO(TestUtils.makeUserWithLocales(),
					"Test Client ",
					CustomerStatus.CLIENT_PARTIAL, null, null, null, null,
					TestObjectFactory.getFees(), null, formedBy, office, null,
					new Date(1222333444000L),
					null, null, null, YesNoFlag.YES.getValue(),
					clientNameDetailView, spouseNameDetailView,
					clientDetailView, null, new CustomerPersistence());
			new ClientPersistence().saveClient(client);
			HibernateUtil.commitTransaction();
		} catch (CustomerException e) {
			throw new RuntimeException(e);
		}
		catch (SystemException e) {
			throw new RuntimeException(e);
		}
		TestObjectFactory.addObject(client);
		return client;
	}

	public void testGet() throws Exception {
		SurveysPersistence persistence = new SurveysPersistence();
		String testName = "testGet survey name";
		SurveyInstance sampleInstance = TestSurvey.makeSurveyInstance(testName);
		Question question1 = new Question("test name 1", "testGet question1 text", AnswerType.NUMBER);
		Question question2 = new Question("test name 2", "testGet question1 text", AnswerType.FREETEXT);

		Survey sampleSurvey = sampleInstance.getSurvey();
		SurveyQuestion surveyQuestion1 = sampleSurvey.addQuestion(question1, true);
		SurveyQuestion surveyQuestion2 =sampleSurvey.addQuestion(question2, true);

		SurveyResponse response1 = new SurveyResponse();
		response1.setSurveyQuestion(surveyQuestion1);
		response1.setStringValue("3");
		response1.setInstance(sampleInstance);
		
		SurveyResponse response2 = new SurveyResponse();
		response2.setSurveyQuestion(surveyQuestion2);
		response2.setStringValue("question2 answer");
		response2.setInstance(sampleInstance);
		
		persistence.createOrUpdate(response1);
		persistence.createOrUpdate(response2);
		persistence.createOrUpdate(sampleInstance);
		sampleInstance = (SurveyInstance) persistence.getPersistentObject(SurveyInstance.class, sampleInstance.getInstanceId());

		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "get");
		addRequestParameter("value(instanceId)", Integer.toString(sampleInstance.getInstanceId()));
		actionPerform();
		verifyNoActionErrors();
		verifyForward("get_success");
		SurveyInstance retrievedInstance = (SurveyInstance) request.getAttribute(SurveysConstants.KEY_INSTANCE);
		assertEquals(sampleInstance.getInstanceId(), retrievedInstance.getInstanceId());
		assertEquals(testName, retrievedInstance.getSurvey().getName());
		String expectedUrl = "clientCustAction.do?method=get&globalCustNum="
				+ sampleInstance.getCustomer().getGlobalCustNum();
		assertEquals(expectedUrl, request.getAttribute(SurveysConstants.KEY_REDIRECT_URL));
	}

	public void testDeleteInstance() throws Exception {
		SurveyInstance instance = TestSurvey.makeSurveyInstance("testDeleteInstance survey name");
		SurveysPersistence persistence = new SurveysPersistence();
		assertTrue(persistence.getInstance(instance.getInstanceId()) != null);
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "delete");
		addRequestParameter("value(surveyType)", "client");
		addRequestParameter("value(instanceId)", Integer.toString(instance.getInstanceId()));
		actionPerform();
		verifyNoActionErrors();
		assertNull(persistence.getInstance(instance.getInstanceId()));
		assertEquals(0, persistence.retrieveResponsesByInstance(instance).size());
	}

	public void testSurveyValidation() throws Exception {
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		SurveyInstance sampleInstance = TestSurvey.makeSurveyInstance("testCreate survey name");
		String clientId = Integer.toString(sampleInstance.getCustomer().getCustomerId());
		UserContext officer = TestUtils.makeUser();
		String officerName = officer.getName();

		Survey survey = sampleInstance.getSurvey();
		Question question1 = new Question("test name 1", "test question 1", AnswerType.FREETEXT);
		Question question2 = new Question("test name 2", "test question 2", AnswerType.NUMBER);
		Question question3 = new Question("test name 3", "test question 3", AnswerType.DATE);
		Question question4 = new Question("test name 4", "test question 4", AnswerType.CHOICE);

		QuestionChoice choice1 = new QuestionChoice("choice 1");
		QuestionChoice choice2 = new QuestionChoice("choice 2");
		question4.addChoice(choice1);
		question4.addChoice(choice2);

		survey.addQuestion(question1, true);
		survey.addQuestion(question2, true);
		survey.addQuestion(question3, true);
		survey.addQuestion(question4, true);

		surveysPersistence.createOrUpdate(survey);

		String globalNum = sampleInstance.getCustomer().getGlobalCustNum();
		addRequestParameter("globalNum", globalNum);
		addRequestParameter("surveyType", "client");
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "choosesurvey");
		actionPerform();
		verifyNoActionErrors();

		addRequestParameter("value(surveyId)", Integer.toString(sampleInstance.getSurvey().getSurveyId()));
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "create_entry");
		actionPerform();
		verifyNoActionErrors();

		Survey retrievedSurvey = (Survey) request.getSession().getAttribute(SurveysConstants.KEY_SURVEY);
		retrievedSurvey = (Survey)HibernateUtil.getSessionTL().get(Survey.class, retrievedSurvey.getSurveyId());

		assertEquals(survey.getSurveyId(), retrievedSurvey.getSurveyId());
		assertEquals(SurveyInstanceAction.getBusinessObjectName(survey
				.getAppliesToAsEnum(), globalNum), (String) request.getAttribute(
						SurveysConstants.KEY_BUSINESS_OBJECT_NAME));

		InstanceStatus status = InstanceStatus.COMPLETED;

		int question3Id = question3.getQuestionId();
		addRequestParameter("value(response_" + question3Id + "_DD)", "14");
		addRequestParameter("value(response_" + question3Id + "_MM)", "30"); // an invalid month
		addRequestParameter("value(response_" + question3Id + "_YY)", "2006");
		addRequestParameter("value(response_" +
				survey.getQuestions().get(0).getQuestion().getQuestionId() + ")",
				"answer 1");
		addRequestParameter("value(response_" +
				survey.getQuestions().get(1).getQuestion().getQuestionId() + ")",
				"notanumber"); // this field should be a number
		addRequestParameter("value(customerId)", clientId);
		addRequestParameter("value(officerName)", officerName);
		addRequestParameter("value(dateSurveyed_DD)", "13");
		addRequestParameter("value(dateSurveyed_MM)", "3");
		addRequestParameter("value(dateSurveyed_YY)", "2007");
		addRequestParameter("value(instanceStatus)", Integer.toString(status.getValue()));

		// this field is missing (commented out)
		//addRequestParameter("value(response_" + question4Id + ")", Integer.toString(choice1.getChoiceId()));
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "preview");
		actionPerform();
		String[] expectedErrors = { "errors.SurveyValidator.MISSING",
				"errors.DateValidator.DATE_FORMAT",
				"errors.NumberValidator.INVALID_NUMBER" };
		verifyActionErrors(expectedErrors);
	}

	public void testCreate() throws Exception {
		UserContext userContext = (UserContext)
			request.getSession().getAttribute(Constants.USERCONTEXT);
		PersonnelBO personnel = createPersonnel(getBranchOffice(),
				PersonnelLevel.LOAN_OFFICER, userContext);
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		SurveyInstance sampleInstance = TestSurvey.makeSurveyInstance("testCreate survey name");
		String clientId = Integer.toString(sampleInstance.getCustomer().getCustomerId());
		String officerName = personnel.getUserName();

		Survey survey = sampleInstance.getSurvey();
		Question question1 = new Question("test name 1", "test question 1", AnswerType.FREETEXT);
		Question question2 = new Question("test name 2", "test question 2", AnswerType.NUMBER);
		Question question3 = new Question("test name 3", "test question 3", AnswerType.DATE);
		Question question4 = new Question("test name 4", "test question 4", AnswerType.CHOICE);
		Question question5 = new Question("test name 5", "test question 4", AnswerType.MULTISELECT);

		QuestionChoice choice1 = new QuestionChoice("choice 1");
		QuestionChoice choice2 = new QuestionChoice("choice 2");
		question4.addChoice(choice1);
		question4.addChoice(choice2);

		QuestionChoice choice3 = new QuestionChoice("choice 3");
		QuestionChoice choice4 = new QuestionChoice("choice 4");
		question5.addChoice(choice3);
		question5.addChoice(choice4);

		survey.addQuestion(question1, true);
		survey.addQuestion(question2, true);
		SurveyQuestion surveyQuestion3 = survey.addQuestion(question3, true);
		SurveyQuestion surveyQuestion4 = survey.addQuestion(question4, true);
		SurveyQuestion surveyQuestion5 = survey.addQuestion(question5, true);

		surveysPersistence.createOrUpdate(survey);

		String globalNum = sampleInstance.getCustomer().getGlobalCustNum();
		addRequestParameter("globalNum", globalNum);
		addRequestParameter("surveyType", "client");
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "choosesurvey");
		actionPerform();
		verifyNoActionErrors();

		addRequestParameter("value(surveyId)", Integer.toString(sampleInstance.getSurvey().getSurveyId()));
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "create_entry");
		actionPerform();
		verifyNoActionErrors();

		Survey retrievedSurvey = (Survey) request.getSession().getAttribute(SurveysConstants.KEY_SURVEY);
		assertEquals(survey.getSurveyId(), retrievedSurvey.getSurveyId());
		assertEquals(SurveyInstanceAction.getBusinessObjectName(survey
				.getAppliesToAsEnum(), globalNum), (String) request.getAttribute(
						SurveysConstants.KEY_BUSINESS_OBJECT_NAME));

		int surveyQuestion3Id = surveyQuestion3.getSurveyQuestionId();
		int surveyQuestion4Id = surveyQuestion4.getSurveyQuestionId();
		int surveyQuestion5Id = surveyQuestion5.getSurveyQuestionId();
		addRequestParameter("value(response_" + surveyQuestion3Id + "_DD)", "14");
		addRequestParameter("value(response_" + surveyQuestion3Id + "_MM)", "3");
		addRequestParameter("value(response_" + surveyQuestion3Id + "_YY)", "2006");
		addRequestParameter("value(response_" +
				survey.getQuestions().get(0).getQuestion().getQuestionId() + ")",
				"answer 1");
		addRequestParameter("value(response_" +
				survey.getQuestions().get(1).getQuestion().getQuestionId() + ")",
				"2");
		addRequestParameter("value(customerId)", clientId);
		addRequestParameter("value(officerName)", officerName);
		addRequestParameter("value(dateSurveyed_DD)", "13");
		addRequestParameter("value(dateSurveyed_MM)", "06");
		addRequestParameter("value(dateSurveyed_YY)", "2007");
		addRequestParameter("value(response_" + surveyQuestion4Id + ")", Integer.toString(choice1.getChoiceId()));
		addRequestParameter("value(response_" + surveyQuestion5Id + ".1)", "1");
		addRequestParameter("value(response_" + surveyQuestion5Id + ".2)", "0");
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();

		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();

		List<SurveyInstance> retrievedInstances = surveysPersistence.retrieveInstancesBySurvey(survey);
		assertEquals(2, retrievedInstances.size());
		SurveyInstance newInstance = retrievedInstances.get(0);
		assertEquals(clientId, Integer.toString(newInstance.getCustomer().getCustomerId()));
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(newInstance.getDateConducted());
		assertEquals(13, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH));
		assertEquals(2007, calendar.get(Calendar.YEAR));
		List<SurveyResponse> responses = surveysPersistence
				.retrieveResponsesByInstance(newInstance);
		assertEquals(5, responses.size());
		assertEquals("answer 1", responses.get(0).getFreetextValue());
		assertEquals(2.0, responses.get(1).getNumberValue());
		Date retrievedDate = responses.get(2).getDateValue();
		calendar.setTime(retrievedDate);
		assertEquals(14, calendar.get(Calendar.DAY_OF_MONTH));
		assertEquals(Calendar.MARCH, calendar.get(Calendar.MONTH));
		assertEquals(2006, calendar.get(Calendar.YEAR));
		assertEquals(choice1.getChoiceId(), responses.get(3).getChoiceValue().getChoiceId());

	}

	public void testCreateWithOfficerDisplayName() throws Exception {
		UserContext userContext = (UserContext)
			request.getSession().getAttribute(Constants.USERCONTEXT);
		PersonnelBO personnel = createPersonnel(getBranchOffice(),
				PersonnelLevel.LOAN_OFFICER, userContext);
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		SurveyInstance sampleInstance = TestSurvey.makeSurveyInstance("testCreate survey name");
		String clientId = Integer.toString(sampleInstance.getCustomer().getCustomerId());
		String officerName = personnel.getDisplayName();

		Survey survey = sampleInstance.getSurvey();
		surveysPersistence.createOrUpdate(survey);

		String globalNum = sampleInstance.getCustomer().getGlobalCustNum();
		addRequestParameter("globalNum", globalNum);
		addRequestParameter("surveyType", "client");
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "choosesurvey");
		actionPerform();
		verifyNoActionErrors();

		addRequestParameter("value(surveyId)", Integer.toString(sampleInstance.getSurvey().getSurveyId()));
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "create_entry");
		actionPerform();
		verifyNoActionErrors();

		addRequestParameter("value(customerId)", clientId);
		addRequestParameter("value(officerName)", officerName);
		addRequestParameter("value(dateSurveyed_DD)", "13");
		addRequestParameter("value(dateSurveyed_MM)", "06");
		addRequestParameter("value(dateSurveyed_YY)", "2007");
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyNoActionErrors();
		verifyForward("preview_success");

		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
	}

	public void testCreateWithOfficerGlobalPersonnelNumber() throws Exception {
		UserContext userContext = (UserContext)
			request.getSession().getAttribute(Constants.USERCONTEXT);
		PersonnelBO personnel = createPersonnel(getBranchOffice(),
				PersonnelLevel.LOAN_OFFICER, userContext);
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		SurveyInstance sampleInstance = TestSurvey.makeSurveyInstance("testCreate survey name");
		String clientId = Integer.toString(sampleInstance.getCustomer().getCustomerId());
		String officerName = personnel.getGlobalPersonnelNum();

		Survey survey = sampleInstance.getSurvey();
		surveysPersistence.createOrUpdate(survey);

		String globalNum = sampleInstance.getCustomer().getGlobalCustNum();
		addRequestParameter("globalNum", globalNum);
		addRequestParameter("surveyType", "client");
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "choosesurvey");
		actionPerform();
		verifyNoActionErrors();

		addRequestParameter("value(surveyId)", Integer.toString(sampleInstance.getSurvey().getSurveyId()));
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "create_entry");
		actionPerform();
		verifyNoActionErrors();

		addRequestParameter("value(customerId)", clientId);
		addRequestParameter("value(officerName)", officerName);
		addRequestParameter("value(dateSurveyed_DD)", "13");
		addRequestParameter("value(dateSurveyed_MM)", "06");
		addRequestParameter("value(dateSurveyed_YY)", "2007");
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyNoActionErrors();
		verifyForward("preview_success");

		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
	}

	public void testCreateFailureBadOfficerValue() throws Exception {
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		SurveyInstance sampleInstance = TestSurvey.makeSurveyInstance("testCreate survey name");
		String clientId = Integer.toString(sampleInstance.getCustomer().getCustomerId());
		String officerName = "If this username exists, this test deserves to fail";

		Survey survey = sampleInstance.getSurvey();
		surveysPersistence.createOrUpdate(survey);

		String globalNum = sampleInstance.getCustomer().getGlobalCustNum();
		addRequestParameter("globalNum", globalNum);
		addRequestParameter("surveyType", "client");
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "choosesurvey");
		actionPerform();
		verifyNoActionErrors();

		addRequestParameter("value(surveyId)", Integer.toString(sampleInstance.getSurvey().getSurveyId()));
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "create_entry");
		actionPerform();
		verifyNoActionErrors();

		addRequestParameter("value(customerId)", clientId);
		addRequestParameter("value(officerName)", officerName);
		addRequestParameter("value(dateSurveyed_DD)", "13");
		addRequestParameter("value(dateSurveyed_MM)", "06");
		addRequestParameter("value(dateSurveyed_YY)", "2007");
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyActionErrors(new String[] {SurveysConstants.INVALID_OFFICER});
	}

	public void testGetPPISurvey() throws Exception {
		setRequestPathInfo("/ppiAction");
		addRequestParameter("method","configure");
		//verifyForward("configure");
		actionPerform();
		verifyNoActionMessages();
		int nonPoorMaxInt = GeneralConfig.getMaxPointsPerPPISurvey();
		Integer nonPoorMax = new Integer(nonPoorMaxInt);
		addRequestParameter("value(country)", Country.INDIA.toString());
		addRequestParameter("value(state)", SurveyState.ACTIVE.toString());
		addRequestParameter("value(veryPoorMin)", "0");
		addRequestParameter("value(veryPoorMax)", "24");
		addRequestParameter("value(poorMin)", "25");
		addRequestParameter("value(poorMax)", "49");
		addRequestParameter("value(atRiskMin)", "50");
		addRequestParameter("value(atRiskMax)", "74");
		addRequestParameter("value(nonPoorMin)", "75");
		addRequestParameter("value(nonPoorMax)", nonPoorMax.toString());
		addRequestParameter("method","preview");
		actionPerform();
		verifyNoActionErrors();

		addRequestParameter("method","update");
		actionPerform();
		verifyNoActionErrors();

		ClientBO client = createClient();
		String globalCustNum = client.getGlobalCustNum();

		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "choosesurvey");
		addRequestParameter("surveyType", SurveyType.CLIENT.getValue());
		addRequestParameter("globalNum", globalCustNum);
		actionPerform();
		verifyNoActionErrors();

		PPIPersistence ppiPersistence = new PPIPersistence();
		PPISurvey ppiSurvey = ppiPersistence.retrieveActivePPISurvey();
		if((ppiSurvey!=null)){
			addRequestParameter("value(surveyId)", Integer.toString(ppiSurvey.getSurveyId()));
		} else {
			addRequestParameter("value(surveyId)", "0");
		}
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "create_entry");
		actionPerform();
		verifyNoActionErrors();

		ppiSurvey = (PPISurvey)HibernateUtil.getSessionTL().get(PPISurvey.class, ppiSurvey.getSurveyId());

		addRequestParameter("value(dateSurveyed_DD)", "01");
		addRequestParameter("value(dateSurveyed_MM)", "02");
		addRequestParameter("value(dateSurveyed_YY)", "1987");
		for (SurveyQuestion question : ppiSurvey.getQuestions()) {
			addRequestParameter("value(response_" + question.getSurveyQuestionId() + ")",
					question.getQuestion().getChoices().get(0).getChoiceId() + "");
		}
		addRequestParameter("method", "preview");
		actionPerform();
		verifyNoActionErrors();

		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();

		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "get");
		addRequestParameter("value(instanceId)", "1");
		actionPerform();
		verifyNoActionErrors();
		SurveyInstance retrievedInstance = (SurveyInstance) request.getAttribute(SurveysConstants.KEY_INSTANCE);
		assertTrue(retrievedInstance instanceof PPISurveyInstance);
		assertTrue(retrievedInstance.getSurvey() instanceof PPISurvey);
		assertEquals(0, ((PPISurveyInstance)retrievedInstance).getScore());
		assertEquals(82.2, ((PPISurveyInstance)retrievedInstance).getBottomHalfBelowPovertyLinePercent());
		assertEquals(6.2, ((PPISurveyInstance)retrievedInstance).getTopHalfBelowPovertyLinePercent());
	}

	public void testChooseSurveyForClient() throws Exception {
		ClientBO client = createClient();
		String globalCustNum = client.getGlobalCustNum();

		String nameBase = "testChooseSurveyForClient survey";
		Survey survey1 = new Survey(nameBase + "1", SurveyState.ACTIVE, SurveyType.CLIENT);
		Survey survey2 = new Survey(nameBase + "2", SurveyState.ACTIVE, SurveyType.LOAN);
		Survey survey3 = new Survey(nameBase + "3", SurveyState.ACTIVE, SurveyType.ALL);
		SurveysPersistence persistence = new SurveysPersistence();
		persistence.createOrUpdate(survey1);
		persistence.createOrUpdate(survey2);
		persistence.createOrUpdate(survey3);

		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "choosesurvey");
		addRequestParameter("surveyType", SurveyType.CLIENT.getValue());
		addRequestParameter("globalNum", globalCustNum);
		actionPerform();
		verifyNoActionErrors();
		assertEquals(client.getDisplayName(),
				request.getAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME));
		List<Survey> surveysList = (List<Survey>) request.getAttribute(SurveysConstants.KEY_SURVEYS_LIST);
		assertEquals(2, surveysList.size());

		assertEquals(nameBase + "1", surveysList.get(0).getName());

		addRequestParameter("method", "create_entry");
		addRequestParameter("value(surveyId)", Integer.toString(surveysList.get(0).getSurveyId()));
		addRequestParameter("value(globalNum)", globalCustNum);
		actionPerform();
		verifyNoActionMessages();
		Survey chosenSurvey = (Survey) request.getSession().getAttribute(SurveysConstants.KEY_SURVEY);
		assertNotNull(chosenSurvey);
		assertEquals(survey1.getName(), chosenSurvey.getName());
	}

	public void testChooseSurveyForLoan() throws Exception {
		String nameBase = "testChooseSurveyForLoan survey";
		Survey survey1 = new Survey(nameBase + "1", SurveyState.ACTIVE, SurveyType.CLIENT);
		Survey survey2 = new Survey(nameBase + "2", SurveyState.ACTIVE, SurveyType.LOAN);
		SurveysPersistence persistence = new SurveysPersistence();
		persistence.createOrUpdate(survey1);
		persistence.createOrUpdate(survey2);

		LoanBO loan = createLoan();
		String globalAccountNum = loan.getGlobalAccountNum();
		assertEquals("000100000000002", globalAccountNum);

		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "choosesurvey");
		addRequestParameter("surveyType", SurveyType.LOAN.getValue());
		addRequestParameter("globalNum", globalAccountNum);
		actionPerform();
		verifyNoActionErrors();
		List<Survey> surveysList = (List<Survey>) request.getAttribute(SurveysConstants.KEY_SURVEYS_LIST);
		assertEquals(1, surveysList.size());
		assertEquals(survey2.getName(), surveysList.get(0).getName());

		addRequestParameter("method", "create_entry");
		addRequestParameter("value(surveyId)", Integer.toString(surveysList.get(0).getSurveyId()));
		addRequestParameter("value(globalNum)", globalAccountNum);
		actionPerform();
		verifyNoActionMessages();
		Survey chosenSurvey = (Survey) request.getSession().getAttribute(SurveysConstants.KEY_SURVEY);
		assertNotNull(chosenSurvey);
		assertEquals(survey2.getName(), chosenSurvey.getName());
	}

	/*
	public void testValidateSuccess() throws Exception {

		String dateConducted = DateUtils.makeDateAsSentFromBrowser();
		//InstanceStatus status = InstanceStatus.INCOMPLETE;
		addRequestParameter("customerId", "4");
		addRequestParameter("officerName", "Someone's Name");
		addRequestDateParameter("dateSurveyed", dateConducted);
		//addRequestParameter("instanceStatus", Integer.toString(status.getValue()));
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyNoActionErrors();
	}
	*/

	private LoanBO createLoan() throws Exception {
		MeetingBO meeting = TestObjectFactory.getNewMeeting(WEEKLY, EVERY_WEEK, LOAN_INSTALLMENT, MONDAY);
		LoanOfferingBO loanOffering = TestObjectFactory.createCompleteLoanOfferingObject();
		CustomerBO customer = TestObjectFactory.createClient("ClientName", meeting, CustomerStatus.CLIENT_ACTIVE);
		return TestObjectFactory.createLoanAccount("12345", customer, AccountState.LOAN_APPROVED, new Date(), loanOffering);
	}

	public void testRedirectUrl() throws Exception {
		String globalNum = "12345";
		assertEquals("clientCustAction.do?method=get&globalCustNum=12345", SurveyInstanceAction.getRedirectUrl(SurveyType.CLIENT, globalNum));
		assertEquals("groupCustAction.do?method=get&globalCustNum=12345", SurveyInstanceAction.getRedirectUrl(SurveyType.GROUP, globalNum));
		assertEquals("centerCustAction.do?method=get&globalCustNum=12345", SurveyInstanceAction.getRedirectUrl(SurveyType.CENTER, globalNum));
		assertEquals("loanAccountAction.do?method=get&globalAccountNum=12345", SurveyInstanceAction.getRedirectUrl(SurveyType.LOAN, globalNum));
		assertEquals("savingsAction.do?method=get&globalAccountNum=12345", SurveyInstanceAction.getRedirectUrl(SurveyType.SAVINGS, globalNum));
	}

	private PersonnelBO createPersonnel(OfficeBO office,
			PersonnelLevel personnelLevel, UserContext userContext) throws Exception{
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				CustomFieldType.NUMERIC));
		 Address address = new Address("abcd","abcd","abcd","abcd","abcd","abcd","abcd","abcd");
		 Name name = new Name("Test", null, null, "User");
		 Date date =new Date();
		 PersonnelBO personnel = new PersonnelBO(personnelLevel,
				 office, Integer.valueOf("1"), Short.valueOf("1"),
					"ABCD", "testusername", "xyz@yahoo.com", null,
					customFieldView, name, "111111", date, Integer
							.valueOf("1"), Integer.valueOf("1"), date, date, address, userContext.getId());
		personnel.save();
		return personnel;
	}

	private OfficeBO getBranchOffice(){
		return TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
	}

}
