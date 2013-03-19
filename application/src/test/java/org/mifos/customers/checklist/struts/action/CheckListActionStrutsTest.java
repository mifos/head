/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.customers.checklist.struts.action;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.persistence.CheckListPersistence;
import org.mifos.customers.checklist.util.helpers.CheckListConstants;
import org.mifos.customers.checklist.util.helpers.CheckListType;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.screen.CheckListStatesView;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;

@SuppressWarnings("unchecked")
public class CheckListActionStrutsTest extends MifosMockStrutsTestCase {

    private String flowKey;

    @Before
    public void setUp() throws Exception {
        UserContext userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId(), userContext.getId());
        request.getSession(false).setAttribute("ActivityContext", ac);
        flowKey = createFlow(request, ChkListAction.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testLoadAllChecklist() throws Exception {
        CheckListBO checkList1 = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        CheckListBO checkList2 = TestObjectFactory.createAccountChecklist(ProductType.SAVINGS.getValue(),
                AccountState.SAVINGS_ACTIVE, (short) 1);
        CheckListBO checkList3 = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        CheckListBO checkList4 = TestObjectFactory.createCustomerChecklist(CustomerLevel.GROUP.getValue(),
                CustomerStatus.GROUP_ACTIVE.getValue(), (short) 1);

        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "loadAllChecklist");
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.loadAllChecklist_success.toString());

       Assert.assertEquals(1, ((List) SessionUtils.getAttribute(CheckListConstants.LOAN_CHECKLIST, request)).size());
       Assert.assertEquals(1, ((List) SessionUtils.getAttribute(CheckListConstants.SAVINGS_CHECKLIST, request)).size());
       Assert.assertEquals(1, ((List) SessionUtils.getAttribute(CheckListConstants.CENTER_CHECKLIST, request)).size());
       Assert.assertEquals(1, ((List) SessionUtils.getAttribute(CheckListConstants.GROUP_CHECKLIST, request)).size());
       Assert.assertEquals(0, ((List) SessionUtils.getAttribute(CheckListConstants.CLIENT_CHECKLIST, request)).size());
    }

    @Test
    public void testGetForCustomerChecklist() throws Exception {

        CheckListBO checkList = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "get");
        addRequestParameter("checkListId", checkList.getChecklistId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.get_success.toString());

        Assert.assertNotNull(SessionUtils.getAttribute(Constants.BUSINESS_KEY, request));
       Assert.assertEquals(CheckListType.CUSTOMER_CHECKLIST.getValue(), SessionUtils.getAttribute(CheckListConstants.TYPE,
                request));
        Assert.assertNotNull(SessionUtils.getAttribute(CheckListConstants.CREATED_BY_NAME, request));

    }

    @Test
    public void testGetForAccountChecklist() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);

        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "get");
        addRequestParameter("checkListId", checkList.getChecklistId().toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.get_success.toString());

        Assert.assertNotNull(SessionUtils.getAttribute(Constants.BUSINESS_KEY, request));
       Assert.assertEquals(CheckListType.ACCOUNT_CHECKLIST.getValue(), SessionUtils.getAttribute(CheckListConstants.TYPE,
                request));
        Assert.assertNotNull(SessionUtils.getAttribute(CheckListConstants.CREATED_BY_NAME, request));

    }

    @Test
    public void testLoad() throws Exception {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "load");
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.load_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(CheckListConstants.CHECKLIST_MASTERDATA, request));
    }

    @Test
    public void testGetStates() throws Exception {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "getStates");
        addRequestParameter("masterTypeId", "1");
        addRequestParameter("isCustomer", "true");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.load_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(CheckListConstants.STATES, request));
       Assert.assertEquals(5, ((List<CheckListStatesView>) SessionUtils.getAttribute(CheckListConstants.STATES, request))
                .size());
    }
    
    @Test
    public void testGetStatesDuplicateForCustomer() throws Exception {
        CustomerCheckListBO checkList = TestObjectFactory.createCustomerChecklist(CustomerLevel.GROUP.getValue(),
                CustomerStatus.GROUP_ACTIVE.getValue(), (short) 1);
        StaticHibernateUtil.flushSession();
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "getStates");
        addRequestParameter("masterTypeId", "2");
        addRequestParameter("isCustomer", "true");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.load_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        List<CheckListStatesView> states = (List<CheckListStatesView>) SessionUtils.getAttribute(
                CheckListConstants.STATES, request);

        for (CheckListStatesView state : states) {
            Assert.assertNotSame(state.getStateId(), checkList.getCustomerStatus().getId());
        }
    }

    @Test
    public void testGetStatesDuplicateForAccount() throws Exception {
        AccountCheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_APPROVED, (short) 1);
        StaticHibernateUtil.flushSession();
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "getStates");
        addRequestParameter("masterTypeId", "1");
        addRequestParameter("isCustomer", "false");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.load_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        List<CheckListStatesView> states = (List<CheckListStatesView>) SessionUtils.getAttribute(
                CheckListConstants.STATES, request);

        for (CheckListStatesView state : states) {
            Assert.assertNotSame(state.getStateId(), checkList.getAccountStateEntity().getId());
        }
    }

    @Test
    public void testPreviewNoChecklistName() {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "preview");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyInputForward();
    }

    @Test
    public void testPreview() {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "preview");
        addRequestParameter("masterTypeId", "3");
        addRequestParameter("isCustomer", "true");
        addRequestParameter("stateId", "13");
        addRequestParameter("checklistName", "checkListname");
        addRequestParameter("detailsList[0]", "newdetails");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.preview_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    @Test
    public void testPrevious() throws Exception {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "previous");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.previous_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    @Test
    public void testCreate_customer() throws Exception {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "create");
        addRequestParameter("masterTypeId", "1");
        addRequestParameter("masterTypeName", "kendra");
        addRequestParameter("isCustomer", "true");
        addRequestParameter("type", "1");
        addRequestParameter("stateId", "13");
        addRequestParameter("stateName", "active");
        addRequestParameter("checklistName", "new checklistName");
        addRequestParameter("detailsList[0]", "newdetails");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.create_success.toString());

        List<CustomerCheckListBO> customerCheckLists = new CheckListPersistence().retreiveAllCustomerCheckLists();
        Assert.assertNotNull(customerCheckLists);
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
       Assert.assertEquals(1, customerCheckLists.size());
        for (CustomerCheckListBO checkList : customerCheckLists) {
           Assert.assertEquals("new checklistName", checkList.getChecklistName());
           Assert.assertEquals("1", checkList.getCustomerLevel().getId().toString());
           Assert.assertEquals("13", checkList.getCustomerStatus().getId().toString());
           Assert.assertEquals("1", checkList.getChecklistStatus().toString());
           Assert.assertEquals(1, checkList.getChecklistDetails().size());
        }
    }

    @Test
    public void testCreate_product() throws Exception {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "create");
        addRequestParameter("masterTypeId", "1");
        addRequestParameter("isCustomer", "false");
        addRequestParameter("stateId", "13");
        addRequestParameter("checklistName", "new checklistName");
        addRequestParameter("detailsList[0]", "newdetails");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.create_success.toString());

        List<AccountCheckListBO> accountCheckLists = new CheckListPersistence().retreiveAllAccountCheckLists();
        Assert.assertNotNull(accountCheckLists);
       Assert.assertEquals(1, accountCheckLists.size());
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        for (AccountCheckListBO checkList : accountCheckLists) {
           Assert.assertEquals("new checklistName", checkList.getChecklistName());
           Assert.assertEquals("1", checkList.getProductTypeEntity().getProductTypeID().toString());
           Assert.assertEquals("1", checkList.getChecklistStatus().toString());
           Assert.assertEquals(1, checkList.getChecklistDetails().size());
        }
    }

    @Test
    public void testCancelCreate() throws Exception {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "cancelCreate");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.cancelCreate_success.toString());
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    @Test
    public void testManage_customerCenter() throws Exception {
        CheckListBO checkList = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        StaticHibernateUtil.flushSession();
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "manage");
        addRequestParameter("checkListId", checkList.getChecklistId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        StaticHibernateUtil.flushAndClearSession();
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        StaticHibernateUtil.flushSession();
        checkList = (CheckListBO) TestObjectFactory.getObject(CustomerCheckListBO.class, checkList.getChecklistId());
        checkList = null;
    }

    @Test
    public void testManage_customerGroup() throws Exception {
        CheckListBO checkList = TestObjectFactory.createCustomerChecklist(CustomerLevel.GROUP.getValue(),
                CustomerStatus.GROUP_ACTIVE.getValue(), (short) 1);
        StaticHibernateUtil.flushSession();
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "manage");
        addRequestParameter("checkListId", checkList.getChecklistId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        StaticHibernateUtil.flushAndClearSession();
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        StaticHibernateUtil.flushSession();
        checkList = (CheckListBO) TestObjectFactory.getObject(CustomerCheckListBO.class, checkList.getChecklistId());
        checkList = null;
    }

    @Test
    public void testManage_customerClient() throws Exception {
        CheckListBO checkList = TestObjectFactory.createCustomerChecklist(CustomerLevel.CLIENT.getValue(),
                CustomerStatus.CLIENT_ACTIVE.getValue(), (short) 1);
        StaticHibernateUtil.flushSession();
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "manage");
        addRequestParameter("checkListId", checkList.getChecklistId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        StaticHibernateUtil.flushAndClearSession();
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        StaticHibernateUtil.flushSession();
        checkList = (CheckListBO) TestObjectFactory.getObject(CustomerCheckListBO.class, checkList.getChecklistId());
        checkList = null;
    }

    @Test
    public void testManage_accountLoan() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        StaticHibernateUtil.flushSession();
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "manage");
        addRequestParameter("checkListId", checkList.getChecklistId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        StaticHibernateUtil.flushAndClearSession();
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        StaticHibernateUtil.flushSession();
        checkList = (CheckListBO) TestObjectFactory.getObject(AccountCheckListBO.class, checkList.getChecklistId());
        checkList = null;
    }

    @Test
    public void testManage_accountSaving() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.SAVINGS.getValue(),
                AccountState.SAVINGS_ACTIVE, (short) 1);
        StaticHibernateUtil.flushSession();
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "manage");
        addRequestParameter("checkListId", checkList.getChecklistId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        StaticHibernateUtil.flushAndClearSession();
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        StaticHibernateUtil.flushSession();
        checkList = (CheckListBO) TestObjectFactory.getObject(AccountCheckListBO.class, checkList.getChecklistId());
        checkList = null;
    }

    @Test
    public void testGetEditStates() throws Exception {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "getEditStates");
        addRequestParameter("masterTypeId", "1");
        addRequestParameter("isCustomer", "false");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.manage_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        Assert.assertNotNull(SessionUtils.getAttribute(CheckListConstants.STATES, request));
       Assert.assertEquals(6, ((List<CheckListStatesView>) SessionUtils.getAttribute(CheckListConstants.STATES, request))
                .size());
    }

    @Test
    public void testManagePreviewValidate() {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "managePreview");
        addRequestParameter("masterTypeId", "3");
        addRequestParameter("isCustomer", "true");
        addRequestParameter("stateId", "13");
        addRequestParameter("detailsList[0]", "newdetails");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { CheckListConstants.MANDATORY, CheckListConstants.MANDATORY });
        verifyInputForward();
    }

    @Test
    public void testManagePreview() {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "managePreview");
        addRequestParameter("masterTypeId", "3");
        addRequestParameter("isCustomer", "true");
        addRequestParameter("stateId", "13");
        addRequestParameter("checklistName", "checkListname");
        addRequestParameter("checklistStatus", CheckListConstants.STATUS_ACTIVE.toString());
        addRequestParameter("detailsList[0]", "newdetails");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.managepreview_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    @Test
    public void testManagePrevious() {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "managePrevious");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.manageprevious_success.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    @Test
    public void testUpdate_customer() throws Exception {
        CheckListBO checkList = TestObjectFactory.createCustomerChecklist(CustomerLevel.CENTER.getValue(),
                CustomerStatus.CENTER_ACTIVE.getValue(), (short) 1);
        StaticHibernateUtil.flushSession();
        checkList = (CheckListBO) TestObjectFactory.getObject(CustomerCheckListBO.class, checkList.getChecklistId());

        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "update");
        addRequestParameter("checklistName", checkList.getChecklistName());
        addRequestParameter("isCustomer", "true");
        addRequestParameter("detailsList[0]", "newdetails");
        addRequestParameter("masterTypeId", ((CustomerCheckListBO) checkList).getCustomerLevel().getId().toString());
        addRequestParameter("stateId", ((CustomerCheckListBO) checkList).getCustomerStatus().getId().toString());
        addRequestParameter("checklistStatus", CheckListConstants.STATUS_INACTIVE.toString());

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, checkList, request);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.update_success.toString());
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        StaticHibernateUtil.flushSession();
        checkList = (CheckListBO) TestObjectFactory.getObject(CustomerCheckListBO.class, checkList.getChecklistId());
       Assert.assertEquals(1, checkList.getChecklistDetails().size());
        checkList = null;
    }

    @Test
    public void testUpdate_product() throws Exception {
        CheckListBO checkList = TestObjectFactory.createAccountChecklist(ProductType.LOAN.getValue(),
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, (short) 1);
        StaticHibernateUtil.flushSession();
        checkList = (CheckListBO) TestObjectFactory.getObject(AccountCheckListBO.class, checkList.getChecklistId());

        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "update");
        addRequestParameter("checklistName", checkList.getChecklistName());
        addRequestParameter("isCustomer", "false");
        addRequestParameter("detailsList[0]", "newdetails");
        addRequestParameter("masterTypeId", ((AccountCheckListBO) checkList).getProductTypeEntity().getProductTypeID()
                .toString());
        addRequestParameter("stateId", ((AccountCheckListBO) checkList).getAccountStateEntity().getId().toString());
        addRequestParameter("checklistStatus", CheckListConstants.STATUS_INACTIVE.toString());

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, checkList, request);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.update_success.toString());
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        StaticHibernateUtil.flushSession();
        checkList = (CheckListBO) TestObjectFactory.getObject(AccountCheckListBO.class, checkList.getChecklistId());
       Assert.assertEquals(1, checkList.getChecklistDetails().size());
        checkList = null;
    }

    @Test
    public void testCancelManage() {
        setRequestPathInfo("/chkListAction");
        addRequestParameter("method", "cancelManage");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.cancelEdit_success.toString());
        Assert.assertNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }
}
