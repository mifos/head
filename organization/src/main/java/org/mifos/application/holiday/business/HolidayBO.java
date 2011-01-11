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

package org.mifos.application.holiday.business;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.application.holiday.util.helpers.HolidayConstants;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.calendar.NextScheduledEventStrategy;
import org.mifos.calendar.NextWorkingDayStrategy;
import org.mifos.dto.domain.HolidayDetails;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.service.BusinessRuleException;

public class HolidayBO extends AbstractBusinessObject implements Holiday {

    private Date holidayThruDate;

    private String holidayName;

    private RepaymentRuleTypes repaymentRuleType;

    private boolean validationEnabled = true;

    private Short holidayChangesAppliedFlag = YesNoFlag.NO.getValue();

    private Date holidayFromDate;

    private Integer id;

    public HolidayBO() {
    }

    public HolidayBO(HolidayDetails holidayDetails) {
        this.holidayName = holidayDetails.getName();
        this.holidayFromDate = holidayDetails.getFromDate().toDateMidnight().toDate();
        if (holidayDetails.getThruDate() != null) {
            this.holidayThruDate = holidayDetails.getThruDate().toDateMidnight().toDate();
        }

        if (holidayDetails.isHolidayChangesApplied()) {
            this.holidayChangesAppliedFlag = YesNoFlag.YES.getValue();
        }

        this.repaymentRuleType = RepaymentRuleTypes.fromInt(holidayDetails.getRepaymentRuleType().intValue());
    }

    @Override
    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    public void setValidationEnabled(final boolean validationEnabled) {
        this.validationEnabled = validationEnabled;
    }

    public RepaymentRuleTypes getRepaymentRuleType() {
        return repaymentRuleType;
    }

    @Override
    public DateTime getFromDate() {
        return new DateTime(getHolidayFromDate());
    }

    public Date getHolidayFromDate() {
        return holidayFromDate;
    }

    public DateTime getThruDate() {
        return new DateTime(getHolidayThruDate());
    }

    public Date getHolidayThruDate() {
        return this.holidayThruDate;
    }

    @Override
    public String getName() {
        return getHolidayName();
    }

    public String getHolidayName() {
        return this.holidayName;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setRepaymentRuleType(final RepaymentRuleTypes repaymentRuleType) {
        this.repaymentRuleType = repaymentRuleType;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setHolidayFromDate(final Date holidayFromDate) {
        this.holidayFromDate = holidayFromDate;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setHolidayThruDate(final Date holidayThruDate) {
        this.holidayThruDate = holidayThruDate;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setHolidayName(final String holidayName) {
        this.holidayName = holidayName;
    }

    public Short getHolidayChangesAppliedFlag() {
        return holidayChangesAppliedFlag;
    }

    public void setHolidayChangesAppliedFlag(final Short flag) {
        this.holidayChangesAppliedFlag = flag;
    }

    @Override
    public boolean encloses(final Date date) {

        LocalDate dateToCheck = new LocalDate(date);

        LocalDate fromDate = new LocalDate(getHolidayFromDate());
        LocalDate thruDate = new LocalDate(getHolidayThruDate());

        return ((dateToCheck.isEqual(fromDate) || dateToCheck.isAfter(fromDate)) &&
        (dateToCheck.isBefore(thruDate) || dateToCheck.isEqual(thruDate)));
    }

    public boolean encloses(final DateTime dateTime) {
        return encloses(dateTime.toDate());
    }

    /**
     * Shift the scheduled day according to the holiday's repayment rule.
     */
    @Override
    public DateTime adjust(final DateTime scheduledDay, final List<Days> workingDays,
            final ScheduledEvent scheduledEvent) {

        // This is a hack -- need to refactor into cleaner code or subclass HolidayBO into
        // specialized classes for each repayment rule

        DateTime adjustedDate = scheduledDay;
        switch (repaymentRuleType) {
        case NEXT_MEETING_OR_REPAYMENT:
            do {
                adjustedDate = (new NextScheduledEventStrategy(scheduledEvent)).adjust(adjustedDate);
            } while (this.containsDate(adjustedDate));
            break;
        case NEXT_WORKING_DAY:
            adjustedDate = new NextWorkingDayStrategy(workingDays).adjust(new DateTime(this.getHolidayThruDate())
                    .plusDays(1));
            break;
        case SAME_DAY:
            break;
        case REPAYMENT_MORATORIUM:
            do {
                adjustedDate = (new NextScheduledEventStrategy(scheduledEvent)).adjust(adjustedDate);
            } while (this.containsDate(adjustedDate));
            break;
        default:
            break;
        }
        return adjustedDate;
    }

    private boolean containsDate(DateTime date) {
        return !(date.isBefore(new DateTime(this.getHolidayFromDate())) || date.isAfter(new DateTime(this
                .getHolidayThruDate())));
    }

    public void markAsApplied() {
        this.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());
    }

    @Override
    public boolean equals(final Object o) {
        if (id != null) {
            return id.equals(((HolidayBO) o).getId());
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    public HolidayDetails toDto() {
        return new HolidayDetails(this.holidayName, this.holidayFromDate, this.holidayThruDate, this.repaymentRuleType.getValue());
    }

    public static HolidayBO fromDto(HolidayDetails holidayDetails) {
        return new HolidayBO(holidayDetails);
    }

    public void validate() {
        validateFromDateAgainstCurrentDate(this.holidayFromDate);
        validateFromDateAgainstThruDate(this.holidayFromDate, this.holidayThruDate);
    }

    public boolean isFutureRepayment() {
        return repaymentRuleType != RepaymentRuleTypes.SAME_DAY;
    }

    private void validateFromDateAgainstCurrentDate(final Date fromDate) {

        LocalDate currentDate = new LocalDate();
        LocalDate fromLocalDate = new LocalDate(fromDate);

        if (!fromLocalDate.isAfter(currentDate)) {
            throw new BusinessRuleException(HolidayConstants.INVALIDFROMDATE);
        }
    }

    private void validateFromDateAgainstThruDate(final Date fromDate, final Date thruDate) {

        LocalDate fromLocalDate = new LocalDate(fromDate);
        LocalDate thruLocalDate = new LocalDate(thruDate);

        if (fromLocalDate.isAfter(thruLocalDate)) {
            throw new BusinessRuleException(HolidayConstants.INVALIDTHRUDATE);
        }
    }
}