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

package org.mifos.application.holiday.business.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.util.helpers.HolidayConstants;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.dto.domain.HolidayDetails;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;

public class HolidayServiceImpl implements HolidayService {

    private final OfficeDao officeDao;
    private final HolidayDao holidayDao;
    private final HibernateTransactionHelper hibernateTransactionHelper;
    private final FiscalCalendarRules fiscalCalendarRules;

    @Autowired
    public HolidayServiceImpl(OfficeDao officeDao, HolidayDao holidayDao,
                              HibernateTransactionHelper hibernateTransactionHelper,
                              FiscalCalendarRules fiscalCalendarRules) {
        this.officeDao = officeDao;
        this.holidayDao = holidayDao;
        this.hibernateTransactionHelper = hibernateTransactionHelper;
        this.fiscalCalendarRules = fiscalCalendarRules;
    }

    @Override
    public void create(HolidayDetails holidayDetails, List<Short> officeIds) {
        HolidayBO holiday = HolidayBO.fromDto(holidayDetails);
        holiday.validate();

        List<OfficeBO> offices = new ArrayList<OfficeBO>();
        for (Short officeId : officeIds) {
            OfficeBO office = officeDao.findOfficeById(officeId);
            offices.add(office);
            if (office.hasChildWithAnyOf(officeIds)) {
                throw new BusinessRuleException(HolidayConstants.HOLIDAY_CREATION_EXCEPTION);
                // "Holidays can only be associated with one level of office in an office hierarchy."
            }
        }

        try {
            hibernateTransactionHelper.startTransaction();

            this.holidayDao.save(holiday);
            for (OfficeBO office : offices) {
                office.addHoliday(holiday);
            }

            hibernateTransactionHelper.commitTransaction();
        } catch (Exception e) {
            hibernateTransactionHelper.rollbackTransaction();
        } finally {
            hibernateTransactionHelper.closeSession();
        }
    }

    @Override
    public boolean isWorkingDay(Calendar day, Short officeId) {
        return (fiscalCalendarRules.isWorkingDay(day)) && !holidayDao.isHoliday(officeId, getLocaleDate(day));
    }

    private String getLocaleDate(Calendar day) {
        return new DateTime(day.getTime().getTime()).toLocalDate().toString();
    }

    @Override
    public Calendar getNextWorkingDay(Calendar day, Short officeId) {
        while (!isWorkingDay(day, officeId)) {
            day.add(Calendar.DATE, 1);
        }
        return day;
    }

    @Override
    public Date getNextWorkingDay(Date day, Short officeId) {
        return getNextWorkingDay(getCalendarDate(day), officeId).getTime();
    }

    @Override
    public boolean isFutureRepaymentHoliday(Calendar date, Short officeId) {
        return isRegularHoliday(date) || holidayDao.isFutureRepaymentHoliday(officeId, getLocaleDate(date));
    }

    private boolean isRegularHoliday(Calendar date) {
        return !fiscalCalendarRules.isWorkingDay(date);
    }

    private static Calendar getCalendarDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}