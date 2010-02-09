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

package org.mifos.framework.components.audit.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.components.audit.util.helpers.AuditLogView;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AuditInterceptorIntegrationTest extends MifosIntegrationTestCase {

    public AuditInterceptorIntegrationTest() throws Exception {
        super();
    }

    protected AccountBO accountBO = null;

    protected CustomerBO center = null;

    protected CustomerBO group = null;


    /*
     * Note: since this loan is active the disbursement date will not be updated
     */
    public void testUpdateLoanForLogging() throws Exception {
        Date newDate = incrementCurrentDate(14);
        accountBO = getLoanAccount();
        accountBO.setUserContext(TestUtils.makeUser());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(accountBO);
        LoanBO loanBO = ((LoanBO) accountBO);
        ((LoanBO) accountBO).updateLoan(true, loanBO.getLoanAmount(), loanBO.getInterestRate(), loanBO
                .getNoOfInstallments(), newDate, (short) 2, TestObjectFactory.SAMPLE_BUSINESS_ACTIVITY_2, "Added note",
                null, null, false, null, null);

        StaticHibernateUtil.commitTransaction();
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());

        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.LOAN, accountBO.getAccountId());
       Assert.assertEquals(1, auditLogList.size());
       Assert.assertEquals(EntityType.LOAN.getValue(), auditLogList.get(0).getEntityType());
       Assert.assertEquals(3, auditLogList.get(0).getAuditLogRecords().size());
        for (AuditLogRecord auditLogRecord : auditLogList.get(0).getAuditLogRecords()) {
            if (auditLogRecord.getFieldName().equalsIgnoreCase("Collateral Notes")) {
               Assert.assertEquals("-", auditLogRecord.getOldValue());
               Assert.assertEquals("Added note", auditLogRecord.getNewValue());
            } else if (auditLogRecord.getFieldName().equalsIgnoreCase("Service Charge deducted At Disbursement")) {
               Assert.assertEquals("1", auditLogRecord.getOldValue());
               Assert.assertEquals("0", auditLogRecord.getNewValue());
            }
        }
        TestObjectFactory.cleanUpChangeLog();
        TestObjectFactory.cleanUp(accountBO);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
    }

    public void testAuditLogView() {
        AuditLogView auditLogView = new AuditLogView();
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        auditLogView.setDate(date.toString());
        auditLogView.setField("field");
        // auditLogView.setMfiLocale(new Locale("1"));
        auditLogView.setNewValue("new value");
        auditLogView.setOldValue("old value");
        auditLogView.setUser("user");
       Assert.assertEquals("value of date", new Date(currentTime).toString(), auditLogView.getDate());
       Assert.assertEquals("value of field", "field", auditLogView.getField());
        //Assert.assertEquals("value of Locale", new Locale("1"), auditLogView
        // .getMfiLocale());
       Assert.assertEquals("value of new value", "new value", auditLogView.getNewValue());
       Assert.assertEquals("value of old value", "old value", auditLogView.getOldValue());
       Assert.assertEquals("value of user", "user", auditLogView.getUser());
    }

    private Date incrementCurrentDate(int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day + noOfDays);
        return DateUtils.getDateWithoutTimeStamp(currentDateCalendar.getTimeInMillis());
    }

    private AccountBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        createInitialCustomers();
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, center.getCustomerMeeting()
                .getMeeting());
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
    }

    private void createInitialCustomers() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
    }
}
