/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.mifos.application.master.MessageLookup;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SearchUtils;
import org.mifos.security.util.UserContext;

public class MeetingHelper {

    public MeetingHelper() {
        super();
    }

    public String getMessage(MeetingBO meeting, UserContext userContext) {
        String key;
        Object[] args = new Object[3];
        if (meeting.isWeekly()) {
                key = MeetingConstants.WEEK_SCHEDULE;
            args[0] = meeting.getMeetingDetails().getRecurAfter();
            WeekDay weekDay = meeting.getMeetingDetails().getMeetingRecurrence().getWeekDayValue();
            String weekdayName = MessageLookup.getInstance().lookup(weekDay.getPropertiesKey());
            weekDay.setWeekdayName(weekdayName);
            args[1] = weekDay.getName();
        } else if (meeting.isMonthlyOnDate()) {
                key = MeetingConstants.MONTH_DAY_SCHEDULE;
            args[0] = meeting.getMeetingDetails().getDayNumber();
            args[1] = meeting.getMeetingDetails().getRecurAfter();
        } else {
                key = MeetingConstants.MONTH_SCHEDULE;
            args[0] = meeting.getMeetingDetails().getMeetingRecurrence().getRankOfDays().getName();
            args[1] = meeting.getMeetingDetails().getMeetingRecurrence().getWeekDay().getName();
            args[2] = meeting.getMeetingDetails().getRecurAfter();
        }
        return SearchUtils.getMessageWithSubstitution(FilePaths.MEETING_RESOURCE, userContext.getPreferredLocale(),
                key, args);
    }

    public String getMessageWithFrequency(MeetingBO meeting, UserContext userContext) {
        String key = null;
        Object[] args = new Object[1];
        if (meeting.isWeekly()) {
            key = MeetingConstants.WEEK_FREQUENCY;
        } else if (meeting.isMonthly()) {
            key = MeetingConstants.MONTH_FREQUENCY;
        }
        args[0] = meeting.getMeetingDetails().getRecurAfter();

        return SearchUtils.getMessageWithSubstitution(FilePaths.MEETING_RESOURCE, userContext.getPreferredLocale(),
                key, args);
    }

    public String getDetailMessageWithFrequency(MeetingBO meeting, UserContext userContext) {
        String key = null;
        Object[] args = new Object[1];
        if (meeting.isWeekly()) {
            key = MeetingConstants.WEEK_SCHEDULE_SHORT;
        } else if (meeting.isMonthly()) {
            key = MeetingConstants.MONTH_SCHEDULE_SHORT;
        }
        args[0] = meeting.getMeetingDetails().getRecurAfter();

        return SearchUtils.getMessageWithSubstitution(FilePaths.MEETING_RESOURCE, userContext.getPreferredLocale(),
                key, args);
    }
}
