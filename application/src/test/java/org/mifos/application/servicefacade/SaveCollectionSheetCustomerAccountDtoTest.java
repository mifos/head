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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link SaveCollectionSheetCustomerAccountDto}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SaveCollectionSheetCustomerAccountDtoTest {

    private Integer validAccountId = 1;
    private Short validCurrencyId = Short.valueOf("2");
    private BigDecimal validAmount = new BigDecimal(95777.88);
    private BigDecimal invalidAmount = new BigDecimal(-34.4);

    @Test
    public void shouldBeSuccessfulObjectCreationWithValidInput() {

        Boolean newSuccess = true;
        try {
            new SaveCollectionSheetCustomerAccountDto(validAccountId, validCurrencyId, validAmount);
        } catch (SaveCollectionSheetException e) {
            newSuccess = false;
        }

        assertThat("New was not successful", newSuccess, is(true));
    }

    @Test
    public void shouldGetACCOUNTID_NULLIfAccountIdNull() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            new SaveCollectionSheetCustomerAccountDto(null, validCurrencyId, validAmount);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(InvalidSaveCollectionSheetReason.ACCOUNTID_NULL));
    }

    @Test
    public void shouldGetACCOUNTID_NEGATIVEifAccountIdNegative() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            new SaveCollectionSheetCustomerAccountDto(-3, validCurrencyId, validAmount);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(InvalidSaveCollectionSheetReason.ACCOUNTID_NEGATIVE));
    }


    @Test
    public void shouldGetCURRENCYID_NULLifCurrencyIdNull() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            new SaveCollectionSheetCustomerAccountDto(validAccountId, null, validAmount);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(InvalidSaveCollectionSheetReason.CURRENCYID_NULL));
    }

    @Test
    public void shouldGetCURRENCYID_NEGATIVEifCurrencyIdNegative() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            new SaveCollectionSheetCustomerAccountDto(validAccountId, Short.valueOf("-1"), validAmount);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(InvalidSaveCollectionSheetReason.CURRENCYID_NEGATIVE));
    }

    @Test
    public void shouldGetTOTALCUSTOMERACCOUNTCOLLECTIONFEE_NEGATIVEifTotalCustomerAccountCollectionFeeNegative() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            new SaveCollectionSheetCustomerAccountDto(validAccountId, validCurrencyId, invalidAmount);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(InvalidSaveCollectionSheetReason.TOTALCUSTOMERACCOUNTCOLLECTIONFEE_NEGATIVE));
    }


    @Test
    public void shouldGetThreeReasonsForAllInvalidInput() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            new SaveCollectionSheetCustomerAccountDto(null, null, invalidAmount);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(3));
        assertThat(InvalidSaveCollectionSheetReasons.contains(InvalidSaveCollectionSheetReason.ACCOUNTID_NULL), is(true));
        assertThat(InvalidSaveCollectionSheetReasons.contains(InvalidSaveCollectionSheetReason.CURRENCYID_NULL), is(true));
        assertThat(InvalidSaveCollectionSheetReasons.contains(InvalidSaveCollectionSheetReason.TOTALCUSTOMERACCOUNTCOLLECTIONFEE_NEGATIVE), is(true));
    }


}
