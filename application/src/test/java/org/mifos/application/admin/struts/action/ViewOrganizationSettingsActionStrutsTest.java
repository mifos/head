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

package org.mifos.application.admin.struts.action;

import junit.framework.Assert;

import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.UserContext;

public class ViewOrganizationSettingsActionStrutsTest extends MifosMockStrutsTestCase {
    public ViewOrganizationSettingsActionStrutsTest() throws Exception {
        super();
    }

    private String flowKey;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        flowKey = createFlow(request, ViewOrganizationSettingsAction.class);

        // no idea what the next three lines do besides make the test pass.
        // Copied them from AdminActionTest
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGet() throws Exception {
        setRequestPathInfo("/viewOrganizationSettingsAction");
        addRequestParameter("method", "get");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.load_success.toString());
        Assert.assertNull(SessionUtils.getAttribute(ViewOrganizationSettingsAction.ORGANIZATION_SETTINGS, request));
    }
}
