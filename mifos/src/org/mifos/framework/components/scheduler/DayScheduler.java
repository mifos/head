package org.mifos.framework.components.scheduler;

import java.util.Calendar;
import java.util.Date;


/**
 * @author navitas
 * Created on Aug 12, 2005
 * Generates schedules based on day recurrence.
 * To generate schedule dates it works on scheduleInputs which wraps has DayData instance, startDate, endDate and occurrences. 
 */

public class DayScheduler extends AbstractScheduler{
		
	/**
	 * Method to calculate and return next schedule date as per DayData instance, obtained from schedule inputs
	 * It does not check for the boundary conditions i.e startdate and enddate and 
	 * does not considers weekoff and holidays while calculating next schedule date.
	 * @param startDate a date after which the next schedule date is to be calculated.
	 * @return next schedule date 
	 */
	protected Date getNextDate(Date startDate) throws SchedulerException{
		gc.setTime(startDate);
		//apply recurrence to startDate
		gc.add(Calendar.DAY_OF_WEEK,scheduleInputs.getScheduleData().getRecurAfter());
		return gc.getTime();
	}
	
	/**
	 * Method to calculate and return immediate/first schedule date.
	 * @param startDate date after which schedule date is to be calculated.
	 * @return schedule date 
	 * @throws SchedulerException
	 */
	protected Date getFirstDate(Date startDate)throws SchedulerException{
		return this.getNextDate(startDate);
	}
	
	protected Date getFirstDateAfterRecurrence(Date startDate)throws SchedulerException{
		return getFirstDate(startDate);
	}
}
