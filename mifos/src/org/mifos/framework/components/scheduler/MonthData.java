package org.mifos.framework.components.scheduler;

/**
 * @author navitas
 * Created on Aug 12, 2005
 * It holds and validate data related to month recurrence.
 */
public class MonthData extends ScheduleData {
	/** dayNumber indicates nth day of the month e.g. 5th day of the month.
	 */
	private int dayNumber=0;
	
	/** weekDay is an integer respective to weekday e.g (sunday=1, monday=2, etc.) 
	 */
	private int weekDay=0;
	
	/** weekRank is an integer that indicates first, second, third, fourth or last week of the month. 
	 */
	private int weekRank=0;
	
	/** recurType is month recurrence type and it is set interally based on data received.
	 * This can have values MonthlyOnDay and MonthlyOnDate
	 */	
	private String recurType=null;
	
	/**
	 * Method to validate member attributes of MonthData class. 
	 * @throws SchedulerException when any of the attribute is invalid. 
	 */
	public void validate()throws SchedulerException{
		super.validate();
		boolean onDate = this.isMonthlyOnDate();
		boolean onDay = this.isMonthlyOnDay();
		if(!onDate && !onDay)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.DayNumber_WeekDay_KEY);
		//set the appropriate recursion type
		recurType=(onDate==true)?Constants.MonthlyOnDate:Constants.MonthlyOnDay;
	}
	
	/**
	 * Method tells if recusion type can be MonthlyOnDate 
	 * @throws SchedulerException when dayNumber is invalid. 
	 */
	private boolean isMonthlyOnDate() throws SchedulerException{
		if(dayNumber==0)
			return false;
		this.validateDayNumber();
		if(weekDay!=0||weekRank!=0)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.Invalid,Constants.DayNumber_WeekDay_KEY);
		return true;
	}
	
	/**
	 * Method tells if recusion type can be MonthlyOnDy 
	 * @throws SchedulerException when weekday or weekrank is invalid. 
	 */
	private boolean isMonthlyOnDay() throws SchedulerException{
		if(weekDay==0 || weekRank==0)
		return false;
		this.validateWeekDay();
		this.validateWeekRank();
		return true;
	}
	/**
	 * Method to set the value of dayNumber member variable.  
	 * @param dayNumber an integer that indicates nth day of the month e.g. 5th day of the month
	 */
	public void setDayNumber(int dayNumber)throws SchedulerException{
		this.dayNumber=dayNumber;
		this.validateDayNumber();
	}
	
	/**
	 * Method to get the value of dayNumber member variable.  
	 * @return integer that indicates nth day of the month
	 */
	public int getDayNumber()throws SchedulerException{
		return this.dayNumber;
	}
	
	/**
	 * Method to set the value of weekDay member variable.  
	 * @param weekDay is an integer that indicates weekday e.g. sunday=1, monday=2 etc.
	 */
	public void setWeekDay(int weekDay)throws SchedulerException{
		this.weekDay=weekDay;
		this.validateWeekDay();
	}
	
	/**
	 * Method to get the value of weekDay member variable. 
	 * @return integer that indicates weekday e.g. sunday=1, monday=2 etc.
	 */
	public int getWeekDay()throws SchedulerException{
		return this.weekDay;
	}
	
	/**
	 * Method to set the value of weekRank member variable.  
	 * @param weekRank is an integer that indicates first, second, third, fourth or last week of the month.
	 */
	public void setWeekRank(int weekRank)throws SchedulerException{
		this.weekRank=weekRank;
		this.validateWeekRank();
	}
	
	/**
	 * Method to get the value of weekRank member variable. 
	 * @return integer that indicates first, second, third, fourth or last week.
	 */	
	public int getWeekRank()throws SchedulerException{
		return this.weekRank;
	}	
	
	/**
	 * Method to get the value of recurType member variable. 
	 * @return String that indicates recurrence type which may be MonthlyOnDay or MonthlyOnDate.
	 */
	public String getRecurType()throws SchedulerException{
		return this.recurType;
	}
	
	/**
	 * Method to validate the value of dayNumber member variable. 
	 * @throws SchedulerException
	 */
	private void validateDayNumber() throws SchedulerException{
		if(dayNumber==0)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.DayNumber_KEY);
		if(dayNumber<0 || dayNumber > 31)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.Invalid,Constants.DayNumber_KEY);
	}
	
	/**
	 * Method to validate the value of weekDay member variable. 
	 * @throws SchedulerException
	 */
	private void validateWeekDay() throws SchedulerException{
		if(weekDay==0)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.WeekDay_Key);
		if(weekDay<0 || weekDay>7)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.Invalid,Constants.WeekDay_Key);
	}
	/**
	 * Method to validate the value of weeekRank member variable. 
	 * @throws SchedulerException
	 */
	private void validateWeekRank() throws SchedulerException{
		if(weekRank==0)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.WeekRank_Key);
		if(!(weekRank>=Constants.FIRST && weekRank<=Constants.FOURTH || weekRank==Constants.LAST))
			throw SchedulerExceptionFactory.getSchedulerException(Constants.Invalid,Constants.WeekRank_Key);
	}
}
