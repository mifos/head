package org.mifos.application.surveys.struts.action;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Session;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.NameType;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.business.TestSurvey;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.InstanceStatus;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSurveyInstanceAction extends MifosMockStrutsTestCase {

	private TestDatabase database; 
	private Session session;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		database = TestDatabase.makeStandard();
		HibernateUtil.closeSession();
		AuditInterceptor interceptor = new AuditInterceptor();
		session = database.openSession(interceptor);
		SessionHolder holder = new SessionHolder(session);
		holder.setInterceptor(interceptor);
		HibernateUtil.setThreadLocal(holder);
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/surveys/struts-config.xml")
				.getPath());
		UserContext userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}
	
	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.resetDatabase();
		super.tearDown();
	}
	
	private ClientBO createClient() {
		ClientBO client = null;
		try {
			Short office = TestObjectFactory.SAMPLE_BRANCH_OFFICE;
			Short formedBy = PersonnelConstants.SYSTEM_USER;
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
					clientDetailView, null);
			client.save();
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
		sampleSurvey.addQuestion(question1, true);
		sampleSurvey.addQuestion(question2, true);
		
		SurveyResponse response1 = new SurveyResponse();
		response1.setQuestion(question1);
		response1.setStringValue("3");
		response1.setInstance(sampleInstance);
		SurveyResponse response2 = new SurveyResponse();
		response2.setQuestion(question2);
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
		assertEquals(survey.getSurveyId(), retrievedSurvey.getSurveyId());
		assertEquals(SurveyInstanceAction.getBusinessObjectName(survey
				.getAppliesToAsEnum(), globalNum), (String) request.getAttribute(
						SurveysConstants.KEY_BUSINESS_OBJECT_NAME));
		
		InstanceStatus status = InstanceStatus.COMPLETED;
		
		int question3Id = question3.getQuestionId();
		int question4Id = question4.getQuestionId();
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
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		SurveyInstance sampleInstance = TestSurvey.makeSurveyInstance("testCreate survey name");
		String clientId = Integer.toString(sampleInstance.getCustomer().getCustomerId());
		String officerName = TestUtils.makeUser().getName();
		
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
		survey.addQuestion(question3, true);
		survey.addQuestion(question4, true);
		survey.addQuestion(question5, true);
		
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
		
		int question3Id = question3.getQuestionId();
		int question4Id = question4.getQuestionId();
		int question5Id = question5.getQuestionId();
		addRequestParameter("value(response_" + question3Id + "_DD)", "14");
		addRequestParameter("value(response_" + question3Id + "_MM)", "3");
		addRequestParameter("value(response_" + question3Id + "_YY)", "2006");
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
		addRequestParameter("value(response_" + question4Id + ")", Integer.toString(choice1.getChoiceId()));
		addRequestParameter("value(response_" + question5Id + ".1)", "1");
		addRequestParameter("value(response_" + question5Id + ".2)", "0");
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
	
	public void testChooseSurvey() throws Exception {
		
		ClientBO client = createClient();
		String globalCustNum = client.getGlobalCustNum();
		
		String nameBase = "testChooseSurvey survey";
		Survey survey1 = new Survey(nameBase + "1", SurveyState.ACTIVE,
				SurveyType.CLIENT);
		Survey survey2 = new Survey(nameBase + "2", SurveyState.ACTIVE,
				SurveyType.LOAN);
		Survey survey3 = new Survey(nameBase + "3", SurveyState.ACTIVE,
				SurveyType.ALL);
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
		
		List<Survey> surveys = (List<Survey>) request
				.getAttribute(SurveysConstants.KEY_SURVEYS_LIST);
		assertEquals(2, surveys.size());
		assertEquals(nameBase + "1", surveys.get(0).getName());
		
		addRequestParameter("method", "create_entry");
		addRequestParameter("value(surveyId)", Integer.toString(surveys.get(0).getSurveyId()));
		addRequestParameter("value(globalNum)", globalCustNum);
		actionPerform();
		verifyNoActionMessages();
		Survey chosenSurvey = (Survey) request.getSession().getAttribute(SurveysConstants.KEY_SURVEY);
		assertNotNull(chosenSurvey);
		assertEquals(survey1.getName(), chosenSurvey.getName());
		
		/*
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "choosesurvey");
		addRequestParameter("surveyType", SurveyType.LOAN.getValue());
		actionPerform();
		verifyNoActionErrors();

		surveys = (List<Survey>) request.getSession().getAttribute(
				SurveysConstants.KEY_SURVEYS_LIST);
		assertEquals(2, surveys.size());
		assertEquals(nameBase + "2", surveys.get(0).getName());
		PersistenceAction.resetDefaultSessionOpener();
		*/
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
	
}
