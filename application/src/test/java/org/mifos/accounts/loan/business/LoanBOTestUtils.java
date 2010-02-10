/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.holiday.util.helpers.HolidayUtilsIntegrationTest;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.accounts.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanBOTestUtils {

    private static final double DELTA = 0.00000001;

    private static final double DEFAULT_LOAN_AMOUNT = 300.0;
    /**
     * Like
     * {@link #createLoanAccountWithDisbursement(String, CustomerBO, AccountState, Date, LoanOfferingBO, int, Short)}
     * but differs in various ways.
     * <p/>
     * TODO: This test code needs to be refactored! By creating the loan with a
     * set of terms, then directly manipulating instance variables to completely
     * change the repayment schedule, it leaves the loan in an inconsistent
     * state, which leads one to suspect the validity of any of the 67 unit
     * tests that use it.
     * 
     * It has been verified that setActionDate method calls in the loop below
     * will set the dates of the installments incorrectly for some if not all
     * cases. For certain classes of tests this doesn't matter, but for others
     * (involving verifying dates) it does. So BEWARE if you call down through
     * this method.
     * 
     * @param globalNum
     *            Currently ignored (TODO: remove it or honor it)
     */
    public static LoanBO createLoanAccount(final String globalNum, final CustomerBO customer, final AccountState state, final Date startDate,
            final LoanOfferingBO loanOffering) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer.getCustomerMeeting().getMeeting());
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);

        LoanBO loan;
        MifosCurrency currency = loanOffering.getCurrency();
        LoanOfferingInstallmentRange eligibleInstallmentRange = loanOffering.getEligibleInstallmentSameForAllLoan();

        try {
            loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering, customer, state, new Money(currency, "300.0"),
                    Short.valueOf("6"), meetingDates.get(0), false, 0.0, (short) 0, null,
                    new ArrayList<FeeView>(), null, DEFAULT_LOAN_AMOUNT, DEFAULT_LOAN_AMOUNT, eligibleInstallmentRange
                            .getMaxNoOfInstall(), eligibleInstallmentRange.getMinNoOfInstall(), false, null);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
        FeeBO maintanenceFee = TestObjectFactory.createPeriodicAmountFee("Mainatnence Fee", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(loan, maintanenceFee,
                ((AmountFeeBO) maintanenceFee).getFeeAmount().getAmountDoubleValue());
        AccountTestUtils.addAccountFees(accountPeriodicFee, loan);
        loan.setLoanMeeting(meeting);
        short i = 0;
        for (Date date : meetingDates) {
            LoanScheduleEntity actionDate = (LoanScheduleEntity) loan.getAccountActionDate(++i);
            actionDate.setPrincipal(new Money(currency, "100.0"));
            actionDate.setInterest(new Money(currency, "12.0"));
            // the following line overwrites the correct loan schedule dates
            // with dates that are not correct!
            actionDate.setActionDate(new java.sql.Date(date.getTime()));

            actionDate.setPaymentStatus(PaymentStatus.UNPAID);
            AccountTestUtils.addAccountActionDate(actionDate, loan);

            AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(actionDate, maintanenceFee,
                    accountPeriodicFee, new Money(currency, "100.0"));
            setFeeAmountPaid(accountFeesaction, new Money(currency, "0.0"));
            actionDate.addAccountFeesAction(accountFeesaction);
        }
        loan.setCreatedBy(Short.valueOf("1"));
        loan.setCreatedDate(new Date(System.currentTimeMillis()));

        setLoanSummary(loan, currency);
        return loan;
    }
    
    public static LoanBO createIndividualLoanAccount(final String globalNum, final CustomerBO customer, final AccountState state,
            final Date startDate, final LoanOfferingBO loanOfering) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer.getCustomerMeeting().getMeeting());
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);

        LoanBO loan;
        MifosCurrency currency = TestObjectFactory.getCurrency();
        try {
            loan = LoanBO.createIndividualLoan(TestUtils.makeUser(), loanOfering, customer, state, new Money(currency,
                    "300.0"), Short.valueOf("6"), meetingDates.get(0), false, false, 0.0, (short) 0, null,
                    new ArrayList<FeeView>(), null);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
        FeeBO maintanenceFee = TestObjectFactory.createPeriodicAmountFee("Mainatnence Fee", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(loan, maintanenceFee,
                ((AmountFeeBO) maintanenceFee).getFeeAmount().getAmountDoubleValue());
        AccountTestUtils.addAccountFees(accountPeriodicFee, loan);
        loan.setLoanMeeting(meeting);
        short i = 0;
        for (Date date : meetingDates) {
            LoanScheduleEntity actionDate = (LoanScheduleEntity) loan.getAccountActionDate(++i);
            actionDate.setPrincipal(new Money(currency, "100.0"));
            actionDate.setInterest(new Money(currency, "12.0"));
            actionDate.setActionDate(new java.sql.Date(date.getTime()));
            actionDate.setPaymentStatus(PaymentStatus.UNPAID);
            AccountTestUtils.addAccountActionDate(actionDate, loan);

            AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(actionDate, maintanenceFee,
                    accountPeriodicFee, new Money(currency, "100.0"));
            setFeeAmountPaid(accountFeesaction, new Money(currency, "0.0"));
            actionDate.addAccountFeesAction(accountFeesaction);
        }
        loan.setCreatedBy(Short.valueOf("1"));
        loan.setCreatedDate(new Date(System.currentTimeMillis()));

        setLoanSummary(loan, currency);
        return loan;
    }
    
    /**
     * Like
     * {@link #createLoanAccount(String, CustomerBO, AccountState, Date, LoanOfferingBO)}
     * but differs in various ways.
     * 
     * Note: the manipulation done in this method looks very suspicious and
     * possibly wrong. Tests that use this method should be considered as
     * suspect.
     */
    public static LoanBO createLoanAccountWithDisbursement(final CustomerBO customer, final AccountState state,
            final Date startDate, final LoanOfferingBO loanOffering, final int disbursalType, final Short noOfInstallments) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer.getCustomerMeeting().getMeeting());
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);

        LoanBO loan;
        MifosCurrency currency = TestObjectFactory.getCurrency();
        LoanOfferingInstallmentRange eligibleInstallmentRange = loanOffering.getEligibleInstallmentSameForAllLoan();

        try {
            loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering, customer, state, new Money(currency, "300.0"),
                    noOfInstallments, meetingDates.get(0), false, 10.0, (short) 0, null,
                    new ArrayList<FeeView>(), null, DEFAULT_LOAN_AMOUNT, DEFAULT_LOAN_AMOUNT, eligibleInstallmentRange
                            .getMaxNoOfInstall(), eligibleInstallmentRange.getMinNoOfInstall(), false, null);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
        FeeBO maintanenceFee = TestObjectFactory.createPeriodicAmountFee("Mainatnence Fee", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(loan, maintanenceFee, new Double("10.0"));
        AccountTestUtils.addAccountFees(accountPeriodicFee, loan);
        AccountFeesEntity accountDisbursementFee = null;
        FeeBO disbursementFee = null;
        AccountFeesEntity accountDisbursementFee2 = null;
        FeeBO disbursementFee2 = null;

        if (disbursalType == 1 || disbursalType == 2) {
            disbursementFee = TestObjectFactory.createOneTimeAmountFee("Disbursement Fee 1", FeeCategory.LOAN, "10",
                    FeePayment.TIME_OF_DISBURSMENT);
            accountDisbursementFee = new AccountFeesEntity(loan, disbursementFee, new Double("10.0"));
            AccountTestUtils.addAccountFees(accountDisbursementFee, loan);

            disbursementFee2 = TestObjectFactory.createOneTimeAmountFee("Disbursement Fee 2", FeeCategory.LOAN, "20",
                    FeePayment.TIME_OF_DISBURSMENT);
            accountDisbursementFee2 = new AccountFeesEntity(loan, disbursementFee2, new Double("20.0"));
            AccountTestUtils.addAccountFees(accountDisbursementFee2, loan);
        }
        loan.setLoanMeeting(meeting);

        if (disbursalType == 2)// 2-Interest At Disbursment
        {
            loan.setInterestDeductedAtDisbursement(true);
            meetingDates = TestObjectFactory.getMeetingDates(loan.getLoanMeeting(), 6);
            short i = 0;
            for (Date date : meetingDates) {
                if (i == 0) {
                    i++;
                    loan.setDisbursementDate(date);
                    LoanScheduleEntity actionDate = (LoanScheduleEntity) loan.getAccountActionDate(i);
                    actionDate.setActionDate(new java.sql.Date(date.getTime()));
                    actionDate.setInterest(new Money(currency, "12.0"));
                    actionDate.setPaymentStatus(PaymentStatus.UNPAID);
                    AccountTestUtils.addAccountActionDate(actionDate, loan);

                    // periodic fee
                    AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(actionDate,
                            maintanenceFee, accountPeriodicFee, new Money(currency, "10.0"));
                    setFeeAmountPaid(accountFeesaction, new Money(currency, "0.0"));
                    actionDate.addAccountFeesAction(accountFeesaction);

                    // dibursement fee one
                    AccountFeesActionDetailEntity accountFeesaction1 = new LoanFeeScheduleEntity(actionDate,
                            disbursementFee, accountDisbursementFee, new Money(currency, "10.0"));

                    setFeeAmountPaid(accountFeesaction1, new Money(currency, "0.0"));
                    actionDate.addAccountFeesAction(accountFeesaction1);

                    // disbursementfee2
                    AccountFeesActionDetailEntity accountFeesaction2 = new LoanFeeScheduleEntity(actionDate,
                            disbursementFee2, accountDisbursementFee2, new Money(currency, "20.0"));
                    setFeeAmountPaid(accountFeesaction2, new Money(currency, "0.0"));
                    actionDate.addAccountFeesAction(accountFeesaction2);

                    continue;
                }
                i++;
                LoanScheduleEntity actionDate = (LoanScheduleEntity) loan.getAccountActionDate(i);
                actionDate.setActionDate(new java.sql.Date(date.getTime()));
                actionDate.setPrincipal(new Money(currency, "100.0"));
                actionDate.setInterest(new Money(currency, "12.0"));
                actionDate.setPaymentStatus(PaymentStatus.UNPAID);
                AccountTestUtils.addAccountActionDate(actionDate, loan);
                AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(actionDate, maintanenceFee,
                        accountPeriodicFee, new Money(currency, "100.0"));
                setFeeAmountPaid(accountFeesaction, new Money(currency, "0.0"));
                actionDate.addAccountFeesAction(accountFeesaction);
            }

        } else if (disbursalType == 1 || disbursalType == 3) {
            loan.setInterestDeductedAtDisbursement(false);
            meetingDates = TestObjectFactory.getMeetingDates(loan.getLoanMeeting(), 6);

            short i = 0;
            for (Date date : meetingDates) {

                if (i == 0) {
                    i++;
                    loan.setDisbursementDate(date);
                    continue;
                }
                LoanScheduleEntity actionDate = (LoanScheduleEntity) loan.getAccountActionDate(i++);
                actionDate.setActionDate(new java.sql.Date(date.getTime()));
                actionDate.setPrincipal(new Money(currency, "100.0"));
                actionDate.setInterest(new Money(currency, "12.0"));
                actionDate.setPaymentStatus(PaymentStatus.UNPAID);
                AccountTestUtils.addAccountActionDate(actionDate, loan);
                AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(actionDate, maintanenceFee,
                        accountPeriodicFee, new Money(currency, "100.0"));
                setFeeAmountPaid(accountFeesaction, new Money(currency, "0.0"));
                actionDate.addAccountFeesAction(accountFeesaction);
            }
        }
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.NONE);
        loan.setGracePeriodType(gracePeriodType);
        loan.setCreatedBy(Short.valueOf("1"));

        // Set collateral type to lookup id 109, which references the lookup
        // value 'Type 1'
        loan.setCollateralTypeId(Integer.valueOf("109"));

        InterestTypesEntity interestTypes = new InterestTypesEntity(InterestType.FLAT);
        loan.setInterestType(interestTypes);
        loan.setInterestRate(10.0);
        loan.setCreatedDate(new Date(System.currentTimeMillis()));

        setLoanSummary(loan, currency);
        return loan;
    }
    
    /**
     * This method is an attempt to fix some of what is wrong in the
     * createLoanAccount method below. This method has been created late in the
     * v1.1 release cycle, so an attempt has not yet been made to try replacing
     * some of the occurrences of createLoanAccount with this method.
     */
    public static LoanBO createBasicLoanAccount(final CustomerBO customer, final AccountState state, final Date startDate,
            final LoanOfferingBO loanOffering) {
        LoanBO loan;
        LoanOfferingInstallmentRange eligibleInstallmentRange = loanOffering.getEligibleInstallmentSameForAllLoan();
        UserContext userContext = TestUtils.makeUser();
        userContext.setLocaleId(null);
        List<FeeView> feeViewList = new ArrayList<FeeView>();
        FeeBO maintanenceFee = TestObjectFactory.createPeriodicAmountFee("Mainatnence Fee", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        feeViewList.add(new FeeView(userContext, maintanenceFee));
        MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer.getCustomerMeeting().getMeeting());
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);

        try {
            loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering, customer, state, new Money(loanOffering.getCurrency(),"300.0"),
                    (short) 6, meetingDates.get(0), false, 0.0, (short) 0, null, feeViewList, null,
                    DEFAULT_LOAN_AMOUNT, DEFAULT_LOAN_AMOUNT, eligibleInstallmentRange.getMaxNoOfInstall(),
                    eligibleInstallmentRange.getMinNoOfInstall(), false, null);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }

        return loan;
    }
    
    public static void setFeeAmountPaid(final AccountFeesActionDetailEntity accountFeesActionDetailEntity, final Money feeAmountPaid) {
        ((LoanFeeScheduleEntity) accountFeesActionDetailEntity).setFeeAmountPaid(feeAmountPaid);
    }

    public static void setActionDate(final AccountActionDateEntity accountActionDateEntity, final java.sql.Date actionDate) {
        ((LoanScheduleEntity) accountActionDateEntity).setActionDate(actionDate);
    }

    public static void setDisbursementDate(final AccountBO account, final Date disbursementDate) {
        ((LoanBO) account).setDisbursementDate(disbursementDate);
    }
    
    private static void setLoanSummary(final LoanBO loan, final MifosCurrency currency) {
        LoanSummaryEntity loanSummary = loan.getLoanSummary();
        loanSummary.setOriginalPrincipal(new Money(currency, "300.0"));
        loanSummary.setOriginalInterest(new Money(currency, "36.0"));
    }
    
    public static void modifyDisbursmentDate(final LoanBO loan, final Date disbursmentDate) {
        loan.setDisbursementDate(disbursmentDate);
    }
    
    /**
     * FIXME: Only called from {@link HolidayUtilsIntegrationTest}. We should
     * not have one test depending on another.
     */
    public LoanScheduleEntity[] createLoanRepaymentSchedule() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());
        AccountBO accountBO = getLoanAccountWithMiscFeeAndPenalty(AccountState.LOAN_APPROVED, startDate, 3, new Money(
                TestUtils.getCurrency(), "20"), new Money(TestUtils.getCurrency(), "30"));

        Set<AccountActionDateEntity> intallments = accountBO.getAccountActionDates();

        AccountActionDateEntity firstInstallment = null;
        for (AccountActionDateEntity entity : intallments) {
            if (entity.getInstallmentId().intValue() == 1) {
                firstInstallment = entity;
                break;
            }
        }
        Calendar disbursalDate = new GregorianCalendar();
        disbursalDate.setTimeInMillis(firstInstallment.getActionDate().getTime());
        Calendar cal = new GregorianCalendar(disbursalDate.get(Calendar.YEAR), disbursalDate.get(Calendar.MONTH),
                disbursalDate.get(Calendar.DATE), 0, 0);
        ((LoanBO) accountBO).disburseLoan("1234", cal.getTime(), Short.valueOf("1"), accountBO.getPersonnel(),
                startDate, Short.valueOf("1"));

        Set<AccountActionDateEntity> actionDateEntities = accountBO.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

        return paymentsArray;
    }
    
    public static LoanScheduleEntity[] getSortedAccountActionDateEntity(
            final Set<AccountActionDateEntity> actionDateCollection) {

        LoanScheduleEntity[] sortedList = new LoanScheduleEntity[actionDateCollection.size()];

        for (AccountActionDateEntity actionDateEntity : actionDateCollection) {
            sortedList[actionDateEntity.getInstallmentId().intValue() - 1] = (LoanScheduleEntity) actionDateEntity;
        }

        return sortedList;
    }
    
    private AccountBO getLoanAccountWithMiscFeeAndPenalty(final AccountState state, final Date startDate, final int disbursalType,
            final Money miscFee, final Money miscPenalty) {
        LoanBO accountBO = getLoanAccount(state, startDate, disbursalType);
        for (AccountActionDateEntity accountAction : accountBO.getAccountActionDates()) {
            LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
            if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                accountActionDateEntity.setMiscFee(miscFee);
                accountActionDateEntity.setMiscPenalty(miscPenalty);
                break;
            }
        }
        LoanSummaryEntity loanSummaryEntity = accountBO.getLoanSummary();
        loanSummaryEntity.setOriginalFees(loanSummaryEntity.getOriginalFees().add(miscFee));
        loanSummaryEntity.setOriginalPenalty(loanSummaryEntity.getOriginalPenalty().add(miscPenalty));
        TestObjectPersistence testObjectPersistence = new TestObjectPersistence();
        testObjectPersistence.update(accountBO);
        return testObjectPersistence.getObject(LoanBO.class, accountBO.getAccountId());
    }
    
    private LoanBO getLoanAccount(final AccountState state, final Date startDate, final int disbursalType) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        CenterBO center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        GroupBO group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccountWithDisbursement("99999999999", group, state, startDate,
                loanOffering, disbursalType);
    }
    
    public static LoanScheduleEntity[] getSortedAccountActionDateEntity(
            final Set<AccountActionDateEntity> actionDateCollection, final int expectedCount) {

        LoanScheduleEntity[] sortedList = new LoanScheduleEntity[actionDateCollection.size()];

       Assert.assertEquals(expectedCount, actionDateCollection.size());

        for (AccountActionDateEntity actionDateEntity : actionDateCollection) {
            sortedList[actionDateEntity.getInstallmentId().intValue() - 1] = (LoanScheduleEntity) actionDateEntity;
        }

        return sortedList;
    }
    
    public static void modifyData(final LoanScheduleEntity accntActionDate, final Money penalty, final Money penaltyPaid,
            final Money miscPenalty, final Money miscPenaltyPaid, final Money miscFee, final Money miscFeePaid, final Money principal,
            final Money principalPaid, final Money interest, final Money interestPaid) {
        accntActionDate.setPenalty(penalty);
        accntActionDate.setMiscPenalty(miscPenalty);
        accntActionDate.setMiscPenaltyPaid(miscPenaltyPaid);
        accntActionDate.setPenaltyPaid(penaltyPaid);
        accntActionDate.setMiscFee(miscFee);
        accntActionDate.setMiscFeePaid(miscFeePaid);
        accntActionDate.setPrincipal(principal);
        accntActionDate.setPrincipalPaid(principalPaid);
        accntActionDate.setInterest(interest);
        accntActionDate.setInterestPaid(interestPaid);
    }
}
