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

package org.mifos.application.checklist.business.service;

import java.util.List;

import junit.framework.Assert;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.checklist.business.CheckListBO;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.util.helpers.CheckListConstants;
import org.mifos.application.checklist.util.helpers.CheckListMasterView;
import org.mifos.application.checklist.util.helpers.CheckListStatesView;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CheckListBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    public CheckListBusinessServiceIntegrationTest() throws Exception {
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
        List<CheckListMasterView> checkListMasterDataView = new CheckListBusinessService()
                .getCheckListMasterData(TestObjectFactory.getContext());
        Assert.assertNotNull(checkListMasterDataView);
       Assert.assertEquals(checkListMasterDataView.size(), 5);
        for (CheckListMasterView view : checkListMasterDataView) {
            if (view.getMasterTypeId().equals(CustomerLevel.CENTER))
               Assert.assertEquals(true, view.getIsCustomer());
            if (view.getMasterTypeId().equals(CustomerLevel.GROUP))
               Assert.assertEquals(true, view.getIsCustomer());
            if (view.getMasterTypeId().equals(CustomerLevel.CLIENT))
               Assert.assertEquals(true, view.getIsCustomer());
            if (view.getMasterTypeId().equals(ProductType.LOAN))
               Assert.assertEquals(false, view.getIsCustomer());
            if (view.getMasterTypeId().equals(ProductType.SAVINGS))
               Assert.assertEquals(false, view.getIsCustomer());
        }
    }

    public void testGetCheckListMasterData_exception() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new CheckListBusinessService().getCheckListMasterData(TestObjectFactory.getContext());
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
    }

    public void testRetreiveAllAccountCheckLists() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        CheckListBO checkList1 = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        List<AccountCheckListBO> checkLists = new CheckListBusinessService().retreiveAllAccountCheckLists();
        Assert.assertNotNull(checkLists);
       Assert.assertEquals(1, checkLists.size());
        TestObjectFactory.cleanUp(checkList);
        TestObjectFactory.cleanUp(checkList1);
    }

    public void testRetreiveAllAccountCheckListsForInvalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new CheckListBusinessService().retreiveAllAccountCheckLists();
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
    }

    public void testRetreiveAllCustomerCheckLists() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        CheckListBO checkList1 = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        List<CustomerCheckListBO> checkLists = new CheckListBusinessService().retreiveAllCustomerCheckLists();
        Assert.assertNotNull(checkLists);
       Assert.assertEquals(1, checkLists.size());
        TestObjectFactory.cleanUp(checkList);
        TestObjectFactory.cleanUp(checkList1);
    }

    public void testRetreiveAllCustomerCheckListsForInvalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new CheckListBusinessService().retreiveAllCustomerCheckLists();
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
    }

    public void testIsValidCheckListState() throws Exception {
        CheckListBO checkList = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        new CheckListBusinessService().isValidCheckListState(((CustomerCheckListBO) checkList).getCustomerLevel()
                .getId(), ((CustomerCheckListBO) checkList).getCustomerStatus().getId(), false);
       Assert.assertTrue(true);
        TestObjectFactory.cleanUp(checkList);
    }

    public void testIsValidCheckListState_failure() throws Exception {
        CheckListBO checkList = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        try {
            new CheckListBusinessService().isValidCheckListState(((CustomerCheckListBO) checkList).getCustomerLevel()
                    .getId(), ((CustomerCheckListBO) checkList).getCustomerStatus().getId(), true);
            Assert.fail();
        } catch (ServiceException se) {
           Assert.assertTrue(true);
        }
        TestObjectFactory.cleanUp(checkList);
    }

    public void testIsValidCheckListState_invalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new CheckListBusinessService().isValidCheckListState(Short.valueOf("1"), Short.valueOf("1"), true);
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
    }

    public void testGetCheckList() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        StaticHibernateUtil.closeSession();
        checkList = new CheckListBusinessService().getCheckList(checkList.getChecklistId());
        Assert.assertNotNull(checkList);
       Assert.assertEquals("productchecklist", checkList.getChecklistName());
       Assert.assertEquals(CheckListConstants.STATUS_ACTIVE, checkList.getChecklistStatus());
       Assert.assertEquals(1, checkList.getChecklistDetails().size());
        TestObjectFactory.cleanUp(checkList);
    }

    public void testGetCheckListForInvalidConnection() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            checkList = new CheckListBusinessService().getCheckList(checkList.getChecklistId());
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
        TestObjectFactory.cleanUp(checkList);
    }

    public void testGetCustomerStates() throws Exception {
        List<CheckListStatesView> statesView = new CheckListBusinessService().getCustomerStates(Short.valueOf("3"),
                Short.valueOf("1"));
        Assert.assertNotNull(statesView);
       Assert.assertEquals(2, statesView.size());
        for (CheckListStatesView state : statesView) {
            if (state.getStateId().equals(CustomerStatus.CENTER_ACTIVE.getValue())) {
               Assert.assertEquals(state.getStateName(), "Active");
            }
        }
    }

    public void testGetCustomerStates_invalidConnection() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new CheckListBusinessService().getCustomerStates(Short.valueOf("1"), Short.valueOf("1"));
           Assert.assertTrue(false);
            Assert.fail();
        } catch (Exception e) {
           Assert.assertTrue(true);
        }
    }

    public void testGetAccountStates() throws Exception {
        List<CheckListStatesView> accountStates = new CheckListBusinessService().getAccountStates(Short.valueOf("2"),
                Short.valueOf("1"));
        Assert.assertNotNull(accountStates);
       Assert.assertEquals(4, accountStates.size());
        for (CheckListStatesView state : accountStates) {
            if (state.getStateId().equals("2")) {
               Assert.assertEquals(state.getStateName(), "Active");
            }
        }
    }

    public void testGetAccountStates_invalidConnection() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new CheckListBusinessService().getAccountStates(Short.valueOf("1"), Short.valueOf("1"));
           Assert.assertTrue(false);
            Assert.fail();
        } catch (Exception e) {
           Assert.assertTrue(true);
        }
    }

}
