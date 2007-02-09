package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanSummaryEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanTrxnDetailEntity extends MifosTestCase {

	private CustomerBO center;

	private CustomerBO group;

	private CustomerBO client;

	private AccountBO account;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(account);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testSuccessSetRunningBalance() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		Date sampleDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
			sampleDate, meeting);
		account = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, 
				sampleDate,
				loanOffering);
		HibernateUtil.closeSession();
		account = new AccountPersistence().getAccount(account
				.getAccountId());
		assertEquals(((LoanBO) account).getLoanOffering().getPrdOfferingName(),
				"Loan");

		List<AccountActionDateEntity> accountActionsToBeUpdated = new ArrayList<AccountActionDateEntity>();
		accountActionsToBeUpdated.add(account.getAccountActionDates()
				.iterator().next());
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accountActionsToBeUpdated, new Money(TestObjectFactory
						.getMFICurrency(), "700.0"), null, account
						.getPersonnel(), "423423", Short.valueOf("1"),
				sampleDate, sampleDate);
		account.applyPayment(paymentData);
		HibernateUtil.commitTransaction();

		assertEquals(1, account.getAccountPayments().size());
		AccountPaymentEntity payment = 
			account.getAccountPayments().iterator().next();
		assertEquals(4, payment.getAccountTrxns().size());
		// Should we assert something about each of those transactions?

		LoanSummaryEntity loanSummaryEntity = ((LoanBO) account)
				.getLoanSummary();
		assertEquals(loanSummaryEntity.getOriginalPrincipal().subtract(
				loanSummaryEntity.getPrincipalPaid()), ((LoanBO) account)
				.getLoanActivityDetails().iterator().next()
				.getPrincipalOutstanding());

	}

}
