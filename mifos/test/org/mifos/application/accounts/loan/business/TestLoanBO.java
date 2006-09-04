package org.mifos.application.accounts.loan.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.GracePeriodTypeConstants;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidUserException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanBO extends MifosTestCase {
	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	private CustomerBO client = null;

	private AccountPersistanceService accountPersistanceService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		accountPersistanceService = new AccountPersistanceService();
	}

	@Override
	protected void tearDown() throws Exception {
		if(accountBO != null)
			accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		if(group != null)
			group = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,
				group.getCustomerId());
		if(center != null)
			center = (CustomerBO) HibernateUtil.getSessionTL().get(
				CustomerBO.class, center.getCustomerId());
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);

		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testGetTotalRepayAmountForCurrentDateBeforeFirstInstallment() {
		accountBO = getLoanAccount();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		changeFirstInstallmentDateToNextDate(accountBO);
		Money totalRepaymentAmount = new Money();
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				totalRepaymentAmount = totalRepaymentAmount
						.add(accountActionDateEntity.getTotalDueWithFees());
			} else {
				totalRepaymentAmount = totalRepaymentAmount
						.add(accountActionDateEntity.getPrincipal());
			}
		}
		assertEquals(totalRepaymentAmount, ((LoanBO) accountBO)
				.getTotalEarlyRepayAmount());
	}

	public void testGetTotalRepayAmountForCurrentDateSameAsInstallmentDate() {
		accountBO = getLoanAccount();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		Money totalRepaymentAmount = new Money();
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				totalRepaymentAmount = totalRepaymentAmount
						.add(accountActionDateEntity.getTotalDueWithFees());
			} else {
				totalRepaymentAmount = totalRepaymentAmount
						.add(accountActionDateEntity.getPrincipal());
			}
		}
		assertEquals(totalRepaymentAmount, ((LoanBO) accountBO)
				.getTotalEarlyRepayAmount());
	}

	public void testGetTotalRepayAmountForCurrentDateLiesBetweenInstallmentDates() {
		accountBO = getLoanAccount();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		changeFirstInstallmentDate(accountBO);
		Money totalRepaymentAmount = new Money();
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1"))
					|| accountActionDateEntity.getInstallmentId().equals(
							Short.valueOf("2"))) {
				totalRepaymentAmount = totalRepaymentAmount
						.add(accountActionDateEntity.getTotalDueWithFees());
			} else {
				totalRepaymentAmount = totalRepaymentAmount
						.add(accountActionDateEntity.getPrincipal());
			}
		}
		assertEquals(totalRepaymentAmount, ((LoanBO) accountBO)
				.getTotalEarlyRepayAmount());
	}

	public void testMakeEarlyRepaymentForCurrentDateSameAsInstallmentDate()
			throws Exception {
		accountBO = getLoanAccount();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		changeFirstInstallmentDate(accountBO);
		LoanBO loanBO = (LoanBO) accountBO;
		UserContext uc = TestObjectFactory.getUserContext();
		Money totalRepaymentAmount = loanBO.getTotalEarlyRepayAmount();
		loanBO.makeEarlyRepayment(loanBO.getTotalEarlyRepayAmount(), null,
				null, "1", uc.getId());

		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		assertEquals(accountBO.getAccountState().getId(), new Short(
				AccountStates.LOANACC_OBLIGATIONSMET));
		LoanSummaryEntity loanSummaryEntity = loanBO.getLoanSummary();
		assertEquals(totalRepaymentAmount, loanSummaryEntity.getPrincipalPaid()
				.add(loanSummaryEntity.getFeesPaid()).add(
						loanSummaryEntity.getInterestPaid()).add(
						loanSummaryEntity.getPenaltyPaid()));
	}

	public void testMakeEarlyRepaymentForCurrentDateLiesBetweenInstallmentDates()
			throws Exception {
		accountBO = getLoanAccount();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loanBO = (LoanBO) accountBO;
		UserContext uc = TestObjectFactory.getUserContext();
		Money totalRepaymentAmount = loanBO.getTotalEarlyRepayAmount();
		loanBO.makeEarlyRepayment(loanBO.getTotalEarlyRepayAmount(), null,
				null, "1", uc.getId());

		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		assertEquals(accountBO.getAccountState().getId(), new Short(
				AccountStates.LOANACC_OBLIGATIONSMET));
		LoanSummaryEntity loanSummaryEntity = loanBO.getLoanSummary();
		assertEquals(totalRepaymentAmount, loanSummaryEntity.getPrincipalPaid()
				.add(loanSummaryEntity.getFeesPaid()).add(
						loanSummaryEntity.getInterestPaid()).add(
						loanSummaryEntity.getPenaltyPaid()));
	}

	public void testUpdateTotalFeeAmount() {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 1);
		LoanBO loanBO = (LoanBO) accountBO;
		LoanSummaryEntity loanSummaryEntity = loanBO.getLoanSummary();
		Money orignalFeesAmount = loanSummaryEntity.getOriginalFees();
		loanBO.updateTotalFeeAmount(new Money("20"));
		assertEquals(loanSummaryEntity.getOriginalFees(), (orignalFeesAmount
				.subtract(new Money("20"))));
	}

	public void testDisburseLoanWithFeeAtDisbursement()
			throws AccountException, SystemException,
			RepaymentScheduleException, FinancialException {
		Date startDate = new Date(System.currentTimeMillis());
		Money miscFee = new Money("20");
		Money miscPenalty = new Money("50");
		accountBO = getLoanAccountWithMiscFeeAndPenalty(Short.valueOf("3"),
				startDate, 1, miscFee, miscPenalty);

		// disburse loan

		((LoanBO) accountBO).disburseLoan("1234", startDate,
				Short.valueOf("1"), accountBO.getPersonnel(), startDate, Short
						.valueOf("1"));
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		session.save(accountBO);
		HibernateUtil.getTransaction().commit();

		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				assertEquals(miscPenalty, accountActionDateEntity
						.getMiscPenalty());
				assertEquals(miscFee, accountActionDateEntity.getMiscFee());
				break;
			}
		}

		Set<AccountPaymentEntity> accountpayments = accountBO
				.getAccountPayments();
		assertEquals(1, accountpayments.size());
		for (AccountPaymentEntity entity : accountpayments) {

			assertEquals("1234", entity.getReceiptNumber());

			// asssert loan trxns

			Set<AccountTrxnEntity> accountTrxns = entity.getAccountTrxns();
			assertEquals(2, accountTrxns.size());
			for (AccountTrxnEntity accountTrxn : accountTrxns) {

				if (accountTrxn.getAccountActionEntity().getId() == AccountConstants.ACTION_FEE_REPAYMENT) {
					assertEquals(30.0, accountTrxn.getAmount()
							.getAmountDoubleValue());
					// it should have two feetrxn's
					Set<FeesTrxnDetailEntity> feesTrxnDetails = ((LoanTrxnDetailEntity) accountTrxn)
							.getFeesTrxnDetails();
					assertEquals(2, feesTrxnDetails.size());
				}
				if (accountTrxn.getAccountActionEntity().getId() == AccountConstants.ACTION_DISBURSAL)
					assertEquals(300.0, accountTrxn.getAmount()
							.getAmountDoubleValue());

				assertEquals(accountBO.getAccountId(), accountTrxn.getAccount()
						.getAccountId());

			}

			// check loan summary fee paid should be updated
			LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
					.getLoanSummary();
			assertEquals(30.0, loanSummaryEntity.getFeesPaid()
					.getAmountDoubleValue());
			// check the loan object for the disbursal date
			assertEquals(startDate, ((LoanBO) accountBO).getDisbursementDate());
			// check new account state
			assertEquals(AccountStates.LOANACC_ACTIVEINGOODSTANDING,
					((LoanBO) accountBO).getAccountState().getId().shortValue());
		}
		accountBO = (AccountBO) TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
	}

	public void testDisbursalLoanNoFeeOrInterestAtDisbursal()
			throws AccountException, SystemException,
			RepaymentScheduleException, FinancialException {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 3);

		// disburse loan

		((LoanBO) accountBO).disburseLoan("1234", startDate,
				Short.valueOf("1"), accountBO.getPersonnel(), startDate, Short
						.valueOf("1"));
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		session.save(accountBO);
		HibernateUtil.getTransaction().commit();
		((LoanBO) accountBO).setLoanMeeting(null);
		Set<AccountPaymentEntity> accountpayments = accountBO
				.getAccountPayments();
		assertEquals(1, accountpayments.size());
		for (AccountPaymentEntity entity : accountpayments) {

			assertEquals("1234", entity.getReceiptNumber());
			// asssert loan trxns
			Set<AccountTrxnEntity> accountTrxns = entity.getAccountTrxns();
			assertEquals(1, accountTrxns.size());
			for (AccountTrxnEntity accountTrxn : accountTrxns) {
				if (accountTrxn.getAccountActionEntity().getId() == AccountConstants.ACTION_DISBURSAL)
					assertEquals(300.0, accountTrxn.getAmount()
							.getAmountDoubleValue());
				assertEquals(accountBO.getAccountId(), accountTrxn.getAccount()
						.getAccountId());

			}
		}

	}

	public void testDisbursalLoanWithInterestDeductedAtDisbursal()
			throws AccountException, SystemException,
			RepaymentScheduleException, FinancialException {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 2);
		int statusChangeHistorySize = accountBO.getAccountStatusChangeHistory()
				.size();

		((LoanBO) accountBO).disburseLoan("1234", startDate,
				Short.valueOf("1"), accountBO.getPersonnel(), startDate, Short
						.valueOf("1"));
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		session.save(accountBO);
		HibernateUtil.getTransaction().commit();
		((LoanBO) accountBO).setLoanMeeting(null);
		Set<AccountPaymentEntity> accountpayments = accountBO
				.getAccountPayments();
		assertEquals(1, accountpayments.size());

		AccountPaymentEntity entity = accountpayments.iterator().next();
		assertEquals("1234", entity.getReceiptNumber());
		// asssert loan trxns
		Set<AccountTrxnEntity> accountTrxns = entity.getAccountTrxns();
		assertEquals(2, accountTrxns.size());
		int finTransaction = 0;
		for (AccountTrxnEntity accountTrxn : accountTrxns) {
			finTransaction += accountTrxn.getFinancialTransactions().size();
			if (accountTrxn.getAccountActionEntity().getId() == AccountConstants.ACTION_FEE_REPAYMENT) {
				assertEquals(40.0, accountTrxn.getAmount()
						.getAmountDoubleValue());
				// it should have two feetrxn's
				Set<FeesTrxnDetailEntity> feesTrxnDetails = ((LoanTrxnDetailEntity) accountTrxn)
						.getFeesTrxnDetails();
				assertEquals(2, feesTrxnDetails.size());
			}
			if (accountTrxn.getAccountActionEntity().getId() == AccountConstants.ACTION_DISBURSAL)
				assertEquals(300.0, accountTrxn.getAmount()
						.getAmountDoubleValue());

			assertEquals(accountBO.getAccountId(), accountTrxn.getAccount()
					.getAccountId());

		}
		assertEquals(10, finTransaction);
		// check loan summary fee paid should be updated

		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(40.0, loanSummaryEntity.getFeesPaid()
				.getAmountDoubleValue());

		// check new account state
		assertEquals(AccountStates.LOANACC_ACTIVEINGOODSTANDING,
				((LoanBO) accountBO).getAccountState().getId().shortValue());
		assertEquals(statusChangeHistorySize + 1, accountBO
				.getAccountStatusChangeHistory().size());

	}

	public void testDisbursalLoanRegeneteRepaymentScheduleFailure()
			throws AccountException, SystemException, FinancialException {

		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 3);

		// disburse loan
		Calendar disbDate = new GregorianCalendar();
		disbDate.setTimeInMillis(startDate.getTime());
		disbDate.roll(Calendar.DATE, 1);
		Date disbursalDate = new Date(disbDate.getTimeInMillis());
		try {
			((LoanBO) accountBO).disburseLoan("1234", disbursalDate, Short
					.valueOf("1"), accountBO.getPersonnel(), startDate, Short
					.valueOf("1"));

			assertEquals(true, false);
		} catch (ApplicationException rse) {
			assertEquals(true, true);
		} finally {
			Session session = HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();
			((LoanBO) accountBO).setLoanMeeting(null);
			session.update(accountBO);
			HibernateUtil.getTransaction().commit();

		}

	}

	public void testDisbursalLoanRegeneteRepaymentSchedule()
			throws AccountException, SystemException, FinancialException,
			InterruptedException {

		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 3);

		Set<AccountActionDateEntity> intallments = accountBO
				.getAccountActionDates();
		AccountActionDateEntity firstInstallment = null;
		for (AccountActionDateEntity entity : intallments) {
			if (entity.getInstallmentId().intValue() == 1) {
				firstInstallment = entity;
				break;
			}
		}
		Calendar disbursalDate = new GregorianCalendar();
		disbursalDate.setTimeInMillis(firstInstallment.getActionDate()
				.getTime());
		Calendar cal = new GregorianCalendar(disbursalDate.get(Calendar.YEAR),
				disbursalDate.get(Calendar.MONTH), disbursalDate
						.get(Calendar.DATE), 0, 0);
		((LoanBO) accountBO).disburseLoan("1234", cal.getTime(), Short
					.valueOf("1"), accountBO.getPersonnel(), startDate, Short
					.valueOf("1"));
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		((LoanBO) accountBO).setLoanMeeting(null);
		session.update(accountBO);
		HibernateUtil.getTransaction().commit();
		assertEquals(true, true);
	}

	public void testGetTotalAmountDueForCurrentDateMeeting() {
		accountBO = getLoanAccount();
		assertEquals(((LoanBO) accountBO).getTotalAmountDue()
				.getAmountDoubleValue(), 212.0);
	}

	public void testGetTotalAmountDueForSingleInstallment() {
		accountBO = getLoanAccount();

		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));

		accountBO = saveAndFetch(accountBO);
		assertEquals(((LoanBO) accountBO).getTotalAmountDue()
				.getAmountDoubleValue(), 424.0);
	}

	public void testGetTotalAmountDueWithPayment() {
		accountBO = getLoanAccount();

		LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountBO
				.getAccountActionDate((short) 1);

		accountActionDateEntity.setPrincipalPaid(new Money("20.0"));
		accountActionDateEntity.setInterestPaid(new Money("10.0"));
		accountActionDateEntity.setPenaltyPaid(new Money("5.0"));
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		accountBO = saveAndFetch(accountBO);
		assertEquals(((LoanBO) accountBO).getTotalAmountDue()
				.getAmountDoubleValue(), 389.0);
	}

	public void testGetTotalAmountDueWithPaymentDone() {
		accountBO = getLoanAccount();

		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID.getValue());

		accountBO = saveAndFetch(accountBO);

		assertEquals(((LoanBO) accountBO).getTotalAmountDue()
				.getAmountDoubleValue(), 212.0);
	}

	public void testGetTotalAmountDueForTwoInstallments() {
		accountBO = getLoanAccount();
		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(2));
		AccountActionDateEntity accountActionDateEntity2 = accountBO
				.getAccountActionDate((short) 2);

		accountActionDateEntity2.setActionDate(offSetCurrentDate(1));

		accountBO = saveAndFetch(accountBO);

		assertEquals(((LoanBO) accountBO).getTotalAmountDue()
				.getAmountDoubleValue(), 636.0);
	}

	public void testGetOustandingBalance() {
		accountBO = getLoanAccount();
		assertEquals(((LoanBO) accountBO).getLoanSummary()
				.getOustandingBalance().getAmountDoubleValue(), 336.0);
	}

	public void testGetOustandingBalancewithPayment() {
		accountBO = getLoanAccount();
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();

		loanSummaryEntity.setPrincipalPaid(new Money("20.0"));
		loanSummaryEntity.setInterestPaid(new Money("10.0"));

		accountBO = saveAndFetch(accountBO);

		assertEquals(((LoanBO) accountBO).getLoanSummary()
				.getOustandingBalance().getAmountDoubleValue(), 306.0);
	}

	public void testGetNextMeetingDateAsCurrentDate() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day);
		accountBO = new AccountPersistanceService().getAccount(accountBO
				.getAccountId());
		assertEquals(((LoanBO) accountBO).getNextMeetingDate().toString(),
				new java.sql.Date(currentDateCalendar.getTimeInMillis())
						.toString());
	}

	public void testGetNextMeetingDateAsFutureDate() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate(Short.valueOf("1"));
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - 1);
		accountActionDateEntity.setActionDate(new java.sql.Date(
				currentDateCalendar.getTimeInMillis()));
		new AccountPersistanceService().updateAccount(accountBO);
		accountBO = new AccountPersistanceService().getAccount(accountBO
				.getAccountId());
		assertEquals(((LoanBO) accountBO).getNextMeetingDate().toString(),
				accountBO.getAccountActionDate(Short.valueOf("2"))
						.getActionDate().toString());
	}

	public void testGetTotalAmountInArrearsForCurrentDateMeeting() {
		accountBO = getLoanAccount();
		assertEquals(((LoanBO) accountBO).getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
	}

	public void testGetTotalAmountInArrearsForSingleInstallmentDue() {
		accountBO = getLoanAccount();
		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		accountBO = saveAndFetch(accountBO);
		assertEquals(((LoanBO) accountBO).getTotalAmountInArrears()
				.getAmountDoubleValue(), 212.0);
	}

	public void testGetTotalAmountInArrearsWithPayment() {
		accountBO = getLoanAccount();

		LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountBO
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setPrincipalPaid(new Money("20.0"));
		accountActionDateEntity.setInterestPaid(new Money("10.0"));
		accountActionDateEntity.setPenaltyPaid(new Money("5.0"));
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));

		accountBO = saveAndFetch(accountBO);
		assertEquals(((LoanBO) accountBO).getTotalAmountInArrears()
				.getAmountDoubleValue(), 177.0);
	}

	public void testGetTotalAmountInArrearsWithPaymentDone() {
		accountBO = getLoanAccount();

		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate(Short.valueOf("1"));
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));
		accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID.getValue());
		accountBO = saveAndFetch(accountBO);
		assertEquals(((LoanBO) accountBO).getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
	}

	public void testGetTotalAmountDueForTwoInstallmentsDue() {
		accountBO = getLoanAccount();

		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setActionDate(offSetCurrentDate(2));
		AccountActionDateEntity accountActionDateEntity2 = accountBO
				.getAccountActionDate((short) 2);
		accountActionDateEntity2.setActionDate(offSetCurrentDate(1));

		accountBO = saveAndFetch(accountBO);

		assertEquals(((LoanBO) accountBO).getTotalAmountInArrears()
				.getAmountDoubleValue(), 424.0);
	}

	public void testChangedStatusForLastInstallmentPaid()
			throws AccountException, SystemException {
		accountBO = getLoanAccount();
		Date startDate = new Date(System.currentTimeMillis());
		java.sql.Date offSetDate = offSetCurrentDate(1);
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			accountAction.setActionDate(offSetDate);
		}
		accountBO = saveAndFetch(accountBO);

		List<AccountActionDateEntity> accountActions = new ArrayList<AccountActionDateEntity>();
		accountActions.addAll(accountBO.getAccountActionDates());

		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accountActions, new Money(Configuration.getInstance()
						.getSystemConfig().getCurrency(), "1272.0"), null,
				accountBO.getPersonnel(), "5435345", Short.valueOf("1"),
				startDate, startDate);

		accountBO.applyPayment(paymentData);
		assertEquals(
				"When Last installment is paid the status has been changed to closed",
				((LoanBO) accountBO).getAccountState().getId().toString(),
				String.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));
		accountBO.getAccountPayments().clear();
	}

	public void testHandleArrears() throws ServiceException, AccountException {
		accountBO = getLoanAccount();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		int statusChangeHistorySize = accountBO.getAccountStatusChangeHistory()
				.size();

		((LoanBO) accountBO).handleArrears();
		assertEquals(Short.valueOf(AccountStates.LOANACC_BADSTANDING),
				accountBO.getAccountState().getId());
		assertEquals(statusChangeHistorySize + 1,
				accountBO.getAccountStatusChangeHistory().size());
	}

	public void testChangedStatusOnPayment() throws AccountException,
			SystemException {
		accountBO = getLoanAccount();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		((LoanBO) accountBO).handleArrears();
		assertEquals(
				"The status of account before payment should be active in bad standing",
				Short.valueOf(AccountStates.LOANACC_BADSTANDING), accountBO
						.getAccountState().getId());
		accountBO = applyPaymentandRetrieveAccount();
		assertEquals(
				"The status of account after payment should be active in good standing",
				Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING),
				accountBO.getAccountState().getId());
	}

	public void testIsInterestDeductedAtDisbursement() throws ServiceException {
		accountBO = getLoanAccount();
		LoanBO loan = (LoanBO) accountBO;
		assertEquals(true, loan.isInterestDeductedAtDisbursement());
		loan
				.setInterestDeductedAtDisbursement(false);
		assertEquals(false, loan.isInterestDeductedAtDisbursement());

	}

	public void testWtiteOff() throws Exception {
		try {
		accountBO = getLoanAccount();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loanBO = (LoanBO) accountBO;
		UserContext uc = TestObjectFactory.getUserContext();
		loanBO.setUserContext(uc);
		loanBO.writeOff("Loan Written Off");
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loan = (LoanBO) accountBO;
		for (LoanActivityEntity loanActivityEntity : loan
				.getLoanActivityDetails()) {
			assertEquals(loanActivityEntity.getComments(), "Loan Written Off");
			break;
		}
		assertEquals(accountBO.getAccountState().getId(), new Short(
				AccountStates.LOANACC_WRITTENOFF));
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetAmountTobePaidAtdisburtail() {

		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 2);
		assertEquals(new Money("52"), ((LoanBO) accountBO)
				.getAmountTobePaidAtdisburtail(startDate));

	}

	public void testWaiveFeeChargeDue() throws Exception {
		accountBO = getLoanAccount();
		HibernateUtil.closeSession();
		LoanBO loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
		UserContext userContext = TestObjectFactory.getUserContext();
		loanBO.setUserContext(userContext);
		loanBO.waiveAmountDue(WaiveEnum.FEES);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
		for (AccountActionDateEntity accountAction : loanBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDateEntity
					.getAccountFeesActionDetails()) {
				
				if (accountActionDateEntity.getInstallmentId().equals(
						Short.valueOf("1"))) {
					assertEquals(new Money(), accountFeesActionDetailEntity
							.getFeeAmount());
				} else {
					assertEquals(new Money("100"),
							accountFeesActionDetailEntity.getFeeAmount());
					
				}
			}
		}
	}

	public void testWaiveFeeChargeOverDue() throws Exception {
		accountBO = getLoanAccount();
		HibernateUtil.closeSession();
		LoanBO loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
		calendar.add(calendar.WEEK_OF_MONTH, -1);
		java.sql.Date lastWeekDate = new java.sql.Date(calendar
				.getTimeInMillis());

		Calendar date = new GregorianCalendar();
		date.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
		date.add(date.WEEK_OF_MONTH, -2);
		java.sql.Date twoWeeksBeforeDate = new java.sql.Date(date
				.getTimeInMillis());

		for (AccountActionDateEntity installment : loanBO
				.getAccountActionDates()) {
			if (installment.getInstallmentId().intValue() == 1) {
				installment.setActionDate(lastWeekDate);
			} else if (installment.getInstallmentId().intValue() == 2) {
				installment.setActionDate(twoWeeksBeforeDate);
			}
		}
		TestObjectFactory.updateObject(loanBO);
		HibernateUtil.closeSession();
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
		UserContext userContext = TestObjectFactory.getUserContext();
		loanBO.setUserContext(userContext);
		loanBO.waiveAmountOverDue(WaiveEnum.FEES);
		TestObjectFactory.flushandCloseSession();
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
		for (AccountActionDateEntity accountAction : loanBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
		}

		for (AccountActionDateEntity accountAction : loanBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDateEntity
					.getAccountFeesActionDetails()) {
				if (accountActionDateEntity.getInstallmentId().equals(
						Short.valueOf("1"))
						|| accountActionDateEntity.getInstallmentId().equals(
								Short.valueOf("2"))) {
					assertEquals(new Money(), accountFeesActionDetailEntity
							.getFeeAmount());
				} else
					assertEquals(new Money("100"),
							accountFeesActionDetailEntity.getFeeAmount());
			}
		}
	}

	public void testRegenerateFutureInstallments() throws AccountException, SchedulerException {
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
		AccountActionDateEntity accountActionDateEntity = accountBO
				.getDetailsOfNextInstallment();
		MeetingBO meeting = accountBO.getCustomer().getCustomerMeeting()
				.getMeeting();
		meeting.getMeetingDetails().setRecurAfter(Short.valueOf("2"));
		meeting.setMeetingStartDate(DateUtils
				.getCalendarDate(accountActionDateEntity.getActionDate()
						.getTime()));
		SchedulerIntf scheduler = SchedulerHelper.getScheduler(meeting);
		List<java.util.Date> meetingDates = scheduler.getAllDates(accountBO
				.getApplicableIdsForFutureInstallments().size() + 1);
		((LoanBO) accountBO)
				.regenerateFutureInstallments((short) (accountActionDateEntity
						.getInstallmentId().intValue() + 1));
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				accountBO.getAccountId());
		for (AccountActionDateEntity actionDateEntity : accountBO
				.getAccountActionDates()) {
			if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
						.get(1).getTime()), DateUtils
						.getDateWithoutTimeStamp(actionDateEntity
								.getActionDate().getTime()));
			else if (actionDateEntity.getInstallmentId().equals(
					Short.valueOf("3")))
				assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates
						.get(2).getTime()), DateUtils
						.getDateWithoutTimeStamp(actionDateEntity
								.getActionDate().getTime()));
		}
	}

	public void testRegenerateFutureInstallmentsWithCancelState()
			throws SchedulerException, HibernateException, ServiceException,
			PersistenceException, AccountException {
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
		java.sql.Date intallment2ndDate = null;
		java.sql.Date intallment3ndDate = null;
		for (AccountActionDateEntity actionDateEntity : accountBO
				.getAccountActionDates()) {
			if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
				intallment2ndDate = actionDateEntity.getActionDate();
			else if (actionDateEntity.getInstallmentId().equals(
					Short.valueOf("3")))
				intallment3ndDate = actionDateEntity.getActionDate();
		}
		AccountActionDateEntity accountActionDateEntity = accountBO
				.getDetailsOfNextInstallment();
		MeetingBO meeting = accountBO.getCustomer().getCustomerMeeting()
				.getMeeting();
		meeting.getMeetingDetails().setRecurAfter(Short.valueOf("2"));
		meeting.setMeetingStartDate(DateUtils
				.getCalendarDate(accountActionDateEntity.getActionDate()
						.getTime()));
		SchedulerIntf scheduler = SchedulerHelper.getScheduler(meeting);
		List<java.util.Date> meetingDates = scheduler.getAllDates(accountBO
				.getApplicableIdsForFutureInstallments().size() + 1);
		AccountStateEntity accountStateEntity = new AccountStateEntity(
				AccountStates.LOANACC_CANCEL);
		accountBO.setAccountState(accountStateEntity);
		((LoanBO) accountBO)
				.regenerateFutureInstallments((short) (accountActionDateEntity
						.getInstallmentId().intValue() + 1));
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				accountBO.getAccountId());
		for (AccountActionDateEntity actionDateEntity : accountBO
				.getAccountActionDates()) {
			if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
				assertEquals(DateUtils
						.getDateWithoutTimeStamp(intallment2ndDate.getTime()),
						DateUtils.getDateWithoutTimeStamp(actionDateEntity
								.getActionDate().getTime()));
			else if (actionDateEntity.getInstallmentId().equals(
					Short.valueOf("3")))
				assertEquals(DateUtils
						.getDateWithoutTimeStamp(intallment3ndDate.getTime()),
						DateUtils.getDateWithoutTimeStamp(actionDateEntity
								.getActionDate().getTime()));
		}
	}

	public void testHasPortfolioAtRisk() {
		accountBO = getLoanAccount();
		assertFalse(((LoanBO) accountBO).hasPortfolioAtRisk());
		changeFirstInstallmentDate(accountBO, 31);
		assertTrue(((LoanBO) accountBO).hasPortfolioAtRisk());
	}

	public void testGetRemainingPrincipalAmount() throws AccountException,
			SystemException {
		accountBO = getLoanAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		LoanBO loan = (LoanBO) accountBO;
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(700), null, accountBO
						.getPersonnel(), "receiptNum", Short.valueOf("1"),
				currentDate, currentDate);
		loan.applyPayment(paymentData);

		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		assertEquals(
				"The amount returned for the payment should have been 700",
				700.0, loan.getLastPmntAmnt());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				loan.getAccountId());
		loan = (LoanBO) accountBO;
		loan.getLoanSummary().getOriginalPrincipal().subtract(
				loan.getLoanSummary().getPrincipalPaid());
		assertEquals(loan.getRemainingPrincipalAmount(), loan.getLoanSummary()
				.getOriginalPrincipal().subtract(
						loan.getLoanSummary().getPrincipalPaid()));
	}

	public void testIsAccountActive() throws AccountException, SystemException,
			NumberFormatException, RepaymentScheduleException,
			FinancialException {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 3);
		assertFalse(((LoanBO) accountBO).isAccountActive());

		// disburse loan

		((LoanBO) accountBO).disburseLoan("1234", startDate,
				Short.valueOf("1"), accountBO.getPersonnel(), startDate, Short
						.valueOf("1"));
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		session.save(accountBO);
		HibernateUtil.getTransaction().commit();
		((LoanBO) accountBO).setLoanMeeting(null);
		Set<AccountPaymentEntity> accountpayments = accountBO
				.getAccountPayments();
		assertEquals(1, accountpayments.size());
		for (AccountPaymentEntity entity : accountpayments) {

			assertEquals("1234", entity.getReceiptNumber());
			// asssert loan trxns
			Set<AccountTrxnEntity> accountTrxns = entity.getAccountTrxns();
			assertEquals(1, accountTrxns.size());
			for (AccountTrxnEntity accountTrxn : accountTrxns) {
				if (accountTrxn.getAccountActionEntity().getId() == AccountConstants.ACTION_DISBURSAL)
					assertEquals(300.0, accountTrxn.getAmount()
							.getAmountDoubleValue());
				assertEquals(accountBO.getAccountId(), accountTrxn.getAccount()
						.getAccountId());

			}
		}
		assertTrue(((LoanBO) accountBO).isAccountActive());
	}

	public void testGetNoOfBackDatedPayments() throws AccountException,
			SystemException {
		accountBO = getLoanAccount();
		AccountStateEntity accountStateEntity = new AccountStateEntity(
				AccountStates.LOANACC_BADSTANDING);
		accountBO.setAccountState(accountStateEntity);
		changeInstallmentDate(accountBO, 14, Short.valueOf("1"));
		changeInstallmentDate(accountBO, 14, Short.valueOf("2"));
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();

		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());

		Date startDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = new PaymentData(new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), "212.0"),
				accountBO.getPersonnel(), Short.valueOf("1"), startDate);
		paymentData.setRecieptDate(startDate);
		paymentData.setRecieptNum("5435345");

		accountBO.applyPayment(paymentData);
		TestObjectFactory.flushandCloseSession();

		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());

		assertEquals(Integer.valueOf("2"), ((LoanBO) accountBO)
				.getMissedPaymentCount());
	}


	public void testGetTotalRepayAmountForCustomerPerfHistory()
			throws Exception {
		accountBO = getLoanAccountWithPerformanceHistory();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		changeFirstInstallmentDate(accountBO);
		LoanBO loanBO = (LoanBO) accountBO;
		UserContext uc = TestObjectFactory.getUserContext();
		Money totalRepaymentAmount = loanBO.getTotalEarlyRepayAmount();
		Integer noOfActiveLoans = ((ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory())
				.getNoOfActiveLoans();
		LoanPerformanceHistoryEntity loanPerfHistory = ((LoanBO) accountBO)
				.getPerformanceHistory();
		Integer noOfPayments = loanPerfHistory.getNoOfPayments();
		loanBO.makeEarlyRepayment(loanBO.getTotalEarlyRepayAmount(), null,
				null, "1", uc.getId());
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		loanPerfHistory = ((LoanBO) accountBO).getPerformanceHistory();
		assertEquals(noOfPayments + 1, loanPerfHistory.getNoOfPayments()
				.intValue());

		ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory();
		assertEquals(Integer.valueOf(1), clientPerfHistory.getLoanCycleNumber());
		assertEquals(noOfActiveLoans - 1, clientPerfHistory
				.getNoOfActiveLoans().intValue());
		assertEquals(totalRepaymentAmount, clientPerfHistory
				.getLastLoanAmount());
	}

	public void testWtiteOffForCustomerPerfHistory() throws Exception {
		accountBO = getLoanAccountWithPerformanceHistory();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loanBO = (LoanBO) accountBO;
		UserContext uc = TestObjectFactory.getUserContext();
		loanBO.setUserContext(uc);
		ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory();
		Integer noOfActiveLoans = clientPerfHistory.getNoOfActiveLoans();
		Integer loanCycleNumber = clientPerfHistory.getLoanCycleNumber();
		loanBO.writeOff("Loan Written Off");
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loan = (LoanBO) accountBO;
		clientPerfHistory = (ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory();
		assertEquals(loanCycleNumber - 1, clientPerfHistory
				.getLoanCycleNumber().intValue());
		assertEquals(noOfActiveLoans - 1, clientPerfHistory
				.getNoOfActiveLoans().intValue());
	}

	public void testDisbursalLoanForCustomerPerfHistory()
			throws AccountException, SystemException,
			RepaymentScheduleException, FinancialException {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccountWithPerformanceHistory(Short.valueOf("3"),
				startDate, 3);

		ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory();
		Integer noOfActiveLoans = clientPerfHistory.getNoOfActiveLoans();
		Integer loanCycleNumber = clientPerfHistory.getLoanCycleNumber();
		((LoanBO) accountBO).disburseLoan("1234", startDate,
				Short.valueOf("1"), accountBO.getPersonnel(), startDate, Short
						.valueOf("1"));
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loan = (LoanBO) accountBO;
		assertEquals(noOfActiveLoans + 1, clientPerfHistory
				.getNoOfActiveLoans().intValue());
		assertEquals(loanCycleNumber + 1, clientPerfHistory
				.getLoanCycleNumber().intValue());
		LoanPerformanceHistoryEntity loanPerfHistory = loan
				.getPerformanceHistory();
		assertEquals(getLastInstallmentAccountAction(loan).getActionDate(),
				loanPerfHistory.getLoanMaturityDate());
	}

	public void testHandleArrearsForCustomerPerfHistory()
			throws AccountException, SystemException {
		accountBO = getLoanAccountWithPerformanceHistory();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory();
		Integer noOfActiveLoans = clientPerfHistory.getNoOfActiveLoans();
		((LoanBO) accountBO).handleArrears();
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loan = (LoanBO) accountBO;
		clientPerfHistory = (ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory();
		assertEquals(noOfActiveLoans + 1, clientPerfHistory
				.getNoOfActiveLoans().intValue());
	}

	public void testMakePaymentForCustomerPerfHistory()
			throws AccountException, SystemException {
		accountBO = getLoanAccountWithPerformanceHistory();
		AccountStateEntity accountStateEntity = new AccountStateEntity(
				AccountStates.LOANACC_BADSTANDING);
		accountBO.setAccountState(accountStateEntity);
		TestObjectFactory.updateObject(accountBO);
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory();
		Integer noOfActiveLoans = clientPerfHistory.getNoOfActiveLoans();
		LoanPerformanceHistoryEntity loanPerfHistory = ((LoanBO) accountBO)
				.getPerformanceHistory();
		Integer noOfPayments = loanPerfHistory.getNoOfPayments();
		accountBO = applyPaymentandRetrieveAccount();
		LoanBO loan = (LoanBO) accountBO;
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		clientPerfHistory = (ClientPerformanceHistoryEntity) loan.getCustomer()
				.getPerformanceHistory();
		assertEquals(noOfActiveLoans - 1, clientPerfHistory
				.getNoOfActiveLoans().intValue());
		assertEquals(noOfPayments + 1, loan.getPerformanceHistory()
				.getNoOfPayments().intValue());
	}

	public void testDisbursalLoanForGroupPerfHistory() throws AccountException,
			SystemException, RepaymentScheduleException, FinancialException {
		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccountWithGroupPerformanceHistory(Short
				.valueOf("3"), startDate, 3);

		GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = (GroupPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory();
		((LoanBO) accountBO).disburseLoan("1234", startDate,
				Short.valueOf("1"), accountBO.getPersonnel(), startDate, Short
						.valueOf("1"));
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loan = (LoanBO) accountBO;
		assertEquals(new Money("300.0"), groupPerformanceHistoryEntity
				.getLastGroupLoanAmount());
		LoanPerformanceHistoryEntity loanPerfHistory = loan
				.getPerformanceHistory();
		assertEquals(getLastInstallmentAccountAction(loan).getActionDate(),
				loanPerfHistory.getLoanMaturityDate());
	}

	public void testRoundInstallments() throws AccountException,
			SystemException {
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		((LoanBO) accountBO).getLoanOffering().setPrinDueLastInst(false);
		List<Short> installmentIdList = new ArrayList<Short>();
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			installmentIdList.add(accountActionDateEntity.getInstallmentId());
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1")))
				accountActionDateEntity.setMiscFee(new Money("20.3"));
		}
		((LoanBO) accountBO).roundInstallments(installmentIdList);
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDate = (LoanScheduleEntity) accountAction;
			if (accountActionDate.getInstallmentId().equals(Short.valueOf("1")))
				assertEquals(new Money("132.3"), accountActionDate
						.getTotalDue());
			else
				assertEquals(new Money("112.0"), accountActionDate
						.getTotalDue());
		}
	}

	public void testBuildLoanWithoutLoanOffering()
			throws NumberFormatException, AccountException, Exception {
		createInitialCustomers();
		try {
			LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(), null,
					group, AccountState.LOANACC_APPROVED, new Money("300.0"),
					Short.valueOf("6"), new Date(System.currentTimeMillis()),
					false, 10.0, (short) 0, new Fund(),
					new ArrayList<FeeView>());
			assertFalse("The Loan object is created for null loan offering",
					true);
		} catch (AccountException ae) {
			assertTrue("The Loan object is not created for null loan offering",
					true);
		}
	}
	
	public void testBuildForInactiveLoanOffering() throws NumberFormatException, InvalidUserException, SystemException, ApplicationException {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false,ProductDefinitionConstants.LOANINACTIVE);
		try {
			LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(), loanOffering,
					group, AccountState.LOANACC_APPROVED, new Money("300.0"),
					Short.valueOf("6"), new Date(System.currentTimeMillis()),
					false, 10.0, (short) 0, new Fund(),
					new ArrayList<FeeView>());
			assertFalse("The Loan object is created for inactive loan offering",
					true);
		} catch (AccountException ae) {
			assertTrue("The Loan object is not created for inactive loan offering",
					true);
		}
	}

	public void testBuildLoanWithoutCustomer() throws NumberFormatException,
			AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		try {
			LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
					loanOffering, null, AccountState.LOANACC_APPROVED,
					new Money("300.0"), Short.valueOf("6"), new Date(System
							.currentTimeMillis()), false, 10.0, (short) 0,
					new Fund(), new ArrayList<FeeView>());
			assertFalse("The Loan object is created for null customer", true);
		} catch (AccountException ae) {
			assertTrue("The Loan object is not created for null customer", true);
		}
		TestObjectFactory.removeObject(loanOffering);
	}
	
	public void testBuildForInactiveCustomer() throws NumberFormatException, InvalidUserException, SystemException, ApplicationException {
		createInitialCustomers();
		group.setCustomerStatus(new CustomerStatusEntity(CustomerStatus.GROUP_CLOSED));
		TestObjectFactory.updateObject(group);
		group = (GroupBO)TestObjectFactory.getObject(GroupBO.class,group.getCustomerId());
		
		LoanOfferingBO loanOffering = createLoanOffering(false);
		try {
			LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
					loanOffering, null, AccountState.LOANACC_APPROVED,
					new Money("300.0"), Short.valueOf("6"), new Date(System
							.currentTimeMillis()), false, 10.0, (short) 0,
					new Fund(), new ArrayList<FeeView>());
			assertFalse("The Loan object is created for inactive customer", true);
		} catch (AccountException ae) {
			assertTrue("The Loan object is not created for inactive customer", true);
		}
		TestObjectFactory.removeObject(loanOffering);
		
	}
	
	public void testMeetingNotMatchingForCustomerAndLoanOffering()
			throws NumberFormatException, InvalidUserException,
			SystemException, ApplicationException {
		createInitialCustomers();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(2, 1, 4, 2));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()),
				ProductDefinitionConstants.LOANACTIVE, 300.0, 1.2, Short
						.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("0"), Short.valueOf("1"),
				meeting);
		try {
			LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
					loanOffering, null, AccountState.LOANACC_APPROVED,
					new Money("300.0"), Short.valueOf("6"), new Date(System
							.currentTimeMillis()), false, 10.0, (short) 0,
					new Fund(), new ArrayList<FeeView>());
			assertFalse(
					"The Loan object is created even if meetings do not match",
					true);
		} catch (AccountException ae) {
			assertTrue(
					"The Loan object is not created if meetings do not match",
					true);
		}
		TestObjectFactory.removeObject(loanOffering);
	}
	
	public void testMeetingRecurrenceOfLoanOfferingInMultipleOfCustomer()
			throws NumberFormatException, InvalidUserException,
			SystemException, ApplicationException {
		createInitialCustomers();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 2, 4, 2));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()),
				ProductDefinitionConstants.LOANACTIVE, 300.0, 1.2, Short
						.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("0"), Short.valueOf("1"),
				meeting);
		LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group, AccountState.LOANACC_APPROVED, new Money(
						"300.0"), Short.valueOf("6"), new Date(System
						.currentTimeMillis()), false, 10.0, (short) 0,
				new Fund(), new ArrayList<FeeView>());
		assertTrue(
				"The Loan object is created if meeting recurrence of loan offering is in multiples of customer",
				true);
		TestObjectFactory.removeObject(loanOffering);
	}
	
	public void testBuildLoanWithoutLoanAmount() throws NumberFormatException,
			AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		try {
			LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
					loanOffering, group, AccountState.LOANACC_APPROVED, null,
					Short.valueOf("6"), new Date(System.currentTimeMillis()),
					false, 10.0, (short) 0, new Fund(),
					new ArrayList<FeeView>());
			assertFalse("The Loan object is created for null customer", true);
		} catch (AccountException ae) {
			assertTrue("The Loan object is not created for null customer", true);
		}
		TestObjectFactory.removeObject(loanOffering);
	}
	
	public void testGracePeriodGraterThanMaxNoOfInst()
			throws NumberFormatException, InvalidUserException,
			SystemException, ApplicationException {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		try {
			LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
					loanOffering, group, AccountState.LOANACC_APPROVED, null,
					Short.valueOf("6"), new Date(System.currentTimeMillis()),
					false, 10.0, (short) 5, new Fund(),
					new ArrayList<FeeView>());
			assertFalse(
					"The Loan object is created for grace period greather than max installments",
					true);
		} catch (AccountException ae) {
			assertTrue(
					"The Loan object is not created for grace period greather than max installments",
					true);
		}
		TestObjectFactory.removeObject(loanOffering);
	}
	
	public void testGracePeriodForInterestNotDedAtDisb()
			throws NumberFormatException, AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);

		LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group, AccountState.LOANACC_APPROVED, new Money(
						"300.0"), Short.valueOf("6"), new Date(System
						.currentTimeMillis()), false, 10.0, (short) 1,
				new Fund(), new ArrayList<FeeView>());
		assertEquals(loanOffering.getGracePeriodType().getId(), loan
				.getGracePeriodType().getId());
		assertEquals(1, loan.getGracePeriodDuration().intValue());
		assertNotSame(new java.sql.Date(DateUtils
				.getCurrentDateWithoutTimeStamp().getTime()).toString(), loan
				.getAccountActionDate((short) 1).getActionDate().toString());

		TestObjectFactory.removeObject(loanOffering);
	}

	public void testGracePeriodForInterestDedAtDisb()
			throws NumberFormatException, AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);

		LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group, AccountState.LOANACC_APPROVED, new Money(
						"300.0"), Short.valueOf("6"), new Date(System
						.currentTimeMillis()), true, 10.0, (short) 5,
				new Fund(), new ArrayList<FeeView>());
		assertEquals(
				"For interest ded at disb, grace period type should be none",
				GracePeriodTypeConstants.NONE, loan.getGracePeriodType()
						.getId());
		assertEquals(0, loan.getGracePeriodDuration().intValue());
		assertEquals(new java.sql.Date(DateUtils
				.getCurrentDateWithoutTimeStamp().getTime()).toString(), loan
				.getAccountActionDate((short) 1).getActionDate().toString());

		TestObjectFactory.removeObject(loanOffering);
	}
	
	public void testBuildLoanWithValidDisbDate()
			throws NumberFormatException, AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		Date disbursementDate = new Date(System.currentTimeMillis());

		try {
			LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
					loanOffering, group, AccountState.LOANACC_APPROVED,
					new Money("300.0"), Short.valueOf("6"), disbursementDate,
					false, 10.0, (short) 0, new Fund(),
					new ArrayList<FeeView>());
			assertTrue(
					"The Loan object is created for valid disbursement date",
					true);
		} catch (AccountException ae) {
			assertFalse(
					"The Loan object is created for valid disbursement date",
					true);
		}
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testBuildLoanWithInvalidDisbDate()
			throws NumberFormatException, AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		Date disbursementDate = incrementCurrentDate(3);

		try {
			LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
					loanOffering, group, AccountState.LOANACC_APPROVED,
					new Money("300.0"), Short.valueOf("6"), disbursementDate,
					false, 10.0, (short) 0, new Fund(),
					new ArrayList<FeeView>());
			assertFalse(
					"The Loan object is created for invalid disbursement date",
					true);
		} catch (AccountException ae) {
			assertTrue(
					"The Loan object is created for invalid disbursement date",
					true);
		}
		TestObjectFactory.removeObject(loanOffering);
	}
	
	public void testBuildLoanWithDisbDateOlderThanCurrentDate()
			throws NumberFormatException, AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		Date disbursementDate = offSetCurrentDate(15);

		try {
			LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
					loanOffering, group, AccountState.LOANACC_APPROVED,
					new Money("300.0"), Short.valueOf("6"), disbursementDate,
					false, 10.0, (short) 0, new Fund(),
					new ArrayList<FeeView>());
			assertFalse(
					"The Loan object is created for invalid disbursement date",
					true);
		} catch (AccountException ae) {
			assertTrue(
					"The Loan object is created for invalid disbursement date",
					true);
		}
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testBuildLoanWithFee() throws NumberFormatException,
			AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		List<FeeView> feeViews = getFeeViews();

		LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group, AccountState.LOANACC_APPROVED, new Money(
						"300.0"), Short.valueOf("6"), new Date(System
						.currentTimeMillis()), true, 10.0, (short) 0,
				new Fund(), feeViews);

		assertEquals(2, loan.getAccountFees().size());
		for (AccountFeesEntity accountFees : loan.getAccountFees()) {
			if (accountFees.getFees().getFeeName()
					.equals("One Time Amount Fee"))
				assertEquals(new Double("120.0"), accountFees.getFeeAmount());
			else
				assertEquals(new Double("10.0"), accountFees.getFeeAmount());
		}
		for (AccountActionDateEntity accountActionDate : loan
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
			if (loanScheduleEntity.getInstallmentId() == 1) {
				assertEquals(2, loanScheduleEntity
						.getAccountFeesActionDetails().size());
				assertEquals(new Money("130.0"), loanScheduleEntity
						.getTotalFeeDue());
			} else {
				assertEquals(1, loanScheduleEntity
						.getAccountFeesActionDetails().size());
				assertEquals(new Money("10.0"), loanScheduleEntity
						.getTotalFeeDue());
			}
		}

		deleteFee(feeViews);
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testBuildLoan() throws NumberFormatException, AccountException,
			Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		List<FeeView> feeViews = getFeeViews();

		LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group, AccountState.LOANACC_APPROVED, new Money(
						"300.0"), Short.valueOf("6"), new Date(System
						.currentTimeMillis()), true, 10.0, (short) 0,
				new Fund(), feeViews);

		assertNotNull(loan.getLoanSummary());
		assertNotNull(loan.getPerformanceHistory());
		assertEquals(new Money("300.0"), loan.getLoanSummary()
				.getOriginalPrincipal());
		assertEquals(new Money("300.0"), loan.getLoanAmount());
		assertEquals(new Money("300.0"), loan.getLoanBalance());
		assertEquals(Short.valueOf("6"), loan.getNoOfInstallments());
		assertEquals(6, loan.getAccountActionDates().size());
		assertEquals(loan.getNoOfInstallments().intValue(), loan
				.getAccountActionDates().size());

		deleteFee(feeViews);
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testCreateLoan() throws NumberFormatException,
			AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		List<FeeView> feeViews = getFeeViews();
		boolean isInterestDedAtDisb = false;
		Short noOfinstallments = (short) 6;

		LoanBO loan = createAndRetrieveLoanAccount(loanOffering,
				isInterestDedAtDisb, feeViews, noOfinstallments);

		assertEquals(2, loan.getAccountFees().size());
		for (AccountFeesEntity accountFees : loan.getAccountFees()) {
			if (accountFees.getFees().getFeeName()
					.equals("One Time Amount Fee"))
				assertEquals(new Double("120.0"), accountFees.getFeeAmount());
			else
				assertEquals(new Double("10.0"), accountFees.getFeeAmount());
		}
		for (AccountActionDateEntity accountActionDate : loan
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
			if (loanScheduleEntity.getInstallmentId() != 6) {
				assertEquals(new Money("50.5"), loanScheduleEntity
						.getPrincipal());
				assertEquals(new Money("0.5"), loanScheduleEntity.getInterest());
			} else {
				assertEquals(new Money("47.5"), loanScheduleEntity
						.getPrincipal());
				assertEquals(new Money("0.5"), loanScheduleEntity.getInterest());
			}
		}
		assertNotNull(loan.getLoanSummary());
		assertNotNull(loan.getPerformanceHistory());
		assertEquals(new Money("300.0"), loan.getLoanSummary()
				.getOriginalPrincipal());
		assertEquals(new Money("300.0"), loan.getLoanAmount());
		assertEquals(new Money("300.0"), loan.getLoanBalance());
		assertEquals(Short.valueOf("6"), loan.getNoOfInstallments());
		assertEquals(6, loan.getAccountActionDates().size());
	}

	public void testCreateLoanForInterestDedAtDisb()
			throws NumberFormatException, AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		List<FeeView> feeViews = getFeeViews();
		boolean isInterestDedAtDisb = true;
		Short noOfinstallments = (short) 6;

		LoanBO loan = createAndRetrieveLoanAccount(loanOffering,
				isInterestDedAtDisb, feeViews, noOfinstallments);

		assertEquals(2, loan.getAccountFees().size());
		for (AccountFeesEntity accountFees : loan.getAccountFees()) {
			if (accountFees.getFees().getFeeName()
					.equals("One Time Amount Fee"))
				assertEquals(new Double("120.0"), accountFees.getFeeAmount());
			else
				assertEquals(new Double("10.0"), accountFees.getFeeAmount());
		}
		for (AccountActionDateEntity accountActionDate : loan
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
			if (loanScheduleEntity.getInstallmentId() != 1) {
				assertEquals(new Money("60.0"), loanScheduleEntity
						.getPrincipal());
				assertEquals(new Money("0.0"), loanScheduleEntity.getInterest());
			} else {
				assertEquals(new Money("0.0"), loanScheduleEntity
						.getPrincipal());
				assertEquals(new Money("3.0"), loanScheduleEntity.getInterest());
			}
		}

		assertNotNull(loan.getLoanSummary());
		assertNotNull(loan.getPerformanceHistory());
		assertEquals(new Money("300.0"), loan.getLoanSummary()
				.getOriginalPrincipal());
		assertEquals(new Money("300.0"), loan.getLoanAmount());
		assertEquals(new Money("300.0"), loan.getLoanBalance());
		assertEquals(Short.valueOf("6"), loan.getNoOfInstallments());
		assertEquals(6, loan.getAccountActionDates().size());
	}
	
	public void testCreateLoanForPrincipalAtLastInst()
			throws NumberFormatException, AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(true);
		List<FeeView> feeViews = getFeeViews();
		boolean isInterestDedAtDisb = false;
		Short noOfinstallments = (short) 6;

		LoanBO loan = createAndRetrieveLoanAccount(loanOffering,
				isInterestDedAtDisb, feeViews, noOfinstallments);

		assertEquals(2, loan.getAccountFees().size());
		for (AccountFeesEntity accountFees : loan.getAccountFees()) {
			if (accountFees.getFees().getFeeName()
					.equals("One Time Amount Fee"))
				assertEquals(new Double("120.0"), accountFees.getFeeAmount());
			else
				assertEquals(new Double("10.0"), accountFees.getFeeAmount());
		}
		for (AccountActionDateEntity accountActionDate : loan
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
			if (loanScheduleEntity.getInstallmentId() != 6) {
				assertEquals(new Money("0.0"), loanScheduleEntity
						.getPrincipal());
				assertEquals(new Money("0.5"), loanScheduleEntity.getInterest());
			} else {
				assertEquals(new Money("300.0"), loanScheduleEntity
						.getPrincipal());
				assertEquals(new Money("0.5"), loanScheduleEntity.getInterest());
			}
		}
		assertNotNull(loan.getLoanSummary());
		assertNotNull(loan.getPerformanceHistory());
		assertEquals(new Money("300.0"), loan.getLoanSummary()
				.getOriginalPrincipal());
		assertEquals(new Money("300.0"), loan.getLoanAmount());
		assertEquals(new Money("300.0"), loan.getLoanBalance());
		assertEquals(Short.valueOf("6"), loan.getNoOfInstallments());
		assertEquals(6, loan.getAccountActionDates().size());

	}

	public void testCreateLoanForPrincipalAtLastInstAndIntDedAtDisb()
			throws NumberFormatException, AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(true);
		List<FeeView> feeViews = getFeeViews();
		boolean isInterestDedAtDisb = true;
		Short noOfinstallments = (short) 6;

		LoanBO loan = createAndRetrieveLoanAccount(loanOffering,
				isInterestDedAtDisb, feeViews, noOfinstallments);

		assertEquals(2, loan.getAccountFees().size());
		for (AccountFeesEntity accountFees : loan.getAccountFees()) {
			if (accountFees.getFees().getFeeName()
					.equals("One Time Amount Fee"))
				assertEquals(new Double("120.0"), accountFees.getFeeAmount());
			else
				assertEquals(new Double("10.0"), accountFees.getFeeAmount());
		}
		for (AccountActionDateEntity accountActionDate : loan
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
			if (loanScheduleEntity.getInstallmentId() == 1) {
				assertEquals(new Money("0.0"), loanScheduleEntity
						.getPrincipal());
				assertEquals(new Money("3.0"), loanScheduleEntity.getInterest());
			} else if (loanScheduleEntity.getInstallmentId() == 6) {
				assertEquals(new Money("300.0"), loanScheduleEntity
						.getPrincipal());
				assertEquals(new Money("0.0"), loanScheduleEntity.getInterest());
			} else {
				assertEquals(new Money("0.0"), loanScheduleEntity
						.getPrincipal());
				assertEquals(new Money("0.0"), loanScheduleEntity.getInterest());
			}

		}
		assertNotNull(loan.getLoanSummary());
		assertNotNull(loan.getPerformanceHistory());
		assertEquals(new Money("300.0"), loan.getLoanSummary()
				.getOriginalPrincipal());
		assertEquals(new Money("300.0"), loan.getLoanAmount());
		assertEquals(new Money("300.0"), loan.getLoanBalance());
		assertEquals(Short.valueOf("6"), loan.getNoOfInstallments());
		assertEquals(6, loan.getAccountActionDates().size());

	}
	
	public void testAmountRoundedWhileCreate() throws NumberFormatException,
			AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		boolean isInterestDedAtDisb = false;
		Short noOfinstallments = (short) 6;

		LoanBO loan = createAndRetrieveLoanAccount(loanOffering,
				isInterestDedAtDisb, null, noOfinstallments);

		for (AccountActionDateEntity accountActionDate : loan
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
			if (loanScheduleEntity.getInstallmentId() != 6) {
				assertEquals(new Money("51.0"), loanScheduleEntity
						.getTotalDueWithFees());
			} else {
				assertEquals(
						"The last installment amount is adjusted for rounding",
						new Money("48.0"), loanScheduleEntity
								.getTotalDueWithFees());
			}
		}
	}
	
	public void testAmountNotRoundedWhileCreate() throws NumberFormatException,
			AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		boolean isInterestDedAtDisb = false;
		Short noOfinstallments = (short) 6;

		LoanBO loan = createAndRetrieveLoanAccount(loanOffering,
				isInterestDedAtDisb, null, noOfinstallments,0.0);

		for (AccountActionDateEntity accountActionDate : loan
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
			if (loanScheduleEntity.getInstallmentId() != 6) {
				assertEquals(new Money("50.0"), loanScheduleEntity
						.getTotalDueWithFees());
			} else {
				assertEquals(
						"The last installment amount is not adjusted for rounding",
						new Money("50.0"), loanScheduleEntity
								.getTotalDueWithFees());
			}
		}
	}
	
	public void testApplyMiscCharge() throws Exception{
		accountBO = getLoanAccount();
		Money intialTotalFeeAmount=((LoanBO)accountBO).getLoanSummary().getOriginalFees();
		TestObjectFactory.flushandCloseSession();
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		accountBO.setUserContext(uc);
		accountBO.applyCharge(Short.valueOf("-1"),new Double("33"));
		for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity=(LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("1")))
				assertEquals(new Money("33.0"),loanScheduleEntity.getMiscFee());
		}
		assertEquals(intialTotalFeeAmount.add(new Money("33.0")),((LoanBO)accountBO).getLoanSummary().getOriginalFees());
		LoanActivityEntity loanActivityEntity=((LoanActivityEntity)(((LoanBO)accountBO).getLoanActivityDetails().toArray())[0]);
		assertEquals(AccountConstants.MISC_FEES_APPLIED,loanActivityEntity.getComments());
		assertEquals(((LoanBO)accountBO).getLoanSummary().getOriginalFees(),loanActivityEntity.getFeeOutstanding());
	}
	
	public void testApplyMiscChargeWithFirstInstallmentPaid() throws Exception{
		accountBO = getLoanAccount();
		Money intialTotalFeeAmount=((LoanBO)accountBO).getLoanSummary().getOriginalFees();
		TestObjectFactory.flushandCloseSession();
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity=(LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("1")))
				loanScheduleEntity.setPaymentStatus(PaymentStatus.PAID.getValue());
		}
		UserContext uc = TestObjectFactory.getUserContext();
		accountBO.setUserContext(uc);
		accountBO.applyCharge(Short.valueOf("-1"),new Double("33"));
		for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity=(LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("2")))
				assertEquals(new Money("33.0"),loanScheduleEntity.getMiscFee());
		}
		assertEquals(intialTotalFeeAmount.add(new Money("33.0")),((LoanBO)accountBO).getLoanSummary().getOriginalFees());
		LoanActivityEntity loanActivityEntity=((LoanActivityEntity)(((LoanBO)accountBO).getLoanActivityDetails().toArray())[0]);
		assertEquals(AccountConstants.MISC_FEES_APPLIED,loanActivityEntity.getComments());
		assertEquals(((LoanBO)accountBO).getLoanSummary().getOriginalFees(),loanActivityEntity.getFeeOutstanding());
	}
	
	
	public void testApplyPeriodicFee() throws Exception{
		accountBO = getLoanAccount();
		Money intialTotalFeeAmount=((LoanBO)accountBO).getLoanSummary().getOriginalFees();
		TestObjectFactory.flushandCloseSession();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "200", MeetingFrequency.WEEKLY, Short
						.valueOf("2"));
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		accountBO.setUserContext(uc);
		accountBO.applyCharge(periodicFee.getFeeId(),((AmountFeeBO)periodicFee).getFeeAmount().getAmountDoubleValue());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		Date lastAppliedDate=null;
		for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity=(LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("2"))){
				assertEquals(2,loanScheduleEntity.getAccountFeesActionDetails().size());
				for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity.getAccountFeesActionDetails()){
					if(accountFeesActionDetailEntity.getFee().getFeeName().equals("Periodic Fee"))
						assertEquals(new Double("200"),accountFeesActionDetailEntity.getFeeAmount().getAmountDoubleValue());
				}
			}
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("4"))){
				assertEquals(2,loanScheduleEntity.getAccountFeesActionDetails().size());
				for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity.getAccountFeesActionDetails()){
					if(accountFeesActionDetailEntity.getFee().getFeeName().equals("Periodic Fee"))
						assertEquals(new Double("200"),accountFeesActionDetailEntity.getFeeAmount().getAmountDoubleValue());
				}
			}
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("6"))){
				assertEquals(2,loanScheduleEntity.getAccountFeesActionDetails().size());
				lastAppliedDate=loanScheduleEntity.getActionDate();
				for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity.getAccountFeesActionDetails()){
					if(accountFeesActionDetailEntity.getFee().getFeeName().equals("Periodic Fee"))
						assertEquals(new Double("200"),accountFeesActionDetailEntity.getFeeAmount().getAmountDoubleValue());
				}
			}
		}
		assertEquals(intialTotalFeeAmount.add(new Money("600.0")),((LoanBO)accountBO).getLoanSummary().getOriginalFees());
		LoanActivityEntity loanActivityEntity=((LoanActivityEntity)(((LoanBO)accountBO).getLoanActivityDetails().toArray())[0]);
		assertEquals(periodicFee.getFeeName()+" applied",loanActivityEntity.getComments());
		assertEquals(((LoanBO)accountBO).getLoanSummary().getOriginalFees(),loanActivityEntity.getFeeOutstanding());
		AccountFeesEntity accountFeesEntity=accountBO.getAccountFees(periodicFee.getFeeId());
		assertEquals(FeeStatus.ACTIVE.getValue(),accountFeesEntity.getFeeStatus());
		assertEquals(DateUtils.getDateWithoutTimeStamp(lastAppliedDate.getTime()),DateUtils.getDateWithoutTimeStamp(accountFeesEntity.getLastAppliedDate().getTime()));
	}
	
	public void testApplyUpfrontFee() throws Exception{
		accountBO = getLoanAccount();
		Money intialTotalFeeAmount=((LoanBO)accountBO).getLoanSummary().getOriginalFees();
		TestObjectFactory.flushandCloseSession();
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee",
				FeeCategory.LOAN, Double.valueOf("20"),FeeFormula.AMOUNT, FeePayment.UPFRONT);
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		accountBO.setUserContext(uc);
		accountBO.applyCharge(upfrontFee.getFeeId(),((RateFeeBO)upfrontFee).getRate());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		Date lastAppliedDate=null;
		Money feeAmountApplied=new Money();
		for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity=(LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("2"))){
				assertEquals(2,loanScheduleEntity.getAccountFeesActionDetails().size());
				lastAppliedDate=loanScheduleEntity.getActionDate();
				for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity.getAccountFeesActionDetails()){
					if(accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Upfront Fee")){
						feeAmountApplied=accountFeesActionDetailEntity.getFeeAmount();
					}
				}
			}
		}
		assertEquals(intialTotalFeeAmount.add(feeAmountApplied),((LoanBO)accountBO).getLoanSummary().getOriginalFees());
		LoanActivityEntity loanActivityEntity=((LoanActivityEntity)(((LoanBO)accountBO).getLoanActivityDetails().toArray())[0]);
		assertEquals(upfrontFee.getFeeName()+" applied",loanActivityEntity.getComments());
		assertEquals(((LoanBO)accountBO).getLoanSummary().getOriginalFees(),loanActivityEntity.getFeeOutstanding());
		AccountFeesEntity accountFeesEntity=accountBO.getAccountFees(upfrontFee.getFeeId());
		assertEquals(FeeStatus.ACTIVE.getValue(),accountFeesEntity.getFeeStatus());
		assertEquals(DateUtils.getDateWithoutTimeStamp(lastAppliedDate.getTime()),DateUtils.getDateWithoutTimeStamp(accountFeesEntity.getLastAppliedDate().getTime()));
	}
	
	public void testUpdateLoanSuccessWithRegeneratingNewRepaymentSchedule() throws ApplicationException, SystemException {
		Date startDate = new Date(System.currentTimeMillis());
		Date newDate = incrementCurrentDate(14);
		accountBO = getLoanAccount();
		accountBO.setAccountState(new AccountStateEntity(AccountState.LOANACC_APPROVED.getValue()));
		((LoanBO)accountBO).setDisbursementDate(newDate);
		((LoanBO)accountBO).updateLoan();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		Date newActionDate = DateUtils.getDateWithoutTimeStamp(accountBO.getAccountActionDate(Short.valueOf("1")).getActionDate().getTime());
		assertEquals("New repayment schedule generated, so first installment date should be same as newDate",newDate,newActionDate);
	}
	
	
	public void testUpdateLoanWithoutRegeneratingNewRepaymentSchedule() throws ApplicationException, SystemException {
		Date startDate = new Date(System.currentTimeMillis());
		Date newDate = incrementCurrentDate(14);
		accountBO = getLoanAccount();
		Date oldActionDate = DateUtils.getDateWithoutTimeStamp(accountBO.getAccountActionDate(Short.valueOf("1")).getActionDate().getTime());
		((LoanBO)accountBO).setDisbursementDate(newDate);
		((LoanBO)accountBO).updateLoan();
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		group = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		Date newActionDate = DateUtils.getDateWithoutTimeStamp(accountBO.getAccountActionDate(Short.valueOf("1")).getActionDate().getTime());
		assertEquals("Didn't generate new repayment schedule, so first installment date should be same as before ",oldActionDate,newActionDate);
	}
	
	public void testCreateLoanAccountWithPrincipalDueInLastPayment() throws NumberFormatException, InvalidUserException, PropertyNotFoundException, SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 2, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		
		List<FeeView> feeViewList=new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "100", MeetingFrequency.WEEKLY, Short
						.valueOf("3"));
		feeViewList.add(new FeeView(periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee",
				FeeCategory.LOAN, Double.valueOf("20"),FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeAmountFee("Disbursment Fee",
				FeeCategory.LOAN,"30", FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(disbursementFee));
		
	   accountBO=new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
				AccountState.getStatus(Short.valueOf("5")),	new Money("300.0"),
				Short.valueOf("6"),new Date(System.currentTimeMillis()),false,1.2,(short) 0,
				new Fund(),feeViewList);
	   new TestObjectPersistence().persist(accountBO);
	   assertEquals(6,accountBO.getAccountActionDates().size());
	   for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
		   if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("6"))){
			   assertEquals(DateUtils.getDateWithoutTimeStamp(incrementCurrentDate(14*6).getTime()),DateUtils.getDateWithoutTimeStamp(((LoanScheduleEntity)accountActionDateEntity).getActionDate().getTime()));
			   assertEquals(((LoanBO)accountBO).getLoanSummary().getOriginalPrincipal(),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals("Periodic Fee",accountFeesActionDetailEntity.getFee().getFeeName());
				   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }
		   else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("2"))
				   ||accountActionDateEntity.getInstallmentId().equals(Short.valueOf("5"))){
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(0,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
		   }else  if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))){
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(2,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   if(accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Periodic Fee"))
					   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
				   else
					   assertEquals(new Money("60.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }else{
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals("Periodic Fee",accountFeesActionDetailEntity.getFee().getFeeName());
				   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }
		   assertEquals(new Money("0.1"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
		}
	   assertEquals(3,accountBO.getAccountFees().size());
	   for(AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()){
		   if(accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")){
			   assertEquals(new Money("60.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("20.0"),accountFeesEntity.getFeeAmount());
		   }else if(accountFeesEntity.getFees().getFeeName().equals("Disbursment Fee")){
			   assertEquals(new Money("30.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("30.0"),accountFeesEntity.getFeeAmount());
		   }else{
			   assertEquals(new Money("100.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("100.0"),accountFeesEntity.getFeeAmount());
		   }
	   }
	   LoanSummaryEntity loanSummaryEntity =  ((LoanBO)accountBO).getLoanSummary();
	   assertEquals(new Money("300.0"),loanSummaryEntity.getOriginalPrincipal());
	   assertEquals(new Money("0.6"),loanSummaryEntity.getOriginalInterest());
	   assertEquals(new Money("490.0"),loanSummaryEntity.getOriginalFees());
	   assertEquals(new Money("0.0"),loanSummaryEntity.getOriginalPenalty());
	}
	
	public void testCreateLoanAccountWithInterestDeductedAtDisbursment() throws NumberFormatException, InvalidUserException, PropertyNotFoundException, SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 2, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		
		List<FeeView> feeViewList=new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "100", MeetingFrequency.WEEKLY, Short
						.valueOf("3"));
		feeViewList.add(new FeeView(periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee",
				FeeCategory.LOAN, Double.valueOf("20"),FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeAmountFee("Disbursment Fee",
				FeeCategory.LOAN,"30", FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(disbursementFee));
		
	   accountBO=new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
				AccountState.getStatus(Short.valueOf("5")),	new Money("300.0"),
				Short.valueOf("6"),new Date(System.currentTimeMillis()),true,1.2,(short) 0,
				new Fund(),feeViewList);
	   new TestObjectPersistence().persist(accountBO);
	   assertEquals(6,accountBO.getAccountActionDates().size());
	   for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
		   if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))){
			   assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(),DateUtils.getDateWithoutTimeStamp(((LoanScheduleEntity)accountActionDateEntity).getActionDate().getTime()));
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(new Money("0.6"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(3,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   if(accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Periodic Fee"))
					   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
				   else if(accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Disbursment Fee"))
					   assertEquals(new Money("30.0"),accountFeesActionDetailEntity.getFeeAmount());
				   else
					   assertEquals(new Money("60.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }
		   else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("2"))
				   || accountActionDateEntity.getInstallmentId().equals(Short.valueOf("5"))){
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("60.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(0,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
		   }else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("6"))){
			   assertEquals(DateUtils.getDateWithoutTimeStamp(incrementCurrentDate(14*5).getTime()),DateUtils.getDateWithoutTimeStamp(((LoanScheduleEntity)accountActionDateEntity).getActionDate().getTime()));
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("60.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals("Periodic Fee",accountFeesActionDetailEntity.getFee().getFeeName());
				   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }else{
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("60.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals("Periodic Fee",accountFeesActionDetailEntity.getFee().getFeeName());
				   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }
		}
	   assertEquals(3,accountBO.getAccountFees().size());
	   for(AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()){
		   if(accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")){
			   assertEquals(new Money("60.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("20.0"),accountFeesEntity.getFeeAmount());
		   }else if(accountFeesEntity.getFees().getFeeName().equals("Disbursment Fee")){
			   assertEquals(new Money("30.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("30.0"),accountFeesEntity.getFeeAmount());
		   }else{
			   assertEquals(new Money("100.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("100.0"),accountFeesEntity.getFeeAmount());
		   }
	   }
	   LoanSummaryEntity loanSummaryEntity =  ((LoanBO)accountBO).getLoanSummary();
	   assertEquals(new Money("300.0"),loanSummaryEntity.getOriginalPrincipal());
	   assertEquals(new Money("0.6"),loanSummaryEntity.getOriginalInterest());
	   assertEquals(new Money("490.0"),loanSummaryEntity.getOriginalFees());
	   assertEquals(new Money("0.0"),loanSummaryEntity.getOriginalPenalty());
	}
	
	public void testCreateLoanAccountWithIDADAndPDILI() throws NumberFormatException, InvalidUserException, PropertyNotFoundException, SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 2, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		
		List<FeeView> feeViewList=new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "100", MeetingFrequency.WEEKLY, Short
						.valueOf("3"));
		feeViewList.add(new FeeView(periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee",
				FeeCategory.LOAN, Double.valueOf("20"),FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeAmountFee("Disbursment Fee",
				FeeCategory.LOAN,"30", FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(disbursementFee));
		
	   accountBO=new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
				AccountState.getStatus(Short.valueOf("5")),	new Money("300.0"),
				Short.valueOf("6"),new Date(System.currentTimeMillis()),true,1.2,(short) 0,
				new Fund(),feeViewList);
	   new TestObjectPersistence().persist(accountBO);
	   assertEquals(6,accountBO.getAccountActionDates().size());
	   for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
		   if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))){
			   assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(),DateUtils.getDateWithoutTimeStamp(((LoanScheduleEntity)accountActionDateEntity).getActionDate().getTime()));
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(new Money("0.6"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(3,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   if(accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Periodic Fee"))
					   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
				   else if(accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Disbursment Fee"))
					   assertEquals(new Money("30.0"),accountFeesActionDetailEntity.getFeeAmount());
				   else
					   assertEquals(new Money("60.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }
		   else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("2"))
				   || accountActionDateEntity.getInstallmentId().equals(Short.valueOf("5"))){
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(0,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
		   }else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("6"))){
			   assertEquals(DateUtils.getDateWithoutTimeStamp(incrementCurrentDate(14*5).getTime()),DateUtils.getDateWithoutTimeStamp(((LoanScheduleEntity)accountActionDateEntity).getActionDate().getTime()));
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("300.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals("Periodic Fee",accountFeesActionDetailEntity.getFee().getFeeName());
				   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }else{
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals("Periodic Fee",accountFeesActionDetailEntity.getFee().getFeeName());
				   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }
		}
	   assertEquals(3,accountBO.getAccountFees().size());
	   for(AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()){
		   if(accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")){
			   assertEquals(new Money("60.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("20.0"),accountFeesEntity.getFeeAmount());
		   }else if(accountFeesEntity.getFees().getFeeName().equals("Disbursment Fee")){
			   assertEquals(new Money("30.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("30.0"),accountFeesEntity.getFeeAmount());
		   }else{
			   assertEquals(new Money("100.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("100.0"),accountFeesEntity.getFeeAmount());
		   }
	   }
	   LoanSummaryEntity loanSummaryEntity =  ((LoanBO)accountBO).getLoanSummary();
	   assertEquals(new Money("300.0"),loanSummaryEntity.getOriginalPrincipal());
	   assertEquals(new Money("0.6"),loanSummaryEntity.getOriginalInterest());
	   assertEquals(new Money("490.0"),loanSummaryEntity.getOriginalFees());
	   assertEquals(new Money("0.0"),loanSummaryEntity.getOriginalPenalty());
	}
	
	
	public void testCreateNormalLoanAccount() throws NumberFormatException, InvalidUserException, PropertyNotFoundException, SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 2, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		
		List<FeeView> feeViewList=new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "100", MeetingFrequency.WEEKLY, Short
						.valueOf("3"));
		feeViewList.add(new FeeView(periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee",
				FeeCategory.LOAN, Double.valueOf("20"),FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeAmountFee("Disbursment Fee",
				FeeCategory.LOAN,"30", FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(disbursementFee));
		
	   accountBO=new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
				AccountState.getStatus(Short.valueOf("5")),	new Money("300.0"),
				Short.valueOf("6"),new Date(System.currentTimeMillis()),false,1.2,(short) 0,
				new Fund(),feeViewList);
	   new TestObjectPersistence().persist(accountBO);
	   assertEquals(6,accountBO.getAccountActionDates().size());
	   for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
		   if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))){
			   assertEquals(DateUtils.getDateWithoutTimeStamp(incrementCurrentDate(14).getTime()),DateUtils.getDateWithoutTimeStamp(((LoanScheduleEntity)accountActionDateEntity).getActionDate().getTime()));
			   assertEquals(new Money("50.9"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(new Money("0.1"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(2,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   if(accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Periodic Fee"))
					   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
				   else
					   assertEquals(new Money("60.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }
		   else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("2"))
				   || accountActionDateEntity.getInstallmentId().equals(Short.valueOf("5"))){
			   assertEquals(new Money("0.1"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("50.9"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(0,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
		   }else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("3"))
				   || accountActionDateEntity.getInstallmentId().equals(Short.valueOf("4"))){
			   assertEquals(new Money("0.1"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("50.9"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals("Periodic Fee",accountFeesActionDetailEntity.getFee().getFeeName());
				   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("6"))){ 
			   assertEquals(DateUtils.getDateWithoutTimeStamp(incrementCurrentDate(14*6).getTime()),DateUtils.getDateWithoutTimeStamp(((LoanScheduleEntity)accountActionDateEntity).getActionDate().getTime()));
			   assertEquals(new Money("0.1"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("45.5"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals("Periodic Fee",accountFeesActionDetailEntity.getFee().getFeeName());
				   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }
		}
	   assertEquals(3,accountBO.getAccountFees().size());
	   for(AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()){
		   if(accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")){
			   assertEquals(new Money("60.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("20.0"),accountFeesEntity.getFeeAmount());
		   }else if(accountFeesEntity.getFees().getFeeName().equals("Disbursment Fee")){
			   assertEquals(new Money("30.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("30.0"),accountFeesEntity.getFeeAmount());
		   }else{
			   assertEquals(new Money("100.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("100.0"),accountFeesEntity.getFeeAmount());
		   }
	   }
	   LoanSummaryEntity loanSummaryEntity =  ((LoanBO)accountBO).getLoanSummary();
	   assertEquals(new Money("300.0"),loanSummaryEntity.getOriginalPrincipal());
	   assertEquals(new Money("0.6"),loanSummaryEntity.getOriginalInterest());
	   assertEquals(new Money("490.0"),loanSummaryEntity.getOriginalFees());
	   assertEquals(new Money("0.0"),loanSummaryEntity.getOriginalPenalty());
	}
	
	public void testCreateNormalLoanAccountWithPricipalOnlyGrace() throws NumberFormatException, InvalidUserException, PropertyNotFoundException, SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 2, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("3"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting(), Short.valueOf("3"));
		
		List<FeeView> feeViewList=new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "100", MeetingFrequency.WEEKLY, Short
						.valueOf("3"));
		feeViewList.add(new FeeView(periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee",
				FeeCategory.LOAN, Double.valueOf("20"),FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeAmountFee("Disbursment Fee",
				FeeCategory.LOAN,"30", FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(disbursementFee));
		
	   accountBO=new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
				AccountState.getStatus(Short.valueOf("5")),	new Money("300.0"),
				Short.valueOf("6"),new Date(System.currentTimeMillis()),false,1.2,(short) 1,
				new Fund(),feeViewList);
	   new TestObjectPersistence().persist(accountBO);
	   assertEquals(6,accountBO.getAccountActionDates().size());
	   for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
		   if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))){
			   assertEquals(DateUtils.getDateWithoutTimeStamp(incrementCurrentDate(14*2).getTime()),DateUtils.getDateWithoutTimeStamp(((LoanScheduleEntity)accountActionDateEntity).getActionDate().getTime()));
			   assertEquals(new Money("0.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(new Money("0.1"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(2,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   if(accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Periodic Fee"))
					   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
				   else
					   assertEquals(new Money("60.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }
		   else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("2"))
				   || accountActionDateEntity.getInstallmentId().equals(Short.valueOf("5"))){
			   assertEquals(new Money("0.1"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("60.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(0,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
		   }else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("3"))
				   || accountActionDateEntity.getInstallmentId().equals(Short.valueOf("4"))){
			   assertEquals(new Money("0.1"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("60.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals("Periodic Fee",accountFeesActionDetailEntity.getFee().getFeeName());
				   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("6"))){ 
			   assertEquals(DateUtils.getDateWithoutTimeStamp(incrementCurrentDate(14*7).getTime()),DateUtils.getDateWithoutTimeStamp(((LoanScheduleEntity)accountActionDateEntity).getActionDate().getTime()));
			   assertEquals(new Money("0.1"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("60.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals("Periodic Fee",accountFeesActionDetailEntity.getFee().getFeeName());
				   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }
		}
	   assertEquals(3,accountBO.getAccountFees().size());
	   for(AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()){
		   if(accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")){
			   assertEquals(new Money("60.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("20.0"),accountFeesEntity.getFeeAmount());
		   }else if(accountFeesEntity.getFees().getFeeName().equals("Disbursment Fee")){
			   assertEquals(new Money("30.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("30.0"),accountFeesEntity.getFeeAmount());
		   }else{
			   assertEquals(new Money("100.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("100.0"),accountFeesEntity.getFeeAmount());
		   }
	   }
	   LoanSummaryEntity loanSummaryEntity =  ((LoanBO)accountBO).getLoanSummary();
	   assertEquals(new Money("300.0"),loanSummaryEntity.getOriginalPrincipal());
	   assertEquals(new Money("0.6"),loanSummaryEntity.getOriginalInterest());
	   assertEquals(new Money("490.0"),loanSummaryEntity.getOriginalFees());
	   assertEquals(new Money("0.0"),loanSummaryEntity.getOriginalPenalty());
	}
	
	public void testCreateNormalLoanAccountWithMonthlyInstallments() throws NumberFormatException, InvalidUserException, PropertyNotFoundException, SystemException, ApplicationException {
		Short dayOfMonth = (short)1;
		MeetingBO meeting = TestObjectFactory.getMeetingHelper(2,2,4);
		meeting.setMeetingStartDate(Calendar.getInstance());
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(dayOfMonth);
		TestObjectFactory.createMeeting(meeting);
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("3"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		
		Calendar disbursementDate = new GregorianCalendar();
		int year = disbursementDate.get(Calendar.YEAR);
		int month = disbursementDate.get(Calendar.MONTH);
		int day = disbursementDate.get(0);
		if(disbursementDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth.intValue())
			disbursementDate = new GregorianCalendar(year, month,day);
		else
			disbursementDate = new GregorianCalendar(year, month+1,day);
	
		List<FeeView> feeViewList=new ArrayList<FeeView>();
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee",
				FeeCategory.LOAN, Double.valueOf("20"),FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeRateFee("Disbursment Fee",
				FeeCategory.LOAN,Double.valueOf("30"), FeeFormula.AMOUNT_AND_INTEREST,FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(disbursementFee));
		FeeBO firstRepaymentFee = TestObjectFactory.createOneTimeRateFee("First Repayment Fee",
				FeeCategory.LOAN,Double.valueOf("40"),FeeFormula.INTEREST, FeePayment.TIME_OF_FIRSTLOANREPAYMENT);
		feeViewList.add(new FeeView(firstRepaymentFee));
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "100", MeetingFrequency.MONTHLY, Short
						.valueOf("1"));
		feeViewList.add(new FeeView(periodicFee));
	   accountBO=new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
				AccountState.getStatus(Short.valueOf("5")),	new Money("300.0"),
				Short.valueOf("6"),disbursementDate.getTime(),false,1.2,(short) 0,
				new Fund(),feeViewList);
	   new TestObjectPersistence().persist(accountBO);
	   assertEquals(6,accountBO.getAccountActionDates().size());
	   for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
		   if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))){
			   assertEquals(new Money("50.0"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(new Money("0.6"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(3,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   if(accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("Upfront Fee"))
					   assertEquals(new Money("60.0"),accountFeesActionDetailEntity.getFeeAmount());
				   else if(accountFeesActionDetailEntity.getFee().getFeeName().equalsIgnoreCase("First Repayment Fee"))
					   assertEquals(new Money("1.4"),accountFeesActionDetailEntity.getFeeAmount());
				   else
					   assertEquals(new Money("100.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		   }else if(accountActionDateEntity.getInstallmentId().equals(Short.valueOf("6"))){ 
			   assertEquals(new Money("0.6"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
			   assertEquals(new Money("48.4"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
			   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
			   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
				   assertEquals(new Money("200.0"),accountFeesActionDetailEntity.getFeeAmount());
			   }
		    }else { 
				   assertEquals(new Money("0.6"),((LoanScheduleEntity)accountActionDateEntity).getInterest());
				   assertEquals(new Money("50.4"),((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
				   assertEquals(1,((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails().size());
				   for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((LoanScheduleEntity)accountActionDateEntity).getAccountFeesActionDetails()){
					   assertEquals(new Money("200.0"),accountFeesActionDetailEntity.getFeeAmount());
				   }
			}
		}
	   assertEquals(4,accountBO.getAccountFees().size());
	   for(AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()){
		  if(accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")){
			   assertEquals(new Money("60.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("20.0"),accountFeesEntity.getFeeAmount());
		   }else if(accountFeesEntity.getFees().getFeeName().equals("Disbursment Fee")){
			   assertEquals(new Money("91.1"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("30.0"),accountFeesEntity.getFeeAmount());
		   }else if(accountFeesEntity.getFees().getFeeName().equals("First Repayment Fee")){
			   assertEquals(new Money("1.4"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("40.0"),accountFeesEntity.getFeeAmount());
		   }else{
			   assertEquals(new Money("100.0"),accountFeesEntity.getAccountFeeAmount());
			   assertEquals(new Double("100.0"),accountFeesEntity.getFeeAmount());
		   }
	   }
	   LoanSummaryEntity loanSummaryEntity =  ((LoanBO)accountBO).getLoanSummary();
	   assertEquals(new Money("300.0"),loanSummaryEntity.getOriginalPrincipal());
	   assertEquals(new Money("3.6"),loanSummaryEntity.getOriginalInterest());
	   assertEquals(new Money("1252.5"),loanSummaryEntity.getOriginalFees());
	   assertEquals(new Money("0.0"),loanSummaryEntity.getOriginalPenalty());
	}

	public void testDisburseLoanWithAllTypeOfFees() throws NumberFormatException, InvalidUserException, PropertyNotFoundException, SystemException, ApplicationException {
		Short dayOfMonth = (short)1;
		MeetingBO meeting = TestObjectFactory.getMeetingHelper(2,2,4);
		meeting.setMeetingStartDate(Calendar.getInstance());
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(dayOfMonth);
		TestObjectFactory.createMeeting(meeting);
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("3"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		
		Calendar disbursementDate = new GregorianCalendar();
		int year = disbursementDate.get(Calendar.YEAR);
		int month = disbursementDate.get(Calendar.MONTH);
		int day = disbursementDate.get(0);
		if(disbursementDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth.intValue())
			disbursementDate = new GregorianCalendar(year, month,day);
		else
			disbursementDate = new GregorianCalendar(year, month+1,day);
	
		List<FeeView> feeViewList=new ArrayList<FeeView>();
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee("Upfront Fee",
				FeeCategory.LOAN, Double.valueOf("20"),FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeRateFee("Disbursment Fee",
				FeeCategory.LOAN,Double.valueOf("30"), FeeFormula.AMOUNT_AND_INTEREST,FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(disbursementFee));
		FeeBO firstRepaymentFee = TestObjectFactory.createOneTimeRateFee("First Repayment Fee",
				FeeCategory.LOAN,Double.valueOf("40"),FeeFormula.INTEREST, FeePayment.TIME_OF_FIRSTLOANREPAYMENT);
		feeViewList.add(new FeeView(firstRepaymentFee));
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "100", MeetingFrequency.MONTHLY, Short
						.valueOf("1"));
		feeViewList.add(new FeeView(periodicFee));
	    accountBO=new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
				AccountState.getStatus(Short.valueOf("5")),	new Money("300.0"),
				Short.valueOf("6"),disbursementDate.getTime(),false,1.2,(short) 0,
				new Fund(),feeViewList);
	    new TestObjectPersistence().persist(accountBO);
	    
	    disbursementDate = new GregorianCalendar();
		year = disbursementDate.get(Calendar.YEAR);
		month = disbursementDate.get(Calendar.MONTH);
		day = disbursementDate.get(0);
		if(disbursementDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth.intValue())
			disbursementDate = new GregorianCalendar(year, month+2,day);
		else
			disbursementDate = new GregorianCalendar(year, month+3,day);
	    ((LoanBO) accountBO).disburseLoan("1234", disbursementDate.getTime(),
				Short.valueOf("1"), accountBO.getPersonnel(), disbursementDate.getTime(), Short
						.valueOf("1"));
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		session.save(accountBO);
		HibernateUtil.getTransaction().commit();
	}
	
	public void testUpdateLoanFailure() {
		accountBO = getLoanAccount();	
		accountBO.setAccountState(new AccountStateEntity(AccountState.LOANACC_PENDINGAPPROVAL));
		((LoanBO)accountBO).setDisbursementDate(offSetCurrentDate(15));
		try{
			((LoanBO)accountBO).updateLoan();
			assertFalse(
					"The Loan object is created for invalid disbursement date",
					true);
		} catch (AccountException ae) {
			assertTrue(
					"The Loan object is created for invalid disbursement date",
					true);
		}
	}
	
	public void testUpdateLoanSuccess() {
		accountBO = getLoanAccount();
		((LoanBO)accountBO).setCollateralNote("Loan account updated");
		try{
			((LoanBO)accountBO).updateLoan();
			assertTrue(
					"The Loan object is created for valid disbursement date",
					true);
		} catch (AccountException ae) {
			assertFalse(
					"The Loan object is created for valid disbursement date",
					true);
		}
	}	

	public void testApplyTimeOfFirstRepaymentFee() throws Exception{
		accountBO = getLoanAccount();
		Money intialTotalFeeAmount=((LoanBO)accountBO).getLoanSummary().getOriginalFees();
		TestObjectFactory.flushandCloseSession();
		FeeBO oneTimeFee = TestObjectFactory.createOneTimeRateFee("Onetime Fee",
				FeeCategory.LOAN, new Double("0.09"),FeeFormula.AMOUNT,FeePayment.TIME_OF_FIRSTLOANREPAYMENT);
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		accountBO.setUserContext(uc);
		accountBO.applyCharge(oneTimeFee.getFeeId(),((RateFeeBO)oneTimeFee).getRate());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO=(AccountBO)TestObjectFactory.getObject(AccountBO.class,accountBO.getAccountId());
		for(AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity=(LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("2"))){
				assertEquals(2,loanScheduleEntity.getAccountFeesActionDetails().size());
				for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity.getAccountFeesActionDetails()){
					if(accountFeesActionDetailEntity.getFee().getFeeName().equals("Onetime Fee"))
						assertEquals(new Money("0.3"),accountFeesActionDetailEntity.getFeeAmount());
				}
			}
		}
		assertEquals(intialTotalFeeAmount.add(new Money("0.3")),((LoanBO)accountBO).getLoanSummary().getOriginalFees());
		LoanActivityEntity loanActivityEntity=((LoanActivityEntity)(((LoanBO)accountBO).getLoanActivityDetails().toArray())[0]);
		assertEquals(oneTimeFee.getFeeName()+" applied",loanActivityEntity.getComments());
		assertEquals(((LoanBO)accountBO).getLoanSummary().getOriginalFees(),loanActivityEntity.getFeeOutstanding());
		AccountFeesEntity accountFeesEntity=accountBO.getAccountFees(oneTimeFee.getFeeId());
		assertEquals(FeeStatus.ACTIVE.getValue(),accountFeesEntity.getFeeStatus());
	}
	
	public void testApplyPaymentForFullPayment() throws AccountException {
		accountBO = getLoanAccount();
		assertEquals(new Money("212.0"),((LoanBO) accountBO).getTotalPaymentDue());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("212"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money(),((LoanBO) accountBO).getTotalPaymentDue());
	}
	
	public void testApplyPaymentForPartialPayment() throws AccountException {
		accountBO = getLoanAccount();
		assertEquals(new Money("212.0"),((LoanBO) accountBO).getTotalPaymentDue());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("200"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money("12"),((LoanBO) accountBO).getTotalPaymentDue());
	}
	
	
	public void testApplyPaymentForFuturePayment() throws AccountException {
		accountBO = getLoanAccount();

		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money("212.0"), ((LoanBO) accountBO)
				.getTotalPaymentDue());
		LoanScheduleEntity nextInstallment = (LoanScheduleEntity) ((LoanBO) accountBO)
				.getApplicableIdsForFutureInstallments().get(0);
		assertEquals(new Money("212.0"), nextInstallment.getTotalDueWithFees());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("312"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money(), ((LoanBO) accountBO).getTotalPaymentDue());
		nextInstallment =(LoanScheduleEntity) accountBO.getAccountActionDate(nextInstallment.getInstallmentId());
		assertEquals(new Money("112.0"), nextInstallment.getTotalDueWithFees());
	}
	
	public void testApplyPaymentForCompletePayment() throws AccountException {
		accountBO = getLoanAccount();

		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money("212.0"), ((LoanBO) accountBO)
				.getTotalPaymentDue());
		LoanScheduleEntity nextInstallment = (LoanScheduleEntity) ((LoanBO) accountBO)
				.getApplicableIdsForFutureInstallments().get(0);
		assertEquals(new Money("212.0"), nextInstallment.getTotalDueWithFees());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("1272"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money(), ((LoanBO) accountBO).getTotalPaymentDue());
		nextInstallment =(LoanScheduleEntity) accountBO.getAccountActionDate(nextInstallment.getInstallmentId());
		assertEquals(new Money(), nextInstallment.getTotalDueWithFees());
		assertEquals(AccountState.LOANACC_OBLIGATIONSMET,accountBO.getState());
	}
	
	public void testApplyPaymentForPaymentGretaterThanTotalDue()  {
		accountBO = getLoanAccount();

		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money("212.0"), ((LoanBO) accountBO)
				.getTotalPaymentDue());
		LoanScheduleEntity nextInstallment = (LoanScheduleEntity) ((LoanBO) accountBO)
				.getApplicableIdsForFutureInstallments().get(0);
		assertEquals(new Money("212.0"), nextInstallment.getTotalDueWithFees());
		try {
			accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
					null, new Money("1300"), accountBO.getCustomer(), accountBO
							.getPersonnel(), "432423", (short) 1, new Date(System
							.currentTimeMillis()), new Date(System
							.currentTimeMillis())));
			assertFalse(true);
		} catch (AccountException e) {
			assertTrue(true);
		}
	}
	
	private LoanBO createAndRetrieveLoanAccount(LoanOfferingBO loanOffering,
			boolean isInterestDedAtDisb, List<FeeView> feeViews,
			Short noOfinstallments, Double interestRate)
			throws NumberFormatException, AccountException,
			InvalidUserException, SystemException, ApplicationException {
		LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group, AccountState.LOANACC_APPROVED, new Money(
						"300.0"), noOfinstallments, new Date(System
						.currentTimeMillis()), isInterestDedAtDisb,
				interestRate, (short) 0, new Fund(), feeViews);
		loan.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				loan.getAccountId());
		return (LoanBO) accountBO;
	}
	
	private LoanBO createAndRetrieveLoanAccount(LoanOfferingBO loanOffering,
			boolean isInterestDedAtDisb, List<FeeView> feeViews,
			Short noOfinstallments) throws NumberFormatException,
			AccountException, InvalidUserException, SystemException,
			ApplicationException {
		return createAndRetrieveLoanAccount(loanOffering, isInterestDedAtDisb,
				feeViews, noOfinstallments, 10.0);
	}

	private LoanOfferingBO createLoanOffering(boolean isPrincipalAtLastInst) {
		return createLoanOffering(isPrincipalAtLastInst,ProductDefinitionConstants.LOANACTIVE);
	}
	
	private LoanOfferingBO createLoanOffering(boolean isPrincipalAtLastInst,Short statusId) {
		Short principalAtLastInst = isPrincipalAtLastInst ? (short) 1
				: (short) 0;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createLoanOffering("Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), statusId,
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), principalAtLastInst,
				Short.valueOf("1"), meeting);
	}

	private List<FeeView> getFeeViews() {
		FeeBO fee1 = TestObjectFactory.createOneTimeAmountFee(
				"One Time Amount Fee", FeeCategory.LOAN, "120.0",
				FeePayment.TIME_OF_DISBURSMENT);
		FeeBO fee3 = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "10.0", MeetingFrequency.WEEKLY, (short) 1);
		List<FeeView> feeViews = new ArrayList<FeeView>();
		FeeView feeView1 = new FeeView(fee1);
		FeeView feeView2 = new FeeView(fee3);
		feeViews.add(feeView1);
		feeViews.add(feeView2);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		return feeViews;
	}

	private void deleteFee(List<FeeView> feeViews) {
		for (FeeView feeView : feeViews) {
			TestObjectFactory.cleanUp((FeeBO) TestObjectFactory.getObject(
					FeeBO.class, feeView.getFeeIdValue()));
		}

	}
	private AccountBO getLoanAccount() {
		createInitialCustomers();
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		return TestObjectFactory.createLoanAccount("42423142341", group, Short
				.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
	}
	
	private void createInitialCustomers() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
	}
	
	private void changeFirstInstallmentDateToNextDate(AccountBO accountBO) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + 1);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			accountActionDateEntity.setActionDate(new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
	}
	
	private AccountBO applyPaymentandRetrieveAccount() throws AccountException,
			SystemException {
		Date startDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = new PaymentData(new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), "212.0"),
				accountBO.getPersonnel(), Short.valueOf("1"), startDate);
		paymentData.setRecieptDate(startDate);
		paymentData.setRecieptNum("5435345");
	
		accountBO.applyPayment(paymentData);
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		return (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
	}

	private AccountBO getLoanAccountWithMiscFeeAndPenalty(Short accountSate,
			Date startDate, int disbursalType, Money miscFee, Money miscPenalty) {
		accountBO = getLoanAccount(accountSate, startDate, disbursalType);
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				accountActionDateEntity.setMiscFee(miscFee);
				accountActionDateEntity.setMiscPenalty(miscPenalty);
				break;
			}
		}
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		loanSummaryEntity.setOriginalFees(loanSummaryEntity.getOriginalFees()
				.add(miscFee));
		loanSummaryEntity.setOriginalPenalty(loanSummaryEntity
				.getOriginalPenalty().add(miscPenalty));
		TestObjectPersistence testObjectPersistence = new TestObjectPersistence();
		testObjectPersistence.update(accountBO);
		return (LoanBO) testObjectPersistence.getObject(LoanBO.class, accountBO
				.getAccountId());
	}

	private void changeInstallmentDate(AccountBO accountBO, int numberOfDays,
			Short installmentId) {
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
						- numberOfDays);
				accountActionDateEntity.setActionDate(new java.sql.Date(
						dateCalendar.getTimeInMillis()));
				break;
			}
		}
	}

	private AccountBO getLoanAccountWithPerformanceHistory() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				ClientConstants.STATUS_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
		((ClientBO)client).getPerformanceHistory().setLoanCycleNumber(1);
		TestObjectFactory.updateObject(client);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", client,
				Short.valueOf("3"), new Date(System.currentTimeMillis()),
				loanOffering);
		TestObjectFactory.updateObject(accountBO);
		return accountBO;
	}

	private AccountBO getLoanAccountWithPerformanceHistory(Short accountSate,
			Date startDate, int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		client = TestObjectFactory.createClient("Client",
				ClientConstants.STATUS_ACTIVE, "1.4.1.1", group, new Date(
						System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		((ClientBO)client).getPerformanceHistory().setLoanCycleNumber(1);
		accountBO = TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", client, accountSate, startDate, loanOffering,
				disbursalType);
		return accountBO;

	}

	private AccountBO getLoanAccountWithGroupPerformanceHistory(
			Short accountSate, Date startDate, int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("1"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		GroupPerformanceHistoryEntity groupPerformanceHistoryEntity = new GroupPerformanceHistoryEntity(
				0, new Money(), new Money(), new Money(), new Money(),
				new Money());
		((GroupBO) group).setPerformanceHistory(groupPerformanceHistoryEntity);
		accountBO = TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, accountSate, startDate, loanOffering,
				disbursalType);
		return accountBO;

	}

	private AccountActionDateEntity getLastInstallmentAccountAction(
			LoanBO loanBO) {
		AccountActionDateEntity nextAccountAction = null;
		if (loanBO.getAccountActionDates() != null
				&& loanBO.getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accountAction : loanBO
					.getAccountActionDates()) {
				if (null == nextAccountAction)
					nextAccountAction = accountAction;
				else if (nextAccountAction.getInstallmentId() < accountAction
						.getInstallmentId())
					nextAccountAction = accountAction;
			}
		}
		return nextAccountAction;
	}
	
	private void changeFirstInstallmentDate(AccountBO accountBO,
			int numberOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day
				- numberOfDays);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			accountActionDateEntity.setActionDate(new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
	}
	
	private void changeFirstInstallmentDate(AccountBO accountBO) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - 1);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			accountActionDateEntity.setActionDate(new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
	}

	private AccountBO saveAndFetch(AccountBO account) {
		accountPersistanceService.updateAccount(account);
		HibernateUtil.closeSession();
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

	private AccountBO getLoanAccount(Short accountSate, Date startDate,
			int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, new Date(System.currentTimeMillis()));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, accountSate, startDate, loanOffering,
				disbursalType);

	}
	
	private Date incrementCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
		return DateUtils.getDateWithoutTimeStamp(currentDateCalendar.getTimeInMillis());
	}

}
