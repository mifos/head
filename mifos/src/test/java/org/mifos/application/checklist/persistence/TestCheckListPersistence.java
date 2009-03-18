package org.mifos.application.checklist.persistence;

import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.util.helpers.CheckListMasterView;
import org.mifos.application.checklist.util.helpers.CheckListStatesView;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCheckListPersistence extends MifosIntegrationTest {

	public TestCheckListPersistence() throws SystemException, ApplicationException {
        super();
    }

    @Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetCheckListMasterData() throws Exception {
		List<CheckListMasterView> masterCheckList = null;

		masterCheckList = new CheckListPersistence()
				.getCheckListMasterData((short) 1);

		assertNotNull(masterCheckList);
		assertEquals(masterCheckList.size(), 5);
	}

	public void testGetCustomerStates() throws Exception {
		List<CheckListStatesView> customerStates = new CheckListPersistence()
				.retrieveAllCustomerStatusList(Short.valueOf("1"), (short) 1);
		assertEquals(customerStates.size(), 5);
		customerStates = new CheckListPersistence()
				.retrieveAllCustomerStatusList(Short.valueOf("2"), (short) 1);
		assertNotNull(customerStates);
		assertEquals(customerStates.size(), 5);
		customerStates = new CheckListPersistence()
				.retrieveAllCustomerStatusList(Short.valueOf("3"), (short) 1);
		assertNotNull(customerStates);
		assertEquals(customerStates.size(), 2);

	}

	public void testGetAccountStates() throws Exception {
		List<CheckListStatesView> accountStates = new CheckListPersistence()
				.retrieveAllAccountStateList(Short.valueOf("1"), (short) 1);
		assertNotNull(accountStates);
		assertEquals(6,accountStates.size());
		accountStates = new CheckListPersistence().retrieveAllAccountStateList(
				Short.valueOf("2"), (short) 1);
		assertNotNull(accountStates);
		assertEquals(accountStates.size(), 4);

	}

	public void testRetreiveAllAccountCheckLists() throws Exception {
		CheckListBO checkList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
		CheckListBO checkList1 = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		List<AccountCheckListBO> checkLists = new CheckListPersistence()
				.retreiveAllAccountCheckLists();
		assertNotNull(checkLists);
		assertEquals(1, checkLists.size());
		TestObjectFactory.cleanUp(checkList);
		TestObjectFactory.cleanUp(checkList1);
	}

	public void testRetreiveAllCustomerCheckLists() throws Exception {
		CheckListBO checkList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		CheckListBO checkList1 = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
		List<CustomerCheckListBO> checkLists = new CheckListPersistence()
				.retreiveAllCustomerCheckLists();
		assertNotNull(checkLists);
		assertEquals(1, checkLists.size());
		TestObjectFactory.cleanUp(checkList);
		TestObjectFactory.cleanUp(checkList1);
	}

	public void testCheckListMasterView() {
		CheckListMasterView checkListMasterView = new CheckListMasterView(Short
				.valueOf("1"), "Loan");
		checkListMasterView.setIsCustomer(true);
		assertEquals(Short.valueOf("1"), checkListMasterView.getMasterTypeId());
		assertEquals("Loan", checkListMasterView.getMasterTypeName());
		assertEquals(true, checkListMasterView.getIsCustomer());
	}

	public void testCheckListStatesView() {
		CheckListStatesView checkListStatesView = new CheckListStatesView(Short
				.valueOf("13"), "Active", Short.valueOf("1"));
		assertEquals(Short.valueOf("13"), checkListStatesView.getStateId());
		assertEquals("Active", checkListStatesView.getStateName());
		assertEquals(Short.valueOf("1"), checkListStatesView.getId());
	}
}
