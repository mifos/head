package org.mifos.application.accounts.loan.business;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.*;
import org.mifos.framework.util.helpers.DateUtils;

public class CalendarTest {

    private Date createPreviousDate(int numberOfDays) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -numberOfDays);
        Date pastDate =  DateUtils.getDateWithoutTimeStamp(calendar.getTime());
        return pastDate;
    }

	Date getFirstDateForWeek(GregorianCalendar gc, Date startDate, int meetingDayOfWeek) {
		gc.setTime(startDate);

		// Jump to next week if the required weekday has passed for current week
		if (gc.get(Calendar.DAY_OF_WEEK) > meetingDayOfWeek) {
			gc.add(Calendar.WEEK_OF_MONTH, 1);
		}
		
		// Set the day of week as the require weekday
		gc.set(Calendar.DAY_OF_WEEK, meetingDayOfWeek);
		return gc.getTime();
	}

	@Test
	public void testDayOfWeek() {
		Date twoWeeksAgo = createPreviousDate(14);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(twoWeeksAgo);
		
		Assert.assertEquals(cal.get(Calendar.DAY_OF_WEEK), 1);
		System.out.println("two weeks ago: " + cal.getTime());
		
		Date nextMondayAfterTwoWeeksAgo = createPreviousDate(13);
		System.out.println("Expected: " + nextMondayAfterTwoWeeksAgo);
		Date computed = getFirstDateForWeek(cal, twoWeeksAgo, 2);
		System.out.println ("actual: " + cal.getTime());
		Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(nextMondayAfterTwoWeeksAgo),
				     DateUtils.getDateWithoutTimeStamp(computed));
	}
		

		@Test
		public void testDayOfWeek2() {
			Date twoWeeksAgo = createPreviousDate(14);
			GregorianCalendar cal = new GregorianCalendar();
			cal.setFirstDayOfWeek(2);
			cal.setTime(twoWeeksAgo);
			
			Assert.assertEquals(cal.get(Calendar.DAY_OF_WEEK), 1);
			System.out.println("two weeks ago: " + cal.getTime());
			
			Date nextMondayAfterTwoWeeksAgo = createPreviousDate(13);
			System.out.println("Expected: " + nextMondayAfterTwoWeeksAgo);
			Date computed = getFirstDateForWeek(cal, twoWeeksAgo, 2);
			System.out.println ("actual: " + cal.getTime());
			Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(nextMondayAfterTwoWeeksAgo),
					     DateUtils.getDateWithoutTimeStamp(computed));
			
	}

		/*
		@Test
		public void testDayOfWeek3() {
			//Wednesday, June 4, 2008
			GregorianCalendar aWednesdayCal = new GregorianCalendar(2008, 5, 4);
			Date aWednesday = aWednesdayCal.getTime();
			
			
			Date computed = getFirstDateForWeek(new GregorianCalendar, aWednesday, 2);
			System.out.println ("actual: " + cal.getTime());
			Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(nextMondayAfterTwoWeeksAgo),
					     DateUtils.getDateWithoutTimeStamp(computed));
		}
		*/
}
