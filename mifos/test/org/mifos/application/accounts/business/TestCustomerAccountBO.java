package org.mifos.application.accounts.business;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerAccountBO extends MifosTestCase {

	private BulkEntryBusinessService bulkEntryBusinessService;

	protected CustomerAccountBO customerAccountBO = null;

	private CustomerBO center = null;

	private CustomerBO group = null;

	private CustomerBO client = null;

	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		bulkEntryBusinessService = (BulkEntryBusinessService) ServiceFactory
				.getInstance().getBusinessService(
						BusinessServiceName.BulkEntryService);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSuccessfulMakePayment() throws AccountException,
			SystemException {
		createCenter();
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		assertNotNull(customerAccount);
		Date transactionDate = new Date(System.currentTimeMillis());
		List<AccountActionDateEntity> dueActionDates = TestObjectFactory
				.getDueActionDatesForAccount(customerAccount.getAccountId(),
						transactionDate);
		assertEquals("The size of the due insallments is ", dueActionDates
				.size(), 1);
		PaymentData accountPaymentDataView = TestObjectFactory
				.getCustomerAccountPaymentDataView(dueActionDates, new Money(
						TestObjectFactory.getMFICurrency(), "100.0"), null,
						center.getPersonnel().getPersonnelId(), "3424324",
						Short.valueOf("1"), transactionDate, transactionDate);
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		customerAccount = center.getCustomerAccount();

		customerAccount.applyPayment(accountPaymentDataView);
		HibernateUtil.commitTransaction();
		assertEquals("The size of the payments done is", customerAccount
				.getAccountPayments().size(), 1);
		assertEquals(
				"The size of the due insallments after payment is",
				TestObjectFactory.getDueActionDatesForAccount(
						customerAccount.getAccountId(), transactionDate).size(),
				0);
		assertEquals(customerAccount.getCustomerActivitDetails().size(), 1);
	}

	public void testFailureMakePayment() throws AccountException,
			SystemException {
		createCenter();
		CustomerAccountBO customerAccount = center.getCustomerAccount();
		assertNotNull(customerAccount);
		Date transactionDate = new Date(System.currentTimeMillis());
		List<AccountActionDateEntity> dueActionDates = TestObjectFactory
				.getDueActionDatesForAccount(customerAccount.getAccountId(),
						transactionDate);
		assertEquals("The size of the due insallments is ", dueActionDates
				.size(), 1);
		PaymentData accountPaymentDataView = TestObjectFactory
				.getCustomerAccountPaymentDataView(dueActionDates, new Money(
						TestObjectFactory.getMFICurrency(), "100.0"), null,
						center.getPersonnel().getPersonnelId(), "3424324",
						Short.valueOf("1"), transactionDate, transactionDate);
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		customerAccount = center.getCustomerAccount();

		customerAccount.applyPayment(accountPaymentDataView);
		HibernateUtil.commitTransaction();
		assertEquals("The size of the payments done is", customerAccount
				.getAccountPayments().size(), 1);

		try {
			customerAccount.applyPayment(accountPaymentDataView);
			assertTrue("Payment is done even though they are no dues", false);
		} catch (AccountException ae) {
			assertTrue("Payment is not allowed when there are no dues", true);
		}
	}

	private void createCenter() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		HibernateUtil.closeSession();
	}

	public void testIsAdjustPossibleOnLastTrxn_NotActiveState()
			throws Exception {
		userContext = TestObjectFactory.getUserContext();
		createInitialObjects();
		client = TestObjectFactory.createClient("Client_Active_test", Short
				.valueOf("13"), "1.1.1", group, new Date(System
				.currentTimeMillis()));
		customerAccountBO = client.getCustomerAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		assertFalse("State is not active hence adjustment is not possible",
				customerAccountBO.isAdjustPossibleOnLastTrxn());

	}

	public void testIsAdjustPossibleOnLastTrxn_LastPaymentNull()
			throws Exception {
		userContext = TestObjectFactory.getUserContext();
		createInitialObjects();
		client = TestObjectFactory.createClient("Client_Active_test", Short
				.valueOf("3"), "1.1.1", group, new Date(System
				.currentTimeMillis()));
		customerAccountBO = client.getCustomerAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		assertFalse("Last payment was null hence adjustment is not possible",
				customerAccountBO.isAdjustPossibleOnLastTrxn());

	}

	public void testIsAdjustPossibleOnLastTrxn_LastPaymentWasAdjustment()
			throws Exception {
		userContext = TestObjectFactory.getUserContext();
		createInitialObjects();
		applyPayment();
		customerAccountBO = (CustomerAccountBO) TestObjectFactory.getObject(
				CustomerAccountBO.class, customerAccountBO.getAccountId());
		client = customerAccountBO.getCustomer();
		customerAccountBO.setUserContext(userContext);
		customerAccountBO.adjustPmnt("payment adjusted");
		TestObjectFactory.updateObject(customerAccountBO);
		assertFalse(
				"Last payment was adjustment hence adjustment is not possible",
				customerAccountBO.isAdjustPossibleOnLastTrxn());

	}

	public void testUpdateInstallmentAfterAdjustment() throws Exception {
		userContext = TestObjectFactory.getUserContext();
		createInitialObjects();
		applyPayment();
		customerAccountBO = (CustomerAccountBO) TestObjectFactory.getObject(
				CustomerAccountBO.class, customerAccountBO.getAccountId());
		client = customerAccountBO.getCustomer();
		customerAccountBO.setUserContext(userContext);
		List<AccountTrxnEntity> reversedTrxns = customerAccountBO.getLastPmnt()
				.reversalAdjustment("payment adjustment done");
		customerAccountBO.updateInstallmentAfterAdjustment(reversedTrxns);
		for (AccountTrxnEntity accntTrxn : reversedTrxns) {
			CustomerTrxnDetailEntity custTrxn = (CustomerTrxnDetailEntity) accntTrxn;
			AccountActionDateEntity accntActionDate = customerAccountBO
					.getAccountActionDate(custTrxn.getInstallmentId());
			assertEquals("Misc Fee Adjusted", accntActionDate.getMiscFeePaid()
					.getAmountDoubleValue(), 0.0);
			assertEquals("Misc Penalty Adjusted", accntActionDate
					.getMiscPenaltyPaid().getAmountDoubleValue(), 0.0);
		}
		for (CustomerActivityEntity customerActivityEntity : customerAccountBO
				.getCustomerActivitDetails()) {
			assertEquals("Amnt Adjusted", customerActivityEntity
					.getDescription());
		}

	}

	public void testAdjustPmnt() throws Exception {
		userContext = TestObjectFactory.getUserContext();
		createInitialObjects();
		applyPayment();
		customerAccountBO = (CustomerAccountBO) TestObjectFactory.getObject(
				CustomerAccountBO.class, customerAccountBO.getAccountId());
		client = customerAccountBO.getCustomer();
		customerAccountBO.setUserContext(userContext);
		customerAccountBO.adjustPmnt("payment adjusted");
		TestObjectFactory.updateObject(customerAccountBO);
		int countFinTrxns = 0;
		for (AccountTrxnEntity accountTrxnEntity : customerAccountBO
				.getLastPmnt().getAccountTrxns()) {
			countFinTrxns += accountTrxnEntity.getFinancialTransactions()
					.size();

		}

		assertEquals(countFinTrxns, 6);

		CustomerTrxnDetailEntity custTrxn = null;
		for (AccountTrxnEntity accTrxn : customerAccountBO.getLastPmnt()
				.getAccountTrxns()) {
			custTrxn = (CustomerTrxnDetailEntity) accTrxn;
			break;
		}
		AccountActionDateEntity installment = customerAccountBO
				.getAccountActionDate(custTrxn.getInstallmentId());
		assertEquals(
				"The installment adjusted should now be marked unpaid(due).",
				installment.getPaymentStatus(), AccountConstants.PAYMENT_UNPAID);

	}

	public void testWaiveChargeDue() throws Exception {
		createInitialObjects();
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		userContext = TestObjectFactory.getUserContext();
		CustomerAccountBO customerAccountBO = group.getCustomerAccount();
		customerAccountBO.setUserContext(userContext);
		customerAccountBO.waiveAmountDue(WaiveEnum.ALL);
		for (AccountActionDateEntity accountActionDateEntity : customerAccountBO
				.getAccountActionDates()) {
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDateEntity
					.getAccountFeesActionDetails()) {
				if (accountActionDateEntity.getInstallmentId().equals(
						Short.valueOf("1")))
					assertEquals(new Money(), accountFeesActionDetailEntity
							.getFeeAmount());
				else
					assertEquals(new Money("100"),
							accountFeesActionDetailEntity.getFeeAmount());
			}
		}
	}

	public void testWaiveChargeOverDue() throws Exception {
		createInitialObjects();
		CustomerAccountBO customerAccountBO = group.getCustomerAccount();
		changeFirstInstallmentDateToPreviousDate(customerAccountBO);
		TestObjectFactory.updateObject(customerAccountBO);
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		userContext = TestObjectFactory.getUserContext();
		customerAccountBO = group.getCustomerAccount();
		customerAccountBO.setUserContext(userContext);
		for (AccountActionDateEntity accountActionDateEntity : customerAccountBO
				.getAccountActionDates()) {
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDateEntity
					.getAccountFeesActionDetails()) {
				System.out.println("fees : "
						+ accountFeesActionDetailEntity.getFee().getFeeId());
				System.out.println("fees : "
						+ accountFeesActionDetailEntity.getFeeAmount());

			}
		}
		customerAccountBO.waiveAmountOverDue(WaiveEnum.ALL);
		for (AccountActionDateEntity accountActionDateEntity : customerAccountBO
				.getAccountActionDates()) {
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDateEntity
					.getAccountFeesActionDetails()) {
				if (accountActionDateEntity.getInstallmentId().equals(
						Short.valueOf("1")))
					assertEquals(new Money(), accountFeesActionDetailEntity
							.getFeeAmount());
				else
					assertEquals(new Money("100"),
							accountFeesActionDetailEntity.getFeeAmount());
			}
		}
	}

	private void changeFirstInstallmentDateToPreviousDate(
			CustomerAccountBO customerAccountBO) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - 1);
		for (AccountActionDateEntity accountActionDateEntity : customerAccountBO
				.getAccountActionDates()) {
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				accountActionDateEntity.setActionDate(new java.sql.Date(
						currentDateCalendar.getTimeInMillis()));
				break;
			}
		}
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("3"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
	}

	private void applyPayment() throws ServiceException, FinancialException {
		client = TestObjectFactory.createClient("Client_Active_test", Short
				.valueOf("3"), "1.1.1", group, new Date(System
				.currentTimeMillis()));
		customerAccountBO = client.getCustomerAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		customerAccountBO.setUserContext(userContext);

		AccountActionDateEntity accountAction = customerAccountBO
				.getAccountActionDate(Short.valueOf("1"));
		accountAction.setMiscFeePaid(TestObjectFactory
				.getMoneyForMFICurrency(100));
		accountAction.setMiscPenaltyPaid(TestObjectFactory
				.getMoneyForMFICurrency(100));
		accountAction.setPaymentDate(currentDate);
		accountAction.setPaymentStatus(AccountConstants.PAYMENT_PAID);

		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);

		AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity();
		accountPaymentEntity.setPaymentDetails(TestObjectFactory
				.getMoneyForMFICurrency(300), "1111", currentDate, Short
				.valueOf("1"));

		Money totalFees = new Money();
		CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity();
		accountTrxnEntity.setActionDate(currentDate);
		accountTrxnEntity.setDueDate(accountAction.getActionDate());
		accountTrxnEntity.setPersonnel(TestObjectFactory
				.getPersonnel(userContext.getId()));
		accountTrxnEntity
				.setAccountActionEntity((AccountActionEntity) masterPersistenceService
						.findById(AccountActionEntity.class,
								AccountConstants.ACTION_PAYMENT));
		accountTrxnEntity.setComments("payment done");
		accountTrxnEntity.setCustomer(client);
		accountTrxnEntity.setTrxnCreatedDate(new Timestamp(System
				.currentTimeMillis()));
		accountTrxnEntity.setInstallmentId(Short.valueOf("1"));
		accountTrxnEntity.setMiscFeeAmount(TestObjectFactory
				.getMoneyForMFICurrency(100));
		accountTrxnEntity.setMiscPenaltyAmount(TestObjectFactory
				.getMoneyForMFICurrency(100));
		accountTrxnEntity.setAmount(TestObjectFactory
				.getMoneyForMFICurrency(300));
		accountTrxnEntity.setTotalAmount(TestObjectFactory
				.getMoneyForMFICurrency(300));

		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction
				.getAccountFeesActionDetails()) {
			accountFeesActionDetailEntity.setFeeAmountPaid(TestObjectFactory
					.getMoneyForMFICurrency(100));
			FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity();
			feeTrxn.makePayment(accountFeesActionDetailEntity);
			accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
			totalFees = accountFeesActionDetailEntity.getFeeAmountPaid();
		}
		accountPaymentEntity.addAcountTrxn(accountTrxnEntity);

		customerAccountBO.addAccountPayment(accountPaymentEntity);

		TestObjectFactory.updateObject(customerAccountBO);
		TestObjectFactory.flushandCloseSession();
	}

	public void testApplyPeriodicFees() throws RepaymentScheduleException,
			SchedulerException, PersistenceException, ServiceException {
		createInitialObjects();
		FeesBO periodicFees = TestObjectFactory.createPeriodicFees(
				"Periodic Fee", 100.0, 1, 1, 5);
		AccountFeesEntity accountFeesEntity = new AccountFeesEntity();
		accountFeesEntity.setAccount(group.getCustomerAccount());
		accountFeesEntity.setAccountFeeAmount(periodicFees.getFeeAmount());
		accountFeesEntity.setFeeAmount(periodicFees.getFeeAmount());
		accountFeesEntity.setFees(periodicFees);
		accountFeesEntity.setLastAppliedDate(new Date(System
				.currentTimeMillis()));

		group.getCustomerAccount().addAccountFees(accountFeesEntity);

		TestObjectFactory.updateObject(group);
		TestObjectFactory.flushandCloseSession();

		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());

		AccountActionDateEntity accountActionDateEntity = (AccountActionDateEntity) group
				.getCustomerAccount().getAccountActionDates().toArray()[0];

		Set<AccountFeesActionDetailEntity> feeDetailsSet = accountActionDateEntity
				.getAccountFeesActionDetails();

		List<Integer> feeList = new ArrayList<Integer>();
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : feeDetailsSet) {
			feeList.add(accountFeesActionDetailEntity
					.getAccountFeesActionDetailId());
		}
		group.getCustomerAccount().applyPeriodicFees(
				new Date(System.currentTimeMillis()));
		TestObjectFactory.flushandCloseSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		AccountActionDateEntity firstInstallment = (AccountActionDateEntity) group
				.getCustomerAccount().getAccountActionDates().toArray()[0];
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : firstInstallment
				.getAccountFeesActionDetails()) {
			if (!feeList.contains(accountFeesActionDetailEntity
					.getAccountFeesActionDetailId())) {
				assertEquals("Periodic Fee", accountFeesActionDetailEntity
						.getFee().getFeeName());
				break;
			}
		}
		HibernateUtil.closeSession();
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
	}

	public void testRemoveFees() throws NumberFormatException, SystemException,
			ApplicationException {
		createInitialObjects();
		CustomerAccountBO customerAccountBO = group.getCustomerAccount();
		for (AccountFeesEntity accountFeesEntity : customerAccountBO
				.getAccountFees()) {
			FeesBO feesBO = accountFeesEntity.getFees();
			customerAccountBO.removeFees(feesBO.getFeeId(), Short.valueOf("1"));
		}
		TestObjectFactory.updateObject(customerAccountBO);
		group = (CustomerBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		customerAccountBO = group.getCustomerAccount();
		Set<CustomerActivityEntity> customerActivitySet = customerAccountBO
				.getCustomerActivitDetails();
		for (CustomerActivityEntity customerActivityEntity : customerActivitySet) {
			assertEquals(1, customerActivityEntity.getPersonnel()
					.getPersonnelId().intValue());
			assertEquals("Mainatnence Fee removed", customerActivityEntity
					.getDescription());
		}
		for (AccountFeesEntity accountFeesEntity : group.getCustomerAccount()
				.getAccountFees()) {
			assertEquals(2, accountFeesEntity.getFeeStatus().intValue());
		}
	}

	public void testUpdateAccountActivity() throws NumberFormatException,
			SystemException, ApplicationException {
		createInitialObjects();
		CustomerAccountBO customerAccountBO = group.getCustomerAccount();
		Short feeId = null;
		for (AccountFeesEntity accountFeesEntity : customerAccountBO
				.getAccountFees()) {
			FeesBO feesBO = accountFeesEntity.getFees();
			feeId = feesBO.getFeeId();
		}
		customerAccountBO.updateAccountActivity(new Money("222"), Short
				.valueOf("1"), "Mainatnence Fee removed");
		TestObjectFactory.updateObject(customerAccountBO);
		group = (CustomerBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		customerAccountBO = group.getCustomerAccount();
		Set<CustomerActivityEntity> customerActivitySet = customerAccountBO
				.getCustomerActivitDetails();
		for (CustomerActivityEntity customerActivityEntity : customerActivitySet) {
			assertEquals(1, customerActivityEntity.getPersonnel()
					.getPersonnelId().intValue());
			assertEquals("Mainatnence Fee removed", customerActivityEntity
					.getDescription());
			assertEquals("222.0", customerActivityEntity.getAmount().toString());
		}
	}
	
	public void testRegenerateFutureInstallments() throws SchedulerException, HibernateException, ServiceException{
		createCenter();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		AccountActionDateEntity accountActionDateEntity =center.getCustomerAccount().getDetailsOfNextInstallment();
		MeetingBO meeting = center.getCustomerMeeting().getMeeting();
		meeting.getMeetingDetails().setRecurAfter(Short.valueOf("2"));
		meeting.setMeetingStartDate(DateUtils.getCalendarDate(accountActionDateEntity.getActionDate().getTime()));
		SchedulerIntf scheduler = SchedulerHelper.getScheduler(meeting);
		List<java.util.Date> meetingDates = scheduler.getAllDates();
		meetingDates.remove(0);
		center.getCustomerAccount().regenerateFutureInstallments(meetingDates,(short)(accountActionDateEntity.getInstallmentId().intValue()+1));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		for(AccountActionDateEntity actionDateEntity : center.getCustomerAccount().getAccountActionDates()){
			if(actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()),DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()));
			else if(actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()),DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()));
		}
	}
	
	public void testRegenerateFutureInstallmentsWithAccountClosed() throws SchedulerException, HibernateException, ServiceException{
		createInitialObjects();
		AccountActionDateEntity accountActionDateEntity =center.getCustomerAccount().getDetailsOfNextInstallment();
		MeetingBO meeting = center.getCustomerMeeting().getMeeting();
		meeting.getMeetingDetails().setRecurAfter(Short.valueOf("2"));
		meeting.setMeetingStartDate(DateUtils.getCalendarDate(accountActionDateEntity.getActionDate().getTime()));
		SchedulerIntf scheduler = SchedulerHelper.getScheduler(meeting);
		List<java.util.Date> meetingDates = scheduler.getAllDates();
		meetingDates.remove(0);
		CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity();
		customerStatusEntity.setStatusId(GroupConstants.CLOSED);
		group.setCustomerStatus(customerStatusEntity);
		group.getCustomerAccount().regenerateFutureInstallments(meetingDates,(short)(accountActionDateEntity.getInstallmentId().intValue()+1));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		center=(CenterBO)TestObjectFactory.getObject(CenterBO.class,center.getCustomerId());
		for(AccountActionDateEntity actionDateEntity : center.getCustomerAccount().getAccountActionDates()){
			if(actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
				assertNotSame(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()),DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()));
			else if(actionDateEntity.getInstallmentId().equals(Short.valueOf("3")))
				assertNotSame(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()),DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()));
		}
	}
}
