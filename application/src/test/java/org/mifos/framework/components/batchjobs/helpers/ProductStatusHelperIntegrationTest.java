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
import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.SessionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingTestUtils;
import org.mifos.accounts.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.MifosScheduler;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.batchjobs.exceptions.TaskSystemException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.core.io.ClassPathResource;

public class ProductStatusHelperIntegrationTest extends MifosIntegrationTestCase {

    MifosScheduler mifosScheduler;

    String jobName;

    LoanOfferingBO product;

    ProductStatusHelper productStatusHelper;

    @Before
    public void setUp() throws Exception {
        TestDatabase.resetMySQLDatabase();
        productStatusHelper = new ProductStatusHelper();
        jobName = "ProductStatusJob";
    }

    @After
    public void tearDown() throws Exception {
        TestObjectFactory.removeObject(product);
        StaticHibernateUtil.closeSession();
    }

    @Test
    public void testExecute() throws PersistenceException, BatchJobException {
        createInactiveLoanOffering();
        productStatusHelper.execute(System.currentTimeMillis());
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        Assert.assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());
    }

    @Test
    public void testExecuteFailure() throws PersistenceException {
        createInactiveLoanOffering();
        TestObjectFactory.simulateInvalidConnection();
        try {
            productStatusHelper.execute(System.currentTimeMillis());
            Assert.fail("unexpected absence of exception");
        } catch (BatchJobException e) {
            Assert.fail("unexpected exception");
        } catch (SessionException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        Assert.assertEquals(PrdStatus.LOAN_INACTIVE, product.getStatus());
    }

    @Test
    public void testExecuteTask() throws Exception {
        createInactiveLoanOffering();
        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/productStatusTestMockTask.xml");
        mifosScheduler.runIndividualTask(jobName);
        Thread.sleep(1000);
        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertTrue(jobInstances.size() > 0);
        JobInstance lastInstance = jobInstances.get(0);
        List<JobExecution> jobExecutions = explorer.getJobExecutions(lastInstance);
        Assert.assertEquals(1, jobExecutions.size());
        JobExecution lastExecution = jobExecutions.get(0);
        Assert.assertEquals(BatchStatus.COMPLETED, lastExecution.getStatus());
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        Assert.assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());
    }

    @Test
    public void testExecuteTaskFailure() throws Exception {
        createInactiveLoanOffering();
        mifosScheduler = getMifosScheduler("org/mifos/framework/components/batchjobs/productStatusTestMockTask2.xml");
        mifosScheduler.runIndividualTask(jobName);
        Thread.sleep(1000);
        JobExplorer explorer = mifosScheduler.getBatchJobExplorer();
        List<JobInstance> jobInstances = explorer.getJobInstances(jobName, 0, 10);
        Assert.assertTrue(jobInstances.size() > 0);
        JobInstance lastInstance = jobInstances.get(0);
        List<JobExecution> jobExecutions = explorer.getJobExecutions(lastInstance);
        Assert.assertEquals(1, jobExecutions.size());
        JobExecution lastExecution = jobExecutions.get(0);
        Assert.assertEquals(BatchStatus.FAILED, lastExecution.getStatus());
        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        Assert.assertEquals(PrdStatus.LOAN_INACTIVE, product.getStatus());
    }

    // TODO QUARTZ: Test cases involving invalid database connections..

    // TODO QUARTZ: Test cases involving testing proper listener work (to be confirmed).

    private void createInactiveLoanOffering() throws PersistenceException {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(WEEKLY, EVERY_WEEK,
                LOAN_INSTALLMENT, MONDAY));
        product = TestObjectFactory.createLoanOffering("Loan Offering", "LOAN", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, frequency);
        LoanOfferingTestUtils.setStatus(product, new PrdOfferingPersistence()
                .getPrdStatus(PrdStatus.LOAN_INACTIVE));
        TestObjectFactory.updateObject(product);
        StaticHibernateUtil.closeSession();
    }

    private MifosScheduler getMifosScheduler(String taskConfigurationPath) throws TaskSystemException, IOException, FileNotFoundException {
        ConfigurationLocator mockConfigurationLocator = createMock(ConfigurationLocator.class);
        expect(mockConfigurationLocator.getFile(SchedulerConstants.CONFIGURATION_FILE_NAME)).andReturn(
                new ClassPathResource(taskConfigurationPath).getFile());
        expectLastCall().times(2);
        replay(mockConfigurationLocator);
        MifosScheduler mifosScheduler = new MifosScheduler();
        mifosScheduler.setConfigurationLocator(mockConfigurationLocator);
        mifosScheduler.initialize();
        return mifosScheduler;
    }

}
