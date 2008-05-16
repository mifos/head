package org.mifos.framework.components.batchjobs.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_WEEK;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.customer.business.CustomerActivityEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.business.TestCustomerAccountBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.batchjobs.SchedulerConstants;
import org.mifos.framework.components.batchjobs.business.Task;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerFeeHelper extends MifosTestCase {

	private CustomerBO center;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() {
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();

	}

	public void testExecute() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("center1_Active_test", meeting);

		for (AccountActionDateEntity accountActionDateEntity : center
				.getCustomerAccount().getAccountActionDates()) {
			TestCustomerAccountBO.setActionDate(accountActionDateEntity,offSetDate(
					accountActionDateEntity.getActionDate(), 1));
		}

		meeting = center.getCustomerMeeting().getMeeting();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(offSetDate(new Date(System.currentTimeMillis()), 1));
		meeting.setMeetingStartDate(calendar);
		meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(
				new WeekDaysEntity(WeekDay.getWeekDay(Short.valueOf(String
						.valueOf(calendar.get(Calendar.DAY_OF_WEEK))))));

		Set<AccountFeesEntity> accountFeeSet = center.getCustomerAccount()
				.getAccountFees();
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee(
				"Training_Fee", FeeCategory.ALLCUSTOMERS, "100",
				RecurrenceType.WEEKLY, Short.valueOf("2"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center
				.getCustomerAccount(), trainingFee, ((AmountFeeBO) trainingFee)
				.getFeeAmount().getAmountDoubleValue());
		accountFeeSet.add(accountPeriodicFee);
		Date lastAppliedFeeDate = offSetDate(new Date(System
				.currentTimeMillis()), 1);
		assertEquals(2, accountFeeSet.size());
		for (Iterator iter = accountFeeSet.iterator(); iter.hasNext();) {
			AccountFeesEntity accountFeesEntity = (AccountFeesEntity) iter
					.next();
			accountFeesEntity.setLastAppliedDate(offSetDate(new Date(System
					.currentTimeMillis()), 1));
		}
		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class,
				center.getCustomerId());
		ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
		ApplyCustomerFeeHelper customerFeeHelper = (ApplyCustomerFeeHelper) applyCustomerFeeTask
				.getTaskHelper();
		customerFeeHelper.execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class,
				center.getCustomerId());

		Set<AccountFeesEntity> periodicFeeSet = center.getCustomerAccount()
				.getAccountFees();
		for (AccountFeesEntity periodicFees : periodicFeeSet) {
			if (periodicFees.getFees().getFeeName().equalsIgnoreCase(
					"Training_Fee"))
				assertEquals(lastAppliedFeeDate, DateUtils
						.getDateWithoutTimeStamp(periodicFees
								.getLastAppliedDate().getTime()));
			else {
				assertEquals(DateUtils.getDateWithoutTimeStamp(offSetDate(
						lastAppliedFeeDate, -7).getTime()), DateUtils
						.getDateWithoutTimeStamp(periodicFees
								.getLastAppliedDate().getTime()));
			}
		}
	}

	public void testExecuteToApplyPeriodicFee() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("center1_Active_test", meeting);
		for (AccountActionDateEntity accountActionDateEntity : center
				.getCustomerAccount().getAccountActionDates()) {
			TestCustomerAccountBO.setActionDate(accountActionDateEntity,offSetDate(
					accountActionDateEntity.getActionDate(), 1));
		}
		meeting = center.getCustomerMeeting().getMeeting();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(offSetDate(new Date(System.currentTimeMillis()), 1));
		meeting.setMeetingStartDate(calendar);
		meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(
				new WeekDaysEntity(WeekDay.getWeekDay(Short.valueOf(String
						.valueOf(calendar.get(Calendar.DAY_OF_WEEK))))));

		Set<AccountFeesEntity> accountFeeSet = center.getCustomerAccount()
				.getAccountFees();
		FeeBO trainingFee = TestObjectFactory.createPeriodicAmountFee(
				"Training_Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("1"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(center
				.getCustomerAccount(), trainingFee, ((AmountFeeBO) trainingFee)
				.getFeeAmount().getAmountDoubleValue());
		accountPeriodicFee.setLastAppliedDate(offSetDate(new Date(System
				.currentTimeMillis()), 1));
		accountFeeSet.add(accountPeriodicFee);

		assertEquals(2, accountFeeSet.size());
		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();

		center = TestObjectFactory.getObject(CenterBO.class,
				center.getCustomerId());
		ApplyCustomerFeeHelper customerFeeHelper = new ApplyCustomerFeeHelper(
				new ApplyCustomerFeeTask());
		customerFeeHelper.execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class,
				center.getCustomerId());
		Date lastAppliedFeeDate = null;
		for (AccountActionDateEntity accountActionDateEntity : center
				.getCustomerAccount().getAccountActionDates()) {
			CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
			if (customerScheduleEntity.getInstallmentId() == 2) {
				lastAppliedFeeDate = customerScheduleEntity.getActionDate();
				assertEquals(2, customerScheduleEntity.getAccountFeesActionDetails().size());
				for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : customerScheduleEntity
						.getAccountFeesActionDetails()) {
					if (accountFeesActionDetailEntity.getFee().getFeeName()
							.equalsIgnoreCase("Training_Fee")) {
						assertEquals(new Money("200.0"),
								accountFeesActionDetailEntity.getFeeAmount());
					}
				}
			}
		}
		for (CustomerActivityEntity customerActivityEntity : center
				.getCustomerAccount().getCustomerActivitDetails()) {
			assertEquals(new Money("200.0"), customerActivityEntity.getAmount());
		}
		Set<AccountFeesEntity> periodicFeeSet = center.getCustomerAccount()
				.getAccountFees();
		for (AccountFeesEntity periodicFees : periodicFeeSet) {
			if (periodicFees.getFees().getFeeName().equalsIgnoreCase(
					"Training_Fee"))
				assertEquals(lastAppliedFeeDate, DateUtils
						.getDateWithoutTimeStamp(periodicFees
								.getLastAppliedDate().getTime()));
		}
	}

	public void testExecuteTask() throws PersistenceException, BatchJobException {
		ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
		applyCustomerFeeTask.name = "ApplyCustomerFeeTask";
		ApplyCustomerFeeHelper applyCustomerFeeHelper = (ApplyCustomerFeeHelper) applyCustomerFeeTask
				.getTaskHelper();
		applyCustomerFeeHelper.executeTask();

		Query query = HibernateUtil.getSessionTL().createQuery(
				"from " +
				Task.class.getName());
		List<Task> tasks = query.list();
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		for (Task task : tasks) {
			assertEquals(TaskStatus.COMPLETE.getValue().shortValue(), task
					.getStatus());
			assertEquals("ApplyCustomerFeeTask", task.getTask());
			assertEquals(SchedulerConstants.FINISHED_SUCCESSFULLY, task
					.getDescription());
			TestObjectFactory.removeObject(task);
		}
	}

	public void testExecuteFailure() {
		ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
		applyCustomerFeeTask.name = "ApplyCustomerFeeTask";
		ApplyCustomerFeeHelper applyCustomerFeeHelper = new ApplyCustomerFeeHelper(
				applyCustomerFeeTask);
		TestObjectFactory.simulateInvalidConnection();
		applyCustomerFeeHelper.executeTask();
		HibernateUtil.closeSession();

		Query query = HibernateUtil.getSessionTL().createQuery(
				"from " +
				Task.class.getName());
		List<Task> tasks = query.list();
		assertEquals(0, tasks.size());

	}

	private java.sql.Date offSetDate(Date date, int noOfDays) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar = new GregorianCalendar(year, month, day - noOfDays);
		return new java.sql.Date(calendar.getTimeInMillis());
	}

}
