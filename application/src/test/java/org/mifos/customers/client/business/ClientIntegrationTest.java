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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerMovementEntity;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.persistence.ClientPersistence;
import org.mifos.customers.client.util.helpers.ClientConstants;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.screen.ClientNameDetailDto;
import org.mifos.dto.screen.ClientPersonalDetailDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientIntegrationTest extends MifosIntegrationTestCase {

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

    @Before
    public void setUp() throws Exception {
        personnel = getTestUser();
        officeBo = getHeadOffice();
    }

    @After
    public void tearDown() throws Exception {
        try {
            accountBO = null;
            client = null;
            group = null;
            group1 = null;
            center = null;
            center1 = null;
            office = null;
            savingsOffering1 = null;
        } catch (Exception e) {

        }
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testPovertyLikelihoodHibernateMapping() throws Exception {
        createInitialObjects();
        Double pct = new Double(55.0);
        String hd = "**FOO**";
        client.setPovertyLikelihoodPercent(pct);
        client.getCustomerDetail().setHandicappedDetails(hd);
        new ClientPersistence().createOrUpdate(client);
        StaticHibernateUtil.flushSession();
        ClientBO retrievedClient = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class,
                client.getCustomerId());
        Assert.assertEquals(hd, retrievedClient.getCustomerDetail().getHandicappedDetails());
        Assert.assertEquals(pct, retrievedClient.getPovertyLikelihoodPercent());

    }

    @Test
    public void testSuccessfulValidateBeforeAddingClientToGroup_Client() throws Exception {
        String oldMeetingPlace = "Tunisia";
        MeetingBO weeklyMeeting = new MeetingBO(WeekDay.FRIDAY, Short.valueOf("1"), new java.util.Date(),
                MeetingType.CUSTOMER_MEETING, oldMeetingPlace);
        client = TestObjectFactory.createClient("clientname", weeklyMeeting, CustomerStatus.CLIENT_CANCELLED);
        group1 = TestObjectFactory.createGroupUnderBranch("Group2", CustomerStatus.GROUP_PENDING, new Short("3"),
                getMeeting(), new Short("1"));
        try {
            client.validateBeforeAddingClientToGroup(group1);
            Assert.fail();
        } catch (CustomerException expected) {
            Assert.assertEquals(CustomerConstants.CLIENT_IS_CLOSED_OR_CANCELLED_EXCEPTION, expected.getKey());
            Assert.assertTrue(true);
        }

    }

    @Test
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
            Assert.assertEquals(CustomerConstants.CLIENT_IS_CLOSED_OR_CANCELLED_EXCEPTION, expected.getKey());
            Assert.assertNotSame(CustomerConstants.CLIENT_HAVE_OPEN_LOAN_ACCOUNT_EXCEPTION, expected.getKey());
            Assert.assertTrue(true);
        }

    }

    @Test
    public void testGenerateScheduleForClient_OnClientCreate() throws Exception {
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("Offering1", "s1",
                SavingsType.MANDATORY, ApplicableTo.GROUPS, new Date(System.currentTimeMillis()));
        createParentObjects(CustomerStatus.GROUP_ACTIVE);
        accountBO = TestObjectFactory.createSavingsAccount("globalNum", center, AccountState.SAVINGS_ACTIVE,
                new java.util.Date(), savingsOffering, TestObjectFactory.getContext());
        Assert.assertEquals(0, accountBO.getAccountActionDates().size());
        client = createClient(CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.flushSession();

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

    @Test
    public void testFailure_InitialSavingsOfferingAtCreate() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        ClientNameDetailDto clientView = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        clientView.setNames(ClientRules.getNameSequence());
        ClientNameDetailDto spouseView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        spouseView.setNames(ClientRules.getNameSequence());
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

    @Test
    public void testInitialSavingsOfferingAtCreate() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("Offering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        savingsOffering2 = TestObjectFactory.createSavingsProduct("Offering2", "s2", SavingsType.VOLUNTARY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        clientNameDetailDto.setNames(ClientRules.getNameSequence());
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        spouseNameDetailView.setNames(ClientRules.getNameSequence());
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
        StaticHibernateUtil.flushSession();

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

    @Test
    public void testCreateClientWithoutName() throws Exception {
        try {
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "", "", "", "");
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
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

    @Test
    public void testCreateClientWithoutOffice() throws Exception {
        try {
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "", "last", "");
            clientNameDetailDto.setNames(ClientRules.getNameSequence());
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            spouseNameDetailView.setNames(ClientRules.getNameSequence());

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

    @Test
    public void testSuccessfulCreateWithoutFeeAndCustomField() throws Exception {
        String name = "Client 1";
        Short povertyStatus = Short.valueOf("41");
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        clientNameDetailDto.setNames(ClientRules.getNameSequence());
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        spouseNameDetailView.setNames(ClientRules.getNameSequence());

        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), povertyStatus);
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, null, null, null, null, personnel, officeBo, null, null, null,
                null, null, YesNoFlag.YES.getValue(), clientNameDetailDto, spouseNameDetailView, clientPersonalDetailDto,
                null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.flushSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(name, client.getDisplayName());
        Assert.assertEquals(povertyStatus, client.getCustomerDetail().getPovertyStatus());
        Assert.assertEquals(officeId, client.getOffice().getOfficeId());
    }

    @Test
    public void testSuccessfulCreateInActiveState_WithAssociatedSavingsOffering() throws Exception {
        savingsOffering1 = TestObjectFactory.createSavingsProduct("offering1", "s1", SavingsType.MANDATORY,
                ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
        StaticHibernateUtil.flushSession();
        List<SavingsOfferingBO> selectedOfferings = new ArrayList<SavingsOfferingBO>();
        selectedOfferings.add(savingsOffering1);

        String name = "Client 1";
        Short povertyStatus = Short.valueOf("41");
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        clientNameDetailDto.setNames(ClientRules.getNameSequence());
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        spouseNameDetailView.setNames(ClientRules.getNameSequence());
        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), povertyStatus);
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(),
                CustomerStatus.CLIENT_ACTIVE, null, null, null, null, null, selectedOfferings, personnel,
                new OfficePersistence().getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE), getMeeting(), personnel,
                null, null, null, null, YesNoFlag.NO.getValue(), clientNameDetailDto, spouseNameDetailView,
                clientPersonalDetailDto, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.flushSession();
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
        StaticHibernateUtil.flushSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        savingsOffering1 = null;
    }

    @Test
    public void testSuccessfulCreateWithParentGroup() throws Exception {
        String name = "Client 1";
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
        clientNameDetailDto.setNames(ClientRules.getNameSequence());
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        spouseNameDetailView.setNames(ClientRules.getNameSequence());

        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        createParentObjects(CustomerStatus.GROUP_PARTIAL);
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, null, null, null, null, personnel, group.getOffice(), group,
                null, null, null, null, YesNoFlag.YES.getValue(), clientNameDetailDto, spouseNameDetailView,
                clientPersonalDetailDto, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.flushSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(name, client.getDisplayName());
        Assert.assertEquals(client.getOffice().getOfficeId(), group.getOffice().getOfficeId());
    }

    @Test
    public void testFailureCreatePendingClientWithParentGroupInLowerStatus() throws Exception {
        try {
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
            clientNameDetailDto.setNames(ClientRules.getNameSequence());
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            spouseNameDetailView.setNames(ClientRules.getNameSequence());

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

    @Test
    public void testFailureCreateActiveClientWithParentGroupInLowerStatus() throws Exception {
        try {
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "Client", "", "1", "");
            clientNameDetailDto.setNames(ClientRules.getNameSequence());
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            spouseNameDetailView.setNames(ClientRules.getNameSequence());

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

    @Test
    public void testFailureCreateActiveClientWithoutLO() throws Exception {
        List<FeeDto> fees = getFees();
        try {
            meeting = getMeeting();
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "", "last", "");
            clientNameDetailDto.setNames(ClientRules.getNameSequence());
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            spouseNameDetailView.setNames(ClientRules.getNameSequence());

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

    @Test
    public void testFailureCreateActiveClientWithoutMeeting() throws Exception {
        try {
            ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "", "last", "");
            clientNameDetailDto.setNames(ClientRules.getNameSequence());
            ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                    TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
            spouseNameDetailView.setNames(ClientRules.getNameSequence());

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

    @Test
    public void testSuccessfulCreateClientInBranch() throws Exception {
        OfficeBO office = new OfficePersistence().getOffice(TestObjectFactory.HEAD_OFFICE);
        String firstName = "Client";
        String lastName = "Last";
        String displayName = "Client Last";
        ClientNameDetailDto clientNameDetailDto = new ClientNameDetailDto(NameType.CLIENT.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, firstName, "", lastName, "");
        clientNameDetailDto.setNames(ClientRules.getNameSequence());
        ClientNameDetailDto spouseNameDetailView = new ClientNameDetailDto(NameType.SPOUSE.getValue(),
                TestObjectFactory.SAMPLE_SALUTATION, "first", "middle", "last", "secondLast");
        spouseNameDetailView.setNames(ClientRules.getNameSequence());

        ClientPersonalDetailDto clientPersonalDetailDto = new ClientPersonalDetailDto(1, 1, 1, 1, 1, 1, Short.valueOf("1"), Short
                .valueOf("1"), Short.valueOf("41"));
        client = new ClientBO(TestUtils.makeUser(), clientNameDetailDto.getDisplayName(), CustomerStatus
                .fromInt(new Short("1")), null, null, null, getCustomFields(), null, null, personnel, office, meeting,
                personnel, null, null, null, null, YesNoFlag.YES.getValue(), clientNameDetailDto,
                spouseNameDetailView, clientPersonalDetailDto, null);
        new ClientPersistence().saveClient(client);
        StaticHibernateUtil.flushSession();
        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertEquals(displayName, client.getDisplayName());
        Assert.assertEquals(firstName, client.getFirstName());
        Assert.assertEquals(lastName, client.getLastName());
        Assert.assertEquals(officeId, client.getOffice().getOfficeId());
    }

    @Test
    public void testUpdateBranchFailure_OfficeNULL() throws Exception {
        createInitialObjects();
        try {
            client.transferToBranch(null);
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(CustomerConstants.INVALID_OFFICE, e.getKey());
        }
    }

    @Test
    public void testUpdateBranchFailure_TransferInSameOffice() throws Exception {
        createInitialObjects();
        try {
            client.transferToBranch(client.getOffice());
            Assert.fail();
        } catch (CustomerException e) {
            Assert.assertEquals(CustomerConstants.ERRORS_SAME_BRANCH_TRANSFER, e.getKey());
        }
    }

    @Test
    public void testUpdateBranchFirstTime() throws Exception {
        StaticHibernateUtil.closeSession();
        createObjectsForClientTransfer();
        Assert.assertNull(client.getActiveCustomerMovement());

        client.transferToBranch(office);
        StaticHibernateUtil.flushSession();

        client = TestObjectFactory.getClient(client.getCustomerId());
        Assert.assertNotNull(client.getActiveCustomerMovement());
        Assert.assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());
        Assert.assertEquals(CustomerStatus.CLIENT_HOLD, client.getStatus());
        office = client.getOffice();
    }

    @Test
    public void testUpdateBranchSecondTime() throws Exception {
        StaticHibernateUtil.closeSession();
        createObjectsForClientTransfer();
        Assert.assertNull(client.getActiveCustomerMovement());
        OfficeBO oldOffice = client.getOffice();

        client.transferToBranch(office);
        StaticHibernateUtil.flushSession();

        client = TestObjectFactory.getClient(client.getCustomerId());
        client.setUserContext(TestUtils.makeUser());
        CustomerMovementEntity currentMovement = client.getActiveCustomerMovement();
        Assert.assertNotNull(currentMovement);
        Assert.assertEquals(office.getOfficeId(), currentMovement.getOffice().getOfficeId());
        Assert.assertEquals(office.getOfficeId(), client.getOffice().getOfficeId());

        client.transferToBranch(oldOffice);
        StaticHibernateUtil.flushSession();

        client = TestObjectFactory.getClient(client.getCustomerId());
        currentMovement = client.getActiveCustomerMovement();
        Assert.assertNotNull(currentMovement);
        Assert.assertEquals(oldOffice.getOfficeId(), currentMovement.getOffice().getOfficeId());
        Assert.assertEquals(oldOffice.getOfficeId(), client.getOffice().getOfficeId());

        office = new OfficePersistence().getOffice(office.getOfficeId());
    }

    @Test
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
        StaticHibernateUtil.flushSession();
    }

    private void createObjectsForClient(String name, CustomerStatus status) throws Exception {
        office = TestObjectFactory.createOffice(OfficeLevel.BRANCHOFFICE, TestObjectFactory
                .getOffice(TestObjectFactory.HEAD_OFFICE), "customer_office", "cust");
        client = TestObjectFactory.createClient(name, getMeeting(), status);
        StaticHibernateUtil.flushSession();
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

    private void removeFees(List<FeeDto> feesToRemove) {
//        for (FeeDto fee : feesToRemove) {
//            TestObjectFactory.cleanUp(new FeePersistence().getFee(fee.getFeeIdValue()));
//        }
    }

    private List<CustomFieldDto> getCustomFields() {
        List<CustomFieldDto> fields = new ArrayList<CustomFieldDto>();
        fields.add(new CustomFieldDto(Short.valueOf("5"), "value1", CustomFieldType.ALPHA_NUMERIC.getValue()));
        fields.add(new CustomFieldDto(Short.valueOf("6"), "value2", CustomFieldType.ALPHA_NUMERIC.getValue()));
        return fields;
    }

    private void createInitialObjects() throws Exception {
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new java.util.Date(),
                MeetingType.CUSTOMER_MEETING, "Delhi");
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        client = createClient(CustomerStatus.CLIENT_ACTIVE);
        StaticHibernateUtil.flushSession();
    }

    private ClientBO createClient(CustomerStatus clientStatus) {
        return TestObjectFactory.createClient("Client", clientStatus, group);
    }

    private void createParentObjects(CustomerStatus groupStatus) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", groupStatus, center);
        StaticHibernateUtil.flushSession();
    }

    private MeetingBO getMeeting() throws Exception {
        return new MeetingBO(WeekDay.MONDAY, Short.valueOf("1"), new java.util.Date(), MeetingType.CUSTOMER_MEETING,
                "Delhi");
    }
}
