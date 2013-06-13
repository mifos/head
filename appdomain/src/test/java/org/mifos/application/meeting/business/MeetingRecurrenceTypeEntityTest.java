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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.dto.domain.MeetingRecurrenceDto;

public class MeetingRecurrenceTypeEntityTest {

    private MeetingRecurrenceEntity meetingRecurrenceEntity;

    @Test
    public void shouldCreateDtoWithCorrectRecurrenceDetailsForWeekDayAndWeekOfMonth() throws Exception {

        // setup
        MeetingDetailsEntity meetingDetails = null;
        RankOfDay rank = RankOfDay.FIRST;
        WeekDay weekDay = WeekDay.MONDAY;
        Short dayNumber = null;

        meetingRecurrenceEntity = new MeetingRecurrenceEntity(dayNumber, weekDay, rank, meetingDetails);

        // exercise test
        MeetingRecurrenceDto meetingRecurrenceDto = meetingRecurrenceEntity.toDto();

        // verification
        assertThat(meetingRecurrenceDto.getDayNumber(), is(1));
        assertThat(meetingRecurrenceDto.getDayOfWeek(), is(WeekDay.MONDAY.getValue().intValue()));
        assertThat(meetingRecurrenceDto.getWeekOfMonth(), is(RankOfDay.FIRST.getValue().intValue()));
    }
}