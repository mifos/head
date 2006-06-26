package org.mifos.framework.components.scheduler;

/**
 * @author navitas
 * Created on Aug 12, 2005
 * Interface to hold constants used in scheduler.
 */

public interface Constants {
	//resource file
	public static final String Resources = "org.mifos.framework.components.scheduler.ApplicationResources";
	public static final String Package="org.mifos.framework.components.scheduler.";
	//schedule data types
	public static final String DAY = "Day";
	public static final String WEEK = "Week";
	public static final String MONTH = "Month";

	//recurrences type
	public static final String WeeklyOnDay = "WeeklyOnDay";
	public static final String MonthlyOnDay = "MonthlyOnDay";
	public static final String MonthlyOnDate = "MonthlyOnDate";

	//error messages key
	public static final String Invalid = "errors.invalid";
	public static final String NotApplicable = "errors.NA";
	public static final String NULL = "errors.NULL";

	//method names key
	public static final String Set_WeekDay_Key = "SetWeekDayKey";
	public static final String Get_WeekDay_Key = "GetWeekDayKey";
	public static final String Set_DayNumber_Key = "SetDayNumberKey";
	public static final String Get_DayNumber_Key = "GetDayNumberKey";
	public static final String Set_WeekRank_Key = "SetWeekRankKey";
	public static final String Get_WeekRank_Key = "GetWeekRankKey";
	public static final String Get_RecurType_Key = "GetRecurTypeKey";

	//attribute keys
	public static final String Recur_After_Key = "RecurAfterKey";
	public static final String WeekDay_Key = "WeekDayKey";
	public static final String WeekRank_Key="WeekRankKey";
	public static final String DayNumber_KEY="DayNumberKey";
	public static final String Occurrences_Key = "OccurrencesKey";
	public static final String DayNumber_WeekDay_KEY="DayNumberOrWeekDayKey";
	public static final String ScheduleInputs_Key="ScheduleInputsKey";
	public static final String ScheduleData_Key="ScheduleDataKey";
	public static final String ScheduleHolidayOption_Key="ScheduleHolidayOptionKey";
	public static final String StartDate_Key="StartDateKey";
	public static final String EndDate_Key="EndDateKey";
	public static final String WeekDayInWeekOffList_Key="WeekDayInWeekOffListKey";
	public static final String WeekOffList_Key="WeekOffListKey";

	//API Parameters
	public static final String API_Param_Key="APIParamKey";

	//weekdays
	public static final int SUNDAY=1;
	public static final int MONDAY=2;
	public static final int TUESDAY=3;
	public static final int WEDNESDAY=4;
	public static final int THURSDAY=5;
	public static final int FRIDAY=6;
	public static final int SATURDAY=7;

	//scheduleholiday option
	public static final int NEXT_WRK_DAY=1;
	public static final int NEXT_SCH_DATE=2;
	public static final int SAME_DAY=3;

	//values for week rank
	public static final int FIRST=1;
	public static final int SECOND=2;
	public static final int THIRD=3;
	public static final int FOURTH=4;
	public static final int LAST=5;

	//others
	public static final String Data="Data";
	public static final String Scheduler="Scheduler";
}
