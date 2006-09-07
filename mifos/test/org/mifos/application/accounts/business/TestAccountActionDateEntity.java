/**
 * 
 */
package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountActionDateEntity extends TestAccount {

	private TestObjectPersistence testObjectPersistence;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		testObjectPersistence = new TestObjectPersistence();
	}

	public void testMakeEarlyRepaymentEnteriesForFeePayment() {
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			accountActionDateEntity
					.makeEarlyRepaymentEnteries(LoanConstants.PAY_FEES_PENALTY_INTEREST);
			assertEquals(accountActionDateEntity.getPrincipal(),
					accountActionDateEntity.getPrincipalPaid());
			assertEquals(accountActionDateEntity.getInterest(),
					accountActionDateEntity.getInterestPaid());
			assertEquals(accountActionDateEntity.getPenalty(),
					accountActionDateEntity.getPenaltyPaid());
			assertEquals(accountActionDateEntity.getMiscFee(),
					accountActionDateEntity.getMiscFeePaid());
			assertEquals(accountActionDateEntity.getPaymentStatus(), Short
					.valueOf("1"));
		}
	}

	public void testMakeEarlyRepaymentEnteriesForNotPayingFee() {
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			accountActionDateEntity
					.makeEarlyRepaymentEnteries(LoanConstants.DONOT_PAY_FEES_PENALTY_INTEREST);
			assertEquals(accountActionDateEntity.getPrincipal(),
					accountActionDateEntity.getPrincipalPaid());
			assertEquals(accountActionDateEntity.getInterest(),
					accountActionDateEntity.getInterestPaid());
			assertEquals(accountActionDateEntity.getPenalty(),
					accountActionDateEntity.getPenaltyPaid());
			assertEquals(accountActionDateEntity.getMiscFee(),
					accountActionDateEntity.getMiscFeePaid());
			assertEquals(accountActionDateEntity.getPaymentStatus(), Short
					.valueOf("1"));
		}
	}

	public void testSuccessRemoveFees() {
		Short feeId = null;
		Set<AccountFeesEntity> accountFeesSet = accountBO.getAccountFees();
		for (AccountFeesEntity accountFeesEntity : accountFeesSet) {
			feeId = accountFeesEntity.getFees().getFeeId();
			break;
		}
		Set<AccountActionDateEntity> accountActionDateEntitySet = accountBO
				.getAccountActionDates();
		Iterator itr = accountActionDateEntitySet.iterator();
		while (itr.hasNext()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) itr
					.next();
			accountActionDateEntity.removeFees(feeId);
			assertTrue(true);
		}
	}

	public void testGetPrincipal() {
		Set<AccountActionDateEntity> accountActionDates = accountBO
				.getAccountActionDates();
		for (AccountActionDateEntity accountActionDate : accountActionDates) {
			Money principal = ((LoanScheduleEntity) accountActionDate)
					.getPrincipal();
			assertEquals(100.0, principal.getAmount().doubleValue());
		}
	}

	public void testGetPrincipalDue() {
		Money principal = null;
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPrincipalPaid(new Money("10.0"));
		assertEquals(90.0, accountActionDate.getPrincipalDue()
				.getAmountDoubleValue());

	}

	public void testGetInterestDue() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setInterestPaid(new Money("2.0"));
		assertEquals(10.0, accountActionDate.getInterestDue()
				.getAmountDoubleValue());

	}

	public void testGetPenaltyDue() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPenalty(new Money("20.0"));
		accountActionDate.setPenaltyPaid(new Money("5.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		assertEquals(25.0, accountActionDate.getPenaltyDue()
				.getAmountDoubleValue());

	}

	public void testGetTotalDue() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPrincipalPaid(new Money("10.0"));
		accountActionDate.setInterestPaid(new Money("2.0"));
		accountActionDate.setPenalty(new Money("20.0"));
		accountActionDate.setPenaltyPaid(new Money("5.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		accountActionDate.setMiscFee(new Money("20.0"));
		accountActionDate.setMiscFeePaid(new Money("5.0"));
		assertEquals(140.0, accountActionDate.getTotalDue()
				.getAmountDoubleValue());

	}

	public void testGetTotalDueWithFees() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPrincipalPaid(new Money("10.0"));
		accountActionDate.setInterestPaid(new Money("2.0"));
		accountActionDate.setPenalty(new Money("20.0"));
		accountActionDate.setPenaltyPaid(new Money("5.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		accountActionDate.setMiscFee(new Money("20.0"));
		accountActionDate.setMiscFeePaid(new Money("5.0"));
		assertEquals(240.0, accountActionDate.getTotalDueWithFees()
				.getAmountDoubleValue());

	}

	public void testGetDueAmounts() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPrincipalPaid(new Money("10.0"));
		accountActionDate.setInterestPaid(new Money("2.0"));
		accountActionDate.setPenalty(new Money("20.0"));
		accountActionDate.setPenaltyPaid(new Money("5.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		accountActionDate.setMiscFee(new Money("20.0"));
		accountActionDate.setMiscFeePaid(new Money("5.0"));
		assertEquals(115.0, accountActionDate.getDueAmnts().getFeesOverdue()
				.getAmountDoubleValue());

	}

	public void testGetTotalDueAmounts() {
		LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountBO
				.getAccountActionDates().toArray()[0];
		accountActionDate.setPrincipalPaid(new Money("10.0"));
		accountActionDate.setInterestPaid(new Money("2.0"));
		accountActionDate.setPenalty(new Money("20.0"));
		accountActionDate.setPenaltyPaid(new Money("5.0"));
		accountActionDate.setMiscPenalty(new Money("10.0"));
		accountActionDate.setMiscFee(new Money("20.0"));
		accountActionDate.setMiscFeePaid(new Money("5.0"));
		OverDueAmounts totalDue = accountActionDate.getDueAmnts();
		assertEquals(115.0, totalDue.getFeesOverdue().getAmountDoubleValue());
		assertEquals(90.0, totalDue.getPrincipalOverDue()
				.getAmountDoubleValue());
		assertEquals(10.0, totalDue.getInterestOverdue().getAmountDoubleValue());
		assertEquals(25.0, totalDue.getPenaltyOverdue().getAmountDoubleValue());

	}

	public void testWaiveCharges() {
		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());

		CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) group
				.getCustomerAccount().getAccountActionDates().toArray()[0];
		accountActionDate.setMiscFee(new Money("20"));
		Money chargeWaived = accountActionDate.waiveCharges();
		assertEquals(new Money(), accountActionDate.getMiscFee());
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDate
				.getAccountFeesActionDetails()) {
			assertEquals(new Money(), accountFeesActionDetailEntity
					.getFeeAmount());
		}
		assertEquals(new Money("120.0"), chargeWaived);
		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
	}

	public void testApplyPeriodicFees() {
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100",
				MeetingFrequency.WEEKLY, Short.valueOf("1"));

		AccountFeesEntity accountFeesEntity = new AccountFeesEntity(group.getCustomerAccount(),periodicFee,((AmountFeeBO) periodicFee)
				.getFeeAmount().getAmountDoubleValue(),null,null,new Date(System
						.currentTimeMillis()));
		group.getCustomerAccount().addAccountFees(accountFeesEntity);
		TestObjectFactory.updateObject(group);

		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());

		CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) group
				.getCustomerAccount().getAccountActionDates().toArray()[0];

		Set<AccountFeesActionDetailEntity> feeDetailsSet = accountActionDateEntity
				.getAccountFeesActionDetails();
		List<Integer> feeList = new ArrayList<Integer>();
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : feeDetailsSet) {
			feeList.add(accountFeesActionDetailEntity
					.getAccountFeesActionDetailId());
		}
		Set<AccountFeesEntity> accountFeeSet = group.getCustomerAccount()
				.getAccountFees();
		for (AccountFeesEntity accFeesEntity : accountFeeSet) {
			if (accFeesEntity.getFees().getFeeName().equalsIgnoreCase(
					"Periodic Fee")) {
				accountActionDateEntity.applyPeriodicFees(accFeesEntity
						.getFees().getFeeId(),new Money("100"));
				break;
			}
		}
		TestObjectFactory.updateObject(group);
		TestObjectFactory.flushandCloseSession();

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		CustomerScheduleEntity firstInstallment = (CustomerScheduleEntity) group
				.getCustomerAccount().getAccountActionDates().toArray()[0];
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : firstInstallment
				.getAccountFeesActionDetails()) {
			if (!feeList.contains(accountFeesActionDetailEntity
					.getAccountFeesActionDetailId())) {
				assertEquals("Periodic Fee", accountFeesActionDetailEntity
						.getFee().getFeeName());
				break;
			}
		}
		HibernateUtil.closeSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
	}
}
