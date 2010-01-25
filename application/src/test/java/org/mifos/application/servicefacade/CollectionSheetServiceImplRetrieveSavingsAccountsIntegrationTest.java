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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;

public class CollectionSheetServiceImplRetrieveSavingsAccountsIntegrationTest extends MifosIntegrationTestCase {

    public CollectionSheetServiceImplRetrieveSavingsAccountsIntegrationTest() throws Exception {
        super();
        collectionSheetService = DependencyInjectedServiceLocator.locateCollectionSheetService();
    }

    private static CollectionSheetService collectionSheetService;
    private TestCollectionSheetRetrieveSavingsAccountsUtils collectionSheetRetrieveSavingsAccountsUtils;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        collectionSheetRetrieveSavingsAccountsUtils = new TestCollectionSheetRetrieveSavingsAccountsUtils();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            collectionSheetRetrieveSavingsAccountsUtils.clearObjects();
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }

        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testRetrievedCollectionSheetHasAnEntryForCentreSavingsAccountsWhenCenterIsOnlyCustomerInHierarchy()
            throws Exception {

        collectionSheetRetrieveSavingsAccountsUtils.setOnlyCreateCenterAndItsSavingsAccount();
        CollectionSheetDto collectionSheet = collectionSheetRetrieveSavingsAccountsUtils
                .createSampleCollectionSheetDto();

        // For the center - There should be one normal savings account and no individual savings account
        assertThat(collectionSheet.getCollectionSheetCustomer().get(0).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(collectionSheet.getCollectionSheetCustomer().get(0).getIndividualSavingAccounts().size(), is(0));

    }

    public void testSavingsAccountsEntriesReturnedWhenNoOutstandingInstallmentExists() throws Exception {

        SaveCollectionSheetDto saveCollectionSheet = collectionSheetRetrieveSavingsAccountsUtils
                .createSampleSaveCollectionSheetDto();

        // 1. Save this completed collection sheet which will leave no outstanding installment amounts
        CollectionSheetErrorsView errors = null;
        try {
            errors = collectionSheetService.saveCollectionSheet(saveCollectionSheet);
        } catch (SaveCollectionSheetException e) {
            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
        }
        assertNotNull("'errors' should not be null", errors);
        assertThat(errors.getSavingsDepNames().size(), is(0));
        assertThat(errors.getSavingsWithNames().size(), is(0));
        assertThat(errors.getLoanDisbursementAccountNumbers().size(), is(0));
        assertThat(errors.getLoanRepaymentAccountNumbers().size(), is(0));
        assertThat(errors.getCustomerAccountNumbers().size(), is(0));
        assertNull("There shouldn't have been a database error", errors.getDatabaseError());

        // 2. Retrieve the collection sheet for the same date (current date)
        CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(
                collectionSheetRetrieveSavingsAccountsUtils.getCenter().getCustomerId(), new LocalDate());

        //3. check all savings accounts are included in the collection sheet
        //check center
        assertThat(collectionSheet.getCollectionSheetCustomer().get(0).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(collectionSheet.getCollectionSheetCustomer().get(0).getIndividualSavingAccounts().size(), is(0));
        //check group with complete_group savings account
        assertThat(collectionSheet.getCollectionSheetCustomer().get(1).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(collectionSheet.getCollectionSheetCustomer().get(1).getIndividualSavingAccounts().size(), is(0));
        //check groups client
        assertThat(collectionSheet.getCollectionSheetCustomer().get(2).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(collectionSheet.getCollectionSheetCustomer().get(2).getIndividualSavingAccounts().size(), is(1));
        //check group with per_individual savings account
        assertThat(collectionSheet.getCollectionSheetCustomer().get(3).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(collectionSheet.getCollectionSheetCustomer().get(3).getIndividualSavingAccounts().size(), is(0));
        //check groups client
        assertThat(collectionSheet.getCollectionSheetCustomer().get(4).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(collectionSheet.getCollectionSheetCustomer().get(4).getIndividualSavingAccounts().size(), is(2));


    }

}
