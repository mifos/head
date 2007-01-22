package org.mifos.framework.components.cronjob.helpers;

import java.sql.Date;
import java.util.List;

import org.hibernate.Query;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBOTest;
import org.mifos.application.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.SchedulerConstants;
import org.mifos.framework.components.cronjobs.business.Task;
import org.mifos.framework.components.cronjobs.exceptions.CronJobException;
import org.mifos.framework.components.cronjobs.helpers.ProductStatus;
import org.mifos.framework.components.cronjobs.helpers.ProductStatusHelper;
import org.mifos.framework.components.cronjobs.helpers.TaskStatus;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestProductStatusHelper extends MifosTestCase {

	LoanOfferingBO loanOffering;

	ProductStatusHelper productStatusHelper;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ProductStatus productStatus = new ProductStatus();
		productStatus.name = "ProductStatus";
		productStatusHelper = (ProductStatusHelper) productStatus
				.getTaskHelper();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(loanOffering);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testExecute() throws PersistenceException, CronJobException {
		createInactiveLoanOffering();

		productStatusHelper.execute(System.currentTimeMillis());

		loanOffering = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, loanOffering.getPrdOfferingId());
		assertEquals(PrdStatus.LOANACTIVE.getValue(), loanOffering
				.getPrdStatus().getOfferingStatusId());
	}

	public void testExecuteFailure() throws PersistenceException {
		createInactiveLoanOffering();

		TestObjectFactory.simulateInvalidConnection();
		try {
			productStatusHelper.execute(System.currentTimeMillis());
			fail();
		} catch (CronJobException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();

		loanOffering = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, loanOffering.getPrdOfferingId());
		assertEquals(PrdStatus.LOANINACTIVE.getValue(), loanOffering
				.getPrdStatus().getOfferingStatusId());
	}

	public void testExecuteTask() throws PersistenceException, CronJobException {
		createInactiveLoanOffering();

		productStatusHelper.executeTask();

		Query query = HibernateUtil.getSessionTL().createQuery(
				"from org.mifos.framework.components.cronjobs.business.Task");
		List<Task> tasks = query.list();
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		for (Task task : tasks) {
			assertEquals(TaskStatus.COMPLETE.getValue().shortValue(), task
					.getStatus());
			assertEquals("ProductStatus", task.getTask());
			assertEquals(SchedulerConstants.FINISHEDSUCCESSFULLY, task
					.getDescription());
			TestObjectFactory.removeObject(task);
		}

		loanOffering = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, loanOffering.getPrdOfferingId());
		assertEquals(PrdStatus.LOANACTIVE.getValue(), loanOffering
				.getPrdStatus().getOfferingStatusId());
	}

	public void testExecuteTaskFailure() throws PersistenceException {
		createInactiveLoanOffering();

		TestObjectFactory.simulateInvalidConnection();
		productStatusHelper.executeTask();
		HibernateUtil.closeSession();

		Query query = HibernateUtil.getSessionTL().createQuery(
				"from org.mifos.framework.components.cronjobs.business.Task");
		List<Task> tasks = query.list();
		assertEquals(0, tasks.size());

		loanOffering = (LoanOfferingBO) TestObjectFactory.getObject(
				LoanOfferingBO.class, loanOffering.getPrdOfferingId());
		assertEquals(PrdStatus.LOANINACTIVE.getValue(), loanOffering
				.getPrdStatus().getOfferingStatusId());
	}

	public void testRegisterStartup() throws CronJobException {
		productStatusHelper.registerStartup(System.currentTimeMillis());
		Query query = HibernateUtil.getSessionTL().createQuery(
				"from org.mifos.framework.components.cronjobs.business.Task");
		List<Task> tasks = query.list();
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		for (Task task : tasks) {
			assertEquals(TaskStatus.INCOMPLETE.getValue().shortValue(), task
					.getStatus());
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
		} catch (CronJobException e) {
			assertTrue(true);
		}
	}

	public void testRegisterCompletion() throws CronJobException {
		productStatusHelper.registerStartup(System.currentTimeMillis());
		productStatusHelper.registerCompletion(0,
				SchedulerConstants.FINISHEDSUCCESSFULLY, TaskStatus.COMPLETE);
		Query query = HibernateUtil.getSessionTL().createQuery(
				"from org.mifos.framework.components.cronjobs.business.Task");
		List<Task> tasks = query.list();
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		for (Task task : tasks) {
			assertEquals(TaskStatus.COMPLETE.getValue().shortValue(), task
					.getStatus());
			assertEquals("ProductStatus", task.getTask());
			assertEquals(SchedulerConstants.FINISHEDSUCCESSFULLY, task
					.getDescription());
			TestObjectFactory.removeObject(task);
		}
	}

	public void testRegisterCompletionFailure() throws CronJobException {
		productStatusHelper.registerStartup(System.currentTimeMillis());

		TestObjectFactory.simulateInvalidConnection();
		productStatusHelper.registerCompletion(0,
				SchedulerConstants.FINISHEDSUCCESSFULLY, TaskStatus.COMPLETE);
		HibernateUtil.closeSession();

		Query query = HibernateUtil.getSessionTL().createQuery(
				"from org.mifos.framework.components.cronjobs.business.Task");
		List<Task> tasks = query.list();
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		for (Task task : tasks) {
			assertEquals(TaskStatus.INCOMPLETE.getValue().shortValue(), task
					.getStatus());
			assertEquals("ProductStatus", task.getTask());
			assertEquals(SchedulerConstants.START, task.getDescription());
			TestObjectFactory.removeObject(task);
		}
	}

	private void createInactiveLoanOffering() throws PersistenceException {
		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingForToday(1, 1, 1, 2));
		loanOffering = TestObjectFactory.createLoanOffering("Loan Offering",
				"LOAN", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), frequency);
		LoanOfferingBOTest.setStatus(loanOffering,new PrdOfferingPersistence()
				.getPrdStatus(PrdStatus.LOANINACTIVE));
		TestObjectFactory.updateObject(loanOffering);
		HibernateUtil.closeSession();
	}
}