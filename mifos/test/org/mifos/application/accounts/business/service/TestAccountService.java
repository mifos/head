/**
 * 
 */
package org.mifos.application.accounts.business.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.business.TestAccountFeesEntity;
import org.mifos.application.accounts.business.TransactionHistoryView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanFeeScheduleEntity;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.ApplicableCharge;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountService extends MifosTestCase {

	protected AccountBO accountBO = null;

	protected SavingsBO savingsBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	private MeetingBO meeting;

	protected AccountPersistence accountPersistence;

	private AccountBusinessService service;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		accountPersistence = new AccountPersistence();
		service = (AccountBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Accounts);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(savingsBO);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		accountPersistence = null;
	}

	public void testGetTrxnHistory() throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		Date currentDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount();
		LoanBO loan = (LoanBO) accountBO;

		UserContext uc = TestObjectFactory.getUserContext();
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(100), null,
						loan.getPersonnel(), "receiptNum", Short.valueOf("1"),
						currentDate, currentDate);

		loan.applyPayment(accountPaymentDataView);
		TestObjectFactory.flushandCloseSession();
		loan = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());
		loan.setUserContext(uc);

		List<TransactionHistoryView> trxnHistlist = accountBusinessService
				.getTrxnHistory(loan, uc);
		assertNotNull("Account TrxnHistoryView list object should not be null",
				trxnHistlist);
		assertTrue(
				"Account TrxnHistoryView list object Size should be greater than zero",
				trxnHistlist.size() > 0);
		TestObjectFactory.flushandCloseSession();
		accountBO = (LoanBO) TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());

	}

	public void testGetAccountAction() throws Exception {
		AccountBusinessService service = (AccountBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Accounts);
		AccountActionEntity accountaction = service.getAccountAction(
				AccountConstants.ACTION_SAVINGS_DEPOSIT, Short.valueOf("1"));
		assertNotNull(accountaction);
		assertEquals(Short.valueOf("1"), accountaction.getLocaleId());
	}

	public void testGetAppllicableFees() throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		List<ApplicableCharge> applicableChargeList = accountBusinessService
				.getAppllicableFees(accountBO.getAccountId(), uc);
		assertEquals(2, applicableChargeList.size());
	}

	public void testGetAppllicableFeesForInstallmentStartingOnCurrentDate()
			throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		accountBO = getLoanAccountWithAllTypesOfFees();
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		List<ApplicableCharge> applicableChargeList = accountBusinessService
				.getAppllicableFees(accountBO.getAccountId(), uc);
		assertEquals(4, applicableChargeList.size());
		for (ApplicableCharge applicableCharge : applicableChargeList) {
			if (applicableCharge.getFeeName().equalsIgnoreCase("Upfront Fee")) {
				assertEquals(new Money("20.0").toString(), applicableCharge
						.getAmountOrRate());
				assertNotNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			} else if (applicableCharge.getFeeName().equalsIgnoreCase(
					"Periodic Fee")) {
				assertEquals(new Money("200.0").toString(), applicableCharge
						.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNotNull(applicableCharge.getPeriodicity());
			} else if (applicableCharge.getFeeName().equalsIgnoreCase(
					"Misc Fee")) {
				assertNull(applicableCharge.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			}
		}
	}

	public void testGetAppllicableFeesForInstallmentStartingAfterCurrentDate()
			throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		accountBO = getLoanAccountWithAllTypesOfFees();
		incrementInstallmentDate(accountBO, 1, Short.valueOf("1"));
		accountBO.setUserContext(TestObjectFactory.getContext());
		accountBO.changeStatus(
				AccountState.LOANACC_DBTOLOANOFFICER.getValue(),null,"");
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		List<ApplicableCharge> applicableChargeList = accountBusinessService
				.getAppllicableFees(accountBO.getAccountId(), uc);
		assertEquals(6, applicableChargeList.size());
		for (ApplicableCharge applicableCharge : applicableChargeList) {
			if (applicableCharge.getFeeName().equalsIgnoreCase("Upfront Fee")) {
				assertEquals(new Money("20.0").toString(), applicableCharge
						.getAmountOrRate());
				assertNotNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			} else if (applicableCharge.getFeeName().equalsIgnoreCase(
					"Periodic Fee")) {
				assertEquals(new Money("200.0").toString(), applicableCharge
						.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNotNull(applicableCharge.getPeriodicity());
			} else if (applicableCharge.getFeeName().equalsIgnoreCase(
					"Misc Fee")) {
				assertNull(applicableCharge.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			}
		}
	}

	public void testGetAppllicableFeesForMeetingStartingOnCurrentDate()
			throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		CustomerAccountBO customerAccountBO = getCustomerAccountWithAllTypesOfFees();
		TestObjectFactory.flushandCloseSession();
		center = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				center.getCustomerId());
		UserContext uc = TestObjectFactory.getUserContext();
		List<ApplicableCharge> applicableChargeList = accountBusinessService
				.getAppllicableFees(customerAccountBO.getAccountId(), uc);
		assertEquals(4, applicableChargeList.size());
		for (ApplicableCharge applicableCharge : applicableChargeList) {
			if (applicableCharge.getFeeName().equalsIgnoreCase("Upfront Fee")) {
				assertEquals(new Money("20.0").toString(), applicableCharge
						.getAmountOrRate());
				assertNotNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			} else if (applicableCharge.getFeeName().equalsIgnoreCase(
					"Misc Fee")) {
				assertNull(applicableCharge.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			} else if (applicableCharge.getFeeName().equalsIgnoreCase(
					"Periodic Fee")) {
				assertEquals(new Money("200.0").toString(), applicableCharge
						.getAmountOrRate());
				assertNull(applicableCharge.getFormula());
				assertNotNull(applicableCharge.getPeriodicity());
			} else if (applicableCharge.getFeeName().equalsIgnoreCase(
					"Mainatnence Fee")) {
				assertFalse(true);
			}
		}
	}

	public void testGetStatusName() throws Exception {
		getInitialCustomersAndAccounts();

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				center.getOffice().getOfficeId(), AccountTypes.SAVINGSACCOUNT,
				null);
		String statusNameForSavings = service.getStatusName(TestObjectFactory
				.getUserContext().getLocaleId(), savingsBO.getState(),
				AccountTypes.SAVINGSACCOUNT);
		assertNotNull(statusNameForSavings);

		AccountStateMachines.getInstance()
				.initialize(TestObjectFactory.getUserContext().getLocaleId(),
						group.getOffice().getOfficeId(),
						AccountTypes.LOANACCOUNT, null);
		String statusNameForLoan = service.getStatusName(TestObjectFactory
				.getUserContext().getLocaleId(), accountBO.getState(),
				AccountTypes.LOANACCOUNT);
		assertNotNull(statusNameForLoan);
	}

	public void testGetFlagName() throws Exception {
		getInitialCustomersAndAccounts();
		savingsBO.setUserContext(TestObjectFactory.getUserContext());
		accountBO.setUserContext(TestObjectFactory.getUserContext());
		savingsBO.changeStatus(AccountState.SAVINGS_ACC_CANCEL.getValue(),
				AccountStateFlag.SAVINGS_REJECTED.getValue(), "rejected flag");
		savingsBO.update();
		accountBO.changeStatus(AccountState.LOANACC_CANCEL.getValue(),
				AccountStateFlag.LOAN_REJECTED.getValue(), "rejected flag");
		accountBO.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		fetchAccounts();

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				center.getOffice().getOfficeId(), AccountTypes.SAVINGSACCOUNT,
				null);
		String flagNameForSavings = service.getFlagName(TestObjectFactory
				.getUserContext().getLocaleId(),
				AccountStateFlag.SAVINGS_REJECTED, AccountTypes.SAVINGSACCOUNT);
		assertNotNull(flagNameForSavings);

		AccountStateMachines.getInstance()
				.initialize(TestObjectFactory.getUserContext().getLocaleId(),
						group.getOffice().getOfficeId(),
						AccountTypes.LOANACCOUNT, null);
		String flagNameForLoan = service.getFlagName(TestObjectFactory
				.getUserContext().getLocaleId(),
				AccountStateFlag.LOAN_REJECTED, AccountTypes.LOANACCOUNT);
		assertNotNull(flagNameForLoan);
	}

	public void testGetStatusList() throws Exception {
		getInitialCustomersAndAccounts();

		AccountStateMachines.getInstance().initialize(
				TestObjectFactory.getUserContext().getLocaleId(),
				center.getOffice().getOfficeId(), AccountTypes.SAVINGSACCOUNT,
				null);
		List<AccountStateEntity> statusListForSavings = service.getStatusList(
				savingsBO.getAccountState(), AccountTypes.SAVINGSACCOUNT,
				TestObjectFactory.getUserContext().getLocaleId());
		assertEquals(2, statusListForSavings.size());

		AccountStateMachines.getInstance()
				.initialize(TestObjectFactory.getUserContext().getLocaleId(),
						group.getOffice().getOfficeId(),
						AccountTypes.LOANACCOUNT, null);
		List<AccountStateEntity> statusListForLoan = service.getStatusList(
				accountBO.getAccountState(), AccountTypes.LOANACCOUNT,
				TestObjectFactory.getUserContext().getLocaleId());
		assertEquals(2, statusListForLoan.size());
	}

	private AccountBO getLoanAccount() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}

	private AccountBO getLoanAccountWithAllTypesOfFees() {
		accountBO = getLoanAccount();

		LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountBO
				.getAccountActionDate(Short.valueOf("1"));

		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		AccountFeesEntity accountUpfrontFee = new AccountFeesEntity(accountBO,
				upfrontFee, new Double("20.0"), FeeStatus.ACTIVE.getValue(),
				null, loanScheduleEntity.getActionDate());
		TestAccountFeesEntity.addAccountFees(accountUpfrontFee, accountBO);
		AccountFeesActionDetailEntity accountUpfrontFeesaction = new LoanFeeScheduleEntity(
				loanScheduleEntity, upfrontFee, accountUpfrontFee, new Money(
						"20.0"));
		loanScheduleEntity.addAccountFeesAction(accountUpfrontFeesaction);
		TestObjectFactory.updateObject(accountBO);

		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		loanScheduleEntity = (LoanScheduleEntity) accountBO
				.getAccountActionDate(Short.valueOf("1"));
		FeeBO timeOfDisbursmentFees = TestObjectFactory.createOneTimeAmountFee(
				"Disbursment Fee", FeeCategory.LOAN, "30",
				FeePayment.TIME_OF_DISBURSMENT);
		AccountFeesEntity accountDisbursmentFee = new AccountFeesEntity(
				accountBO, timeOfDisbursmentFees, new Double("30.0"),
				FeeStatus.ACTIVE.getValue(), null, loanScheduleEntity
						.getActionDate());
		TestAccountFeesEntity.addAccountFees(accountDisbursmentFee, accountBO);
		AccountFeesActionDetailEntity accountDisbursmentFeesaction = new LoanFeeScheduleEntity(
				loanScheduleEntity, timeOfDisbursmentFees,
				accountDisbursmentFee, new Money("30.0"));
		loanScheduleEntity.addAccountFeesAction(accountDisbursmentFeesaction);
		TestObjectFactory.updateObject(accountBO);

		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		loanScheduleEntity = (LoanScheduleEntity) accountBO
				.getAccountActionDate(Short.valueOf("1"));
		FeeBO firstLoanRepaymentFee = TestObjectFactory.createOneTimeAmountFee(
				"First Loan Repayment Fee", FeeCategory.LOAN, "40",
				FeePayment.TIME_OF_FIRSTLOANREPAYMENT);
		AccountFeesEntity accountFirstLoanRepaymentFee = new AccountFeesEntity(
				accountBO, firstLoanRepaymentFee, new Double("40.0"),
				FeeStatus.ACTIVE.getValue(), null, loanScheduleEntity
						.getActionDate());
		TestAccountFeesEntity.addAccountFees(accountFirstLoanRepaymentFee, accountBO);
		AccountFeesActionDetailEntity accountTimeOfFirstLoanRepaymentFeesaction = new LoanFeeScheduleEntity(
				loanScheduleEntity, firstLoanRepaymentFee,
				accountFirstLoanRepaymentFee, new Money("40.0"));
		loanScheduleEntity
				.addAccountFeesAction(accountTimeOfFirstLoanRepaymentFeesaction);
		TestObjectFactory.updateObject(accountBO);

		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "200", RecurrenceType.WEEKLY,
				Short.valueOf("1"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(accountBO,
				periodicFee, new Double("200.0"),
				FeeStatus.INACTIVE.getValue(), null, null);
		TestAccountFeesEntity.addAccountFees(accountPeriodicFee, accountBO);
		TestObjectFactory.updateObject(accountBO);

		return accountBO;
	}

	private CustomerAccountBO getCustomerAccountWithAllTypesOfFees() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		CustomerAccountBO customerAccountBO = center.getCustomerAccount();

		CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) customerAccountBO
				.getAccountActionDate(Short.valueOf("1"));

		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.CENTER, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		AccountFeesEntity accountUpfrontFee = new AccountFeesEntity(
				customerAccountBO, upfrontFee, new Double("20.0"),
				FeeStatus.ACTIVE.getValue(), null, customerScheduleEntity
						.getActionDate());
		TestAccountFeesEntity.addAccountFees(accountUpfrontFee, customerAccountBO);
		AccountFeesActionDetailEntity accountUpfrontFeesaction = new CustomerFeeScheduleEntity(
				customerScheduleEntity, upfrontFee, accountUpfrontFee,
				new Money("20.0"));
		customerScheduleEntity.addAccountFeesAction(accountUpfrontFeesaction);
		TestObjectFactory.updateObject(center);

		customerAccountBO = center.getCustomerAccount();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.ALLCUSTOMERS, "200",
				RecurrenceType.WEEKLY, Short.valueOf("1"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(
				customerAccountBO, periodicFee, new Double("200.0"),
				FeeStatus.INACTIVE.getValue(), null, null);
		TestAccountFeesEntity.addAccountFees(accountPeriodicFee, customerAccountBO);
		TestObjectFactory.updateObject(center);

		return customerAccountBO;
	}

	private void incrementInstallmentDate(AccountBO accountBO,
			int numberOfDays, Short installmentId) {
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			if (accountActionDateEntity.getInstallmentId()
					.equals(installmentId)) {
				Calendar dateCalendar = new GregorianCalendar();
				dateCalendar.setTimeInMillis(accountActionDateEntity
						.getActionDate().getTime());
				int year = dateCalendar.get(Calendar.YEAR);
				int month = dateCalendar.get(Calendar.MONTH);
				int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
				dateCalendar = new GregorianCalendar(year, month, day
						+ numberOfDays);
				accountActionDateEntity.setActionDate(new java.sql.Date(
						dateCalendar.getTimeInMillis()));
				break;
			}
		}
	}

	private SavingsBO createSavingsAccount(String offeringName, String shortName)
			throws Exception {
		SavingsOfferingBO savingsOffering = new SavingsTestHelper()
				.createSavingsOffering(offeringName, shortName);
		return TestObjectFactory.createSavingsAccount("000100000000017",
				center, AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, new Date(
						System.currentTimeMillis()), savingsOffering);
	}

	private void createCustomers() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
	}

	private AccountBO getLoanAccount(CustomerBO customerBO) {
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customerBO,
				AccountState.LOANACC_PARTIALAPPLICATION.getValue(), new Date(
						System.currentTimeMillis()), loanOffering);
	}

	private void getInitialCustomersAndAccounts() throws Exception {
		createCustomers();
		accountBO = getLoanAccount(group);
		savingsBO = createSavingsAccount("fsaf6", "ads6");
		HibernateUtil.closeSession();
		fetchAccounts();
	}

	private void fetchAccounts() {
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		savingsBO = (SavingsBO) TestObjectFactory.getObject(SavingsBO.class,
				savingsBO.getAccountId());
		group = (GroupBO) TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
	}
}
