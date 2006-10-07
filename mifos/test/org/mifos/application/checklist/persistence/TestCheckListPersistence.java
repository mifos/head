package org.mifos.application.checklist.persistence;

import java.util.List;

import org.mifos.application.checklist.util.helpers.CheckListMasterView;
import org.mifos.application.checklist.util.helpers.CheckListStatesView;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCheckListPersistence extends MifosTestCase {

	private CheckListPersistence checkListPersistence = null;

	private UserContext userContext;

	@Override
	protected void setUp() throws Exception {
		userContext = TestObjectFactory.getUserContext();
		createCheckListObj();
	}

	@Override
	protected void tearDown() {
		userContext = null;
		checkListPersistence = null;
	}

	public void testGetCheckListMasterData() throws Exception {
		List<CheckListMasterView> masterCheckList = null;

		masterCheckList = checkListPersistence
				.getCheckListMasterData(userContext.getLocaleId());

		assertNotNull(masterCheckList);
		assertEquals(masterCheckList.size(), 5);
	}

	public void testGetCustomerStates() throws Exception {
		List<CheckListStatesView> customerStates = new CheckListPersistence()
				.retrieveAllCustomerStatusList(Short.valueOf("1"), userContext
						.getLocaleId());
		assertEquals(customerStates.size(), 6);
		customerStates = new CheckListPersistence()
				.retrieveAllCustomerStatusList(Short.valueOf("2"), userContext
						.getLocaleId());
		assertNotNull(customerStates);
		assertEquals(customerStates.size(), 6);
		customerStates = new CheckListPersistence()
				.retrieveAllCustomerStatusList(Short.valueOf("3"), userContext
						.getLocaleId());
		assertNotNull(customerStates);
		assertEquals(customerStates.size(), 2);
		
	}

	public void testGetAccountStates() throws Exception {
		List<CheckListStatesView> accountStates = new CheckListPersistence()
				.retrieveAllAccountStateList(Short.valueOf("1"), userContext
						.getLocaleId());
		assertNotNull(accountStates);
		assertEquals(accountStates.size(), 12);
		accountStates = new CheckListPersistence().retrieveAllAccountStateList(
				Short.valueOf("2"), userContext.getLocaleId());
		assertNotNull(accountStates);
		assertEquals(accountStates.size(), 6);
		
	}

	private void createCheckListObj() {
		checkListPersistence = new CheckListPersistence();
	}

}
