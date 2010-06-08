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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.holiday.business.service.HolidayService;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HolidayServiceFacadeWebTierTest {

    // class under test
    private HolidayServiceFacade holidayServiceFacade;

    @Mock
    private HolidayService holidayService;

    @Mock
    private HolidayDao holidayDao;

    private HolidayDetails holidayDetails;

    @Before
    public void setupAndInjectDependencies() {
        String name = "testHoliday";
        DateTime dateTime = new DateTime();
        Date fromDate = dateTime.plusDays(10).toDate();
        Date thruDate = dateTime.plusDays(20).toDate();
        RepaymentRuleTypes repaymentRule = RepaymentRuleTypes.SAME_DAY;
        holidayDetails = new HolidayDetails(name, fromDate, thruDate, repaymentRule);
        holidayServiceFacade =  new HolidayServiceFacadeWebTier(holidayService, holidayDao);
    }

    @Ignore
    @Test
    public void shouldCreateHoliday() throws Exception {

        List<Short> officeIds = Arrays.asList(Short.valueOf("1"), Short.valueOf("2"));

        holidayServiceFacade.createHoliday(holidayDetails, officeIds);

//        verify(officeDao).addHoliday(eq(Short.valueOf("1")), Mockito.any(HolidayBO.class));
//        verify(officeDao).addHoliday(eq(Short.valueOf("2")), Mockito.any(HolidayBO.class));
    }
}
