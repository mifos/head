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
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.components.scheduler.helpers.SchedulerHelper;
import org.mifos.framework.exceptions.PersistenceException;
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
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		group = (CustomerBO) HibernateUtil.getSessionTL().get(CustomerBO.class,
				group.getCustomerId());
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
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
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
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
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
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
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

		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
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
		} catch (RepaymentScheduleException rse) {

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
		try {
			((LoanBO) accountBO).disburseLoan("1234", cal.getTime(), Short
					.valueOf("1"), accountBO.getPersonnel(), startDate, Short
					.valueOf("1"));
			Session session = HibernateUtil.getSessionTL();
			HibernateUtil.startTransaction();
			((LoanBO) accountBO).setLoanMeeting(null);
			session.update(accountBO);
			HibernateUtil.getTransaction().commit();
			assertEquals(true, true);
		} catch (RepaymentScheduleException rse) {
			rse.printStackTrace();
			assertEquals(true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		AccountActionDateEntity accountActionDateEntity = accountBO
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

		AccountActionDateEntity accountActionDateEntity = accountBO
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
						.getSystemConfig().getCurrency(), "100.0"), null,
				accountBO.getPersonnel(), "5435345", Short.valueOf("1"),
				startDate, startDate);

		accountBO.applyPayment(paymentData);
		assertEquals(
				"When Last installment is paid the status has been changed to closed",
				((LoanBO) accountBO).getAccountState().getId().toString(),
				String.valueOf(AccountStates.LOANACC_OBLIGATIONSMET));
		accountBO.getAccountPayments().clear();
	}

	public void testHandleArrears() throws ServiceException {
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
				accountBO.accountStatusChangeHistory.size());
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
		assertEquals(false, loan.isInterestDeductedAtDisbursement());
		loan
				.setIntrestAtDisbursement(LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT);
		assertEquals(true, loan.isInterestDeductedAtDisbursement());

	}

	public void testWtiteOff() throws Exception {
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
	}

	public void testGetAmountTobePaidAtdisburtail() {

		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 2);
		assertEquals(new Money("52"), ((LoanBO) accountBO)
				.getAmountTobePaidAtdisburtail(startDate));

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

	private AccountBO applyPaymentandRetrieveAccount() throws AccountException,
			SystemException {
		Date startDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = new PaymentData(new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), "100.0"),
				accountBO.getPersonnel(), Short.valueOf("1"), startDate);
		paymentData.setRecieptDate(startDate);
		paymentData.setRecieptNum("5435345");
		AccountActionDateEntity actionDate = accountBO
				.getAccountActionDate(Short.valueOf("1"));
		LoanPaymentData loanPaymentData = new LoanPaymentData(actionDate);
		paymentData.addAccountPaymentData(loanPaymentData);
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
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
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

	public void testWaiveFeeChargeDue() throws Exception {
		accountBO = getLoanAccount();
		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		TestObjectFactory.flushandCloseSession();
		LoanBO loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
		UserContext userContext = TestObjectFactory.getUserContext();
		loanBO.setUserContext(userContext);
		loanBO.waiveAmountDue(WaiveEnum.FEES);
		TestObjectFactory.flushandCloseSession();
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, accountBO
				.getAccountId());
		for (AccountActionDateEntity accountActionDateEntity : loanBO
				.getAccountActionDates()) {
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDateEntity
					.getAccountFeesActionDetails()) {
				if (accountActionDateEntity.getInstallmentId().equals(
						Short.valueOf("1"))) {
					assertEquals(new Money(), accountFeesActionDetailEntity
							.getFeeAmount());
				} else
					assertEquals(new Money("100"),
							accountFeesActionDetailEntity.getFeeAmount());
			}
		}
	}

	public void testWaiveFeeChargeOverDue() throws Exception {
		accountBO = getLoanAccount();
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

		for (AccountActionDateEntity installment : accountBO
				.getAccountActionDates()) {
			if (installment.getInstallmentId().intValue() == 1) {
				installment.setActionDate(lastWeekDate);
			} else if (installment.getInstallmentId().intValue() == 2) {
				installment.setActionDate(twoWeeksBeforeDate);
			}
		}
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, accountBO
				.getAccountId());
		UserContext userContext = TestObjectFactory.getUserContext();
		loanBO.setUserContext(userContext);
		loanBO.waiveAmountOverDue(WaiveEnum.FEES);
		TestObjectFactory.flushandCloseSession();
		loanBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, accountBO
				.getAccountId());
		for (AccountActionDateEntity accountActionDateEntity : loanBO
				.getAccountActionDates()) {
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

	public void testRegenerateFutureInstallments() throws SchedulerException,
			HibernateException, ServiceException, PersistenceException {
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
			PersistenceException {
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

	public void testGetRemainingPrincipalAmount() throws AccountException,
			SystemException {
		accountBO = getLoanAccount();
		Date currentDate = new Date(System.currentTimeMillis());
		LoanBO loan = (LoanBO) accountBO;
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accntActionDates, TestObjectFactory
						.getMoneyForMFICurrency(212 * 6), null, accountBO
						.getPersonnel(), "receiptNum", Short.valueOf("1"),
				currentDate, currentDate);
		loan.applyPayment(paymentData);

		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		assertEquals(
				"The amount returned for the payment should have been 1272",
				1272.0, loan.getLastPmntAmnt());
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
				.getInstance().getSystemConfig().getCurrency(), "100.0"),
				accountBO.getPersonnel(), Short.valueOf("1"), startDate);
		paymentData.setRecieptDate(startDate);
		paymentData.setRecieptNum("5435345");
		AccountActionDateEntity actionDate = accountBO
				.getAccountActionDate(Short.valueOf("1"));
		LoanPaymentData loanPaymentData = new LoanPaymentData(actionDate);
		paymentData.addAccountPaymentData(loanPaymentData);

		accountBO.applyPayment(paymentData);
		TestObjectFactory.flushandCloseSession();

		accountBO = (AccountBO) TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());

		assertEquals(Integer.valueOf("2"), ((LoanBO) accountBO)
				.getMissedPaymentCount());
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
		ClientPerformanceHistoryEntity clientPerfHistory = new ClientPerformanceHistoryEntity(
				1, 0, new Money(), new Money(), new Money());
		((ClientBO) client).setPerformanceHistory(clientPerfHistory);
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
		LoanPerformanceHistoryEntity loanPerfHistory = new LoanPerformanceHistoryEntity();
		((LoanBO) accountBO).setPerformanceHistory(loanPerfHistory);
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
		ClientPerformanceHistoryEntity clientPerfHistory = new ClientPerformanceHistoryEntity(
				1, 0, new Money(), new Money(), new Money());
		((ClientBO) client).setPerformanceHistory(clientPerfHistory);
		accountBO = TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", client, accountSate, startDate, loanOffering,
				disbursalType);
		LoanPerformanceHistoryEntity loanPerfHistory = new LoanPerformanceHistoryEntity();
		((LoanBO) accountBO).setPerformanceHistory(loanPerfHistory);
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
		LoanPerformanceHistoryEntity loanPerfHistory = new LoanPerformanceHistoryEntity();
		((LoanBO) accountBO).setPerformanceHistory(loanPerfHistory);
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
				.getCustomer().getCustomerPerformanceHistory())
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
				.getCustomer().getCustomerPerformanceHistory();
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
				.getCustomer().getCustomerPerformanceHistory();
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
				.getCustomer().getCustomerPerformanceHistory();
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
				.getCustomer().getCustomerPerformanceHistory();
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
				.getCustomer().getCustomerPerformanceHistory();
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
				.getCustomer().getCustomerPerformanceHistory();
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
				.getCustomer().getCustomerPerformanceHistory();
		Integer noOfActiveLoans = clientPerfHistory.getNoOfActiveLoans();
		LoanPerformanceHistoryEntity loanPerfHistory = ((LoanBO) accountBO)
				.getPerformanceHistory();
		Integer noOfPayments = loanPerfHistory.getNoOfPayments();
		accountBO = applyPaymentandRetrieveAccount();
		LoanBO loan = (LoanBO) accountBO;
		client = (CustomerBO) TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		clientPerfHistory = (ClientPerformanceHistoryEntity) loan.getCustomer()
				.getCustomerPerformanceHistory();
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
				.getCustomer().getCustomerPerformanceHistory();
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
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
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
		for (AccountActionDateEntity accountActionDate : accountBO
				.getAccountActionDates()) {
			if (accountActionDate.getInstallmentId().equals(Short.valueOf("1")))
				assertEquals(new Money("133.0"), accountActionDate
						.getTotalDue());
			else if (accountActionDate.getInstallmentId().equals(
					Short.valueOf("6")))
				assertEquals(new Money("111.3"), accountActionDate
						.getTotalDue());
			else
				assertEquals(new Money("112.0"), accountActionDate
						.getTotalDue());
		}
	}

}
