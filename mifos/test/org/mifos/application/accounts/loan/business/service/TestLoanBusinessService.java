package org.mifos.application.accounts.loan.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanBusinessService extends MifosTestCase {

	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	protected AccountPersistence accountPersistence;

	protected LoanBusinessService loanBusinessService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		loanBusinessService = (LoanBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Loan);
		accountPersistence = new AccountPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		group = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,
				group.getCustomerId());
		center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);

		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testFindBySystemId() throws Exception {
		accountBO = getLoanAccount();
		loanBusinessService = new LoanBusinessService();
		LoanBO loanBO = loanBusinessService.findBySystemId(accountBO
				.getGlobalAccountNum());
		assertEquals(loanBO.getGlobalAccountNum(), accountBO
				.getGlobalAccountNum());
		assertEquals(loanBO.getAccountId(), accountBO.getAccountId());
	}

	public void testGetRecentActivityView() throws SystemException,
			NumberFormatException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		HibernateUtil.closeSession();

		for (AccountActionDateEntity actionDate : accountBO
				.getAccountActionDates()) {
			accountBO = accountPersistence.getAccount(accountBO.getAccountId());
			PaymentData paymentData = createPaymentViewObject(accountBO);
			accountBO.applyPayment(paymentData);
			TestObjectFactory.updateObject(accountBO);
		}

		accountBO = accountPersistence.getAccount(accountBO.getAccountId());
		List<LoanActivityView> loanRecentActivityView = loanBusinessService
				.getRecentActivityView(accountBO.getGlobalAccountNum(), Short
						.valueOf("1"));

		assertEquals(3, loanRecentActivityView.size());
		assertNotNull(loanRecentActivityView);
	}

	public void testGetAllActivityView() throws SystemException,
			NumberFormatException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		HibernateUtil.closeSession();

		for (AccountActionDateEntity actionDate : accountBO
				.getAccountActionDates()) {
			accountBO = accountPersistence.getAccount(accountBO.getAccountId());
			PaymentData paymentData = createPaymentViewObject(accountBO);
			accountBO.applyPayment(paymentData);
			TestObjectFactory.updateObject(accountBO);
		}

		accountBO = accountPersistence.getAccount(accountBO.getAccountId());
		List<LoanActivityView> loanAllActivityView = loanBusinessService
				.getAllActivityView(accountBO.getGlobalAccountNum(), Short
						.valueOf("1"));

		assertNotNull(loanAllActivityView);
		assertEquals(6, loanAllActivityView.size());
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

	private PaymentData createPaymentViewObject(AccountBO accountBO) {
		PaymentData paymentData = new PaymentData(new Money(TestObjectFactory
				.getMFICurrency(), "212.0"), accountBO.getPersonnel(), Short
				.valueOf("1"), new Date(System.currentTimeMillis()));
		paymentData.setRecieptDate(new Date(System.currentTimeMillis()));
		paymentData.setRecieptNum("423423");
		return paymentData;
	}
}
