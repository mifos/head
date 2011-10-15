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

package org.mifos.customers.center.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.customers.center.persistence.CenterPersistence;
import org.mifos.customers.center.util.helpers.CenterSearchResultsDto;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterBOIntegrationTest extends MifosIntegrationTestCase {

    private CenterBO center;
    private final Short officeId = 1;
    private OfficeBO officeBo;

    private final Short personnelId = 3;
    private PersonnelBO personnelBo;

    private MeetingBO meeting;

    private final OfficePersistence officePersistence = new OfficePersistence();

    @Before
    public void setUp() throws Exception {
        officeBo = officePersistence.getOffice(officeId);
        personnelBo = legacyPersonnelDao.getPersonnel(personnelId);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
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

    @Test
    public void testSuccessfulCreateWithoutFeeAndCustomField() throws Exception {
        String name = "Center";
        meeting = getMeeting();
        center = new CenterBO(TestUtils.makeUser(), name, null, null, null, null, null, officeBo, meeting, personnelBo,
                new CustomerPersistence());
        new CenterPersistence().saveCenter(center);
        StaticHibernateUtil.flushSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
       Assert.assertEquals(name, center.getDisplayName());
       Assert.assertEquals(officeId, center.getOffice().getOfficeId());
    }

    @Test
    public void testSuccessfulCreateWithoutFee() throws Exception {
        String name = "Center";
        meeting = getMeeting();
        center = new CenterBO(TestUtils.makeUser(), name, null, getCustomFields(), null, null, null, officeBo, meeting,
                personnelBo, new CustomerPersistence());
        new CenterPersistence().saveCenter(center);
        StaticHibernateUtil.flushSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
       Assert.assertEquals(name, center.getDisplayName());
       Assert.assertEquals(officeId, center.getOffice().getOfficeId());
       Assert.assertEquals(2, center.getCustomFields().size());
    }

    @Test
    public void testSuccessfulCreate() throws Exception {
        String name = "Center";
        String externalId = "12345";
        Date mfiJoiningDate = getDate("11/12/2005");
        meeting = getMeeting();
        List<FeeDto> fees = getFees();
        center = new CenterBO(TestUtils.makeUser(), name, null, getCustomFields(), fees, externalId, mfiJoiningDate,
                new OfficePersistence().getOffice(officeId), meeting, legacyPersonnelDao
                        .getPersonnel(personnelId), new CustomerPersistence());
        new CenterPersistence().saveCenter(center);
        StaticHibernateUtil.flushSession();
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

    @Test
    public void testCenterSearchResultsView() {

        CenterSearchResultsDto searchResults = new CenterSearchResultsDto();
        searchResults.setCenterName("Center");
        searchResults.setCenterSystemId("1234");
        searchResults.setParentOfficeId(Short.valueOf("1"));
        searchResults.setParentOfficeName("BO");
       Assert.assertEquals("Center", searchResults.getCenterName());
       Assert.assertEquals("1234", searchResults.getCenterSystemId());
       Assert.assertEquals(Short.valueOf("1").shortValue(), searchResults.getParentOfficeId());
       Assert.assertEquals("BO", searchResults.getParentOfficeName());

    }

    @Test
    public void testSearchIdOnlyUniquePerOffice() throws Exception {
        Date startDate = new Date();

        StaticHibernateUtil.startTransaction();

        // In real life, would be another branch rather than an area
        OfficeBO branch1 = new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_AREA_OFFICE);

        MeetingBO meeting = new MeetingBO(WeekDay.THURSDAY, (short) 1, startDate, MeetingType.CUSTOMER_MEETING, "Delhi");

        PersonnelBO systemUser = legacyPersonnelDao.getPersonnel(PersonnelConstants.SYSTEM_USER);
        center = new CenterBO(TestUtils.makeUser(), "Center", null, null, null, null,
                startDate, branch1, meeting, systemUser, new CustomerPersistence());
        StaticHibernateUtil.getSessionTL().save(center);

        CenterBO center2 = new CenterBO(TestUtils.makeUser(), "center2", null, null, null,
                null, startDate, new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE), meeting,
                systemUser, new CustomerPersistence());

        CenterBO sameBranch = new CenterBO(TestUtils.makeUser(), "sameBranch", null, null,
                null, null, startDate, branch1, meeting, systemUser, new CustomerPersistence());
        StaticHibernateUtil.getSessionTL().save(center);
        StaticHibernateUtil.flushSession();

       Assert.assertEquals("1.1", center.getSearchId());
       Assert.assertEquals("1.1", center2.getSearchId());
       Assert.assertEquals("1.2", sameBranch.getSearchId());
    }

    private MeetingBO getMeeting() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return meeting;
    }

    private List<CustomFieldDto> getCustomFields() {
        List<CustomFieldDto> fields = new ArrayList<CustomFieldDto>();
        fields.add(new CustomFieldDto(Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC.getValue()));
        fields.add(new CustomFieldDto(Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC.getValue()));
        return fields;
    }

    private List<FeeDto> getFees() {
        List<FeeDto> fees = new ArrayList<FeeDto>();
        AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee",
                FeeCategory.CENTER, "200", RecurrenceType.WEEKLY, Short.valueOf("2"));
        AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory.createOneTimeAmountFee("OneTimeAmountFee",
                FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
        fees.add(new FeeDto(TestObjectFactory.getContext(), fee1));
        fees.add(new FeeDto(TestObjectFactory.getContext(), fee2));
        StaticHibernateUtil.flushSession();
        return fees;
    }
}