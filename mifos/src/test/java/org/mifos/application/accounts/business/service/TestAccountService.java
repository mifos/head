/**
 * 
 */
package org.mifos.application.accounts.business.service;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.ApplicableCharge;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerFeeScheduleEntity;
import org.mifos.application.customer.business.CustomerScheduleEntity;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountService extends MifosIntegrationTest {

	public TestAccountService() throws SystemException, ApplicationException {
        super();
    }

    protected AccountBO accountBO = null;

	protected SavingsBO savingsBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	protected AccountPersistence accountPersistence;

	private AccountBusinessService service;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		accountPersistence = new AccountPersistence();
		service = new AccountBusinessService();
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			TestObjectFactory.cleanUp(accountBO);
			TestObjectFactory.cleanUp(savingsBO);
			TestObjectFactory.cleanUp(group);
			TestObjectFactory.cleanUp(center);
			accountPersistence = null;
		} catch (Exception e) {
			// TODO Whoops, cleanup didnt work, reset db
			TestDatabase.resetMySQLDatabase();
		}		
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetTrxnHistory() throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		Date currentDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount();
		LoanBO loan = (LoanBO) accountBO;

		UserContext uc = TestUtils.makeUser();
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData accountPaymentDataView = TestObjectFactory
				.getLoanAccountPaymentData(accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(100), null,
						loan.getPersonnel(), "receiptNum", Short.valueOf("1"),
						currentDate, currentDate);

		loan.applyPaymentWithPersist(accountPaymentDataView);
		TestObjectFactory.flushandCloseSession();
		loan = TestObjectFactory.getObject(LoanBO.class, loan
				.getAccountId());
		loan.setUserContext(uc);

		List<TransactionHistoryView> trxnHistlist = accountBusinessService
				.getTrxnHistory(loan, uc);
		Collections.sort(trxnHistlist);
		assertNotNull("Account TrxnHistoryView list object should not be null",
				trxnHistlist);
		assertTrue(
				"Account TrxnHistoryView list object Size should be greater than zero",
				trxnHistlist.size() > 0);
		for(TransactionHistoryView view : trxnHistlist) {
			assertEquals("100.0",view.getBalance());
			assertNotNull(view.getClientName());
			assertEquals("-",view.getDebit());
			assertEquals("100.0",view.getCredit());
			assertNotNull(view.getGlcode());
			assertEquals("-",view.getNotes());
			assertNotNull(view.getPostedBy());
			assertNotNull(view.getType());
			assertNotNull(view.getUserPrefferedPostedDate());
			assertNotNull(view.getUserPrefferedTransactionDate());
			assertNotNull(view.getAccountTrxnId());
			assertNull(view.getLocale());
			assertNotNull(view.getPaymentId());
			assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(),DateUtils.getDateWithoutTimeStamp(view.getPostedDate().getTime()));
			assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(),DateUtils.getDateWithoutTimeStamp(view.getTransactionDate().getTime()));
			break;
		}
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());

	}

	public void testGetAccountAction() throws Exception {
		AccountBusinessService service = new AccountBusinessService();
		AccountActionEntity accountaction = service.getAccountAction(
				AccountActionTypes.SAVINGS_DEPOSIT.getValue(), Short.valueOf("1"));
		assertNotNull(accountaction);
		assertEquals(Short.valueOf("1"), accountaction.getLocaleId());
	}

	public void testGetAppllicableFees() throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getCustomer(center.getCustomerId());
		group = TestObjectFactory.getCustomer(group.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		UserContext uc = TestUtils.makeUser();
		List<ApplicableCharge> applicableChargeList = accountBusinessService
				.getAppllicableFees(accountBO.getAccountId(), uc);
		assertEquals(2, applicableChargeList.size());
	}

	public void testGetAppllicableFeesForInstallmentStartingOnCurrentDate()
			throws Exception {
		AccountBusinessService accountBusinessService = new AccountBusinessService();
		accountBO = getLoanAccountWithAllTypesOfFees();
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getCustomer(center.getCustomerId());
		group = TestObjectFactory.getCustomer(group.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		UserContext uc = TestUtils.makeUser();
		List<ApplicableCharge> applicableChargeList = accountBusinessService
				.getAppllicableFees(accountBO.getAccountId(), uc);
		assertEquals(4, applicableChargeList.size());
		for (ApplicableCharge applicableCharge : applicableChargeList) {
			if (applicableCharge.getFeeName().equalsIgnoreCase("Upfront Fee")) {
				assertEquals(new Money("20.0"), new Money(applicableCharge
						.getAmountOrRate()));
				assertNotNull(applicableCharge.getFormula());
				assertNull(applicableCharge.getPeriodicity());
			} else if (applicableCharge.getFeeName().equalsIgnoreCase(
					"Periodic Fee")) {
				assertEquals(new Money("200.0"), new Money(applicableCharge
						.getAmountOrRate()));
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
				AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue(),null,"");
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		center = TestObjectFactory.getCustomer(center.getCustomerId());
		group = TestObjectFactory.getCustomer(group.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		UserContext uc = TestUtils.makeUser();
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
				assertEquals(new Money("200.0"), new Money(applicableCharge
						.getAmountOrRate()));
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
		center = TestObjectFactory.getCustomer(center.getCustomerId());
		UserContext uc = TestUtils.makeUser();
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
				assertEquals(new Money("200.0"), new Money(applicableCharge
						.getAmountOrRate()));
				assertNull(applicableCharge.getFormula());
				assertNotNull(applicableCharge.getPeriodicity());
			} else if (applicableCharge.getFeeName().equalsIgnoreCase(
					"Mainatnence Fee")) {
				assertFalse(true);
			}
		}
	}

	public void testGetStatusName() throws Exception {
		AccountStateMachines.getInstance().initialize(Short.valueOf("1"),
				Short.valueOf("1"), AccountTypes.SAVINGS_ACCOUNT, null);
		String statusNameForSavings = service.getStatusName(Short.valueOf("1"),
				AccountState.SAVINGS_PARTIAL_APPLICATION,
				AccountTypes.SAVINGS_ACCOUNT);
		assertNotNull(statusNameForSavings);

		AccountStateMachines.getInstance().initialize(Short.valueOf("1"),
				Short.valueOf("1"), AccountTypes.LOAN_ACCOUNT, null);
		String statusNameForLoan = service.getStatusName(Short.valueOf("1"),
				AccountState.LOAN_PARTIAL_APPLICATION,
				AccountTypes.LOAN_ACCOUNT);
		assertNotNull(statusNameForLoan);
	}

	public void testGetFlagName() throws Exception {
		AccountStateMachines.getInstance().initialize(Short.valueOf("1"),
				Short.valueOf("1"), AccountTypes.SAVINGS_ACCOUNT, null);
		String flagNameForSavings = service.getFlagName(Short.valueOf("1"),
				AccountStateFlag.SAVINGS_REJECTED, AccountTypes.SAVINGS_ACCOUNT);
		assertNotNull(flagNameForSavings);

		AccountStateMachines.getInstance().initialize(Short.valueOf("1"),
				Short.valueOf("1"), AccountTypes.LOAN_ACCOUNT, null);
		String flagNameForLoan = service.getFlagName(Short.valueOf("1"),
				AccountStateFlag.LOAN_REJECTED, AccountTypes.LOAN_ACCOUNT);
		assertNotNull(flagNameForLoan);
		HibernateUtil.closeSession();
	}

	public void testGetStatusList() throws Exception {
		AccountStateMachines.getInstance().initialize(Short.valueOf("1"),
				Short.valueOf("1"), AccountTypes.SAVINGS_ACCOUNT, null);
		List<AccountStateEntity> statusListForSavings = service.getStatusList(
				new AccountStateEntity(AccountState.SAVINGS_PARTIAL_APPLICATION),
				AccountTypes.SAVINGS_ACCOUNT, TestUtils.makeUser()
						.getLocaleId());
		assertEquals(2, statusListForSavings.size());

		AccountStateMachines.getInstance().initialize(Short.valueOf("1"),
				Short.valueOf("1"), AccountTypes.LOAN_ACCOUNT, null);
		List<AccountStateEntity> statusListForLoan = service
				.getStatusList(new AccountStateEntity(
						AccountState.LOAN_PARTIAL_APPLICATION),
						AccountTypes.LOAN_ACCOUNT, Short.valueOf("1"));
		assertEquals(2, statusListForLoan.size());
		HibernateUtil.closeSession();
	}
	
	public void testRetrieveCustomFieldsDefinition() throws Exception {
		List<CustomFieldDefinitionEntity> customFields = service
				.retrieveCustomFieldsDefinition(EntityType.LOAN);
		assertNotNull(customFields);
		assertEquals(TestConstants.LOAN_CUSTOMFIELDS_NUMBER, customFields
				.size());
	}

	public void testRetrieveCustomFieldsDefinitionForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.retrieveCustomFieldsDefinition(EntityType.LOAN);
			fail();
		} catch (ServiceException e) {
			assertEquals("exception.framework.ApplicationException", e.getKey());
		} finally {
			HibernateUtil.closeSession();
		}
	}

	private AccountBO getLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", group, 
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate,
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

		accountBO = TestObjectFactory.getObject(AccountBO.class,
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

		accountBO = TestObjectFactory.getObject(AccountBO.class,
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

		accountBO = TestObjectFactory.getObject(AccountBO.class,
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
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
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
				TestLoanBO.setActionDate(accountActionDateEntity,new java.sql.Date(
						dateCalendar.getTimeInMillis()));
				break;
			}
		}
	}
}
