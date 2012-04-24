/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static java.util.Collections.EMPTY_LIST;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.acceptedpaymenttype.persistence.LegacyAcceptedPaymentTypeDao;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTypeEntity;
import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.ScheduleCalculatorAdaptor;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.application.admin.servicefacade.MonthClosingServiceFacade;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WebTierAccountServiceFacadeTest {

    @Mock
    private ScheduleCalculatorAdaptor scheduleCalculatorAdaptor;

    @Mock
    private AccountBusinessService accountBusinessService;

    @Mock
    private LoanBO loanBO;

    @Mock
    private CustomerBO customerBO;

    @Mock
    private CustomerDto customerDto;

    @Mock
    private LegacyAcceptedPaymentTypeDao acceptedPaymentTypePersistence;

    @Mock
    private AccountTypeEntity accountTypeEntity;

    @Mock
    private LegacyPersonnelDao personnelPersistence;
    @Mock
    private LegacyAccountDao legacyAccountDao;
    @Mock
    private HibernateTransactionHelper transactionHelper;
    @Mock
    private UserContext userContext;
    @Mock
    private MonthClosingServiceFacade monthClosingServiceFacade;
    @Mock
    private ClientServiceFacade clientServiceFacade;
    @Mock
    private SavingsServiceFacade savingsServiceFacade;

    private WebTierAccountServiceFacade accountServiceFacade;
    private MifosCurrency rupee;
    private static final int LOAN_ID = 1;
    private static final int CUSTOMER_ID = 1;

    @Before
    public void setUp() throws Exception {
        accountServiceFacade = new WebTierAccountServiceFacade(null, transactionHelper,
                accountBusinessService, scheduleCalculatorAdaptor, acceptedPaymentTypePersistence, personnelPersistence,
                legacyAccountDao, monthClosingServiceFacade, clientServiceFacade, savingsServiceFacade){
            @Override
            void clearSessionAndRollback() {
                // do nothing
            }

            @Override
            UserContext getUserContext() {
                return userContext;
            }
        };
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAccountPaymentInformation() throws ServiceException, PersistenceException {
        Date paymentDate = TestUtils.getDate(12, 12, 2012);
        when(accountBusinessService.getAccount(LOAN_ID)).thenReturn(loanBO);
        when(accountTypeEntity.getAccountTypeId()).thenReturn((short)1);
        when(loanBO.getAccountType()).thenReturn(accountTypeEntity);
        when(loanBO.getTotalPaymentDue()).thenReturn(Money.zero(rupee));
        Date lastPaymentDate = TestUtils.getDate(12, 12, 2012);
        when(loanBO.findMostRecentNonzeroPaymentByPaymentDate()).thenReturn(new AccountPaymentEntity(null, null, null, null, null, lastPaymentDate));
        Short transactionId = Short.valueOf("2");
        when(acceptedPaymentTypePersistence.getAcceptedPaymentTypesForATransaction(TEST_LOCALE, transactionId)).thenReturn(EMPTY_LIST);
        when(loanBO.getCustomer()).thenReturn(customerBO);
        when(customerBO.toCustomerDto()).thenReturn(customerDto);
        when(customerDto.getCustomerId()).thenReturn(CUSTOMER_ID);
        AccountPaymentDto accountPaymentInformation = accountServiceFacade.getAccountPaymentInformation(LOAN_ID, Constants.LOAN, (short) 1, null, paymentDate);
        Assert.assertEquals(lastPaymentDate, accountPaymentInformation.getLastPaymentDate());
        verify(loanBO).findMostRecentNonzeroPaymentByPaymentDate();
        verify(scheduleCalculatorAdaptor, times(1)).computeExtraInterest(loanBO, paymentDate);
        verify(accountBusinessService, times(1)).getAccount(LOAN_ID);
        verify(accountTypeEntity, times(1)).getAccountTypeId();
        verify(loanBO, times(1)).getAccountType();
        verify(loanBO, times(1)).getTotalPaymentDue();
        verify(acceptedPaymentTypePersistence, times(1)).getAcceptedPaymentTypesForATransaction(TEST_LOCALE, transactionId);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetAccountPaymentInformationWhenPreviousPaymentsDoNotExist() throws ServiceException, PersistenceException {
        Date paymentDate = TestUtils.getDate(12, 12, 2012);
        when(accountBusinessService.getAccount(LOAN_ID)).thenReturn(loanBO);
        when(accountTypeEntity.getAccountTypeId()).thenReturn((short)1);
        when(loanBO.getAccountType()).thenReturn(accountTypeEntity);
        when(loanBO.getTotalPaymentDue()).thenReturn(Money.zero(rupee));
        when(loanBO.findMostRecentNonzeroPaymentByPaymentDate()).thenReturn(null);
        Short transactionId = Short.valueOf("2");
        when(acceptedPaymentTypePersistence.getAcceptedPaymentTypesForATransaction(TEST_LOCALE, transactionId)).thenReturn(EMPTY_LIST);
        when(loanBO.getCustomer()).thenReturn(customerBO);
        when(customerBO.toCustomerDto()).thenReturn(customerDto);
        when(customerDto.getCustomerId()).thenReturn(CUSTOMER_ID);
        AccountPaymentDto accountPaymentInformation = accountServiceFacade.getAccountPaymentInformation(LOAN_ID, Constants.LOAN, (short) 1, null, paymentDate);
        Assert.assertEquals(new Date(0), accountPaymentInformation.getLastPaymentDate());
        verify(loanBO).findMostRecentNonzeroPaymentByPaymentDate();
        verify(scheduleCalculatorAdaptor, times(1)).computeExtraInterest(loanBO, paymentDate);
        verify(accountBusinessService, times(1)).getAccount(LOAN_ID);
        verify(accountTypeEntity, times(1)).getAccountTypeId();
        verify(loanBO, times(1)).getAccountType();
        verify(loanBO, times(1)).getTotalPaymentDue();
        verify(acceptedPaymentTypePersistence, times(1)).getAcceptedPaymentTypesForATransaction(TEST_LOCALE, transactionId);
    }

    @Test
    public void shouldAdjustBackdatedPaymentMadeOnAccountIfAllowed() throws ServiceException, AccountException, PersistenceException {
        String globalAccountNum = "123";
        String adjustmentNote = "note";
        Short personnelId = Short.valueOf("1");
        PersonnelBO personnelBO = mock(PersonnelBO.class);
        PersonnelBO loanOfficer = mock(PersonnelBO.class);
        Date paymentDate = TestUtils.getDate(10, 10, 2010);
        new DateTimeService().setCurrentDateTime(TestUtils.getDateTime(11, 10, 2010));
        Short recordOfficeId = new Short("1");
        Short recordLoanOfficer = new Short("1");
        AccountPaymentEntity lastPmntToBeAdjusted = mock(AccountPaymentEntity.class);
        when(loanBO.getLastPmntToBeAdjusted()).thenReturn(lastPmntToBeAdjusted);
        when(loanBO.getOfficeId()).thenReturn(recordOfficeId);
        when(loanBO.getPersonnel()).thenReturn(loanOfficer);
        when(loanBO.getUserContext()).thenReturn(userContext);
        when(loanOfficer.getPersonnelId()).thenReturn(recordOfficeId);
        when(lastPmntToBeAdjusted.getPaymentDate()).thenReturn(paymentDate);
        when(accountBusinessService.findBySystemId(globalAccountNum)).thenReturn(loanBO);
        when(personnelPersistence.findPersonnelById(personnelId)).thenReturn(personnelBO);
        accountServiceFacade.applyAdjustment(globalAccountNum, adjustmentNote, personnelId);
        verify(accountBusinessService).findBySystemId(globalAccountNum);
        verify(personnelPersistence).findPersonnelById(personnelId);
        verify(loanBO).adjustLastPayment(adjustmentNote, personnelBO);
        verify(loanBO).setUserContext(userContext);
        verify(legacyAccountDao).createOrUpdate(loanBO);
        verify(transactionHelper).startTransaction();
        verify(transactionHelper).commitTransaction();
        verify(accountBusinessService).checkPermissionForAdjustmentOnBackDatedPayments(paymentDate, userContext,
                recordOfficeId, recordLoanOfficer);
        verify(lastPmntToBeAdjusted, times(2)).getPaymentDate();
    }

    @Test
    public void shouldNotAdjustBackdatedPaymentMadeOnAccountIfNotAllowed() throws ServiceException, AccountException, PersistenceException {
        String globalAccountNum = "123";
        String adjustmentNote = "note";
        Short personnelId = Short.valueOf("1");
        PersonnelBO personnelBO = mock(PersonnelBO.class);
        Date paymentDate = TestUtils.getDate(10, 10, 2010);
        new DateTimeService().setCurrentDateTime(TestUtils.getDateTime(11, 10, 2010));
        AccountPaymentEntity lastPmntToBeAdjusted = mock(AccountPaymentEntity.class);
        Short recordOfficeId = new Short("1");
        Short recordLoanOfficer = new Short("1");
        PersonnelBO loanOfficer = mock(PersonnelBO.class);

        when(loanBO.getPersonnel()).thenReturn(loanOfficer);
        when(loanBO.getUserContext()).thenReturn(userContext);
        when(loanOfficer.getPersonnelId()).thenReturn(recordOfficeId);
        when(loanBO.getOfficeId()).thenReturn(recordOfficeId);
        when(loanBO.getLastPmntToBeAdjusted()).thenReturn(lastPmntToBeAdjusted);
        when(lastPmntToBeAdjusted.getPaymentDate()).thenReturn(paymentDate);
        when(accountBusinessService.findBySystemId(globalAccountNum)).thenReturn(loanBO);
        when(personnelPersistence.findPersonnelById(personnelId)).thenReturn(personnelBO);
        doThrow(new ServiceException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED)).
                when(accountBusinessService).checkPermissionForAdjustmentOnBackDatedPayments(paymentDate, userContext,
                recordOfficeId,recordLoanOfficer);
        try {
            accountServiceFacade.applyAdjustment(globalAccountNum, adjustmentNote, personnelId);
        } catch (MifosRuntimeException e) {
            assertThat((ServiceException) e.getCause(), CoreMatchers.any(ServiceException.class));
            assertThat(((ServiceException) e.getCause()).getKey(), is(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED));
        }
        verify(accountBusinessService).findBySystemId(globalAccountNum);
        verify(lastPmntToBeAdjusted).getPaymentDate();
        verify(accountBusinessService).checkPermissionForAdjustmentOnBackDatedPayments(paymentDate, userContext, recordOfficeId, recordLoanOfficer);

        verify(personnelPersistence, never()).findPersonnelById(personnelId);
        verify(loanBO, never()).adjustLastPayment(anyString(), Matchers.<PersonnelBO>anyObject());
        verify(legacyAccountDao, never()).createOrUpdate(loanBO);
        verify(loanBO).setUserContext(userContext);
        verify(transactionHelper, never()).startTransaction();
        verify(transactionHelper, never()).commitTransaction();
        verify(transactionHelper).rollbackTransaction();
    }

}
