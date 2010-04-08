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

package org.mifos.application.holiday.business.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.HolidayConstants;
import org.mifos.application.holiday.util.helpers.HolidayUtils;
import org.mifos.calendar.MoratoriumSwitch;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Predicate;
import org.mifos.security.util.UserContext;

public class HolidayBusinessService implements BusinessService {

    private MoratoriumSwitch moratoriumSwitch = new MoratoriumSwitch();

    public void setMoratoriumSwitch (MoratoriumSwitch moratoriumSwitch) {
        this.moratoriumSwitch = moratoriumSwitch;
    }

    @Override
    public AbstractBusinessObject getBusinessObject(final UserContext userContext) {
        return null;
    }

    public void isValidHolidayState(final Short levelId, final Short stateId, final boolean isCustomer) throws ServiceException {
        try {
            Integer records = new HolidayPersistence().isValidHolidayState(levelId, stateId, isCustomer);
            if (records.intValue() != 0) {
                throw new ServiceException(HolidayConstants.EXCEPTION_STATE_ALREADY_EXIST);
            }
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    @Deprecated
    public List<HolidayBO> getHolidays(final int year) throws ServiceException {
        // HolidayBO.isWorkingDay(Calendar.getInstance());
        try {
            return new HolidayPersistence().getHolidays(year);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public List<RepaymentRuleEntity> getRepaymentRuleTypes() throws ServiceException {
        try {
            List<RepaymentRuleEntity> rules =  new HolidayPersistence().getRepaymentRuleTypes();
            if ( moratoriumSwitch.isOn() ) {
                return rules;
            }
            // remove Repayment Moratorium rule
            return (List<RepaymentRuleEntity>) CollectionUtils.select(rules, isNotRepaymentMoratoriumRule());
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        } catch (Exception e) { //declared by Predicate.evaluate()
            throw new ServiceException(e);
        }
    }

    private Predicate<RepaymentRuleEntity> isNotRepaymentMoratoriumRule() {
        return new Predicate<RepaymentRuleEntity>() {
            public boolean evaluate (RepaymentRuleEntity rule) {
                return rule.getId() < 4;
            }
        };
    }

    public List<HolidayBO> getDistinctYears() throws ServiceException {
        try {
            return new HolidayPersistence().getDistinctYears();
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    public HolidayBO getHolidayContaining (Date date) {
        return HolidayUtils.inHoliday(DateUtils.getCalendarDate(date.getTime()));
    }

    public List<HolidayBO> getAllPushOutHolidaysContaining (Date date) {
        //TODO: implement this
        return null;
    }

    public HolidayBO findNonPushOutHolidayContaining (Date date) {
        //TODO: implement
        return null;
    }

    public boolean isWorkingDay(Date day) throws RuntimeException {
        return new FiscalCalendarRules().isWorkingDay(DateUtils.getCalendar(day));
    }

    public boolean isWorkingDay(Calendar day) throws RuntimeException {
        return new FiscalCalendarRules().isWorkingDay(day);
    }

    public Date getNextWorkingDay(Date day) {
        Calendar calendarDay = DateUtils.getCalendar(day);
        do {
            calendarDay.add(Calendar.DATE, 1);
        } while (!isWorkingDay(calendarDay));
        return calendarDay.getTime();
    }

    /**
     * Get the first working day of the week that the given day is in.
     *
     * Precondition: The given day is a working day.
     *
     * @return the given day, if it's the first working day of the week, otherwise
     * back up to the first working day of the week.
     */
    public Date getFirstWorkingDayOfWeekForDate (Date day) {
        if (!isWorkingDay(day)) {
            throw new RuntimeException("Day must be a working day");
        }
        final GregorianCalendar firstDateForWeek = new GregorianCalendar();
        firstDateForWeek.setTime(day);
        //back up to first non-working day
        while (isWorkingDay (firstDateForWeek)) {
            firstDateForWeek.add(Calendar.DAY_OF_WEEK, -1);
        }
        //then move forward to first working day
        firstDateForWeek.add(Calendar.DAY_OF_WEEK, 1);
        return firstDateForWeek.getTime();
    }

}
