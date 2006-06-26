package org.mifos.framework.components.scheduler;

import java.util.Date;

/**
 * @author navitas
 * Created on Aug 12, 2005
 * It defines Inputs which vary across different schedules. e.g. startDate, endDate, recurrence details etc. 
 */
public interface ScheduleInputsIntf {
	/**
	 * Method to set the value of startDate member variable.  
	 * @param startDate is the schedule start date. 
	 * throws SchedulerException
	 */
	public void setStartDate(Date startDate)throws SchedulerException;
	
	/**
	 * Method to set the value of endDate member variable.  
	 * @param endDate is the schedule end date. 
	 * throws SchedulerException
	 */
	public void setEndDate(Date endDate)throws SchedulerException;
	
	/**
	 * Method to set the value of scheduleData member variable.  
	 * @param scheduleData is instance of DayData/WeekData/MonthData Class.
	 * @throws SchedulerException
	 */
	public void setScheduleData(ScheduleDataIntf scheduleData)throws SchedulerException;
	
	/**
	 * Method to set the value of occurrences member variable.  
	 * @param occurrences is the number of schedule dates which this schedule can have.
	 * throws SchedulerException
	 */
	public void setOccurrences(int occurrences)throws SchedulerException;
	
	/**
	 * Method to get the value of startDate member variable.  
	 * @return Date which is the schedule start date. 
	 */
	public Date getStartDate();
	
	/**
	 * Method to get the value of endDate member variable.  
	 * @return Date which is the schedule end date. 
	 */
	public Date getEndDate();
	
	/**
	 * Method to get the value of scheduleData member variable.  
	 * @return instance of DayData/WeekData/MonthData class. 
	 * @throws SchedulerException
	 */
	public ScheduleDataIntf getScheduleData()throws SchedulerException;
	
	/**
	 * Method to get the value of occurrences member variable.  
	 * @return an integer indicating number of schedule dates which this schedule can have.
	 */
	public int getOccurrences();
	
	/**
	 * Method to validate member attributes of class implementing this interface. 
	 * @throws SchedulerException when any of the attribute is invalid. 
	 */
	public void validate()throws SchedulerException;
}
