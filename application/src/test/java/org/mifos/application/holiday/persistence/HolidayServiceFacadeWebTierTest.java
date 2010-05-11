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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Work in progress on story 1556
 */
@RunWith(MockitoJUnitRunner.class)
public class HolidayServiceFacadeWebTierTest {

    HolidayDetails holidayDetails;
    HolidayServiceFacade holidayServiceFacade;
    OfficePersistence officePersistence;

    @Before
    public void setupAndInjectDependencies() {
        String name = "testHoliday";
        DateTime dateTime = new DateTime();
        Date fromDate = dateTime.plusDays(10).toDate();
        Date thruDate = dateTime.plusDays(20).toDate();
        RepaymentRuleTypes repaymentRule = RepaymentRuleTypes.SAME_DAY;
        holidayDetails = new HolidayDetails(name, fromDate, thruDate, repaymentRule);
        officePersistence = Mockito.mock(OfficePersistence.class);
        holidayServiceFacade = new HolidayServiceFacadeWebTier(officePersistence);
    }

    @Test
    public void shouldCreateHoliday() throws ServiceException, PersistenceException {
        List<Short> officeIds = new ArrayList<Short>();
        Short officeId1 = new Short((short) 1);
        Short officeId2 = new Short((short) 2);
        officeIds.add(officeId1);
        officeIds.add(officeId2);
        holidayServiceFacade.createHoliday(holidayDetails, officeIds);
        Mockito.verify(officePersistence, Mockito.times(1)).addHoliday(Mockito.eq(officeId1),
                Mockito.any(HolidayBO.class));
        Mockito.verify(officePersistence, Mockito.times(1)).addHoliday(Mockito.eq(officeId2),
                Mockito.any(HolidayBO.class));
    }
}
