package org.mifos.framework.components.scheduler;

/**
 * @author navitas
 * Created on Aug 12, 2005
 * It is an abstract class which implements ScheduleDataIntf and acts as an adapter class.
 * Other classes extending ScheduleData class implements methods respective to them.
 */
public abstract class ScheduleData implements ScheduleDataIntf {
	/** recurAfter is the number of days/weeks/months after which schedule date has to reoccur.
	 */
	private int recurAfter=0;
	
	/**
	 * Method to validate member attributes of DayData/WeekData/MonthData class.
	 * This method is overidden by DayData, WeekData & MonthData classes. 
	 * @throws SchedulerException when any of the attribute is invalid. 
	 */
	public void validate()throws SchedulerException{
		this.validateRecurAfter();	
	}
	
	/**
	 * Method to set the value of recurAfter member variable.
	 * @param recurAfter an integer that indicates the no of days/weeks/months after which schedule repeats.
	 * @throws SchedulerException
	 */
	public void setRecurAfter(int recurAfter)throws SchedulerException{
		this.recurAfter=recurAfter;
		this.validateRecurAfter();
	}
	
	/**
	 * Method to get the value of recurAfter member variable.
	 * @@return integer that indicates the no of days/weeks/months after which schedule repeats.
	 */
	public  int getRecurAfter(){
		return recurAfter;
	}
	
	/**
	 * Method to set the value of weekDay member variable.
	 * This method is overidden by WeekData & MonthData classes.
	 * @param weekDay an integer that indicates weekday e.g. sunday=1, monday=2 etc.
	 * @throws SchedulerException when invoked on DayData class instance.
	 */
	public void setWeekDay(int weekDay)throws SchedulerException{
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NotApplicable,Constants.Set_WeekDay_Key);
	}
	
	/**
	 * Method to set the value of dayNumber member variable.
	 * This method is overidden by MonthData class.  
	 * @param dayNumber an integer that indicates nth day of the month e.g. 5th day of the month
	 * @throws SchedulerException when invoked on instance of DayData or WeekData class.
	 */
	public void setDayNumber(int dayNumber)throws SchedulerException{
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NotApplicable,Constants.Set_DayNumber_Key);
	}

	/**
	 * Method to set the value of weekRank member variable.
	 * This method is overidden by MonthData class. 
	 * @param weekRank an integer that indicates first, second, third, fourth or last week of the month.
	 * @throws SchedulerException when invoked on instance of DayData or WeekData class.
	 */
	public void setWeekRank(int weekRank)throws SchedulerException{
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NotApplicable,Constants.Set_WeekRank_Key);
	}
	
	/**
	 * Method to get the value of weekDay member variable.
	 * This method is overidden by WeekData & MonthData classes.
	 * @return integer that indicates weekday e.g. sunday=1, monday=2 etc.
	 * @throws SchedulerException when invoked on DayData class instance.
	 */
	public int getWeekDay()throws SchedulerException{
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NotApplicable,Constants.Get_WeekDay_Key);
	}
	
	/**
	 * Method to get the value of dayNumber member variable.
	 * This method is overidden by MonthData class. 
	 * @return integer that indicates nth day of the month
	 * @throws SchedulerException when invoked on instance of DayData or WeekData class.
	 */
	public int getDayNumber()throws SchedulerException{
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NotApplicable,Constants.Get_DayNumber_Key);
	}
	
	/**
	 * Method to get the value of weekRank member variable.
	 * This method is overidden by MonthData class.  
	 * @return integer that indicates first, second, third, fourth or last week.
	 * @throws SchedulerException when invoked on instance of DayData or WeekData class.
	 */	
	public int getWeekRank()throws SchedulerException{
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NotApplicable,Constants.Get_WeekRank_Key);
	}
	
	/**
	 * Method to get the value of recurType member variable.
	 * This method is overidden by MonthData class. 
	 * @return String that indicates recurrence type which may be MonthlyOnDay or MonthlyOnDate.
	 * @throws SchedulerException when invoked on instance of DayData or WeekData class.
	 */
	public String getRecurType()throws SchedulerException{
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NotApplicable,Constants.Get_RecurType_Key);
	}
	
	/**
	 * Method to validate recurAfter member variable.
	 * @throws SchedulerException when recurAfter attribute is invalid. 
	 */
	private void validateRecurAfter() throws SchedulerException{
		if (recurAfter<=0)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.Recur_After_Key);
	}
}
