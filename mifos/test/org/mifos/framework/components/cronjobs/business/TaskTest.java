package org.mifos.framework.components.cronjobs.business;

import org.mifos.framework.components.cronjobs.helpers.TaskStatus;

import junit.framework.TestCase;

public class TaskTest extends TestCase {
	
	public void testStatus() throws Exception {
		/* More or less a test of setters/getters, but there is some
		   enum magic going on */
		Task task = new Task();
		task.setStatus(TaskStatus.COMPLETE);
		assertSame(TaskStatus.COMPLETE, task.getStatusEnum());
		task.setStatus(TaskStatus.INCOMPLETE);
		assertSame(TaskStatus.INCOMPLETE, task.getStatusEnum());
	}

}
