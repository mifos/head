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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialInitializer;
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
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.spring.SpringUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml", "/org/mifos/config/resources/hibernate-daos.xml",
        "/org/mifos/config/resources/services.xml" })
public class LoanAdjustmentWhenCompletedIntegrationTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    private CenterBO center;
    private GroupBO group;
    private ClientBO client;
    private LoanBO loan;

    @BeforeClass
    public static void initialiseHibernateUtil() throws FinancialException {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();
        SpringUtil.initializeSpring();
        FinancialInitializer.initialize();
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
        new DateTimeService().resetToCurrentSystemDateTime();
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void cleanDatabaseTables() throws Exception {

        databaseCleaner.clean();
    }

    @Test
    public void testWhenALoanIsRepaidEarlyAndThenAdjustedThatTheLoanSummaryAndSchedulesDetailsAreTheSameBeforeAndAfter()
            throws Exception {
        // relates to mifos-1986
        new DateTimeService().setCurrentDateTimeFixed(date(2010, 10, 13));

        loan = createLoan();
        Money initialOriginalPrincipal = loan.getLoanSummary().getOriginalPrincipal();
        Money initialOriginalInterest = loan.getLoanSummary().getOriginalInterest();
        Money initialOriginalFees = loan.getLoanSummary().getOriginalFees();
        Money initialPrincipalPaid = loan.getLoanSummary().getPrincipalPaid();
        Money initialInterestPaid = loan.getLoanSummary().getInterestPaid();
        Money initialFeesPaid = loan.getLoanSummary().getFeesPaid();

        // pay 3 installments
        makePayment(loan, "333.0");
        loan = (LoanBO) new AccountPersistence().getAccount(loan.getAccountId());
        assertThat(loan.getLoanSummary().getOriginalPrincipal(), is(initialOriginalPrincipal));
        assertThat(loan.getLoanSummary().getOriginalInterest(), is(initialOriginalInterest));
        assertThat(loan.getLoanSummary().getOriginalFees(), is(initialOriginalFees));
        assertFalse(loan.getLoanSummary().getPrincipalPaid().equals(initialPrincipalPaid));
        assertFalse(loan.getLoanSummary().getInterestPaid().equals(initialInterestPaid));
        assertFalse(loan.getLoanSummary().getFeesPaid().equals(initialFeesPaid));
        List<LoanInstallment> copySchedule = copyLoanSchedule(loan);

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
        assertFalse(sameSchedule(copySchedule, loan.getAccountActionDates()));

        adjustCompletedLoan(loan);
        loan = (LoanBO) new AccountPersistence().getAccount(loan.getAccountId());
        // The adjustment of a completed loan should have caused the original amounts to be reset
        assertThat(loan.getLoanSummary().getOriginalPrincipal(), is(initialOriginalPrincipal));
        assertThat(loan.getLoanSummary().getOriginalInterest(), is(initialOriginalInterest));
        assertThat(loan.getLoanSummary().getOriginalFees(), is(initialOriginalFees));
        assertTrue(sameSchedule(copySchedule, loan.getAccountActionDates()));
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

        LoanOfferingBO loanOffering = new LoanProductBuilder().withName("Adjust Loan Product").withMeeting(
                weeklyMeeting).buildForIntegrationTests();
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

    private List<LoanInstallment> copyLoanSchedule(LoanBO loan) {

        List<LoanInstallment> copySchedule = new ArrayList<LoanInstallment>();
        for (AccountActionDateEntity accountInstallment : loan.getAccountActionDates()) {

            LoanScheduleEntity loanInstallment = (LoanScheduleEntity) accountInstallment;
            copySchedule.add(new LoanInstallment(loanInstallment));
        }
        return copySchedule;
    }

    private boolean sameSchedule(List<LoanInstallment> copySchedule, Set<AccountActionDateEntity> loanSchedule) {

        Integer index = 0;
        for (AccountActionDateEntity accountInstallment : loanSchedule) {
            if (copySchedule.get(index).different((LoanScheduleEntity) accountInstallment)) {
                return false;
            }
            index++;
        }
        return true;
    }

    private class LoanInstallment {

        private final Short installmentId;
        private final Date actionDate;
        private final Boolean isPaid;
        private final Date paymentDate;
        private final Money principal;
        private final Money interest;
        private final Money miscFee;
        private final Money miscPenalty;
        private final Money principalPaid;
        private final Money interestPaid;
        private Money miscFeePaid;
        private Money miscPenaltyPaid;
        private List<LoanFeeInstallment> loanFeeInstallments;

        public LoanInstallment(final LoanScheduleEntity loanInstallment) {
            this.installmentId = loanInstallment.getInstallmentId();
            this.actionDate = loanInstallment.getActionDate();
            this.isPaid = loanInstallment.isPaid();
            this.paymentDate = loanInstallment.getPaymentDate();
            this.principal = loanInstallment.getPrincipal();
            this.interest = loanInstallment.getInterest();
            this.miscFee = loanInstallment.getMiscFee();
            this.miscPenalty = loanInstallment.getMiscPenalty();
            this.principalPaid = loanInstallment.getPrincipalPaid();
            this.interestPaid = loanInstallment.getInterestPaid();
            this.miscFeePaid = loanInstallment.getMiscFeePaid();
            this.miscPenaltyPaid = loanInstallment.getMiscPenaltyPaid();
            if (loanInstallment.getAccountFeesActionDetails() != null) {
                loanFeeInstallments = new ArrayList<LoanFeeInstallment>();
                for (AccountFeesActionDetailEntity accountFeesActionDetail : loanInstallment
                        .getAccountFeesActionDetails()) {
                    this.loanFeeInstallments.add(new LoanFeeInstallment(accountFeesActionDetail));
                }
            }

        }

        public boolean different(LoanScheduleEntity loanInstallment) {

            // System.out.println("installmentId:" + this.installmentId + " - " + loanInstallment.getInstallmentId());
            if (!this.installmentId.equals(loanInstallment.getInstallmentId())) {
                // System.out.println("installmentId different:" + this.installmentId + " - " +
                // loanInstallment.getInstallmentId());
                return true;
            }
            if (!this.actionDate.equals(loanInstallment.getActionDate())) {
                // System.out.println("actionDate different:" + this.actionDate + " - " +
                // loanInstallment.getActionDate());
                return true;
            }
            if (!this.isPaid.equals(loanInstallment.isPaid())) {
                // System.out.println("isPaid different:" + this.isPaid + " - " + loanInstallment.isPaid());
                return true;
            }
            if ((this.paymentDate == null && loanInstallment.getPaymentDate() != null)
                    || (this.paymentDate != null && loanInstallment.getPaymentDate() == null)) {
                System.out.println("paymentDate null/not null different");
                return true;
            }
            if (this.paymentDate != null) {
                if (!this.paymentDate.equals(loanInstallment.getPaymentDate())) {
                    System.out.println("paymentDate different:" + this.paymentDate + " - "
                            + loanInstallment.getPaymentDate());
                    return true;
                }
            }
            if (!this.principal.equals(loanInstallment.getPrincipal())) {
                // System.out.println("principal different:" + this.principal + " - " + loanInstallment.getPrincipal());
                return true;
            }
            if (!this.interest.equals(loanInstallment.getInterest())) {
                // System.out.println("interest different:" + this.interest + " - " + loanInstallment.getInterest());
                return true;
            }
            if (!this.miscFee.equals(loanInstallment.getMiscFee())) {
                // System.out.println("miscFee different:" + this.miscFee + " - " + loanInstallment.getMiscFee());
                return true;
            }
            if (!this.miscPenalty.equals(loanInstallment.getMiscPenalty())) {
                // System.out.println("miscPenalty different:" + this.miscPenalty + " - " +
                // loanInstallment.getMiscPenalty());
                return true;
            }
            if (!this.principalPaid.equals(loanInstallment.getPrincipalPaid())) {
                // System.out.println("principalPaid different:" + this.principalPaid + " - " +
                // loanInstallment.getPrincipalPaid());
                return true;
            }
            if (!this.interestPaid.equals(loanInstallment.getInterestPaid())) {
                // System.out.println("interestPaid different:" + this.interestPaid + " - " +
                // loanInstallment.getInterestPaid());
                return true;
            }
            if (!this.miscFeePaid.equals(loanInstallment.getMiscFeePaid())) {
                // System.out.println("miscFeePaid different:" + this.miscFeePaid + " - " +
                // loanInstallment.getMiscFeePaid());
                return true;
            }
            if (!this.miscPenaltyPaid.equals(loanInstallment.getMiscPenaltyPaid())) {
                // System.out.println("miscPenaltyPaid different:" + this.miscPenaltyPaid + " - " +
                // loanInstallment.getMiscPenaltyPaid());
                return true;
            }
            if ((this.loanFeeInstallments == null && loanInstallment.getAccountFeesActionDetails() != null)
                    || (this.loanFeeInstallments != null && loanInstallment.getAccountFeesActionDetails() == null)) {
                // System.out.println("Loan Fee Installment null/not null different");
                return true;
            }
            if (loanInstallment.getAccountFeesActionDetails() != null) {
                Integer index = 0;
                for (AccountFeesActionDetailEntity accountFeesActionDetail : loanInstallment
                        .getAccountFeesActionDetails()) {
                    if (this.loanFeeInstallments.get(index).different(accountFeesActionDetail)) {
                        return true;
                    }
                    index++;
                }
            }

            return false;
        }

    }

    private class LoanFeeInstallment {

        private final Money feeAmount;
        private final Money feeAmountPaid;

        public LoanFeeInstallment(final AccountFeesActionDetailEntity feeInstallment) {
            this.feeAmount = feeInstallment.getFeeAmount();
            this.feeAmountPaid = feeInstallment.getFeeAmountPaid();
        }

        public boolean different(AccountFeesActionDetailEntity accountFeesActionDetail) {

            // System.out.println("checking fees");
            if (!this.feeAmount.equals(accountFeesActionDetail.getFeeAmount())) {
                // System.out.println("feeAmount different:" + this.feeAmount + " - " +
                // accountFeesActionDetail.getFeeAmount());
                return true;
            }
            if (!this.feeAmountPaid.equals(accountFeesActionDetail.getFeeAmountPaid())) {
                // System.out.println("feeAmountPaid different:" + this.feeAmountPaid + " - " +
                // accountFeesActionDetail.getFeeAmountPaid());
                return true;
            }
            return false;
        }

    }

}