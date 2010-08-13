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

}
