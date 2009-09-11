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
 * I test {@link CollectionSheetCustomerLoanDto}.
 */
public class CollectionSheetCustomerLoanDtoTest {

    private CollectionSheetCustomerLoanDto customerLoanDto;

    @Test
    public void shouldHaveATotalRepaymentOfZeroAfterConstruction() {
        // setup
        customerLoanDto = new CollectionSheetCustomerLoanDto();

        // exercise test
        final Double totalRepayment = customerLoanDto.getTotalRepaymentDue();
        
        // verification
        assertThat(totalRepayment, is(Double.valueOf("0.0")));
    }
    
    @Test
    public void shouldHaveATotalRepaymentOfZeroWhenFeesPenaltiesInterestAndPrincipalDueAreNull() {
        // setup
        customerLoanDto = new CollectionSheetCustomerLoanDto();
        customerLoanDto.setPrincipalDue(null);
        customerLoanDto.setPrincipalPaid(null);
        customerLoanDto.setInterestDue(null);
        customerLoanDto.setInterestPaid(null);
        customerLoanDto.setPenaltyDue(null);
        customerLoanDto.setPenaltyPaid(null);
        customerLoanDto.setMiscFeesDue(null);
        customerLoanDto.setMiscFeesPaid(null);
        customerLoanDto.setMiscPenaltyDue(null);
        customerLoanDto.setMiscPenaltyPaid(null);

        // exercise test
        final Double totalRepayment = customerLoanDto.getTotalRepaymentDue();

        // verification
        assertThat(totalRepayment, is(Double.valueOf("0.0")));
    }
    
    @Test
    public void shouldHaveATotalRepaymentSummingAllFeesPenaltiesInterestAndPrincipalDueAreNull() {
        // setup
        customerLoanDto = new CollectionSheetCustomerLoanDto();
        customerLoanDto.setPrincipalDue(BigDecimal.TEN);
        customerLoanDto.setPrincipalPaid(BigDecimal.ONE);
        customerLoanDto.setInterestDue(BigDecimal.TEN);
        customerLoanDto.setInterestPaid(BigDecimal.ONE);
        customerLoanDto.setPenaltyDue(BigDecimal.TEN);
        customerLoanDto.setPenaltyPaid(BigDecimal.ONE);
        customerLoanDto.setMiscFeesDue(BigDecimal.TEN);
        customerLoanDto.setMiscFeesPaid(BigDecimal.ONE);
        customerLoanDto.setMiscPenaltyDue(BigDecimal.TEN);
        customerLoanDto.setMiscPenaltyPaid(BigDecimal.ONE);
        customerLoanDto.setTotalAccountFees(Double.valueOf("3.5"));

        // exercise test
        final Double totalRepayment = customerLoanDto.getTotalRepaymentDue();

        // verification
        assertThat(totalRepayment, is(Double.valueOf("48.5")));
    }

    @Test
    public void shouldHaveATotalDisbursementOfZeroAfterConstruction() {
        // setup
        customerLoanDto = new CollectionSheetCustomerLoanDto();

        // exercise test
        final Double totalDisbursement = customerLoanDto.getTotalDisbursement();

        // verification
        assertThat(totalDisbursement, is(Double.valueOf("0.0")));
    }
    
    @Test
    public void shouldHaveAmountDueAtDisbursementOfZeroAfterConstruction() {
        // setup
        customerLoanDto = new CollectionSheetCustomerLoanDto();

        // exercise test
        final Double result = customerLoanDto.getAmountDueAtDisbursement();

        // verification
        assertThat(result, is(Double.valueOf("0.0")));
    }
}
