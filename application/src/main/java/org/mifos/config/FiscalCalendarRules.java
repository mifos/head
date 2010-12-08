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

package org.mifos.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.Days;
import org.mifos.application.meeting.util.helpers.WeekDay;

public class FiscalCalendarRules {

    private static final Logger logger = LoggerFactory.getLogger(FiscalCalendarRules.class);

    final String FiscalCalendarRulesWorkingDays = "FiscalCalendarRules.WorkingDays";

    private final String FiscalCalendarRulesScheduleTypeForMeetingIfNonWorkingDay = "FiscalCalendarRules.ScheduleMeetingIfNonWorkingDay";

    private String[] configWorkingDays = getConfiguredWorkingDays();

    private String[] getConfiguredWorkingDays() {
        String[] workingDays = null;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if (configMgr.containsKey(FiscalCalendarRulesWorkingDays)) {
            workingDays = configMgr.getStringArray(FiscalCalendarRulesWorkingDays);
            if (workingDays == null) {
                throw new RuntimeException("The working days are not configured correctly in the config file.");
            }
        } else {
            throw new RuntimeException("The working days are not defined in the config file.");
        }
        return workingDays;
    }

    public void reloadConfigWorkingDays() {
        configWorkingDays = getConfiguredWorkingDays();
    }

    private static WeekDay findWeekDay(final String workingDay) {
        WeekDay[] weekDays = WeekDay.values(); // all weekdays

        for (WeekDay weekDay : weekDays) {
            if (weekDay.name().equalsIgnoreCase(workingDay)) {
                return weekDay;
            }
        }
        throw new RuntimeException("The configured working day " + workingDay + " is not a week day enum.");

    }

    public List<WeekDay> getWeekDaysList() {
        WeekDay[] weekDays = WeekDay.values();
        List<WeekDay> list = new ArrayList<WeekDay>();
        for (WeekDay weekDay : weekDays) {
            list.add(weekDay);
        }
        return list;
    }

    public List<WeekDay> getWorkingDays() {
        if (configWorkingDays == null) {
            throw new RuntimeException("The working days are not defined in the config file.");
        }
        List<WeekDay> workingDays = new ArrayList<WeekDay>(); // returned working days
        for (String configWorkingDay : configWorkingDays) {
            workingDays.add(findWeekDay(configWorkingDay));
        }

        return workingDays;
    }

    public List<Short> getWeekDayOffList() {
        if (configWorkingDays == null) {
            throw new RuntimeException("The working days are not defined in the config file.");
        }
        List<Short> offDays = new ArrayList<Short>(); // returned Non-working days
        WeekDay[] weekDays = WeekDay.values();
        for (int i = 0; i < weekDays.length; i++) {
            if (!isWorkingDay(weekDays[i])) {
                offDays.add(weekDays[i].getValue());
            }
        }
        return offDays;
    }

    private boolean isWorkingDay(final WeekDay weekDay) {
        for (String configWorkingDay : configWorkingDays) {
            if (configWorkingDay.equalsIgnoreCase(weekDay.name())) {
                return true;
            }
        }
        return false;
    }

    public boolean isWorkingDay(final Calendar day) {
        if (configWorkingDays == null) {
            throw new RuntimeException("The working days are not defined in the config file.");
        }
        return isWorkingDay(WeekDay.getWeekDay(day.get(Calendar.DAY_OF_WEEK)));
    }

    public boolean isWorkingDay(final Short dayOfWeek) {
        if (configWorkingDays == null) {
            throw new RuntimeException("The working days are not defined in the config file.");
        }
        WeekDay weekDay = WeekDay.getWeekDay(dayOfWeek);
        return isWorkingDay(weekDay);

    }

    public boolean isStartOfFiscalWeek(final Short dayOfWeek) {
        if (configWorkingDays == null) {
            throw new RuntimeException("The working days are not defined in the config file.");
        }
        Short startOfWeekDay = getStartOfWeek();
        return (dayOfWeek.shortValue() == startOfWeekDay.shortValue());

    }

    public Short getStartOfWeek() {
        return getStartOfWeekWeekDay().getValue();
    }

    public WeekDay getStartOfWeekWeekDay() {
        if (configWorkingDays == null) {
            throw new RuntimeException("The working days are not defined in the config file.");
        }
        WeekDay startOfWeek = findWeekDay(configWorkingDays[0]);
        return startOfWeek;
    }


    public String getScheduleMeetingIfNonWorkingDay() {
        String scheduleType = null;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if (configMgr.containsKey(FiscalCalendarRulesScheduleTypeForMeetingIfNonWorkingDay)) {
            scheduleType = configMgr.getString(FiscalCalendarRulesScheduleTypeForMeetingIfNonWorkingDay);
        } else {
            throw new RuntimeException(
                    "The schedule type for meeting if non working day is not defined in the config file.");
        }
        return scheduleType;
    }

    public void setWorkingDays(final String workingDays) {
        ConfigurationManager.getInstance().setProperty(FiscalCalendarRulesWorkingDays, workingDays);
        reloadConfigWorkingDays();
    }

    public void setWorkingDays(final List<WeekDay> workingDays) {
        String workingDaysString = workingDays.toString().replace("[", "").replace("]", "");
        setWorkingDays(workingDaysString);
    }

    @SuppressWarnings("unchecked")
    public String  getWorkingDaysAsString() {
        String property = "";
        for(String day: (ArrayList<String>) ConfigurationManager.getInstance().getProperty(FiscalCalendarRulesWorkingDays)) {
            property += ","+day;
        }
        return property.replaceFirst(",", "");
    }

    public void setScheduleTypeForMeetingIfNonWorkingDay(final String ScheduleMeetingIfNonWorkingDay) {
        ConfigurationManager.getInstance().setProperty(FiscalCalendarRulesScheduleTypeForMeetingIfNonWorkingDay, ScheduleMeetingIfNonWorkingDay);
    }

    public List<Days> getWorkingDaysAsJodaTimeDays() {

        List<Days> jodaWorkingDays = new ArrayList<Days>();

        List<WeekDay> workingDaysAsWeekDays = getWorkingDays();
        for (WeekDay weekDay : workingDaysAsWeekDays) {

            Days jodaWeekDay =  Days.days(WeekDay.getJodaDayOfWeekThatMatchesMifosWeekDay(weekDay.getValue()));

            jodaWorkingDays.add(jodaWeekDay);
        }

        return jodaWorkingDays;
    }

}
