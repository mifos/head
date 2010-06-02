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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.ValidationException;

/**
 *
 */
public class HolidayServiceFacadeWebTier implements HolidayServiceFacade {

    private final OfficePersistence officePersistence;

    public HolidayServiceFacadeWebTier(OfficePersistence officePersistence) {
        this.officePersistence = officePersistence;
    }

    @Override
    public void createHoliday(HolidayDetails holidayDetails, List<Short> officeIds) throws ServiceException {
        try {
            holidayDetails.validate();
            HolidayBO holiday = new HolidayBO(holidayDetails);
            for (Short officeId : officeIds) {
                officePersistence.addHoliday(officeId, holiday);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        } catch (ValidationException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public Map<String, List<HolidayOfficeNames>> holidaysByYear() throws ServiceException{
        HolidayDaoHibernate holidayDaoHibernate = new HolidayDaoHibernate(new GenericDaoHibernate());
        List<HolidayBO> holidays = holidayDaoHibernate.findAllHolidays();
        Map<String, List<HolidayOfficeNames>> holidaysByYear = new TreeMap<String, List<HolidayOfficeNames>>();
        for (HolidayBO holiday : holidays) {
            HolidayDetails holidayDetail = new HolidayDetails(holiday.getHolidayName(), holiday.getHolidayFromDate(), holiday
                    .getHolidayThruDate(), holiday.getRepaymentRuleType());
            int year = holiday.getThruDate().getYear();
            List<HolidayOfficeNames> holidaysInYear = holidaysByYear.get(Integer.toString(year));
            if (holidaysInYear == null) {
                holidaysInYear = new LinkedList<HolidayOfficeNames>();
            }
            holidaysInYear.add(new HolidayOfficeNames(holidayDetail,holidayDaoHibernate.applicableOffices(holiday.getId())));
            holidaysByYear.put(Integer.toString(year), holidaysInYear);
        }
        return holidaysByYear;
    }

}
