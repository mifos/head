package org.mifos.application.accounts.loan.struts.action;

import java.util.Date;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ReverseLoanDisbursalActionTest extends MifosMockStrutsTestCase {

	private UserContext userContext;

	private LoanBO loan = null;

	private CenterBO center = null;

	protected GroupBO group = null;

	private ClientBO client = null;

	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		loadAccountStrutsConfig();
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext",
				TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, MultipleLoanAccountsCreationAction.class);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(loan);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSearch() throws Exception {
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "search");
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.search_success.toString());
	}

	public void testLoadWithoutAccountGlobalNum() throws Exception {
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "load");
		actionPerform();
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { LoanConstants.ERROR_LOAN_ACCOUNT_ID });
		verifyInputForward();
	}

	public void testLoadForInvalidAccountNum() throws Exception {
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "load");
		addRequestParameter("searchString", "123");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { LoanConstants.NOSEARCHRESULTS });
		verifyForward(ActionForwards.search_success.toString());
	}

	public void testLoadForInvalidAccountState() {
		createLoanAccount();
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "load");
		addRequestParameter("searchString", loan.getGlobalAccountNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyActionErrors(new String[] { LoanConstants.NOSEARCHRESULTS });
		verifyForward(ActionForwards.search_success.toString());
	}

	public void testLoad() throws AccountException, PageExpiredException {
		createLoanAccount();
		disburseLoan();

		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "load");
		addRequestParameter("searchString", loan.getGlobalAccountNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());

		assertNotNull(SessionUtils
				.getAttribute(Constants.BUSINESS_KEY, request));
		assertNotNull(SessionUtils.getAttribute(LoanConstants.PAYMENTS_LIST,
				request));
		assertNotNull(SessionUtils.getAttribute(LoanConstants.PAYMENTS_SIZE,
				request));
		HibernateUtil.closeSession();
	}

	public void testPreviewWithoutNotes() throws AccountException,
			PageExpiredException {
		createLoanAccount();
		disburseLoan();

		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "load");
		addRequestParameter("searchString", loan.getGlobalAccountNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		HibernateUtil.closeSession();
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		verifyActionErrors(new String[] { LoanConstants.MANDATORY });
		verifyInputForward();
	}

	public void testPreviewWithNoteGretaerThanMax() throws AccountException,
			PageExpiredException {
		createLoanAccount();
		disburseLoan();

		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "load");
		addRequestParameter("searchString", loan.getGlobalAccountNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		HibernateUtil.closeSession();
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("note", "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789"
				+ "0123456789012345678901234567890123456789");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		verifyActionErrors(new String[] { LoanConstants.MAX_LENGTH });
		verifyInputForward();

	}

	public void testPreview() throws AccountException, PageExpiredException {
		createLoanAccount();
		disburseLoan();

		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "load");
		addRequestParameter("searchString", loan.getGlobalAccountNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		HibernateUtil.closeSession();
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("note", "0123456789012345678901234567890123456789");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();

		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
	}

	public void testUpdate() throws AccountException, PageExpiredException {
		createLoanAccount();
		disburseLoan();

		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "load");
		addRequestParameter("searchString", loan.getGlobalAccountNum());
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		HibernateUtil.closeSession();
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "preview");
		addRequestParameter("note", "0123456789012345678901234567890123456789");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "update");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.update_success.toString());
		
		HibernateUtil.closeSession();
		loan = (LoanBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				loan.getAccountId());
	}

	public void testCancel() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "cancel");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request
				.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.cancel_success.toString());
	}

	public void testValidate() throws Exception {
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.search_success.toString());
	}

	public void testValidateForPreview() throws Exception {
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute("methodCalled", Methods.preview.toString());

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.load_success.toString());
	}

	public void testVaildateForUpdate() throws Exception {
		setRequestPathInfo("/reverseloandisbaction.do");
		addRequestParameter("method", "validate");
		addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
		request.setAttribute("methodCalled", Methods.update.toString());

		actionPerform();
		verifyNoActionErrors();
		verifyNoActionMessages();
		verifyForward(ActionForwards.preview_success.toString());
	}

	private void createInitialCustomers() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
	}

	private void createLoanAccount() {
		createInitialCustomers();
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		loan = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOANACC_APPROVED.getValue(), new Date(System
						.currentTimeMillis()), loanOffering);
	}

	private void disburseLoan() throws AccountException {
		loan.setUserContext(userContext);
		loan.disburseLoan("4534", new Date(), Short.valueOf("1"), group
				.getPersonnel(), new Date(), Short.valueOf("1"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
	}
}
