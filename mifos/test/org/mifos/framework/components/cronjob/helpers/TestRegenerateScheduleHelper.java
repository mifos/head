package org.mifos.framework.components.cronjob.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_MONTH;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_WEEK;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.TestCustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.helpers.RegenerateScheduleHelper;
import org.mifos.framework.components.cronjobs.helpers.RegenerateScheduleTask;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestRegenerateScheduleHelper extends MifosTestCase {

	private MeetingBO meeting;

	private CustomerBO center;

	private CustomerBO client;

	private CustomerBO group;

	private AccountBO accountBO;

	private SavingsOfferingBO savingsOffering;

	private LoanOfferingBO loanOfferingBO;

	private CustomerBO client1;

	private CustomerBO client2;

	private SavingsBO savings;

	private UserContext userContext;

	PersonnelBO createdBy = null;

	RegenerateScheduleHelper regenerateScheduleHelper;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		RegenerateScheduleTask regenerateScheduleTask = new RegenerateScheduleTask();
		regenerateScheduleHelper = (RegenerateScheduleHelper) regenerateScheduleTask
				.getTaskHelper();
		userContext = new UserContext();
		userContext.setId(new Short("1"));
		userContext.setLocaleId(new Short("1"));
		Set<Short> set = new HashSet<Short>();
		set.add(Short.valueOf("1"));
		userContext.setRoles(set);
		userContext.setLevelId(Short.valueOf("2"));
		userContext.setName("mifos");
		userContext.setPereferedLocale(new Locale("en", "US"));
		userContext.setBranchId(new Short("1"));
		userContext.setBranchGlobalNum("0001");
		createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		regenerateScheduleHelper = null;
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testExcuteWithCustomerAccounts() throws NumberFormatException,
			SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		CenterBO center1 = TestObjectFactory.createCenter(
				"Center_Active_test1", meeting);
		GroupBO group1 = TestObjectFactory.createGroupUnderCenter("Group1",
				CustomerStatus.GROUP_ACTIVE, center1);
		client = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_CLOSED, group);
		ClientBO client3 = TestObjectFactory.createClient("client3",
				CustomerStatus.CLIENT_CANCELLED, group1);
		center.getCustomerMeeting().getMeeting().getMeetingDetails()
				.setRecurAfter(Short.valueOf("2"));
		TestCustomerBO.setUpdatedFlag(center.getCustomerMeeting(),
				YesNoFlag.YES.getValue());

		List<java.util.Date> meetingDates = center.getCustomerMeeting()
				.getMeeting().getAllDates((short) 10);
		meetingDates.remove(0);
		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();

		regenerateScheduleHelper.execute(System.currentTimeMillis());
		HibernateUtil.closeSession();

		center = TestObjectFactory.getObject(CenterBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		center1 = TestObjectFactory.getObject(CenterBO.class,
				center1.getCustomerId());
		group1 = TestObjectFactory.getObject(GroupBO.class, group1
				.getCustomerId());
		client = TestObjectFactory.getObject(ClientBO.class,
				client.getCustomerId());
		client2 = TestObjectFactory.getObject(ClientBO.class,
				client2.getCustomerId());
		client3 = TestObjectFactory.getObject(ClientBO.class,
				client3.getCustomerId());

		for (AccountActionDateEntity actionDateEntity : center
				.getCustomerAccount().getAccountActionDates()) {
			if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
						.get(0).getTime()), DateUtils
						.getDateWithoutTimeStamp(actionDateEntity
								.getActionDate().getTime()));
			else if (actionDateEntity.getInstallmentId().equals(
					Short.valueOf("3")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
						.get(1).getTime()), DateUtils
						.getDateWithoutTimeStamp(actionDateEntity
								.getActionDate().getTime()));
		}

		for (AccountActionDateEntity actionDateEntity : group
				.getCustomerAccount().getAccountActionDates()) {
			if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
						.get(0).getTime()), DateUtils
						.getDateWithoutTimeStamp(actionDateEntity
								.getActionDate().getTime()));
			else if (actionDateEntity.getInstallmentId().equals(
					Short.valueOf("3")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
						.get(1).getTime()), DateUtils
						.getDateWithoutTimeStamp(actionDateEntity
								.getActionDate().getTime()));
		}
		assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting()
				.getUpdatedFlag());
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(group1);
		TestObjectFactory.cleanUp(center1);
	}

	public void testExecuteWithLoanAccount() throws Exception {
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				accountBO.getAccountId());

		center
				.getCustomerMeeting()
				.getMeeting()
				.getMeetingDetails()
				.getMeetingRecurrence()
				.setWeekDay(
						(WeekDaysEntity) new MasterPersistence()
								.retrieveMasterEntity(WeekDay.THURSDAY
										.getValue(), WeekDaysEntity.class, null));
		TestCustomerBO.setUpdatedFlag(center.getCustomerMeeting(),
				YesNoFlag.YES.getValue());

		AccountActionDateEntity accountActionDateEntity = center
				.getCustomerAccount().getDetailsOfNextInstallment();
		center.getCustomerMeeting().getMeeting().setMeetingStartDate(
				DateUtils.getCalendarDate(accountActionDateEntity
						.getActionDate().getTime()));

		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();
		regenerateScheduleHelper.execute(System.currentTimeMillis());

		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		List<java.util.Date> meetingDates = null;
		center.getCustomerMeeting().getMeeting().setMeetingStartDate(
				DateUtils.getCalendarDate(accountActionDateEntity
						.getActionDate().getTime()));
		meetingDates = center.getCustomerMeeting().getMeeting().getAllDates(
				(short) 10);
		Calendar calendar = new GregorianCalendar();
		int dayOfWeek = calendar.get(calendar.DAY_OF_WEEK);
		if (dayOfWeek == 5)
			meetingDates.remove(0);
		for (AccountBO account : center.getAccounts()) {
			for (AccountActionDateEntity actionDateEntity : account
					.getAccountActionDates()) {
				if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(0).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
				else if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(1).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
			}
		}
		for (AccountBO account : group.getAccounts()) {
			for (AccountActionDateEntity actionDateEntity : account
					.getAccountActionDates()) {
				if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(0).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
				else if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(1).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
			}
		}
		assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting()
				.getUpdatedFlag());

		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
	}

	public void testExecuteWithLoanAccountWithPastInstallmentsdPaid()
			throws Exception {
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CenterBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				accountBO.getAccountId());
		TestLoanBO.setDisbursementDate(accountBO, offSetCurrentDate(21));
		TestLoanBO.setPaymentStatus(accountBO.getAccountActionDate((short) 1),
				PaymentStatus.PAID.getValue());
		TestLoanBO.setPaymentStatus(accountBO.getAccountActionDate((short) 2),
				PaymentStatus.PAID.getValue());
		TestLoanBO.setPaymentStatus(accountBO.getAccountActionDate((short) 3),
				PaymentStatus.PAID.getValue());

		center
				.getCustomerMeeting()
				.getMeeting()
				.getMeetingDetails()
				.getMeetingRecurrence()
				.setWeekDay(
						(WeekDaysEntity) new MasterPersistence()
								.retrieveMasterEntity(WeekDay.THURSDAY
										.getValue(), WeekDaysEntity.class, null));
		TestCustomerBO.setUpdatedFlag(center.getCustomerMeeting(),
				YesNoFlag.YES.getValue());

		AccountActionDateEntity accountActionDateEntity = center
				.getCustomerAccount().getDetailsOfNextInstallment();
		center.getCustomerMeeting().getMeeting().setMeetingStartDate(
				DateUtils.getCalendarDate(accountActionDateEntity
						.getActionDate().getTime()));

		TestObjectFactory.updateObject(center);
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		regenerateScheduleHelper.execute(System.currentTimeMillis());

		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		List<java.util.Date> meetingDates = null;
		center.getCustomerMeeting().getMeeting().setMeetingStartDate(
				DateUtils.getCalendarDate(accountActionDateEntity
						.getActionDate().getTime()));
		meetingDates = center.getCustomerMeeting().getMeeting().getAllDates(
				(short) 10);
		Calendar calendar = new GregorianCalendar();
		int dayOfWeek = calendar.get(calendar.DAY_OF_WEEK);
		if (dayOfWeek == 5)
			meetingDates.remove(0);
		for (AccountBO account : center.getAccounts()) {
			for (AccountActionDateEntity actionDateEntity : account
					.getAccountActionDates()) {
				if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(0).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
				else if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(1).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
			}
		}
		for (AccountBO account : group.getAccounts()) {
			for (AccountActionDateEntity actionDateEntity : account
					.getAccountActionDates()) {
				if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(0).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
				else if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(1).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
			}
		}
		assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting()
				.getUpdatedFlag());

		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
	}

	public void testExecuteWithSavingsAccount() throws Exception {
		savings = getSavingAccount();
		TestObjectFactory.flushandCloseSession();
		savings = TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		client1 = TestObjectFactory.getObject(CustomerBO.class,
				client1.getCustomerId());
		client2 = TestObjectFactory.getObject(CustomerBO.class,
				client2.getCustomerId());

		center.getCustomerMeeting().getMeeting().getMeetingDetails()
				.setRecurAfter(Short.valueOf("1"));
		TestCustomerBO.setUpdatedFlag(center.getCustomerMeeting(),
				YesNoFlag.YES.getValue());

		Calendar meetingStartDate = center.getCustomerMeeting().getMeeting()
				.getMeetingStartDate();
		AccountActionDateEntity accountActionDateEntity = center
				.getCustomerAccount().getDetailsOfNextInstallment();
		center.getCustomerMeeting().getMeeting().setMeetingStartDate(
				DateUtils.getCalendarDate(accountActionDateEntity
						.getActionDate().getTime()));

		List<java.util.Date> meetingDates = center.getCustomerMeeting()
				.getMeeting().getAllDates(DateUtils.getLastDayOfNextYear());
		center.getCustomerMeeting().getMeeting().setMeetingStartDate(
				meetingStartDate);
		meetingDates.remove(0);

		TestObjectFactory.updateObject(center);
		TestObjectFactory.flushandCloseSession();
		regenerateScheduleHelper.execute(System.currentTimeMillis());
		TestObjectFactory.flushandCloseSession();

		savings = TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		client1 = TestObjectFactory.getObject(CustomerBO.class,
				client1.getCustomerId());
		client2 = TestObjectFactory.getObject(CustomerBO.class,
				client2.getCustomerId());

		for (AccountBO account : center.getAccounts()) {
			for (AccountActionDateEntity actionDateEntity : account
					.getAccountActionDates()) {
				if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(0).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
				else if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(1).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
			}
		}
		for (AccountBO account : group.getAccounts()) {
			for (AccountActionDateEntity actionDateEntity : account
					.getAccountActionDates()) {
				if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(0).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
				else if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(1).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
			}
		}
		for (AccountBO account : client1.getAccounts()) {
			for (AccountActionDateEntity actionDateEntity : account
					.getAccountActionDates()) {
				if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(0).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
				else if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(1).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
			}
		}

		for (AccountBO account : client2.getAccounts()) {
			for (AccountActionDateEntity actionDateEntity : account
					.getAccountActionDates()) {
				if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("2")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(0).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
				else if (actionDateEntity.getInstallmentId().equals(
						Short.valueOf("3")))
					assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
							.get(1).getTime()), DateUtils
							.getDateWithoutTimeStamp(actionDateEntity
									.getActionDate().getTime()));
			}
		}

		assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting()
				.getUpdatedFlag());

		TestObjectFactory.flushandCloseSession();

		savings = TestObjectFactory.getObject(SavingsBO.class,
				savings.getAccountId());
		center = TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		client1 = TestObjectFactory.getObject(CustomerBO.class,
				client1.getCustomerId());
		client2 = TestObjectFactory.getObject(CustomerBO.class,
				client2.getCustomerId());
	}

	private SavingsBO getSavingAccount() throws Exception {
		MeetingBO meeting = TestObjectFactory
			.getNewMeeting(MONTHLY, EVERY_SECOND_MONTH, CUSTOMER_MEETING, MONDAY);
		meeting.setMeetingStartDate(Calendar.getInstance());
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(
				new Short("1"));
		TestObjectFactory.createMeeting(meeting);
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group1",
				CustomerStatus.GROUP_ACTIVE, center);
		client1 = TestObjectFactory.createClient("client1",
				CustomerStatus.CLIENT_ACTIVE, group);
		client2 = TestObjectFactory.createClient("client2",
				CustomerStatus.CLIENT_ACTIVE, group);
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		savingsOffering = TestObjectFactory.createSavingsOffering("SavingPrd1",
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("1"), 300.0, Short.valueOf("1"), 24.0, 200.0,
				200.0, Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
		SavingsBO savings = new SavingsBO(userContext, savingsOffering, group,
				AccountState.SAVINGS_ACC_APPROVED, savingsOffering
						.getRecommendedAmount(), TestObjectFactory
						.getCustomFields());
		savings.save();
		HibernateUtil.getTransaction().commit();
		return savings;
	}

	private AccountBO getLoanAccount() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		loanOfferingBO = TestObjectFactory.createLoanOffering("Loan", Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
				.valueOf("1"), Short.valueOf("1"), Short
				.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOfferingBO);
	}

	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}
}
