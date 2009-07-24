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

package org.mifos.framework.components.batchjobs.helpers;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.hibernate.Query;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanBOTestUtils;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsBOIntegrationTest;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerAccountBOIntegrationTest;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.config.ConfigurationManager;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.configuration.util.helpers.ConfigConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CollectionSheetHelperIntegrationTest extends MifosIntegrationTest {

    public CollectionSheetHelperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private CenterBO center;
    private GroupBO group;
    private MeetingBO meeting;
    private SavingsTestHelper helper = new SavingsTestHelper();
    private SavingsOfferingBO savingsOffering;
    private LoanBO loanBO;
    private SavingsBO savingsBO;
    private ConfigurationManager configMgr = ConfigurationManager.getInstance();
    private int initialDaysInAdvance;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        initialDaysInAdvance = configMgr.getInt(ConfigConstants.COLLECTION_SHEET_DAYS_IN_ADVANCE);
    }

    @Override
    public void tearDown() throws Exception {
        configMgr.setProperty(ConfigConstants.COLLECTION_SHEET_DAYS_IN_ADVANCE, initialDaysInAdvance);
        TestObjectFactory.cleanUp(loanBO);
        TestObjectFactory.cleanUp(savingsBO);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testOneDayInAdvance() throws Exception {
        int daysInAdvance = 1;
        configMgr.setProperty(ConfigConstants.COLLECTION_SHEET_DAYS_IN_ADVANCE, daysInAdvance);
        basicTest(daysInAdvance);
    }

    public void testFiveDaysInAdvance() throws Exception {
        int daysInAdvance = 5;
        configMgr.setProperty(ConfigConstants.COLLECTION_SHEET_DAYS_IN_ADVANCE, daysInAdvance);
        basicTest(daysInAdvance);
    }

    private void basicTest(int daysInAdvance) throws Exception {
        createInitialObjects();
        loanBO = getLoanAccount(group, meeting);
        savingsBO = getSavingsAccount(center, "SAVINGS_OFFERING", "SAV");
        CollectionSheetHelper collectionSheetHelper = new CollectionSheetHelper(new CollectionSheetTask());

        assertEquals(CollectionSheetHelper.getDaysInAdvance(), daysInAdvance);

        for (AccountActionDateEntity accountActionDateEntity : center.getCustomerAccount().getAccountActionDates()) {
            CustomerAccountBOIntegrationTest.setActionDate(accountActionDateEntity, offSetDate(accountActionDateEntity
                    .getActionDate(), collectionSheetHelper.getDaysInAdvance()));
        }

        for (AccountActionDateEntity accountActionDateEntity : loanBO.getAccountActionDates()) {
            LoanBOTestUtils.setActionDate(accountActionDateEntity, offSetDate(accountActionDateEntity
                    .getActionDate(), collectionSheetHelper.getDaysInAdvance()));
        }

        for (AccountActionDateEntity accountActionDateEntity : savingsBO.getAccountActionDates()) {
            SavingsBOIntegrationTest.setActionDate(accountActionDateEntity, offSetDate(accountActionDateEntity
                    .getActionDate(), collectionSheetHelper.getDaysInAdvance()));
        }

        long runTime = System.currentTimeMillis();
        collectionSheetHelper.execute(runTime);

        List<CollectionSheetBO> collectionSheets = getCollectionSheets();
        assertEquals("Size of collectionSheets should be 1", 1, collectionSheets.size());

        CollectionSheetBO collectionSheet = collectionSheets.get(0);

        // we need to trim off time information so that we can
        // match the value returned by a java.sql.Date object which
        // also truncates all time information
        Calendar collectionSheetDate = new GregorianCalendar();
        collectionSheetDate.setTimeInMillis(runTime);
        collectionSheetDate.set(Calendar.HOUR_OF_DAY, 0);
        collectionSheetDate.set(Calendar.MINUTE, 0);
        collectionSheetDate.set(Calendar.SECOND, 0);
        collectionSheetDate.set(Calendar.MILLISECOND, 0);
        long normalizedRunTime = collectionSheetDate.getTimeInMillis();

        collectionSheetDate.roll(Calendar.DATE, collectionSheetHelper.getDaysInAdvance());
        long normalizedCollectionSheetTime = collectionSheetDate.getTimeInMillis();

        assertEquals(collectionSheet.getRunDate().getTime(), normalizedRunTime);
        assertEquals(collectionSheet.getCollSheetDate().getTime(), normalizedCollectionSheetTime);

        clearCollectionSheets(collectionSheets);
    }

    private void clearCollectionSheets(List<CollectionSheetBO> collectionSheets) {
        for (CollectionSheetBO collectionSheetBO : collectionSheets)
            TestObjectFactory.cleanUp(collectionSheetBO);
    }

    private SavingsBO getSavingsAccount(CustomerBO customer, String offeringName, String shortName) throws Exception {
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customer, AccountStates.SAVINGS_ACC_APPROVED,
                new Date(System.currentTimeMillis()), savingsOffering);
    }

    private void createInitialObjects() {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY,
                TestObjectFactory.EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.MONDAY));
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
    }

    private LoanBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", customer, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);

    }

    private java.sql.Date offSetDate(Date date, int noOfDays) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar = new GregorianCalendar(year, month, day + noOfDays);
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    private List<CollectionSheetBO> getCollectionSheets() {
        Query query = StaticHibernateUtil.getSessionTL().createQuery(
                "from org.mifos.application.collectionsheet.business.CollectionSheetBO");
        return query.list();
    }
}
