package org.mifos.application.checklist.business.service;

import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.util.helpers.CheckListMasterView;
import org.mifos.application.checklist.util.helpers.CheckListStatesView;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCheckListBusinessService extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		HibernateUtil.closeSession();
	}

	public void testGetCheckListMasterData() throws Exception {
		List<CheckListMasterView> checkListMasterDataView = new CheckListBusinessService()
				.getCheckListMasterData(TestObjectFactory.getContext());
		assertNotNull(checkListMasterDataView);
		assertEquals(checkListMasterDataView.size(), 5);
		for(CheckListMasterView view : checkListMasterDataView){
			if(view.getMasterTypeId().equals(CustomerLevel.CENTER))
				assertEquals(true,view.getIsCustomer());
			if(view.getMasterTypeId().equals(CustomerLevel.GROUP))
				assertEquals(true,view.getIsCustomer());
			if(view.getMasterTypeId().equals(CustomerLevel.CLIENT))
				assertEquals(true,view.getIsCustomer());
			if(view.getMasterTypeId().equals(ProductType.LOAN))
				assertEquals(false,view.getIsCustomer());
			if(view.getMasterTypeId().equals(ProductType.SAVINGS))
				assertEquals(false,view.getIsCustomer());
		}
	}

	public void testGetCheckListMasterData_exception() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new CheckListBusinessService()
					.getCheckListMasterData(TestObjectFactory.getContext());
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testRetreiveAllAccountCheckLists() throws Exception {
		CheckListBO checkList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING, (short) 1);
		CheckListBO checkList1 = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		List<AccountCheckListBO> checkLists = new CheckListBusinessService()
				.retreiveAllAccountCheckLists();
		assertNotNull(checkLists);
		assertEquals(1, checkLists.size());
		TestObjectFactory.cleanUp(checkList);
		TestObjectFactory.cleanUp(checkList1);
	}

	public void testRetreiveAllAccountCheckListsForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new CheckListBusinessService().retreiveAllAccountCheckLists();
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testRetreiveAllCustomerCheckLists() throws Exception {
		CheckListBO checkList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING, (short) 1);
		CheckListBO checkList1 = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		List<CustomerCheckListBO> checkLists = new CheckListBusinessService()
				.retreiveAllCustomerCheckLists();
		assertNotNull(checkLists);
		assertEquals(1, checkLists.size());
		TestObjectFactory.cleanUp(checkList);
		TestObjectFactory.cleanUp(checkList1);
	}

	public void testRetreiveAllCustomerCheckListsForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new CheckListBusinessService().retreiveAllCustomerCheckLists();
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testIsValidCheckListState() throws Exception {
		CheckListBO checkList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		new CheckListBusinessService().isValidCheckListState(
				((CustomerCheckListBO) checkList).getCustomerLevel().getId(),
				((CustomerCheckListBO) checkList).getCustomerStatus().getId(),
				false);
		assertTrue(true);
		TestObjectFactory.cleanUp(checkList);
	}
	
	public void testIsValidCheckListState_failure() throws Exception {
		CheckListBO checkList = TestObjectFactory.createCustomerChecklist(
				CustomerLevel.CENTER.getValue(), CustomerStatus.CENTER_ACTIVE
						.getValue(), (short) 1);
		try{
		new CheckListBusinessService().isValidCheckListState(
				((CustomerCheckListBO) checkList).getCustomerLevel().getId(),
				((CustomerCheckListBO) checkList).getCustomerStatus().getId(),
				true);
		fail();
		}catch(ServiceException se){
			assertTrue(true);
		}
		TestObjectFactory.cleanUp(checkList);
	}

	public void testIsValidCheckListState_invalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new CheckListBusinessService().isValidCheckListState(Short
					.valueOf("1"), Short.valueOf("1"), true);
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testGetCheckList() throws Exception {
		CheckListBO checkList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING, (short) 1);
		HibernateUtil.closeSession();
		checkList = new CheckListBusinessService().getCheckList(checkList
				.getChecklistId());
		assertNotNull(checkList);
		assertEquals("productchecklist", checkList.getChecklistName());
		assertEquals(CheckListConstants.STATUS_ACTIVE, checkList.getChecklistStatus());
		assertEquals(1, checkList.getChecklistDetails().size());
		TestObjectFactory.cleanUp(checkList);
	}

	public void testGetCheckListForInvalidConnection() throws Exception {
		CheckListBO checkList = TestObjectFactory.createAccountChecklist(
				ProductType.LOAN.getValue(),
				AccountState.LOANACC_ACTIVEINGOODSTANDING, (short) 1);
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			checkList = new CheckListBusinessService().getCheckList(checkList
					.getChecklistId());
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
		TestObjectFactory.cleanUp(checkList);
	}

	public void testGetCustomerStates() throws Exception {
		List<CheckListStatesView> statesView = new CheckListBusinessService()
				.getCustomerStates(Short.valueOf("3"), Short
						.valueOf("1"));
		assertNotNull(statesView);
		assertEquals(2, statesView.size());
		for (CheckListStatesView state : statesView){
			if(state.getStateId().equals(CustomerStatus.CENTER_ACTIVE.getValue())){
				assertEquals(state.getStateName(),"Active");
			}
		}
	}

	public void testGetCustomerStates_invalidConnection() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new CheckListBusinessService().getCustomerStates(Short
					.valueOf("1"), Short.valueOf("1"));
			assertTrue(false);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	public void testGetAccountStates() throws Exception {
		List<CheckListStatesView> accountStates = new CheckListBusinessService()
				.getAccountStates(Short.valueOf("2"), Short
						.valueOf("1"));
		assertNotNull(accountStates);
		assertEquals(6, accountStates.size());
		for (CheckListStatesView state : accountStates){
			if(state.getStateId().equals("2")){
				assertEquals(state.getStateName(),"Active");
			}
		}
	}

	public void testGetAccountStates_invalidConnection() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new CheckListBusinessService().getAccountStates(Short
					.valueOf("1"), Short.valueOf("1"));
			assertTrue(false);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
	}

}
