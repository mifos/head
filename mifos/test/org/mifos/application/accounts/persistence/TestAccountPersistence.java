package org.mifos.application.accounts.persistence;

import java.util.Date;
import java.util.List;

import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestAccountPersistence extends TestAccount {

	private AccountPersistence accountPersistence = new AccountPersistence();

	public void testSuccessGetNextInstallmentList() {
		List<AccountActionDateEntity> installmentIdList = accountBO
				.getApplicableIdsForFutureInstallments();
		assertTrue(true);
	}

	public void testSuccessLoadBusinessObject() {
		try {
			AccountBO accountObject = accountPersistence.getAccount(accountBO
					.getAccountId());
			assertTrue(true);
		} catch (PersistenceException e) {
			assertTrue(false);
		}
	}

	public void testFailureLoadBusinessObject() {
		try {
			accountPersistence.getAccount(null);
			fail();
		} catch (PersistenceException e) {
			assertTrue(true);
		}
	}

	public void testGetAccountAction() throws Exception {
		AccountActionEntity accountaction = (AccountActionEntity) new MasterPersistence()
				.getPersistentObject(AccountActionEntity.class,
						AccountConstants.ACTION_SAVINGS_INTEREST_POSTING);
		assertNotNull(accountaction);
	}

	public void testOptionalAccountStates() throws Exception {
		assertEquals(Integer.valueOf(1).intValue(), accountPersistence
				.getAccountStates(Short.valueOf("0")).size());
	}

	public void testAccountStatesInUse() throws Exception {
		assertEquals(Integer.valueOf(17).intValue(), accountPersistence
				.getAccountStates(Short.valueOf("1")).size());
	}

	public void testGetAccountsWithYesterdaysInstallment()
			throws PersistenceException {
		assertEquals(0, accountPersistence
				.getAccountsWithYesterdaysInstallment().size());
	}

	public void testGetActiveCustomerAndSavingsAccounts() throws Exception {
		SavingsBO savingsBO = TestObjectFactory.createSavingsAccount(
				"12345678910", group, new Short("16"), new Date(),
				createSavingsOffering("qqqqq"), TestObjectFactory
						.getUserContext());
		List<Integer> customerAccounts = accountPersistence
				.getActiveCustomerAndSavingsAccounts();
		assertNotNull(customerAccounts);
		assertEquals(3, customerAccounts.size());
		TestObjectFactory.cleanUp(savingsBO);
	}

	public void testSearchAccount() throws Exception {
		SavingsBO savingsBO = getSavingsAccount();

		QueryResult queryResult = null;

		queryResult = accountPersistence.search(
				savingsBO.getGlobalAccountNum(), Short.valueOf("3"));
		assertNotNull(queryResult);
		assertEquals(1, queryResult.getSize());
		assertEquals(1, queryResult.get(0,10).size());
		TestObjectFactory.cleanUp(savingsBO);

	}

	public void testSearchCustomerAccount() throws Exception {
		QueryResult queryResult = null;
		queryResult = accountPersistence.search(
				center.getCustomerAccount().getGlobalAccountNum(), Short.valueOf("3"));
		assertNull(queryResult);

	}	
	
	
	private SavingsBO getSavingsAccount() throws Exception {
		return TestObjectFactory.createSavingsAccount("12345678910", group,
				new Short("16"), new Date(), createSavingsOffering("qqqqq"),
				TestObjectFactory.getUserContext());

	}

	private SavingsOfferingBO createSavingsOffering(String offeringName) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createSavingsOffering(offeringName, Short
				.valueOf("2"), new Date(System.currentTimeMillis()), Short
				.valueOf("2"), 300.0, Short.valueOf("1"), 1.2, 200.0, 200.0,
				Short.valueOf("2"), Short.valueOf("1"), meetingIntCalc,
				meetingIntPost);
	}
}
