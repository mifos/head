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

package org.mifos.customers.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.aDifferentExistingLoanOfficer;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.aWeeklyMeeting;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anActiveCenter;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anExistingActiveCenter;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anExistingBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anExistingGroupUnderCenterInDifferentBranchAs;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anExistingGroupUnderCenterInSameBranchAs;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anExistingLoanOfficer;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMotherBuilderDsl.anExistingOffice;

import java.util.Locale;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.Localization;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class GroupTransferUsingCustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void cleanDatabaseTables() {
        databaseCleaner.clean();

        Locale locale = Localization.getInstance().getMainLocale();
        AuditConfigurtion.init(locale);
    }

    @Test
    public void transferingGroupToCenterInDifferentBranchCreatesNewCustomerMovementsForGroupAndChangesGroupStatusFromActiveToOnHold() throws Exception {

        // setup
        OfficeBO branchOffice = anExistingBranchOffice(new OfficeBuilder().withGlobalOfficeNum("xxxx-003").withName("builder-branch-office1").withShortName("bf1").withSearchId("1.1.1.1."));

        OfficeBO branchOffice2 = new OfficeBuilder().withParentOffice(branchOffice.getParentOffice()).branchOffice().withGlobalOfficeNum("xxxx-004").withName("builder-branch-office2").withShortName("bf1").withSearchId("1.1.1.2.").build();
        IntegrationTestObjectMother.createOffice(branchOffice2);

        CenterBuilder centerWithWeeklyMeeting = anActiveCenter().withName("center-with-no-children")
                                                                .with(aWeeklyMeeting())
                                                                .with(branchOffice)
                                                                .withLoanOfficer(anExistingLoanOfficer());

        CenterBO centerWithNoChildren = anExistingActiveCenter(centerWithWeeklyMeeting);
        GroupBO groupForTransfer = anExistingGroupUnderCenterInDifferentBranchAs(centerWithWeeklyMeeting.withName("center-with-group")
                                                                                                        .with(branchOffice2));

        // pre-verification
        assertThat(groupForTransfer.getStatus().isGroupActive(), is(true));
        assertThat(groupForTransfer.countOfCustomerMovements(), is(0));

        // exercise test
        customerService.transferGroupTo(groupForTransfer, centerWithNoChildren);

        // verification
        groupForTransfer = customerDao.findGroupBySystemId(groupForTransfer.getGlobalCustNum());

        assertThat(groupForTransfer.getStatus().isGroupActive(), is(false));
        assertThat(groupForTransfer.getStatus().isGroupOnHold(), is(true));
        assertThat(groupForTransfer.countOfCustomerMovements(), is(2));
    }

    @Test
    public void transferingGroupToCenterInSameBranchIncrementsMaxChildCountOfNewParentAndDoesNotDecrementsMaxChildCountOfOldParent() throws Exception {

        // setup
        CenterBuilder centerWithWeeklyMeeting = anActiveCenter().withName("center-with-no-children").with(aWeeklyMeeting()).with(
                anExistingOffice()).withLoanOfficer(anExistingLoanOfficer());

        CenterBO centerWithNoChildren = anExistingActiveCenter(centerWithWeeklyMeeting);
        GroupBO groupForTransfer = anExistingGroupUnderCenterInSameBranchAs(centerWithWeeklyMeeting.withName("center-with-group"));

        // pre-verification
        assertThat(centerWithNoChildren.getMaxChildCount(), is(0));
        assertThat(centerWithNoChildren.getChildren().size(), is(0));
        assertThat(groupForTransfer.getParentCustomer().getMaxChildCount(), is(1));
        final String oldGroupParentSystemId = groupForTransfer.getParentCustomer().getGlobalCustNum();

        // exercise test
        customerService.transferGroupTo(groupForTransfer, centerWithNoChildren);

        // verification
        centerWithNoChildren = customerDao.findCenterBySystemId(centerWithNoChildren.getGlobalCustNum());
        CenterBO previousParent = customerDao.findCenterBySystemId(oldGroupParentSystemId);

        assertThat(centerWithNoChildren.getMaxChildCount(), is(1));
        assertThat(centerWithNoChildren.getChildren(), is(notNullValue()));
        assertThat(centerWithNoChildren.getChildren().size(), is(1));

        assertThat(previousParent.getDisplayName(), is("center-with-group"));
        assertThat(previousParent.getMaxChildCount(), is(1));
        assertThat(previousParent.getChildren().size(), is(0));
    }

    @Test
    public void transferingGroupToCenterInSameBranchCreatesActiveCustomerHierarchyBetweenGroupAndNewParent() throws Exception {

        // setup
        CenterBuilder centerWithWeeklyMeeting = anActiveCenter().withName("center-with-no-children").with(aWeeklyMeeting()).with(
                anExistingOffice()).withLoanOfficer(anExistingLoanOfficer());

        CenterBO centerWithNoChildren = anExistingActiveCenter(centerWithWeeklyMeeting);
        GroupBO groupForTransfer = anExistingGroupUnderCenterInSameBranchAs(centerWithWeeklyMeeting.withName("center-with-group"));

        // pre-verification
        assertThat(centerWithNoChildren.getActiveCustomerHierarchy(), is(nullValue()));
        assertThat(groupForTransfer.getActiveCustomerHierarchy().getParentCustomer().getDisplayName(), is("center-with-group"));
        final String oldGroupParentSystemId = groupForTransfer.getParentCustomer().getGlobalCustNum();

        // exercise test
        customerService.transferGroupTo(groupForTransfer, centerWithNoChildren);

        // verification
        centerWithNoChildren = customerDao.findCenterBySystemId(centerWithNoChildren.getGlobalCustNum());
        CenterBO previousParent = customerDao.findCenterBySystemId(oldGroupParentSystemId);

        assertThat(groupForTransfer.getActiveCustomerHierarchy(), is(notNullValue()));
        assertThat(groupForTransfer.getActiveCustomerHierarchy().getParentCustomer().getDisplayName(), is("center-with-no-children"));
        assertThat(previousParent.getActiveCustomerHierarchy(), is(nullValue()));
    }

    @Test
    public void transferingGroupToCenterInSameBranchShouldModifyGroupToHaveSameLoanOfficerAsReceivingCenter() throws Exception {

        // setup
        CenterBuilder centerWithWeeklyMeeting = anActiveCenter().withName("center-with-no-children").with(aWeeklyMeeting()).with(
                anExistingOffice()).withLoanOfficer(anExistingLoanOfficer());

        CenterBO centerWithNoChildren = anExistingActiveCenter(centerWithWeeklyMeeting);
        GroupBO groupForTransfer = anExistingGroupUnderCenterInSameBranchAs(centerWithWeeklyMeeting.withName("center-with-group").withLoanOfficer(aDifferentExistingLoanOfficer()));

        // pre-verification
        assertThat(centerWithNoChildren.getPersonnel().getDisplayName(), is("loan officer"));
        assertThat(groupForTransfer.getPersonnel().getDisplayName(), is("mifos"));

        // exercise test
        customerService.transferGroupTo(groupForTransfer, centerWithNoChildren);

        // verification
        centerWithNoChildren = customerDao.findCenterBySystemId(centerWithNoChildren.getGlobalCustNum());

        assertThat(centerWithNoChildren.getPersonnel().getDisplayName(), is("loan officer"));
        assertThat(groupForTransfer.getPersonnel().getDisplayName(), is("loan officer"));
    }
}