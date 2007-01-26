package org.mifos.application.accounts.loan.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.TestAccountActionDateEntity;
import org.mifos.application.accounts.business.TestAccountFeesEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.collectionsheet.business.CollSheetCustBO;
import org.mifos.application.collectionsheet.business.CollectionSheetBO;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.TestCustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.ClientPerformanceHistoryEntity;
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
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.CollateralTypeEntity;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.GraceTypeConstants;
import org.mifos.application.productdefinition.util.helpers.InterestTypeConstants;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
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
import static org.mifos.framework.util.helpers.TestObjectFactory.*; 
import static org.mifos.application.meeting.util.helpers.MeetingType.*;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.*;
import static org.mifos.application.meeting.util.helpers.WeekDay.*;

public class TestLoanBO extends MifosTestCase {
	
	LoanOfferingBO loanOffering = null;
	
	// TODO: probably should be of type LoanBO
	protected AccountBO accountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	private CustomerBO client = null;

	private AccountPersistence accountPersistence = null;

	private MeetingBO meeting;

	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();
		accountPersistence = new AccountPersistence();
	}

	@Override
	protected void tearDown() throws Exception {
		
		TestObjectFactory.removeObject(loanOffering);
		if (accountBO != null)
			accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
					AccountBO.class, accountBO.getAccountId());
		if (group != null)
			group = (CustomerBO) HibernateUtil.getSessionTL().get(
					CustomerBO.class, group.getCustomerId());
		if (center != null)
			center = (CustomerBO) HibernateUtil.getSessionTL().get(
					CustomerBO.class, center.getCustomerId());
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);

		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testWaiveMiscFeeAfterPayment() throws Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		List<FeeView> feeViews = new ArrayList<FeeView>();
		boolean isInterestDedAtDisb = false;
		Short noOfinstallments = (short) 6;
		LoanBO loan = createAndRetrieveLoanAccount(loanOffering,
				isInterestDedAtDisb, feeViews, noOfinstallments);
		loan.setUserContext(TestObjectFactory.getUserContext());
		loan.applyCharge(Short.valueOf("-1"),Double.valueOf("200"));
		for(AccountActionDateEntity accountActionDateEntity : loan.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))){
				assertEquals(loanScheduleEntity.getMiscFee(),new Money("200"));
				assertEquals(loanScheduleEntity.getMiscFeePaid(),new Money());
			}
		}
		assertEquals(loan.getLoanSummary().getOriginalFees(),new Money("200"));
		assertEquals(loan.getLoanSummary().getFeesPaid(),new Money());
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		Date currentDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accntActionDates,
				TestObjectFactory.getMoneyForMFICurrency(200), null, accountBO
						.getPersonnel(), "receiptNum", Short.valueOf("1"),
				currentDate, currentDate);
		loan.applyPayment(paymentData);
		TestObjectFactory.updateObject(loan);
		for(AccountActionDateEntity accountActionDateEntity : loan.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))){
				assertEquals(loanScheduleEntity.getMiscFee(),new Money("200"));
				assertEquals(loanScheduleEntity.getMiscFeePaid(),new Money("200"));
			}
		}
		assertEquals(loan.getLoanSummary().getOriginalFees(),new Money("200"));
		assertEquals(loan.getLoanSummary().getFeesPaid(),new Money("200"));
		loan.applyCharge(Short.valueOf("-1"),Double.valueOf("300"));
		TestObjectFactory.updateObject(loan);
		for(AccountActionDateEntity accountActionDateEntity : loan.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))){
				assertEquals(loanScheduleEntity.getMiscFee(),new Money("500"));
				assertEquals(loanScheduleEntity.getMiscFeePaid(),new Money("200"));
			}
		}
		assertEquals(loan.getLoanSummary().getOriginalFees(),new Money("500"));
		assertEquals(loan.getLoanSummary().getFeesPaid(),new Money("200"));
		loan.waiveAmountDue(WaiveEnum.FEES);
		for(AccountActionDateEntity accountActionDateEntity : loan.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))){
				assertEquals(loanScheduleEntity.getMiscFee(),new Money("200"));
				assertEquals(loanScheduleEntity.getMiscFeePaid(),new Money("200"));
			}
		}
		assertEquals(loan.getLoanSummary().getOriginalFees(),new Money("200"));
		assertEquals(loan.getLoanSummary().getFeesPaid(),new Money("200"));
	}

	
	public void testWaiveMiscPenaltyAfterPayment() throws Exception {
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		accountBO=TestObjectFactory.getObject(LoanBO.class,accountBO.getAccountId());
		accountBO.setUserContext(TestObjectFactory.getUserContext());
		Date currentDate = new Date(System.currentTimeMillis());
		LoanBO loan = (LoanBO) accountBO;
		loan.applyCharge(Short.valueOf("-2"),Double.valueOf("200"));
		TestObjectFactory.updateObject(loan);
		for(AccountActionDateEntity accountActionDateEntity : loan.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))){
				assertEquals(loanScheduleEntity.getMiscPenalty(),new Money("200"));
				assertEquals(loanScheduleEntity.getMiscPenaltyPaid(),new Money());
			}
		}
		assertEquals(loan.getLoanSummary().getOriginalPenalty(),new Money("200"));
		assertEquals(loan.getLoanSummary().getPenaltyPaid(),new Money());
		List<AccountActionDateEntity> accntActionDates = new ArrayList<AccountActionDateEntity>();
		accntActionDates.addAll(loan.getAccountActionDates());
		PaymentData paymentData = TestObjectFactory.getLoanAccountPaymentData(
				accntActionDates,
				TestObjectFactory.getMoneyForMFICurrency(200), null, accountBO
						.getPersonnel(), "receiptNum", Short.valueOf("1"),
				currentDate, currentDate);
		loan.applyPayment(paymentData);
		TestObjectFactory.updateObject(loan);
		for(AccountActionDateEntity accountActionDateEntity : loan.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))){
				assertEquals(loanScheduleEntity.getMiscPenalty(),new Money("200"));
				assertEquals(loanScheduleEntity.getMiscPenaltyPaid(),new Money("200"));
			}
		}
		assertEquals(loan.getLoanSummary().getOriginalPenalty(),new Money("200"));
		assertEquals(loan.getLoanSummary().getPenaltyPaid(),new Money("200"));
		loan.applyCharge(Short.valueOf("-2"),Double.valueOf("300"));
		TestObjectFactory.updateObject(loan);
		for(AccountActionDateEntity accountActionDateEntity : loan.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))){
				assertEquals(loanScheduleEntity.getMiscPenalty(),new Money("500"));
				assertEquals(loanScheduleEntity.getMiscPenaltyPaid(),new Money("200"));
			}
		}
		assertEquals(loan.getLoanSummary().getOriginalPenalty(),new Money("500"));
		assertEquals(loan.getLoanSummary().getPenaltyPaid(),new Money("200"));
		loan.waiveAmountDue(WaiveEnum.PENALTY);
		for(AccountActionDateEntity accountActionDateEntity : loan.getAccountActionDates()){
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity)accountActionDateEntity;
			if(loanScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))){
				assertEquals(loanScheduleEntity.getMiscPenalty(),new Money("200"));
				assertEquals(loanScheduleEntity.getMiscPenaltyPaid(),new Money("200"));
			}
		}
		assertEquals(loan.getLoanSummary().getOriginalPenalty(),new Money("200"));
		assertEquals(loan.getLoanSummary().getPenaltyPaid(),new Money("200"));
	}

	public static void setFeeAmount(
			AccountFeesActionDetailEntity accountFeesActionDetailEntity,
			Money feeAmount) {
		((LoanFeeScheduleEntity) accountFeesActionDetailEntity)
				.setFeeAmount(feeAmount);
	}

	public static void setFeeAmountPaid(
			AccountFeesActionDetailEntity accountFeesActionDetailEntity,
			Money feeAmountPaid) {
		((LoanFeeScheduleEntity) accountFeesActionDetailEntity)
				.setFeeAmountPaid(feeAmountPaid);
	}
	
	public static void setActionDate(
			AccountActionDateEntity accountActionDateEntity,
			java.sql.Date actionDate) {
		((LoanScheduleEntity) accountActionDateEntity)
				.setActionDate(actionDate);
	}

	public static void setPaymentStatus(
			AccountActionDateEntity accountActionDateEntity, Short paymentStatus) {
		((LoanScheduleEntity) accountActionDateEntity)
				.setPaymentStatus(paymentStatus);
	}
	
	public static void setDisbursementDate(
			AccountBO account,Date disbursementDate) {
		((LoanBO)account).setDisbursementDate(disbursementDate);
	}
	
	public void testAddLoanDetailsForDisbursal() {
		LoanBO loan = (LoanBO) createLoanAccount();
		loan.setLoanAmount(TestObjectFactory.getMoneyForMFICurrency(100));
		loan.setNoOfInstallments(Short.valueOf("5"));
		InterestTypesEntity interestType = new InterestTypesEntity(
				InterestTypeConstants.FLATINTERST);
		loan.setInterestType(interestType);
		List<LoanBO> loanWithDisbursalDate = new ArrayList<LoanBO>();
		loanWithDisbursalDate.add(loan);
		CollectionSheetBO collSheet = new CollectionSheetBO();
		collSheet.addLoanDetailsForDisbursal(loanWithDisbursalDate);
		CollSheetCustBO collectionSheetCustomer = collSheet
				.getCollectionSheetCustomerForCustomerId(group.getCustomerId());
		assertNotNull(collectionSheetCustomer);
		assertEquals(collectionSheetCustomer.getLoanDetailsForAccntId(
				loan.getAccountId()).getTotalNoOfInstallments(), Short
				.valueOf("5"));
	}

	public void testLoanPerfObject() throws PersistenceException {
		java.sql.Date currentDate = new java.sql.Date(System
				.currentTimeMillis());
		createInitialObjects();
		accountBO = getLoanAccount(client, meeting);
		LoanBO loanBO = (LoanBO) accountBO;
		LoanPerformanceHistoryEntity loanPerformanceHistory = loanBO
				.getPerformanceHistory();
		loanPerformanceHistory.setNoOfPayments(Integer.valueOf("3"));
		loanPerformanceHistory.setLoanMaturityDate(currentDate);
		TestObjectFactory.updateObject(loanBO);
		loanBO = (LoanBO) new AccountPersistence().getAccount(loanBO
				.getAccountId());
		assertEquals(Integer.valueOf("3"), loanBO.getPerformanceHistory()
				.getNoOfPayments());
		assertEquals(currentDate, loanBO.getPerformanceHistory()
				.getLoanMaturityDate());
	}

	public void testSuccessRemoveFees() throws Exception {
		accountBO = getLoanAccount();
		UserContext uc = TestObjectFactory.getUserContext();
		Set<AccountFeesEntity> accountFeesEntitySet = accountBO
				.getAccountFees();
		((LoanBO) accountBO).getLoanOffering().setPrinDueLastInst(false);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("2")))
				((LoanScheduleEntity) accountActionDateEntity)
						.setMiscFee(new Money("20.3"));
		}
		Iterator itr = accountFeesEntitySet.iterator();
		while (itr.hasNext())
			accountBO.removeFees(((AccountFeesEntity) itr.next()).getFees()
					.getFeeId(), uc.getId());

		HibernateUtil.getTransaction().commit();
		for (AccountFeesEntity accountFeesEntity : accountFeesEntitySet) {
			assertEquals(accountFeesEntity.getFeeStatus(),
					AccountConstants.INACTIVE_FEES);
		}
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		for (LoanActivityEntity accountNonTrxnEntity : ((LoanBO) accountBO)
				.getLoanActivityDetails()) {
			assertEquals(loanSummaryEntity.getOriginalFees().subtract(
					loanSummaryEntity.getFeesPaid()), accountNonTrxnEntity
					.getFeeOutstanding());
			assertEquals(loanSummaryEntity.getOriginalPrincipal().subtract(
					loanSummaryEntity.getPrincipalPaid()), accountNonTrxnEntity
					.getPrincipalOutstanding());
			assertEquals(loanSummaryEntity.getOriginalInterest().subtract(
					loanSummaryEntity.getInterestPaid()), accountNonTrxnEntity
					.getInterestOutstanding());
			assertEquals(loanSummaryEntity.getOriginalPenalty().subtract(
					loanSummaryEntity.getPenaltyPaid()), accountNonTrxnEntity
					.getPenaltyOutstanding());
			break;
		}
		for (AccountActionDateEntity accountActionDate : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDate;
			if (accountActionDate.getInstallmentId().equals(Short.valueOf("1")))
				assertEquals(new Money("212.0"), loanScheduleEntity
						.getTotalDueWithFees());
			else if (loanScheduleEntity.getInstallmentId().equals(
					Short.valueOf("2")))
				assertEquals(new Money("133.0"), loanScheduleEntity
						.getTotalDueWithFees());
			else if (loanScheduleEntity.getInstallmentId().equals(
					Short.valueOf("6")))
				assertEquals(new Money("111.3"), loanScheduleEntity
						.getTotalDueWithFees());
			else
				assertEquals(new Money("112.0"), loanScheduleEntity
						.getTotalDueWithFees());
		}

	}

	public void testCreateLoanAccountWithIntersetDeductedAtDisbursementFailure()throws Exception{
		
		createInitialCustomers();
		MeetingBO meeting = TestObjectFactory.createLoanMeeting(group
				.getCustomerMeeting().getMeeting());
		
		 loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("1"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
			List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 1);
			MifosCurrency currency = TestObjectFactory.getCurrency();
		 try{
			 new LoanBO(TestObjectFactory.getUserContext(), loanOffering,
						group, AccountState.LOANACC_PARTIALAPPLICATION,
						new Money(currency, "300.0"), Short.valueOf("1"),
						meetingDates.get(0), true, 10.0, (short) 0, new FundBO(),
						new ArrayList<FeeView>(),null);
			 
			 fail();
		 }
		 catch (AccountException e) {
			assertTrue(true);
		}

		
	}

	/**
	 * Like
	 * {@link #createLoanAccountWithDisbursement(String, CustomerBO, AccountState, Date, LoanOfferingBO, int, Short)}
	 * but differs in various ways.
	 * @param globalNum Currently ignored (TODO: remove it or honor it)
	 */
	public static LoanBO createLoanAccount(String globalNum,
			CustomerBO customer, AccountState state, Date startDate,
			LoanOfferingBO loanOfering) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer
				.getCustomerMeeting().getMeeting());
		List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);

		LoanBO loan;
		MifosCurrency currency = TestObjectFactory.getCurrency();
		try {
			loan = new LoanBO(TestObjectFactory.getUserContext(), loanOfering,
					customer, state,
					new Money(currency, "300.0"), Short.valueOf("6"),
					meetingDates.get(0), true, 0.0, (short) 0, new FundBO(),
					new ArrayList<FeeView>(),null);
		}
		catch (ApplicationException e) {
			throw new RuntimeException(e);
		}
		FeeBO maintanenceFee = TestObjectFactory.createPeriodicAmountFee(
				"Mainatnence Fee", FeeCategory.LOAN, "100",
				RecurrenceType.WEEKLY, Short.valueOf("1"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(loan,
				maintanenceFee, ((AmountFeeBO) maintanenceFee).getFeeAmount()
						.getAmountDoubleValue());
		TestAccountFeesEntity.addAccountFees(accountPeriodicFee, loan);
		loan.setLoanMeeting(meeting);
		short i = 0;
		for (Date date : meetingDates) {
			LoanScheduleEntity actionDate = (LoanScheduleEntity) loan
					.getAccountActionDate(++i);
			actionDate.setPrincipal(new Money(currency, "100.0"));
			actionDate.setInterest(new Money(currency, "12.0"));
			actionDate.setActionDate(new java.sql.Date(date.getTime()));
			actionDate.setPaymentStatus(PaymentStatus.UNPAID.getValue());
			TestAccountActionDateEntity.addAccountActionDate(actionDate,loan);

			AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(
					actionDate, maintanenceFee, accountPeriodicFee, new Money(
							currency, "100.0"));
			setFeeAmountPaid(accountFeesaction,new Money(currency, "0.0"));
			actionDate.addAccountFeesAction(accountFeesaction);
		}
		loan.setCreatedBy(Short.valueOf("1"));
		loan.setCreatedDate(new Date(System.currentTimeMillis()));

		setLoanSummary(loan, currency);
		return loan;
	}

	private static void setLoanSummary(LoanBO loan, MifosCurrency currency) {
		LoanSummaryEntity loanSummary = loan.getLoanSummary();
		loanSummary.setOriginalPrincipal(new Money(currency, "300.0"));
		loanSummary.setOriginalInterest(new Money(currency, "36.0"));
	}
	
	/**
	 * Like
	 * {@link #createLoanAccount(String, CustomerBO, AccountState, Date, LoanOfferingBO)}
	 * but differs in various ways.
	 * @param globalNum Currently ignored (TODO: remove it or honor it)
	 */
	public static LoanBO createLoanAccountWithDisbursement(String globalNum,
			CustomerBO customer, AccountState state, Date startDate,
			LoanOfferingBO loanOfering, int disbursalType,
			Short noOfInstallments) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer
				.getCustomerMeeting().getMeeting());
		List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);

		LoanBO loan;
		MifosCurrency currency = TestObjectFactory.getCurrency();
		try {
			loan = new LoanBO(TestObjectFactory.getUserContext(), loanOfering,
					customer, state,
					new Money(currency, "300.0"), noOfInstallments,
					meetingDates.get(0), false, 10.0, (short) 0, new FundBO(),
					new ArrayList<FeeView>(),null);
		}
		catch (ApplicationException e) {
			throw new RuntimeException(e);
		}
		FeeBO maintanenceFee = TestObjectFactory.createPeriodicAmountFee(
				"Mainatnence Fee", FeeCategory.LOAN, "100",
				RecurrenceType.WEEKLY, Short.valueOf("1"));
		AccountFeesEntity accountPeriodicFee = new AccountFeesEntity(loan,
				maintanenceFee, new Double("10.0"));
		TestAccountFeesEntity.addAccountFees(accountPeriodicFee, loan);
		AccountFeesEntity accountDisbursementFee = null;
		FeeBO disbursementFee = null;
		AccountFeesEntity accountDisbursementFee2 = null;
		FeeBO disbursementFee2 = null;

		if (disbursalType == 1 || disbursalType == 2) {
			disbursementFee = TestObjectFactory.createOneTimeAmountFee(
					"Disbursement Fee 1", FeeCategory.LOAN, "10",
					FeePayment.TIME_OF_DISBURSMENT);
			accountDisbursementFee = new AccountFeesEntity(loan,
					disbursementFee, new Double("10.0"));
			TestAccountFeesEntity.addAccountFees(accountDisbursementFee, loan);

			disbursementFee2 = TestObjectFactory.createOneTimeAmountFee(
					"Disbursement Fee 2", FeeCategory.LOAN, "20",
					FeePayment.TIME_OF_DISBURSMENT);
			accountDisbursementFee2 = new AccountFeesEntity(loan,
					disbursementFee2, new Double("20.0"));
			TestAccountFeesEntity.addAccountFees(accountDisbursementFee2, loan);
		}
		loan.setLoanMeeting(meeting);

		if (disbursalType == 2)// 2-Interest At Disbursment
		{
			loan.setInterestDeductedAtDisbursement(true);
			meetingDates = TestObjectFactory.getMeetingDates(loan
					.getLoanMeeting(), 6);
			short i = 0;
			for (Date date : meetingDates) {
				if (i == 0) {
					i++;
					loan.setDisbursementDate(date);
					LoanScheduleEntity actionDate = (LoanScheduleEntity) loan
							.getAccountActionDate(i);
					actionDate.setActionDate(new java.sql.Date(date.getTime()));
					actionDate.setInterest(new Money(currency, "12.0"));
					actionDate
							.setPaymentStatus(PaymentStatus.UNPAID.getValue());
					TestAccountActionDateEntity.addAccountActionDate(actionDate,loan);

					// periodic fee
					AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(
							actionDate, maintanenceFee, accountPeriodicFee,
							new Money(currency, "10.0"));
					setFeeAmountPaid(accountFeesaction,new Money(currency,
							"0.0"));
					actionDate.addAccountFeesAction(accountFeesaction);

					// dibursement fee one
					AccountFeesActionDetailEntity accountFeesaction1 = new LoanFeeScheduleEntity(
							actionDate, disbursementFee,
							accountDisbursementFee, new Money(currency, "10.0"));

					setFeeAmountPaid(accountFeesaction1,new Money(currency,
							"0.0"));
					actionDate.addAccountFeesAction(accountFeesaction1);

					// disbursementfee2
					AccountFeesActionDetailEntity accountFeesaction2 = new LoanFeeScheduleEntity(
							actionDate, disbursementFee2,
							accountDisbursementFee2,
							new Money(currency, "20.0"));
					setFeeAmountPaid(accountFeesaction2,new Money(currency,
							"0.0"));
					actionDate.addAccountFeesAction(accountFeesaction2);

					continue;
				}
				i++;
				LoanScheduleEntity actionDate = (LoanScheduleEntity) loan
						.getAccountActionDate(i);
				actionDate.setActionDate(new java.sql.Date(date.getTime()));
				actionDate.setPrincipal(new Money(currency, "100.0"));
				actionDate.setInterest(new Money(currency, "12.0"));
				actionDate.setPaymentStatus(PaymentStatus.UNPAID.getValue());
				TestAccountActionDateEntity.addAccountActionDate(actionDate,loan);
				AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(
						actionDate, maintanenceFee, accountPeriodicFee,
						new Money(currency, "100.0"));
				setFeeAmountPaid(accountFeesaction,new Money(currency, "0.0"));
				actionDate.addAccountFeesAction(accountFeesaction);
			}

		} else if (disbursalType == 1 || disbursalType == 3) {
			loan.setInterestDeductedAtDisbursement(false);
			meetingDates = TestObjectFactory.getMeetingDates(loan
					.getLoanMeeting(), 6);

			short i = 0;
			for (Date date : meetingDates) {

				if (i == 0) {
					i++;
					loan.setDisbursementDate(date);
					continue;
				}
				LoanScheduleEntity actionDate = (LoanScheduleEntity) loan
						.getAccountActionDate(i++);
				actionDate.setActionDate(new java.sql.Date(date.getTime()));
				actionDate.setPrincipal(new Money(currency, "100.0"));
				actionDate.setInterest(new Money(currency, "12.0"));
				actionDate.setPaymentStatus(PaymentStatus.UNPAID.getValue());
				TestAccountActionDateEntity.addAccountActionDate(actionDate,loan);
				AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(
						actionDate, maintanenceFee, accountPeriodicFee,
						new Money(currency, "100.0"));
				setFeeAmountPaid(accountFeesaction,new Money(currency, "0.0"));
				actionDate.addAccountFeesAction(accountFeesaction);
			}
		}
		GracePeriodTypeEntity gracePeriodType = null;
		try {
			gracePeriodType = new GracePeriodTypeEntity(GraceTypeConstants
					.getGraceTypeConstants(Short.valueOf("1")));
		} catch (PropertyNotFoundException e) {
			throw new RuntimeException(e);
		}
		loan.setGracePeriodType(gracePeriodType);
		loan.setCreatedBy(Short.valueOf("1"));

		CollateralTypeEntity collateralType = new CollateralTypeEntity(Short
				.valueOf("1"));
		loan.setCollateralType(collateralType);

		InterestTypesEntity interestTypes = null;
		try {
			interestTypes = new InterestTypesEntity(InterestTypeConstants
					.getInterestTypeConstants(Short.valueOf("1")));
		} catch (PropertyNotFoundException e) {
			throw new RuntimeException(e);
		}
		loan.setInterestType(interestTypes);
		loan.setInterestRate(10.0);
		loan.setCreatedDate(new Date(System.currentTimeMillis()));

		setLoanSummary(loan, currency);
		return loan;
	}

	public void testHandleArrearsAging_Create() throws Exception {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
		calendar.add(calendar.WEEK_OF_MONTH, -2);
		java.sql.Date secondWeekDate = new java.sql.Date(calendar
				.getTimeInMillis());

		accountBO = getLoanAccount();
		for (AccountActionDateEntity installment : accountBO
				.getAccountActionDates()) {
			if (installment.getInstallmentId().intValue() == 1) {
				((LoanScheduleEntity)installment).setActionDate(secondWeekDate);
			}
		}
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertNull(((LoanBO) accountBO).getLoanArrearsAgingEntity());

		((LoanBO) accountBO).handleArrearsAging();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		LoanArrearsAgingEntity agingEntity = ((LoanBO) accountBO)
				.getLoanArrearsAgingEntity();

		assertNotNull(agingEntity);
		assertEquals(new Money("100"), agingEntity.getOverduePrincipal());
		assertEquals(new Money("12"), agingEntity.getOverdueInterest());
		assertEquals(new Money("112"), agingEntity.getOverdueBalance());
		assertEquals(Short.valueOf("14"), agingEntity.getDaysInArrears());

		assertEquals(((LoanBO) accountBO).getLoanSummary().getPrincipalDue(),
				agingEntity.getUnpaidPrincipal());
		assertEquals(((LoanBO) accountBO).getLoanSummary().getInterestDue(),
				agingEntity.getUnpaidInterest());
		assertEquals(((LoanBO) accountBO).getLoanSummary().getPrincipalDue()
				.add(((LoanBO) accountBO).getLoanSummary().getInterestDue()),
				agingEntity.getUnpaidBalance());

		assertEquals(((LoanBO) accountBO).getTotalPrincipalAmountInArrears(),
				agingEntity.getOverduePrincipal());
		assertEquals(((LoanBO) accountBO).getTotalInterestAmountInArrears(),
				agingEntity.getOverdueInterest());
		assertEquals(((LoanBO) accountBO).getTotalPrincipalAmountInArrears()
				.add(((LoanBO) accountBO).getTotalInterestAmountInArrears()),
				agingEntity.getOverdueBalance());
	}

	public void testHandleArrearsAging_Update() throws Exception {
		testHandleArrearsAging_Create();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
		calendar.add(calendar.WEEK_OF_MONTH, -1);
		java.sql.Date lastWeekDate = new java.sql.Date(calendar
				.getTimeInMillis());

		for (AccountActionDateEntity installment : accountBO
				.getAccountActionDates()) {
			if (installment.getInstallmentId().intValue() == 2) {
				((LoanScheduleEntity)installment).setActionDate(lastWeekDate);
			}
		}
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());

		assertNotNull(((LoanBO) accountBO).getLoanArrearsAgingEntity());

		((LoanBO) accountBO).handleArrearsAging();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		LoanArrearsAgingEntity agingEntity = ((LoanBO) accountBO)
				.getLoanArrearsAgingEntity();

		assertEquals(new Money("200"), agingEntity.getOverduePrincipal());
		assertEquals(new Money("24"), agingEntity.getOverdueInterest());
		assertEquals(new Money("224"), agingEntity.getOverdueBalance());
		assertEquals(Short.valueOf("14"), agingEntity.getDaysInArrears());

		assertEquals(((LoanBO) accountBO).getLoanSummary().getPrincipalDue(),
				agingEntity.getUnpaidPrincipal());
		assertEquals(((LoanBO) accountBO).getLoanSummary().getInterestDue(),
				agingEntity.getUnpaidInterest());
		assertEquals(((LoanBO) accountBO).getLoanSummary().getPrincipalDue()
				.add(((LoanBO) accountBO).getLoanSummary().getInterestDue()),
				agingEntity.getUnpaidBalance());

		assertEquals(((LoanBO) accountBO).getTotalPrincipalAmountInArrears(),
				agingEntity.getOverduePrincipal());
		assertEquals(((LoanBO) accountBO).getTotalInterestAmountInArrears(),
				agingEntity.getOverdueInterest());
		assertEquals(((LoanBO) accountBO).getTotalPrincipalAmountInArrears()
				.add(((LoanBO) accountBO).getTotalInterestAmountInArrears()),
				agingEntity.getOverdueBalance());
	}

	public void testUpdateLoanForLogging() throws ApplicationException,
			SystemException {
		Date newDate = incrementCurrentDate(14);
		accountBO = getLoanAccount();
		accountBO.setUserContext(TestObjectFactory.getUserContext());
		HibernateUtil.getInterceptor().createInitialValueMap(accountBO);

		LoanBO loanBO = ((LoanBO) accountBO);
		((LoanBO) accountBO).updateLoan(false, loanBO.getLoanAmount(), loanBO
				.getInterestRate(), loanBO.getNoOfInstallments(), newDate,
				Short.valueOf("2"), Integer.valueOf("2"), "Added note", null,null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(
				EntityType.LOAN.getValue(), accountBO.getAccountId());
		assertEquals(1, auditLogList.size());
		assertEquals(EntityType.LOAN.getValue(), auditLogList.get(0)
				.getEntityType());
		assertEquals(4, auditLogList.get(0).getAuditLogRecords().size());
		for (AuditLogRecord auditLogRecord : auditLogList.get(0)
				.getAuditLogRecords()) {
			if (auditLogRecord.getFieldName().equalsIgnoreCase(
					"Collateral Notes")) {
				assertEquals("-", auditLogRecord.getOldValue());
				assertEquals("Added note", auditLogRecord.getNewValue());
			} else if (auditLogRecord.getFieldName().equalsIgnoreCase(
					"Service Charge deducted At Disbursement")) {
				assertEquals("1", auditLogRecord.getOldValue());
				assertEquals("0", auditLogRecord.getNewValue());
			}
		}
		TestObjectFactory.cleanUpChangeLog();

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

	public void testMakeEarlyRepaymentForCurrentDateLiesBetweenInstallmentDates()
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

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		assertEquals(accountBO.getAccountState().getId(), Short.valueOf(
				AccountStates.LOANACC_OBLIGATIONSMET));
		LoanSummaryEntity loanSummaryEntity = loanBO.getLoanSummary();
		assertEquals(totalRepaymentAmount, loanSummaryEntity.getPrincipalPaid()
				.add(loanSummaryEntity.getFeesPaid()).add(
						loanSummaryEntity.getInterestPaid()).add(
						loanSummaryEntity.getPenaltyPaid()));
		assertEquals(1, accountBO.getAccountPayments().size());
		/// Change this to more clearly separate what we are testing for from the
		 // machinery needed to get that data?
		 //
		for (AccountPaymentEntity accountPaymentEntity : accountBO
				.getAccountPayments()) {
			assertEquals(6, accountPaymentEntity.getAccountTrxns().size());
			for (AccountTrxnEntity accountTrxnEntity : accountPaymentEntity
					.getAccountTrxns()) {
				if (accountTrxnEntity.getInstallmentId().equals(
						Short.valueOf("1"))
						|| accountTrxnEntity.getInstallmentId().equals(
								Short.valueOf("2"))) {
					assertEquals(new Money("212.0"), accountTrxnEntity
							.getAmount());
					LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
					assertEquals(new Money("100.0"), loanTrxnDetailEntity
							.getPrincipalAmount());
					assertEquals(new Money("12.0"), loanTrxnDetailEntity
							.getInterestAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getMiscFeeAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getMiscPenaltyAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getPenaltyAmount());
					assertEquals(6, accountTrxnEntity
							.getFinancialTransactions().size());
					assertEquals(1, loanTrxnDetailEntity.getFeesTrxnDetails()
							.size());
					for (FeesTrxnDetailEntity feesTrxnDetailEntity : loanTrxnDetailEntity
							.getFeesTrxnDetails()) {
						assertEquals(new Money("100.0"), feesTrxnDetailEntity
								.getFeeAmount());
					}
				} else {
					LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
					assertEquals(new Money("100.0"), accountTrxnEntity
							.getAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getInterestAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getMiscFeeAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getMiscPenaltyAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getPenaltyAmount());
					assertEquals(2, accountTrxnEntity
							.getFinancialTransactions().size());
					assertEquals(0, loanTrxnDetailEntity.getFeesTrxnDetails()
							.size());
				}
			}
		}
	}

	public void testMakeEarlyRepaymentForCurrentDateSameAsInstallmentDate()
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

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		assertEquals(accountBO.getAccountState().getId(), Short.valueOf(
				AccountStates.LOANACC_OBLIGATIONSMET));
		LoanSummaryEntity loanSummaryEntity = loanBO.getLoanSummary();
		assertEquals(totalRepaymentAmount, loanSummaryEntity.getPrincipalPaid()
				.add(loanSummaryEntity.getFeesPaid()).add(
						loanSummaryEntity.getInterestPaid()).add(
						loanSummaryEntity.getPenaltyPaid()));
		assertEquals(1, accountBO.getAccountPayments().size());
		
		 //Change this to more clearly separate what we are testing for from the
		 //machinery needed to get that data?
		 
		for (AccountPaymentEntity accountPaymentEntity : accountBO
				.getAccountPayments()) {
			assertEquals(6, accountPaymentEntity.getAccountTrxns().size());
			for (AccountTrxnEntity accountTrxnEntity : accountPaymentEntity
					.getAccountTrxns()) {
				if (accountTrxnEntity.getInstallmentId().equals(
						Short.valueOf("1"))) {
					assertEquals(new Money("212.0"), accountTrxnEntity
							.getAmount());
					LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
					assertEquals(new Money("100.0"), loanTrxnDetailEntity
							.getPrincipalAmount());
					assertEquals(new Money("12.0"), loanTrxnDetailEntity
							.getInterestAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getMiscFeeAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getMiscPenaltyAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getPenaltyAmount());
					assertEquals(6, accountTrxnEntity
							.getFinancialTransactions().size());
					assertEquals(1, loanTrxnDetailEntity.getFeesTrxnDetails()
							.size());
					for (FeesTrxnDetailEntity feesTrxnDetailEntity : loanTrxnDetailEntity
							.getFeesTrxnDetails()) {
						assertEquals(new Money("100.0"), feesTrxnDetailEntity
								.getFeeAmount());
					}
				} else {
					LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
					assertEquals(new Money("100.0"), accountTrxnEntity
							.getAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getInterestAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getMiscFeeAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getMiscPenaltyAmount());
					assertEquals(new Money(), loanTrxnDetailEntity
							.getPenaltyAmount());
					assertEquals(2, accountTrxnEntity
							.getFinancialTransactions().size());
					assertEquals(0, loanTrxnDetailEntity.getFeesTrxnDetails()
							.size());
				}
			}
		}
	}

	public void testMakeEarlyRepaymentOnPartiallyPaidAccount() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		UserContext userContext = TestObjectFactory.getUserContext();
		userContext.setLocaleId(null);
		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("1"));
		feeViewList.add(new FeeView(userContext, periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(userContext, upfrontFee));

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, 1.2, (short) 0, new FundBO(), feeViewList,getCustomFields());
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());
		assertEquals(1, accountBO.getAccountCustomFields().size());
		HibernateUtil.closeSession();

		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("160"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		assertEquals(new Money(), ((LoanBO) accountBO).getTotalPaymentDue());
		LoanBO loanBO = (LoanBO) accountBO;
		UserContext uc = TestObjectFactory.getUserContext();
		Money totalRepaymentAmount = loanBO.getTotalEarlyRepayAmount();
		assertEquals(new Money("300.1"), totalRepaymentAmount);
		loanBO.makeEarlyRepayment(loanBO.getTotalEarlyRepayAmount(), null,
				null, "1", uc.getId());

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		assertEquals(accountBO.getAccountState().getId(), Short.valueOf(
				AccountStates.LOANACC_OBLIGATIONSMET));
		LoanSummaryEntity loanSummaryEntity = loanBO.getLoanSummary();
		assertEquals(new Money(), loanSummaryEntity.getPrincipalDue().add(
				loanSummaryEntity.getFeesDue()).add(
				loanSummaryEntity.getInterestDue()).add(
				loanSummaryEntity.getPenaltyDue()));
		assertEquals(2, accountBO.getAccountPayments().size());
		AccountPaymentEntity accountPaymentEntity = (AccountPaymentEntity) Arrays
				.asList(accountBO.getAccountPayments().toArray()).get(0);

		//Change this to more clearly separate what we are testing for from the
		// machinery needed to get that data?
		
		for (AccountTrxnEntity accountTrxnEntity : accountPaymentEntity
				.getAccountTrxns()) {
			if (accountTrxnEntity.getInstallmentId().equals(Short.valueOf("6"))) {
				LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
				assertEquals(new Money("45.5"), accountTrxnEntity.getAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getInterestAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscFeeAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscPenaltyAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getPenaltyAmount());
				assertEquals(4, accountTrxnEntity.getFinancialTransactions()
						.size());
				assertEquals(0, loanTrxnDetailEntity.getFeesTrxnDetails()
						.size());
			} else if (accountTrxnEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
				assertEquals(new Money("51.0"), accountTrxnEntity.getAmount());
				assertEquals(new Money("0.1"), loanTrxnDetailEntity
						.getInterestAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscFeeAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscPenaltyAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getPenaltyAmount());
				assertEquals(4, accountTrxnEntity.getFinancialTransactions()
						.size());
				assertEquals(0, loanTrxnDetailEntity.getFeesTrxnDetails()
						.size());
			} else {
				LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
				assertEquals(new Money("50.9"), accountTrxnEntity.getAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getInterestAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscFeeAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscPenaltyAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getPenaltyAmount());
				assertEquals(2, accountTrxnEntity.getFinancialTransactions()
						.size());
				assertEquals(0, loanTrxnDetailEntity.getFeesTrxnDetails()
						.size());
			}
		}
	}

	public void testMakeEarlyRepaymentOnPartiallyPaidPricipal()
			throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		UserContext userContext = TestObjectFactory.getUserContext();
		userContext.setLocaleId(null);
		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("1"));
		feeViewList.add(new FeeView(userContext, periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(userContext, upfrontFee));

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, 1.2, (short) 0, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HibernateUtil.closeSession();

		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("180.1"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		assertEquals(new Money(), ((LoanBO) accountBO).getTotalPaymentDue());
		LoanBO loanBO = (LoanBO) accountBO;
		UserContext uc = TestObjectFactory.getUserContext();
		Money totalRepaymentAmount = loanBO.getTotalEarlyRepayAmount();
		assertEquals(new Money("280.0"), totalRepaymentAmount);
		loanBO.makeEarlyRepayment(loanBO.getTotalEarlyRepayAmount(), null,
				null, "1", uc.getId());

		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		assertEquals(accountBO.getAccountState().getId(), Short.valueOf(
				AccountStates.LOANACC_OBLIGATIONSMET));
		LoanSummaryEntity loanSummaryEntity = loanBO.getLoanSummary();
		assertEquals(new Money(), loanSummaryEntity.getPrincipalDue().add(
				loanSummaryEntity.getFeesDue()).add(
				loanSummaryEntity.getInterestDue()).add(
				loanSummaryEntity.getPenaltyDue()));
		assertEquals(2, accountBO.getAccountPayments().size());
		AccountPaymentEntity accountPaymentEntity = (AccountPaymentEntity) Arrays
				.asList(accountBO.getAccountPayments().toArray()).get(0);
		assertEquals(6, accountPaymentEntity.getAccountTrxns().size());
		
		 //Change this to more clearly separate what we are testing for from the
		 //machinery needed to get that data?
		 
		for (AccountTrxnEntity accountTrxnEntity : accountPaymentEntity
				.getAccountTrxns()) {
			if (accountTrxnEntity.getInstallmentId().equals(Short.valueOf("6"))) {
				LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
				assertEquals(new Money("45.5"), accountTrxnEntity.getAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getInterestAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscFeeAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscPenaltyAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getPenaltyAmount());
				assertEquals(4, accountTrxnEntity.getFinancialTransactions()
						.size());
				assertEquals(0, loanTrxnDetailEntity.getFeesTrxnDetails()
						.size());
			} else if (accountTrxnEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
				assertEquals(new Money("30.9"), accountTrxnEntity.getAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getInterestAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscFeeAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscPenaltyAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getPenaltyAmount());
				assertEquals(2, accountTrxnEntity.getFinancialTransactions()
						.size());
				assertEquals(0, loanTrxnDetailEntity.getFeesTrxnDetails()
						.size());
			} else {
				LoanTrxnDetailEntity loanTrxnDetailEntity = (LoanTrxnDetailEntity) accountTrxnEntity;
				assertEquals(new Money("50.9"), accountTrxnEntity.getAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getInterestAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscFeeAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getMiscPenaltyAmount());
				assertEquals(new Money(), loanTrxnDetailEntity
						.getPenaltyAmount());
				assertEquals(2, accountTrxnEntity.getFinancialTransactions()
						.size());
				assertEquals(0, loanTrxnDetailEntity.getFeesTrxnDetails()
						.size());
			}
		}
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

	public void testDisburseLoanWithFeeAtDisbursement() throws Exception {
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
		LoanSummaryEntity loanSummary = ((LoanBO) accountBO).getLoanSummary();
		for (LoanActivityEntity loanActivityEntity : ((LoanBO) accountBO)
				.getLoanActivityDetails()) {
			if (loanActivityEntity.getComments().equalsIgnoreCase(
					"Loan Disbursal")) {
				assertEquals(loanSummary.getOriginalPrincipal(),
						loanActivityEntity.getPrincipalOutstanding());
				assertEquals(loanSummary.getOriginalFees(), loanActivityEntity
						.getFeeOutstanding());
				break;
			}
		}
		accountBO = TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
	}

	public void testDisbursalLoanNoFeeOrInterestAtDisbursal() throws Exception {
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
			throws Exception {
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
		accountBO = getLoanAccountWithMiscFeeAndPenalty(Short.valueOf("3"),
				startDate, 3, new Money("20"), new Money("30"));

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

		Set<AccountActionDateEntity> actionDateEntities = accountBO
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(null, null, null, "20", "30", null, null, null,
				null, null, paymentsArray[0]);

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

	public void testGetTotalAmountDueForSingleInstallment() throws Exception {
		accountBO = getLoanAccount();

		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate((short) 1);
		((LoanScheduleEntity)accountActionDateEntity).setActionDate(offSetCurrentDate(1));

		accountBO = saveAndFetch(accountBO);
		assertEquals(((LoanBO) accountBO).getTotalAmountDue()
				.getAmountDoubleValue(), 424.0);
	}

	public void testGetTotalAmountDueWithPayment() throws Exception {
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

	public void testGetTotalAmountDueWithPaymentDone() throws Exception {
		accountBO = getLoanAccount();

		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate((short) 1);
		((LoanScheduleEntity)accountActionDateEntity).setActionDate(offSetCurrentDate(1));
		((LoanScheduleEntity)accountActionDateEntity).setPaymentStatus(PaymentStatus.PAID.getValue());

		accountBO = saveAndFetch(accountBO);

		assertEquals(((LoanBO) accountBO).getTotalAmountDue()
				.getAmountDoubleValue(), 212.0);
	}

	public void testGetTotalAmountDueForTwoInstallments() throws Exception {
		accountBO = getLoanAccount();
		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate((short) 1);
		((LoanScheduleEntity)accountActionDateEntity).setActionDate(offSetCurrentDate(2));
		AccountActionDateEntity accountActionDateEntity2 = accountBO
				.getAccountActionDate((short) 2);

		((LoanScheduleEntity)accountActionDateEntity2).setActionDate(offSetCurrentDate(1));

		accountBO = saveAndFetch(accountBO);

		assertEquals(((LoanBO) accountBO).getTotalAmountDue()
				.getAmountDoubleValue(), 636.0);
	}

	public void testGetOustandingBalance() {
		accountBO = getLoanAccount();
		assertEquals(((LoanBO) accountBO).getLoanSummary()
				.getOustandingBalance().getAmountDoubleValue(), 336.0);
	}

	public void testGetOustandingBalancewithPayment() throws Exception {
		accountBO = getLoanAccount();
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();

		loanSummaryEntity.setPrincipalPaid(new Money("20.0"));
		loanSummaryEntity.setInterestPaid(new Money("10.0"));

		accountBO = saveAndFetch(accountBO);

		assertEquals(((LoanBO) accountBO).getLoanSummary()
				.getOustandingBalance().getAmountDoubleValue(), 306.0);
	}

	public void testGetNextMeetingDateAsCurrentDate() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
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
		accountBO = accountPersistence.getAccount(accountBO.getAccountId());
		assertEquals(((LoanBO) accountBO).getNextMeetingDate().toString(),
				new java.sql.Date(currentDateCalendar.getTimeInMillis())
						.toString());
	}

	public void testGetNextMeetingDateAsFutureDate() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
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
		((LoanScheduleEntity)accountActionDateEntity).setActionDate(new java.sql.Date(
				currentDateCalendar.getTimeInMillis()));
		TestObjectFactory.updateObject(accountBO);

		accountBO = accountPersistence.getAccount(accountBO.getAccountId());
		assertEquals(((LoanBO) accountBO).getNextMeetingDate().toString(),
				accountBO.getAccountActionDate(Short.valueOf("2"))
						.getActionDate().toString());
	}

	public void testGetTotalAmountInArrearsForCurrentDateMeeting() {
		accountBO = getLoanAccount();
		assertEquals(((LoanBO) accountBO).getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
	}

	public void testGetTotalAmountInArrearsForSingleInstallmentDue()
			throws Exception {
		accountBO = getLoanAccount();
		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate((short) 1);
		((LoanScheduleEntity)accountActionDateEntity).setActionDate(offSetCurrentDate(1));
		accountBO = saveAndFetch(accountBO);
		assertEquals(((LoanBO) accountBO).getTotalAmountInArrears()
				.getAmountDoubleValue(), 212.0);
	}

	public void testGetTotalAmountInArrearsWithPayment() throws Exception {
		accountBO = getLoanAccount();

		LoanScheduleEntity accountActionDateEntity = 
			(LoanScheduleEntity) accountBO
				.getAccountActionDate((short) 1);
		accountActionDateEntity.setPrincipalPaid(new Money("20.0"));
		accountActionDateEntity.setInterestPaid(new Money("10.0"));
		accountActionDateEntity.setPenaltyPaid(new Money("5.0"));
		accountActionDateEntity.setActionDate(offSetCurrentDate(1));

		accountBO = saveAndFetch(accountBO);
		assertEquals(((LoanBO) accountBO).getTotalAmountInArrears()
				.getAmountDoubleValue(), 177.0);
	}

	public void testGetTotalAmountInArrearsWithPaymentDone() throws Exception {
		accountBO = getLoanAccount();

		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate(Short.valueOf("1"));
		((LoanScheduleEntity)accountActionDateEntity).setActionDate(offSetCurrentDate(1));
		((LoanScheduleEntity)accountActionDateEntity).setPaymentStatus(PaymentStatus.PAID.getValue());
		accountBO = saveAndFetch(accountBO);
		assertEquals(((LoanBO) accountBO).getTotalAmountInArrears()
				.getAmountDoubleValue(), 0.0);
	}

	public void testGetTotalAmountDueForTwoInstallmentsDue() throws Exception {
		accountBO = getLoanAccount();

		AccountActionDateEntity accountActionDateEntity = accountBO
				.getAccountActionDate((short) 1);
		((LoanScheduleEntity)accountActionDateEntity).setActionDate(offSetCurrentDate(2));
		AccountActionDateEntity accountActionDateEntity2 = accountBO
				.getAccountActionDate((short) 2);
		((LoanScheduleEntity)accountActionDateEntity2).setActionDate(offSetCurrentDate(1));

		accountBO = saveAndFetch(accountBO);

		assertEquals(((LoanBO) accountBO).getTotalAmountInArrears()
				.getAmountDoubleValue(), 424.0);
	}

	public void testChangedStatusForLastInstallmentPaid() throws Exception {
		accountBO = getLoanAccount();
		Date startDate = new Date(System.currentTimeMillis());
		java.sql.Date offSetDate = offSetCurrentDate(1);
		for (AccountActionDateEntity accountAction : accountBO
				.getAccountActionDates()) {
			((LoanScheduleEntity)accountAction).setActionDate(offSetDate);
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
		assertEquals(statusChangeHistorySize + 1, accountBO
				.getAccountStatusChangeHistory().size());
	}

	public void testChangedStatusOnPayment() throws AccountException,
			SystemException {
		accountBO = getLoanAccount();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
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
		loan.setInterestDeductedAtDisbursement(false);
		assertEquals(false, loan.isInterestDeductedAtDisbursement());

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
		loanBO.writeOff();
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loan = (LoanBO) accountBO;
		for (LoanActivityEntity loanActivityEntity : loan
				.getLoanActivityDetails()) {
			assertEquals(loanActivityEntity.getComments(), "Loan Written Off");
			assertEquals(new Timestamp(DateUtils
					.getCurrentDateWithoutTimeStamp().getTime()),
					loanActivityEntity.getTrxnCreatedDate());
			break;
		}
	}

	public void testGetAmountTobePaidAtdisburtail() throws Exception {

		Date startDate = new Date(System.currentTimeMillis());
		accountBO = getLoanAccount(Short.valueOf("3"), startDate, 2);
		assertEquals(new Money("52"), ((LoanBO) accountBO)
				.getAmountTobePaidAtdisburtail(startDate));

	}
	
	public void testWaivePenaltyChargeDue() throws Exception {
		accountBO = getLoanAccount();
		for (AccountActionDateEntity accountAction : ((LoanBO) accountBO)
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				accountActionDateEntity.setMiscPenalty(new Money("100"));
			}
		}
		((LoanBO) accountBO).getLoanSummary().setOriginalPenalty(
				new Money("100"));
		TestObjectFactory.updateObject(accountBO);

		HibernateUtil.closeSession();
		LoanBO loanBO = TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());

		UserContext userContext = TestObjectFactory.getUserContext();
		loanBO.setUserContext(userContext);
		loanBO.waiveAmountDue(WaiveEnum.PENALTY);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
		for (AccountActionDateEntity accountAction : loanBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			if (accountActionDateEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				assertEquals(new Money(), accountActionDateEntity
						.getMiscPenalty());
			}

		}

		Set<LoanActivityEntity> loanActivityDetailsSet = loanBO
				.getLoanActivityDetails();
		List<Object> objectList = Arrays.asList(loanActivityDetailsSet
				.toArray());
		LoanActivityEntity loanActivityEntity = (LoanActivityEntity) objectList
				.get(0);
		assertEquals(LoanConstants.PENALTY_WAIVED, loanActivityEntity.getComments());
		assertEquals(new Money("100"), loanActivityEntity.getPenalty());
		assertEquals(new Timestamp(DateUtils.getCurrentDateWithoutTimeStamp()
				.getTime()), loanActivityEntity.getTrxnCreatedDate());
		assertEquals(loanBO.getLoanSummary().getOriginalPenalty().subtract(
				loanBO.getLoanSummary().getPenaltyPaid()), loanActivityEntity
				.getPenaltyOutstanding());
	}

	public void testWaivePenaltyOverDue() throws Exception {
		accountBO = getLoanAccount();

		for (AccountActionDateEntity installment : ((LoanBO) accountBO)
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) installment;
			if (installment.getInstallmentId().intValue() == 1) {
				loanScheduleEntity.setMiscPenalty(new Money("100"));
			} else if (installment.getInstallmentId().intValue() == 2) {
				loanScheduleEntity.setMiscPenalty(new Money("100"));
			}
		}

		((LoanBO) accountBO).getLoanSummary().setOriginalPenalty(
				new Money("200"));
		TestObjectFactory.updateObject(accountBO);

		HibernateUtil.closeSession();
		LoanBO loanBO = TestObjectFactory.getObject(LoanBO.class,
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
				((LoanScheduleEntity)installment).setActionDate(lastWeekDate);
			} else if (installment.getInstallmentId().intValue() == 2) {
				((LoanScheduleEntity)installment).setActionDate(twoWeeksBeforeDate);
			}
		}
		TestObjectFactory.updateObject(loanBO);
		HibernateUtil.closeSession();
		loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
		UserContext userContext = TestObjectFactory.getUserContext();
		loanBO.setUserContext(userContext);
		loanBO.waiveAmountOverDue(WaiveEnum.PENALTY);
		TestObjectFactory.flushandCloseSession();
		loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
		for (AccountActionDateEntity accountAction : loanBO
				.getAccountActionDates()) {
			LoanScheduleEntity accountActionDateEntity = (LoanScheduleEntity) accountAction;
			assertEquals(new Money(), accountActionDateEntity.getMiscPenalty());
		}

		Set<LoanActivityEntity> loanActivityDetailsSet = loanBO
				.getLoanActivityDetails();
		List<Object> objectList = Arrays.asList(loanActivityDetailsSet
				.toArray());
		LoanActivityEntity loanActivityEntity = (LoanActivityEntity) objectList
				.get(0);
		assertEquals(new Timestamp(DateUtils.getCurrentDateWithoutTimeStamp()
				.getTime()), loanActivityEntity.getTrxnCreatedDate());
		assertEquals(new Money("200"), loanActivityEntity.getPenalty());
		assertEquals(loanBO.getLoanSummary().getOriginalPenalty().subtract(
				loanBO.getLoanSummary().getPenaltyPaid()), loanActivityEntity
				.getPenaltyOutstanding());
	}

	public void testWaiveFeeChargeDue() throws Exception {
		accountBO = getLoanAccount();
		HibernateUtil.closeSession();
		LoanBO loanBO = TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
		UserContext userContext = TestObjectFactory.getUserContext();
		loanBO.setUserContext(userContext);
		loanBO.waiveAmountDue(WaiveEnum.FEES);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
		
		 //Change this to more clearly separate what we are testing for from the
		 //machinery needed to get that data?
		 
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

		Set<LoanActivityEntity> loanActivityDetailsSet = loanBO
				.getLoanActivityDetails();
		List<Object> objectList = Arrays.asList(loanActivityDetailsSet
				.toArray());
		LoanActivityEntity loanActivityEntity = (LoanActivityEntity) objectList
				.get(0);
		assertEquals(LoanConstants.FEE_WAIVED, loanActivityEntity.getComments());
		assertEquals(new Money("100"), loanActivityEntity.getFee());
		assertEquals(new Timestamp(DateUtils.getCurrentDateWithoutTimeStamp()
				.getTime()), loanActivityEntity.getTrxnCreatedDate());
		assertEquals(loanBO.getLoanSummary().getOriginalFees().subtract(
				loanBO.getLoanSummary().getFeesPaid()), loanActivityEntity
				.getFeeOutstanding());

	}

	public void testWaiveFeeChargeOverDue() throws Exception {
		accountBO = getLoanAccount();
		HibernateUtil.closeSession();
		LoanBO loanBO = TestObjectFactory.getObject(LoanBO.class,
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
				((LoanScheduleEntity)installment).setActionDate(lastWeekDate);
			} else if (installment.getInstallmentId().intValue() == 2) {
				((LoanScheduleEntity)installment).setActionDate(twoWeeksBeforeDate);
			}
		}
		TestObjectFactory.updateObject(loanBO);
		HibernateUtil.closeSession();
		loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
		UserContext userContext = TestObjectFactory.getUserContext();
		loanBO.setUserContext(userContext);
		loanBO.waiveAmountOverDue(WaiveEnum.FEES);
		TestObjectFactory.flushandCloseSession();
		loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO
				.getAccountId());
		
		 //Change this to more clearly separate what we are testing for from the
		 //machinery needed to get that data?
		 
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

		Set<LoanActivityEntity> loanActivityDetailsSet = loanBO
				.getLoanActivityDetails();
		List<Object> objectList = Arrays.asList(loanActivityDetailsSet
				.toArray());
		LoanActivityEntity loanActivityEntity = (LoanActivityEntity) objectList
				.get(0);
		assertEquals(new Money("200"), loanActivityEntity.getFee());
		assertEquals(new Timestamp(DateUtils.getCurrentDateWithoutTimeStamp()
				.getTime()), loanActivityEntity.getTrxnCreatedDate());
		assertEquals(loanBO.getLoanSummary().getOriginalFees().subtract(
				loanBO.getLoanSummary().getFeesPaid()), loanActivityEntity
				.getFeeOutstanding());
	}

	public void testRegenerateFutureInstallments() throws Exception {
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
		AccountActionDateEntity accountActionDateEntity = accountBO
				.getDetailsOfNextInstallment();
		MeetingBO meeting = accountBO.getCustomer().getCustomerMeeting()
				.getMeeting();
		meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(
				(WeekDaysEntity) new MasterPersistence()
						.retrieveMasterEntity(WeekDay.THURSDAY.getValue(),
								WeekDaysEntity.class, null));
		meeting.setMeetingStartDate(DateUtils
				.getCalendarDate(accountActionDateEntity.getActionDate()
						.getTime()));
		List<java.util.Date> meetingDates = meeting.getAllDates(accountBO
				.getApplicableIdsForFutureInstallments().size() + 1);
		Calendar calendar = new GregorianCalendar();
		int dayOfWeek = calendar.get(calendar.DAY_OF_WEEK);
		if (dayOfWeek == 5)
			meetingDates.remove(0);
		((LoanBO) accountBO)
				.regenerateFutureInstallments((short) (accountActionDateEntity
						.getInstallmentId().intValue() + 1));
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				accountBO.getAccountId());
		
		 //Change this to more clearly separate what we are testing for from the
		 //machinery needed to get that data?
		 
		for (AccountActionDateEntity actionDateEntity : accountBO
				.getAccountActionDates()) {
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
	}

	public void testRegenerateFutureWhenDayScheduleChanges() throws Exception {
		createInitialCustomers();
		MeetingBO loanOfferingMeeting = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), loanOfferingMeeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(LoanBO.class,
				accountBO.getAccountId());
		AccountActionDateEntity accountActionDateEntity = accountBO
				.getDetailsOfNextInstallment();
		MeetingBO meeting = accountBO.getCustomer().getCustomerMeeting()
				.getMeeting();

		Short weekDay = null;
		if (meeting.getMeetingDetails().getMeetingRecurrence().getWeekDay()
				.getId().equals(WeekDay.SATURDAY.getValue()))
			weekDay = WeekDay.SUNDAY.getValue();
		else
			weekDay = (short) (meeting.getMeetingDetails()
					.getMeetingRecurrence().getWeekDay().getId() + 1);

		meeting.getMeetingDetails().getMeetingRecurrence().setWeekDay(
				new WeekDaysEntity(WeekDay.getWeekDay(weekDay)));

		meeting.setMeetingStartDate(DateUtils
				.getCalendarDate(accountActionDateEntity.getActionDate()
						.getTime()));
		((LoanBO) accountBO)
				.regenerateFutureInstallments((short) (accountActionDateEntity
						.getInstallmentId().intValue() + 1));
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				accountBO.getAccountId());
		assertEquals(Short.valueOf("1"), accountBO.getCustomer()
				.getCustomerMeeting().getMeeting().getMeetingDetails()
				.getRecurAfter());
		assertEquals(Short.valueOf("2"), ((LoanBO) accountBO).getLoanOffering()
				.getLoanOfferingMeeting().getMeeting().getMeetingDetails()
				.getRecurAfter());
		for (AccountActionDateEntity actionDateEntity : accountBO
				.getAccountActionDates()) {
			if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2")))
				assertEquals(DateUtils
						.getDateWithoutTimeStamp(incrementCurrentDate(1)
								.getTime()), DateUtils
						.getDateWithoutTimeStamp(actionDateEntity
								.getActionDate().getTime()));

		}
	}

	public void testRegenerateFutureInstallmentsWithCancelState()
			throws Exception {
		accountBO = getLoanAccount();
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(LoanBO.class,
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
		accountBO.setUserContext(TestObjectFactory.getContext());
		accountBO
				.changeStatus(AccountState.LOANACC_CANCEL.getValue(), null, "");
		((LoanBO) accountBO)
				.regenerateFutureInstallments((short) (accountActionDateEntity
						.getInstallmentId().intValue() + 1));
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(LoanBO.class,
				accountBO.getAccountId());
		
		 //Change this to more clearly separate what we are testing for from the
		 //machinery needed to get that data?
		 
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
				accntActionDates,
				TestObjectFactory.getMoneyForMFICurrency(700), null, accountBO
						.getPersonnel(), "receiptNum", Short.valueOf("1"),
				currentDate, currentDate);
		loan.applyPayment(paymentData);

		TestObjectFactory.updateObject(loan);
		TestObjectFactory.flushandCloseSession();
		assertEquals(
				"The amount returned for the payment should have been 700",
				700.0, loan.getLastPmntAmnt());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				loan.getAccountId());
		loan = (LoanBO) accountBO;
		loan.getLoanSummary().getOriginalPrincipal().subtract(
				loan.getLoanSummary().getPrincipalPaid());
		assertEquals(loan.getRemainingPrincipalAmount(), loan.getLoanSummary()
				.getOriginalPrincipal().subtract(
						loan.getLoanSummary().getPrincipalPaid()));
	}

	public void testIsAccountActive() throws Exception {
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
		accountBO.setUserContext(TestObjectFactory.getContext());
		accountBO.changeStatus(AccountState.LOANACC_BADSTANDING.getValue(),
				null, "");
		changeInstallmentDate(accountBO, 14, Short.valueOf("1"));
		changeInstallmentDate(accountBO, 14, Short.valueOf("2"));
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();

		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());

		Date startDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = new PaymentData(new Money(Configuration
				.getInstance().getSystemConfig().getCurrency(), "212.0"),
				accountBO.getPersonnel(), Short.valueOf("1"), startDate);
		paymentData.setRecieptDate(startDate);
		paymentData.setRecieptNum("5435345");

		accountBO.applyPayment(paymentData);
		TestObjectFactory.flushandCloseSession();

		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());

		assertEquals(Integer.valueOf("2"), ((LoanBO) accountBO)
				.getPerformanceHistory().getTotalNoOfMissedPayments());
	}

	public void testGetTotalRepayAmountForCustomerPerfHistory()
			throws Exception {
		accountBO = getLoanAccountWithPerformanceHistory();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		changeFirstInstallmentDate(accountBO);
		LoanBO loanBO = (LoanBO) accountBO;
		UserContext uc = TestObjectFactory.getUserContext();
		Money totalRepaymentAmount = loanBO.getTotalEarlyRepayAmount();
		Integer noOfActiveLoans = ((ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory()).getNoOfActiveLoans();
		LoanPerformanceHistoryEntity loanPerfHistory = ((LoanBO) accountBO)
				.getPerformanceHistory();
		Integer noOfPayments = loanPerfHistory.getNoOfPayments();
		loanBO.makeEarlyRepayment(loanBO.getTotalEarlyRepayAmount(), null,
				null, "1", uc.getId());
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(CustomerBO.class,
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
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(CustomerBO.class,	client.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
	}

	public void testWtiteOffForCustomerPerfHistory() throws Exception {
		accountBO = getLoanAccountWithPerformanceHistory();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(CustomerBO.class,
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
		loanBO.writeOff();
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loan = (LoanBO) accountBO;
		clientPerfHistory = (ClientPerformanceHistoryEntity) (loan)
				.getCustomer().getPerformanceHistory();
		assertEquals(loanCycleNumber - 1, clientPerfHistory
				.getLoanCycleNumber().intValue());
		assertEquals(noOfActiveLoans - 1, clientPerfHistory
				.getNoOfActiveLoans().intValue());
	}
	
	public void testDisbursalLoanForCustomerPerfHistory() throws Exception {
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
		client = TestObjectFactory.getObject(CustomerBO.class,
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
		client = TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory();
		Integer noOfActiveLoans = clientPerfHistory.getNoOfActiveLoans();
		((LoanBO) accountBO).handleArrears();
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		LoanBO loan = (LoanBO) accountBO;
		clientPerfHistory = (ClientPerformanceHistoryEntity) loan.getCustomer()
				.getPerformanceHistory();
		assertEquals(noOfActiveLoans + 1, clientPerfHistory
				.getNoOfActiveLoans().intValue());
	}

	public void testMakePaymentForCustomerPerfHistory()
			throws AccountException, SystemException {
		accountBO = getLoanAccountWithPerformanceHistory();
		accountBO.setUserContext(TestObjectFactory.getContext());
		accountBO.changeStatus(AccountState.LOANACC_BADSTANDING.getValue(),
				null, "");

		TestObjectFactory.updateObject(accountBO);
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		client = TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		ClientPerformanceHistoryEntity clientPerfHistory = (ClientPerformanceHistoryEntity) ((LoanBO) accountBO)
				.getCustomer().getPerformanceHistory();
		Integer noOfActiveLoans = clientPerfHistory.getNoOfActiveLoans();
		LoanPerformanceHistoryEntity loanPerfHistory = ((LoanBO) accountBO)
				.getPerformanceHistory();
		Integer noOfPayments = loanPerfHistory.getNoOfPayments();
		accountBO = applyPaymentandRetrieveAccount();
		LoanBO loan = (LoanBO) accountBO;
		client = TestObjectFactory.getObject(CustomerBO.class,
				client.getCustomerId());
		clientPerfHistory = (ClientPerformanceHistoryEntity) loan.getCustomer()
				.getPerformanceHistory();
		assertEquals(noOfActiveLoans - 1, clientPerfHistory
				.getNoOfActiveLoans().intValue());
		assertEquals(noOfPayments + 1, loan.getPerformanceHistory()
				.getNoOfPayments().intValue());
	}

	public void testDisbursalLoanForGroupPerfHistory() throws Exception {
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
		group = TestObjectFactory.getObject(CustomerBO.class,
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
		accountBO = TestObjectFactory.getObject(AccountBO.class,
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
		((LoanBO) accountBO).applyRounding();
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();

		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());

		Set<AccountActionDateEntity> actionDateEntities = accountBO
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkTotalDueWithFees("233.0", paymentsArray[0]);
		checkTotalDueWithFees("212.0", paymentsArray[1]);
		checkTotalDueWithFees("212.0", paymentsArray[2]);
		checkTotalDueWithFees("212.0", paymentsArray[3]);
		checkTotalDueWithFees("212.0", paymentsArray[4]);
		checkTotalDueWithFees("211.3", paymentsArray[5]);
	}

	public void testBuildLoanWithoutLoanOffering()
			throws NumberFormatException, AccountException, Exception {
		createInitialCustomers();
		try {
			new LoanBO(TestObjectFactory.getUserContext(), null, group,
					AccountState.LOANACC_APPROVED, new Money("300.0"), Short
							.valueOf("6"),
					new Date(System.currentTimeMillis()), false, 10.0,
					(short) 0, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
			assertFalse("The Loan object is created for null loan offering",
					true);
		} catch (AccountException ae) {
			assertTrue("The Loan object is not created for null loan offering",
					true);
		}
	}

	public void testBuildForInactiveLoanOffering()
			throws NumberFormatException, 
			SystemException, ApplicationException {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false,
				PrdStatus.LOANINACTIVE.getValue());
		try {
			new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
					AccountState.LOANACC_APPROVED, new Money("300.0"), Short
							.valueOf("6"),
					new Date(System.currentTimeMillis()), false, 10.0,
					(short) 0, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
			fail("The Loan object is created for inactive loan offering");
		} catch (AccountException ae) {
			assertTrue(
					"The Loan object is not created for inactive loan offering",
					true);
		}
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testBuildLoanWithoutCustomer() throws NumberFormatException,
			AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		try {
			new LoanBO(TestObjectFactory.getUserContext(), loanOffering, null,
					AccountState.LOANACC_APPROVED, new Money("300.0"), Short
							.valueOf("6"),
					new Date(System.currentTimeMillis()), false, 10.0,
					(short) 0, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
			assertFalse("The Loan object is created for null customer", true);
		} catch (AccountException ae) {
			assertTrue("The Loan object is not created for null customer", true);
		}
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testBuildForInactiveCustomer() throws NumberFormatException,
			 SystemException, ApplicationException {
		createInitialCustomers();
		TestCustomerBO.setCustomerStatus(group,new CustomerStatusEntity(
				CustomerStatus.GROUP_CLOSED));
		TestObjectFactory.updateObject(group);
		group = TestObjectFactory.getObject(GroupBO.class, group
				.getCustomerId());

		LoanOfferingBO loanOffering = createLoanOffering(false);
		try {
			new LoanBO(TestObjectFactory.getUserContext(), loanOffering, null,
					AccountState.LOANACC_APPROVED, new Money("300.0"), Short
							.valueOf("6"),
					new Date(System.currentTimeMillis()), false, 10.0,
					(short) 0, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
			assertFalse("The Loan object is created for inactive customer",
					true);
		} catch (AccountException ae) {
			assertTrue("The Loan object is not created for inactive customer",
					true);
		}
		TestObjectFactory.removeObject(loanOffering);

	}

	public void testMeetingNotMatchingForCustomerAndLoanOffering()
			throws NumberFormatException, 
			SystemException, ApplicationException {
		createInitialCustomers();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(MONTHLY, EVERY_MONTH, CUSTOMER_MEETING));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), PrdStatus.LOANACTIVE
						.getValue(), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("0"), Short.valueOf("1"), meeting);
		try {
			new LoanBO(TestObjectFactory.getUserContext(), loanOffering, null,
					AccountState.LOANACC_APPROVED, new Money("300.0"), Short
							.valueOf("6"),
					new Date(System.currentTimeMillis()), false, 10.0,
					(short) 0, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
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
			throws NumberFormatException, 
			SystemException, ApplicationException {
		createInitialCustomers();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), PrdStatus.LOANACTIVE
						.getValue(), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("0"), Short.valueOf("1"), meeting);
		new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
				AccountState.LOANACC_APPROVED, new Money("300.0"), Short
						.valueOf("6"), new Date(System.currentTimeMillis()),
				false, 10.0, (short) 0, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
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
			new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
					AccountState.LOANACC_APPROVED, null, Short.valueOf("6"),
					new Date(System.currentTimeMillis()), false, 10.0,
					(short) 0, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
			assertFalse("The Loan object is created for null customer", true);
		} catch (AccountException ae) {
			assertTrue("The Loan object is not created for null customer", true);
		}
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testGracePeriodGraterThanMaxNoOfInst()
			throws NumberFormatException, 
			SystemException, ApplicationException {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		try {
			new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
					AccountState.LOANACC_APPROVED, null, Short.valueOf("6"),
					new Date(System.currentTimeMillis()), false, 10.0,
					(short) 5, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
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
				new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
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
				new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
		assertEquals(
				"For interest ded at disb, grace period type should be none",
				GraceTypeConstants.NONE.getValue(), loan.getGracePeriodType()
						.getId());
		assertEquals(0, loan.getGracePeriodDuration().intValue());
		assertEquals(new java.sql.Date(DateUtils
				.getCurrentDateWithoutTimeStamp().getTime()).toString(), loan
				.getAccountActionDate((short) 1).getActionDate().toString());

		TestObjectFactory.removeObject(loanOffering);
	}

	public void testBuildLoanWithValidDisbDate() throws NumberFormatException,
			AccountException, Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		Date disbursementDate = new Date(System.currentTimeMillis());

		try {
			new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
					AccountState.LOANACC_APPROVED, new Money("300.0"), Short
							.valueOf("6"), disbursementDate, false, 10.0,
					(short) 0, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
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
			new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
					AccountState.LOANACC_APPROVED, new Money("300.0"), Short
							.valueOf("6"), disbursementDate, false, 10.0,
					(short) 0, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
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
			new LoanBO(TestObjectFactory.getUserContext(), loanOffering, group,
					AccountState.LOANACC_APPROVED, new Money("300.0"), Short
							.valueOf("6"), disbursementDate, false, 10.0,
					(short) 0, new FundBO(), new ArrayList<FeeView>(),new ArrayList<CustomFieldView>());
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
				new FundBO(), feeViews,null);

		assertEquals(2, loan.getAccountFees().size());
		for (AccountFeesEntity accountFees : loan.getAccountFees()) {
			if (accountFees.getFees().getFeeName()
					.equals("One Time Amount Fee"))
				assertEquals(new Double("120.0"), accountFees.getFeeAmount());
			else
				assertEquals(new Double("10.0"), accountFees.getFeeAmount());
		}

		HashMap fees1 = new HashMap();
		fees1.put("Periodic Fee", "10.0");
		fees1.put("One Time Amount Fee", "120.0");

		HashMap fees2 = new HashMap();
		fees2.put("Periodic Fee", "10.0");

		Set<AccountActionDateEntity> actionDateEntities = loan
				.getAccountActionDates();
		assertEquals(6, actionDateEntities.size());
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkFees(fees1, "130.0", paymentsArray[0]);
		checkFees(fees2, "10.0", paymentsArray[1]);
		checkFees(fees2, "10.0", paymentsArray[2]);
		checkFees(fees2, "10.0", paymentsArray[3]);
		checkFees(fees2, "10.0", paymentsArray[4]);
		checkFees(fees2, "10.0", paymentsArray[5]);

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
				new FundBO(), feeViews,null);

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

		Set<AccountActionDateEntity> actionDateEntities = loan
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkPrincipalAndInterest("50.5", "0.5", paymentsArray[0]);
		checkPrincipalAndInterest("50.5", "0.5", paymentsArray[1]);
		checkPrincipalAndInterest("50.5", "0.5", paymentsArray[2]);
		checkPrincipalAndInterest("50.5", "0.5", paymentsArray[3]);
		checkPrincipalAndInterest("50.5", "0.5", paymentsArray[4]);
		checkPrincipalAndInterest("47.5", "0.5", paymentsArray[5]);

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

		Set<AccountActionDateEntity> actionDateEntities = loan
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkPrincipalAndInterest("0.0", "3.0", paymentsArray[0]);
		checkPrincipalAndInterest("60.0", "0.0", paymentsArray[1]);
		checkPrincipalAndInterest("60.0", "0.0", paymentsArray[2]);
		checkPrincipalAndInterest("60.0", "0.0", paymentsArray[3]);
		checkPrincipalAndInterest("60.0", "0.0", paymentsArray[4]);
		checkPrincipalAndInterest("60.0", "0.0", paymentsArray[5]);

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

		Set<AccountActionDateEntity> actionDateEntities = loan
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkPrincipalAndInterest("0.0", "0.5", paymentsArray[0]);
		checkPrincipalAndInterest("0.0", "0.5", paymentsArray[1]);
		checkPrincipalAndInterest("0.0", "0.5", paymentsArray[2]);
		checkPrincipalAndInterest("0.0", "0.5", paymentsArray[3]);
		checkPrincipalAndInterest("0.0", "0.5", paymentsArray[4]);
		checkPrincipalAndInterest("300.0", "0.5", paymentsArray[5]);

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

		Set<AccountActionDateEntity> actionDateEntities = loan
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkPrincipalAndInterest("0.0", "3.0", paymentsArray[0]);
		checkPrincipalAndInterest("0.0", "0.0", paymentsArray[1]);
		checkPrincipalAndInterest("0.0", "0.0", paymentsArray[2]);
		checkPrincipalAndInterest("0.0", "0.0", paymentsArray[3]);
		checkPrincipalAndInterest("0.0", "0.0", paymentsArray[4]);
		checkPrincipalAndInterest("300.0", "0.0", paymentsArray[5]);

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

	public void testAmountNotRoundedWhileCreate() throws Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		boolean isInterestDedAtDisb = false;
		Short noOfinstallments = (short) 6;

		LoanBO loan = createAndRetrieveLoanAccount(loanOffering,
				isInterestDedAtDisb, null, noOfinstallments, 0.0);

		
		 //Change this to more clearly separate what we are testing for from the machinery needed to get that data?
		 
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

	public void testApplyMiscCharge() throws Exception {
		accountBO = getLoanAccount();
		Money intialTotalFeeAmount = ((LoanBO) accountBO).getLoanSummary()
				.getOriginalFees();
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		accountBO.setUserContext(uc);
		accountBO.applyCharge(Short.valueOf("-1"), new Double("33"));
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
			if (loanScheduleEntity.getInstallmentId()
					.equals(Short.valueOf("1")))
				assertEquals(new Money("33.0"), loanScheduleEntity.getMiscFee());
		}
		assertEquals(intialTotalFeeAmount.add(new Money("33.0")),
				((LoanBO) accountBO).getLoanSummary().getOriginalFees());
		LoanActivityEntity loanActivityEntity = ((LoanActivityEntity) (((LoanBO) accountBO)
				.getLoanActivityDetails().toArray())[0]);
		assertEquals(AccountConstants.MISC_FEES_APPLIED, loanActivityEntity
				.getComments());
		assertEquals(((LoanBO) accountBO).getLoanSummary().getOriginalFees(),
				loanActivityEntity.getFeeOutstanding());
	}

	public void testApplyMiscChargeWithFirstInstallmentPaid() throws Exception {
		accountBO = getLoanAccount();
		Money intialTotalFeeAmount = ((LoanBO) accountBO).getLoanSummary()
				.getOriginalFees();
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
			if (loanScheduleEntity.getInstallmentId()
					.equals(Short.valueOf("1")))
				loanScheduleEntity.setPaymentStatus(PaymentStatus.PAID
						.getValue());
		}
		UserContext uc = TestObjectFactory.getUserContext();
		accountBO.setUserContext(uc);
		accountBO.applyCharge(Short.valueOf("-1"), new Double("33"));
		
		 //Change this to more clearly separate what we are testing for from the machinery needed to get that data?
		 
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
			if (loanScheduleEntity.getInstallmentId()
					.equals(Short.valueOf("2")))
				assertEquals(new Money("33.0"), loanScheduleEntity.getMiscFee());
		}
		assertEquals(intialTotalFeeAmount.add(new Money("33.0")),
				((LoanBO) accountBO).getLoanSummary().getOriginalFees());
		LoanActivityEntity loanActivityEntity = ((LoanActivityEntity) (((LoanBO) accountBO)
				.getLoanActivityDetails().toArray())[0]);
		assertEquals(AccountConstants.MISC_FEES_APPLIED, loanActivityEntity
				.getComments());
		assertEquals(((LoanBO) accountBO).getLoanSummary().getOriginalFees(),
				loanActivityEntity.getFeeOutstanding());
	}

	public void testApplyPeriodicFee() throws Exception {
		accountBO = getLoanAccount();
		Money intialTotalFeeAmount = ((LoanBO) accountBO).getLoanSummary()
				.getOriginalFees();
		TestObjectFactory.flushandCloseSession();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "200", RecurrenceType.WEEKLY,
				Short.valueOf("2"));
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		accountBO.setUserContext(uc);
		accountBO.applyCharge(periodicFee.getFeeId(),
				((AmountFeeBO) periodicFee).getFeeAmount()
						.getAmountDoubleValue());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		Date lastAppliedDate = null;

		HashMap fees1 = new HashMap();
		fees1.put("Periodic Fee", "200.0");// missing an entry
		fees1.put("Mainatnence Fee", "100.0");

		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(((LoanBO) accountBO)
				.getAccountActionDates());
		assertEquals(6, paymentsArray.length);

		checkFees(fees1, paymentsArray[0], false);
		checkFees(fees1, paymentsArray[2], false);
		checkFees(fees1, paymentsArray[4], false);

		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;

			if (loanScheduleEntity.getInstallmentId()
					.equals(Short.valueOf("5"))) {
				assertEquals(2, loanScheduleEntity
						.getAccountFeesActionDetails().size());
				lastAppliedDate = loanScheduleEntity.getActionDate();

			}
		}

		assertEquals(intialTotalFeeAmount.add(new Money("600.0")),
				((LoanBO) accountBO).getLoanSummary().getOriginalFees());
		LoanActivityEntity loanActivityEntity = ((LoanActivityEntity) (((LoanBO) accountBO)
				.getLoanActivityDetails().toArray())[0]);
		assertEquals(periodicFee.getFeeName() + " applied", loanActivityEntity
				.getComments());
		assertEquals(((LoanBO) accountBO).getLoanSummary().getOriginalFees(),
				loanActivityEntity.getFeeOutstanding());
		AccountFeesEntity accountFeesEntity = accountBO
				.getAccountFees(periodicFee.getFeeId());
		assertEquals(FeeStatus.ACTIVE.getValue(), accountFeesEntity
				.getFeeStatus());
		assertEquals(DateUtils.getDateWithoutTimeStamp(lastAppliedDate
				.getTime()), DateUtils
				.getDateWithoutTimeStamp(accountFeesEntity.getLastAppliedDate()
						.getTime()));
	}

	public void testApplyUpfrontFee() throws Exception {
		accountBO = getLoanAccount();
		Money intialTotalFeeAmount = ((LoanBO) accountBO).getLoanSummary()
				.getOriginalFees();
		TestObjectFactory.flushandCloseSession();
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		accountBO.setUserContext(uc);
		accountBO.applyCharge(upfrontFee.getFeeId(), ((RateFeeBO) upfrontFee)
				.getRate());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		Date lastAppliedDate = null;
		Money feeAmountApplied = new Money();
		HashMap fees2 = new HashMap();
		fees2.put("Upfront Fee", "60.0");
		fees2.put("Mainatnence Fee", "100.0");

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		assertEquals(6, actionDateEntities.size());
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkFees(fees2, paymentsArray[0], false);

		// setting of values here
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
			if (loanScheduleEntity.getInstallmentId()
					.equals(Short.valueOf("1"))) {

				lastAppliedDate = loanScheduleEntity.getActionDate();
				for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity
						.getAccountFeesActionDetails()) {
					if (accountFeesActionDetailEntity.getFee().getFeeName()
							.equalsIgnoreCase("Upfront Fee")) {
						feeAmountApplied = accountFeesActionDetailEntity
								.getFeeAmount();
					}
				}
			}
		}
		assertEquals(intialTotalFeeAmount.add(feeAmountApplied),
				((LoanBO) accountBO).getLoanSummary().getOriginalFees());
		LoanActivityEntity loanActivityEntity = ((LoanActivityEntity) (((LoanBO) accountBO)
				.getLoanActivityDetails().toArray())[0]);
		assertEquals(upfrontFee.getFeeName() + " applied", loanActivityEntity
				.getComments());
		assertEquals(((LoanBO) accountBO).getLoanSummary().getOriginalFees(),
				loanActivityEntity.getFeeOutstanding());
		AccountFeesEntity accountFeesEntity = accountBO
				.getAccountFees(upfrontFee.getFeeId());
		assertEquals(FeeStatus.ACTIVE.getValue(), accountFeesEntity
				.getFeeStatus());
		assertEquals(DateUtils.getDateWithoutTimeStamp(lastAppliedDate
				.getTime()), DateUtils
				.getDateWithoutTimeStamp(accountFeesEntity.getLastAppliedDate()
						.getTime()));
	}

	public void testUpdateLoanSuccessWithRegeneratingNewRepaymentSchedule()
			throws Exception {
		Date newDate = incrementCurrentDate(14);
		accountBO = getLoanAccount();
		accountBO.setUserContext(TestObjectFactory.getContext());
		accountBO.changeStatus(AccountState.LOANACC_APPROVED.getValue(), null,
				"");

		LoanBO loanBO = ((LoanBO) accountBO);
		((LoanBO) accountBO).updateLoan(loanBO
				.isInterestDeductedAtDisbursement(), loanBO.getLoanAmount(),
				loanBO.getInterestRate(), loanBO.getNoOfInstallments(),
				newDate, loanBO.getGracePeriodDuration(), loanBO
						.getBusinessActivityId(), loanBO.getCollateralNote(),
				null,null);
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		Date newActionDate = DateUtils.getDateWithoutTimeStamp(accountBO
				.getAccountActionDate(Short.valueOf("1")).getActionDate()
				.getTime());
		assertEquals(
				"New repayment schedule generated, so first installment date should be same as newDate",
				newDate, newActionDate);
	}

	public void testUpdateLoanWithoutRegeneratingNewRepaymentSchedule()
			throws ApplicationException, SystemException {
		Date newDate = incrementCurrentDate(14);
		accountBO = getLoanAccount();
		Date oldActionDate = DateUtils.getDateWithoutTimeStamp(accountBO
				.getAccountActionDate(Short.valueOf("1")).getActionDate()
				.getTime());
		LoanBO loanBO = ((LoanBO) accountBO);
		((LoanBO) accountBO).updateLoan(loanBO
				.isInterestDeductedAtDisbursement(), loanBO.getLoanAmount(),
				loanBO.getInterestRate(), loanBO.getNoOfInstallments(),
				newDate, loanBO.getGracePeriodDuration(), loanBO
						.getBusinessActivityId(), loanBO.getCollateralNote(),
				null,null);
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		group = TestObjectFactory.getObject(CustomerBO.class,
				group.getCustomerId());
		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());
		Date newActionDate = DateUtils.getDateWithoutTimeStamp(accountBO
				.getAccountActionDate(Short.valueOf("1")).getActionDate()
				.getTime());
		assertEquals(
				"Didn't generate new repayment schedule, so first installment date should be same as before ",
				oldActionDate, newActionDate);
	}

	public void testCreateLoanAccountWithPrincipalDueInLastPayment()
			throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		UserContext userContext = TestObjectFactory.getUserContext();
		userContext.setLocaleId(null);
		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("3"));
		feeViewList.add(new FeeView(userContext, periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(userContext, upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeAmountFee(
				"Disbursment Fee", FeeCategory.LOAN, "30",
				FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(userContext, disbursementFee));

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, 1.2, (short) 0, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

		HashMap fees1 = new HashMap();
		fees1.put("Periodic Fee", "100.0");

		HashMap fees2 = new HashMap();
		fees2.put("Periodic Fee", "100.0");
		fees2.put("Upfront Fee", "60.0");

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(null, "0.0", "0.1", fees2, paymentsArray[0]);
		checkLoanScheduleEntity(null, "0.0", "0.1", fees0, paymentsArray[1]);
		checkLoanScheduleEntity(null, "0.0", "0.1", fees1, paymentsArray[2]);
		checkLoanScheduleEntity(null, "0.0", "0.1", fees1, paymentsArray[3]);
		checkLoanScheduleEntity(null, "0.0", "0.1", fees0, paymentsArray[4]);
		checkLoanScheduleEntity(incrementCurrentDate(14 * 6), "300.0", "0.1",
				fees1, paymentsArray[5]);

		assertEquals(3, accountBO.getAccountFees().size());
		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			if (accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")) {
				assertEquals(new Money("60.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("20.0"), accountFeesEntity
						.getFeeAmount());
			} else if (accountFeesEntity.getFees().getFeeName().equals(
					"Disbursment Fee")) {
				assertEquals(new Money("30.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("30.0"), accountFeesEntity
						.getFeeAmount());
			} else {
				assertEquals(new Money("100.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("100.0"), accountFeesEntity
						.getFeeAmount());
			}
		}
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());
		assertEquals(new Money("0.6"), loanSummaryEntity.getOriginalInterest());
		assertEquals(new Money("490.0"), loanSummaryEntity.getOriginalFees());
		assertEquals(new Money("0.0"), loanSummaryEntity.getOriginalPenalty());
	}

	public void testCreateLoanAccountWithInterestDeductedAtDisbursment()
			throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		UserContext userContext = TestObjectFactory.getUserContext();
		userContext.setLocaleId(null);
		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("3"));
		feeViewList.add(new FeeView(userContext, periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(userContext, upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeAmountFee(
				"Disbursment Fee", FeeCategory.LOAN, "30",
				FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(userContext, disbursementFee));

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()), true,
				1.2, (short) 0, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);

		HashMap fees3 = new HashMap();
		fees3.put("Periodic Fee", "100.0");
		fees3.put("Disbursment Fee", "30.0");
		fees3.put("Upfront Fee", "60.0");

		HashMap fees0 = new HashMap();

		HashMap fees1 = new HashMap();
		fees1.put("Periodic Fee", "100.0");

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(incrementCurrentDate(0), "0.0", "0.6", fees3,
				paymentsArray[0]);
		checkLoanScheduleEntity(null, "60.0", "0.0", fees0, paymentsArray[1]);
		checkLoanScheduleEntity(null, "60.0", "0.0", fees1, paymentsArray[2]);
		checkLoanScheduleEntity(null, "60.0", "0.0", fees1, paymentsArray[3]);
		checkLoanScheduleEntity(null, "60.0", "0.0", fees0, paymentsArray[4]);
		checkLoanScheduleEntity(incrementCurrentDate(14 * 5), "60.0", "0.0",
				fees1, paymentsArray[5]);

		assertEquals(3, accountBO.getAccountFees().size());
		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			if (accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")) {
				assertEquals(new Money("60.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("20.0"), accountFeesEntity
						.getFeeAmount());
			} else if (accountFeesEntity.getFees().getFeeName().equals(
					"Disbursment Fee")) {
				assertEquals(new Money("30.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("30.0"), accountFeesEntity
						.getFeeAmount());
			} else {
				assertEquals(new Money("100.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("100.0"), accountFeesEntity
						.getFeeAmount());
			}
		}
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());
		assertEquals(new Money("0.6"), loanSummaryEntity.getOriginalInterest());
		assertEquals(new Money("490.0"), loanSummaryEntity.getOriginalFees());
		assertEquals(new Money("0.0"), loanSummaryEntity.getOriginalPenalty());
	}

	public void testCreateLoanAccountWithIDADAndPDILI() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		UserContext userContext = TestObjectFactory.getUserContext();
		userContext.setLocaleId(null);
		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("3"));
		feeViewList.add(new FeeView(userContext, periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(userContext, upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeAmountFee(
				"Disbursment Fee", FeeCategory.LOAN, "30",
				FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(userContext, disbursementFee));

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()), true,
				1.2, (short) 0, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);

		HashMap fees0 = new HashMap();

		HashMap fees1 = new HashMap();
		fees1.put("Periodic Fee", "100.0");

		HashMap fees3 = new HashMap();
		fees3.put("Periodic Fee", "100.0");
		fees3.put("Disbursment Fee", "30.0");
		fees3.put("Upfront Fee", "60.0");

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(incrementCurrentDate(0), "0.0", "0.6", null,
				null, null, fees3, null, null, null, paymentsArray[0]);
		checkLoanScheduleEntity(null, "0.0", "0.0", null, null, null, fees0,
				null, null, null, paymentsArray[1]);
		checkLoanScheduleEntity(null, "0.0", "0.0", null, null, null, fees1,
				null, null, null, paymentsArray[2]);
		checkLoanScheduleEntity(null, "0.0", "0.0", null, null, null, fees1,
				null, null, null, paymentsArray[3]);
		checkLoanScheduleEntity(null, "0.0", "0.0", null, null, null, fees0,
				null, null, null, paymentsArray[4]);
		checkLoanScheduleEntity(incrementCurrentDate(14 * 5), "300.0", "0.0",
				null, null, null, fees1, null, null, null, paymentsArray[5]);

		assertEquals(3, accountBO.getAccountFees().size());
		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			if (accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")) {
				assertEquals(new Money("60.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("20.0"), accountFeesEntity
						.getFeeAmount());
			} else if (accountFeesEntity.getFees().getFeeName().equals(
					"Disbursment Fee")) {
				assertEquals(new Money("30.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("30.0"), accountFeesEntity
						.getFeeAmount());
			} else {
				assertEquals(new Money("100.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("100.0"), accountFeesEntity
						.getFeeAmount());
			}
		}
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());
		assertEquals(new Money("0.6"), loanSummaryEntity.getOriginalInterest());
		assertEquals(new Money("490.0"), loanSummaryEntity.getOriginalFees());
		assertEquals(new Money("0.0"), loanSummaryEntity.getOriginalPenalty());
	}

	public void testCreateNormalLoanAccount() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		UserContext userContext = TestObjectFactory.getUserContext();
		userContext.setLocaleId(null);
		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("3"));
		feeViewList.add(new FeeView(userContext, periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(userContext, upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeAmountFee(
				"Disbursment Fee", FeeCategory.LOAN, "30",
				FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(userContext, disbursementFee));

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, 1.2, (short) 0, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);

		HashMap fees0 = new HashMap();

		HashMap fees1 = new HashMap();
		fees1.put("Periodic Fee", "100.0");

		HashMap fees2 = new HashMap();
		fees2.put("Periodic Fee", "100.0");
		fees2.put("Upfront Fee", "60.0");

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(incrementCurrentDate(14), "50.9", "0.1", fees2,
				paymentsArray[0]);
		checkLoanScheduleEntity(null, "50.9", "0.1", fees0, paymentsArray[1]);
		checkLoanScheduleEntity(null, "50.9", "0.1", fees1, paymentsArray[2]);
		checkLoanScheduleEntity(null, "50.9", "0.1", fees1, paymentsArray[3]);
		checkLoanScheduleEntity(null, "50.9", "0.1", fees0, paymentsArray[4]);
		checkLoanScheduleEntity(incrementCurrentDate(14 * 6), "45.5", "0.1",
				fees1, paymentsArray[5]);

		assertEquals(3, accountBO.getAccountFees().size());
		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			if (accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")) {
				assertEquals(new Money("60.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("20.0"), accountFeesEntity
						.getFeeAmount());
			} else if (accountFeesEntity.getFees().getFeeName().equals(
					"Disbursment Fee")) {
				assertEquals(new Money("30.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("30.0"), accountFeesEntity
						.getFeeAmount());
			} else {
				assertEquals(new Money("100.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("100.0"), accountFeesEntity
						.getFeeAmount());
			}
		}
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());
		assertEquals(new Money("0.6"), loanSummaryEntity.getOriginalInterest());
		assertEquals(new Money("490.0"), loanSummaryEntity.getOriginalFees());
		assertEquals(new Money("0.0"), loanSummaryEntity.getOriginalPenalty());
	}

	public void testCreateNormalLoanAccountWithPricipalOnlyGrace()
			throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("3"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting(),
				Short.valueOf("3"));
		UserContext userContext = TestObjectFactory.getUserContext();
		userContext.setLocaleId(null);
		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("3"));
		feeViewList.add(new FeeView(userContext, periodicFee));
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(userContext, upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeAmountFee(
				"Disbursment Fee", FeeCategory.LOAN, "30",
				FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(userContext, disbursementFee));

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, 1.2, (short) 1, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);

		HashMap fees0 = new HashMap();

		HashMap fees1 = new HashMap();
		fees1.put("Periodic Fee", "100.0");

		HashMap fees2 = new HashMap();
		fees2.put("Periodic Fee", "100.0");
		fees2.put("Upfront Fee", "60.0");

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(incrementCurrentDate(14), "0.0", "0.1", fees2,
				paymentsArray[0]);
		checkLoanScheduleEntity(null, "60.0", "0.1", fees0, paymentsArray[1]);
		checkLoanScheduleEntity(null, "60.0", "0.1", fees1, paymentsArray[2]);
		checkLoanScheduleEntity(null, "60.0", "0.1", fees1, paymentsArray[3]);
		checkLoanScheduleEntity(null, "60.0", "0.1", fees0, paymentsArray[4]);
		checkLoanScheduleEntity(incrementCurrentDate(14 * 6), "60.0", "0.1",
				fees1, paymentsArray[5]);

		assertEquals(3, accountBO.getAccountFees().size());
		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			if (accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")) {
				assertEquals(new Money("60.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("20.0"), accountFeesEntity
						.getFeeAmount());
			} else if (accountFeesEntity.getFees().getFeeName().equals(
					"Disbursment Fee")) {
				assertEquals(new Money("30.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("30.0"), accountFeesEntity
						.getFeeAmount());
			} else {
				assertEquals(new Money("100.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("100.0"), accountFeesEntity
						.getFeeAmount());
			}
		}
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());
		assertEquals(new Money("0.6"), loanSummaryEntity.getOriginalInterest());
		assertEquals(new Money("490.0"), loanSummaryEntity.getOriginalFees());
		assertEquals(new Money("0.0"), loanSummaryEntity.getOriginalPenalty());
	}

	public void testCreateNormalLoanAccountWithMonthlyInstallments()
			throws Exception {
		Short dayOfMonth = (short) 1;
		MeetingBO meeting = TestObjectFactory
			.getNewMeeting(MONTHLY, EVERY_SECOND_MONTH, CUSTOMER_MEETING, MONDAY);
		meeting.setMeetingStartDate(Calendar.getInstance());
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(
				dayOfMonth);
		TestObjectFactory.createMeeting(meeting);
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
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
		if (disbursementDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth
				.intValue())
			disbursementDate = new GregorianCalendar(year, month, day);
		else
			disbursementDate = new GregorianCalendar(year, month + 1, day);
		UserContext userContext = TestObjectFactory.getUserContext();
		userContext.setLocaleId(null);
		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(userContext, upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeRateFee(
				"Disbursment Fee", FeeCategory.LOAN, Double.valueOf("30"),
				FeeFormula.AMOUNT_AND_INTEREST, FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(userContext, disbursementFee));
		FeeBO firstRepaymentFee = TestObjectFactory.createOneTimeRateFee(
				"First Repayment Fee", FeeCategory.LOAN, Double.valueOf("40"),
				FeeFormula.INTEREST, FeePayment.TIME_OF_FIRSTLOANREPAYMENT);
		feeViewList.add(new FeeView(userContext, firstRepaymentFee));
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100",
				RecurrenceType.MONTHLY, Short.valueOf("1"));
		feeViewList.add(new FeeView(userContext, periodicFee));
		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), disbursementDate.getTime(), false, 1.2,
				(short) 0, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);

		HashMap fees1 = new HashMap();
		fees1.put("Periodic Fee", "200.0");

		HashMap fees3 = new HashMap();
		fees3.put("Periodic Fee", "100.0");
		fees3.put("Upfront Fee", "60.0");
		fees3.put("First Repayment Fee", "1.4");

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(null, "50.0", "0.6", fees3, paymentsArray[0]);
		checkLoanScheduleEntity(null, "50.4", "0.6", fees1, paymentsArray[1]);
		checkLoanScheduleEntity(null, "50.4", "0.6", fees1, paymentsArray[2]);
		checkLoanScheduleEntity(null, "50.4", "0.6", fees1, paymentsArray[3]);
		checkLoanScheduleEntity(null, "50.4", "0.6", fees1, paymentsArray[4]);
		checkLoanScheduleEntity(null, "48.4", "0.6", fees1, paymentsArray[5]);

		assertEquals(4, accountBO.getAccountFees().size());
		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			if (accountFeesEntity.getFees().getFeeName().equals("Upfront Fee")) {
				assertEquals(new Money("60.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("20.0"), accountFeesEntity
						.getFeeAmount());
			} else if (accountFeesEntity.getFees().getFeeName().equals(
					"Disbursment Fee")) {
				assertEquals(new Money("91.1"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("30.0"), accountFeesEntity
						.getFeeAmount());
			} else if (accountFeesEntity.getFees().getFeeName().equals(
					"First Repayment Fee")) {
				assertEquals(new Money("1.4"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("40.0"), accountFeesEntity
						.getFeeAmount());
			} else {
				assertEquals(new Money("100.0"), accountFeesEntity
						.getAccountFeeAmount());
				assertEquals(new Double("100.0"), accountFeesEntity
						.getFeeAmount());
			}
		}
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());
		assertEquals(new Money("3.6"), loanSummaryEntity.getOriginalInterest());
		assertEquals(new Money("1252.5"), loanSummaryEntity.getOriginalFees());
		assertEquals(new Money("0.0"), loanSummaryEntity.getOriginalPenalty());
	}

	public void testDisburseLoanWithAllTypeOfFees() throws Exception {
		Short dayOfMonth = (short) 25;
		MeetingBO meeting = TestObjectFactory
			.getNewMeeting(MONTHLY, EVERY_MONTH, CUSTOMER_MEETING, MONDAY);

		TestObjectFactory.createMeeting(meeting);
		meeting.setMeetingStartDate(Calendar.getInstance());
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(
				dayOfMonth);
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("3"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());

		Calendar disbursementDate = new GregorianCalendar();
		int year = disbursementDate.get(Calendar.YEAR);
		int month = disbursementDate.get(Calendar.MONTH);
		int day = 25;
		if (disbursementDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth
				.intValue())
			disbursementDate = new GregorianCalendar(year, month, day);
		else
			disbursementDate = new GregorianCalendar(year, month + 1, day);
		UserContext userContext = TestObjectFactory.getUserContext();
		userContext.setLocaleId(null);
		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO upfrontFee = TestObjectFactory.createOneTimeRateFee(
				"Upfront Fee", FeeCategory.LOAN, Double.valueOf("20"),
				FeeFormula.AMOUNT, FeePayment.UPFRONT);
		feeViewList.add(new FeeView(userContext, upfrontFee));
		FeeBO disbursementFee = TestObjectFactory.createOneTimeRateFee(
				"Disbursment Fee", FeeCategory.LOAN, Double.valueOf("30"),
				FeeFormula.AMOUNT_AND_INTEREST, FeePayment.TIME_OF_DISBURSMENT);
		feeViewList.add(new FeeView(userContext, disbursementFee));
		FeeBO firstRepaymentFee = TestObjectFactory.createOneTimeRateFee(
				"First Repayment Fee", FeeCategory.LOAN, Double.valueOf("40"),
				FeeFormula.INTEREST, FeePayment.TIME_OF_FIRSTLOANREPAYMENT);
		feeViewList.add(new FeeView(userContext, firstRepaymentFee));
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100",
				RecurrenceType.MONTHLY, Short.valueOf("1"));
		feeViewList.add(new FeeView(userContext, periodicFee));
		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), disbursementDate.getTime(), false, 1.2,
				(short) 0, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);

		disbursementDate = new GregorianCalendar();
		year = disbursementDate.get(Calendar.YEAR);
		month = disbursementDate.get(Calendar.MONTH);
		day = 25;
		if (disbursementDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth
				.intValue())
			disbursementDate = new GregorianCalendar(year, month + 1, day);
		else
			disbursementDate = new GregorianCalendar(year, month + 2, day);
		((LoanBO) accountBO).disburseLoan("1234", disbursementDate.getTime(),
				Short.valueOf("1"), accountBO.getPersonnel(), disbursementDate
						.getTime(), Short.valueOf("1"));
		Session session = HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		session.save(accountBO);
		HibernateUtil.getTransaction().commit();
	}

	public void testUpdateLoanFailure() {
		createInitialCustomers();
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOANACC_PENDINGAPPROVAL.getValue(), new Date(
						System.currentTimeMillis()), loanOffering);
		try {
			LoanBO loanBO = (LoanBO) accountBO;
			((LoanBO) accountBO).updateLoan(loanBO
					.isInterestDeductedAtDisbursement(),
					loanBO.getLoanAmount(), loanBO.getInterestRate(), loanBO
							.getNoOfInstallments(), offSetCurrentDate(15),
					loanBO.getGracePeriodDuration(), loanBO
							.getBusinessActivityId(), "Loan account updated",
					null,null);
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
		try {
			LoanBO loanBO = ((LoanBO) accountBO);
			((LoanBO) accountBO).updateLoan(loanBO
					.isInterestDeductedAtDisbursement(),
					loanBO.getLoanAmount(), loanBO.getInterestRate(), loanBO
							.getNoOfInstallments(), loanBO
							.getDisbursementDate(), loanBO
							.getGracePeriodDuration(), loanBO
							.getBusinessActivityId(), "Loan account updated",
					null,null);
			assertTrue(
					"The Loan object is created for valid disbursement date",
					true);
		} catch (AccountException ae) {
			assertFalse(
					"The Loan object is created for valid disbursement date",
					true);
		}
	}
	
	public void testUpdateLoanWithInterestDeducted() {
		accountBO = getLoanAccount();
		try {
			LoanBO loanBO = ((LoanBO) accountBO);
			((LoanBO) accountBO).updateLoan(loanBO
					.isInterestDeductedAtDisbursement(),
					loanBO.getLoanAmount(), loanBO.getInterestRate(), Short.valueOf("1"), loanBO
							.getDisbursementDate(), loanBO
							.getGracePeriodDuration(), loanBO
							.getBusinessActivityId(), "Loan account updated",
					null,null);
			assertFalse(true);
		} catch (AccountException ae) {
			assertTrue("Invalid no of installment",true);
		}
	}

	public void testApplyTimeOfFirstRepaymentFee() throws Exception {
		accountBO = getLoanAccount();
		Money intialTotalFeeAmount = ((LoanBO) accountBO).getLoanSummary()
				.getOriginalFees();
		TestObjectFactory.flushandCloseSession();
		FeeBO oneTimeFee = TestObjectFactory.createOneTimeRateFee(
				"Onetime Fee", FeeCategory.LOAN, new Double("0.09"),
				FeeFormula.AMOUNT, FeePayment.TIME_OF_FIRSTLOANREPAYMENT);
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		UserContext uc = TestObjectFactory.getUserContext();
		accountBO.setUserContext(uc);
		accountBO.applyCharge(oneTimeFee.getFeeId(), ((RateFeeBO) oneTimeFee)
				.getRate());
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());

		HashMap fees2 = new HashMap();
		fees2.put("Onetime Fee", "0.3");
		fees2.put("Mainatnence Fee", "100.0");

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		assertEquals(6, actionDateEntities.size());
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkFees(fees2, paymentsArray[0], false);

		assertEquals(intialTotalFeeAmount.add(new Money("0.3")),
				((LoanBO) accountBO).getLoanSummary().getOriginalFees());
		LoanActivityEntity loanActivityEntity = ((LoanActivityEntity) (((LoanBO) accountBO)
				.getLoanActivityDetails().toArray())[0]);
		assertEquals(oneTimeFee.getFeeName() + " applied", loanActivityEntity
				.getComments());
		assertEquals(((LoanBO) accountBO).getLoanSummary().getOriginalFees(),
				loanActivityEntity.getFeeOutstanding());
		AccountFeesEntity accountFeesEntity = accountBO
				.getAccountFees(oneTimeFee.getFeeId());
		assertEquals(FeeStatus.ACTIVE.getValue(), accountFeesEntity
				.getFeeStatus());
	}

	public void testApplyPaymentForFullPayment() throws Exception {
		accountBO = getLoanAccount();
		assertEquals(new Money("212.0"), ((LoanBO) accountBO)
				.getTotalPaymentDue());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("212"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money(), ((LoanBO) accountBO).getTotalPaymentDue());
	}

	public void testApplyPaymentForPartialPayment() throws Exception {
		accountBO = getLoanAccount();
		assertEquals(new Money("212.0"), ((LoanBO) accountBO)
				.getTotalPaymentDue());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("200"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money("12"), ((LoanBO) accountBO).getTotalPaymentDue());
		assertEquals(Integer.valueOf(0), ((LoanBO) accountBO)
				.getPerformanceHistory().getNoOfPayments());
	}

	public void testApplyPaymentForFuturePayment() throws Exception {
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
		nextInstallment = (LoanScheduleEntity) accountBO
				.getAccountActionDate(nextInstallment.getInstallmentId());
		assertEquals(new Money("112.0"), nextInstallment.getTotalDueWithFees());
	}

	public void testApplyPaymentForCompletePayment() throws Exception {
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
		nextInstallment = (LoanScheduleEntity) accountBO
				.getAccountActionDate(nextInstallment.getInstallmentId());
		assertEquals(new Money(), nextInstallment.getTotalDueWithFees());
		assertEquals(AccountState.LOANACC_OBLIGATIONSMET, accountBO.getState());
	}

	public void testApplyPaymentForPaymentGretaterThanTotalDue()
			throws Exception {
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
							.getPersonnel(), "432423", (short) 1, new Date(
							System.currentTimeMillis()), new Date(System
							.currentTimeMillis())));
			assertFalse(true);
		} catch (AccountException e) {
			assertTrue(true);
		}
	}

	public void testFeeForMultiplePayments() throws Exception {
		accountBO = getLoanAccount();

		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money("212.0"), ((LoanBO) accountBO)
				.getTotalPaymentDue());
		LoanScheduleEntity nextInstallment = (LoanScheduleEntity) ((LoanBO) accountBO)
				.getDetailsOfNextInstallment();
		assertEquals(new Money("100.0"), nextInstallment.getTotalFeeDue());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("10"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		nextInstallment = (LoanScheduleEntity) ((LoanBO) accountBO)
				.getDetailsOfNextInstallment();
		assertEquals(new Money("90"), nextInstallment.getTotalFeeDue());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("30"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		nextInstallment = (LoanScheduleEntity) ((LoanBO) accountBO)
				.getDetailsOfNextInstallment();
		assertEquals(new Money("60"), nextInstallment.getTotalFeeDue());
	}

	public void testLoanPerfHistoryForUndisbursedLoans() throws Exception {
		accountBO = getLoanAccount();
		LoanBO loan = (LoanBO) accountBO;
		Date disbursementDate = offSetCurrentDate(28);
		AccountActionDateEntity accountActionDate1 = loan
				.getAccountActionDate((short) 1);
		AccountActionDateEntity accountActionDate2 = loan
				.getAccountActionDate((short) 2);
		accountBO.setUserContext(TestObjectFactory.getContext());
		accountBO.changeStatus(AccountState.LOANACC_APPROVED.getValue(), null,
				"");
		((LoanScheduleEntity)accountActionDate1).setActionDate(offSetCurrentDate(21));
		((LoanScheduleEntity)accountActionDate2).setActionDate(offSetCurrentDate(14));
		loan.setDisbursementDate(disbursementDate);
		accountBO = saveAndFetch(loan);
		loan = (LoanBO) accountBO;
		assertEquals(Integer.valueOf("0"), loan.getPerformanceHistory()
				.getTotalNoOfMissedPayments());
		assertEquals(Short.valueOf("0"), loan.getPerformanceHistory()
				.getDaysInArrears());
		assertEquals(Integer.valueOf("0"), loan.getPerformanceHistory()
				.getNoOfPayments());
	}

	public void testFeeForMultiplePaymentsIncludingCompletePayment()
			throws Exception {
		accountBO = getLoanAccount();

		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money("212.0"), ((LoanBO) accountBO)
				.getTotalPaymentDue());
		LoanScheduleEntity nextInstallment = (LoanScheduleEntity) ((LoanBO) accountBO)
				.getDetailsOfNextInstallment();
		assertEquals(new Money("100.0"), nextInstallment.getTotalFeeDue());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("10"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		nextInstallment = (LoanScheduleEntity) ((LoanBO) accountBO)
				.getDetailsOfNextInstallment();
		assertEquals(new Money("90"), nextInstallment.getTotalFeeDue());
		assertEquals(new Money("202.0"), ((LoanBO) accountBO)
				.getTotalPaymentDue());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("202"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		nextInstallment = (LoanScheduleEntity) ((LoanBO) accountBO)
				.getDetailsOfNextInstallment();
		assertEquals(new Money(), nextInstallment.getTotalFeeDue());

	}

	public void testRemoveFeeForPartiallyPaidFeesAccount() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());

		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("1"));
		feeViewList
				.add(new FeeView(TestObjectFactory.getContext(), periodicFee));

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, 1.2, (short) 0, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());
		HibernateUtil.closeSession();

		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("60"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		HibernateUtil.commitTransaction();

		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			accountBO.removeFees(accountFeesEntity.getFees().getFeeId(), Short
					.valueOf("1"));
		}
		HibernateUtil.commitTransaction();

		HashMap fees0 = new HashMap();

		HashMap fees1 = new HashMap();
		fees1.put("Periodic Fee", "60");

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		assertEquals(6, actionDateEntities.size());
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkFees(fees1, paymentsArray[0], false);
		checkFees(fees0, paymentsArray[1], false);
		checkFees(fees0, paymentsArray[2], false);
		checkFees(fees0, paymentsArray[3], false);
		checkFees(fees0, paymentsArray[4], false);
		checkFees(fees0, paymentsArray[5], false);

		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			assertEquals(AccountConstants.INACTIVE_FEES, accountFeesEntity
					.getFeeStatus());
			assertNull(accountFeesEntity.getLastAppliedDate());
		}
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("60"), loanSummaryEntity.getFeesPaid());
		assertEquals(new Money("60"), loanSummaryEntity.getOriginalFees());
		assertEquals(new Money(), loanSummaryEntity.getFeesDue());
		for (LoanActivityEntity loanActivityEntity : ((LoanBO) accountBO)
				.getLoanActivityDetails()) {
			if (loanActivityEntity.getComments().equalsIgnoreCase(
					"Periodic Fee removed")) {
				assertEquals(loanSummaryEntity.getFeesDue(), loanActivityEntity
						.getFeeOutstanding());
				assertEquals(new Money("1040"), loanActivityEntity.getFee());
				break;
			}
		}
	}

	public void testApplyChargeForPartiallyPaidFeesAccount() throws Exception {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("0"),
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting());

		List<FeeView> feeViewList = new ArrayList<FeeView>();
		FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee(
				"Periodic Fee", FeeCategory.LOAN, "100", RecurrenceType.WEEKLY,
				Short.valueOf("1"));
		feeViewList
				.add(new FeeView(TestObjectFactory.getContext(), periodicFee));

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, 1.2, (short) 0, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());
		HibernateUtil.closeSession();

		accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
				AccountBO.class, accountBO.getAccountId());

		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("60"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		HibernateUtil.commitTransaction();

		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			accountBO.removeFees(accountFeesEntity.getFees().getFeeId(), Short
					.valueOf("1"));
		}
		HibernateUtil.commitTransaction();
		HashMap fees0 = new HashMap(0);

		HashMap fees1 = new HashMap();
		fees1.put("Periodic Fee", "60");
		HashMap feesPaid1 = new HashMap();
		feesPaid1.put("Periodic Fee", "60");

		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(((LoanBO) accountBO)
				.getAccountActionDates());

		assertEquals(6, paymentsArray.length);
		checkLoanScheduleEntity(null, null, null, null, null, null, fees1,
				feesPaid1, null, null, paymentsArray[0]);
		checkLoanScheduleEntity(null, null, null, null, null, null, fees0,
				null, null, null, paymentsArray[1]);
		checkLoanScheduleEntity(null, null, null, null, null, null, fees0,
				null, null, null, paymentsArray[2]);
		checkLoanScheduleEntity(null, null, null, null, null, null, fees0,
				null, null, null, paymentsArray[3]);
		checkLoanScheduleEntity(null, null, null, null, null, null, fees0,
				null, null, null, paymentsArray[4]);
		checkLoanScheduleEntity(null, null, null, null, null, null, fees0,
				null, null, null, paymentsArray[5]);

		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			assertEquals(AccountConstants.INACTIVE_FEES, accountFeesEntity
					.getFeeStatus());
			assertNull(accountFeesEntity.getLastAppliedDate());
		}
		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("60"), loanSummaryEntity.getFeesPaid());
		assertEquals(new Money("60"), loanSummaryEntity.getOriginalFees());
		assertEquals(new Money(), loanSummaryEntity.getFeesDue());
		for (LoanActivityEntity loanActivityEntity : ((LoanBO) accountBO)
				.getLoanActivityDetails()) {
			if (loanActivityEntity.getComments().equalsIgnoreCase(
					"Periodic Fee removed")) {
				assertEquals(loanSummaryEntity.getFeesDue(), loanActivityEntity
						.getFeeOutstanding());
				assertEquals(new Money("1040"), loanActivityEntity.getFee());
				break;
			}
		}

		accountBO.setUserContext(TestObjectFactory.getUserContext());
		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			accountBO.applyCharge(accountFeesEntity.getFees().getFeeId(),
					Double.valueOf("200"));
		}
		HibernateUtil.commitTransaction();

		Map fee260 = new HashMap();
		fee260.put("Periodic Fee", "260");

		Map fee400 = new HashMap();
		fee400.put("Periodic Fee", "400");

		Set<AccountActionDateEntity> actionDateEntities1 = ((LoanBO) accountBO)
				.getAccountActionDates();
		assertEquals(6, actionDateEntities1.size());
		LoanScheduleEntity[] paymentsArray1 = getSortedAccountActionDateEntity(actionDateEntities1);

		checkLoanScheduleEntity(null, null, null, null, null, null, fee260,
				null, null, null, paymentsArray1[0]);
		checkLoanScheduleEntity(null, null, null, null, null, null, fee400,
				null, null, null, paymentsArray1[1]);
		checkLoanScheduleEntity(null, null, null, null, null, null, fee400,
				null, null, null, paymentsArray1[2]);
		checkLoanScheduleEntity(null, null, null, null, null, null, fee400,
				null, null, null, paymentsArray1[3]);
		checkLoanScheduleEntity(null, null, null, null, null, null, fee400,
				null, null, null, paymentsArray1[4]);
		checkLoanScheduleEntity(null, null, null, null, null, null, fee400,
				null, null, null, paymentsArray1[5]);

		// not sure if the remaining is being handled by validatePayments
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			LoanScheduleEntity loanScheduleEntity = (LoanScheduleEntity) accountActionDateEntity;
			if (!loanScheduleEntity.getInstallmentId().equals(
					Short.valueOf("1"))) {
				assertEquals(1, loanScheduleEntity
						.getAccountFeesActionDetails().size());
				for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity
						.getAccountFeesActionDetails()) {
					LoanFeeScheduleEntity loanFeeScheduleEntity = (LoanFeeScheduleEntity) accountFeesActionDetailEntity;
					assertNull(loanFeeScheduleEntity.getFeeAmountPaid());
				}
			}
		}

		for (AccountFeesEntity accountFeesEntity : accountBO.getAccountFees()) {
			assertEquals(AccountConstants.ACTIVE_FEES, accountFeesEntity
					.getFeeStatus());
			assertNotNull(accountFeesEntity.getLastAppliedDate());
		}
		loanSummaryEntity = ((LoanBO) accountBO).getLoanSummary();
		assertEquals(new Money("60"), loanSummaryEntity.getFeesPaid());
		assertEquals(new Money("2260"), loanSummaryEntity.getOriginalFees());
		assertEquals(new Money("2200"), loanSummaryEntity.getFeesDue());
		for (LoanActivityEntity loanActivityEntity : ((LoanBO) accountBO)
				.getLoanActivityDetails()) {
			if (loanActivityEntity.getComments().equalsIgnoreCase(
					"Periodic Fee applied")) {
				assertEquals(loanSummaryEntity.getFeesDue(), loanActivityEntity
						.getFeeOutstanding());
				assertEquals(new Money("2200"), loanActivityEntity.getFee());
				break;
			}
		}
	}

	public void testPartialPaymentForPrincipalGrace() throws Exception {
		accountBO = getLoanAccount();
		((LoanScheduleEntity) accountBO.getAccountActionDate((short) 1))
				.setPrincipal(new Money());
		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money("112.0"), ((LoanBO) accountBO)
				.getTotalPaymentDue());
		accountBO.applyPayment(TestObjectFactory.getLoanAccountPaymentData(
				null, new Money("100"), accountBO.getCustomer(), accountBO
						.getPersonnel(), "432423", (short) 1, new Date(System
						.currentTimeMillis()), new Date(System
						.currentTimeMillis())));
		accountBO = saveAndFetch(accountBO);
		assertEquals(new Money("12"), ((LoanBO) accountBO).getTotalPaymentDue());
		assertEquals(Integer.valueOf(0), ((LoanBO) accountBO)
				.getPerformanceHistory().getNoOfPayments());
		assertEquals(PaymentStatus.UNPAID.getValue(), ((LoanBO) accountBO)
				.getAccountActionDate((short) 1).getPaymentStatus());
	}

	public void testGetDaysInArrears() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
		calendar.add(calendar.DAY_OF_MONTH, -14);
		java.sql.Date lastWeekDate = new java.sql.Date(calendar
				.getTimeInMillis());

		Calendar date = new GregorianCalendar();
		date.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
		date.add(date.DAY_OF_MONTH, -21);
		java.sql.Date twoWeeksBeforeDate = new java.sql.Date(date
				.getTimeInMillis());

		accountBO = getLoanAccount();
		for (AccountActionDateEntity installment : accountBO
				.getAccountActionDates()) {
			if (installment.getInstallmentId().intValue() == 1) {
				((LoanScheduleEntity)installment).setActionDate(twoWeeksBeforeDate);
			} else if (installment.getInstallmentId().intValue() == 2) {
				((LoanScheduleEntity)installment).setActionDate(lastWeekDate);
			}
		}
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(Short.valueOf("21"), ((LoanBO) accountBO)
				.getDaysInArrears());
	}

	public void testGetTotalInterestAmountInArrears() {
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
		accountBO = getLoanAccount();
		Money interest = new Money();
		for (AccountActionDateEntity installment : accountBO
				.getAccountActionDates()) {
			if (installment.getInstallmentId().intValue() == 1) {
				((LoanScheduleEntity)installment).setActionDate(lastWeekDate);
				interest = interest.add(((LoanScheduleEntity) installment)
						.getInterest());
			} else if (installment.getInstallmentId().intValue() == 2) {
				interest = interest.add(((LoanScheduleEntity) installment)
						.getInterest());
				((LoanScheduleEntity)installment).setActionDate(twoWeeksBeforeDate);
			}
		}
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(interest, ((LoanBO) accountBO)
				.getTotalInterestAmountInArrears());
	}

	public void testGetTotalPrincipalAmountInArrears() {
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
		accountBO = getLoanAccount();
		for (AccountActionDateEntity installment : accountBO
				.getAccountActionDates()) {
			if (installment.getInstallmentId().intValue() == 1) {
				((LoanScheduleEntity)installment).setActionDate(lastWeekDate);
			} else if (installment.getInstallmentId().intValue() == 2) {
				((LoanScheduleEntity)installment).setActionDate(twoWeeksBeforeDate);
			}
		}
		TestObjectFactory.updateObject(accountBO);
		TestObjectFactory.flushandCloseSession();
		accountBO = TestObjectFactory.getObject(AccountBO.class,
				accountBO.getAccountId());
		assertEquals(new Money("200"), ((LoanBO) accountBO)
				.getTotalPrincipalAmountInArrears());
	}

	public void testSaveLoanForInvalidConnection() throws Exception {
		createInitialCustomers();
		LoanOfferingBO loanOffering = createLoanOffering(false);
		List<FeeView> feeViews = getFeeViews();

		LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group, AccountState.LOANACC_APPROVED, new Money(
						"300.0"), Short.valueOf("6"), new Date(System
						.currentTimeMillis()), true, 10.0, (short) 0,
				new FundBO(), feeViews,null);
		TestObjectFactory.simulateInvalidConnection();
		try {
			loan.save();
			fail();
		} catch (AccountException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
		deleteFee(feeViews);
		TestObjectFactory.removeObject(loanOffering);
	}

	public void testUpdateLoanFOrInvalidConnection() {
		accountBO = getLoanAccount();
		TestObjectFactory.simulateInvalidConnection();
		try {
			accountBO.update();
			fail();
		} catch (AccountException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}

	}

	public void testSuccessUpdateTotalFeeAmount() {
		accountBO = getLoanAccount();
		LoanBO loanBO = (LoanBO) accountBO;
		LoanSummaryEntity loanSummaryEntity = loanBO.getLoanSummary();
		Money orignalFeesAmount = loanSummaryEntity.getOriginalFees();
		loanBO.updateTotalFeeAmount(new Money(TestObjectFactory
				.getMFICurrency(), "20"));
		assertEquals(loanSummaryEntity.getOriginalFees(), (orignalFeesAmount
				.subtract(new Money(TestObjectFactory.getMFICurrency(), "20"))));
	}

	public static void modifyDisbursmentDate(LoanBO loan, Date disbursmentDate) {
		loan.setDisbursementDate(disbursmentDate);
	}

	public void testCreateLoanAccountWithDecliningInterestNoGracePeriod()
			throws NumberFormatException, 
			PropertyNotFoundException, SystemException, ApplicationException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("2"), Short
						.valueOf("0"), Short.valueOf("0"), Short.valueOf("0"), // penalty
																				// grace,
																				// intDebAtDisb,
																				// princDueLastInst
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting(),
				GraceTypeConstants.NONE.getValue());

		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, // 6 installments
				1.2, (short) 0, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 1), "50.9", "0.1",
				fees0, paymentsArray[0]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 2), "50.9", "0.1",
				fees0, paymentsArray[1]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 3), "50.9", "0.1",
				fees0, paymentsArray[2]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 4), "50.9", "0.1",
				fees0, paymentsArray[3]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 5), "51.0", "0.0",
				fees0, paymentsArray[4]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 6), "45.6", "0.0",
				fees0, paymentsArray[5]);

		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();

		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());

	}

	public void testCreateLoanAccountWithDecliningInterestGraceAllRepayments()
			throws NumberFormatException, 
			PropertyNotFoundException, SystemException, ApplicationException {

		short graceDuration = (short) 2;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("2"), Short
						.valueOf("0"), Short.valueOf("0"), Short.valueOf("0"), // penalty
																				// grace,
																				// intDebAtDisb,
																				// princDueLastInst
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting(),
				GraceTypeConstants.GRACEONALLREPAYMENTS.getValue());

		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, // 6 installments
				1.2, graceDuration, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());
		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (1 + graceDuration)),
				"50.9", "0.1", fees0, paymentsArray[0]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (2 + graceDuration)),
				"50.9", "0.1", fees0, paymentsArray[1]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (3 + graceDuration)),
				"50.9", "0.1", fees0, paymentsArray[2]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (4 + graceDuration)),
				"50.9", "0.1", fees0, paymentsArray[3]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (5 + graceDuration)),
				"51.0", "0.0", fees0, paymentsArray[4]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (6 + graceDuration)),
				"45.6", "0.0", fees0, paymentsArray[5]);

		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());

	}

	public void testCreateLoanAccountWithDecliningInterestGracePrincipalOnly()
			throws NumberFormatException, 
			PropertyNotFoundException, SystemException, ApplicationException {

		short graceDuration = (short) 2;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("2"), Short
						.valueOf("0"), Short.valueOf("0"), Short.valueOf("0"), // penalty
																				// grace,
																				// intDebAtDisb,
																				// princDueLastInst
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting(),
				GraceTypeConstants.PRINCIPALONLYGRACE.getValue());

		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, // 6 installments
				1.2, graceDuration, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 1), "0.0", "0.1",
				fees0, paymentsArray[0]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 2), "0.0", "0.1",
				fees0, paymentsArray[1]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 3), "75.0", "0.1",
				fees0, paymentsArray[2]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 4), "75.0", "0.1",
				fees0, paymentsArray[3]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 5), "75.0", "0.1",
				fees0, paymentsArray[4]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 6), "75.1", "0.0",
				fees0, paymentsArray[5]);

		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());

	}

	public void testCreateLoanAccountWithDecliningInterestPrincipalDueOnLastInstallment()
			throws NumberFormatException, 
			PropertyNotFoundException, SystemException, ApplicationException {

		short graceDuration = (short) 2;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("2"), Short
						.valueOf("0"), Short.valueOf("0"), Short.valueOf("1"), // penalty
																				// grace,
																				// intDebAtDisb,
																				// princDueLastInst
				Short.valueOf("1"), center.getCustomerMeeting().getMeeting(),
				GraceTypeConstants.NONE.getValue());

		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group,
				AccountState.LOANACC_ACTIVEINGOODSTANDING, new Money("300.0"),
				Short.valueOf("6"), new Date(System.currentTimeMillis()),
				false, // 6 installments
				1.2, graceDuration, new FundBO(), feeViewList,null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 1), "0.0", "0.1",
				fees0, paymentsArray[0]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 2), "0.0", "0.1",
				fees0, paymentsArray[1]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 3), "0.0", "0.1",
				fees0, paymentsArray[2]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 4), "0.0", "0.1",
				fees0, paymentsArray[3]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 5), "0.0", "0.1",
				fees0, paymentsArray[4]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 6), "300.0", "0.1",
				fees0, paymentsArray[5]);

		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
				.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());

	}
	
	private LoanBO createAndRetrieveLoanAccount(LoanOfferingBO loanOffering,
			boolean isInterestDedAtDisb, List<FeeView> feeViews,
			Short noOfinstallments, Double interestRate)
			throws NumberFormatException, AccountException,
			 SystemException, ApplicationException {
		LoanBO loan = new LoanBO(TestObjectFactory.getUserContext(),
				loanOffering, group, AccountState.LOANACC_APPROVED, new Money(
						"300.0"), noOfinstallments, new Date(System
						.currentTimeMillis()), isInterestDedAtDisb,
				interestRate, (short) 0, new FundBO(), feeViews,null);
		loan.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		accountBO = TestObjectFactory.getObject(AccountBO.class,
				loan.getAccountId());
		return (LoanBO) accountBO;
	}

	private LoanBO createAndRetrieveLoanAccount(LoanOfferingBO loanOffering,
			boolean isInterestDedAtDisb, List<FeeView> feeViews,
			Short noOfinstallments) throws NumberFormatException,
			AccountException,  SystemException,
			ApplicationException {
		return createAndRetrieveLoanAccount(loanOffering, isInterestDedAtDisb,
				feeViews, noOfinstallments, 10.0);
	}

	private LoanOfferingBO createLoanOffering(boolean isPrincipalAtLastInst) {
		return createLoanOffering(isPrincipalAtLastInst, PrdStatus.LOANACTIVE
				.getValue());
	}

	private LoanOfferingBO createLoanOffering(boolean isPrincipalAtLastInst,
			Short statusId) {
		Short principalAtLastInst = isPrincipalAtLastInst ? (short) 1
				: (short) 0;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		return TestObjectFactory.createLoanOffering("Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), statusId, 300.0, 1.2,
				Short.valueOf("3"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), principalAtLastInst, Short.valueOf("1"),
				meeting);
	}

	private List<FeeView> getFeeViews() {
		FeeBO fee1 = TestObjectFactory.createOneTimeAmountFee(
				"One Time Amount Fee", FeeCategory.LOAN, "120.0",
				FeePayment.TIME_OF_DISBURSMENT);
		FeeBO fee3 = TestObjectFactory.createPeriodicAmountFee("Periodic Fee",
				FeeCategory.LOAN, "10.0", RecurrenceType.WEEKLY, (short) 1);
		List<FeeView> feeViews = new ArrayList<FeeView>();
		FeeView feeView1 = new FeeView(userContext, fee1);
		FeeView feeView2 = new FeeView(userContext, fee3);
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
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
	}

	private void changeFirstInstallmentDateToNextDate(AccountBO accountBO) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + 1);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			((LoanScheduleEntity)accountActionDateEntity).setActionDate(new java.sql.Date(
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
		return TestObjectFactory.getObject(AccountBO.class,
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
		return testObjectPersistence.getObject(LoanBO.class, accountBO
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
				((LoanScheduleEntity)accountActionDateEntity).setActionDate(new java.sql.Date(
						dateCalendar.getTimeInMillis()));
				break;
			}
		}
	}

	private AccountBO getLoanAccountWithPerformanceHistory() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		/*((ClientBO) client).getPerformanceHistory().setLoanCycleNumber(1);
		TestObjectFactory.updateObject(client);*/
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"),
				new Date(System.currentTimeMillis()), Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", client,
				Short.valueOf("3"), new Date(System.currentTimeMillis()),
				loanOffering);
		((ClientBO) client).getPerformanceHistory().updateLoanCounter(loanOffering,YesNoFlag.YES);
		TestObjectFactory.updateObject(client);
		TestObjectFactory.updateObject(accountBO);
		return accountBO;
	}

	private AccountBO getLoanAccountWithPerformanceHistory(Short accountSate,
			Date startDate, int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		//((ClientBO) client).getPerformanceHistory().setLoanCycleNumber(1);
		accountBO = TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", client, accountSate, startDate, loanOffering,
				disbursalType);
		((ClientBO) client).getPerformanceHistory().updateLoanCounter(loanOffering,YesNoFlag.YES);
		return accountBO;

	}

	private AccountBO getLoanAccountWithGroupPerformanceHistory(
			Short accountSate, Date startDate, int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("1"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
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
			((LoanScheduleEntity)accountActionDateEntity).setActionDate(new java.sql.Date(
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
			((LoanScheduleEntity)accountActionDateEntity).setActionDate(new java.sql.Date(
					currentDateCalendar.getTimeInMillis()));
			break;
		}
	}

	private AccountBO saveAndFetch(AccountBO account) throws Exception {
		TestObjectFactory.updateObject(account);
		HibernateUtil.closeSession();
		return accountPersistence.getAccount(account.getAccountId());
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
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
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
		return DateUtils.getDateWithoutTimeStamp(currentDateCalendar
				.getTimeInMillis());
	}

	private void checkLoanScheduleEntity(Date date, String principal,
			String interest, String miscFee, String miscPenalty,
			String penalty, Map fees, Map feesPaid, String totalFeeDue,
			String totalDueWithFees, LoanScheduleEntity entity) {
		if (date != null) {
			assertDate(date, entity);
		}

		if (principal != null) {
			assertEquals(new Money(principal), entity.getPrincipal());
		}
		if (interest != null) {
			assertEquals(new Money(interest), entity.getInterest());
		}

		if (miscFee != null) {
			assertEquals(new Money(miscFee), entity.getMiscFee());
		}

		if (miscPenalty != null) {
			assertEquals(new Money(miscPenalty), entity.getMiscPenalty());
		}

		if (penalty != null) {
			assertEquals(new Money(penalty), entity.getPenalty());
		}

		if (fees != null) {
			checkFees(fees, entity, false);
		}

		if (feesPaid != null) {
			checkFees(feesPaid, entity, true);
		}

		if (totalFeeDue != null) {
			checkTotalDue(totalFeeDue, entity);
		}

		if (totalDueWithFees != null) {
			checkTotalDueWithFees(totalDueWithFees, entity);
		}
	}

	private void checkTotalDueWithFees(String totalDueWithFees,
			LoanScheduleEntity entity) {
		assertEquals(new Money(totalDueWithFees), entity.getTotalDueWithFees());
	}

	private void checkTotalDue(String totalFeeDue, LoanScheduleEntity entity) {
		assertEquals(new Money(totalFeeDue), entity.getTotalFeeDue());
	}

	private void checkLoanScheduleEntity(Date date, String principal,
			String interest, Map fees, LoanScheduleEntity entity) {
		if (date != null) {
			assertDate(date, entity);
		}

		assertEquals(new Money(principal), entity.getPrincipal());
		assertEquals(new Money(interest), entity.getInterest());

		checkFees(fees, entity, false);
	}

	private void assertDate(Date date, LoanScheduleEntity entity) {
		assertEquals(DateUtils.getDateWithoutTimeStamp(date.getTime()),
				DateUtils.getDateWithoutTimeStamp(entity.getActionDate()
						.getTime()));
	}

	private void checkFees(Map expectedFess, String totalFeeDue,
			LoanScheduleEntity entity) {
		checkFees(expectedFess, entity, false);
		assertEquals(new Money(totalFeeDue), entity.getTotalFeeDue());
	}

	private void checkPrincipalAndInterest(String principal, String interest,
			LoanScheduleEntity entity) {
		assertEquals(new Money(principal), entity.getPrincipal());
		assertEquals(new Money(interest), entity.getInterest());
	}

	private void checkFees(Map expected, LoanScheduleEntity loanScheduleEntity,
			boolean checkPaid) {
		assertEquals(expected.size(), loanScheduleEntity
				.getAccountFeesActionDetails().size());

		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : loanScheduleEntity
				.getAccountFeesActionDetails()) {

			if (expected.get(accountFeesActionDetailEntity.getFee()
					.getFeeName()) != null) {
				assertEquals(new Money((String) expected
						.get(accountFeesActionDetailEntity.getFee()
								.getFeeName())),
						checkPaid ? accountFeesActionDetailEntity
								.getFeeAmountPaid()
								: accountFeesActionDetailEntity.getFeeAmount());
			} else {

				assertFalse("Fee amount not found for "
						+ accountFeesActionDetailEntity.getFee().getFeeName(),
						true);
			}
		}
	}

	protected LoanScheduleEntity[] getSortedAccountActionDateEntity(
			Set<AccountActionDateEntity> actionDateCollection) {

		LoanScheduleEntity[] sortedList = new LoanScheduleEntity[actionDateCollection
				.size()];

		// Don't know whether it will always be 6 for future tests, but
		// right now it is...
		assertEquals(6, actionDateCollection.size());

		for (AccountActionDateEntity actionDateEntity : actionDateCollection) {
			sortedList[actionDateEntity.getInstallmentId().intValue() - 1] = (LoanScheduleEntity) actionDateEntity;
		}

		return sortedList;

	}

	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center",
				meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				Short.valueOf("5"), startDate, loanOffering);

	}

	private AccountBO createLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", Short.valueOf("2"), startDate, Short.valueOf("1"),
				300.0, 1.2, Short.valueOf("3"), Short.valueOf("1"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				Short.valueOf("5"), new Date(System.currentTimeMillis()),
				loanOffering);
		return accountBO;
	}
	
	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(Short.valueOf("5"), "value1",
				CustomFieldType.ALPHA_NUMERIC.getValue()));
		return fields;
	}

}
