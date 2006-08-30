package org.mifos.framework.components.scheduler;

/**
 * It is a common Interface to all scheudle data classes, needed by scheduler to generate schedules.
 */
public interface ScheduleDataIntf {
	/**
	 * Method to validate member attributes of DayData/WeekData/MonthData class.
	 * This method is implemented by DayData, WeekData & MonthData classes. 
	 * @throws SchedulerException when any of the attribute is invalid. 
	 */
	public void validate()throws SchedulerException;
	
	/**
	 * Method to set the value of recurAfter member variable.
	 * This method is implemented by DayData, WeekData & MonthData classes.
	 * @param recurAfter an integer that indicates the no of days/weeks/months after which schedule repeats.
	 * @throws SchedulerException
	 */
	public void setRecurAfter(int recurAfter)throws SchedulerException;
	
	/**
	 * Method to get the value of recurAfter member variable.
	 * This method is implemented by DayData, WeekData & MonthData classes.
	 * @@return integer that indicates the no of days/weeks/months after which schedule repeats.
	 */
	public int getRecurAfter();
	
	/**
	 * Method to set the value of weekDay member variable.
	 * This method is implemented by ScheduleData, WeekData & MonthData classes.
	 * @param weekDay an integer that indicates weekday e.g. sunday=1, monday=2 etc.
	 * @throws SchedulerException when invoked on DayData class instance.
	 */
	public void setWeekDay(int weekDay)throws SchedulerException;
	
	/**
	 * Method to get the value of weekDay member variable.
	 * This method is implemented by WeekData & MonthData classes.
	 * @return integer that indicates weekday e.g. sunday=1, monday=2 etc.
	 * @throws SchedulerException when invoked on DayData class instance.
	 */
	public int getWeekDay()throws SchedulerException;
	
	/**
	 * Method to set the value of dayNumber member variable.
	 * This method is implemented by MonthData class.  
	 * @param dayNumber an integer that indicates nth day of the month e.g. 5th day of the month
	 * @throws SchedulerException when invoked on instance of DayData or WeekData class.
	 */
	public void setDayNumber(int dayNumber)throws SchedulerException;
	
	/**
	 * Method to get the value of dayNumber member variable.
	 * This method is implemented by MonthData class. 
	 * @return integer that indicates nth day of the month
	 * @throws SchedulerException when invoked on instance of DayData or WeekData class.
	 */
	public int getDayNumber()throws SchedulerException;
	
	/**
	 * Method to set the value of weekRank member variable.
	 * This method is implemented by MonthData class. 
	 * @param weekRank an integer that indicates first, second, third, fourth or last week of the month.
	 * @throws SchedulerException when invoked on instance of DayData or WeekData class.
	 */
	public void setWeekRank(int weekRank)throws SchedulerException;
	
	/**
	 * Method to get the value of weekRank member variable.
	 * This method is overidden by MonthData class.  
	 * @return integer that indicates first, second, third, fourth or last week.
	 * @throws SchedulerException when invoked on instance of DayData or WeekData class.
	 */	
	public int getWeekRank()throws SchedulerException;
	
	/**
	 * Method to get the value of recurType member variable.
	 * This method is overidden by MonthData class. 
	 * @return String that indicates recurrence type which may be MonthlyOnDay or MonthlyOnDate.
	 * @throws SchedulerException when invoked on instance of DayData or WeekData class.
	 */
	public String getRecurType()throws SchedulerException;
}
