package org.mifos.application.accounts.loan.persistence;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.LoanPersistance;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.InterestTypesEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.GracePeriodTypeEntity;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.application.productdefinition.business.PrdApplicableMasterEntity;
import org.mifos.application.productdefinition.business.ProductCategoryBO;
import org.mifos.application.productdefinition.util.helpers.GraceTypeConstants;
import org.mifos.application.productdefinition.util.helpers.InterestTypeConstants;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestLoanPersistence extends MifosTestCase {

	LoanPersistance loanPersistence;

	CustomerBO center = null;

	CustomerBO group = null;

	MeetingBO meeting = null;

	AccountBO loanAccount = null;

	AccountBO loanAccountForDisbursement = null;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		loanPersistence = new LoanPersistance();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, startDate);
		group = TestObjectFactory.createGroup("Group", Short.valueOf("9"),
				"1.1.1", center, startDate);

		loanAccount = getLoanAccount(group, meeting);

	}

	@Override
	public void tearDown() throws Exception {

		TestObjectFactory.cleanUp(loanAccount);
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
				accountAction.setActionDate(new Date(twoDaysBack
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

		LoanBO testBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, list
				.get(0));
		assertEquals(Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING),
				testBO.getAccountState().getId());
		AccountActionDateEntity actionDate = testBO.getAccountActionDate(Short
				.valueOf("1"));
		assertEquals(PaymentStatus.UNPAID.getValue(), actionDate
				.getPaymentStatus());

		HibernateUtil.closeSession();
		list = loanPersistence.getLoanAccountsInArrears(Short.valueOf("3"));
		assertEquals(0, list.size());

		HibernateUtil.closeSession();
		loanAccount = (LoanBO) TestObjectFactory.getObject(LoanBO.class,
				loanAccount.getAccountId());
	}

	public void testFindBySystemId() throws Exception {
		LoanPersistance loanPersistance = new LoanPersistance();
		LoanBO loanBO = loanPersistance.findBySystemId(loanAccount
				.getGlobalAccountNum());
		assertEquals(loanBO.getGlobalAccountNum(), loanAccount
				.getGlobalAccountNum());
		assertEquals(loanBO.getAccountId(), loanAccount.getAccountId());
	}

	public void testGetFeeAmountAtDisbursement() throws Exception {
		loanAccountForDisbursement = getLoanAccount("cdfg", group, meeting,
				Short.valueOf("3"));
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
			accountAction.setActionDate(startDate);
		}
		TestObjectFactory.updateObject(loanAccount);
		loanAccount = new AccountPersistence().getAccount(loanAccount
				.getAccountId());
		List<Integer> list = loanPersistence
				.getLoanAccountsInArrearsInGoodStanding(latenessDays);
		assertNotNull(list);
		LoanBO testBO = (LoanBO) TestObjectFactory.getObject(LoanBO.class, list
				.get(0));
		assertEquals(Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING),
				testBO.getAccountState().getId());
		// Get the first action date i.e for the first Installment
		AccountActionDateEntity actionDates = testBO.getAccountActionDate(Short
				.valueOf("1"));
		// assert that the date comes after the action date
		assertTrue(date.after(actionDates.getActionDate()));
		// assert that the payment status in 0 - unpaid
		assertEquals(PaymentStatus.UNPAID.getValue(), actionDates
				.getPaymentStatus());
	}

	public void testGetAccount() throws Exception {
		LoanBO loanBO = loanPersistence.getAccount(loanAccount.getAccountId());
		assertEquals(loanBO.getAccountId(), loanAccount.getAccountId());
	}

	public void testGetLastPaymentAction() throws Exception {
		Date startDate = new Date(System.currentTimeMillis());
		loanAccountForDisbursement = getLoanAccount(
				AccountState.LOANACC_APPROVED.getValue(), startDate, 1);
		disburseLoan(startDate);
		assertEquals("Last payment action should be 'PAYMENT'",
				AccountActionTypes.DISBURSAL.getValue(), loanPersistence
						.getLastPaymentAction(loanAccountForDisbursement
								.getAccountId()));
	}
	
	public void testGetLoanOffering() throws Exception {
		LoanOfferingBO loanOffering = getCompleteLoanOfferingObject();
		LoanOfferingBO loanOfferingBO = loanPersistence.getLoanOffering(loanOffering.getPrdOfferingId(),
				TestObjectFactory.getUserContext().getLocaleId());
		assertEquals(loanOfferingBO.getPrdOfferingId(),loanOffering.getPrdOfferingId());
		TestObjectFactory.removeObject(loanOfferingBO);
	}

	private void disburseLoan(Date startDate) throws Exception {
		((LoanBO) loanAccountForDisbursement).disburseLoan("1234", startDate,
				Short.valueOf("1"), loanAccountForDisbursement.getPersonnel(),
				startDate, Short.valueOf("1"));
		HibernateUtil.commitTransaction();
	}

	private AccountBO getLoanAccount(Short accountSate, Date startDate,
			int disbursalType) {
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loanvcfg", "bhgf", Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"99999999999", group, accountSate, startDate, loanOffering,
				disbursalType);
	}

	private AccountBO getLoanAccount(String shortName, CustomerBO customer,
			MeetingBO meeting, Short accountSate) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loan123", shortName, Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccountWithDisbursement(
				"42423142341", customer, accountSate, startDate, loanOffering,
				1);

	}

	private AccountBO getLoanAccount(CustomerBO customer, MeetingBO meeting) {
		Date startDate = new Date(System.currentTimeMillis());
		LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(
				"Loancfgb", "dhsq", Short.valueOf("2"), startDate, Short
						.valueOf("1"), 300.0, 1.2, Short.valueOf("3"), Short
						.valueOf("1"), Short.valueOf("1"), Short.valueOf("1"),
				Short.valueOf("1"), Short.valueOf("1"), meeting);
		return TestObjectFactory.createLoanAccount("42423142341", customer,
				Short.valueOf("5"), startDate, loanOffering);

	}
	
	private LoanOfferingBO getCompleteLoanOfferingObject() throws Exception {
		PrdApplicableMasterEntity prdApplicableMaster = new PrdApplicableMasterEntity(
				PrdApplicableMaster.GROUPS);
		MeetingBO frequency = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		GLCodeEntity principalglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		GLCodeEntity intglCodeEntity = (GLCodeEntity) HibernateUtil
				.getSessionTL().get(GLCodeEntity.class, (short) 7);
		ProductCategoryBO productCategory = TestObjectFactory
				.getLoanPrdCategory();
		InterestTypesEntity interestTypes = new InterestTypesEntity(
				InterestTypeConstants.FLATINTERST);
		GracePeriodTypeEntity gracePeriodType = new GracePeriodTypeEntity(
				GraceTypeConstants.GRACEONALLREPAYMENTS);
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

}
