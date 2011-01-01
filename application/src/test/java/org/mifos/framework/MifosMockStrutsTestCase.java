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

package org.mifos.framework;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionServlet;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.domain.builders.MifosUserBuilder;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.DatabaseDependentTest;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.*;
import org.mifos.security.MifosUser;
import org.mifos.service.test.TestMode;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import servletunit.HttpServletRequestSimulator;
import servletunit.HttpServletResponseSimulator;
import servletunit.ServletContextSimulator;
import servletunit.struts.MockStrutsTestCase;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * All classes extending this class must be names as <b>*StrutsTest.java</b> to support maven-surefire-plugin autofind
 * feature.
 * <br />
 * <br />
 */
public class MifosMockStrutsTestCase extends TestCase {

    private static boolean isTestingModeSet = false;

    private static String savedFiscalCalendarRulesWorkingDays;

    protected MockStruts mockSturts = new MockStruts();

    protected HttpServletRequest request;

    protected ServletContextSimulator context;

    protected MifosMockStrutsTestCase() throws Exception {
        super();
        if (!isTestingModeSet) {
            new StandardTestingService().setTestMode(TestMode.INTEGRATION);
            SpringTestUtil.initializeSpring();
            new TestCaseInitializer().initialize();
            isTestingModeSet = true;
        }
    }

    private boolean strutsConfigSet = false;

    protected void setStrutsConfig() {
        /*
         * Add a pointer to the context directory so that the web.xml file can
         * be located when running test cases using the junit plugin inside
         * eclipse.
         */
        setContextDirectory(new File("application/target/test-classes"));

        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/other-struts-config.xml");

        request = mockSturts.getMockRequest();
        context = mockSturts.getMockContext();
    }

    protected void setConfigFile(String pathname) {
        mockSturts.setConfigFile(pathname);
    }

    protected void setUp() throws Exception {
        mockSturts.setUp();
        if (!strutsConfigSet) {
            setStrutsConfig();
            strutsConfigSet = true;
        }
        DatabaseDependentTest.before();
        getActionServlet().getServletContext().setAttribute(ShutdownManager.class.getName(), new ShutdownManager());

        SecurityContext securityContext = new SecurityContextImpl();
        MifosUser principal = new MifosUserBuilder().build();
        Authentication authentication = new TestingAuthenticationToken(principal, principal);
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
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

    protected void tearDown() throws Exception {
        getActionServlet().getServletContext().removeAttribute(ShutdownManager.class.getName());
        doCleanUp(request);
        doCleanUp(request.getSession());
        diableCustomWorkingDays();
        DatabaseDependentTest.after();
        mockSturts.tearDown();
        TestUtils.dereferenceObjects(this);
    }

    protected void doCleanUp(HttpSession session) {
        Enumeration keys = session.getAttributeNames();
        String attributeKey = null;
        if (null != keys) {
            while (keys.hasMoreElements()) {

                attributeKey = (String) keys.nextElement();

                Object obj = session.getAttribute(attributeKey);
                session.removeAttribute(attributeKey);
                if (obj.getClass().getName().equals("java.util.ArrayList")) {

                    List l = (List) obj;

                    while (l.size() != 0) {
                        l.remove(0);
                    }
                }
            }// end-while
        }// end-if
        session = null;
    }// end-doCleanUp

    protected void doCleanUp(HttpServletRequest request) {
        Enumeration keys = request.getAttributeNames();
        String attributeKey = null;
        if (null != keys) {
            while (keys.hasMoreElements()) {
                attributeKey = (String) keys.nextElement();
                request.removeAttribute(attributeKey);
            }// end-while
        }// end-if
        request = null;
    }// end-doCleanUp

    protected String createFlow(HttpServletRequest request, Class flowClass) {
        Flow flow = new Flow();
        String flowKey = String.valueOf(System.currentTimeMillis());
        FlowManager flowManager = new FlowManager();
        flowManager.addFLow(flowKey, flow, flowClass.getName());
        request.getSession(false).setAttribute(Constants.FLOWMANAGER, flowManager);
        return flowKey;
    }

    protected String createFlowAndAddToRequest(Class flowClass) {
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
        mockSturts.verifyNoActionMessages();
    }

    protected void verifyNoActionErrors() {
        mockSturts.verifyNoActionErrors();
    }

    protected void actionPerform() {
        mockSturts.actionPerform();
    }

    protected void setRequestPathInfo(String pathInfo) {
        mockSturts.setRequestPathInfo(pathInfo);
    }

    protected void verifyForward(String forwardName) {
        mockSturts.verifyForward(forwardName);
    }

    protected void verifyInputForward() {
        mockSturts.verifyInputForward();
    }

    protected void verifyActionErrors(String[] errorNames) {
        mockSturts.verifyActionErrors(errorNames);
    }

    protected HttpSession getSession() {
        return mockSturts.getSession();
    }

    protected void setActionForm(ActionForm form) {
        mockSturts.setActionForm(form);
    }

    protected HttpServletRequest getRequest() {
        return mockSturts.getRequest();
    }

    protected ActionServlet getActionServlet() {
        return mockSturts.getActionServlet();
    }

    protected void verifyForwardPath(String forwardPath) {
        mockSturts.verifyForwardPath(forwardPath);
    }

    protected ActionForm getActionForm() {
        return mockSturts.getActionForm();
    }

    protected HttpServletResponseSimulator getMockResponse() {
        return mockSturts.getMockResponse();
    }

    protected HttpServletRequestSimulator getMockRequest() {
        return mockSturts.getMockRequest();
    }

    protected void addRequestParameter(String parameterName, String parameterValue) {
        mockSturts.addRequestParameter(parameterName, parameterValue);
    }

    protected void addRequestParameter(String parameterName, String[] parameterValues) {
        mockSturts.addRequestParameter(parameterName, parameterValues);
    }

    protected void clearRequestParameters() {
        mockSturts.clearRequestParameters();
    }
    /**
     * see MIFOS-2659 <br><br>
     * This will be disabled automatically at the end of a test case
     *
     */
    protected static void enableCustomWorkingDays() {
        savedFiscalCalendarRulesWorkingDays = new FiscalCalendarRules().getWorkingDaysAsString();
        new FiscalCalendarRules().setWorkingDays("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY");
    }

    /**
     * see MIFOS-2659
     */
    private static void diableCustomWorkingDays() {
        if(savedFiscalCalendarRulesWorkingDays != null) {
        new FiscalCalendarRules().setWorkingDays(savedFiscalCalendarRulesWorkingDays);
        }
        savedFiscalCalendarRulesWorkingDays = null;
    }

    public MifosCurrency getCurrency() {
        // TODO: will be replaced by a way to get currency for mock struts tests
        return Money.getDefaultCurrency();
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
