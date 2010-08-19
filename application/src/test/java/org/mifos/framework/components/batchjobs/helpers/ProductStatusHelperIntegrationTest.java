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

import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.Query;
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
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ProductStatusHelperIntegrationTest extends MifosIntegrationTestCase {

    LoanOfferingBO product;

    ProductStatusHelper productStatusHelper;

    @Before
    public void setUp() throws Exception {
        ProductStatus productStatus = new ProductStatus();
        productStatusHelper = (ProductStatusHelper) productStatus.getTaskHelper();
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
    public void testExecuteTask() throws PersistenceException, BatchJobException {
        // TODO: Create a test running ProductStatus task, testing if it completed successfully and below assertion is met:
        // createInactiveLoanOffering();
        // run task, check (Assert) if it executed correctly
        // product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        // Assert.assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());
    }

    @Test
    public void testExecuteTaskFailure() throws PersistenceException {
        // TODO: This test tested invalid task execution when DB connection was invalid. Since quartz requires a DB for
        // it's JobStore to run, we should think of a different test case..
    }

    @Test
    public void testRegisterStartup() throws BatchJobException {
        // TODO: Create a test running ProductStatus and checking if it's startup was registered correctly.
    }

    // TODO: Test cases involving invalid database connections..

    @Test
    public void testRegisterCompletion() throws BatchJobException {
        // TODO: Create a test running ProductStatus successfully task and checking if it's completion was correctly registered
    }

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
}
