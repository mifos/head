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
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class HolidayServiceFacadeWebTierIntegrationTest extends MifosIntegrationTestCase {
    HolidayDetails holidayDetails;
    HolidayServiceFacade holidayServiceFacade;

    public HolidayServiceFacadeWebTierIntegrationTest() throws Exception {
        super();
    }

    @Override
    public void setUp() throws Exception {
        String name = "testHoliday";
        DateTime dateTime = new DateTime();
        Date fromDate = dateTime.plusDays(10).toDate();
        Date thruDate = dateTime.plusDays(20).toDate();
        RepaymentRuleTypes repaymentRule = RepaymentRuleTypes.SAME_DAY;
        holidayDetails = new HolidayDetails(name, fromDate, thruDate, repaymentRule);
        List<Short> officeIds = new ArrayList<Short>();
        Short officeId = new Short((short) 1);
        officeIds.add(officeId);
        holidayServiceFacade = new HolidayServiceFacadeWebTier(new OfficePersistence());
        holidayServiceFacade.createHoliday(holidayDetails, officeIds);
        StaticHibernateUtil.getSessionTL().flush();
    }

    @Override
    protected void tearDown() throws Exception {
        Session session = StaticHibernateUtil.getSessionTL();
        if (session.isOpen()) {
            session.clear();
            Transaction transaction = session.getTransaction();
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    public void testShouldCreateHolidayForOfficeAndChildOffices() throws PersistenceException {
        assertEquals(1, new OfficePersistence().getOffice((short) 1).getHolidays().size());
        assertEquals(1, new OfficePersistence().getOffice((short) 2).getHolidays().size());
        assertEquals(1, new OfficePersistence().getOffice((short) 3).getHolidays().size());
    }

    public void testShouldReturnHolidaysByYear() throws ServiceException {
         Map<String, List<HolidayOfficeNames>> holidaysByYear = holidayServiceFacade.holidaysByYear();
         assertNotNull(holidaysByYear);
         assertEquals(1, holidaysByYear.size());
         List<HolidayOfficeNames> holidayOfficeNames = holidaysByYear.get(Integer.toString(new DateTime(holidayDetails
         .getFromDate()).getYear()));
         assertNotNull(holidayOfficeNames);
         assertEquals(1, holidayOfficeNames.size());
         assertEquals("testHoliday", holidayOfficeNames.get(0).getHolidayDetails().getName());
         assertEquals(1, holidayOfficeNames.get(0).getOfficeNames().size());
         assertEquals("Mifos HO ", holidayOfficeNames.get(0).getOfficeNames().get(0));
    }
}
