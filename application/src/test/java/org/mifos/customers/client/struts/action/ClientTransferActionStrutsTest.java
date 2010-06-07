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

package org.mifos.customers.client.struts.action;

import java.util.List;

import junit.framework.Assert;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.business.CustomerHierarchyEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerSearchInputDto;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class ClientTransferActionStrutsTest extends MifosMockStrutsTestCase {
    public ClientTransferActionStrutsTest() throws Exception {
        super();
    }

    private CenterBO center;
    private GroupBO group;
    private CenterBO center1;
    private GroupBO group1;
    private ClientBO client;
    private OfficeBO office;
    private String flowKey;

    @Override
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        flowKey = createFlow(request, ClientTransferAction.class);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(group1);
            TestObjectFactory.cleanUp(center);
            TestObjectFactory.cleanUp(center1);
            TestObjectFactory.cleanUp(office);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoad_transferToBranch() throws Exception {
        setRequestPathInfo("/clientTransferAction.do");
        addRequestParameter("method", "loadBranches");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.loadBranches_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testSuccessfulPreview_transferToBranch() throws Exception {
        createObjectsForClientTransfer();
        setRequestPathInfo("/clientTransferAction.do");
        addRequestParameter("method", "previewBranchTransfer");
        addRequestParameter("officeId", client.getOffice().getOfficeId().toString());
        addRequestParameter("officeName", client.getOffice().getOfficeName());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.previewBranchTransfer_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testFailure_transferToBranch() throws Exception {
        createObjectsForClientTransfer();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
        setRequestPathInfo("/clientTransferAction.do");
        addRequestParameter("method", "transferToBranch");
        addRequestParameter("officeId", client.getOffice().getOfficeId().toString());
        addRequestParameter("officeName", client.getOffice().getOfficeName());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyActionErrors(new String[] { CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER });
        verifyForward(ActionForwards.transferToBranch_failure.toString());
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
    }

    public void testSuccessful_transferToBranch() throws Exception {
        createObjectsForClientTransfer();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
        setRequestPathInfo("/clientTransferAction.do");
        addRequestParameter("method", "transferToBranch");
        addRequestParameter("officeId", office.getOfficeId().toString());
        addRequestParameter("officeName", office.getOfficeName());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.update_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(client.getOffice().getOfficeId(), office.getOfficeId());
        Assert.assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
        office = client.getOffice();
    }

    public void testCancel() throws Exception {
        setRequestPathInfo("/clientTransferAction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.cancel_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testLoad_updateParent() throws Exception {
        setRequestPathInfo("/clientTransferAction.do");
        addRequestParameter("method", "loadParents");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.loadParents_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        CustomerSearchInputDto clientSearchInput = (CustomerSearchInputDto) SessionUtils.getAttribute(
                CustomerConstants.CUSTOMER_SEARCH_INPUT, request.getSession());
        Assert.assertNotNull(clientSearchInput);
        Assert.assertEquals(TestObjectFactory.HEAD_OFFICE, clientSearchInput.getOfficeId());
    }

    public void testPreview_transferToParent() throws Exception {
        createObjectsForTransferringClientInGroup();
        setRequestPathInfo("/clientTransferAction.do");
        addRequestParameter("method", "previewParentTransfer");
        addRequestParameter("parentGroupId", client.getParentCustomer().getCustomerId().toString());
        addRequestParameter("parentGroupName", client.getParentCustomer().getDisplayName());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.previewParentTransfer_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testSuccessful_transferToParent() throws Exception {
        createObjectsForTransferringClientInGroup();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
        setRequestPathInfo("/clientTransferAction.do");
        addRequestParameter("method", "updateParent");
        addRequestParameter("parentGroupId", group1.getCustomerId().toString());
        addRequestParameter("parentGroupName", group1.getDisplayName());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.update_success.toString());
        client = TestObjectFactory.getClient(client.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        Assert.assertEquals(group1.getCustomerId(), client.getParentCustomer().getCustomerId());
        Assert.assertEquals(1, group1.getMaxChildCount().intValue());
        Assert.assertEquals(center1.getSearchId() + ".1.1", client.getSearchId());
        CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
        Assert.assertEquals(group1.getCustomerId(), currentHierarchy.getParentCustomer().getCustomerId());
    }

    public void testSuccessful_transferToBranch_AuditLog() throws Exception {
        createObjectsForClientTransfer();
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
        setRequestPathInfo("/clientTransferAction.do");
        addRequestParameter("method", "transferToBranch");
        addRequestParameter("officeId", office.getOfficeId().toString());
        addRequestParameter("officeName", office.getOfficeName());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.update_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(client.getOffice().getOfficeId(), office.getOfficeId());
        Assert.assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
        office = client.getOffice();
        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.CLIENT, client.getCustomerId());
        Assert.assertEquals(1, auditLogList.size());
        Assert.assertEquals(EntityType.CLIENT.getValue(), auditLogList.get(0).getEntityType());
        Assert.assertEquals(client.getCustomerId(), auditLogList.get(0).getEntityId());

        Assert.assertEquals(3, auditLogList.get(0).getAuditLogRecords().size());

        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Loan Officer Assigned")) {
                matchValues(auditLogRecord, "mifos", "-");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Status")) {
                matchValues(auditLogRecord, "Active", "On Hold");
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Branch Office Name")) {
                matchValues(auditLogRecord, "TestBranchOffice", "customer_office");
            }
        }
        TestObjectFactory.cleanUpChangeLog();
    }

    private void createObjectsForClientTransfer() throws Exception {
        office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
                .getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
        client = TestObjectFactory.createClient("client_to_transfer", getMeeting(), CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.closeSession();
    }

    private void createObjectsForTransferringClientInGroup() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meeting1 = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        center1 = TestObjectFactory.createWeeklyFeeCenter("Center1", meeting1);
        group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group2", CustomerStatus.GROUP_ACTIVE, center1);
        client = TestObjectFactory.createClient("Client11", CustomerStatus.CLIENT_ACTIVE, group);
        StaticHibernateUtil.closeSession();
    }

    private MeetingBO getMeeting() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return meeting;
    }
}
