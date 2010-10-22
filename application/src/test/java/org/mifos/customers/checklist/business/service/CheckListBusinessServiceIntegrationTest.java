/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.checklist.business.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.util.helpers.CheckListConstants;
import org.mifos.customers.checklist.util.helpers.CheckListMasterDto;
import org.mifos.customers.checklist.util.helpers.CheckListStatesView;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

import java.util.List;

public class CheckListBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testGetCheckListMasterData() throws Exception {
        List<CheckListMasterDto> checkListMasterDataView = new CheckListBusinessService()
                .getCheckListMasterData(TestObjectFactory.getContext());
        Assert.assertNotNull(checkListMasterDataView);
        Assert.assertEquals(checkListMasterDataView.size(), 5);
        for (CheckListMasterDto view : checkListMasterDataView) {
            if (view.getMasterTypeId().equals(CustomerLevel.CENTER)) {
                Assert.assertEquals(true, view.getIsCustomer());
            }
            if (view.getMasterTypeId().equals(CustomerLevel.GROUP)) {
                Assert.assertEquals(true, view.getIsCustomer());
            }
            if (view.getMasterTypeId().equals(CustomerLevel.CLIENT)) {
                Assert.assertEquals(true, view.getIsCustomer());
            }
            if (view.getMasterTypeId().equals(ProductType.LOAN)) {
                Assert.assertEquals(false, view.getIsCustomer());
            }
            if (view.getMasterTypeId().equals(ProductType.SAVINGS)) {
                Assert.assertEquals(false, view.getIsCustomer());
            }
        }
    }

    @Test
    public void testRetreiveAllAccountCheckLists() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        CheckListBO checkList1 = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        List<AccountCheckListBO> checkLists = new CheckListBusinessService().retreiveAllAccountCheckLists();
        Assert.assertNotNull(checkLists);
        Assert.assertEquals(1, checkLists.size());
        checkList = null;
        checkList1 = null;
    }

    @Test
    public void testRetreiveAllCustomerCheckLists() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        CheckListBO checkList1 = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        List<CustomerCheckListBO> checkLists = new CheckListBusinessService().retreiveAllCustomerCheckLists();
        Assert.assertNotNull(checkLists);
        Assert.assertEquals(1, checkLists.size());
        checkList = null;
        checkList1 = null;
    }

    @Test
    public void testIsValidCheckListState() throws Exception {
        CheckListBO checkList = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        new CheckListBusinessService().isValidCheckListState(((CustomerCheckListBO) checkList).getCustomerLevel()
                .getId(), ((CustomerCheckListBO) checkList).getCustomerStatus().getId(), false);
        Assert.assertTrue(true);
        checkList = null;
    }

    @Test
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
        checkList = null;
    }

    @Test
    public void testGetCheckList() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        StaticHibernateUtil.flushSession();
        checkList = new CheckListBusinessService().getCheckList(checkList.getChecklistId());
        Assert.assertNotNull(checkList);
        Assert.assertEquals("productchecklist", checkList.getChecklistName());
        Assert.assertEquals(CheckListConstants.STATUS_ACTIVE, checkList.getChecklistStatus());
        Assert.assertEquals(1, checkList.getChecklistDetails().size());
        checkList = null;
    }

    @Test
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

    @Test
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

}
