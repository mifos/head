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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link SaveCollectionSheetCustomerDto}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SaveCollectionSheetCustomerDtoTest {

    private Integer validcustomerId = 0;
    private Integer invalidcustomerId = -1;

    @Test
    public void shouldBeSuccessfulObjectCreationWithValidInput() {

        Boolean newSuccess = true;
        try {
            new SaveCollectionSheetCustomerDto(validcustomerId, null, null, null, null, null, null);
        } catch (SaveCollectionSheetException e) {
            newSuccess = false;
        }

        assertThat("New was not successful", newSuccess, is(true));
    }

    @Test
    public void shouldGetCUSTOMERID_NULLIfCustomerIdNull() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            new SaveCollectionSheetCustomerDto(null, null, null, null, null, null, null);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(InvalidSaveCollectionSheetReason.CUSTOMERID_NULL));
    }

    @Test
    public void shouldGetCUSTOMERID_NEGATIVEIfCustomerIdNegative() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            new SaveCollectionSheetCustomerDto(invalidcustomerId, null, null, null, null, null, null);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(InvalidSaveCollectionSheetReason.CUSTOMERID_NEGATIVE));
    }

    @Test
    public void shouldGetPARENTCUSTOMERID_NEGATIVEIfParentCustomerIdNegative() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            new SaveCollectionSheetCustomerDto(validcustomerId, invalidcustomerId, null, null, null, null, null);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0),
                is(InvalidSaveCollectionSheetReason.PARENTCUSTOMERID_NEGATIVE));
    }

    @Test
    public void shouldGetACCOUNT_LISTED_MORE_THAN_ONCEIfDuplicateCustomerLoanAccount() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        Integer validAccountId = 78;
        Short validCurrencyId = Short.valueOf("2");
        try {
            List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans = new ArrayList<SaveCollectionSheetCustomerLoanDto>();
            saveCollectionSheetCustomerLoans.add(new SaveCollectionSheetCustomerLoanDto(validAccountId,
                    validCurrencyId, null, null));
            saveCollectionSheetCustomerLoans.add(new SaveCollectionSheetCustomerLoanDto(validAccountId,
                    validCurrencyId, null, null));

            new SaveCollectionSheetCustomerDto(validcustomerId, null, null, null, saveCollectionSheetCustomerLoans,
                    null, null);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0),
                is(InvalidSaveCollectionSheetReason.ACCOUNT_LISTED_MORE_THAN_ONCE));
    }

    @Test
    public void shouldGetACCOUNT_LISTED_MORE_THAN_ONCEIfDuplicateCustomerSavingsAccount() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        Integer validAccountId = 78;
        Short validCurrencyId = Short.valueOf("2");
        try {
            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSaving = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
            saveCollectionSheetCustomerSaving.add(new SaveCollectionSheetCustomerSavingDto(validAccountId,
                    validCurrencyId, null, null));
            saveCollectionSheetCustomerSaving.add(new SaveCollectionSheetCustomerSavingDto(validAccountId,
                    validCurrencyId, null, null));

            new SaveCollectionSheetCustomerDto(validcustomerId, null, null, null, null,
                    saveCollectionSheetCustomerSaving, null);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0),
                is(InvalidSaveCollectionSheetReason.ACCOUNT_LISTED_MORE_THAN_ONCE));
    }

    @Test
    public void shouldGetACCOUNT_LISTED_MORE_THAN_ONCEIfDuplicateCustomerIndividualSavingsAccount() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        Integer validAccountId = 78;
        Short validCurrencyId = Short.valueOf("2");
        try {
            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerIndividualSavings = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
            saveCollectionSheetCustomerIndividualSavings.add(new SaveCollectionSheetCustomerSavingDto(validAccountId,
                    validCurrencyId, null, null));
            saveCollectionSheetCustomerIndividualSavings.add(new SaveCollectionSheetCustomerSavingDto(validAccountId,
                    validCurrencyId, null, null));

            new SaveCollectionSheetCustomerDto(validcustomerId, null, null, null, null,
                    null, saveCollectionSheetCustomerIndividualSavings);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0),
                is(InvalidSaveCollectionSheetReason.ACCOUNT_LISTED_MORE_THAN_ONCE));
    }

    @Test
    public void shouldGetTwoReasonsForAllInvalidInput() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            new SaveCollectionSheetCustomerDto(null, invalidcustomerId, null, null, null, null, null);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(2));
        assertThat(InvalidSaveCollectionSheetReasons.contains(InvalidSaveCollectionSheetReason.CUSTOMERID_NULL),
                is(true));
        assertThat(InvalidSaveCollectionSheetReasons
                .contains(InvalidSaveCollectionSheetReason.PARENTCUSTOMERID_NEGATIVE), is(true));
    }

}
