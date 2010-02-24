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

package org.mifos.customers.struts.uihelpers;

import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerFlagDetailEntity;
import org.mifos.customers.business.CustomerPositionEntity;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.struts.action.EditCustomerStatusAction;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerUIHelperFnStrutsTest extends MifosMockStrutsTestCase {

    public CustomerUIHelperFnStrutsTest() throws Exception {
        super();
    }

    private CustomerBO center;

    private CustomerBO group;

    private CustomerBO client;

    private MeetingBO meeting;

    private String flowKey;

    UserContext userContext;

    @Override 
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }
        
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USER_CONTEXT_KEY, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());

        flowKey = createFlow(request, EditCustomerStatusAction.class);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testUIHelperWhenClientIsAssignedPosition() throws CustomerException, PageExpiredException {
        createInitialObjects();
        PositionEntity positionEntity = (PositionEntity) TestObjectFactory.getObject(PositionEntity.class, Short
                .valueOf("1"));
        CustomerPositionEntity customerPositionEntity = new CustomerPositionEntity(positionEntity, client, client
                .getParentCustomer());
        group.addCustomerPosition(customerPositionEntity);
        group.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getCustomer(client.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group.setUserContext(TestObjectFactory.getContext());
        for (CustomerPositionEntity customerPositionEntity2 : group.getCustomerPositions()) {
            customerPositionEntity2.getPosition().setLocaleId(TestObjectFactory.getContext().getLocaleId());
        }
        String positionName = CustomerUIHelperFn.getClientPosition(group.getCustomerPositions(), client);
       Assert.assertEquals("(Center Leader)", positionName);
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
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        Assert.assertFalse(client.isActive());
        for (CustomerFlagDetailEntity customerFlagDetailEntity : client.getCustomerFlags()) {
            Assert.assertFalse(customerFlagDetailEntity.getStatusFlag().isBlackListed());
            break;
        }
        for (CustomerPositionEntity customerPosition : group.getCustomerPositions()) {
            Assert.assertNull(customerPosition.getCustomer());
            break;
        }
    }

    public void testUIHelperWhenClientIsNotAssignedPosition() throws CustomerException, PageExpiredException {
        createInitialObjects();
        PositionEntity positionEntity = (PositionEntity) TestObjectFactory.getObject(PositionEntity.class, Short
                .valueOf("1"));
        CustomerPositionEntity customerPositionEntity = new CustomerPositionEntity(positionEntity, client, client
                .getParentCustomer());
        group.addCustomerPosition(customerPositionEntity);
        group.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getCustomer(client.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group.setUserContext(TestObjectFactory.getContext());
        String positionName = CustomerUIHelperFn.getClientPosition(group.getCustomerPositions(), client);
       Assert.assertEquals("(Center Leader)", positionName);
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
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        Assert.assertFalse(client.isActive());
        for (CustomerFlagDetailEntity customerFlagDetailEntity : client.getCustomerFlags()) {
            Assert.assertFalse(customerFlagDetailEntity.getStatusFlag().isBlackListed());
            break;
        }
        for (CustomerPositionEntity customerPosition : group.getCustomerPositions()) {
            Assert.assertNull(customerPosition.getCustomer());
            break;
        }
    }

    private void createInitialObjects() {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
    }

}
