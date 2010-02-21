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

import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.framework.security.util.UserContext;
import org.mifos.application.admin.system.ShutdownManager;
import junit.framework.Assert;


public class ShutdownActionStrutsTest extends MifosMockStrutsTestCase {
    public ShutdownActionStrutsTest() throws Exception {
        super();
    }

    private String flowKey;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        flowKey = createFlow(request, ShutdownAction.class);
        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testVerifyAdminForward() throws PageExpiredException {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/shutdownAction.do");
        addRequestParameter("method", "load");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForwardPath("/pages/application/admin/jsp/shutdown.jsp");
        Assert.assertNotNull(SessionUtils.getAttribute("shutdownManager", request.getSession()));
        Assert.assertFalse(ShutdownManager.isShutdownInProgress());
    }

    public void testVerifyShutdownCancel() throws PageExpiredException {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/shutdownAction.do");
        addRequestParameter("method", "shutdown");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForwardPath("/pages/application/admin/jsp/shutdown.jsp");
        Assert.assertNotNull(SessionUtils.getAttribute("shutdownManager", request.getSession()));
        Assert.assertTrue(ShutdownManager.isShutdownInProgress());

        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        setRequestPathInfo("/shutdownAction.do");
        addRequestParameter("method", "cancelShutdown");
        addRequestParameter(Constants.CURRENTFLOWKEY, (String) request.getAttribute(Constants.CURRENTFLOWKEY));
        performNoErrors();
        verifyForwardPath("/pages/application/admin/jsp/shutdown.jsp");
        Assert.assertNotNull(SessionUtils.getAttribute("shutdownManager", request.getSession()));
        Assert.assertFalse(ShutdownManager.isShutdownInProgress());
    }
}
