package org.mifos.application.surveys.struts.action;

import java.util.Date;
import java.util.List;

import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientDetailView;
import org.mifos.application.customer.client.business.ClientNameDetailView;
import org.mifos.application.customer.client.business.NameType;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.PersistenceAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSurveyInstanceAction extends MifosMockStrutsTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
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
		PersistenceAction.resetDefaultSessionOpener();
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
		assertEquals(client.getDisplayName(), request
				.getAttribute(SurveysConstants.KEY_BUSINESS_OBJECT_NAME));
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
		Survey chosenSurvey = (Survey) request.getAttribute(SurveysConstants.KEY_SURVEY);
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
	public void testCreate() throws Exception {
		addRequestParameter("survey", "1");
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "create_entry");
		actionPerform();
		verifyNoActionErrors();
		
		SurveyInstance sampleInstance = TestSurvey.makeSurveyInstance("testCreate survey name");
		String clientId = Integer.toString(sampleInstance.getClient().getCustomerId());
		String officerId = Short.toString(sampleInstance.getOfficer().getPersonnelId());
		
		String dateConducted = DateUtils.makeDateAsSentFromBrowser();
		InstanceStatus status = InstanceStatus.INCOMPLETE;
		Survey survey = sampleInstance.getSurvey();
		getSession().setAttribute(SurveysConstants.KEY_SURVEY, survey);
		
		addRequestParameter("customerId", clientId);
		addRequestParameter("officerId", officerId);
		addRequestDateParameter("dateSurveyed", dateConducted);
		addRequestParameter("instanceStatus", Integer.toString(status.getValue()));
		setRequestPathInfo("/surveyInstanceAction");
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
	}
	
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
