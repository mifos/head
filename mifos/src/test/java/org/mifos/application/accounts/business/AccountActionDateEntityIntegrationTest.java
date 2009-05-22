/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.AccountIntegrationTest;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.CustomerAccountBOIntegrationTest;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AccountActionDateEntityIntegrationTest extends AccountIntegrationTest {

	public AccountActionDateEntityIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.00000001;

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
			assertEquals(100.0, principal.getAmount().doubleValue(), DELTA);
		}
	}

	public void testWaiveCharges() {
		StaticHibernateUtil.closeSession();
		group = TestObjectFactory.getGroup(group
				.getCustomerId());

		CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) group
				.getCustomerAccount().getAccountActionDates().toArray()[0];
		CustomerAccountBOIntegrationTest.setMiscFee(accountActionDate,new Money("20"));
		Money chargeWaived = CustomerAccountBOIntegrationTest.waiveCharges(accountActionDate);
		assertEquals(new Money(), accountActionDate.getMiscFee());
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDate
				.getAccountFeesActionDetails()) {
			assertEquals(new Money(), accountFeesActionDetailEntity
					.getFeeAmount());
		}
		assertEquals(new Money("120.0"), chargeWaived);
		StaticHibernateUtil.closeSession();
		group = TestObjectFactory.getGroup(group
				.getCustomerId());
		center = TestObjectFactory.getCenter(center
				.getCustomerId());
		accountBO = TestObjectFactory.getObject(LoanBO.class,
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
		group = TestObjectFactory.getGroup(group
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
				CustomerAccountBOIntegrationTest.applyPeriodicFees(accountActionDateEntity,accFeesEntity
						.getFees().getFeeId(), new Money("100"));
				break;
			}
		}
		TestObjectFactory.updateObject(group);
		TestObjectFactory.flushandCloseSession();

		group = TestObjectFactory.getGroup(group
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
		StaticHibernateUtil.closeSession();
		accountBO = TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
		group = TestObjectFactory.getGroup(group
				.getCustomerId());
	}
	
	/**
	 * Changes <em>all</em> installment dates to yesterday. In production,
	 * multiple installments should never have the same ACTION_DATE.
	 */
	public static void changeInstallmentDatesToPreviousDate(AccountBO accountBO) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			accountActionDateEntity.setActionDate(new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
		}
	}
	
	public static void changeInstallmentDatesToPreviousDateExceptLastInstallment(
			AccountBO accountBO,int noOfInstallmentsToBeChanged) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - 1);
		for (int i = 1; i <=noOfInstallmentsToBeChanged; i++) {
			AccountActionDateEntity accountActionDateEntity = accountBO
					.getAccountActionDate(Integer.valueOf(i).shortValue());
			accountActionDateEntity.setActionDate(new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
		}
	}

}
