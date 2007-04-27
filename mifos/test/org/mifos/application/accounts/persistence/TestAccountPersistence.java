package org.mifos.application.accounts.persistence;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.TestConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountPersistence extends TestAccount {

	private AccountPersistence accountPersistence = new AccountPersistence();

	public void testSuccessGetNextInstallmentList() {
		List<AccountActionDateEntity> installmentIdList = accountBO
				.getApplicableIdsForFutureInstallments();
		assertEquals(5, installmentIdList.size());
	}

	public void testSuccessLoadBusinessObject() throws Exception {
		AccountBO readAccount = 
			accountPersistence.getAccount(accountBO.getAccountId());
		assertEquals(AccountState.LOANACC_ACTIVEINGOODSTANDING,
			readAccount.getState());
	}

	public void testFailureLoadBusinessObject() {
		try {
			accountPersistence.getAccount(null);
			fail();
		} catch (PersistenceException expected) {
		}
	}

	public void testGetAccountAction() throws Exception {
		AccountActionEntity accountAction = 
			(AccountActionEntity) new MasterPersistence()
				.getPersistentObject(AccountActionEntity.class,
						AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue());
		assertNotNull(accountAction);
	}

	public void testOptionalAccountStates() throws Exception {
		assertEquals(1, accountPersistence
				.getAccountStates(Short.valueOf("0")).size());
	}

	public void testAccountStatesInUse() throws Exception {
		assertEquals(17, accountPersistence
				.getAccountStates(Short.valueOf("1")).size());
	}

	public void testGetAccountsWithYesterdaysInstallment()
			throws PersistenceException {
		assertEquals(0, accountPersistence
				.getAccountsWithYesterdaysInstallment().size());
	}

	public void testGetActiveCustomerAndSavingsAccounts() throws Exception {
		SavingsBO savingsBO = TestObjectFactory.createSavingsAccount(
				"12345678910", group, AccountState.SAVINGS_ACC_APPROVED, 
				new Date(),
				createSavingsOffering("qqqqq"), 
				TestUtils.makeUser());
		List<Integer> customerAccounts = accountPersistence
				.getActiveCustomerAndSavingsAccounts();
		assertEquals(3, customerAccounts.size());
		TestObjectFactory.cleanUp(savingsBO);
	}

	public void testSearchAccount() throws Exception {
		SavingsBO savingsBO = createSavingsAccount();

		QueryResult queryResult = null;

		queryResult = accountPersistence.search(
				savingsBO.getGlobalAccountNum(), (short)3);
		assertNotNull(queryResult);
		assertEquals(1, queryResult.getSize());
		assertEquals(1, queryResult.get(0,10).size());
		TestObjectFactory.cleanUp(savingsBO);
	}

	public void testSearchCustomerAccount() throws Exception {
		QueryResult queryResult = null;
		queryResult = accountPersistence.search(
				center.getCustomerAccount().getGlobalAccountNum(), (short)3);
		assertNull(queryResult);
	}	
	
	public void testRetrieveCustomFieldsDefinition() throws Exception {
		List<CustomFieldDefinitionEntity> customFields = accountPersistence
				.retrieveCustomFieldsDefinition(EntityType.LOAN.getValue());
		assertNotNull(customFields);
		assertEquals(TestConstants.LOAN_CUSTOMFIELDS_NUMBER,
			customFields.size());
	}
	
	private SavingsBO createSavingsAccount() throws Exception {
		return TestObjectFactory.createSavingsAccount("12345678910", group,
				AccountState.SAVINGS_ACC_APPROVED, new Date(), 
				createSavingsOffering("qqqqq"),
				TestUtils.makeUser());
	}

	private SavingsOfferingBO createSavingsOffering(String offeringName) {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		return TestObjectFactory.createSavingsOffering(offeringName, 
				ApplicableTo.GROUPS, startDate, 
				PrdStatus.SAVINGS_ACTIVE,
				300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 
				1.2, 200.0, 200.0, 
				SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, 
				meetingIntCalc, meetingIntPost);
	}
	
}
