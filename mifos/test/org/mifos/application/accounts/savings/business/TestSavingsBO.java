package org.mifos.application.accounts.savings.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.savings.persistence.service.SavingsPersistenceService;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.valueobjects.InterestCalcType;
import org.mifos.application.master.util.valueobjects.SavingsType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SecurityException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.HibernateStartUp;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsBO extends TestCase {
	private UserContext userContext;

	private CustomerBO group;

	private CustomerBO center;

	private CustomerBO client1;

	private CustomerBO client2;

	private SavingsBO savings;

	private SavingsOfferingBO savingsOffering;

	private SavingsTestHelper helper = new SavingsTestHelper();

	private SavingsPersistenceService savingsService = new SavingsPersistenceService();
	
	private AccountPersistanceService accountPersistanceService = new AccountPersistanceService();

	private MifosCurrency currency = Configuration.getInstance()
			.getSystemConfig().getCurrency();
	PersonnelBO createdBy = null;
	
	public TestSavingsBO() {

	}

	public TestSavingsBO(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
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
		createdBy = new PersonnelPersistence().getPersonnel(userContext.getId());
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(savings);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(client2);
		HibernateUtil.closeSession();
		super.tearDown();
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

	private SavingsBO createSavingsWithAccountPayments() throws Exception {
		AccountPaymentEntity payment;
		SavingsBO savingsObj = new SavingsBO(userContext);
		
		payment = helper.createAccountPayment(new Money(currency, "2500.0"),
				new Date(), createdBy);
		payment.addAcountTrxn(helper.createAccountTrxn(null, new Money(
				currency, "2500"), new Money(currency, "7500"), helper
				.getDate("10/01/2006"), null, 1,
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savingsObj, createdBy,
				group));
		savingsObj.addAccountPayment(payment);

		payment = helper.createAccountPayment(new Money(currency, "3500.0"),
				new Date(), createdBy);
		payment.addAcountTrxn(helper.createAccountTrxn(null, new Money(
				currency, "3500"), new Money(currency, "4000"), helper
				.getDate("16/02/2006"), null, 2,
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savingsObj,
				createdBy, group));
		savingsObj.addAccountPayment(payment);

		payment = helper.createAccountPayment(new Money(currency, "1000.0"),
				new Date(), createdBy);
		payment.addAcountTrxn(helper.createAccountTrxn(null, new Money(
				currency, "1000"), new Money(currency, "5000"), helper.getDate(
				"05/03/2006"), null, 3,
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savingsObj, createdBy,
				group));
		savingsObj.addAccountPayment(payment);

		payment = helper.createAccountPayment(new Money(currency, "500.0"),
				new Date(), createdBy);
		payment.addAcountTrxn(helper.createAccountTrxn(null, new Money(
				currency, "500.0"), new Money(currency, "4200.0"), helper.getDate(
				"05/03/2006"), null, 5,
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savingsObj,
				createdBy, group));
		savingsObj.addAccountPayment(payment);
		
		payment = helper.createAccountPayment(new Money(currency, "500.0"),
				new Date(), createdBy);
		payment.addAcountTrxn(helper.createAccountTrxn(null, new Money(
				currency, "1200.0"), new Money(currency, "3800.0"), helper.getDate(
				"05/03/2006"), null, 4,
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savingsObj,
				createdBy, group));
		savingsObj.addAccountPayment(payment);
		
		payment = helper.createAccountPayment(new Money(currency, "200.0"),
				new Date(), createdBy);
		payment.addAcountTrxn(helper.createAccountTrxn(null, new Money(
				currency, "200"), new Money(currency,"4000"), helper
				.getDate("05/04/2006"), null, 6,
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savingsObj,
				createdBy, group));
		savingsObj.addAccountPayment(payment);
		return savingsObj;
	}

	public void testSuccessfulSave() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();

		savings = new SavingsBO(userContext);
		savings.setRecommendedAmount(new Money(currency, "500.0"));
		savings.setSavingsOffering(savingsOffering);
		savings.setAccountState(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION));
		savings.setCustomer(group);

		Set<AccountCustomFieldEntity> customFields = new HashSet<AccountCustomFieldEntity>();
		AccountCustomFieldEntity field = new AccountCustomFieldEntity();
		field.setFieldId(new Short("1"));
		field.setFieldValue("13");
		customFields.add(field);
		savings.setAccountCustomFieldSet(customFields);

		savings.save();
		HibernateUtil.getTransaction().commit();
		assertTrue(true);
		assertEquals(savings.getInterestRate(),savingsOffering.getInterestRate());
		assertEquals(savings.getMinAmntForInt().getAmountDoubleValue(),savingsOffering.getMinAmntForInt().getAmountDoubleValue());
		assertEquals(savings.getMinAmntForInt(),savingsOffering.getMinAmntForInt());
		assertEquals(savings.getTimePerForInstcalc().getMeetingDetails().getRecurAfter(),savingsOffering.getTimePerForInstcalc().getMeeting().getMeetingDetails().getRecurAfter());
		assertEquals(savings.getFreqOfPostIntcalc().getMeetingDetails().getRecurAfter(),savingsOffering.getFreqOfPostIntcalc().getMeeting().getMeetingDetails().getRecurAfter());
	}

	public void testSuccessfulSaveInApprovedState() throws Exception {
		center = helper.createCenter();
		group = TestObjectFactory.createGroup("Group1", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client1 = TestObjectFactory.createClient("client1",
				ClientConstants.STATUS_CLOSED, "1.1.1.1", group, new Date(
						System.currentTimeMillis()));
		client2 = TestObjectFactory.createClient("client2",
				ClientConstants.STATUS_ACTIVE, "1.1.1.2", group, new Date(
						System.currentTimeMillis()));
		savingsOffering = helper.createSavingsOffering();
		savings = new SavingsBO(userContext);
		savings.setSavingsOffering(savingsOffering);
		savings.setCustomer(group);
		savings.setAccountState(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_APPROVED));
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());

		Set<AccountCustomFieldEntity> customFields = new HashSet<AccountCustomFieldEntity>();
		AccountCustomFieldEntity field = new AccountCustomFieldEntity();
		field.setFieldId(new Short("1"));
		field.setFieldValue("13");
		customFields.add(field);
		savings.setAccountCustomFieldSet(customFields);

		savings.save();
		HibernateUtil.getTransaction().commit();
		assertTrue(true);
		assertEquals(TestObjectFactory.getAllMeetingDates(
				center.getCustomerMeeting().getMeeting()).size(), savings
				.getAccountActionDates().size());
		savings = new SavingsPersistenceService().findById(savings
				.getAccountId());
		assertEquals(savings.getInterestRate(),savingsOffering.getInterestRate());
		assertEquals(savings.getInterestCalcType(),savingsOffering.getInterestCalcType());
		assertEquals(savings.getMinAmntForInt().getAmountDoubleValue(),savingsOffering.getMinAmntForInt().getAmountDoubleValue());
		assertEquals(savings.getTimePerForInstcalc().getMeetingDetails().getRecurAfter(),savingsOffering.getTimePerForInstcalc().getMeeting().getMeetingDetails().getRecurAfter());
		assertEquals(savings.getFreqOfPostIntcalc().getMeetingDetails().getRecurAfter(),savingsOffering.getFreqOfPostIntcalc().getMeeting().getMeetingDetails().getRecurAfter());
	}

	public void testSuccessfulUpdate() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savings.setCustomer(group);

		Set<AccountCustomFieldEntity> customFields = new HashSet<AccountCustomFieldEntity>();
		AccountCustomFieldEntity field = new AccountCustomFieldEntity();
		field.setFieldId(new Short("1"));
		field.setFieldValue("13");
		customFields.add(field);
		savings.setAccountCustomFieldSet(customFields);

		savings.setRecommendedAmount(new Money(currency, "700.0"));
		savings.update();
		HibernateUtil.getTransaction().commit();
		assertTrue(true);
		assertEquals(new Double(700).doubleValue(), savings
				.getRecommendedAmount().getAmountDoubleValue());
	}

	public void testSuccessfulUpdateDepositSchedule() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savings.setRecommendedAmount(new Money(currency, "700.0"));
		savings.update();
		HibernateUtil.getTransaction().commit();
		assertTrue(true);
		assertEquals(new Double(700).doubleValue(), savings
				.getRecommendedAmount().getAmountDoubleValue());
	}

	public void testGetMinimumBalance() throws Exception {
		createInitialObjects();
		SavingsBO savingsObj = createSavingsWithAccountPayments();
		Money initialBal = new Money(currency, "5500");
		SavingsTrxnDetailEntity initialTrxn = helper.createAccountTrxn(null,initialBal, initialBal, helper.getDate("04/01/2006"),null,1, AccountConstants.ACTION_SAVINGS_DEPOSIT, savingsObj, createdBy , group);
		Money minBal = savingsObj.getMinimumBalance(helper
				.getDate("05/01/2006"), helper.getDate("10/05/2006"),
				initialTrxn);
		assertEquals(Double.valueOf("4000"),minBal.getAmountDoubleValue());
	}

	/*
	 * Following tranxs are used for average calculation Date Days Amount
	 * (01/01/2006 - 10/01/06) 05 5500*5 + (10/01/2006 - 16/02/06) 37 7500*37 +
	 * (16/02/2006 - 05/03/06) 17 4000*17 + (05/03/2006 - 05/04/06) 31 4200*31 +
	 * (05/04/2006 - 10/04/06) 05 4000*5 = 523200 Avg = 523200/95 = 5507.3
	 */
	public void testGetAverageBalance() throws Exception {
		createInitialObjects();
		SavingsBO savingsObj = createSavingsWithAccountPayments();
		Money initialBal = new Money(currency, "5500");
		SavingsTrxnDetailEntity initialTrxn = helper.createAccountTrxn(null,initialBal, initialBal, helper.getDate("04/01/2006"),null,1, AccountConstants.ACTION_SAVINGS_DEPOSIT, savingsObj, createdBy , group);
		Money avgBalance = savingsObj.getAverageBalance(helper
				.getDate("05/01/2006"), helper.getDate("10/04/2006"),
				initialTrxn);
		assertEquals(Double.valueOf("5507.4"), avgBalance
				.getAmountDoubleValue());
	}

	public void testCalculateInterestForClosureAvgBal() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		InterestCalcType intType = new InterestCalcType();
		intType.setInterestCalculationTypeID(Short.valueOf("2"));
		savingsOffering.setInterestCalcType(intType);

		savings = new SavingsBO(userContext);
		savings.setSavingsOffering(savingsOffering);
		savings.setCustomer(group);
		savings.setAccountState(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_APPROVED));
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());
		savings.save();
		HibernateUtil.getSessionTL().flush();
		savings.setActivationDate(helper.getDate("15/05/2006"));

		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				new Money(currency, "1000.0"), new Money(currency, "1000.0"),
				helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		savings.getSavingsOffering().setMinAmntForInt(new Money("0"));
		double intAmount = savings.calculateInterestForClosure(
				helper.getDate("30/05/2006")).getAmountDoubleValue();
		assertEquals(Double.valueOf("6.6"), intAmount);
	}

	public void testCalculateInterestForClosureWithMinValueForIntCalc()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		InterestCalcType intType = new InterestCalcType();
		intType.setInterestCalculationTypeID(Short.valueOf("2"));
		savingsOffering.setInterestCalcType(intType);
		savingsOffering.setMinAmntForInt(new Money("5000"));
		savings = new SavingsBO(userContext);
		savings.setSavingsOffering(savingsOffering);
		savings.setCustomer(group);
		savings.setAccountState(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_APPROVED));
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());
		savings.save();
		HibernateUtil.getSessionTL().flush();
		savings.setActivationDate(helper.getDate("15/05/2006"));

		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				new Money(currency, "1000.0"), new Money(currency, "1000.0"),
				helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();

		double intAmount = savings.calculateInterestForClosure(
				helper.getDate("30/05/2006")).getAmountDoubleValue();
		assertEquals(Double.valueOf("0"), intAmount);
	}

	public void testCalculateInterestForClosure() throws Exception {
	//using min balance for interest calculation
	//from 15/01/2006 to 10/03/2006 = 85 no of days
	//interest Rate 24% per anum
	//interest = minBal 1400 * 242/(365*100)*85 = 78.2
		
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = new SavingsBO(userContext);
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				new Money(currency, "700.0"), new Money(currency, "1700.0"),
				helper.getDate("15/01/2006"),
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
		savings.setActivationDate(helper.getDate("05/01/2006"));

		payment = helper.createAccountPaymentToPersist(new Money(currency,
				"1000.0"), new Money(currency, "2700.0"), helper
				.getDate("20/02/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();

		payment = helper.createAccountPaymentToPersist(new Money(currency,
				"500.0"), new Money(currency, "2200.0"), helper
				.getDate("10/03/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();

		payment = helper.createAccountPaymentToPersist(new Money(currency,
				"1200.0"), new Money(currency, "3400.0"), helper
				.getDate("15/03/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();

		payment = helper.createAccountPaymentToPersist(new Money(currency,
				"2000.0"), new Money(currency, "1400.0"), helper
				.getDate("25/03/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();

		double intAmount = savings.calculateInterestForClosure(
				helper.getDate("10/04/2006")).getAmountDoubleValue();
		assertEquals(Double.valueOf("78.2"), intAmount);
	}

	public void testIsMandatory() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		assertEquals(Boolean.valueOf(false).booleanValue(), savings
				.isMandatory());
	}

	public void testIsDepositScheduleBeRegenerated() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		assertEquals(Boolean.valueOf(false).booleanValue(), savings
				.isMandatory());
	}

	public void testGenerateAndUpdateDepositActionsForClient() throws Exception {

		center = helper.createCenter();
		group = TestObjectFactory.createGroup("Group1", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		savingsOffering = helper.createSavingsOffering();
		savings = new SavingsBO(userContext);
		savings.setSavingsOffering(savingsOffering);
		savings.setCustomer(group);
		savings.setAccountState(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_APPROVED));
		savings.setRecommendedAmount(savingsOffering.getRecommendedAmount());
		savings.save();
		HibernateUtil.closeSession();

		client1 = TestObjectFactory.createClient("client1",
				ClientConstants.STATUS_ACTIVE, "1.1.1.1", group, new Date(
						System.currentTimeMillis()));
		HibernateUtil.closeSession();
		savings = new SavingsPersistenceService().findById(savings
				.getAccountId());

		savings.setUserContext(userContext);
		savings.generateAndUpdateDepositActionsForClient(client1);
		group = savings.getCustomer();
		center = group.getParentCustomer();
		int actionDatesSize = TestObjectFactory.getAllMeetingDates(
				center.getCustomerMeeting().getMeeting()).size();
		assertEquals(actionDatesSize, savings.getAccountActionDates().size());
		HibernateUtil.closeSession();
		savings = new SavingsPersistenceService().findById(savings
				.getAccountId());
		client1 = new CustomerPersistenceService().getCustomer(client1
				.getCustomerId());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testSuccessfulWithdraw() throws AccountException,
			SystemException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client1 = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1.1", group, new Date(System.currentTimeMillis()));
		savingsOffering = helper.createSavingsOffering();
		savings = TestObjectFactory.createSavingsAccount("43245434", client1,
				Short.valueOf("16"), new Date(System.currentTimeMillis()),
				savingsOffering);

		HibernateUtil.getSessionTL().flush();
		savings = (SavingsBO) (new AccountPersistanceService()
				.getAccount(savings.getAccountId()));
		savings.setSavingsBalance(new Money(TestObjectFactory.getMFICurrency(),
				"100.0"));
		Money enteredAmount = new Money(currency, "100.0");
		PaymentData paymentData = new PaymentData(enteredAmount, Short
				.valueOf("1"), Short.valueOf("1"), new Date(System
				.currentTimeMillis()));
		paymentData.setCustomerId(client1.getCustomerId());
		paymentData.setRecieptDate(new Date(System.currentTimeMillis()));
		paymentData.setRecieptNum("34244");

		savings.withdraw(paymentData);
		assertEquals(0.0, savings.getSavingsBalance().getAmountDoubleValue());
		assertEquals(1, savings.getSavingsActivityDetails().size());
		savings.getAccountPayments().clear();
	}

	public void testSuccessfulApplyPayment() throws AccountException,
			SystemException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client1 = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1.1", group, new Date(System.currentTimeMillis()));
		savingsOffering = helper.createSavingsOffering();
		savings = TestObjectFactory.createSavingsAccount("43245434", client1,
				Short.valueOf("16"), new Date(System.currentTimeMillis()),
				savingsOffering);

		HibernateUtil.closeSession();
		savings = (SavingsBO) (new AccountPersistanceService()
				.getAccount(savings.getAccountId()));
		savings.setSavingsBalance(new Money());

		Money enteredAmount = new Money(currency, "100.0");
		PaymentData paymentData = new PaymentData(enteredAmount, Short
				.valueOf("1"), Short.valueOf("1"), new Date(System
				.currentTimeMillis()));
		paymentData.setCustomerId(client1.getCustomerId());
		paymentData.setRecieptDate(new Date(System.currentTimeMillis()));
		paymentData.setRecieptNum("34244");
		AccountActionDateEntity accountActionDate = savings
				.getAccountActionDate(Short.valueOf("1"));

		SavingsPaymentData savingsPaymentData = new SavingsPaymentData(
				accountActionDate);
		paymentData.addAccountPaymentData(savingsPaymentData);
		savings.applyPayment(paymentData);
		assertEquals(100.0, savings.getSavingsBalance().getAmountDoubleValue());
		assertEquals(1, savings.getSavingsActivityDetails().size());
		savings.getAccountPayments().clear();
		client1 = new CustomerPersistenceService().getCustomer(client1
				.getCustomerId());
	}

	public void testGetStatusName() throws ApplicationException,
			SystemException {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000X00000000013", savingsOffering, group,
				AccountStates.SAVINGS_ACC_CANCEL, userContext);
		savings.initializeSavingsStateMachine(userContext.getLocaleId());
		String name = savings.getStatusName(userContext.getLocaleId(), savings
				.getAccountState().getId());
		assertEquals("Cancelled", name);
	}

	public void testGetFlagName() throws NumberFormatException,
			ApplicationException, SystemException {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000X00000000013", savingsOffering,group,
				AccountStates.SAVINGS_ACC_CANCEL, userContext);
		savings.initializeSavingsStateMachine(userContext.getLocaleId());
		String name = savings.getFlagName(Short.valueOf("4"));
		assertEquals("Withdraw", name);
	}

	public void testRetrieveAccountStateEntityMasterObject()
			throws NumberFormatException, ApplicationException, SystemException {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000X00000000013", savingsOffering,group,
				AccountStates.SAVINGS_ACC_CANCEL, userContext);
		savings.initializeSavingsStateMachine(userContext.getLocaleId());
		AccountStateEntity accountStateEntity = savings
				.retrieveAccountStateEntityMasterObject(savings
						.getAccountState());
		assertEquals("Cancelled", accountStateEntity.getName(userContext
				.getLocaleId()));
	}

	public void testMaxWithdrawAmount() throws AccountException,
			SystemException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client1 = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1.1", group, new Date(System.currentTimeMillis()));
		savingsOffering = helper.createSavingsOffering();
		savings = TestObjectFactory.createSavingsAccount("43245434", client1,
				Short.valueOf("16"), new Date(System.currentTimeMillis()),
				savingsOffering);

		HibernateUtil.getSessionTL().flush();
		savings = (SavingsBO) (new AccountPersistanceService()
				.getAccount(savings.getAccountId()));
		savings.setSavingsBalance(new Money(TestObjectFactory.getMFICurrency(),
				"100.0"));
		Money enteredAmount = new Money(currency, "300.0");
		PaymentData paymentData = new PaymentData(enteredAmount, Short
				.valueOf("1"), Short.valueOf("1"), new Date(System
				.currentTimeMillis()));
		paymentData.setCustomerId(client1.getCustomerId());
		paymentData.setRecieptDate(new Date(System.currentTimeMillis()));
		paymentData.setRecieptNum("34244");
		try {
			savings.withdraw(paymentData);
			assertTrue(
					"No Exception is thrown. Even amount greater than max withdrawal allowed to be withdrawn",
					false);
		} catch (AccountException ae) {
			assertTrue(
					"Exception is thrown. Amount greater than max withdrawal not allowed to be withdrawn",
					true);
		}
		savings.getAccountPayments().clear();
	}

	public void testSuccessfulFlagSave() throws Exception {
		Session session = HibernateUtil.getSessionTL();
		Transaction trxn = session.beginTransaction();
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000X00000000013", savingsOffering,group,
				AccountStates.SAVINGS_ACC_CANCEL,userContext);
		savings.setUserContext(this.userContext);
		AccountStateEntity state = (AccountStateEntity) session.get(
				AccountStateEntity.class, (short) 15);
		for (AccountStateFlagEntity flag : state.getFlagSet())
			savings.addAccountFlag(flag);
		session.update(savings);
		trxn.commit();
		HibernateUtil.closeSession();
		session = HibernateUtil.getSessionTL();
		SavingsBO savingsNew = (SavingsBO) (session.get(SavingsBO.class,
				new Integer(savings.getAccountId())));
		assertEquals(savingsNew.getAccountFlags().size(), 3);
		session.evict(savingsNew);
		HibernateUtil.closeSession();
	}

	public void testChangeStatusPermissionToPendingApprovalSucess()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		SavingsStateMachine.getInstance().initialize((short) 1, (short) 1);
		savings.changeStatus(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_PENDINGAPPROVAL), helper
				.getAccountNotes(savings), null, userContext);
		assertEquals(AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, savings
				.getAccountState().getId().shortValue());

	}

	public void testChangeStatusPermissionToPendingApprovalFailure()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);

		try {
			SavingsStateMachine.getInstance().initialize((short) 1, (short) 1);
			Set<Short> set = new HashSet<Short>();
			set.add(Short.valueOf("2"));
			userContext.setRoles(set);

			savings.changeStatus(new AccountStateEntity(
					AccountStates.SAVINGS_ACC_PENDINGAPPROVAL), helper
					.getAccountNotes(savings), null, userContext);
			assertEquals(true, false);
		} catch (ApplicationException se) {
			if (se instanceof SecurityException)
				assertEquals(true, true);

			else
				assertEquals(true, false);

		}

	}

	public void testChangeStatusPermissionToCancelBlacklistedSucess()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, userContext);
		SavingsStateMachine.getInstance().initialize((short) 1, (short) 1);
		AccountStateFlagEntity stateFlag = null;
		// 6 is blacklisted
		Session session = HibernateUtil.getSessionTL();
		stateFlag = (AccountStateFlagEntity) session.get(
				AccountStateFlagEntity.class, Short.valueOf("6"));
		savings.changeStatus(new AccountStateEntity(
				AccountStates.SAVINGS_ACC_CANCEL), helper
				.getAccountNotes(savings), stateFlag, userContext);
		assertEquals(AccountStates.SAVINGS_ACC_CANCEL, savings
				.getAccountState().getId().shortValue());

	}

	public void testChangeStatusPermissionToCancelBlacklistedFailure()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PENDINGAPPROVAL, userContext);

		try {
			SavingsStateMachine.getInstance().initialize((short) 1, (short) 1);
			Set<Short> set = new HashSet<Short>();
			set.add(Short.valueOf("2"));
			userContext.setRoles(set);
			AccountStateFlagEntity stateFlag = null;
			// 6 is blacklisted
			Session session = HibernateUtil.getSessionTL();
			stateFlag = (AccountStateFlagEntity) session.get(
					AccountStateFlagEntity.class, Short.valueOf("6"));

			savings.changeStatus(new AccountStateEntity(
					AccountStates.SAVINGS_ACC_CANCEL), helper
					.getAccountNotes(savings), stateFlag, userContext);
			assertEquals(true, false);
		} catch (ApplicationException se) {
			if (se instanceof SecurityException)
				assertEquals(true, true);

			else
				assertEquals(true, false);

		}
	}

	public void testIsAdjustPossibleOnLastTrxn_OnPartialAccount()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		Money amountAdjustedTo = new Money(currency, "500.0");
		boolean isAdjustPossible = savings
				.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
		assertEquals(savings.getAccountState().getId().shortValue(),
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
		assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
	}

	public void testIsAdjustPossibleOnLastTrxn_NoLastPayment() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		Money amountAdjustedTo = new Money(currency, "500.0");
		boolean isAdjustPossible = savings
				.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
		assertEquals(savings.getAccountState().getId().shortValue(),
				AccountStates.SAVINGS_ACC_APPROVED);
		assertNull(savings.getLastPmnt());
		assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
	}

	public void testIsAdjustPossibleOnLastTrxn_LastPaymentIsInterestPosting()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				new Money(currency, "1000.0"), new Money(currency, "1000.0"),
				helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_INTEREST_POSTING, savings,
				createdBy, group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		Money amountAdjustedTo = new Money(currency, "500.0");
		boolean isAdjustPossible = savings
				.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
		assertEquals(savings.getAccountState().getId().shortValue(),
				AccountStates.SAVINGS_ACC_APPROVED);
		assertNotNull(savings.getLastPmnt());
		assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
	}

	public void testIsAdjustPossibleOnLastTrxn_AmountSame() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				new Money(currency, "1000.0"), new Money(currency, "1000.0"),
				helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		Money amountAdjustedTo = new Money(currency, "1000.0");
		boolean isAdjustPossible = savings
				.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
		assertEquals(savings.getAccountState().getId().shortValue(),
				AccountStates.SAVINGS_ACC_APPROVED);
		assertNotNull(savings.getLastPmnt());
		assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
	}

	public void testIsAdjustPossibleOnLastTrxn_MaxWithdrawalAmntIsLess()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				new Money(currency, "1000.0"), new Money(currency, "1000.0"),
				helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		savingsOffering.setMaxAmntWithdrawl(new Money("1200"));
		Money amountAdjustedTo = new Money(currency, "1500.0");
		boolean isAdjustPossible = savings
				.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
		assertEquals(savings.getAccountState().getId().shortValue(),
				AccountStates.SAVINGS_ACC_APPROVED);
		assertNotNull(savings.getLastPmnt());
		assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
	}

	public void testIsAdjustPossibleOnLastTrxn_Balance_Negative()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				new Money(currency, "1000.0"), new Money(currency, "1000.0"),
				helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(new Money(currency, "400.0"));
		savings.update();

		HibernateUtil.getSessionTL().flush();
		savingsOffering.setMaxAmntWithdrawl(new Money("2500"));
		Money amountAdjustedTo = new Money(currency, "2000.0");
		boolean isAdjustPossible = savings
				.isAdjustPossibleOnLastTrxn(amountAdjustedTo);
		assertEquals(savings.getAccountState().getId().shortValue(),
				AccountStates.SAVINGS_ACC_APPROVED);
		assertNotNull(savings.getLastPmnt());
		assertEquals(savings.getSavingsBalance(), new Money(currency, "400.0"));
		assertEquals(Boolean.FALSE.booleanValue(), isAdjustPossible);
	}

	/*
	 * When IsAdjustPossibleOnLastTrxn returns false.
	 */
	public void testAdjustPmntFailure() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				new Money(currency, "1000.0"), new Money(currency, "1000.0"),
				helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(new Money(currency, "400.0"));
		savings.update();
		HibernateUtil.getSessionTL().flush();
		savingsOffering.setMaxAmntWithdrawl(new Money("2500"));
		Money amountAdjustedTo = new Money(currency, "2000.0");
		try {
			savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
		} catch (ApplicationException ae) {
			assertTrue(true);
			assertEquals(
					"exception.accounts.ApplicationException.CannotAdjust", ae
							.getKey());
		}

		assertEquals(savings.getAccountState().getId().shortValue(),
				AccountStates.SAVINGS_ACC_APPROVED);
		assertNotNull(savings.getLastPmnt());
		assertEquals(savings.getSavingsBalance(), new Money(currency, "400.0"));
	}

	public void testAdjustPmnt_LastPaymentNullified() throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				new Money(currency, "1000.0"), new Money(currency, "1000.0"),
				helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(new Money(currency, "400.0"));
		savings.update();
		savingsOffering.setMaxAmntWithdrawl(new Money("2500"));
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		savings.getSavingsOffering().setMaxAmntWithdrawl(new Money("2500"));
		savings.setUserContext(userContext);
		Money amountAdjustedTo = new Money();
		assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns()
				.size());
		try {
			savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
		} catch (ApplicationException ae) {
			assertTrue(false);
		}
		payment = savings.getLastPmnt();
		assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns()
				.size());
		assertEquals(savings.getSavingsBalance(), new Money(currency, "1400.0"));
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testAdjustPmnt_LastPaymentDecreasedForWithdrawal()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savingsOffering.setMaxAmntWithdrawl(new Money("2500"));
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		Money withdrawalAmount = new Money(currency, "1000.0");
		Money balanceAmount = new Money(currency, "4500.0");
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				withdrawalAmount, balanceAmount, helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(balanceAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);

		assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns()
				.size());
		Money amountAdjustedTo = new Money(currency, "500.0");

		payment = savings.getLastPmnt();

		savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());

		assertEquals(Integer.valueOf(2).intValue(), savings
				.getAccountPayments().size());
		assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns()
				.size());
		assertEquals(new Money(), payment.getAmount());
		assertEquals(savings.getSavingsBalance(), new Money(currency, "5000.0"));
		assertEquals(amountAdjustedTo, savings.getLastPmnt().getAmount());

		Hibernate.initialize(savings.getAccountActionDates());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testAdjustPmnt_LastPaymentIncreasedForWithdrawal()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savingsOffering.setMaxAmntWithdrawl(new Money("2500"));
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		Money withdrawalAmount = new Money(currency, "1000.0");
		Money balanceAmount = new Money(currency, "4500.0");
		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				withdrawalAmount, balanceAmount, helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(balanceAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);

		assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns()
				.size());
		Money amountAdjustedTo = new Money(currency, "1900.0");

		payment = savings.getLastPmnt();

		savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());

		assertEquals(Integer.valueOf(2).intValue(), savings
				.getAccountPayments().size());
		assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns()
				.size());
		assertEquals(new Money(), payment.getAmount());
		assertEquals(new Money(currency, "3600.0"), savings.getSavingsBalance());
		assertEquals(amountAdjustedTo, savings.getLastPmnt().getAmount());

		Hibernate.initialize(savings.getAccountActionDates());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testAdjustPmnt_LastPaymentDepositVol_without_schedule()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savingsOffering.setMaxAmntWithdrawl(new Money("2500"));
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		Money depositAmount = new Money(currency, "1000.0");
		Money balanceAmount = new Money(currency, "4500.0");

		AccountPaymentEntity payment = helper.createAccountPaymentToPersist(
				depositAmount, balanceAmount, helper.getDate("20/05/2006"),
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);
		savings.addAccountPayment(payment);
		savings.setSavingsBalance(balanceAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);

		assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns()
				.size());
		Money amountAdjustedTo = new Money(currency, "2000.0");

		payment = savings.getLastPmnt();

		savings.adjustLastUserAction(amountAdjustedTo, "correction entry");
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		assertEquals(Integer.valueOf(2).intValue(), savings
				.getAccountPayments().size());
		assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns()
				.size());
		assertEquals(new Money(), payment.getAmount());
		assertEquals(new Money(currency, "5500.0"), savings.getSavingsBalance());
		assertEquals(amountAdjustedTo, savings.getLastPmnt().getAmount());

		Hibernate.initialize(savings.getAccountActionDates());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testAdjustPmnt_LastPaymentDepositMandatory_PaidAllDue()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		Money recommendedAmnt = new Money(currency, "500.0");
		Date paymentDate = helper.getDate("09/05/2006");
		AccountActionDateEntity actionDate1 = helper.createAccountActionDate(
				Short.valueOf("1"), helper.getDate("01/05/2006"), paymentDate,
				savings.getCustomer(), recommendedAmnt, recommendedAmnt,
				AccountConstants.PAYMENT_PAID);
		AccountActionDateEntity actionDate2 = helper.createAccountActionDate(
				Short.valueOf("2"), helper.getDate("08/05/2006"), paymentDate,
				savings.getCustomer(), recommendedAmnt, recommendedAmnt,
				AccountConstants.PAYMENT_PAID);

		savings.addAccountActionDate(actionDate1);
		savings.addAccountActionDate(actionDate2);

		Money depositAmount = new Money(currency, "1200.0");

		// Adding 1 account payment of Rs 1200. With three transactions of (500
		// + 500 + 200).
		AccountPaymentEntity payment = helper.createAccountPayment(
				depositAmount, paymentDate, createdBy);
		Money balanceAmount = new Money(currency, "4500.0");
		SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(Short
				.valueOf("1"), recommendedAmnt, balanceAmount, paymentDate,
				helper.getDate("01/05/2006"), null,
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);

		balanceAmount = new Money(currency, "5000.0");
		SavingsTrxnDetailEntity trxn2 = helper.createAccountTrxn(Short
				.valueOf("2"), recommendedAmnt, balanceAmount, paymentDate,
				helper.getDate("08/05/2006"), null,
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);

		balanceAmount = new Money(currency, "5200.0");
		SavingsTrxnDetailEntity trxn3 = helper.createAccountTrxn(null,
				new Money(currency, "200.0"), balanceAmount, paymentDate, null,
				null, AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,
				createdBy, group);

		payment.addAcountTrxn(trxn1);
		payment.addAcountTrxn(trxn2);
		payment.addAcountTrxn(trxn3);
		savings.addAccountPayment(payment);

		savings.setSavingsBalance(balanceAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = savings.getLastPmnt();
		for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
			Hibernate.initialize(accountTrxn.getFinancialTransactions());
		}
		assertEquals(Integer.valueOf(3).intValue(), payment.getAccountTrxns()
				.size());

		// Adjust last deposit of 1200 to 2000.
		Money amountAdjustedTo = new Money(currency, "2000.0");
		SavingsType savingsType = new SavingsType();
		savingsType.setSavingsTypeId(ProductDefinitionConstants.MANDATORY);
		savings.setSavingsType(savingsType);
		savings.adjustLastUserAction(amountAdjustedTo, "correction entry");

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		AccountPaymentEntity newPayment = savings.getLastPmnt();

		assertEquals(Integer.valueOf(2).intValue(), savings
				.getAccountPayments().size());
		assertEquals(Integer.valueOf(6).intValue(), payment.getAccountTrxns()
				.size());
		int countFinancialTrxns = 0;
		for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
			countFinancialTrxns += accountTrxn.getFinancialTransactions()
					.size();
			if (accountTrxn.getAccountActionEntity().getId().equals(
					AccountConstants.ACTION_SAVINGS_ADJUSTMENT))
				for (FinancialTransactionBO finTrxn : accountTrxn
						.getFinancialTransactions()) {
					assertEquals("correction entry", finTrxn.getNotes());
				}
		}
		assertEquals(Integer.valueOf(6).intValue(), countFinancialTrxns);
		assertEquals(payment.getAmount(), new Money());

		assertEquals(new Money(currency, "6000.0"), savings.getSavingsBalance());
		assertEquals(newPayment.getAmount(), amountAdjustedTo);
		assertEquals(Integer.valueOf(3).intValue(), newPayment
				.getAccountTrxns().size());

		Hibernate.initialize(savings.getAccountActionDates());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testAdjustPmnt_LastPaymentDepositMandatory_PaidSomeDueInstallments()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		Money recommendedAmnt = new Money(currency, "500.0");
		Date paymentDate = helper.getDate("08/05/2006");
		AccountActionDateEntity actionDate1 = helper.createAccountActionDate(
				Short.valueOf("1"), helper.getDate("01/05/2006"), paymentDate,
				savings.getCustomer(), recommendedAmnt, recommendedAmnt,
				AccountConstants.PAYMENT_PAID);
		AccountActionDateEntity actionDate2 = helper.createAccountActionDate(
				Short.valueOf("2"), helper.getDate("08/05/2006"), null, savings
						.getCustomer(), recommendedAmnt, null,
				AccountConstants.PAYMENT_UNPAID);

		savings.addAccountActionDate(actionDate1);
		savings.addAccountActionDate(actionDate2);

		Money depositAmount = new Money(currency, "500.0");

		// Adding 1 account payment of Rs 500. With one transactions of 500.
		AccountPaymentEntity payment = helper.createAccountPayment(
				depositAmount, paymentDate, createdBy);
		Money balanceAmount = new Money(currency, "4500.0");

		SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(Short
				.valueOf("1"), recommendedAmnt, balanceAmount, paymentDate,
				helper.getDate("01/05/2006"), null,
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);

		payment.addAcountTrxn(trxn1);
		savings.addAccountPayment(payment);

		savings.setSavingsBalance(balanceAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = savings.getLastPmnt();

		assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns()
				.size());

		// Adjust last deposit of 500 to 1200.
		Money amountAdjustedTo = new Money(currency, "1200.0");
		SavingsType savingsType = new SavingsType();
		savingsType.setSavingsTypeId(ProductDefinitionConstants.MANDATORY);
		savings.setSavingsType(savingsType);
		savings.adjustLastUserAction(amountAdjustedTo, "correction entry");

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());

		AccountPaymentEntity newPayment = savings.getLastPmnt();
		for (AccountTrxnEntity accountTrxn : newPayment.getAccountTrxns()) {
			if (accountTrxn.getInstallmentId() != null
					&& accountTrxn.getInstallmentId().shortValue() == 1)
				assertTrue(true);
		}

		assertEquals(Integer.valueOf(2).intValue(), savings
				.getAccountPayments().size());
		assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns()
				.size());
		assertEquals(payment.getAmount(), new Money());

		assertEquals(new Money(currency, "5200.0"), savings.getSavingsBalance());
		assertEquals(amountAdjustedTo, newPayment.getAmount());
		assertEquals(Integer.valueOf(3).intValue(), newPayment
				.getAccountTrxns().size());

		Hibernate.initialize(savings.getAccountActionDates());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testAdjustPmnt_LastPaymentDepositMandatory_PaidPartialDueAmount()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		Money recommendedAmnt = new Money(currency, "500.0");
		Money partialAmnt = new Money(currency, "200.0");
		Date paymentDate = helper.getDate("08/05/2006");
		AccountActionDateEntity actionDate1 = helper.createAccountActionDate(
				Short.valueOf("1"), helper.getDate("01/05/2006"), paymentDate,
				savings.getCustomer(), recommendedAmnt, recommendedAmnt,
				AccountConstants.PAYMENT_PAID);
		AccountActionDateEntity actionDate2 = helper.createAccountActionDate(
				Short.valueOf("2"), helper.getDate("08/05/2006"), paymentDate,
				savings.getCustomer(), recommendedAmnt, partialAmnt,
				AccountConstants.PAYMENT_UNPAID);

		savings.addAccountActionDate(actionDate1);
		savings.addAccountActionDate(actionDate2);

		Money depositAmount = new Money(currency, "700.0");

		// Adding 1 account payment of Rs 500. With one transactions of 500.
		AccountPaymentEntity payment = helper.createAccountPayment(
				depositAmount, paymentDate, createdBy);
		Money balanceAmount = new Money(currency, "4500.0");
		SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(Short
				.valueOf("1"), recommendedAmnt, balanceAmount, paymentDate,
				helper.getDate("01/05/2006"), null,
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);

		balanceAmount = new Money(currency, "4700.0");
		SavingsTrxnDetailEntity trxn2 = helper.createAccountTrxn(Short
				.valueOf("2"), partialAmnt, balanceAmount, paymentDate, helper
				.getDate("08/05/2006"), null,
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);

		payment.addAcountTrxn(trxn1);
		payment.addAcountTrxn(trxn2);
		savings.addAccountPayment(payment);

		savings.setSavingsBalance(balanceAmount);
		SavingsType savingsType = new SavingsType();
		savingsType.setSavingsTypeId(ProductDefinitionConstants.MANDATORY);
		savings.getSavingsOffering().setSavingsType(savingsType);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = savings.getLastPmnt();

		assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns()
				.size());

		// Adjust last deposit of 700 to 1000.
		Money amountAdjustedTo = new Money(currency, "1000.0");
		savings.adjustLastUserAction(amountAdjustedTo, "correction entry");

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());

		AccountPaymentEntity newPayment = savings.getLastPmnt();

		assertEquals(Integer.valueOf(2).intValue(), savings
				.getAccountPayments().size());
		assertEquals(Integer.valueOf(4).intValue(), payment.getAccountTrxns()
				.size());
		assertEquals(payment.getAmount(), new Money());

		assertEquals(new Money(currency, "5000.0"), savings.getSavingsBalance());
		assertEquals(amountAdjustedTo, newPayment.getAmount());
		assertEquals(Integer.valueOf(2).intValue(), newPayment
				.getAccountTrxns().size());

		Hibernate.initialize(savings.getAccountActionDates());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testAdjustPmnt_LastPaymentDepositVol_PaidDueExactAmount()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		Money recommendedAmnt = new Money(currency, "500.0");
		Money partialAmnt = new Money(currency, "200.0");
		Date paymentDate = helper.getDate("06/05/2006");
		AccountActionDateEntity actionDate1 = helper.createAccountActionDate(
				Short.valueOf("1"), helper.getDate("01/05/2006"), paymentDate,
				savings.getCustomer(), recommendedAmnt, partialAmnt,
				AccountConstants.PAYMENT_PAID);
		AccountActionDateEntity actionDate2 = helper.createAccountActionDate(
				Short.valueOf("2"), helper.getDate("08/05/2006"), null, savings
						.getCustomer(), recommendedAmnt, null,
				AccountConstants.PAYMENT_UNPAID);

		savings.addAccountActionDate(actionDate1);
		savings.addAccountActionDate(actionDate2);

		Money depositAmount = new Money(currency, "200.0");

		// Adding 1 account payment of Rs 500. With one transactions of 500.
		AccountPaymentEntity payment = helper.createAccountPayment(
				depositAmount, paymentDate, createdBy);
		Money balanceAmount = new Money(currency, "4200.0");
		SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(Short
				.valueOf("1"), partialAmnt, balanceAmount, paymentDate, helper
				.getDate("01/05/2006"), null,
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);

		payment.addAcountTrxn(trxn1);
		savings.addAccountPayment(payment);

		savings.setSavingsBalance(balanceAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = savings.getLastPmnt();

		assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns()
				.size());

		// Adjust last deposit of 700 to 1000.
		Money amountAdjustedTo = new Money(currency, "500.0");
		savings.adjustLastUserAction(amountAdjustedTo, "correction entry");

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());

		AccountPaymentEntity newPayment = savings.getLastPmnt();

		assertEquals(Integer.valueOf(2).intValue(), savings
				.getAccountPayments().size());
		assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns()
				.size());
		assertEquals(payment.getAmount(), new Money());

		assertEquals(new Money(currency, "4500.0"), savings.getSavingsBalance());
		assertEquals(amountAdjustedTo, newPayment.getAmount());
		assertEquals(Integer.valueOf(1).intValue(), newPayment
				.getAccountTrxns().size());

		Hibernate.initialize(savings.getAccountActionDates());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testAdjustPmnt_LastPaymentDepositVol_PaidDueExcessAmount()
			throws Exception {
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		Money recommendedAmnt = new Money(currency, "500.0");
		Money partialAmnt = new Money(currency, "200.0");
		Date paymentDate = helper.getDate("06/05/2006");
		AccountActionDateEntity actionDate1 = helper.createAccountActionDate(
				Short.valueOf("1"), helper.getDate("01/05/2006"), paymentDate,
				savings.getCustomer(), recommendedAmnt, partialAmnt,
				AccountConstants.PAYMENT_PAID);
		AccountActionDateEntity actionDate2 = helper.createAccountActionDate(
				Short.valueOf("2"), helper.getDate("08/05/2006"), null, savings
						.getCustomer(), recommendedAmnt, null,
				AccountConstants.PAYMENT_UNPAID);

		savings.addAccountActionDate(actionDate1);
		savings.addAccountActionDate(actionDate2);

		Money depositAmount = new Money(currency, "200.0");

		// Adding 1 account payment of Rs 500. With one transactions of 500.
		AccountPaymentEntity payment = helper.createAccountPayment(
				depositAmount, paymentDate, createdBy);
		Money balanceAmount = new Money(currency, "4200.0");
		SavingsTrxnDetailEntity trxn1 = helper.createAccountTrxn(Short
				.valueOf("1"), partialAmnt, balanceAmount, paymentDate, helper
				.getDate("01/05/2006"), null,
				AccountConstants.ACTION_SAVINGS_DEPOSIT, savings, createdBy,
				group);

		payment.addAcountTrxn(trxn1);
		savings.addAccountPayment(payment);

		savings.setSavingsBalance(balanceAmount);
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();

		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		payment = savings.getLastPmnt();

		assertEquals(Integer.valueOf(1).intValue(), payment.getAccountTrxns()
				.size());

		// Adjust last deposit of 700 to 1000.
		Money amountAdjustedTo = new Money(currency, "800.0");
		savings.adjustLastUserAction(amountAdjustedTo, "correction entry");

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());

		AccountPaymentEntity newPayment = savings.getLastPmnt();

		assertEquals(Integer.valueOf(2).intValue(), savings
				.getAccountPayments().size());
		assertEquals(Integer.valueOf(2).intValue(), payment.getAccountTrxns()
				.size());
		assertEquals(payment.getAmount(), new Money());

		assertEquals(new Money(currency, "4800.0"), savings.getSavingsBalance());
		assertEquals(amountAdjustedTo, newPayment.getAmount());
		assertEquals(Integer.valueOf(2).intValue(), newPayment
				.getAccountTrxns().size());

		Hibernate.initialize(savings.getAccountActionDates());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testGetOverDueDepositAmountForMandatoryAccounts()
			throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = TestObjectFactory
				.createSavingsOffering("SavingPrd1", Short.valueOf("2"),
						new Date(System.currentTimeMillis()), Short
								.valueOf("2"), 300.0, Short.valueOf("1"), 1.2,
						200.0, 200.0, Short.valueOf("1"), Short.valueOf("1"),
						meetingIntCalc, meetingIntPost);
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client1 = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1.1", group, new Date(System.currentTimeMillis()));
		savings = TestObjectFactory.createSavingsAccount("43245434", client1,
				Short.valueOf("16"), new Date(System.currentTimeMillis()),
				savingsOffering);
		Session session = HibernateUtil.getSessionTL();
		Transaction trxn = session.beginTransaction();
		AccountActionDateEntity accountActionDate = savings
				.getAccountActionDate((short) 1);
		accountActionDate.setDepositPaid(new Money(TestObjectFactory
				.getMFICurrency(), "100.0"));
		session.update(savings);
		trxn.commit();
		HibernateUtil.closeSession();
		accountActionDate = savings.getAccountActionDate((short) 3);
		assertEquals(300.00, savings.getOverDueDepositAmount(
				accountActionDate.getActionDate()).getAmountDoubleValue());

	}

	public void testGetOverDueDepositAmountForVoluntaryAccounts()
			throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		SavingsOfferingBO savingsOffering = helper.createSavingsOffering();
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client1 = TestObjectFactory.createClient("Client", Short.valueOf("3"),
				"1.1.1.1", group, new Date(System.currentTimeMillis()));
		savings = TestObjectFactory.createSavingsAccount("43245434", client1,
				Short.valueOf("16"), new Date(System.currentTimeMillis()),
				savingsOffering);
		Session session = HibernateUtil.getSessionTL();
		Transaction trxn = session.beginTransaction();
		AccountActionDateEntity accountActionDate = savings
				.getAccountActionDate((short) 1);
		accountActionDate.setDepositPaid(new Money(TestObjectFactory
				.getMFICurrency(), "100.0"));
		session.update(savings);
		trxn.commit();
		HibernateUtil.closeSession();
		assertEquals(0.00, savings.getOverDueDepositAmount(
				accountActionDate.getActionDate()).getAmountDoubleValue());

	}
	
	public void testGetRecentAccountActivity()throws Exception{
		try{
		createInitialObjects();
		savingsOffering = helper.createSavingsOffering();
		savings = helper.createSavingsAccount("000100000000017",
				savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		
		SavingsActivityEntity savingsActivity = new SavingsActivityEntity(savings.getPersonnel(),(AccountActionEntity)HibernateUtil.getSessionTL().get(AccountActionEntity.class,Short.valueOf("1")),new Money("100"),new Money("22"));
		savingsActivity.setAccount(savings);
		savingsActivity.setCreatedBy(Short.valueOf("1"));
		savingsActivity.setCreatedDate(new Date(System.currentTimeMillis()));
		
		savings.addSavingsActivityDetails(savingsActivity);
		
		savings.update();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		assertEquals(1,savings.getRecentAccountActivity(3).size());
		group = savings.getCustomer();
		center = group.getParentCustomer();
		}catch (Exception e ){e.printStackTrace();}
	}
	
	
	/**
	 * For the testcases for amount in arrears, we are creating a savings account with deposit due of 200.0
	 * for each installment. So all the values and asserts are based on that.
	 * 
	 * So, if 1 installments is not paid, then amount in arrears will be 200.0 and the total amount due will
	 * be 400.0 (which includes amount in arrears + amount to be paid for next installment)  
	 * 
	 */
	public void testGetTotalAmountInArrearsForCurrentDateMeeting() throws Exception {
		savings = getSavingsAccount();
		assertEquals(savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
	}
	
	public void testGetTotalAmountInArrearsForSingleInstallmentDue() {
		savings = getSavingsAccount();
		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals(savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 200.0);
	}

	public void testGetTotalAmountInArrearsWithPartialPayment() {
		savings = getSavingsAccount();
		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setDepositPaid(new Money("20.0"));
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));

		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals( savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 180.0);
	}

	public void testGetTotalAmountInArrearsWithPaymentDone() {
		savings = getSavingsAccount();

		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate(Short.valueOf("1"));
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		accountActionDateEntity.setPaymentStatus(AccountConstants.PAYMENT_PAID);
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals( savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
	}

	public void testGetTotalAmountDueForTwoInstallmentsDue() {
		savings = getSavingsAccount();

		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(2));
		AccountActionDateEntity accountActionDateEntity2 = savings
				.getAccountActionDate((short) 2);
		accountActionDateEntity2.setActionDate(offSetCurrentDate(1));

		savings = (SavingsBO)saveAndFetch(savings);

		assertEquals( savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 400.0);
	}
	
	public void testGetTotalAmountDueForCurrentDateMeeting() {
		savings = getSavingsAccount();
		assertEquals( savings.getTotalAmountDue()
				.getAmountDoubleValue(), 200.0);
	}

	public void testGetTotalAmountDueForSingleInstallment() {
		savings = getSavingsAccount();

		AccountActionDateEntity accountActionDateEntity =  savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));

		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals( savings.getTotalAmountDue()
				.getAmountDoubleValue(), 400.0);
	}

	public void testGetTotalAmountDueWithPartialPayment() {
		savings = getSavingsAccount();

		AccountActionDateEntity accountActionDateEntity =  savings
				.getAccountActionDate((short) 1);

		accountActionDateEntity.setDepositPaid(new Money("20.0"));
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals( savings.getTotalAmountDue()
				.getAmountDoubleValue(), 380.0);
	}

	public void testGetTotalAmountDueWithPaymentDone() {
		savings = getSavingsAccount();

		AccountActionDateEntity accountActionDateEntity =  savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		accountActionDateEntity.setPaymentStatus(AccountConstants.PAYMENT_PAID);

		savings = (SavingsBO)saveAndFetch(savings);

		assertEquals( savings.getTotalAmountDue()
				.getAmountDoubleValue(), 200.0);
	}

	public void testGetTotalAmountDueForTwoInstallments() {
		savings = getSavingsAccount();
		AccountActionDateEntity accountActionDateEntity =  savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(2));
		AccountActionDateEntity accountActionDateEntity2 =  savings
				.getAccountActionDate((short) 2);

		accountActionDateEntity2.setActionDate(offSetCurrentDate(1));

		savings = (SavingsBO)saveAndFetch(savings);

		assertEquals( savings.getTotalAmountDue()
				.getAmountDoubleValue(), 600.0);
	}
	
	public void testGetTotalAmountDueForNextInstallmentForCurrentDateMeeting() throws Exception {
		savings = getSavingsAccount();
		assertEquals(savings.getTotalAmountDueForNextInstallment()
				.getAmountDoubleValue(), 200.0);
	}
	
	public void testGetTotalAmountDueForNextInstallmentWithPartialPayment() {
		savings = getSavingsAccount();
		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setDepositPaid(new Money("20.0"));

		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals( savings.getTotalAmountDueForNextInstallment()
				.getAmountDoubleValue(), 180.0);
	}

	public void testGetTotalAmountDueForNextInstallmentPaymentDone() {
		savings = getSavingsAccount();

		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate(Short.valueOf("1"));
		accountActionDateEntity.setPaymentStatus(AccountConstants.PAYMENT_PAID);
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals( savings.getTotalAmountDueForNextInstallment()
				.getAmountDoubleValue(), 0.0);
	}

	public void testGetDetailsOfInstallmentsInArrearsForCurrentDateMeeting() throws Exception {
		savings = getSavingsAccount();
		assertEquals(savings.getDetailsOfInstallmentsInArrears()
				.size(), 0);
	}
	
	public void testGetDetailsOfInstallmentsInArrearsForSingleInstallmentDue() {
		savings = getSavingsAccount();
		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals(savings.getDetailsOfInstallmentsInArrears()
				.size(), 1);
	}

	public void testGetDetailsOfInstallmentsInArrearsWithPaymentDone() {
		savings = getSavingsAccount();

		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate(Short.valueOf("1"));
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		accountActionDateEntity.setPaymentStatus(AccountConstants.PAYMENT_PAID);
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals( savings.getDetailsOfInstallmentsInArrears()
				.size(), 0);
	}

	public void testGetDetailsOfInstallmentsInArrearsForTwoInstallmentsDue() {
		savings = getSavingsAccount();

		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(2));
		AccountActionDateEntity accountActionDateEntity2 = savings
				.getAccountActionDate((short) 2);
		accountActionDateEntity2.setActionDate(offSetCurrentDate(1));

		savings = (SavingsBO)saveAndFetch(savings);

		assertEquals( savings.getDetailsOfInstallmentsInArrears()
				.size(), 2);
	}

	public void testWaiveAmountOverDueForSingleInstallmentDue() throws ServiceException, AccountException {
		savings = getSavingsAccount();
		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals(savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 200.0);
		savings.waiveAmountOverDue();
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals(savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
		assertEquals(savings.getSavingsActivityDetails().size(), 1);
	}

	public void testWaiveAmountOverDueWithPartialPayment() throws ServiceException, AccountException {
		savings = getSavingsAccount();
		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setDepositPaid(new Money("20.0"));
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));

		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals( savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 180.0);
		
		savings.waiveAmountOverDue();
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals(savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
		assertEquals(savings.getSavingsActivityDetails().size(), 1);
	}

	public void testWaiveAmountOverDueForTwoInstallmentsDue() throws ServiceException, AccountException {
		savings = getSavingsAccount();

		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(2));
		AccountActionDateEntity accountActionDateEntity2 = savings
				.getAccountActionDate((short) 2);
		accountActionDateEntity2.setActionDate(offSetCurrentDate(1));

		savings = (SavingsBO)saveAndFetch(savings);

		assertEquals( savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 400.0);
		
		savings.waiveAmountOverDue();
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals(savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
		assertEquals(savings.getSavingsActivityDetails().size(), 1);
	}

	public void testWaiveAmountDueForCurrentDateMeeting() throws Exception {
		savings = getSavingsAccount();
		assertEquals(savings.getTotalAmountDueForNextInstallment()
				.getAmountDoubleValue(), 200.0);
		savings.waiveAmountDue();
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals(savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
		assertEquals(savings.getSavingsActivityDetails().size(), 1);
	}
	
	public void testWaiveAmountDueWithPartialPayment() throws ServiceException, AccountException {
		savings = getSavingsAccount();
		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setDepositPaid(new Money("20.0"));

		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals( savings.getTotalAmountDueForNextInstallment()
				.getAmountDoubleValue(), 180.0);
		savings.waiveAmountDue();
		savings = (SavingsBO)saveAndFetch(savings);
		assertEquals(savings.getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
		assertEquals(savings.getSavingsActivityDetails().size(), 1);
	}

	private void createCustomerObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center_Active_test", Short
				.valueOf("13"), "1.1", meeting, new Date(System
				.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		client1 = TestObjectFactory.createClient("client1",
				ClientConstants.STATUS_CLOSED, "1.1.1.1", group, new Date(
						System.currentTimeMillis()));
		client2 = TestObjectFactory.createClient("client2",
				ClientConstants.STATUS_ACTIVE, "1.1.1.2", group, new Date(
						System.currentTimeMillis()));
	}
	
	
	public void testCalculateInterest_IntCalcFreqOneDay_minbal()throws Exception{
		createInitialObjects();
		savingsOffering = createSavingsOfferingForIntCalc("prd1",Short.valueOf("1"), Short.valueOf("1"));
		savings = helper.createSavingsAccount(savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		savings.setNextIntCalcDate(helper.getDate("06/03/2006"));
		savings.setActivationDate(helper.getDate("05/03/2006"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		savings.updateInterestAccrued();
		assertEquals(0.0,savings.getInterestToBePosted().getAmountDoubleValue());
		assertEquals(helper.getDate("07/03/2006"),savings.getNextIntCalcDate());
		

		AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(new Money(currency, "1000.0"), new Money(currency, "1000.0"),
		helper.getDate("06/03/2006"),AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,	createdBy, group);
		
		savings.addAccountPayment(payment1);
		savings.update();
		HibernateUtil.commitTransaction();
		
		AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(new Money(currency, "2500.0"), new Money(currency, "3500.0"),
		helper.getDate("06/03/2006"),AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,createdBy, group);
		savings.addAccountPayment(payment2);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		savings.updateInterestAccrued();
		assertEquals(1.2,savings.getInterestToBePosted().getAmountDoubleValue());
		assertEquals(helper.getDate("08/03/2006"),savings.getNextIntCalcDate());
	}
	
	
	public void testCalculateInterest_IntCalcFreqOneDay_avgBal()throws Exception{
		createInitialObjects();
		savingsOffering = createSavingsOfferingForIntCalc("prd1",Short.valueOf("2"), Short.valueOf("1"));
		savings = helper.createSavingsAccount(savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		savings.setNextIntCalcDate(helper.getDate("06/03/2006"));
		savings.setActivationDate(helper.getDate("05/03/2006"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		savings.updateInterestAccrued();
		assertEquals(0.0,savings.getInterestToBePosted().getAmountDoubleValue());
		assertEquals(helper.getDate("07/03/2006"),savings.getNextIntCalcDate());
		
		AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(new Money(currency, "1000.0"), new Money(currency, "1000.0"),
		helper.getDate("06/03/2006"),AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,	createdBy, group);
		
		savings.addAccountPayment(payment1);
		savings.update();
		HibernateUtil.commitTransaction();
		
		AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(new Money(currency, "2500.0"), new Money(currency, "3500.0"),
		helper.getDate("06/03/2006"),AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,createdBy, group);
		savings.addAccountPayment(payment2);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		savings.updateInterestAccrued();
		assertEquals(1.2,savings.getInterestToBePosted().getAmountDoubleValue());
		assertEquals(helper.getDate("08/03/2006"),savings.getNextIntCalcDate());
	}
	
	
	public void testCalculateInterest_IntCalcFreqTenDays_minbal()throws Exception{
		createInitialObjects();
		savingsOffering = createSavingsOfferingForIntCalc("prd1",Short.valueOf("1"), Short.valueOf("10"));
		savings = helper.createSavingsAccount(savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		savings.setNextIntCalcDate(helper.getDate("12/03/2006"));
		savings.setActivationDate(helper.getDate("05/03/2006"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		savings.updateInterestAccrued();
		assertEquals(0.0,savings.getInterestToBePosted().getAmountDoubleValue());
		assertEquals(helper.getDate("22/03/2006"),savings.getNextIntCalcDate());
		
		AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(new Money(currency, "1000.0"), new Money(currency, "1000.0"),
		helper.getDate("11/03/2006"),AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,	createdBy, group);
		
		savings.addAccountPayment(payment1);
		savings.update();
		HibernateUtil.commitTransaction();
		
		AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(new Money(currency, "500.0"), new Money(currency, "500.0"),
		helper.getDate("15/03/2006"),AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings,createdBy, group);
		savings.addAccountPayment(payment2);
		savings.update();
		
		AccountPaymentEntity payment3 = helper.createAccountPaymentToPersist(new Money(currency, "2500.0"), new Money(currency, "3000.0"),
		helper.getDate("19/03/2006"),AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,createdBy, group);
		savings.addAccountPayment(payment3);
		savings.update();
		
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		savings.updateInterestAccrued();
		assertEquals(1.6,savings.getInterestToBePosted().getAmountDoubleValue());
		assertEquals(helper.getDate("01/04/2006"),savings.getNextIntCalcDate());
	}
	
	public void testCalculateInterest_IntCalcFreqTenDays_avg()throws Exception{
		createInitialObjects();
		savingsOffering = createSavingsOfferingForIntCalc("prd1",Short.valueOf("2"), Short.valueOf("10"));
		savings = helper.createSavingsAccount(savingsOffering, group, AccountStates.SAVINGS_ACC_APPROVED, userContext);
		savings.setNextIntCalcDate(helper.getDate("12/03/2006"));
		savings.setActivationDate(helper.getDate("05/03/2006"));
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		savings.updateInterestAccrued();
		assertEquals(0.0,savings.getInterestToBePosted().getAmountDoubleValue());
		assertEquals(helper.getDate("22/03/2006"),savings.getNextIntCalcDate());
		
		AccountPaymentEntity payment1 = helper.createAccountPaymentToPersist(new Money(currency, "1000.0"), new Money(currency, "1000.0"),
		helper.getDate("11/03/2006"),AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,	createdBy, group);
		
		savings.addAccountPayment(payment1);
		savings.update();
		HibernateUtil.commitTransaction();
		
		AccountPaymentEntity payment2 = helper.createAccountPaymentToPersist(new Money(currency, "500.0"), new Money(currency, "500.0"),
		helper.getDate("15/03/2006"),AccountConstants.ACTION_SAVINGS_WITHDRAWAL, savings,createdBy, group);
		savings.addAccountPayment(payment2);
		savings.update();
		
		AccountPaymentEntity payment3 = helper.createAccountPaymentToPersist(new Money(currency, "2500.0"), new Money(currency, "3000.0"),
		helper.getDate("19/03/2006"),AccountConstants.ACTION_SAVINGS_DEPOSIT, savings,createdBy, group);
		savings.addAccountPayment(payment3);
		savings.update();
		
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsService.findById(savings.getAccountId());
		savings.updateInterestAccrued();
		assertEquals(4.6,savings.getInterestToBePosted().getAmountDoubleValue());
		assertEquals(helper.getDate("01/04/2006"),savings.getNextIntCalcDate());
	}
	
	private SavingsOfferingBO createSavingsOfferingForIntCalc(String offeringName, Short interestCalcType, Short recurAfterIntCalc)throws Exception{
		MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(helper.getMeeting("3",recurAfterIntCalc,Short.valueOf("2")));
		MeetingBO meetingIntPost = TestObjectFactory.createMeeting(helper.getMeeting("2",Short.valueOf("1"),Short.valueOf("3")));
		return TestObjectFactory.createSavingsOffering(offeringName,Short.valueOf("2"),new Date(System.currentTimeMillis()),
				Short.valueOf("2"),300.0,Short.valueOf("1"),12.0,200.0,200.0,Short.valueOf("2"),interestCalcType,meetingIntCalc,meetingIntPost);
	}
	
	private SavingsBO getSavingsAccount() {
		createCustomerObjects();
		savingsOffering = helper.createSavingsOffering();
		return TestObjectFactory.createSavingsAccount("000100000000017",client1, 
				AccountStates.SAVINGS_ACC_APPROVED,
				new Date(System.currentTimeMillis()),savingsOffering );
	} 

	private AccountBO saveAndFetch(AccountBO account) {
		accountPersistanceService.updateAccount(account);
		return accountPersistanceService.getAccount(account.getAccountId());
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
