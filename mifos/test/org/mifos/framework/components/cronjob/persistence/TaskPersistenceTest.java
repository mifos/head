package org.mifos.framework.components.cronjob.persistence;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Query;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.cronjobs.business.Task;
import org.mifos.framework.components.cronjobs.helpers.TaskStatus;
import org.mifos.framework.components.cronjobs.persistence.TaskPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TaskPersistenceTest extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
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
				"from org.mifos.framework.components.cronjobs.business.Task");
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
				"from org.mifos.framework.components.cronjobs.business.Task");
		List<Task> tasks = query.list();
		assertNotNull(tasks);
		assertEquals(0, tasks.size());
	}

}
