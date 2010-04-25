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

package org.mifos.application.servicefacade;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

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
        TestDatabase.resetMySQLDatabase();
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

    public void testCollectionSheetRetrieveOnlyReturnsActiveAndInactiveSavingsAccounts() throws Exception {

        AccountPersistence accountPersistence = new AccountPersistence();
        UserContext userContext = TestUtils.makeUser();

        collectionSheetRetrieveSavingsAccountsUtils.createSampleCenterHierarchy();

        SavingsBO centerSavingsAccount = (SavingsBO) accountPersistence
                .getAccount(collectionSheetRetrieveSavingsAccountsUtils.getCenterSavingsAccount().getAccountId());
        centerSavingsAccount.setUserContext(userContext);
        centerSavingsAccount.changeStatus(AccountState.SAVINGS_INACTIVE, Short.valueOf("1"),
                "Make Center Savings Account Inactive");

        SavingsBO clientSavings = (SavingsBO) accountPersistence.getAccount(collectionSheetRetrieveSavingsAccountsUtils
                .getClientOfGroupCompleteGroupSavingsAccount().getAccountId());
        AccountPaymentEntity payment = new AccountPaymentEntity(clientSavings, new Money(clientSavings.getCurrency()),
                null, null, new PaymentTypeEntity(Short.valueOf("1")), new Date());
        AccountNotesEntity notes = new AccountNotesEntity(new java.sql.Date(System.currentTimeMillis()),
                "close client savings account", TestObjectFactory.getPersonnel(userContext.getId()), clientSavings);
        clientSavings.setUserContext(userContext);
        clientSavings.closeAccount(payment, notes, clientSavings.getCustomer());

        StaticHibernateUtil.commitTransaction();

        CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(
                collectionSheetRetrieveSavingsAccountsUtils.getCenter().getCustomerId(), new LocalDate());

        List<CollectionSheetCustomerDto> customers = collectionSheet.getCollectionSheetCustomer();

        for (CollectionSheetCustomerDto customer : customers) {
            for (CollectionSheetCustomerSavingDto customerSaving : customer.getCollectionSheetCustomerSaving()) {
                Boolean accountIsActiveOrInactive = false;
                AccountBO accountBO = accountPersistence.getAccount(customerSaving.getAccountId());
                if ((accountBO.getState().equals(AccountState.SAVINGS_ACTIVE))
                        || (accountBO.getState().equals(AccountState.SAVINGS_INACTIVE))) {
                    accountIsActiveOrInactive = true;
                }
                assertTrue("Found Account State: " + accountBO.getState().toString()
                        + " - Only Active and Inactive Savings Accounts are Allowed", accountIsActiveOrInactive);
            }
        }
    }

    public void testAllSavingsAccountsEntriesReturnedWithCorrectTotalDepositAmount() throws Exception {

        CollectionSheetDto collectionSheet = collectionSheetRetrieveSavingsAccountsUtils
                .createSampleCollectionSheetDto();

        List<CollectionSheetCustomerDto> customers = collectionSheet.getCollectionSheetCustomer();

        // check all savings accounts are included in the collection sheet and all have the correct deposit values.
        verifyAllSavingsAccountsIncluded(customers);
        // check group with complete_group savings account
        assertThat(customers.get(1).getCollectionSheetCustomerSaving().get(0).getTotalDepositAmount(), is(2.0));
        // check complete_group client
//        assertThat(customers.get(2).getCollectionSheetCustomerSaving().get(0).getTotalDepositAmount(), is(3.0));
        assertThat(customers.get(2).getIndividualSavingAccounts().get(0).getTotalDepositAmount(), is(1.0));
        // check per_individual client
//        assertThat(customers.get(4).getCollectionSheetCustomerSaving().get(0).getTotalDepositAmount(), is(5.0));

        // The individual accounts could be returned in any order
        Integer centerAccountId = customers.get(0).getCollectionSheetCustomerSaving().get(0).getAccountId();
        Integer clientIndividualSavingsIndex = getIndexMatchingAccountId(
                customers.get(4).getIndividualSavingAccounts(), centerAccountId);

        Double firstIndividualAccountAmount = 1.0;
        Double secondIndividualAccountAmount = 4.0;
        if (clientIndividualSavingsIndex.equals(1)) {
            firstIndividualAccountAmount = 4.0;
            secondIndividualAccountAmount = 1.0;
        }

        assertThat(customers.get(4).getIndividualSavingAccounts().get(0).getTotalDepositAmount(),
                is(firstIndividualAccountAmount));
        assertThat(customers.get(4).getIndividualSavingAccounts().get(1).getTotalDepositAmount(),
                is(secondIndividualAccountAmount));
    }

    public void testSavingsAccountsEntriesReturnedWithZeroTotalDepositAmountWhenNoOutstandingInstallmentExists()
            throws Exception {

        CollectionSheetDto collectionSheet = collectionSheetRetrieveSavingsAccountsUtils
                .createSampleCollectionSheetDto();

        // process
        List<CollectionSheetCustomerDto> customersAfterProcessing = convertSaveRetrieveVerifyAndReturnProcessedCollectionSheetCustomers(collectionSheet);

        // All the accounts below are set to a value but should be zero when no outstanding installments
        Double zeroDouble = 0.0;
        // check group with complete_group savings account
        assertThat(customersAfterProcessing.get(1).getCollectionSheetCustomerSaving().get(0).getTotalDepositAmount(),
                is(zeroDouble));
        // check complete_group client
        assertThat(customersAfterProcessing.get(2).getCollectionSheetCustomerSaving().get(0).getTotalDepositAmount(),
                is(zeroDouble));
        assertThat(customersAfterProcessing.get(2).getIndividualSavingAccounts().get(0).getTotalDepositAmount(),
                is(zeroDouble));
        // check per_individual client
        assertThat(customersAfterProcessing.get(4).getCollectionSheetCustomerSaving().get(0).getTotalDepositAmount(),
                is(zeroDouble));
        assertThat(customersAfterProcessing.get(4).getIndividualSavingAccounts().get(0).getTotalDepositAmount(),
                is(zeroDouble));
        assertThat(customersAfterProcessing.get(4).getIndividualSavingAccounts().get(1).getTotalDepositAmount(),
                is(zeroDouble));
    }

    public void testPartPayingVoluntarySavingsAccountResultsInAZeroTotalDepositAmountWhenCollectionSheetRetrievedAgain()
            throws Exception {

        CollectionSheetDto collectionSheet = collectionSheetRetrieveSavingsAccountsUtils
                .createSampleCollectionSheetDto();

        // pay 0.5 of the 2.0 voluntary group complete_group savings account amount due
        collectionSheet.getCollectionSheetCustomer().get(1).getCollectionSheetCustomerSaving().get(0).setDepositDue(
                new BigDecimal("0.5"));

        // process
        List<CollectionSheetCustomerDto> customersAfterProcessing = convertSaveRetrieveVerifyAndReturnProcessedCollectionSheetCustomers(collectionSheet);

        assertThat(customersAfterProcessing.get(1).getCollectionSheetCustomerSaving().get(0).getTotalDepositAmount(),
                is(0.0));
    }

    public void testPartPayingMandatorySavingsAccountResultsInAReducedTotalDepositAmountWhenCollectionSheetRetrievedAgain()
            throws Exception {

        CollectionSheetDto collectionSheet = collectionSheetRetrieveSavingsAccountsUtils
                .createSampleCollectionSheetDto();

        // pay 0.2 of the 1.0 mandatory client individual savings amount due (derived from center mandatory savings
        // account)
        collectionSheet.getCollectionSheetCustomer().get(2).getIndividualSavingAccounts().get(0).setDepositDue(
                new BigDecimal("0.2"));

        // process
        List<CollectionSheetCustomerDto> customersAfterProcessing = convertSaveRetrieveVerifyAndReturnProcessedCollectionSheetCustomers(collectionSheet);

        assertThat(customersAfterProcessing.get(2).getIndividualSavingAccounts().get(0).getTotalDepositAmount(),
                is(0.8));
    }

    public void testNotPayingVoluntarySavingsAccountForClientUnderPerIndividualGroupResultsInAFullTotalDepositAmountWhenCollectionSheetRetrievedAgain()
            throws Exception {

        CollectionSheetDto collectionSheet = collectionSheetRetrieveSavingsAccountsUtils
                .createSampleCollectionSheetDto();

        // Find the client's group individual account
        Integer groupPerIndividualAccountId = collectionSheet.getCollectionSheetCustomer().get(3)
                .getCollectionSheetCustomerSaving().get(0).getAccountId();
        Integer clientIndividualSavingsIndex = getIndexMatchingAccountId(collectionSheet.getCollectionSheetCustomer()
                .get(4).getIndividualSavingAccounts(), groupPerIndividualAccountId);

        // pay none of the 4.0 voluntary group individual savings amount due account)
        collectionSheet.getCollectionSheetCustomer().get(4).getIndividualSavingAccounts().get(
                clientIndividualSavingsIndex).setDepositDue(BigDecimal.ZERO);

        // process
        List<CollectionSheetCustomerDto> customersAfterProcessing = convertSaveRetrieveVerifyAndReturnProcessedCollectionSheetCustomers(collectionSheet);

        groupPerIndividualAccountId = customersAfterProcessing.get(3).getCollectionSheetCustomerSaving().get(0)
                .getAccountId();
        clientIndividualSavingsIndex = getIndexMatchingAccountId(customersAfterProcessing.get(4)
                .getIndividualSavingAccounts(), groupPerIndividualAccountId);
        assertThat(customersAfterProcessing.get(4).getIndividualSavingAccounts().get(clientIndividualSavingsIndex)
                .getTotalDepositAmount(), is(4.0));
    }

    /*
     * method verifies that all the savings accounts from the sample savings account center structure (created by a
     * utility method) are represented in the retrieved collection sheet and are in their appropriate list
     *
     * This is considered important because understanding individual savings can be tricky.
     */
    private void verifyAllSavingsAccountsIncluded(List<CollectionSheetCustomerDto> customers) {

        // per_individual accounts are always zero in collection sheet
        Double zeroDouble = 0.0;
        assertThat(customers.get(0).getCollectionSheetCustomerSaving().get(0).getTotalDepositAmount(), is(zeroDouble));
        assertThat(customers.get(3).getCollectionSheetCustomerSaving().get(0).getTotalDepositAmount(), is(zeroDouble));

        // structure checking
        // check center
        assertThat(customers.get(0).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(customers.get(0).getIndividualSavingAccounts().size(), is(0));

        // check group with complete_group savings account
        assertThat(customers.get(1).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(customers.get(1).getIndividualSavingAccounts().size(), is(0));

        // check groups client
        assertThat(customers.get(2).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(customers.get(2).getIndividualSavingAccounts().size(), is(1));
        assertThat(customers.get(2).getIndividualSavingAccounts().get(0).getAccountId(), is(customers.get(0)
                .getCollectionSheetCustomerSaving().get(0).getAccountId()));

        // check group with per_individual savings account
        assertThat(customers.get(3).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(customers.get(3).getIndividualSavingAccounts().size(), is(0));

        // check groups client
        assertThat(customers.get(4).getCollectionSheetCustomerSaving().size(), is(1));
        assertThat(customers.get(4).getIndividualSavingAccounts().size(), is(2));

        List<Integer> perIndividualAccountIds = new ArrayList<Integer>();
        perIndividualAccountIds.add(customers.get(0).getCollectionSheetCustomerSaving().get(0).getAccountId());
        perIndividualAccountIds.add(customers.get(3).getCollectionSheetCustomerSaving().get(0).getAccountId());
        CollectionSheetCustomerDto customer4 = customers.get(4);
        assertTrue("Client Id: " + customer4.getCustomerId() + " Account Id: "
                + customer4.getIndividualSavingAccounts().get(0).getAccountId() + " Product: "
                + customer4.getIndividualSavingAccounts().get(0).getProductShortName(), isPerIndividualAccount(
                perIndividualAccountIds, customer4.getIndividualSavingAccounts().get(0).getAccountId()));
        assertTrue("Client Id: " + customer4.getCustomerId() + " Account Id: "
                + customer4.getIndividualSavingAccounts().get(1).getAccountId() + " Product: "
                + customer4.getIndividualSavingAccounts().get(1).getProductShortName(), isPerIndividualAccount(
                perIndividualAccountIds, customer4.getIndividualSavingAccounts().get(1).getAccountId()));
    }

    /*
     * Search list of client individual savings accounts as the accounts don't have to be in accountId order
     */
    private boolean isPerIndividualAccount(List<Integer> perIndividualAccountIds, Integer accountId) {
        for (Integer individualAccountId : perIndividualAccountIds) {
            if (individualAccountId.compareTo(accountId) == 0) {
                return true;
            }
        }
        return false;
    }

    private List<CollectionSheetCustomerDto> convertSaveRetrieveVerifyAndReturnProcessedCollectionSheetCustomers(
            CollectionSheetDto collectionSheet) {

        Integer customerId = collectionSheet.getCollectionSheetCustomer().get(0).getCustomerId();

        // convert to save collection sheet format
        TestSaveCollectionSheetUtils saveCollectionSheetUtils = new TestSaveCollectionSheetUtils();
        SaveCollectionSheetDto saveCollectionSheet = saveCollectionSheetUtils.assembleSaveCollectionSheetDto(
                collectionSheet, new LocalDate());

        // Save collection sheet
        saveAndVerifyCollectionSheetSave(saveCollectionSheet);

        // Retrieve the collection sheet customer list for the same date (current date)
        List<CollectionSheetCustomerDto> customers = collectionSheetService.retrieveCollectionSheet(customerId,
                new LocalDate()).getCollectionSheetCustomer();

        // check all savings accounts are included in the collection sheet
        verifyAllSavingsAccountsIncluded(customers);

        return customers;
    }

    private void saveAndVerifyCollectionSheetSave(SaveCollectionSheetDto saveCollectionSheet) {
        CollectionSheetErrorsDto errors = null;
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
    }

    private Integer getIndexMatchingAccountId(List<CollectionSheetCustomerSavingDto> savingsAccounts, Integer accountId) {

        // If the first doesn't match the second one must (for this data)
        if (savingsAccounts.get(0).getAccountId().compareTo(accountId) == 0) {
            return 0;
        }
        return 1;
    }

}
