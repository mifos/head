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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Query;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.calendar.CalendarEvent;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.office.persistence.AccountOffice;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class HolidayDaoHibernate extends Persistence implements HolidayDao {

    private final GenericDao genericDao;

    public HolidayDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public final List<Holiday> findAllHolidaysThisYearAndNext(short officeId) {
        DateTime today = new DateTime();

        List<HolidayBO> holidaysThisYear = findAllHolidaysForYear(officeId, today.getYear());
        List<HolidayBO> holidaysNextYear = findAllHolidaysForYear(officeId, today.plusYears(1).getYear());

        List<Holiday> orderedHolidays = new ArrayList<Holiday>(holidaysThisYear);
        orderedHolidays.addAll(holidaysNextYear);

        return orderedHolidays;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HolidayBO> findAllHolidays() {
        return (List<HolidayBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_HOLIDAYS,
                new HashMap<String, Object>());
    }

    @SuppressWarnings("unchecked")
    public List<HolidayBO> findAllHolidaysForYear(short officeId, final int year) {
        SimpleDateFormat isoDateFormat = isoDateFormat();
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        try {
            queryParameters.put("OFFICE_ID", officeId);
            queryParameters.put("START_OF_YEAR", isoDateFormat.parse(year + "-01-01"));
            queryParameters.put("END_OF_YEAR", isoDateFormat.parse(year + "-12-31"));
        } catch (ParseException e) {
            throw new MifosRuntimeException(e);
        }
        return (List<HolidayBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_OFFICE_HOLIDAYS, queryParameters);
    }

    private SimpleDateFormat isoDateFormat() {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en", "GB"));
        isoDateFormat.setLenient(false);
        return isoDateFormat;
    }


    @SuppressWarnings("unchecked")
   public List<Holiday> getUnAppliedHolidays() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("FLAG", YesNoFlag.NO.getValue());
        return (List<Holiday>) genericDao.executeNamedQuery(NamedQueryConstants.GET_HOLIDAYS_BY_FLAG, queryParameters);
    }



    @Override
    public final void save(final Holiday holiday) throws PersistenceException {
        createOrUpdate(holiday);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> applicableOffices(Integer id) {
        Query sqlQuery = getSession().getNamedQuery(NamedQueryConstants.GET_APPLICABLE_OFFICES_FOR_HOLIDAYS);
        return sqlQuery.setInteger("HOLIDAY_ID", id).list();
    }

    @Override
    public final CalendarEvent findCalendarEventsForThisYearAndNext(short officeId) {

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        List<Holiday> upcomingHolidays = this.findAllHolidaysThisYearAndNext(officeId);

        return new CalendarEvent(workingDays, upcomingHolidays);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Map<Short, List<HolidayBO>> unappliedOfficeHolidays(Collection<Short> officeIds) {
        Query namedQuery = getSession().getNamedQuery(NamedQueryConstants.GET_UNAPPLIED_OFFICE_HOLIDAYS);
        List<OfficeHoliday> unappliedOfficeHolidays = namedQuery.setParameterList("OFFICE_IDS", officeIds)
                .setParameter("FLAG", YesNoFlag.NO.getValue()).list();
        return toMap(unappliedOfficeHolidays);
    }

    private Map<Short, List<HolidayBO>> toMap(List<OfficeHoliday> officeHolidays) {
        Map<Short, List<HolidayBO>> result = new HashMap<Short, List<HolidayBO>>();
        for (OfficeHoliday officeHoliday : officeHolidays) {
            Short officeId = officeHoliday.getOfficeId();
            List<HolidayBO> holidays = result.get(officeId);
            if (holidays == null) {
                holidays = new LinkedList<HolidayBO>();
                result.put(officeId, holidays);
            }
            holidays.add(officeHoliday.getHoliday());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Map<Short, List<HolidayBO>> holidaysForOffices(Collection<Short> officeIds, int startYear, int endYear) {
        SimpleDateFormat isoDateFormat = isoDateFormat();
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        try {
            queryParameters.put("START_OF_YEAR", isoDateFormat.parse(startYear + "-01-01"));
            queryParameters.put("END_OF_YEAR", isoDateFormat.parse(endYear + "-12-31"));
        } catch (ParseException e) {
            throw new MifosRuntimeException(e);
        }
        Query namedQuery = getSession().getNamedQuery(NamedQueryConstants.GET_HOLIDAYS_FOR_OFFICES);
        List<OfficeHoliday> holidaysForOffices = namedQuery.setParameterList("OFFICE_IDS", officeIds)
                .setProperties(queryParameters).list();
        return toMap(holidaysForOffices);
    }
}