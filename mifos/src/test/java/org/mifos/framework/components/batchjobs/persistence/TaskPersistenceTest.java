package org.mifos.framework.components.batchjobs.persistence;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Query;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.components.batchjobs.helpers.TaskStatus;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TaskPersistenceTest extends MifosIntegrationTest {

	public TaskPersistenceTest() throws SystemException, ApplicationException {
        super();
    }

    @Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	public void testHasLoanArrearsTaskRunSuccessfully() throws PersistenceException
	{
		Task task1 = new Task();
		task1.setCreatedBy((short) 1);
		task1.setCreatedDate(new Date(System.currentTimeMillis()));
		task1.setDescription(SchedulerConstants.FINISHED_SUCCESSFULLY);
		task1.setStartTime(new Timestamp(System.currentTimeMillis()));
		task1.setEndTime(new Timestamp(System.currentTimeMillis()));
		task1.setStatus(TaskStatus.COMPLETE.getValue());
		task1.setTask("LoanArrearsTask");
		
		TaskPersistence p = new TaskPersistence();
		p.saveAndCommitTask(task1);
		
		HibernateUtil.closeSession();
		p = new TaskPersistence();
		assertTrue(p.hasLoanArrearsTaskRunSuccessfully());
		Task task2 = new Task();
		task2.setCreatedBy((short) 1);
		task2.setCreatedDate(new Date(System.currentTimeMillis()));
		task2.setDescription(SchedulerConstants.FINISHED_SUCCESSFULLY);
		task2.setStartTime(new Timestamp(System.currentTimeMillis()));
		task2.setEndTime(new Timestamp(System.currentTimeMillis()));
		task2.setStatus(TaskStatus.INCOMPLETE.getValue());
		task2.setTask("LoanArrearsTask");
		new TaskPersistence().saveAndCommitTask(task2);
		
		HibernateUtil.closeSession();
		assertFalse(p.hasLoanArrearsTaskRunSuccessfully());
		TestObjectFactory.removeObject(task1);
		TestObjectFactory.removeObject(task2);
	}
	
	public void testSaveAndCommit() throws PersistenceException {
		Task task = new Task();
		task.setCreatedBy((short) 1);
		task.setCreatedDate(new Date(System.currentTimeMillis()));
		task.setDescription(SchedulerConstants.FINISHED_SUCCESSFULLY);
		task.setStartTime(new Timestamp(System.currentTimeMillis()));
		task.setEndTime(new Timestamp(System.currentTimeMillis()));
		task.setStatus(TaskStatus.COMPLETE.getValue());
		task.setTask("ProductStatus");
		new TaskPersistence().saveAndCommitTask(task);
		HibernateUtil.closeSession();
		Query query = HibernateUtil.getSessionTL().createQuery(
				"from " +
				Task.class.getName());
		List<Task> tasks = query.list();
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		for (Task task1 : tasks) {
			assertEquals(TaskStatus.COMPLETE.getValue().shortValue(), task1
					.getStatus());
			assertEquals("ProductStatus", task1.getTask());
			assertEquals(SchedulerConstants.FINISHED_SUCCESSFULLY, task1
					.getDescription());
			TestObjectFactory.removeObject(task1);
		}
	}

	public void testSaveAndCommitForInvalidConnection() {
		Task task = new Task();
		task.setId(1);
		task.setCreatedBy((short) 1);
		task.setCreatedDate(new Date(System.currentTimeMillis()));
		task.setDescription(SchedulerConstants.FINISHED_SUCCESSFULLY);
		task.setStartTime(new Timestamp(System.currentTimeMillis()));
		task.setEndTime(new Timestamp(System.currentTimeMillis()));
		task.setStatus(TaskStatus.COMPLETE.getValue());
		task.setTask("ProductStatus");
		try {
			new TaskPersistence().saveAndCommitTask(task);
			fail();
		} catch (PersistenceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();

		Query query = HibernateUtil.getSessionTL().createQuery(
				"from " +
				Task.class.getName());
		List<Task> tasks = query.list();
		assertNotNull(tasks);
		assertEquals(0, tasks.size());
	}
	
	

}
