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

package org.mifos.application.holiday.struts.action;

import org.mifos.application.holiday.util.helpers.HolidayConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;

public class HolidayActionStrutsTest extends MifosMockStrutsTestCase {

    public HolidayActionStrutsTest() throws SystemException, ApplicationException {
        super();
    }

    private String flowKey;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        // addRequestParameter("recordLoanOfficerId", "1");
        // addRequestParameter("recordOfficeId", "1");
        // ActivityContext ac = new ActivityContext((short) 0, userContext
        // .getBranchId().shortValue(), userContext.getId().shortValue());
        // request.getSession(false).setAttribute("ActivityContext", ac);
        flowKey = createFlow(request, HolidayAction.class);
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testLoad() throws Exception {
        setRequestPathInfo("/holidayAction");
        addRequestParameter("method", "load");
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.load_success.toString());
        assertNotNull(SessionUtils.getAttribute(HolidayConstants.REPAYMENTRULETYPES, request)); // HOLIDAY_MASTERDATA
    }

    public void testGetHolidays() throws Exception {
        setRequestPathInfo("/holidayAction");
        addRequestParameter("method", "getHolidays");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyNoActionErrors();
        verifyForward(ActionForwards.get_success.toString());
        assertNull(SessionUtils.getAttribute("noOfYears", request));
    }

}
