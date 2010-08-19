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

package org.mifos.framework.components.batchjobs;

import java.sql.Timestamp;
import java.util.Date;

import junit.framework.Assert;

import org.hibernate.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.components.batchjobs.helpers.SavingsIntCalcTask;
import org.mifos.framework.components.batchjobs.helpers.SavingsIntPostingTask;
import org.mifos.framework.components.batchjobs.helpers.TaskStatus;
import org.mifos.framework.components.batchjobs.persistence.TaskPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TaskHelperIntegrationTest extends MifosIntegrationTestCase {

    public TaskHelperIntegrationTest() throws Exception {
        super();
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        for (Object task : query.list()) {
            TestObjectFactory.removeObject((Task) task);
        }
    }

    @Test
    public void testIncompleteTaskHandling() throws PersistenceException {
        MifosScheduler mifosScheduler = new MifosScheduler();

        Task task1 = new Task();
        task1.setDescription(SchedulerConstants.FINISHED_SUCCESSFULLY);
        task1.setStartTime(new Timestamp(System.currentTimeMillis() - 86500000)); // over a day ago
        task1.setEndTime(new Timestamp(System.currentTimeMillis() - 86500000));
        task1.setStatus(TaskStatus.COMPLETE.getValue());
        task1.setTask(SavingsIntPostingTask.class.getSimpleName());
        TaskPersistence p = new TaskPersistence();
        p.saveAndCommitTask(task1);

        Task task2 = new Task();
        task2.setDescription(SchedulerConstants.FINISHED_SUCCESSFULLY);
        task2.setStartTime(new Timestamp(System.currentTimeMillis() - 175000000)); // over two days ago
        task2.setEndTime(new Timestamp(System.currentTimeMillis() - 175000000));
        task2.setStatus(TaskStatus.COMPLETE.getValue());
        task2.setTask(SavingsIntCalcTask.class.getSimpleName());
        p = new TaskPersistence();
        p.saveAndCommitTask(task2);

        StaticHibernateUtil.closeSession();

        SavingsIntPostingTask savingsIntPostingTask = new SavingsIntPostingTask();
        savingsIntPostingTask.name = SavingsIntPostingTask.class.getSimpleName();
        mifosScheduler.schedule(savingsIntPostingTask, new Date(System.currentTimeMillis() + 3600000), 86400000);

        SavingsIntCalcTask savingsIntCalcTask = new SavingsIntCalcTask();
        savingsIntCalcTask.name = SavingsIntCalcTask.class.getSimpleName();
        mifosScheduler.schedule(savingsIntCalcTask, new Date(System.currentTimeMillis() + 3600000), 86400000);

        mifosScheduler.runAllTasks();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        Assert.assertEquals(5, query.list().size());
    }

    @Test
    public void testIncompleteTaskDelay() throws PersistenceException {
        MifosScheduler mifosScheduler = new MifosScheduler();

        Task task1 = new Task();
        task1.setDescription(SchedulerConstants.FINISHED_SUCCESSFULLY);
        task1.setStartTime(new Timestamp(System.currentTimeMillis() - 350000000)); // over four days ago
        task1.setEndTime(new Timestamp(System.currentTimeMillis() - 350000000));
        task1.setStatus(TaskStatus.COMPLETE.getValue());
        task1.setTask(SavingsIntPostingTask.class.getSimpleName());
        TaskPersistence p = new TaskPersistence();
        p.saveAndCommitTask(task1);

        Task task2 = new Task();
        task2.setDescription(SchedulerConstants.FINISHED_SUCCESSFULLY);
        task2.setStartTime(new Timestamp(System.currentTimeMillis() - 350000000)); // over four days ago
        task2.setEndTime(new Timestamp(System.currentTimeMillis() - 350000000));
        task2.setStatus(TaskStatus.COMPLETE.getValue());
        task2.setTask(SavingsIntCalcTask.class.getSimpleName());
        p = new TaskPersistence();
        p.saveAndCommitTask(task2);

        StaticHibernateUtil.closeSession();

        SavingsIntPostingTask savingsIntPostingTask = new SavingsIntPostingTask();
        savingsIntPostingTask.name = SavingsIntPostingTask.class.getSimpleName();
        long delay = 86400000; // one day
        savingsIntPostingTask.delay = delay;
        mifosScheduler.schedule(savingsIntPostingTask, new Date(System.currentTimeMillis() + 3600000), delay);

        SavingsIntCalcTask savingsIntCalcTask = new SavingsIntCalcTask();
        savingsIntCalcTask.name = SavingsIntCalcTask.class.getSimpleName();
        delay = 172800000; // two days
        savingsIntCalcTask.delay = delay;
        mifosScheduler.schedule(savingsIntCalcTask, new Date(System.currentTimeMillis() + 3600000), delay);

        mifosScheduler.runAllTasks();

        Query query = StaticHibernateUtil.getSessionTL().createQuery("from " + Task.class.getName());
        Assert.assertEquals(8, query.list().size());
    }

}
