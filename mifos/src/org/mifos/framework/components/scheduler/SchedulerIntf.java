package org.mifos.framework.components.scheduler;

import java.util.List;
import java.util.Date;

/**
 * Returns different schedules based on day/week/month recurrence.
 * It is a scheduler interface to user.
 * It delegates method calls to DayScheduler, WeekScheduler or 
 * MonthScheduler based on the type of recurrence choosed.
 */

public interface SchedulerIntf {
	/**
	 * Method to validate date as per scheduleinputs.
	 * It delegates method call to Day/Week/MonthScheduler.
	 * @param scheduleDate a date object to be validated.
	 * @return true if date is valid schedule date, otherwise false.
	 * @throws SchedulerException
	 */
	public boolean isValidScheduleDate(Date scheduleDate)throws SchedulerException;

	/**
	 * Method to calculate and return next schedule date as per schedule inputs.
	 * It delegates method call to Day/Week/MonthScheduler.
	 * @param startDate a date after which the next schedule date is to be calculated.
	 * @return next schedule date as per scheduleinputs.
	 * @throws SchedulerException
	 */
	public Date getNextScheduleDate(Date startDate)throws SchedulerException;
	
	public Date getNextScheduleDateAfterRecurrence(Date startDate)throws SchedulerException;
	
	public Date getPrevScheduleDateAfterRecurrence(Date scheduleDate)throws SchedulerException;
	
	/**
	 * Method to calculate and return list of schedule date as per schedule inputs.
	 * It delegates method call to Day/Week/MonthScheduler.
	 * @param endDate schedule dates must lie in between startdate(inclusive) and enddate(inclusive).
	 * @return List of schedule dates as per schedule inputs.
	 * @throws SchedulerException
	 */
	public List<Date> getAllDates(Date endDate)throws SchedulerException;

	/**
	 * Method to calculate and return list of schedule date as per schedule inputs.
	 * It delegates method call to Day/Week/MonthScheduler.
	 * @param occurrences it is the number of schedule dates to be returned
	 * @return List of schedule dates as per schedule inputs.
	 * @throws SchedulerException
	 */
	public List<Date> getAllDates(int occurrences)throws SchedulerException;

	public List<Date> getAllDates()throws SchedulerException;

	public Date getSpecificScheduleDate(int occurence) throws SchedulerException;

	/**
	 * Method to set scheduler and its scheduleInputs.
	 * It chooses which scheduler instance (Day/Week/Month) is to be created based on the scheduleData stored in schedule inputs.
	 * It delegates method call to Day/Week/MonthScheduler.
	 * @param scheduleInputs scheduleInputs for day/week/month scheduler.
	 * @throws SchedulerException
	 */
	public void setScheduleInputs(ScheduleInputsIntf scheduleInputs)throws SchedulerException;

	/**
	 * Method to set list of holidays.
	 * @param holidayList is the list of holidays
	 */
	public void setHolidayList(List holidayList);

	/**
	 * Method to set scheduleHolidayOption which tells how to schedule a date that lies on a holiday.
	 * @param scheduleHolidayOption
	 * @throws SchedulerException
	 */
	public void setScheduleHolidayOption(int scheduleHolidayOption)throws SchedulerException;

	/**
	 * Method to set weekOffList that stores the weekdays on which there is off
	 * @param weekOffList
	 * @throws SchedulerException
	 */
	public void setWeekOffList(List weekOffList)throws SchedulerException;

	/**
	 * Method to set duplicateScheduleDates that tells whether to include same schedule date again if holiday is scheduled on that date.
	 * @param duplicate
	 */
	public  void setDuplicateScheduleDates(boolean duplicate);
}
