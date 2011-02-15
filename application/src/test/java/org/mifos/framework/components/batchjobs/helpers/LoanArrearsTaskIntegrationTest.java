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

package org.mifos.framework.components.batchjobs.helpers;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.core.MifosResourceUtil;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.beans.factory.annotation.Autowired;
public class LoanArrearsTaskIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    private LegacyAccountDao legacyAccountDao;

    MifosScheduler mifosScheduler = null;

    String jobName = null;

    CustomerBO center = null;

    CustomerBO group = null;

    MeetingBO meeting = null;

    AccountBO loanAccount = null;

    @Before
    public void setUp() throws Exception {

        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/loanArrearsTestTask.xml");
        jobName = "LoanArrearsTaskJob";
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        loanAccount = getLoanAccount(group, meeting);
    }

    @After
    public void tearDown() throws Exception {
        mifosScheduler.shutdown();
        mifosScheduler = null;
        loanAccount = null;
        group = null;
        center = null;
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testExecute() throws Exception {
        int statusChangeHistorySize = loanAccount.getAccountStatusChangeHistory().size();
        mifosScheduler.runIndividualTask(jobName);
        Thread.sleep(1500);
        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertEquals(1, jobInstances.size());
        JobInstance lastInstance = jobInstances.get(0);
        List<JobExecution> jobExecutions = explorer.getJobExecutions(lastInstance);
        Assert.assertEquals(1, jobExecutions.size());
        JobExecution lastExecution = jobExecutions.get(0);
        Assert.assertEquals(BatchStatus.COMPLETED, lastExecution.getStatus());
        StaticHibernateUtil.getSessionTL().refresh(loanAccount);
        loanAccount = legacyAccountDao.getAccount(loanAccount.getAccountId());
        Assert.assertEquals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING, loanAccount.getState());
        Assert.assertEquals(statusChangeHistorySize + 1, loanAccount.getAccountStatusChangeHistory().size());
    }

    private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) throws AccountException {
        Date currentdate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(currentdate, meeting);
        loanAccount = TestObjectFactory.createLoanAccount("42423142341", customer,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, currentdate, loanOffering);
        setDisbursementDateAsOldDate(loanAccount);
        loanAccount.update();
        StaticHibernateUtil.flushSession();
        return loanAccount;
    }

    private MifosScheduler getMifosScheduler(String taskConfigurationPath) throws TaskSystemException, IOException, FileNotFoundException {
        ConfigurationLocator mockConfigurationLocator = createMock(ConfigurationLocator.class);
        expect(mockConfigurationLocator.getFileInputStream(SchedulerConstants.CONFIGURATION_FILE_NAME)).andReturn(
                MifosResourceUtil.getClassPathResourceAsStream(taskConfigurationPath));
        expectLastCall().times(2);
        replay(mockConfigurationLocator);
        MifosScheduler mifosScheduler = new MifosScheduler();
        mifosScheduler.setConfigurationLocator(mockConfigurationLocator);
        mifosScheduler.initialize();
        return mifosScheduler;
    }

    private void setDisbursementDateAsOldDate(AccountBO account) {
        Date startDate = offSetCurrentDate(15);
        LoanBO loan = (LoanBO) account;
        LoanBOTestUtils.modifyDisbursementDate(loan, startDate);
        for (AccountActionDateEntity actionDate : loan.getAccountActionDates()) {
            LoanBOTestUtils.setActionDate(actionDate, offSetGivenDate(actionDate.getActionDate(), 18));
        }
    }

    private java.sql.Date offSetGivenDate(Date date, int numberOfDays) {
        Calendar dateCalendar = new GregorianCalendar();
        dateCalendar.setTimeInMillis(date.getTime());
        int year = dateCalendar.get(Calendar.YEAR);
        int month = dateCalendar.get(Calendar.MONTH);
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        dateCalendar = new GregorianCalendar(year, month, day - numberOfDays);
        return new java.sql.Date(dateCalendar.getTimeInMillis());
    }

    private java.sql.Date offSetCurrentDate(int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }
}
