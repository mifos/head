/**

 * MeetingBOTest.java    version: 1.0

 

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
package org.mifos.application.meeting.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class MeetingBOTest extends MifosTestCase{
	private Short recurAfter;
	private MeetingBO meeting;
	private Date startDate;
	private Date endDate;
	private Short dayNumber;	
	private int occurrences;

	public void testFailureGetAllDates_EndDateIsNull()throws Exception{
		try{
			recurAfter = Short.valueOf("10");
			startDate = getDate("18/08/2005");
			endDate = getDate("02/10/2005");
			meeting = createDailyMeeting(recurAfter, startDate);
			List list = meeting.getAllDates(null);
			assertNull(list);
			fail();
		}catch (MeetingException e){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_ENDDATE,e.getKey());
		}
	}
	public void testGetAllDatesWeeklyWithRepaymentIndepOfMeetingEnabled()throws Exception{
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		endDate=getDate("01/03/2006");
		recurAfter = Short.valueOf("1");
		meeting = createWeeklyMeeting(WeekDay.THURSDAY, recurAfter, startDate);
		list = meeting.getAllDatesWithRepaymentIndepOfMeetingEnabled(1);
		expectedList = createExpectedList("17/11/2005");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testGetAllDatesMonthlyWithRepaymentIndepOfMeetingEnabled()throws Exception{
		//dates that lies on second monday of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		endDate=getDate("15/10/2006");
		recurAfter = Short.valueOf("1");
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, recurAfter, startDate);
		list = meeting.getAllDatesWithRepaymentIndepOfMeetingEnabled(2);
		expectedList = createExpectedList("12/12/2005,09/01/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	public void testFailureGetAllDates_EndDateBeforeStartDate()throws Exception{
		try{
			recurAfter = Short.valueOf("10");
			startDate = getDate("18/08/2006");
			endDate = getDate("17/08/2006");
			meeting = createDailyMeeting(recurAfter, startDate);
			List list = meeting.getAllDates(endDate);
			assertNull(list);
			fail();
		}catch (MeetingException e){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_ENDDATE,e.getKey());
		}
	}
	
	public void testSuccessfulGetAllDates_EndDateEqualStartDate()throws Exception{
		recurAfter = Short.valueOf("1");
		startDate = getDate("18/08/2006");
		endDate = getDate("18/08/2006");
		meeting = createDailyMeeting(recurAfter, startDate);
		List list = meeting.getAllDates(endDate);
		assertNotNull(list);
		assertEquals(0, list.size());
	}

	public void testSuccessfulGetAllDates_Day()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfter = Short.valueOf("10");
		startDate = getDate("18/08/2005");
		endDate = getDate("02/10/2005");
		meeting = createDailyMeeting(recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		assertNotNull(list);
		expectedList = createExpectedList("28/08/2005,07/09/2005,17/09/2005,27/09/2005");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_30Day()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfter = Short.valueOf("1");
		startDate = getDate("28/09/2006");
		endDate = getDate("02/10/2006");
		meeting = createDailyMeeting(recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		assertNotNull(list);
		expectedList = createExpectedList("29/09/2006,30/09/2006,01/10/2006,02/10/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_31Day()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfter = Short.valueOf("1");
		startDate = getDate("28/08/2006");
		endDate = getDate("02/09/2006");
		meeting = createDailyMeeting(recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		assertNotNull(list);
		expectedList = createExpectedList("29/08/2006,30/08/2006,31/08/2006,01/09/2006,02/09/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_28Day()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfter = Short.valueOf("1");
		startDate = getDate("25/02/2006");
		endDate = getDate("02/03/2006");
		meeting = createDailyMeeting(recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		assertNotNull(list);
		expectedList = createExpectedList("26/02/2006,27/02/2006,28/02/2006,01/03/2006,02/03/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_29Day()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfter = Short.valueOf("1");
		startDate = getDate("26/02/2004");
		endDate = getDate("02/03/2004");
		meeting = createDailyMeeting(recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		assertNotNull(list);
		expectedList = createExpectedList("27/02/2004,28/02/2004,29/02/2004,01/03/2004,02/03/2004");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testGelAllDates_EveryWeek()throws Exception{
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		endDate=getDate("01/03/2006");
		recurAfter = Short.valueOf("1");
		meeting = createWeeklyMeeting(WeekDay.THURSDAY, recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("17/11/2005,24/11/2005,1/12/2005,08/12/2005,15/12/2005,22/12/2005,29/12/2005,05/01/2006,12/01/2006,19/01/2006,26/01/2006,02/02/2006,09/02/2006,16/02/2006,23/02/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testGelAllDates_EveryThreeWeek()throws Exception{
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		endDate=getDate("01/03/2006");
		recurAfter = Short.valueOf("3");
		meeting = createWeeklyMeeting(WeekDay.FRIDAY, recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("18/11/2005,09/12/2005,30/12/2005,20/01/2006,10/02/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testGelAllDates_EverySixWeek()throws Exception{
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		endDate=getDate("01/03/2006");
		recurAfter = Short.valueOf("6");
		meeting = createWeeklyMeeting(WeekDay.TUESDAY, recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("15/11/2005,27/12/2005,07/02/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testGelAllDates_MonthlyOnWeekDay()throws Exception{
		//dates that lies on second monday of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		endDate=getDate("15/10/2006");
		recurAfter = Short.valueOf("1");
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("12/12/2005,09/01/2006,13/02/2006,13/03/2006,10/04/2006,08/05/2006,12/06/2006,10/07/2006,14/08/2006,11/09/2006,09/10/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_2MonthlyOnWeekDay()throws Exception{
		//dates that lies on last friday of every  two months
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		endDate=getDate("15/10/2006");
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankType.LAST, recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("25/11/2005,27/01/2006,31/03/2006,26/05/2006,28/07/2006,29/09/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_MonthlyOnDate()throws Exception{
		//5th day of every three months
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		endDate=getDate("15/10/2006");
		recurAfter = Short.valueOf("3");
		dayNumber = Short.valueOf("5");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("05/12/2005,05/03/2006,05/06/2006,05/09/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_30MonthlyOnDate()throws Exception{
		//30th day of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		endDate=getDate("15/04/2006");
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("30");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("30/11/2005,30/12/2005,30/01/2006,28/02/2006,30/03/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_31MonthlyOnDate()throws Exception{
		//30th day of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		endDate=getDate("15/05/2006");
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("31");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("30/11/2005,31/12/2005,31/01/2006,28/02/2006,31/03/2006,30/04/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_28MonthlyOnDate()throws Exception{
		//28th day of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2003");
		endDate=getDate("15/04/2004");
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("28");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("28/11/2003,28/12/2003,28/01/2004,28/02/2004,28/03/2004");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_29MonthlyOnDate()throws Exception{
		//29th day of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2003");
		endDate=getDate("29/03/2004");
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("29");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("29/11/2003,29/12/2003,29/01/2004,29/02/2004,29/03/2004");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testFailureGetAllDates_ZeroOccurences()throws Exception{
		try{
			recurAfter = Short.valueOf("10");
			startDate = getDate("18/08/2006");
			occurrences = 0;
			meeting = createDailyMeeting(recurAfter, startDate);
			meeting.getAllDates(occurrences);
			fail();
		}catch (MeetingException e){
			assertTrue(true);
		}
	}
	
	public void testSuccessfulGetAllDates_Day_3Occurences()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfter = Short.valueOf("10");
		startDate = getDate("18/08/2005");
		occurrences = 3;
		meeting = createDailyMeeting(recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		assertNotNull(list);
		expectedList = createExpectedList("28/08/2005,07/09/2005,17/09/2005");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_30Day_4Occurences()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfter = Short.valueOf("1");
		startDate = getDate("28/09/2006");
		occurrences = 4;
		meeting = createDailyMeeting(recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		assertNotNull(list);
		expectedList = createExpectedList("29/09/2006,30/09/2006,01/10/2006,02/10/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_5Occurences()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfter = Short.valueOf("1");
		startDate = getDate("28/08/2006");
		occurrences = 5;
		meeting = createDailyMeeting(recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		assertNotNull(list);
		expectedList = createExpectedList("29/08/2006,30/08/2006,31/08/2006,01/09/2006,02/09/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_28Day_5Occurences()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfter = Short.valueOf("1");
		startDate = getDate("25/02/2006");
		occurrences = 5;
		meeting = createDailyMeeting(recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		assertNotNull(list);
		expectedList = createExpectedList("26/02/2006,27/02/2006,28/02/2006,01/03/2006,02/03/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_29Day_6Occurences()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfter = Short.valueOf("1");
		startDate = getDate("26/02/2004");
		occurrences = 6;
		meeting = createDailyMeeting(recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		assertNotNull(list);
		expectedList = createExpectedList("27/02/2004,28/02/2004,29/02/2004,01/03/2004,02/03/2004,03/03/2004");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testGelAllDates_EveryWeek_15Occurences()throws Exception{
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		occurrences = 15;
		recurAfter = Short.valueOf("1");
		meeting = createWeeklyMeeting(WeekDay.THURSDAY, recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("17/11/2005,24/11/2005,1/12/2005,08/12/2005,15/12/2005,22/12/2005,29/12/2005,05/01/2006,12/01/2006,19/01/2006,26/01/2006,02/02/2006,09/02/2006,16/02/2006,23/02/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testGelAllDates_EveryThreeWeek_5Occurences()throws Exception{
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		occurrences = 5;
		recurAfter = Short.valueOf("3");
		meeting = createWeeklyMeeting(WeekDay.FRIDAY, recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("18/11/2005,09/12/2005,30/12/2005,20/01/2006,10/02/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testGelAllDates_EverySixWeek_3Occurences()throws Exception{
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		occurrences = 3;
		recurAfter = Short.valueOf("6");
		meeting = createWeeklyMeeting(WeekDay.TUESDAY, recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("15/11/2005,27/12/2005,07/02/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testGelAllDates_MonthlyOnWeekDay_11Occurences()throws Exception{
		//dates that lies on second monday of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		occurrences = 11;
		recurAfter = Short.valueOf("1");
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("12/12/2005,09/01/2006,13/02/2006,13/03/2006,10/04/2006,08/05/2006,12/06/2006,10/07/2006,14/08/2006,11/09/2006,09/10/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_2MonthlyOnWeekDay_6Occurences()throws Exception{
		//dates that lies on last friday of every  two months
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		occurrences = 6;
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankType.LAST, recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("25/11/2005,27/01/2006,31/03/2006,26/05/2006,28/07/2006,29/09/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_MonthlyOnDate_4Occurences()throws Exception{
		//5th day of every three months
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		occurrences = 4;
		recurAfter = Short.valueOf("3");
		dayNumber = Short.valueOf("5");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("05/12/2005,05/03/2006,05/06/2006,05/09/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_30MonthlyOnDate_5Occurences()throws Exception{
		//30th day of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		occurrences = 5;
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("30");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("30/11/2005,30/12/2005,30/01/2006,28/02/2006,30/03/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_31MonthlyOnDate_6Occurences()throws Exception{
		//30th day of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2005");
		occurrences = 6;
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("31");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("30/11/2005,31/12/2005,31/01/2006,28/02/2006,31/03/2006,30/04/2006");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_28MonthlyOnDate_5Occurences()throws Exception{
		//28th day of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2003");
		occurrences = 5;
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("28");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("28/11/2003,28/12/2003,28/01/2004,28/02/2004,28/03/2004");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testGelAllDates_29MonthlyOnDate_5Occurences()throws Exception{
		//29th day of every month
		List list=null;
		List expectedList=null;
		startDate=getDate("15/11/2003");
		occurrences = 5;
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("29");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("29/11/2003,29/12/2003,29/01/2004,29/02/2004,29/03/2004");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testFailureIsValidScheduleDate_MeetingDateNull()throws Exception{
		startDate=getDate("15/11/2003");
		occurrences = 5;
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("29");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		try{
			meeting.isValidMeetingDate(null,occurrences);
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_MEETINGDATE,me.getKey());
		}
	}
	
	public void testFailureIsValidScheduleDate_OccurencesNull()throws Exception{
		startDate=getDate("15/11/2003");
		occurrences = 5;
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("29");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		try{
			meeting.isValidMeetingDate(getDate("29/02/2004"),0);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_OCCURENCES,me.getKey());
		}
	}
	
	//	ExpectedList("29/11/2003,29/12/2003,29/01/2004,29/02/2004,29/03/2004");
	public void testIsValidScheduleDate_MonthlyOnDate_5Occurences()throws Exception{
		startDate=getDate("15/11/2003");
		occurrences = 5;
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("29");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);		
		assertTrue(meeting.isValidMeetingDate(getDate("29/02/2004"),occurrences));
		assertFalse(meeting.isValidMeetingDate(getDate("28/02/2004"),occurrences));		
	}
	
	//ExpectedList("25/11/2005,27/01/2006,31/03/2006,26/05/2006,28/07/2006,29/09/2006");
	public void testIsValidScheduleDate_MonthlyOnWeekDay_6Occurences()throws Exception{
		//dates that lies on last friday of every  two months
		startDate=getDate("15/11/2005");
		occurrences = 6;
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankType.LAST, recurAfter, startDate);
		assertTrue(meeting.isValidMeetingDate(getDate("31/03/2006"),occurrences));
		assertFalse(meeting.isValidMeetingDate(getDate("27/10/2006"),occurrences));	
	}
	
	//ExpectedList("15/11/2005,27/12/2005,07/02/2006");
	public void testIsValidScheduleDate_EverySixWeek_3Occurences()throws Exception{
		startDate=getDate("15/11/2005");
		occurrences = 3;
		recurAfter = Short.valueOf("6");
		meeting = createWeeklyMeeting(WeekDay.TUESDAY, recurAfter, startDate);
		
		assertTrue(meeting.isValidMeetingDate(getDate("27/12/2005"),occurrences));
		assertFalse(meeting.isValidMeetingDate(getDate("26/12/2006"),occurrences));	
	}
	
	//ExpectedList("28/08/2005,07/09/2005,17/09/2005");
	public void testIsValidScheduleDate_Day_3Occurences()throws Exception{
		recurAfter = Short.valueOf("10");
		startDate = getDate("18/08/2005");
		occurrences = 3;
		meeting = createDailyMeeting(recurAfter, startDate);
		assertTrue(meeting.isValidMeetingDate(getDate("07/09/2005"),occurrences));
		assertFalse(meeting.isValidMeetingDate(getDate("27/09/2006"),occurrences));		
	}
	
	public void testFailureIsValidScheduleDate_WhenMeetingDateNull()throws Exception{
		startDate=getDate("15/11/2003");
		endDate=getDate("15/10/2006");
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("29");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		try{
			meeting.isValidMeetingDate(null,endDate);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_MEETINGDATE,me.getKey());
		}
	}
	
	public void testFailureIsValidScheduleDate_EndDateNull()throws Exception{
		startDate=getDate("15/11/2003");
		endDate=getDate("15/10/2006");
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("29");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		try{
			meeting.isValidMeetingDate(getDate("29/02/2004"),null);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_ENDDATE,me.getKey());
		}
	}
	
	//ExpectedList("05/12/2005,05/03/2006,05/06/2006,05/09/2006");	
	public void testIsValidScheduleDate_MonthlyOnDate()throws Exception{
		//5th day of every three months
		startDate=getDate("15/11/2005");
		endDate=getDate("15/10/2006");
		recurAfter = Short.valueOf("3");
		dayNumber = Short.valueOf("5");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);

		assertTrue(meeting.isValidMeetingDate(getDate("05/09/2006"),endDate));
		assertFalse(meeting.isValidMeetingDate(getDate("05/12/2006"),endDate));
	}
	
	//ExpectedList("29/08/2006,30/08/2006,31/08/2006,01/09/2006,02/09/2006");	
	public void testIsValidScheduleDate_31Day()throws Exception{
		recurAfter = Short.valueOf("1");
		startDate = getDate("28/08/2006");
		endDate = getDate("02/09/2006");
		meeting = createDailyMeeting(recurAfter, startDate);
		assertTrue(meeting.isValidMeetingDate(getDate("29/08/2006"),endDate));
		assertFalse(meeting.isValidMeetingDate(getDate("01/19/2006"),endDate));
	}
	
	//ExpectedList("12/12/2005,09/01/2006,13/02/2006,13/03/2006,10/04/2006,08/05/2006,12/06/2006,10/07/2006,14/08/2006,11/09/2006,09/10/2006");
	public void testIsValidScheduleDate_MonthlyOnWeekDay()throws Exception{
		//dates that lies on second monday of every month
		startDate=getDate("15/11/2005");
		endDate=getDate("15/10/2006");
		recurAfter = Short.valueOf("1");
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, recurAfter, startDate);
		assertTrue(meeting.isValidMeetingDate(getDate("13/03/2006"),endDate));
		assertFalse(meeting.isValidMeetingDate(getDate("15/10/2006"),endDate));
	}
	
	//ExpectedList("18/11/2005,09/12/2005,30/12/2005,20/01/2006,10/02/2006");
	public void testIsValidScheduleDate_EveryThreeWeek()throws Exception{
		startDate=getDate("15/11/2005");
		endDate=getDate("01/03/2006");
		recurAfter = Short.valueOf("3");
		meeting = createWeeklyMeeting(WeekDay.FRIDAY, recurAfter, startDate);		
		assertTrue(meeting.isValidMeetingDate(getDate("18/11/2005"),endDate));
		assertFalse(meeting.isValidMeetingDate(getDate("03/03/2006"),endDate));
	}
	
		
	public void testGetFailureNextScheduleDate()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = Short.valueOf("1");
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		try{
			meeting.getNextScheduleDateAfterRecurrence(null);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_MEETINGDATE,me.getKey());
		}
	}
	
	public void testGetNextScheduleDateAfterRecurrenceOnStartofMonth()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = Short.valueOf("1");
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		Date resultDate = meeting.getNextScheduleDateAfterRecurrence(getDate("01/01/2006"));
		assertEquals(getDate("01/03/2006"),resultDate);
	}
	
	public void testGetNextScheduleDateAfterRecurrenceOnEndofMonth()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = Short.valueOf("31");
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		Date resultDate = meeting.getNextScheduleDateAfterRecurrence(getDate("01/01/2006"));
		assertEquals(getDate("31/03/2006"),resultDate);
	}
	
	/** What case is this intended to test?  Right now it is just
        the same as {@link #testGetNextScheduleDateAfterRecurrenceOnEndofMonth}
    */
	public void xtestGetFailurePrevScheduleDate()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = Short.valueOf("31");
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		try{
			meeting.getNextScheduleDateAfterRecurrence(getDate("01/01/2006"));
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_MEETINGDATE,me.getKey());
		}
	}
	
	public void testGetPrevScheduleDateAfterRecurrenceOnStartofMonth()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = Short.valueOf("1");
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		Date resultDate = meeting.getPrevScheduleDateAfterRecurrence(getDate("01/05/2006"));
		assertEquals(getDate("01/03/2006"),resultDate);
	}
	
	public void testGetPrevScheduleDateAfterRecurrenceOnEndofMonth()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = Short.valueOf("31");
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, startDate);
		Date resultDate = meeting.getPrevScheduleDateAfterRecurrence(getDate("31/05/2006"));
		assertEquals(getDate("31/03/2006"),resultDate);
	}
	
	
	public void testFailureCreateDailyMeeting_recurAfterIsNull(){
		try{
			meeting = createDailyMeeting(null,new Date());
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_RECURAFTER,me.getKey());
		}
	}
	
	public void testFailureCreateDailyMeeting_recurAfterInvalid(){
		try{
			recurAfter = (short)-1;
			meeting = createDailyMeeting(recurAfter,new Date());
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
		}
	}
	
	public void testFailureCreateDailyMeeting_startDateIsNull(){
		try{
			recurAfter = (short)1;
			meeting = createDailyMeeting(recurAfter,null);
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_STARTDATE,me.getKey());
		}
	}
	
	public void testSuccessfulCreateDailyMeeting() throws MeetingException{
		recurAfter = Short.valueOf("1");
		meeting = createDailyMeeting(recurAfter,new Date());
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = (MeetingBO)HibernateUtil.getSessionTL().get(MeetingBO.class,meeting.getMeetingId());
		assertNotNull(meeting);
		assertEquals(recurAfter,meeting.getMeetingDetails().getRecurAfter());
		assertEquals(RecurrenceType.DAILY, 
			meeting.getMeetingDetails().getRecurrenceTypeEnum());
	}
	
	public void testFailureCreateWeeklyMeeting_weekDayIsNull(){
		try{
			recurAfter = Short.valueOf("1");
			meeting = createWeeklyMeeting(null, recurAfter, new Date());
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_WEEKDAY, me.getKey());
		}
	}
	
	public void testFailureCreateWeeklyMeeting_recurAfterIsNull(){
		try{
			meeting = createWeeklyMeeting(WeekDay.MONDAY, null, new Date());
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
		}
	}
	
	public void testFailureCreateWeeklyMeeting_startDateIsNull(){
		try{
			recurAfter = Short.valueOf("1");
			meeting = createWeeklyMeeting(WeekDay.MONDAY, recurAfter, null);
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_STARTDATE,me.getKey());
		}
	}
	
	public void testFailureCreateWeeklyMeeting_meetingPlaceIsNull(){
		try{
			recurAfter = Short.valueOf("1");
			meeting = new MeetingBO(WeekDay.MONDAY, recurAfter, new Date(), MeetingType.CUSTOMER_MEETING, "");
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_MEETINGPLACE,me.getKey());
		}
	}
	
	public void testSuccessfulCreateWeeklyMeeting() throws MeetingException{
		recurAfter = (short)2;
		meeting = createWeeklyMeeting(WeekDay.MONDAY,recurAfter, new Date());
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = (MeetingBO)HibernateUtil.getSessionTL().get(MeetingBO.class,meeting.getMeetingId());
		assertNotNull(meeting);
		assertEquals(recurAfter,meeting.getMeetingDetails().getRecurAfter());
		assertTrue(meeting.isWeekly());
		assertEquals(WeekDay.MONDAY, meeting.getMeetingDetails().getWeekDay());
	}
	
	public void testFailureCreateMonthlyMeetingOnDate_dayNumberIsNull(){
		try{
			recurAfter = Short.valueOf("1");
			meeting = createMonthlyMeetingOnDate(null, recurAfter, new Date());
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_DAYNUMBER_OR_WEEK, me.getKey());
		}
	}
	
	public void testFailureCreateMonthlyMeetingOnDate_dayNumberIsNInvalid(){
		try{
			recurAfter = Short.valueOf("1");
			dayNumber = Short.valueOf("32");
			meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, new Date());
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_DAYNUMBER, me.getKey());
		}
	}
	
	public void testFailureCreateMonthlyMeetingOnDate_recurAfterIsNull(){
		try{
			dayNumber = Short.valueOf("5");
			meeting = createMonthlyMeetingOnDate(dayNumber, null, new Date());
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
		}
	}
	
	public void testFailureCreateMonthlyMeetingOnDate_startDateIsNull(){
		try{
			recurAfter = Short.valueOf("1");
			dayNumber = Short.valueOf("5");
			meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, null);
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_STARTDATE, me.getKey());
		}
	}
	
	public void testSuccessfulCreateMonthlyMeetingOnDate() throws MeetingException{
		recurAfter = Short.valueOf("1");
		dayNumber = Short.valueOf("5");
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfter, new Date());
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = (MeetingBO)HibernateUtil.getSessionTL().get(MeetingBO.class,meeting.getMeetingId());
		assertNotNull(meeting);
		assertEquals(recurAfter,meeting.getMeetingDetails().getRecurAfter());
		assertTrue(meeting.isMonthlyOnDate());
		assertEquals(dayNumber, meeting.getMeetingDetails().getDayNumber());
	}
	
	public void testFailureCreateMonthlyMeetingOnWeekDay_weekDayIsNull(){
		try{
			recurAfter = Short.valueOf("1");
			meeting = createMonthlyMeetingOnWeekDay(null,RankType.FIRST,recurAfter, new Date());
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK, me.getKey());
		}
	}
	
	public void testFailureCreateMonthlyMeetingOnWeekDay_weekRankIsNull(){
		try{
			recurAfter = Short.valueOf("1");
			meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, null, recurAfter, new Date());
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK, me.getKey());
		}
	}
	
	public void testFailureCreateMonthlyMeetingOnWeekDay_recurAfterIsNull(){
		try{
			recurAfter = Short.valueOf("1");
			meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, null, new Date());
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
		}
	}
	
	public void testFailureCreateMonthlyMeetingOnWeekDay_startDateIsNull(){
		try{
			recurAfter = Short.valueOf("1");
			dayNumber = Short.valueOf("5");
			meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY,RankType.FIRST,recurAfter, null);
			assertNull(meeting);
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_STARTDATE, me.getKey());
		}
	}
	
	public void testSuccessfulCreateMonthlyMeetingOnWeekDay() throws MeetingException{
		recurAfter = Short.valueOf("1");
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY,RankType.FIRST,recurAfter, new Date());
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = (MeetingBO)HibernateUtil.getSessionTL().get(MeetingBO.class,meeting.getMeetingId());
		assertNotNull(meeting);
		assertEquals(recurAfter,meeting.getMeetingDetails().getRecurAfter());
		assertTrue(meeting.isMonthly());
		assertFalse(meeting.isMonthlyOnDate());
		assertEquals(WeekDay.MONDAY, meeting.getMeetingDetails().getWeekDay());
		assertEquals(RankType.FIRST, meeting.getMeetingDetails().getWeekRank());
	}
	
	public void testFailureUpdateWeeklyMeeting_MeetingPlace_Null() throws MeetingException{
		recurAfter = Short.valueOf("5");
		meeting = createWeeklyMeeting(WeekDay.MONDAY, recurAfter, new Date());
		try{
			meeting.update(WeekDay.WEDNESDAY,"");
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_MEETINGPLACE, me.getKey());
		}
	}
	
	public void testSuccessfulUpdateWeeklyMeeting() throws MeetingException{
		recurAfter = Short.valueOf("5");
		meeting = createWeeklyMeeting(WeekDay.MONDAY, recurAfter, new Date());
		meeting.update(WeekDay.WEDNESDAY,"Delhi");
		assertEquals("Delhi", meeting.getMeetingPlace());
		assertEquals(recurAfter, meeting.getMeetingDetails().getRecurAfter());
		assertEquals(WeekDay.WEDNESDAY, meeting.getMeetingDetails().getWeekDay());
	}
	
	public void testFailureUpdateMontlyMeetingInValidDayNumber() throws MeetingException{
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnDate(Short.valueOf("5"), recurAfter, new Date());
		try{
			meeting.update(Short.valueOf("32"),"Delhi");
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_DAYNUMBER, me.getKey());
		}
	}
	
	public void testFailureUpdateMontlyMeeting_MeetingPlace_Null() throws MeetingException{
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnDate(Short.valueOf("5"), recurAfter, new Date());
		try{
			meeting.update(Short.valueOf("6"),"");
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_MEETINGPLACE, me.getKey());
		}
	}
	
	public void testSuccessfulUpdateMontlyMeetingOnDate() throws MeetingException{
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnDate(Short.valueOf("5"), recurAfter, new Date());		
		meeting.update(Short.valueOf("10"), "Delhi");
		assertEquals("Delhi", meeting.getMeetingPlace());
		assertEquals(recurAfter, meeting.getMeetingDetails().getRecurAfter());
		assertEquals(Short.valueOf("10"), meeting.getMeetingDetails().getDayNumber());
	}
	
	public void testFailureUpdateMontlyMeetingOnWeekDay() throws MeetingException{
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, recurAfter, new Date());
		try{
			meeting.update(null, null, "Delhi");
			fail();
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK, me.getKey());
		}
	}
	
	public void testSuccessfulUpdateMontlyMeetingOnWeekDay() throws MeetingException{
		recurAfter = Short.valueOf("2");
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, recurAfter, new Date());
		meeting.update(WeekDay.THURSDAY, RankType.THIRD, "Delhi");
		assertEquals("Delhi", meeting.getMeetingPlace());
		assertEquals(recurAfter, meeting.getMeetingDetails().getRecurAfter());
		assertEquals(WeekDay.THURSDAY, meeting.getMeetingDetails().getWeekDay());
		assertEquals(RankType.THIRD, meeting.getMeetingDetails().getWeekRank());
	}
	
	private MeetingBO createDailyMeeting(Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(RecurrenceType.DAILY, recurAfer, startDate, MeetingType.CUSTOMER_MEETING);
	}
	
	private MeetingBO createWeeklyMeeting(WeekDay weekDay, Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(weekDay, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
	}
	
	private MeetingBO createMonthlyMeetingOnDate(Short dayNumber, Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(dayNumber, recurAfer, startDate, MeetingType.CUSTOMER_MEETING,"MeetingPlace");	
	}
	
	private MeetingBO createMonthlyMeetingOnWeekDay(WeekDay weekDay, RankType rank, Short recurAfer, Date startDate) throws MeetingException{
		return new MeetingBO(weekDay, rank, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
	}
	
	private List createExpectedList(String dates)throws Exception{
		List expectedList=new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(dates,",");
		while(tokenizer.hasMoreTokens()){
			String date = tokenizer.nextToken();
			expectedList.add(getDate(date));	
		}
		return expectedList;
	}
	
	void matchDateLists(List<Date> expectedList, List<Date> list){
		for(int i=0; i<expectedList.size(); i++)
			assertEquals("Dates are invalid", expectedList.get(i), list.get(i));
	}		
}
