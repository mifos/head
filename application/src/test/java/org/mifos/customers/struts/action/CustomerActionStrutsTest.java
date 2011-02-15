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

package org.mifos.customers.struts.action;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class CustomerActionStrutsTest extends MifosMockStrutsTestCase {



    private UserContext userContext;
    private CustomerBO client;
    private CustomerBO group;
    private CustomerBO center;
    private String flowKey;

    @Override
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }

    @Before
    public void setUp() throws Exception {
        userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);

        flowKey = createFlow(request, CustomerAction.class);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
    }

    @After
    public void tearDown() throws Exception {
        client = null;
        group = null;
        center = null;
    }

    public void ignore_testForwardWaiveChargeDue() {
        createInitialObjects();
        setRequestPathInfo("/customerAction.do");
        addRequestParameter("method", "waiveChargeDue");
        addRequestParameter("type", "Client");
        AccountBO accountBO = client.getCustomerAccount();
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("waiveChargesDue_Success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    public void ignore_testForwardWaiveChargeOverDue() {
        createInitialObjects();
        setRequestPathInfo("/customerAction.do");
        addRequestParameter("method", "waiveChargeOverDue");
        addRequestParameter("type", "Client");
        AccountBO accountBO = client.getCustomerAccount();
        addRequestParameter("accountId", accountBO.getAccountId().toString());
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("waiveChargesOverDue_Success");
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    @Test
    public void testGetAllActivity() {
        createInitialObjects();
        setRequestPathInfo("/customerAction.do");
        addRequestParameter("method", "getAllActivity");
        addRequestParameter("type", "Client");
        addRequestParameter("globalCustNum", client.getGlobalCustNum());
        getRequest().getSession().setAttribute("security_param", "Client");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward("viewClientActivity");
        verifyNoActionErrors();
        verifyNoActionMessages();
        Assert.assertNotNull(request.getAttribute(Constants.CURRENTFLOWKEY));
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient(this.getClass().getSimpleName() + " Client", CustomerStatus.CLIENT_ACTIVE, group);
    }

}
