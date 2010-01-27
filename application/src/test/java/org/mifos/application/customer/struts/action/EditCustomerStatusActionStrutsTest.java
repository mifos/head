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

package org.mifos.application.customer.struts.action;

import java.sql.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerBOTestUtils;
import org.mifos.application.customer.business.CustomerFlagDetailEntity;
import org.mifos.application.customer.business.CustomerPositionEntity;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class EditCustomerStatusActionStrutsTest extends MifosMockStrutsTestCase {

    public EditCustomerStatusActionStrutsTest() throws Exception {
        super();
    }

    private CustomerBO client;

    private CustomerBO group;

    private CustomerBO center;

    private LoanBO loanBO;

    private String flowKey;

    private OfficeBO office;

    @Override 
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }
        
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());

        flowKey = createFlow(request, EditCustomerStatusAction.class);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @SuppressWarnings("unchecked")
    public void testLoad() throws PageExpiredException {
        createInitialObjects();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", center.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 1, ((List<CustomerStatusEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());
        cleanInitialObjects();

    }

    public void testFailurePreviewWithAllValuesNull() throws Exception {
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        actionPerform();
       Assert.assertEquals(2, getErrorSize());
       Assert.assertEquals("Status id", 1, getErrorSize(CustomerConstants.MANDATORY_SELECT));
       Assert.assertEquals("Notes", 1, getErrorSize(CustomerConstants.MANDATORY_TEXTBOX));
        verifyInputForward();
    }

    public void testFailurePreviewWithFlagValueNull() throws Exception {
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("newStatusId", "11");
        actionPerform();
       Assert.assertEquals(2, getErrorSize());
       Assert.assertEquals("flag id", 1, getErrorSize(CustomerConstants.MANDATORY_SELECT));
       Assert.assertEquals("Notes", 1, getErrorSize(CustomerConstants.MANDATORY_TEXTBOX));
        verifyInputForward();
    }

    public void testFailurePreviewWithNotesValueNull() throws Exception {
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("newStatusId", "11");
        addRequestParameter("flagId", "1");
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
       Assert.assertEquals("Notes", 1, getErrorSize(CustomerConstants.MANDATORY_TEXTBOX));
        verifyInputForward();
    }

    public void testFailurePreviewWithNotesValueExceedingMaxLength() throws Exception {
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("newStatusId", "14");
        addRequestParameter("flagId", "");
        addRequestParameter("notes", "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters "
                + "Testing for comment length exceeding by 500 characters "
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters"
                + "Testing for comment length exceeding by 500 characters");
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
       Assert.assertEquals("Notes", 1, getErrorSize(CustomerConstants.MAXIMUM_LENGTH));
        verifyInputForward();
    }

    @SuppressWarnings("unchecked")
    public void testPreviewSuccess() throws PageExpiredException {
        cleanInitialObjects();
        createInitialObjects();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", center.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 1, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", center.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "14");
        addRequestParameter("flagId", "");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNull("Since new Status is not cancel,so flag should be null.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request.getSession()));
        cleanInitialObjects();
    }

    @SuppressWarnings("unchecked")
    public void testUpdateCenterStatus() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", center.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 1, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", center.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "14");
        addRequestParameter("flagId", "");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNull("Since new Status is not cancel,so flag should be null.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request.getSession()));
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.center_detail_page.toString());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        Assert.assertFalse(center.isActive());
        TestDatabase.resetMySQLDatabase();
    }

    @SuppressWarnings("unchecked")
    public void testLoadForClient() throws PageExpiredException {
        createInitialObjects();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<CustomerStatusEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());
        cleanInitialObjects();
    }

    public void testFailurePreviewWithAllValuesNullForClient() throws Exception {
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        actionPerform();
       Assert.assertEquals(2, getErrorSize());
       Assert.assertEquals("Status id", 1, getErrorSize(CustomerConstants.MANDATORY_SELECT));
       Assert.assertEquals("Notes", 1, getErrorSize(CustomerConstants.MANDATORY_TEXTBOX));
        verifyInputForward();
    }

    public void testFailurePreviewWithFlagValueNullForCLient() throws Exception {
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("newStatusId", "6");
        actionPerform();
       Assert.assertEquals(2, getErrorSize());
       Assert.assertEquals("flag id", 1, getErrorSize(CustomerConstants.MANDATORY_SELECT));
       Assert.assertEquals("Notes", 1, getErrorSize(CustomerConstants.MANDATORY_TEXTBOX));
        verifyInputForward();
    }

    public void testFailurePreviewWithNotesValueNullForClient() throws Exception {
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("newStatusId", "6");
        addRequestParameter("flagId", "10");
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
       Assert.assertEquals("Notes", 1, getErrorSize(CustomerConstants.MANDATORY_TEXTBOX));
        verifyInputForward();
    }

    @SuppressWarnings("unchecked")
    public void testPreviewSuccessForClient() throws PageExpiredException {
        createInitialObjects();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", client.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "6");
        addRequestParameter("flagId", "10");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNotNull("Since new Status is Closed,so flag should not be null.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request));
        cleanInitialObjects();
    }

    public void testPrevious() {
        createInitialObjects();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previousStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.previousStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
    }

    public void testCancel() {
        createInitialObjects();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.cancelStatus.toString());
        addRequestParameter("input", "client");
        actionPerform();
        verifyForward(ActionForwards.client_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        cleanInitialObjects();
    }

    @SuppressWarnings("unchecked")
    public void testUpdateStatusForClient() throws PageExpiredException {
        createInitialObjects();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", client.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "4");
        addRequestParameter("flagId", "");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNull("Since new Status is not Closed,so flag should be null.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request.getSession()));
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.client_detail_page.toString());
        client = TestObjectFactory.getCustomer(client.getCustomerId());
        Assert.assertFalse(client.isActive());
        cleanInitialObjects();
    }

    @SuppressWarnings("unchecked")
    public void testUpdateStatusForClientForFirstTimeActive() throws PageExpiredException {
        createInitialObjects(CustomerStatus.CENTER_ACTIVE, CustomerStatus.GROUP_ACTIVE, CustomerStatus.CLIENT_PARTIAL);
       Assert.assertTrue(((ClientBO) client).getCustomerAccount().getAccountActionDates().isEmpty());
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", client.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "3");
        addRequestParameter("flagId", "");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNull("Since new Status is not Closed,so flag should be null.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request.getSession()));
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.client_detail_page.toString());
        client = TestObjectFactory.getCustomer(client.getCustomerId());
       Assert.assertTrue(client.isActive());
        Assert.assertFalse(((ClientBO) client).getCustomerAccount().getAccountActionDates().isEmpty());
       Assert.assertEquals("ActivationDate should be the current date.", DateUtils
                .getDateWithoutTimeStamp(new java.util.Date().getTime()), DateUtils.getDateWithoutTimeStamp(client
                .getCustomerActivationDate().getTime()));
        cleanInitialObjects();
    }

    @SuppressWarnings("unchecked")
    public void testUpdateStatusForClientForActiveLoanOfficer() throws CustomerException, PageExpiredException {
        createInitialObjects();
        CustomerBOTestUtils.setCustomerStatus(client, new CustomerStatusEntity(CustomerStatus.CLIENT_PARTIAL
                .getValue()));
        client.update();
        StaticHibernateUtil.commitTransaction();

        client = TestObjectFactory.getCustomer(client.getCustomerId());
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", client.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "3");
        addRequestParameter("flagId", "");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNull("Since new Status is not Closed,so flag should be null.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request.getSession()));
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.client_detail_page.toString());
        client = TestObjectFactory.getCustomer(client.getCustomerId());
       Assert.assertTrue(client.isActive());
        cleanInitialObjects();
    }

    @SuppressWarnings("unchecked")
    public void testUpdateStatusForClientWhenParentCustomerIsInPartialState() throws PageExpiredException {
        createInitialObjects(CustomerStatus.CENTER_ACTIVE, CustomerStatus.GROUP_PARTIAL, CustomerStatus.CLIENT_PARTIAL);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", client.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "3");
        addRequestParameter("flagId", "");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNull("Since new Status is not Closed,so flag should be null.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request));
        setRequestPathInfo("/editCustomerStatusAction");
        addRequestParameter("method", Methods.updateStatus.toString());
        actionPerform();
        verifyActionErrors(new String[] { ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION });
        verifyForward(ActionForwards.updateStatus_failure.toString());
        client = TestObjectFactory.getCustomer(client.getCustomerId());
        Assert.assertFalse(client.isActive());
        cleanInitialObjects();
    }

    @SuppressWarnings("unchecked")
    public void testUpdateStatusForClientWhenClientHasActiveAccounts() throws CustomerException, PageExpiredException {
        createInitialObjects();
        loanBO = getLoanAccount(client, "dsafdsfds", "12ed");
        client.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getCustomer(client.getCustomerId());
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", client.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "6");
        addRequestParameter("flagId", "7");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNotNull("Since new Status is Closed,so flag should be Duplicate.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request));
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        actionPerform();
        verifyActionErrors(new String[] { CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION });
        verifyForward(ActionForwards.updateStatus_failure.toString());
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getCustomer(client.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO.getAccountId());
        TestObjectFactory.cleanUp(loanBO);
        cleanInitialObjects();
    }

    @SuppressWarnings("unchecked")
    public void testUpdateStatusForClientWhenClientIsAssignedPosition() throws CustomerException, PageExpiredException {
        createInitialObjects();
        CustomerPositionEntity customerPositionEntity = new CustomerPositionEntity(new PositionEntity(Short
                .valueOf("1")), client, client.getParentCustomer());
        group.addCustomerPosition(customerPositionEntity);
        group.update();
        StaticHibernateUtil.commitTransaction();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", client.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "6");
        addRequestParameter("flagId", "7");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNotNull("Since new Status is Closed,so flag should be Duplicate.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request));
        for (CustomerPositionEntity customerPosition : group.getCustomerPositions()) {
            Assert.assertNotNull(customerPosition.getCustomer());
            break;
        }
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.client_detail_page.toString());
        client = TestObjectFactory.getCustomer(client.getCustomerId());
        Assert.assertFalse(client.isActive());
        for (CustomerFlagDetailEntity customerFlagDetailEntity : client.getCustomerFlags()) {
            Assert.assertFalse(customerFlagDetailEntity.getStatusFlag().isBlackListed());
            break;
        }
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        for (CustomerPositionEntity customerPosition : group.getCustomerPositions()) {
            Assert.assertNull(customerPosition.getCustomer());
            break;
        }
        cleanInitialObjects();
    }

    @SuppressWarnings("unchecked")
    public void testChangeStatusToActiveForClient() throws Exception {
        createObjectsForClient("Client");
        CustomerBOTestUtils.setPersonnel(client, null);
        client.update();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", client.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", CustomerStatus.CLIENT_ACTIVE.getValue().toString());
        addRequestParameter("flagId", "");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNull("Since new Status is not Closed,so flag should be null.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request.getSession()));
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        actionPerform();
        verifyActionErrors(new String[] { ClientConstants.CLIENT_LOANOFFICER_NOT_ASSIGNED });
        verifyForward(ActionForwards.updateStatus_failure.toString());
        cleanObjectsForClient();
    }

    @SuppressWarnings("unchecked")
    public void testChangeStatusToActiveForClientForMeetingNull() throws Exception {
        createClientWithoutMeeting("Client");
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", client.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<AccountStateEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());

        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", client.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", CustomerStatus.CLIENT_ACTIVE.getValue().toString());
        addRequestParameter("flagId", "");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        actionPerform();
        verifyActionErrors(new String[] { GroupConstants.MEETING_NOT_ASSIGNED });
        verifyForward(ActionForwards.updateStatus_failure.toString());
        cleanObjectsForClient();
    }

    @SuppressWarnings("unchecked")
    public void testLoadSuccessForGroup() throws PageExpiredException {
        createInitialObjects();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", group.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(SessionUtils.getAttribute(SavingsConstants.STATUS_LIST, request));
       Assert.assertEquals("Size of the status list should be 2", 2, ((List<CustomerStatusEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request)).size());
        cleanInitialObjects();
    }

    public void testPreviewSuccessForGroup() throws PageExpiredException {
        createInitialObjects();
        invokeLoadSuccessfully();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", group.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "10");
        addRequestParameter("flagId", "");
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
       Assert.assertEquals(getStatusName(CustomerStatus.fromInt(Short.valueOf("10"))), (String) SessionUtils.getAttribute(
                SavingsConstants.NEW_STATUS_NAME, request));
        Assert.assertNull("Since new Status is not Closed,so flag should be null.", SessionUtils.getAttribute(
                SavingsConstants.FLAG_NAME, request));
        cleanInitialObjects();
    }

    public void testPreviewStatusFailureWithAllValuesNullForGroup() throws Exception {
        createInitialObjects();
        invokeLoadSuccessfully();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("levelId", group.getCustomerLevel().getId().toString());
        addRequestParameter("flagId", "20");
        actionPerform();
       Assert.assertEquals(2, getErrorSize());
       Assert.assertEquals("Status id", 1, getErrorSize(CustomerConstants.MANDATORY_SELECT));
       Assert.assertEquals("Notes", 1, getErrorSize(CustomerConstants.MANDATORY_TEXTBOX));
        verifyInputForward();
        cleanInitialObjects();
    }

    public void testPreviewStatusFailureWithFlagValueNullForGroup() throws Exception {
        createInitialObjects();
        invokeLoadSuccessfully();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", group.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "12");
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
       Assert.assertEquals("flag id", 1, getErrorSize(CustomerConstants.MANDATORY_SELECT));
        verifyInputForward();
        cleanInitialObjects();
    }

    public void testPreviewStatusFailureWhenStatusIsNull() {
        createInitialObjects();
        invokeLoadSuccessfully();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("levelId", group.getCustomerLevel().getId().toString());
        addRequestParameter("flagId", "20");
        addRequestParameter("notes", "Test");
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
       Assert.assertEquals("Status", 1, getErrorSize(CustomerConstants.MANDATORY_SELECT));
        verifyInputForward();
        cleanInitialObjects();
    }

    public void testPreviewStatusFailureWhenNotesIsNull() {
        createInitialObjects();
        invokeLoadSuccessfully();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("levelId", group.getCustomerLevel().getId().toString());
        addRequestParameter("newStatusId", "12");
        addRequestParameter("flagId", "20");
        actionPerform();
       Assert.assertEquals(1, getErrorSize());
       Assert.assertEquals("Notes", 1, getErrorSize(CustomerConstants.MANDATORY_TEXTBOX));
        verifyInputForward();
        cleanInitialObjects();
    }

    public void testPreviousStatus() {
        createInitialObjects();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previousStatus.toString());
        addRequestParameter("customerId", group.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.previousStatus_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        cleanInitialObjects();
    }

    public void testCancelStatus() {
        createInitialObjects();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", "cancelStatus");
        addRequestParameter("input", "group");
        actionPerform();
        verifyForward(ActionForwards.group_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        cleanInitialObjects();
    }

    public void testUpdateStatusSuccess() {
        createInitialObjects(CustomerStatus.CENTER_ACTIVE, CustomerStatus.GROUP_PARTIAL, CustomerStatus.CLIENT_CLOSED);
        invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_CLOSED, CustomerStatusFlag.GROUP_CLOSED_BLACKLISTED);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        addRequestParameter("input", "group");
        actionPerform();
        verifyForward(ActionForwards.group_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNull(request.getAttribute(Constants.FLOWMANAGER));
        group = TestObjectFactory.getCustomer(group.getCustomerId());
       Assert.assertTrue(group.isBlackListed());
        cleanInitialObjects();
    }

    public void testUpdateStatusSuccessWhileChangingStatusToActive() {
        createInitialObjects(CustomerStatus.CENTER_ACTIVE, CustomerStatus.GROUP_PARTIAL, CustomerStatus.CLIENT_CLOSED);
        invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_ACTIVE, null);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        addRequestParameter("input", "group");
        actionPerform();
        verifyForward(ActionForwards.group_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNull(request.getAttribute(Constants.FLOWMANAGER));
        group = TestObjectFactory.getCustomer(group.getCustomerId());
       Assert.assertEquals("ActivationDate should be the current date.", DateUtils
                .getDateWithoutTimeStamp(new java.util.Date().getTime()), DateUtils.getDateWithoutTimeStamp(group
                .getCustomerActivationDate().getTime()));
        cleanInitialObjects();
    }

    public void testUpdateStatusFailureWhenGroupHasActiveAccounts() throws CustomerException {
        createInitialObjects();
        loanBO = getLoanAccount(group, "dsafdsfsdgfdg", "23vf");
        group.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_CLOSED, CustomerStatusFlag.GROUP_CLOSED_OTHER);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        addRequestParameter("input", "group");
        actionPerform();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        verifyActionErrors(new String[] { CustomerConstants.CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION });
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        client = TestObjectFactory.getCustomer(client.getCustomerId());
        loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO.getAccountId());
        TestObjectFactory.cleanUp(loanBO);
        cleanInitialObjects();
    }

    public void testUpdateStatusFailureWhenGroupHasActiveClients() {
        createInitialObjects();
        invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_CLOSED, CustomerStatusFlag.GROUP_CLOSED_OTHER);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        addRequestParameter("input", "group");
        actionPerform();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        verifyActionErrors(new String[] { CustomerConstants.ERROR_STATE_CHANGE_EXCEPTION });
        cleanInitialObjects();
    }

    public void testUpdateStatusFailureWhenGroupHasActiveClientsWhenOfficeInactiveWhileChangingStatusCancelToPartial()
            throws NumberFormatException, Exception {
        createInitialObjectsOfficeInactive(CustomerStatus.GROUP_CANCELLED, CustomerStatus.CLIENT_CLOSED);
        OfficeBO officeBO = group.getOffice();
        officeBO.update(officeBO.getOfficeName(), officeBO.getShortName(), OfficeStatus.INACTIVE, officeBO
                .getOfficeLevel(), officeBO.getParentOffice(), null, null);
        StaticHibernateUtil.commitTransaction();
        invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_PARTIAL, null);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        addRequestParameter("input", "group");
        actionPerform();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        verifyActionErrors(new String[] { GroupConstants.BRANCH_INACTIVE });
        office = (OfficeBO) TestObjectFactory.getObject(OfficeBO.class, office.getOfficeId());
        cleanInitialObjectsOfficeInactive();
    }

    public void testUpdateStatusFailureWhenGroupHasActiveClientsWhenCenterIsInactiveWhileChangingStatusCancelToPartial()
            throws CustomerException {
        createInitialObjects(CustomerStatus.CENTER_ACTIVE, CustomerStatus.GROUP_CANCELLED, CustomerStatus.CLIENT_CLOSED);
        center.changeStatus(CustomerStatus.CENTER_INACTIVE, null, "center is inactive now");
        StaticHibernateUtil.commitTransaction();
        invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_PARTIAL, null);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        addRequestParameter("input", "group");
        actionPerform();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        verifyActionErrors(new String[] { GroupConstants.CENTER_INACTIVE });
        cleanInitialObjects();
    }

    public void testUpdateStatusFailureWhenGroupIsUnderBranchWhileChangingStatusCancelToPartial() {
        createInitialObjectsWhenCenterHierarchyNotExist(CustomerStatus.GROUP_CANCELLED, CustomerStatus.CLIENT_CLOSED);
        invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_PARTIAL, null);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        addRequestParameter("input", "group");
        actionPerform();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        verifyActionErrors(new String[] { GroupConstants.LOANOFFICER_INACTIVE });
        cleanInitialObjectsWhenCenterHierarchyNotExist();
    }

    public void testChangeStatusToActiveForGroupUnderBranchWithNoLO() {
        createInitialObjectsWhenCenterHierarchyNotExistWithNoLO(CustomerStatus.GROUP_PARTIAL,
                CustomerStatus.CLIENT_CLOSED);
        invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_ACTIVE, null);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        addRequestParameter("input", "group");
        actionPerform();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        verifyActionErrors(new String[] { GroupConstants.GROUP_LOANOFFICER_NOT_ASSIGNED });
        cleanInitialObjectsWhenCenterHierarchyNotExist();
    }

    public void testUpdateStatusFailureWhenGroupIsUnderBranchWitnNoMeetingsWhileChangingStatusToActive() {
        createInitialObjectsWhenCenterHierarchyNotExistWithNoMeeting(CustomerStatus.GROUP_PARTIAL,
                CustomerStatus.CLIENT_CLOSED);
        invokeLoadAndPreviewSuccessfully(CustomerStatus.GROUP_ACTIVE, null);
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.updateStatus.toString());
        addRequestParameter("input", "group");
        actionPerform();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
        verifyActionErrors(new String[] { GroupConstants.MEETING_NOT_ASSIGNED });
        cleanInitialObjectsWhenCenterHierarchyNotExist();
    }

    private void invokeLoadSuccessfully() {
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.loadStatus.toString());
        addRequestParameter("customerId", group.getCustomerId().toString());
        actionPerform();
        verifyForward(ActionForwards.loadStatus_success.toString());
    }

    private void invokeLoadAndPreviewSuccessfully(CustomerStatus groupStatus, CustomerStatusFlag groupStatusFlag) {
        invokeLoadSuccessfully();
        setRequestPathInfo("/editCustomerStatusAction.do");
        addRequestParameter("method", Methods.previewStatus.toString());
        addRequestParameter("notes", "Test");
        addRequestParameter("levelId", group.getCustomerLevel().getId().toString());
        if (groupStatus != null)
            addRequestParameter("newStatusId", groupStatus.getValue().toString());
        if (groupStatusFlag != null)
            addRequestParameter("flagId", groupStatusFlag.getValue().toString());
        actionPerform();
        verifyForward(ActionForwards.previewStatus_success.toString());
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private void createInitialObjects(CustomerStatus centerStatus, CustomerStatus groupStatus,
            CustomerStatus clientStatus) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", groupStatus, center);
        client = TestObjectFactory.createClient("Client", clientStatus, group);
    }
    
    private void cleanInitialObjects(){
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
    }

    private void createInitialObjectsWhenCenterHierarchyNotExist(CustomerStatus groupStatus, CustomerStatus clientStatus) {
        Short officeId = new Short("3");
        Short personnel = new Short("1");
        group = TestObjectFactory.createGroupUnderBranch("Group", groupStatus, officeId, getMeeting(), personnel);
        client = TestObjectFactory.createClient("Client", clientStatus, group, new java.util.Date());
    }
    private void cleanInitialObjectsWhenCenterHierarchyNotExist() {
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
    }

    private void createInitialObjectsWhenCenterHierarchyNotExistWithNoLO(CustomerStatus groupStatus,
            CustomerStatus clientStatus) {
        Short officeId = new Short("3");
        group = TestObjectFactory.createGroupUnderBranch("Group", groupStatus, officeId, getMeeting(), null);
        client = TestObjectFactory.createClient("Client", clientStatus, group, new java.util.Date());
    }

    private void createObjectsForClient(String name) throws Exception {
        office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
                .getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
        client = TestObjectFactory.createClient( name, getMeeting(), CustomerStatus.CLIENT_PARTIAL);
    }
    
    private void cleanObjectsForClient() {
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(office);
    }

    private void createClientWithoutMeeting(String name) throws Exception {
        office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
                .getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
        client = TestObjectFactory.createClient( name, null, CustomerStatus.CLIENT_PARTIAL);
    }

    private void createInitialObjectsOfficeInactive(CustomerStatus groupStatus, CustomerStatus clientStatus)
            throws NumberFormatException, Exception {
        office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
                .getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
        group = TestObjectFactory.createGroupUnderBranch("Group", groupStatus, office.getOfficeId(), getMeeting(),
                PersonnelConstants.TEST_USER);
        client = TestObjectFactory.createClient("Client", clientStatus, group, new java.util.Date());
    }
    
    private void cleanInitialObjectsOfficeInactive(){
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(office);
    }

    private void createInitialObjectsWhenCenterHierarchyNotExistWithNoMeeting(CustomerStatus groupStatus,
            CustomerStatus clientStatus) {
        Short officeId = new Short("3");
        Short personnel = new Short("1");
        group = TestObjectFactory.createGroupUnderBranch("Group", groupStatus, officeId, null, personnel);
        client = TestObjectFactory.createClient("Client", clientStatus, group, new java.util.Date());
    }

    private MeetingBO getMeeting() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return meeting;
    }

    private LoanBO getLoanAccount(CustomerBO customerBO, String offeringName, String shortName) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(offeringName, shortName, startDate, center
                .getCustomerMeeting().getMeeting());
        return TestObjectFactory.createLoanAccount("42423142341", customerBO,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);
    }

    @SuppressWarnings("unchecked")
    private String getStatusName(CustomerStatus customerStatus) throws PageExpiredException {
        List<CustomerStatusEntity> customerStatusList = (List<CustomerStatusEntity>) SessionUtils.getAttribute(
                SavingsConstants.STATUS_LIST, request);
        for (CustomerStatusEntity custStatus : customerStatusList) {
            if (customerStatus.getValue().equals(custStatus.getId()))
                return custStatus.getName();
        }
        return null;
    }
}
