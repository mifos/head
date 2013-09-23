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

package org.mifos.accounts.loan.persistence;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.loan.business.LoanFeeScheduleEntity;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.OriginalLoanFeeScheduleEntity;
import org.mifos.accounts.loan.business.OriginalLoanScheduleEntity;
import org.mifos.accounts.loan.business.matchers.OriginalLoanFeeScheduleEntityMatcher;
import org.mifos.accounts.loan.business.matchers.OriginalLoanScheduleEntitiesMatcher;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class LegacyLoanDaoIntegrationTest extends MifosIntegrationTestCase {

    private static final double DELTA = 0.00000001;

    @Autowired
    private LegacyLoanDao legacyLoanDao;

    // collaborators
    private CustomerBO center;
    private CustomerBO group;
    private MeetingBO meeting;
    private AccountBO loanAccount;
    private AccountBO loanAccountForDisbursement;
    private LoanBO badAccount;
    private LoanBO goodAccount;

    @Autowired
    private LegacyAccountDao legacyAccountDao;

    @Before
    public void setUp() throws Exception {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenterForTestGetLoanAccounts("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);

        loanAccount = getLoanAccount(group, meeting);
        badAccount = getBadAccount();
        goodAccount = getGoodAccount();
    }

    @After
    public void tearDown() throws Exception {
        try {
            loanAccount = null;
            badAccount = null;
            goodAccount = null;
            loanAccountForDisbursement = null;
            group = null;
            center = null;
        } catch (Exception e) {

        } finally {
            StaticHibernateUtil.flushSession();
        }
    }

    @Test
    public void testFindBySystemId() throws Exception {
        LegacyLoanDao loanPersistance = legacyLoanDao;
        LoanBO loanBO = loanPersistance.findBySystemId(loanAccount.getGlobalAccountNum());
        Assert.assertEquals(loanAccount.getGlobalAccountNum(), loanBO.getGlobalAccountNum());
        Assert.assertEquals(loanAccount.getAccountId(), loanBO.getAccountId());
    }

    @Test
    public void testFindByExternalId() throws Exception {
        String externalId = "ABC";
        StaticHibernateUtil.startTransaction();
        loanAccount.setExternalId(externalId);
        StaticHibernateUtil.flushSession();

        LegacyLoanDao loanPersistance = legacyLoanDao;
        LoanBO loanBO = loanPersistance.findByExternalId(loanAccount.getExternalId());
        Assert.assertEquals(externalId, loanBO.getExternalId());
        Assert.assertEquals(loanAccount.getAccountId(), loanBO.getAccountId());
    }

    @Test
    public void testFindIndividualLoans() throws Exception {
        LegacyLoanDao loanPersistance = legacyLoanDao;
        List<LoanBO> listLoanBO = loanPersistance.findIndividualLoans(loanAccount.getAccountId().toString());

        Assert.assertEquals(0, listLoanBO.size());
    }

    @Ignore
    @Test
    public void testGetFeeAmountAtDisbursement() throws Exception {
        loanAccountForDisbursement = getLoanAccount("cdfg", group, meeting, AccountState.LOAN_APPROVED);
        Assert.assertEquals(30.0,
                legacyLoanDao.getFeeAmountAtDisbursement(loanAccountForDisbursement.getAccountId(), getCurrency()).getAmountDoubleValue(), DELTA);
    }

    @Test
    public void testGetLoanAccountsInArrearsInGoodStanding() throws PersistenceException, InvalidDateException {
        Short latenessDays = 1;
        Calendar actionDate = new GregorianCalendar();
        int year = actionDate.get(Calendar.YEAR);
        int month = actionDate.get(Calendar.MONTH);
        int day = actionDate.get(Calendar.DAY_OF_MONTH);
        actionDate = new GregorianCalendar(year, month, day - latenessDays);

        Date date = new Date(actionDate.getTimeInMillis());
        Calendar checkDate = new GregorianCalendar(year, month, day - 15);
        Date startDate = new Date(checkDate.getTimeInMillis());
        for (AccountActionDateEntity accountAction : loanAccount.getAccountActionDates()) {
            LoanBOTestUtils.setActionDate(accountAction, startDate);
        }
        TestObjectFactory.updateObject(loanAccount);
        loanAccount = legacyAccountDao.getAccount(loanAccount.getAccountId());
        List<Integer> list = legacyLoanDao.getLoanAccountsInArrearsInGoodStanding(latenessDays);
        Assert.assertNotNull(list);
        LoanBO testBO = TestObjectFactory.getObject(LoanBO.class, list.get(0));
        Assert
                .assertEquals(Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING), testBO.getAccountState()
                        .getId());

        // Get the first action date i.e for the first Installment
        Short firstInstallment = 1;
        AccountActionDateEntity actionDates = testBO.getAccountActionDate(firstInstallment);

        Assert.assertTrue(date.after(actionDates.getActionDate()));
        Assert.assertFalse(actionDates.isPaid());
    }

    @Test
    public void testGetAccount() throws Exception {
        LoanBO loanBO = legacyLoanDao.getAccount(loanAccount.getAccountId());
        Assert.assertEquals(loanBO.getAccountId(), loanAccount.getAccountId());
    }

    @Test
    public void testGetLoanAccountsActiveInGoodBadStanding() throws PersistenceException {
        List<LoanBO> loanBO1 = legacyLoanDao.getLoanAccountsActiveInGoodBadStanding(loanAccount.getCustomer()
                .getCustomerId());
        Assert.assertEquals(3, loanBO1.size());
    }

    @Test
    public void testGetLastPaymentAction() throws Exception {
        Date startDate = new Date(System.currentTimeMillis());
        loanAccountForDisbursement = getLoanAccount(AccountState.LOAN_APPROVED, startDate);
        disburseLoan(startDate);
        Assert.assertEquals("Last payment action should be 'PAYMENT'", AccountActionTypes.DISBURSAL.getValue(),
                legacyLoanDao.getLastPaymentAction(loanAccountForDisbursement.getAccountId()));
    }

    @Test
    public void testGetLoanOffering() throws Exception {
        LoanOfferingBO loanOffering = TestObjectFactory.createCompleteLoanOfferingObject();
        LoanOfferingBO loanOfferingBO = legacyLoanDao.getLoanOffering(loanOffering.getPrdOfferingId(),
                TestObjectFactory.TEST_LOCALE);
        Assert.assertEquals(loanOfferingBO.getPrdOfferingId(), loanOffering.getPrdOfferingId());
        TestObjectFactory.removeObject(loanOfferingBO);
    }

    @SuppressWarnings("deprecation")
    private void disburseLoan(final Date startDate) throws Exception {
        ((LoanBO) loanAccountForDisbursement).disburseLoan("1234", startDate, Short.valueOf("1"),
                loanAccountForDisbursement.getPersonnel(), startDate, Short.valueOf("1"), Short.valueOf("1"), null);
        StaticHibernateUtil.flushSession();
    }

    private AccountBO getLoanAccount(final AccountState state, final Date startDate) {
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loanvcfg", "bhgf", ApplicableTo.GROUPS,
                startDate, PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, meeting);
        final int disbursalType = 1;
        return TestObjectFactory.createLoanAccountWithDisbursement("99999999999", group, state, startDate,
                loanOffering, disbursalType);
    }

    private AccountBO getLoanAccount(final String shortName, final CustomerBO customer, final MeetingBO meeting,
                                     final AccountState state) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loan123", shortName, ApplicableTo.GROUPS,
                startDate, PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, meeting);
        final int disbursalType = 1;
        return TestObjectFactory.createLoanAccountWithDisbursement("42423142341", customer, state, startDate, loanOffering, disbursalType);

    }

    private AccountBO getLoanAccount(final CustomerBO customer, final MeetingBO meeting) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loancfgb", "dhsq", ApplicableTo.GROUPS,
                startDate, PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", customer, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

    }

    private LoanBO getBadAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loanabcd", "abcd", ApplicableTo.CLIENTS,
                startDate, PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, meeting);
        return TestObjectFactory.createLoanAccount("42423142323", group, AccountState.LOAN_ACTIVE_IN_BAD_STANDING,
                startDate, loanOffering);
    }

    private LoanBO getGoodAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loanabf", "abf", ApplicableTo.CLIENTS,
                startDate, PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, meeting);
        return TestObjectFactory.createLoanAccount("42423142342", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    @Ignore
    @Test
    public void testGetLoanAccountsInActiveBadStandingShouldReturnLoanBOInActiveBadByBranchId() throws Exception {
        short branchId = 3;
        List<LoanBO> loanList = legacyLoanDao.getLoanAccountsInActiveBadStanding(branchId, (short) -1, (short) -1);
        Assert.assertEquals(1, loanList.size());
    }

    @Ignore
    @Test
    public void testGetLoanAccountsInActiveBadStandingShouldReturnLoanBOInActiveBadByBranchIdAndLoanOfficerId()
            throws Exception {
        short branchId = 3;
        short loanOfficerId = 3;
        List<LoanBO> loanList = legacyLoanDao.getLoanAccountsInActiveBadStanding(branchId, loanOfficerId, (short) -1);
        Assert.assertEquals(1, loanList.size());

    }

    @Ignore
    @Test
    public void testGetLoanAccountsInActiveBadStandingShouldReturnLoanBOInActiveBadByBranchIdLoanOfficerIdAndLoanProductId()
            throws Exception {
        short branchId = 3;
        short loanOfficerId = 3;
        short loanProductId = badAccount.getLoanOffering().getPrdOfferingId();
        List<LoanBO> loanList = legacyLoanDao.getLoanAccountsInActiveBadStanding(branchId, loanOfficerId,
                loanProductId);
        Assert.assertEquals(1, loanList.size());

    }

    @Ignore
    @Test
    public void testGetTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStandingByBranchId() throws Exception {
        short branchId = 3;
        BigDecimal money = legacyLoanDao.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(branchId,
                (short) -1, (short) -1);
        Assert.assertEquals(0, new BigDecimal(600).compareTo(money));
    }

    @Ignore
    @Test
    public void testGetTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStandingByBranchIdAndLoanOfficerId()
            throws Exception {
        short branchId = 3;
        short loanOfficerId = 3;
        BigDecimal money = legacyLoanDao.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(branchId,
                loanOfficerId, (short) -1);
        Assert.assertEquals(0, new BigDecimal(600).compareTo(money));
    }

    @Test
    public void testGetTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStandingByBranchIdLoanOfficerIdAndLoanProductId()
            throws Exception {
        short branchId = 3;
        short loanOfficerId = 3;
        short loanProductId = goodAccount.getLoanOffering().getPrdOfferingId();
        BigDecimal money = legacyLoanDao.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(branchId,
                loanOfficerId, loanProductId);
        Assert.assertEquals(0, new BigDecimal(300).compareTo(money));
    }

    @Test
    public void testGetActiveLoansBothInGoodAndBadStandingByLoanOfficer() throws Exception {
        short branchId = 3;
        short loanOfficerId = 3;
        List<LoanBO> loanList = legacyLoanDao.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId,
                loanOfficerId, (short) -1);
        Assert.assertEquals(3, loanList.size());
    }

    @Test
    public void testGetActiveLoansBothInGoodAndBadStandingByLoanOfficerAndLoanProduct() throws Exception {
        short branchId = 3;
        short loanOfficerId = 3;
        short goodLoanProductId = goodAccount.getLoanOffering().getPrdOfferingId();
        List<LoanBO> goodLoanList = legacyLoanDao.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId,
                loanOfficerId, goodLoanProductId);
        Assert.assertEquals(1, goodLoanList.size());

        short badLoanProductId = badAccount.getLoanOffering().getPrdOfferingId();
        List<LoanBO> badLoanList = legacyLoanDao.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId,
                loanOfficerId, badLoanProductId);
        Assert.assertEquals(1, badLoanList.size());
    }

    @Test
    public void testSaveAndGetOriginalLoanScheduleEntity() throws PersistenceException, IOException {
        ArrayList<OriginalLoanScheduleEntity> originalLoanScheduleEntities = new ArrayList<OriginalLoanScheduleEntity>();
        Short installmentId = new Short("1");
        Date date = new Date(new java.util.Date().getTime());
        LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(goodAccount, group, installmentId, date, PaymentStatus.UNPAID, Money.zero(), Money.zero());
        originalLoanScheduleEntities.add(new OriginalLoanScheduleEntity(loanScheduleEntity));
        legacyLoanDao.saveOriginalSchedule(originalLoanScheduleEntities);
        List<OriginalLoanScheduleEntity> actual = legacyLoanDao.getOriginalLoanScheduleEntity(goodAccount.getAccountId());
        Assert.assertEquals(1, actual.size());
        Assert.assertNotNull(actual.get(0));
        assertThat(actual, is(new OriginalLoanScheduleEntitiesMatcher(originalLoanScheduleEntities)));
    }

    @Test
    public void testSaveAndGetOriginalLoanScheduleEntityWithFees() throws PersistenceException {
        ArrayList<OriginalLoanScheduleEntity> originalLoanScheduleEntities = new ArrayList<OriginalLoanScheduleEntity>();
        Date date = new Date(new java.util.Date().getTime());
        Short installmentId = new Short("1");

        FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee", FeeCategory.LOAN,
                Double.valueOf("20"), FeeFormula.AMOUNT, FeePayment.UPFRONT, null);
        AccountFeesEntity accountUpfrontFee = new AccountFeesEntity(goodAccount, upfrontFee, new Double("20.0"),
                FeeStatus.ACTIVE.getValue(), null, date);
        AccountTestUtils.addAccountFees(accountUpfrontFee, goodAccount);
        TestObjectFactory.updateObject(goodAccount);
        goodAccount = (LoanBO) TestObjectFactory.getObject(AccountBO.class, goodAccount.getAccountId());

        LoanScheduleEntity loanScheduleEntity = new LoanScheduleEntity(goodAccount, group, installmentId, date, PaymentStatus.UNPAID, Money.zero(), Money.zero());
        LoanFeeScheduleEntity feesEntity = new LoanFeeScheduleEntity(loanScheduleEntity, upfrontFee, accountUpfrontFee, Money.zero());
        loanScheduleEntity.addAccountFeesAction(feesEntity);

        OriginalLoanScheduleEntity originalLoanScheduleEntity = new OriginalLoanScheduleEntity(loanScheduleEntity);
        OriginalLoanFeeScheduleEntity scheduleEntityFee = new OriginalLoanFeeScheduleEntity(feesEntity, originalLoanScheduleEntity);

        originalLoanScheduleEntities.add(originalLoanScheduleEntity);

        legacyLoanDao.saveOriginalSchedule(originalLoanScheduleEntities);

        List<OriginalLoanScheduleEntity> actual = legacyLoanDao.getOriginalLoanScheduleEntity(goodAccount.getAccountId());

        List<OriginalLoanFeeScheduleEntity> fees = new ArrayList<OriginalLoanFeeScheduleEntity>(actual.get(0).getAccountFeesActionDetails());
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals(1, fees.size());
        assertThat(actual, is(new OriginalLoanScheduleEntitiesMatcher(originalLoanScheduleEntities)));
        assertThat(fees.get(0), is(new OriginalLoanFeeScheduleEntityMatcher(scheduleEntityFee)));
    }

    @Test
    public void testGetAllLoanAccounts() throws Exception {
        List<LoanBO> loanAccounts = legacyLoanDao.getAllLoanAccounts();
        Assert.assertEquals(3, loanAccounts.size());
    }
}
