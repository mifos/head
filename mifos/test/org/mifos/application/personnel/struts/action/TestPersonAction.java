package org.mifos.application.personnel.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.struts.actionforms.PersonActionForm;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfig;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
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
		userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);

		flowKey = createFlow(request, PersonAction.class);

		EntityMasterData.getInstance().init();
		FieldConfig fieldConfig = FieldConfig.getInstance();
		fieldConfig.init();
		getActionServlet().getServletContext().setAttribute(
				Constants.FIELD_CONFIGURATION,
				fieldConfig.getEntityMandatoryFieldMap());
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("input", "CreateUser");
		PersonnelBusinessService personnelBusinessService = new PersonnelBusinessService();
		SessionUtils.setAttribute(PersonnelConstants.OFFICE,personnelBusinessService.getOffice(TestObjectFactory.HEAD_OFFICE), request);
		SessionUtils.setCollectionAttribute(PersonnelConstants.ROLES_LIST, personnelBusinessService.getRoles(), request);
		SessionUtils.setCollectionAttribute(PersonnelConstants.ROLEMASTERLIST,
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
		addActionAndMethod(Methods.chooseOffice.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.chooseOffice_success.toString());
	}

	public void testLoad() throws Exception {
		addActionAndMethod(Methods.load.toString());
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
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		assertEquals(currentDate, DateUtils.getDateAsSentFromBrowser(actionForm.getDateOfJoiningMFI()));

	}
	public void testLoadWithBranchOffice() throws Exception {
		addActionAndMethod(Methods.load.toString());
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
		addActionAndMethod(Methods.preview.toString());
		actionPerform();
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_FIRSTNAME));
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_LASTNAME));
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_GENDER));
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_LEVEL));
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_USER_NAME));
		assertEquals(1, getErrorSize(PersonnelConstants.PASSWORD));
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_DOB));
		verifyInputForward();
	}

	public void testPreviewFailureWrongPasswordLength() throws Exception {
		addActionAndMethod(Methods.preview.toString());
		setRequestData();
		addRequestParameter("userPassword", "XXX");
		actionPerform();
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_PASSWORD_LENGTH));
		verifyInputForward();
	}

	public void testPreviewFailureWrongPasswordAndReaptPassword()
			throws Exception {
		addActionAndMethod(Methods.preview.toString());
		setRequestData();
		addRequestParameter("userPassword", "XXXXXX");
		addRequestParameter("passwordRepeat", "XXXXXZ");
		actionPerform();
		assertEquals(1, getErrorSize(PersonnelConstants.PASSWORD));
		verifyInputForward();
	}

	public void testPreviewSucess() throws Exception {
		addActionAndMethod(Methods.preview.toString());
		addRequestParameter("userPassword", "XXXXXXXX");
		addRequestParameter("passwordRepeat", "XXXXXXXX");

		setRequestData();
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
	}
	public void testPreviousSucess() throws Exception {
		addActionAndMethod(Methods.previous.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previous_success.toString());
	}

	public void testCreateSucess() throws Exception {
		addActionAndMethod(Methods.create.toString());
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
		addActionAndMethod(Methods.create.toString());
		addRequestParameter("firstName", "Jim");
		addRequestParameter("lastName", "khan");
		addRequestParameter("gender", "1");
		addRequestParameter("level", "1");
		addRequestParameter("title", "1");
		addRequestParameter("emailId", "1@1.com");
		addRequestDateParameter("dob", "20/03/76");
		addRequestParameter("loginName", "tarzen");
		addRequestParameter("preferredLocale","1");
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
		addActionAndMethod(Methods.get.toString());
		addRequestParameter("globalPersonnelNum", "1");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyMasterData();
		verifyForward(ActionForwards.get_success.toString());
	}
	public void testSearchSucess()throws Exception{
		addActionAndMethod(Methods.search.toString());
		addRequestParameter("searchString", "Mi");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		QueryResult queryResult =(QueryResult) SessionUtils.getAttribute(Constants.SEARCH_RESULTS,request);
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());
		verifyForward(ActionForwards.search_success.toString());
	}

	public void testSearchwithNoinput()throws Exception{
		cleanRequest();
		addActionAndMethod(Methods.search.toString());
		addRequestParameter("input", "");
		actionPerform();
		assertEquals(1, getErrorSize(PersonnelConstants.NO_SEARCH_STRING));
		verifyInputForward();
	}

	public void testLoadSearchSucess()throws Exception{
		addActionAndMethod(Methods.search.toString());
		addRequestParameter("searchString", "Mi");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.search_success.toString());
	}
	private void addActionAndMethod(String method){
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", method);

	}
	private void cleanRequest() throws PageExpiredException{
		SessionUtils.removeAttribute(PersonnelConstants.OFFICE, request);
		SessionUtils.removeAttribute(PersonnelConstants.ROLES_LIST,  request);
		SessionUtils.removeAttribute(PersonnelConstants.ROLEMASTERLIST, request);

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
		addActionAndMethod(Methods.manage.toString());
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
		addActionAndMethod(Methods.manage.toString());
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
		addActionAndMethod(Methods.previewManage.toString());
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
		addActionAndMethod(Methods.previewManage.toString());
		actionPerform();
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_FIRSTNAME));
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_LASTNAME));
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_GENDER));
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_LEVEL));
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_USER_NAME));
		assertEquals(1, getErrorSize(PersonnelConstants.PASSWORD));
		assertEquals(1, getErrorSize(PersonnelConstants.ERROR_DOB));
		assertEquals(1, getErrorSize(PersonnelConstants.OFFICE));
		assertEquals(1, getErrorSize(PersonnelConstants.STATUS));
		verifyInputForward();
	}

	public void testManagePreviewFailureWrongPasswordLength() throws Exception {
		addActionAndMethod(Methods.preview.toString());
		setRequestData();
		addRequestParameter("userPassword", "XXX");
		actionPerform();
		assertEquals(2, getErrorSize("password"));
		verifyInputForward();
	}

	public void testManagePreviewFailureLoginNameWithSpace() throws Exception {
		addActionAndMethod(Methods.preview.toString());
		setRequestData();
		addRequestParameter("loginName", "XYZ PQR");
		actionPerform();
		assertEquals(1, getErrorSize(PersonnelConstants.INVALID_USER_NAME));
		verifyInputForward();
	}


	public void testManagePreviewFailureWrongPasswordAndReaptPassword()
			throws Exception {
		addActionAndMethod(Methods.preview.toString());
		setRequestData();
		addRequestParameter("userPassword", "XXXXXX");
		addRequestParameter("passwordRepeat", "XXXXXZ");
		actionPerform();
		assertEquals(1, getErrorSize("password"));
		verifyInputForward();
	}

	public void testUpdateSuccess() throws Exception{
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		assertEquals(1, personnel.getPersonnelDetails().getGender().intValue());
		assertEquals(1, personnel.getPersonnelDetails().getGender().intValue());
		addActionAndMethod(Methods.manage.toString());
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
		addActionAndMethod(Methods.previewManage.toString());

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
		addActionAndMethod(Methods.update.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());

		personnel = (PersonnelBO)TestObjectFactory.getObject(PersonnelBO.class,personnel.getPersonnelId());

		assertEquals(2, personnel.getPersonnelDetails().getGender().intValue());
	}

	public void testLoadUnLockUser() throws Exception {
		addActionAndMethod(Methods.loadUnLockUser.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadUnLockUser_success.toString());
		assertEquals(LoginConstants.MAXTRIES,SessionUtils.getAttribute(PersonnelConstants.LOGIN_ATTEMPTS_COUNT,request));
	}

	public void testUnLockUser() throws Exception {
		createPersonnelAndSetInSession(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		addActionAndMethod(Methods.unLockUserAccount.toString());
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
				CustomFieldType.NUMERIC));
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
		return TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
	}

	private void setRequestData() throws PageExpiredException, ServiceException {
		addRequestParameter("firstName", "Jim");
		addRequestParameter("lastName", "khan");
		addRequestParameter("gender", "1");
		addRequestParameter("level", "1");
		addRequestParameter("title", "1");
		addRequestParameter("emailId", "1@1.com");
		addRequestDateParameter("dob", "20/03/76");
		addRequestParameter("loginName", "tarzen");
		addRequestParameter("personnelRoles", "1");
		addRequestParameter("preferredLocale","1");
	}

	public void testLoadChangeLog() throws Exception {
		addActionAndMethod(Methods.get.toString());
		addRequestParameter("globalPersonnelNum", "1");
		actionPerform();
		flowKey = request.getAttribute(Constants.CURRENTFLOWKEY).toString();
		personnel = (PersonnelBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
		AuditLog auditLog = new AuditLog(personnel.getPersonnelId().intValue(), EntityType.PERSONNEL.getValue(), "Mifos", new java.sql.Date(System.currentTimeMillis()),
				Short.valueOf("3"));
		Set<AuditLogRecord> auditLogRecords = new HashSet<AuditLogRecord>();
		AuditLogRecord auditLogRecord = new AuditLogRecord("ColumnName_1",
				"test_1", "new_test_1", auditLog);
		auditLogRecords.add(auditLogRecord);
		auditLog.addAuditLogRecords(auditLogRecords);
		auditLog.save();
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		addRequestParameter("method", "loadChangeLog");
		addRequestParameter("entityType", "Personnel");
		addRequestParameter("entityId", personnel.getPersonnelId().toString());
		actionPerform();
		assertEquals(1,((List)request.getSession().getAttribute(AuditConstants.AUDITLOGRECORDS)).size());
		verifyForward("viewPersonnelChangeLog");
		personnel = null;
		TestObjectFactory.cleanUpChangeLog();
	}

	public void testCancelChangeLog(){
		setRequestPathInfo("/PersonAction.do");
		addRequestParameter("method", "cancelChangeLog");
		addRequestParameter("entityType", "Personnel");
		actionPerform();
		verifyForward("cancelPersonnelChangeLog");
		personnel = null;
	}
}
