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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.service.HolidayService;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.master.MessageLookup;
import org.mifos.calendar.WorkingDay;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.dto.domain.HolidayDetails;
import org.mifos.dto.domain.OfficeHoliday;
import org.mifos.service.BusinessRuleException;

public class HolidayServiceFacadeWebTier implements HolidayServiceFacade {

    private final HolidayService holidayService;
    private final HolidayDao holidayDao;

    public HolidayServiceFacadeWebTier(HolidayService holidayService, HolidayDao holidayDao) {
        this.holidayService = holidayService;
        this.holidayDao = holidayDao;
    }

    @Override
    public void createHoliday(HolidayDetails holidayDetails, List<Short> officeIds) {
        this.holidayService.create(holidayDetails, officeIds);
    }

    @Override
    public Map<String, List<OfficeHoliday>> holidaysByYear() {

        List<HolidayBO> holidays = this.holidayDao.findAllHolidays();

        Map<String, List<OfficeHoliday>> holidaysByYear = new TreeMap<String, List<OfficeHoliday>>();
        for (HolidayBO holiday : holidays) {
            HolidayDetails holidayDetail = new HolidayDetails(holiday.getHolidayName(), holiday.getHolidayFromDate(), holiday
                    .getHolidayThruDate(), holiday.getRepaymentRuleType().getValue());

            String holidayRepaymentRuleName = MessageLookup.getInstance().lookup(holiday.getRepaymentRuleType().getPropertiesKey());
            holidayDetail.setRepaymentRuleName(holidayRepaymentRuleName);

            int year = holiday.getThruDate().getYear();
            List<OfficeHoliday> holidaysInYear = holidaysByYear.get(Integer.toString(year));
            if (holidaysInYear == null) {
                holidaysInYear = new LinkedList<OfficeHoliday>();
            }
            holidaysInYear.add(new OfficeHoliday(holidayDetail, this.holidayDao.applicableOffices(holiday.getId())));
            holidaysByYear.put(Integer.toString(year), holidaysInYear);
        }
        sortValuesByFromDate(holidaysByYear);
        return holidaysByYear;
    }

    private void sortValuesByFromDate(Map<String, List<OfficeHoliday>> holidays) {
        for (String year : holidays.keySet()) {
            List<OfficeHoliday> holidayList = holidays.get(year);
            Collections.sort(holidayList, new Comparator<OfficeHoliday>() {
                @Override
                public int compare(OfficeHoliday o1, OfficeHoliday o2) {
                    return o1.getHolidayDetails().getFromDate().compareTo(o2.getHolidayDetails().getFromDate());
                }
            });
        }
    }

    @Override
    public OfficeHoliday retrieveHolidayDetailsForPreview(HolidayDetails holidayDetail, List<Short> officeIds) {

        String holidayRepaymentRuleName = MessageLookup.getInstance().lookup(RepaymentRuleTypes.fromInt(holidayDetail.getRepaymentRuleType().intValue()).getPropertiesKey());
        holidayDetail.setRepaymentRuleName(holidayRepaymentRuleName);

        List<String> officeNames = this.holidayDao.retrieveApplicableOfficeNames(officeIds);

        return new OfficeHoliday(holidayDetail, officeNames);
    }

    @Override
    public List<String> retrieveOtherHolidayNamesWithTheSameDate(HolidayDetails holidayDetail, List<Short> branchIds) {
        List<String> holidayNames = new ArrayList<String>();
        // TODO I assume we should look at dates only (without branches). Is this the correct assumption? (MIFOS-3428)
        //List<String> offices = this.holidayDao.retrieveApplicableOfficeNames(branchIds);

        for (HolidayBO holiday : this.holidayDao.findAllHolidays()) {
            //List<String> holidayOffices = this.holidayDao.applicableOffices(holiday.getId());
            //if (!Collections.disjoint(offices, holidayOffices)) {
                for (LocalDate date = holidayDetail.getFromDate(); date.compareTo(holidayDetail.getThruDate()) <= 0; date = date.plusDays(1)) {
                    if (holiday.encloses(date.toDateTimeAtStartOfDay())) {
                        holidayNames.add(holiday.getName());
                        break;
                    }
                }
            //}
        }
        return holidayNames;
    }

    @Override
    public boolean isWorkingDay(Calendar day, Short officeId) {
        return holidayService.isWorkingDay(day, officeId);
    }

    @Override
    public Calendar getNextWorkingDay(Calendar day, Short officeId) {
        return holidayService.getNextWorkingDay(day, officeId);
    }

    @Override
    public Date getNextWorkingDay(Date day, Short officeId) {
        return holidayService.getNextWorkingDay(day, officeId);
    }

    @Override
    public void validateDisbursementDateForNewLoan(Short officeId, DateTime disbursementDate) {

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        validateDisbursementDateIsWorkingDay(disbursementDate, workingDays);

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext(officeId);
        validateDisbursementDateIsNotInHoliday(disbursementDate, holidays);
    }

    private void validateDisbursementDateIsWorkingDay(DateTime disbursementDate, List<Days> workingDays) {
        if (WorkingDay.isNotWorkingDay(disbursementDate, workingDays)) {
            throw new BusinessRuleException(LoanExceptionConstants.DISBURSEMENTDATE_MUST_BE_A_WORKING_DAY);
        }
    }

    private void validateDisbursementDateIsNotInHoliday (DateTime disbursementDate, List<Holiday> holidays) {
        for (Holiday holiday : holidays) {
            if (holiday.encloses(disbursementDate)) {
                throw new BusinessRuleException(LoanExceptionConstants.DISBURSEMENTDATE_MUST_NOT_BE_IN_A_HOLIDAY);
            }
        }
    }

    public boolean isFutureRepaymentHoliday(Short officeId, Calendar date) {
        return holidayService.isFutureRepaymentHoliday(date, officeId);
    }

}