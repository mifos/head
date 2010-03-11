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

package org.mifos.application.admin.struts.action;

import junit.framework.Assert;

import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ServletUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class ShutdownActionStrutsTest extends MifosMockStrutsTestCase {
    public ShutdownActionStrutsTest() throws Exception {
        super();
    }

    private String flowKey;
    private ShutdownManager shutdownManager;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        flowKey = createFlow(request, ShutdownAction.class);
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
        shutdownManager = (ShutdownManager) ServletUtils.getGlobal(request, ShutdownManager.class.getName());
    }

    @Override
    protected void tearDown() throws Exception {
        shutdownManager = null;
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testVerifyAdminForward() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/shutdownAction.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForwardPath("/pages/application/admin/jsp/shutdown.jsp");
        Assert.assertFalse(shutdownManager.isShutdownInProgress());
    }

    public void testVerifyShutdownCancel() {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/shutdownAction.do");
        addRequestParameter("method", "shutdown");
        addRequestParameter("shutdownTimeout", "600");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForwardPath("/pages/application/admin/jsp/shutdown.jsp");

        Assert.assertTrue(shutdownManager.isShutdownInProgress());

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/shutdownAction.do");
        addRequestParameter("method", "cancelShutdown");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForwardPath("/pages/application/admin/jsp/shutdown.jsp");

        Assert.assertFalse(shutdownManager.isShutdownInProgress());
    }
}
