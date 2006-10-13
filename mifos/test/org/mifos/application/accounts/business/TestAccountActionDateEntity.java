/**
 * 
 */
package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
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

	public static void addAccountActionDate(
			AccountActionDateEntity accountAction, AccountBO account) {
		account.addAccountActionDate(accountAction);
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
				"Periodic Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("1"));

		AccountFeesEntity accountFeesEntity = new AccountFeesEntity(group
				.getCustomerAccount(), periodicFee, ((AmountFeeBO) periodicFee)
				.getFeeAmount().getAmountDoubleValue(), null, null, new Date(
				System.currentTimeMillis()));
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
						.getFees().getFeeId(), new Money("100"));
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
