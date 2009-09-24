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

import static org.mifos.framework.util.helpers.NumberUtils.DOUBLE_ZERO;
import static org.mifos.framework.util.helpers.NumberUtils.SHORT_ZERO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.CenterTemplate;
import org.mifos.application.customer.center.CenterTemplateImpl;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.GroupTemplate;
import org.mifos.application.customer.group.GroupTemplateImpl;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.MeetingTemplateImpl;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingTestUtils;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.PrdStatusEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestGeneralLedgerCode;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class LoanBORedoDisbursalIntegrationTest extends MifosIntegrationTestCase {

    public LoanBORedoDisbursalIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private CenterPersistence centerPersistence;
    private GroupPersistence groupPersistence;
    private UserContext userContext;
    private CustomerBO center = null;
    private CustomerBO group = null;
    private AccountBO loanBO = null;
    private MeetingBO meeting = null;
    private FeeBO fee;

    @Override
    public void setUp() throws Exception {
        centerPersistence = new CenterPersistence();
        groupPersistence = new GroupPersistence();
        initializeStatisticsService();
        userContext = TestUtils.makeUser();
        fee = null;
    }

    @Override
    public void tearDown() throws Exception {
        // TestObjectFactory.removeObject(loanOffering);
        if (loanBO != null)
            loanBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, loanBO.getAccountId());
        if (group != null)
            group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
        if (center != null)
            center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
        TestObjectFactory.cleanUp(loanBO);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        if (null != fee) {
            fee = (FeeBO) StaticHibernateUtil.getSessionTL().get(FeeBO.class, fee.getFeeId());
            TestObjectFactory.cleanUp(fee);
        }
        StaticHibernateUtil.closeSession();
    }

    private LoanOfferingBO createLoanOffering(UserContext userContext, MeetingBO meeting, Date loanProductStartDate)
            throws ProductDefinitionException {
        PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(ApplicableTo.GROUPS);
        ProductCategoryBO productCategory = TestObjectFactory.getLoanPrdCategory();
        GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(GraceType.NONE);
        InterestTypesEntity interestType = new InterestTypesEntity(InterestType.FLAT);
        GLCodeEntity glCodePrincipal = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.LOANS_TO_CLIENTS);
        GLCodeEntity glCodeInterest = (GLCodeEntity) StaticHibernateUtil.getSessionTL().get(GLCodeEntity.class,
                TestGeneralLedgerCode.INTEREST_ON_LOANS);

        boolean interestDeductedAtDisbursement = false;
        boolean principalDueInLastInstallment = false;
        Money loanAmount = new Money("300");
        Double interestRate = new Double(1.2);
        Short installments = new Short((short) 6);
        LoanOfferingBO loanOffering = LoanOfferingTestUtils.createInstanceForTest(userContext, "TestLoanOffering", "TLO",
                productCategory, prdApplicableMaster, loanProductStartDate, null, null, gracePeriodType, (short) 0,
                interestType, loanAmount, loanAmount, loanAmount, interestRate, interestRate, interestRate,
                installments, installments, installments, true, interestDeductedAtDisbursement,
                principalDueInLastInstallment, new ArrayList<FundBO>(), new ArrayList<FeeBO>(), meeting,
                glCodePrincipal, glCodeInterest);

        PrdStatusEntity prdStatus = new TestObjectPersistence().retrievePrdStatus(PrdStatus.LOAN_ACTIVE);
        LoanOfferingTestUtils.setStatus(loanOffering, prdStatus);
        LoanOfferingTestUtils.setGracePeriodType(loanOffering, gracePeriodType);
        loanOffering.save();

        return loanOffering;
    }

    private LoanBO redoLoanAccount(GroupBO group, LoanOfferingBO loanOffering, MeetingBO meeting,
            Date disbursementDate, List<FeeView> feeViews) throws AccountException {
        MifosCurrency currency = TestObjectFactory.getCurrency();
        Short numberOfInstallments = Short.valueOf("6");
        List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, numberOfInstallments);
        loanBO = LoanBO.redoLoan(TestUtils.makeUser(), loanOffering, group, AccountState.LOAN_APPROVED, new Money(
                currency, "300.0"), numberOfInstallments, meetingDates.get(0), false, 1.2, (short) 0, new FundBO(),
                feeViews, null, DOUBLE_ZERO, DOUBLE_ZERO, SHORT_ZERO, SHORT_ZERO, false, null);
        ((LoanBO) loanBO).save();
        new TestObjectPersistence().persist(loanBO);
        return (LoanBO) loanBO;
    }

    private void disburseLoan(UserContext userContext, LoanBO loan, Date loanDisbursalDate) throws AccountException,
            PersistenceException {
        PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userContext.getId());
        loan.disburseLoan(null, loanDisbursalDate, Short.valueOf("1"), personnel, null, Short.valueOf("1"));
        new TestObjectPersistence().persist(loan);
    }

    private void applyPaymentForLoan(UserContext userContext, LoanBO loan, Date paymentDate, Money money)
            throws AccountException {
        loan.setUserContext(userContext);
        List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
        accntActionDates.addAll(loan.getAccountActionDates());

        PaymentData paymentData = loan.createPaymentData(userContext, money, paymentDate, null, null, Short
                .valueOf("1"));
        loan.applyPaymentWithPersist(paymentData);
        new TestObjectPersistence().persist(loan);
    }

    private Date createPreviousDate(int numberOfDays) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -numberOfDays);
        Date pastDate = DateUtils.getDateWithoutTimeStamp(calendar.getTime());
        return pastDate;
    }

    private LoanBO redoLoanWithMondayMeeting(UserContext userContext, Date disbursementDate, List<FeeView> feeViews)
            throws Exception {
        /*
         * OfficeTemplate template =
         * OfficeTemplateImpl.createUniqueOfficeTemplate
         * (OfficeLevel.BRANCHOFFICE); OfficeBO office =
         * getOfficePersistence().createOffice(userContext, template);
         */
        OfficeBO office = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        meeting = new MeetingBO(MeetingTemplateImpl.createWeeklyMeetingTemplateOnMondaysStartingFrom(disbursementDate));

        CenterTemplate centerTemplate = new CenterTemplateImpl(meeting, office.getOfficeId());
        center = getCenterPersistence().createCenter(userContext, centerTemplate);

        GroupTemplate groupTemplate = GroupTemplateImpl.createNonUniqueGroupTemplate(center.getCustomerId());

        group = createGroup(userContext, groupTemplate, disbursementDate);

        LoanOfferingBO loanOffering = createLoanOffering(userContext, meeting, disbursementDate);
        return redoLoanAccount((GroupBO) group, loanOffering, meeting, disbursementDate, feeViews);
    }

    private LoanBO redoLoanWithMeetingToday(UserContext userContext, Date disbursementDate, List<FeeView> feeViews)
            throws Exception {
        /*
         * OfficeTemplate template =
         * OfficeTemplateImpl.createUniqueOfficeTemplate
         * (OfficeLevel.BRANCHOFFICE); OfficeBO office =
         * getOfficePersistence().createOffice(userContext, template);
         */
        OfficeBO office = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
        meeting = new MeetingBO(MeetingTemplateImpl.createWeeklyMeetingTemplateStartingFrom(disbursementDate));

        CenterTemplate centerTemplate = new CenterTemplateImpl(meeting, office.getOfficeId());
        center = getCenterPersistence().createCenter(userContext, centerTemplate);

        GroupTemplate groupTemplate = GroupTemplateImpl.createNonUniqueGroupTemplate(center.getCustomerId());

        group = createGroup(userContext, groupTemplate, disbursementDate);

        LoanOfferingBO loanOffering = createLoanOffering(userContext, meeting, disbursementDate);
        return redoLoanAccount((GroupBO) group, loanOffering, meeting, disbursementDate, feeViews);
    }

    protected LoanBO redoLoanWithMondayMeetingAndVerify(UserContext userContext, int numberOfDaysInPast,
            List<FeeView> feeViews) throws Exception {
        LoanBO loan = redoLoanWithMondayMeeting(userContext, createPreviousDate(numberOfDaysInPast), feeViews);
       Assert.assertEquals(new Money("300.0"), loan.getLoanAmount());
        return loan;
    }

    protected LoanBO redoLoanWithMeetingTodayAndVerify(UserContext userContext, int numberOfDaysInPast,
            List<FeeView> feeViews) throws Exception {
        LoanBO loan = redoLoanWithMeetingToday(userContext, createPreviousDate(numberOfDaysInPast), feeViews);
       Assert.assertEquals(new Money("300.0"), loan.getLoanAmount());
        return loan;
    }

    // inlined the persistence method to set the activation/creation date of
    // customer on or before disbursement date
    private GroupBO createGroup(UserContext userContext, GroupTemplate groupTemplate, final Date disbursementDate)
            throws PersistenceException, ValidationException, CustomerException {
        CenterBO center = null;
        if (groupTemplate.getParentCenterId() != null) {
            center = getGroupPersistence().getCenterPersistence().getCenter(groupTemplate.getParentCenterId());
            if (center == null) {
                throw new ValidationException(GroupConstants.PARENT_OFFICE_ID);
            }
        }
        GroupBO group = TestObjectFactory.createInstanceForTest(userContext, groupTemplate, center, disbursementDate);
        new GroupPersistence().saveGroup(group);
        return group;
    }

    protected LoanBO redoLoanAndVerify(UserContext userContext, Date disbursementDate, List<FeeView> feeViews)
            throws Exception {
        LoanBO loan = redoLoanWithMondayMeeting(userContext, disbursementDate, new ArrayList<FeeView>());
       Assert.assertEquals(new Money("300.0"), loan.getLoanAmount());
        return loan;
    }

    private void disburseLoanAndVerify(UserContext userContext, LoanBO loan, int numberofDaysInPast) throws Exception {
        Date disbursementDate = createPreviousDate(numberofDaysInPast);
        disburseLoan(userContext, loan, disbursementDate);
       Assert.assertEquals(disbursementDate.getTime(), loan.getDisbursementDate().getTime());

        // Validate disbursement information
        Iterator<AccountPaymentEntity> payments = loan.getAccountPayments().iterator();
        AccountPaymentEntity disbursement = payments.next();
        Iterator<AccountTrxnEntity> trxns = disbursement.getAccountTrxns().iterator();
        AccountTrxnEntity trxn;
        do {
            trxn = trxns.next();
            if (trxn.getAccountAction() == AccountActionTypes.DISBURSAL) {
                break;
            }
        } while (trxns.hasNext());
       Assert.assertEquals(AccountActionTypes.DISBURSAL, trxn.getAccountAction());
       Assert.assertEquals(loan.getLoanAmount(), trxn.getAmount());
       Assert.assertEquals(disbursementDate.getTime(), trxn.getActionDate().getTime());

    }

    private void verifyPayment(LoanBO loan, Date paymentDate, Money paymentAmount) throws Exception {

        Iterator<AccountPaymentEntity> payments = loan.getAccountPayments().iterator();
        AccountPaymentEntity payment;
        Iterator<AccountTrxnEntity> trxns = null;
        AccountTrxnEntity trxn = null;
        boolean foundThePayment = false;
        do {
            payment = payments.next();
            trxns = payment.getAccountTrxns().iterator();
            do {
                trxn = trxns.next();
                if (trxn.getAccountAction() == AccountActionTypes.LOAN_REPAYMENT
                        && trxn.getAmount().equals(paymentAmount)) {
                    foundThePayment = true;
                   Assert.assertEquals(paymentDate, trxn.getActionDate());
                    break;
                }
            } while (trxns.hasNext());
        } while (payments.hasNext());
       Assert.assertTrue("Couldnt find a LOAN_REPAYMENT", foundThePayment);
    }

    protected void applyAndVerifyPayment(UserContext userContext, LoanBO loan, int numberOfDaysInPast, Money amount)
            throws Exception {

        Date paymentDate = createPreviousDate(numberOfDaysInPast);
        applyPaymentForLoan(userContext, loan, paymentDate, amount);
        verifyPayment(loan, paymentDate, amount);

    }

    protected void removeAccountFee(LoanBO loan) throws Exception {
        for (AccountFeesEntity accountFeesEntity : loan.getAccountFees()) {
            loan.removeFees(accountFeesEntity.getFees().getFeeId(), Short.valueOf("1"));
        }
    }

    private FeeBO createOneTimeAmountFee(double amount) {
        fee = TestObjectFactory.createOneTimeAmountFee("oneTimeAmountFee", FeeCategory.GROUP, String.valueOf(amount),
                FeePayment.TIME_OF_FIRSTLOANREPAYMENT);
        return fee;
    }

    private List<FeeView> createFeeViewsWithOneTimeAmountFee(double amount) {
        List<FeeView> feeViews = new ArrayList<FeeView>();
        FeeBO upFrontAmountFee = createOneTimeAmountFee(amount);
        feeViews.add(new FeeView(userContext, upFrontAmountFee));
        return feeViews;
    }

    private FeeBO createPeriodicAmountFee(double amount) {
        fee = TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee", FeeCategory.GROUP, String.valueOf(amount),
                RecurrenceType.WEEKLY, (short) 1);
        return fee;
    }

    private List<FeeView> createFeeViewsWithPeriodicAmountFee(double amount) {
        List<FeeView> feeViews = new ArrayList<FeeView>();
        FeeBO upFrontAmountFee = createPeriodicAmountFee(amount);
        feeViews.add(new FeeView(userContext, upFrontAmountFee));
        return feeViews;
    }

    private FeeBO createPeriodicRateFee(double rate) {
        fee = TestObjectFactory.createPeriodicRateFee("PeriodicRateFee", FeeCategory.GROUP, rate, FeeFormula.AMOUNT,
                RecurrenceType.WEEKLY, (short) 1);
        return fee;
    }

    private List<FeeView> createFeeViewsWithPeriodicRateFee(double rate) {
        List<FeeView> feeViews = new ArrayList<FeeView>();
        FeeBO periodicRateFee = createPeriodicRateFee(rate);
        feeViews.add(new FeeView(userContext, periodicRateFee));
        return feeViews;
    }

    private void applyCharge(LoanBO loan, short chargeId, double chargeAmount) throws Exception {
        loan.applyCharge(chargeId, chargeAmount);
        new TestObjectPersistence().persist(loan);
    }

    @Test
    public void testRedoLoan() throws Exception {

        // long transactionCount =
        // getStatisticsService().getSuccessfulTransactionCount();

        int loanStartDaysAgo = 14;
        int paymentDaysAgo = 8;

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, loanStartDaysAgo, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, loanStartDaysAgo);

        Assert.assertFalse(loan.havePaymentsBeenMade());

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyAndVerifyPayment(userContext, loan, paymentDaysAgo, new Money("50"));

       Assert.assertTrue(loan.havePaymentsBeenMade());

        LoanTestUtils.assertInstallmentDetails(loan, 1, 1.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    @Test
    public void testRedoLoanApplyWholeMiscPenaltyBeforeRepayments() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        Double feeAmount = new Double("33.0");
       Assert.assertTrue(loan.isRoundedAmount(feeAmount));
        applyCharge(loan, Short.valueOf(AccountConstants.MISC_PENALTY), feeAmount);

        Assert.assertFalse(loan.havePaymentsBeenMade());

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 33.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    @Test
    public void testRedoLoanApplyWholeMiscPenaltyAfterPartialPayment() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyAndVerifyPayment(userContext, loan, 7, new Money("50"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 1.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyCharge(loan, Short.valueOf(AccountConstants.MISC_PENALTY), new Double("33"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 1.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 33.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    @Test
    public void testRedoLoanApplyWholeMiscPenaltyAfterFullPayment() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        // make one full repayment
        applyAndVerifyPayment(userContext, loan, 7, new Money("51"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyCharge(loan, Short.valueOf(AccountConstants.MISC_PENALTY), new Double("33"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 33.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    @Test
    public void testRedoLoanApplyFractionalMiscPenaltyBeforeRepayments() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        applyCharge(loan, Short.valueOf(AccountConstants.MISC_PENALTY), new Double("33.7"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 51.2, 0.1, 0.0, 0.0, 33.7);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.2, 0.8, 0.0, 0.0, 0.0);
    }

    @Test(expected = org.mifos.application.accounts.exceptions.AccountException.class)
    public void testRedoLoanApplyFractionalMiscPenaltyAfterPartialPayment() throws Exception {

        try {
            LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
            disburseLoanAndVerify(userContext, loan, 14);

            LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

            applyAndVerifyPayment(userContext, loan, 7, new Money("50"));

            LoanTestUtils.assertInstallmentDetails(loan, 1, 1.0, 0.0, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

            Assert.assertFalse(loan.isRoundedAmount(33.7));
            Assert.assertFalse(loan.canApplyMiscCharge(new Money(new BigDecimal(33.7))));
            // Should throw AccountExcption
            applyCharge(loan, Short.valueOf(AccountConstants.MISC_PENALTY), new Double("33.7"));

            LoanTestUtils.assertInstallmentDetails(loan, 1, 1.0, 0.0, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 51.2, 0.1, 0.0, 0.0, 33.7);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.2, 0.8, 0.0, 0.0, 0.0);
            Assert.fail("Expected AccountException !!");
        } catch (AccountException e) {
        }
    }

    public void testRedoLoanApplyFractionalMiscPenaltyAfterFullPayment() throws Exception {

        try {
            LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
            disburseLoanAndVerify(userContext, loan, 14);

            LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

            // make one full repayment
            applyAndVerifyPayment(userContext, loan, 7, new Money("51"));

            LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

            // Should throw AccountException
            applyCharge(loan, Short.valueOf(AccountConstants.MISC_PENALTY), new Double("33.7"));

            LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 51.2, 0.1, 0.0, 0.0, 33.7);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.2, 0.8, 0.0, 0.0, 0.0);
            Assert.fail("Expected AccountException !!");
        } catch (AccountException e) {
        }
    }

    @Test
    public void testRedoLoanApplyWholeMiscFeeBeforeRepayments() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        Double feeAmount = new Double("33.0");
       Assert.assertTrue(loan.isRoundedAmount(feeAmount));
        applyCharge(loan, Short.valueOf(AccountConstants.MISC_FEES), feeAmount);

        Assert.assertFalse(loan.havePaymentsBeenMade());

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 33.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    @Test
    public void testRedoLoanApplyWholeMiscFeeAfterPartialPayment() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyAndVerifyPayment(userContext, loan, 7, new Money("50"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 1.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyCharge(loan, Short.valueOf(AccountConstants.MISC_FEES), new Double("33"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 1.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 33.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    public void testRedoLoanApplyWholeMiscFeeAfterFullPayment() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        // make one full repayment
        applyAndVerifyPayment(userContext, loan, 7, new Money("51"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyCharge(loan, Short.valueOf(AccountConstants.MISC_FEES), new Double("33"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 33.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    public void testRedoLoanApplyFractionalMiscFeeBeforeRepayments() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        applyCharge(loan, Short.valueOf(AccountConstants.MISC_FEES), new Double("33.7"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 51.2, 0.1, 0.0, 33.7, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.2, 0.8, 0.0, 0.0, 0.0);
    }

    public void testRedoLoanApplyFractionalMiscFeeAfterPartialPayment() throws Exception {

        try {
            LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
            disburseLoanAndVerify(userContext, loan, 14);

            LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

            applyAndVerifyPayment(userContext, loan, 7, new Money("50"));

            LoanTestUtils.assertInstallmentDetails(loan, 1, 1.0, 0.0, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

            Assert.assertFalse(loan.isRoundedAmount(33.7));
            Assert.assertFalse(loan.canApplyMiscCharge(new Money(new BigDecimal(33.7))));
            // Should throw AccountExcption
            applyCharge(loan, Short.valueOf(AccountConstants.MISC_FEES), new Double("33.7"));

            LoanTestUtils.assertInstallmentDetails(loan, 1, 1.0, 0.0, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 51.2, 0.1, 0.0, 33.7, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.2, 0.8, 0.0, 0.0, 0.0);
            Assert.fail("Expected AccountException !!");
        } catch (AccountException e) {
        }
    }

    public void testRedoLoanApplyFractionalMiscFeeAfterFullPayment() throws Exception {

        try {
            LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
            disburseLoanAndVerify(userContext, loan, 14);

            LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

            // make one full repayment
            applyAndVerifyPayment(userContext, loan, 7, new Money("51"));

            LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

            // Should throw AccountException
            applyCharge(loan, Short.valueOf(AccountConstants.MISC_FEES), new Double("33.7"));

            LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 51.2, 0.1, 0.0, 33.7, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.2, 0.8, 0.0, 0.0, 0.0);
            Assert.fail("Expected AccountException !!");
        } catch (AccountException e) {
        }
    }

    public void testRedoLoanWithOneTimeWholeAmountFee() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, createFeeViewsWithOneTimeAmountFee(10.0));

        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    public void testRedoLoanApplyOneTimeWholeAmountFeeBeforeRepayment() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyCharge(loan, createOneTimeAmountFee(10.0).getFeeId(), 10.0);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    public void testRedoLoanApplyOneTimeWholeAmountFeeAfterRepayment() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.5, 0.5, 0.0, 0.0, 0.0);

        // make one full repayment
        applyAndVerifyPayment(userContext, loan, 7, new Money("51"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyCharge(loan, createOneTimeAmountFee(10.0).getFeeId(), 10.0);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    public void testRedoLoanWithOneTimeFractionalAmountFee() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, createFeeViewsWithOneTimeAmountFee(10.2));
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 61.0, 50.7, 0.1, 10.2, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.7, 0.3, 0.0, 0.0, 0.0);
    }

    public void testRedoLoanApplyOneTimeFractionalAmountFeeBeforeRepayment() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyCharge(loan, createOneTimeAmountFee(10.2).getFeeId(), 10.2);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 62.0, 51.7, 0.1, 10.2, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 44.7, 1.3, 0.0, 0.0, 0.0);
    }

    public void testRedoLoanWithOneTimeFractionalAmountFeeAfterRepayment() throws Exception {
        try {
            LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
            disburseLoanAndVerify(userContext, loan, 14);

            LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.5, 0.5, 0.0, 0.0, 0.0);

            // make one full repayment
            applyAndVerifyPayment(userContext, loan, 7, new Money("51"));

            LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

            applyCharge(loan, createOneTimeAmountFee(10.2).getFeeId(), 10.2);
            Assert.fail("Expected AccountException !!");
        } catch (AccountException e) {
        }
    }

    public void testRedoLoanWithPeriodicWholeAmountFee() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, createFeeViewsWithPeriodicAmountFee(10.0));

        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 56.0, 45.5, 0.5, 10.0, 0.0, 0.0);
    }

    public void testRedoLoanApplyPeriodicWholeAmountFeeBeforeRepayment() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyCharge(loan, createPeriodicAmountFee(10.0).getFeeId(), 10.0);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 71.0, 50.9, 0.1, 20.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 56.0, 45.5, 0.5, 10.0, 0.0, 0.0);
    }

    public void testRedoLoanWithPeriodicWholeAmountFeeAfterRepayment() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.5, 0.5, 0.0, 0.0, 0.0);

        // make one full repayment
        applyAndVerifyPayment(userContext, loan, 7, new Money("51"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyCharge(loan, createPeriodicAmountFee(10.0).getFeeId(), 10.0);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 71.0, 50.9, 0.1, 20.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 61.0, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 56.0, 45.5, 0.5, 10.0, 0.0, 0.0);
    }

    public void testRedoLoanWithPeriodicRateFee() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, createFeeViewsWithPeriodicRateFee(5.0));

        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 15.0, 0.0, 0.0);
    }

    public void testRedoLoanApplyPeriodicRateFeeBeforeRepayment() throws Exception {

        LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.5, 0.5, 0.0, 0.0, 0.0);

        applyCharge(loan, createPeriodicRateFee(5.0).getFeeId(), 5.0);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 30.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 15.0, 0.0, 0.0);

    }

    public void testRedoLoanApplyPeriodicRateFeeAfterRepayment() throws Exception {
        try {
            LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, new ArrayList<FeeView>());
            disburseLoanAndVerify(userContext, loan, 14);

            LoanTestUtils.assertInstallmentDetails(loan, 1, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 51.0, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 45.5, 0.5, 0.0, 0.0, 0.0);

            // make one full repayment
            applyAndVerifyPayment(userContext, loan, 7, new Money("51"));

            LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

            // Expect AccountException
            applyCharge(loan, createPeriodicRateFee(5.0).getFeeId(), 5.0);
            Assert.fail("Expected AccountException !!");
        } catch (AccountException e) {
        }
    }

    public void testRedoLoanRemovePeriodicWholeAmountFeeBeforeRepayment() throws Exception {

        LoanBO loan = redoLoanWithMeetingTodayAndVerify(userContext, 14, createFeeViewsWithPeriodicAmountFee(10.0));

        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 10.0, 0.0, 0.0);

        removeAccountFee(loan);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    public void testRedoLoanRemovePeriodicWholeAmountFeeAfterRepayment() throws Exception {

        LoanBO loan = redoLoanWithMeetingTodayAndVerify(userContext, 14, createFeeViewsWithPeriodicAmountFee(10.0));

        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 10.0, 0.0, 0.0);

        // make one full repayment
        applyAndVerifyPayment(userContext, loan, 7, new Money("61"));

        LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 10.0, 0.0, 0.0);

        removeAccountFee(loan);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 0.0, 0.0, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 10.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);
    }

    /**
     * Removing a periodic amount fee, whose amount is more precise than the
     * rounding precision specified for the installment, should result in an
     * AccountException.
     * <p>
     * TODO: financial-calculation. This is a short-term solution (V1.1) until
     * it is determined how rounding should be applied when fees or charges are
     * added or removed partway through a loan cycle.
     */

    public void testRedoLoanRemovePeriodicFractionalAmountFeeBeforePayment() throws Exception {

        LoanBO loan = redoLoanWithMeetingTodayAndVerify(userContext, 14, createFeeViewsWithPeriodicAmountFee(5.1));

        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.8, 0.1, 5.1, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.8, 0.1, 5.1, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.8, 0.1, 5.1, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.8, 0.1, 5.1, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.8, 0.1, 5.1, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 46.0, 0.9, 5.1, 0.0, 0.0);

        removeAccountFee(loan);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.8, 0.1, 5.1, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.8, 0.1, 5.1, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.7, 1.3, 0.0, 0.0, 0.0);
    }

    /**
     * Removing a periodic amount fee, whose amount (5.1) is more precise than
     * the rounding precision specified for the installment (0), should result
     * in an AccountException.
     * <p>
     * TODO: financial-calculation. This is a short-term solution (V1.1) until
     * it is determined how rounding should be applied when fees or charges are
     * added or removed partway through a loan cycle.
     */

    public void testRedoLoanRemovePeriodicFractionalAmountFeeAfterPayment() throws Exception {
        try {
            LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, createFeeViewsWithPeriodicAmountFee(5.1));

            disburseLoanAndVerify(userContext, loan, 14);

            applyAndVerifyPayment(userContext, loan, 7, new Money("50"));

            // expect account exception
            removeAccountFee(loan);
            Assert.fail("Expected AccountException !!");
        } catch (AccountException e) {
        }
    }

    public void testRedoLoanRemovePeriodicRateFeeBeforeRepayment() throws Exception {

        LoanBO loan = redoLoanWithMeetingTodayAndVerify(userContext, 14, createFeeViewsWithPeriodicRateFee(5.0));

        disburseLoanAndVerify(userContext, loan, 14);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 15.0, 0.0, 0.0);

        removeAccountFee(loan);

        LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 15.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 0.0, 0.0, 0.0);
        LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 0.0, 0.0, 0.0);

    }

    public void testRedoLoanRemovePeriodicRateFeeAfterRepayment() throws Exception {
        try {
            LoanBO loan = redoLoanWithMondayMeetingAndVerify(userContext, 14, createFeeViewsWithPeriodicRateFee(5.0));

            disburseLoanAndVerify(userContext, loan, 14);

            LoanTestUtils.assertInstallmentDetails(loan, 1, 50.9, 0.1, 15.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 2, 50.9, 0.1, 15.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 3, 50.9, 0.1, 15.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 4, 50.9, 0.1, 15.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 5, 50.9, 0.1, 15.0, 0.0, 0.0);
            LoanTestUtils.assertInstallmentDetails(loan, 6, 45.5, 0.5, 15.0, 0.0, 0.0);

            applyAndVerifyPayment(userContext, loan, 7, new Money("50"));

            // Expect AccountException
            removeAccountFee(loan);
            Assert.fail("Expected AccountException !!");
        } catch (AccountException e) {
        }
    }


    private CenterPersistence getCenterPersistence() {
        return centerPersistence;
    }

    private GroupPersistence getGroupPersistence() {
        return groupPersistence;
    }
}
