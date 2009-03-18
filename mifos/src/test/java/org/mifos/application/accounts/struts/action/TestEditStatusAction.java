package org.mifos.application.accounts.struts.action;

import java.sql.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestEditStatusAction extends MifosMockStrutsTestCase {
	public TestEditStatusAction() throws SystemException, ApplicationException {
        super();
    }

    private AccountBO accountBO;

	private UserContext userContext;

	private CustomerBO client;

	private CustomerBO group;

	private CustomerBO center;

	private MeetingBO meeting;

	private SavingsOfferingBO savingsOffering;

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, EditStatusAction.class);
	}

	private void reloadMembers() {
		if (accountBO != null) {
			accountBO = (AccountBO)StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
		}
		if (group != null) {
			group = (GroupBO)StaticHibernateUtil.getSessionTL().get(GroupBO.class, group.getCustomerId());
		}
		if (center != null) {
			center = (CenterBO)StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
		}
		if (client != null) {
			client = (CustomerBO)StaticHibernateUtil.getSessionTL().get(CustomerBO.class, client.getCustomerId());
		}
		
	}
	
	@Override
	public void tearDown() throws Exception {		
		try {
			reloadMembers();
			TestObjectFactory.cleanUp(accountBO);
			TestObjectFactory.cleanUp(client);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testLoad() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		accountBO = getLoanAccount(client, meeting,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);
		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("input", "loan");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("load_success");
		verifyNoActionErrors();
		verifyNoActionMessages();

		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request)).size());
		StaticHibernateUtil.closeSession();
	}

	public void testPreviewSuccess() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		accountBO = getLoanAccount(client, meeting,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("input", "loan");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request)).size());
		
		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("method", "preview");
		addRequestParameter("input", "loan");
		addRequestParameter("notes", "Test");
		addRequestParameter("accountTypeId", accountBO.getType()
				.getValue().toString());
		addRequestParameter("newStatusId", "8");
		addRequestParameter("flagId", "1");
		actionPerform();
		verifyForward("preview_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
		assertEquals("Closed- Rescheduled", (String) SessionUtils.getAttribute(
				SavingsConstants.NEW_STATUS_NAME, request));
		assertNull("Since new Status is not cancel,so flag should be null.",
				SessionUtils.getAttribute(SavingsConstants.FLAG_NAME, request
						.getSession()));
		StaticHibernateUtil.closeSession();
	}

	public void testPreviewFailure() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		accountBO = getLoanAccount(client, meeting,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("input", "loan");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request)).size());
		StaticHibernateUtil.closeSession();
		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "loan");
		addRequestParameter("accountTypeId", accountBO.getType()
				.getValue().toString());
		addRequestParameter("newStatusId", "8");
		addRequestParameter("flagId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		assertEquals(1, getErrorSize());
		verifyActionErrors(new String[] { LoanConstants.MANDATORY_TEXTBOX });
	}

	public void testPrevious() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		accountBO = getLoanAccount(client, meeting,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);
		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "previous");
		addRequestParameter("input", "loan");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("previous_success");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testCancel() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter("input", "loan");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("loan_detail_page");
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testUpdateSuccessForLoan() throws Exception {
		TestObjectFactory.cleanUpChangeLog();
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		accountBO = getLoanAccount(client, meeting,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING);

		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("input", "loan");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request)).size());

		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "loan");
		addRequestParameter("notes", "Test");
		addRequestParameter("accountTypeId", accountBO.getType()
				.getValue().toString());
		addRequestParameter("newStatusId", "8");
		addRequestParameter("flagId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("preview_success");
		
		StaticHibernateUtil.closeSession();
		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("notes", "Test");
		addRequestParameter("accountTypeId", accountBO.getType()
				.getValue().toString());
		addRequestParameter("newStatusId", "8");
		addRequestParameter("flagId", "1");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.loan_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		accountBO = TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(
				EntityType.LOAN, accountBO.getAccountId());
		assertEquals(1, auditLogList.size());
		assertEquals(EntityType.LOAN.getValue(), auditLogList.get(0)
				.getEntityType());
		assertEquals(2, auditLogList.get(0).getAuditLogRecords().size());
		for (AuditLogRecord auditLogRecord : auditLogList.get(0)
				.getAuditLogRecords()) {
			if (auditLogRecord.getFieldName().equalsIgnoreCase("Explanation")) {
				assertEquals("-", auditLogRecord.getOldValue());
				assertEquals("Withdraw", auditLogRecord.getNewValue());
			}else if (auditLogRecord.getFieldName().equalsIgnoreCase("Status")) {
				assertEquals("Active in Good Standing", auditLogRecord
						.getOldValue());
				assertEquals("Closed- Rescheduled", auditLogRecord
						.getNewValue());
			}
		}
		TestObjectFactory.cleanUpChangeLog();
	}

	public void testUpdateStatusForLoanToCancel() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		accountBO = getLoanAccount(client, meeting,
				AccountState.LOAN_PARTIAL_APPLICATION);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,accountBO,request);
		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter("notes", "Test");
		addRequestParameter("accountTypeId", accountBO.getType()
				.getValue().toString());
		addRequestParameter("newStatusId", AccountState.LOAN_CANCELLED
				.getValue().toString());
		addRequestParameter("flagId", AccountStateFlag.LOAN_WITHDRAW.getValue()
				.toString());
		addRequestParameter("input", "loan");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.loan_detail_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testUpdateSuccessForSavings() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		accountBO = createSavingsAccount("000X00000000019", savingsOffering,
				AccountState.SAVINGS_PARTIAL_APPLICATION);

		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "load");
		addRequestParameter("input", "savings");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("load_success");
		assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST,
				request));
		assertEquals("Size of the status list should be 2", 2,
				((List<AccountStateEntity>) SessionUtils.getAttribute(
						SavingsConstants.STATUS_LIST, request)).size());

		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("input", "savings");
		addRequestParameter("notes", "Test");
		addRequestParameter("accountTypeId", accountBO.getType()
				.getValue().toString());
		addRequestParameter("newStatusId", "15");
		addRequestParameter("flagId", "4");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("preview_success");

		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("notes", "Test");
		addRequestParameter("accountTypeId", accountBO.getType()
				.getValue().toString());
		addRequestParameter("newStatusId", "15");
		addRequestParameter("flagId", "4");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.savings_details_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testUpdateStatusForSavingsToCancel() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		accountBO = createSavingsAccount("000X00000000019", savingsOffering,
				AccountState.SAVINGS_PARTIAL_APPLICATION);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,accountBO,request);
		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter("notes", "Test");
		addRequestParameter("accountTypeId", accountBO.getType()
				.getValue().toString());
		addRequestParameter("input", "savings");
		addRequestParameter("newStatusId", AccountState.SAVINGS_CANCELLED
				.getValue().toString());
		addRequestParameter("flagId", AccountStateFlag.SAVINGS_BLACKLISTED
				.getValue().toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward(ActionForwards.savings_details_page.toString());
		verifyNoActionErrors();
		verifyNoActionMessages();
	}

	public void testUpdateStatusFailureNoPermission() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		createInitialObjects();
		savingsOffering = createSavingsOffering();
		accountBO = createSavingsAccount("000X00000000019", savingsOffering,
				AccountState.SAVINGS_PARTIAL_APPLICATION);
		SessionUtils.setAttribute(Constants.BUSINESS_KEY,accountBO,request);
		SessionUtils.setAttribute(Constants.USERCONTEXT, createUser(), request
				.getSession());
		setRequestPathInfo("/editStatusAction.do");
		addRequestParameter("method", "update");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter("notes", "Test");
		addRequestParameter("accountTypeId", accountBO.getType()
				.getValue().toString());
		addRequestParameter("newStatusId", "15");
		addRequestParameter("flagId", "4");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyActionErrors(new String[] { SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED });
	}

	private UserContext createUser() throws Exception {
		this.userContext = TestUtils.makeUser(TestUtils.DUMMY_ROLE);
		return userContext;
	}

	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting,
			AccountState accountState) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				accountState, startDate, loanOffering);

	}

	private SavingsOfferingBO createSavingsOffering() {
		Date currentDate = new Date(System.currentTimeMillis());
		return TestObjectFactory.createSavingsProduct(
			"SavingPrd1", "S", currentDate);
	}

	private SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering, AccountState state)
			throws Exception {
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				state, new Date(System.currentTimeMillis()),
				savingsOffering, userContext);
	}
}
