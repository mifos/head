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

import java.math.BigDecimal;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.AccountIntegrationTestCase;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.dto.domain.UserReferenceDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.AuthenticationAuthorizationServiceFacade;
import org.springframework.beans.factory.annotation.Autowired;

public class StandardAccountServiceIntegrationTest extends AccountIntegrationTestCase {

    private AccountService standardAccountService;
    private PaymentTypeDto defaultPaymentType;

    @Autowired private AuthenticationAuthorizationServiceFacade authenticationAuthorizationService;
    
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        StaticHibernateUtil.startTransaction();
        standardAccountService = ApplicationContextProvider.getBean(AccountService.class);
        defaultPaymentType = standardAccountService.getLoanPaymentTypes().get(0);
        authenticationAuthorizationService.reloadUserDetailsForSecurityContext("mifos");
    }


    @Test
    public void testMakePaymentForLoanAccount() throws Exception {
        String payemntAmount = "700";
        CustomerDto clientDto = new CustomerDto();
        clientDto.setCustomerId(client.getCustomerId());
        LocalDate paymentDate = new LocalDate();
        LocalDate receiptDate = new LocalDate().minusDays(3);
        String receiptNumber = "AA/03/UX-9Q";

        AccountPaymentParametersDto loanPayment = new AccountPaymentParametersDto(new UserReferenceDto(
                groupLoan.getPersonnel().getPersonnelId()), new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(payemntAmount), paymentDate, defaultPaymentType, "comment",receiptDate, receiptNumber,
                clientDto);
        standardAccountService.makePayment(loanPayment);

        TestObjectFactory.updateObject(groupLoan);

        Assert.assertEquals("The amount returned for the payment should have been " + payemntAmount, Double
                .parseDouble(payemntAmount), groupLoan.getLastPmntAmnt());

        Assert.assertEquals(1, groupLoan.getAccountPayments().size());
        for (AccountPaymentEntity payment : groupLoan.getAccountPayments()) {

            Assert.assertEquals(TestUtils.createMoney(payemntAmount), payment.getAmount());
            Assert.assertEquals(paymentDate.toDateMidnight().toDate(), payment.getPaymentDate());
            Assert.assertEquals(defaultPaymentType.getName(), payment.getPaymentType().getName());
            Assert.assertEquals("comment", payment.getComment());
            Assert.assertEquals(groupLoan, payment.getAccount());
            Assert.assertEquals(groupLoan.getPersonnel(), payment.getCreatedByUser());
            Assert.assertEquals(receiptDate.toDateMidnight().toDate(), payment.getReceiptDate());
            Assert.assertEquals(receiptNumber, payment.getReceiptNumber());
            Assert.assertNull(payment.getCheckNumber());
            Assert.assertNull(payment.getBankName());
            Assert.assertNull(payment.getVoucherNumber());
            Assert.assertFalse(payment.isSavingsDeposit());
            Assert.assertFalse(payment.isSavingsWithdrawal());
            Assert.assertFalse(payment.isSavingsDepositOrWithdrawal());
            Assert.assertEquals(4, payment.getAccountTrxns().size());

            for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
                Assert.assertEquals(group.getCustomerId(), accountTrxn.getCustomer().getCustomerId());
            }
        }
    }

    @Test
    public void testMakePaymentForSavingsAccountOnDeposit() throws Exception {
        savingsBO = createClientSavingsAccount();
        String deposit = "400";
        CustomerDto clientDto = new CustomerDto();
        clientDto.setCustomerId(client.getCustomerId());
        //FIXME why one day in future payment is allowed
        LocalDate paymentDate = new LocalDate().plusDays(1);
        LocalDate receiptDate = new LocalDate().minusDays(3);
        String receiptNumber = "AA/03/UX-9Q";

        Assert.assertEquals(0, savingsBO.getAccountPayments().size());

        AccountPaymentParametersDto depositPayment = new AccountPaymentParametersDto(new UserReferenceDto(savingsBO
                .getPersonnel().getPersonnelId()), new AccountReferenceDto(savingsBO.getAccountId()), new BigDecimal(
                deposit), paymentDate, defaultPaymentType, "comment", receiptDate, receiptNumber,
                clientDto);
        standardAccountService.makePayment(depositPayment);

        TestObjectFactory.updateObject(savingsBO);

        Assert.assertEquals("The amount returned for the payment should have been " + deposit, Double
                .parseDouble(deposit), savingsBO.getLastPmntAmnt());

        Assert.assertEquals(1, savingsBO.getAccountPayments().size());
        for (AccountPaymentEntity payment : savingsBO.getAccountPayments()) {

            Assert.assertEquals(TestUtils.createMoney(deposit), payment.getAmount());
            Assert.assertEquals(1, payment.getAccountTrxns().size());
            Assert.assertEquals(paymentDate.toDateMidnight().toDate(), payment.getPaymentDate());
            Assert.assertEquals(defaultPaymentType.getName(), payment.getPaymentType().getName());
            Assert.assertEquals("comment", payment.getComment());
            Assert.assertEquals(savingsBO, payment.getAccount());
            Assert.assertEquals(savingsBO.getPersonnel(), payment.getCreatedByUser());
            Assert.assertEquals(receiptDate.toDateMidnight().toDate(), payment.getReceiptDate());
            Assert.assertEquals(receiptNumber, payment.getReceiptNumber());
            Assert.assertNull(payment.getCheckNumber());
            Assert.assertNull(payment.getBankName());
            Assert.assertNull(payment.getVoucherNumber());
            Assert.assertTrue(payment.isSavingsDeposit());
            Assert.assertFalse(payment.isSavingsWithdrawal());
            Assert.assertTrue(payment.isSavingsDepositOrWithdrawal());

            for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
                Assert.assertEquals(client.getCustomerId(), accountTrxn.getCustomer().getCustomerId());
            }
        }
    }

    @Test
    public void testValidateValidPayment() throws Exception {
        String paymentAmount = "10";

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(new UserReferenceDto(
                groupLoan.getPersonnel().getPersonnelId()), new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount), new LocalDate(), defaultPaymentType, "");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void testValidatePaymentWithInvalidDate() throws Exception {
        String paymentAmount = "700";

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(new UserReferenceDto(
                groupLoan.getPersonnel().getPersonnelId()), new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount), new LocalDate(1980, 1, 1), defaultPaymentType, "");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.INVALID_DATE));
    }

    @Test
    public void testMakePaymentComment() throws Exception {
        String paymentAmount = "700";
        String comment = "test comment";

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(new UserReferenceDto(
                groupLoan.getPersonnel().getPersonnelId()), new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount), new LocalDate(), defaultPaymentType, comment);
        standardAccountService.makePayment(accountPaymentParametersDto);

        TestObjectFactory.updateObject(groupLoan);
        Assert.assertEquals("We should get the comment back", comment, groupLoan.findMostRecentPaymentByPaymentDate().getComment());
    }

    @Test
    public void testLookupLoanIdFromExternalId() throws Exception {
        String externalId = "ABC";
        groupLoan.setExternalId(externalId);
        groupLoan.save();
        StaticHibernateUtil.flushSession();

        AccountReferenceDto accountReferenceDto = standardAccountService
                .lookupLoanAccountReferenceFromExternalId(externalId);

        Assert.assertEquals(groupLoan.getAccountId().intValue(), accountReferenceDto.getAccountId());
    }

    @Test
    public void testLookupLoanFromClientGovernmentIdAndLoanProductShortName() throws Exception {
        String clientGovernmentId = client.getGovernmentId();
        String loanProductShortName = clientLoan.getLoanOffering().getPrdOfferingShortName();
        AccountReferenceDto account = standardAccountService
                .lookupLoanAccountReferenceFromClientGovernmentIdAndLoanProductShortName(clientGovernmentId,
                        loanProductShortName);
        Assert.assertEquals(clientLoan.getAccountId().intValue(), account.getAccountId());
    }

    @Test
    public void testFailureOfLookupLoanFromClientGovernmentIdAndLoanProductShortName() throws Exception {
        try {
            standardAccountService.lookupLoanAccountReferenceFromClientGovernmentIdAndLoanProductShortName(client
                    .getGovernmentId(), "W");
            Assert.fail("expected PersistenceException");
        } catch (PersistenceException e) {
            Assert.assertEquals("loan not found for client government id 1034556 and loan product short name W", e
                    .getMessage());
        }
        try {
            standardAccountService.lookupLoanAccountReferenceFromClientGovernmentIdAndLoanProductShortName("1000556",
                    clientLoan.getLoanOffering().getPrdOfferingShortName());
            Assert.fail("expected PersistenceException");
        } catch (PersistenceException e) {
            Assert.assertEquals("loan not found for client government id 1000556 and loan product short name C", e
                    .getMessage());
        }
        try {
            standardAccountService.lookupLoanAccountReferenceFromClientGovernmentIdAndLoanProductShortName("1000556",
                    "W");
            Assert.fail("expected PersistenceException");
        } catch (PersistenceException e) {
            Assert.assertEquals("loan not found for client government id 1000556 and loan product short name W", e
                    .getMessage());
        }
    }

    @Test
    public void testLookupSavingsFromClientGovernmentIdAndSavingsProductShortName() throws Exception {
        savingsBO = createClientSavingsAccount();
        String clientGovernmentId = client.getGovernmentId();
        String savingsProductShortName = savingsBO.getSavingsOffering().getPrdOfferingShortName();
        AccountReferenceDto account = standardAccountService
                .lookupSavingsAccountReferenceFromClientGovernmentIdAndSavingsProductShortName(clientGovernmentId,
                        savingsProductShortName);
        Assert.assertEquals(savingsBO.getAccountId().intValue(), account.getAccountId());
    }

    @Test
    public void testFailureOfLookupSavingsFromClientGovernmentIdAndSavingsProductShortName() throws Exception {
        savingsBO = createClientSavingsAccount();
        try {
            standardAccountService.lookupSavingsAccountReferenceFromClientGovernmentIdAndSavingsProductShortName(client
                    .getGovernmentId(), "W");
            Assert.fail("expected PersistenceException");
        } catch (PersistenceException e) {
            Assert.assertEquals("savings not found for client government id 1034556 and savings product short name W",
                    e.getMessage());
        }
        try {
            standardAccountService.lookupSavingsAccountReferenceFromClientGovernmentIdAndSavingsProductShortName(
                    "1000556", savingsBO.getSavingsOffering().getPrdOfferingShortName());
            Assert.fail("expected PersistenceException");
        } catch (PersistenceException e) {
            Assert.assertEquals("savings not found for client government id 1000556 and savings product short name S",
                    e.getMessage());
        }
        try {
            standardAccountService.lookupSavingsAccountReferenceFromClientGovernmentIdAndSavingsProductShortName(
                    "1000556", "W");
            Assert.fail("expected PersistenceException");
        } catch (PersistenceException e) {
            Assert.assertEquals("savings not found for client government id 1000556 and savings product short name W",
                    e.getMessage());
        }
    }

    @Test
    public void testValidatePaymentWithInvalidPaymentType() throws Exception {
        String paymentAmount = "700";
        PaymentTypeDto invalidPaymentType = new PaymentTypeDto((short) -1, "pseudo payment type! Not cash, check, etc.");

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(new UserReferenceDto(
                groupLoan.getPersonnel().getPersonnelId()), new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount), new LocalDate(), invalidPaymentType, "");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.UNSUPPORTED_PAYMENT_TYPE));
    }

    @Test
    public void testValidatePaymentWithInvalidLargePaymentAmount() throws Exception {
        String paymentAmount = "700000";

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(new UserReferenceDto(
                groupLoan.getPersonnel().getPersonnelId()), new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount), new LocalDate(), defaultPaymentType, "");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.INVALID_PAYMENT_AMOUNT));
    }

    @Test
    public void testValidatePaymentWithInvalidNegativePaymentAmount() throws Exception {
        String paymentAmount = "-1";

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(new UserReferenceDto(
                groupLoan.getPersonnel().getPersonnelId()), new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount), new LocalDate(), defaultPaymentType, "");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.INVALID_PAYMENT_AMOUNT));
    }

    private SavingsBO createClientSavingsAccount() throws Exception {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(
                RecurrenceType.WEEKLY, TestObjectFactory.EVERY_WEEK, MeetingType.CUSTOMER_MEETING));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(
                RecurrenceType.WEEKLY, TestObjectFactory.EVERY_WEEK, MeetingType.CUSTOMER_MEETING));

        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("Savings", ApplicableTo.CLIENTS,
                new DateTimeService().getCurrentJavaDateTime(), PrdStatus.SAVINGS_ACTIVE, 300.0,
                RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 200.0, 200.0, SavingsType.VOLUNTARY,
                InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);

        return TestObjectFactory.createSavingsAccount("12345678910", client, AccountState.SAVINGS_ACTIVE,
                new DateTimeService().getCurrentJavaDateTime(), savingsOffering, TestUtils.makeUser());
    }
}
