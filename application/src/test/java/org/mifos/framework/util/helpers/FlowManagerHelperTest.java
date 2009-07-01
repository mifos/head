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

package org.mifos.framework.util.helpers;

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;

public class FlowManagerHelperTest extends MifosMockStrutsTestCase {

    public FlowManagerHelperTest() throws SystemException, ApplicationException {
        super();
    }

    private FlowManagerHelper flowManagerHelper = null;

    private String flowKey = "";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestUtils.makeUserWithLocales();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
        flowKey = createFlow(request, FlowManagerHelperTest.class);
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        flowManagerHelper = new FlowManagerHelper();
        SessionUtils.setAttribute("test", "test", request);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetFlow() {
        FlowManager manager = (FlowManager) SessionUtils.getAttribute(Constants.FLOWMANAGER, request.getSession());
        Flow flow = (Flow) flowManagerHelper.getFlow(manager, flowKey);
        assertEquals("test", (String) flow.getObjectFromSession("test"));
    }

    public void testGetFlowFlowManagerString() {
        FlowManager manager = (FlowManager) SessionUtils.getAttribute(Constants.FLOWMANAGER, request.getSession());
        assertEquals("test", (String) flowManagerHelper.getFromSession(manager, flowKey, "test"));
        addRequestParameter(Constants.CURRENTFLOWKEY, "");
        assertNull(flowManagerHelper.getFromSession(manager, null, "test"));
    }
}
