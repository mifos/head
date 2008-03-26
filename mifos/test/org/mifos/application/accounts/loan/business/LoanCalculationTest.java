package org.mifos.application.accounts.loan.business;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_WEEK;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.math.BigDecimal;
import java.math.MathContext;
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
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.TestAccountActionDateEntity;
import org.mifos.application.accounts.business.TestAccountFeesEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.persistence.AccountPersistence;
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
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

/*
 * LoanCalculationTest is a starting point for defining and exploring
 * expected behavior for different loan payment calculations.
 * 
 * This is a work in progress so there is still lots of extraneous junk
 * in this file, that will be cleaned up as we go forward.
 */
public class LoanCalculationTest extends MifosTestCase {

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






	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	/****************************************************************************/
	
	public static void modifyDisbursmentDate(LoanBO loan, Date disbursmentDate) {
		loan.setDisbursementDate(disbursmentDate);
	}

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

	public void testIssue1623()
	throws NumberFormatException, PropertyNotFoundException,
	SystemException, ApplicationException {
		
		int digitsRightOfDecimal = 10;
		
		String expectedTotalInterest = "189.86";
		
		short numberOfInstallments = 11;
		double interestRate = 36.0;
		String loanAmount = "2500";
		short gracePeriodDuration = 0;
		InterestType interestType = InterestType.FLAT;
		
		Date startDate = new Date(System.currentTimeMillis());

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
		((ClientBO) client).getPerformanceHistory().updateLoanCounter(
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
		((ClientBO) client).getPerformanceHistory().updateLoanCounter(
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
