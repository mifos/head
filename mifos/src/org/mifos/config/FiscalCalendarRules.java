package org.mifos.config;

import org.mifos.application.meeting.util.helpers.WeekDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FiscalCalendarRules {
	
	public static final String FiscalCalendarRulesWorkingDays = "FiscalCalendarRules.WorkingDays";
	public static final String FiscalCalendarRulesScheduleTypeForMeetingOnHoliday = "FiscalCalendarRules.ScheduleTypeForMeetingOnHoliday";
	public static final String FiscalCalendarRulesDaysForCalendarDefinition = "FiscalCalendarRules.DaysForCalendarDefinition";
	private static String[] configWorkingDays = getConfiguredWorkingDays();
	
	private static String[] getConfiguredWorkingDays()
	{
		String[] workingDays = null;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(FiscalCalendarRulesWorkingDays))
		{
			workingDays = configMgr.getStringArray(FiscalCalendarRulesWorkingDays);
			if (workingDays == null)
				throw new RuntimeException("The working days are not configured correctly in the config file.");
		}
		else
			throw new RuntimeException("The working days are not defined in the config file.");
		return workingDays;
	}
	
	public static void reloadConfigWorkingDays()
	{
		configWorkingDays = getConfiguredWorkingDays();
	}
	
	private static WeekDay findWeekDay(String workingDay)
	{
		WeekDay[] weekDays = WeekDay.values(); // all weekdays
		
		for (int i=0; i < weekDays.length; i++)
		{
			if (weekDays[i].name().toUpperCase().equals(workingDay.toUpperCase()))
			{
				return weekDays[i];
			}
		}
		throw new RuntimeException("The configured working day " + workingDay + " is not a week day enum.");
		
	}
	
	public static List<WeekDay> getWeekDaysList()
	{
		WeekDay[] weekDays = WeekDay.values();
		List<WeekDay> list = new ArrayList();
		for (int i=0; i < weekDays.length; i++)
			list.add(weekDays[i]);
	    return list;
	}
	
	public static List<WeekDay> getWorkingDays()
	{
		if (configWorkingDays == null)
			throw new RuntimeException("The working days are not defined in the config file.");
        List<WeekDay> workingDays = new ArrayList(); // returned working days
		for (int i= 0; i < configWorkingDays.length; i++)
			workingDays.add(findWeekDay(configWorkingDays[i]));
		
		return workingDays;
	}
	
	public static List<Short> getWeekDayOffList()
	{
		if (configWorkingDays == null)
			throw new RuntimeException("The working days are not defined in the config file.");
        List<Short> offDays = new ArrayList(); // returned off days
        WeekDay[] weekDays = WeekDay.values();
		for (int i= 0; i < weekDays.length; i++)
			if (!isWorkingDay(weekDays[i]))
				offDays.add(weekDays[i].getValue());
		return offDays;
	}
	
	private static boolean isWorkingDay(WeekDay weekDay)
	{
		for (int i= 0; i < configWorkingDays.length; i++)
			if (configWorkingDays[i].toUpperCase().equals(weekDay.name().toUpperCase()))
				return true;
		return false;
	}
	
	public static boolean isWorkingDay(Calendar day) {
		if (configWorkingDays == null)
			throw new RuntimeException("The working days are not defined in the config file.");
		int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
		Short dayOfWeekShort = new Short((short)dayOfWeek);
		WeekDay weekDay = WeekDay.getWeekDay(dayOfWeekShort);
		return isWorkingDay(weekDay);
		
	}
	
	public static boolean isWorkingDay(Short dayOfWeek) {
		if (configWorkingDays == null)
			throw new RuntimeException("The working days are not defined in the config file.");
		WeekDay weekDay = WeekDay.getWeekDay(dayOfWeek);
		return isWorkingDay(weekDay);
		
	}
	
	public static boolean isStartOfFiscalWeek(Short dayOfWeek) {
		if (configWorkingDays == null)
			throw new RuntimeException("The working days are not defined in the config file.");
		Short startOfWeekDay = getStartOfWeek();
		return (dayOfWeek.shortValue() == startOfWeekDay.shortValue());
		
	}
	
	public static Short getStartOfWeek() {
		if (configWorkingDays == null)
			throw new RuntimeException("The working days are not defined in the config file.");
		WeekDay startOfWeek = findWeekDay(configWorkingDays[0]);
		return startOfWeek.getValue();
	}
	
	public static String getScheduleTypeForMeetingOnHoliday()
	{
        String scheduleType = null;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(FiscalCalendarRulesScheduleTypeForMeetingOnHoliday))
			scheduleType = configMgr.getString(FiscalCalendarRulesScheduleTypeForMeetingOnHoliday);
		else
			throw new RuntimeException("The schedule type for meeting on holiday is not defined in the config file.");
		return scheduleType;
	}
	
	public static Short getDaysForCalendarDefinition()
	{
        Short days = null;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(FiscalCalendarRulesDaysForCalendarDefinition))
			days = configMgr.getShort(FiscalCalendarRulesDaysForCalendarDefinition);
		else
			throw new RuntimeException("The days for calendar definition is not defined in the config file.");
		return days;
	}

}
