package org.mifos.application.accounts.savings.persistence;

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
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListDetailEntity;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.ProductTypeEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsPersistence extends MifosTestCase {

	private UserContext userContext;

	private SavingsPersistence savingsPersistence;

	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings;

	private SavingsBO savings1;

	private SavingsBO savings2;

	private SavingsOfferingBO savingsOffering;

	private SavingsOfferingBO savingsOffering1;

	private SavingsOfferingBO savingsOffering2;

	private AccountCheckListBO accountCheckList;
	
	

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		savingsPersistence = new SavingsPersistence();
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
		if (savings1 != null) {
			TestObjectFactory.cleanUp(savings1);
			savingsOffering1 = null;
		}
		if (savings2 != null) {
			TestObjectFactory.cleanUp(savings2);
			savingsOffering2 = null;
		}

		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(accountCheckList);
		if (savingsOffering1 != null)
			TestObjectFactory.removeObject(savingsOffering1);
		if (savingsOffering2 != null)
			TestObjectFactory.removeObject(savingsOffering2);
		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testGetSavingsProducts() throws Exception {
		createInitialObjects();
		savingsOffering1 = createSavingsOffering("SavingPrd1");
		savingsOffering2 = createSavingsOffering("SavingPrd2");
		List<PrdOfferingView> products = savingsPersistence.getSavingsProducts(
				null, group.getCustomerLevel(), new Short("2"));
		assertEquals(2, products.size());
		assertEquals("Offerng name for the first product do not match.",
				products.get(0).getPrdOfferingName(), "SavingPrd1");
		assertEquals("Offerng name for the second product do not match.",
				products.get(1).getPrdOfferingName(), "SavingPrd2");

	}

	public void testRetrieveCustomFieldsDefinition() throws Exception {
		List<CustomFieldDefinitionEntity> customFields = savingsPersistence
				.retrieveCustomFieldsDefinition(SavingsConstants.SAVINGS_CUSTOM_FIELD_ENTITY_TYPE);
		assertNotNull(customFields);
		assertEquals(TestConstants.SAVINGS_CUSTOMFIELDS_NUMBER, customFields
				.size());
	}

	public void testFindById() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("SavingPrd1");
		savings = createSavingsAccount("FFFF", savingsOffering);
		SavingsBO savings1 = savingsPersistence
				.findById(savings.getAccountId());
		assertEquals("FFFF", savings1.getGlobalAccountNum());
		assertEquals(savingsOffering.getRecommendedAmount()
				.getAmountDoubleValue(), savings1.getRecommendedAmount()
				.getAmountDoubleValue());
	}

	public void testGetAccountStatus() throws Exception {
		AccountStateEntity accountState = savingsPersistence
				.getAccountStatusObject(AccountStates.SAVINGS_ACC_CLOSED);
		assertNotNull(accountState);
		assertEquals(accountState.getId().shortValue(),
				AccountStates.SAVINGS_ACC_CLOSED);
	}

	public void testRetrieveAllAccountStateList() throws NumberFormatException,
			PersistenceException {
		List<AccountStateEntity> accountStateEntityList = savingsPersistence
				.retrieveAllAccountStateList(Short.valueOf("2"));
		assertNotNull(accountStateEntityList);
		assertEquals(6, accountStateEntityList.size());
	}

	public void testGetStatusChecklist() {
		createCheckList();
		List statusCheckList = savingsPersistence.getStatusChecklist(Short
				.valueOf("13"), Short.valueOf("2"));
		assertNotNull(statusCheckList);
		assertEquals(1, statusCheckList.size());
	}

	public void testFindBySystemId() throws Exception {
		createInitialObjects();
		savingsOffering = createSavingsOffering("SavingPrd1");
		savings = createSavingsAccount("kkk", savingsOffering);
		SavingsBO savings1 = savingsPersistence.findBySystemId("kkk");
		assertEquals("kkk", savings1.getGlobalAccountNum());
		assertEquals(savings.getAccountId(), savings1.getAccountId());
		assertEquals(savingsOffering.getRecommendedAmount()
				.getAmountDoubleValue(), savings1.getRecommendedAmount()
				.getAmountDoubleValue());
	}

	public void testRetrieveLastTransaction() throws Exception {
		try {
			SavingsTestHelper helper = new SavingsTestHelper();
			createInitialObjects();
			PersonnelBO createdBy = new PersonnelPersistence()
					.getPersonnel(userContext.getId());
			savingsOffering = helper.createSavingsOffering();
			savings = new SavingsBO(userContext);
			AccountPaymentEntity payment = helper
					.createAccountPaymentToPersist(savings,new Money(Configuration
							.getInstance().getSystemConfig().getCurrency(),
							"700.0"), new Money(Configuration.getInstance()
							.getSystemConfig().getCurrency(), "1700.0"), helper
							.getDate("15/01/2006"),
							AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,
							createdBy, group);
			savings.addAccountPayment(payment);
			savings.setSavingsOffering(savingsOffering);
			savings.setCustomer(group);
			savings.setAccountState(new AccountStateEntity(
					AccountStates.SAVINGS_ACC_APPROVED));
			savings
					.setRecommendedAmount(savingsOffering
							.getRecommendedAmount());
			savings.setOffice(group.getOffice());
			savings.save();
			HibernateUtil.getSessionTL().flush();
			HibernateUtil.closeSession();

			payment = helper.createAccountPaymentToPersist(savings,
					new Money(Configuration.getInstance().getSystemConfig()
							.getCurrency(), "1000.0"), new Money(Configuration
							.getInstance().getSystemConfig().getCurrency(),
							"2700.0"), helper.getDate("20/02/2006"),
					AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,
					createdBy, group);
			savings.addAccountPayment(payment);
			savings.update();
			HibernateUtil.getSessionTL().flush();
			HibernateUtil.closeSession();

			savings = savingsPersistence.findById(savings.getAccountId());
			savings.setUserContext(userContext);
			payment = helper.createAccountPaymentToPersist(savings,
					new Money(Configuration.getInstance().getSystemConfig()
							.getCurrency(), "500.0"), new Money(Configuration
							.getInstance().getSystemConfig().getCurrency(),
							"2200.0"), helper.getDate("10/03/2006"),
					AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings,
					createdBy, group);
			savings.addAccountPayment(payment);
			savings.update();
			HibernateUtil.getSessionTL().flush();
			HibernateUtil.closeSession();

			savings = savingsPersistence.findById(savings.getAccountId());
			savings.setUserContext(userContext);
			payment = helper.createAccountPaymentToPersist(savings,
					new Money(Configuration.getInstance().getSystemConfig()
							.getCurrency(), "1200.0"), new Money(Configuration
							.getInstance().getSystemConfig().getCurrency(),
							"3400.0"), helper.getDate("15/03/2006"),
					AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,
					createdBy, group);
			savings.addAccountPayment(payment);
			savings.update();
			HibernateUtil.getSessionTL().flush();
			HibernateUtil.closeSession();

			savings = savingsPersistence.findById(savings.getAccountId());
			savings.setUserContext(userContext);
			payment = helper.createAccountPaymentToPersist(savings,
					new Money(Configuration.getInstance().getSystemConfig()
							.getCurrency(), "2500.0"), new Money(Configuration
							.getInstance().getSystemConfig().getCurrency(),
							"900.0"), helper.getDate("25/03/2006"),
					AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings,
					createdBy, group);
			savings.addAccountPayment(payment);
			savings.update();
			HibernateUtil.getSessionTL().flush();
			HibernateUtil.closeSession();

			savings = savingsPersistence.findById(savings.getAccountId());
			savings.setUserContext(userContext);
			SavingsTrxnDetailEntity trxn = savingsPersistence
					.retrieveLastTransaction(savings.getAccountId(), helper
							.getDate("12/03/2006"));
			assertEquals(Double.valueOf("500"), trxn.getAmount()
					.getAmountDoubleValue());
			group = savings.getCustomer();
			center = group.getParentCustomer();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		UserContext userContext = new UserContext();
		userContext.setId(new Short("1"));
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
		List<SavingsAccountView> savingsAccounts = savingsPersistence
				.getSavingsAccountsForCustomer(center.getCustomerId());
		assertEquals(1, savingsAccounts.size());
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

	public void testGetAccountsPendingForIntCalc() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		createInitialObjects();
		savingsOffering = createSavingsOffering("prd1");
		savingsOffering1 = createSavingsOffering("prd2");
		savingsOffering2 = createSavingsOffering("prd3");
		savings = helper.createSavingsAccount("000100000000021",
				savingsOffering, group, AccountStates.SAVINGS_ACC_INACTIVE,
				userContext);
		savings1 = helper.createSavingsAccount("000100000000022",
				savingsOffering1, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savings2 = helper.createSavingsAccount("000100000000023",
				savingsOffering2, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		savings.setNextIntCalcDate(helper.getDate("01/07/2006"));
		savings1.setNextIntCalcDate(helper.getDate("01/07/2006"));
		savings2.setNextIntCalcDate(helper.getDate("01/08/2006"));
		savings.update();
		savings1.update();
		savings2.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		List<Integer> savingsList = savingsPersistence
				.retreiveAccountsPendingForIntCalc(helper.getDate("01/07/2006"));
		assertEquals(Integer.valueOf("1").intValue(), savingsList.size());
		assertEquals(savings.getAccountId(), savingsList.get(0));

		// retrieve objects to remove
		savings = savingsPersistence.findById(savings.getAccountId());
		savings1 = savingsPersistence.findById(savings1.getAccountId());
		savings2 = savingsPersistence.findById(savings2.getAccountId());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testGetAccountsPendingForIntPost() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		createInitialObjects();
		savingsOffering = createSavingsOffering("prd1");
		savingsOffering1 = createSavingsOffering("prd2");
		savingsOffering2 = createSavingsOffering("prd3");
		savings = helper.createSavingsAccount("000100000000021",
				savingsOffering, group, AccountStates.SAVINGS_ACC_INACTIVE,
				userContext);
		savings1 = helper.createSavingsAccount("000100000000022",
				savingsOffering1, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savings2 = helper.createSavingsAccount("000100000000023",
				savingsOffering2, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);

		savings.setNextIntPostDate(helper.getDate("31/07/2006"));
		savings1.setNextIntPostDate(helper.getDate("31/07/2006"));
		savings2.setNextIntPostDate(helper.getDate("31/08/2006"));
		savings.update();
		savings1.update();
		savings2.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		List<Integer> savingsList = savingsPersistence
				.retreiveAccountsPendingForIntPosting(helper
						.getDate("01/08/2006"));
		assertEquals(Integer.valueOf("1").intValue(), savingsList.size());

		assertEquals(savings.getAccountId(), savingsList.get(0));

		// retrieve objects to remove
		savings = savingsPersistence.findById(savings.getAccountId());
		savings1 = savingsPersistence.findById(savings1.getAccountId());
		savings2 = savingsPersistence.findById(savings2.getAccountId());
		group = savings.getCustomer();
		center = group.getParentCustomer();
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
		
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = savingsPersistence.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		HibernateUtil.getSessionTL().flush();
		Calendar currentDateCalendar = new GregorianCalendar();
		java.sql.Date currentDate = new java.sql.Date(currentDateCalendar.getTimeInMillis());
		
		assertEquals(savingsPersistence.getMissedDeposits(savings.getAccountId(), currentDate), 1);
	}
	
	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
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
		accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID.getValue());
		Calendar currentDateCalendar = new GregorianCalendar();
		java.sql.Date currentDate = new java.sql.Date(currentDateCalendar.getTimeInMillis());

		accountActionDateEntity.setPaymentDate(currentDate);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		savings = savingsPersistence.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		HibernateUtil.getSessionTL().flush();
		assertEquals(savingsPersistence.getMissedDepositsPaidAfterDueDate(savings.getAccountId()),1);
	}

}
