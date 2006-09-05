package org.mifos.application.personnel.struts.action;

import java.net.URISyntaxException;

import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.struts.actionforms.PersonActionForm;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPersonAction extends MifosMockStrutsTestCase {
	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
					.getPath());
			setConfigFile(ResourceLoader.getURI(
					"org/mifos/framework/util/helpers/struts-config.xml")
					.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		UserContext userContext = TestObjectFactory.getUserContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		Flow flow = new Flow();
		flowKey = String.valueOf(System.currentTimeMillis());
		FlowManager flowManager = new FlowManager();
		flowManager.addFLow(flowKey, flow);
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,
				flowManager);
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("input", "CreateUser");
		PersonnelBusinessService personnelBusinessService = new PersonnelBusinessService();
		SessionUtils.setAttribute(PersonnelConstants.OFFICE,personnelBusinessService.getOffice(Short.valueOf("1")), request);
		SessionUtils.setAttribute(PersonnelConstants.ROLES_LIST, personnelBusinessService.getRoles(), request);
		SessionUtils.setAttribute(PersonnelConstants.ROLEMASTERLIST,
				personnelBusinessService.getRoles(), request);
		
		personnelBusinessService=null;

	}

	public void testChooseOffice() {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.chooseOffice.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.chooseOffice_success.toString());
	}

	public void testLoad() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.load.toString());
		addRequestParameter("officeId", "1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		OfficeBO office=(OfficeBO)SessionUtils.getAttribute(PersonnelConstants.OFFICE,
				request);
		assertNotNull(office);
		assertEquals(1,office.getOfficeId().intValue());
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.TITLE_LIST,
				request));
		assertNotNull(SessionUtils.getAttribute(
				PersonnelConstants.PERSONNEL_LEVEL_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.GENDER_LIST,
				request));
		assertNotNull(SessionUtils.getAttribute(
				PersonnelConstants.MARITAL_STATUS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				PersonnelConstants.LANGUAGE_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.ROLES_LIST,
				request));
		assertNotNull(SessionUtils.getAttribute(
				CustomerConstants.CUSTOM_FIELDS_LIST, request));
		PersonActionForm personActionForm = (PersonActionForm) request
				.getSession().getAttribute("personActionForm");
		assertNotNull(personActionForm);
		assertEquals(1, personActionForm.getCustomFields().size());
		verifyForward(ActionForwards.load_success.toString());
	}

	public void testPreviewFailure() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.preview.toString());
		actionPerform();
		assertEquals(1, getErrrorSize("firstName"));
		assertEquals(1, getErrrorSize("lastName"));
		assertEquals(1, getErrrorSize("gender"));
		assertEquals(1, getErrrorSize("level"));
		assertEquals(1, getErrrorSize("loginName"));
		assertEquals(1, getErrrorSize("userPassword"));
		assertEquals(1, getErrrorSize("dob"));
		verifyInputForward();
	}

	public void testPreviewFailureWrongPasswordLength() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.preview.toString());
		setRequestData();
		addRequestParameter("userPassword", "XXX");
		actionPerform();
		assertEquals(1, getErrrorSize("userPassword"));
		verifyInputForward();
	}

	public void testPreviewFailureWrongPasswordAndReaptPassword()
			throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.preview.toString());
		setRequestData();
		addRequestParameter("userPassword", "XXXXXX");
		addRequestParameter("passwordRepeat", "XXXXXZ");
		actionPerform();
		assertEquals(1, getErrrorSize("userPassword"));
		verifyInputForward();
	}

	public void testPreviewSucess() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter("userPassword", "XXXXXXXX");
		addRequestParameter("passwordRepeat", "XXXXXXXX");

		setRequestData();
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
	}
	public void testPreviousSucess() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.previous.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previous_success.toString());
	}
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testCreateSucess() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.create.toString());
		setRequestData();
		addRequestParameter("userPassword", "XXXXXXXX");
		addRequestParameter("passwordRepeat", "XXXXXXXX");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.create_success.toString());
		assertNotNull(request.getAttribute("globalPersonnelNum"));
		assertNotNull(request.getAttribute("displayName"));
		PersonnelBO personnelBO = new PersonnelPersistence().getPersonnelByGlobalPersonnelNum((String)request.getAttribute("globalPersonnelNum"));
		assertNotNull(personnelBO);
		//assert few values 
		assertEquals("Jim",personnelBO.getPersonnelDetails().getName().getFirstName());
		assertEquals("khan",personnelBO.getPersonnelDetails().getName().getLastName());
		assertEquals(1,personnelBO.getPersonnelDetails().getGender().intValue());
		TestObjectFactory.cleanUp(personnelBO);
		personnelBO=null;
		
	}
	
	private void setRequestData() throws PageExpiredException, ServiceException {
		
		addRequestParameter("firstName", "Jim");
		addRequestParameter("lastName", "khan");
		addRequestParameter("gender", "1");
		addRequestParameter("level", "1");
		addRequestParameter("title", "1");
		addRequestParameter("emailId", "1@1.com");
		addRequestParameter("dob", "20/03/76");
		addRequestParameter("loginName", "tarzen");
		addRequestParameter("personnelRoles", "1");
		addRequestParameter("preferredLocale","189");



		
	}
	
}
