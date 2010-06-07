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

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayDetails;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class LoanApplyFeeSchedulingIntegrationTest extends MifosIntegrationTestCase {

    public LoanApplyFeeSchedulingIntegrationTest() throws Exception {
        super();
    }

    private LoanOfferingBO loanOffering = null;
    private AccountBO accountBO = null;
    private AccountBO badAccountBO = null;
    private CustomerBO center = null;
    private CustomerBO group = null;
    private CustomerBO client = null;
    private DateTimeService dateTimeService = new DateTimeService();
    private FeeBO periodicFee;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.removeObject(loanOffering);
            if (accountBO != null) {
                accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                        accountBO.getAccountId());
            }
            if (badAccountBO != null) {
                badAccountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                        badAccountBO.getAccountId());
            }
            if (group != null) {
                group = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, group.getCustomerId());
            }
            if (center != null) {
                center = (CustomerBO) StaticHibernateUtil.getSessionTL().get(CustomerBO.class, center.getCustomerId());
            }
            TestObjectFactory.cleanUp(accountBO);
            TestObjectFactory.cleanUp(badAccountBO);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
            deleteHolidays();
//            new OfficePersistence().getOffice((short)1).getHolidays().clear();
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        } finally {
            StaticHibernateUtil.closeSession();
            new DateTimeService().resetToCurrentSystemDateTime();
            new ConfigurationPersistence().updateConfigurationKeyValueInteger("repaymentSchedulesIndependentOfMeetingIsEnabled", 0);
        }
    }

    public void testApplyPeriodicFeeWithNextWorkingDayHoliday() throws Exception {
        DateTime startDate = date(2008,5,23); //Friday
        dateTimeService.setCurrentDateTime(startDate);

        accountBO = getLoanAccount(startDate.toDate(), AccountState.LOAN_APPROVED);

        Money intialTotalFeeAmount = ((LoanBO) accountBO).getLoanSummary().getOriginalFees();
        TestObjectFactory.flushandCloseSession();

        // create holiday on first installment date
        buildAndPersistHoliday(startDate.plusWeeks(1), startDate.plusWeeks(1),
                RepaymentRuleTypes.NEXT_WORKING_DAY);

        StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
            LoanBO loanBO = (LoanBO) accountBO;
            loanBO.updateLoan(loanBO.isInterestDeductedAtDisbursement(), loanBO.getLoanAmount(), loanBO
                    .getInterestRate(), loanBO.getNoOfInstallments(), loanBO.getDisbursementDate(), loanBO
                    .getGracePeriodDuration(), loanBO.getBusinessActivityId(), "Loan account updated", null, null,
                    false, null, null);

            periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.LOAN, "25",
                    RecurrenceType.WEEKLY, EVERY_WEEK);

            UserContext uc = TestUtils.makeUser();
            loanBO.setUserContext(uc);
            loanBO.applyCharge(periodicFee.getFeeId(), ((AmountFeeBO) periodicFee).getFeeAmount()
                    .getAmountDoubleValue());
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();

            Map<String, String> fees1 = new HashMap<String, String>();
            fees1.put("Mainatnence Fee", "100.0");

            Map<String, String> fees2 = new HashMap<String, String>();
            fees2.put("Mainatnence Fee", "100.0");
            fees2.put("Periodic Fee", "25.0");

            LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(loanBO
                    .getAccountActionDates());

            List<Date> installmentDates = new ArrayList<Date>();
            List<Date> feeDates = new ArrayList<Date>();
            for (LoanScheduleEntity loanScheduleEntity : paymentsArray) {
                installmentDates.add(loanScheduleEntity.getActionDate());
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity
                        .getAccountFeesActionDetails()) {
                    Date feeDate = accountFeesActionDetailEntity.getAccountActionDate().getActionDate();
                    feeDates.add(feeDate);
                }
            }

            Assert.assertEquals(6, paymentsArray.length);

            checkFees(fees2, paymentsArray[0], false);
            checkFees(fees2, paymentsArray[1], false);
            checkFees(fees2, paymentsArray[2], false);
            checkFees(fees2, paymentsArray[3], false);
            checkFees(fees2, paymentsArray[4], false);
            checkFees(fees2, paymentsArray[5], false);

            List<Date> expectedDates = new ArrayList<Date>();
            expectedDates.add(DateUtils.getDate(2008, Calendar.MAY, 31));
            expectedDates.add(DateUtils.getDate(2008, Calendar.JUNE, 06));
            expectedDates.add(DateUtils.getDate(2008, Calendar.JUNE, 13));
            expectedDates.add(DateUtils.getDate(2008, Calendar.JUNE, 20));
            expectedDates.add(DateUtils.getDate(2008, Calendar.JUNE, 27));
            expectedDates.add(DateUtils.getDate(2008, Calendar.JULY, 04));

            int i = 0;
            for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
                LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
                Assert.assertEquals(expectedDates.get(i++), loanScheduleEntity.getActionDate());
                Assert.assertEquals(new Money(getCurrency(), "125"), loanScheduleEntity.getTotalFees());
            }

            Assert.assertEquals(intialTotalFeeAmount.add(new Money(getCurrency(), "750.0")), loanBO.getLoanSummary()
                    .getOriginalFees());
    }

    public void testApplyPeriodicFeeWithMoratorium() throws Exception {
        DateTime startDate = date(2008,5,23); //Friday
        dateTimeService.setCurrentDateTime(startDate);

        accountBO = getLoanAccount(startDate.toDate(), AccountState.LOAN_APPROVED);

        Money intialTotalFeeAmount = ((LoanBO) accountBO).getLoanSummary().getOriginalFees();
        TestObjectFactory.flushandCloseSession();

        // create holiday on first installment date
        buildAndPersistHoliday(startDate.plusWeeks(1), startDate.plusWeeks(1),
                RepaymentRuleTypes.REPAYMENT_MORATORIUM);

            StaticHibernateUtil.getSessionTL();
            StaticHibernateUtil.startTransaction();
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
            LoanBO loanBO = (LoanBO) accountBO;
            loanBO.updateLoan(loanBO.isInterestDeductedAtDisbursement(), loanBO.getLoanAmount(), loanBO
                    .getInterestRate(), loanBO.getNoOfInstallments(), loanBO.getDisbursementDate(), loanBO
                    .getGracePeriodDuration(), loanBO.getBusinessActivityId(), "Loan account updated", null, null,
                    false, null, null);

            periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.LOAN, "25",
                    RecurrenceType.WEEKLY, EVERY_WEEK);

            UserContext uc = TestUtils.makeUser();
            loanBO.setUserContext(uc);
            loanBO.applyCharge(periodicFee.getFeeId(), ((AmountFeeBO) periodicFee).getFeeAmount()
                    .getAmountDoubleValue());
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();

            Map<String, String> fees1 = new HashMap<String, String>();
            fees1.put("Mainatnence Fee", "100.0");

            Map<String, String> fees2 = new HashMap<String, String>();
            fees2.put("Mainatnence Fee", "100.0");
            fees2.put("Periodic Fee", "25.0");

            LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(loanBO
                    .getAccountActionDates());

            List<Date> installmentDates = new ArrayList<Date>();
            List<Date> feeDates = new ArrayList<Date>();
            for (LoanScheduleEntity loanScheduleEntity : paymentsArray) {
                installmentDates.add(loanScheduleEntity.getActionDate());
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity
                        .getAccountFeesActionDetails()) {
                    Date feeDate = accountFeesActionDetailEntity.getAccountActionDate().getActionDate();
                    feeDates.add(feeDate);
                }
            }
            System.out.println(installmentDates);
            System.out.println(feeDates);

            Assert.assertEquals(6, paymentsArray.length);

            checkFees(fees2, paymentsArray[0], false);
            checkFees(fees2, paymentsArray[1], false);
            checkFees(fees2, paymentsArray[2], false);
            checkFees(fees2, paymentsArray[3], false);
            checkFees(fees2, paymentsArray[4], false);
            checkFees(fees2, paymentsArray[5], false);

            List<Date> expectedDates = new ArrayList<Date>();

            // moratorium on first installment date pushes all dates out one Week from unadjusted date.
            expectedDates.add(startDate.plusWeeks(2).toDate());
            expectedDates.add(startDate.plusWeeks(3).toDate());
            expectedDates.add(startDate.plusWeeks(4).toDate());
            expectedDates.add(startDate.plusWeeks(5).toDate());
            expectedDates.add(startDate.plusWeeks(6).toDate());
            expectedDates.add(startDate.plusWeeks(7).toDate());

            int i = 0;
            for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
                LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
                Assert.assertEquals(expectedDates.get(i++), loanScheduleEntity.getActionDate());
                Assert.assertEquals(new Money(getCurrency(), "125"), loanScheduleEntity.getTotalFees());
            }

            Assert.assertEquals(intialTotalFeeAmount.add(new Money(getCurrency(), "750.0")), loanBO.getLoanSummary()
                    .getOriginalFees());
    }

    public void testApplyPeriodicFeeNoHolidayShouldApplyToFourthAndLaterInstallments() throws Exception {
        DateTime startDate = date(2008,5,23); //Friday
        dateTimeService.setCurrentDateTime(startDate);

            // This method creates a loan, then restructures its fee schedule so that the first
            // repayment is on the given startDate, may 23rd
        accountBO = getLoanAccount(startDate.toDate(), AccountState.LOAN_APPROVED);

            LoanBO loanBO = (LoanBO) accountBO;

            periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.LOAN, "25",
                    RecurrenceType.WEEKLY, EVERY_WEEK);

            UserContext uc = TestUtils.makeUser();
            loanBO.setUserContext(uc);

            // set current date to the day after the third repayment.
            dateTimeService.setCurrentDateTime(startDate.plusWeeks(2).plusDays(1));

            // charges should be applied to the fourth payment and beyond
            loanBO.applyCharge(periodicFee.getFeeId(), ((AmountFeeBO) periodicFee).getFeeAmount()
                    .getAmountDoubleValue());


            Map<String, String> fees1 = new HashMap<String, String>();
            fees1.put("Mainatnence Fee", "100.0");

            Map<String, String> fees2 = new HashMap<String, String>();
            fees2.put("Mainatnence Fee", "100.0");
            fees2.put("Periodic Fee", "25.0");

            LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(loanBO
                    .getAccountActionDates());

            List<Date> installmentDates = new ArrayList<Date>();
            List<Date> feeDates = new ArrayList<Date>();
            for (LoanScheduleEntity loanScheduleEntity : paymentsArray) {
                installmentDates.add(loanScheduleEntity.getActionDate());
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity
                        .getAccountFeesActionDetails()) {
                    Date feeDate = accountFeesActionDetailEntity.getAccountActionDate().getActionDate();
                    feeDates.add(feeDate);
                }
            }
            System.out.println(installmentDates);
            System.out.println(feeDates);

            Assert.assertEquals(6, paymentsArray.length);

            checkFees(fees1, paymentsArray[0], false);
            checkFees(fees1, paymentsArray[1], false);
            checkFees(fees1, paymentsArray[2], false);
            checkFees(fees2, paymentsArray[3], false);
            checkFees(fees2, paymentsArray[4], false);
            checkFees(fees2, paymentsArray[5], false);

            List<Date> expectedDates = new ArrayList<Date>();
            expectedDates.add(startDate.toDate());
            expectedDates.add(startDate.plusWeeks(1).toDate());
            expectedDates.add(startDate.plusWeeks(2).toDate());
            expectedDates.add(startDate.plusWeeks(3).toDate());
            expectedDates.add(startDate.plusWeeks(4).toDate());
            expectedDates.add(startDate.plusWeeks(5).toDate());

            int i = 0;
            for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
                LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
                Assert.assertEquals(expectedDates.get(i++), loanScheduleEntity.getActionDate());
                if (i <= 3) {
                Assert.assertEquals(new Money(getCurrency(), "100"), loanScheduleEntity.getTotalFees());
                } else {
                    Assert.assertEquals(new Money(getCurrency(), "125"), loanScheduleEntity.getTotalFees());
                }
            }
    }

    public void testApplyPeriodicFeeWithMoratoriumShouldApplyToFourthAndLaterInstallments() throws Exception {
        DateTime startDate = date(2008,5,23); //Friday
        dateTimeService.setCurrentDateTime(startDate);

            // This method creates a loan, then restructures its fee schedule so that the first
            // repayment is on the given startDate, may 23rd
        accountBO = getLoanAccount(startDate.toDate(), AccountState.LOAN_APPROVED);

            TestObjectFactory.flushandCloseSession();

            // create that pushes fourth and later installments out three weeks
            buildAndPersistHoliday(startDate.plusWeeks(4), startDate.plusWeeks(6),
                    RepaymentRuleTypes.REPAYMENT_MORATORIUM);

                StaticHibernateUtil.getSessionTL();
                StaticHibernateUtil.startTransaction();
            accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
                LoanBO loanBO = (LoanBO) accountBO;

                // Updating undoes the hacks that getLoanAccount() does to installment dates
                // It starts with the original disbursement date (5/23/08) and generates
                // installments from the following week, adjusting for the holiday.
                loanBO.updateLoan(loanBO.isInterestDeductedAtDisbursement(), loanBO.getLoanAmount(), loanBO
                        .getInterestRate(), loanBO.getNoOfInstallments(), loanBO.getDisbursementDate(), loanBO
                        .getGracePeriodDuration(), loanBO.getBusinessActivityId(), "Loan account updated", null, null,
                        false, null, null);

            periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.LOAN, "25",
                    RecurrenceType.WEEKLY, EVERY_WEEK);

            UserContext uc = TestUtils.makeUser();
            loanBO.setUserContext(uc);

            // set current date to the day after the second repayment.
            dateTimeService.setCurrentDateTime(startDate.plusWeeks(2).plusDays(1));

            // charges should be applied to the fourth payment and beyond
            loanBO.applyCharge(periodicFee.getFeeId(), ((AmountFeeBO) periodicFee).getFeeAmount()
                    .getAmountDoubleValue());


            Map<String, String> fees1 = new HashMap<String, String>();
            fees1.put("Mainatnence Fee", "100.0");

            Map<String, String> fees2 = new HashMap<String, String>();
            fees2.put("Mainatnence Fee", "100.0");
            fees2.put("Periodic Fee", "25.0");

            LoanScheduleEntity[] paymentsArray = LoanBOTestUtils.getSortedAccountActionDateEntity(loanBO
                    .getAccountActionDates());

            List<Date> installmentDates = new ArrayList<Date>();
            List<Date> feeDates = new ArrayList<Date>();
            for (LoanScheduleEntity loanScheduleEntity : paymentsArray) {
                installmentDates.add(loanScheduleEntity.getActionDate());
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity
                        .getAccountFeesActionDetails()) {
                    Date feeDate = accountFeesActionDetailEntity.getAccountActionDate().getActionDate();
                    feeDates.add(feeDate);
                }
            }
            System.out.println(installmentDates);
            System.out.println(feeDates);

            Assert.assertEquals(6, paymentsArray.length);

            // Note that fees no longer pile up on the first installment after the holiday
            checkFees(fees1, paymentsArray[0], false);
            checkFees(fees1, paymentsArray[1], false);
            checkFees(fees2, paymentsArray[2], false);
            checkFees(fees2, paymentsArray[3], false);
            checkFees(fees2, paymentsArray[4], false);
            checkFees(fees2, paymentsArray[5], false);

            List<Date> expectedDates = new ArrayList<Date>();
            expectedDates.add(startDate.plusWeeks(1).toDate());
            expectedDates.add(startDate.plusWeeks(2).toDate());
            expectedDates.add(startDate.plusWeeks(3).toDate());
            expectedDates.add(startDate.plusWeeks(7).toDate());
            expectedDates.add(startDate.plusWeeks(8).toDate());
            expectedDates.add(startDate.plusWeeks(9).toDate());

            int i = 0;
            for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
                LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
                Assert.assertEquals(expectedDates.get(i++), loanScheduleEntity.getActionDate());
                if (i <= 2) {
                Assert.assertEquals(new Money(getCurrency(), "100"), loanScheduleEntity.getTotalFees());
                } else {
                    Assert.assertEquals(new Money(getCurrency(), "125"), loanScheduleEntity.getTotalFees());
                }
            }
    }

    /*********************************
     * Helper Methods
     ***********************************/

    private AccountBO getLoanAccount(final Date startDate, final AccountState state) throws MeetingException {
        createInitialCustomers(startDate);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, center.getCustomerMeeting()
                .getMeeting());
        return TestObjectFactory.createLoanAccount("42423142341", group, state, startDate, loanOffering);
    }

    private void createInitialCustomers(final Date meetingStartDate) throws MeetingException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(meetingStartDate);
        // create a meeting based on meetingStartDate rather than the
        // current system date
        MeetingBO meeting = TestObjectFactory.createMeeting(new MeetingBO(WeekDay.getWeekDay(cal
                .get(Calendar.DAY_OF_WEEK)), EVERY_WEEK, meetingStartDate, CUSTOMER_MEETING, "place"));
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group",
                CustomerStatus.GROUP_ACTIVE, center);
    }


    private void checkFees(final Map<String, String> expected, final LoanScheduleEntity loanScheduleEntity,
            final boolean checkPaid) {
        Set<AccountFeesActionDetailEntity> accountFeesActionDetails = loanScheduleEntity.getAccountFeesActionDetails();
        Assert.assertEquals("fees were " + feeNames(accountFeesActionDetails), expected.size(),
                accountFeesActionDetails.size());

        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {

            if (expected.get(accountFeesActionDetailEntity.getFee().getFeeName()) != null) {
                Assert.assertEquals(new Money(TestUtils.RUPEE,
                        expected.get(accountFeesActionDetailEntity.getFee().getFeeName())),
                        checkPaid ? accountFeesActionDetailEntity.getFeeAmountPaid()
                        : accountFeesActionDetailEntity.getFeeAmount());
            } else {

                Assert.assertFalse("Fee amount not found for " + accountFeesActionDetailEntity.getFee().getFeeName(),
                        true);
            }
        }
    }

    private String feeNames(final Collection<AccountFeesActionDetailEntity> details) {
        StringBuilder debugString = new StringBuilder();
        for (Iterator<AccountFeesActionDetailEntity> iter = details.iterator(); iter.hasNext();) {
            AccountFeesActionDetailEntity detail = iter.next();
            debugString.append(detail.getFee().getFeeName());
            if (iter.hasNext()) {
                debugString.append(", ");
            }
        }
        return debugString.toString();
    }

    private DateTime date (int year, int month, int day) {
        return new DateTime().withDate(year, month, day).toDateMidnight().toDateTime();
    }


    private void buildAndPersistHoliday (DateTime start, DateTime through, RepaymentRuleTypes rule) throws ServiceException {
        HolidayDetails holidayDetails = new HolidayDetails("testHoliday", start.toDate(), through.toDate(), rule);
        List<Short> officeIds = new LinkedList<Short>();
        officeIds.add((short)1);
        DependencyInjectedServiceLocator.locateHolidayServiceFacade().createHoliday(holidayDetails, officeIds);
    }

    private void deleteHolidays() throws PersistenceException {
        OfficePersistence officePersistence = new OfficePersistence();
        Set<HolidayBO> holidays = officePersistence.getOffice(new Short("1")).getHolidays();
        HolidayBO holidayBOToDelete = null;
        for (HolidayBO holidayBO : holidays) {
            holidayBOToDelete = holidayBO;
        }
        holidays.clear();
        officePersistence.getOffice(new Short("2")).getHolidays().clear();
        officePersistence.getOffice(new Short("3")).getHolidays().clear();
        if (holidayBOToDelete != null) {
            StaticHibernateUtil.getSessionTL().delete(holidayBOToDelete);
        }
        StaticHibernateUtil.getTransaction().commit();
    }


}
