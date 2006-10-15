package org.mifos.application.login.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.components.cronjobs.MifosTask;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoginActionTest extends MifosMockStrutsTestCase {

	private OfficeBO office;

	private PersonnelBO personnel;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/login/struts-config.xml").getPath());
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(personnel);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() {
		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.load.toString());
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
	}

	public void testLoginForFirstTimeUser() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadChangePassword_success.toString());
		assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT,request.getSession()));
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testLoginForUserNull() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", "");
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyInputForward();
		verifyActionErrors(new String[] { "Please specify the value for Username." });
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testLoginForPasswordNull() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "");
		actionPerform();
		verifyInputForward();
		verifyActionErrors(new String[] { "Please specify the value for Password." });
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testUpdatePassword() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadChangePassword_success.toString());
		assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT,request.getSession()));
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.TEMPUSERCONTEXT, request);
		userContext.setId(Short.valueOf("1"));
		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.updatePassword.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("oldPassword", "PASSWORD");
		addRequestParameter("newPassword", "NEW_PASSWORD");
		addRequestParameter("confirmPassword", "NEW_PASSWORD");
		addRequestParameter("input", "LoginChangePW");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.updatePassword_success.toString());
		assertNotNull(SessionUtils.getAttribute(Constants.USERCONTEXT,request.getSession()));
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		HibernateUtil.commitTransaction();
		assertTrue(personnel.isPasswordChanged());
	}

	public void testUpdatePasswordWithUserNull() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadChangePassword_success.toString());
		assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT,request.getSession()));
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.updatePassword.toString());
		addRequestParameter("userName", "");
		addRequestParameter("oldPassword", "PASSWORD");
		addRequestParameter("newPassword", "NEW_PASSWORD");
		addRequestParameter("confirmPassword", "NEW_PASSWORD");
		addRequestParameter("input", "LoginChangePW");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyInputForward();
		verifyActionErrors(new String[] { "Please specify the value for Username." });
	}

	public void testUpdatePasswordWithOldPasswordNotMatching() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadChangePassword_success.toString());
		assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT,request.getSession()));
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.updatePassword.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("oldPassword", "WRONG_PASSWORD");
		addRequestParameter("newPassword", "NEW_PASSWORD");
		addRequestParameter("confirmPassword", "NEW_PASSWORD");
		addRequestParameter("input", "LoginChangePW");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyActionErrors(new String[] { LoginConstants.INVALIDOLDPASSWORD });
		verifyForward(ActionForwards.updatePassword_failure.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testUpdatePasswordWithSameOldAndNewPassword() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadChangePassword_success.toString());
		assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT,request.getSession()));
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.updatePassword.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("oldPassword", "oldPassword");
		addRequestParameter("newPassword", "oldPassword");
		addRequestParameter("confirmPassword", "oldPassword");
		addRequestParameter("input", "LoginChangePW");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyActionErrors(new String[] { LoginConstants.SAME_OLD_AND_NEW_PASSWORD});
		verifyInputForward();
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testLoginForRegularUser() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadChangePassword_success.toString());
		assertNull(SessionUtils.getAttribute(Constants.USERCONTEXT,request.getSession()));
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
		UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.TEMPUSERCONTEXT, request);
		userContext.setId(Short.valueOf("1"));
		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.updatePassword.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("oldPassword", "PASSWORD");
		addRequestParameter("newPassword", "NEW_PASSWORD");
		addRequestParameter("confirmPassword", "NEW_PASSWORD");
		addRequestParameter("input", "LoginChangePW");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();

		HibernateUtil.commitTransaction();
		assertTrue(personnel.isPasswordChanged());
		assertNotNull(SessionUtils.getAttribute(Constants.USERCONTEXT,request.getSession()));
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "NEW_PASSWORD");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.login_success.toString());
		assertNotNull(SessionUtils.getAttribute(Constants.USERCONTEXT,request.getSession()));
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testLoginForInvalidUser() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();
		assertFalse(personnel.isLocked());

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", "WRONG_USERNAME");
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyActionErrors(new String[] { LoginConstants.KEYINVALIDUSER });
		verifyForward(ActionForwards.login_failure.toString());

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
		assertFalse(personnel.isLocked());
		assertEquals(0, personnel.getNoOfTries().intValue());
	}

	public void testLoginForInvalidPassword() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "WRONG_PASSWORD");
		actionPerform();
		verifyActionErrors(new String[] { LoginConstants.INVALIDOLDPASSWORD });
		verifyForward(ActionForwards.login_failure.toString());

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
		assertFalse(personnel.isLocked());
		assertEquals(1, personnel.getNoOfTries().intValue());
	}

	public void testLoginForLockedUser() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();
		lockUser();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyActionErrors(new String[] { LoginConstants.KEYUSERLOCKED });
		verifyForward(ActionForwards.login_failure.toString());

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
		assertTrue(personnel.isLocked());
		assertEquals(5, personnel.getNoOfTries().intValue());
	}

	public void testLogout() {
		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.logout.toString());
		actionPerform();
		verifyActionErrors(new String[] { LoginConstants.LOGOUTOUT });
		verifyForward(ActionForwards.logout_success.toString());
	}

	public void testCancel() throws Exception {
		loadLoginPage();
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadChangePassword_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));

		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.cancel.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancel_success.toString());
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testLogin_cronJobNotRunning() throws Exception {
		loadLoginPage();
		assertEquals(false, MifosTask.isCronJobRunning());
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();
		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.loadChangePassword_success.toString());
		assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	public void testLogin_cronJobRunning() throws Exception {
		loadLoginPage();
		MifosTask.cronJobStarted();
		assertEquals(true, MifosTask.isCronJobRunning());
		assertNotNull(request.getSession().getAttribute(Constants.FLOWMANAGER));
		personnel = createPersonnel();
		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "PASSWORD");
		actionPerform();
		verifyForward(ActionForwards.load_main_page.toString());
		MifosTask.cronJobFinished();
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	private PersonnelBO createPersonnel() throws Exception {
		office = TestObjectFactory.getOffice(Short.valueOf("1"));
		Name name = new Name("XYZ", null, null, null);
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				Short.valueOf("1")));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();
		personnel = new PersonnelBO(PersonnelLevel.LOAN_OFFICER, office,
				Integer.valueOf("1"), Short.valueOf("1"), "PASSWORD",
				"USERNAME", "xyz@yahoo.com", null, customFieldView, name,
				"111111", date, Integer.valueOf("1"), Integer.valueOf("1"),
				date, date, address, Short.valueOf("1"));
		personnel.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		return personnel;
	}

	private void tryLoginWithWrongPassword() {
		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.login.toString());
		addRequestParameter("userName", personnel.getUserName());
		addRequestParameter("password", "WRONG_PASSWORD");
		actionPerform();
		verifyActionErrors(new String[] { LoginConstants.INVALIDOLDPASSWORD });
		verifyForward(ActionForwards.login_failure.toString());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = TestObjectFactory.getPersonnel(personnel.getPersonnelId());
	}

	private void lockUser() {
		tryLoginWithWrongPassword();
		assertFalse(personnel.isLocked());
		assertEquals(1, personnel.getNoOfTries().intValue());
		tryLoginWithWrongPassword();
		assertFalse(personnel.isLocked());
		assertEquals(2, personnel.getNoOfTries().intValue());
		tryLoginWithWrongPassword();
		assertFalse(personnel.isLocked());
		assertEquals(3, personnel.getNoOfTries().intValue());
		tryLoginWithWrongPassword();
		assertFalse(personnel.isLocked());
		assertEquals(4, personnel.getNoOfTries().intValue());
		tryLoginWithWrongPassword();
		assertTrue(personnel.isLocked());
		assertEquals(5, personnel.getNoOfTries().intValue());
	}

	private void loadLoginPage() {
		setRequestPathInfo("/loginAction.do");
		addRequestParameter("method", Methods.load.toString());
		actionPerform();
	}
}
