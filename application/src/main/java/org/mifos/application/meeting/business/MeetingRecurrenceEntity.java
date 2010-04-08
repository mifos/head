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

import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.business.AbstractEntity;
import org.mifos.framework.exceptions.PersistenceException;

/**
 * This class encapsulate the MeetingRecurrence details
 */
public class MeetingRecurrenceEntity extends AbstractEntity {

    private final Integer detailsId;

    private WeekDaysEntity weekDay;

    private RankOfDaysEntity rankOfDays;

    private Short dayNumber;

    private final MeetingDetailsEntity meetingDetails;

    private int versionNo;

    /**
     * default constructor for hibernate
     */
    protected MeetingRecurrenceEntity() {
        detailsId = null;
        meetingDetails = null;
    }

    /**
     * minimal legal constructor
     */
    public MeetingRecurrenceEntity(final WeekDay weekDay, final MeetingDetailsEntity meetingDetails) {
        validateWeekDay(weekDay);
        this.weekDay = new WeekDaysEntity(weekDay);
        this.meetingDetails = meetingDetails;
        this.detailsId = null;
    }

    public MeetingRecurrenceEntity(final MeetingDetailsEntity meetingDetails) {
        this.meetingDetails = meetingDetails;
        this.weekDay = null;
        this.rankOfDays = null;
        this.dayNumber = null;
        this.detailsId = null;
    }

    public MeetingRecurrenceEntity(final Short dayNumber, final WeekDay weekDay, final RankType rank, final MeetingDetailsEntity meetingDetails,
            final MasterPersistence masterPersistence)
            throws MeetingException {
        validateFields(dayNumber, weekDay, rank);
        if (dayNumber != null) {
            this.dayNumber = dayNumber;
        } else {
            try {
                this.weekDay = (WeekDaysEntity) masterPersistence.retrieveMasterEntity(weekDay.getValue(),
                        WeekDaysEntity.class, null);
                this.rankOfDays = (RankOfDaysEntity) masterPersistence.retrieveMasterEntity(rank.getValue(),
                        RankOfDaysEntity.class, null);
            } catch (PersistenceException pe) {
                throw new MeetingException(pe);
            }
        }
        this.meetingDetails = meetingDetails;
        this.detailsId = null;
    }

    public Short getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(final Short dayNumber) {
        this.dayNumber = dayNumber;
    }

    public Integer getDetailsId() {
        return detailsId;
    }

    public MeetingDetailsEntity getMeetingDetails() {
        return meetingDetails;
    }

    public RankOfDaysEntity getRankOfDays() {
        return rankOfDays;
    }

    public void setRankOfDays(final RankOfDaysEntity rankOfDays) {
        this.rankOfDays = rankOfDays;
    }

    /**
     * This method is deprecated and intended to be replaced with
     * {@link #getWeekDayValue()}. If you need to look up a message, see
     * {@link MessageLookup}; if you need to look up something else, we plan to
     * make similar methods available.
     */
    public WeekDaysEntity getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(final WeekDaysEntity weekDay) {
        this.weekDay = weekDay;
    }

    public void setWeekDay(final WeekDay weekDay) {
        this.weekDay = new WeekDaysEntity(weekDay);
    }

    public boolean isOnDate() {
        return getDayNumber() != null;
    }

    public WeekDay getWeekDayValue() {
        return weekDay != null ? WeekDay.getWeekDay(weekDay.getId()) : null;
    }

    public RankType getWeekRank() {
        return rankOfDays != null ? RankType.getRankType(rankOfDays.getId()) : null;
    }

    public void updateDayNumber(final Short dayNumber) throws MeetingException {
        validateDayNumber(dayNumber);
        this.dayNumber = dayNumber;
    }

    public void updateWeekDay(final WeekDay weekDay) throws MeetingException {
        validateWeekDay(weekDay);
        this.weekDay = new WeekDaysEntity(weekDay);
    }

    public void update(final WeekDay weekDay, final RankType rank) throws MeetingException {
        if (weekDay == null || rank == null) {
            throw new MeetingException(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK);
        }
        this.weekDay = new WeekDaysEntity(weekDay);
        this.rankOfDays = new RankOfDaysEntity(rank);
    }

    private void validateWeekDay(final WeekDay weekDay) {
        if (weekDay == null) {
            throw new IllegalStateException(MeetingConstants.INVALID_WEEKDAY);
        }
    }

    private void validateFields(final Short dayNumber, final WeekDay weekDay, final RankType rank) throws MeetingException {
        validateDayNumber(dayNumber);

        if (dayNumber == null && weekDay == null && rank == null) {
            throw new MeetingException(MeetingConstants.INVALID_DAYNUMBER_OR_WEEK);
        }

        if (dayNumber == null && (weekDay == null || rank == null)) {
            throw new MeetingException(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK);
        }
    }

    private void validateDayNumber(final Short dayNumber) throws MeetingException {
        if (dayNumber != null && (dayNumber < 1 || dayNumber > 31)) {
            throw new MeetingException(MeetingConstants.INVALID_DAYNUMBER);
        }
    }

    public void setVersionNo(int versionNo) {
        this.versionNo = versionNo;
    }

    public int getVersionNo() {
        return versionNo;
    }
}
