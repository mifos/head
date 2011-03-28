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

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.application.collectionsheet.persistence.LoanAccountBuilder;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

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
    private AccountBO someAccountBo;

    @Mock
    private LoanPersistence loanPersistence;

    @Mock
    private AcceptedPaymentTypePersistence acceptedPaymentTypePersistence;

    @Mock
    private PersonnelDao personnelDao;
    @Mock
    private LoanBusinessService loanBusinessService;

    @Mock
    private CustomerDao customerDao;

    @Mock
    private HibernateTransactionHelper transactionHelper;

    @Mock
    LoanBO mockLoanAccount;

    @Before
    public void setup() {
        standardAccountService = new StandardAccountService(accountPersistence, loanPersistence,
                acceptedPaymentTypePersistence, personnelDao, customerDao, loanBusinessService, transactionHelper);
        Money.setDefaultCurrency(TestUtils.RUPEE);
        LoanBO accountBO = new LoanAccountBuilder().withCustomer(customerBO).build();
        accountBO.setAccountPersistence(accountPersistence);
    }

    @Test
    public void disburseLoan() throws Exception {
        LocalDate disbursalDate = new LocalDate(2011, 1, 10);
        PaymentTypeDto paymentTypeDto = new PaymentTypeDto((short) 1, PaymentTypes.CASH.toString());
        AccountPaymentParametersDto accountPaymentParameter = new AccountPaymentParametersDto((short)1, 1, BigDecimal.ONE, disbursalDate, paymentTypeDto, "");
        standardAccountService.disburseLoan(accountPaymentParameter, Locale.US);
    }

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
        standardAccountService.lookupLoanAccountReferenceFromGlobalAccountNumber(globalAccountNumber);
    }
}
