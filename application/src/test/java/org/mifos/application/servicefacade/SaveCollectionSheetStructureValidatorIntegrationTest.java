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

package org.mifos.application.servicefacade;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.client.business.AttendanceType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;

public class SaveCollectionSheetStructureValidatorIntegrationTest extends MifosIntegrationTestCase {

    private SaveCollectionSheetStructureValidator savecollectionSheetStructureValidator;
    private TestSaveCollectionSheetUtils saveCollectionSheetUtils;
    private TestCollectionSheetRetrieveSavingsAccountsUtils collectionSheetRetrieveSavingsAccountsUtils;

    @Autowired
    private LegacyAccountDao legacyAccountDao;

    @Before
    public void setUp() throws Exception {
        enableCustomWorkingDays();
        saveCollectionSheetUtils = new TestSaveCollectionSheetUtils();
        savecollectionSheetStructureValidator = new SaveCollectionSheetStructureValidator();
        collectionSheetRetrieveSavingsAccountsUtils = new TestCollectionSheetRetrieveSavingsAccountsUtils();
    }

    @After
    public void tearDown() throws Exception {
        try {
            saveCollectionSheetUtils.clearObjects();
            collectionSheetRetrieveSavingsAccountsUtils.clearObjects();
        } catch (Exception e) {

        }

        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testShouldBeNoStructureValidationErrorsWithValidInput() throws Exception {

        SaveCollectionSheetDto saveCollectionSheet = saveCollectionSheetUtils.createSampleSaveCollectionSheet();

        Boolean newSuccess = true;
        try {
            savecollectionSheetStructureValidator.execute(saveCollectionSheet);
        } catch (SaveCollectionSheetException e) {
            newSuccess = false;
        }

        assertThat("There were structure validation errors.", newSuccess, is(true));
    }

    @Test
    public void testShouldGetINVALID_TOP_CUSTOMERIfTopCustomerNotFound() throws Exception {

        LocalDate validDate = new LocalDate();
        Short validPaymentType = PaymentTypes.CHEQUE.getValue();
        Short validUserId = Short.valueOf("1");
        Integer invalidCustomerId = 500000;

        SaveCollectionSheetDto saveCollectionSheet = null;
        try {
            List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();
            SaveCollectionSheetCustomerDto saveCollectionSheetCustomer = new SaveCollectionSheetCustomerDto(
                    invalidCustomerId, null, null, null, null, null, null);
            saveCollectionSheetCustomers.add(saveCollectionSheetCustomer);

            saveCollectionSheet = new SaveCollectionSheetDto(saveCollectionSheetCustomers, validPaymentType, validDate,
                    null, null, validUserId);
        } catch (SaveCollectionSheetException e) {
            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
        }

        verifyInvalidReason(saveCollectionSheet, InvalidSaveCollectionSheetReason.INVALID_TOP_CUSTOMER);
    }

    @Test
    public void testShouldGetCUSTOMER_NOT_FOUNDIfInvalidCustomerIdInjected() throws Exception {

        saveCollectionSheetUtils.setFirstClientDoesntExist();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.CUSTOMER_NOT_FOUND);
    }

    @Test
    public void testShouldGetATTENDANCE_TYPE_NULLIfNullClientAttendanceTypeInjected() throws Exception {

        saveCollectionSheetUtils.setFirstClientAttendanceType(null);

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.ATTENDANCE_TYPE_NULL);

    }

    @Test
    public void testShouldGetUNSUPPORTED_ATTENDANCE_TYPEIfInvalidClientAttendanceTypeInjected() throws Exception {

        saveCollectionSheetUtils.setFirstClientAttendanceType(Short.valueOf("99"));

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.UNSUPPORTED_ATTENDANCE_TYPE);

    }

    @Test
    public void testShouldGetATTENDANCE_TYPE_ONLY_VALID_FOR_CLIENTSIfNonNullGroupAttendanceTypeInjected()
            throws Exception {

        saveCollectionSheetUtils.setFirstGroupAttendanceType(AttendanceType.PRESENT.getValue());

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.ATTENDANCE_TYPE_ONLY_VALID_FOR_CLIENTS);

    }

    @Test
    public void testShouldGetINDIVIDUAL_SAVINGS_ACCOUNTS_ONLY_VALID_FOR_CLIENTSIfGroupHasIndividualSavingsAccount()
            throws Exception {

        saveCollectionSheetUtils.setIndividualSavingsAccountUnderFirstGroup();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.INDIVIDUAL_SAVINGS_ACCOUNTS_ONLY_VALID_FOR_CLIENTS);

    }

    @Test
    public void testShouldGetINVALID_CUSTOMER_PARENTIfClientParentInvalid() throws Exception {

        saveCollectionSheetUtils.setFirstClientParentIdInvalid();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.INVALID_CUSTOMER_PARENT);

    }

    @Test
    public void testShouldGetCUSTOMER_IS_NOT_PART_OF_TOPCUSTOMER_HIERARCHYIfCustomerIdFromAnotherCenterInjected()
            throws Exception {

        saveCollectionSheetUtils.addAnotherClientFromADifferentCenter();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.CUSTOMER_IS_NOT_PART_OF_TOPCUSTOMER_HIERARCHY);

    }

    @Test
    public void testShouldGetINVALID_CURRENCYIfNotDefaultCurrency() throws Exception {

        saveCollectionSheetUtils.setLoanAccountInvalidCurrency();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.INVALID_CURRENCY);

    }

    @Test
    public void testShouldGetACCOUNT_NOT_FOUNDIfInvalidLoanAccountIdInjected() throws Exception {

        saveCollectionSheetUtils.setLoanAccountIdInvalid();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.ACCOUNT_NOT_FOUND);

    }

    @Test
    public void testShouldGetINVALID_LOAN_ACCOUNT_STATUSIfLoanAccountCancelled() throws Exception {

        saveCollectionSheetUtils.setLoanAccountCancelled();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.INVALID_LOAN_ACCOUNT_STATUS);

    }

    public void ignore_testShouldGetINVALID_SAVINGS_ACCOUNT_STATUSIfSavingsAccountClosed() throws Exception {

        LocalDate transactionDate = new LocalDate();

        // create a center hierarchy with savings accounts
        collectionSheetRetrieveSavingsAccountsUtils.createSampleCenterHierarchy();

        // retrieve the collection sheet for today
        CollectionSheetService collectionSheetService = ApplicationContextProvider.getBean(CollectionSheetService.class);
        CollectionSheetDto collectionSheet = collectionSheetService.retrieveCollectionSheet(
                collectionSheetRetrieveSavingsAccountsUtils.getCenter().getCustomerId(), transactionDate);

        // assemble dto for saving collection sheet
        SaveCollectionSheetDto saveCollectionSheet = saveCollectionSheetUtils.assembleSaveCollectionSheetDto(
                collectionSheet, transactionDate);

        // close a savings account that is about to be saved
        UserContext userContext = TestUtils.makeUser();

        SavingsBO clientSavings = (SavingsBO) legacyAccountDao.getAccount(collectionSheetRetrieveSavingsAccountsUtils
                .getClientOfGroupCompleteGroupSavingsAccount().getAccountId());
        AccountPaymentEntity payment = new AccountPaymentEntity(clientSavings, new Money(clientSavings.getCurrency()),
                null, null, new PaymentTypeEntity(Short.valueOf("1")), new Date());
        AccountNotesEntity notes = new AccountNotesEntity(new java.sql.Date(System.currentTimeMillis()),
                "close client savings account", TestObjectFactory.getPersonnel(userContext.getId()), clientSavings);
        clientSavings.setUserContext(userContext);

//        clientSavings.closeAccount(payment, notes, clientSavings.getCustomer());
        StaticHibernateUtil.flushSession();

        // Save collection sheet and test for errors returned
        verifyInvalidReason(saveCollectionSheet, InvalidSaveCollectionSheetReason.INVALID_SAVINGS_ACCOUNT_STATUS);
    }

    @Test
    public void testShouldGetACCOUNT_DOESNT_BELONG_TO_CUSTOMERIfLoanAccountIdPlacedUnderGroup() throws Exception {

        saveCollectionSheetUtils.setLoanAccountUnderFirstGroupWithFirstClientLoanId();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.ACCOUNT_DOESNT_BELONG_TO_CUSTOMER);

    }

    public void ignore_testShouldGetACCOUNT_NOT_A_CUSTOMER_ACCOUNTIfLoanAccountIdReplacesCustomerAccountIdForFirstClient()
            throws Exception {

        saveCollectionSheetUtils.setCustomerAccountIdtoLoanAccountIdForFirstClient();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.ACCOUNT_NOT_A_CUSTOMER_ACCOUNT);

    }

    @Test
    public void testShouldGetACCOUNT_NOT_A_LOAN_ACCOUNTIfCustomerAccountIdReplacesLoanAccountIdForFirstClient()
            throws Exception {

        saveCollectionSheetUtils.setLoanAccountIdtoCustomerAccountIdForFirstClient();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.ACCOUNT_NOT_A_LOAN_ACCOUNT);

    }

    @Test
    public void testShouldGetACCOUNT_NOT_A_SAVINGS_ACCOUNTIfLoanAccountIdReplacesSavingsAccountIdForFirstClient()
            throws Exception {

        saveCollectionSheetUtils.setSavingsAccountIdtoLoanAccountIdForFirstClient();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.ACCOUNT_NOT_A_SAVINGS_ACCOUNT);

    }

    @Test
    public void testShouldGetINVALID_DATEIfInvalidTransactionDateTwoDaysInFuture() throws Exception {

        saveCollectionSheetUtils.setInvalidTransactionDate();

        createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason.INVALID_DATE);

    }

    private void createSampleCollectionSheetAndVerifyInvalidReason(InvalidSaveCollectionSheetReason invalidReason)
            throws Exception {

        SaveCollectionSheetDto saveCollectionSheet = saveCollectionSheetUtils.createSampleSaveCollectionSheet();

        verifyInvalidReason(saveCollectionSheet, invalidReason);
    }

    private void verifyInvalidReason(SaveCollectionSheetDto saveCollectionSheet,
            InvalidSaveCollectionSheetReason invalidReason) throws Exception {

        List<InvalidSaveCollectionSheetReason> invalidSaveCollectionSheetReasons = null;

        try {
            savecollectionSheetStructureValidator.execute(saveCollectionSheet);
        } catch (SaveCollectionSheetException e) {
            invalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        Assert.assertNotNull("List was not set", invalidSaveCollectionSheetReasons);
        assertThat(invalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(invalidSaveCollectionSheetReasons.get(0), is(invalidReason));
    }
}
