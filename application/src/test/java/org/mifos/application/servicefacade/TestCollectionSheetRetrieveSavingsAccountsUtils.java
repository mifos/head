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

package org.mifos.application.servicefacade;

import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.TestObjectFactory;

/**
 * Class contains utility methods for setting up, processing and configuring a sample collection sheet hierarchy for
 * testing savings account for collection sheets.
 */
public class TestCollectionSheetRetrieveSavingsAccountsUtils {

    public TestCollectionSheetRetrieveSavingsAccountsUtils() {
        collectionSheetService = DependencyInjectedServiceLocator.locateCollectionSheetService();
    }

    private Boolean onlyCreateCenterAndItsSavingsAccount = false;
    /*
     *
     */
    private CenterBO center;
    private SavingsBO centerSavingsAccount;
    private GroupBO groupCompleteGroup;
    private SavingsBO groupCompleteGroupSavingsAccount;
    private ClientBO clientOfGroupCompleteGroup;
    private SavingsBO clientOfGroupCompleteGroupSavingsAccount;
    private GroupBO groupPerIndividual;
    private SavingsBO groupPerIndividualSavingsAccount;
    private ClientBO clientOfGroupPerIndividual;
    private SavingsBO clientOfGroupPerIndividualSavingsAccount;

    private CollectionSheetService collectionSheetService;

    /**
     * Write a sample center hierarchy with savings accounts and retrieve the collection sheet information.
     *
     * Convert the collection sheet information into the format for saving.
     */
    public SaveCollectionSheetDto createSampleSaveCollectionSheetDto() {

        CollectionSheetDto collectionSheet = createSampleCollectionSheetDto();

        TestSaveCollectionSheetUtils saveCollectionSheetUtils = new TestSaveCollectionSheetUtils();

        return saveCollectionSheetUtils.assembleSaveCollectionSheetDto(collectionSheet, new LocalDate());
    }

    /**
     * Write a sample center hierarchy with savings accounts and retrieve the collection sheet information.
     */
    public CollectionSheetDto createSampleCollectionSheetDto() {

        try {
            createSampleCenterHierarchy();
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
        return collectionSheetService.retrieveCollectionSheet(center.getCustomerId(), new LocalDate());
    }

    /**
     * By default generates 1 center with a mandatory savings account (center savings accounts are always
     * PER_INDIVIDUAL)
     *
     * 1 group with a voluntary COMPLETE_GROUP savings account
     *
     * 1 client with a mandatory savings account
     *
     * 1 group with a mandatory PER_INDIVIDUAL savings account
     *
     * 1 client with a voluntary savings account
     */
    public void createSampleCenterHierarchy() throws Exception {
        // TODO - correct clientBuilder so that when there are "PER_INDIVIDUAL" savings accounts relevant to it... a
        // saving schedule is created.
        //
        // As a work-around for this problem... the two "PER_INDIVIDUAL" savings accounts (centerSavingsAccount and
        // groupPerIndividualSavingsAccount) are created last... this kicks of a process that creates schedules for all
        // the relevant clients.
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        String searchId = "10.4";

        center = new CenterBuilder().withSearchId(searchId).withMeeting(weeklyMeeting).withName("Savings Center")
                .withOffice(sampleBranchOffice()).withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.saveCustomer(center);

        if (!onlyCreateCenterAndItsSavingsAccount) {
            groupCompleteGroup = new GroupBuilder().withSearchId(searchId + ".1").withMeeting(weeklyMeeting).withName(
                    "Savings Group Complete Group").withOffice(sampleBranchOffice()).withLoanOfficer(testUser())
                    .withParentCustomer(center).build();
            IntegrationTestObjectMother.saveCustomer(groupCompleteGroup);
            groupCompleteGroupSavingsAccount = createSavingsAccount(groupCompleteGroup, "gvcg", "2.0", true, false);

            clientOfGroupCompleteGroup = new ClientBuilder().withSearchId(searchId + ".1.1").withMeeting(weeklyMeeting)
                    .withName("Savings Client Of Group Complete Group").withOffice(sampleBranchOffice())
                    .withLoanOfficer(testUser()).withParentCustomer(groupCompleteGroup).buildForIntegrationTests();
            IntegrationTestObjectMother.saveCustomer(clientOfGroupCompleteGroup);
            clientOfGroupCompleteGroupSavingsAccount = createSavingsAccount(clientOfGroupCompleteGroup, "clm", "3.0",
                    false, false);

            groupPerIndividual = new GroupBuilder().withSearchId(searchId + ".2").withMeeting(weeklyMeeting).withName(
                    "Savings Group Per Individual").withOffice(sampleBranchOffice()).withLoanOfficer(testUser())
                    .withParentCustomer(center).build();
            IntegrationTestObjectMother.saveCustomer(groupPerIndividual);

            clientOfGroupPerIndividual = new ClientBuilder().withSearchId(searchId + ".2.1").withMeeting(weeklyMeeting)
                    .withName("Savings Client Of Group Per Individual").withOffice(sampleBranchOffice())
                    .withLoanOfficer(testUser()).withParentCustomer(groupPerIndividual).buildForIntegrationTests();
            IntegrationTestObjectMother.saveCustomer(clientOfGroupPerIndividual);
            clientOfGroupPerIndividualSavingsAccount = createSavingsAccount(clientOfGroupPerIndividual, "clv", "5.0",
                    true, false);

            groupPerIndividualSavingsAccount = createSavingsAccount(groupPerIndividual, "gmi", "4.0", false, true);
        }

        centerSavingsAccount = createSavingsAccount(center, "cemi", "1.0", false, false);

    }

    public SavingsBO createSavingsAccount(CustomerBO customer, String shortName, String amount, boolean isVoluntary,
            boolean isPerIndividual) {
        // johnw - unfortunately, in the current builder code, the settings for mandatory/voluntary and
        // complete_group/per_individual are set at savings account level rather than at savings offering level as in
        // the production system.  However, this method creates a good savings account
        SavingsProductBuilder savingsProductBuilder = new SavingsProductBuilder().withName(customer.getDisplayName())
                .withShortName(shortName);
        if (customer.getCustomerLevel().getId().compareTo(CustomerLevel.CENTER.getValue()) == 0) {
            savingsProductBuilder.appliesToCentersOnly();
        }
        if (customer.getCustomerLevel().getId().compareTo(CustomerLevel.GROUP.getValue()) == 0) {
            savingsProductBuilder.appliesToGroupsOnly();
        }
        if (customer.getCustomerLevel().getId().compareTo(CustomerLevel.CLIENT.getValue()) == 0) {
            savingsProductBuilder.appliesToClientsOnly();
        }
        SavingsOfferingBO savingsProduct = savingsProductBuilder.buildForIntegrationTests();

        SavingsAccountBuilder savingsAccountBuilder = new SavingsAccountBuilder().mandatory().completeGroup()
                .withSavingsProduct(savingsProduct).withCustomer(customer).withRecommendedAmount(
                        TestUtils.createMoney(amount));
        if (isVoluntary) {
            savingsAccountBuilder.voluntary();
        }
        if (isPerIndividual) {
            savingsAccountBuilder.perIndividual();
        }
        SavingsBO savingsAccount = savingsAccountBuilder.build();

        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);
        return savingsAccount;
    }

    public CenterBO getCenter() {
        return this.center;
    }

    public SavingsBO getCenterSavingsAccount() {
        return this.centerSavingsAccount;
    }

    public SavingsBO getClientOfGroupCompleteGroupSavingsAccount() {
        return this.clientOfGroupCompleteGroupSavingsAccount;
    }

    /**
     * clears persistent objects created by this class
     */
    public void clearObjects() throws Exception {

        if (clientOfGroupPerIndividualSavingsAccount != null) {
            clientOfGroupPerIndividualSavingsAccount = (SavingsBO) StaticHibernateUtil.getSessionTL().get(
                    AccountBO.class, clientOfGroupPerIndividualSavingsAccount.getAccountId());
        }
        if (groupPerIndividualSavingsAccount != null) {
            groupPerIndividualSavingsAccount = (SavingsBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                    groupPerIndividualSavingsAccount.getAccountId());
        }
        if (clientOfGroupCompleteGroupSavingsAccount != null) {
            clientOfGroupCompleteGroupSavingsAccount = (SavingsBO) StaticHibernateUtil.getSessionTL().get(
                    AccountBO.class, clientOfGroupCompleteGroupSavingsAccount.getAccountId());
        }
        if (groupCompleteGroupSavingsAccount != null) {
            groupCompleteGroupSavingsAccount = (SavingsBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                    groupCompleteGroupSavingsAccount.getAccountId());
        }
        if (centerSavingsAccount != null) {
            centerSavingsAccount = (SavingsBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                    centerSavingsAccount.getAccountId());
        }
        if (clientOfGroupPerIndividual != null) {
            clientOfGroupPerIndividual = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class,
                    clientOfGroupPerIndividual.getCustomerId());
        }
        if (clientOfGroupCompleteGroup != null) {
            clientOfGroupCompleteGroup = (ClientBO) StaticHibernateUtil.getSessionTL().get(ClientBO.class,
                    clientOfGroupCompleteGroup.getCustomerId());
        }
        if (groupPerIndividual != null) {
            groupPerIndividual = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class,
                    groupPerIndividual.getCustomerId());
        }
        if (groupCompleteGroup != null) {
            groupCompleteGroup = (GroupBO) StaticHibernateUtil.getSessionTL().get(GroupBO.class,
                    groupCompleteGroup.getCustomerId());
        }
        if (center != null) {
            center = (CenterBO) StaticHibernateUtil.getSessionTL().get(CenterBO.class, center.getCustomerId());
        }
        TestObjectFactory.cleanUp(clientOfGroupPerIndividualSavingsAccount);
        TestObjectFactory.cleanUp(groupPerIndividualSavingsAccount);
        TestObjectFactory.cleanUp(clientOfGroupCompleteGroupSavingsAccount);
        TestObjectFactory.cleanUp(groupCompleteGroupSavingsAccount);
        TestObjectFactory.cleanUp(centerSavingsAccount);
        TestObjectFactory.cleanUp(clientOfGroupPerIndividual);
        TestObjectFactory.cleanUp(clientOfGroupCompleteGroup);
        TestObjectFactory.cleanUp(groupPerIndividual);
        TestObjectFactory.cleanUp(groupCompleteGroup);
        TestObjectFactory.cleanUp(center);
    }

    /*
     *
     * methods to configure invalid entries below
     */

    public void setOnlyCreateCenterAndItsSavingsAccount() {
        this.onlyCreateCenterAndItsSavingsAccount = true;
    }
}
