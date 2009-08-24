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

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerNotesActionStrutsTest extends MifosMockStrutsTestCase {

    public CustomerNotesActionStrutsTest() throws SystemException, ApplicationException {
        super();
    }

    private UserContext userContext;

    private CustomerBO client;

    private CustomerBO group;

    private CustomerBO center;

    private String flowKey;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());

        flowKey = createFlow(request, CustomerNotesAction.class);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
    }

    @Override
    public void tearDown() throws Exception {
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoad() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", center.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("load_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testFailurePreviewWithNotesValueNull() throws Exception {
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        assertEquals(1, getErrorSize());
        assertEquals("Notes", 1, getErrorSize(CustomerConstants.ERROR_MANDATORY_TEXT_AREA));
        verifyInputForward();
    }

    public void testFailurePreviewWithNotesValueExceedingMaxLength() throws Exception {
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("comment", "Testing for comment length exceeding by 500 characters"
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
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        assertEquals(1, getErrorSize());
        assertEquals("Notes", 1, getErrorSize(CustomerConstants.MAXIMUM_LENGTH));
        verifyInputForward();
    }

    public void testPreviewSuccess() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("comment", "Test");
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("preview_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testPreviousSuccess() {
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "previous");
        addRequestParameter("comment", "Test");
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("previous_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testCancelSuccess() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", center.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("load_success");
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.center_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    

    public void testLoadForClient() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", client.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("load_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testFailurePreviewWithNotesValueNullForClient() throws Exception {
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        assertEquals(1, getErrorSize());
        assertEquals("Notes", 1, getErrorSize(CustomerConstants.ERROR_MANDATORY_TEXT_AREA));
        verifyInputForward();
    }

    public void testPreviewSuccessForClient() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("comment", "Test");
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("preview_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testPreviousSuccessForClient() {
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "previous");
        addRequestParameter("comment", "Test");
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("previous_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testCancelSuccessForClient() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", client.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("load_success");
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.client_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testCreateNotesForClient() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", client.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("comment", "Notes created");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "create");
        addRequestParameter("comment", "Notes created");
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.client_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        client = (ClientBO) (StaticHibernateUtil.getSessionTL()
                .get(ClientBO.class, new Integer(client.getCustomerId())));
        assertEquals(1, client.getRecentCustomerNotes().size());
        assertEquals(1, client.getCustomerNotes().size());
    }

    public void testSearch() throws Exception {
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", center.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("comment", "Notes created");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "create");
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter("comment", "Notes created");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/centerCustAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("customerId", center.getCustomerId().toString());
        addRequestParameter("globalCustNum", center.getGlobalCustNum());
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "search");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("search_success");
        verifyNoActionErrors();
        verifyNoActionMessages();

        assertEquals("Size of the search result should be 1", 1, ((QueryResult) SessionUtils.getAttribute(
                Constants.SEARCH_RESULTS, request)).getSize());
        StaticHibernateUtil.closeSession();

        getobjects();
    }

    private void getobjects() {
        client = TestObjectFactory.getCustomer(client.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
    }

    public void testLoadForGroup() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("load_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testFailurePreviewWithNotesValueNullForGroup() throws Exception {
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        assertEquals(1, getErrorSize());
        assertEquals("Notes", 1, getErrorSize(CustomerConstants.ERROR_MANDATORY_TEXT_AREA));
        verifyInputForward();
    }

    public void testPreviewSuccessForGroup() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("comment", "Test");
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("preview_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testPreviousSuccessForGroup() {
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "previous");
        addRequestParameter("comment", "Test");
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("previous_success");
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testCancelSuccessForGroup() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("load_success");
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "cancel");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.group_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testCreateNotesForGroup() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("comment", "Notes created");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "create");
        addRequestParameter("comment", "Notes created");
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.group_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        group = (GroupBO) (StaticHibernateUtil.getSessionTL().get(GroupBO.class, new Integer(group.getCustomerId())));
        assertEquals(1, group.getRecentCustomerNotes().size());
        assertEquals(1, group.getCustomerNotes().size());
    }

    public void testSearchForGroup() throws Exception {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", group.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("comment", "Notes created");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "create");
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter("comment", "Notes created");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        StaticHibernateUtil.closeSession();
        setRequestPathInfo("/groupCustAction.do");
        addRequestParameter("method", "get");
        addRequestParameter("customerId", group.getCustomerId().toString());
        addRequestParameter("globalCustNum", group.getGlobalCustNum());
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "search");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        actionPerform();
        verifyForward("search_success");
        verifyNoActionErrors();
        verifyNoActionMessages();

        assertEquals("Size of the search result should be 1", 1, ((QueryResult) SessionUtils.getAttribute(
                Constants.SEARCH_RESULTS, request)).getSize());
        StaticHibernateUtil.closeSession();
        getobjects();
    }

    public void testCreate_CenterNotes() {
        createInitialObjects();
        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "load");
        addRequestParameter("customerId", center.getCustomerId().toString());
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "preview");
        addRequestParameter("comment", "Notes created");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();

        setRequestPathInfo("/customerNotesAction.do");
        addRequestParameter("method", "create");
        addRequestParameter("comment", "Notes created");
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.center_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        center = (CenterBO) (StaticHibernateUtil.getSessionTL()
                .get(CenterBO.class, new Integer(center.getCustomerId())));
        assertEquals(1, center.getCustomerNotes().size());
    }
    
    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
    }

}
