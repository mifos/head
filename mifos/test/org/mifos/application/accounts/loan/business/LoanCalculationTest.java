package org.mifos.application.accounts.loan.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_MONTH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.TestAccountActionDateEntity;
import org.mifos.application.accounts.business.TestAccountFeesEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFormula;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.config.AccountingRules;
import org.mifos.config.ConfigurationManager;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;




/*
 * LoanCalculationTest is a starting point for defining and exploring
 * expected behavior for different loan payment calculations.
 * 
 * This is a work in progress so there is still lots of extraneous junk
 * in this file, that will be cleaned up as we go forward.
 */
public class LoanCalculationTest extends MifosTestCase {
	// these constants for parsing the spreadsheet
	final String principal = "Principal";
	final String loanType = "Loan Type";
	final String annualInterest = "Annual Interest";
	final String numberOfPayments = "Number of Payments";
	final String paymentFrequency = "Payment Frequency";
	final String initialRoundingMode = "InitialRoundingMode";
	final String initialRoundOffMultiple = "InitialRoundOffMultiple";
	final String finalRoundingMode = "FinalRoundingMode";
	final String finalRoundOffMultiple = "FinalRoundOffMultiple";
	final String currencyRounding = "CurrencyRounding";
	final String digitsAfterDecimal = "Digits After Decimal";
	final String daysInYear = "Days in Year";
	final String totals = "Summed Totals";
	final String start = "Start";
	final String gracePeriodType = "GracePeriodType";
	final String gracePeriod = "GracePeriod";
	final String calculatedTotals = "Calculated Totals";
	final String feeFrequency = "FeeFrequency";
	final String feeType = "FeeType";
	final String feeValue = "FeeValue";
	final String feePercentage = "FeePercentage";
	
	final String feeTypePrincipalPlusInterest = "Principal+Interest";
	final String feeTypeInterest = "Interest";
	final String feeTypePrincipal = "Principal";
	final String feeTypeValue = "Value";
	final String account999 = "Account 999";
	String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/";

	

	LoanOfferingBO loanOffering = null;

	// TODO: probably should be of type LoanBO
	protected AccountBO accountBO = null;

	protected AccountBO badAccountBO = null;

	protected CustomerBO center = null;

	protected CustomerBO group = null;

	private CustomerBO client = null;
	private BigDecimal savedInitialRoundOffMultiple = null;
	private BigDecimal savedFinalRoundOffMultiple = null;
	private RoundingMode savedCurrencyRoundingMode = null;
	private RoundingMode savedInitialRoundingMode = null;
	private RoundingMode savedFinalRoundingMode = null;
	private Short savedDigitAfterDecimal;
	
	
	
	private UserContext userContext;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		userContext = TestObjectFactory.getContext();

		Money.setUsingNewMoney(true);
		LoanBO.setUsingNewLoanSchedulingMethod(true);
		savedInitialRoundOffMultiple = AccountingRules.getInitialRoundOffMultiple();
		savedFinalRoundOffMultiple = AccountingRules.getFinalRoundOffMultiple();
		savedCurrencyRoundingMode = AccountingRules.getCurrencyRoundingMode();
		savedDigitAfterDecimal = AccountingRules.getDigitsAfterDecimal();
		savedInitialRoundingMode = AccountingRules.getInitialRoundingMode();
		savedFinalRoundingMode = AccountingRules.getFinalRoundingMode();

	}


	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(loanOffering);
		if (accountBO != null)
			accountBO = (AccountBO) HibernateUtil.getSessionTL().get(
					AccountBO.class, accountBO.getAccountId());
		if (badAccountBO != null)
			badAccountBO = (AccountBO) HibernateUtil.getSessionTL().get(
					AccountBO.class, badAccountBO.getAccountId());
		if (group != null)
			group = (CustomerBO) HibernateUtil.getSessionTL().get(
					CustomerBO.class, group.getCustomerId());
		if (center != null)
			center = (CustomerBO) HibernateUtil.getSessionTL().get(
					CustomerBO.class, center.getCustomerId());
		TestObjectFactory.cleanUp(accountBO);
		TestObjectFactory.cleanUp(badAccountBO);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);

		HibernateUtil.closeSession();
		super.tearDown();
		Money.setUsingNewMoney(false);
		LoanBO.setUsingNewLoanSchedulingMethod(false);
		AccountingRules.setInitialRoundOffMultiple(savedInitialRoundOffMultiple);
		AccountingRules.setFinalRoundOffMultiple(savedFinalRoundOffMultiple);
		AccountingRules.setCurrencyRoundingMode(savedCurrencyRoundingMode);
		AccountingRules.setDigitsAfterDecimal(savedDigitAfterDecimal);
		AccountingRules.setInitialRoundingMode(savedInitialRoundingMode);
		AccountingRules.setFinalRoundingMode(savedFinalRoundingMode);
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

	public static void setDisbursementDate(AccountBO account,
			Date disbursementDate) {
		((LoanBO) account).setDisbursementDate(disbursementDate);
	}

	public static LoanBO createIndividualLoanAccount(String globalNum,
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
			loan = LoanBO.createIndividualLoan(TestUtils.makeUser(), loanOfering, customer,
					state, new Money(currency, "300.0"), Short.valueOf("6"),
					meetingDates.get(0), true,false, 0.0, (short) 0, new FundBO(),
					new ArrayList<FeeView>(), null);
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
			actionDate.setPaymentStatus(PaymentStatus.UNPAID);
			TestAccountActionDateEntity.addAccountActionDate(actionDate, loan);

			AccountFeesActionDetailEntity accountFeesaction = new LoanFeeScheduleEntity(
					actionDate, maintanenceFee, accountPeriodicFee, new Money(
							currency, "100.0"));
			setFeeAmountPaid(accountFeesaction, new Money(currency, "0.0"));
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
	
	public static void modifyDisbursmentDate(LoanBO loan, Date disbursmentDate) {
		loan.setDisbursementDate(disbursmentDate);
	}
	
	/* This part is for the testing of 999 account */
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	
	public static RateFeeBO createPeriodicRateFee(
			InternalConfiguration config, LoanParameters loanParams, MeetingBO meeting) {
		
		try {
			return TestObjectFactory.createPeriodicRateFee(
					"testLoanFee", FeeCategory.LOAN, new Double (config.getFeePercentage()), config.getFeeType(),
					loanParams.getPaymentFrequency(), (short) 1, TestUtils.makeUserWithLocales(), meeting);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private void runOne999AccountTestCaseWithDataFromSpreadSheetForLastPaymentReversal(String fileName, 
			int expected999AccountTransactions, int paymentToReverse, boolean payLastPayment) throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{

		LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
		Results calculatedResults = new Results();
		InternalConfiguration config = testCaseData.getInternalConfig();
		LoanParameters loanParams = testCaseData.getLoanParams();
		accountBO = setUpLoanFor999AccountTestLastPaymentReversal(config, loanParams, calculatedResults, paymentToReverse, payLastPayment);
		
		Set<AccountPaymentEntity> paymentList = ((LoanBO)accountBO).getAccountPayments();
		int transactionCount = 0;
		for (Iterator<AccountPaymentEntity> paymentIterator = paymentList.iterator(); paymentIterator.hasNext();) 
		{
			AccountPaymentEntity payment = paymentIterator.next();
			Set<AccountTrxnEntity> transactionList = payment.getAccountTrxns();
			for (AccountTrxnEntity transaction : transactionList)
			{
				Set<FinancialTransactionBO> list = ((LoanTrxnDetailEntity)transaction).getFinancialTransactions();
				for (Iterator<FinancialTransactionBO> iterator = list.iterator(); iterator.hasNext();) 
				{
					FinancialTransactionBO financialTransaction = iterator.next();
					if (financialTransaction.getPostedAmount().equals(testCaseData.getExpectedResult().getAccount999())
							|| financialTransaction.getPostedAmount().negate().equals(testCaseData.getExpectedResult().getAccount999()))
					{
						transactionCount++;
						String debitOrCredit = "Credit";
						if (financialTransaction.getDebitCreditFlag() == 0)
							debitOrCredit = "Debit";
						System.out.println("Posted amount: " + financialTransaction.getPostedAmount().getAmountDoubleValue() +
								           " Debit/Credit: " + debitOrCredit +
								           " GLCode: " + financialTransaction.getGlcode().getGlcode() +
								           " Transaction Id: " + financialTransaction.getTrxnId());
					}
	
				}
			}
		}
		
		assertEquals(transactionCount, expected999AccountTransactions);

	}
	
	private void runOne999AccountTestCaseWithDataFromSpreadSheetForLoanReversal(String fileName, 
			int expected999AccountTransactions, int paymentToReverse, boolean payLastPayment) throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{

		LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
		Results calculatedResults = new Results();
		InternalConfiguration config = testCaseData.getInternalConfig();
		LoanParameters loanParams = testCaseData.getLoanParams();
		accountBO = setUpLoanFor999AccountTestLoanReversal(config, loanParams, calculatedResults, paymentToReverse, payLastPayment);
		
		Set<AccountPaymentEntity> paymentList = ((LoanBO)accountBO).getAccountPayments();
		int transactionCount=0;
		for (Iterator<AccountPaymentEntity> paymentIterator = paymentList.iterator(); paymentIterator.hasNext();) 
		{
			AccountPaymentEntity payment = paymentIterator.next();
			Set<AccountTrxnEntity> transactionList = payment.getAccountTrxns();
			for (AccountTrxnEntity transaction : transactionList)
			{
				Set<FinancialTransactionBO> list = ((LoanTrxnDetailEntity)transaction).getFinancialTransactions();
				for (Iterator<FinancialTransactionBO> iterator = list.iterator(); iterator.hasNext();) 
				{
					FinancialTransactionBO financialTransaction = iterator.next();
					if (financialTransaction.getPostedAmount().equals(testCaseData.getExpectedResult().getAccount999())
							|| financialTransaction.getPostedAmount().negate().equals(testCaseData.getExpectedResult().getAccount999()))
					{
						transactionCount++;
						String debitOrCredit = "Credit";
						if (financialTransaction.getDebitCreditFlag() == 0)
							debitOrCredit = "Debit";
						System.out.println("Posted amount: " + financialTransaction.getPostedAmount().getAmountDoubleValue() +
								           " Debit/Credit: " + debitOrCredit +
								           " GLCode: " + financialTransaction.getGlcode().getGlcode() +
								           " Transaction Id: " + financialTransaction.getTrxnId());
					}
	
				}
			}
		}
		
		assertEquals(transactionCount, expected999AccountTransactions);

	}
	
	private void verifyReversedLastPaymentLoanSchedules(LoanScheduleEntity[] schedules, Results expectedResults)
	{
		List<PaymentDetail> list = expectedResults.getPayments();
		assertEquals(list.size(), schedules.length);
		for (int i=0; i < schedules.length; i++)
		{
			if (i == schedules.length - 1)
			{
				Money zeroAmount = new Money("0");
				assertEquals(schedules[i].getPrincipalPaid(), zeroAmount);
				assertEquals(schedules[i].getPaymentDate(), null);
				assertEquals(schedules[i].isPaid(), false);
			}
			else
			{
				assertEquals(schedules[i].isPaid(), true);
			}
			verifyScheduleAndPaymentDetail(schedules[i], list.get(i));
		}
		
	}
	
	private void setUpLoanAndVerifyForScheduleGenerationWhenLastPaymentIsReversed(InternalConfiguration config, LoanParameters loanParams, 
			Results expectedResults) throws
	AccountException, PersistenceException, MeetingException
	{
		
		accountBO = setUpLoanFor999Account(config, loanParams);
		PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = accountBO
		.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 
				loanParams.getNumberOfPayments());
        // before any payment is made
        printLoanScheduleEntities(paymentsArray);
        PersonnelBO personnelBO =  new PersonnelPersistence().getPersonnel(userContext.getId());
        
        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();
        
		for (int i = 0; i < paymentsArray.length; i++) {
			loanSchedule = paymentsArray[i];
			Money amountPaid = loanSchedule.getTotalDueWithFees();
			paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
			accountBO.applyPayment(paymentData, true);
		}
		
		new TestObjectPersistence().persist(accountBO);
		actionDateEntities = accountBO.getAccountActionDates();
		paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
		// after all payments are made
		printLoanScheduleEntities(paymentsArray);
		List<AccountPaymentEntity> accountPayments = new LoanPersistence().retrieveAllAccountPayments(accountBO.getAccountId());
		Set<AccountPaymentEntity> list = new LinkedHashSet<AccountPaymentEntity>();
		for (AccountPaymentEntity payment : accountPayments)
			list.add(payment);
		accountBO.setAccountPayments(list);
		accountBO.adjustLastPayment("Adjust last payment");
		new TestObjectPersistence().persist(accountBO);
		accountPayments = new LoanPersistence().retrieveAllAccountPayments(accountBO.getAccountId());
		assertEquals(accountPayments.get(0).getAmount(), new Money("0")); // this is the last payment reversed so amount is 0
		actionDateEntities = accountBO.getAccountActionDates();
		paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
		//		 after last payment is reversed
		printLoanScheduleEntities(paymentsArray);
		verifyReversedLastPaymentLoanSchedules(paymentsArray, expectedResults);
		

	}
	
	private void runOneLoanScheduleGenerationForLastPaymentReversal(String fileName)
					throws NumberFormatException, PropertyNotFoundException, SystemException, ApplicationException, URISyntaxException 
	
	{

		LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
		InternalConfiguration config = testCaseData.getInternalConfig();
		LoanParameters loanParams = testCaseData.getLoanParams();
		setUpLoanAndVerifyForScheduleGenerationWhenLastPaymentIsReversed(config, loanParams, testCaseData.expectedResult);
		
	}
	
	private void runOneLoanScheduleGenerationForLoanReversal(String fileName) 
		throws NumberFormatException, PropertyNotFoundException, SystemException, ApplicationException, URISyntaxException 
	
	{
	
		LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
		InternalConfiguration config = testCaseData.getInternalConfig();
		LoanParameters loanParams = testCaseData.getLoanParams();
		setUpLoanAndVerifyForScheduleGenerationWhenLoanIsReversed(config, loanParams, testCaseData.expectedResult);
	
	}
	
	private void verifyScheduleAndPaymentDetail(LoanScheduleEntity schedule, PaymentDetail payment)
	{
		assertEquals(schedule.getPrincipal(), payment.getPrincipal());
		assertEquals(schedule.getInterest(), payment.getInterest());
		assertEquals(schedule.getTotalFeeDue(), payment.getFee());
	}
	
	private void verifyReversedLoanSchedules(LoanScheduleEntity[] schedules, Results expectedResults)
	{
		List<PaymentDetail> list = expectedResults.getPayments();
		assertEquals(list.size(), schedules.length);
		for (int i=0; i < schedules.length; i++)
		{
			Money zeroAmount = new Money("0");
			assertEquals(schedules[i].getPrincipalPaid(), zeroAmount);
			assertEquals(schedules[i].getPaymentDate(), null);
			assertEquals(schedules[i].isPaid(), false);
			verifyScheduleAndPaymentDetail(schedules[i], list.get(i));
		}
		
	}
	
	private void setUpLoanAndVerifyForScheduleGenerationWhenLoanIsReversed(InternalConfiguration config, LoanParameters loanParams, 
			Results expectedResults) throws
	AccountException, PersistenceException, MeetingException
	{
		
		accountBO = setUpLoanFor999Account(config, loanParams);
		PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = accountBO.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 
				loanParams.getNumberOfPayments());
        // before any payment is made
        printLoanScheduleEntities(paymentsArray);
        PersonnelBO personnelBO =  new PersonnelPersistence().getPersonnel(userContext.getId());
        
        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();
        
		for (int i = 0; i < paymentsArray.length; i++) {
			loanSchedule = paymentsArray[i];
			Money amountPaid = loanSchedule.getTotalDueWithFees();
			paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
			accountBO.applyPayment(paymentData, true);
		}
		
		new TestObjectPersistence().persist(accountBO);
		actionDateEntities = accountBO.getAccountActionDates();
		paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
		// after all payments are made
		printLoanScheduleEntities(paymentsArray);
		// reverse loan
		((LoanBO)accountBO).reverseLoanDisbursal(personnelBO, "Reverse this loan for testing");
		new TestObjectPersistence().persist(accountBO);
		actionDateEntities = accountBO.getAccountActionDates();
		paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
		verifyReversedLoanSchedules(paymentsArray, expectedResults);
		List<AccountPaymentEntity> accountPayments = new LoanPersistence().retrieveAllAccountPayments(accountBO.getAccountId());
		// every payment is reversed so amount is 0
		for (AccountPaymentEntity payment : accountPayments)
			assertEquals(payment.getAmount(), new Money("0"));  
	
	}
	
	private AccountBO setUpLoanFor999Account(InternalConfiguration config, LoanParameters loanParams) throws
			AccountException, PersistenceException, MeetingException
	{
		setNumberOfInterestDays(config.getDaysInYear());
		AccountingRules.setDigitsAfterDecimal((short)config.getDigitsAfterDecimal());
		Money.setDefaultCurrency(AccountingRules.getMifosCurrency());
		setInitialRoundingMode(config.getInitialRoundingMode());
		setFinalRoundingMode(config.getFinalRoundingMode());
		AccountingRules.setInitialRoundOffMultiple(new BigDecimal(config.getInitialRoundOffMultiple()));
		AccountingRules.setFinalRoundOffMultiple(new BigDecimal(config.getFinalRoundOffMultiple()));
		AccountingRules.setCurrencyRoundingMode(config.getCurrencyRoundingMode());
		// AccountingRules.setRoundingRule(config.getCurrencyRoundingMode());
		
		/*
		 * When constructing a "meeting" here, it looks like the frequency 
		 * should be "EVERY_X" for weekly or monthly loan interest posting.
		 */
		// EVERY_WEEK, EVERY_DAY and EVERY_MONTH are defined as 1
		MeetingBO meeting = null;
		if (loanParams.getPaymentFrequency() == RecurrenceType.MONTHLY) {
			meeting = new MeetingBO((short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
				TestObjectFactory.EVERY_MONTH, new Date(), CUSTOMER_MEETING, "meeting place");
		} else {
			meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(loanParams.getPaymentFrequency(), EVERY_MONTH,
						CUSTOMER_MEETING));
		}
		
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		
		Date startDate = new Date(System.currentTimeMillis());
		
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "L", ApplicableTo.GROUPS, startDate,
				PrdStatus.LOAN_ACTIVE, Double.parseDouble(loanParams.getPrincipal()), 
				Double.parseDouble(loanParams.getAnnualInterest()), loanParams.getNumberOfPayments(),
				loanParams.getLoanType(), false, false, center
				.getCustomerMeeting().getMeeting(), config.getGracePeriodType(),
				"1", "1");
		
		List<FeeView> feeViewList = createFeeViews(config, loanParams, meeting);

		AccountBO loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				new Money(loanParams.getPrincipal()), loanParams.getNumberOfPayments(), startDate, false, 
				Double.parseDouble(loanParams.getAnnualInterest()), config.getGracePeriod(), 
				new FundBO(), feeViewList, null);
		
		return loan;
       
	}
	
	
	private AccountBO setUpLoanFor999AccountTestLoanReversal(InternalConfiguration config, LoanParameters loanParams, 
			Results calculatedResults, int paymentToReverse, boolean payLastPayment) throws
	AccountException, PersistenceException, MeetingException
	{
		
		AccountBO loan = setUpLoanFor999Account(config, loanParams);
		
		PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = loan.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 
				loanParams.getNumberOfPayments());
        PersonnelBO personnelBO =  new PersonnelPersistence().getPersonnel(userContext.getId());
        
        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();
        
		for (int i = 0; i < paymentsArray.length; i++) {
			loanSchedule = paymentsArray[i];
			Money amountPaid = loanSchedule.getTotalDueWithFees();
			paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
			loan.applyPayment(paymentData, true);
			if (i==(paymentToReverse-1))
				break;
		}
		
		boolean lastPayment = paymentToReverse == paymentsArray.length;
		calculatedResults.setAccount999(((LoanBO)loan).calculate999Account(lastPayment));
		new TestObjectPersistence().persist(loan);
		((LoanBO)loan).reverseLoanDisbursal(personnelBO, "Test 999 account for loan reversal");
		new TestObjectPersistence().persist(loan);
		
		return loan;
	}
	
	private AccountBO setUpLoanFor999AccountTestLastPaymentReversal(InternalConfiguration config, LoanParameters loanParams, 
			Results calculatedResults, int paymentToReverse, boolean payLastPayment) throws
	AccountException, PersistenceException, MeetingException
	{
		
		AccountBO loan = setUpLoanFor999Account(config, loanParams);
		
		PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = loan.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 
				loanParams.getNumberOfPayments());
        PersonnelBO personnelBO =  new PersonnelPersistence().getPersonnel(userContext.getId());
        
        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();
        
		for (int i = 0; i < paymentsArray.length; i++) {
			loanSchedule = paymentsArray[i];
			Money amountPaid = loanSchedule.getTotalDueWithFees();
			paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
			loan.applyPayment(paymentData, true);
			if (i==(paymentToReverse-1))
				break;
		}
		
		boolean lastPayment = paymentToReverse == paymentsArray.length;
		calculatedResults.setAccount999(((LoanBO)loan).calculate999Account(lastPayment));
		new TestObjectPersistence().persist(loan);
		actionDateEntities = loan.getAccountActionDates();
		paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
		List<AccountPaymentEntity> accountPayments = new LoanPersistence().retrieveAllAccountPayments(loan.getAccountId());
		assertEquals(accountPayments.size(), paymentToReverse);
		Set<AccountPaymentEntity> list = new LinkedHashSet<AccountPaymentEntity>();
		for (AccountPaymentEntity payment : accountPayments)
			list.add(payment);
		loan.setAccountPayments(list);
		loan.adjustLastPayment("Adjust last payment");
		new TestObjectPersistence().persist(loan);
		accountPayments = new LoanPersistence().retrieveAllAccountPayments(loan.getAccountId());
		assertEquals(accountPayments.get(0).getAmount(), new Money("0")); // this is the last payment reversed so amount is 0
		actionDateEntities = loan.getAccountActionDates();
		paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
		assertEquals(paymentsArray[paymentToReverse-1].getPrincipalPaid(),  new Money("0"));		
		
		if (payLastPayment)
		{
			for (int i = paymentToReverse -1; i < paymentsArray.length; i++) {
				loanSchedule = paymentsArray[i];
				Money amountPaid = loanSchedule.getTotalDueWithFees();
				paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
				loan.applyPayment(paymentData, true);
				new TestObjectPersistence().persist(loan);
			}
		
		}
		
		return loan;
	}
	
	
	
	
	private void setUpLoanAndVerify999AccountWhenLoanIsRepaid(InternalConfiguration config, LoanParameters loanParams, 
			Results expectedResults) throws AccountException, PersistenceException, MeetingException
	{
		
		accountBO = setUpLoanFor999Account(config, loanParams);
		PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = accountBO
		.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 
				loanParams.getNumberOfPayments());
        // before any payment is made
        printLoanScheduleEntities(paymentsArray);
        PersonnelBO personnelBO =  new PersonnelPersistence().getPersonnel(userContext.getId());
        
        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();
        // pay one payment
		for (int i = 0; i < 1; i++) {
			loanSchedule = paymentsArray[i];
            Money amountPaid = loanSchedule.getPrincipal().add(loanSchedule.getInterest());
			paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
			accountBO.applyPayment(paymentData, true);
		}
		new TestObjectPersistence().persist(accountBO);
		actionDateEntities = accountBO.getAccountActionDates();
		paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
		printLoanScheduleEntities(paymentsArray);
		// loan repay
		UserContext uc = TestUtils.makeUser();
		((LoanBO)accountBO).makeEarlyRepayment(((LoanBO)accountBO).getTotalEarlyRepayAmount(), null,
				null, "1", uc.getId());
		new TestObjectPersistence().persist(accountBO);
		actionDateEntities = accountBO.getAccountActionDates();
		paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, loanParams.getNumberOfPayments());
		printLoanScheduleEntities(paymentsArray);
		// no 999 account is logged
		Set<AccountPaymentEntity> paymentList = ((LoanBO)accountBO).getAccountPayments();
		int i=0;
		for (Iterator<AccountPaymentEntity> paymentIterator = paymentList.iterator(); paymentIterator.hasNext();) 
		{
			AccountPaymentEntity payment = paymentIterator.next();
			Set<AccountTrxnEntity> transactionList = payment.getAccountTrxns();
			for (AccountTrxnEntity transaction : transactionList)
			{
				Set<FinancialTransactionBO> list = ((LoanTrxnDetailEntity)transaction).getFinancialTransactions();
				for (Iterator<FinancialTransactionBO> iterator = list.iterator(); iterator.hasNext();) 
				{
					FinancialTransactionBO financialTransaction = iterator.next();
					if (financialTransaction.getPostedAmount().equals(expectedResults.getAccount999())
							|| financialTransaction.getPostedAmount().negate().equals(expectedResults.getAccount999()))
					{
						i++;
					}
	
				}
			}
		}
		assertEquals(i, 0);

	}
	
	private void run999AccountWhenLoanIsRepaid(String fileName)
	throws NumberFormatException, PropertyNotFoundException, SystemException, ApplicationException, URISyntaxException 
	{
	
		LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
		InternalConfiguration config = testCaseData.getInternalConfig();
		LoanParameters loanParams = testCaseData.getLoanParams();
		setUpLoanAndVerify999AccountWhenLoanIsRepaid(config, loanParams, testCaseData.expectedResult);
	}
	
	public void test999AccountLoansWithFees() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException, Exception
	{
		String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/decliningInterest/";
		String[] dataFileNames = getCSVFiles(rootPath);
		for (int i=0; i < dataFileNames.length; i++) {
			if (dataFileNames[i].startsWith("testcase-2008-05-13-declining-grace-fee-set1"))  {
				runOne999AccountTestCaseLoanWithFees(rootPath + dataFileNames[i]);
				tearDown();
				setUp();
			}
		}
	}
	
	public void test999AccountLoansWithFees2() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException, Exception
	{
		String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/flatInterest/";
		String[] dataFileNames = getCSVFiles(rootPath);
		for (int i=0; i < dataFileNames.length; i++) {
			if (dataFileNames[i].startsWith("testcase") && dataFileNames[i].contains("flat-grace-fee-set")) {
				runOne999AccountTestCaseLoanWithFees(rootPath + dataFileNames[i]);
				tearDown();
				setUp();
			}
		}
		
	}

//	 verify that 999 account transactions are logged after last payment is made
	public void testPositive999AccountTest2LoanWithFees() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{
		String dataFileName = "account999-withfees.csv";
		runOne999AccountTestCaseLoanWithFees(rootPath + dataFileName);	
	}
	
	public void test999AccountWhenLoanIsRepaid() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException, Exception 
	{
		String dataFileName = "account999-test3.csv";
		run999AccountWhenLoanIsRepaid(rootPath + dataFileName);
	}
	
	public void test999AccountForLoanReversal() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException, Exception 
	{
		String dataFileName = "account999-test3.csv";
		int expected999AccountTransactions = 4;
		int paymentToReverse = 3;
		boolean payLastPayment = false;
		runOne999AccountTestCaseWithDataFromSpreadSheetForLoanReversal(rootPath + dataFileName, expected999AccountTransactions,
				paymentToReverse, payLastPayment);	
	}
	
	public void test999AccountForLastPaymentReversal() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException, Exception 
	{
		String dataFileName = "account999-test3.csv";
		int expected999AccountTransactions = 4;
		int paymentToReverse = 3;
		boolean payLastPayment = false;
		runOne999AccountTestCaseWithDataFromSpreadSheetForLastPaymentReversal(rootPath + dataFileName, expected999AccountTransactions,
				paymentToReverse, payLastPayment);	
	}
	
	public void testLoanScheduleGenerationWhenLastPaymentIsReversed() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException, Exception 
	{
		String dataFileName = "account999-test3.csv";
		runOneLoanScheduleGenerationForLastPaymentReversal(rootPath + dataFileName);
	}
	
	public void testLoanScheduleGenerationWhenLoanIsReversed() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException, Exception 
	{
		String dataFileName = "account999-test3.csv";
		runOneLoanScheduleGenerationForLoanReversal(rootPath + dataFileName);
	}
	
	//	 verify that 999 account transactions are logged after last payment is made
	public void testNegative999Account() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{
		String dataFileName = "account999-test2.csv";
		runOne999AccountTestCaseWithDataFromSpreadSheet(rootPath + dataFileName);	
	}
	
	//	 verify that 999 account transactions are logged after last payment is made
	public void test999AccountTest1() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException
	{
		String dataFileName = "account999-test1.csv";
		runOne999AccountTestCaseWithDataFromSpreadSheet(rootPath + dataFileName);	
	}
	
	
	
	
	
	// no 999account should be logged in this case
	public void test999AccountForMiddlePaymentReversal() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException, Exception
	{
		String dataFileName = "account999-test3.csv";
		int expected999AccountTransactions = 0;
		int paymentToReverse = 2;
		boolean payLastPayment = false;
		runOne999AccountTestCaseWithDataFromSpreadSheetForLastPaymentReversal(rootPath + dataFileName, expected999AccountTransactions,
				paymentToReverse, payLastPayment);	
	}
	
	//	payment is reversed and repay to the last payment
	public void test999AccountForMiddlePaymentReversalAndPayToLastPayment() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{
		String dataFileName = "account999-test3.csv";
		int expected999AccountTransactions = 2;
		int paymentToReverse = 2;
		boolean payLastPayment = true;
		runOne999AccountTestCaseWithDataFromSpreadSheetForLastPaymentReversal(rootPath + dataFileName, expected999AccountTransactions,
				paymentToReverse, payLastPayment);	
	}
	
	private AccountBO setUpLoanFor999AccountTest(InternalConfiguration config, LoanParameters loanParams, Results calculatedResults) throws
	AccountException, PersistenceException, MeetingException
	{
		setNumberOfInterestDays(config.getDaysInYear());
		AccountingRules.setDigitsAfterDecimal((short)config.getDigitsAfterDecimal());
		Money.setDefaultCurrency(AccountingRules.getMifosCurrency());
		setInitialRoundingMode(config.getInitialRoundingMode());
		setFinalRoundingMode(config.getFinalRoundingMode());
		AccountingRules.setInitialRoundOffMultiple(new BigDecimal(config.getInitialRoundOffMultiple()));
		AccountingRules.setFinalRoundOffMultiple(new BigDecimal(config.getFinalRoundOffMultiple()));
		AccountingRules.setCurrencyRoundingMode(config.getCurrencyRoundingMode());
		// AccountingRules.setRoundingRule(config.getCurrencyRoundingMode());
		
		/*
		 * When constructing a "meeting" here, it looks like the frequency 
		 * should be "EVERY_X" for weekly or monthly loan interest posting.
		 */
		// EVERY_WEEK, EVERY_DAY and EVERY_MONTH are defined as 1
		MeetingBO meeting = null;
		if (loanParams.getPaymentFrequency() == RecurrenceType.MONTHLY) {
			meeting = new MeetingBO((short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
				TestObjectFactory.EVERY_MONTH, new Date(), CUSTOMER_MEETING, "meeting place");
		} else {
			meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(loanParams.getPaymentFrequency(), EVERY_MONTH,
						CUSTOMER_MEETING));
		}
		
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		
		Date startDate = new Date(System.currentTimeMillis());
		
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "L", ApplicableTo.GROUPS, startDate,
				PrdStatus.LOAN_ACTIVE, Double.parseDouble(loanParams.getPrincipal()), 
				Double.parseDouble(loanParams.getAnnualInterest()), loanParams.getNumberOfPayments(),
				loanParams.getLoanType(), false, false, center
				.getCustomerMeeting().getMeeting(), config.getGracePeriodType(),
				"1", "1");
		
		List<FeeView> feeViewList = createFeeViews(config, loanParams, meeting);

		AccountBO loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				new Money(loanParams.getPrincipal()), loanParams.getNumberOfPayments(), startDate, false, 
				Double.parseDouble(loanParams.getAnnualInterest()), config.getGracePeriod(), 
				new FundBO(), feeViewList, null);
       
		
		PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = loan
		.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 
				loanParams.getNumberOfPayments());
        PersonnelBO personnelBO =  new PersonnelPersistence().getPersonnel(userContext.getId());
        
        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();
		for (int i = 0; i < paymentsArray.length; i++) {
			loanSchedule = paymentsArray[i];
			Money amountPaid = loanSchedule.getTotalDueWithFees();
			paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
			loan.applyPayment(paymentData, true);
		}
		boolean lastPayment = true;
		calculatedResults.setAccount999(((LoanBO)loan).calculate999Account(lastPayment));
		new TestObjectPersistence().persist(loan);
		return loan;
	}
	
	private AccountBO setUpLoanFor999AccountTestLoanWithFees(InternalConfiguration config, LoanParameters loanParams, Results calculatedResults) throws
	AccountException, PersistenceException, MeetingException
	{
		setNumberOfInterestDays(config.getDaysInYear());
		AccountingRules.setDigitsAfterDecimal((short)config.getDigitsAfterDecimal());
		Money.setDefaultCurrency(AccountingRules.getMifosCurrency());
		setInitialRoundingMode(config.getInitialRoundingMode());
		setFinalRoundingMode(config.getFinalRoundingMode());
		AccountingRules.setInitialRoundOffMultiple(new BigDecimal(config.getInitialRoundOffMultiple()));
		AccountingRules.setFinalRoundOffMultiple(new BigDecimal(config.getFinalRoundOffMultiple()));
		AccountingRules.setCurrencyRoundingMode(config.getCurrencyRoundingMode());
		// AccountingRules.setRoundingRule(config.getCurrencyRoundingMode());
		
		/*
		 * When constructing a "meeting" here, it looks like the frequency 
		 * should be "EVERY_X" for weekly or monthly loan interest posting.
		 */
		// EVERY_WEEK, EVERY_DAY and EVERY_MONTH are defined as 1
		MeetingBO meeting = null;
		if (loanParams.getPaymentFrequency() == RecurrenceType.MONTHLY) {
			meeting = new MeetingBO((short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
				TestObjectFactory.EVERY_MONTH, new Date(), CUSTOMER_MEETING, "meeting place");
		} else {
			meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(loanParams.getPaymentFrequency(), EVERY_MONTH,
						CUSTOMER_MEETING));
		}
	
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		
		Date startDate = new Date(System.currentTimeMillis());
		
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "L", ApplicableTo.GROUPS, startDate,
				PrdStatus.LOAN_ACTIVE, Double.parseDouble(loanParams.getPrincipal()), 
				Double.parseDouble(loanParams.getAnnualInterest()), loanParams.getNumberOfPayments(),
				loanParams.getLoanType(), false, false, center
				.getCustomerMeeting().getMeeting(), config.getGracePeriodType(),
				"1", "1");
		
//		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = createFeeViews(config, loanParams, meeting);


		AccountBO loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				new Money(loanParams.getPrincipal()), loanParams.getNumberOfPayments(), startDate, false, 
				Double.parseDouble(loanParams.getAnnualInterest()), config.getGracePeriod(), 
				new FundBO(), feeViewList, null);
       
		
		PaymentData paymentData = null;
        Set<AccountActionDateEntity> actionDateEntities = loan
		.getAccountActionDates();
        LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 
				loanParams.getNumberOfPayments());
        PersonnelBO personnelBO =  new PersonnelPersistence().getPersonnel(userContext.getId());
        LoanScheduleEntity loanSchedule = null;
        Short paymentTypeId = PaymentTypes.CASH.getValue();
		for (int i = 0; i < paymentsArray.length; i++) {
			loanSchedule = paymentsArray[i];
			Money amountPaid = loanSchedule.getTotalDueWithFees();
			paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
			loan.applyPayment(paymentData, true);
		}
		boolean lastPayment = true;
		calculatedResults.setAccount999(((LoanBO)loan).calculate999Account(lastPayment));
		new TestObjectPersistence().persist(loan);
		return loan;
	}
	
	private void compare999Account(Money expected999Account , Money calculated999Account, String testName)
	{
		System.out.println("Running test: " + testName);
		System.out.println("Results   (Expected : Calculated : Difference)");
		printComparison("999 Account:   ", expected999Account, 
				calculated999Account);
		assertEquals(expected999Account, calculated999Account);
	}
	
	private void runOne999AccountTestCaseWithDataFromSpreadSheet(String fileName) throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{

		LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
		Results calculatedResults = new Results();
		InternalConfiguration config = testCaseData.getInternalConfig();
		LoanParameters loanParams = testCaseData.getLoanParams();
		accountBO = setUpLoanFor999AccountTest(config, loanParams, calculatedResults);
		AccountPaymentEntity lastPmt = null;
		Set<AccountPaymentEntity> paymentList = ((LoanBO)accountBO).getAccountPayments();
		int i=1;
		for (Iterator<AccountPaymentEntity> paymentIterator = paymentList.iterator(); paymentIterator.hasNext();) 
		{
			AccountPaymentEntity payment = paymentIterator.next();
			if (i == loanParams.getNumberOfPayments())
			{
				lastPmt = payment;
				break;
			}
			i++;
		}
		Set<AccountTrxnEntity> transactionList = lastPmt.getAccountTrxns();
		for (AccountTrxnEntity transaction : transactionList)
		{
			Set<FinancialTransactionBO> list = ((LoanTrxnDetailEntity)transaction).getFinancialTransactions();
			for (Iterator<FinancialTransactionBO> iterator = list.iterator(); iterator.hasNext();) 
			{
				FinancialTransactionBO financialTransaction = iterator.next();
				if (financialTransaction.getGlcode().getGlcodeId() == 51)
				{
					assertEquals(financialTransaction.getGlcode().getGlcode(), "31401");
					Money postedAmount = financialTransaction.getPostedAmount();
					Money expected999Account = testCaseData.getExpectedResult().getAccount999();
					assertEquals(postedAmount, expected999Account);
					if (expected999Account.getAmountDoubleValue() > 0)
					{
						assertEquals(FinancialConstants.fromValue(financialTransaction.getDebitCreditFlag()), FinancialConstants.CREDIT);
					}
					else
					{
						assertEquals(FinancialConstants.fromValue(financialTransaction.getDebitCreditFlag()), FinancialConstants.DEBIT);
					}
					
				}
			}
		}
		compare999Account(testCaseData.getExpectedResult().getAccount999(), calculatedResults.getAccount999(), fileName);

	}
	
	private void runOne999AccountTestCaseLoanWithFees(String fileName) throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{

		LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
		Results calculatedResults = new Results();
		InternalConfiguration config = testCaseData.getInternalConfig();
		LoanParameters loanParams = testCaseData.getLoanParams();
		accountBO = setUpLoanFor999AccountTestLoanWithFees(config, loanParams, calculatedResults);
		AccountPaymentEntity lastPmt = null;
		Set<AccountPaymentEntity> paymentList = ((LoanBO)accountBO).getAccountPayments();
		int i=1;
		for (Iterator<AccountPaymentEntity> paymentIterator = paymentList.iterator(); paymentIterator.hasNext();) 
		{
			AccountPaymentEntity payment = paymentIterator.next();
			if (i == loanParams.getNumberOfPayments())
			{
				lastPmt = payment;
				break;
			}
			i++;
		}
		Set<AccountTrxnEntity> transactionList = lastPmt.getAccountTrxns();
		for (AccountTrxnEntity transaction : transactionList)
		{
			Set<FinancialTransactionBO> list = ((LoanTrxnDetailEntity)transaction).getFinancialTransactions();
			for (Iterator<FinancialTransactionBO> iterator = list.iterator(); iterator.hasNext();) 
			{
				FinancialTransactionBO financialTransaction = iterator.next();
				if (financialTransaction.getGlcode().getGlcodeId() == 51)
				{
					assertEquals(financialTransaction.getGlcode().getGlcode(), "31401");
					Money postedAmount = financialTransaction.getPostedAmount();
					Money expected999Account = testCaseData.getExpectedResult().getAccount999();
					if (!postedAmount.equals(expected999Account))
					{
						System.out.println("File name: " + fileName + " posted amount: " + postedAmount.getAmountDoubleValue() +
							" expected amount: " + expected999Account.getAmountDoubleValue());
					}
					assertEquals(postedAmount, expected999Account);
					if (expected999Account.getAmountDoubleValue() > 0)
					{
						assertEquals(FinancialConstants.fromValue(financialTransaction.getDebitCreditFlag()), FinancialConstants.CREDIT);
					}
					else
					{
						assertEquals(FinancialConstants.fromValue(financialTransaction.getDebitCreditFlag()), FinancialConstants.DEBIT);
					}
					
				}
			}
		}
		compare999Account(testCaseData.getExpectedResult().getAccount999(), calculatedResults.getAccount999(), fileName);

	}
	
	
	
	
	//	 verify that 999 account transactions are logged after last payment is made
	public void testPositive999AccountTest2() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{
		String dataFileName = "account999-test3.csv";
		runOne999AccountTestCaseWithDataFromSpreadSheet(rootPath + dataFileName);	
	}
	
	private void printLoanScheduleEntities(LoanScheduleEntity[] loanSchedules)
	{
		for (int i=0; i < loanSchedules.length; i++)
		{
			System.out.println("Loan Schedule #: " + (i+1));
			System.out.println("Principal:   " + loanSchedules[i].getPrincipal().getAmountDoubleValue());
			System.out.println("Principal Paid:   " +  loanSchedules[i].getPrincipalPaid().getAmountDoubleValue());
			System.out.println("Interest Paid:   " +  loanSchedules[i].getInterestPaid().getAmountDoubleValue());
			System.out.println("Interest:   " +  loanSchedules[i].getInterest().getAmountDoubleValue());
			System.out.println("Total Due:   " + loanSchedules[i].getTotalDue().getAmountDoubleValue());
			if (loanSchedules[i].getPaymentDate() != null)
			{
				System.out.println("Payment Date:   " + loanSchedules[i].getPaymentDate().toString());
			}
			else
			{
				System.out.println("Payment Date: null");
			}
			System.out.println("Is paid:   " +  loanSchedules[i].isPaid());
		}		
	}
	
	
	/* end of of 999 account testing */
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	
	


	/* This part is for the new financial calculation */
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	
	
	

	class LoanParameters {
		private String principal = null;
		private InterestType loanType = null;
		private String annualInterest = null;
		private short numberOfPayments = 0;
		private RecurrenceType paymentFrequency = null;
		
		public LoanParameters(String principal, InterestType loanType,
				String annualInterest, short numberOfPayments,
				RecurrenceType paymentFrequency) {
			super();
			this.principal = principal;
			this.loanType = loanType;
			this.annualInterest = annualInterest;
			this.numberOfPayments = numberOfPayments;
			this.paymentFrequency = paymentFrequency;
		}
		
		public LoanParameters()
		{
		}

		public String getPrincipal() {
			return principal;
		}

		public void setPrincipal(String principal) {
			this.principal = principal;
		}

		public InterestType getLoanType() {
			return loanType;
		}

		public void setLoanType(InterestType loanType) {
			this.loanType = loanType;
		}

		public String getAnnualInterest() {
			return annualInterest;
		}

		public void setAnnualInterest(String annualInterest) {
			this.annualInterest = annualInterest;
		}

		public short getNumberOfPayments() {
			return numberOfPayments;
		}

		public void setNumberOfPayments(short numberOfPayments) {
			this.numberOfPayments = numberOfPayments;
		}

		public RecurrenceType getPaymentFrequency() {
			return paymentFrequency;
		}

		public void setPaymentFrequency(RecurrenceType paymentFrequency) {
			this.paymentFrequency = paymentFrequency;
		}
		
	}
	
	class InternalConfiguration {
		private int daysInYear = 0;
		// right now we are just supporting CEILING, FLOOR, HALF_UP
		private RoundingMode initialRoundingMode = null;
		// should this be constrained to .001, .01, .5, .1, 1 as in the spreadsheet?
		private String initialRoundOffMultiple = null; 
		// right now we are just supporting CEILING, FLOOR, HALF_UP
		private RoundingMode finalRoundingMode = null;
		// should this be constrained to .001, .01, .5, .1, 1 as in the spreadsheet?
		private String finalRoundOffMultiple = null; 
		// right now we are just supporting CEILING, FLOOR, HALF_UP
		private RoundingMode currencyRoundingMode = null;
		// the number of digits to use to the right of the decimal for interal caculations
		private int internalPrecision = 13;
		// digits after decimal right now is in the application configuration
		private int digitsAfterDecimal = 1;
		// grace period type
		private GraceType gracePeriodType = GraceType.NONE; 
		private short gracePeriod = 0; 
		private FeeFrequencyType feeFrequency;  // if feeFrequency is null there is no fee setting for the loan
		private FeeFormula feeType;             // if rate-based fee, indicates what the rate applies to
		private boolean isFeeRateBased;         // true if rate based, false if applies a fixed amount
		private String feeValue = null;
		private String feePercentage = null;
		
		public FeeFrequencyType getFeeFrequency() {
			return feeFrequency;
		}

		public void setFeeFrequency(FeeFrequencyType feeFrequency) {
			this.feeFrequency = feeFrequency;
		}

		public String getFeePercentage() {
			return feePercentage;
		}

		public void setFeePercentage(String feePercentage) {
			this.feePercentage = feePercentage;
		}

		public FeeFormula getFeeType() {
			return feeType;
		}

		public void setFeeType(FeeFormula feeType) {
			this.feeType = feeType;
		}

		public String getFeeValue() {
			return feeValue;
		}

		public void setFeeValue(String feeValue) {
			this.feeValue = feeValue;
		}

		public int getDigitsAfterDecimal() {
			return digitsAfterDecimal;
		}

		public void setDigitsAfterDecimal(int digitsAfterDecimal) {
			this.digitsAfterDecimal = digitsAfterDecimal;
		}

		public InternalConfiguration(int daysInYear,
				RoundingMode initialRoundingMode,
				String initialRoundOffMultiple, RoundingMode finalRoundingMode,
				String finalRoundOffMultiple, RoundingMode currencyRoundingMode,
				int internalPrecision, GraceType gracePeriodType, short gracePeriod) {
			super();
			this.daysInYear = daysInYear;
			this.initialRoundingMode = initialRoundingMode;
			this.initialRoundOffMultiple = initialRoundOffMultiple;
			this.finalRoundingMode = finalRoundingMode;
			this.finalRoundOffMultiple = finalRoundOffMultiple;
			this.currencyRoundingMode = currencyRoundingMode;
			this.internalPrecision = internalPrecision;
			this.gracePeriodType = gracePeriodType;
			this.gracePeriod = gracePeriod;
		}
		
		public InternalConfiguration()
		{
		}

		public int getDaysInYear() {
			return daysInYear;
		}

		public void setDaysInYear(int daysInYear) {
			this.daysInYear = daysInYear;
		}

		public RoundingMode getInitialRoundingMode() {
			return initialRoundingMode;
		}

		public void setInitialRoundingMode(RoundingMode initialRoundingMode) {
			this.initialRoundingMode = initialRoundingMode;
		}

		public String getInitialRoundOffMultiple() {
			return initialRoundOffMultiple;
		}

		public void setInitialRoundOffMultiple(String initialRoundOffMultiple) {
			this.initialRoundOffMultiple = initialRoundOffMultiple;
		}

		public RoundingMode getFinalRoundingMode() {
			return finalRoundingMode;
		}

		public void setFinalRoundingMode(RoundingMode finalRoundingMode) {
			this.finalRoundingMode = finalRoundingMode;
		}

		public String getFinalRoundOffMultiple() {
			return finalRoundOffMultiple;
		}

		public void setFinalRoundOffMultiple(String finalRoundOffMultiple) {
			this.finalRoundOffMultiple = finalRoundOffMultiple;
		}

		public RoundingMode getCurrencyRoundingMode() {
			return currencyRoundingMode;
		}

		public void setCurrencyRoundingMode(RoundingMode currencyRoundingMode) {
			this.currencyRoundingMode = currencyRoundingMode;
		}

		public int getInternalPrecision() {
			return internalPrecision;
		}

		public void setInternalPrecision(int internalPrecision) {
			this.internalPrecision = internalPrecision;
		}

		public short getGracePeriod() {
			return gracePeriod;
		}

		public void setGracePeriod(short gracePeriod) {
			this.gracePeriod = gracePeriod;
		}

		public GraceType getGracePeriodType() {
			return gracePeriodType;
		}

		public void setGracePeriodType(GraceType gracePeriodType) {
			this.gracePeriodType = gracePeriodType;
		}

		public boolean isFeeRateBased() {
			return this.isFeeRateBased;
		}
		
		public void setIsFeeRateBased (boolean isRateBased) {
			this.isFeeRateBased = isRateBased;
		}
	}
	
	
	class Results {
		// each installment has payment = interest + principal
		Money totalPayments = null; // sum of all payments 
		Money totalInterest = null; // sum of all interests for all payments
		Money totalFee = null;
		Money totalPrincipal = null;  // sum of all principals for all payments
		// detailed list of all payments. Each payment includes payment, interest, principal and balance
		List<PaymentDetail> payments = null;
		Money roundedTotalInterest = null;
		Money account999 = null;
		
		public List<PaymentDetail> getPayments() {
			return payments;
		}
		public void setPayments(List<PaymentDetail> payments) {
			this.payments = payments;
		}
		public Money getTotalInterest() {
			return totalInterest;
		}
		public void setTotalInterest(Money totalInterest) {
			this.totalInterest = totalInterest;
		}
		public Money getTotalPayments() {
			return totalPayments;
		}
		public void setTotalPayments(Money totalPayments) {
			this.totalPayments = totalPayments;
		}
		public Money getTotalPrincipal() {
			return totalPrincipal;
		}
		public void setTotalPrincipal(Money totalPrincipal) {
			this.totalPrincipal = totalPrincipal;
		}
		public Money getAccount999() {
			return account999;
		}
		public void setAccount999(Money account999) {
			this.account999 = account999;
		}
		public Money getRoundedTotalInterest() {
			return roundedTotalInterest;
		}
		public void setRoundedTotalInterest(Money roundedTotalInterest) {
			this.roundedTotalInterest = roundedTotalInterest;
		}
		
		public Money getTotalFee() {
			return totalFee;
		}
		public void setTotalFee(Money totalFee) {
			this.totalFee = totalFee;
		}
	}
	
class LoanTestCaseData {
		
		private LoanParameters loanParams = null;
		private Results expectedResult = null;
		InternalConfiguration internalConfig = null;
		
		public InternalConfiguration getInternalConfig() {
			return internalConfig;
		}

		public void setInternalConfig(InternalConfiguration config) {
			this.internalConfig = config;
		}

		public LoanTestCaseData()
		{
		}
		
		public Results getExpectedResult() {
			return expectedResult;
		}
		public void setExpectedResult(Results expectedResult) {
			this.expectedResult = expectedResult;
		}
		public LoanParameters getLoanParams() {
			return loanParams;
		}
		public void setLoanParams(LoanParameters loanParams) {
			this.loanParams = loanParams;
		}
	}
	
	private void printResults(Results expectedResult, Results calculatedResult, String testName) {
		//System.out.println("Running test: " + testName);
		System.out.println("Results are (Expected : Calculated : Difference)");
		printComparison("Total Interest: ",expectedResult.getTotalInterest(),
			calculatedResult.getTotalInterest());
		printComparison("Total Payments: " , expectedResult.getTotalPayments(),
			calculatedResult.getTotalPayments());
		printComparison("Total Principal: ", expectedResult.getTotalPrincipal(), 
			calculatedResult.getTotalPrincipal());
		printComparison("Total Fees: ", expectedResult.getTotalFee(), 
				calculatedResult.getTotalFee());
		
		List<PaymentDetail> expectedPayments = expectedResult.getPayments();
		List<PaymentDetail> calculatedPayments = calculatedResult.getPayments();
		System.out.println("Number of Installments: " + expectedPayments.size() + 
				" : " + calculatedPayments.size() + " : " + (expectedPayments.size() -
				calculatedPayments.size()));
		for (int i=0; i < expectedPayments.size(); i++)
		{
			System.out.println("Payment #: " + (i+1));
			printComparison("Balance:   ", expectedPayments.get(i).getBalance(), 
				calculatedPayments.get(i).getBalance());
			printComparison("Interest:  ", expectedPayments.get(i).getInterest(), 
				calculatedPayments.get(i).getInterest());
			printComparison("Payment:   ", expectedPayments.get(i).getPayment(), 
				calculatedPayments.get(i).getPayment());
			printComparison("Principal: ", expectedPayments.get(i).getPrincipal(), 
				calculatedPayments.get(i).getPrincipal());
			printComparison("Fee:       ", expectedPayments.get(i).getFee(), 
					calculatedPayments.get(i).getFee());
		}		
	}

	private void compareResults(Results expectedResult, Results calculatedResult, String testName)
	{
		printResults(expectedResult, calculatedResult, testName);
		
		assertEquals(testName, expectedResult.getTotalInterest(), 
				calculatedResult.getTotalInterest());
		assertEquals(testName, expectedResult.getTotalPayments(), 
				calculatedResult.getTotalPayments());
		assertEquals(testName, expectedResult.getTotalPrincipal(), 
				calculatedResult.getTotalPrincipal());
		List<PaymentDetail> expectedPayments = expectedResult.getPayments();
		List<PaymentDetail> calculatedPayments = calculatedResult.getPayments();
		assertEquals(testName, expectedPayments.size(), calculatedPayments.size());
		for (int i=0; i < expectedPayments.size(); i++)
		{
			/*
			 * Do not assert balance since it is derived from loan information
			assertEquals(testName, expectedPayments.get(i).getBalance(), 
					calculatedPayments.get(i).getBalance());
			 */
			assertEquals(testName, expectedPayments.get(i).getInterest(), 
					calculatedPayments.get(i).getInterest());
			assertEquals(testName, expectedPayments.get(i).getPayment(), 
					calculatedPayments.get(i).getPayment());
			assertEquals(testName, expectedPayments.get(i).getPrincipal(), 
					calculatedPayments.get(i).getPrincipal());
		}
		
	}
	
	private void printComparison(String label, Money expected, Money calculated) {
		System.out.println(label + expected + 
				" : " + calculated + " : " + expected.subtract(calculated));
	}
	
	private void setNumberOfInterestDays(int days) {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRules.AccountingRulesNumberOfInterestDays,new Short((short)days));
	}
	
	private void setInitialRoundingMode(RoundingMode mode) {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRules.AccountingRulesInitialRoundingMode, mode.toString());		
	}
	
	private void setFinalRoundingMode(RoundingMode mode) {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRules.AccountingRulesFinalRoundingMode, mode.toString());		
	}
	

	private AccountBO setUpLoan(InternalConfiguration config, LoanParameters loanParams) throws MeetingException, NumberFormatException, AccountException

	{
		setNumberOfInterestDays(config.getDaysInYear());
		AccountingRules.setDigitsAfterDecimal((short)config.getDigitsAfterDecimal());
		Money.setDefaultCurrency(AccountingRules.getMifosCurrency());
		setInitialRoundingMode(config.getInitialRoundingMode());
		setFinalRoundingMode(config.getFinalRoundingMode());
		AccountingRules.setInitialRoundOffMultiple(new BigDecimal(config.getInitialRoundOffMultiple()));
		AccountingRules.setFinalRoundOffMultiple(new BigDecimal(config.getFinalRoundOffMultiple()));
		AccountingRules.setCurrencyRoundingMode(config.getCurrencyRoundingMode());
		
		/*
		 * When constructing a "meeting" here, it looks like the frequency 
		 * should be "EVERY_X" for weekly or monthly loan interest posting.
		 */
		// EVERY_WEEK, EVERY_DAY and EVERY_MONTH are defined as 1
		MeetingBO meeting = null;
		if (loanParams.getPaymentFrequency() == RecurrenceType.MONTHLY) {
			meeting = new MeetingBO((short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
				TestObjectFactory.EVERY_MONTH, new Date(), CUSTOMER_MEETING, "meeting place");
		} else {
			meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(loanParams.getPaymentFrequency(), EVERY_MONTH,
						CUSTOMER_MEETING));
		}

		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		
		Date startDate = new Date(System.currentTimeMillis());
		
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "L", ApplicableTo.GROUPS, startDate,
				PrdStatus.LOAN_ACTIVE, Double.parseDouble(loanParams.getPrincipal()), 
				Double.parseDouble(loanParams.getAnnualInterest()), loanParams.getNumberOfPayments(),
				loanParams.getLoanType(), false, false, center
				.getCustomerMeeting().getMeeting(), config.getGracePeriodType(),
				"1", "1");
		
		List<FeeView> feeViewList = createFeeViews(config, loanParams, meeting);

		AccountBO accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				new Money(loanParams.getPrincipal()), loanParams.getNumberOfPayments(), startDate, false, 
				Double.parseDouble(loanParams.getAnnualInterest()), config.getGracePeriod(), 
				new FundBO(), feeViewList, null);
		
		
		new TestObjectPersistence().persist(accountBO);
		return accountBO;
	}
	
	private List<FeeView> createFeeViews (InternalConfiguration config, LoanParameters loanParams, MeetingBO meeting) {
		
		List<FeeView> feeViews = new ArrayList<FeeView>();
		
		//Only periodic fees get merged into loan installments
		if (!(config.getFeeFrequency() == null) && config.getFeeFrequency() == FeeFrequencyType.PERIODIC){
				feeViews.add(createPeriodicFeeView(config, loanParams, meeting));
			}

		return feeViews;
	}
	
	private FeeView createPeriodicFeeView (InternalConfiguration config, LoanParameters loanParams, MeetingBO meeting) {
		FeeBO fee = null;
		if (config.isFeeRateBased()){
			fee = TestObjectFactory.createPeriodicRateFee("testLoanFee",
					FeeCategory.LOAN, new Double (config.getFeePercentage()), 
					config.getFeeType(),
					loanParams.getPaymentFrequency(), (short) 1, userContext, meeting);
		}
		else {
			fee = TestObjectFactory.createPeriodicAmountFee("testLoanFee", FeeCategory.LOAN, config.getFeeValue(), 
                    loanParams.getPaymentFrequency(), Short.valueOf("1"));
		}

		FeeView feeView = new FeeView(userContext, fee);
		return feeView;
	}
	
	private Results calculatePayments(InternalConfiguration config, AccountBO accountBO, LoanParameters loanParams)
	{
		
		
		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
		.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 
				loanParams.getNumberOfPayments());

	
		MathContext context = new MathContext(config.getInternalPrecision());
		BigDecimal totalPrincipal = new BigDecimal(0, context);
		BigDecimal totalInterest = new BigDecimal(0, context);
		BigDecimal totalFees = new BigDecimal(0, context);
		Money totalPayments = new Money("0");
		Results calculatedResult = new Results();
		List<PaymentDetail> payments = new ArrayList<PaymentDetail>();
		for (LoanScheduleEntity loanEntry : paymentsArray)
		{
			PaymentDetail payment = new PaymentDetail();
			Money calculatedPayment = new Money(loanEntry.getPrincipal().getAmount()
													.add (loanEntry.getInterest().getAmount()
															.add (loanEntry.getTotalFees().getAmount())));
			payment.setPayment   (calculatedPayment);
			payment.setInterest  (loanEntry.getInterest());
			payment.setPrincipal (loanEntry.getPrincipal());
			payment.setFee       (loanEntry.getTotalFees());
			
			totalPrincipal = totalPrincipal.add (loanEntry.getPrincipal().getAmount());
			totalInterest  = totalInterest .add (loanEntry.getInterest().getAmount());
			totalPayments  = totalPayments .add (calculatedPayment);
			totalFees      = totalFees     .add (loanEntry.getTotalFees().getAmount());
			
			payments.add(payment);
		}	
		calculatedResult.setPayments(payments);
		calculatedResult.setTotalInterest(new Money(totalInterest));
		calculatedResult.setTotalPayments(totalPayments);
		calculatedResult.setTotalPrincipal(new Money(totalPrincipal));
		calculatedResult.setTotalFee(new Money (totalFees));
		
		/*
		 * Set balance after each installment is paid, excluding fees or penalties.
		 * For flat-interest loans, balance is total of all remaining principal and interest.
		 * For declining-interest loans, balance is total remaining principal.
		 */  
		if (loanParams.loanType.getValue()==InterestType.FLAT.getValue()) {
			Money balance = new Money(totalPrincipal.add(totalInterest));
			for( PaymentDetail paymentDetail : payments)
			{
				balance = balance.subtract(paymentDetail.getPrincipal())
									.subtract(paymentDetail.getInterest());
				paymentDetail.setBalance(balance);
			}
		}
		else if (loanParams.loanType.getValue()==InterestType.DECLINING.getValue()) {
			Money balance = new Money (totalPrincipal);
			for( PaymentDetail paymentDetail : payments)
			{
				balance = balance.subtract(paymentDetail.getPrincipal());
				paymentDetail.setBalance(balance);
			}
		}
			
		return calculatedResult;
		
	}
	
	
	
	private void parseLoanParams(String paramType, String line, LoanParameters loanParams)
	{
		String tempLine = line.substring(paramType.length(), line.length() - 1);
		String[] tokens = tempLine.split(",");
		for (int i=0; i < tokens.length; i++)
		{
			String token = tokens[i];
			if (StringUtils.isNullAndEmptySafe(token))
			{
				if ((paramType.indexOf(principal)>= 0) && (loanParams.getPrincipal() == null))
					loanParams.setPrincipal(token);
				else if (paramType.indexOf(loanType)>= 0)
				{
					InterestType interestType = null;
					if (token.equals("Fixed Principal"))
							interestType = InterestType.DECLINING_EPI;
					else
						interestType = InterestType.valueOf(token.toUpperCase());
					loanParams.setLoanType(interestType);
				}
				else if (paramType.indexOf(annualInterest)>= 0)
				{
					int pos = token.indexOf("%");
					String interest = token.substring(0, pos);
					loanParams.setAnnualInterest(interest);
				}
				else if (paramType.indexOf(numberOfPayments) >= 0)
					loanParams.setNumberOfPayments(Short.parseShort(token));
				else if (paramType.indexOf(paymentFrequency)>= 0)
				{
					RecurrenceType recurrenceType = RecurrenceType.valueOf(token.toUpperCase());
					loanParams.setPaymentFrequency(recurrenceType);
				}
				break;
					
			}
		}
		
		
	}
	
	private String getToken(String line, String param)
	{
		int index = line.indexOf(param);
		line = line.substring(index + param.length(), line.length() - 1);
		
		String[] tokens = line.split(",");
		String token = null;
		for (int j=0; j < tokens.length; j++) 
		{
			token = tokens[j];
			if (StringUtils.isNullAndEmptySafe(token))
				break;
		}
		return token;
	}
	
	
	private void parseConfigParams(String paramType, String line, InternalConfiguration config)
	{
		String tempLine = line.substring(paramType.length(), line.length() - 1);
		String[] tokens = tempLine.split(",");
		for (int i=0; i < tokens.length; i++)
		{
			String token = tokens[i];
			if (StringUtils.isNullAndEmptySafe(token))
			{
				if (paramType.indexOf(initialRoundingMode) >= 0)
				{
					RoundingMode mode = RoundingMode.valueOf(token.toUpperCase());
					config.setInitialRoundingMode(mode);
					token = getToken(tempLine, feeFrequency);
					if (token.toUpperCase().equals("PERIODIC"))
						config.setFeeFrequency(FeeFrequencyType.PERIODIC);
					else if (token.toUpperCase().equals("ONETIME"))
						config.setFeeFrequency(FeeFrequencyType.ONETIME);
					else
						config.setFeeFrequency(null);
				}
				else if (paramType.indexOf(initialRoundOffMultiple) >= 0)
				{
					config.setInitialRoundOffMultiple(token);
					token = getToken(tempLine, feeType);
					config.setIsFeeRateBased(true);
					if (token.equals(feeTypePrincipalPlusInterest))
						config.setFeeType(FeeFormula.AMOUNT_AND_INTEREST);
					else if (token.equals(feeTypeInterest))
						config.setFeeType(FeeFormula.INTEREST);
					else if (token.equals(feeTypePrincipal))
						config.setFeeType(FeeFormula.AMOUNT);
					else if (token.equals(feeTypeValue))  //Not rate-based, don't use FeeFormula
						config.setIsFeeRateBased(false);
					else
						throw new RuntimeException("Unrecognized fee type: " + token);
				}
				else if (paramType.indexOf(finalRoundingMode) >= 0)
				{
					RoundingMode mode = RoundingMode.valueOf(token.toUpperCase());
					config.setFinalRoundingMode(mode);
					token = getToken(tempLine, feeValue);
					config.setFeeValue(token);
				}
				else if (paramType.indexOf(finalRoundOffMultiple) >= 0)
				{
					config.setFinalRoundOffMultiple(token);
					token = getToken(tempLine, feePercentage);
					int pos = token.indexOf("%");
					token = token.substring(0, pos);
					config.setFeePercentage(token);
				}
				else if (paramType.indexOf(currencyRounding)>= 0)
				{
					RoundingMode mode = RoundingMode.valueOf(token.toUpperCase());
					config.setCurrencyRoundingMode(mode);
					token = getToken(tempLine, gracePeriodType);
					GraceType type = null;
					if (token.toUpperCase().equals("ALL"))
						type = GraceType.GRACEONALLREPAYMENTS;
					else if (token.toUpperCase().equals("PRINCIPAL"))
						type = GraceType.PRINCIPALONLYGRACE;
					else 
						type = GraceType.NONE;
					
					config.setGracePeriodType(type);
					
				}
				else if (paramType.indexOf(digitsAfterDecimal) >= 0)
				{
					config.setDigitsAfterDecimal(Short.parseShort(token));
					token = getToken(tempLine, gracePeriod);
					config.setGracePeriod(Short.parseShort(token));
				}
				else if (paramType.indexOf(daysInYear) >= 0)
				{
					config.setDaysInYear(Short.parseShort(token));
				}
				/*else if (paramType.indexOf(gracePeriodType)>= 0)
				{
					GraceType type = null;
					if (token.toUpperCase().equals("ALL"))
						type = GraceType.GRACEONALLREPAYMENTS;
					else if (token.toUpperCase().equals("PRINCIPAL"))
						type = GraceType.PRINCIPALONLYGRACE;
					else 
						type = GraceType.NONE;
					
					config.setGracePeriodType(type);
				}
				else if (paramType.indexOf(gracePeriod)>= 0)
				{
					if (config.getGracePeriodType() != GraceType.NONE)
						config.setGracePeriod(Short.parseShort(token));
				}*/
				break;
					
			}
		}
		
		
	}
	
	private void parseTotals(String paramType, String line, Results result)
	{
		String tempLine = line.substring(paramType.length(), line.length() -1);
		int index = tempLine.indexOf(paramType);
		tempLine = tempLine.substring(index + paramType.length(), tempLine.length() -1);
		String[] tokens = tempLine.split(",");
		boolean totalPayments = false;
		boolean totalInterests = true;
		boolean totalFee = true;
		boolean totalPrincipals = true;
		for (int i=0; i < tokens.length; i++)
		{
			String token = tokens[i].trim();
			if (StringUtils.isNullAndEmptySafe(token))
			{
				if (totalPayments == false)
				{
					result.setTotalPayments(new Money(token));
					totalPayments = true;
					totalPrincipals = false;
				}
				else if (totalInterests == false)
				{
					result.setTotalInterest(new Money(token));
					totalInterests = true;
					totalFee = false;
				}
				else if (totalFee == false)
				{
					result.setTotalFee(new Money(token));
					totalFee = true;
				}
				else if (totalPrincipals == false)
				{
					result.setTotalPrincipal(new Money(token));
					totalPrincipals = true;
					totalInterests = false;
				}
				else 
					return;
					
			}
		}
		
		
	}
	
	private void parse999Account(String paramType, String line, Results result)
	{
		String tempLine = line.substring(paramType.length(), line.length() -1);
		int index = tempLine.indexOf(paramType);
		tempLine = tempLine.substring(index + paramType.length(), tempLine.length() -1);
		String[] tokens = tempLine.split(",");
		if (tokens.length < 2)
			return;
		result.setAccount999(new Money(tokens[1]).negate());
		
	}
	
	private void parseRoundedTotalInterest(String paramType, String line, Results result)
	{
		String tempLine = line.substring(paramType.length(), line.length() -1);
		int index = tempLine.indexOf(paramType);
		tempLine = tempLine.substring(index + paramType.length(), tempLine.length() -1);
		String[] tokens = tempLine.split(",");
		if (tokens.length < 8)
			return;
		result.setRoundedTotalInterest(new Money(tokens[3]));
		
	}
	
	private void parsePaymentDetail(String paramType, String line, Results result)
	{
		
		int index = line.indexOf(",,");
		String tempLine = line.substring(index + 1, line.length() -1);
		String[] tokens = tempLine.split(",");
		boolean paymentIndex = false;
		boolean payment = true;
		boolean principal = true;
		boolean interest = true;
		boolean balance = true;
		boolean fee = true;
		PaymentDetail paymentDetail = new PaymentDetail();
		for (int i=0; i < tokens.length; i++)
		{
			String token = tokens[i].trim();
			if (StringUtils.isNullAndEmptySafe(token))
			{
				if (paymentIndex == false)
				{
					int paymentNumber = Integer.parseInt(token);
					int expectedPaymentNumber = result.getPayments().size() + 1;
					if (paymentNumber !=  expectedPaymentNumber)
						throw new RuntimeException("Parsing error. paymentNumber " + paymentNumber + " Expected: " + expectedPaymentNumber);
					paymentIndex = true;
					payment = false;
				}
				else if (payment == false)
				{
					paymentDetail.setPayment(new Money(token));
					payment = true;
					principal = false;
				}
				else if (principal == false)
				{
					paymentDetail.setPrincipal(new Money(token));
					principal = true;
					interest = false;
				}
				else if (interest == false)
				{
					paymentDetail.setInterest(new Money(token));
					interest = true;
					fee = false;
				}
				else if (fee == false)
				{
					paymentDetail.setFee(new Money(token));
					fee = true;
					balance = false;
				}
				else if (balance == false)
				{
					paymentDetail.setBalance(new Money(token));
					result.getPayments().add(paymentDetail);
					return;
				}
			}
		}
		
		
	}
	
	private LoanTestCaseData loadSpreadSheetData(String fileName) throws URISyntaxException
	{
		File file = new File(ResourceLoader.getURI(fileName));
	    FileInputStream fileInputStream = null;
	    InputStreamReader inputStreamReader = null;
	    BufferedReader bufferedReader = null;
	    LoanTestCaseData testCaseData = new LoanTestCaseData();
	    boolean startPayment = false;
	    int paymentIndex = 0;
	    String line = null;

	    try 
	    {
	    	fileInputStream = new FileInputStream(file);
	    	inputStreamReader = new InputStreamReader(fileInputStream);
	    	bufferedReader = new BufferedReader(inputStreamReader);
	    	
		      // dataInputStream.available() returns 0 if the file does not have more lines.
	    	
	    	LoanParameters loanParams = new LoanParameters();
	    	InternalConfiguration config = new InternalConfiguration();
	    	Results expectedResult = new Results();
	    	List<PaymentDetail> list = new ArrayList<PaymentDetail>();
	    	expectedResult.setPayments(list);
		    while ((line = bufferedReader.readLine()) != null) 
		    {
		       String[] tokens = line.split(",");
		       for (int i=0; i < tokens.length; i++)
		       {
		    	   String token = tokens[i];
		    	   if (StringUtils.isNullAndEmptySafe(token))
		    	   {
		    		   if ((token.indexOf(principal) >= 0) ||( token.indexOf(loanType) >= 0) || (token.indexOf(annualInterest) >=0)
		    				   || (token.indexOf(numberOfPayments) >=0) || (token.indexOf(paymentFrequency) >= 0))
		    		   {
		    			   parseLoanParams(token, line, loanParams);
		    			   break;
		    		   }
		    		   else if ((token.indexOf(initialRoundingMode) >= 0 ) || (token.indexOf(finalRoundingMode)>= 0 )
		    		           || (token.indexOf(initialRoundOffMultiple) >= 0 )
		    				   || (token.indexOf(finalRoundOffMultiple) >= 0 ) || (token.indexOf(currencyRounding)>= 0 ) 
		    				   || (token.indexOf(digitsAfterDecimal) >= 0 ) || (token.indexOf(daysInYear) >= 0))
		    				   
		    		   {
		    			   parseConfigParams(token, line, config);
		    			   break;
		    		   }
		    		   else if (token.indexOf(calculatedTotals) == 0)
		    			   parseRoundedTotalInterest(token, line, expectedResult);
		    		   else if (token.indexOf(account999) == 0)
		    			   parse999Account(token, line, expectedResult);
		    		   else if (token.indexOf(totals)  >= 0)
		    			   parseTotals(token, line, expectedResult);
		    		   else if (token.indexOf(start)  >= 0) 
		    		   {
		    			   startPayment = true;
		    			   break;
		    		   }
		    		   else if (startPayment)
		    		   {
		    			   parsePaymentDetail(token, line, expectedResult);
		    			   paymentIndex++;
		    			   if (paymentIndex >= loanParams.getNumberOfPayments())
		    			   {
		    				   testCaseData.setExpectedResult(expectedResult);
		    				   testCaseData.setInternalConfig(config);
		    				   testCaseData.setLoanParams(loanParams);
		    				   return testCaseData;
		    			   }
		    			   break;
		    		   }
		    			   
		    	   }
		    	
		       }
		       
		    }
            if (fileInputStream != null)
            	fileInputStream.close();
            if (inputStreamReader != null)
            	inputStreamReader.close();
            if (bufferedReader != null)
            	bufferedReader.close();

	    } 
	    catch (Exception e) 
	    {
	    	throw new RuntimeException(e);
	    } 
	    return testCaseData;
	   
	}
	


	
	/*
	 * This test case will populate the data classes for a loan test case with data from spreadsheet and
	 * calculates payments and compares
	 */
	private void runOneTestCaseWithDataFromSpreadSheet(String directoryName, String fileName) throws NumberFormatException, PropertyNotFoundException,
								SystemException, ApplicationException, URISyntaxException 
	{

		System.out.println();
		System.out.println("Running Test: " + fileName);
		LoanTestCaseData testCaseData = loadSpreadSheetData(directoryName + fileName);
		accountBO = setUpLoan(testCaseData.getInternalConfig(), testCaseData.getLoanParams());
		// calculated results
		Results calculatedResult = calculatePayments(testCaseData.getInternalConfig(), accountBO, testCaseData.getLoanParams());  
		compareResults(testCaseData.getExpectedResult(), calculatedResult, fileName);
		
		
	}
	

	private String[] getCSVFiles(String directoryPath) throws URISyntaxException {
		File dir = new File(ResourceLoader.getURI(directoryPath));
	    
	    FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return name.endsWith(".csv");
	        }
	    };
	    return dir.list(filter);
	    		
	}
	
	public void xtestCaseWithDataFromSpreadSheets() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{
		//String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/flatInterest/";
		String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/";
		
		//String[] dataFileNames = {"testcases-2008-04-22.set1.01.csv"};
		String[] dataFileNames = {"loan-repayment-master-test1.csv"};
		for (int i=0; i < dataFileNames.length; i++)
			runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
	}

	public void xtestIssue1623FromSpreadSheets() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{

		String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/";
		String[] dataFileNames = {"loan-repayment-master-issue1623.csv"};
		for (int i=0; i < dataFileNames.length; i++)
			runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);

	}
 
	public void testAllFlatInterestTestCases() throws Exception 
	{
		String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/flatInterest/";
		String[] dataFileNames = getCSVFiles(rootPath);
		for (int i=0; i < dataFileNames.length; i++) {
			if (dataFileNames[i].startsWith("testcase-2008-05-13-flat-grace-fee-set1")) {
				runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
				tearDown();
				setUp();
			}
		}
	}

	public void xtestFlatInterestGraceAndFeesTestCases() throws Exception 
	{
		String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/flatInterest/";
		String[] dataFileNames = getCSVFiles(rootPath);
		for (int i=0; i < dataFileNames.length; i++) {
			if (dataFileNames[i].startsWith("testcase") && dataFileNames[i].contains("flat-grace-fee-set")) {
				runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
				tearDown();
				setUp();
			}
		}
	}
    
	public void testDecliningInterestTestCases() throws Exception 
	{
		String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/decliningInterest/";
		String[] dataFileNames = getCSVFiles(rootPath);
		for (int i=0; i < dataFileNames.length; i++) {
			if (dataFileNames[i].startsWith("testcase-2008-05-13-declining-grace-fee-set1")) {
				runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
				tearDown();
				setUp();
			}
		}
	}

	public void xtestAllDecliningInterestTestCases() throws Exception 
	{
		String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/decliningInterest/";
		String[] dataFileNames = getCSVFiles(rootPath);
		for (int i=0; i < dataFileNames.length; i++) {
			if (dataFileNames[i].startsWith("testcase-2008-05-12-declining-set1")) {
				runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
				tearDown();
				setUp();
			}
		}
	}

	
	/*
	 * This test case populates data from spreadsheet for loan params and expected results
	 */
	public void xtestOneExampleOfTestCaseFromSpreadSheet() throws NumberFormatException, PropertyNotFoundException,
								SystemException, ApplicationException 
	{
		
		// set up config
		InternalConfiguration config = new InternalConfiguration();
		config.setDaysInYear(365);
		config.setFinalRoundingMode(RoundingMode.CEILING);
		config.setFinalRoundOffMultiple("0.01");
		config.setInitialRoundingMode(RoundingMode.CEILING);
		config.setInitialRoundOffMultiple("1");
		config.setCurrencyRoundingMode(RoundingMode.CEILING);
		config.setInternalPrecision(13);
		config.setDigitsAfterDecimal(3);

		
		// set up loan params
		LoanParameters loanParams = new LoanParameters();
		loanParams.setLoanType(InterestType.FLAT);
		loanParams.setNumberOfPayments((short)5);
		loanParams.setPaymentFrequency(RecurrenceType.WEEKLY);
		loanParams.setAnnualInterest("12.00");
		loanParams.setPrincipal("1002");
		
		// set up expected results
		Results expectedResult = new Results();
		expectedResult.setTotalInterest(new Money("11.53"));
		expectedResult.setTotalPayments(new Money("1013.53"));  
		expectedResult.setTotalPrincipal(new Money("1002"));  // this loan amount
		List<PaymentDetail> list = new ArrayList<PaymentDetail>();
		// 1st payment
		PaymentDetail payment = new PaymentDetail();
		payment.setPayment(new Money("203.000"));
		payment.setInterest(new Money("2.306"));
		payment.setBalance(new Money("810.530"));
		payment.setPrincipal(new Money("200.694"));
		list.add(payment);
		//	2nd payment
		payment = new PaymentDetail();
		payment.setPayment(new Money("203.000"));
		payment.setInterest(new Money("2.306"));
		payment.setBalance(new Money("607.530"));
		payment.setPrincipal(new Money("200.694"));
		list.add(payment);
		//	3rd  payment
		payment = new PaymentDetail();
		payment.setPayment(new Money("203.000"));
		payment.setInterest(new Money("2.306"));
		payment.setBalance(new Money("404.530"));
		payment.setPrincipal(new Money("200.694"));
		list.add(payment);
		//	4th  payment
		payment = new PaymentDetail();
		payment.setPayment(new Money("203.000"));
		payment.setInterest(new Money("2.306"));
		payment.setBalance(new Money("201.530"));
		payment.setPrincipal(new Money("200.694"));
		list.add(payment);
		//	last payment
		payment = new PaymentDetail();
		payment.setPayment(new Money("201.530"));
		payment.setInterest(new Money("2.306"));
		payment.setBalance(new Money("0"));
		payment.setPrincipal(new Money("199.224"));
		list.add(payment);
		expectedResult.setPayments(list);
		
		expectedResult.setPayments(list);
		
		accountBO = setUpLoan(config, loanParams);
		
		// calculated results
		Results calculatedResult = calculatePayments(config, accountBO, loanParams);  
		compareResults(expectedResult, calculatedResult, "testOneExampleOfTestCaseFromSpreadSheet");
		
		
	}


	
	/*
	 * This test case is meant to reproduce issue 1648 using the new data classes
	 */
	public void xtestIssue1648New() throws NumberFormatException, PropertyNotFoundException,
								SystemException, ApplicationException 
	{
		// set up config
		InternalConfiguration config = new InternalConfiguration();
		config.setDaysInYear(365);
		config.setFinalRoundingMode(RoundingMode.HALF_UP);
		config.setFinalRoundOffMultiple("0.001");
		config.setInitialRoundingMode(RoundingMode.HALF_UP);
		config.setInitialRoundOffMultiple("0.001");
		config.setCurrencyRoundingMode(RoundingMode.HALF_UP);
		config.setInternalPrecision(10);
		
		// set up loan params
		LoanParameters loanParams = new LoanParameters();
		loanParams.setLoanType(InterestType.DECLINING);
		loanParams.setNumberOfPayments((short)50);
		loanParams.setPaymentFrequency(RecurrenceType.WEEKLY);
		loanParams.setAnnualInterest("19.0");
		loanParams.setPrincipal("10000");
		
		// set up expected results
		Results expectedResult = new Results();
		expectedResult.setTotalInterest(new Money("956.8"));
		expectedResult.setTotalPayments(new Money("10000.4"));  // this test doesn't compare this, I made this up
		expectedResult.setTotalPrincipal(new Money("10000"));  // this loan amount
		List<PaymentDetail> list = new ArrayList<PaymentDetail>();  // we don't have this info from this test
		expectedResult.setPayments(list);
		
		accountBO = setUpLoan(config, loanParams);
		
		// calculated results
		Results calculatedResult = calculatePayments(config, accountBO, loanParams);  
		compareResults(expectedResult, calculatedResult, "testIssue1648New");
		
		
	}
	
	/****************************************************************************************/
	/****************************************************************************************/
	/****************************************************************************************/
	
	/*
	 * This test case is meant to reproduce issue 1648 
	 */
	public void xtestIssue1648()
	throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException {
		
		int digitsRightOfDecimal = 10;
		
		/**
		 * What should expectedTotalInterest be?  From the spreadsheet test 
		 * case we have 956.76.  But we are using 1 digit to the right of the 
		 * decimal place, so depending on the rounding mode, it should be
		 * 956.7 or 956.8.
		 */
		String expectedTotalInterest = "956.8";
		
		short numberOfInstallments = 50;
		double interestRate = 19.0;
		InterestType interestType = InterestType.DECLINING;
		String loanAmount = "10000";
		short gracePeriodDuration = 0;
		Date startDate = new Date(System.currentTimeMillis());

		/*
		 * When constructing a "meeting" here, it looks like the frequency 
		 * should be "EVERY_X" for weekly or monthly loan interest posting.
		 */
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
						CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "L", ApplicableTo.GROUPS, startDate,
				PrdStatus.LOAN_ACTIVE, 10000.0, interestRate, numberOfInstallments,
				interestType, false, false, center
				.getCustomerMeeting().getMeeting(), GraceType.NONE,
				"1", "1");
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(
				loanAmount), numberOfInstallments, startDate, false, 
				interestRate, gracePeriodDuration, new FundBO(), feeViewList, null);
		
		new TestObjectPersistence().persist(accountBO);
		assertEquals(numberOfInstallments, accountBO.getAccountActionDates().size());

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
		.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, numberOfInstallments);

		int paymentCount = 1;

		MathContext context = new MathContext(digitsRightOfDecimal);
		BigDecimal totalPrincipal = new BigDecimal(0, context);
		BigDecimal totalInterest = new BigDecimal(0, context);
		
		for (LoanScheduleEntity loanEntry : paymentsArray) {
			System.out.println(paymentCount + " -- P: " + loanEntry.getPrincipal() + "  I: " + loanEntry.getInterest());
			totalPrincipal = totalPrincipal.add(loanEntry.getPrincipal().getAmount());
			totalInterest = totalInterest.add(loanEntry.getInterest().getAmount());
			++paymentCount;
		}
		System.out.println("TotalPrincipal: " + totalPrincipal + "   TotalInterest: " + totalInterest);
		
		assertEquals(new BigDecimal(loanAmount), totalPrincipal);
		assertEquals(new BigDecimal(expectedTotalInterest), totalInterest);
		
	}
	
	
	
	
	class PaymentDetail{
		Money payment = null;
		Money principal = null;
		Money interest = null;
		Money fee = null;
		Money balance = null;
		
		public PaymentDetail(Money payment, Money principal, Money interest, Money balance) {
			super();
			this.payment = payment;
			this.principal = principal;
			this.interest = interest;
			this.balance = balance;
		}
		
		public PaymentDetail()
		{
		}
		
		public Money getBalance() {
			return balance;
		}

		public void setBalance(Money balance) {
			this.balance = balance;
		}

		public Money getInterest() {
			return interest;
		}

		public void setInterest(Money interest) {
			this.interest = interest;
		}

		public Money getPayment() {
			return payment;
		}

		public void setPayment(Money payment) {
			this.payment = payment;
		}

		public Money getPrincipal() {
			return principal;
		}

		public void setPrincipal(Money principal) {
			this.principal = principal;
		}

		public Money getFee() {
			return fee;
		}

		public void setFee(Money fee) {
			this.fee = fee;
		}

		
	}

	public static LoanScheduleEntity[] getSortedAccountActionDateEntity(
			Set<AccountActionDateEntity> actionDateCollection, int expectedCount) {

		LoanScheduleEntity[] sortedList = new LoanScheduleEntity[actionDateCollection
				.size()];

		assertEquals(expectedCount, actionDateCollection.size());

		for (AccountActionDateEntity actionDateEntity : actionDateCollection) {
			sortedList[actionDateEntity.getInstallmentId().intValue() - 1] = (LoanScheduleEntity) actionDateEntity;
		}

		return sortedList;
	}

}
