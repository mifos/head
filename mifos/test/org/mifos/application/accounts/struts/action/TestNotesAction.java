package org.mifos.application.accounts.struts.action;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.framework.util.valueobjects.Context;

public class TestNotesAction extends MifosMockStrutsTestCase {

	private SavingsBO savingsBO;

	private LoanBO loanBO;

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
					"org/mifos/application/accounts/struts-config.xml")
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
		TestObjectFactory.cleanUp(loanBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf1", "ads1");
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", savingsBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Savings");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreview_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf2", "ads2");
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPrevious_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf3", "ads3");
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancel_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf4", "ads4");
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("accountTypeId", savingsBO.getAccountType()
				.getAccountTypeId().toString());
		actionPerform();
		verifyForward("savings_details_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCreate_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf5", "ads5");

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", savingsBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Savings");
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("comment", "Notes created");
		getRequest().getSession().setAttribute("security_param", "Savings");
		actionPerform();
		verifyForward("savings_details_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSearch_Savings() throws Exception {
		savingsBO = getSavingsAccount("fsaf6", "ads6");
		Context context = new Context();
		SessionUtils.setAttribute(Constants.CONTEXT, context, request
				.getSession());

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", savingsBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Savings");
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "create");
		getRequest().getSession().setAttribute("security_param", "Savings");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "search");
		actionPerform();
		verifyForward("search_success");
		verifyNoActionErrors();
		verifyNoActionMessages();

		context = (Context) SessionUtils.getAttribute(Constants.CONTEXT,
				request.getSession());
		assertEquals("Size of the search result should be 1", 1, context
				.getSearchResult().getSize());
	}

	public void testLoad_Loan() {
		loanBO = getLoanAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", loanBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Loan");
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPreview_Loan() {
		loanBO = getLoanAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testPrevious_Loan() {
		loanBO = getLoanAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "previous");
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancel_Loan() {
		loanBO = getLoanAccount();
		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("accountTypeId", loanBO.getAccountType()
				.getAccountTypeId().toString());
		actionPerform();
		verifyForward("loan_detail_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCreate_Loan() {
		loanBO = getLoanAccount();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", loanBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Loan");
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "create");
		addRequestParameter("comment", "Notes created");
		getRequest().getSession().setAttribute("security_param", "Loan");
		actionPerform();
		verifyForward("loan_detail_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testSearch_Loan() throws HibernateSearchException {
		loanBO = getLoanAccount();
		Context context = new Context();
		SessionUtils.setAttribute(Constants.CONTEXT, context, request
				.getSession());

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("accountId", loanBO.getAccountId().toString());
		getRequest().getSession().setAttribute("security_param", "Loan");
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "create");
		getRequest().getSession().setAttribute("security_param", "Loan");
		addRequestParameter("comment", "Notes created");
		actionPerform();

		setRequestPathInfo("/notesAction.do");
		addRequestParameter("method", "search");
		actionPerform();
		verifyForward("search_success");
		verifyNoActionErrors();
		verifyNoActionMessages();

		context = (Context) SessionUtils.getAttribute(Constants.CONTEXT,
				request.getSession());
		assertEquals("Size of the search result should be 1", 1, context
				.getSearchResult().getSize());
	}

	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.4", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", GroupConstants.ACTIVE,
				"1.4.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				ClientConstants.STATUS_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
	}

	private SavingsBO getSavingsAccount(String offeringName, String shortName)
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering(offeringName, shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017",
				client, AccountStates.SAVINGS_ACC_APPROVED, new Date(System
						.currentTimeMillis()), savingsOffering);
	}

	private LoanBO getLoanAccount() {
		createInitialObjects();
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", client, Short
				.valueOf("5"), startDate, loanOffering);

	}
}
