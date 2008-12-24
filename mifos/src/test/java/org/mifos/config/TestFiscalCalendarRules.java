package org.mifos.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.FilePaths;

public class TestFiscalCalendarRules {
	
	private static ConfigurationManager configMgr = null;
	private static String savedConfigWorkingDays = null;
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestFiscalCalendarRules.class);
	}
	
	
	
	@BeforeClass
	public static void init() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
		configMgr = ConfigurationManager.getInstance();
		savedConfigWorkingDays = configMgr.getProperty(FiscalCalendarRules.FiscalCalendarRulesWorkingDays).toString();
		savedConfigWorkingDays = savedConfigWorkingDays.replace("[", "");
		savedConfigWorkingDays = savedConfigWorkingDays.replace("]", "");
		
	}
	
	private void setSavedConfig()
	{
		configMgr.setProperty(FiscalCalendarRules.FiscalCalendarRulesWorkingDays, savedConfigWorkingDays);
		FiscalCalendarRules.reloadConfigWorkingDays();
	}
	
	private void setNewWorkingDays(String newWorkingDays)
	{
		configMgr.setProperty(FiscalCalendarRules.FiscalCalendarRulesWorkingDays, newWorkingDays);
		FiscalCalendarRules.reloadConfigWorkingDays();
	}
	
	@Test 
	public void testGetWorkingDays() {
		
		String configWorkingDays = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY";
		setNewWorkingDays(configWorkingDays);
		List<WeekDay> workingDays = FiscalCalendarRules.getWorkingDays();
		assertEquals(workingDays.size(), 6);
		WeekDay[] weekDays = WeekDay.values();
		for (int i=0; i < workingDays.size(); i++)
			assertEquals(workingDays.get(i).toString(), weekDays[i+1].name());
		configWorkingDays = "TUESDAY,WEDNESDAY,THURSDAY,FRIDAY";
		setNewWorkingDays(configWorkingDays);
		workingDays = FiscalCalendarRules.getWorkingDays();
		assertEquals(workingDays.size(), 4);
		for (int i=0; i < workingDays.size(); i++)
			assertEquals(workingDays.get(i).toString().toUpperCase(), weekDays[i+2].name().toUpperCase());
		//	set it back
		setSavedConfig();
		
	}
	
	@Test 
	public void testGetWeekDaysList() {
		List<WeekDay> weekDaysFromFiscalCalendarRules = FiscalCalendarRules.getWeekDaysList();
		WeekDay[] weekDays = WeekDay.values();
		for (int i=0; i < weekDays.length; i++)
			assertEquals(weekDaysFromFiscalCalendarRules.get(i).toString(), weekDays[i].name());
	}
	
	@Test 
	public void testGetWeekDayOffList() {
		String configWorkingDays = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY";
		setNewWorkingDays(configWorkingDays);
		List<Short> list = FiscalCalendarRules.getWeekDayOffList();
		assertEquals(list.size(), 2);
		Short dayOff = 1;
		assertEquals(list.get(0), dayOff);
		//	set it back
		setSavedConfig();
	}
	
	@Test
	public void testIsWorkingDay() {
			String configWorkingDays = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY";
			setNewWorkingDays(configWorkingDays);
			//			 get the supported ids for GMT-08:00 (Pacific Standard Time)
			 String[] ids = TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000);
			 // if no ids were returned, something is wrong. get out.
			 // Otherwise get an id for Pacific Standard Time
			 String pstId = ids[0];

			 // create a Pacific Standard Time time zone
			 SimpleTimeZone pdt = new SimpleTimeZone(-8 * 60 * 60 * 1000, pstId);

			 // set up rules for daylight savings time
			 pdt.setStartRule(Calendar.APRIL, 1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);
			 pdt.setEndRule(Calendar.OCTOBER, -1, Calendar.SUNDAY, 2 * 60 * 60 * 1000);

			 // create a GregorianCalendar with the Pacific Daylight time zone
			 // and the current date and time
			 Calendar calendar = new GregorianCalendar(pdt);
			 try
			 {
				 Locale savedLocale = Localization.getInstance().getMainLocale();
				 LocalizationConverter.getInstance().setCurrentLocale(Locale.US);
				 SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
				 //Keith: DateFormat must be set to the same timezone as the calendar
				 //Otherwise dates don't roll over at the same exact time, causing
				 //this and several other unit tests to fail
				 df.setTimeZone(TimeZone.getTimeZone(pstId));
				 df.applyPattern("yyyy/MM/dd");
				 Date thursday = df.parse("2007/10/11");
				 calendar.setTime(thursday);
				 String out = thursday.toString();
				 out.contains("A");
				 LocalizationConverter.getInstance().setCurrentLocale(savedLocale);
			 }
			 catch (Exception e)
			 {
				 
			 }
			 
			assertTrue(FiscalCalendarRules.isWorkingDay(calendar));
			calendar.add(Calendar.DAY_OF_WEEK, 1); // Friday
			assertTrue(FiscalCalendarRules.isWorkingDay(calendar));
			calendar.add(Calendar.DAY_OF_WEEK, 1); // Sat
			assertTrue(!FiscalCalendarRules.isWorkingDay(calendar));
			calendar.add(Calendar.DAY_OF_WEEK, 1); // Sunday
			assertTrue(!FiscalCalendarRules.isWorkingDay(calendar));
			//	set it back
			setSavedConfig();
		}
	
	
	@Test 
	public void testGetStartOfWeek() {
		Short startOfWeekDay = FiscalCalendarRules.getStartOfWeek(); 
		Short start = 2;
		assertEquals(startOfWeekDay, start);
	}
	
	@Test 
	public void testGetScheduleTypeForMeetingOnHoliday() {
		String scheduleType = FiscalCalendarRules.getScheduleTypeForMeetingOnHoliday();
		assertEquals(scheduleType.toUpperCase(), "same_day".toUpperCase());
	}
	
	@Test 
	public void testGetDaysForCalDefinition() {
		Short days = FiscalCalendarRules.getDaysForCalendarDefinition();
		assertEquals(days.shortValue(), 30);
	}



}
