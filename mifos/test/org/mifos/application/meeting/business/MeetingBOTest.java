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
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.meeting.util.resources.MeetingConstants;
import org.mifos.framework.MifosTestCase;

public class MeetingBOTest extends MifosTestCase{
	private Short recurAfer;
	private MeetingBO meeting;
	private Date startDate;
	private Date endDate;
	private Short dayNumber;	
	private int occurrences;

	public void testFailureGetAllDates_EndDateIsNull()throws Exception{
		try{
			recurAfer = 10;
			startDate = getDate("18/08/2005");
			endDate = getDate("02/10/2005");
			meeting = createDailyMeeting(recurAfer, startDate);
			List list = meeting.getAllDates(null);
			assertNull(list);
		}catch (MeetingException e){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_ENDDATE,e.getKey());
		}
	}
	
	public void testFailureGetAllDates_EndDateBeforeStartDate()throws Exception{
		try{
			recurAfer = 10;
			startDate = getDate("18/08/2006");
			endDate = getDate("17/08/2006");
			meeting = createDailyMeeting(recurAfer, startDate);
			List list = meeting.getAllDates(endDate);
			assertNull(list);
		}catch (MeetingException e){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_ENDDATE,e.getKey());
		}
	}
	
	public void testSuccessfulGetAllDates_Day()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfer = 10;
		startDate = getDate("18/08/2005");
		endDate = getDate("02/10/2005");
		meeting = createDailyMeeting(recurAfer, startDate);
		list = meeting.getAllDates(endDate);
		assertNotNull(list);
		expectedList = createExpectedList("28/08/2005,07/09/2005,17/09/2005,27/09/2005");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_30Day()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfer = 1;
		startDate = getDate("28/09/2006");
		endDate = getDate("02/10/2006");
		meeting = createDailyMeeting(recurAfer, startDate);
		list = meeting.getAllDates(endDate);
		assertNotNull(list);
		expectedList = createExpectedList("29/09/2006,30/09/2006,01/10/2006,02/10/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_31Day()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfer = 1;
		startDate = getDate("28/08/2006");
		endDate = getDate("02/09/2006");
		meeting = createDailyMeeting(recurAfer, startDate);
		list = meeting.getAllDates(endDate);
		assertNotNull(list);
		expectedList = createExpectedList("29/08/2006,30/08/2006,31/08/2006,01/09/2006,02/09/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_28Day()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfer = 1;
		startDate = getDate("25/02/2006");
		endDate = getDate("02/03/2006");
		meeting = createDailyMeeting(recurAfer, startDate);
		list = meeting.getAllDates(endDate);
		assertNotNull(list);
		expectedList = createExpectedList("26/02/2006,27/02/2006,28/02/2006,01/03/2006,02/03/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_29Day()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfer = 1;
		startDate = getDate("26/02/2004");
		endDate = getDate("02/03/2004");
		meeting = createDailyMeeting(recurAfer, startDate);
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
		recurAfer = 1;
		meeting = createWeeklyMeeting(WeekDay.THURSDAY, recurAfer, startDate);
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
		recurAfer = 3;
		meeting = createWeeklyMeeting(WeekDay.FRIDAY, recurAfer, startDate);
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
		recurAfer = 6;
		meeting = createWeeklyMeeting(WeekDay.TUESDAY, recurAfer, startDate);
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
		recurAfer = 1;
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, recurAfer, startDate);
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
		recurAfer = 2;
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankType.LAST, recurAfer, startDate);
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
		recurAfer = 3;
		dayNumber = 5;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
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
		recurAfer = 1;
		dayNumber = 30;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
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
		recurAfer = 1;
		dayNumber = 31;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
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
		recurAfer = 1;
		dayNumber = 28;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
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
		recurAfer = 1;
		dayNumber = 29;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		list = meeting.getAllDates(endDate);
		expectedList = createExpectedList("29/11/2003,29/12/2003,29/01/2004,29/02/2004,29/03/2004");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testFailureGetAllDates_ZeroOccurences()throws Exception{
		try{
			recurAfer = 10;
			startDate = getDate("18/08/2006");
			occurrences = 0;
			meeting = createDailyMeeting(recurAfer, startDate);
			List list = meeting.getAllDates(occurrences);
			assertNull(list);
		}catch (MeetingException e){
			assertTrue(true);
		}
	}
	
	public void testSuccessfulGetAllDates_Day_3Occurences()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfer = 10;
		startDate = getDate("18/08/2005");
		occurrences = 3;
		meeting = createDailyMeeting(recurAfer, startDate);
		list = meeting.getAllDates(occurrences);
		assertNotNull(list);
		expectedList = createExpectedList("28/08/2005,07/09/2005,17/09/2005");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_30Day_4Occurences()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfer = 1;
		startDate = getDate("28/09/2006");
		occurrences = 4;
		meeting = createDailyMeeting(recurAfer, startDate);
		list = meeting.getAllDates(occurrences);
		assertNotNull(list);
		expectedList = createExpectedList("29/09/2006,30/09/2006,01/10/2006,02/10/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_5Occurences()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfer = 1;
		startDate = getDate("28/08/2006");
		occurrences = 5;
		meeting = createDailyMeeting(recurAfer, startDate);
		list = meeting.getAllDates(occurrences);
		assertNotNull(list);
		expectedList = createExpectedList("29/08/2006,30/08/2006,31/08/2006,01/09/2006,02/09/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_28Day_5Occurences()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfer = 1;
		startDate = getDate("25/02/2006");
		occurrences = 5;
		meeting = createDailyMeeting(recurAfer, startDate);
		list = meeting.getAllDates(occurrences);
		assertNotNull(list);
		expectedList = createExpectedList("26/02/2006,27/02/2006,28/02/2006,01/03/2006,02/03/2006");
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);		
	}
	
	public void testSuccessfulGetAllDates_29Day_6Occurences()throws Exception{
		List list=null;
		List expectedList=null;		
		recurAfer = 1;
		startDate = getDate("26/02/2004");
		occurrences = 6;
		meeting = createDailyMeeting(recurAfer, startDate);
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
		recurAfer = 1;
		meeting = createWeeklyMeeting(WeekDay.THURSDAY, recurAfer, startDate);
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
		recurAfer = 3;
		meeting = createWeeklyMeeting(WeekDay.FRIDAY, recurAfer, startDate);
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
		recurAfer = 6;
		meeting = createWeeklyMeeting(WeekDay.TUESDAY, recurAfer, startDate);
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
		recurAfer = 1;
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, recurAfer, startDate);
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
		recurAfer = 2;
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankType.LAST, recurAfer, startDate);
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
		recurAfer = 3;
		dayNumber = 5;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
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
		recurAfer = 1;
		dayNumber = 30;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
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
		recurAfer = 1;
		dayNumber = 31;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
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
		recurAfer = 1;
		dayNumber = 28;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
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
		recurAfer = 1;
		dayNumber = 29;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		list = meeting.getAllDates(occurrences);
		expectedList = createExpectedList("29/11/2003,29/12/2003,29/01/2004,29/02/2004,29/03/2004");
		assertNotNull(list);
		assertEquals(expectedList.size(),list.size());
		matchDateLists(expectedList,list);	
	}
	
	public void testFailureIsValidScheduleDate_MeetingDateNull()throws Exception{
		startDate=getDate("15/11/2003");
		occurrences = 5;
		recurAfer = 1;
		dayNumber = 29;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
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
		recurAfer = 1;
		dayNumber = 29;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		try{
			meeting.isValidMeetingDate(getDate("29/02/2004"),0);
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_OCCURENCES,me.getKey());
		}
	}
	
	//	ExpectedList("29/11/2003,29/12/2003,29/01/2004,29/02/2004,29/03/2004");
	public void testIsValidScheduleDate_MonthlyOnDate_5Occurences()throws Exception{
		startDate=getDate("15/11/2003");
		occurrences = 5;
		recurAfer = 1;
		dayNumber = 29;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);		
		assertTrue(meeting.isValidMeetingDate(getDate("29/02/2004"),occurrences));
		assertFalse(meeting.isValidMeetingDate(getDate("28/02/2004"),occurrences));		
	}
	
	//ExpectedList("25/11/2005,27/01/2006,31/03/2006,26/05/2006,28/07/2006,29/09/2006");
	public void testIsValidScheduleDate_MonthlyOnWeekDay_6Occurences()throws Exception{
		//dates that lies on last friday of every  two months
		startDate=getDate("15/11/2005");
		occurrences = 6;
		recurAfer = 2;
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankType.LAST, recurAfer, startDate);
		assertTrue(meeting.isValidMeetingDate(getDate("31/03/2006"),occurrences));
		assertFalse(meeting.isValidMeetingDate(getDate("27/10/2006"),occurrences));	
	}
	
	//ExpectedList("15/11/2005,27/12/2005,07/02/2006");
	public void testIsValidScheduleDate_EverySixWeek_3Occurences()throws Exception{
		startDate=getDate("15/11/2005");
		occurrences = 3;
		recurAfer = 6;
		meeting = createWeeklyMeeting(WeekDay.TUESDAY, recurAfer, startDate);
		
		assertTrue(meeting.isValidMeetingDate(getDate("27/12/2005"),occurrences));
		assertFalse(meeting.isValidMeetingDate(getDate("26/12/2006"),occurrences));	
	}
	
	//ExpectedList("28/08/2005,07/09/2005,17/09/2005");
	public void testIsValidScheduleDate_Day_3Occurences()throws Exception{
		recurAfer = 10;
		startDate = getDate("18/08/2005");
		occurrences = 3;
		meeting = createDailyMeeting(recurAfer, startDate);
		assertTrue(meeting.isValidMeetingDate(getDate("07/09/2005"),occurrences));
		assertFalse(meeting.isValidMeetingDate(getDate("27/09/2006"),occurrences));		
	}
	
	public void testFailureIsValidScheduleDate_WhenMeetingDateNull()throws Exception{
		startDate=getDate("15/11/2003");
		endDate=getDate("15/10/2006");
		recurAfer = 1;
		dayNumber = 29;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		try{
			meeting.isValidMeetingDate(null,endDate);
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_MEETINGDATE,me.getKey());
		}
	}
	
	public void testFailureIsValidScheduleDate_EndDateNull()throws Exception{
		startDate=getDate("15/11/2003");
		endDate=getDate("15/10/2006");
		recurAfer = 1;
		dayNumber = 29;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		try{
			meeting.isValidMeetingDate(getDate("29/02/2004"),null);
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
		recurAfer = 3;
		dayNumber = 5;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);

		assertTrue(meeting.isValidMeetingDate(getDate("05/09/2006"),endDate));
		assertFalse(meeting.isValidMeetingDate(getDate("05/12/2006"),endDate));
	}
	
	//ExpectedList("29/08/2006,30/08/2006,31/08/2006,01/09/2006,02/09/2006");	
	public void testIsValidScheduleDate_31Day()throws Exception{
		recurAfer = 1;
		startDate = getDate("28/08/2006");
		endDate = getDate("02/09/2006");
		meeting = createDailyMeeting(recurAfer, startDate);
		assertTrue(meeting.isValidMeetingDate(getDate("29/08/2006"),endDate));
		assertFalse(meeting.isValidMeetingDate(getDate("01/19/2006"),endDate));
	}
	
	//ExpectedList("12/12/2005,09/01/2006,13/02/2006,13/03/2006,10/04/2006,08/05/2006,12/06/2006,10/07/2006,14/08/2006,11/09/2006,09/10/2006");
	public void testIsValidScheduleDate_MonthlyOnWeekDay()throws Exception{
		//dates that lies on second monday of every month
		startDate=getDate("15/11/2005");
		endDate=getDate("15/10/2006");
		recurAfer = 1;
		meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, recurAfer, startDate);
		assertTrue(meeting.isValidMeetingDate(getDate("13/03/2006"),endDate));
		assertFalse(meeting.isValidMeetingDate(getDate("15/10/2006"),endDate));
	}
	
	//ExpectedList("18/11/2005,09/12/2005,30/12/2005,20/01/2006,10/02/2006");
	public void testIsValidScheduleDate_EveryThreeWeek()throws Exception{
		startDate=getDate("15/11/2005");
		endDate=getDate("01/03/2006");
		recurAfer = 3;
		meeting = createWeeklyMeeting(WeekDay.FRIDAY, recurAfer, startDate);		
		assertTrue(meeting.isValidMeetingDate(getDate("18/11/2005"),endDate));
		assertFalse(meeting.isValidMeetingDate(getDate("03/03/2006"),endDate));
	}
	
		
	public void testGetFailureNextScheduleDate()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = 1;
		recurAfer = 2;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		try{
			meeting.getNextScheduleDateAfterRecurrence(null);
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_MEETINGDATE,me.getKey());
		}
	}
	
	public void testGetNextScheduleDateAfterRecurrenceOnStartofMonth()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = 1;
		recurAfer = 2;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		Date resultDate = meeting.getNextScheduleDateAfterRecurrence(getDate("01/01/2006"));
		assertEquals(getDate("01/03/2006"),resultDate);
	}
	
	public void testGetNextScheduleDateAfterRecurrenceOnEndofMonth()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = 31;
		recurAfer = 2;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		Date resultDate = meeting.getNextScheduleDateAfterRecurrence(getDate("01/01/2006"));
		assertEquals(getDate("31/03/2006"),resultDate);
	}
	
	public void testGetFailurePrevScheduleDate()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = 31;
		recurAfer = 2;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		try{
			meeting.getNextScheduleDateAfterRecurrence(getDate("01/01/2006"));
		}catch(MeetingException me){
			assertTrue(true);
			assertEquals(MeetingConstants.INVALID_MEETINGDATE,me.getKey());
		}
	}
	
	public void testGetPrevScheduleDateAfterRecurrenceOnStartofMonth()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = 1;
		recurAfer = 2;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		Date resultDate = meeting.getPrevScheduleDateAfterRecurrence(getDate("01/05/2006"));
		assertEquals(getDate("01/03/2006"),resultDate);
	}
	
	public void testGetPrevScheduleDateAfterRecurrenceOnEndofMonth()throws Exception{
		startDate = getDate("01/01/2006");
		dayNumber = 31;
		recurAfer = 2;
		meeting = createMonthlyMeetingOnDate(dayNumber, recurAfer, startDate);
		Date resultDate = meeting.getPrevScheduleDateAfterRecurrence(getDate("31/05/2006"));
		assertEquals(getDate("31/03/2006"),resultDate);
	}
	
	
	private MeetingBO createDailyMeeting(Short recurAfer, Date startDate){
		return new MeetingBO(RecurrenceType.DAILY, recurAfer, startDate, MeetingType.CUSTOMERMEETING);
	}
	
	private MeetingBO createWeeklyMeeting(WeekDay weekDay, Short recurAfer, Date startDate){
		return new MeetingBO(weekDay, recurAfer, startDate, MeetingType.CUSTOMERMEETING);
	}
	
	private MeetingBO createMonthlyMeetingOnDate(Short dayNumber, Short recurAfer, Date startDate){
		return new MeetingBO(dayNumber, recurAfer, startDate, MeetingType.CUSTOMERMEETING);	
	}
	
	private MeetingBO createMonthlyMeetingOnWeekDay(WeekDay weekDay, RankType rank, Short recurAfer, Date startDate){
		return new MeetingBO(weekDay, rank, recurAfer, startDate, MeetingType.CUSTOMERMEETING);
	}
	
	private List createExpectedList(String dates)throws Exception{
		List expectedList=new ArrayList();
		StringTokenizer st= new StringTokenizer(dates,",");
		String str;
		while(st.hasMoreTokens()){
			str=st.nextToken();
			expectedList.add(getDate(str));	
		}
		return expectedList;
	}
	
	void matchDateLists(List<Date> expectedList, List<Date> list){
		for(int i=0; i<expectedList.size(); i++)
			assertEquals("Dates are invalid", expectedList.get(i), list.get(i));
	}		
}
