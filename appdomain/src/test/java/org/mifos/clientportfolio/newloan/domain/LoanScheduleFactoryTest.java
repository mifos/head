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

package org.mifos.clientportfolio.newloan.domain;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.application.master.business.MifosCurrency;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoanScheduleFactoryTest {

    private LoanScheduleFactory loanScheduleFactory;

    @Mock private LoanOfferingBO loanProduct;
    @Mock private MifosCurrency currency;
    @Mock private FeeDao feeDao;

    @Before
    public void setup() {
        loanScheduleFactory = new IndividualLoanScheduleFactory(feeDao);
    }

    @Ignore
    @Test
    public void shouldCreateLoanSchedule() {

        // setup
        List<DateTime> loanScheduleDates = new ArrayList<DateTime>();
        loanScheduleDates.add(new DateTime().minusWeeks(5));

        // stubbing
        when(loanProduct.getCurrency()).thenReturn(currency);
//        when(loanProduct.getLoanOfferingMeetingValue()).thenReturn(new MeetingBuilder().build());
        when(loanProduct.getInterestType()).thenReturn(InterestType.FLAT);

        // exercise test
//        IndividualLoanSchedule loanSchedule = loanScheduleFactory.create(loanScheduleDates, loanProduct, null, null, null);

        // verification
//        verify(loanProductDao).findBySystemId(loanProductId.globalId());
//        assertThat(loanSchedule, is(notNullValue()));
    }
}