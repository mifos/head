package org.mifos.framework.components.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Calendar;

import org.mifos.framework.util.helpers.DateUtils;

public abstract class  AbstractScheduler implements SchedulerIntf{
	/** gc is the calender used to calculates schedule dates.
	 */
	protected GregorianCalendar gc = new GregorianCalendar();

	/** scheduleInputs is the instance of scheduleInputs which wraps, DayData/WeekData/MonthData instance, startData, endDate and occurrences.
	 */
	protected ScheduleInputsIntf scheduleInputs=null;

	/** holidayList is the list of dates on which there is a holiday.
	 */
	protected List holidayList=null;

	/** weekOffList is the list of integers indicating weekdays on which it is off.
	 */
	protected List weekOffList=null;

	/** scheduleHolidayOption tells how to calculate next schedule date for a schedule date that lies on a holiday.
	 */
	protected int scheduleHolidayOption=0;

	/** duplicateScheduleDates tells whether to include same schedule date again if holiday is scheduled on that date.
	 * The default is true.
	 */
	protected boolean duplicateScheduleDates=true;

	/**
	 * Method to validate date as per scheduleinputs.
	 * @param scheduleDate a date object to be validated.
	 * @return true if date is valid schedule date, otherwise false.
	 * @throws SchedulerException
	 */
	public boolean isValidScheduleDate(Date scheduleDate)throws SchedulerException{
		Date currentScheduleDate;
		Date endDate=scheduleInputs.getEndDate();
		if(scheduleDate==null)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.API_Param_Key);

		if (endDate!=null){
			return isValidScheduleDateLimitByEndDate(scheduleDate, endDate);
		}else{
			int occurrences=scheduleInputs.getOccurrences();
			return (occurrences>0)?isValidScheduleDateLimitByOccurrences(scheduleDate,occurrences):isValidScheduleDateLimitWOLimit(scheduleDate);
		}
	}

	/**
	 * Method to validate date as per scheduleinputs, where schedule is limit by endDate.
	 * @param scheduleDate a date object to be validated.
	 * @param endDate It is schedule enddate.
	 * @return true if date is valid schedule date, otherwise false.
	 * @throws SchedulerException
	 */
	private boolean isValidScheduleDateLimitByEndDate(Date scheduleDate, Date endDate)throws SchedulerException{
		Date sDate = getFirstDate(scheduleInputs.getStartDate());
		Date currentScheduleDate=getNextWorkingDate(sDate);
		Date nextDate=null;
		while((currentScheduleDate.compareTo(scheduleDate)<0) && currentScheduleDate.compareTo(endDate)<0){
			for(nextDate=getNextDate(sDate);currentScheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate));
			//end-for
			sDate=nextDate;
			currentScheduleDate=getNextWorkingDate(sDate);
		}

		return (currentScheduleDate.compareTo(scheduleDate)==0 && currentScheduleDate.compareTo(endDate)<=0)?true:false;
	}

	/**
	 * Method to validate date as per scheduleinputs, where schedule is limit by occurrences.
	 * @param scheduleDate a date object to be validated.
	 * @param occurrences is the total number of schedule dates that this schedule can have
	 * @return true if date is valid schedule date, otherwise false.
	 * @throws SchedulerException
	 */
	private boolean isValidScheduleDateLimitByOccurrences(Date scheduleDate, int occurrences)throws SchedulerException{
		Date sDate=getFirstDate(scheduleInputs.getStartDate());
		Date currentScheduleDate=getNextWorkingDate(sDate);
		Date nextDate=null;
		for(int currentNumber=1; (currentScheduleDate.compareTo(scheduleDate)<0) && currentNumber<occurrences; currentNumber++){
			for(nextDate=getNextDate(sDate);currentScheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate),currentNumber++);
			//end-for
			sDate=nextDate;
			currentScheduleDate=getNextWorkingDate(sDate);
		}
		return (currentScheduleDate.compareTo(scheduleDate)==0)?true:false;
	}

	/**
	 * Method to validate date as per scheduleinputs.
	 * @param scheduleDate a date object to be validated.
	 * @return true if date is valid schedule date, otherwise false.
	 * @throws SchedulerException
	 */
	private boolean isValidScheduleDateLimitWOLimit(Date scheduleDate)throws SchedulerException{
		Date sDate = getFirstDate(scheduleInputs.getStartDate());
		Date currentScheduleDate=getNextWorkingDate(sDate);
		Date nextDate=null;
		while(DateUtils.getDateWithoutTimeStamp(currentScheduleDate.getTime()).
				compareTo(DateUtils.getDateWithoutTimeStamp(scheduleDate.getTime()))<0){
			for(nextDate=getNextDate(sDate);currentScheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate));
			//end-for
			sDate=nextDate;
			currentScheduleDate=getNextWorkingDate(sDate);
		}
		return (DateUtils.getDateWithoutTimeStamp(currentScheduleDate.getTime()).
				compareTo(DateUtils.getDateWithoutTimeStamp(scheduleDate.getTime()))==0)?true:false;
		

	}

	/**
	 * Method to calculate and return next schedule date as per schedule inputs.
	 * It also checks for the boundry conditions i.e startDate and endDate.
	 * @param startDate a date after which the next schedule date is to be calculated.
	 * @return next schedule date as per scheduleinputs.
	 * @throws SchedulerException
	 */
	public Date getNextScheduleDate(Date startDate)throws SchedulerException{
		if(startDate==null)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.API_Param_Key);

		Date endDate=scheduleInputs.getEndDate();
		if (endDate!=null)
			return getNextScheduleDateLimitByEndDate(startDate, endDate);
		else{
			int occurrences=scheduleInputs.getOccurrences();
			return (occurrences>0)? getNextScheduleDateLimitByOccurrences(startDate,occurrences):getNextScheduleDateWOEndLimit(startDate);
		}
	}

	public Date getNextScheduleDateAfterRecurrence(Date startDate)throws SchedulerException{
		if(startDate==null)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL,Constants.API_Param_Key);

		Date endDate=scheduleInputs.getEndDate();
		if (endDate!=null)
			return getNextScheduleDateAfterRecurrenceLimitByEndDate(startDate, endDate);
		else{
			int occurrences=scheduleInputs.getOccurrences();
			return (occurrences>0)? getNextScheduleDateAfterRecurrenceLimitByOccurrences(startDate,occurrences):getNextScheduleDateAfterRecurrenceWOEndLimit(startDate);
		}
	}
	/**
	 * Method to calculate and return next schedule date after startDate limit by endDate.
	 * @param startDate a date after which the next schedule date is to be calculated.
	 * @param endDate It is schedule end date.
	 * @return next schedule date as per scheduleinputs limit by endDate.
	 * @throws SchedulerException
	 */
	private Date getNextScheduleDateLimitByEndDate(Date startDate, Date endDate)throws SchedulerException{
		//get first schedule date
		Date sDate = getFirstDate(scheduleInputs.getStartDate());
		Date currentScheduleDate=getNextWorkingDate(sDate);
		//Find next schedule date after startDate limit by endDate
		Date nextDate=null;
		while(currentScheduleDate.compareTo(startDate)<=0 && currentScheduleDate.compareTo(endDate)<0){
			for(nextDate=getNextDate(sDate);currentScheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate));
			//end-for
			sDate=nextDate;
			currentScheduleDate=getNextWorkingDate(sDate);
		}
		//return null, if schedule date is after endDate
		return (currentScheduleDate.compareTo(endDate)<=0)?currentScheduleDate:null;
	}

	private Date getNextScheduleDateAfterRecurrenceLimitByEndDate(Date startDate, Date endDate)throws SchedulerException{
		//get first schedule date
		Date sDate = getFirstDateAfterRecurrence(scheduleInputs.getStartDate());
		Date currentScheduleDate=getNextWorkingDate(sDate);
		//Find next schedule date after startDate limit by endDate
		Date nextDate=null;
		while(currentScheduleDate.compareTo(startDate)<=0 && currentScheduleDate.compareTo(endDate)<0){
			for(nextDate=getNextDate(sDate);currentScheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate));
			//end-for
			sDate=nextDate;
			currentScheduleDate=getNextWorkingDate(sDate);
		}
		//return null, if schedule date is after endDate
		return (currentScheduleDate.compareTo(endDate)<=0)?currentScheduleDate:null;
	}
	
	/**
	 * Method to calculate and return next schedule date after date passed in as parameter limit by occurrences.
	 * @param startDate a date after which the next schedule date is to be calculated.
	 * @param occurrences is the total number of schedule dates that this schedule can have
	 * @return next schedule date as per scheduleinputs limit by occurrences.
	 * @throws SchedulerException
	 */
	private Date getNextScheduleDateLimitByOccurrences(Date startDate, int occurrences)throws SchedulerException{
		//get first schedule date
		Date sDate = getFirstDate(scheduleInputs.getStartDate());
		Date currentScheduleDate=getNextWorkingDate(sDate);
		//Find next schedule date after startDate limit by occurrences
		Date nextDate=null;
		for (int currentNumber=1;(currentScheduleDate.compareTo(startDate)<=0) && currentNumber<occurrences ;currentNumber++){
			for(nextDate=getNextDate(sDate);currentScheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate),currentNumber++);
			//end-for
			sDate=nextDate;
			currentScheduleDate=getNextWorkingDate(sDate);
		}

		//return null, if schedule date does not lie within occurrences
		return (currentScheduleDate.compareTo(startDate)>0)?currentScheduleDate:null;
	}

	
	private Date getNextScheduleDateAfterRecurrenceLimitByOccurrences(Date startDate, int occurrences)throws SchedulerException{
		//get first schedule date
		Date sDate = getFirstDateAfterRecurrence(scheduleInputs.getStartDate());
		Date currentScheduleDate=getNextWorkingDate(sDate);
		//Find next schedule date after startDate limit by occurrences
		Date nextDate=null;
		for (int currentNumber=1;(currentScheduleDate.compareTo(startDate)<=0) && currentNumber<occurrences ;currentNumber++){
			for(nextDate=getNextDate(sDate);currentScheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate),currentNumber++);
			//end-for
			sDate=nextDate;
			currentScheduleDate=getNextWorkingDate(sDate);
		}

		//return null, if schedule date does not lie within occurrences
		return (currentScheduleDate.compareTo(startDate)>0)?currentScheduleDate:null;
	}
	/**
	 * Method to calculate and return next schedule date after startDate
	 * @param startDate a date after which the next schedule date is to be calculated.
	 * @return next schedule date as per scheduleinputs
	 * @throws SchedulerException
	 */
	private Date getNextScheduleDateWOEndLimit(Date startDate)throws SchedulerException{
		//get first schedule date
		Date sDate = getFirstDate(scheduleInputs.getStartDate());
		Date currentScheduleDate=getNextWorkingDate(sDate);
		Date nextDate=null;
		//Find next schedule date after date received in parameter
		while(currentScheduleDate.compareTo(startDate)<=0){
			for(nextDate=getNextDate(sDate);currentScheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate));
			//end-for
			sDate=nextDate;
			currentScheduleDate=getNextWorkingDate(sDate);
		}
		return currentScheduleDate;
	}

	private Date getNextScheduleDateAfterRecurrenceWOEndLimit(Date startDate)throws SchedulerException{
		//get first schedule date
		Date sDate = getFirstDateAfterRecurrence(scheduleInputs.getStartDate());
		Date currentScheduleDate=getNextWorkingDate(sDate);
		Date nextDate=null;
		//Find next schedule date after date received in parameter
		while(currentScheduleDate.compareTo(startDate)<=0){
			for(nextDate=getNextDate(sDate);currentScheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate));
			//end-for
			sDate=nextDate;
			currentScheduleDate=getNextWorkingDate(sDate);
		}
		return currentScheduleDate;
	}
	/**
	 * Method to calculate and return list of schedule dates as per schedule inputs.
	 * @param endDate schedule dates must lie in between startdate(inclusive) and enddate(inclusive if given).
	 * @return List of schedule dates as per schedule inputs.
	 * @throws SchedulerException
	 */
	public List getAllDates(Date endDate)throws SchedulerException{
		List scheduleDates= null;

		//when enddate passed in API is null, get the endDate from schedule inputs
		if (endDate==null)
			endDate=scheduleInputs.getEndDate();

		if(endDate!=null){
			scheduleDates=getAllDatesByEndDate(endDate);
		}else{ //when end date is null, chk for occurrences from schedule inputs
			int occurrences=scheduleInputs.getOccurrences();
			if(occurrences>0)
				scheduleDates=this.getAllDates(occurrences);
			else{ //when occurrence is not available , return the first schedule date from startdate.
				scheduleDates=new ArrayList();
				scheduleDates.add(this.getNextScheduleDate(scheduleInputs.getStartDate()));
			}
		}
		//list of schedule dates
		return scheduleDates;
	}



	public List getAllDates()throws SchedulerException
	{

				List scheduleDates=new ArrayList();

				Date startDate = scheduleInputs.getStartDate();
				Calendar startDateCal = new GregorianCalendar();

				startDateCal.setTime(startDate);

				Calendar endDateCal = new GregorianCalendar();
				endDateCal.set(startDateCal.get(Calendar.YEAR), Calendar.DECEMBER,31);


				return getAllDates(endDateCal.getTime());


		}
	/**
	 * Method to calculate and return list of schedule dates as per schedule inputs.
	 * @param endDate schedule dates must lie in between startdate(inclusive) and enddate(inclusive).
	 * @return List of schedule dates as per schedule inputs.
	 * @throws SchedulerException
	 */
	private List getAllDatesByEndDate(Date endDate)throws SchedulerException{
		List scheduleDates= new ArrayList();

		//get first schedule date
		Date sDate = getFirstDate(scheduleInputs.getStartDate());
		Date scheduleDate=getNextWorkingDate(sDate);
		Date nextDate=null;
		//calculate all schedule dates till end date and put in list
		while(!scheduleDate.after(endDate)){
			for(nextDate=getNextDate(sDate);scheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate)){
				//when duplicateSchedulerDates==true, add holiday schedule date to list
				if (duplicateScheduleDates)
					scheduleDates.add(scheduleDate);
			}
			//add schedule date to list
			scheduleDates.add(scheduleDate);
			//get next schedule date after applying recurrence
			sDate=nextDate;
			scheduleDate=getNextWorkingDate(sDate);
		}//end-while
		return scheduleDates;
	}

	/**
	 * Method to calculate and return list of schedule date as per schedule inputs.
	 * @param occurrences number of schedule dates to be returned
	 * @return List of schedule dates as per schedule inputs.
	 * @throws SchedulerException
	 */
	public List getAllDates(int occurrences)throws SchedulerException{
		List scheduleDates=new ArrayList();

		if (occurrences<=0)
			throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL, Constants.API_Param_Key);

		//get first schedule date
		Date sDate = getFirstDate(scheduleInputs.getStartDate());
		Date scheduleDate=getNextWorkingDate(sDate);
		Date nextDate=null;
		//calculate  occurrences number of schedule dates and put in the list
		for(int dateCount=0;dateCount<occurrences;dateCount++){
			for(nextDate=getNextDate(sDate);scheduleDate.compareTo(nextDate)>=0;nextDate=getNextDate(nextDate),dateCount++){
				//when duplicateSchedulerDates==true, add holiday schedule date to list
				if (duplicateScheduleDates)
					scheduleDates.add(scheduleDate);
			}
			//add schedule date to list
		    scheduleDates.add(scheduleDate);
			//get next working schedule date after applying recurrence
		    sDate=nextDate;
		    scheduleDate=getNextWorkingDate(sDate);
		}
		//list of schedule dates
		return scheduleDates;
	}

	public Date getSpecificScheduleDate(int occurence) throws SchedulerException
	{
		List<Date> l = getAllDates(occurence);
		return l.get(l.size() - 1);
	}

	/**
	 * Method to calculate and return next schedule date, which is a working day
	 * It does not check for the boundary conditions i.e enddate.
	 * It considers weekoff and holidays while calculating next wrk schedule date.
	 * @param sDate a date from which(inclusive) the wrk schedule date is to be calculated.
	 * @return next working schedule date
	 * @throws SchedulerException
	 */
	private Date getNextWorkingDate(Date sDate)throws SchedulerException{
		Date scheduleDate=sDate;
		Date wrkScheduleDate=SchedulerHelper.chkAndGetScheduleDate(scheduleDate,weekOffList, holidayList, scheduleHolidayOption);
		while(wrkScheduleDate==null){
			//get next schedule date after applying recurrence
			scheduleDate=getNextDate(scheduleDate);
			//chk and calculate schedule date for weekoff and holiday
			wrkScheduleDate=SchedulerHelper.chkAndGetScheduleDate(scheduleDate,weekOffList, holidayList, scheduleHolidayOption);
		}
		return wrkScheduleDate;
	}
	
	public Date getPrevScheduleDateAfterRecurrence(Date scheduleDate)throws SchedulerException{
		Date prevScheduleDate=null;
		for(Date currentScheduleDate=getFirstDateAfterRecurrence(scheduleInputs.getStartDate()); 
			(currentScheduleDate.compareTo(scheduleDate)<0);
			prevScheduleDate = currentScheduleDate,currentScheduleDate = getNextDate(currentScheduleDate));
		return prevScheduleDate;		
	}
	
	/**
	 * Method to calculate and return immediate/first schedule date.
	 * This method is implemented by DayScheduler,WeekScheduler and MonthScheduler classes.
	 * @param startDate date after which schedule date is to be calculated.
	 * @return schedule date
	 * @throws SchedulerException
	 */
	abstract protected Date getFirstDate(Date startDate)throws SchedulerException;
	
	abstract protected Date getFirstDateAfterRecurrence(Date startDate)throws SchedulerException;

	/**
	 * Method to calculate and return next schedule date as per DataData/WeekData/MonthData instance, obtained from schedule inputs
	 * It does not check for the boundary conditions i.e startdate and enddate and
	 * does not considers weekoff and holidays while calculating next schedule date.
	 * This method is implemented by DayScheduler,WeekScheduler and MonthScheduler classes.
	 * @param startDate a date after which the next schedule date is to be calculated.
	 * @return next schedule date
	 * @throws SchedulerException
	 */
	abstract protected Date getNextDate(Date startDate)throws SchedulerException;

	/**
	 * Method to set scheduleInputs member variable.
	 * It wraps MonthData instance, startDate, endDate and occurrences
	 * @param scheduleInputs scheduleInputs for month scheduler.
	 * @throws SchedulerException
	 */
	public void setScheduleInputs(ScheduleInputsIntf scheduleInputs)throws SchedulerException{
		this.scheduleInputs = scheduleInputs;
	}

	/**
	 * Method to set list of dates on which there is a holiday.
	 * When a date on which a holiday lie is also a weekoff, then it will be considered as week off only.
	 * @param holidayList is the list of holidays
	 */
	public void setHolidayList(List holidayList) {
		this.holidayList = holidayList;
	}

	/**
	 * Method to set scheduleHolidayOption which tells how to schedule a date that lies on a holiday.
	 * @param scheduleHolidayOption
	 * @throws SchedulerException
	 */
	public void setScheduleHolidayOption(int scheduleHolidayOption)throws SchedulerException {
		this.scheduleHolidayOption = scheduleHolidayOption;
	}

	/**
	 * Method to set weekOffList that stores the weekdays on which there is off
	 * @param weekOffList
	 * @throws SchedulerException
	 */
	public void setWeekOffList(List weekOffList)throws SchedulerException {
		this.weekOffList = weekOffList;
	}

	/**
	 * Method to set duplicateScheduleDates that tells whether to include same schedule date again if holiday is scheduled on that date.
	 * @param duplicate
	 */
	public  void setDuplicateScheduleDates(boolean duplicate){
		this.duplicateScheduleDates=duplicate;
	}
}
