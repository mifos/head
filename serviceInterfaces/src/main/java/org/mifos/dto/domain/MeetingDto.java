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

package org.mifos.dto.domain;

import java.io.Serializable;

import org.joda.time.LocalDate;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class MeetingDto implements Serializable{

    private final LocalDate meetingStartDate;
    private final String meetingPlace;
    private final MeetingTypeDto meetingType;
    private final MeetingDetailsDto meetingDetailsDto;
    private String meetingSchedule;

    public String getMeetingSchedule() {
        return meetingSchedule;
    }

    public void setMeetingSchedule(String meetingSchedule) {
        this.meetingSchedule = meetingSchedule;
    }

	public MeetingDto(LocalDate meetingStartDate, String meetingPlace, MeetingTypeDto meetingType, MeetingDetailsDto meetingDetailsDto) {
        this.meetingStartDate = meetingStartDate;
        this.meetingPlace = meetingPlace;
        this.meetingType = meetingType;
        this.meetingDetailsDto = meetingDetailsDto;
    }

    public LocalDate getMeetingStartDate() {
        return this.meetingStartDate;
    }

    public String getMeetingPlace() {
        return this.meetingPlace;
    }

    public MeetingTypeDto getMeetingType() {
        return this.meetingType;
    }

    public MeetingDetailsDto getMeetingDetailsDto() {
        return this.meetingDetailsDto;
    }
}