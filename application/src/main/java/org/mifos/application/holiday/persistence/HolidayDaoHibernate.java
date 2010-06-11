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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Query;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.calendar.CalendarEvent;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.core.MifosRuntimeException;

public class HolidayDaoHibernate implements HolidayDao {

    private final GenericDao genericDao;

    public HolidayDaoHibernate(final GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public HolidayBO findHolidayById(Integer id) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("holidayId", id);
        return (HolidayBO) this.genericDao.executeUniqueResultNamedQuery("findById", queryParameters);
    }

    @Override
    public final void save(final Holiday holiday) {
        this.genericDao.createOrUpdate(holiday);
    }

    @Override
    public List<Holiday> findCurrentAndFutureOfficeHolidaysEarliestFirst(final Short officeId) {

        return retrieveCurrentAndFutureHolidaysForOfficeHierarchyInAscendingOrder(officeId);
    }

    @SuppressWarnings("unchecked")
    private List<Holiday> retrieveCurrentAndFutureHolidaysForOfficeHierarchyInAscendingOrder(final Short officeId) {
        List<Holiday> orderedHolidays = new ArrayList<Holiday>();
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CURRENT_DATE", new LocalDate().toString());
        queryParameters.put("OFFICE_ID", officeId);

        List<HolidayBO> queryResult = (List<HolidayBO>) genericDao.executeNamedQuery("holiday.findCurrentAndFutureOfficeHolidaysEarliestFirst", queryParameters);
        orderedHolidays.addAll(queryResult);

        return orderedHolidays;
    }

    @Override
    public final List<Holiday> findAllHolidaysThisYearAndNext(final short officeId) {
        return retrieveCurrentAndFutureHolidaysForOfficeHierarchyInAscendingOrder(officeId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<HolidayBO> findAllHolidays() {
        return (List<HolidayBO>) genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_HOLIDAYS,
                new HashMap<String, Object>());
    }

    @SuppressWarnings("unchecked")
    public List<HolidayBO> findAllHolidaysForYear(short officeId, final int year) {
        SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("en", "GB"));
        isoDateFormat.setLenient(false);
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

    @SuppressWarnings("unchecked")
    public List<Holiday> getUnAppliedHolidays() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("FLAG", YesNoFlag.NO.getValue());
        return (List<Holiday>) genericDao.executeNamedQuery(NamedQueryConstants.GET_HOLIDAYS_BY_FLAG, queryParameters);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> applicableOffices(Integer id) {
        Query sqlQuery = ((GenericDaoHibernate) genericDao).getHibernateUtil().getSessionTL().getNamedQuery(NamedQueryConstants.GET_APPLICABLE_OFFICES_FOR_HOLIDAYS);
        return sqlQuery.setInteger("HOLIDAY_ID", id).list();
    }

    @Override
    public final CalendarEvent findCalendarEventsForThisYearAndNext(short officeId) {

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        List<Holiday> upcomingHolidays = retrieveCurrentAndFutureHolidaysForOfficeHierarchyInAscendingOrder(officeId);

        return new CalendarEvent(workingDays, upcomingHolidays);
    }
}