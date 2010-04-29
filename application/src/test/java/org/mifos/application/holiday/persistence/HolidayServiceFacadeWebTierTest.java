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
package org.mifos.application.holiday.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Work in progress on story 1556
 */
@RunWith(MockitoJUnitRunner.class)
public class HolidayServiceFacadeWebTierTest {

    // class under test
    private HolidayServiceFacade holidayServiceFacade;

    // collaborators
//    @Mock
//    private LoanProductDao loanProductDao;


    @Before
    public void setupAndInjectDependencies() {
        holidayServiceFacade = new HolidayServiceFacadeWebTier();
    }

    @Test
    public void createHolidayShouldSucceed() {

        List<Short> officeIds = new ArrayList<Short>();
        LocalDate fromDate = new LocalDate(2010,4,10);
        LocalDate toDate = new LocalDate(2010,4,14);
        short repaymentRuleId = 1;
        HolidayDto holidayDto = holidayServiceFacade.createHoliday(officeIds, fromDate, toDate, repaymentRuleId);
        assertThat(holidayDto.getFromDate(), is(fromDate));
        assertThat(holidayDto.getToDate(), is(toDate));
        assertThat(holidayDto.getRepaymentRuleId(), is(repaymentRuleId));
        assertTrue(holidayDto.getOfficesForHoliday().containsAll(officeIds));
/*
        // setup
        when(customer.getCustomerLevel()).thenReturn(customerLevelEntity);
        when(loanProductDao.findActiveLoanProductsApplicableToCustomerLevel(customerLevelEntity)).thenReturn(
                activeLoanProducts);
        when(customer.getCustomerMeetingValue()).thenReturn(meeting);
        when(activeLoanProduct.getLoanOfferingMeetingValue()).thenReturn(meeting);

        // exercise test
        List<LoanOfferingBO> activeLoanProductsForCustomer = loanServiceFacade
                .loadActiveProductsApplicableForCustomer(customer);

        // verification
        assertThat(activeLoanProductsForCustomer, hasItem(activeLoanProduct));
*/
    }
}
