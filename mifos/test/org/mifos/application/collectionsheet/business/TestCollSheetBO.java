/**
 * 
 */
package org.mifos.application.collectionsheet.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.internal.ExpectedInvocation;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanFeeScheduleEntity;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.loan.business.TestLoanScheduleEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.collectionsheet.persistence.CollectionSheetPersistence;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

import static org.easymock.classextension.EasyMock.createMock;

public class TestCollSheetBO extends MifosTestCase {

	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	protected CustomerBO client = null;

	protected CollectionSheetBO collectionSheet = null;

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(collectionSheet);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		HibernateUtil.getSessionTL();
	}

	public void testAddCollectionSheetCustomer() {
		CollSheetCustBO collectionSheetCustomer = new CollSheetCustBO();
		collectionSheetCustomer.setCustId(Integer.valueOf("1"));

		CollectionSheetBO collSheet = new CollectionSheetBO();
		collSheet.addCollectionSheetCustomer(collectionSheetCustomer);

	}

	public void testGetCollectionSheetCustomerForCustomerId() {

		CollSheetCustBO collectionSheetCustomer = new CollSheetCustBO();
		collectionSheetCustomer.setCustId(Integer.valueOf("1"));

		CollectionSheetBO collSheet = new CollectionSheetBO();
		assertNull(collSheet.getCollectionSheetCustomerForCustomerId(Integer
				.valueOf("1")));
		collSheet.addCollectionSheetCustomer(collectionSheetCustomer);

		assertEquals(collSheet.getCollectionSheetCustomerForCustomerId(
				Integer.valueOf("1")).getCustId(), Integer.valueOf("1"));

	}

	public void testCollSheetForLoanDisbursal() throws Exception {

		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 1);
		LoanBO loan = (LoanBO) accountBO;
		collectionSheet = createCollectionSheet(getCurrentDate());
		generateCollectionSheetForDate(collectionSheet);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collectionSheet.update(Short.valueOf("2"));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		Money disbursedAmount = collectionSheet
				.getCollectionSheetCustomerForCustomerId(group.getCustomerId())
				.getLoanDetailsForAccntId(loan.getAccountId())
				.getAmntToBeDisbursed();
		assertEquals(300.00, disbursedAmount.getAmountDoubleValue());

	}

	public void testCollSheetForLoanDisbursalInterestDeducted()
	throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(AccountState.LOAN_APPROVED, startDate, 2);
		LoanBO loan = (LoanBO) accountBO;
		collectionSheet = createCollectionSheet(getCurrentDate());
		generateCollectionSheetForDate(collectionSheet);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collectionSheet.update(Short.valueOf("2"));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		CollSheetLnDetailsEntity collSheetLoanDetail = collectionSheet
				.getCollectionSheetCustomerForCustomerId(group.getCustomerId())
				.getLoanDetailsForAccntId(loan.getAccountId());
		Money disbursedAmount = collSheetLoanDetail.getAmntToBeDisbursed();
		assertEquals(300.00, disbursedAmount.getAmountDoubleValue());
		assertEquals(12.00, collSheetLoanDetail.getInterestDue()
				.getAmountDoubleValue());
		assertEquals(40.00, collSheetLoanDetail.getFeesDue()
				.getAmountDoubleValue());
	}

	public void testCollSheetForSavingsDeposit() throws Exception {
		accountBO = createSavingsAccount(SavingsType.MANDATORY);
		SavingsBO savings = (SavingsBO) accountBO;
		AccountActionDateEntity firstInstallmentActionDate = savings
				.getAccountActionDate((short) 3);
		collectionSheet = createCollectionSheet(firstInstallmentActionDate
				.getActionDate());
		generateCollectionSheetForDate(collectionSheet);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collectionSheet.update(Short.valueOf("2"));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = collectionSheet
				.getCollectionSheetCustomerForCustomerId(client.getCustomerId())
				.getSavingsDetailsForAccntId(savings.getAccountId());
		assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue()
				.getAmountDoubleValue());
		assertEquals(400.00, collSheetSavingsDetail.getAmntOverDue()
				.getAmountDoubleValue());
	}

	public void testCollSheetForSavingsDepositVoluntary() throws Exception {
		accountBO = createSavingsAccount(SavingsType.VOLUNTARY);
		SavingsBO savings = (SavingsBO) accountBO;
		AccountActionDateEntity firstInstallmentActionDate = savings
				.getAccountActionDate((short) 3);
		collectionSheet = createCollectionSheet(firstInstallmentActionDate
				.getActionDate());
		generateCollectionSheetForDate(collectionSheet);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collectionSheet.update(Short.valueOf("2"));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		CollSheetSavingsDetailsEntity collSheetSavingsDetail = collectionSheet
				.getCollectionSheetCustomerForCustomerId(client.getCustomerId())
				.getSavingsDetailsForAccntId(savings.getAccountId());
		assertEquals(200.00, collSheetSavingsDetail.getRecommendedAmntDue()
				.getAmountDoubleValue());
		assertEquals(0.00, collSheetSavingsDetail.getAmntOverDue()
				.getAmountDoubleValue());
	}

	public void testPouplateCustomers() {
		CollectionSheetBO collSheet = new CollectionSheetBO();
		List<AccountActionDateEntity> accountActionDates = 
			getCustomerAccntDetails();
		collSheet.pouplateCustAndCustAccntDetails(accountActionDates);
		assertEquals(collSheet.getCollectionSheetCustomers().size(), 3);
		for (AccountActionDateEntity entity : accountActionDates) {
			TestObjectFactory.cleanUp(entity.getCustomer());
		}
	}

	public void testPopulateLoanAccounts() throws Exception {
		CollectionSheetBO collSheet = new CollectionSheetBO();
		List<AccountActionDateEntity> accountActionDates = getLnAccntDetails();
		collSheet.pouplateCustAndCustAccntDetails(accountActionDates);

		collSheet.populateLoanAccounts(accountActionDates);

		Set<CollSheetCustBO> collSheetCustBOs = collSheet
				.getCollectionSheetCustomers();
		for (CollSheetCustBO collSheetCust : collSheetCustBOs) {
			assertNotNull(collSheetCust.getCollectionSheetLoanDetails());
		}

		doCleanUp(accountActionDates);
	}

	public void testUpdateCollectiveTotals() throws Exception {
		CollectionSheetBO collSheet = new CollectionSheetBO();
		List<AccountActionDateEntity> accountActionDates = getLnAccntDetails();
		collSheet.pouplateCustAndCustAccntDetails(accountActionDates);
		collSheet.populateLoanAccounts(accountActionDates);
		doCleanUp(accountActionDates);
	}

	public void testCreateSuccess() throws Exception {
		collectionSheet = createCollectionSheet();
		Session session = HibernateUtil.getSessionTL();
		CollectionSheetBO collectionSheetBO = (CollectionSheetBO) session.get(
				CollectionSheetBO.class, collectionSheet.getCollSheetID());
		assertNotNull(collectionSheetBO);
	}

	public void testCreateFailure() throws Exception {
		CollectionSheetBO collSheet = new CollectionSheetBO();

		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		try {
			collSheet.create();
			fail();
		}
		catch (PersistenceException expected) {
		}

		assertNull(collSheet.getCollSheetID());
	}

	public void testUpdate() throws Exception {
		accountBO = createSavingsAccount(SavingsType.VOLUNTARY);
		SavingsBO savings = (SavingsBO) accountBO;
		AccountActionDateEntity firstInstallmentActionDate = savings
				.getAccountActionDate((short) 3);
		collectionSheet = createCollectionSheet(firstInstallmentActionDate
				.getActionDate());
		generateCollectionSheetForDate(collectionSheet);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collectionSheet.update(Short.valueOf("2"));
		HibernateUtil.getTransaction().commit();
		HibernateUtil.closeSession();
		assertEquals(Short.valueOf("2"), collectionSheet.getStatusFlag());
	}

	private CollectionSheetBO createCollectionSheet() throws Exception {

		CollectionSheetBO collSheet = new CollectionSheetBO();

		collSheet.setCollectionSheetCustomers(null);
		collSheet.setCollSheetDate(getCurrentDate());
		collSheet.setCreatedDate(getCurrentDate());
		collSheet.setRunDate(getCurrentDate());
		collSheet.setStatusFlag(null);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collSheet.create();
		HibernateUtil.getTransaction().commit();
		return collSheet;

	}

	private CollectionSheetBO createCollectionSheet(Date runDate)
			throws Exception {

		CollectionSheetBO collSheet = new CollectionSheetBO();
		collSheet.setCollectionSheetCustomers(null);
		collSheet.setCollSheetDate(runDate);
		collSheet.setCreatedDate(getCurrentDate());
		collSheet.setRunDate(runDate);
		collSheet.setStatusFlag(null);
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		collSheet.create();
		HibernateUtil.getTransaction().commit();
		return collSheet;

	}

	private Date getCurrentDate() {
		return new Date(System.currentTimeMillis());
	}

	private List<AccountActionDateEntity> getAccountActionDates(
			AccountBO account, Short installmentId) {

		MeetingBO meeting = TestObjectFactory.getTypicalMeeting();
		TestObjectFactory.createMeeting(meeting);
		CustomerBO center = TestObjectFactory.createCenter("ashCenter", meeting);

		// TODO: Is CLIENT_PARTIAL right or should this be GROUP_PARTIAL?
		CustomerBO group = TestObjectFactory.createGroupUnderCenter("ashGrp", 
			CustomerStatus.CLIENT_PARTIAL, center);

		CustomerBO client = TestObjectFactory.createClient("ash", 
				CustomerStatus.CLIENT_PARTIAL, group);

		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();

		AccountActionDateEntity accntActionDateClient = new LoanScheduleEntity(
				account, client, installmentId, new Date(System
						.currentTimeMillis()), PaymentStatus.UNPAID,
				new Money(), new Money());

		AccountActionDateEntity accntActionDateGrp = new LoanScheduleEntity(
				account, group, installmentId, new Date(System
						.currentTimeMillis()), PaymentStatus.UNPAID,
				new Money(), new Money());

		AccountActionDateEntity accntActionDateCenter = new LoanScheduleEntity(
				account, center, installmentId, new Date(System
						.currentTimeMillis()), PaymentStatus.UNPAID,
				new Money(), new Money());

		accntActionDates.add(accntActionDateClient);
		accntActionDates.add(accntActionDateGrp);
		accntActionDates.add(accntActionDateCenter);

		return accntActionDates;
	}

	private List<CustomerBO> getCustomers() {

		MeetingBO meeting = TestObjectFactory.getTypicalMeeting();
		TestObjectFactory.createMeeting(meeting);
		CustomerBO center = TestObjectFactory.createCenter("ashCenter", meeting);

		CustomerBO group = TestObjectFactory.createGroupUnderCenter("ashGrp", CustomerStatus.GROUP_ACTIVE, center);

		CustomerBO client = TestObjectFactory.createClient("ash", 
				CustomerStatus.CLIENT_ACTIVE, group);

		List<CustomerBO> customers = new ArrayList<CustomerBO>();
		customers.add(client);
		customers.add(group);
		customers.add(center);
		return customers;
	}

	private List<AccountActionDateEntity> getCustomerAccntDetails() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center1", meeting);
		AccountBO accountBO = center.getCustomerAccount();

		List<AccountActionDateEntity> accntActionDates = getAccountActionDates(
				accountBO, (short) 1);
		for (AccountActionDateEntity accountActionDateEntity : accntActionDates) {
			LoanScheduleEntity accntActionDate = (LoanScheduleEntity) accountActionDateEntity;
			AccountFeesActionDetailEntity accntFeesActionDetailEntity = new LoanFeeScheduleEntity(
					accntActionDate,null,
					null, new Money("5"));

			TestLoanBO.setFeeAmountPaid(accntFeesActionDetailEntity,TestObjectFactory
					.getMoneyForMFICurrency(3));

			accntActionDate.addAccountFeesAction(accntFeesActionDetailEntity);
			TestLoanScheduleEntity.modifyData(accntActionDate,
					TestObjectFactory.getMoneyForMFICurrency(10),
					TestObjectFactory.getMoneyForMFICurrency(5),
					TestObjectFactory.getMoneyForMFICurrency(3),
					TestObjectFactory.getMoneyForMFICurrency(0),
					TestObjectFactory.getMoneyForMFICurrency(5),
					TestObjectFactory.getMoneyForMFICurrency(5),
					accntActionDate.getPrincipal(), accntActionDate
							.getPrincipalPaid(), accntActionDate.getInterest(),
					accntActionDate.getInterestPaid());
		
		}

		return accntActionDates;
	}

	private List<AccountActionDateEntity> getLnAccntDetails() {
		List<CustomerBO> customers = getCustomers();
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		int i=0;
		for (CustomerBO customer : customers) {
			LoanBO loan = createLoanAccount(customer,"loan"+i++,"LN"+i++);
			LoanScheduleEntity accntActionDate = new LoanScheduleEntity(loan,
					customer, (short) 2, new Date(System.currentTimeMillis()),
					PaymentStatus.UNPAID, new Money(), new Money());
			AccountFeesActionDetailEntity accntFeesActionDetailEntity = new LoanFeeScheduleEntity(
					accntActionDate,  null,
					null, new Money("5"));
			TestLoanBO.setFeeAmountPaid(accntFeesActionDetailEntity,TestObjectFactory
					.getMoneyForMFICurrency(3));

			accntActionDate.addAccountFeesAction(accntFeesActionDetailEntity);

			TestLoanScheduleEntity.modifyData(accntActionDate,
					TestObjectFactory.getMoneyForMFICurrency(10),
					TestObjectFactory.getMoneyForMFICurrency(5),
					TestObjectFactory.getMoneyForMFICurrency(3),
					TestObjectFactory.getMoneyForMFICurrency(0),
					TestObjectFactory.getMoneyForMFICurrency(5),
					TestObjectFactory.getMoneyForMFICurrency(4),
					TestObjectFactory.getMoneyForMFICurrency(10),
					TestObjectFactory.getMoneyForMFICurrency(5),
					TestObjectFactory.getMoneyForMFICurrency(2),
					TestObjectFactory.getMoneyForMFICurrency(1));
			accntActionDates.add(accntActionDate);
		}
		return accntActionDates;
	}

	private LoanBO createLoanAccount(CustomerBO customerBO,String name,String shortName) {
		Date currentTime = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				name, shortName, currentTime, 
				customerBO.getCustomerMeeting().getMeeting());
		return TestObjectFactory.createLoanAccount("42423142341", customerBO,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, currentTime,
				loanOffering);

	}

	private void doCleanUp(List<AccountActionDateEntity> accountActionDates) {
		for (AccountActionDateEntity entity : accountActionDates) {
			TestObjectFactory.cleanUp(entity.getAccount());
			TestObjectFactory.cleanUp(entity.getCustomer());
		}

	}

	private AccountBO getLoanAccount(AccountState state, Date startDate,
			int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, state, startDate, loanOffering,
				disbursalType);

	}

	/**
	 * It queries the database for valid customers which have the meeting date
	 * tomorrow and populates its corresponding fields with the data.
	 */
	private void generateCollectionSheetForDate(
			CollectionSheetBO collectionSheet) 
	throws SystemException, ApplicationException {
		List<AccountActionDateEntity> accountActionDates = new CollectionSheetPersistence()
				.getCustFromAccountActionsDate(collectionSheet
						.getCollSheetDate());
		MifosLogManager
				.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER)
				.debug(
						"After retrieving account action date objects for next meeting date. ");
		if (null != accountActionDates && accountActionDates.size() > 0) {
			collectionSheet.populateAccountActionDates(accountActionDates);

		}
		List<LoanBO> loanBOs = new CollectionSheetPersistence()
				.getLnAccntsWithDisbursalDate(collectionSheet
						.getCollSheetDate());
		MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER)
				.debug("After retrieving loan accounts due for disbursal");
		if (null != loanBOs && loanBOs.size() > 0) {
			collectionSheet.addLoanDetailsForDisbursal(loanBOs);
			MifosLogManager
					.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER)
					.debug(
							"After processing loan accounts which had disbursal due.");
		}

		if (null != collectionSheet.getCollectionSheetCustomers()
				&& collectionSheet.getCollectionSheetCustomers().size() > 0) {
			collectionSheet.updateCollectiveTotals();
			MifosLogManager
					.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER)
					.debug("After updating collective totals");
		}
	}

	private SavingsBO createSavingsAccount(SavingsType savingsType) 
	throws Exception {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getTypicalMeeting());
		SavingsOfferingBO savingsOffering = 
			TestObjectFactory.createSavingsProduct("SavingPrd1", 
				ApplicableTo.GROUPS, 
				startDate, PrdStatus.SAVINGS_ACTIVE, 
				300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 
				1.2, 200.0, 200.0, 
				savingsType, InterestCalcType.MINIMUM_BALANCE, 
				meetingIntCalc, meetingIntPost);
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient(
				"Client", CustomerStatus.CLIENT_ACTIVE, group);
		return TestObjectFactory.createSavingsAccount("43245434", client,
				(short) 16, startDate,
				savingsOffering);
	}
	
	public void testRetrieveCollectionSheetMeetingDateReturnsAllCollectionSheetsForSpecifiedMeeting() throws Exception {
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = session.beginTransaction();
		Calendar calendar = Calendar.getInstance();
		calendar.set(2010, Calendar.JANUARY, 0, 0, 0, 0);
		Date year2010 = new Date(calendar.getTimeInMillis());
		CollectionSheetBO collectionSheet = new CollectionSheetBO();
		collectionSheet.setCollSheetDate(year2010);
		collectionSheet.setRunDate(new Date(System.currentTimeMillis()));
		collectionSheet.create();
		HashMap queryParameters = new HashMap();
		queryParameters.put("MEETING_DATE", year2010);
		List matchingCollectionSheets = new CollectionSheetPersistence().executeNamedQuery(NamedQueryConstants.COLLECTION_SHEETS_FOR_MEETING_DATE,queryParameters);
		assertEquals(1, matchingCollectionSheets.size());
		assertEquals(collectionSheet, matchingCollectionSheets.get(0));
		transaction.rollback();
		session.close();
	}
}
