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

package org.mifos.application.accounts.savings.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.TestAccountPaymentEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.savings.business.TestSavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSavingsPersistence extends MifosTestCase {

	private UserContext userContext;

	private SavingsPersistence savingsPersistence;

	private AccountPersistence accountPersistence;
	
	private CustomerPersistence customerPersistence;

	private CustomerBO group;

	private CustomerBO center;

	private SavingsBO savings;

	private SavingsBO savings1;

	private SavingsBO savings2;

	private SavingsOfferingBO savingsOffering;

	private SavingsOfferingBO savingsOffering1;

	private SavingsOfferingBO savingsOffering2;

	private AccountCheckListBO accountCheckList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		savingsPersistence = new SavingsPersistence();
		accountPersistence = new AccountPersistence();
		customerPersistence = new CustomerPersistence();
		userContext = TestUtils.makeUser();

	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(savings);
		if (savings1 != null) {
			TestObjectFactory.cleanUp(savings1);
			savingsOffering1 = null;
		}
		if (savings2 != null) {
			TestObjectFactory.cleanUp(savings2);
			savingsOffering2 = null;
		}

		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		TestObjectFactory.cleanUp(accountCheckList);
		TestObjectFactory.removeObject(savingsOffering1);
		TestObjectFactory.removeObject(savingsOffering2);
		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testGetSavingsProducts() throws Exception {
		createInitialObjects();
		Date currentDate = new Date(System.currentTimeMillis());
		savingsOffering1 = TestObjectFactory.createSavingsProduct(
				"SavingPrd1", "sdcf", currentDate);
		savingsOffering2 = TestObjectFactory.createSavingsProduct(
				"SavingPrd2", "1asq", currentDate);
		List<PrdOfferingView> products = savingsPersistence.getSavingsProducts(
				null, group.getCustomerLevel(), new Short("2"));
		assertEquals(2, products.size());
		assertEquals("Offerng name for the first product do not match.",
				products.get(0).getPrdOfferingName(), "SavingPrd1");
		assertEquals("Offerng name for the second product do not match.",
				products.get(1).getPrdOfferingName(), "SavingPrd2");

	}

	public void testRetrieveCustomFieldsDefinition() throws Exception {
		List<CustomFieldDefinitionEntity> customFields = savingsPersistence
				.retrieveCustomFieldsDefinition(SavingsConstants.SAVINGS_CUSTOM_FIELD_ENTITY_TYPE);
		assertNotNull(customFields);
		assertEquals(TestConstants.SAVINGS_CUSTOMFIELDS_NUMBER, customFields
				.size());
	}

	public void testFindById() throws Exception {
		createInitialObjects();
		Date currentDate = new Date(System.currentTimeMillis());
		savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", "xdsa", currentDate);
		savings = createSavingsAccount("FFFF", savingsOffering);
		SavingsBO savings1 = savingsPersistence
				.findById(savings.getAccountId());
		assertEquals(savingsOffering.getRecommendedAmount()
				.getAmountDoubleValue(), savings1.getRecommendedAmount()
				.getAmountDoubleValue());
	}

	public void testGetAccountStatus() throws Exception {
		AccountStateEntity accountState = savingsPersistence
				.getAccountStatusObject(AccountStates.SAVINGS_ACC_CLOSED);
		assertNotNull(accountState);
		assertEquals(accountState.getId().shortValue(),
				AccountStates.SAVINGS_ACC_CLOSED);
	}

	public void testRetrieveAllAccountStateList() throws NumberFormatException,
			PersistenceException {
		List<AccountStateEntity> accountStateEntityList = accountPersistence
				.retrieveAllAccountStateList(Short.valueOf("2"));
		assertNotNull(accountStateEntityList);
		assertEquals(6, accountStateEntityList.size());
	}
	
	public void testRetrieveAllActiveAccountStateList() throws NumberFormatException,
			PersistenceException {
		List<AccountStateEntity> accountStateEntityList = accountPersistence
				.retrieveAllActiveAccountStateList(Short.valueOf("2"));
		assertNotNull(accountStateEntityList);
		assertEquals(6, accountStateEntityList.size());
	}

	public void testGetStatusChecklist() throws Exception {
		accountCheckList = TestObjectFactory
				.createAccountChecklist(AccountTypes.SAVINGS_ACCOUNT.getValue(),
						AccountState.SAVINGS_PARTIAL_APPLICATION, Short
								.valueOf("1"));
		List statusCheckList = accountPersistence.getStatusChecklist(Short
				.valueOf("13"), AccountTypes.SAVINGS_ACCOUNT.getValue());
		assertNotNull(statusCheckList);

		assertEquals(1, statusCheckList.size());
	}

	public void testFindBySystemId() throws Exception {
		createInitialObjects();
		Date currentDate = new Date(System.currentTimeMillis());
		savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", "v1ws", currentDate);
		savings = createSavingsAccount("kkk", savingsOffering);
		SavingsBO savings1 = savingsPersistence.findBySystemId(savings.getGlobalAccountNum());
		assertEquals(savings.getAccountId(), savings1.getAccountId());
		assertEquals(savingsOffering.getRecommendedAmount()
				.getAmountDoubleValue(), savings1.getRecommendedAmount()
				.getAmountDoubleValue());
	}

	public void testRetrieveLastTransaction() throws Exception {
		try {
			SavingsTestHelper helper = new SavingsTestHelper();
			createInitialObjects();
			PersonnelBO createdBy = new PersonnelPersistence()
					.getPersonnel(userContext.getId());
			savingsOffering = helper.createSavingsOffering("effwe", "231");
			savings = new SavingsBO(userContext, savingsOffering, group,
					AccountState.SAVINGS_ACTIVE, savingsOffering
							.getRecommendedAmount(), null, customerPersistence);

			AccountPaymentEntity payment = helper
					.createAccountPaymentToPersist(savings, new Money(
							Configuration.getInstance().getSystemConfig()
									.getCurrency(), "700.0"), new Money(
							Configuration.getInstance().getSystemConfig()
									.getCurrency(), "1700.0"), helper
							.getDate("15/01/2006"),
							AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
							createdBy, group);
			TestAccountPaymentEntity.addAccountPayment(payment,savings);
			savings.save();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();

			payment = helper.createAccountPaymentToPersist(savings,
					new Money(Configuration.getInstance().getSystemConfig()
							.getCurrency(), "1000.0"), new Money(Configuration
							.getInstance().getSystemConfig().getCurrency(),
							"2700.0"), helper.getDate("20/02/2006"),
					AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
					createdBy, group);
			TestAccountPaymentEntity.addAccountPayment(payment,savings);
			savings.update();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();

			savings = savingsPersistence.findById(savings.getAccountId());
			savings.setUserContext(userContext);
			payment = helper.createAccountPaymentToPersist(savings,
					new Money(Configuration.getInstance().getSystemConfig()
							.getCurrency(), "500.0"), new Money(Configuration
							.getInstance().getSystemConfig().getCurrency(),
							"2200.0"), helper.getDate("10/03/2006"),
					AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings,
					createdBy, group);
			TestAccountPaymentEntity.addAccountPayment(payment,savings);
			savings.update();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();

			savings = savingsPersistence.findById(savings.getAccountId());
			savings.setUserContext(userContext);
			payment = helper.createAccountPaymentToPersist(savings,
					new Money(Configuration.getInstance().getSystemConfig()
							.getCurrency(), "1200.0"), new Money(Configuration
							.getInstance().getSystemConfig().getCurrency(),
							"3400.0"), helper.getDate("15/03/2006"),
					AccountActionTypes.SAVINGS_DEPOSIT.getValue(), savings,
					createdBy, group);
			TestAccountPaymentEntity.addAccountPayment(payment,savings);
			savings.update();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();

			savings = savingsPersistence.findById(savings.getAccountId());
			savings.setUserContext(userContext);
			payment = helper.createAccountPaymentToPersist(savings,
					new Money(Configuration.getInstance().getSystemConfig()
							.getCurrency(), "2500.0"), new Money(Configuration
							.getInstance().getSystemConfig().getCurrency(),
							"900.0"), helper.getDate("25/03/2006"),
					AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), savings,
					createdBy, group);
			TestAccountPaymentEntity.addAccountPayment(payment,savings);
			savings.update();
			HibernateUtil.commitTransaction();
			HibernateUtil.closeSession();

			savings = savingsPersistence.findById(savings.getAccountId());
			savings.setUserContext(userContext);
			SavingsTrxnDetailEntity trxn = savingsPersistence
					.retrieveLastTransaction(savings.getAccountId(), helper
							.getDate("12/03/2006"));
			assertEquals(Double.valueOf("500"), trxn.getAmount()
					.getAmountDoubleValue());
			group = savings.getCustomer();
			center = group.getParentCustomer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testGetAccountsPendingForIntCalc() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		createInitialObjects();
		Date currentDate = new Date(System.currentTimeMillis());

		savingsOffering = TestObjectFactory.createSavingsProduct(
				"prd1", "sagf", currentDate);
		savingsOffering1 = TestObjectFactory.createSavingsProduct(
				"prd2", "q14f", currentDate);
		savingsOffering2 = TestObjectFactory.createSavingsProduct(
				"prd3", "z1as", currentDate);
		savings = helper.createSavingsAccount("000100000000021",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savings.setUserContext(TestObjectFactory.getContext());
		savings.changeStatus(AccountState.SAVINGS_INACTIVE.getValue(),
				null, "", customerPersistence);

		savings1 = helper.createSavingsAccount("000100000000022",
				savingsOffering1, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savings2 = helper.createSavingsAccount("000100000000023",
				savingsOffering2, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		TestSavingsBO.setNextIntCalcDate(savings,helper.getDate("30/06/2006"));
		TestSavingsBO.setNextIntCalcDate(savings1,helper.getDate("30/06/2006"));
		TestSavingsBO.setNextIntCalcDate(savings2,helper.getDate("31/07/2006"));
		
		
		savings.update();
		savings1.update();
		savings2.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		List<Integer> savingsList = savingsPersistence
				.retreiveAccountsPendingForIntCalc(helper.getDate("01/07/2006"));
		assertEquals(Integer.valueOf("1").intValue(), savingsList.size());
		assertEquals(savings.getAccountId(), savingsList.get(0));

		// retrieve objects to remove
		savings = savingsPersistence.findById(savings.getAccountId());
		savings1 = savingsPersistence.findById(savings1.getAccountId());
		savings2 = savingsPersistence.findById(savings2.getAccountId());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testGetAccountsPendingForIntPost() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		createInitialObjects();
		Date currentDate = new Date(System.currentTimeMillis());
		savingsOffering = TestObjectFactory.createSavingsProduct(
				"prd1", "lv4r", currentDate);
		savingsOffering1 = TestObjectFactory.createSavingsProduct(
				"prd2", "tj81", currentDate);
		savingsOffering2 = TestObjectFactory.createSavingsProduct(
				"prd3", "nvr4", currentDate);
		savings = helper.createSavingsAccount("000100000000021",
				savingsOffering, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savings.setUserContext(TestObjectFactory.getContext());
		savings.changeStatus(AccountState.SAVINGS_INACTIVE.getValue(),
				null, "", customerPersistence);
		savings1 = helper.createSavingsAccount("000100000000022",
				savingsOffering1, group,
				AccountStates.SAVINGS_ACC_PARTIALAPPLICATION, userContext);
		savings2 = helper.createSavingsAccount("000100000000023",
				savingsOffering2, group, AccountStates.SAVINGS_ACC_APPROVED,
				userContext);
		TestSavingsBO.setNextIntPostDate(savings,helper.getDate("31/07/2006"));
		TestSavingsBO.setNextIntPostDate(savings1,helper.getDate("31/07/2006"));
		TestSavingsBO.setNextIntPostDate(savings2,helper.getDate("31/08/2006"));
		savings.update();
		savings1.update();
		savings2.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		List<Integer> savingsList = savingsPersistence
				.retreiveAccountsPendingForIntPosting(helper
						.getDate("01/08/2006"));
		assertEquals(Integer.valueOf("1").intValue(), savingsList.size());

		assertEquals(savings.getAccountId(), savingsList.get(0));

		// retrieve objects to remove
		savings = savingsPersistence.findById(savings.getAccountId());
		savings1 = savingsPersistence.findById(savings1.getAccountId());
		savings2 = savingsPersistence.findById(savings2.getAccountId());
		group = savings.getCustomer();
		center = group.getParentCustomer();
	}

	public void testGetMissedDeposits() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		savingsOffering = helper.createSavingsOffering("SavingPrd1", "wsed",
				Short.valueOf("1"), Short.valueOf("1"));
		;
		savings = TestObjectFactory.createSavingsAccount("43245434", group,
				Short.valueOf("16"), new Date(System.currentTimeMillis()),
				savingsOffering);

		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		TestSavingsBO.setActionDate(accountActionDateEntity,offSetCurrentDate(7));

		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();

		savings = savingsPersistence.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		HibernateUtil.commitTransaction();
		Calendar currentDateCalendar = new GregorianCalendar();
		java.sql.Date currentDate = new java.sql.Date(currentDateCalendar
				.getTimeInMillis());

		assertEquals(savingsPersistence.getMissedDeposits(savings
				.getAccountId(), currentDate), 1);
	}

	public void testGetMissedDepositsPaidAfterDueDate() throws Exception {
		SavingsTestHelper helper = new SavingsTestHelper();
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
		savingsOffering = helper.createSavingsOffering("SavingPrd1", "cvfg",
				Short.valueOf("1"), Short.valueOf("1"));
		;
		savings = TestObjectFactory.createSavingsAccount("43245434", group,
				Short.valueOf("16"), new Date(System.currentTimeMillis()),
				savingsOffering);

		AccountActionDateEntity accountActionDateEntity = savings
				.getAccountActionDate((short) 1);
		TestSavingsBO.setActionDate(accountActionDateEntity,offSetCurrentDate(7));
		accountActionDateEntity.setPaymentStatus(PaymentStatus.PAID);
		Calendar currentDateCalendar = new GregorianCalendar();
		java.sql.Date currentDate = new java.sql.Date(currentDateCalendar
				.getTimeInMillis());

		TestSavingsBO.setPaymentDate(accountActionDateEntity,currentDate);
		savings.update();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		savings = savingsPersistence.findById(savings.getAccountId());
		savings.setUserContext(userContext);
		HibernateUtil.commitTransaction();
		assertEquals(savingsPersistence
				.getMissedDepositsPaidAfterDueDate(savings.getAccountId()), 1);
	}

	public void testGetAllSavingsAccount() throws Exception {
		createInitialObjects();
		Date currentDate = new Date(System.currentTimeMillis());
		savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", "v1ws", currentDate);
		savings = createSavingsAccount("kkk", savingsOffering);
		
		List<SavingsBO> savingsAccounts = savingsPersistence.getAllSavingsAccount();
		assertNotNull(savingsAccounts);
		assertEquals(1, savingsAccounts.size());
		
	}

	private void createInitialObjects() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center_Active_test", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);

	}

	private SavingsBO createSavingsAccount(String globalAccountNum,
			SavingsOfferingBO savingsOffering) throws NumberFormatException,
			Exception {
		UserContext userContext = new UserContext();
		userContext.setId(PersonnelConstants.SYSTEM_USER);
		userContext.setBranchGlobalNum("1001");
		return TestObjectFactory.createSavingsAccount(globalAccountNum, group,
				AccountState.SAVINGS_PENDING_APPROVAL, 
				new Date(), savingsOffering, userContext);
	}

	private java.sql.Date offSetCurrentDate(int noOfDays) {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
		return new java.sql.Date(currentDateCalendar.getTimeInMillis());
	}
}
