package org.mifos.framework.components.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author navitas
 * Created on Aug 12, 2005
 * Generates schedules based on month recurrence.
 * To generate schedule dates it works on scheduleInputs which wraps MonthData instance, startDate, endDate and occurrences. 
 */

public class MonthScheduler extends AbstractScheduler{
		
	/**
	 * Method to calculate and return immediate/first schedule date.
	 * @param startDate date after which schedule date is to be calculated.
	 * @return schedule date
	 * @throws SchedulerException 
	 */
	protected Date getFirstDate(Date startDate)throws SchedulerException{
		Date scheduleDate=null;
		gc.setTime(startDate);
		ScheduleDataIntf scheduleData=scheduleInputs.getScheduleData(); 
			
		if (scheduleData.getRecurType().equals(Constants.MonthlyOnDate)){
			int dt = gc.get(GregorianCalendar.DATE);
			//if date passed in, is after the date on which schedule has to lie, move to next month 
			if(dt>scheduleData.getDayNumber())
				gc.add(GregorianCalendar.MONTH,1);
			//set the date on which schedule has to lie
			int M1 = gc.get(GregorianCalendar.MONTH);
			gc.set(GregorianCalendar.DATE,scheduleData.getDayNumber());
			int M2 = gc.get(GregorianCalendar.MONTH);
			int daynum=scheduleData.getDayNumber();
			while(M1!=M2){
				gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
				gc.set(GregorianCalendar.DATE,daynum-1);
				M2 = gc.get(GregorianCalendar.MONTH);
				daynum--;
			}
			scheduleDate=gc.getTime();
			
		}else{//recurType=MonthlyOnDay
			//if current weekday is after the weekday on which schedule has to lie, move to next week
			if (gc.get(Calendar.DAY_OF_WEEK)>scheduleData.getWeekDay())
				gc.add(Calendar.WEEK_OF_MONTH,1);
			//set the weekday on which schedule has to lie
			gc.set(Calendar.DAY_OF_WEEK,scheduleData.getWeekDay());
			//if week rank is First, Second, Third or Fourth, Set the respective week.
			//if current week rank is after the weekrank on which schedule has to lie, move to next month
			if(scheduleData.getWeekRank()==Constants.FIRST || scheduleData.getWeekRank()==Constants.SECOND ||  scheduleData.getWeekRank()==Constants.THIRD ||  scheduleData.getWeekRank()==Constants.FOURTH){
				if(gc.get(Calendar.DAY_OF_WEEK_IN_MONTH)>scheduleData.getWeekRank()){
					gc.add(GregorianCalendar.MONTH,1);
					gc.set(GregorianCalendar.DATE,1);
				}
				//set the weekrank on which schedule has to lie
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,scheduleData.getWeekRank());
				scheduleDate=gc.getTime();
			}
			else {//scheduleData.getWeekRank()=Last
				int M1 = gc.get(GregorianCalendar.MONTH);
				//assumption: there are 5 weekdays in the month
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,5);
				int M2 = gc.get(GregorianCalendar.MONTH);
				//if assumption fails, it means there exists 4 weekdays in a month, return last weekday date
				//if M1==M2, means there exists 5 weekdays otherwise 4 weekdays in a month
				if (M1!=M2){
					gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
					gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,4);
				}
				scheduleDate=gc.getTime();
			}
		}
		return scheduleDate;
	}
	
	protected Date getFirstDateAfterRecurrence(Date startDate)throws SchedulerException{
		return getNextDate(startDate);
	}
	
	/**
	 * Method to calculate and return next schedule date as per MonthData instance, obtained from schedule inputs
	 * It does not check for the boundary conditions i.e startdate and enddate and
	 * does not considers weekoff and holidays while calculating next schedule date. 
	 * @param startDate a date after which the next schedule date is to be calculated.
	 * @return next schedule date 
	 * @throws SchedulerException
	 */
	protected Date getNextDate(Date startDate)throws SchedulerException{
		Date scheduleDate=null;
		ScheduleDataIntf scheduleData=scheduleInputs.getScheduleData(); 
		gc.setTime(startDate);
		if(scheduleData.getRecurType().equals(Constants.MonthlyOnDate)){
			
			//move to next month and return date.
			gc.add(GregorianCalendar.MONTH,scheduleData.getRecurAfter());
			int M1 = gc.get(GregorianCalendar.MONTH);
			gc.set(GregorianCalendar.DATE,scheduleData.getDayNumber());
			int M2 = gc.get(GregorianCalendar.MONTH);
			int daynum=scheduleData.getDayNumber();
			while(M1!=M2){
				gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
				gc.set(GregorianCalendar.DATE,daynum-1);
				M2 = gc.get(GregorianCalendar.MONTH);
				daynum--;
			}
			scheduleDate=gc.getTime();
		}else{//recurType=MonthlyOnDay
			if(scheduleData.getWeekRank()==Constants.FIRST || scheduleData.getWeekRank()==Constants.SECOND ||  scheduleData.getWeekRank()==Constants.THIRD ||  scheduleData.getWeekRank()==Constants.FOURTH)
			{
				//apply month recurrence
				gc.add(GregorianCalendar.MONTH,scheduleData.getRecurAfter());
				gc.set(Calendar.DAY_OF_WEEK,scheduleData.getWeekDay());
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,scheduleData.getWeekRank());
				scheduleDate=gc.getTime();
			}else{//weekCount=-1
				gc.set(GregorianCalendar.DATE,15);
				gc.add(GregorianCalendar.MONTH,scheduleData.getRecurAfter());
				gc.set(Calendar.DAY_OF_WEEK,scheduleData.getWeekDay());
				int M1 = gc.get(GregorianCalendar.MONTH);
				//assumption: there are 5 weekdays in the month
				gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,5);
				int M2 = gc.get(GregorianCalendar.MONTH);
				//if assumption fails, it means there exists 4 weekdays in a month, return last weekday date
				//if M1==M2, means there exists 5 weekdays otherwise 4 weekdays	in a month			
				if (M1!=M2){
					gc.set(GregorianCalendar.MONTH,gc.get(GregorianCalendar.MONTH)-1);
					gc.set(GregorianCalendar.DAY_OF_WEEK_IN_MONTH,4);
				}
				scheduleDate=gc.getTime();
			}
		}
		return scheduleDate;
	}	
	
}
