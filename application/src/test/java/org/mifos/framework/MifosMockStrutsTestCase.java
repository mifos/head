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

package org.mifos.framework;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionServlet;
import org.junit.After;
import org.junit.Before;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.builders.MifosUserBuilder;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.struts.ActionServlet30;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.security.MifosUser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import servletunit.HttpServletRequestSimulator;
import servletunit.HttpServletResponseSimulator;
import servletunit.ServletContextSimulator;
import servletunit.struts.MockStrutsTestCase;

/**
 * All classes extending this class must be names as <b>*StrutsTest.java</b> to support maven-surefire-plugin autofind
 * feature. <br />
 * <br />
 */
public class MifosMockStrutsTestCase extends MifosIntegrationTestCase {

    protected MockStruts mockStruts = new MockStruts();

    protected HttpServletRequest request;

    protected ServletContextSimulator context;

    private boolean strutsConfigSet = false;

    protected void setStrutsConfig() throws IOException {
        /*
         * Add a pointer to the context directory so that the web.xml file can be located when running test cases using
         * the junit plugin inside Eclipse.
         * 
         * Find the Web Resources dir (where WEB-INF lives) via Classpath, not hard-coded filenames.
         */
        Resource r = new ClassPathResource("META-INF/resources/WEB-INF/struts-config.xml");
        if (!r.exists() || !r.isReadable()) {
            fail(r.getDescription() + " does not exist or is not readable");
        }
        File webResourcesDirectory = r.getFile().getParentFile().getParentFile();
        mockStruts.setContextDirectory(webResourcesDirectory);

        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/other-struts-config.xml");

        /*
         * Because there is no more old-style web.xml as strutstest expects, we simply hard-code our ActionServlet
         * (actually our new ActionServlet30).
         * 
         * Because web.xml (now web-fragment.xml) isn't actually read, the ActionServlet is not initialized with servlet
         * init-params config for all /WEB-INF/*-struts-config.xml listed in web(-fragment).xml, nor are the
         * context-param read into config initParameter from web.xml by the MockStrutsTestCase. All Mifos *StrutsTest
         * don't seem to need that though, and pass with this.
         */
        mockStruts.setActionServlet(new ActionServlet30());

        request = mockStruts.getMockRequest();
        context = mockStruts.getMockContext();
    }

    protected void setConfigFile(String pathname) {
        mockStruts.setConfigFile(pathname);
    }

    @Before
    public void beforeStrutsTest() throws Exception {
        mockStruts.setUp();
        if (!strutsConfigSet) {
            setStrutsConfig();
            strutsConfigSet = true;
        }
        getActionServlet().getServletContext().setAttribute(ShutdownManager.class.getName(), new ShutdownManager());

        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @After
    public void afterStrutsTest() throws Exception {
        getActionServlet().getServletContext().removeAttribute(ShutdownManager.class.getName());
        doCleanUp(request);
        doCleanUp(request.getSession());
        mockStruts.tearDown();
        TestUtils.dereferenceObjects(this);
    }

    protected void addRequestDateParameter(String param, String dateStr) throws InvalidDateException {
        java.sql.Date date = DateUtils.getDateAsSentFromBrowser(dateStr);
        if (date != null) {
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);

            addRequestParameter(param + "DD", Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
            addRequestParameter(param + "MM", Integer.toString(cal.get(Calendar.MONTH) + 1));
            addRequestParameter(param + "YY", Integer.toString(cal.get(Calendar.YEAR)));
        } else {
            addRequestParameter(param + "DD", "");
            addRequestParameter(param + "MM", "");
            addRequestParameter(param + "YY", "");
        }
    }

    protected int getErrorSize(String field) {
        ActionErrors errors = (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
        return errors != null ? errors.size(field) : 0;
    }

    protected int getErrorSize() {
        ActionErrors errors = (ActionErrors) request.getAttribute(Globals.ERROR_KEY);
        return errors == null ? 0 : errors.size();
    }

    protected void matchValues(AuditLogRecord auditLogRecord, String oldValue, String newValue) {
        Assert.assertEquals(oldValue, auditLogRecord.getOldValue());
        Assert.assertEquals(newValue, auditLogRecord.getNewValue());
    }

    protected void doCleanUp(HttpSession session) {
        Enumeration<?> keys = session.getAttributeNames();
        String attributeKey = null;
        if (null != keys) {
            while (keys.hasMoreElements()) {

                attributeKey = (String) keys.nextElement();

                Object obj = session.getAttribute(attributeKey);
                session.removeAttribute(attributeKey);
                if (obj.getClass().getName().equals("java.util.ArrayList")) {

                    List<?> l = (List<?>) obj;

                    while (l.size() != 0) {
                        l.remove(0);
                    }
                }
            }
        }
    }

    protected void doCleanUp(HttpServletRequest request) {
        Enumeration<?> keys = request.getAttributeNames();
        String attributeKey = null;
        if (null != keys) {
            while (keys.hasMoreElements()) {
                attributeKey = (String) keys.nextElement();
                request.removeAttribute(attributeKey);
            }
        }
    }

    protected String createFlow(HttpServletRequest request, Class<?> flowClass) {
        Flow flow = new Flow();
        String flowKey = String.valueOf(System.currentTimeMillis());
        FlowManager flowManager = new FlowManager();
        flowManager.addFLow(flowKey, flow, flowClass.getName());
        request.getSession(false).setAttribute(Constants.FLOWMANAGER, flowManager);
        return flowKey;
    }

    protected String createFlowAndAddToRequest(Class<?> flowClass) {
        String key = createFlow(request, flowClass);
        request.setAttribute(Constants.CURRENTFLOWKEY, key);
        addRequestParameter(Constants.CURRENTFLOWKEY, key);
        return key;
    }

    protected void performNoErrors() {
        actionPerform();
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    protected void verifyNoActionMessages() {
        mockStruts.verifyNoActionMessages();
    }

    protected void verifyNoActionErrors() {
        mockStruts.verifyNoActionErrors();
    }

    protected void actionPerform() {
        mockStruts.actionPerform();
    }

    protected void setRequestPathInfo(String pathInfo) {
        mockStruts.setRequestPathInfo(pathInfo);
    }

    protected void verifyForward(String forwardName) {
        mockStruts.verifyForward(forwardName);
    }

    protected void verifyInputForward() {
        mockStruts.verifyInputForward();
    }

    protected void verifyActionErrors(String[] errorNames) {
        mockStruts.verifyActionErrors(errorNames);
    }

    protected HttpSession getSession() {
        return mockStruts.getSession();
    }

    protected void setActionForm(ActionForm form) {
        mockStruts.setActionForm(form);
    }

    protected HttpServletRequest getRequest() {
        return mockStruts.getRequest();
    }

    protected ActionServlet getActionServlet() {
        return mockStruts.getActionServlet();
    }

    protected void verifyForwardPath(String forwardPath) {
        mockStruts.verifyForwardPath(forwardPath);
    }

    protected ActionForm getActionForm() {
        return mockStruts.getActionForm();
    }

    protected HttpServletResponseSimulator getMockResponse() {
        return mockStruts.getMockResponse();
    }

    protected HttpServletRequestSimulator getMockRequest() {
        return mockStruts.getMockRequest();
    }

    protected void addRequestParameter(String parameterName, String parameterValue) {
        mockStruts.addRequestParameter(parameterName, parameterValue);
    }

    protected void addRequestParameter(String parameterName, String[] parameterValues) {
        mockStruts.addRequestParameter(parameterName, parameterValues);
    }

    protected void clearRequestParameters() {
        mockStruts.clearRequestParameters();
    }

    class MockStruts extends MockStrutsTestCase {
        public MockStruts() {
            super();
        }

        protected ServletContextSimulator getMockContext() {
            return context;
        }

        @Override
        public void setUp() throws Exception {
            super.setUp();
        }

        @Override
        public void tearDown() throws Exception {
            super.tearDown();
        }
    }
}
