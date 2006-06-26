package org.mifos.framework.components.scheduler;

import java.util.Calendar;
import java.util.Date;

/**
 * @author navitas
 * Created on Aug 12, 2005
 * Generates schedules based on week recurrence.
 * To generate schedule dates it works on scheduleInputs which wraps WeekData instance, startDate, endDate and occurrences. 
 */

public class WeekScheduler extends AbstractScheduler{
	/**
	 * Method to calculate and return immediate/first schedule date.
	 * @param startDate date after which schedule date is to be calculated.
	 * @return schedule date 
	 * @throws SchedulerException
	 */
	protected Date getFirstDate(Date startDate)throws SchedulerException{
		gc.setTime(startDate);
		ScheduleDataIntf scheduleData=scheduleInputs.getScheduleData(); 
		if (gc.get(Calendar.DAY_OF_WEEK)>scheduleData.getWeekDay()){
			gc.add(Calendar.WEEK_OF_MONTH,1);
		}
		gc.set(Calendar.DAY_OF_WEEK,scheduleData.getWeekDay());
		return gc.getTime();
	}
	
	protected Date getFirstDateAfterRecurrence(Date startDate)throws SchedulerException{
		return getNextDate(startDate);
	}
	
	/**
	 * Method to calculate and return next schedule date as per WeekData instance, obtained from schedule inputs
	 * It does not check for the boundary conditions i.e startdate and enddate and
	 * does not considers weekoff and holidays while calculating next schedule date.
	 * @param startDate a date after which the next schedule date is to be calculated.
	 * @return next schedule date 
	 * @throws SchedulerException
	 */
	protected Date getNextDate(Date startDate)throws SchedulerException{
		gc.setTime(startDate);
		//apply recurrence to startDate recieved in parameter
		gc.add(Calendar.WEEK_OF_MONTH,scheduleInputs.getScheduleData().getRecurAfter());
		return gc.getTime();
	}
}
