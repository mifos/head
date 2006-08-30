package org.mifos.framework.components.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 * It provides methods related to weekoff and holiday mgmt.
 * This servers as helper class to DayScheduler, WeekScheduler and MonthScheduler
 */
public class SchedulerHelper {
	/**
	 * Method to check shceduleDate for weekOff and holiday and return a schedule date, which is a working day
	 * @param scheduleDate is date starting from (inclusive) working day is to be find.
	 * @param weekOffList list of weekdays that are off
	 * @param holidayList list of holidays (dates)
	 * @param option indicates how to schedule a date that lies on holiday
	 * @return next date, which is a working day
	 */
	public static Date chkAndGetScheduleDate(Date scheduleDate,  List weekOffList,List holidayList, int option) throws SchedulerException{
		Date sdate = scheduleDate;
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(sdate);

		//if current date is week off get next working date
		if(isWeekOffDay(gc.get(Calendar.DAY_OF_WEEK), weekOffList)){
			sdate=getNextWorkingDate(sdate, weekOffList,gc);
		}
		//if current date is a holiday
		while(isHoliday(sdate,holidayList)){
			//when scheduledate lies on a holiday calculate next schedule date according to option
			//if option is  Next schedule Date, it returns null, since it does not have recurrence pattern to calculated next schedule date.
			if (option==Constants.NEXT_WRK_DAY)
				sdate=getNextWorkingDate(sdate, weekOffList, gc);
			else if(option==Constants.NEXT_SCH_DATE)
					return null;
			else if(option==Constants.SAME_DAY)
				    return sdate;
				else
					throw SchedulerExceptionFactory.getSchedulerException(Constants.NULL, Constants.ScheduleHolidayOption_Key);
		}
		return sdate;
	}

	/**
	 * Method to return a schedule date, which is a working day
	 * @param date after which next working day is to be find.
	 * @param weekOffList list of weekdays that are off
	 * @param gc calendar instance
	 * @return next date, which is a working day
	 */
	public static Date getNextWorkingDate(Date date, List weekOffList, GregorianCalendar gc){
		gc.setTime(date);
	    gc.add(Calendar.DAY_OF_WEEK,1);
	    Date wrkDate;
	    wrkDate=gc.getTime();
	    //increments date by one and checks for week off
	    while(isWeekOffDay(gc.get(Calendar.DAY_OF_WEEK), weekOffList)){
	    	gc.add(Calendar.DAY_OF_WEEK,1);
	    	wrkDate=gc.getTime();
	    }
	    return wrkDate;
	}

	/**
	 * Method checks a date for holiday
	 * @param date to be checked for holiday
	 * @param holidayList list of holidays (dates)
	 * @return true if date is a holiday otherwise false
	 */
	public static boolean isHoliday(Date date, List holidayList){
		if (holidayList!=null){
			Iterator holidayIterator= holidayList.iterator();
			Date holidayDT;
			while(holidayIterator.hasNext()){
				holidayDT=(Date)holidayIterator.next();
				if(holidayDT.compareTo(date)==0)
					return true;
			}
		}
		return false;
	}

	/**
	 * Method checks a date for working day
	 * @param wday integer representing weekday that is to be checked for a working day
	 * @param weekOffList list of weekdays that are off
	 * @return false if date is a working day, otherwise true.
	 */
	public static boolean isWeekOffDay(int wday, List weekOffList){
		if (weekOffList!=null){
			Iterator weekOffIterator=weekOffList.iterator();
			while(weekOffIterator.hasNext()){
				if (wday==((Integer)weekOffIterator.next()).intValue())
					return true;
			}
		}
		return false;
	}

	/**
	 * Method checks a date for working day
	 * @param date to be checked for a working day
	 * @param weekOffList list of weekdays that are off
	 * @param gc calendar instance
	 * @return false if date is a working day, otherwise true.
	 */
	public static boolean isWeekOffDay(Date date, List weekOffList,GregorianCalendar gc){
		gc.setTime(date);
		int wday=gc.get(Calendar.DAY_OF_WEEK);
		if (weekOffList!=null){
			Iterator weekOffIterator=weekOffList.iterator();
			while(weekOffIterator.hasNext()){
				if (wday==((Integer)weekOffIterator.next()).intValue())
					return true;
			}
		}
		return false;
	}
}
