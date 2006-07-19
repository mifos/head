package org.mifos.application.accounts.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestNotesAction extends MifosMockStrutsTestCase {
	
	private SavingsBO savingsBO;

	private UserContext userContext;
	
	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;
	
	private MeetingBO meeting;
	
	private SavingsTestHelper helper = new SavingsTestHelper();
	
	private SavingsOfferingBO savingsOffering;

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
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
	}

	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savingsBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() {
		savingsBO = getSavingsAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreview() {
		savingsBO = getSavingsAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPrevious() {
		savingsBO = getSavingsAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancel() {
		savingsBO = getSavingsAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("accountTypeId", savingsBO.getAccountType().getAccountTypeId().toString());
		actionPerform();
		verifyForward("savings_details_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCreate() {
		savingsBO = getSavingsAccount();
		
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();
		
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("accountTypeId", savingsBO.getAccountType().getAccountTypeId().toString());
		addRequestParameter("accountId", savingsBO.getAccountId().toString());
		addRequestParameter("comment", "Notes created");
		actionPerform();
		verifyForward("savings_details_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}
	
	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short
				.valueOf("13"), "1.4", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE, "1.4.1", center, new Date(System
				.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",ClientConstants.STATUS_ACTIVE,"1.4.1.1",group,new Date(System
				.currentTimeMillis()));
	}
	
	private SavingsBO getSavingsAccount() {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		return TestObjectFactory.createSavingsAccount("000100000000017",
				client, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}

}
