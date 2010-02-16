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

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

import junit.framework.Assert;

import org.mifos.customers.business.CustomerHistoricalDataEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustHistoricalDataActionStrutsTest extends MifosMockStrutsTestCase {

    public CustHistoricalDataActionStrutsTest() throws Exception {
        super();
    }

    private ClientBO client;

    private GroupBO group;

    private CenterBO center;

    private MeetingBO meeting;

    private String flowKey;

    @Override 
    protected void setStrutsConfig() {
        super.setStrutsConfig();
        setConfigFile("/WEB-INF/struts-config.xml,/WEB-INF/customer-struts-config.xml");
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestObjectFactory.getContext();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        addRequestParameter("recordLoanOfficerId", "1");
        addRequestParameter("recordOfficeId", "1");

        request.getSession(false).setAttribute("ActivityContext", TestObjectFactory.getActivityContext());

        flowKey = createFlow(request, CustHistoricalDataAction.class);
    }

    @Override
    public void tearDown() throws Exception {
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetHistoricalDataWhenCustHistoricalDataIsNull() throws Exception {
        createInitialObjects();
        setRequestPathInfo("/custHistoricalDataAction.do");
        addRequestParameter("method", "getHistoricalData");
        addRequestParameter("globalCustNum", group.getGlobalCustNum());
        getRequest().getSession().setAttribute("security_param", "Group");
        actionPerform();
        verifyForward(ActionForwards.getHistoricalData_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
       Assert.assertEquals(new java.sql.Date(DateUtils.getCurrentDateWithoutTimeStamp().getTime()).toString(), SessionUtils
                .getAttribute(CustomerConstants.MFIJOININGDATE, request).toString());
    }

    public void testGetHistoricalDataWhenCustHistoricalDataIsNotNull() throws Exception {
        createInitialObjects();
        CustomerHistoricalDataEntity customerHistoricalDataEntity = new CustomerHistoricalDataEntity(group);
        customerHistoricalDataEntity.setMfiJoiningDate(offSetCurrentDate(10));
        Date mfiDate = new Date(customerHistoricalDataEntity.getMfiJoiningDate().getTime());
        group.updateHistoricalData(customerHistoricalDataEntity);
        group.update();
        StaticHibernateUtil.commitTransaction();
       Assert.assertEquals(mfiDate, new Date(group.getMfiJoiningDate().getTime()));
        setRequestPathInfo("/custHistoricalDataAction.do");
        addRequestParameter("method", "getHistoricalData");
        addRequestParameter("globalCustNum", group.getGlobalCustNum());
        getRequest().getSession().setAttribute("security_param", "Group");
        actionPerform();
        verifyForward(ActionForwards.getHistoricalData_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
       Assert.assertEquals(new java.sql.Date(DateUtils.getDateWithoutTimeStamp(mfiDate.getTime()).getTime()).toString(),
                SessionUtils.getAttribute(CustomerConstants.MFIJOININGDATE, request).toString());
    }

    public void testLoadHistoricalDataWhenCustHistoricalDataIsNull() {
        createInitialObjects();
        setRequestPathInfo("/custHistoricalDataAction.do");
        addRequestParameter("method", "getHistoricalData");
        addRequestParameter("globalCustNum", group.getGlobalCustNum());
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        verifyForward(ActionForwards.getHistoricalData_success.toString());

        setRequestPathInfo("/custHistoricalDataAction.do");
        addRequestParameter("method", "loadHistoricalData");
        getRequest().getSession().setAttribute("security_param", "Group");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.loadHistoricalData_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testPreviewHistoricalData() {
        createInitialObjects();
        setRequestPathInfo("/custHistoricalDataAction.do");
        addRequestParameter("method", "previewHistoricalData");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.previewHistoricalData_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testPreviewHistoricalDataLargeNotes() {
        createInitialObjects();
        setRequestPathInfo("/custHistoricalDataAction.do");
        addRequestParameter("method", "previewHistoricalData");
        addRequestParameter("commentNotes", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyInputForward();
        verifyActionErrors(new String[] { CustomerConstants.MAXIMUM_LENGTH });
    }

    public void testPreviousHistoricalData() {
        createInitialObjects();
        setRequestPathInfo("/custHistoricalDataAction.do");
        addRequestParameter("method", "previousHistoricalData");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.previousHistoricalData_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testCancelHistoricalData() {
        createInitialObjects();
        setRequestPathInfo("/custHistoricalDataAction.do");
        addRequestParameter("method", "cancelHistoricalData");
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        addRequestParameter("type", "Group");
        actionPerform();
        verifyForward(ActionForwards.group_detail_page.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
    }

    public void testUpdateHistoricalDataWhenCustHistoricalDataIsNull() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createInitialObjects();
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
        setRequestPathInfo("/custHistoricalDataAction.do");
        addRequestParameter("method", "updateHistoricalData");
        addRequestParameter("productName", "Test");
        addRequestParameter("loanAmount", "100");
        addRequestParameter("totalAmountPaid", "50");
        addRequestParameter("interestPaid", "10");
        addRequestParameter("missedPaymentsCount", "1");
        addRequestParameter("totalPaymentsCount", "2");
        addRequestParameter("commentNotes", "Test notes");
        addRequestParameter("loanCycleNumber", "1");
        addRequestParameter("type", "Group");
        addRequestParameter("mfiJoiningDate", DateUtils.getCurrentDate(((UserContext) request.getSession()
                .getAttribute("UserContext")).getPreferredLocale()));
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.updateHistoricalData_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        group = TestObjectFactory.getGroup(group.getCustomerId());
       Assert.assertEquals("Test", group.getHistoricalData().getProductName());
       Assert.assertEquals("Test notes", group.getHistoricalData().getNotes());
       Assert.assertEquals(new Money(getCurrency(), "100"), group.getHistoricalData().getLoanAmount());
       Assert.assertEquals(new Money(getCurrency(), "50"), group.getHistoricalData().getTotalAmountPaid());
       Assert.assertEquals(new Money(getCurrency(), "10"), group.getHistoricalData().getInterestPaid());
       Assert.assertEquals(1, group.getHistoricalData().getMissedPaymentsCount().intValue());
       Assert.assertEquals(2, group.getHistoricalData().getTotalPaymentsCount().intValue());
       Assert.assertEquals(1, group.getHistoricalData().getLoanCycleNumber().intValue());
    }

    public void testUpdateHistoricalDataWhenCustHistoricalDataIsNotNull() throws Exception {
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);
        createInitialObjects();
        CustomerHistoricalDataEntity customerHistoricalDataEntity = new CustomerHistoricalDataEntity(group);
        group.updateHistoricalData(customerHistoricalDataEntity);
        group.update();
        StaticHibernateUtil.commitTransaction();

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
        setRequestPathInfo("/custHistoricalDataAction.do");
        addRequestParameter("method", "updateHistoricalData");
        addRequestParameter("productName", "Test");
        addRequestParameter("loanAmount", "200");
        addRequestParameter("totalAmountPaid", "150");
        addRequestParameter("interestPaid", "50");
        addRequestParameter("missedPaymentsCount", "2");
        addRequestParameter("totalPaymentsCount", "3");
        addRequestParameter("commentNotes", "Test notes");
        addRequestParameter("loanCycleNumber", "2");
        addRequestParameter("type", "Group");
        addRequestParameter("mfiJoiningDate", DateUtils.getCurrentDate(((UserContext) request.getSession()
                .getAttribute("UserContext")).getPreferredLocale()));
        addRequestParameter(Constants.CURRENTFLOWKEY, flowKey);
        actionPerform();
        verifyForward(ActionForwards.updateHistoricalData_success.toString());
        verifyNoActionErrors();
        verifyNoActionMessages();
        group = TestObjectFactory.getGroup(group.getCustomerId());
       Assert.assertEquals("Test", group.getHistoricalData().getProductName());
       Assert.assertEquals("Test notes", group.getHistoricalData().getNotes());
       Assert.assertEquals(new Money(getCurrency(), "200"), group.getHistoricalData().getLoanAmount());
       Assert.assertEquals(new Money(getCurrency(), "150"), group.getHistoricalData().getTotalAmountPaid());
       Assert.assertEquals(new Money(getCurrency(), "50"), group.getHistoricalData().getInterestPaid());
       Assert.assertEquals(2, group.getHistoricalData().getMissedPaymentsCount().intValue());
       Assert.assertEquals(3, group.getHistoricalData().getTotalPaymentsCount().intValue());
       Assert.assertEquals(2, group.getHistoricalData().getLoanCycleNumber().intValue());
    }

    private void createInitialObjects() {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient(this.getClass().getSimpleName() + " Client", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private java.sql.Date offSetCurrentDate(int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }
}
