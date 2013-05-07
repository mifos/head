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

package org.mifos.customers.group.struts.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.NameType;
import org.mifos.customers.client.persistence.LegacyClientDao;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class AddGroupMembershipActionStrutsTest extends MifosMockStrutsTestCase {

    private GroupBO group;
    private ClientBO client;
    private MeetingBO meeting;
    private String flowKey;

    @Autowired
    LegacyClientDao legacyClientDao;

    @Override
    protected void setStrutsConfig() throws IOException {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }

    @Before
    public void setUp() throws Exception {
        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        FlowManager flowManager = new FlowManager();
        request.getSession(false).setAttribute(Constants.FLOWMANAGER, flowManager);

        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

        flowKey = createFlow(request, AddGroupMembershipAction.class);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
    }

    @After
    public void tearDown() throws Exception {
        client = null;
        group = null;
    }

    @Test
    public void testSuccessfulPrevious() throws Exception {
        setRequestPathInfo("/addGroupMembershipAction.do");
        addRequestParameter("method", "loadSearch");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.loadSearch_success.toString().toString());
        verifyNoActionErrors();
        verifyNoActionMessages();

    }

    @Test
    public void testCancel() throws Exception {

        setRequestPathInfo("/addGroupMembershipAction.do");
        addRequestParameter("method", "cancel");

        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.cancel_success.toString());
        verifyNoActionErrors();

    }

    @Test
    public void testPreviewParentAddClient() throws Exception {

        setRequestPathInfo("/addGroupMembershipAction.do");
        addRequestParameter("method", "previewParentAddClient");

        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.confirmAddClientToGroup_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();

    }

    @Test
    public void testSuccessfulUpdateParent() throws Exception {
        createAndSetClientInSession();
        createParentGroup();
       Assert.assertEquals(false, client.isClientUnderGroup());
       Assert.assertNotSame(group.getCustomerMeeting().getMeeting().getMeetingId(), client.getCustomerMeeting().getMeeting()
                .getMeetingId());

        setRequestPathInfo("/addGroupMembershipAction.do");
        addRequestParameter("method", "updateParent");
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
        addRequestParameter("parentGroupId", group.getCustomerId().toString());
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        Assert.assertNotNull(client);
       Assert.assertEquals(client.getCustomerMeeting().getMeeting().getMeetingId(), group.getCustomerMeeting().getMeeting()
                .getMeetingId());

       Assert.assertEquals(true, client.isClientUnderGroup());
        verifyNoActionMessages();
        verifyForward(ActionForwards.view_client_details_page.toString());
        verifyNoActionErrors();

    }

    private void createParentGroup() {
        Short officeId = 1;
        Short personnel = 3;
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        group = TestObjectFactory.createGroupUnderBranch("group1", CustomerStatus.GROUP_ACTIVE, officeId, meeting,
                personnel);

    }

    private void createAndSetClientInSession() throws Exception {
        OfficeBO office = new OfficePersistence().getOffice(TestObjectFactory.HEAD_OFFICE);
        PersonnelBO personnel = legacyPersonnelDao.getPersonnel(PersonnelConstants.TEST_USER);
        meeting = getMeeting();
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(), 1, "Client", "", "1", "");
        clientNameDetailDto.setNames(ClientRules.getNameSequence());
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(), 1, "first", "middle",
                "last", "secondLast");
        spouseNameDetailView.setNames(ClientRules.getNameSequence());
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, new Address(), getCustomFields(), null, null, personnel, office,
                meeting, personnel, new java.util.Date(), null, null, null, YesNoFlag.NO.getValue(),
                clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto, null);
        legacyClientDao.saveClient(client);
        StaticHibernateUtil.flushSession();
        client = TestObjectFactory.getClient(Integer.valueOf(client.getCustomerId()).intValue());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
    }

    private MeetingBO getMeeting() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return meeting;
    }

    private List<CustomFieldDto> getCustomFields() {
        List<CustomFieldDto> fields = new ArrayList<CustomFieldDto>();
        fields.add(new CustomFieldDto(Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC.getValue()));
        fields.add(new CustomFieldDto(Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC.getValue()));
        return fields;
    }

}
