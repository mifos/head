/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.accounts.persistence;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Date;
import java.util.List;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.mifos.application.accounts.AccountIntegrationTest;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.COAHierarchyEntity;
import org.mifos.application.accounts.financial.business.GLCategoryType;
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
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.TestConstants;
import org.mifos.framework.util.helpers.TestGeneralLedgerCode;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AccountPersistenceIntegrationTest extends AccountIntegrationTest {

	public AccountPersistenceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final String ASSETS_GL_ACCOUNT_CODE = "10000";
	private static final String DIRECT_EXPENDITURE_GL_ACCOUNT_CODE = "41000";
	private AccountPersistence accountPersistence = new AccountPersistence();

	public void testAddDuplicateGlAccounts() {
		String name = "New Account Name";
		String name2 = "New Account Name 2";
		String glCode = "999999";
		String parentGlCode = ASSETS_GL_ACCOUNT_CODE;

		try {
			COABO coa = accountPersistence.addGeneralLedgerAccount(name,
					glCode, parentGlCode, null);
			COABO coa2 = accountPersistence.addGeneralLedgerAccount(name2,
					glCode, parentGlCode, null);
			fail();
		}
		catch (Exception e) {
			assertTrue(e.getMessage().contains(
					"An account already exists with glcode"));
		}
		finally {
			StaticHibernateUtil.rollbackTransaction();
			StaticHibernateUtil.closeSession();
		}

	}

	public void testAddGlAccount() {
		String name = "New Account Name";
		String glCode = "999999";
		String parentGlCode = ASSETS_GL_ACCOUNT_CODE;

		try {
			COABO coa = accountPersistence.addGeneralLedgerAccount(name,
					glCode, parentGlCode, null);
			assertEquals(coa.getAccountId(), accountPersistence
					.getAccountIdFromGlCode(glCode));
		}
		finally {
			StaticHibernateUtil.rollbackTransaction();
			StaticHibernateUtil.closeSession();
		}

	}

	/**
	 * The Chart of Accounts hierarchy is created when TestCaseInitializer is
	 * instantiated in parent class static initializer. Verify it worked as
	 * planned.
	 */
	public void testAddCoaHierarchy() {
		short id = TestGeneralLedgerCode.COST_OF_FUNDS;
		COAHierarchyEntity h = (COAHierarchyEntity) StaticHibernateUtil
				.getSessionTL().load(COAHierarchyEntity.class, id);
		assertEquals(DIRECT_EXPENDITURE_GL_ACCOUNT_CODE, h.getParentAccount()
				.getCoa().getAssociatedGlcode().getGlcode());
	}

	/**
	 * The top-level "ASSETS" general ledger account should always be the first
	 * one inserted. This will hopefully be reliable enough for testing
	 * purposes.
	 */
	public void testGetAccountIdForGLCode() {
		assertEquals(new Short((short) 1), TestGeneralLedgerCode.ASSETS);
	}

	public void testTopLevelAccountPersisted() throws Exception {
		COABO incomeCategory = accountPersistence
				.getCategory(GLCategoryType.INCOME);
		assertEquals(GLCategoryType.INCOME, incomeCategory.getCategoryType());
	}

	public void testDumpChartOfAccounts() throws Exception {
		String expected_chart = "<configuration>"
				+ "  <ChartOfAccounts>"
				+ "    <GLAssetsAccount code=\"10000\" name=\"ASSETS\">"
				+ "      <GLAccount code=\"11000\" name=\"Cash and bank balances\">"
				+ "        <GLAccount code=\"11100\" name=\"Petty Cash Accounts\">"
				+ "          <GLAccount code=\"11101\" name=\"Cash 1\"/>"
				+ "          <GLAccount code=\"11102\" name=\"Cash 2\"/>"
				+ "        </GLAccount>"
				+ "        <GLAccount code=\"11200\" name=\"Bank Balances\">"
				+ "          <GLAccount code=\"11201\" name=\"Bank Account 1\"/>"
				+ "          <GLAccount code=\"11202\" name=\"Bank Account 2\"/>"
				+ "        </GLAccount>"
				+ "      </GLAccount>"
				+ "      <GLAccount code=\"13000\" name=\"Loan Portfolio\">"
				+ "        <GLAccount code=\"13100\" name=\"Loans and Advances\">"
				+ "          <GLAccount code=\"13101\" name=\"Loans to clients\"/>"
				+ "          <GLAccount code=\"1501\" name=\"IGLoan\"/>"
				+ "          <GLAccount code=\"1502\" name=\"ManagedICICI-IGLoan\"/>"
				+ "          <GLAccount code=\"1503\" name=\"SPLoan\"/>"
				+ "          <GLAccount code=\"1504\" name=\"ManagedICICI-SPLoan\"/>"
				+ "          <GLAccount code=\"1505\" name=\"WFLoan\"/>"
				+ "          <GLAccount code=\"1506\" name=\"Managed WFLoan\"/>"
				+ "          <GLAccount code=\"1507\" name=\"Emergency Loans\"/>"
				+ "          <GLAccount code=\"1508\" name=\"Special Loans\"/>"
				+ "          <GLAccount code=\"1509\" name=\"Micro Enterprises Loans\"/>"
				+ "        </GLAccount>"
				+ "        <GLAccount code=\"13200\" name=\"Loan Loss Provisions\">"
				+ "          <GLAccount code=\"13201\" name=\"Write-offs\"/>"
				+ "        </GLAccount>"
				+ "      </GLAccount>"
				+ "    </GLAssetsAccount>"
				+ "    <GLLiabilitiesAccount code=\"20000\" name=\"LIABILITIES\">"
				+ "      <GLAccount code=\"22000\" name=\"Interest Payable\">"
				+ "        <GLAccount code=\"22100\" name=\"Interest payable on clients savings\">"
				+ "          <GLAccount code=\"22101\" name=\"Interest on mandatory savings\"/>"
				+ "        </GLAccount>"
				+ "      </GLAccount>"
				+ "      <GLAccount code=\"23000\" name=\"Clients Deposits\">"
				+ "        <GLAccount code=\"23100\" name=\"Clients Deposits\">"
				+ "          <GLAccount code=\"23101\" name=\"Savings accounts\"/>"
				+ "          <GLAccount code=\"4601\" name=\"Emergency Fund\"/>"
				+ "          <GLAccount code=\"4602\" name=\"Margin Money-1\"/>"
				+ "          <GLAccount code=\"4603\" name=\"Margin Money-2\"/>"
				+ "          <GLAccount code=\"4606\" name=\"Village Development Fund\"/>"
				+ "        </GLAccount>"
				+ "      </GLAccount>"
				+ "      <GLAccount code=\"24000\" name=\"Mandatory Savings\">"
				+ "        <GLAccount code=\"24100\" name=\"Mandatory Savings\">"
				+ "          <GLAccount code=\"24101\" name=\"Mandatory Savings Accounts\"/>"
				+ "        </GLAccount>"
				+ "      </GLAccount>"
				+ "    </GLLiabilitiesAccount>"
				+ "    <GLIncomeAccount code=\"30000\" name=\"INCOME\">"
				+ "      <GLAccount code=\"31000\" name=\"Direct Income\">"
				+ "        <GLAccount code=\"31100\" name=\"Interest income from loans\">"
				+ "          <GLAccount code=\"31101\" name=\"Interest on loans\"/>"
				+ "          <GLAccount code=\"31102\" name=\"Penalty\"/>"
				+ "          <GLAccount code=\"5001\" name=\"Interest\"/>"
				+ "        </GLAccount>"
				+ "        <GLAccount code=\"31300\" name=\"Income from micro credit &amp; lending activities\">"
				+ "          <GLAccount code=\"31301\" name=\"Fees\"/>"
				+ "          <GLAccount code=\"5201\" name=\"Processing Fees\"/>"
				+ "          <GLAccount code=\"5202\" name=\"Annual Subscription Fee\"/>"
				+ "          <GLAccount code=\"5203\" name=\"Emergency Loan Documentation Fee\"/>"
				+ "          <GLAccount code=\"5204\" name=\"Sale of Publication\"/>"
				+ "          <GLAccount code=\"5205\" name=\"Fines &amp; Penalties\"/>"
				+ "          <GLAccount code=\"6201\" name=\"Miscelleneous Income\"/>"
				+ "        </GLAccount>"
				+ "      </GLAccount>"
				+ "      <GLAccount code=\"31401\" name=\"Income from 999 Account\"/>"
				+ "    </GLIncomeAccount>"
				+ "    <GLExpenditureAccount code=\"40000\" name=\"EXPENDITURE\">"
				+ "      <GLAccount code=\"41000\" name=\"Direct Expenditure\">"
				+ "        <GLAccount code=\"41100\" name=\"Cost of Funds\">"
				+ "          <GLAccount code=\"41101\" name=\"Interest on clients voluntary savings\"/>"
				+ "          <GLAccount code=\"41102\" name=\"Interest on clients mandatory savings\"/>"
				+ "        </GLAccount>"
				+ "      </GLAccount>"
				+ "    </GLExpenditureAccount>"
				+ "  </ChartOfAccounts>"
				+ "</configuration>";
		String chart = accountPersistence.dumpChartOfAccounts();

		// save old values so they can be restored when we clean up before
		// leaving this test method
		boolean ignoreWhitespace = XMLUnit.getIgnoreWhitespace();
		XMLUnit.setIgnoreWhitespace(true);

		XMLAssert.assertXMLEqual(expected_chart, chart);

		XMLUnit.setIgnoreWhitespace(ignoreWhitespace);
	}

	public void testSuccessGetNextInstallmentList() {
		List<AccountActionDateEntity> installmentIdList = accountBO
				.getApplicableIdsForFutureInstallments();
		assertEquals(5, installmentIdList.size());
	}

	public void testSuccessLoadBusinessObject() throws Exception {
		AccountBO readAccount = accountPersistence.getAccount(accountBO
				.getAccountId());
		assertEquals(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, readAccount
				.getState());
	}

	public void testFailureLoadBusinessObject() {
		try {
			accountPersistence.getAccount(null);
			fail();
		}
		catch (PersistenceException expected) {
		}
	}

	public void testGetAccountAction() throws Exception {
		AccountActionEntity accountAction = (AccountActionEntity) new MasterPersistence()
				.getPersistentObject(AccountActionEntity.class,
						AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue());
		assertNotNull(accountAction);
	}

	public void testOptionalAccountStates() throws Exception {
		assertEquals(1, accountPersistence.getAccountStates(Short.valueOf("0"))
				.size());
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
				"12345678910", group, AccountState.SAVINGS_ACTIVE, new Date(),
				createSavingsOffering("qqqqq"), TestUtils.makeUser());
		List<Integer> customerAccounts = accountPersistence
				.getActiveCustomerAndSavingsAccounts();
		assertEquals(3, customerAccounts.size());
		TestObjectFactory.cleanUp(savingsBO);
	}

	public void testSearchAccount() throws Exception {
		SavingsBO savingsBO = createSavingsAccount();

		QueryResult queryResult = null;

		queryResult = accountPersistence.search(
				savingsBO.getGlobalAccountNum(), (short) 3);
		assertNotNull(queryResult);
		assertEquals(1, queryResult.getSize());
		assertEquals(1, queryResult.get(0, 10).size());
		TestObjectFactory.cleanUp(savingsBO);
	}

	public void testSearchCustomerAccount() throws Exception {
		QueryResult queryResult = null;
		queryResult = accountPersistence.search(center.getCustomerAccount()
				.getGlobalAccountNum(), (short) 3);
		assertNull(queryResult);
	}

	public void testRetrieveCustomFieldsDefinition() throws Exception {
		List<CustomFieldDefinitionEntity> customFields = accountPersistence
				.retrieveCustomFieldsDefinition(EntityType.LOAN.getValue());
		assertNotNull(customFields);
		assertEquals(TestConstants.LOAN_CUSTOMFIELDS_NUMBER, customFields
				.size());
	}

	private SavingsBO createSavingsAccount() throws Exception {
		return TestObjectFactory.createSavingsAccount("12345678910", group,
				AccountState.SAVINGS_ACTIVE, new Date(),
				createSavingsOffering("qqqqq"), TestUtils.makeUser());
	}

	private SavingsOfferingBO createSavingsOffering(String offeringName) {
		Date startDate = new Date(System.currentTimeMillis());

		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
						EVERY_WEEK, CUSTOMER_MEETING));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
						EVERY_WEEK, CUSTOMER_MEETING));
		return TestObjectFactory.createSavingsProduct(offeringName,
				ApplicableTo.GROUPS, startDate, PrdStatus.SAVINGS_ACTIVE,
				300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 1.2, 200.0, 200.0,
				SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE,
				meetingIntCalc, meetingIntPost);
	}

}
