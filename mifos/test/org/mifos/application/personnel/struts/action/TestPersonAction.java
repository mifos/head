package org.mifos.application.personnel.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.struts.actionforms.PersonActionForm;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPersonAction extends MifosMockStrutsTestCase {
	private String flowKey;
	
	private UserContext userContext;
	
	private OfficeBO createdBranchOffice;
	
	PersonnelBO personnel;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/personnel/struts-config.xml")
					.getPath());
		userContext = TestObjectFactory.getUserContext();
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
		EntityMasterData.getInstance().init();
		FieldConfigItf fieldConfigItf = FieldConfigImplementer.getInstance();
		fieldConfigItf.init();
		FieldConfigImplementer.getInstance();
		getActionServlet().getServletContext().setAttribute(
				Constants.FIELD_CONFIGURATION,
				fieldConfigItf.getEntityMandatoryFieldMap());
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

	
	@Override
	protected void tearDown() throws Exception {
		userContext = null;
		TestObjectFactory.cleanUp(personnel);
		TestObjectFactory.cleanUp(createdBranchOffice);
		HibernateUtil.closeSession();
		super.tearDown();
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
		verifyMasterData();
		PersonActionForm personActionForm = (PersonActionForm) request
				.getSession().getAttribute("personActionForm");
		assertNotNull(personActionForm);
		assertEquals(1, personActionForm.getCustomFields().size());
		verifyForward(ActionForwards.load_success.toString());
		PersonActionForm actionForm = (PersonActionForm)request.getSession().getAttribute("personActionForm");
		String currentDate = DateHelper.getCurrentDate(TestObjectFactory.getUserContext().getPereferedLocale());
		assertEquals(currentDate,actionForm.getDateOfJoiningMFI());

	}
	public void testLoadWithBranchOffice() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.load.toString());
		addRequestParameter("officeId", "3");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		OfficeBO office=(OfficeBO)SessionUtils.getAttribute(PersonnelConstants.OFFICE,
				request);
		assertNotNull(office);
		assertEquals(3,office.getOfficeId().intValue());
		verifyMasterData();
		PersonActionForm personActionForm = (PersonActionForm) request
				.getSession().getAttribute("personActionForm");
		assertNotNull(personActionForm);
		assertEquals(1, personActionForm.getCustomFields().size());
		assertNotNull(SessionUtils.getAttribute(
				PersonnelConstants.PERSONNEL_LEVEL_LIST, request));
		assertEquals(2,((List)SessionUtils.getAttribute(
				PersonnelConstants.PERSONNEL_LEVEL_LIST, request)).size());
		verifyForward(ActionForwards.load_success.toString());
	}
	public void testPreviewFailure() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.preview.toString());
		actionPerform();
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_FIRSTNAME));
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_LASTNAME));
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_GENDER));
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_LEVEL));
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_USER_NAME));
		assertEquals(1, getErrrorSize(PersonnelConstants.PASSWORD));
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_DOB));
		verifyInputForward();
	}

	public void testPreviewFailureWrongPasswordLength() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.preview.toString());
		setRequestData();
		addRequestParameter("userPassword", "XXX");
		actionPerform();
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_PASSWORD_LENGTH));
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
		assertEquals(1, getErrrorSize(PersonnelConstants.PASSWORD));
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
		PersonnelBO personnelBO = 
			new PersonnelPersistence().getPersonnelByGlobalPersonnelNum(
				(String)request.getAttribute("globalPersonnelNum")
			);
		assertNotNull(personnelBO);
		//assert few values 
		assertEquals("Jim",personnelBO.getPersonnelDetails().getName().getFirstName());
		assertEquals("khan",personnelBO.getPersonnelDetails().getName().getLastName());
		assertEquals(1,personnelBO.getPersonnelDetails().getGender().intValue());
		TestObjectFactory.cleanUp(personnelBO);
	}

	public void testCreateSucessWithNoRoles() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.create.toString());
		addRequestParameter("firstName", "Jim");
		addRequestParameter("lastName", "khan");
		addRequestParameter("gender", "1");
		addRequestParameter("level", "1");
		addRequestParameter("title", "1");
		addRequestParameter("emailId", "1@1.com");
		addRequestParameter("dob", "20/03/76");
		addRequestParameter("loginName", "tarzen");
		addRequestParameter("preferredLocale","189");
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
	
	public void testGetSucess()throws Exception{
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.get.toString());
		addRequestParameter("globalPersonnelNum", "1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyMasterData();
		verifyForward(ActionForwards.get_success.toString());
	}

	private void verifyMasterData()throws Exception{
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
	}

	public void testManage() throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.manage.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.TITLE_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.GENDER_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.MARITAL_STATUS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.LANGUAGE_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.ROLES_LIST, request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
	}
	
	public void testPreviewManage() throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.manage.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.TITLE_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.GENDER_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.MARITAL_STATUS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.LANGUAGE_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.ROLES_LIST, request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.previewManage.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("userPassword", "abcdef");
		addRequestParameter("passwordRepeat", "abcdef");
		addRequestParameter("personnelRoles", "1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previewManage_success.toString());
	}
	
	public void testManagePreviewFailure() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.previewManage.toString());
		actionPerform();
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_FIRSTNAME));
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_LASTNAME));
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_GENDER));
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_LEVEL));
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_USER_NAME));
		assertEquals(1, getErrrorSize(PersonnelConstants.PASSWORD));
		assertEquals(1, getErrrorSize(PersonnelConstants.ERROR_DOB));
		assertEquals(1, getErrrorSize(PersonnelConstants.OFFICE));
		verifyInputForward();
	}

	public void testManagePreviewFailureWrongPasswordLength() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.preview.toString());
		setRequestData();
		addRequestParameter("userPassword", "XXX");
		actionPerform();
		assertEquals(1, getErrrorSize("password"));
		verifyInputForward();
	}

	public void testManagePreviewFailureWrongPasswordAndReaptPassword()
			throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.preview.toString());
		setRequestData();
		addRequestParameter("userPassword", "XXXXXX");
		addRequestParameter("passwordRepeat", "XXXXXZ");
		actionPerform();
		assertEquals(1, getErrrorSize("password"));
		verifyInputForward();
	}
	
	public void testUpdateSuccess() throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		assertEquals(1, personnel.getPersonnelDetails().getGender().intValue());
		assertEquals(1, personnel.getPersonnelDetails().getGender().intValue());
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.manage.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.TITLE_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.PERSONNEL_LEVEL_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.GENDER_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.MARITAL_STATUS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.LANGUAGE_LIST, request));
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.ROLES_LIST, request));
		assertNotNull(SessionUtils.getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request));
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.previewManage.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("personnelRoles", "1");
		addRequestParameter("gender", "2");
		addRequestParameter("maritalStatus", "2");
		addRequestParameter("userPassword", "abcdef");
		addRequestParameter("passwordRepeat", "abcdef");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previewManage_success.toString());
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.update.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());

		assertEquals(2, personnel.getPersonnelDetails().getGender().intValue());
		assertEquals(2, personnel.getPersonnelDetails().getGender().intValue());
		personnel = (PersonnelBO)TestObjectFactory.getObject(PersonnelBO.class,personnel.getPersonnelId());	
	}
	
	public void testLoadUnLockUser() throws Exception {
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.loadUnLockUser.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadUnLockUser_success.toString());
		assertEquals(LoginConstants.MAXTRIES,SessionUtils.getAttribute(PersonnelConstants.LOGIN_ATTEMPTS_COUNT,request));
	}
	
	public void testUnLockUser() throws Exception {
		createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", Methods.unLockUserAccount.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.unLockUserAccount_success.toString());
		assertFalse(personnel.isLocked());
		assertEquals(0,personnel.getNoOfTries().intValue());
	}
	
	private void createPersonnelAndSetInSession(OfficeBO office, PersonnelLevel personnelLevel) throws Exception{
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				Short.valueOf("1")));
		 Address address = new Address("abcd","abcd","abcd","abcd","abcd","abcd","abcd","abcd");
		 Name name = new Name("XYZ", null, null, "Last Name");
		 Date date =new Date();
		 personnel = new PersonnelBO(personnelLevel,
				 office, Integer.valueOf("1"), Short.valueOf("1"),
					"ABCD", "XYZ", "xyz@yahoo.com", null,
					customFieldView, name, "111111", date, Integer
							.valueOf("1"), Integer.valueOf("1"), date, date, address, userContext.getId());
		personnel.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel=(PersonnelBO)HibernateUtil.getSessionTL().get(PersonnelBO.class,personnel.getPersonnelId());
		SessionUtils.setAttribute(Constants.BUSINESS_KEY, personnel, request);
	}
	
	public OfficeBO getBranchOffice(){
		return TestObjectFactory.getOffice(Short.valueOf("3"));
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
