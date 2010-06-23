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

package org.mifos.accounts.loan.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanProductBuilder;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanAdjustmentWhenCompletedIntegrationTest extends MifosIntegrationTestCase {

    public LoanAdjustmentWhenCompletedIntegrationTest() throws Exception {
        super();
    }

    private CenterBO center;
    private GroupBO group;
    private ClientBO client;
    private LoanBO loan;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(loan);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        new DateTimeService().resetToCurrentSystemDateTime();
        super.tearDown();
    }

    public void testWhenALoanIsRepaidEarlyAndThenAdjustedThatTheLoanSummaryAndSchedulesDetailsAreTheSameBeforeAndAfter()
            throws Exception {

        new DateTimeService().setCurrentDateTimeFixed(date(2010, 10, 13));

        loan = createLoan();
        Money initialOriginalPrincipal = loan.getLoanSummary().getOriginalPrincipal();
        Money initialOriginalInterest = loan.getLoanSummary().getOriginalInterest();
        Money initialOriginalFees = loan.getLoanSummary().getOriginalFees();
        Money initialPrincipalPaid = loan.getLoanSummary().getPrincipalPaid();
        Money initialInterestPaid = loan.getLoanSummary().getInterestPaid();
        Money initialFeesPaid = loan.getLoanSummary().getFeesPaid();

        makePayment(loan, "300.0");
        loan = (LoanBO) new AccountPersistence().getAccount(loan.getAccountId());
        assertThat(loan.getLoanSummary().getOriginalPrincipal(), is(initialOriginalPrincipal));
        assertThat(loan.getLoanSummary().getOriginalInterest(), is(initialOriginalInterest));
        assertThat(loan.getLoanSummary().getOriginalFees(), is(initialOriginalFees));
        assertFalse(loan.getLoanSummary().getPrincipalPaid().equals(initialPrincipalPaid));
        assertFalse(loan.getLoanSummary().getInterestPaid().equals(initialInterestPaid));
        assertFalse(loan.getLoanSummary().getFeesPaid().equals(initialFeesPaid));

        makeEarlyPayment(loan);
        loan = (LoanBO) new AccountPersistence().getAccount(loan.getAccountId());
        // The early repayment should have caused the original interest and fees to be changed to equal the amounts
        // paid.
        assertThat(loan.getLoanSummary().getOriginalPrincipal(), is(initialOriginalPrincipal));
        assertFalse(loan.getLoanSummary().getOriginalInterest().equals(initialOriginalInterest));
        assertFalse(loan.getLoanSummary().getOriginalFees().equals(initialOriginalFees));
        assertThat(loan.getLoanSummary().getPrincipalPaid(), is(loan.getLoanSummary().getOriginalPrincipal()));
        assertThat(loan.getLoanSummary().getInterestPaid(), is(loan.getLoanSummary().getOriginalInterest()));
        assertThat(loan.getLoanSummary().getFeesPaid(), is(loan.getLoanSummary().getOriginalFees()));

        adjustCompletedLoan(loan);
        loan = (LoanBO) new AccountPersistence().getAccount(loan.getAccountId());
        // The adjustment of a completed loan should have caused the original amounts to be reset
        assertThat(loan.getLoanSummary().getOriginalPrincipal(), is(initialOriginalPrincipal));
        assertThat(loan.getLoanSummary().getOriginalInterest(), is(initialOriginalInterest));
        assertThat(loan.getLoanSummary().getOriginalFees(), is(initialOriginalFees));
    }

    private LoanBO createLoan() throws Exception {

        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);


        center = new CenterBuilder().with(weeklyMeeting).withName("Center").with(sampleBranchOffice()).withLoanOfficer(
                testUser()).build();
        IntegrationTestObjectMother.createCenter(center, weeklyMeeting);

        group = new GroupBuilder().withMeeting(weeklyMeeting).withName("Group").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withParentCustomer(center).build();
        IntegrationTestObjectMother.createGroup(group, weeklyMeeting);

        client = new ClientBuilder().withMeeting(weeklyMeeting).withName("Client").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withParentCustomer(group).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(client, weeklyMeeting);

        LoanOfferingBO loanOffering = new LoanProductBuilder().withName("Adjust Loan Product").withMeeting(weeklyMeeting).buildForIntegrationTests();
        IntegrationTestObjectMother.createProduct(loanOffering);

        AmountFeeBO periodicFee = new FeeBuilder().appliesToLoans().periodic().withFeeAmount("10.0").withName(
                "Periodic Fee").with(sampleBranchOffice()).build();
        IntegrationTestObjectMother.saveFee(periodicFee);

        FeeDto feeView;
        List<FeeDto> feeDtos = new ArrayList<FeeDto>();
        feeView = new FeeDto(TestUtils.makeUser(), periodicFee);
        feeDtos.add(feeView);

        LoanBO loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering, client,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(Money.getDefaultCurrency(), "1000.0"), Short
                        .valueOf("10"), new DateTime().toDate(), false, loanOffering.getDefInterestRate(), (short) 0,
                null, feeDtos, null, 1000.0, 1000.0, loanOffering.getEligibleInstallmentSameForAllLoan()
                        .getMaxNoOfInstall(), loanOffering.getEligibleInstallmentSameForAllLoan().getMinNoOfInstall(),
                false, null);

        loan.save();
        StaticHibernateUtil.commitTransaction();
        return loan;

    }

    private void makePayment(LoanBO loan, String amount) throws Exception {

        PaymentData paymentData = PaymentData.createPaymentData(new Money(loan.getCurrency(), amount), testUser(),
                (short) 1, new DateTime().toDate());
        loan.applyPayment(paymentData, true);
        StaticHibernateUtil.commitTransaction();
    }

    private void makeEarlyPayment(LoanBO loan) throws AccountException {

        loan.makeEarlyRepayment(loan.getTotalEarlyRepayAmount(), null, null, "1", testUser().getPersonnelId());
        StaticHibernateUtil.commitTransaction();
    }

    private void adjustCompletedLoan(LoanBO loan) throws AccountException, PersistenceException {
        StaticHibernateUtil.closeSession();
        LoanBO tempLoan = (LoanBO) new AccountPersistence().getAccount(loan.getAccountId());
        tempLoan.setUserContext(loan.getUserContext());
        tempLoan.adjustLastPayment("Undo last payment");
        StaticHibernateUtil.commitTransaction();
    }

    private DateTime date(int year, int month, int day) {
        return new DateTime().withDate(year, month, day).toDateMidnight().toDateTime();
    }
}
