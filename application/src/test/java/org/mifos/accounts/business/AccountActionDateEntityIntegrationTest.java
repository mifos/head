/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.accounts.AccountIntegrationTestCase;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.customers.business.CustomerAccountBOTestUtils;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AccountActionDateEntityIntegrationTest extends AccountIntegrationTestCase {

    public AccountActionDateEntityIntegrationTest() throws Exception {
        super();
    }

    private static final double DELTA = 0.00000001;

    public void testGetPrincipal() {
        Set<AccountActionDateEntity> accountActionDates = groupLoan.getAccountActionDates();
        for (AccountActionDateEntity accountActionDate : accountActionDates) {
            Money principal = ((LoanScheduleEntity) accountActionDate).getPrincipal();
           Assert.assertEquals(100.0, principal.getAmount().doubleValue(), DELTA);
        }
    }

    public void testWaiveCharges() {
        StaticHibernateUtil.closeSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());

        CustomerScheduleEntity accountActionDate = (CustomerScheduleEntity) group.getCustomerAccount()
                .getAccountActionDates().toArray()[0];
        CustomerAccountBOTestUtils.setMiscFee(accountActionDate, new Money(getCurrency(), "20"));
        Money chargeWaived = CustomerAccountBOTestUtils.waiveCharges(accountActionDate);
       Assert.assertEquals(new Money(getCurrency()), accountActionDate.getMiscFee());
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDate
                .getAccountFeesActionDetails()) {
           Assert.assertEquals(new Money(getCurrency()), accountFeesActionDetailEntity.getFeeAmount());
        }
       Assert.assertEquals(new Money(getCurrency(), "120.0"), chargeWaived);
        StaticHibernateUtil.closeSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        groupLoan = TestObjectFactory.getObject(LoanBO.class, groupLoan.getAccountId());
    }

    public void testApplyPeriodicFees() {
        FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));

        AccountFeesEntity accountFeesEntity = new AccountFeesEntity(group.getCustomerAccount(), periodicFee,
                ((AmountFeeBO) periodicFee).getFeeAmount().getAmountDoubleValue(), null, null, new Date(System
                        .currentTimeMillis()));
        group.getCustomerAccount().addAccountFees(accountFeesEntity);
        TestObjectFactory.updateObject(group);

        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());

        CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) group.getCustomerAccount()
                .getAccountActionDates().toArray()[0];

        Set<AccountFeesActionDetailEntity> feeDetailsSet = accountActionDateEntity.getAccountFeesActionDetails();
        List<Integer> feeList = new ArrayList<Integer>();
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : feeDetailsSet) {
            feeList.add(accountFeesActionDetailEntity.getAccountFeesActionDetailId());
        }
        Set<AccountFeesEntity> accountFeeSet = group.getCustomerAccount().getAccountFees();
        for (AccountFeesEntity accFeesEntity : accountFeeSet) {
            if (accFeesEntity.getFees().getFeeName().equalsIgnoreCase("Periodic Fee")) {
                CustomerAccountBOTestUtils.applyPeriodicFees(accountActionDateEntity, accFeesEntity.getFees()
                        .getFeeId(), new Money(getCurrency(), "100"));
                break;
            }
        }
        TestObjectFactory.updateObject(group);
        TestObjectFactory.flushandCloseSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        CustomerScheduleEntity firstInstallment = (CustomerScheduleEntity) group.getCustomerAccount()
                .getAccountActionDates().toArray()[0];
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : firstInstallment
                .getAccountFeesActionDetails()) {
            if (!feeList.contains(accountFeesActionDetailEntity.getAccountFeesActionDetailId())) {
               Assert.assertEquals("Periodic Fee", accountFeesActionDetailEntity.getFee().getFeeName());
                break;
            }
        }
        StaticHibernateUtil.closeSession();
        groupLoan = TestObjectFactory.getObject(LoanBO.class, groupLoan.getAccountId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

}
