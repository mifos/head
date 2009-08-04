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
package org.mifos.application.accounts.loan.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionDateEntityIntegrationTest;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountFeesEntityIntegrationTest;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
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
    public static LoanBO createLoanAccount(String globalNum, CustomerBO customer, AccountState state, Date startDate,
            LoanOfferingBO loanOffering) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer.getCustomerMeeting().getMeeting());
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);

        LoanBO loan;
        MifosCurrency currency = TestObjectFactory.getCurrency();
        LoanOfferingInstallmentRange eligibleInstallmentRange = loanOffering.getEligibleInstallmentSameForAllLoan();

        try {
            loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering, customer, state, new Money(currency, "300.0"),
                    Short.valueOf("6"), meetingDates.get(0), false, 0.0, (short) 0, new FundBO(),
                    new ArrayList<FeeView>(), null, DEFAULT_LOAN_AMOUNT, DEFAULT_LOAN_AMOUNT, eligibleInstallmentRange
                            .getMaxNoOfInstall(), eligibleInstallmentRange.getMinNoOfInstall(), false, null);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
        FeeBO maintanenceFee = TestObjectFactory.createPeriodicAmountFee("Mainatnence Fee", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(loan, maintanenceFee,
                ((AmountFeeBO) maintanenceFee).getFeeAmount().getAmountDoubleValue());
        AccountFeesEntityIntegrationTest.addAccountFees(accountPeriodicFee, loan);
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
            AccountActionDateEntityIntegrationTest.addAccountActionDate(actionDate, loan);

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
    
    public static LoanBO createIndividualLoanAccount(String globalNum, CustomerBO customer, AccountState state,
            Date startDate, LoanOfferingBO loanOfering) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer.getCustomerMeeting().getMeeting());
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);

        LoanBO loan;
        MifosCurrency currency = TestObjectFactory.getCurrency();
        try {
            loan = LoanBO.createIndividualLoan(TestUtils.makeUser(), loanOfering, customer, state, new Money(currency,
                    "300.0"), Short.valueOf("6"), meetingDates.get(0), false, false, 0.0, (short) 0, new FundBO(),
                    new ArrayList<FeeView>(), null);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
        FeeBO maintanenceFee = TestObjectFactory.createPeriodicAmountFee("Mainatnence Fee", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(loan, maintanenceFee,
                ((AmountFeeBO) maintanenceFee).getFeeAmount().getAmountDoubleValue());
        AccountFeesEntityIntegrationTest.addAccountFees(accountPeriodicFee, loan);
        loan.setLoanMeeting(meeting);
        short i = 0;
        for (Date date : meetingDates) {
            LoanScheduleEntity actionDate = (LoanScheduleEntity) loan.getAccountActionDate(++i);
            actionDate.setPrincipal(new Money(currency, "100.0"));
            actionDate.setInterest(new Money(currency, "12.0"));
            actionDate.setActionDate(new java.sql.Date(date.getTime()));
            actionDate.setPaymentStatus(PaymentStatus.UNPAID);
            AccountActionDateEntityIntegrationTest.addAccountActionDate(actionDate, loan);

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
     * 
     * @param globalNum
     *            Currently ignored (TODO: remove it or honor it)
     */
    public static LoanBO createLoanAccountWithDisbursement(String globalNum, CustomerBO customer, AccountState state,
            Date startDate, LoanOfferingBO loanOffering, int disbursalType, Short noOfInstallments) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer.getCustomerMeeting().getMeeting());
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);

        LoanBO loan;
        MifosCurrency currency = TestObjectFactory.getCurrency();
        LoanOfferingInstallmentRange eligibleInstallmentRange = loanOffering.getEligibleInstallmentSameForAllLoan();

        try {
            loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering, customer, state, new Money(currency, "300.0"),
                    noOfInstallments, meetingDates.get(0), false, 10.0, (short) 0, new FundBO(),
                    new ArrayList<FeeView>(), null, DEFAULT_LOAN_AMOUNT, DEFAULT_LOAN_AMOUNT, eligibleInstallmentRange
                            .getMaxNoOfInstall(), eligibleInstallmentRange.getMinNoOfInstall(), false, null);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
        FeeBO maintanenceFee = TestObjectFactory.createPeriodicAmountFee("Mainatnence Fee", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(loan, maintanenceFee, new Double("10.0"));
        AccountFeesEntityIntegrationTest.addAccountFees(accountPeriodicFee, loan);
        AccountFeesEntity accountDisbursementFee = null;
        FeeBO disbursementFee = null;
        AccountFeesEntity accountDisbursementFee2 = null;
        FeeBO disbursementFee2 = null;

        if (disbursalType == 1 || disbursalType == 2) {
            disbursementFee = TestObjectFactory.createOneTimeAmountFee("Disbursement Fee 1", FeeCategory.LOAN, "10",
                    FeePayment.TIME_OF_DISBURSMENT);
            accountDisbursementFee = new AccountFeesEntity(loan, disbursementFee, new Double("10.0"));
            AccountFeesEntityIntegrationTest.addAccountFees(accountDisbursementFee, loan);

            disbursementFee2 = TestObjectFactory.createOneTimeAmountFee("Disbursement Fee 2", FeeCategory.LOAN, "20",
                    FeePayment.TIME_OF_DISBURSMENT);
            accountDisbursementFee2 = new AccountFeesEntity(loan, disbursementFee2, new Double("20.0"));
            AccountFeesEntityIntegrationTest.addAccountFees(accountDisbursementFee2, loan);
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
                    AccountActionDateEntityIntegrationTest.addAccountActionDate(actionDate, loan);

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
                AccountActionDateEntityIntegrationTest.addAccountActionDate(actionDate, loan);
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
                AccountActionDateEntityIntegrationTest.addAccountActionDate(actionDate, loan);
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
    public static LoanBO createBasicLoanAccount(CustomerBO customer, AccountState state, Date startDate,
            LoanOfferingBO loanOffering) {
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
            loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering, customer, state, new Money("300.0"),
                    (short) 6, meetingDates.get(0), false, 0.0, (short) 0, new FundBO(), feeViewList, null,
                    DEFAULT_LOAN_AMOUNT, DEFAULT_LOAN_AMOUNT, eligibleInstallmentRange.getMaxNoOfInstall(),
                    eligibleInstallmentRange.getMinNoOfInstall(), false, null);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }

        return loan;
    }
    
    public static void setFeeAmountPaid(AccountFeesActionDetailEntity accountFeesActionDetailEntity, Money feeAmountPaid) {
        ((LoanFeeScheduleEntity) accountFeesActionDetailEntity).setFeeAmountPaid(feeAmountPaid);
    }

    public static void setActionDate(AccountActionDateEntity accountActionDateEntity, java.sql.Date actionDate) {
        ((LoanScheduleEntity) accountActionDateEntity).setActionDate(actionDate);
    }

    public static void setDisbursementDate(AccountBO account, Date disbursementDate) {
        ((LoanBO) account).setDisbursementDate(disbursementDate);
    }
    
    private static void setLoanSummary(LoanBO loan, MifosCurrency currency) {
        LoanSummaryEntity loanSummary = loan.getLoanSummary();
        loanSummary.setOriginalPrincipal(new Money(currency, "300.0"));
        loanSummary.setOriginalInterest(new Money(currency, "36.0"));
    }
    
    public static void modifyDisbursmentDate(LoanBO loan, Date disbursmentDate) {
        loan.setDisbursementDate(disbursmentDate);
    }
    
    
}
