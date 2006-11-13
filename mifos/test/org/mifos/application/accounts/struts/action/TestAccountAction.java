/**
 *
 */
package org.mifos.application.accounts.struts.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.TransactionHistoryView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountAction extends MifosMockStrutsTestCase {

	protected AccountBO accountBO = null;

	private CustomerBO center = null;

	private CustomerBO group = null;

	private UserContext userContext;
	
	private String flowKey;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
					"org/mifos/application/accounts/struts-config.xml")
					.getPath());
		userContext = TestObjectFactory.getContext();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
		flowKey = createFlow(request, AccountAppAction.class);
		accountBO = getLoanAccount();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSuccessfulRemoveFees() {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		Short feeId = null;
		Set<AccountFeesEntity> accountFeesSet = accountBO.getAccountFees();
		for (AccountFeesEntity accountFeesEntity : accountFeesSet) {
			feeId = accountFeesEntity.getFees().getFeeId();
		}
		setRequestPathInfo("/accountAppAction");
		addRequestParameter("method", "removeFees");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter("feeId", feeId.toString());
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		actionPerform();
		verifyForward("remove_success");
		assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
	}

	private AccountBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}

	public void testGetTrxnHistorySucess() throws Exception {
		request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
		Date currentDate = new Date(System.currentTimeMillis());
		setRequestPathInfo("/accountAppAction");
		addRequestParameter("method", "getTrxnHistory");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter("feeId", "123");
		addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
		addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
		LoanBO loan = (LoanBO) accountBO;
		loan.setUserContext(TestObjectFactory.getUserContext());
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(0), null, loan.getPersonnel(),
						"receiptNum", Short.valueOf("1"), currentDate,
						currentDate);
		loan.applyPayment(accountPaymentDataView);
		TestObjectFactory.flushandCloseSession();
		actionPerform();
		verifyForward("getTransactionHistory_success");
		TestObjectFactory.flushandCloseSession();
		accountBO = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());
		List<TransactionHistoryView> trxnHistoryList = (List<TransactionHistoryView>) SessionUtils
				.getAttribute(SavingsConstants.TRXN_HISTORY_LIST, request);
		for (TransactionHistoryView transactionHistoryView : trxnHistoryList)
			assertEquals(accountBO.getUserContext().getName(),
					transactionHistoryView.getPostedBy());
	}
}
