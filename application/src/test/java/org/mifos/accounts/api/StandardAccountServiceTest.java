/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.accounts.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.acceptedpaymenttype.persistence.LegacyAcceptedPaymentTypeDao;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.application.admin.servicefacade.MonthClosingServiceFacade;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.servicefacade.GroupLoanAccountServiceFacade;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.domain.builders.LoanAccountBuilder;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StandardAccountServiceTest {

    private StandardAccountService standardAccountService;

    @Mock
    private LegacyAccountDao legacyAccountDao;

    @Mock
    private LegacyMasterDao legacyMasterDao;

    @Mock
    private CustomerPersistence customerPersistence;

    @Mock
    private ConfigurationPersistence configurationPersistence;

    @Mock
    private LegacyPersonnelDao personnelPersistence;

    @Mock
    private PersonnelBO personnelBO;

    @Mock
    private CustomerBO customerBO;

    @Mock
    private AccountBO someAccountBo;

    private LoanBO accountBO;

    @Mock
    private LegacyLoanDao legacyLoanDao;

    @Mock
    private LegacyAcceptedPaymentTypeDao acceptedPaymentTypePersistence;

    @Mock
    private PersonnelDao personnelDao;

    @Mock
    private LoanBusinessService loanBusinessService;

    @Mock
    private MonthClosingServiceFacade monthClosingServiceFacade;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private HibernateTransactionHelper transactionHelper;

    @Mock
    private SavingsServiceFacade savingsServiceFacade;
    
    @Mock
    private GroupLoanAccountServiceFacade groupLoanAccountServiceFacade;

    @Before
    public void setup() {
        standardAccountService = new StandardAccountService(legacyAccountDao,
                legacyLoanDao,acceptedPaymentTypePersistence, personnelDao,
                customerDao, loanBusinessService, transactionHelper, legacyMasterDao,
                monthClosingServiceFacade, savingsServiceFacade, groupLoanAccountServiceFacade);
        Money.setDefaultCurrency(TestUtils.RUPEE);
        accountBO = new LoanAccountBuilder().withCustomer(customerBO).build();
        accountBO.setlegacyAccountDao(legacyAccountDao);
    }

    @Ignore
    @Test
    public void testMakeLoanPayment() throws Exception {
        short userId = 1;
        int accountId = 100;
        int customerId = 1;
        UserReferenceDto userMakingPayment = new UserReferenceDto(userId);
        AccountReferenceDto loanAccount = new AccountReferenceDto(accountId);
        BigDecimal paymentAmount = new BigDecimal("100");
        LocalDate paymentDate = new LocalDate(2009, 10, 1);
        String comment = "some comment";
        java.sql.Date lastMeetingDate = new java.sql.Date(paymentDate.minusWeeks(3).toDateMidnight().getMillis());

        when(customerBO.getCustomerId()).thenReturn(customerId);
        when(legacyAccountDao.getAccount(accountId)).thenReturn(accountBO);
        // when(accountBO.isTrxnDateValid(paymentDate.toDateMidnight().toDate())).thenReturn(true);
        when(configurationPersistence.isRepaymentIndepOfMeetingEnabled()).thenReturn(false);
        when(customerPersistence.getLastMeetingDateForCustomer(anyInt())).thenReturn(lastMeetingDate);
        when(personnelPersistence.getPersonnel(anyShort())).thenReturn(personnelBO);

        // FIXME - work in progress Vanmh
        // standardLoanAccountService.makeLoanPayment(new AccountPaymentParametersDTO(userMakingPayment, loanAccount,
        // paymentAmount, paymentDate, PaymentTypeDTO.CASH, comment));
    }

    private AccountPaymentParametersDto createAccountPaymentParametersDto(short userId, int accountId,
            String paymentAmount) {
        AccountPaymentParametersDto accountPaymentParametersDTO = new AccountPaymentParametersDto(new UserReferenceDto(
                userId), new AccountReferenceDto(accountId), new BigDecimal(paymentAmount), new LocalDate(),
                new PaymentTypeDto((short) 1, "CASH"), "");

        return accountPaymentParametersDTO;
    }

    /*
     * IGNORING as is not running as unit tests as it accesses hibernate..
     *
     * Make sure that a payment is made for each DTO passed in
     */
    @Ignore
    @Test
    public void testMakeLoanPayments() throws Exception {
        short userId = 1;
        int accountId = 100;
        String paymentAmount = "100";
        List<AccountPaymentParametersDto> accountPaymentParametersDtoList = new ArrayList<AccountPaymentParametersDto>();
        AccountPaymentParametersDto dto1 = createAccountPaymentParametersDto(userId, accountId, paymentAmount);
        AccountPaymentParametersDto dto2 = createAccountPaymentParametersDto(userId, accountId, paymentAmount);
        accountPaymentParametersDtoList.add(dto1);
        accountPaymentParametersDtoList.add(dto2);

        StandardAccountService standardAccountServiceSpy = spy(standardAccountService);
        doNothing().when(standardAccountServiceSpy).makePaymentNoCommit((AccountPaymentParametersDto) any());

        standardAccountServiceSpy.makePayments(accountPaymentParametersDtoList);
        verify(standardAccountServiceSpy).makePaymentNoCommit(dto1);
        verify(standardAccountServiceSpy).makePaymentNoCommit(dto2);

    }

    @Mock
    LoanBO mockLoanAccount;

    @Test
    public void testDisbursalAmountMustMatchFullLoanAmount() throws Exception {
        when(mockLoanAccount.getLoanAmount()).thenReturn(new Money(TestUtils.EURO, "300"));
        AccountPaymentParametersDto disbursal = new AccountPaymentParametersDto(new UserReferenceDto((short) 1),
                new AccountReferenceDto(1), new BigDecimal("299"), new LocalDate(), new PaymentTypeDto(
                        (short) 1, "CASH"), "");
        List<InvalidPaymentReason> errors = new ArrayList<InvalidPaymentReason>();
        standardAccountService.disbursalAmountMatchesFullLoanAmount(disbursal.getPaymentAmount(), errors, mockLoanAccount);
        assertThat(errors.get(0), is(InvalidPaymentReason.INVALID_LOAN_DISBURSAL_AMOUNT));
    }

    @Test
    public void testDisbursalAmountScaleDifferenceDoesNotMatter() throws Exception {
        when(mockLoanAccount.getLoanAmount()).thenReturn(new Money(TestUtils.EURO, "300"));
        AccountPaymentParametersDto disbursal = new AccountPaymentParametersDto(new UserReferenceDto((short) 1),
                new AccountReferenceDto(1), new BigDecimal("300.0000000000000"), new LocalDate(), new PaymentTypeDto(
                        (short) 1, "CASH"), "");
        List<InvalidPaymentReason> errors = new ArrayList<InvalidPaymentReason>();
        standardAccountService.disbursalAmountMatchesFullLoanAmount(disbursal.getPaymentAmount(), errors, mockLoanAccount);
        assertThat(errors.isEmpty(), is(true));
    }

    @Test
    public void testLookupLoanAccountReferenceFromGlobalAccountNumber() throws Exception {
        String globalAccountNumber = "123456789012345";
        int accountId = 3;
        when(legacyAccountDao.findBySystemId(globalAccountNumber)).thenReturn(someAccountBo);
        when(someAccountBo.getAccountId()).thenReturn(accountId);
        AccountReferenceDto accountReferenceDto = standardAccountService
                .lookupLoanAccountReferenceFromGlobalAccountNumber(globalAccountNumber);
        assertThat(accountReferenceDto.getAccountId(), is(accountId));
    }

    @Test(expected = PersistenceException.class)
    public void testFailureOfLookupLoanAccountReferenceFromGlobalAccountNumber() throws Exception {
        String globalAccountNumber = "123456789012345";
        when(legacyAccountDao.findBySystemId(globalAccountNumber)).thenReturn(null);

        standardAccountService.lookupLoanAccountReferenceFromGlobalAccountNumber(globalAccountNumber);
    }
}
