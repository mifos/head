/**

 * MeetingConstants.java    version: 1.0

 

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
package org.mifos.application.meeting.util.resources;

/**
 * This class contains all the constants related to the meeting module
 */
public interface MeetingConstants {

	// dependency

	public final String MEETINGDEPENDENCY = "Meeting";

	public final String MEETING = "Meeting";

	public final short WEEK = 1;

	public final short DAYS = 3;
	
	public final String WEEKLY = "1";
	
	public final String MONTHLY = "2";


	public final String WEEKDAYSLIST = "WeekDayList";

	public final String WEEKRANKLIST = "WeekRankList";

	public final String WEEKDAYSENTITY = "WeekDays";

	public final String WEEKDAYID = "weekDayId";

	public final String RANKDAYENTITY = "DayRank";

	public final String RANKDAYID = "rankOfDayId";

	public final String WEEKDAYCLASSPATH = "org.mifos.application.meeting.util.valueobjects.WeekDays";

	public final String RANKOFDAYCLASSPATH = "org.mifos.application.meeting.util.valueobjects.RankOfDays";

	public final short MONTH = 2;

	public final short MONTHRECURDAY = 1;

	public final short MONTHRECURRANK = 2;
	
	public final String MONTH_WITHOUT_RANK ="1";
	
	public final String MONTH_WITH_RANK ="2";
	
	//MeetingTypes
	public final Short INTEREST_CALC_FREQ = 2;
	public final Short INTEREST_POST_FREQ = 3;
	
	public final String KEYCREATEFAILED = "meeting.error.creationFailed";

	public final String KEYLOADFAILED = "meeting.error.loadFailed";

	public final String KEYUPDATEFAILED = "meeting.error.updateFailed";

	public final String MEETINGDETAILSVERSIONNO = "MeetingDetailsVersionNo";

	public final String MEETINGRECURRENCESVERSIONNO = "MeetingRecurrenceNo";

	public final String LOADMEETING = "loadMeeting";

	public final String KEYINVALIDMONTH = "meeting.error.invalidMomthDay";

	public final String FORWARD_GROUP_SUCESS = "group_success";

	public final String FORWARD_CLIENT_SUCESS = "client_success";

	public final String FORWARD_CENTER_SUCESS = "center_success";

	public final String FORWARD_EDIT_CENTER_MEETING_SUCESS = "editCenterMeeting_success";

	public final String FORWARD_EDIT_CLIENT_MEETING_SUCESS = "editClientMeeting_success";

	public final String FORWARD_GROUP_DETAILS_PAGE = "group_details_page";

	public final String FORWARD_LOAD_MEETING_SUCESS = "loadMeeting_success";

	public final String GROUP = "Group";

	public final String CLIENT = "Client";

	public final String CENTER = "Center";

	public final String CENTER_DETAILS = "CenterDetails";

	public final String CLIENT_DETAILS = "ClientDetails";

	public final String GROUP_DETAILS = "GroupDetails";

	public final String CUSTOMER = "Customer";
	
	public final String KEYINVALIDRECURAFTER="meeting.error.invalidRecurAfter";

}
