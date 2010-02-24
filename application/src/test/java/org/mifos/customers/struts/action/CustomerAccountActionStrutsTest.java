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

package org.mifos.customers.struts.action;

import junit.framework.Assert;

import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerAccountActionStrutsTest extends MifosMockStrutsTestCase {

    public CustomerAccountActionStrutsTest() throws Exception {
        super();
    }

    private ClientBO client;

    private GroupBO group;

    private CenterBO center;

    private MeetingBO meeting;

    private String flowKey;

    private UserContext userContext;

    @Override 
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }
        
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        flowKey = createFlow(request, CustomerAccountAction.class);
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

    public void testLoadClientChargesDetails_client() {
        initialization("Client");
        addRequestParameter("globalCustNum", client.getGlobalCustNum());
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.client_detail_page.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testLoadClientChargesDetails_group() {
        initialization("Group");
        addRequestParameter("globalCustNum", group.getGlobalCustNum());
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.group_detail_page.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void testLoadClientChargesDetails_center() {
        initialization("Center");
        addRequestParameter("globalCustNum", center.getGlobalCustNum());
        getRequest().getSession().setAttribute("security_param", "Center");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.center_detail_page.toString());
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    private void initialization(String customer) {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());

        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        if (!(customer == "Center"))
            group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        if (!(customer == "Center" || customer == "Group"))
            client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        setPath();
    }

    private void setPath() {
        setRequestPathInfo("/customerAccountAction.do");
        addRequestParameter("method", "load");
    }

}
