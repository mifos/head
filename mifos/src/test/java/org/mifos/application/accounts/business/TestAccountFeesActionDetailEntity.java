package org.mifos.application.accounts.business;

import java.util.Date;

import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountFeesActionDetailEntity extends MifosTestCase {

	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	public void testMakeEarlyRepaymentEnteriesForFeePayment() {
		for (AccountActionDateEntity installment : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) installment;
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDateEntity
					.getAccountFeesActionDetails()) {
				accountFeesActionDetailEntity
						.makeRepaymentEnteries(LoanConstants.PAY_FEES_PENALTY_INTEREST);
				assertEquals(accountFeesActionDetailEntity.getFeeAmount(),
						accountFeesActionDetailEntity.getFeeAmountPaid());
			}
		}
	}

	public void testMakeEarlyRepaymentEnteriesForNotPayingFee() {
		for (AccountActionDateEntity installment : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) installment;
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDateEntity
					.getAccountFeesActionDetails()) {
				accountFeesActionDetailEntity
						.makeRepaymentEnteries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST);
				assertEquals(accountFeesActionDetailEntity.getFeeAmount(),
						accountFeesActionDetailEntity.getFeeAmountPaid());
			}
		}
	}


	public void testWaiveCharges() {
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());

		CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) group
				.getCustomerAccount().getAccountActionDates().toArray()[0];
		Money chargeWaived = new Money();
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDate
				.getAccountFeesActionDetails()) {
			chargeWaived = accountFeesActionDetailEntity.waiveCharges();
			assertEquals(new Money(), accountFeesActionDetailEntity
					.getFeeAmount());
		}
		assertEquals(new Money("100"), chargeWaived);
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(GroupBO.class, group.getCustomerId());
		center = TestObjectFactory.getObject(CenterBO.class, center.getCustomerId());
		accountBO=TestObjectFactory.getObject(LoanBO.class,accountBO.getAccountId());
	}

	private AccountBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", ApplicableTo.GROUPS,
				new Date(System.currentTimeMillis()), 
				PrdStatus.LOAN_ACTIVE,
				300.0, 1.2, 3, InterestType.FLAT, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, 
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
				new Date(System.currentTimeMillis()),
				loanOffering);
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

	@Override
	protected void setUp() throws Exception {
		accountBO = getLoanAccount();
		super.setUp();
	}

}
