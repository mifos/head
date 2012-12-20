/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.meeting.util.helpers;

/**
 * This class contains all the constants related to the meeting module
 */
public interface MeetingConstants {

    // dependency

    String MEETINGDEPENDENCY = "Meeting";

    String MEETING = "Meeting";

    String WEEKDAYSLIST = "WeekDayList";

    String WEEKRANKLIST = "WeekRankList";

    String MONTHLY_ON_DATE = "1";

    String MONTHLY_ON_WEEK_DAY = "2";

    String INPUT_CREATE = "create";
    String INPUT_EDIT = "edit";
    // MeetingTypes
    Short INTEREST_CALC_FREQ = 2;
    Short INTEREST_POST_FREQ = 3;

    String KEYCREATEFAILED = "meeting.error.creationFailed";

    String KEYLOADFAILED = "meeting.error.loadFailed";

    String KEYUPDATEFAILED = "meeting.error.updateFailed";

    String MEETINGDETAILSVERSIONNO = "MeetingDetailsVersionNo";

    String MEETINGRECURRENCESVERSIONNO = "MeetingRecurrenceNo";

    String LOADMEETING = "loadMeeting";

    String KEYINVALIDMONTH = "meeting.error.invalidMomthDay";

    String FORWARD_GROUP_SUCESS = "group_success";

    String FORWARD_CLIENT_SUCESS = "client_success";

    String FORWARD_CENTER_SUCESS = "center_success";

    String FORWARD_EDIT_CENTER_MEETING_SUCESS = "editCenterMeeting_success";

    String FORWARD_EDIT_CLIENT_MEETING_SUCESS = "editClientMeeting_success";

    String FORWARD_GROUP_DETAILS_PAGE = "group_details_page";

    String FORWARD_LOAD_MEETING_SUCESS = "loadMeeting_success";

    String GROUP = "Group";

    String CLIENT = "Client";

    String CENTER = "Center";

    String CENTER_DETAILS = "CenterDetails";

    String CLIENT_DETAILS = "ClientDetails";

    String GROUP_DETAILS = "GroupDetails";

    String CUSTOMER = "Customer";

    String DAY_SCHEDULE = "meeting.everyDaySchedule";
    String WEEK_SCHEDULE = "meeting.weekSchedule";
    String MONTH_DAY_SCHEDULE = "meeting.daySchedule";
    String MONTH_SCHEDULE = "meeting.monthSchedule";
    String WEEK_SCHEDULE_CHANGE = "meeting.weekSchedule.change";
    String MONTH_DAY_SCHEDULE_CHANGE = "meeting.daySchedule.change";
    String MONTH_SCHEDULE_CHANGE = "meeting.monthSchedule.change";
    String WEEK_SCHEDULE_SHORT = "meeting.weekScheduleShort";
    String MONTH_SCHEDULE_SHORT = "meeting.monthScheduleShort";
    String WEEK_FREQUENCY = "meeting.weekFrequency";
    String MONTH_FREQUENCY = "meeting.monthFrequency";

    String KEYINVALIDRECURAFTER = "meeting.error.invalidRecurAfter";
    String INVALID_ENDDATE = "errors.Meeting.invalidEndDate";
    String INVALID_OCCURENCES = "errors.Meeting.invalidOccurences";
    String INVALID_MEETINGDATE = "errors.Meeting.invalidMeetingDate";
    String INVALID_STARTDATE = "errors.Meeting.invalidStartDate";
    String INVALID_MEETINGTYPE = "errors.Meeting.invalidMeetingType";
    String INVALID_MEETINGPLACE = "errors.Meeting.invalidMeetingPlace";
    String INVALID_RECURRENCETYPE = "errors.Meeting.invalidRecurrenceType";
    String INVALID_RECURAFTER = "errors.Meeting.invalidRecurAfter";
    String INVALID_DAYNUMBER = "errors.Meeting.invalidDayNumber";
    String INVALID_WEEKDAY = "errors.Meeting.invalidWeekDay";
    String INVALID_WEEKDAY_OR_WEEKRANK = "errors.Meeting.invalidWeekDayOrWeekRank";
    String INVALID_DAYNUMBER_OR_WEEK = "errors.Meeting.invalidDayNumberOrWeek";
    String ERRORS_SPECIFY_WEEKDAY_AND_RECURAFTER = "errors.Meeting.specifyWeekDayAndRecurAfter";
    String ERRORS_SPECIFY_DAYNUM_AND_RECURAFTER = "errors.Meeting.specifyDayNumAndRecurAfter";
    String ERRORS_SPECIFY_MONTHLY_MEETING_ON_WEEKDAY = "errors.Meeting.specifyMonthlyMeetingOnWeekDay";

    String NOT_SUPPORTED_FREQUENCY_TYPE = "errors.not_supported_frequencytype";

}
