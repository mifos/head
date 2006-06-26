/**
 *
 */
package org.mifos.application.accounts.struts.action;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.TestObjectFactory;

import servletunit.struts.MockStrutsTestCase;

/**
 * @author krishankg
 *
 */
public class TestAccountAction extends MockStrutsTestCase {

	protected AccountBO accountBO = null;

	private CustomerBO center = null;

	private CustomerBO group = null;

	// success
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
		UserContext userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute(LoginConstants.ACTIVITYCONTEXT,
				ac);
		accountBO = getLoanAccount();

	}

	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}

	public void testSuccessfulRemoveFees() {
		Short feeId = null;
		Set<AccountFeesEntity> accountFeesSet = accountBO.getAccountFees();
		for (AccountFeesEntity accountFeesEntity : accountFeesSet) {
			feeId = accountFeesEntity.getFees().getFeeId();
		}
		setRequestPathInfo("/accountAppAction");
		addRequestParameter("method", "removeFees");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter("feeId", feeId.toString());
		actionPerform();
		verifyForward("remove_success");

	}

	private AccountBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
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

	public void  testGetTrxnHistorySucess()throws Exception{
		Date currentDate = new Date(System.currentTimeMillis());
		setRequestPathInfo("/accountAppAction");
		addRequestParameter("method", "getTrxnHistory");
		addRequestParameter("accountId", accountBO.getAccountId().toString());
		addRequestParameter("feeId", "123");

		addRequestParameter("globalAccountNum", accountBO.getGlobalAccountNum());
		LoanBO loan = (LoanBO) accountBO;
		loan.setUserContext(TestObjectFactory.getUserContext());
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(0), null, Short.valueOf("1"),
						"receiptNum", Short.valueOf("1"), currentDate,
						currentDate);
		loan.applyPayment(accountPaymentDataView);
		TestObjectFactory.flushandCloseSession();
		actionPerform();
		verifyForward("getTransactionHistory_success");
		TestObjectFactory.flushandCloseSession();
		accountBO = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());
	}
}
