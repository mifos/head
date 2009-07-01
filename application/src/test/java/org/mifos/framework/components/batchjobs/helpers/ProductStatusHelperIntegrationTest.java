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

import static org.mifos.application.meeting.util.helpers.MeetingType.LOAN_INSTALLMENT;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionException;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBOIntegrationTest;
import org.mifos.application.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ProductStatusHelperIntegrationTest extends MifosIntegrationTest {

    public ProductStatusHelperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    LoanOfferingBO product;

    ProductStatusHelper productStatusHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ProductStatus productStatus = new ProductStatus();
        productStatus.name = "ProductStatus";
        productStatusHelper = (ProductStatusHelper) productStatus.getTaskHelper();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.removeObject(product);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testExecute() throws PersistenceException, BatchJobException {
        createInactiveLoanOffering();

        productStatusHelper.execute(System.currentTimeMillis());

        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());
    }

    public void testExecuteFailure() throws PersistenceException {
        createInactiveLoanOffering();

        TestObjectFactory.simulateInvalidConnection();
        try {
            productStatusHelper.execute(System.currentTimeMillis());
            fail("unexpected absence of exception");
        } catch (BatchJobException e) {
            fail("unexpected exception");
        } catch (SessionException e) {
            assertTrue(true);
        }
        StaticHibernateUtil.closeSession();

        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        assertEquals(PrdStatus.LOAN_INACTIVE, product.getStatus());
    }

    public void testExecuteTask() throws PersistenceException, BatchJobException {
        createInactiveLoanOffering();

        productStatusHelper.executeTask();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        for (Task task : tasks) {
            assertEquals(TaskStatus.COMPLETE.getValue().shortValue(), task.getStatus());
            assertEquals("ProductStatus", task.getTask());
            assertEquals(SchedulerConstants.FINISHED_SUCCESSFULLY, task.getDescription());
            TestObjectFactory.removeObject(task);
        }

        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        assertEquals(PrdStatus.LOAN_ACTIVE, product.getStatus());
    }

    public void testExecuteTaskFailure() throws PersistenceException {
        createInactiveLoanOffering();

        TestObjectFactory.simulateInvalidConnection();
        productStatusHelper.executeTask();
        StaticHibernateUtil.closeSession();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
        assertEquals(0, tasks.size());

        product = (LoanOfferingBO) TestObjectFactory.getObject(LoanOfferingBO.class, product.getPrdOfferingId());
        assertEquals(PrdStatus.LOAN_INACTIVE, product.getStatus());
    }

    public void testRegisterStartup() throws BatchJobException {
        productStatusHelper.registerStartup(System.currentTimeMillis());
        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        for (Task task : tasks) {
            assertEquals(TaskStatus.INCOMPLETE.getValue().shortValue(), task.getStatus());
            assertEquals("ProductStatus", task.getTask());
            assertEquals(SchedulerConstants.START, task.getDescription());
            TestObjectFactory.removeObject(task);
        }
    }

    public void testIsTaskAllowedToRun() {
        assertTrue(productStatusHelper.isTaskAllowedToRun());
    }

    public void testRegisterStartupFailure() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            productStatusHelper.registerStartup(System.currentTimeMillis());
            fail();
        } catch (BatchJobException e) {
            assertTrue(true);
        }
    }

    public void testRegisterCompletion() throws BatchJobException {
        productStatusHelper.registerStartup(System.currentTimeMillis());
        productStatusHelper.registerCompletion(0, SchedulerConstants.FINISHED_SUCCESSFULLY, TaskStatus.COMPLETE);
        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        for (Task task : tasks) {
            assertEquals(TaskStatus.COMPLETE.getValue().shortValue(), task.getStatus());
            assertEquals("ProductStatus", task.getTask());
            assertEquals(SchedulerConstants.FINISHED_SUCCESSFULLY, task.getDescription());
            TestObjectFactory.removeObject(task);
        }
    }

    public void testRegisterCompletionFailure() throws BatchJobException {
        productStatusHelper.registerStartup(System.currentTimeMillis());

        TestObjectFactory.simulateInvalidConnection();
        productStatusHelper.registerCompletion(0, SchedulerConstants.FINISHED_SUCCESSFULLY, TaskStatus.COMPLETE);
        StaticHibernateUtil.closeSession();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        for (Task task : tasks) {
            assertEquals(TaskStatus.INCOMPLETE.getValue().shortValue(), task.getStatus());
            assertEquals("ProductStatus", task.getTask());
            assertEquals(SchedulerConstants.START, task.getDescription());
            TestObjectFactory.removeObject(task);
        }
    }

    private void createInactiveLoanOffering() throws PersistenceException {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(WEEKLY, EVERY_WEEK,
                LOAN_INSTALLMENT, MONDAY));
        product = TestObjectFactory.createLoanOffering("Loan Offering", "LOAN", ApplicableTo.GROUPS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, frequency);
        LoanOfferingBOIntegrationTest.setStatus(product, new PrdOfferingPersistence()
                .getPrdStatus(PrdStatus.LOAN_INACTIVE));
        TestObjectFactory.updateObject(product);
        StaticHibernateUtil.closeSession();
    }
}