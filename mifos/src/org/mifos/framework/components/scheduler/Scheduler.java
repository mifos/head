package org.mifos.framework.components.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author navitas
 * Created on Aug 12, 2005
 * Returns different schedules based on day/week/month recurrence.
 * It delegates method calls to DayScheduler, WeekScheduler or MonthScheduler based on the type of recurrence choosed.
 */

public class Scheduler implements SchedulerIntf {
	/** scheduler holds DayScheduler/WeekScheduler/MonthScheduler instance;
	 */
	private SchedulerIntf scheduler=null;

	/** holidayList is the list of dates on which there is a holiday.
	 */
	private List holidayList=null;

	/** weekOffList is the list of integers indicating weekdays on which it is off.
	 */
	private List weekOffList=null;

	/** scheduleHolidayOption tells how to calculate next schedule date for which schedule date lies on a holiday.
	 */
	private int scheduleHolidayOption=0;

	/** duplicateScheduleDates tells whether to include same schedule date again if holiday is scheduled on that date.
	 * The default is true.
	 */
	private boolean duplicateScheduleDates=true;

	/**
	 * Method to validate date as per scheduleinputs.
	 * It delegates method call to Day/Week/MonthScheduler.
	 * @param scheduleDate a date object to be validated.
	 * @return true if date is valid schedule date, otherwise false.
	 * @throws SchedulerException
	 */
	public boolean isValidScheduleDate(Date scheduleDate)throws SchedulerException{
		if(scheduler!=null)
			return scheduler.isValidScheduleDate(scheduleDate);
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleInputs_Key);
	}

	/**
	 * Method to calculate and return next schedule date as per schedule inputs.
	 * It delegates method call to Day/Week/MonthScheduler.
	 * @param startDate a date after which the next schedule date is to be calculated.
	 * @return next schedule date as per scheduleinputs.
	 * @throws SchedulerException
	 */
	public Date getNextScheduleDate(Date startDate)throws SchedulerException{
		if(scheduler!=null)
			return scheduler.getNextScheduleDate(startDate);
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleInputs_Key);
	}
	
	public Date getNextScheduleDateAfterRecurrence(Date startDate)throws SchedulerException{
		if(scheduler!=null)
			return scheduler.getNextScheduleDateAfterRecurrence(startDate);
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleInputs_Key);
	}
	
	public Date getPrevScheduleDateAfterRecurrence(Date scheduleDate)throws SchedulerException{
		if(scheduler!=null)
			return scheduler.getPrevScheduleDateAfterRecurrence(scheduleDate);
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleInputs_Key);
	}
	/**
	 * Method to calculate and return list of schedule date as per schedule inputs.
	 * It delegates method call to Day/Week/MonthScheduler.
	 * @param endDate schedule dates must lie in between startdate(inclusive) and enddate(inclusive).
	 * @return List of schedule dates as per schedule inputs.
	 * @throws SchedulerException
	 */
	public List getAllDates(Date endDate)throws SchedulerException{
		if(scheduler!=null)
			return scheduler.getAllDates(endDate);
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleInputs_Key);
	}

	/**
	 * Method to calculate and return list of schedule date as per schedule inputs.
	 * It delegates method call to Day/Week/MonthScheduler.
	 * @param occurrences it is the number of schedule dates to be returned
	 * @return List of schedule dates as per schedule inputs.
	 * @throws SchedulerException
	 */
	public List getAllDates(int occurrences)throws SchedulerException{
		if(scheduler!=null)
			return scheduler.getAllDates(occurrences);
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleInputs_Key);
	}

	public List getAllDates()throws SchedulerException
	{
		if(scheduler!=null)
			return scheduler.getAllDates();
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleInputs_Key);

	}

	public Date getSpecificScheduleDate(int occurence) throws SchedulerException
	{
		if(scheduler!=null)
			return scheduler.getSpecificScheduleDate(occurence);
		throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleInputs_Key);
	}

	/**
	 * Method to set scheduler and its scheduleInputs.
	 * It chooses which scheduler instance (Day/Week/Month) is to be created based on the scheduleData stored in schedule inputs.
	 * It delegates method call to Day/Week/MonthScheduler.
	 * @param scheduleInputs scheduleInputs for day/week/month scheduler.
	 * @throws SchedulerException
	 */
	public void setScheduleInputs(ScheduleInputsIntf scheduleInputs)throws SchedulerException{
		if(scheduleInputs==null)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.ScheduleInputs_Key);
		//validate scheduler inputs
		scheduleInputs.validate();
		//get a scheduler based on schedule data obtained from schedule inputs
		this.scheduler=getScheduler(scheduleInputs.getScheduleData());
		//initialize scheduler
		this.scheduler.setHolidayList(holidayList);
		this.scheduler.setWeekOffList(weekOffList);
		this.scheduler.setScheduleHolidayOption(scheduleHolidayOption);
		this.scheduler.setDuplicateScheduleDates(duplicateScheduleDates);
		this.scheduler.setScheduleInputs(scheduleInputs);
	}

	/**
	 * Method to get DayScheduler/WeekScheduler/MonthScheduler based on schedule data
	 * @param scheduleData based on schedule data Day/Week/Month Scheduler will be created.
	 * @return an instance of DayScheduler,WeekScheduler or MonthScheduler
	 * @throws SchedulerException
	 */
	private SchedulerIntf getScheduler(ScheduleDataIntf scheduleData)throws SchedulerException{
		Class c = scheduleData.getClass();
		String str = c.getName();
		str=str.replaceFirst(Constants.Data,Constants.Scheduler);
		try{
			 c = Class.forName(str);
			 return (SchedulerIntf)c.newInstance();
		}catch(ClassNotFoundException cne){
			throw SchedulerExceptionFactory.getSchedulerException(cne.getMessage(), false);
		}
		catch(InstantiationException ie){
			throw SchedulerExceptionFactory.getSchedulerException(ie.getMessage(), false);
		}
		catch(IllegalAccessException iae){
			throw SchedulerExceptionFactory.getSchedulerException(iae.getMessage(), false);
		}
	}
	/**
	 * Method to set list of dates on which there is a holiday.
	 * @param holidayList is the list of holidays
	 * @throws SchedulerException
	 */
	public void setHolidayList(List holidayList){
		if(scheduler!=null)
			scheduler.setHolidayList(holidayList);
		this.holidayList=holidayList;
	}

	/**
	 * Method to set scheduleHolidayOption which tells how to schedule a date that lies on a holiday.
	 * @param scheduleHolidayOption
	 * @throws SchedulerException
	 */
	public void setScheduleHolidayOption(int scheduleHolidayOption)throws SchedulerException {
		if(scheduler!=null)
			scheduler.setScheduleHolidayOption(scheduleHolidayOption);
		this.scheduleHolidayOption=scheduleHolidayOption;
		validateScheduleHolidayOption();
	}

	/**
	 * Method to set weekOffList that stores the weekdays on which there is off
	 * @param weekOffList
	 * @throws SchedulerException
	 */
	public void setWeekOffList(List weekOffList) throws SchedulerException{
		if(scheduler!=null)
			scheduler.setWeekOffList(weekOffList);
		this.weekOffList=weekOffList;
		validateWeekOffList();
	}

	/**
	 * Method to set duplicateScheduleDates that tells whether to include same schedule date again if holiday is scheduled on that date.
	 * @param duplicate
	 */
	public void setDuplicateScheduleDates(boolean duplicate){
		if (scheduler!=null)
			scheduler.setDuplicateScheduleDates(duplicate);
		this.duplicateScheduleDates=duplicate;
	}

	private void validateWeekOffList() throws SchedulerException{
		if(weekOffList!=null){
			int weekday=0;
			Iterator it= weekOffList.iterator();
			//prepare a weekday list that has all weekdays
			List weekDayList=new ArrayList();
			for(int wday=1;wday<8;wday++)
				weekDayList.add(new Integer(wday));

			while(it.hasNext()){
				weekday=((Integer)it.next()).intValue();
				//validate a weekday in week off list
				if (weekday<1 || weekday>7)
					throw SchedulerExceptionFactory.getSchedulerException(Constants.Invalid,Constants.WeekDayInWeekOffList_Key);
				//remove weekday that is off from weekday list
				weekDayList.remove(new Integer(weekday));
			}
			//when all weekdays are present in weekoff list, throw an exception
			if (weekDayList.size()==0)
				throw SchedulerExceptionFactory.getSchedulerException(Constants.Invalid,Constants.WeekOffList_Key);
		}
	}

	private void validateScheduleHolidayOption() throws SchedulerException{
		if(!(scheduleHolidayOption==Constants.NEXT_WRK_DAY || scheduleHolidayOption==Constants.NEXT_SCH_DATE || scheduleHolidayOption==Constants.SAME_DAY))
			throw SchedulerExceptionFactory.getSchedulerException(Constants.Invalid, Constants.ScheduleHolidayOption_Key);
	}
}
