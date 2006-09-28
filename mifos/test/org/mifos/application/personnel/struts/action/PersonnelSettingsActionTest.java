package org.mifos.application.personnel.struts.action;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigItf;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterData;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PersonnelSettingsActionTest extends MifosMockStrutsTestCase {
	private String flowKey;

	private UserContext userContext;

	PersonnelBO personnel;

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
		SessionUtils
				.setAttribute(PersonnelConstants.OFFICE,
						personnelBusinessService.getOffice(Short.valueOf("1")),
						request);
		SessionUtils.setAttribute(PersonnelConstants.ROLES_LIST,
				personnelBusinessService.getRoles(), request);
		SessionUtils.setAttribute(PersonnelConstants.ROLEMASTERLIST,
				personnelBusinessService.getRoles(), request);

		personnelBusinessService = null;

	}

	@Override
	protected void tearDown() throws Exception {
		userContext = null;
		TestObjectFactory.cleanUp(personnel);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGet() throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.get.toString());
		addRequestParameter("globalPersonnelNum", personnel
				.getGlobalPersonnelNum());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyMasterData();
		verifyForward(ActionForwards.get_success.toString());
	}

	public void testManage() throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.get.toString());
		addRequestParameter("globalPersonnelNum", personnel
				.getGlobalPersonnelNum());
		actionPerform();

		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.manage.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.manage_success.toString());
	}

	public void testFailurePreview() throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter("middleName", personnel.getPersonnelDetails()
				.getName().getMiddleName());
		addRequestParameter("secondLastName", personnel.getPersonnelDetails()
				.getName().getSecondLastName());
		addRequestParameter("maritalStatus", personnel.getPersonnelDetails()
				.getMaritalStatus().toString());
		addRequestParameter("emailId", personnel.getEmailId());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrrorSize("firstName"));
		assertEquals(1, getErrrorSize("lastName"));
		assertEquals(1, getErrrorSize("gender"));
		verifyInputForward();
	}

	public void testFailurePreviewNoFirstName() throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter("middleName", personnel.getPersonnelDetails()
				.getName().getMiddleName());
		addRequestParameter("secondLastName", personnel.getPersonnelDetails()
				.getName().getSecondLastName());
		addRequestParameter("lastName", personnel.getPersonnelDetails()
				.getName().getLastName());
		addRequestParameter("gender", personnel.getPersonnelDetails()
				.getGender().toString());
		addRequestParameter("maritalStatus", personnel.getPersonnelDetails()
				.getMaritalStatus().toString());
		addRequestParameter("emailId", personnel.getEmailId());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrrorSize("firstName"));
		verifyInputForward();
	}

	public void testFailurePreviewFirstNameLengthExceedsLimit()
			throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter(
				"firstName",
				"Testing for firstName length exceeding by 100 characters"
						+ "Testing for firstName length exceeding by 100 characters"
						+ "Testing for firstName length exceeding by 100 characters"
						+ "Testing for firstName length exceeding by 100 characters"
						+ "Testing for firstName length exceeding by 100 characters "
						+ "Testing for firstName length exceeding by 100 characters "
						+ "Testing for firstName length exceeding by 100 characters");
		addRequestParameter("middleName", personnel.getPersonnelDetails()
				.getName().getMiddleName());
		addRequestParameter("secondLastName", personnel.getPersonnelDetails()
				.getName().getSecondLastName());
		addRequestParameter("lastName", personnel.getPersonnelDetails()
				.getName().getLastName());
		addRequestParameter("gender", personnel.getPersonnelDetails()
				.getGender().toString());
		addRequestParameter("maritalStatus", personnel.getPersonnelDetails()
				.getMaritalStatus().toString());
		addRequestParameter("emailId", personnel.getEmailId());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrrorSize("firstName"));
		verifyInputForward();
	}

	public void testFailurePreviewLastNameLengthExceedsLimit() throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter(
				"lastName",
				"Testing for lastName length exceeding by 100 characters"
						+ "Testing for lastName length exceeding by 100 characters"
						+ "Testing for lastName length exceeding by 100 characters"
						+ "Testing for lastName length exceeding by 100 characters"
						+ "Testing for lastName length exceeding by 100 characters "
						+ "Testing for lastName length exceeding by 100 characters "
						+ "Testing for lastName length exceeding by 100 characters");
		addRequestParameter("middleName", personnel.getPersonnelDetails()
				.getName().getMiddleName());
		addRequestParameter("secondLastName", personnel.getPersonnelDetails()
				.getName().getSecondLastName());
		addRequestParameter("firstName", personnel.getPersonnelDetails()
				.getName().getFirstName());
		addRequestParameter("gender", personnel.getPersonnelDetails()
				.getGender().toString());
		addRequestParameter("maritalStatus", personnel.getPersonnelDetails()
				.getMaritalStatus().toString());
		addRequestParameter("emailId", personnel.getEmailId());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrrorSize("lastName"));
		verifyInputForward();
	}

	public void testFailurePreviewNoLastName() throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter("firstName", personnel.getPersonnelDetails()
				.getName().getFirstName());
		addRequestParameter("middleName", personnel.getPersonnelDetails()
				.getName().getMiddleName());
		addRequestParameter("secondLastName", personnel.getPersonnelDetails()
				.getName().getSecondLastName());
		addRequestParameter("gender", personnel.getPersonnelDetails()
				.getGender().toString());
		addRequestParameter("maritalStatus", personnel.getPersonnelDetails()
				.getMaritalStatus().toString());
		addRequestParameter("emailId", personnel.getEmailId());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrrorSize("lastName"));
		verifyInputForward();
	}

	public void testFailurePreviewNoGenderSelected() throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter("firstName", personnel.getPersonnelDetails()
				.getName().getFirstName());
		addRequestParameter("middleName", personnel.getPersonnelDetails()
				.getName().getMiddleName());
		addRequestParameter("secondLastName", personnel.getPersonnelDetails()
				.getName().getSecondLastName());
		addRequestParameter("lastName", personnel.getPersonnelDetails()
				.getName().getLastName());
		addRequestParameter("maritalStatus", personnel.getPersonnelDetails()
				.getMaritalStatus().toString());
		addRequestParameter("emailId", personnel.getEmailId());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrrorSize("gender"));
		verifyInputForward();
	}

	public void testFailurePreviewDisplayLengthExceedsMaxLimit()
			throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		userContext.setId(personnel.getPersonnelId());
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.get.toString());
		addRequestParameter("globalPersonnelNum", personnel
				.getGlobalPersonnelNum());
		actionPerform();

		userContext.setId(Short.valueOf("1"));
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.manage.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();

		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter(
				"firstName",
				"Testing for displayName length exceeding by 200 characters.It should be less than 200");
		addRequestParameter("middleName", "new middle name");
		addRequestParameter("secondLastName", "new second Last name");
		addRequestParameter(
				"lastName",
				"Testing for displayName length exceeding by 200 characters.It should be less than 200");
		addRequestParameter("gender", "2");
		addRequestParameter("maritalStatus", "2");
		addRequestParameter("emailId", "XYZ@aditi.com");
		addRequestParameter("preferredLocale", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();

		assertEquals(1, getErrrorSize("displayName"));
		verifyInputForward();
	}

	public void testSuccessPreview() throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter("firstName", personnel.getPersonnelDetails()
				.getName().getFirstName());
		addRequestParameter("middleName", personnel.getPersonnelDetails()
				.getName().getMiddleName());
		addRequestParameter("secondLastName", personnel.getPersonnelDetails()
				.getName().getSecondLastName());
		addRequestParameter("lastName", personnel.getPersonnelDetails()
				.getName().getLastName());
		addRequestParameter("gender", personnel.getPersonnelDetails()
				.getGender().toString());
		addRequestParameter("maritalStatus", personnel.getPersonnelDetails()
				.getMaritalStatus().toString());
		addRequestParameter("emailId", personnel.getEmailId());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
	}
	
	public void testPrevious() throws Exception {
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.previous.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.previous_success.toString());
	}

	public void testSuccessUpdate() throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		userContext.setId(personnel.getPersonnelId());
		request.getSession().setAttribute(Constants.USER_CONTEXT_KEY,
				userContext);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.get.toString());
		addRequestParameter("globalPersonnelNum", personnel
				.getGlobalPersonnelNum());
		actionPerform();

		userContext.setId(Short.valueOf("1"));
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.manage.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();

		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.preview.toString());
		addRequestParameter("firstName", "new first name");
		addRequestParameter("middleName", "new middle name");
		addRequestParameter("secondLastName", "new second Last name");
		addRequestParameter("lastName", "new last name");
		addRequestParameter("gender", "2");
		addRequestParameter("maritalStatus", "2");
		addRequestParameter("emailId", "XYZ@aditi.com");
		addRequestParameter("preferredLocale", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();

		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.update.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.updateSettings_success.toString());

		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		assertEquals("new first name", personnel.getPersonnelDetails()
				.getName().getFirstName());
		assertEquals("new middle name", personnel.getPersonnelDetails()
				.getName().getMiddleName());
		assertEquals("new second Last name", personnel.getPersonnelDetails()
				.getName().getSecondLastName());
		assertEquals("new last name", personnel.getPersonnelDetails().getName()
				.getLastName());
		assertEquals("XYZ@aditi.com", personnel.getEmailId());
		assertEquals(2, personnel.getPersonnelDetails().getGender().intValue());
		assertEquals(1, personnel.getPreferredLocale().getLocaleId().intValue());
		assertEquals(2, personnel.getPersonnelDetails().getMaritalStatus()
				.intValue());
	}

	public void testLoadChangePassword() throws Exception {
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.loadChangePassword.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadChangePassword_success.toString());
	}

	public void testGet_cronJobNotRunning() throws Exception {
		assertEquals(false, MifosTask.isCronJobRunning());
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.get.toString());
		addRequestParameter("globalPersonnelNum", personnel
				.getGlobalPersonnelNum());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyMasterData();
		verifyForward(ActionForwards.get_success.toString());
	}

	public void testGet_cronJobRunning() throws Exception {
		MifosTask.cronJobStarted();
		assertEquals(true, MifosTask.isCronJobRunning());
		createPersonnel(getBranchOffice(), PersonnelLevel.LOAN_OFFICER);
		setRequestPathInfo("/yourSettings.do");
		addRequestParameter("method", Methods.get.toString());
		addRequestParameter("globalPersonnelNum", personnel
				.getGlobalPersonnelNum());
		actionPerform();
		verifyForward(ActionForwards.load_main_page.toString());
		MifosTask.cronJobFinished();
	}
	
	private void verifyMasterData() throws Exception {
		assertNotNull(SessionUtils.getAttribute(PersonnelConstants.GENDER_LIST,
				request));
		assertNotNull(SessionUtils.getAttribute(
				PersonnelConstants.MARITAL_STATUS_LIST, request));
		assertNotNull(SessionUtils.getAttribute(
				PersonnelConstants.LANGUAGE_LIST, request));
	}

	private PersonnelBO createPersonnel(OfficeBO office,
			PersonnelLevel personnelLevel) throws Exception {
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				Short.valueOf("1")));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();
		personnel = new PersonnelBO(personnelLevel, office, Integer
				.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
				"xyz@yahoo.com", getRoles(), customFieldView, new Name("XYZ",
						null, null, "ABC"), "111111", date, Integer
						.valueOf("1"), Integer.valueOf("1"), date, date,
				address, userContext.getId());
		personnel.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		return personnel;
	}

	public List<RoleBO> getRoles() throws Exception {
		return ((PersonnelBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Personnel)).getRoles();
	}

	private OfficeBO getBranchOffice() {
		return TestObjectFactory.getOffice(Short.valueOf("1"));

	}
}
