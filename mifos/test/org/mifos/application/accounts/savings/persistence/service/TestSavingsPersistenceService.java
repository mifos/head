package org.mifos.application.accounts.savings.persistence.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListDetailEntity;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.TestConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsPersistenceService extends MifosTestCase {

	private UserContext userContext;

	private SavingsPersistenceService dbService;

	private CustomerBO client;
	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings;

	private SavingsOfferingBO savingsOffering;

	private SavingsOfferingBO savingsOffering1;

	private SavingsOfferingBO savingsOffering2;

	private SavingsOfferingBO savingsOffering3;

	private AccountCheckListBO accountCheckList;
	private MifosCurrency currency = Configuration.getInstance()
	.getSystemConfig().getCurrency();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dbService = (SavingsPersistenceService) ServiceFactory.getInstance()
				.getPersistenceService(PersistenceServiceName.Savings);
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

	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		//TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(accountCheckList);
		if (savingsOffering1 != null)
			TestObjectFactory.removeObject(savingsOffering1);
		if (savingsOffering2 != null)
			TestObjectFactory.removeObject(savingsOffering2);
		if (savingsOffering3 != null)
			TestObjectFactory.removeObject(savingsOffering3);

		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testGetSortedSavingsProducts() throws Exception {
		createInitialObjects();
		savingsOffering1 = createSavingsOffering("SavingPrd1");
		savingsOffering2 = createSavingsOffering("FixedDeposit");
		savingsOffering3 = createSavingsOffering("RecurringDeposit");
		List<PrdOfferingView> products = dbService.getSavingsProducts(null,
				group.getCustomerLevel(), new Short("2"));
		assertEquals(3, products.size());

		assertEquals("Offerng name for the first product do not match.",
				products.get(0).getPrdOfferingName(), "FixedDeposit");
		assertEquals("Offerng name for the second product do not match.",
				products.get(1).getPrdOfferingName(), "RecurringDeposit");
		assertEquals("Offerng name for the second product do not match.",
				products.get(2).getPrdOfferingName(), "SavingPrd1");
	}

	public void testRetrieveCustomFieldsDefinition() throws Exception {
		List<CustomFieldDefinitionEntity> customFields = dbService
				.retrieveCustomFieldsDefinition(SavingsConstants.SAVINGS_CUSTOM_FIELD_ENTITY_TYPE);
		assertNotNull(customFields);
		assertEquals(TestConstants.SAVINGS_CUSTOMFIELDS_NUMBER, customFields
				.size());
	}

	public void testFindById() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("SavingPrd1");
		savings = createSavingsAccount("FFFF", savingsOffering);
		SavingsBO savings1 = dbService.findById(savings.getAccountId());
		assertEquals("FFFF", savings1.getGlobalAccountNum());
	}

	public void testGetAccountStatus() throws Exception {
		AccountStateEntity accountState = dbService
				.getAccountStatusObject(AccountStates.SAVINGS_ACC_CLOSED);
		assertNotNull(accountState);
		assertEquals(accountState.getId().shortValue(),
				AccountStates.SAVINGS_ACC_CLOSED);
	}

	public void testGetStatusChecklist() {
		createCheckList();
		List statusCheckList = dbService.getStatusChecklist(
				Short.valueOf("13"), Short.valueOf("2"));
		assertNotNull(statusCheckList);
		assertEquals(1, statusCheckList.size());
	}

	public void testFindBySystemId() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("SavingPrd1");
		savings = createSavingsAccount("xxxx", savingsOffering);
		SavingsBO savings1 = dbService.findBySystemId("xxxx");
		assertEquals("xxxx", savings1.getGlobalAccountNum());
		assertEquals(savings.getAccountId(), savings1.getAccountId());
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));

	}

	private SavingsOfferingBO createSavingsOffering(String offeringName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName, Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0, 200.0,
				Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}

	private SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering) {
		userContext.setBranchGlobalNum("1001");
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				new Short("14"), new Date(), savingsOffering, userContext);
	}

	public void testGetSavingsAccountsForCustomer() {
		createInitialObjects();
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));

		Date startDate = new Date(System.currentTimeMillis());
		savingsOffering = TestObjectFactory.createSavingsOffering("SavingPrd1",
				Short.valueOf("2"), new Date(System.currentTimeMillis()), Short
						.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0,
				200.0, Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
		savings = TestObjectFactory.createSavingsAccount("432434", center,
				Short.valueOf("16"), startDate, savingsOffering);
		List<SavingsAccountView> savingsAccounts = dbService
				.getSavingsAccountsForCustomer(center.getCustomerId());
		assertEquals(1, savingsAccounts.size());
	}

	public void testRetrieveLastTransaction() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		createInitialObjects();
		PersonnelBO createdBy = new PersonnelPersistence()
				.getPersonnel(userContext.getId());
		savingsOffering = helper.createSavingsOffering();

		savings = new SavingsBO(userContext);
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(savings,
				new Money(Configuration.getInstance().getSystemConfig()
						.getCurrency(), "700.0"), new Money(Configuration
						.getInstance().getSystemConfig().getCurrency(),
						"1700.0"), helper.getDate("15/01/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.setSavingsOffering(savingsOffering);
		savings.setCustomer(group);
		savings.setAccountState(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_APPROVED));
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());
		savings.save();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = dbService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = helper.createAccountPaymentToPersist(savings,new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), "1000.0"),
				new Money(Configuration.getInstance().getSystemConfig()
						.getCurrency(), "2700.0"),
				helper.getDate("20/02/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = dbService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = helper.createAccountPaymentToPersist(savings,new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), "500.0"),
				new Money(Configuration.getInstance().getSystemConfig()
						.getCurrency(), "2200.0"),
				helper.getDate("10/03/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = dbService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		HibernateUtil.getSessionTL().flush();
		payment = helper.createAccountPaymentToPersist(savings,new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), "1200.0"),
				new Money(Configuration.getInstance().getSystemConfig()
						.getCurrency(), "3400.0"),
				helper.getDate("15/03/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = dbService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = helper.createAccountPaymentToPersist(savings,new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), "2500.0"),
				new Money(Configuration.getInstance().getSystemConfig()
						.getCurrency(), "900.0"), helper.getDate("25/03/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = dbService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		SavingsTrxnDetailEntity trxn = dbService.retrieveLastTransaction(
				savings.getAccountId(), helper.getDate("12/03/2006"));
		assertEquals(Double.valueOf("500"), trxn.getAmount()
				.getAmountDoubleValue());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	private void createCheckList() {
		Session session = HibernateUtil.getSessionTL();
		Transaction tx = null;
		tx = session.beginTransaction();

		accountCheckList = new AccountCheckListBO();
		accountCheckList.setChecklistName("productchecklist");
		accountCheckList.setChecklistStatus(Short.valueOf("1"));

		SupportedLocalesEntity supportedLocales = new SupportedLocalesEntity();
		supportedLocales.setLocaleId(Short.valueOf("1"));
		accountCheckList.setSupportedLocales(supportedLocales);

		CheckListDetailEntity checkListDetailEntity = new CheckListDetailEntity();
		checkListDetailEntity.setDetailText("item1");
		checkListDetailEntity.setAnswerType(Short.valueOf("1"));
		checkListDetailEntity.setSupportedLocales(supportedLocales);
		accountCheckList.addChecklistDetail(checkListDetailEntity);

		ProductTypeEntity productTypeEntity = (ProductTypeEntity) session.get(
				ProductTypeEntity.class, (short) 2);

		AccountStateEntity accountStateEntity = new AccountStateEntity(Short
				.valueOf("13"));

		accountCheckList.setAccountStateEntity(accountStateEntity);
		accountCheckList.setProductTypeEntity(productTypeEntity);

		accountCheckList.create();

		tx.commit();
		HibernateUtil.closeSession();

	}
	
	public void testGetMissedDeposits() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		savingsOffering = helper.createSavingsOffering("SavingPrd1", Short.valueOf("1"),Short.valueOf("1"));;
		savings = TestObjectFactory.createSavingsAccount("43245434", group,
				Short.valueOf("16"), new Date(System.currentTimeMillis()),
				savingsOffering);


		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(7));
		
		Calendar currentDateCalendar = new GregorianCalendar();
		java.sql.Date currentDate = new java.sql.Date(currentDateCalendar.getTimeInMillis());
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = dbService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		HibernateUtil.getSessionTL().flush();
		assertEquals(dbService.getMissedDeposits(savings.getAccountId(),currentDate), 1);
	}
	
	public void testGetMissedDepositsPaidAfterDueDate() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		savingsOffering = helper.createSavingsOffering("SavingPrd1", Short.valueOf("1"),Short.valueOf("1"));;
		savings = TestObjectFactory.createSavingsAccount("43245434", group,
				Short.valueOf("16"), new Date(System.currentTimeMillis()),
				savingsOffering);


		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(7));
		Calendar currentDateCalendar = new GregorianCalendar();
		java.sql.Date currentDate = new java.sql.Date(currentDateCalendar.getTimeInMillis());

		accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID.getValue());
		accountActionDateEntity.setPaymentDate(currentDate);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		savings = dbService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		HibernateUtil.getSessionTL().flush();
		assertEquals(dbService.getMissedDepositsPaidAfterDueDate(savings.getAccountId()), 1);
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

