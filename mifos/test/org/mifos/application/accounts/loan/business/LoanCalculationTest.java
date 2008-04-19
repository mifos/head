package org.mifos.application.accounts.loan.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_WEEK;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.TestAccountActionDateEntity;
import org.mifos.application.accounts.business.TestAccountFeesEntity;
import org.mifos.application.accounts.business.TestAccountPaymentEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.AccountingRules;
import org.mifos.config.ConfigurationManager;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.configuration.business.AccountConfig;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ResourceLoader;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.BufferedReader;



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
	final String interestRounding = "InterestRounding";
	final String digitsAfterDecimal = "Digits After Decimal";
	final String daysInYear = "Days in Year";
	final String totals = "Summed Totals";
	final String start = "Start";
	final String gracePeriodType = "GracePeriodType";
	final String gracePeriod = "GracePeriod";
	final String calculatedTotals = "Calculated Totals";
	String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/";

	

	LoanOfferingBO loanOffering = null;

	// TODO: probably should be of type LoanBO
	protected AccountBO accountBO = null;

	protected AccountBO badAccountBO = null;

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
		Money.setUsingNewMoney(true);
		LoanBO.setUsingNewLoanSchedulingMethod(true);

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
	
	

	/**
	 * Like
	 * {@link #createLoanAccountWithDisbursement(String, CustomerBO, AccountState, Date, LoanOfferingBO, int, Short)}
	 * but differs in various ways.
	 * 
	 * @param globalNum
	 *            Currently ignored (TODO: remove it or honor it)
	 */
	public static LoanBO createLoanAccount(String globalNum,
			CustomerBO customer, AccountState state, Date startDate,
			LoanOfferingBO loanOfering) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startDate);
		MeetingBO meeting = TestObjectFactory.createLoanMeeting(customer
				.getCustomerMeeting().getMeeting());
		List<Date> meetingDates = TestObjectFactory.getMeetingDates(meeting, 6);
		loanOfering.updateLoanOfferingSameForAllLoan(loanOfering);
		LoanBO loan;
		MifosCurrency currency = TestObjectFactory.getCurrency();
		try {
			loan = LoanBO.createLoan(TestUtils.makeUser(), loanOfering,
					customer, state, new Money(currency, "300.0"), Short
							.valueOf("6"), meetingDates.get(0), true, 0.0,
					(short) 0, new FundBO(), new ArrayList<FeeView>(), null,
					Double.parseDouble(loanOfering.getMaxLoanAmount()
							.toString()), Double.parseDouble(loanOfering
							.getMinLoanAmount().toString()), loanOfering
							.getMaxNoInstallments(), loanOfering
							.getMinNoInstallments(),false,null);
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
	
	
	private AccountBO setUpLoanFor999AccountTest(InternalConfiguration config, LoanParameters loanParams, Results calculatedResults) throws
	AccountException, PersistenceException
	{
		setNumberOfInterestDays(config.getDaysInYear());
		AccountingRules.setDigitsAfterDecimal((short)config.getDigitsAfterDecimal());
		Money.setDefaultCurrency(AccountingRules.getMifosCurrency());
		setInitialRoundingMode(config.getInitialRoundingMode());
		setFinalRoundingMode(config.getFinalRoundingMode());
		AccountingRules.setInitialRoundOffMultiple(new BigDecimal(config.getInitialRoundOffMultiple()));
		AccountingRules.setFinalRoundOffMultiple(new BigDecimal(config.getFinalRoundOffMultiple()));
		AccountingRules.setInterestRoundingMode(config.getInterestRoundingMode());
		
		/*
		 * When constructing a "meeting" here, it looks like the frequency 
		 * should be "EVERY_X" for weekly or monthly loan interest posting.
		 */
		// EVERY_WEEK, EVERY_DAY and EVERY_MONTH are defined as 1
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(loanParams.getPaymentFrequency(), EVERY_WEEK,
						CUSTOMER_MEETING));
		
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
		
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		AccountBO loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				new Money(loanParams.getPrincipal()), loanParams.getNumberOfPayments(), startDate, false, 
				Double.parseDouble(loanParams.getAnnualInterest()), config.getGracePeriod(), 
				new FundBO(), feeViewList, null);
		
		//loan.setLoanMeeting(meeting);
		
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
            Money amountPaid = loanSchedule.getPrincipal().add(loanSchedule.getInterest());
			paymentData = PaymentData.createPaymentData(amountPaid, personnelBO, paymentTypeId, loanSchedule.getActionDate());
			loan.applyPayment(paymentData, true);
			
		}
		calculatedResults.setAccount999(((LoanBO)loan).calculate999Account());
		new TestObjectPersistence().persist(loan);
		return loan;
	}
	
	private void compare999Account(Money expected999Account , Money calculated999Account, String testName)
	{
		System.out.println("Running test: " + testName);
		System.out.println("Results   (Expected : Calculated : Difference)");
		printComparison("999 Account:   ", expected999Account, 
				calculated999Account);
		// assertEquals(expected999Account, calculated999Account);
	}
	
	private void runOne999AccountTestCaseWithDataFromSpreadSheet(String fileName) throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{

		LoanTestCaseData testCaseData = loadSpreadSheetData(fileName);
		Results calculatedResults = new Results();
		accountBO = setUpLoanFor999AccountTest(testCaseData.getInternalConfig(), testCaseData.getLoanParams(), calculatedResults);
		compare999Account(testCaseData.getExpectedResult().getAccount999(), calculatedResults.getAccount999(), fileName);

	}
	
	public void test999Account() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{
		String dataFileName = "account999-test1.csv";
		runOne999AccountTestCaseWithDataFromSpreadSheet(rootPath + dataFileName);
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
		private RoundingMode interestRoundingMode = null;
		// the number of digits to use to the right of the decimal for interal caculations
		private int internalPrecision = 13;
		// digits after decimal right now is in the application configuration
		private int digitsAfterDecimal = 1;
		// grace period type
		private GraceType gracePeriodType = GraceType.NONE; 
		private short gracePeriod = 0; 
		
		public int getDigitsAfterDecimal() {
			return digitsAfterDecimal;
		}

		public void setDigitsAfterDecimal(int digitsAfterDecimal) {
			this.digitsAfterDecimal = digitsAfterDecimal;
		}

		public InternalConfiguration(int daysInYear,
				RoundingMode initialRoundingMode,
				String initialRoundOffMultiple, RoundingMode finalRoundingMode,
				String finalRoundOffMultiple, RoundingMode interestRoundingMode,
				int internalPrecision, GraceType gracePeriodType, short gracePeriod) {
			super();
			this.daysInYear = daysInYear;
			this.initialRoundingMode = initialRoundingMode;
			this.initialRoundOffMultiple = initialRoundOffMultiple;
			this.finalRoundingMode = finalRoundingMode;
			this.finalRoundOffMultiple = finalRoundOffMultiple;
			this.interestRoundingMode = interestRoundingMode;
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

		public RoundingMode getInterestRoundingMode() {
			return interestRoundingMode;
		}

		public void setInterestRoundingMode(RoundingMode interestRoundingMode) {
			this.interestRoundingMode = interestRoundingMode;
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

	}
	
	
	class Results {
		// each installment has payment = interest + principal
		Money totalPayments = null; // sum of all payments 
		Money totalInterest = null; // sum of all interests for all payments
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
		System.out.println("Running test: " + testName);
		System.out.println("Results are (Expected : Calculated : Difference)");
		printComparison("Total Interest: ",expectedResult.getTotalInterest(),
			calculatedResult.getTotalInterest());
		printComparison("Total Payments: " , expectedResult.getTotalPayments(),
			calculatedResult.getTotalPayments());
		printComparison("Total Principal: ", expectedResult.getTotalPrincipal(), 
			calculatedResult.getTotalPrincipal());
		
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
			assertEquals(testName, expectedPayments.get(i).getBalance(), 
					calculatedPayments.get(i).getBalance());
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
		AccountingRules.setInterestRoundingMode(config.getInterestRoundingMode());
		
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
				.getNewMeetingForToday(loanParams.getPaymentFrequency(), EVERY_WEEK,
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
		
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		AccountBO accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				new Money(loanParams.getPrincipal()), loanParams.getNumberOfPayments(), startDate, false, 
				Double.parseDouble(loanParams.getAnnualInterest()), config.getGracePeriod(), 
				new FundBO(), feeViewList, null);
		
		
		new TestObjectPersistence().persist(accountBO);
		return accountBO;
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
		Money totalPayments = new Money("0");
		Results calculatedResult = new Results();
		List<PaymentDetail> payments = new ArrayList<PaymentDetail>();
		for (LoanScheduleEntity loanEntry : paymentsArray)
		{
			PaymentDetail payment = new PaymentDetail();
			Money calculatedPayment = new Money(loanEntry.getPrincipal().getAmount().add(loanEntry.getInterest().getAmount()));
			payment.setPayment(calculatedPayment);
			payment.setInterest(loanEntry.getInterest());
			payment.setPrincipal(loanEntry.getPrincipal());
			totalPrincipal = totalPrincipal.add(loanEntry.getPrincipal().getAmount());
			totalInterest = totalInterest.add(loanEntry.getInterest().getAmount());
			totalPayments = totalPayments.add(calculatedPayment);
			payments.add(payment);
		}	
		calculatedResult.setPayments(payments);
		calculatedResult.setTotalInterest(new Money(totalInterest));
		calculatedResult.setTotalPayments(totalPayments);
		calculatedResult.setTotalPrincipal(new Money(totalPrincipal));
		
		// set balance after each payment 
		Money balance = totalPayments;
		for( PaymentDetail paymentDetail : payments)
		{
			Money onePayment = paymentDetail.getPayment();
			balance = balance.subtract(onePayment);
			paymentDetail.setBalance(balance);
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
				}
				else if (paramType.indexOf(initialRoundOffMultiple) >= 0)
				{
					config.setInitialRoundOffMultiple(token);
				}
				else if (paramType.indexOf(finalRoundingMode) >= 0)
				{
					RoundingMode mode = RoundingMode.valueOf(token.toUpperCase());
					config.setFinalRoundingMode(mode);
				}
				else if (paramType.indexOf(finalRoundOffMultiple) >= 0)
				{
					config.setFinalRoundOffMultiple(token);
				}
				else if (paramType.indexOf(interestRounding)>= 0)
				{
					RoundingMode mode = RoundingMode.valueOf(token.toUpperCase());
					config.setInterestRoundingMode(mode);
				}
				else if (paramType.indexOf(digitsAfterDecimal) >= 0)
				{
					config.setDigitsAfterDecimal(Short.parseShort(token));
				}
				else if (paramType.indexOf(daysInYear) >= 0)
				{
					config.setDaysInYear(Short.parseShort(token));
				}
				else if (paramType.indexOf(gracePeriodType)>= 0)
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
				}
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
		if (tokens.length < 7)
			return;
		result.setRoundedTotalInterest(new Money(tokens[5]));
		result.setAccount999((new Money(tokens[6])));
		
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
	    

	    try 
	    {
	    	fileInputStream = new FileInputStream(file);
	    	inputStreamReader = new InputStreamReader(fileInputStream);
	    	bufferedReader = new BufferedReader(inputStreamReader);
	    	
		      // dataInputStream.available() returns 0 if the file does not have more lines.
	    	String line = null;
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
		    				   || (token.indexOf(finalRoundOffMultiple) >= 0 ) || (token.indexOf(interestRounding)>= 0 ) 
		    				   || (token.indexOf(digitsAfterDecimal) >= 0 ) || (token.indexOf(daysInYear) >= 0)
		    				   || (token.indexOf(gracePeriodType) >= 0) || (token.indexOf(gracePeriod) >= 0 ))
		    		   {
		    			   parseConfigParams(token, line, config);
		    			   break;
		    		   }
		    		   else if (token.indexOf(calculatedTotals) == 0)
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

	public void testCaseWithDataFromSpreadSheets() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{
		String[] dataFileNames = {"loan-repayment-master-test1.csv"};
		for (int i=0; i < dataFileNames.length; i++)
			runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
	}

	public void testIssue1623FromSpreadSheets() throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException, URISyntaxException 
	{

		String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/";
		String[] dataFileNames = {"loan-repayment-master-issue1623.csv"};
		for (int i=0; i < dataFileNames.length; i++)
			runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);

	}

	public void testAllTestCases() throws Exception 
	{
		String rootPath = "org/mifos/application/accounts/loan/business/testCaseData/";
		String[] dataFileNames = getCSVFiles(rootPath);
		for (int i=0; i < dataFileNames.length; i++) {
			runOneTestCaseWithDataFromSpreadSheet(rootPath, dataFileNames[i]);
			tearDown();
			setUp();
		}
	}
	
	/*
	 * This test case populates data from spreadsheet for loan params and expected results
	 */
	public void testOneExampleOfTestCaseFromSpreadSheet() throws NumberFormatException, PropertyNotFoundException,
								SystemException, ApplicationException 
	{
		
		// set up config
		InternalConfiguration config = new InternalConfiguration();
		config.setDaysInYear(365);
		config.setFinalRoundingMode(RoundingMode.CEILING);
		config.setFinalRoundOffMultiple("0.01");
		config.setInitialRoundingMode(RoundingMode.CEILING);
		config.setInitialRoundOffMultiple("1");
		config.setInterestRoundingMode(RoundingMode.CEILING);
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
	public void testIssue1648New() throws NumberFormatException, PropertyNotFoundException,
								SystemException, ApplicationException 
	{
		// set up config
		InternalConfiguration config = new InternalConfiguration();
		config.setDaysInYear(365);
		config.setFinalRoundingMode(RoundingMode.HALF_UP);
		config.setFinalRoundOffMultiple("0.001");
		config.setInitialRoundingMode(RoundingMode.HALF_UP);
		config.setInitialRoundOffMultiple("0.001");
		config.setInterestRoundingMode(RoundingMode.HALF_UP);
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
	public void testIssue1648()
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
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(
				loanAmount), numberOfInstallments, startDate, false, 
				interestRate, gracePeriodDuration, new FundBO(), feeViewList, null);
		
		new TestObjectPersistence().persist(accountBO);
		assertEquals(numberOfInstallments, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

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

		
	}



	
/*	
	public void testCreateLoanAccountWithDecliningInterestNoGracePeriod()
			throws NumberFormatException, PropertyNotFoundException,
			SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK,
						CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "L", ApplicableTo.GROUPS, startDate,
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3,
				InterestType.DECLINING, false, false, center
						.getCustomerMeeting().getMeeting(), GraceType.NONE,
				"1", "1");
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(
						"300.0"), Short.valueOf("6"), startDate, false, // 6
				// installments
				1.2, (short) 0, new FundBO(), feeViewList, null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 6);

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
			throws NumberFormatException, PropertyNotFoundException,
			SystemException, ApplicationException {

		short graceDuration = (short) 2;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK,
						CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", ApplicableTo.GROUPS, new Date(System
						.currentTimeMillis()), PrdStatus.LOAN_ACTIVE, 300.0,
				1.2, (short) 3, InterestType.DECLINING, false, false, center
						.getCustomerMeeting().getMeeting());
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(
						"300.0"), Short.valueOf("6"), new Date(System
						.currentTimeMillis()), false, // 6 installments
				1.2, graceDuration, new FundBO(), feeViewList, null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());
		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 6);

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
			throws NumberFormatException, PropertyNotFoundException,
			SystemException, ApplicationException {

		short graceDuration = (short) 2;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK,
						CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "L", ApplicableTo.GROUPS, new Date(System
						.currentTimeMillis()), PrdStatus.LOAN_ACTIVE, 300.0,
				1.2, (short) 3, InterestType.DECLINING, false, false, center
						.getCustomerMeeting().getMeeting(),
				GraceType.PRINCIPALONLYGRACE, "1", "1");
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(
						"300.0"), Short.valueOf("6"), new Date(System
						.currentTimeMillis()), false, // 6 installments
				1.2, graceDuration, new FundBO(), feeViewList, null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 6);

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
			throws NumberFormatException, PropertyNotFoundException,
			SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());

		short graceDuration = (short) 2;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK,
						CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "Loan".substring(0, 1), ApplicableTo.GROUPS, startDate,
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3,
				InterestType.DECLINING, false, true, center
						.getCustomerMeeting().getMeeting(), GraceType.NONE,
				"1", "1");
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(
						"300.0"), Short.valueOf("6"), startDate, false, // 6
				// installments
				1.2, graceDuration, new FundBO(), feeViewList, null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
				.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 6);

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

	public void testCreateLoanAccountWithEqualPrincipalDecliningInterestNoGracePeriod()
	throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK,
						CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "L", ApplicableTo.GROUPS, startDate,
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3,
				InterestType.DECLINING_EPI, false, false, center
				.getCustomerMeeting().getMeeting(), GraceType.NONE,
				"1", "1");
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(
						"300.0"), Short.valueOf("6"), startDate, false, // 6
						// installments
						1.2, (short) 0, new FundBO(), feeViewList, null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
		.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 6);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 1), "50.9", "0.1",
				fees0, paymentsArray[0]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 2), "50.9", "0.1",
				fees0, paymentsArray[1]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 3), "50.9", "0.1",
				fees0, paymentsArray[2]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 4), "50.9", "0.1",
				fees0, paymentsArray[3]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 5), "50.0", "0.0",
				fees0, paymentsArray[4]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * 6), "46.4", "0.0",
				fees0, paymentsArray[5]);

		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
		.getLoanSummary();

		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());

	}


	public void testCreateLoanAccountWithEqualPrincipalDecliningInterestGraceAllRepayments()
	throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException {

		short graceDuration = (short) 2;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK,
						CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", ApplicableTo.GROUPS, new Date(System
						.currentTimeMillis()), PrdStatus.LOAN_ACTIVE, 300.0,
						1.2, (short) 3, InterestType.DECLINING_EPI, false, false, center
						.getCustomerMeeting().getMeeting());
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(
						"300.0"), Short.valueOf("6"), new Date(System
								.currentTimeMillis()), false, // 6 installments
								1.2, graceDuration, new FundBO(), feeViewList, null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());
		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
		.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 6);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (1 + graceDuration)),
				"50.9", "0.1", fees0, paymentsArray[0]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (2 + graceDuration)),
				"50.9", "0.1", fees0, paymentsArray[1]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (3 + graceDuration)),
				"50.9", "0.1", fees0, paymentsArray[2]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (4 + graceDuration)),
				"50.9", "0.1", fees0, paymentsArray[3]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (5 + graceDuration)),
				"50.0", "0.0", fees0, paymentsArray[4]);

		checkLoanScheduleEntity(incrementCurrentDate(14 * (6 + graceDuration)),
				"46.4", "0.0", fees0, paymentsArray[5]);

		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
		.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());

	}

	public void testCreateLoanAccountWithEqualPrincipalDecliningInterestGracePrincipalOnly()
	throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException {

		short graceDuration = (short) 2;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK,
						CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "L", ApplicableTo.GROUPS, new Date(System
						.currentTimeMillis()), PrdStatus.LOAN_ACTIVE, 300.0,
						1.2, (short) 3, InterestType.DECLINING_EPI, false, false, center
						.getCustomerMeeting().getMeeting(),
						GraceType.PRINCIPALONLYGRACE, "1", "1");
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(
						"300.0"), Short.valueOf("6"), new Date(System
								.currentTimeMillis()), false, // 6 installments
								1.2, graceDuration, new FundBO(), feeViewList, null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
		.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 6);

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

		checkLoanScheduleEntity(incrementCurrentDate(14 * 6), "75.0", "0.0",
				fees0, paymentsArray[5]);

		LoanSummaryEntity loanSummaryEntity = ((LoanBO) accountBO)
		.getLoanSummary();
		assertEquals(new Money("300.0"), loanSummaryEntity
				.getOriginalPrincipal());

	}

	public void testCreateLoanAccountWithEqualPrincipalDecliningInterestPrincipalDueOnLastInstallment()
	throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException {
		Date startDate = new Date(System.currentTimeMillis());

		short graceDuration = (short) 2;
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK,
						CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", "Loan".substring(0, 1), ApplicableTo.GROUPS, startDate,
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3,
				InterestType.DECLINING_EPI, false, true, center
				.getCustomerMeeting().getMeeting(), GraceType.NONE,
				"1", "1");
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		List<FeeView> feeViewList = new ArrayList<FeeView>();

		accountBO = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, new Money(
						"300.0"), Short.valueOf("6"), startDate, false, // 6
						// installments
						1.2, graceDuration, new FundBO(), feeViewList, null);
		new TestObjectPersistence().persist(accountBO);
		assertEquals(6, accountBO.getAccountActionDates().size());

		HashMap fees0 = new HashMap();

		Set<AccountActionDateEntity> actionDateEntities = ((LoanBO) accountBO)
		.getAccountActionDates();
		LoanScheduleEntity[] paymentsArray = getSortedAccountActionDateEntity(actionDateEntities, 6);

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
*/	
	private java.sql.Date setDate(int dayUnit, int interval) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(DateUtils.getCurrentDateWithoutTimeStamp());
		calendar.add(dayUnit, interval);
		return new java.sql.Date(calendar.getTimeInMillis());
	}

	private LoanBO createAndRetrieveLoanAccount(LoanOfferingBO loanOffering,
			boolean isInterestDedAtDisb, List<FeeView> feeViews,
			Short noOfinstallments, Double interestRate)
			throws NumberFormatException, AccountException, SystemException,
			ApplicationException {
		LoanBO loan = LoanBO.createLoan(TestUtils.makeUser(), loanOffering,
				group, AccountState.LOAN_APPROVED, new Money("300.0"),
				noOfinstallments, new Date(System.currentTimeMillis()),
				isInterestDedAtDisb, interestRate, (short) 0, new FundBO(),
				feeViews, null, Double.parseDouble(loanOffering
						.getMaxLoanAmount().toString()),
				Double.parseDouble(loanOffering.getMinLoanAmount().toString()),
				loanOffering.getMaxNoInstallments(), loanOffering
						.getMinNoInstallments(),false,null);
		loan.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		accountBO = TestObjectFactory.getObject(AccountBO.class, loan
				.getAccountId());
		return (LoanBO) accountBO;
	}

	private LoanBO createAndRetrieveLoanAccount(LoanOfferingBO loanOffering,
			boolean isInterestDedAtDisb, List<FeeView> feeViews,
			Short noOfinstallments) throws NumberFormatException,
			AccountException, SystemException, ApplicationException {
		return createAndRetrieveLoanAccount(loanOffering, isInterestDedAtDisb,
				feeViews, noOfinstallments, 10.0);
	}

	private LoanOfferingBO createLoanOffering(boolean isPrincipalAtLastInst) {
		return createLoanOffering(isPrincipalAtLastInst, PrdStatus.LOAN_ACTIVE);
	}

	private LoanOfferingBO createLoanOffering(boolean principalAtLastInst,
			PrdStatus status) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		return TestObjectFactory.createLoanOffering("Loan",
				ApplicableTo.GROUPS, new Date(System.currentTimeMillis()),
				status, 300.0, 1.2, 3, InterestType.FLAT, true,
				principalAtLastInst, meeting);
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
		Date startDate = new Date(System.currentTimeMillis());
		createInitialCustomers();
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, center.getCustomerMeeting().getMeeting());
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		return TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
	}


	private void createInitialCustomers() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
	}

	private void changeFirstInstallmentDateToNextDate(AccountBO accountBO) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day + 1);
		for (AccountActionDateEntity accountActionDateEntity : accountBO
				.getAccountActionDates()) {
			((LoanScheduleEntity) accountActionDateEntity)
					.setActionDate(new java.sql.Date(currentDateCalendar
							.getTimeInMillis()));
			break;
		}
	}

	private AccountBO applyPaymentandRetrieveAccount() throws AccountException,
			SystemException {
		Date startDate = new Date(System.currentTimeMillis());
		PaymentData paymentData = PaymentData.createPaymentData(new Money(
				Configuration.getInstance().getSystemConfig().getCurrency(),
				"212.0"), accountBO.getPersonnel(), Short.valueOf("1"),
				startDate);
		paymentData.setRecieptDate(startDate);
		paymentData.setRecieptNum("5435345");

		accountBO.applyPaymentWithPersist(paymentData);
		HibernateUtil.commitTransaction();
		HibernateUtil.getSessionTL().flush();
		HibernateUtil.closeSession();
		return TestObjectFactory.getObject(AccountBO.class, accountBO
				.getAccountId());
	}

	private AccountBO getLoanAccountWithMiscFeeAndPenalty(AccountState state,
			Date startDate, int disbursalType, Money miscFee, Money miscPenalty) {
		LoanBO accountBO = getLoanAccount(state, startDate, disbursalType);
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
		LoanSummaryEntity loanSummaryEntity = accountBO.getLoanSummary();
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
				((LoanScheduleEntity) accountActionDateEntity)
						.setActionDate(new java.sql.Date(dateCalendar
								.getTimeInMillis()));
				break;
			}
		}
	}

	private AccountBO getLoanAccountWithPerformanceHistory() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		/*((ClientBO) client).getPerformanceHistory().setLoanCycleNumber(1);
		 TestObjectFactory.updateObject(client);*/
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", client,
				AccountState.LOAN_APPROVED, startDate, loanOffering);
		((ClientBO) client).getClientPerformanceHistory().updateLoanCounter(
				loanOffering, YesNoFlag.YES);
		TestObjectFactory.updateObject(client);
		TestObjectFactory.updateObject(accountBO);
		return accountBO;
	}

	private AccountBO getLoanAccountWithPerformanceHistory(AccountState state,
			Date startDate, int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		// ((ClientBO) client).getPerformanceHistory().setLoanCycleNumber(1);
		accountBO = TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", client, state, startDate, loanOffering,
				disbursalType);
		((ClientBO) client).getClientPerformanceHistory().updateLoanCounter(
				loanOffering, YesNoFlag.YES);
		return accountBO;

	}

	private AccountBO getLoanAccountWithGroupPerformanceHistory(
			AccountState state, Date startDate, int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan", ApplicableTo.CLIENTS, startDate, PrdStatus.LOAN_ACTIVE,
				300.0, 1.2, 3, InterestType.FLAT, true, true, meeting);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		accountBO = TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, state, startDate, loanOffering,
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
			((LoanScheduleEntity) accountActionDateEntity)
					.setActionDate(new java.sql.Date(currentDateCalendar
							.getTimeInMillis()));
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
			((LoanScheduleEntity) accountActionDateEntity)
					.setActionDate(new java.sql.Date(currentDateCalendar
							.getTimeInMillis()));
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

	private LoanBO getLoanAccount(AccountState state, Date startDate,
			int disbursalType) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, state, startDate, loanOffering,
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
		Set<AccountFeesActionDetailEntity> accountFeesActionDetails = loanScheduleEntity
				.getAccountFeesActionDetails();
		assertEquals("fees were " + feeNames(accountFeesActionDetails),
				expected.size(), accountFeesActionDetails.size());

		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetails) {

			if (expected.get(accountFeesActionDetailEntity.getFee()
					.getFeeName()) != null) {
				assertEquals(new Money((String) expected
						.get(accountFeesActionDetailEntity.getFee()
								.getFeeName())),
						checkPaid ? accountFeesActionDetailEntity
								.getFeeAmountPaid()
								: accountFeesActionDetailEntity.getFeeAmount());
			}
			else {

				assertFalse("Fee amount not found for "
						+ accountFeesActionDetailEntity.getFee().getFeeName(),
						true);
			}
		}
	}

	private String feeNames(Collection<AccountFeesActionDetailEntity> details) {
		StringBuilder debugString = new StringBuilder();
		for (Iterator<AccountFeesActionDetailEntity> iter = details.iterator(); iter
				.hasNext();) {
			AccountFeesActionDetailEntity detail = iter.next();
			debugString.append(detail.getFee().getFeeName());
			if (iter.hasNext()) {
				debugString.append(", ");
			}
		}
		return debugString.toString();
	}

	// altered form protected
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

	private void createInitialObjects() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);

	}

	private AccountBO createLoanAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				startDate, meeting);
		accountBO = TestObjectFactory.createLoanAccount("42423142341", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate,
				loanOffering);
		return accountBO;
	}

	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(Short.valueOf("5"), "value1",
				CustomFieldType.ALPHA_NUMERIC));
		return fields;
	}

}
