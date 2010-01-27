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

package org.mifos.framework;

import java.io.File;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestCaseInitializer;
import org.mifos.framework.util.helpers.TestObjectFactory;

import servletunit.struts.MockStrutsTestCase;

public class MifosMockStrutsTestCase extends MockStrutsTestCase {

    protected MifosMockStrutsTestCase() throws Exception {
        super();
        new TestCaseInitializer().initialize();
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
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if (!strutsConfigSet) {
            setStrutsConfig();
            strutsConfigSet = true;
        }
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
        assertEquals(oldValue, auditLogRecord.getOldValue());
        assertEquals(newValue, auditLogRecord.getNewValue());
    }

    @Override
    protected void tearDown() throws Exception {
        doCleanUp(request);
        doCleanUp(request.getSession());
        TestObjectFactory.cleanUpTestObjects();
        super.tearDown();

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

    public MifosCurrency getCurrency() {
        // TODO: will be replaced by a way to get currency for mock struts tests
        return Money.getDefaultCurrency();
    }
}
