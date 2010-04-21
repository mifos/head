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
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.util.List;

import junit.framework.Assert;

import org.hibernate.Query;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerFeeHelperIntegrationTest extends MifosIntegrationTestCase {

    public CustomerFeeHelperIntegrationTest() throws Exception {
        super();
    }

    private CustomerBO center;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    @SuppressWarnings("unchecked")
    public void testExecuteTask() throws Exception {
        ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
        applyCustomerFeeTask.name = "ApplyCustomerFeeTask";
        ApplyCustomerFeeHelper applyCustomerFeeHelper = (ApplyCustomerFeeHelper) applyCustomerFeeTask.getTaskHelper();
        applyCustomerFeeHelper.executeTask();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        for (Task task : tasks) {
            Assert.assertEquals(TaskStatus.COMPLETE.getValue().shortValue(), task.getStatus());
            Assert.assertEquals("ApplyCustomerFeeTask", task.getTask());
            Assert.assertEquals(SchedulerConstants.FINISHED_SUCCESSFULLY, task.getDescription());
            TestObjectFactory.removeObject(task);
        }
    }

    @SuppressWarnings("unchecked")
    public void testExecuteTaskAndForceException() throws Exception {
        ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
        applyCustomerFeeTask.name = "ApplyCustomerFeeTask";
        ApplyCustomerFeeHelper applyCustomerFeeHelper = (ApplyCustomerFeeHelper) applyCustomerFeeTask.getTaskHelper();
        AccountPersistence accountPersistenceMock = createMock(AccountPersistence.class);
        expect(accountPersistenceMock.getAccountsWithYesterdaysInstallment()).andThrow(
                new PersistenceException("mock exception"));
        replay(accountPersistenceMock);
        applyCustomerFeeHelper.setAccountPersistence(accountPersistenceMock);

        applyCustomerFeeHelper.executeTask();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        for (Task task : tasks) {
            Assert.assertEquals(TaskStatus.FAILED.getValue().shortValue(), task.getStatus());
            Assert.assertEquals("ApplyCustomerFeeTask", task.getTask());
            TestObjectFactory.removeObject(task);
        }
    }

    @SuppressWarnings("unchecked")
    public void testExecuteFailure() {
        ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
        applyCustomerFeeTask.name = "ApplyCustomerFeeTask";
        ApplyCustomerFeeHelper applyCustomerFeeHelper = new ApplyCustomerFeeHelper(applyCustomerFeeTask);
        TestObjectFactory.simulateInvalidConnection();
        applyCustomerFeeHelper.executeTask();
        StaticHibernateUtil.closeSession();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        List<Task> tasks = query.list();
        Assert.assertEquals(0, tasks.size());

    }
}