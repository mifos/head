package org.mifos.framework.components.scheduler;

/**
 * @author navitas
 * Created on Aug 12, 2005
 * It holds and validate data related to week recurrence.
 */

public class WeekData extends ScheduleData {
	/** weekDay is an integer respective to weekday e.g (sunday=1, monday=2, etc.) 
	 */
	private int weekDay=0;
	
	/**
	 * Method to validate member attributes of WeekData class. 
	 * @throws SchedulerException when any of the attribute is invalid. 
	 */
	public void validate()throws SchedulerException{
		super.validate();
		validateWeekDay();		
	}
	
	/**
	 * Method to set the value of weekDay member variable.  
	 * @param weekDay is an integer that indicates weekday e.g. sunday=1, monday=2 etc.
	 * @throws SchedulerException
	 */
	public void setWeekDay(int weekDay)throws SchedulerException{
		this.weekDay=weekDay;
		this.validateWeekDay();
	}
	
	/**
	 * Method to get the value of weekDay member variable. 
	 * @return integer that indicates weekday e.g. sunday=1, monday=2 etc.
	 * @throws SchedulerException
	 */
	public int getWeekDay()throws SchedulerException{
		return this.weekDay;
	}
	
	/**
	 * Method to validate weekDay member attribute. 
	 * @throws SchedulerException when weekDay is invalid. 
	 */
	private void validateWeekDay()throws SchedulerException{
		if (weekDay==0)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.WeekDay_Key);
		if (weekDay<0 || weekDay>7)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.Invalid,Constants.WeekDay_Key);
	}
}
