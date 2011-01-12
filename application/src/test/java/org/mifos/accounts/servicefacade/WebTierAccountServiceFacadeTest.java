/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.accounts.servicefacade;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTypeEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;

import static java.util.Collections.EMPTY_LIST;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebTierAccountServiceFacadeTest {

    @Mock
    private ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;

    @Mock
    private AccountBusinessService accountBusinessService;

    @Mock
    private LoanBO loanBO;

    @Mock
    private AcceptedPaymentTypePersistence acceptedPaymentTypePersistence;

    @Mock
    private AccountTypeEntity accountTypeEntity;

    @Mock
    private PersonnelPersistence personnelPersistence;
    @Mock
    private AccountPersistence accountPersistence;
    @Mock

    private HibernateTransactionHelper transactionHelper;
    private WebTierAccountServiceFacade accountServiceFacade;
    private MifosCurrency rupee;
    private static final int LOAN_ID = 1;

    @Before
    public void setUp() throws Exception {
        accountServiceFacade = new WebTierAccountServiceFacade(null, transactionHelper,
                accountBusinessService, scheduleCalculatorAdaptor, acceptedPaymentTypePersistence, personnelPersistence, accountPersistence){
            @Override
            void clearSessionAndRollback() {
                // do nothing
            }
        };
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
    }

    @Test
    public void testGetAccountPaymentInformation() throws ServiceException, PersistenceException {
        Date paymentDate = TestUtils.getDate(12, 12, 2012);
        when(accountBusinessService.getAccount(LOAN_ID)).thenReturn(loanBO);
        when(accountTypeEntity.getAccountTypeId()).thenReturn((short)1);
        when(loanBO.getAccountType()).thenReturn(accountTypeEntity);
        when(loanBO.getTotalPaymentDue()).thenReturn(Money.zero(rupee));
        Date lastPaymentDate = TestUtils.getDate(12, 12, 2012);
        when(loanBO.getLastPmnt()).thenReturn(new AccountPaymentEntity(null, null, null, null, null, lastPaymentDate));
        Short transactionId = Short.valueOf("2");
        when(acceptedPaymentTypePersistence.getAcceptedPaymentTypesForATransaction(TEST_LOCALE, transactionId)).thenReturn(EMPTY_LIST);
        AccountPaymentDto accountPaymentInformation = accountServiceFacade.getAccountPaymentInformation(LOAN_ID, Constants.LOAN, (short) 1, null, paymentDate);
        Assert.assertEquals(lastPaymentDate, accountPaymentInformation.getLastPaymentDate());
        verify(loanBO).getLastPmnt();
        verify(scheduleCalculatorAdaptor, times(1)).computeExtraInterest(loanBO, paymentDate);
        verify(accountBusinessService, times(1)).getAccount(LOAN_ID);
        verify(accountTypeEntity, times(1)).getAccountTypeId();
        verify(loanBO, times(1)).getAccountType();
        verify(loanBO, times(1)).getTotalPaymentDue();
        verify(acceptedPaymentTypePersistence, times(1)).getAcceptedPaymentTypesForATransaction(TEST_LOCALE, transactionId);
    }
    
    @Test
    public void testGetAccountPaymentInformationWhenPreviousPaymentsDoNotExist() throws ServiceException, PersistenceException {
        Date paymentDate = TestUtils.getDate(12, 12, 2012);
        when(accountBusinessService.getAccount(LOAN_ID)).thenReturn(loanBO);
        when(accountTypeEntity.getAccountTypeId()).thenReturn((short)1);
        when(loanBO.getAccountType()).thenReturn(accountTypeEntity);
        when(loanBO.getTotalPaymentDue()).thenReturn(Money.zero(rupee));
        when(loanBO.getLastPmnt()).thenReturn(null);
        Short transactionId = Short.valueOf("2");
        when(acceptedPaymentTypePersistence.getAcceptedPaymentTypesForATransaction(TEST_LOCALE, transactionId)).thenReturn(EMPTY_LIST);
        AccountPaymentDto accountPaymentInformation = accountServiceFacade.getAccountPaymentInformation(LOAN_ID, Constants.LOAN, (short) 1, null, paymentDate);
        Assert.assertEquals(new Date(0), accountPaymentInformation.getLastPaymentDate());
        verify(loanBO).getLastPmnt();
        verify(scheduleCalculatorAdaptor, times(1)).computeExtraInterest(loanBO, paymentDate);
        verify(accountBusinessService, times(1)).getAccount(LOAN_ID);
        verify(accountTypeEntity, times(1)).getAccountTypeId();
        verify(loanBO, times(1)).getAccountType();
        verify(loanBO, times(1)).getTotalPaymentDue();
        verify(acceptedPaymentTypePersistence, times(1)).getAcceptedPaymentTypesForATransaction(TEST_LOCALE, transactionId);
    }

    @Test
    public void shouldAdjustLastPaymentMadeOnAccount() throws ServiceException, AccountException, PersistenceException {
        String globalAccountNum = "123";
        String adjustmentNote = "note";
        Short personnelId = Short.valueOf("1");
        PersonnelBO personnelBO = mock(PersonnelBO.class);
        when(accountBusinessService.findBySystemId(globalAccountNum)).thenReturn(loanBO);
        when(personnelPersistence.findPersonnelById(personnelId)).thenReturn(personnelBO);
        accountServiceFacade.applyAdjustment(globalAccountNum, adjustmentNote, personnelId);
        verify(accountBusinessService).findBySystemId(globalAccountNum);
        verify(personnelPersistence).findPersonnelById(personnelId);
        verify(loanBO).adjustLastPayment(adjustmentNote, personnelBO);
        verify(accountPersistence).createOrUpdate(loanBO);
        verify(transactionHelper).startTransaction();
        verify(transactionHelper).commitTransaction();
    }
}
