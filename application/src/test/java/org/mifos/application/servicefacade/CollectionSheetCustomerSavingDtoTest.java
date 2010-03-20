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

import org.junit.Test;

/**
 * I test {@link CollectionSheetCustomerSavingDto}.
 */
public class CollectionSheetCustomerSavingDtoTest {

    private CollectionSheetCustomerSavingDto customerSavingsDto;

    @Test
    public void shouldHaveATotalDepositOfZeroAfterConstruction() {
        // setup
        customerSavingsDto = new CollectionSheetCustomerSavingDto();

        // exercise test
        final Double totalRepayment = customerSavingsDto.getTotalDepositAmount();

        // verification
        assertThat(totalRepayment, is(Double.valueOf("0.0")));
    }

    @Test
    public void shouldHaveATotalDepositOfZeroWhenDepositsDueAndPaidAreNull() {
        // setup
        customerSavingsDto = new CollectionSheetCustomerSavingDto();
        customerSavingsDto.setDepositDue(null);
        customerSavingsDto.setDepositPaid(null);

        // exercise test
        final Double totalRepayment = customerSavingsDto.getTotalDepositAmount();

        // verification
        assertThat(totalRepayment, is(Double.valueOf("0.0")));
    }

    @Test
    public void shouldSumTotalDeposit() {
        // setup
        customerSavingsDto = new CollectionSheetCustomerSavingDto();
        customerSavingsDto.setDepositDue(BigDecimal.TEN);
        customerSavingsDto.setDepositPaid(BigDecimal.ONE);

        // exercise test
        final Double totalRepayment = customerSavingsDto.getTotalDepositAmount();

        // verification
        assertThat(totalRepayment, is(Double.valueOf("9.0")));
    }
}
