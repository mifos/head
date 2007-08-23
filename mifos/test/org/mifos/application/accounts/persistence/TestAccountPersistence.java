package org.mifos.application.accounts.persistence;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.mifos.application.accounts.TestAccount;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
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

	public void testDumpChartOfAccounts() throws ConfigurationException {
		String expected_chart = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
		"<configuration>\n" + 
		"<ChartOfAccounts>\n" + 
		"<GLAssetsAccount code=\"10000\" name=\"ASSETS\">\n" + 
		"<GLAccount code=\"11000\" name=\"Cash and bank balances\">\n" + 
		"<GLAccount code=\"11100\" name=\"Petty Cash Accounts\">\n" + 
		"<GLAccount code=\"11101\" name=\"Cash 1\"/>\n" + 
		"<GLAccount code=\"11102\" name=\"Cash 2\"/>\n" + 
		"</GLAccount>\n" + 
		"<GLAccount code=\"11200\" name=\"Bank Balances\">\n" + 
		"<GLAccount code=\"11201\" name=\"Bank Account 1\"/>\n" + 
		"<GLAccount code=\"11202\" name=\"Bank Account 2\"/>\n" + 
		"</GLAccount>\n" + 
		"</GLAccount>\n" + 
		"<GLAccount code=\"13000\" name=\"Loan Portfolio\">\n" + 
		"<GLAccount code=\"13100\" name=\"Loans and Advances\">\n" + 
		"<GLAccount code=\"1501\" name=\"IGLoan\"/>\n" + 
		"<GLAccount code=\"1502\" name=\"ManagedICICI-IGLoan\"/>\n" + 
		"<GLAccount code=\"1503\" name=\"SPLoan\"/>\n" + 
		"<GLAccount code=\"1504\" name=\"ManagedICICI-SPLoan\"/>\n" + 
		"<GLAccount code=\"1505\" name=\"WFLoan\"/>\n" + 
		"<GLAccount code=\"1506\" name=\"Managed WFLoan\"/>\n" + 
		"<GLAccount code=\"1507\" name=\"Emergency Loans\"/>\n" + 
		"<GLAccount code=\"1508\" name=\"Special  Loans\"/>\n" + 
		"<GLAccount code=\"1509\" name=\"Micro Enterprises Loans\"/>\n" + 
		"<GLAccount code=\"13101\" name=\"Loans to clients\"/>\n" + 
		"</GLAccount>\n" + 
		"<GLAccount code=\"13200\" name=\"Loan Loss Provisions \">\n" + 
		"<GLAccount code=\"13201\" name=\"Write-offs\"/>\n" + 
		"</GLAccount>\n" + 
		"</GLAccount>\n" + 
		"</GLAssetsAccount>\n" + 
		"<GLLiabilitiesAccount code=\"20000\" name=\"LIABILITIES\">\n" + 
		"<GLAccount code=\"22000\" name=\"Interest Payable\">\n" + 
		"<GLAccount code=\"22100\" name=\"Interest payable on clients savings\">\n" + 
		"<GLAccount code=\"22101\" name=\"Interest on mandatory savings\"/>\n" + 
		"</GLAccount>\n" + 
		"</GLAccount>\n" + 
		"<GLAccount code=\"23000\" name=\"Clients Deposits\">\n" + 
		"<GLAccount code=\"23100\" name=\"Clients Deposits\">\n" + 
		"<GLAccount code=\"4601\" name=\"Emergency Fund\"/>\n" + 
		"<GLAccount code=\"4602\" name=\"Margin Money-1\"/>\n" + 
		"<GLAccount code=\"4603\" name=\"Margin Money-2\"/>\n" + 
		"<GLAccount code=\"4606\" name=\"Village Development Fund\"/>\n" + 
		"<GLAccount code=\"23101\" name=\"Savings accounts \"/>\n" + 
		"</GLAccount>\n" + 
		"</GLAccount>\n" + 
		"<GLAccount code=\"24000\" name=\"Mandatory Savings\">\n" + 
		"<GLAccount code=\"24100\" name=\"Mandatory Savings\">\n" + 
		"<GLAccount code=\"24101\" name=\"Mandatory Savings Accounts\"/>\n" + 
		"</GLAccount>\n" + 
		"</GLAccount>\n" + 
		"</GLLiabilitiesAccount>\n" + 
		"<GLIncomeAccount code=\"30000\" name=\"INCOME\">\n" + 
		"<GLAccount code=\"31000\" name=\"Direct Income\">\n" + 
		"<GLAccount code=\"31100\" name=\"Interest income from loans\">\n" + 
		"<GLAccount code=\"5001\" name=\"Interest\"/>\n" + 
		"<GLAccount code=\"31101\" name=\"Interest on loans\"/>\n" + 
		"<GLAccount code=\"31102\" name=\"Penalty\"/>\n" + 
		"</GLAccount>\n" + 
		"<GLAccount code=\"31300\" name=\"Income from micro credit &amp; lending activities\">\n" + 
		"<GLAccount code=\"5201\" name=\"Processing Fees\"/>\n" + 
		"<GLAccount code=\"5202\" name=\"Annual Subscription Fee\"/>\n" + 
		"<GLAccount code=\"5203\" name=\"Emergency Loan Documentation Fee\"/>\n" + 
		"<GLAccount code=\"5204\" name=\"Sale of Publication\"/>\n" + 
		"<GLAccount code=\"5205\" name=\"Fines &amp; Penalties\"/>\n" + 
		"<GLAccount code=\"6201\" name=\"Miscelleneous Income\"/>\n" + 
		"<GLAccount code=\"31301\" name=\"Fees\"/>\n" + 
		"</GLAccount>\n" + 
		"</GLAccount>\n" + 
		"<GLAccount code=\"31401\" name=\"Income from 999 Account\"/>\n" + 
		"</GLIncomeAccount>\n" + 
		"<GLExpenditureAccount code=\"40000\" name=\"EXPENDITURE\">\n" + 
		"<GLAccount code=\"41000\" name=\"Direct Expenditure\">\n" + 
		"<GLAccount code=\"41100\" name=\"Cost of Funds\">\n" + 
		"<GLAccount code=\"41101\" name=\"Interest on clients voluntary savings\"/>\n" + 
		"<GLAccount code=\"41102\" name=\"Interest on clients mandatory savings\"/>\n" + 
		"</GLAccount>\n" + 
		"</GLAccount>\n" + 
		"</GLExpenditureAccount>\n" + 
		"</ChartOfAccounts>\n" + 
		"</configuration>\n";
		String chart = accountPersistence.dumpChartOfAccounts();
		assertEquals(expected_chart, chart.replace("\r", ""));
	}
	
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
		return TestObjectFactory.createSavingsProduct(offeringName, 
				ApplicableTo.GROUPS, startDate, 
				PrdStatus.SAVINGS_ACTIVE,
				300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 
				1.2, 200.0, 200.0, 
				SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, 
				meetingIntCalc, meetingIntPost);
	}
	
}
