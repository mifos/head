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

package org.mifos.customers.group.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupBOTransferIntegrationTest extends MifosIntegrationTestCase {

    public GroupBOTransferIntegrationTest() throws Exception {
        super();
    }

    private AccountBO account1 = null;
    private AccountBO account2 = null;

    private CenterBO center;
    private CenterBO center1 = null;

    private GroupBO group;
    private GroupBO group1;
    private GroupBO group2;
    private GroupBO group3;

    private ClientBO client;
    private ClientBO client1 = null;
    private ClientBO client2 = null;
    private ClientBO client3 = null;

    private MeetingBO meeting;

    private OfficeBO officeBO;
    private OfficeBO officeBO1;

    private SavingsTestHelper helper = new SavingsTestHelper();

    private SavingsOfferingBO savingsOffering;

    private Short officeId3 = 3;

    private Short officeId1 = 1;
    private OfficeBO officeBo1;

    private Short personnelId = 3;
    private PersonnelBO personnelBo;

    CustomerPersistence customerPersistence = new CustomerPersistence();
    PersonnelPersistence personnelPersistence = new PersonnelPersistence();
    MasterPersistence masterPersistence = new MasterPersistence();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        personnelBo = personnelPersistence.getPersonnel(personnelId);
        officeBo1 = new OfficePersistence().getOffice(officeId1);
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(account2);
            TestObjectFactory.cleanUp(account1);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(client1);
            TestObjectFactory.cleanUp(client2);
            TestObjectFactory.cleanUp(client3);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(group1);
            TestObjectFactory.cleanUp(group2);
            TestObjectFactory.cleanUp(group3);
            TestObjectFactory.cleanUp(center);
            TestObjectFactory.cleanUp(center1);
            TestObjectFactory.cleanUp(officeBO);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testDuplicateSearchIdAfterTransferToBranch() throws Exception {
        StaticHibernateUtil.startTransaction();
        officeBO = createOffice("off1");
        group = createGroupUnderBranch("group",CustomerStatus.GROUP_ACTIVE, officeBO.getOfficeId());
        client = createClient(group, CustomerStatus.CLIENT_ACTIVE);
        group1 = createGroupUnderBranch("group1",CustomerStatus.GROUP_ACTIVE, officeBO.getOfficeId());
        client1 = createClient(group1, CustomerStatus.CLIENT_ACTIVE);

        officeBO1 = createOffice("off2");
        group2 = createGroupUnderBranch("group2",CustomerStatus.GROUP_ACTIVE, officeBO1.getOfficeId());
        client2 = createClient(group2, CustomerStatus.CLIENT_ACTIVE);
        group3 = createGroupUnderBranch("group3",CustomerStatus.GROUP_ACTIVE, officeBO1.getOfficeId());
        client3 = createClient(group3, CustomerStatus.CLIENT_ACTIVE);

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());
        group.setUserContext(TestUtils.makeUser());

        CustomerService customerService = DependencyInjectedServiceLocator.locateCustomerService();
        customerService.transferGroupTo(group, officeBO1);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group2 = TestObjectFactory.getGroup(group2.getCustomerId());
        group2.setUserContext(TestUtils.makeUser());
        System.out.println("group2 searchId: "+ group2.getSearchId());

        customerService.transferGroupTo(group2, officeBO);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        group2 = TestObjectFactory.getGroup(group2.getCustomerId());
        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
        officeBO = new OfficePersistence().getOffice(officeBO.getOfficeId());

        System.out.println("group2 officeId,searchId: "+ group2.getOffice().getOfficeId() + "," + group2.getSearchId());
        System.out.println("group1 officeId,searchId: "+ group1.getOffice().getOfficeId() + "," + group1.getSearchId());

        // group1 and group2 are now under the same office
        Assert.assertEquals(group1.getOffice().getOfficeId(), group2.getOffice().getOfficeId());

        // the searchId for group1 and group2 should be different
        Assert.assertFalse("The searchIds for group1 and group2 should be unique.", group1.getSearchId().compareTo(group2.getSearchId()) == 0);
    }



    private GroupBO createGroupUnderBranchWithoutMeeting(String name) {
        return TestObjectFactory.createGroupUnderBranch(name, CustomerStatus.GROUP_PENDING, officeId1, null,
                personnelId);
    }

    private void createCenter() {
        meeting = getMeeting();
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
    }

    private CenterBO createCenter(String name) throws Exception {
        return createCenter(name, officeId3, WeekDay.MONDAY);
    }

    private CenterBO createCenter(String name, Short officeId, WeekDay weekDay) throws Exception {
        meeting = new MeetingBO(weekDay, Short.valueOf("1"), new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        return TestObjectFactory.createWeeklyFeeCenter(name, meeting, officeId, personnelId);
    }

    private void createGroup(String name) {
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(name, CustomerStatus.GROUP_ACTIVE, center);
    }

    private GroupBO createGroupUnderBranch(String name, CustomerStatus customerStatus) {
        meeting = getMeeting();
        return TestObjectFactory.createGroupUnderBranch(name, customerStatus, officeId1, meeting, personnelId);
    }

    private GroupBO createGroupUnderBranch(String name, CustomerStatus customerStatus,
            List<CustomFieldView> customFieldView) {
        meeting = getMeeting();
        return TestObjectFactory.createGroupUnderBranch(name, customerStatus, officeId1, meeting, personnelId,
                customFieldView);
    }

    private GroupBO createGroupUnderBranch(String name, CustomerStatus groupStatus, Short officeId) throws Exception {
        meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        return TestObjectFactory.createGroupUnderBranchWithMakeUser(name, groupStatus, officeId, meeting,
                personnelId);
    }

    private MeetingBO getMeeting() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return meeting;
    }

    private void removeFees(List<FeeDto> feesToRemove) {
        for (FeeDto fee : feesToRemove) {
            TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
        }
    }

    private List<CustomFieldView> getCustomFields() {
        List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
        fields.add(new CustomFieldView(Short.valueOf("4"), "value1", CustomFieldType.ALPHA_NUMERIC));
        fields.add(new CustomFieldView(Short.valueOf("3"), "value2", CustomFieldType.NUMERIC));
        return fields;
    }

    private List<CustomFieldView> getNewCustomFields() {
        List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
        fields.add(new CustomFieldView(Short.valueOf("4"), "value3", CustomFieldType.ALPHA_NUMERIC));
        fields.add(new CustomFieldView(Short.valueOf("3"), "value4", CustomFieldType.NUMERIC));
        return fields;
    }

    private Address getAddress() {
        Address address = new Address();
        address.setLine1("Aditi");
        address.setCity("Bangalore");
        return address;
    }

    private List<FeeDto> getFees() {
        List<FeeDto> fees = new ArrayList<FeeDto>();
        AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee",
                FeeCategory.GROUP, "200", RecurrenceType.WEEKLY, Short.valueOf("2"));
        AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory.createOneTimeAmountFee("OneTimeAmountFee",
                FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
        fees.add(new FeeDto(TestObjectFactory.getContext(), fee1));
        fees.add(new FeeDto(TestObjectFactory.getContext(), fee2));
        StaticHibernateUtil.commitTransaction();
        return fees;
    }

    private void createObjectsForTranferToCenterInSameBranch() throws Exception {
        createInitialObjects();
        client1 = createClient(group, CustomerStatus.CLIENT_PARTIAL);
        client2 = createClient(group, CustomerStatus.CLIENT_CANCELLED);
        center1 = createCenter("toTransfer", officeId3, WeekDay.THURSDAY);
        group1 = createGroup("newGroup", center1);
    }

    private ClientBO createClient(GroupBO group, CustomerStatus clientStatus) {
        return TestObjectFactory.createClient("client1", clientStatus, group, new Date());
    }

    private GroupBO createGroup(String name, CenterBO center) {
        return TestObjectFactory.createWeeklyFeeGroupUnderCenter(name, CustomerStatus.GROUP_ACTIVE, center);
    }

    private OfficeBO createOffice(String name) throws Exception {
        return TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
                .getOffice(TestObjectFactory.HEAD_OFFICE), name, name);
    }

    private void createInitialObjects() throws Exception {
        center = createCenter("Center");
        group = createGroup("Group", center);
        client = createClient(group, CustomerStatus.CLIENT_ACTIVE);
    }

    private SavingsBO getSavingsAccount(CustomerBO customerBO, String offeringName, String shortName) throws Exception {
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customerBO,
                AccountStates.SAVINGS_ACC_APPROVED, new Date(System.currentTimeMillis()), savingsOffering);
    }

    private void createInitialObject() {
        Date startDate = new Date(System.currentTimeMillis());

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient("Client", CustomerStatus.CLIENT_ACTIVE, group);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loandsdasd", "fsad", startDate, meeting);
        account1 = TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOffering);
        loanOffering = TestObjectFactory.createLoanOffering("Loandfas", "dsvd", ApplicableTo.CLIENTS, startDate,
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, 3, InterestType.FLAT, meeting);
        account2 = TestObjectFactory.createLoanAccount("42427777341", client,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);
    }

    private void changeFirstInstallmentDate(AccountBO accountBO, int numberOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - numberOfDays);
        for (AccountActionDateEntity accountActionDateEntity : accountBO.getAccountActionDates()) {
            LoanBOTestUtils.setActionDate(accountActionDateEntity, new java.sql.Date(currentDateCalendar
                    .getTimeInMillis()));
            break;
        }
    }

}
