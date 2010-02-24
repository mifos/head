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
import static org.apache.commons.lang.math.NumberUtils.DOUBLE_ZERO;
import static org.apache.commons.lang.math.NumberUtils.SHORT_ZERO;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.config.AccountingRules;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.config.ConfigurationManager;
import org.mifos.core.ClasspathResource;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

/*
 * LoanCalculationTest is a starting point for defining and exploring
 * expected behavior for different loan payment calculations.
 * 
 */
public class LoanCalculationIntegrationTest extends MifosIntegrationTestCase {

    public LoanCalculationIntegrationTest() throws Exception {
        super();
    }


    // these constants for parsing the spreadsheet
    final String principal = "Principal";
    final String loanType = "Loan Type";
    final String annualInterest = "Annual Interest";
    final String numberOfPayments = "Number of Payments";
    final String paymentFrequency = "Payment Frequency";
    final String initialRoundingMode = "InitialRoundingMode";
    final String initialRoundOffMultiple = "InitialRoundOffMultiple";
    final String finalRoundingMode = "FinalRoundingMode";
    final String finalRoundOffMultiple = "FinalRoundOffMultiple";
    final String currencyRounding = "CurrencyRounding";
    final String digitsAfterDecimal = "Digits After Decimal";
    final String daysInYear = "Days in Year";
    final String totals = "Summed Totals";
    final String start = "Start";
    final String gracePeriodType = "GracePeriodType";
    final String gracePeriod = "GracePeriod";
    final String calculatedTotals = "Calculated Totals";
    final String feeFrequency = "FeeFrequency";
    final String feeType = "FeeType";
    final String feeValue = "FeeValue";
    final String feePercentage = "FeePercentage";

    final String feeTypePrincipalPlusInterest = "Principal+Interest";
    final String feeTypeInterest = "Interest";
    final String feeTypePrincipal = "Principal";
    final String feeTypeValue = "Value";
    final String account999 = "Account 999";
    String rootPath = "org/mifos/accounts/loan/business/testCaseData/";

    LoanOfferingBO loanOffering = null;

    // TODO: probably should be of type LoanBO
    protected AccountBO accountBO = null;

    protected AccountBO badAccountBO = null;

    protected CustomerBO center = null;

    protected CustomerBO group = null;

    private CustomerBO client = null;
    private BigDecimal savedInitialRoundOffMultiple = null;
    private BigDecimal savedFinalRoundOffMultiple = null;
    private RoundingMode savedCurrencyRoundingMode = null;
    private RoundingMode savedInitialRoundingMode = null;
    private RoundingMode savedFinalRoundingMode = null;
    private Short savedDigitAfterDecimal;
    private int savedDaysInYear = 0;

    private UserContext userContext;
    private boolean allConsoleOutputEnabled = false;
    private boolean isFileNameConsoleOutputEnabled = false;

    private LoanDao loanDao;

    @Override
    public void setUp() throws Exception {
        userContext = TestObjectFactory.getContext();

        savedInitialRoundOffMultiple = AccountingRules.getInitialRoundOffMultiple();
        savedFinalRoundOffMultiple = AccountingRules.getFinalRoundOffMultiple();
        savedCurrencyRoundingMode = AccountingRules.getCurrencyRoundingMode();
        savedDigitAfterDecimal = AccountingRules.getDigitsAfterDecimal();
        savedInitialRoundingMode = AccountingRules.getInitialRoundingMode();
        savedFinalRoundingMode = AccountingRules.getFinalRoundingMode();
        savedDaysInYear = AccountingRules.getNumberOfInterestDays();

        loanDao = new LoanDao();
    }

    @Override
    public void tearDown() throws Exception {
        TestObjectFactory.removeObject(loanOffering);
        if (accountBO != null)
            accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
        if (badAccountBO != null)
            badAccountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                    badAccountBO.getAccountId());
        if (group != null)
            group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
        if (center != null)
            center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
        TestObjectFactory.cleanUp(accountBO);
        TestObjectFactory.cleanUp(badAccountBO);
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);

        StaticHibernateUtil.closeSession();
        // Money.setUsingNewMoney(false);
        // LoanBO.setUsingNewLoanSchedulingMethod(false);
        AccountingRules.setInitialRoundOffMultiple(savedInitialRoundOffMultiple);
        AccountingRules.setFinalRoundOffMultiple(savedFinalRoundOffMultiple);
        AccountingRules.setCurrencyRoundingMode(savedCurrencyRoundingMode);
        AccountingRules.setDigitsAfterDecimal(savedDigitAfterDecimal);
        AccountingRules.setInitialRoundingMode(savedInitialRoundingMode);
        AccountingRules.setFinalRoundingMode(savedFinalRoundingMode);
        setNumberOfInterestDays(savedDaysInYear);
    }

    /* This part is for the testing of 999 account */
    /****************************************************************************/
    /****************************************************************************/
    /****************************************************************************/
    /****************************************************************************/

    private void runOne999AccountTestCaseWithDataFromSpreadSheetForLastPaymentReversal(String fileName,
            int expected999AccountTransactions, int paymentToReverse, boolean payLastPayment)
            throws NumberFormatException, PropertyNotFoundException, SystemException, ApplicationException,
            URISyntaxException {

        LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
        Results calculatedResults = new Results();
        InternalConfiguration config = testCaseData.getInternalConfig();
        LoanParameters loanParams = testCaseData.getLoanParams();
        accountBO = setUpLoanFor999AccountTestLastPaymentReversal(config, loanParams, calculatedResults,
                paymentToReverse, payLastPayment);

        List<AccountPaymentEntity> paymentList = ((LoanBO) accountBO).getAccountPayments();
        int transactionCount = 0;
        for (Iterator<AccountPaymentEntity> paymentIterator = paymentList.iterator(); paymentIterator.hasNext();) {
            AccountPaymentEntity payment = paymentIterator.next();
            Set<AccountTrxnEntity> transactionList = payment.getAccountTrxns();
            for (AccountTrxnEntity transaction : transactionList) {
                Set<FinancialTransactionBO> list = ((LoanTrxnDetailEntity) transaction).getFinancialTransactions();
                for (Iterator<FinancialTransactionBO> iterator = list.iterator(); iterator.hasNext();) {
                    FinancialTransactionBO financialTransaction = iterator.next();
                    if (financialTransaction.getPostedAmount().equals(testCaseData.getExpectedResult().getAccount999())
                            || financialTransaction.getPostedAmount().negate().equals(
                                    testCaseData.getExpectedResult().getAccount999())) {
                        transactionCount++;
                        String debitOrCredit = "Credit";
                        if (financialTransaction.getDebitCreditFlag() == 0)
                            debitOrCredit = "Debit";
                        if (isAllConsoleOutputEnabled()) {
                            System.out.println("Posted amount: "
                                    + financialTransaction.getPostedAmount() + " Debit/Credit: "
                                    + debitOrCredit + " GLCode: " + financialTransaction.getGlcode().getGlcode()
                                    + " Transaction Id: " + financialTransaction.getTrxnId());
                        }
                    }

                }
            }
        }

       Assert.assertEquals(transactionCount, expected999AccountTransactions);

    }

    private void runOne999AccountTestCaseWithDataFromSpreadSheetForLoanReversal(String fileName,
            int expected999AccountTransactions, int paymentToReverse, boolean payLastPayment)
            throws NumberFormatException, PropertyNotFoundException, SystemException, ApplicationException,
            URISyntaxException {

        LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
        Results calculatedResults = new Results();
        InternalConfiguration config = testCaseData.getInternalConfig();
        LoanParameters loanParams = testCaseData.getLoanParams();
        accountBO = setUpLoanFor999AccountTestLoanReversal(config, loanParams, calculatedResults, paymentToReverse,
                payLastPayment);

        List<AccountPaymentEntity> paymentList = ((LoanBO) accountBO).getAccountPayments();
        int transactionCount = 0;
        for (Iterator<AccountPaymentEntity> paymentIterator = paymentList.iterator(); paymentIterator.hasNext();) {
            AccountPaymentEntity payment = paymentIterator.next();
            Set<AccountTrxnEntity> transactionList = payment.getAccountTrxns();
            for (AccountTrxnEntity transaction : transactionList) {
                Set<FinancialTransactionBO> list = ((LoanTrxnDetailEntity) transaction).getFinancialTransactions();
                for (Iterator<FinancialTransactionBO> iterator = list.iterator(); iterator.hasNext();) {
                    FinancialTransactionBO financialTransaction = iterator.next();
                    if (financialTransaction.getPostedAmount().equals(testCaseData.getExpectedResult().getAccount999())
                            || financialTransaction.getPostedAmount().negate().equals(
                                    testCaseData.getExpectedResult().getAccount999())) {
                        transactionCount++;
                        String debitOrCredit = "Credit";
                        if (financialTransaction.getDebitCreditFlag() == 0)
                            debitOrCredit = "Debit";
                        if (isAllConsoleOutputEnabled()) {
                            System.out.println("Posted amount: "
                                    + financialTransaction.getPostedAmount() + " Debit/Credit: "
                                    + debitOrCredit + " GLCode: " + financialTransaction.getGlcode().getGlcode()
                                    + " Transaction Id: " + financialTransaction.getTrxnId());
                        }
                    }

                }
            }
        }

       Assert.assertEquals(transactionCount, expected999AccountTransactions);

    }

    private void verifyReversedLastPaymentLoanSchedules(LoanScheduleEntity[] schedules, Results expectedResults) {
        List<PaymentDetail> list = expectedResults.getPayments();
       Assert.assertEquals(list.size(), schedules.length);
        for (int i = 0; i < schedules.length; i++) {
            if (i == schedules.length - 1) {
                Money zeroAmount = new Money(getCurrency(), "0");
               Assert.assertEquals(schedules[i].getPrincipalPaid(), zeroAmount);
               Assert.assertEquals(schedules[i].getPaymentDate(), null);
               Assert.assertEquals(schedules[i].isPaid(), false);
            } else {
               Assert.assertEquals(schedules[i].isPaid(), true);
            }
            verifyScheduleAndPaymentDetail(schedules[i], list.get(i));
        }

    }

    private void setUpLoanAndVerifyForScheduleGenerationWhenLastPaymentIsReversed(InternalConfiguration config,
            LoanParameters loanParams, Results expectedResults) throws AccountException, PersistenceException,
            MeetingException {

        accountBO = setUpLoanFor999Account(config, loanParams);
        PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = accountBO.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams
                .getNumberOfPayments());
        // before any payment is made
        printLoanScheduleEntities(paymentsArray);
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(userContext.getId());

        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();

        for (int i = 0; i < paymentsArray.length; i++) {
            loanSchedule = paymentsArray[i];
            Money amountPaid = loanSchedule.getTotalDueWithFees();
            paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule
                    .getActionDate());
            accountBO.applyPayment(paymentData, true);
        }

        new TestObjectPersistence().persist(accountBO);
        actionDateEntities = accountBO.getAccountActionDates();
        paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
        // after all payments are made
        printLoanScheduleEntities(paymentsArray);
        List<AccountPaymentEntity> accountPayments = new LoanPersistence().retrieveAllAccountPayments(accountBO
                .getAccountId());
        accountBO.setAccountPayments(accountPayments);
        accountBO.adjustLastPayment("Adjust last payment");
        new TestObjectPersistence().persist(accountBO);
        accountPayments = new LoanPersistence().retrieveAllAccountPayments(accountBO.getAccountId());
       Assert.assertEquals(accountPayments.get(0).getAmount(), new Money(getCurrency(), "0")); // this
        // is
        // the
        // last
        // payment
        // reversed
        // so
        // amount
        // is
        // 0
        actionDateEntities = accountBO.getAccountActionDates();
        paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
        // after last payment is reversed
        printLoanScheduleEntities(paymentsArray);
        verifyReversedLastPaymentLoanSchedules(paymentsArray, expectedResults);

    }

    private void runOneLoanScheduleGenerationForLastPaymentReversal(String fileName) throws NumberFormatException,
            SystemException, ApplicationException, URISyntaxException

    {

        LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
        InternalConfiguration config = testCaseData.getInternalConfig();
        LoanParameters loanParams = testCaseData.getLoanParams();
        setUpLoanAndVerifyForScheduleGenerationWhenLastPaymentIsReversed(config, loanParams,
                testCaseData.expectedResult);

    }

    private void runOneLoanScheduleGenerationForLoanReversal(String fileName) throws NumberFormatException,
            PropertyNotFoundException, SystemException, ApplicationException, URISyntaxException

    {

        LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
        InternalConfiguration config = testCaseData.getInternalConfig();
        LoanParameters loanParams = testCaseData.getLoanParams();
        setUpLoanAndVerifyForScheduleGenerationWhenLoanIsReversed(config, loanParams, testCaseData.expectedResult);

    }

    private void verifyScheduleAndPaymentDetail(LoanScheduleEntity schedule, PaymentDetail payment) {
       Assert.assertEquals(schedule.getPrincipal(), payment.getPrincipal());
       Assert.assertEquals(schedule.getInterest(), payment.getInterest());
       Assert.assertEquals(schedule.getTotalFeeDue(), payment.getFee());
    }

    private void verifyReversedLoanSchedules(LoanScheduleEntity[] schedules, Results expectedResults) {
        List<PaymentDetail> list = expectedResults.getPayments();
       Assert.assertEquals(list.size(), schedules.length);
        for (int i = 0; i < schedules.length; i++) {
            Money zeroAmount = new Money(getCurrency(), "0");
           Assert.assertEquals(schedules[i].getPrincipalPaid(), zeroAmount);
           Assert.assertEquals(schedules[i].getPaymentDate(), null);
           Assert.assertEquals(schedules[i].isPaid(), false);
            verifyScheduleAndPaymentDetail(schedules[i], list.get(i));
        }

    }

    private void setUpLoanAndVerifyForScheduleGenerationWhenLoanIsReversed(InternalConfiguration config,
            LoanParameters loanParams, Results expectedResults) throws AccountException, PersistenceException,
            MeetingException {

        accountBO = setUpLoanFor999Account(config, loanParams);
        PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = accountBO.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams
                .getNumberOfPayments());
        // before any payment is made
        printLoanScheduleEntities(paymentsArray);
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(userContext.getId());

        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();

        for (int i = 0; i < paymentsArray.length; i++) {
            loanSchedule = paymentsArray[i];
            Money amountPaid = loanSchedule.getTotalDueWithFees();
            paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule
                    .getActionDate());
            accountBO.applyPayment(paymentData, true);
        }

        new TestObjectPersistence().persist(accountBO);
        actionDateEntities = accountBO.getAccountActionDates();
        paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
        // after all payments are made
        printLoanScheduleEntities(paymentsArray);
        // reverse loan
        ((LoanBO) accountBO).reverseLoanDisbursal(personnelBO, "Reverse this loan for testing");
        new TestObjectPersistence().persist(accountBO);
        actionDateEntities = accountBO.getAccountActionDates();
        paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
        verifyReversedLoanSchedules(paymentsArray, expectedResults);
        List<AccountPaymentEntity> accountPayments = new LoanPersistence().retrieveAllAccountPayments(accountBO
                .getAccountId());
        // every payment is reversed so amount is 0
        for (AccountPaymentEntity payment : accountPayments)
           Assert.assertEquals(payment.getAmount(), new Money(getCurrency(), "0"));

    }

    private AccountBO setUpLoanFor999Account(InternalConfiguration config, LoanParameters loanParams)
            throws AccountException, PersistenceException, MeetingException {
        setNumberOfInterestDays(config.getDaysInYear());
        AccountingRules.setDigitsAfterDecimal((short) config.getDigitsAfterDecimal());
        Money.setDefaultCurrency(AccountingRules.getMifosCurrency(new ConfigurationPersistence()));
        setInitialRoundingMode(config.getInitialRoundingMode());
        setFinalRoundingMode(config.getFinalRoundingMode());
        AccountingRules.setInitialRoundOffMultiple(new BigDecimal(config.getInitialRoundOffMultiple()));
        AccountingRules.setFinalRoundOffMultiple(new BigDecimal(config.getFinalRoundOffMultiple()));
        AccountingRules.setCurrencyRoundingMode(config.getCurrencyRoundingMode());
        // AccountingRules.setRoundingRule(config.getCurrencyRoundingMode());

        /*
         * When constructing a "meeting" here, it looks like the frequency
         * should be "EVERY_X" for weekly or monthly loan interest posting.
         */
        // EVERY_WEEK, EVERY_DAY and EVERY_MONTH are defined as 1
        MeetingBO meeting = null;
        if (loanParams.getPaymentFrequency() == RecurrenceType.MONTHLY) {
            meeting = new MeetingBO((short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    TestObjectFactory.EVERY_MONTH, new Date(), CUSTOMER_MEETING, "meeting place");
        } else {
            meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(loanParams
                    .getPaymentFrequency(), EVERY_MONTH, CUSTOMER_MEETING));
        }

        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);

        Date startDate = new Date(System.currentTimeMillis());

        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(this.getClass().getSimpleName() + " Loan", "L", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, Double.parseDouble(loanParams.getPrincipal()), Double.parseDouble(loanParams
                        .getAnnualInterest()), loanParams.getNumberOfPayments(), loanParams.getLoanType(), false,
                false, center.getCustomerMeeting().getMeeting(), config.getGracePeriodType(), "1", "1");

        List<FeeView> feeViewList = createFeeViews(config, loanParams, meeting);

        AccountBO loan = loanDao.createLoan(TestUtils.makeUser(), loanOffering, group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(getCurrency(), loanParams.getPrincipal()), loanParams
                        .getNumberOfPayments(), startDate, false, Double.parseDouble(loanParams.getAnnualInterest()),
                config.getGracePeriod(), null, feeViewList, null, DOUBLE_ZERO, DOUBLE_ZERO, SHORT_ZERO,
                SHORT_ZERO, false);

        return loan;

    }

    private AccountBO setUpLoanFor999AccountTestLoanReversal(InternalConfiguration config, LoanParameters loanParams,
            Results calculatedResults, int paymentToReverse, boolean payLastPayment) throws AccountException,
            PersistenceException, MeetingException {

        AccountBO loan = setUpLoanFor999Account(config, loanParams);

        PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = loan.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams
                .getNumberOfPayments());
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(userContext.getId());

        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();

        for (int i = 0; i < paymentsArray.length; i++) {
            loanSchedule = paymentsArray[i];
            Money amountPaid = loanSchedule.getTotalDueWithFees();
            paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule
                    .getActionDate());
            loan.applyPayment(paymentData, true);
            if (i == (paymentToReverse - 1))
                break;
        }

        boolean lastPayment = paymentToReverse == paymentsArray.length;
        calculatedResults.setAccount999(((LoanBO) loan).calculate999Account(lastPayment));
        new TestObjectPersistence().persist(loan);
        ((LoanBO) loan).reverseLoanDisbursal(personnelBO, "Test 999 account for loan reversal");
        new TestObjectPersistence().persist(loan);

        return loan;
    }

    private AccountBO setUpLoanFor999AccountTestLastPaymentReversal(InternalConfiguration config,
            LoanParameters loanParams, Results calculatedResults, int paymentToReverse, boolean payLastPayment)
            throws AccountException, PersistenceException, MeetingException {

        AccountBO loan = setUpLoanFor999Account(config, loanParams);

        PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = loan.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams
                .getNumberOfPayments());
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(userContext.getId());

        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();

        for (int i = 0; i < paymentsArray.length; i++) {
            loanSchedule = paymentsArray[i];
            Money amountPaid = loanSchedule.getTotalDueWithFees();
            paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule
                    .getActionDate());
            loan.applyPayment(paymentData, true);
            if (i == (paymentToReverse - 1))
                break;
        }

        boolean lastPayment = paymentToReverse == paymentsArray.length;
        calculatedResults.setAccount999(((LoanBO) loan).calculate999Account(lastPayment));
        new TestObjectPersistence().persist(loan);
        actionDateEntities = loan.getAccountActionDates();
        paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
        List<AccountPaymentEntity> accountPayments = new LoanPersistence().retrieveAllAccountPayments(loan
                .getAccountId());
       Assert.assertEquals(accountPayments.size(), paymentToReverse);
        loan.setAccountPayments(accountPayments);
        loan.adjustLastPayment("Adjust last payment");
        new TestObjectPersistence().persist(loan);
        accountPayments = new LoanPersistence().retrieveAllAccountPayments(loan.getAccountId());
       Assert.assertEquals(accountPayments.get(0).getAmount(), new Money(getCurrency(), "0")); // this
        // is
        // the
        // last
        // payment
        // reversed
        // so
        // amount
        // is
        // 0
        actionDateEntities = loan.getAccountActionDates();
        paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
       Assert.assertEquals(paymentsArray[paymentToReverse - 1].getPrincipalPaid(), new Money(getCurrency(), "0"));

        if (payLastPayment) {
            for (int i = paymentToReverse - 1; i < paymentsArray.length; i++) {
                loanSchedule = paymentsArray[i];
                Money amountPaid = loanSchedule.getTotalDueWithFees();
                paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule
                        .getActionDate());
                loan.applyPayment(paymentData, true);
                new TestObjectPersistence().persist(loan);
            }

        }

        return loan;
    }

    private void setUpLoanAndVerify999AccountWhenLoanIsRepaid(InternalConfiguration config, LoanParameters loanParams,
            Results expectedResults) throws AccountException, PersistenceException, MeetingException {

        accountBO = setUpLoanFor999Account(config, loanParams);
        PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = accountBO.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams
                .getNumberOfPayments());
        // before any payment is made
        printLoanScheduleEntities(paymentsArray);
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(userContext.getId());

        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();
        // pay one payment
        for (int i = 0; i < 1; i++) {
            loanSchedule = paymentsArray[i];
            Money amountPaid = loanSchedule.getPrincipal().add(loanSchedule.getInterest());
            paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule
                    .getActionDate());
            accountBO.applyPayment(paymentData, true);
        }
        new TestObjectPersistence().persist(accountBO);
        actionDateEntities = accountBO.getAccountActionDates();
        paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
        printLoanScheduleEntities(paymentsArray);
        // loan repay
        UserContext uc = TestUtils.makeUser();
        ((LoanBO) accountBO).makeEarlyRepayment(((LoanBO) accountBO).getTotalEarlyRepayAmount(), null, null, "1", uc
                .getId());
        new TestObjectPersistence().persist(accountBO);
        actionDateEntities = accountBO.getAccountActionDates();
        paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
        printLoanScheduleEntities(paymentsArray);
        // no 999 account is logged
        List<AccountPaymentEntity> paymentList = ((LoanBO) accountBO).getAccountPayments();
        int i = 0;
        for (Iterator<AccountPaymentEntity> paymentIterator = paymentList.iterator(); paymentIterator.hasNext();) {
            AccountPaymentEntity payment = paymentIterator.next();
            Set<AccountTrxnEntity> transactionList = payment.getAccountTrxns();
            for (AccountTrxnEntity transaction : transactionList) {
                Set<FinancialTransactionBO> list = ((LoanTrxnDetailEntity) transaction).getFinancialTransactions();
                for (Iterator<FinancialTransactionBO> iterator = list.iterator(); iterator.hasNext();) {
                    FinancialTransactionBO financialTransaction = iterator.next();
                    if (financialTransaction.getPostedAmount().equals(expectedResults.getAccount999())
                            || financialTransaction.getPostedAmount().negate().equals(expectedResults.getAccount999())) {
                        i++;
                    }

                }
            }
        }
       Assert.assertEquals(i, 0);

    }

    private void run999AccountWhenLoanIsRepaid(String fileName) throws NumberFormatException,
            PropertyNotFoundException, SystemException, ApplicationException, URISyntaxException {

        LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
        InternalConfiguration config = testCaseData.getInternalConfig();
        LoanParameters loanParams = testCaseData.getLoanParams();
        setUpLoanAndVerify999AccountWhenLoanIsRepaid(config, loanParams, testCaseData.expectedResult);
    }

    public void testDecliningInterestEPITestCases() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningEPI/";
        String[] dataFileNames = getCSVFiles(rootPath);
        for (int i = 0; i < dataFileNames.length; i++) {
            if (fileNameContains(dataFileNames[i], decliningEPIGraceFeeTestCases)) {
                runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
                tearDown();
                setUp();
            }
        }
    }

    // decliningEPI
    public void testDecliningEPISoham25Installments1DigitAfterDecimal() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningEPI/";
        runOneTestCaseWithDataFromSpreadSheet(rootPath, "decliningEPI-Soham25Installments1DigitAfterDecimal.csv");

    }

    public void testDecliningEPISoham60Installments1DigitAfterDecimal() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningEPI/";
        runOneTestCaseWithDataFromSpreadSheet(rootPath, "decliningEPI-Soham60Installments1DigitAfterDecimal.csv");

    }

    public void testDecliningEPISoham25Installments() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningEPI/";
        runOneTestCaseWithDataFromSpreadSheet(rootPath, "decliningEPI-Soham25Installments.csv");

    }

    public void testDecliningEPISoham60Installments() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningEPI/";
        runOneTestCaseWithDataFromSpreadSheet(rootPath, "decliningEPI-Soham60Installments.csv");
    }

    public void testDecliningEPINoFeeNoGrace() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningEPI/";
        runOneTestCaseWithDataFromSpreadSheet(rootPath, "decliningEPI-NoFee-NoGrace.csv");
    }

    public void testDecliningEPIWithFeeWithGrace() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningEPI/";
        runOneTestCaseWithDataFromSpreadSheet(rootPath, "decliningEPI-WithFee-WithGrace.csv");
    }

    public void testDecliningEPINoFeeWithGrace() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningEPI/";
        runOneTestCaseWithDataFromSpreadSheet(rootPath, "decliningEPI-NoFee-WithGrace.csv");
    }

    public void test999AccountLoansWithFees() throws NumberFormatException, PropertyNotFoundException, SystemException,
            ApplicationException, URISyntaxException, Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningInterest/";
        String[] dataFileNames = getCSVFiles(rootPath);
        for (int i = 0; i < dataFileNames.length; i++) {
            if (dataFileNames[i].startsWith("testcase-2008-05-13-declining-grace-fee-set1")) {
                runOne999AccountTestCaseLoanWithFees(rootPath + dataFileNames[i]);
                tearDown();
                setUp();
            }
        }
    }

    public void test999AccountLoansWithFees2() throws NumberFormatException, PropertyNotFoundException,
            SystemException, ApplicationException, URISyntaxException, Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/flatInterest/";
        String[] dataFileNames = getCSVFiles(rootPath);
        for (int i = 0; i < dataFileNames.length; i++) {
            if (dataFileNames[i].startsWith("testcase") && dataFileNames[i].contains("flat-grace-fee-set")) {
                runOne999AccountTestCaseLoanWithFees(rootPath + dataFileNames[i]);
                tearDown();
                setUp();
            }
        }

    }

    // verify that 999 account transactions are logged after last payment is
    // made
    public void testPositive999AccountTest2LoanWithFees() throws NumberFormatException, PropertyNotFoundException,
            SystemException, ApplicationException, URISyntaxException {
        String dataFileName = "account999-withfees.csv";
        runOne999AccountTestCaseLoanWithFees(rootPath + dataFileName);
    }

    public void test999AccountWhenLoanIsRepaid() throws NumberFormatException, PropertyNotFoundException,
            SystemException, ApplicationException, URISyntaxException, Exception {
        String dataFileName = "account999-test3.csv";
        run999AccountWhenLoanIsRepaid(rootPath + dataFileName);
    }

    public void test999AccountForLoanReversal() throws NumberFormatException, PropertyNotFoundException,
            SystemException, ApplicationException, URISyntaxException, Exception {
        String dataFileName = "account999-test3.csv";
        int expected999AccountTransactions = 4;
        int paymentToReverse = 3;
        boolean payLastPayment = false;
        runOne999AccountTestCaseWithDataFromSpreadSheetForLoanReversal(rootPath + dataFileName,
                expected999AccountTransactions, paymentToReverse, payLastPayment);
    }

    public void test999AccountForLastPaymentReversal() throws NumberFormatException, PropertyNotFoundException,
            SystemException, ApplicationException, URISyntaxException, Exception {
        String dataFileName = "account999-test3.csv";
        int expected999AccountTransactions = 4;
        int paymentToReverse = 3;
        boolean payLastPayment = false;
        runOne999AccountTestCaseWithDataFromSpreadSheetForLastPaymentReversal(rootPath + dataFileName,
                expected999AccountTransactions, paymentToReverse, payLastPayment);
    }

    public void testLoanScheduleGenerationWhenLastPaymentIsReversed() throws NumberFormatException,
            PropertyNotFoundException, SystemException, ApplicationException, URISyntaxException, Exception {
        String dataFileName = "account999-test3.csv";
        runOneLoanScheduleGenerationForLastPaymentReversal(rootPath + dataFileName);
    }

    public void testLoanScheduleGenerationWhenLoanIsReversed() throws NumberFormatException, PropertyNotFoundException,
            SystemException, ApplicationException, URISyntaxException, Exception {
        String dataFileName = "account999-test3.csv";
        runOneLoanScheduleGenerationForLoanReversal(rootPath + dataFileName);
    }

    // verify that 999 account transactions are logged after last payment is
    // made
    public void test999Account() throws NumberFormatException, PropertyNotFoundException, SystemException,
            ApplicationException, URISyntaxException {
        String dataFileName = "account999-test2.csv";
        runOne999AccountTestCaseWithDataFromSpreadSheet(rootPath + dataFileName);
    }

    // verify that 999 account transactions are logged after last payment is
    // made
    public void test999AccountTest1() throws NumberFormatException, PropertyNotFoundException, SystemException,
            ApplicationException, URISyntaxException {
        String dataFileName = "account999-test1.csv";
        runOne999AccountTestCaseWithDataFromSpreadSheet(rootPath + dataFileName);
    }

    // no 999account should be logged in this case
    public void test999AccountForMiddlePaymentReversal() throws NumberFormatException, PropertyNotFoundException,
            SystemException, ApplicationException, URISyntaxException, Exception {
        String dataFileName = "account999-test3.csv";
        int expected999AccountTransactions = 0;
        int paymentToReverse = 2;
        boolean payLastPayment = false;
        runOne999AccountTestCaseWithDataFromSpreadSheetForLastPaymentReversal(rootPath + dataFileName,
                expected999AccountTransactions, paymentToReverse, payLastPayment);
    }

    // payment is reversed and repay to the last payment
    public void test999AccountForMiddlePaymentReversalAndPayToLastPayment() throws NumberFormatException,
            PropertyNotFoundException, SystemException, ApplicationException, URISyntaxException {
        String dataFileName = "account999-test3.csv";
        int expected999AccountTransactions = 2;
        int paymentToReverse = 2;
        boolean payLastPayment = true;
        runOne999AccountTestCaseWithDataFromSpreadSheetForLastPaymentReversal(rootPath + dataFileName,
                expected999AccountTransactions, paymentToReverse, payLastPayment);
    }

    private AccountBO setUpLoanFor999AccountTest(InternalConfiguration config, LoanParameters loanParams,
            Results calculatedResults) throws AccountException, PersistenceException, MeetingException {
        setNumberOfInterestDays(config.getDaysInYear());
        AccountingRules.setDigitsAfterDecimal((short) config.getDigitsAfterDecimal());
        Money.setDefaultCurrency(AccountingRules.getMifosCurrency(new ConfigurationPersistence()));
        setInitialRoundingMode(config.getInitialRoundingMode());
        setFinalRoundingMode(config.getFinalRoundingMode());
        AccountingRules.setInitialRoundOffMultiple(new BigDecimal(config.getInitialRoundOffMultiple()));
        AccountingRules.setFinalRoundOffMultiple(new BigDecimal(config.getFinalRoundOffMultiple()));
        AccountingRules.setCurrencyRoundingMode(config.getCurrencyRoundingMode());
        // AccountingRules.setRoundingRule(config.getCurrencyRoundingMode());

        /*
         * When constructing a "meeting" here, it looks like the frequency
         * should be "EVERY_X" for weekly or monthly loan interest posting.
         */
        // EVERY_WEEK, EVERY_DAY and EVERY_MONTH are defined as 1
        MeetingBO meeting = null;
        if (loanParams.getPaymentFrequency() == RecurrenceType.MONTHLY) {
            meeting = new MeetingBO((short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    TestObjectFactory.EVERY_MONTH, new Date(), CUSTOMER_MEETING, "meeting place");
        } else {
            meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(loanParams
                    .getPaymentFrequency(), EVERY_MONTH, CUSTOMER_MEETING));
        }

        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);

        Date startDate = new Date(System.currentTimeMillis());

        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(this.getClass().getSimpleName() + " Loan", "L", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, Double.parseDouble(loanParams.getPrincipal()), Double.parseDouble(loanParams
                        .getAnnualInterest()), loanParams.getNumberOfPayments(), loanParams.getLoanType(), false,
                false, center.getCustomerMeeting().getMeeting(), config.getGracePeriodType(), "1", "1");

        List<FeeView> feeViewList = createFeeViews(config, loanParams, meeting);

        AccountBO loan = loanDao.createLoan(TestUtils.makeUser(), loanOffering, group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(getCurrency(), loanParams.getPrincipal()), loanParams
                        .getNumberOfPayments(), startDate, false, Double.parseDouble(loanParams.getAnnualInterest()),
                config.getGracePeriod(), null, feeViewList, null, DOUBLE_ZERO, DOUBLE_ZERO, SHORT_ZERO,
                SHORT_ZERO, false);

        PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = loan.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams
                .getNumberOfPayments());
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(userContext.getId());

        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();
        for (int i = 0; i < paymentsArray.length; i++) {
            loanSchedule = paymentsArray[i];
            Money amountPaid = loanSchedule.getTotalDueWithFees();
            paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule
                    .getActionDate());
            loan.applyPayment(paymentData, true);
        }
        boolean lastPayment = true;
        calculatedResults.setAccount999(((LoanBO) loan).calculate999Account(lastPayment));
        new TestObjectPersistence().persist(loan);
        return loan;
    }

    private AccountBO setUpLoanFor999AccountTestLoanWithFees(InternalConfiguration config, LoanParameters loanParams,
            Results calculatedResults) throws AccountException, PersistenceException, MeetingException {
        setNumberOfInterestDays(config.getDaysInYear());
        AccountingRules.setDigitsAfterDecimal((short) config.getDigitsAfterDecimal());
        Money.setDefaultCurrency(AccountingRules.getMifosCurrency(new ConfigurationPersistence()));
        setInitialRoundingMode(config.getInitialRoundingMode());
        setFinalRoundingMode(config.getFinalRoundingMode());
        AccountingRules.setInitialRoundOffMultiple(new BigDecimal(config.getInitialRoundOffMultiple()));
        AccountingRules.setFinalRoundOffMultiple(new BigDecimal(config.getFinalRoundOffMultiple()));
        AccountingRules.setCurrencyRoundingMode(config.getCurrencyRoundingMode());
        // AccountingRules.setRoundingRule(config.getCurrencyRoundingMode());

        /*
         * When constructing a "meeting" here, it looks like the frequency
         * should be "EVERY_X" for weekly or monthly loan interest posting.
         */
        // EVERY_WEEK, EVERY_DAY and EVERY_MONTH are defined as 1
        MeetingBO meeting = null;
        if (loanParams.getPaymentFrequency() == RecurrenceType.MONTHLY) {
            meeting = new MeetingBO((short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    TestObjectFactory.EVERY_MONTH, new Date(), CUSTOMER_MEETING, "meeting place");
        } else {
            meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(loanParams
                    .getPaymentFrequency(), EVERY_MONTH, CUSTOMER_MEETING));
        }

        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);

        Date startDate = new Date(System.currentTimeMillis());

        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(this.getClass().getSimpleName() + " Loan", "L", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, Double.parseDouble(loanParams.getPrincipal()), Double.parseDouble(loanParams
                        .getAnnualInterest()), loanParams.getNumberOfPayments(), loanParams.getLoanType(), false,
                false, center.getCustomerMeeting().getMeeting(), config.getGracePeriodType(), "1", "1");

        // loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
        List<FeeView> feeViewList = createFeeViews(config, loanParams, meeting);

        AccountBO loan = loanDao.createLoan(TestUtils.makeUser(), loanOffering, group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(getCurrency(), loanParams.getPrincipal()), loanParams
                        .getNumberOfPayments(), startDate, false, Double.parseDouble(loanParams.getAnnualInterest()),
                config.getGracePeriod(), null, feeViewList, null, DOUBLE_ZERO, DOUBLE_ZERO, SHORT_ZERO,
                SHORT_ZERO, false);

        PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = loan.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams
                .getNumberOfPayments());
        PersonnelBO personnelBO = new PersonnelPersistence().getPersonnel(userContext.getId());
        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();
        for (int i = 0; i < paymentsArray.length; i++) {
            loanSchedule = paymentsArray[i];
            Money amountPaid = loanSchedule.getTotalDueWithFees();
            paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule
                    .getActionDate());
            loan.applyPayment(paymentData, true);
        }
        boolean lastPayment = true;
        calculatedResults.setAccount999(((LoanBO) loan).calculate999Account(lastPayment));
        new TestObjectPersistence().persist(loan);
        return loan;
    }

    private void compare999Account(Money expected999Account, Money calculated999Account, String testName) {
        if (isAllConsoleOutputEnabled()) {
            System.out.println("Running test: " + testName);
            System.out.println("Results   (Expected : Calculated : Difference)");
            printComparison("999 Account:   ", expected999Account, calculated999Account);
        }
       Assert.assertEquals(expected999Account, calculated999Account);
    }

    private void runOne999AccountTestCaseWithDataFromSpreadSheet(String fileName) throws NumberFormatException,
            PropertyNotFoundException, SystemException, ApplicationException, URISyntaxException {

        LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
        Results calculatedResults = new Results();
        InternalConfiguration config = testCaseData.getInternalConfig();
        LoanParameters loanParams = testCaseData.getLoanParams();
        accountBO = setUpLoanFor999AccountTest(config, loanParams, calculatedResults);
        AccountPaymentEntity lastPmt = null;
        List<AccountPaymentEntity> paymentList = ((LoanBO) accountBO).getAccountPayments();
        int i = 1;
        for (Iterator<AccountPaymentEntity> paymentIterator = paymentList.iterator(); paymentIterator.hasNext();) {
            AccountPaymentEntity payment = paymentIterator.next();
            if (i == loanParams.getNumberOfPayments()) {
                lastPmt = payment;
                break;
            }
            i++;
        }
        Set<AccountTrxnEntity> transactionList = lastPmt.getAccountTrxns();
        for (AccountTrxnEntity transaction : transactionList) {
            Set<FinancialTransactionBO> list = ((LoanTrxnDetailEntity) transaction).getFinancialTransactions();
            for (Iterator<FinancialTransactionBO> iterator = list.iterator(); iterator.hasNext();) {
                FinancialTransactionBO financialTransaction = iterator.next();
                if (financialTransaction.getGlcode().getGlcodeId() == 51) {
                   Assert.assertEquals(financialTransaction.getGlcode().getGlcode(), "31401");
                    Money postedAmount = financialTransaction.getPostedAmount();
                    Money expected999Account = testCaseData.getExpectedResult().getAccount999();
                   Assert.assertEquals(removeSign(expected999Account), postedAmount);
                    if (expected999Account.isGreaterThanZero()) {
                       Assert.assertEquals(FinancialConstants.fromValue(financialTransaction.getDebitCreditFlag()),
                                FinancialConstants.CREDIT);
                    } else {
                       Assert.assertEquals(FinancialConstants.fromValue(financialTransaction.getDebitCreditFlag()),
                                FinancialConstants.DEBIT);
                    }

                }
            }
        }
        compare999Account(testCaseData.getExpectedResult().getAccount999(), calculatedResults.getAccount999(), fileName);

    }

    private void runOne999AccountTestCaseLoanWithFees(String fileName) throws NumberFormatException,
            PropertyNotFoundException, SystemException, ApplicationException, URISyntaxException {
        if (isAllConsoleOutputEnabled() || isFileNameConsoleOutputEnabled()) {
            System.out.println("Running 999 Account Test with Fees: " + fileName);
        }

        LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
        Results calculatedResults = new Results();
        InternalConfiguration config = testCaseData.getInternalConfig();
        LoanParameters loanParams = testCaseData.getLoanParams();
        accountBO = setUpLoanFor999AccountTestLoanWithFees(config, loanParams, calculatedResults);
        AccountPaymentEntity lastPmt = null;
        List<AccountPaymentEntity> paymentList = ((LoanBO) accountBO).getAccountPayments();
        int i = 1;
        for (Iterator<AccountPaymentEntity> paymentIterator = paymentList.iterator(); paymentIterator.hasNext();) {
            AccountPaymentEntity payment = paymentIterator.next();
            if (i == loanParams.getNumberOfPayments()) {
                lastPmt = payment;
                break;
            }
            i++;
        }
        Set<AccountTrxnEntity> transactionList = lastPmt.getAccountTrxns();
        for (AccountTrxnEntity transaction : transactionList) {
            Set<FinancialTransactionBO> list = ((LoanTrxnDetailEntity) transaction).getFinancialTransactions();
            for (Iterator<FinancialTransactionBO> iterator = list.iterator(); iterator.hasNext();) {
                FinancialTransactionBO financialTransaction = iterator.next();
                if (financialTransaction.getGlcode().getGlcodeId() == 51) {
                   Assert.assertEquals(financialTransaction.getGlcode().getGlcode(), "31401");
                    Money postedAmount = financialTransaction.getPostedAmount();
                    Money expected999Account = testCaseData.getExpectedResult().getAccount999();
                    if (!postedAmount.equals(removeSign(expected999Account))) {
                        System.out.println("File name: " + fileName + " posted amount: "
                                + postedAmount + " expected amount: "
                                + expected999Account);
                    }
                   Assert.assertEquals(removeSign(expected999Account), postedAmount);
                    if (expected999Account.isGreaterThanZero()) {
                       Assert.assertEquals(FinancialConstants.fromValue(financialTransaction.getDebitCreditFlag()),
                                FinancialConstants.CREDIT);
                    } else {
                       Assert.assertEquals(FinancialConstants.fromValue(financialTransaction.getDebitCreditFlag()),
                                FinancialConstants.DEBIT);
                    }

                }
            }
        }
        compare999Account(testCaseData.getExpectedResult().getAccount999(), calculatedResults.getAccount999(), fileName);

    }
    
    /**
     * Added as a fix for negative posted amounts stored in cvs for test
     * Since the MIFOS-2474 no account transaction is records -ve amount
     * @param amount
     * @return
     */
    private Money removeSign(final Money amount) {
        if (amount != null && amount.isLessThanZero()) {
            return amount.negate();
        }
        return amount;
    }

    // verify that 999 account transactions are logged after last payment is
    // made
    public void testPositive999AccountTest2() throws NumberFormatException, PropertyNotFoundException, SystemException,
            ApplicationException, URISyntaxException {
        String dataFileName = "account999-test3.csv";
        runOne999AccountTestCaseWithDataFromSpreadSheet(rootPath + dataFileName);
    }

    private void printLoanScheduleEntities(LoanScheduleEntity[] loanSchedules) {
        if (!isAllConsoleOutputEnabled())
            return;

        for (int i = 0; i < loanSchedules.length; i++) {
            System.out.println("Loan Schedule #: " + (i + 1));
            System.out.println("Principal:   " + loanSchedules[i].getPrincipal());
            System.out.println("Principal Paid:   " + loanSchedules[i].getPrincipalPaid());
            System.out.println("Interest Paid:   " + loanSchedules[i].getInterestPaid());
            System.out.println("Interest:   " + loanSchedules[i].getInterest());
            System.out.println("Total Due:   " + loanSchedules[i].getTotalDue());
            if (loanSchedules[i].getPaymentDate() != null) {
                System.out.println("Payment Date:   " + loanSchedules[i].getPaymentDate().toString());
            } else {
                System.out.println("Payment Date: null");
            }
            System.out.println("Is paid:   " + loanSchedules[i].isPaid());
        }
    }

    private boolean isAllConsoleOutputEnabled() {
        return allConsoleOutputEnabled;
    }

    private boolean isFileNameConsoleOutputEnabled() {
        return isFileNameConsoleOutputEnabled;
    }

    /* end of of 999 account testing */
    /****************************************************************************/
    /****************************************************************************/
    /****************************************************************************/
    /****************************************************************************/

    /* This part is for the new financial calculation */
    /****************************************************************************/
    /****************************************************************************/
    /****************************************************************************/
    /****************************************************************************/

    class LoanParameters {
        private String principal = null;
        private InterestType loanType = null;
        private String annualInterest = null;
        private short numberOfPayments = 0;
        private RecurrenceType paymentFrequency = null;

        public LoanParameters(String principal, InterestType loanType, String annualInterest, short numberOfPayments,
                RecurrenceType paymentFrequency) {
            super();
            this.principal = principal;
            this.loanType = loanType;
            this.annualInterest = annualInterest;
            this.numberOfPayments = numberOfPayments;
            this.paymentFrequency = paymentFrequency;
        }

        public LoanParameters() {
        }

        public String getPrincipal() {
            return principal;
        }

        public void setPrincipal(String principal) {
            this.principal = principal;
        }

        public InterestType getLoanType() {
            return loanType;
        }

        public void setLoanType(InterestType loanType) {
            this.loanType = loanType;
        }

        public String getAnnualInterest() {
            return annualInterest;
        }

        public void setAnnualInterest(String annualInterest) {
            this.annualInterest = annualInterest;
        }

        public short getNumberOfPayments() {
            return numberOfPayments;
        }

        public void setNumberOfPayments(short numberOfPayments) {
            this.numberOfPayments = numberOfPayments;
        }

        public RecurrenceType getPaymentFrequency() {
            return paymentFrequency;
        }

        public void setPaymentFrequency(RecurrenceType paymentFrequency) {
            this.paymentFrequency = paymentFrequency;
        }

    }

    class InternalConfiguration {
        private int daysInYear = 0;
        // right now we are just supporting CEILING, FLOOR, HALF_UP
        private RoundingMode initialRoundingMode = null;
        // should this be constrained to .001, .01, .5, .1, 1 as in the
        // spreadsheet?
        private String initialRoundOffMultiple = null;
        // right now we are just supporting CEILING, FLOOR, HALF_UP
        private RoundingMode finalRoundingMode = null;
        // should this be constrained to .001, .01, .5, .1, 1 as in the
        // spreadsheet?
        private String finalRoundOffMultiple = null;
        // right now we are just supporting CEILING, FLOOR, HALF_UP
        private RoundingMode currencyRoundingMode = null;
        // the number of digits to use to the right of the decimal for interal
        // caculations
        private int internalPrecision = 13;
        // digits after decimal right now is in the application configuration
        private int digitsAfterDecimal = 1;
        // grace period type
        private GraceType gracePeriodType = GraceType.NONE;
        private short gracePeriod = 0;
        private FeeFrequencyType feeFrequency; // if feeFrequency is null there
        // is no fee setting for the loan
        private FeeFormula feeType; // if rate-based fee, indicates what the
        // rate applies to
        private boolean isFeeRateBased; // true if rate based, false if applies
        // a fixed amount
        private String feeValue = null;
        private String feePercentage = null;

        public FeeFrequencyType getFeeFrequency() {
            return feeFrequency;
        }

        public void setFeeFrequency(FeeFrequencyType feeFrequency) {
            this.feeFrequency = feeFrequency;
        }

        public String getFeePercentage() {
            return feePercentage;
        }

        public void setFeePercentage(String feePercentage) {
            this.feePercentage = feePercentage;
        }

        public FeeFormula getFeeType() {
            return feeType;
        }

        public void setFeeType(FeeFormula feeType) {
            this.feeType = feeType;
        }

        public String getFeeValue() {
            return feeValue;
        }

        public void setFeeValue(String feeValue) {
            this.feeValue = feeValue;
        }

        public int getDigitsAfterDecimal() {
            return digitsAfterDecimal;
        }

        public void setDigitsAfterDecimal(int digitsAfterDecimal) {
            this.digitsAfterDecimal = digitsAfterDecimal;
        }

        public InternalConfiguration(int daysInYear, RoundingMode initialRoundingMode, String initialRoundOffMultiple,
                RoundingMode finalRoundingMode, String finalRoundOffMultiple, RoundingMode currencyRoundingMode,
                int internalPrecision, GraceType gracePeriodType, short gracePeriod) {
            super();
            this.daysInYear = daysInYear;
            this.initialRoundingMode = initialRoundingMode;
            this.initialRoundOffMultiple = initialRoundOffMultiple;
            this.finalRoundingMode = finalRoundingMode;
            this.finalRoundOffMultiple = finalRoundOffMultiple;
            this.currencyRoundingMode = currencyRoundingMode;
            this.internalPrecision = internalPrecision;
            this.gracePeriodType = gracePeriodType;
            this.gracePeriod = gracePeriod;
        }

        public InternalConfiguration() {
        }

        public int getDaysInYear() {
            return daysInYear;
        }

        public void setDaysInYear(int daysInYear) {
            this.daysInYear = daysInYear;
        }

        public RoundingMode getInitialRoundingMode() {
            return initialRoundingMode;
        }

        public void setInitialRoundingMode(RoundingMode initialRoundingMode) {
            this.initialRoundingMode = initialRoundingMode;
        }

        public String getInitialRoundOffMultiple() {
            return initialRoundOffMultiple;
        }

        public void setInitialRoundOffMultiple(String initialRoundOffMultiple) {
            this.initialRoundOffMultiple = initialRoundOffMultiple;
        }

        public RoundingMode getFinalRoundingMode() {
            return finalRoundingMode;
        }

        public void setFinalRoundingMode(RoundingMode finalRoundingMode) {
            this.finalRoundingMode = finalRoundingMode;
        }

        public String getFinalRoundOffMultiple() {
            return finalRoundOffMultiple;
        }

        public void setFinalRoundOffMultiple(String finalRoundOffMultiple) {
            this.finalRoundOffMultiple = finalRoundOffMultiple;
        }

        public RoundingMode getCurrencyRoundingMode() {
            return currencyRoundingMode;
        }

        public void setCurrencyRoundingMode(RoundingMode currencyRoundingMode) {
            this.currencyRoundingMode = currencyRoundingMode;
        }

        public int getInternalPrecision() {
            return internalPrecision;
        }

        public void setInternalPrecision(int internalPrecision) {
            this.internalPrecision = internalPrecision;
        }

        public short getGracePeriod() {
            return gracePeriod;
        }

        public void setGracePeriod(short gracePeriod) {
            this.gracePeriod = gracePeriod;
        }

        public GraceType getGracePeriodType() {
            return gracePeriodType;
        }

        public void setGracePeriodType(GraceType gracePeriodType) {
            this.gracePeriodType = gracePeriodType;
        }

        public boolean isFeeRateBased() {
            return this.isFeeRateBased;
        }

        public void setIsFeeRateBased(boolean isRateBased) {
            this.isFeeRateBased = isRateBased;
        }
    }

    class Results {
        // each installment has payment = interest + principal
        Money totalPayments = null; // sum of all payments
        Money totalInterest = null; // sum of all interests for all payments
        Money totalFee = null;
        Money totalPrincipal = null; // sum of all principals for all payments
        // detailed list of all payments. Each payment includes payment,
        // interest, principal and balance
        List<PaymentDetail> payments = null;
        Money roundedTotalInterest = null;
        Money account999 = null;

        public List<PaymentDetail> getPayments() {
            return payments;
        }

        public void setPayments(List<PaymentDetail> payments) {
            this.payments = payments;
        }

        public Money getTotalInterest() {
            return totalInterest;
        }

        public void setTotalInterest(Money totalInterest) {
            this.totalInterest = totalInterest;
        }

        public Money getTotalPayments() {
            return totalPayments;
        }

        public void setTotalPayments(Money totalPayments) {
            this.totalPayments = totalPayments;
        }

        public Money getTotalPrincipal() {
            return totalPrincipal;
        }

        public void setTotalPrincipal(Money totalPrincipal) {
            this.totalPrincipal = totalPrincipal;
        }

        public Money getAccount999() {
            return account999;
        }

        public void setAccount999(Money account999) {
            this.account999 = account999;
        }

        public Money getRoundedTotalInterest() {
            return roundedTotalInterest;
        }

        public void setRoundedTotalInterest(Money roundedTotalInterest) {
            this.roundedTotalInterest = roundedTotalInterest;
        }

        public Money getTotalFee() {
            return totalFee;
        }

        public void setTotalFee(Money totalFee) {
            this.totalFee = totalFee;
        }
    }

    class LoanTestCaseData {

        private LoanParameters loanParams = null;
        private Results expectedResult = null;
        InternalConfiguration internalConfig = null;

        public InternalConfiguration getInternalConfig() {
            return internalConfig;
        }

        public void setInternalConfig(InternalConfiguration config) {
            this.internalConfig = config;
        }

        public LoanTestCaseData() {
        }

        public Results getExpectedResult() {
            return expectedResult;
        }

        public void setExpectedResult(Results expectedResult) {
            this.expectedResult = expectedResult;
        }

        public LoanParameters getLoanParams() {
            return loanParams;
        }

        public void setLoanParams(LoanParameters loanParams) {
            this.loanParams = loanParams;
        }
    }

    private void printResults(Results expectedResult, Results calculatedResult, String testName) {
        if (!isAllConsoleOutputEnabled())
            return;

        // System.out.println("Running test: " + testName);
        System.out.println("Results are (Expected : Calculated : Difference)");
        printComparison("Total Interest: ", expectedResult.getTotalInterest(), calculatedResult.getTotalInterest());
        printComparison("Total Payments: ", expectedResult.getTotalPayments(), calculatedResult.getTotalPayments());
        printComparison("Total Principal: ", expectedResult.getTotalPrincipal(), calculatedResult.getTotalPrincipal());
        printComparison("Total Fees: ", expectedResult.getTotalFee(), calculatedResult.getTotalFee());

        List<PaymentDetail> expectedPayments = expectedResult.getPayments();
        List<PaymentDetail> calculatedPayments = calculatedResult.getPayments();
        System.out.println("Number of Installments: " + expectedPayments.size() + " : " + calculatedPayments.size()
                + " : " + (expectedPayments.size() - calculatedPayments.size()));
        for (int i = 0; i < expectedPayments.size(); i++) {
            System.out.println("Payment #: " + (i + 1));
            printComparison("Balance:   ", expectedPayments.get(i).getBalance(), calculatedPayments.get(i).getBalance());
            printComparison("Interest:  ", expectedPayments.get(i).getInterest(), calculatedPayments.get(i)
                    .getInterest());
            printComparison("Payment:   ", expectedPayments.get(i).getPayment(), calculatedPayments.get(i).getPayment());
            printComparison("Principal: ", expectedPayments.get(i).getPrincipal(), calculatedPayments.get(i)
                    .getPrincipal());
            printComparison("Fee:       ", expectedPayments.get(i).getFee(), calculatedPayments.get(i).getFee());
        }
    }

    private void compareResults(Results expectedResult, Results calculatedResult, String testName) {
        printResults(expectedResult, calculatedResult, testName);

       Assert.assertEquals(testName, expectedResult.getTotalInterest(), calculatedResult.getTotalInterest());
       Assert.assertEquals(testName, expectedResult.getTotalPayments(), calculatedResult.getTotalPayments());
       Assert.assertEquals(testName, expectedResult.getTotalPrincipal(), calculatedResult.getTotalPrincipal());
        List<PaymentDetail> expectedPayments = expectedResult.getPayments();
        List<PaymentDetail> calculatedPayments = calculatedResult.getPayments();
       Assert.assertEquals(testName, expectedPayments.size(), calculatedPayments.size());
        for (int i = 0; i < expectedPayments.size(); i++) {
            /*
             * Do not assert balance since it is derived from loan information
             *Assert.assertEquals(testName, expectedPayments.get(i).getBalance(),
             * calculatedPayments.get(i).getBalance());
             */
           Assert.assertEquals(testName, expectedPayments.get(i).getInterest(), calculatedPayments.get(i).getInterest());
           Assert.assertEquals(testName, expectedPayments.get(i).getPayment(), calculatedPayments.get(i).getPayment());
           Assert.assertEquals(testName, expectedPayments.get(i).getPrincipal(), calculatedPayments.get(i).getPrincipal());
        }

    }

    private void printComparison(String label, Money expected, Money calculated) {
        if (!isAllConsoleOutputEnabled())
            return;

        System.out.println(label + expected + " : " + calculated + " : " + expected.subtract(calculated));
    }

    private void setNumberOfInterestDays(int days) {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS, new Short((short) days));
    }

    private void setInitialRoundingMode(RoundingMode mode) {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.INITIAL_ROUNDING_MODE, mode.toString());
    }

    private void setFinalRoundingMode(RoundingMode mode) {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.FINAL_ROUNDING_MODE, mode.toString());
    }

    private AccountBO setUpLoan(InternalConfiguration config, LoanParameters loanParams) throws MeetingException,
            NumberFormatException, AccountException

    {
        setNumberOfInterestDays(config.getDaysInYear());
        AccountingRules.setDigitsAfterDecimal((short) config.getDigitsAfterDecimal());
        setInitialRoundingMode(config.getInitialRoundingMode());
        setFinalRoundingMode(config.getFinalRoundingMode());
        AccountingRules.setInitialRoundOffMultiple(new BigDecimal(config.getInitialRoundOffMultiple()));
        AccountingRules.setFinalRoundOffMultiple(new BigDecimal(config.getFinalRoundOffMultiple()));
        AccountingRules.setCurrencyRoundingMode(config.getCurrencyRoundingMode());
        Money.setDefaultCurrency(AccountingRules.getMifosCurrency(new ConfigurationPersistence()));

        /*
         * When constructing a "meeting" here, it looks like the frequency
         * should be "EVERY_X" for weekly or monthly loan interest posting.
         */
        // EVERY_WEEK, EVERY_DAY and EVERY_MONTH are defined as 1
        MeetingBO meeting = null;
        if (loanParams.getPaymentFrequency() == RecurrenceType.MONTHLY) {
            meeting = new MeetingBO((short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                    TestObjectFactory.EVERY_MONTH, new Date(), CUSTOMER_MEETING, "meeting place");
        } else {
            meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(loanParams
                    .getPaymentFrequency(), EVERY_MONTH, CUSTOMER_MEETING));
        }

        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);

        Date startDate = new Date(System.currentTimeMillis());

        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(this.getClass().getSimpleName() + " Loan", "L", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, Double.parseDouble(loanParams.getPrincipal()), Double.parseDouble(loanParams
                        .getAnnualInterest()), loanParams.getNumberOfPayments(), loanParams.getLoanType(), false,
                false, center.getCustomerMeeting().getMeeting(), config.getGracePeriodType(), "1", "1");

        List<FeeView> feeViewList = createFeeViews(config, loanParams, meeting);

        AccountBO accountBO = loanDao.createLoan(TestUtils.makeUser(), loanOffering, group,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(getCurrency(), loanParams.getPrincipal()), loanParams
                        .getNumberOfPayments(), startDate, false, Double.parseDouble(loanParams.getAnnualInterest()),
                config.getGracePeriod(), null, feeViewList, null, DOUBLE_ZERO, DOUBLE_ZERO, SHORT_ZERO,
                SHORT_ZERO, false);

        new TestObjectPersistence().persist(accountBO);
        return accountBO;
    }

    private List<FeeView> createFeeViews(InternalConfiguration config, LoanParameters loanParams, MeetingBO meeting) {

        List<FeeView> feeViews = new ArrayList<FeeView>();

        // Only periodic fees get merged into loan installments
        if (!(config.getFeeFrequency() == null) && config.getFeeFrequency() == FeeFrequencyType.PERIODIC) {
            feeViews.add(createPeriodicFeeView(config, loanParams, meeting));
        }

        return feeViews;
    }

    private FeeView createPeriodicFeeView(InternalConfiguration config, LoanParameters loanParams, MeetingBO meeting) {
        FeeBO fee = null;
        if (config.isFeeRateBased()) {
            fee = TestObjectFactory.createPeriodicRateFee("testLoanFee", FeeCategory.LOAN, new Double(config
                    .getFeePercentage()), config.getFeeType(), loanParams.getPaymentFrequency(), (short) 1,
                    userContext, meeting);
        } else {
            fee = TestObjectFactory.createPeriodicAmountFee("testLoanFee", FeeCategory.LOAN, config.getFeeValue(),
                    loanParams.getPaymentFrequency(), Short.valueOf("1"));
        }

        FeeView feeView = new FeeView(userContext, fee);
        return feeView;
    }

    private Results calculatePayments(InternalConfiguration config, AccountBO accountBO, LoanParameters loanParams) {

        Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO).getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities, loanParams
                .getNumberOfPayments());

        MathContext context = new MathContext(config.getInternalPrecision());
        BigDecimal totalPrincipal = new BigDecimal(0, context);
        BigDecimal totalInterest = new BigDecimal(0, context);
        BigDecimal totalFees = new BigDecimal(0, context);
        Money totalPayments = new Money(getCurrency(), "0");
        Results calculatedResult = new Results();
        List<PaymentDetail> payments = new ArrayList<PaymentDetail>();
        for (LoanScheduleEntity loanEntry : paymentsArray) {
            PaymentDetail payment = new PaymentDetail();
            Money calculatedPayment = new Money(getCurrency(), loanEntry.getPrincipal().getAmount().add(
                    loanEntry.getInterest().getAmount().add(loanEntry.getTotalFees().getAmount())));
            payment.setPayment(calculatedPayment);
            payment.setInterest(loanEntry.getInterest());
            payment.setPrincipal(loanEntry.getPrincipal());
            payment.setFee(loanEntry.getTotalFees());

            totalPrincipal = totalPrincipal.add(loanEntry.getPrincipal().getAmount());
            totalInterest = totalInterest.add(loanEntry.getInterest().getAmount());
            totalPayments = totalPayments.add(calculatedPayment);
            totalFees = totalFees.add(loanEntry.getTotalFees().getAmount());

            payments.add(payment);
        }
        calculatedResult.setPayments(payments);
        calculatedResult.setTotalInterest(new Money(getCurrency(), totalInterest));
        calculatedResult.setTotalPayments(totalPayments);
        calculatedResult.setTotalPrincipal(new Money(getCurrency(), totalPrincipal));
        calculatedResult.setTotalFee(new Money(getCurrency(), totalFees));

        /*
         * Set balance after each installment is paid, excluding fees or
         * penalties. For flat-interest loans, balance is total of all remaining
         * principal and interest. For declining-interest loans, balance is
         * total remaining principal.
         */
        if (loanParams.loanType.getValue() == InterestType.FLAT.getValue()) {
            Money balance = new Money(getCurrency(), totalPrincipal.add(totalInterest));
            for (PaymentDetail paymentDetail : payments) {
                balance = balance.subtract(paymentDetail.getPrincipal()).subtract(paymentDetail.getInterest());
                paymentDetail.setBalance(balance);
            }
        } else if (loanParams.loanType.getValue() == InterestType.DECLINING.getValue()) {
            Money balance = new Money(getCurrency(), totalPrincipal);
            for (PaymentDetail paymentDetail : payments) {
                balance = balance.subtract(paymentDetail.getPrincipal());
                paymentDetail.setBalance(balance);
            }
        }

        return calculatedResult;

    }

    private void parseLoanParams(String paramType, String line, LoanParameters loanParams) {
        String tempLine = line.substring(paramType.length(), line.length() - 1);
        String[] tokens = tempLine.split(",");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (StringUtils.isNotBlank(token)) {
                if ((paramType.indexOf(principal) >= 0) && (loanParams.getPrincipal() == null))
                    loanParams.setPrincipal(token);
                else if (paramType.indexOf(loanType) >= 0) {
                    InterestType interestType = null;
                    if (token.equals("Fixed Principal"))
                        interestType = InterestType.DECLINING_EPI;
                    else
                        interestType = InterestType.valueOf(token.toUpperCase());
                    loanParams.setLoanType(interestType);
                } else if (paramType.indexOf(annualInterest) >= 0) {
                    int pos = token.indexOf("%");
                    String interest = token.substring(0, pos);
                    loanParams.setAnnualInterest(interest);
                } else if (paramType.indexOf(numberOfPayments) >= 0)
                    loanParams.setNumberOfPayments(Short.parseShort(token));
                else if (paramType.indexOf(paymentFrequency) >= 0) {
                    RecurrenceType recurrenceType = RecurrenceType.valueOf(token.toUpperCase());
                    loanParams.setPaymentFrequency(recurrenceType);
                }
                break;

            }
        }

    }

    private String getToken(String line, String param) {
        int index = line.indexOf(param);
        line = line.substring(index + param.length(), line.length() - 1);

        String[] tokens = line.split(",");
        String token = null;
        for (int j = 0; j < tokens.length; j++) {
            token = tokens[j];
            if (StringUtils.isNotBlank(token))
                break;
        }
        return token;
    }

    private void parseConfigParams(String paramType, String line, InternalConfiguration config) {
        String tempLine = line.substring(paramType.length(), line.length() - 1);
        String[] tokens = tempLine.split(",");
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (StringUtils.isNotBlank(token)) {
                if (paramType.indexOf(initialRoundingMode) >= 0) {
                    RoundingMode mode = RoundingMode.valueOf(token.toUpperCase());
                    config.setInitialRoundingMode(mode);
                    token = getToken(tempLine, feeFrequency);
                    if (token.toUpperCase().equals("PERIODIC"))
                        config.setFeeFrequency(FeeFrequencyType.PERIODIC);
                    else if (token.toUpperCase().equals("ONETIME"))
                        config.setFeeFrequency(FeeFrequencyType.ONETIME);
                    else
                        config.setFeeFrequency(null);
                } else if (paramType.indexOf(initialRoundOffMultiple) >= 0) {
                    config.setInitialRoundOffMultiple(token);
                    token = getToken(tempLine, feeType);
                    config.setIsFeeRateBased(true);
                    if (token.equals(feeTypePrincipalPlusInterest))
                        config.setFeeType(FeeFormula.AMOUNT_AND_INTEREST);
                    else if (token.equals(feeTypeInterest))
                        config.setFeeType(FeeFormula.INTEREST);
                    else if (token.equals(feeTypePrincipal))
                        config.setFeeType(FeeFormula.AMOUNT);
                    else if (token.equals(feeTypeValue)) // Not rate-based,
                        // don't use FeeFormula
                        config.setIsFeeRateBased(false);
                    else
                        throw new RuntimeException("Unrecognized fee type: " + token);
                } else if (paramType.indexOf(finalRoundingMode) >= 0) {
                    RoundingMode mode = RoundingMode.valueOf(token.toUpperCase());
                    config.setFinalRoundingMode(mode);
                    token = getToken(tempLine, feeValue);
                    config.setFeeValue(token);
                } else if (paramType.indexOf(finalRoundOffMultiple) >= 0) {
                    config.setFinalRoundOffMultiple(token);
                    token = getToken(tempLine, feePercentage);
                    int pos = token.indexOf("%");
                    token = token.substring(0, pos);
                    config.setFeePercentage(token);
                } else if (paramType.indexOf(currencyRounding) >= 0) {
                    RoundingMode mode = RoundingMode.valueOf(token.toUpperCase());
                    config.setCurrencyRoundingMode(mode);
                    token = getToken(tempLine, gracePeriodType);
                    GraceType type = null;
                    if (token.toUpperCase().equals("ALL"))
                        type = GraceType.GRACEONALLREPAYMENTS;
                    else if (token.toUpperCase().equals("PRINCIPAL"))
                        type = GraceType.PRINCIPALONLYGRACE;
                    else
                        type = GraceType.NONE;

                    config.setGracePeriodType(type);

                } else if (paramType.indexOf(digitsAfterDecimal) >= 0) {
                    config.setDigitsAfterDecimal(Short.parseShort(token));
                    token = getToken(tempLine, gracePeriod);
                    config.setGracePeriod(Short.parseShort(token));
                } else if (paramType.indexOf(daysInYear) >= 0) {
                    config.setDaysInYear(Short.parseShort(token));
                }
                /*
                 * else if (paramType.indexOf(gracePeriodType)>= 0) { GraceType
                 * type = null; if (token.toUpperCase().equals("ALL")) type =
                 * GraceType.GRACEONALLREPAYMENTS; else if
                 * (token.toUpperCase().equals("PRINCIPAL")) type =
                 * GraceType.PRINCIPALONLYGRACE; else type = GraceType.NONE;
                 * 
                 * config.setGracePeriodType(type); } else if
                 * (paramType.indexOf(gracePeriod)>= 0) { if
                 * (config.getGracePeriodType() != GraceType.NONE)
                 * config.setGracePeriod(Short.parseShort(token)); }
                 */
                break;

            }
        }

    }

    private void parseTotals(String paramType, String line, Results result) {
        String tempLine = line.substring(paramType.length(), line.length() - 1);
        int index = tempLine.indexOf(paramType);
        tempLine = tempLine.substring(index + paramType.length(), tempLine.length() - 1);
        String[] tokens = tempLine.split(",");
        boolean totalPayments = false;
        boolean totalInterests = true;
        boolean totalFee = true;
        boolean totalPrincipals = true;
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (StringUtils.isNotBlank(token)) {
                if (totalPayments == false) {
                    result.setTotalPayments(new Money(getCurrency(), token));
                    totalPayments = true;
                    totalPrincipals = false;
                } else if (totalInterests == false) {
                    result.setTotalInterest(new Money(getCurrency(), token));
                    totalInterests = true;
                    totalFee = false;
                } else if (totalFee == false) {
                    result.setTotalFee(new Money(getCurrency(), token));
                    totalFee = true;
                } else if (totalPrincipals == false) {
                    result.setTotalPrincipal(new Money(getCurrency(), token));
                    totalPrincipals = true;
                    totalInterests = false;
                } else
                    return;

            }
        }

    }

    private void parse999Account(String paramType, String line, Results result) {
        String tempLine = line.substring(paramType.length(), line.length() - 1);
        int index = tempLine.indexOf(paramType);
        tempLine = tempLine.substring(index + paramType.length(), tempLine.length() - 1);
        String[] tokens = tempLine.split(",");
        if (tokens.length < 2)
            return;
        result.setAccount999(new Money(getCurrency(), tokens[1]).negate());

    }

    private void parseRoundedTotalInterest(String paramType, String line, Results result) {
        String tempLine = line.substring(paramType.length(), line.length() - 1);
        int index = tempLine.indexOf(paramType);
        tempLine = tempLine.substring(index + paramType.length(), tempLine.length() - 1);
        String[] tokens = tempLine.split(",");
        if (tokens.length < 8)
            return;
        result.setRoundedTotalInterest(new Money(getCurrency(), tokens[3]));

    }

    private void parsePaymentDetail(String paramType, String line, Results result) {

        int index = line.indexOf(",,");
        String tempLine = line.substring(index + 1, line.length() - 1);
        String[] tokens = tempLine.split(",");
        boolean paymentIndex = false;
        boolean payment = true;
        boolean principal = true;
        boolean interest = true;
        boolean balance = true;
        boolean fee = true;
        PaymentDetail paymentDetail = new PaymentDetail();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (StringUtils.isNotBlank(token)) {
                if (paymentIndex == false) {
                    int paymentNumber = Integer.parseInt(token);
                    int expectedPaymentNumber = result.getPayments().size() + 1;
                    if (paymentNumber != expectedPaymentNumber)
                        throw new RuntimeException("Parsing error. paymentNumber " + paymentNumber + " Expected: "
                                + expectedPaymentNumber);
                    paymentIndex = true;
                    payment = false;
                } else if (payment == false) {
                    paymentDetail.setPayment(new Money(getCurrency(), token));
                    payment = true;
                    principal = false;
                } else if (principal == false) {
                    paymentDetail.setPrincipal(new Money(getCurrency(), token));
                    principal = true;
                    interest = false;
                } else if (interest == false) {
                    paymentDetail.setInterest(new Money(getCurrency(), token));
                    interest = true;
                    fee = false;
                } else if (fee == false) {
                    paymentDetail.setFee(new Money(getCurrency(), token));
                    fee = true;
                    balance = false;
                } else if (balance == false) {
                    paymentDetail.setBalance(new Money(getCurrency(), token));
                    result.getPayments().add(paymentDetail);
                    return;
                }
            }
        }

    }

    private LoanTestCaseData loadSpreadSheetData(String fileName) throws URISyntaxException {
        File file = new File(ClasspathResource.getURI(fileName));
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        LoanTestCaseData testCaseData = new LoanTestCaseData();
        boolean startPayment = false;
        int paymentIndex = 0;
        String line = null;

        try {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            // dataInputStream.available() returns 0 if the file does not have
            // more lines.

            LoanParameters loanParams = new LoanParameters();
            InternalConfiguration config = new InternalConfiguration();
            Results expectedResult = new Results();
            List<PaymentDetail> list = new ArrayList<PaymentDetail>();
            expectedResult.setPayments(list);
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(",");
                for (int i = 0; i < tokens.length; i++) {
                    String token = tokens[i];
                    if (StringUtils.isNotBlank(token)) {
                        if ((token.indexOf(principal) >= 0) || (token.indexOf(loanType) >= 0)
                                || (token.indexOf(annualInterest) >= 0) || (token.indexOf(numberOfPayments) >= 0)
                                || (token.indexOf(paymentFrequency) >= 0)) {
                            parseLoanParams(token, line, loanParams);
                            break;
                        } else if ((token.indexOf(initialRoundingMode) >= 0) || (token.indexOf(finalRoundingMode) >= 0)
                                || (token.indexOf(initialRoundOffMultiple) >= 0)
                                || (token.indexOf(finalRoundOffMultiple) >= 0)
                                || (token.indexOf(currencyRounding) >= 0) || (token.indexOf(digitsAfterDecimal) >= 0)
                                || (token.indexOf(daysInYear) >= 0))

                        {
                            parseConfigParams(token, line, config);
                            break;
                        } else if (token.indexOf(calculatedTotals) == 0)
                            parseRoundedTotalInterest(token, line, expectedResult);
                        else if (token.indexOf(account999) == 0)
                            parse999Account(token, line, expectedResult);
                        else if (token.indexOf(totals) >= 0)
                            parseTotals(token, line, expectedResult);
                        else if (token.indexOf(start) >= 0) {
                            startPayment = true;
                            break;
                        } else if (startPayment) {
                            parsePaymentDetail(token, line, expectedResult);
                            paymentIndex++;
                            if (paymentIndex >= loanParams.getNumberOfPayments()) {
                                testCaseData.setExpectedResult(expectedResult);
                                testCaseData.setInternalConfig(config);
                                testCaseData.setLoanParams(loanParams);
                                return testCaseData;
                            }
                            break;
                        }

                    }

                }

            }
            if (fileInputStream != null)
                fileInputStream.close();
            if (inputStreamReader != null)
                inputStreamReader.close();
            if (bufferedReader != null)
                bufferedReader.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return testCaseData;

    }

    /*
     * This test case will populate the data classes for a loan test case with
     * data from spreadsheet and calculates payments and compares
     */
    private void runOneTestCaseWithDataFromSpreadSheet(String directoryName, String fileName)
            throws NumberFormatException, PropertyNotFoundException, SystemException, ApplicationException,
            URISyntaxException {

        if (isAllConsoleOutputEnabled() || isFileNameConsoleOutputEnabled()) {
            System.out.println("Running Test: " + fileName);
        }
        LoanTestCaseData testCaseData = loadSpreadSheetData(directoryName + fileName);
        accountBO = setUpLoan(testCaseData.getInternalConfig(), testCaseData.getLoanParams());
        // calculated results
        Results calculatedResult = calculatePayments(testCaseData.getInternalConfig(), accountBO, testCaseData
                .getLoanParams());
        compareResults(testCaseData.getExpectedResult(), calculatedResult, fileName);

    }

    private String[] getCSVFiles(String directoryPath) throws URISyntaxException {
        File dir = new File(ClasspathResource.getURI(directoryPath));

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        };
        return dir.list(filter);

    }

    public void testCaseWithDataFromSpreadSheets() throws NumberFormatException, PropertyNotFoundException,
            SystemException, ApplicationException, URISyntaxException {
        // String rootPath =
        // "org/mifos/accounts/loan/business/testCaseData/flatInterest/";
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/";

        // String[] dataFileNames = {"testcases-2008-04-22.set1.01.csv"};
        String[] dataFileNames = { "loan-repayment-master-test1.csv" };
        for (int i = 0; i < dataFileNames.length; i++)
            runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
    }

    public void testIssue1623FromSpreadSheets() throws NumberFormatException, PropertyNotFoundException,
            SystemException, ApplicationException, URISyntaxException {

        String rootPath = "org/mifos/accounts/loan/business/testCaseData/";
        String[] dataFileNames = { "loan-repayment-master-issue1623.csv" };
        for (int i = 0; i < dataFileNames.length; i++)
            runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);

    }

    public void testFlatInterestTestCases() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/flatInterest/";
        String[] dataFileNames = getCSVFiles(rootPath);
        for (int i = 0; i < dataFileNames.length; i++) {
            if (
            // dataFileNames[i].contains("set1.23")

            fileNameContains(dataFileNames[i], flatGraceFeeTestCases)
                    || fileNameContains(dataFileNames[i], flatNegativeLastPaymentTestCases)) {
                runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
                tearDown();
                setUp();
            }
        }
    }

    public void testDecliningInterestTestCases() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningInterest/";
        String[] dataFileNames = getCSVFiles(rootPath);
        for (int i = 0; i < dataFileNames.length; i++) {
            if (fileNameContains(dataFileNames[i], decliningGraceFeeTestCases)
                    || fileNameContains(dataFileNames[i], decliningNegativeLastPaymentTestCases)) {
                runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
                tearDown();
                setUp();
            }
        }
    }

    public void xtestAllDecliningInterestEdgeTestCases() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/decliningInterestedge/";
        String[] dataFileNames = getCSVFiles(rootPath);
        for (int i = 0; i < dataFileNames.length; i++) {
            if (dataFileNames[i].startsWith("testcase") && fileNameContains(dataFileNames[i], selectedCaseNumbers)) {
                runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
                tearDown();
                setUp();
            }
        }
    }

    // marked "@Ignore" in JUnit4, so converting to xtest so it won't run in JUnit3
    public void xtestAllFlatInterestEdgeTestCases() throws Exception {
        String rootPath = "org/mifos/accounts/loan/business/testCaseData/flatinterestedge/";
        String[] dataFileNames = getCSVFiles(rootPath);
        for (int i = 0; i < dataFileNames.length; i++) {
            if (dataFileNames[i].startsWith("testcase") && fileNameContains(dataFileNames[i], flatTestCases)) {
                runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
                tearDown();
                setUp();
            }
        }
    }

    private String[] flatTestCases = { "testcase-2008-05-12-flat-set1.01", "testcase-2008-05-12-flat-set1.02",
            "testcase-2008-05-12-flat-set1.03", "testcase-2008-05-12-flat-set1.04", "testcase-2008-05-12-flat-set1.05",
            "testcase-2008-05-12-flat-set1.06", "testcase-2008-05-12-flat-set1.07", "testcase-2008-05-12-flat-set1.08",
            "testcase-2008-05-12-flat-set1.09", "testcase-2008-05-12-flat-set1.10", "testcase-2008-05-12-flat-set1.11",
            "testcase-2008-05-12-flat-set1.12", "testcase-2008-05-12-flat-set1.13", "testcase-2008-05-12-flat-set1.14",
            "testcase-2008-05-12-flat-set1.15", "testcase-2008-05-12-flat-set1.16", "testcase-2008-05-12-flat-set1.17",
            "testcase-2008-05-12-flat-set1.18", "testcase-2008-05-12-flat-set1.19" };

    private String[] flatNegativeLastPaymentTestCases = { "testcase-2008-05-27-flat-negative-payment-set1.01.csv",
            "testcase-2008-05-27-flat-negative-payment-set1.02.csv",
            "testcase-2008-05-27-flat-negative-payment-set1.03.csv" };

    private String[] decliningNegativeLastPaymentTestCases = {
            "testcase-2008-05-27-declining-negative-payment-set1.01.csv",
            "testcase-2008-05-27-declining-negative-payment-set1.02.csv",
            "testcase-2008-05-27-declining-negative-payment-set1.03.csv" };

    private String[] flatGraceFeeTestCases = { "testcase-2008-05-13-flat-grace-fee-set1.01",
            "testcase-2008-05-13-flat-grace-fee-set1.02", "testcase-2008-05-13-flat-grace-fee-set1.03",
            "testcase-2008-05-13-flat-grace-fee-set1.04", "testcase-2008-05-13-flat-grace-fee-set1.05",
            "testcase-2008-05-13-flat-grace-fee-set1.06", "testcase-2008-05-13-flat-grace-fee-set1.07",
            "testcase-2008-05-13-flat-grace-fee-set1.08", "testcase-2008-05-13-flat-grace-fee-set1.09",
            "testcase-2008-05-13-flat-grace-fee-set1.10", "testcase-2008-05-13-flat-grace-fee-set1.11",
            "testcase-2008-05-13-flat-grace-fee-set1.12", "testcase-2008-05-13-flat-grace-fee-set1.13",
            "testcase-2008-05-13-flat-grace-fee-set1.14", "testcase-2008-05-13-flat-grace-fee-set1.15",
            "testcase-2008-05-13-flat-grace-fee-set1.16", "testcase-2008-05-13-flat-grace-fee-set1.17",
            "testcase-2008-05-13-flat-grace-fee-set1.18", "testcase-2008-05-13-flat-grace-fee-set1.19",
            "testcase-2008-05-13-flat-grace-fee-set1.20", "testcase-2008-05-13-flat-grace-fee-set1.21",
            "testcase-2008-05-13-flat-grace-fee-set1.22", "testcase-2008-05-13-flat-grace-fee-set1.23",
            "testcase-2008-05-13-flat-grace-fee-set1.24", "testcase-2008-05-13-flat-grace-fee-set1.25",
            "testcase-2008-05-13-flat-grace-fee-set1.26" };

    private String[] decliningEPIGraceFeeTestCases = { "testcase-2008-06-27-decliningEPI-grace-fee-set1.01",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.02", "testcase-2008-06-27-decliningEPI-grace-fee-set1.03",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.04", "testcase-2008-06-27-decliningEPI-grace-fee-set1.05",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.06", "testcase-2008-06-27-decliningEPI-grace-fee-set1.07",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.08", "testcase-2008-06-27-decliningEPI-grace-fee-set1.09",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.10", "testcase-2008-06-27-decliningEPI-grace-fee-set1.11",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.12", "testcase-2008-06-27-decliningEPI-grace-fee-set1.13",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.14", "testcase-2008-06-27-decliningEPI-grace-fee-set1.15",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.16", "testcase-2008-06-27-decliningEPI-grace-fee-set1.17",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.18", "testcase-2008-06-27-decliningEPI-grace-fee-set1.19",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.20", "testcase-2008-06-27-decliningEPI-grace-fee-set1.21",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.22", "testcase-2008-06-27-decliningEPI-grace-fee-set1.23",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.24", "testcase-2008-06-27-decliningEPI-grace-fee-set1.25",
            "testcase-2008-06-27-decliningEPI-grace-fee-set1.26"

    };

    private String[] decliningGraceFeeTestCases = { "testcase-2008-05-13-declining-grace-fee-set1.01",
            "testcase-2008-05-13-declining-grace-fee-set1.02", "testcase-2008-05-13-declining-grace-fee-set1.03",
            "testcase-2008-05-13-declining-grace-fee-set1.04", "testcase-2008-05-13-declining-grace-fee-set1.05",
            "testcase-2008-05-13-declining-grace-fee-set1.06", "testcase-2008-05-13-declining-grace-fee-set1.07",
            "testcase-2008-05-13-declining-grace-fee-set1.08", "testcase-2008-05-13-declining-grace-fee-set1.09",
            "testcase-2008-05-13-declining-grace-fee-set1.10", "testcase-2008-05-13-declining-grace-fee-set1.11",
            "testcase-2008-05-13-declining-grace-fee-set1.12", "testcase-2008-05-13-declining-grace-fee-set1.13",
            "testcase-2008-05-13-declining-grace-fee-set1.14", "testcase-2008-05-13-declining-grace-fee-set1.15",
            "testcase-2008-05-13-declining-grace-fee-set1.16", "testcase-2008-05-13-declining-grace-fee-set1.17",
            "testcase-2008-05-13-declining-grace-fee-set1.18", "testcase-2008-05-13-declining-grace-fee-set1.19",
            "testcase-2008-05-13-declining-grace-fee-set1.20", "testcase-2008-05-13-declining-grace-fee-set1.21",
            "testcase-2008-05-13-declining-grace-fee-set1.22", "testcase-2008-05-13-declining-grace-fee-set1.23",
            "testcase-2008-05-13-declining-grace-fee-set1.24", "testcase-2008-05-13-declining-grace-fee-set1.25",
            "testcase-2008-05-13-declining-grace-fee-set1.26" };

    private String[] selectedCaseNumbers = {
    // "set1.01", //JDBC error could not insert LoanOfferingBO
    // "set1.02a",
    // "set1.03", //negative principal payments
    // "set1.04",
    // "set1.05" //Infinite or NAN
    // "set1.06", //JDBC error could not insert LoanOfferingBO
    // "set1.07", //negative principal payments
    // "set1.08",
    // "set1.09",
    // "set1.10" //Infinite or NAN
    // "set1.11", //JDBC error could not insert LoanOfferingBO
    // "set1.12",
    "set1.13" // assertion failed -- comparison failure
    // "set1.14",
    // "set1.15", //spreadsheet error
    // "set1.16", //spreadsheet error
    // "set1.17", //spreadsheet error
    // "set1.18",
    // "set1.19", //JDBC error could not insert LoanOfferingBO
    // "set1.20",
    // "set1.21", //Infinite or NAN
    // "set1.22", //spreadsheet error
    // "set1.23",
    // "set1.24", //JDBC error could not insert LoanOfferingBO
    // "set1.25", //spreadsheet error
    // "set1.26" //Infinite or NAN
    // "set2"
    };

    private boolean fileNameContains(String fileName, String[] testNumbers) {
        for (int i = 0; i < testNumbers.length; i++) {
            if (fileName.contains(testNumbers[i]))
                return true;
        }
        return false;
    }

    /*
     * This test case populates data from spreadsheet for loan params and
     * expected results
     */
    // marked "@Ignore" in JUnit4, so converting to xtest so it won't run in JUnit3
    // getting NullPointerException
    public void testOneExampleOfTestCaseFromSpreadSheet() throws NumberFormatException, PropertyNotFoundException,
            SystemException, ApplicationException {

        // set up config
        InternalConfiguration config = new InternalConfiguration();
        config.setDaysInYear(365);
        config.setFinalRoundingMode(RoundingMode.CEILING);
        config.setFinalRoundOffMultiple("0.01");
        config.setInitialRoundingMode(RoundingMode.CEILING);
        config.setInitialRoundOffMultiple("1");
        config.setCurrencyRoundingMode(RoundingMode.CEILING);
        config.setInternalPrecision(13);
        config.setDigitsAfterDecimal(3);

        // set up loan params
        LoanParameters loanParams = new LoanParameters();
        loanParams.setLoanType(InterestType.FLAT);
        loanParams.setNumberOfPayments((short) 5);
        loanParams.setPaymentFrequency(RecurrenceType.WEEKLY);
        loanParams.setAnnualInterest("12.00");
        loanParams.setPrincipal("1002");

        // set up expected results
        Results expectedResult = new Results();
        expectedResult.setTotalInterest(new Money(getCurrency(), "11.53"));
        expectedResult.setTotalPayments(new Money(getCurrency(), "1013.53"));
        expectedResult.setTotalPrincipal(new Money(getCurrency(), "1002")); // this loan amount
        List<PaymentDetail> list = new ArrayList<PaymentDetail>();
        // 1st payment
        PaymentDetail payment = new PaymentDetail();
        payment.setPayment(new Money(getCurrency(), "203.000"));
        payment.setInterest(new Money(getCurrency(), "2.306"));
        payment.setBalance(new Money(getCurrency(), "810.530"));
        payment.setPrincipal(new Money(getCurrency(), "200.694"));
        list.add(payment);
        // 2nd payment
        payment = new PaymentDetail();
        payment.setPayment(new Money(getCurrency(), "203.000"));
        payment.setInterest(new Money(getCurrency(), "2.306"));
        payment.setBalance(new Money(getCurrency(), "607.530"));
        payment.setPrincipal(new Money(getCurrency(), "200.694"));
        list.add(payment);
        // 3rd payment
        payment = new PaymentDetail();
        payment.setPayment(new Money(getCurrency(), "203.000"));
        payment.setInterest(new Money(getCurrency(), "2.306"));
        payment.setBalance(new Money(getCurrency(), "404.530"));
        payment.setPrincipal(new Money(getCurrency(), "200.694"));
        list.add(payment);
        // 4th payment
        payment = new PaymentDetail();
        payment.setPayment(new Money(getCurrency(), "203.000"));
        payment.setInterest(new Money(getCurrency(), "2.306"));
        payment.setBalance(new Money(getCurrency(), "201.530"));
        payment.setPrincipal(new Money(getCurrency(), "200.694"));
        list.add(payment);
        // last payment
        payment = new PaymentDetail();
        payment.setPayment(new Money(getCurrency(), "201.530"));
        payment.setInterest(new Money(getCurrency(), "2.306"));
        payment.setBalance(new Money(getCurrency(), "0"));
        payment.setPrincipal(new Money(getCurrency(), "199.224"));
        list.add(payment);
        expectedResult.setPayments(list);

        expectedResult.setPayments(list);

        accountBO = setUpLoan(config, loanParams);

        // calculated results
        Results calculatedResult = calculatePayments(config, accountBO, loanParams);
        compareResults(expectedResult, calculatedResult, "testOneExampleOfTestCaseFromSpreadSheet");

    }

    class PaymentDetail {
        Money payment = null;
        Money principal = null;
        Money interest = null;
        Money fee = null;
        Money balance = null;

        public PaymentDetail(Money payment, Money principal, Money interest, Money balance) {
            super();
            this.payment = payment;
            this.principal = principal;
            this.interest = interest;
            this.balance = balance;
        }

        public PaymentDetail() {
        }

        public Money getBalance() {
            return balance;
        }

        public void setBalance(Money balance) {
            this.balance = balance;
        }

        public Money getInterest() {
            return interest;
        }

        public void setInterest(Money interest) {
            this.interest = interest;
        }

        public Money getPayment() {
            return payment;
        }

        public void setPayment(Money payment) {
            this.payment = payment;
        }

        public Money getPrincipal() {
            return principal;
        }

        public void setPrincipal(Money principal) {
            this.principal = principal;
        }

        public Money getFee() {
            return fee;
        }

        public void setFee(Money fee) {
            this.fee = fee;
        }

    }
}
