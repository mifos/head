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

package org.mifos.application.customer.group.struts.action;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.customer.business.CustomerHierarchyEntity;
import org.mifos.application.customer.business.CustomerMovementEntity;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.CenterSearchInput;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupTransferActionStrutsTest extends MifosMockStrutsTestCase {
    public GroupTransferActionStrutsTest() throws Exception {
        super();
    }

    private CenterBO center;
    private GroupBO group;
    private CenterBO center1;
    private GroupBO group1;
    private ClientBO client;
    private OfficeBO office;
    private Short officeId = 3;
    private Short personnelId = 3;
    private String flowKey;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        FlowManager flowManager = new FlowManager();
        request.getSession(false).setAttribute(Constants.FLOWMANAGER, flowManager);

        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

        flowKey = createFlow(request, GroupTransferAction.class);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(group1);
        TestObjectFactory.cleanUp(center);
        TestObjectFactory.cleanUp(center1);
        TestObjectFactory.cleanUp(office);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoad_transferToBranch() throws Exception {
        loadOffices();
        verifyForward(ActionForwards.loadBranches_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testSuccessfulPreview_transferToBranch() throws Exception {
        loadOffices();
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "previewBranchTransfer");
        addRequestParameter("officeId", group.getOffice().getOfficeId().toString());
        addRequestParameter("officeName", group.getOffice().getOfficeName());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        verifyForward(ActionForwards.previewBranchTransfer_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testFailure_transferToBranch() throws Exception {
        loadOffices();
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "previewBranchTransfer");
        addRequestParameter("officeId", group.getOffice().getOfficeId().toString());
        addRequestParameter("officeName", group.getOffice().getOfficeName());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "transferToBranch");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyActionErrors(new String[] { CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER });
        verifyForward(ActionForwards.transferToBranch_failure.toString());
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    public void testSuccessful_transferToBranch() throws Exception {
        TestObjectFactory.cleanUpChangeLog();
        office = createOffice();
        loadOffices();
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "previewBranchTransfer");
        addRequestParameter("officeId", office.getOfficeId().toString());
        addRequestParameter("officeName", office.getOfficeName());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "transferToBranch");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.update_success.toString());

        group = TestObjectFactory.getGroup(group.getCustomerId());
       Assert.assertEquals(office.getOfficeId(), group.getOffice().getOfficeId());
       Assert.assertEquals(CustomerStatus.GROUP_HOLD, group.getStatus());

        CustomerMovementEntity customerMovement = group.getActiveCustomerMovement();
        Assert.assertNotNull(customerMovement);
       Assert.assertEquals(office.getOfficeId(), customerMovement.getOffice().getOfficeId());
        office = group.getOffice();

        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.GROUP, group.getCustomerId());
       Assert.assertEquals(1, auditLogList.size());
       Assert.assertEquals(EntityType.GROUP.getValue(), auditLogList.get(0).getEntityType());
       Assert.assertEquals(3, auditLogList.get(0).getAuditLogRecords().size());
        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Branch Office Name")) {
               Assert.assertEquals("TestBranchOffice", auditLogRecord.getOldValue());
               Assert.assertEquals("customer_office", auditLogRecord.getNewValue());
            }
        }
        TestObjectFactory.cleanUpChangeLog();
    }

    public void testLoad_updateParent() throws Exception {
        loadParents();
        verifyForward(ActionForwards.loadParents_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        CenterSearchInput centerSearchInput = (CenterSearchInput) SessionUtils.getAttribute(
                GroupConstants.CENTER_SEARCH_INPUT, request.getSession());
        Assert.assertNotNull(centerSearchInput);
       Assert.assertEquals(TestObjectFactory.HEAD_OFFICE, centerSearchInput.getOfficeId());
    }

    public void testSuccessfulPreview_transferToCenter() throws Exception {
        loadParents();
        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "previewParentTransfer");
        addRequestParameter("centerSystemId", center.getGlobalCustNum());
        addRequestParameter("centerName", center.getDisplayName());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        verifyForward(ActionForwards.previewParentTransfer_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testFailure_transferToCenter() throws Exception {
        loadParents();
        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "previewParentTransfer");
        addRequestParameter("centerSystemId", center.getGlobalCustNum());
        addRequestParameter("centerName", center.getDisplayName());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "transferToCenter");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyActionErrors(new String[] { CustomerConstants.ERRORS_SAME_PARENT_TRANSFER });
        verifyForward(ActionForwards.transferToCenter_failure.toString());

        group = TestObjectFactory.getGroup(group.getCustomerId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        center1 = TestObjectFactory.getCenter(center1.getCustomerId());
        office = TestObjectFactory.getOffice(office.getOfficeId());
    }

    public void testSuccessful_transferToCenter() throws Exception {
        TestObjectFactory.cleanUpChangeLog();
        loadParents();
        StaticHibernateUtil.closeSession();

        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "previewParentTransfer");
        addRequestParameter("centerSystemId", center1.getGlobalCustNum());
        addRequestParameter("centerName", center1.getDisplayName());
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();

        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "transferToCenter");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        center1 = TestObjectFactory.getCenter(center1.getCustomerId());
        office = TestObjectFactory.getOffice(office.getOfficeId());

       Assert.assertEquals(center1.getCustomerId(), group.getParentCustomer().getCustomerId());

        CustomerHierarchyEntity customerHierarchy = group.getActiveCustomerHierarchy();
       Assert.assertEquals(center1.getCustomerId(), customerHierarchy.getParentCustomer().getCustomerId());
       Assert.assertEquals(group.getCustomerId(), customerHierarchy.getCustomer().getCustomerId());
        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.GROUP, new Integer(group
                .getCustomerId().toString()));
       Assert.assertEquals(1, auditLogList.size());
       Assert.assertEquals(EntityType.GROUP.getValue(), auditLogList.get(0).getEntityType());
        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Kendra Name")) {
                matchValues(auditLogRecord, "Center", "MyCenter");
            } else {
                // TODO: Kendra versus Center?
                // Assert.fail();
            }
        }
        TestObjectFactory.cleanUpChangeLog();

    }

    // Test for Remove Group MemberShip
    public void testSuccessful_removeGroupMemberShip() throws Exception {
        TestObjectFactory.cleanUpChangeLog();
        loadParents();
        StaticHibernateUtil.closeSession();

        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);

        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "removeGroupMemberShip");
        addRequestParameter("assignedLoanOfficerId", "7");
        addRequestParameter("comment", "My notes");
        Assert.assertNotNull(client);

        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
        verifyForward(ActionForwards.view_client_details_page.toString());

    }

    // Test for Load Group MemberShip
    public void testSuccessful_LoadGrpMemberShip() throws Exception {
        TestObjectFactory.cleanUpChangeLog();
        loadParents();
        StaticHibernateUtil.closeSession();
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
        MeetingBO meeting = createWeeklyMeeting(WeekDay.MONDAY, Short.valueOf("1"), new Date());
        client = TestObjectFactory.createClient("client", meeting, CustomerStatus.CLIENT_ACTIVE);
        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "previewParentTransfer");
        addRequestParameter("centerSystemId", center1.getGlobalCustNum());
        addRequestParameter("centerName", center1.getDisplayName());
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();

        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        setRequestPathInfo("/groupTransferAction.do");
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
        Assert.assertNotNull(client);

        addRequestParameter("method", "loadGrpMemberShip");
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();

    }

    private MeetingBO createWeeklyMeeting(WeekDay weekDay, Short recurAfer, Date startDate) throws MeetingException {
        return new MeetingBO(weekDay, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    public void testCancel() throws Exception {
        loadOffices();
        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "cancel");

        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
    }

    private void loadParents() throws Exception {
        center = createCenter("Center", officeId);
        office = createOffice();
        center1 = createCenter("MyCenter", office.getOfficeId());
        group = createGroup("group", center);
        startFlowForGroup();
        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "loadParents");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
    }

    private void loadOffices() {
        group = createGroupUnderBranch(CustomerStatus.GROUP_ACTIVE, officeId);
        startFlowForGroup();
        setRequestPathInfo("/groupTransferAction.do");
        addRequestParameter("method", "loadBranches");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
    }

    private void startFlowForGroup() {
        setRequestPathInfo("/groupCustAction.do");
        addRequestParameter("globalCustNum", group.getGlobalCustNum());
        addRequestParameter("method", "get");
        actionPerform();
    }

    private GroupBO createGroupUnderBranch(CustomerStatus groupStatus, Short officeId) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createGroupUnderBranch("group1", groupStatus, officeId, meeting, personnelId);
    }

    private CenterBO createCenter(String name, Short officeId) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createWeeklyFeeCenter(name, meeting, officeId, personnelId);
    }

    private OfficeBO createOffice() throws Exception {
        return TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
                .getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
    }

    private GroupBO createGroup(String name, CenterBO center) {
        return TestObjectFactory.createWeeklyFeeGroupUnderCenter(name, CustomerStatus.GROUP_ACTIVE, center);
    }

}
