package org.mifos.framework.components.scheduler;

import java.util.Date;

/**
 * It defines Inputs which vary across different schedules. e.g. startDate, endDate, recurrence details etc.
 */

public class ScheduleInputs implements ScheduleInputsIntf {
	/** startDate is schedule start date.
	 */
	private Date startDate;
	
	/** endDate is schedule end date and it is optional.
	 */
	private Date endDate;
	
	/** occurrences is the number of schedule dates which this schedule can have and it is optional.
	 */
	private int occurrences;
	
	/** scheduleData is instance of DayData/WeekData/MonthData Class.
	 */
	private ScheduleDataIntf scheduleData;
	
	/**
	 * Method to set the value of startDate member variable.  
	 * @param startDate is the schedule start date. 
	 * throws SchedulerException
	 */
	public void setStartDate(Date startDate) throws SchedulerException{
		this.startDate=startDate;
		this.validateStartDate();		
	}
	
	/**
	 * Method to set the value of endDate member variable.  
	 * @param endDate is the schedule end date. 
	 * throws SchedulerException
	 */
	public void setEndDate(Date endDate)throws SchedulerException{
		this.endDate=endDate;
		this.validateEndDate();	
	}
	
	/**
	 * Method to set the value of scheduleData member variable.  
	 * @param scheduleData is instance of DayData/WeekData/MonthData class.
	 * @throws SchedulerException
	 */
	public void setScheduleData(ScheduleDataIntf scheduleData)throws SchedulerException{
		if(scheduleData==null)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleData_Key);
		scheduleData.validate();
		this.scheduleData=scheduleData;
	}
	
	/**
	 * Method to set the value of occurrences member variable.  
	 * @param occurrences is the number of schedule dates which this schedule can have.
	 * throws SchedulerException
	 */
	public void setOccurrences(int occurrences)throws SchedulerException{
		this.occurrences=occurrences;
		this.validateOccurrences();
	}
	
	/**
	 * Method to get the value of startDate member variable.  
	 * @return Date which is the schedule start date. 
	 */
	public Date getStartDate(){
		return startDate;
	}
	
	/**
	 * Method to get the value of endDate member variable.  
	 * @return Date which is the schedule end date. 
	 */
	public Date getEndDate(){
		return endDate;
	}
	
	/**
	 * Method to get the value of scheduleData member variable.  
	 * @return instance of DayData/WeekData/MonthData class. 
	 * @throws SchedulerException
	 */
	public ScheduleDataIntf getScheduleData() throws SchedulerException{
		if (scheduleData==null)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleData_Key);
		return scheduleData;
	}
	
	/**
	 * Method to get the value of occurrences member variable.  
	 * @return an integer indicating number of schedule dates which this schedule can have.
	 */
	public int getOccurrences(){
		return occurrences;
	}
	
	/**
	 * Method to validate member attributes of ScheduleInput class. 
	 * @throws SchedulerException when any of the attribute is invalid. 
	 */
	public void validate()throws SchedulerException{
		if (scheduleData==null)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleData_Key);
		validateStartDate();
		validateEndDate();
		validateOccurrences();
	}
	
	/**
	 * Method to validate startDate member attribute. 
	 * @throws SchedulerException start date is invalid. 
	 */
	private void validateStartDate()throws SchedulerException{
		if (startDate==null)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.StartDate_Key);
		if(endDate!=null)
			if(startDate.after(endDate))
				throw SchedulerExceptionFactory.getSchedulerException(Constants.Invalid,Constants.StartDate_Key);
	}
	
	/**
	 * Method to validate endDate member attribute. 
	 * @throws SchedulerException end date is invalid. 
	 */
	private void validateEndDate()throws SchedulerException{
		if(endDate!=null && startDate!=null)
			if(endDate.before(startDate))
				throw SchedulerExceptionFactory.getSchedulerException(Constants.Invalid,Constants.EndDate_Key);
	}
	
	/**
	 * Method to validate occurrences member attribute. 
	 * @throws SchedulerException occurrences is invalid. 
	 */
	private void validateOccurrences()throws SchedulerException{
		if (occurrences<0)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.Occurrences_Key);
	}
}
