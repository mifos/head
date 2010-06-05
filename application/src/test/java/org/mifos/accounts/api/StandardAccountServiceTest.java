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

package org.mifos.accounts.api;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanProductBuilder;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.collectionsheet.persistence.LoanAccountBuilder;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StandardAccountServiceTest {

    private StandardAccountService standardAccountService;

    @Mock
    private AccountPersistence accountPersistence;

    @Mock
    private CustomerPersistence customerPersistence;

    @Mock
    private ConfigurationPersistence configurationPersistence;

    @Mock
    private PersonnelPersistence personnelPersistence;

    @Mock
    private PersonnelBO personnelBO;

    @Mock
    private CustomerBO customerBO;

    @Mock
    private HibernateUtil hibernateUtil;

    @Mock
    private AccountBO someAccountBo;

    private LoanBO accountBO;

    @Mock
    private LoanPersistence loanPersistence;

    @Mock
    private AcceptedPaymentTypePersistence acceptedPaymentTypePersistence;

    @Mock
    private PersonnelDao personnelDao;

    @Before
    public void setup() {
        standardAccountService = new StandardAccountService(accountPersistence, loanPersistence,
                acceptedPaymentTypePersistence, personnelDao);
        Money.setDefaultCurrency(TestUtils.RUPEE);
        accountBO = new LoanAccountBuilder().withCustomer(customerBO).build();
        accountBO.setAccountPersistence(accountPersistence);
        accountBO.setCustomerPersistence(customerPersistence);
        accountBO.setConfigurationPersistence(configurationPersistence);
        accountBO.setPersonnelPersistence(personnelPersistence);
    }

    @Ignore
    @Test
    public void testMakeLoanPayment() throws PersistenceException, AccountException {
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
        when(accountPersistence.getAccount(accountId)).thenReturn(accountBO);
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
        List<AccountPaymentParametersDto> accountPaymentParametersDtoList = new ArrayList();
        AccountPaymentParametersDto dto1 = createAccountPaymentParametersDto(userId, accountId, paymentAmount);
        AccountPaymentParametersDto dto2 = createAccountPaymentParametersDto(userId, accountId, paymentAmount);
        accountPaymentParametersDtoList.add(dto1);
        accountPaymentParametersDtoList.add(dto2);

        try {
            StaticHibernateUtil.setHibernateUtil(hibernateUtil);
            StandardAccountService standardAccountServiceSpy = spy(standardAccountService);
            doNothing().when(standardAccountServiceSpy).makePaymentNoCommit((AccountPaymentParametersDto) any());

            standardAccountServiceSpy.makePayments(accountPaymentParametersDtoList);
            verify(standardAccountServiceSpy).makePaymentNoCommit(dto1);
            verify(standardAccountServiceSpy).makePaymentNoCommit(dto2);
        } finally {
            StaticHibernateUtil.setHibernateUtil(new HibernateUtil());
        }

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
        standardAccountService.disbursalAmountMatchesFullLoanAmount(disbursal, errors, mockLoanAccount);
        assertThat(errors.get(0), is(InvalidPaymentReason.INVALID_LOAN_DISBURSAL_AMOUNT));
    }

    @Test
    public void testDisbursalAmountScaleDifferenceDoesNotMatter() throws Exception {
        when(mockLoanAccount.getLoanAmount()).thenReturn(new Money(TestUtils.EURO, "300"));
        AccountPaymentParametersDto disbursal = new AccountPaymentParametersDto(new UserReferenceDto((short) 1),
                new AccountReferenceDto(1), new BigDecimal("300.0000000000000"), new LocalDate(), new PaymentTypeDto(
                        (short) 1, "CASH"), "");
        List<InvalidPaymentReason> errors = new ArrayList<InvalidPaymentReason>();
        standardAccountService.disbursalAmountMatchesFullLoanAmount(disbursal, errors, mockLoanAccount);
        assertThat(errors.isEmpty(), is(true));
    }

    @Test
    public void testLookupLoanAccountReferenceFromGlobalAccountNumber() throws Exception {
        String globalAccountNumber = "123456789012345";
        int accountId = 3;
        when(accountPersistence.findBySystemId(globalAccountNumber)).thenReturn(someAccountBo);
        when(someAccountBo.getAccountId()).thenReturn(accountId);
        AccountReferenceDto accountReferenceDto = standardAccountService
                .lookupLoanAccountReferenceFromGlobalAccountNumber(globalAccountNumber);
        assertThat(accountReferenceDto.getAccountId(), is(accountId));
    }

    @Test(expected = PersistenceException.class)
    public void testFailureOfLookupLoanAccountReferenceFromGlobalAccountNumber() throws Exception {
        String globalAccountNumber = "123456789012345";
        when(accountPersistence.findBySystemId(globalAccountNumber)).thenReturn(null);
        AccountReferenceDto accountReferenceDto = standardAccountService
                .lookupLoanAccountReferenceFromGlobalAccountNumber(globalAccountNumber);
    }
}
