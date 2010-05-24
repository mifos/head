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

import java.math.BigDecimal;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.mifos.accounts.AccountIntegrationTestCase;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.persistence.AccountPersistence;
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
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class StandardAccountServiceIntegrationTest extends AccountIntegrationTestCase {

    private StandardAccountService standardAccountService;
    private List<PaymentTypeDto> paymentTypeDtos;
    private PaymentTypeDto defaultPaymentType;

    public StandardAccountServiceIntegrationTest() throws Exception {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        standardAccountService = new StandardAccountService(new AccountPersistence(),
                new LoanPersistence(), new AcceptedPaymentTypePersistence());
        paymentTypeDtos = standardAccountService.getLoanPaymentTypes();
        defaultPaymentType = paymentTypeDtos.get(0);
        StaticHibernateUtil.commitTransaction();
    }

    public void testMakePayment() throws Exception {
        String paymentAmount = "700";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(groupLoan.getPersonnel().getPersonnelId()),
                new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount),
                new LocalDate(),
                defaultPaymentType,"");
        standardAccountService.makePayment(accountPaymentParametersDto);

        TestObjectFactory.updateObject(groupLoan);
        Assert.assertEquals("The amount returned for the payment should have been " + paymentAmount,
                Double.parseDouble(paymentAmount), groupLoan.getLastPmntAmnt());
    }

    public void testValidateValidPayment() throws Exception {
        String paymentAmount = "10";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(groupLoan.getPersonnel().getPersonnelId()),
                new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount),
                new LocalDate(),
                defaultPaymentType,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertEquals(0, errors.size());
    }

    public void testValidatePaymentWithInvalidDate() throws Exception {
        String paymentAmount = "700";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(groupLoan.getPersonnel().getPersonnelId()),
                new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount),
                new LocalDate(1980, 1, 1),
                defaultPaymentType,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.INVALID_DATE));
    }

    public void testMakePaymentComment() throws Exception {
        String paymentAmount = "700";
        String comment = "test comment";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(groupLoan.getPersonnel().getPersonnelId()),
                new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount),
                new LocalDate(),
                defaultPaymentType,
                comment);
        standardAccountService.makePayment(accountPaymentParametersDto);

        TestObjectFactory.updateObject(groupLoan);
        Assert.assertEquals("We should get the comment back",
                comment, groupLoan.getLastPmnt().getComment());
    }

    public void testMakeLoanDisbursements() throws Exception {
        Assert.fail("unimplemented");
    }

    public void testValidateDisbursement() throws Exception {
        Assert.fail("unimplemented");
    }

    public void testLookupLoanIdFromExternalId() throws Exception {
        String externalId = "ABC";
        groupLoan.setExternalId(externalId);
        groupLoan.save();
        StaticHibernateUtil.commitTransaction();

        standardAccountService.setAccountPersistence(new AccountPersistence());
        standardAccountService.setLoanPersistence(new LoanPersistence());

        AccountReferenceDto accountReferenceDto = standardAccountService.lookupLoanAccountReferenceFromExternalId(externalId);

        Assert.assertEquals(groupLoan.getAccountId().intValue(), accountReferenceDto.getAccountId());
    }

    public void testLookupLoanFromClientGovernmentIdAndLoanProductShortName() throws Exception {
        String clientGovernmentId = client.getGovernmentId();
        String loanProductShortName = clientLoan.getLoanOffering().getPrdOfferingShortName();
        AccountReferenceDto account = standardAccountService
                .lookupLoanAccountReferenceFromClientGovernmentIdAndLoanProductShortName(clientGovernmentId,
                        loanProductShortName);
        Assert.assertEquals(clientLoan.getAccountId().intValue(), account.getAccountId());
    }

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

    public void testLookupSavingsFromClientGovernmentIdAndSavingsProductShortName() throws Exception {
        savingsBO = createSavingsAccount();
        String clientGovernmentId = client.getGovernmentId();
        String savingsProductShortName = savingsBO.getSavingsOffering().getPrdOfferingShortName();
        AccountReferenceDto account = standardAccountService
                .lookupSavingsAccountReferenceFromClientGovernmentIdAndSavingsProductShortName(clientGovernmentId,
                        savingsProductShortName);
        Assert.assertEquals(savingsBO.getAccountId().intValue(), account.getAccountId());
    }

    public void testFailureOfLookupSavingsFromClientGovernmentIdAndSavingsProductShortName() throws Exception {
        savingsBO = createSavingsAccount();
        try {
            standardAccountService.lookupSavingsAccountReferenceFromClientGovernmentIdAndSavingsProductShortName(client
                    .getGovernmentId(), "W");
            Assert.fail("expected PersistenceException");
        } catch (PersistenceException e) {
            Assert.assertEquals("savings not found for client government id 1034556 and savings product short name W", e
                    .getMessage());
        }
        try {
            standardAccountService.lookupSavingsAccountReferenceFromClientGovernmentIdAndSavingsProductShortName("1000556",
                    savingsBO.getSavingsOffering().getPrdOfferingShortName());
            Assert.fail("expected PersistenceException");
        } catch (PersistenceException e) {
            Assert.assertEquals("savings not found for client government id 1000556 and savings product short name S", e
                    .getMessage());
        }
        try {
            standardAccountService.lookupSavingsAccountReferenceFromClientGovernmentIdAndSavingsProductShortName("1000556",
                    "W");
            Assert.fail("expected PersistenceException");
        } catch (PersistenceException e) {
            Assert.assertEquals("savings not found for client government id 1000556 and savings product short name W", e
                    .getMessage());
        }
    }

    public void testValidatePaymentWithInvalidPaymentType() throws Exception {
        String paymentAmount = "700";
        standardAccountService.setAccountPersistence(new AccountPersistence());
        PaymentTypeDto invalidPaymentType = new PaymentTypeDto((short)-1, "pseudo payment type! Not cash, check, etc.");

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(groupLoan.getPersonnel().getPersonnelId()),
                new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount),
                new LocalDate(),
                invalidPaymentType,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.UNSUPPORTED_PAYMENT_TYPE));
    }

    public void testValidatePaymentWithInvalidLargePaymentAmount() throws Exception {
        String paymentAmount = "700000";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(groupLoan.getPersonnel().getPersonnelId()),
                new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount),
                new LocalDate(),
                defaultPaymentType,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.INVALID_PAYMENT_AMOUNT));
    }

    public void testValidatePaymentWithInvalidNegativePaymentAmount() throws Exception {
        String paymentAmount = "-1";
        standardAccountService.setAccountPersistence(new AccountPersistence());

        AccountPaymentParametersDto accountPaymentParametersDto = new AccountPaymentParametersDto(
                new UserReferenceDto(groupLoan.getPersonnel().getPersonnelId()),
                new AccountReferenceDto(groupLoan.getAccountId()),
                new BigDecimal(paymentAmount),
                new LocalDate(),
                defaultPaymentType,"");
        List<InvalidPaymentReason> errors = standardAccountService.validatePayment(accountPaymentParametersDto);

        Assert.assertTrue(errors.contains(InvalidPaymentReason.INVALID_PAYMENT_AMOUNT));
    }

    private SavingsBO createSavingsAccount() throws Exception {
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
