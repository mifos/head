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

package org.mifos.application.meeting.business;

import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.dto.domain.MeetingDetailsDto;
import org.mifos.dto.domain.MeetingDto;
import org.mifos.service.BusinessRuleException;

public class MeetingFactory {

    public MeetingBO create(MeetingDto meetingDto) {
        try {
            MeetingDetailsDto meetingDetailsDto = meetingDto.getMeetingDetailsDto();
            MeetingBO meeting = new MeetingBO(RecurrenceType.fromInt(meetingDetailsDto.getRecurrenceTypeId()
                    .shortValue()), meetingDetailsDto.getEvery().shortValue(), meetingDto.getMeetingStartDate()
                    .toDateMidnight().toDate(), MeetingType.CUSTOMER_MEETING);

            RankOfDay rank = null;
            Integer weekOfMonth = meetingDetailsDto.getRecurrenceDetails().getWeekOfMonth();
            if (weekOfMonth != null && weekOfMonth > 0) {
                rank = RankOfDay.getRankOfDay(meetingDetailsDto.getRecurrenceDetails().getWeekOfMonth());
            }

            WeekDay weekDay = null;
            Integer weekDayNum = meetingDetailsDto.getRecurrenceDetails().getDayOfWeek();
            if (weekDayNum != null && weekDayNum > 0) {
                weekDay = WeekDay.getWeekDay(meetingDetailsDto.getRecurrenceDetails().getDayOfWeek());
            }

            Integer dayNumber = meetingDetailsDto.getRecurrenceDetails().getDayNumber();
            if (dayNumber != null && dayNumber > 0) {
                meeting.update(meetingDetailsDto.getRecurrenceDetails().getDayNumber().shortValue(), meetingDto
                        .getMeetingPlace());
            }

            if (rank != null && weekDay != null) {
                meeting = new MeetingBO(weekDay, rank, meetingDetailsDto.getEvery().shortValue(), meetingDto
                        .getMeetingStartDate().toDateMidnight().toDate(), MeetingType.CUSTOMER_MEETING, meetingDto
                        .getMeetingPlace());
            }
            else if (weekDay != null) {
                meeting = new MeetingBO(weekDay, meetingDetailsDto.getEvery().shortValue(), meetingDto
                        .getMeetingStartDate().toDateMidnight().toDate(), MeetingType.CUSTOMER_MEETING, meetingDto
                        .getMeetingPlace());
            }

            return meeting;
        } catch (MeetingException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

}
