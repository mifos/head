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

package org.mifos.customers.client.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerHierarchyEntity;
import org.mifos.customers.business.CustomerMovementEntity;
import org.mifos.customers.business.CustomerPositionEntity;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.office.util.helpers.OfficeStatus;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientIntegrationTest extends MifosIntegrationTestCase {
    public ClientIntegrationTest() throws Exception {
        super();
    }

    private AccountBO accountBO;
    private CustomerBO center;
    private CenterBO center1;
    private CustomerBO group;
    private GroupBO group1;
    private SavingsOfferingBO savingsOffering1;
    private SavingsOfferingBO savingsOffering2;
    private ClientBO client;
    private MeetingBO meeting;
    private PersonnelBO personnel;
    private Short officeId = 1;
    private OfficeBO office;
    private OfficeBO officeBo;
    private CustomerPersistence customerPersistence = new CustomerPersistence();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        personnel = getTestUser();
        officeBo = getHeadOffice();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(accountBO);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(group1);
            TestObjectFactory.cleanUp(center);
            TestObjectFactory.cleanUp(center1);
            TestObjectFactory.cleanUp(office);
            TestObjectFactory.removeObject(savingsOffering1);
            TestObjectFactory.removeObject(savingsOffering2);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testPovertyLikelihoodHibernateMapping() throws Exception {
        createInitialObjects();
        Double pct = new Double(55.0);
        String hd = "**FOO**";
        client.setPovertyLikelihoodPercent(pct);
        client.getCustomerDetail().setHandicappedDetails(hd);
        new ClientPersistence().createOrUpdate(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        ClientBO retrievedClient = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class,
                client.getCustomerId());
        Assert.assertEquals(hd, retrievedClient.getCustomerDetail().getHandicappedDetails());
        Assert.assertEquals(pct, retrievedClient.getPovertyLikelihoodPercent());

    }

    public void testRemoveClientFromGroup() throws Exception {
        createInitialObjects();
        client.updateClientFlag();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());

    }

    public void testSuccessfulAddClientToGroupWithMeeting() throws Exception {
        createObjectsForTransferToGroup_WithMeeting();
        Assert.assertEquals(0, group1.getMaxChildCount().intValue());

        client.addClientToGroup(group1);

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        Assert.assertNotNull(client.getParentCustomer());
        Assert.assertEquals(group1.getCustomerId(), client.getParentCustomer().getCustomerId());

        client = TestObjectFactory.getClient(client.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        group1 = TestObjectFactory.getGroup(group1.getCustomerId());

        Assert.assertEquals(1, group1.getMaxChildCount().intValue());
        Assert.assertEquals(group1.getCustomerId(), client.getParentCustomer().getCustomerId());
        Assert.assertEquals(true, client.isClientUnderGroup());
    }

    public void testSuccessfulValidateBeforeAddingClientToGroup_Client() throws Exception {
        String oldMeetingPlace = "Tunisia";
        MeetingBO weeklyMeeting = new MeetingBO(WeekDay.FRIDAY, Short.valueOf("1"), new java.util.Date(),
                MeetingType.CUSTOMER_MEETING, oldMeetingPlace);
        client = TestObjectFactory.createClient("clientname", weeklyMeeting, CustomerStatus.CLIENT_CANCELLED);
        group1 = TestObjectFactory.createGroupUnderBranch("Group2", CustomerStatus.GROUP_PENDING, new Short("3"),
                getMeeting(), new Short("1"));
        try {
            client.validateBeforeAddingClientToGroup(group1);
            fail();
        } catch (CustomerException expected) {
            Assert.assertEquals(CustomerConstants.CLIENT_IS_CLOSED_OR_CANCELLED_EXCEPTION, expected.getKey());
            Assert.assertTrue(true);
        }

    }

    public void testSuccessfulValidateBeforeAddingClientToGroup_Amount() throws Exception {
        String oldMeetingPlace = "Tunis";
        MeetingBO weeklyMeeting = new MeetingBO(WeekDay.FRIDAY, Short.valueOf("1"), new java.util.Date(),
                MeetingType.CUSTOMER_MEETING, oldMeetingPlace);
        client = TestObjectFactory.createClient("clientname", weeklyMeeting, CustomerStatus.CLIENT_CANCELLED);
        group1 = TestObjectFactory.createGroupUnderBranch("Group2", CustomerStatus.GROUP_PENDING, new Short("3"),
                getMeeting(), new Short("1"));
        try {
            client.validateBeforeAddingClientToGroup(group1);
            Assert.fail();
        } catch (CustomerException expected) {
            assertEquals(CustomerConstants.CLIENT_IS_CLOSED_OR_CANCELLED_EXCEPTION, expected.getKey());
            assertNotSame(CustomerConstants.CLIENT_HAVE_OPEN_LOAN_ACCOUNT_EXCEPTION, expected.getKey());
            Assert.assertTrue(true);
        }

    }

    public void testGenerateScheduleForClient_OnClientCreate() throws Exception {
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("Offering1", "s1",
                SavingsType.MANDATORY, ApplicableTo.GROUPS, new Date(System.currentTimeMillis()));
        createParentObjects(CustomerStatus.GROUP_ACTIVE);
        accountBO = TestObjectFactory.createSavingsAccount("globalNum", center, AccountState.SAVINGS_ACTIVE,
                new java.util.Date(), savingsOffering, TestObjectFactory.getContext());
        Assert.assertEquals(0, accountBO.getAccountActionDates().size());
        client = createClient(CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.closeSession();

        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
        Assert.assertEquals(1, accountBO.getAccountCustomFields().size());
        Assert.assertEquals(10, accountBO.getAccountActionDates().size());
        for (AccountActionDateEntity actionDate : accountBO.getAccountActionDates()) {
            Assert.assertEquals(client.getCustomerId(), actionDate.getCustomer().getCustomerId());
            Assert.assertTrue(true);
        }

        client = TestObjectFactory.getClient(client.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
    }

    public void testFailure_InitialSavingsOfferingAtCreate() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        ClientNameDetailDto clientView = new ClientNameDetailDto(NameType.CLIENT,
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        ClientNameDetailDto spouseView = new ClientNameDetailDto(NameType.SPOUSE,
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        List<SavingsOfferingBO> offerings = new ArrayList<SavingsOfferingBO>();
        offerings.add(savingsOffering1);
        offerings.add(savingsOffering1);
        try {
            client = new ClientBO(TestObjectFactory.getContext(), clientView.getDisplayName(),
                    CustomerStatus.CLIENT_PARTIAL, null, null, null, null, null, offerings, personnel, officeBo, null,
                    null, null, null, null, YesNoFlag.NO.getValue(), clientView, spouseView, clientPersonalDetailDto, null);
        } catch (CustomerException ce) {
            Assert.assertEquals(ClientConstants.ERRORS_DUPLICATE_OFFERING_SELECTED, ce.getKey());
            Assert.assertTrue(true);
        }
        savingsOffering1 = (SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class, savingsOffering1
                .getPrdOfferingId());
    }

    public void testInitialSavingsOfferingAtCreate() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        savingsOffering2 = TestObjectFactory.createSavingsProduct("Offering2", "s2", SavingsType.VOLUNTARY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        List<SavingsOfferingBO> offerings = new ArrayList<SavingsOfferingBO>();
        offerings.add(savingsOffering1);
        offerings.add(savingsOffering2);
        client = new ClientBO(TestObjectFactory.getContext(), clientNameDetailDto.getDisplayName(),
                CustomerStatus.CLIENT_PARTIAL, null, null, null, null, null, offerings, personnel, officeBo, null,
                null, null, null, null, YesNoFlag.YES.getValue(), clientNameDetailDto, spouseNameDetailView,
                clientPersonalDetailDto, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        client = new ClientPersistence().getClient(client.getCustomerId());
        Assert.assertEquals(offerings.size(), client.getOfferingsAssociatedInCreate().size());
        for (ClientInitialSavingsOfferingEntity clientOffering : client.getOfferingsAssociatedInCreate()) {
            if (clientOffering.getSavingsOffering().getPrdOfferingId().equals(savingsOffering1.getPrdOfferingId())) {
                Assert.assertTrue(true);
            }
            if (clientOffering.getSavingsOffering().getPrdOfferingId().equals(savingsOffering2.getPrdOfferingId())) {
                Assert.assertTrue(true);
            }
        }
        savingsOffering1 = (SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class, savingsOffering1
                .getPrdOfferingId());
        savingsOffering2 = (SavingsOfferingBO) TestObjectFactory.getObject(SavingsOfferingBO.class, savingsOffering2
                .getPrdOfferingId());
    }

    public void testCreateClientWithoutName() throws Exception {
        try {
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                    TestObjectFactory.SAMPLE_SALUTATION, "", "", "", "");
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                    .valueOf("1"), Short.valueOf("41"));
            client = new ClientBO(TestUtils.makeUser(), "", CustomerStatus.fromInt(new Short("1")), null, null, null,
                    null, null, null, personnel, officeBo, null, null, null, null, null, YesNoFlag.YES.getValue(),
                    clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto, null);
            Assert.fail("Client Created");
        } catch (CustomerException ce) {
            Assert.assertNull(client);
            Assert.assertEquals(CustomerConstants.INVALID_NAME, ce.getKey());
        }
    }

    public void testCreateClientWithoutOffice() throws Exception {
        try {
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "", "last", "");
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                    .valueOf("1"), Short.valueOf("41"));
            client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                    .fromInt(new Short("1")), null, null, null, null, null, null, personnel, null, null, null, null,
                    null, null, YesNoFlag.YES.getValue(), clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto,
                    null);
            Assert.fail("Client Created");
        } catch (CustomerException ce) {
            Assert.assertNull(client);
            Assert.assertEquals(ce.getKey(), CustomerConstants.INVALID_OFFICE);
        }
    }

    public void testSuccessfulCreateWithoutFeeAndCustomField() throws Exception {
        String name = "Client 1";
        Short povertyStatus = Short.valueOf("41");
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), povertyStatus);
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, null, null, null, null, personnel, officeBo, null, null, null,
                null, null, YesNoFlag.YES.getValue(), clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto,
                null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(name, client.getDisplayName());
        Assert.assertEquals(povertyStatus, client.getCustomerDetail().getPovertyStatus());
        Assert.assertEquals(officeId, client.getOffice().getOfficeId());
    }

    public void testSuccessfulCreateInActiveState_WithAssociatedSavingsOffering() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("offering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        StaticHibernateUtil.closeSession();
        List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
        selectedOfferings.add(savingsOffering1);

        String name = "Client 1";
        Short povertyStatus = Short.valueOf("41");
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), povertyStatus);
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(),
                CustomerStatus.CLIENT_ACTIVE, null, null, null, null, null, selectedOfferings, personnel,
                new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE), getMeeting(), personnel,
                null, null, null, null, YesNoFlag.NO.getValue(), clientNameDetailDto, spouseNameDetailView,
                clientPersonalDetailDto, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(name, client.getDisplayName());
        Assert.assertEquals(1, client.getOfferingsAssociatedInCreate().size());
        Assert.assertEquals(2, client.getAccounts().size());
        for (AccountBO account : client.getAccounts()) {
            if (account instanceof SavingsBO) {
                Assert.assertEquals(savingsOffering1.getPrdOfferingId(), ((SavingsBO) account).getSavingsOffering()
                        .getPrdOfferingId());
                Assert.assertNotNull(account.getGlobalAccountNum());
                Assert.assertTrue(true);
            }
        }
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        savingsOffering1 = null;
    }

    public void ignore_testSavingsAccountOnChangeStatusToActive() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("offering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        savingsOffering2 = TestObjectFactory.createSavingsProduct("offering2", "s2", SavingsType.VOLUNTARY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        StaticHibernateUtil.closeSession();
        List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
        selectedOfferings.add(savingsOffering1);
        selectedOfferings.add(savingsOffering2);

        Short povertyStatus = Short.valueOf("41");
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), povertyStatus);
        client = new ClientBO(TestObjectFactory.getContext(), clientNameDetailDto.getDisplayName(),
                CustomerStatus.CLIENT_PENDING, null, null, null, null, null, selectedOfferings, personnel,
                getBranchOffice(), getMeeting(), personnel, null, null, null, null, YesNoFlag.YES.getValue(),
                clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(2, client.getOfferingsAssociatedInCreate().size());
        Assert.assertEquals(1, client.getAccounts().size());

        client.setUserContext(TestObjectFactory.getContext());
        // FIXME - keithw - use builder for creation of client for tests in given state.
        // client.changeStatus(CustomerStatus.CLIENT_ACTIVE, null, "Client Made Active");

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(3, client.getAccounts().size());

        for (AccountBO account : client.getAccounts()) {
            if (account instanceof SavingsBO) {
                Assert.assertNotNull(account.getGlobalAccountNum());
                Assert.assertTrue(true);
            }
        }
        savingsOffering1 = null;
        savingsOffering2 = null;
    }

    public void testSuccessfulCreateWithParentGroup() throws Exception {
        String name = "Client 1";
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        createParentObjects(CustomerStatus.GROUP_PARTIAL);
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, null, null, null, null, personnel, group.getOffice(), group,
                null, null, null, null, YesNoFlag.YES.getValue(), clientNameDetailDto, spouseNameDetailView,
                clientPersonalDetailDto, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(name, client.getDisplayName());
        Assert.assertEquals(client.getOffice().getOfficeId(), group.getOffice().getOfficeId());
    }

    public void testFailureCreatePendingClientWithParentGroupInLowerStatus() throws Exception {
        try {
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                    TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                    .valueOf("1"), Short.valueOf("41"));
            createParentObjects(CustomerStatus.GROUP_PARTIAL);
            client = new ClientBO(TestUtils.makeUserWithLocales(), clientNameDetailDto.getDisplayName(),
                    CustomerStatus.CLIENT_PENDING, null, null, null, null, null, null, personnel, group.getOffice(),
                    group, null, null, null, null, YesNoFlag.YES.getValue(), clientNameDetailDto,
                    spouseNameDetailView, clientPersonalDetailDto, null);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertNull(client);
            Assert.assertEquals(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION, e.getKey());
        }
    }

    public void testFailureCreateActiveClientWithParentGroupInLowerStatus() throws Exception {
        try {
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                    TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                    .valueOf("1"), Short.valueOf("41"));
            createParentObjects(CustomerStatus.GROUP_PARTIAL);
            client = new ClientBO(TestUtils.makeUserWithLocales(), clientNameDetailDto.getDisplayName(),
                    CustomerStatus.CLIENT_ACTIVE, null, null, null, null, null, null, personnel, group.getOffice(),
                    group, null, null, null, null, YesNoFlag.YES.getValue(), clientNameDetailDto,
                    spouseNameDetailView, clientPersonalDetailDto, null);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertNull(client);
            Assert.assertEquals(ClientConstants.INVALID_CLIENT_STATUS_EXCEPTION, e.getKey());
        }
    }

    public void testFailureCreateActiveClientWithoutLO() throws Exception {
        List<FeeDto> fees = getFees();
        try {
            meeting = getMeeting();
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "", "last", "");
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                    .valueOf("1"), Short.valueOf("41"));
            client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                    .fromInt(new Short("3")), null, null, null, null, fees, null, personnel, officeBo, meeting, null,
                    null, null, null, null, YesNoFlag.NO.getValue(), clientNameDetailDto, spouseNameDetailView,
                    clientPersonalDetailDto, null);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertNull(client);
            Assert.assertEquals(CustomerConstants.INVALID_LOAN_OFFICER, e.getKey());
        }
        removeFees(fees);
    }

    public void testFailureCreateActiveClientWithoutMeeting() throws Exception {
        try {
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "", "last", "");
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                    .valueOf("1"), Short.valueOf("41"));
            client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                    .fromInt(new Short("3")), null, null, null, null, null, null, personnel, officeBo, null, personnel,
                    null, null, null, null, YesNoFlag.NO.getValue(), clientNameDetailDto, spouseNameDetailView,
                    clientPersonalDetailDto, null);
            Assert.fail();
        } catch (CustomerException ce) {
            Assert.assertNull(client);
            Assert.assertEquals(CustomerConstants.INVALID_MEETING, ce.getKey());
        }

    }

    // FIXME - #000010 - keithw - put back on after client creation is complete!
    public void ignore_testFailureCreateClientWithDuplicateNameAndDOB() throws Exception {
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, null, null, null, null, personnel, officeBo, null, personnel,
                date, null, null, null, YesNoFlag.YES.getValue(), clientNameDetailDto, spouseNameDetailView,
                clientPersonalDetailDto, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        try {
            new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus.fromInt(new Short(
                    "1")), null, null, null, null, null, null, personnel, officeBo, null, personnel, date, null, null,
                    null, YesNoFlag.NO.getValue(), clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto, null);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(CustomerConstants.CUSTOMER_DUPLICATE_CUSTOMERNAME_EXCEPTION, e.getKey());
        }

    }

    // FIXME - #000010 - keithw - put back on after client creation is complete!
    public void ignore_testFailureCreateClientWithDuplicateGovtId() throws Exception {
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, null, null, null, null, personnel, officeBo, null, personnel,
                new java.util.Date(), "1", null, null, YesNoFlag.YES.getValue(), clientNameDetailDto,
                spouseNameDetailView, clientPersonalDetailDto, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getObject(ClientBO.class, client.getCustomerId());
        try {
            client = new ClientBO(TestUtils.makeUserWithLocales(), clientNameDetailDto.getDisplayName(),
                    CustomerStatus.CLIENT_PARTIAL, null, null, null, null, null, null, personnel, officeBo, null,
                    personnel, new java.util.Date(), "1", null, null, YesNoFlag.NO.getValue(), clientNameDetailDto,
                    spouseNameDetailView, clientPersonalDetailDto, null);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION, e.getKey());
        }

    }

    public void testSuccessfulCreateClientInBranch() throws Exception {
        OfficeBO office = new OfficePersistence().getOffice(TestObjectFactory.HEAD_OFFICE);
        String firstName = "Client";
        String lastName = "Last";
        String displayName = "Client Last";
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT,
                TestObjectFactory.SAMPLE_SALUTATION, firstName, "", lastName, "");
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE,
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, null, getCustomFields(), null, null, personnel, office, meeting,
                personnel, null, null, null, null, YesNoFlag.YES.getValue(), clientNameDetailDto,
                spouseNameDetailView, clientPersonalDetailDto, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(displayName, client.getDisplayName());
        Assert.assertEquals(firstName, client.getFirstName());
        Assert.assertEquals(lastName, client.getLastName());
        Assert.assertEquals(officeId, client.getOffice().getOfficeId());
    }

    public void testUpdateGroupFailure_GroupNULL() throws Exception {
        createInitialObjects();
        try {
            client.transferToGroup(null);
            Assert.fail();
        } catch (CustomerException ce) {
            Assert.assertEquals(CustomerConstants.INVALID_PARENT, ce.getKey());
        }
    }

    public void testUpdateGroupFailure_TransferInSameGroup() throws Exception {
        createInitialObjects();
        try {
            client.transferToGroup((GroupBO) client.getParentCustomer());
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(CustomerConstants.ERRORS_SAME_PARENT_TRANSFER, e.getKey());
        }
    }

    public void ignore_testUpdateGroupFailure_GroupCancelled() throws Exception {
        createObjectsForTransferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
        refetchGroup();
        // FIXME - keithw - use builder for creation of client for tests in given state.
        // group1.changeStatus(CustomerStatus.GROUP_CANCELLED, CustomerStatusFlag.GROUP_CANCEL_WITHDRAW,
        // "Status Changed");
        StaticHibernateUtil.commitTransaction();
        try {
            client.transferToGroup(group1);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE, e.getKey());
        }
    }

    private void refetchGroup() throws PersistenceException {
        group1 = (GroupBO) customerPersistence.getCustomer(group1.getCustomerId());
        group1.setUserContext(TestUtils.makeUserWithLocales());
    }

    public void ignore_testUpdateGroupFailure_GroupClosed() throws Exception {
        createObjectsForTransferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
        refetchGroup();
        // FIXME - keithw - use builder for creation of client for tests in given state.
        // group1.changeStatus(CustomerStatus.GROUP_CLOSED, CustomerStatusFlag.GROUP_CLOSED_TRANSFERRED,
        // "Status Changed");
        StaticHibernateUtil.commitTransaction();
        try {
            client.transferToGroup(group1);
            Assert.fail();
        } catch (CustomerException expected) {
            Assert.assertEquals(CustomerConstants.ERRORS_INTRANSFER_PARENT_INACTIVE, expected.getKey());
        }
    }

    public void testUpdateGroupFailure_GroupStatusLower() throws Exception {
        createObjectsForTransferToGroup_SameBranch(CustomerStatus.GROUP_PARTIAL);
        try {
            client.transferToGroup(group1);
            Assert.fail();
        } catch (CustomerException expected) {
            Assert.assertEquals(ClientConstants.ERRORS_LOWER_GROUP_STATUS, expected.getKey());
        }
    }

    public void ignore_testUpdateGroupFailure_GroupStatusLower_Client_OnHold() throws Exception {
        createObjectsForTransferToGroup_SameBranch(CustomerStatus.GROUP_PARTIAL);
        client = (ClientBO) customerPersistence.getCustomer(client.getCustomerId());
        client.setUserContext(TestUtils.makeUserWithLocales());
        // FIXME - keithw - use builder for creation of client for tests in given state.
        // client.changeStatus(CustomerStatus.CLIENT_HOLD, null, "client on hold");
        try {
            client.transferToGroup(group1);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(ClientConstants.ERRORS_LOWER_GROUP_STATUS, e.getKey());
        }
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
    }

    public void testUpdateGroupFailure_ClientHasActiveAccounts() throws Exception {
        createObjectsForTransferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
        accountBO = createSavingsAccount(client, "fsaf6", "ads6");
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        client.setUserContext(TestUtils.makeUserWithLocales());
        try {
            client.transferToGroup(group1);
            Assert.fail();
        } catch (CustomerException ce) {
            Assert.assertEquals(ClientConstants.ERRORS_ACTIVE_ACCOUNTS_PRESENT, ce.getKey());
        }
        StaticHibernateUtil.closeSession();
        accountBO = TestObjectFactory.getObject(AccountBO.class, accountBO.getAccountId());
        client = TestObjectFactory.getClient(client.getCustomerId());
    }

    public void testUpdateGroupFailure_MeetingFrequencyMismatch() throws Exception {
        createInitialObjects();
        MeetingBO meeting = new MeetingBO(Short.valueOf("2"), Short.valueOf("1"), new java.util.Date(),
                MeetingType.CUSTOMER_MEETING, "Bangalore");
        center1 = TestObjectFactory.createWeeklyFeeCenter("Center1", meeting);
        group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group2", CustomerStatus.GROUP_ACTIVE, center1);
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        client.setUserContext(TestUtils.makeUser());
        try {
            client.transferToGroup(group1);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(CustomerConstants.ERRORS_MEETING_FREQUENCY_MISMATCH, e.getKey());
        }
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
    }

    public void testSuccessfulTransferToGroupInSameBranch() throws Exception {
        createObjectsForTransferToGroup_SameBranch(CustomerStatus.GROUP_ACTIVE);
        PositionEntity position = new MasterPersistence().retrieveMasterEntities(PositionEntity.class,
                Short.valueOf("1")).get(0);
        group.addCustomerPosition(new CustomerPositionEntity(position, client, group));
        center.addCustomerPosition(new CustomerPositionEntity(position, client, group));
        client.transferToGroup(group1);

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        Assert.assertEquals(group1.getCustomerMeeting().getMeeting().getMeetingId(), client.getCustomerMeeting()
                .getUpdatedMeeting().getMeetingId());
        Assert.assertEquals(group1.getCustomerId(), client.getParentCustomer().getCustomerId());
        Assert.assertEquals(1, group1.getMaxChildCount().intValue());
        Assert.assertEquals(center1.getSearchId() + ".1.1", client.getSearchId());
        CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
        Assert.assertEquals(group1.getCustomerId(), currentHierarchy.getParentCustomer().getCustomerId());
    }

    public void testSuccessfulTransferToGroup_WithMeeting() throws Exception {
        createObjectsForTransferToGroup_WithMeeting();

        client.transferToGroup(group1);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        Assert.assertEquals(WeekDay.MONDAY, client.getCustomerMeeting().getMeeting().getMeetingDetails().getWeekDay());
        Assert.assertNull(client.getCustomerMeeting().getUpdatedMeeting());

        client = TestObjectFactory.getClient(client.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        group1 = TestObjectFactory.getGroup(group1.getCustomerId());

        Assert.assertEquals(group1.getCustomerId(), client.getParentCustomer().getCustomerId());
        CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
        Assert.assertEquals(group1.getCustomerId(), currentHierarchy.getParentCustomer().getCustomerId());
    }

    /**
     * Transfer a client created outside a group to a group. This was originally created to address <a
     * href="https://mifos.dev.java.net/issues/show_bug.cgi?id=2184">issue 2184</a>.
     * <p>
     * In the UI, the second transfer is what causes the the null pointer exception to be thrown.
     */
    public void testSuccessfulTransferToGroupFromOutsideGroup() throws Exception {
        createObjectsForTransferToGroup_OutsideGroup();

        client.addClientToGroup((GroupBO) group); // FIXME: this method should be adding hierarchy
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        Assert.assertEquals(1, group.getMaxChildCount().intValue());

        CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
        Assert.assertNotNull("Adding client to group should also create hierarchy entities.", currentHierarchy);
        Assert.assertEquals(group.getCustomerId(), currentHierarchy.getParentCustomer().getCustomerId());

        // this "normal" group transfer is tested elsewhere, but I added this
        // specifically because
        // issue 2184 reproduced after *two* transfers, one into a group from
        // outside any group,
        // the 2nd from group to group.
        StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.startTransaction();
        client.transferToGroup(group1);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        currentHierarchy = client.getActiveCustomerHierarchy();
        Assert.assertNotNull(currentHierarchy);
        Assert.assertEquals(group1.getCustomerId(), currentHierarchy.getParentCustomer().getCustomerId());
    }

    public void testSuccessfulTransferToGroup_WithOutMeeting() throws Exception {
        createObjectsForTransferToGroup_WithoutMeeting();
        Assert.assertNotNull(client.getCustomerMeeting());

        client.transferToGroup(group1);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        client = TestObjectFactory.getClient(client.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        group1 = TestObjectFactory.getGroup(group1.getCustomerId());

        Assert.assertNull(client.getCustomerMeeting());
        Assert.assertEquals(group1.getCustomerId(), client.getParentCustomer().getCustomerId());
    }

    public void testSuccessfulTransferToGroupInDifferentBranch() throws Exception {
        createObjectsForTransferToGroup_DifferentBranch();
        PositionEntity position = new MasterPersistence().retrieveMasterEntities(PositionEntity.class,
                Short.valueOf("1")).get(0);
        group.addCustomerPosition(new CustomerPositionEntity(position, client, group));
        center.addCustomerPosition(new CustomerPositionEntity(position, client, group));

        client.transferToGroup(group1);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        client = TestObjectFactory.getClient(client.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        Assert.assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());
        Assert.assertEquals(group1.getCustomerId(), client.getParentCustomer().getCustomerId());
        Assert.assertEquals(1, group1.getMaxChildCount().intValue());
        Assert.assertEquals(center1.getSearchId() + ".1.1", client.getSearchId());
        CustomerHierarchyEntity currentHierarchy = client.getActiveCustomerHierarchy();
        Assert.assertEquals(group1.getCustomerId(), currentHierarchy.getParentCustomer().getCustomerId());
        CustomerMovementEntity customerMovementEntity = client.getActiveCustomerMovement();
        Assert.assertEquals(office.getOfficeId(), customerMovementEntity.getOffice().getOfficeId());

        client.setUserContext(TestObjectFactory.getContext());
        client.transferToGroup((GroupBO) group);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        client = TestObjectFactory.getClient(client.getCustomerId());

        group = TestObjectFactory.getGroup(group.getCustomerId());
        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        center1 = TestObjectFactory.getCenter(center1.getCustomerId());
        office = new OfficePersistence().getOffice(office.getOfficeId());
    }

    public void testUpdateBranchFailure_OfficeNULL() throws Exception {
        createInitialObjects();
        try {
            client.transferToBranch(null);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(CustomerConstants.INVALID_OFFICE, e.getKey());
        }
    }

    public void testUpdateBranchFailure_TransferInSameOffice() throws Exception {
        createInitialObjects();
        try {
            client.transferToBranch(client.getOffice());
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER, e.getKey());
        }
    }

    public void testUpdateBranchFailure_OfficeInactive() throws Exception {
        createObjectsForClientTransfer();
        office.update(office.getOfficeName(), office.getShortName(), OfficeStatus.INACTIVE, office.getOfficeLevel(),
                office.getParentOffice(), null, null);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        try {
            client.transferToBranch(office);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(CustomerConstants.ERRORS_TRANSFER_IN_INACTIVE_OFFICE, e.getKey());
        }
    }

    public void testUpdateBranchFirstTime() throws Exception {
        createObjectsForClientTransfer();
        Assert.assertNull(client.getActiveCustomerMovement());

        client.transferToBranch(office);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertNotNull(client.getActiveCustomerMovement());
        Assert.assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());
        Assert.assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
        office = client.getOffice();
    }

    public void testUpdateBranchSecondTime() throws Exception {
        createObjectsForClientTransfer();
        Assert.assertNull(client.getActiveCustomerMovement());
        OfficeBO oldOffice = client.getOffice();

        client.transferToBranch(office);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        client = TestObjectFactory.getClient(client.getCustomerId());
        client.setUserContext(TestUtils.makeUser());
        CustomerMovementEntity currentMovement = client.getActiveCustomerMovement();
        Assert.assertNotNull(currentMovement);
        Assert.assertEquals(office.getOfficeId(), currentMovement.getOffice().getOfficeId());
        Assert.assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());

        client.transferToBranch(oldOffice);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        client = TestObjectFactory.getClient(client.getCustomerId());
        currentMovement = client.getActiveCustomerMovement();
        Assert.assertNotNull(currentMovement);
        Assert.assertEquals(oldOffice.getOfficeId(), currentMovement.getOffice().getOfficeId());
        Assert.assertEquals(oldOffice.getOfficeId(), client.getOffice().getOfficeId());

        office = new OfficePersistence().getOffice(office.getOfficeId());
    }

    public void testGetClientAndSpouseName() throws Exception {
        createObjectsForClient("Client 1", CustomerStatus.CLIENT_ACTIVE);
        Assert.assertEquals(client.getClientName().getName().getFirstName(), "Client 1");
        Assert.assertEquals(client.getSpouseName().getName().getFirstName(), "Client 1");
        client = TestObjectFactory.getClient(client.getCustomerId());
        office = new OfficePersistence().getOffice(office.getOfficeId());
    }

    private void createObjectsForClientTransfer() throws Exception {
        office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
                .getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
        client = TestObjectFactory.createClient("client_to_transfer", getMeeting(), CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.closeSession();
    }

    private SavingsBO createSavingsAccount(CustomerBO customer, String offeringName, String shortName) throws Exception {
        SavingsOfferingBO savingsOffering = new SavingsTestHelper().createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customer, AccountStates.SAVINGS_ACC_APPROVED,
                new Date(System.currentTimeMillis()), savingsOffering);
    }

    private void createObjectsForTransferToGroup_WithMeeting() throws Exception {
        Short officeId = new Short("3");
        Short personnel = new Short("1");
        group = TestObjectFactory.createGroupUnderBranch("Group", CustomerStatus.GROUP_PENDING, officeId, null,
                personnel);
        group1 = TestObjectFactory.createGroupUnderBranch("Group2", CustomerStatus.GROUP_PENDING, officeId,
                getMeeting(), personnel);
        client = TestObjectFactory.createClient("new client", CustomerStatus.CLIENT_PARTIAL, group,
                new java.util.Date());
        StaticHibernateUtil.closeSession();
    }

    private void createObjectsForTransferToGroup_WithoutMeeting() throws Exception {
        Short officeId = new Short("3");
        Short personnel = new Short("1");
        group = TestObjectFactory.createGroupUnderBranch("Group", CustomerStatus.GROUP_PENDING, officeId, getMeeting(),
                personnel);
        group1 = TestObjectFactory.createGroupUnderBranch("Group2", CustomerStatus.GROUP_PENDING, officeId, null,
                personnel);
        client = TestObjectFactory.createClient("new client", CustomerStatus.CLIENT_PARTIAL, group,
                new java.util.Date());
        StaticHibernateUtil.closeSession();
    }

    private void createObjectsForTransferToGroup_OutsideGroup() throws Exception {
        Short officeId = new Short("3");
        Short personnel = new Short("1");
        group = TestObjectFactory.createGroupUnderBranch("Group", CustomerStatus.GROUP_PENDING, officeId, getMeeting(),
                personnel);
        group1 = TestObjectFactory.createGroupUnderBranch("Group2", CustomerStatus.GROUP_PENDING, officeId,
                getMeeting(), personnel);
        client = TestObjectFactory
                .createClient("new client", CustomerStatus.CLIENT_PARTIAL, null, new java.util.Date());
        StaticHibernateUtil.closeSession();
    }

    private void createObjectsForTransferToGroup_SameBranch(CustomerStatus groupStatus) throws Exception {
        createInitialObjects();
        MeetingBO meeting = new MeetingBO(WeekDay.THURSDAY, Short.valueOf("1"), new java.util.Date(),
                MeetingType.CUSTOMER_MEETING, "Bangalore");
        center1 = TestObjectFactory.createWeeklyFeeCenter("Center1", meeting);
        group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group2", groupStatus, center1);
        StaticHibernateUtil.closeSession();
    }

    private void createObjectsForTransferToGroup_DifferentBranch() throws Exception {
        createInitialObjects();
        office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
                .getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
        StaticHibernateUtil.closeSession();
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center1 = TestObjectFactory.createWeeklyFeeCenter("Center1", meeting, office.getOfficeId(),
                PersonnelConstants.SYSTEM_USER);
        group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group2", CustomerStatus.GROUP_ACTIVE, center1);
        StaticHibernateUtil.closeSession();
    }

    private void createObjectsForClient(String name, CustomerStatus status) throws Exception {
        office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
                .getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
        client = TestObjectFactory.createClient(name, getMeeting(), status);
        StaticHibernateUtil.closeSession();
    }

    private List<FeeDto> getFees() {
        List<FeeDto> fees = new ArrayList<FeeDto>();
        AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory.createPeriodicAmountFee("PeriodicAmountFee",
                FeeCategory.CENTER, "200", RecurrenceType.WEEKLY, Short.valueOf("2"));
        AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory.createOneTimeAmountFee("OneTimeAmountFee",
                FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
        fees.add(new FeeDto(TestObjectFactory.getContext(), fee1));
        fees.add(new FeeDto(TestObjectFactory.getContext(), fee2));
        StaticHibernateUtil.commitTransaction();
        return fees;
    }

    private void removeFees(List<FeeDto> feesToRemove) {
        for (FeeDto fee : feesToRemove) {
            TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
        }
    }

    private List<CustomFieldDto> getCustomFields() {
        List<CustomFieldDto> fields = new ArrayList<CustomFieldDto>();
        fields.add(new CustomFieldDto(Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC));
        fields.add(new CustomFieldDto(Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC));
        return fields;
    }

    private void createInitialObjects() throws Exception {
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new java.util.Date(),
                MeetingType.CUSTOMER_MEETING, "Delhi");
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = createClient(CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.closeSession();
    }

    private ClientBO createClient(CustomerStatus clientStatus) {
        return TestObjectFactory.createClient("Client", clientStatus, group);
    }

    private void createParentObjects(CustomerStatus groupStatus) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", groupStatus, center);
        StaticHibernateUtil.closeSession();
    }

    private MeetingBO getMeeting() throws Exception {
        return new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMER_MEETING,
                "Delhi");
    }
}
