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

package org.mifos.customers.center.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.customers.center.persistence.CenterPersistence;
import org.mifos.customers.center.util.helpers.CenterSearchResults;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.components.audit.business.AuditLog;
import org.mifos.framework.components.audit.business.AuditLogRecord;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterBOIntegrationTest extends MifosIntegrationTestCase {

    public CenterBOIntegrationTest() throws Exception {
        super();
    }

    private CenterBO center;

    private GroupBO group;

    private ClientBO client;

    private final Short officeId = 1;
    private OfficeBO officeBo;

    private final Short personnelId = 3;
    private PersonnelBO personnelBo;

    private MeetingBO meeting;

    private final OfficePersistence officePersistence = new OfficePersistence();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        officeBo = officePersistence.getOffice(officeId);
        personnelBo = new PersonnelPersistence().getPersonnel(personnelId);
    }

    @Override
    protected void tearDown() throws Exception {
        TestDatabase.resetMySQLDatabase();
        super.tearDown();
    }

    public void testSuccessfulUpdateForLogging() throws Exception {
        createCustomers();
        Date mfiJoiningDate = getDate("11/12/2005");
        String externalId = "1234";
        Address address = new Address();
        address.setCity("Bangalore");
        address.setLine1("Aditi");

        center.setUserContext(TestUtils.makeUserWithLocales());
        StaticHibernateUtil.getInterceptor().createInitialValueMap(center);
        center.update(TestUtils.makeUser(), personnelId, externalId, mfiJoiningDate, address, null, null);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());

        List<AuditLog> auditLogList = TestObjectFactory.getChangeLog(EntityType.CENTER, center.getCustomerId());
       Assert.assertEquals(1, auditLogList.size());
        AuditLog auditLog = auditLogList.get(0);

       Assert.assertEquals(EntityType.CENTER.getValue(), auditLog.getEntityType());
        Set<AuditLogRecord> records = auditLog.getAuditLogRecords();
       Assert.assertEquals(5, records.size());
        for (AuditLogRecord auditLogRecord : records) {
            String fieldName = auditLogRecord.getFieldName();
            if (fieldName.equalsIgnoreCase("Address1")) {
               Assert.assertEquals("-", auditLogRecord.getOldValue());
               Assert.assertEquals("Aditi", auditLogRecord.getNewValue());
            } else if (fieldName.equalsIgnoreCase("City/District")) {
               Assert.assertEquals("-", auditLogRecord.getOldValue());
               Assert.assertEquals("Bangalore", auditLogRecord.getNewValue());
            } else if (fieldName.equalsIgnoreCase("Loan Officer Assigned")) {
               Assert.assertEquals("mifos", auditLogRecord.getOldValue());
               Assert.assertEquals("loan officer", auditLogRecord.getNewValue());
            } else if (fieldName.equalsIgnoreCase("MFI Joining Date")) {
               Assert.assertEquals("-", auditLogRecord.getOldValue());
               Assert.assertEquals("11/12/2005", auditLogRecord.getNewValue());
            } else if (fieldName.equalsIgnoreCase("External Id")) {
               Assert.assertEquals("-", auditLogRecord.getOldValue());
               Assert.assertEquals("1234", auditLogRecord.getNewValue());
            } else {
                Assert.fail("unrecognized field name " + fieldName);
            }
        }

        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        TestObjectFactory.cleanUpChangeLog();
    }

    public void testCreateWithoutName() throws Exception {
        try {
            meeting = getMeeting();
            center = new CenterBO(TestUtils.makeUser(), "", null, null, null, null, null, officeBo, meeting,
                    personnelBo, new CustomerPersistence());
            Assert.fail();
        } catch (CustomerException ce) {
            Assert.assertNull(center);
           Assert.assertEquals(CustomerConstants.INVALID_NAME, ce.getKey());
        }
        TestObjectFactory.removeObject(meeting);
    }

    public void testCreateWithoutLO() throws Exception {
        try {
            meeting = getMeeting();
            center = new CenterBO(TestUtils.makeUser(), "Center", null, null, null, null, null,
                    officeBo, meeting, null, new CustomerPersistence());
            Assert.fail();
        } catch (CustomerException ce) {
            Assert.assertNull(center);
           Assert.assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, ce.getKey());
        }
        TestObjectFactory.removeObject(meeting);
    }

    public void testCreateWithoutMeeting() throws Exception {
        try {
            center = new CenterBO(TestUtils.makeUser(), "Center", null, null, null, null, null,
                    officeBo, meeting, personnelBo, new CustomerPersistence());
            Assert.fail();
        } catch (CustomerException ce) {
            Assert.assertNull(center);
           Assert.assertEquals(CustomerConstants.INVALID_MEETING, ce.getKey());
        }
    }

    public void testCreateWithoutOffice() throws Exception {
        try {
            meeting = getMeeting();
            center = new CenterBO(TestUtils.makeUser(), "Center", null, null, null, null, null,
                    null, meeting, personnelBo, new CustomerPersistence());
            Assert.fail();
        } catch (CustomerException ce) {
            Assert.assertNull(center);
           Assert.assertEquals(CustomerConstants.INVALID_OFFICE, ce.getKey());
        }
        TestObjectFactory.removeObject(meeting);
    }

    public void testSuccessfulCreateWithoutFeeAndCustomField() throws Exception {
        String name = "Center";
        meeting = getMeeting();
        center = new CenterBO(TestUtils.makeUser(), name, null, null, null, null, null, officeBo, meeting, personnelBo,
                new CustomerPersistence());
        new CenterPersistence().saveCenter(center);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
       Assert.assertEquals(name, center.getDisplayName());
       Assert.assertEquals(officeId, center.getOffice().getOfficeId());
    }

    public void testSuccessfulCreateWithoutFee() throws Exception {
        String name = "Center";
        meeting = getMeeting();
        center = new CenterBO(TestUtils.makeUser(), name, null, getCustomFields(), null, null, null, officeBo, meeting,
                personnelBo, new CustomerPersistence());
        new CenterPersistence().saveCenter(center);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
       Assert.assertEquals(name, center.getDisplayName());
       Assert.assertEquals(officeId, center.getOffice().getOfficeId());
       Assert.assertEquals(2, center.getCustomFields().size());
    }

    public void testFailureCreate_DuplicateName() throws Exception {
        String name = "Center";
        center = TestObjectFactory.createWeeklyFeeCenter(name, getMeeting());
        StaticHibernateUtil.closeSession();

        String externalId = "12345";
        Date mfiJoiningDate = getDate("11/12/2005");
        meeting = getMeeting();
        List<FeeView> fees = getFees();
        try {
            center = new CenterBO(TestUtils.makeUser(), name, null, null, fees, externalId, mfiJoiningDate, officeBo,
                    meeting, personnelBo, new CustomerPersistence());
            Assert.fail();
        } catch (CustomerException e) {
           Assert.assertEquals(CustomerConstants.ERRORS_DUPLICATE_CUSTOMER, e.getKey());
        }
        removeFees(fees);
    }

    public void testFailureDuplicateName() throws Exception {
        String name = "Center";
        center = TestObjectFactory.createWeeklyFeeCenter(name, getMeeting());
        StaticHibernateUtil.closeSession();

        String externalId = "12345";
        Date mfiJoiningDate = getDate("11/12/2005");
        meeting = getMeeting();
        UserContext userContext = TestUtils.makeUser();
        TestObjectFactory.simulateInvalidConnection();
        try {
            center = new CenterBO(userContext, name, null, null, null, externalId, mfiJoiningDate, officeBo, meeting,
                    personnelBo, new CustomerPersistence());
            Assert.fail();
        } catch (CustomerException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testSuccessfulCreate() throws Exception {
        String name = "Center";
        String externalId = "12345";
        Date mfiJoiningDate = getDate("11/12/2005");
        meeting = getMeeting();
        List<FeeView> fees = getFees();
        center = new CenterBO(TestUtils.makeUser(), name, null, getCustomFields(), fees, externalId, mfiJoiningDate,
                new OfficePersistence().getOffice(officeId), meeting, new PersonnelPersistence()
                        .getPersonnel(personnelId), new CustomerPersistence());
        new CenterPersistence().saveCenter(center);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
       Assert.assertEquals(name, center.getDisplayName());
       Assert.assertEquals(externalId, center.getExternalId());
       Assert.assertEquals(mfiJoiningDate, DateUtils.getDateWithoutTimeStamp(center.getMfiJoiningDate().getTime()));
       Assert.assertEquals(officeId, center.getOffice().getOfficeId());
       Assert.assertEquals(2, center.getCustomFields().size());
       Assert.assertEquals(AccountState.CUSTOMER_ACCOUNT_ACTIVE.getValue(), center.getCustomerAccount().getAccountState()
                .getId());
        // check if values in account fees are entered.
        Assert.assertNotNull(center.getCustomerAccount().getAccountFees(fees.get(0).getFeeIdValue()));
        Assert.assertNotNull(center.getCustomerAccount().getAccountFees(fees.get(1).getFeeIdValue()));

    }

    public void testUpdateFailure() throws Exception {
        createCustomers();
        try {
            center.update(TestUtils.makeUser(), null, "1234", null, null, null, null);
            Assert.fail();
        } catch (CustomerException ce) {
           Assert.assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, ce.getKey());
        }
    }

    public void testSuccessfulUpdate() throws Exception {
        createCustomers();
        Date mfiJoiningDate = getDate("11/12/2005");
        String city = "Bangalore";
        String addressLine1 = "Aditi";
        String externalId = "1234";
        Address address = new Address();
        address.setCity(city);
        address.setLine1(addressLine1);

        center.update(TestUtils.makeUser(), personnelId, externalId, mfiJoiningDate, address, null, null);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());

       Assert.assertEquals(city, center.getCustomerAddressDetail().getAddress().getCity());
       Assert.assertEquals(addressLine1, center.getCustomerAddressDetail().getAddress().getLine1());
       Assert.assertEquals(externalId, center.getExternalId());
       Assert.assertEquals(mfiJoiningDate, DateUtils.getDateWithoutTimeStamp(center.getMfiJoiningDate().getTime()));
       Assert.assertEquals(personnelId, center.getPersonnel().getPersonnelId());
    }

    public void testSuccessfulUpdateWithoutLO_in_InActiveState() throws Exception {
        createCustomers();
        client.changeStatus(CustomerStatus.CLIENT_CANCELLED, CustomerStatusFlag.CLIENT_CANCEL_WITHDRAW,
                "client cancelled");
        client.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group.changeStatus(CustomerStatus.GROUP_CANCELLED, CustomerStatusFlag.GROUP_CANCEL_WITHDRAW, "group cancelled");
        group.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        center.changeStatus(CustomerStatus.CENTER_INACTIVE, null, "Center_Inactive");
        center.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());

        StaticHibernateUtil.startTransaction();
        center.update(TestUtils.makeUser(), null, center.getExternalId(), center.getMfiJoiningDate(), center
                .getAddress(), null, null);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());

        Assert.assertNull(center.getPersonnel());
        Assert.assertNull(group.getPersonnel());
        Assert.assertNull(client.getPersonnel());
    }

    public void testUpdateLOsForAllChildren() throws Exception {
        createCustomers();
       Assert.assertEquals(center.getPersonnel().getPersonnelId(), group.getPersonnel().getPersonnelId());
       Assert.assertEquals(center.getPersonnel().getPersonnelId(), client.getPersonnel().getPersonnelId());
        PersonnelBO newLO = TestObjectFactory.getPersonnel(Short.valueOf("2"));
        StaticHibernateUtil.closeSession();
        StaticHibernateUtil.startTransaction();
        center.update(TestUtils.makeUser(), newLO.getPersonnelId(), center.getExternalId(), center.getMfiJoiningDate(),
                center.getAddress(), null, null);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
       Assert.assertEquals(newLO.getPersonnelId(), center.getPersonnel().getPersonnelId());
       Assert.assertEquals(newLO.getPersonnelId(), group.getPersonnel().getPersonnelId());
       Assert.assertEquals(newLO.getPersonnelId(), client.getPersonnel().getPersonnelId());
    }

    public void testUpdateMeeting_SaveToUpdateLater() throws Exception {
        createCustomers();
        String oldMeetingPlace = "Delhi";
        MeetingBO centerMeeting = center.getCustomerMeeting().getMeeting();
        String meetingPlace = "Bangalore";
        MeetingBO newMeeting = new MeetingBO(WeekDay.MONDAY, centerMeeting.getMeetingDetails().getRecurAfter(),
                centerMeeting.getStartDate(), MeetingType.CUSTOMER_MEETING, meetingPlace);
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        center.setUserContext(TestObjectFactory.getContext());
        center.updateMeeting(newMeeting);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());

       Assert.assertEquals(WeekDay.THURSDAY, center.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
       Assert.assertEquals(WeekDay.THURSDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
       Assert.assertEquals(WeekDay.THURSDAY, client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());

       Assert.assertEquals(WeekDay.MONDAY, center.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
       Assert.assertEquals(WeekDay.MONDAY, group.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());
       Assert.assertEquals(WeekDay.MONDAY, client.getCustomerMeeting().getUpdatedMeeting().getMeetingDetails().getWeekDay());

       Assert.assertEquals(oldMeetingPlace, center.getCustomerMeeting().getMeeting().getMeetingPlace());
       Assert.assertEquals(oldMeetingPlace, group.getCustomerMeeting().getMeeting().getMeetingPlace());
       Assert.assertEquals(oldMeetingPlace, client.getCustomerMeeting().getMeeting().getMeetingPlace());

       Assert.assertEquals(meetingPlace, center.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
       Assert.assertEquals(meetingPlace, group.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
       Assert.assertEquals(meetingPlace, client.getCustomerMeeting().getUpdatedMeeting().getMeetingPlace());
    }

    public void testUpdateMeeting_updateWithSaveMeeting() throws Exception {
        testUpdateMeeting_SaveToUpdateLater();
        Integer updatedMeeting = center.getCustomerMeeting().getUpdatedMeeting().getMeetingId();
        center.changeUpdatedMeeting();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());

       Assert.assertEquals(WeekDay.MONDAY, center.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
       Assert.assertEquals(WeekDay.MONDAY, group.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
       Assert.assertEquals(WeekDay.MONDAY, client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());

        Assert.assertNull(center.getCustomerMeeting().getUpdatedMeeting());
        Assert.assertNull(group.getCustomerMeeting().getUpdatedMeeting());
        Assert.assertNull(client.getCustomerMeeting().getUpdatedMeeting());

        MeetingBO meeting = new MeetingPersistence().getMeeting(updatedMeeting);
        Assert.assertNull(meeting);
    }

    public void testCenterSearchResultsView() {

        CenterSearchResults searchResults = new CenterSearchResults();
        searchResults.setCenterName("Center");
        searchResults.setCenterSystemId("1234");
        searchResults.setParentOfficeId(Short.valueOf("1"));
        searchResults.setParentOfficeName("BO");
       Assert.assertEquals("Center", searchResults.getCenterName());
       Assert.assertEquals("1234", searchResults.getCenterSystemId());
       Assert.assertEquals(Short.valueOf("1").shortValue(), searchResults.getParentOfficeId());
       Assert.assertEquals("BO", searchResults.getParentOfficeName());

    }

    public void testSearchIdOnlyUniquePerOffice() throws Exception {
        Date startDate = new Date();

        StaticHibernateUtil.startTransaction();

        // In real life, would be another branch rather than an area
        OfficeBO branch1 = new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_AREA_OFFICE);

        MeetingBO meeting = new MeetingBO(WeekDay.THURSDAY, (short) 1, startDate, MeetingType.CUSTOMER_MEETING, "Delhi");

        PersonnelBO systemUser = new PersonnelPersistence().getPersonnel(PersonnelConstants.SYSTEM_USER);
        center = new CenterBO(TestUtils.makeUser(), "Center", null, null, null, null,
                startDate, branch1, meeting, systemUser, new CustomerPersistence());
        StaticHibernateUtil.getSessionTL().save(center);

        CenterBO center2 = new CenterBO(TestUtils.makeUser(), "center2", null, null, null,
                null, startDate, new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE), meeting,
                systemUser, new CustomerPersistence());

        CenterBO sameBranch = new CenterBO(TestUtils.makeUser(), "sameBranch", null, null,
                null, null, startDate, branch1, meeting, systemUser, new CustomerPersistence());
        StaticHibernateUtil.getSessionTL().save(center);
        StaticHibernateUtil.commitTransaction();

       Assert.assertEquals("1.1", center.getSearchId());
       Assert.assertEquals("1.1", center2.getSearchId());
       Assert.assertEquals("1.2", sameBranch.getSearchId());
    }

    private void createCustomers() throws Exception {
        meeting = new MeetingBO(WeekDay.THURSDAY, TestObjectFactory.EVERY_WEEK, new Date(),
                MeetingType.CUSTOMER_MEETING, "Delhi");
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE,
                center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
    }

    private MeetingBO getMeeting() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return meeting;
    }

    private List<CustomFieldView> getCustomFields() {
        List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
        fields.add(new CustomFieldView(Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC));
        fields.add(new CustomFieldView(Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC));
        return fields;
    }

    private List<FeeView> getFees() {
        List<FeeView> fees = new ArrayList<FeeView>();
        AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee",
                FeeCategory.CENTER, "200", RecurrenceType.WEEKLY, Short.valueOf("2"));
        AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory.createOneTimeAmountFee("OneTimeAmountFee",
                FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
        fees.add(new FeeView(TestObjectFactory.getContext(), fee1));
        fees.add(new FeeView(TestObjectFactory.getContext(), fee2));
        StaticHibernateUtil.commitTransaction();
        return fees;
    }

    private void removeFees(List<FeeView> feesToRemove) {
        for (FeeView fee : feesToRemove) {
            TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
        }
    }

}
