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

package org.mifos.application.meeting.business;

import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.business.AbstractEntity;

/**
 * This class encapsulate the details about the meeting
 */
public class MeetingDetailsEntity extends AbstractEntity {

    private final Integer detailsId;

    private final RecurrenceTypeEntity recurrenceType;

    /*
     * The number of intervals between meetings. For example, if meetings are
     * weekly and recurAfter = 1 then meetings occur every week, recurAfter = 2
     * means every second week.
     */
    private Short recurAfter;

    private MeetingRecurrenceEntity meetingRecurrence;

    private final MeetingBO meeting;

    private int versionNo;

    protected MeetingDetailsEntity() {
        detailsId = null;
        recurrenceType = null;
        meetingRecurrence = null;
        meeting = null;
    }

    public MeetingDetailsEntity(final MeetingBO meeting, final RecurrenceType recurrenceType, final Short recurAfter) {
        this.detailsId = null;
        this.recurrenceType = new RecurrenceTypeEntity(recurrenceType);
        this.meeting = meeting;
        this.recurAfter = recurAfter;
    }

    public MeetingDetailsEntity(final RecurrenceTypeEntity recurrenceType, final Short dayNumber, final WeekDay weekDay, final RankOfDay rank,
            final Short recurAfter, final MeetingBO meeting) throws MeetingException {
        this.validateFields(recurAfter);
        this.recurrenceType = recurrenceType;
        this.recurAfter = recurAfter;
        this.meeting = meeting;
        if (recurrenceType.isWeekly()) {
            this.meetingRecurrence = new MeetingRecurrenceEntity(weekDay, this);
        } else if (recurrenceType.isMonthly()) {
            this.meetingRecurrence = new MeetingRecurrenceEntity(dayNumber, weekDay, rank, this);
        } else {
            this.meetingRecurrence = new MeetingRecurrenceEntity(this);
        }

        detailsId = null;
    }

    public Short getRecurAfter() {
        return recurAfter;
    }

    public void setRecurAfter(final Short recurAfter) {
        this.recurAfter = recurAfter;
    }

    public MeetingBO getMeeting() {
        return meeting;
    }

    public MeetingRecurrenceEntity getMeetingRecurrence() {
        return meetingRecurrence;
    }

    public RecurrenceTypeEntity getRecurrenceType() {
        return recurrenceType;
    }

    public RecurrenceType getRecurrenceTypeEnum() {
        return recurrenceType.asEnum();
    }

    public boolean isWeekly() {
        return getRecurrenceType().isWeekly();
    }

    public boolean isMonthly() {
        return getRecurrenceType().isMonthly();
    }

    public boolean isMonthlyOnDate() {
        return isMonthly() && getMeetingRecurrence().isOnDate();
    }

    public WeekDay getWeekDay() {
        return getMeetingRecurrence().getWeekDayValue();
    }

    public RankOfDay getWeekRank() {
        return getMeetingRecurrence().getWeekRank();
    }

    public Short getDayNumber() {
        return getMeetingRecurrence().getDayNumber();
    }

    private void validateFields(final Short recurAfter) throws MeetingException {
        if (recurAfter == null || recurAfter < 1) {
            throw new MeetingException(MeetingConstants.INVALID_RECURAFTER);
        }
    }

    public Integer getDetailsId() {
        return detailsId;
    }

    public void setMeetingRecurrence(final MeetingRecurrenceEntity meetingRecurrence) {
        this.meetingRecurrence = meetingRecurrence;
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public int getVersionNo() {
        return versionNo;
    }

}
