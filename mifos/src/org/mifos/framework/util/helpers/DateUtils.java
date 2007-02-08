/**

 * DateUtils.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.framework.util.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

	public DateUtils() {
	}

	public static Date getCurrentDateWithoutTimeStamp() {
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day);
		return new Date(currentDateCalendar.getTimeInMillis());
	}
	
	public static Date getDateWithoutTimeStamp(long timeInMills) {
		Calendar dateCalendar = new GregorianCalendar();
		dateCalendar.setTimeInMillis(timeInMills);
		int year = dateCalendar.get(Calendar.YEAR);
		int month = dateCalendar.get(Calendar.MONTH);
		int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
		dateCalendar = new GregorianCalendar(year, month, day);
		return new Date(dateCalendar.getTimeInMillis());
	}
	
	public static Calendar getCalendarDate(long timeInMills) {
		Calendar dateCalendar = new GregorianCalendar();
		dateCalendar.setTimeInMillis(timeInMills);
		return dateCalendar;
	}
	public static Date getLastDayOfCurrentYear(){
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH,Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		Calendar cal1 = Calendar.getInstance();
		cal1.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
		return new Date(cal1.getTimeInMillis());
	}
	
	public static Calendar getFistDayOfNextYear(Calendar cal){
		cal.roll(Calendar.YEAR,1);
		cal.set(Calendar.MONTH,Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
		Calendar cal1 = Calendar.getInstance();
		cal1.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
		return cal1;
	}
	
	public static Date getLastDayOfNextYear(){
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.MONTH,Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		Calendar cal1 = Calendar.getInstance();
		cal1.set(cal.get(Calendar.YEAR)+1,cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
		return cal1.getTime();
	}
	
	public static Calendar getLastDayOfYearAfterNextYear(){
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.MONTH,Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		Calendar cal1 = Calendar.getInstance();
		cal1.set(cal.get(Calendar.YEAR)+2,cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
		return cal1;
	}
	
	public static Date getCurrentDateOfNextYearWithOutTimeStamp(){
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year+1, month, day);
		return new Date(currentDateCalendar.getTimeInMillis());
	}
	
	public static Calendar getFistDayOfYearAfterNextYear(){
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.MONTH,Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
		Calendar cal1 = Calendar.getInstance();
		cal1.set(cal.get(Calendar.YEAR)+2,cal.get(Calendar.MONTH),cal.get(Calendar.DATE),0,0,0);
		return cal1;
	}
	
	/**
	 * Not sure why this is just copying over the year, month, and day.
	 * Also see {@link #getCalendarDate(long)}.
	 */
	public static Calendar getCalendar(Date date) {
		Calendar dateCalendar = new GregorianCalendar();
		dateCalendar.setTimeInMillis(date.getTime());
		int year = dateCalendar.get(Calendar.YEAR);
		int month = dateCalendar.get(Calendar.MONTH);
		int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
		dateCalendar = new GregorianCalendar(year, month, day);
		return dateCalendar;
	}
}
