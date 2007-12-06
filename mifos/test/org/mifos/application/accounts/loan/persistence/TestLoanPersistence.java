package org.mifos.application.accounts.loan.persistence;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.TestLoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.GraceType;
import org.mifos.application.productdefinition.util.helpers.InterestType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

public class TestLoanPersistence extends MifosTestCase {

	LoanPersistence loanPersistence;

	CustomerBO center = null;

	CustomerBO group = null;

	MeetingBO meeting = null;

	AccountBO loanAccount = null;

	AccountBO loanAccountForDisbursement = null;

	LoanBO badAccount;

	LoanBO goodAccount;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		loanPersistence = new LoanPersistence();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		center = TestObjectFactory.createCenterForTestGetLoanAccounts("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		
		loanAccount = getLoanAccount(group, meeting);
	    badAccount = getBadAccount();
	    goodAccount = getGoodAccount();
	}

	@Override
	public void tearDown() throws Exception {

		TestObjectFactory.cleanUp(loanAccount);
		TestObjectFactory.cleanUp(badAccount);
		TestObjectFactory.cleanUp(goodAccount);
		TestObjectFactory.cleanUp(loanAccountForDisbursement);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}
	


	public void testGetLoanAccountsInArrears() throws Exception {
		Calendar currentDate = new GregorianCalendar();
		Calendar twoDaysBack = new GregorianCalendar(currentDate
				.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
				currentDate.get(Calendar.DAY_OF_MONTH) - 2, 0, 0, 0);

		for (AccountActionDateEntity accountAction : loanAccount
				.getAccountActionDates()) {
			if (accountAction.getInstallmentId().equals(Short.valueOf("1")))
				TestLoanBO.setActionDate(accountAction,new Date(twoDaysBack
						.getTimeInMillis()));
		}

		TestObjectFactory.updateObject(loanAccount);
		HibernateUtil.closeSession();
		loanAccount = new AccountPersistence().getAccount(loanAccount
				.getAccountId());

		List<Integer> list = loanPersistence.getLoanAccountsInArrears(Short
				.valueOf("1"));
		assertEquals(1, list.size());

		list = loanPersistence.getLoanAccountsInArrears(Short.valueOf("2"));
		assertEquals(1, list.size());
		HibernateUtil.closeSession();

		LoanBO testBO = TestObjectFactory.getObject(LoanBO.class, list
				.get(0));
		assertEquals(Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING),
				testBO.getAccountState().getId());
		AccountActionDateEntity actionDate = testBO.getAccountActionDate(Short
				.valueOf("1"));
		assertFalse(actionDate.isPaid());

		HibernateUtil.closeSession();
		list = loanPersistence.getLoanAccountsInArrears(Short.valueOf("3"));
		assertEquals(0, list.size());

		HibernateUtil.closeSession();
		loanAccount = TestObjectFactory.getObject(LoanBO.class,
				loanAccount.getAccountId());
	}

	public void testFindBySystemId() throws Exception {
		LoanPersistence loanPersistance = new LoanPersistence();
		LoanBO loanBO = loanPersistance.findBySystemId(loanAccount
				.getGlobalAccountNum());
		assertEquals(loanBO.getGlobalAccountNum(), loanAccount
				.getGlobalAccountNum());
		assertEquals(loanBO.getAccountId(), loanAccount.getAccountId());
	}
	
	public void testFindIndividualLoans() throws Exception {
		LoanPersistence loanPersistance = new LoanPersistence();
		List<LoanBO> listLoanBO = loanPersistance.findIndividualLoans(loanAccount
				.getAccountId().toString());
		
		assertEquals(0,listLoanBO.size());
	}
	public void testGetFeeAmountAtDisbursement() throws Exception {
		loanAccountForDisbursement = getLoanAccount("cdfg", group, meeting,
				AccountState.LOAN_APPROVED);
		assertEquals(30.0, loanPersistence
				.getFeeAmountAtDisbursement(loanAccountForDisbursement
						.getAccountId()));
	}

	public void testGetLoanAccountsInArrearsInGoodStanding()
			throws PersistenceException {
		Short latenessDays = 1;
		Calendar actionDate = new GregorianCalendar();
		int year = actionDate.get(Calendar.YEAR);
		int month = actionDate.get(Calendar.MONTH);
		int day = actionDate.get(Calendar.DAY_OF_MONTH);
		actionDate = new GregorianCalendar(year, month, day - latenessDays);

		Date date = new Date(actionDate.getTimeInMillis());
		Calendar checkDate = new GregorianCalendar(year, month, day - 15);
		Date startDate = new Date(checkDate.getTimeInMillis());
		for (AccountActionDateEntity accountAction : loanAccount
				.getAccountActionDates()) {
			TestLoanBO.setActionDate(accountAction,startDate);
		}
		TestObjectFactory.updateObject(loanAccount);
		loanAccount = new AccountPersistence().getAccount(loanAccount
				.getAccountId());
		List<Integer> list = loanPersistence
				.getLoanAccountsInArrearsInGoodStanding(latenessDays);
		assertNotNull(list);
		LoanBO testBO = TestObjectFactory.getObject(LoanBO.class, list
				.get(0));
		assertEquals(Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING),
				testBO.getAccountState().getId());

		// Get the first action date i.e for the first Installment
		Short firstInstallment = 1;
		AccountActionDateEntity actionDates = 
			testBO.getAccountActionDate(firstInstallment);

		assertTrue(date.after(actionDates.getActionDate()));
		assertFalse(actionDates.isPaid());
	}

	public void testGetAccount() throws Exception {
		LoanBO loanBO = loanPersistence.getAccount(loanAccount.getAccountId());
		assertEquals(loanBO.getAccountId(), loanAccount.getAccountId());
	}
	public void testGetLoanAccountsActiveInGoodBadStanding()
			throws PersistenceException {
		List<LoanBO> loanBO1 = loanPersistence.getLoanAccountsActiveInGoodBadStanding(loanAccount.getCustomer().getCustomerId());
		assertEquals(3, loanBO1.size());
	}

	public void testGetLastPaymentAction() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		loanAccountForDisbursement = getLoanAccount(
				AccountState.LOAN_APPROVED, startDate, 1);
		disburseLoan(startDate);
		assertEquals("Last payment action should be 'PAYMENT'",
				AccountActionTypes.DISBURSAL.getValue(), loanPersistence
						.getLastPaymentAction(loanAccountForDisbursement
								.getAccountId()));
	}
	
	public void testGetLoanOffering() throws Exception {
		LoanOfferingBO loanOffering = getCompleteLoanOfferingObject();
		LoanOfferingBO loanOfferingBO = loanPersistence.getLoanOffering(
				loanOffering.getPrdOfferingId(),
				TestObjectFactory.TEST_LOCALE);
		assertEquals(loanOfferingBO.getPrdOfferingId(),loanOffering.getPrdOfferingId());
		TestObjectFactory.removeObject(loanOfferingBO);
	}
	
	public void testGetLastLoanAmountForCustomer() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		loanAccountForDisbursement = getLoanAccount(
				AccountState.LOAN_APPROVED, startDate, 1);
		disburseLoan(startDate);
		assertEquals(((LoanBO) loanAccountForDisbursement).getLoanAmount(),
				loanPersistence.getLastLoanAmountForCustomer(group
						.getCustomerId()));
	}

	private void disburseLoan(Date startDate) throws Exception {
		((LoanBO) loanAccountForDisbursement).disburseLoan("1234", startDate,
				Short.valueOf("1"), loanAccountForDisbursement.getPersonnel(),
				startDate, Short.valueOf("1"));
		HibernateUtil.commitTransaction();
	}

	private AccountBO getLoanAccount(AccountState state, Date startDate,
			int disbursalType) {
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loanvcfg", "bhgf", ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short)3, 
				InterestType.FLAT, true, true,
				meeting);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, state, startDate, loanOffering,
				disbursalType);
	}

	private AccountBO getLoanAccount(String shortName, CustomerBO customer,
			MeetingBO meeting, AccountState state) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan123", shortName, ApplicableTo.GROUPS, startDate, 
				PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short)3, 
				InterestType.FLAT, true, true,
				meeting);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"42423142341", customer, state, startDate, loanOffering,
				1);

	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loancfgb", "dhsq", ApplicableTo.GROUPS, 
				startDate, PrdStatus.LOAN_ACTIVE, 
				300.0, 1.2, (short)3, 
				InterestType.FLAT, true, true,
				meeting);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate, loanOffering);

	}
	
	private LoanOfferingBO getCompleteLoanOfferingObject() throws Exception {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				ApplicableTo.GROUPS);
		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		GLCodeEntity principalglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = TestObjectFactory
				.getLoanPrdCategory();
		InterestTypesEntity interestTypes = new InterestTypesEntity(
				InterestType.FLAT);
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceType.GRACEONALLREPAYMENTS);
		List<FeeBO> fees = new ArrayList<FeeBO>();
		List<FundBO> funds = new ArrayList<FundBO>();
		FundBO fundBO = (FundBO) HibernateUtil.getSessionTL().get(FundBO.class,
				Short.valueOf("2"));
		funds.add(fundBO);
		LoanOfferingBO loanOfferingBO = new LoanOfferingBO(TestObjectFactory
				.getContext(), "Loan Offering", "LOAP", productCategory,
				prdApplicableMaster, new Date(System.currentTimeMillis()), null, null, gracePeriodType,
				(short) 2, interestTypes, new Money("1000"), new Money("3000"),
				new Money("2000.0"), 12.0, 2.0, 3.0, (short) 20, (short) 11,
				(short) 17, false, false, false, funds, fees, frequency,
				principalglCodeEntity, intglCodeEntity);
		loanOfferingBO.save();
		return loanOfferingBO;
	}
	
	private LoanBO getBadAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loanabcd", "abcd", ApplicableTo.CLIENTS, 
				startDate, PrdStatus.LOAN_ACTIVE, 
				300.0, 1.2, (short)3, 
				InterestType.FLAT, true, true,
				meeting);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		return TestObjectFactory.createLoanAccount("42423142323", group,
				AccountState.LOAN_ACTIVE_IN_BAD_STANDING, 
				startDate, loanOffering);
	}
	private LoanBO getGoodAccount() {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loanabf", "abf", ApplicableTo.CLIENTS, 
				startDate, PrdStatus.LOAN_ACTIVE, 
				300.0, 1.2, (short)3, 
				InterestType.FLAT, true, true,
				meeting);
		loanOffering.updateLoanOfferingSameForAllLoan(loanOffering);
		return TestObjectFactory.createLoanAccount("42423142342", group,
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, 
				startDate, loanOffering);
	}

	public void testGetLoanAccountsInActiveBadStandingShouldReturnLoanBOInActiveBadByBranchId() throws Exception {
		short branchId = 3;
		List<LoanBO> loanList = loanPersistence.getLoanAccountsInActiveBadStanding(branchId,(short)-1,(short)-1);
		assertEquals(1,loanList.size());
	}
	
	
	public void testGetLoanAccountsInActiveBadStandingShouldReturnLoanBOInActiveBadByBranchIdAndLoanOfficerId() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		List<LoanBO> loanList = loanPersistence.getLoanAccountsInActiveBadStanding(branchId,loanOfficerId,(short)-1);
		assertEquals(1,loanList.size());
		
	}
	public void testGetLoanAccountsInActiveBadStandingShouldReturnLoanBOInActiveBadByBranchIdLoanOfficerIdAndLoanProductId() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		short loanProductId = badAccount.getLoanOffering().getPrdOfferingId() ;
		List<LoanBO> loanList = loanPersistence.getLoanAccountsInActiveBadStanding(branchId,loanOfficerId,loanProductId);
		assertEquals(1,loanList.size());
		
	}
	
	public void testGetTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStandingByBranchId() throws Exception {
		short branchId = 3;
		BigDecimal money = loanPersistence.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(branchId,(short)-1,(short)-1);
		assertEquals(0,new BigDecimal(600).compareTo(money));
	}
	public void testGetTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStandingByBranchIdAndLoanOfficerId() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		BigDecimal money = loanPersistence.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(branchId,loanOfficerId,(short)-1);
		assertEquals(0,new BigDecimal(600).compareTo(money));
	}
	public void testGetTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStandingByBranchIdLoanOfficerIdAndLoanProductId() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		short loanProductId = goodAccount.getLoanOffering().getPrdOfferingId();
		BigDecimal money = loanPersistence.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(branchId,loanOfficerId,loanProductId);
		assertEquals(0,new BigDecimal(300).compareTo(money));
	}
	public void testGetActiveLoansBothInGoodAndBadStandingByLoanOfficer() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		List<LoanBO> loanList = loanPersistence.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId,loanOfficerId,(short)-1);
		assertEquals(3,loanList.size());
	}
	public void testGetActiveLoansBothInGoodAndBadStandingByLoanOfficerAndLoanProduct() throws Exception {
		short branchId = 3;
		short loanOfficerId = 3;
		short goodLoanProductId = goodAccount.getLoanOffering().getPrdOfferingId();
		List<LoanBO> goodLoanList = loanPersistence.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId,loanOfficerId,goodLoanProductId);
		assertEquals(1,goodLoanList.size());
		
		short badLoanProductId = badAccount.getLoanOffering().getPrdOfferingId();
		List<LoanBO> badLoanList = loanPersistence.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(branchId,loanOfficerId,badLoanProductId);
		assertEquals(1,badLoanList.size());
		
		
	}	
}
