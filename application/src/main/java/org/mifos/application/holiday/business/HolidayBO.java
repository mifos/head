/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import static org.mifos.framework.util.helpers.DateUtils.getDateWithoutTimeStamp;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.HolidayConstants;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.calendar.DateAdjustmentStrategy;
import org.mifos.calendar.HolidayAdjustmentRuleFactory;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.schedule.ScheduledEvent;

public class HolidayBO extends BusinessObject implements Holiday {

    private HolidayPK holidayPK;

    private Date holidayThruDate;

    private String holidayName;

    private RepaymentRuleEntity repaymentRuleEntity;

    private boolean validationEnabled = true;

    private Short holidayChangesAppliedFlag;

    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    public void setValidationEnabled(final boolean validationEnabled) {
        this.validationEnabled = validationEnabled;
    }

    protected HolidayBO() {
        this.holidayPK = null;
        this.holidayChangesAppliedFlag = YesNoFlag.NO.getValue();
    }

    public HolidayBO(final HolidayPK holidayPK, final Date holidayThruDate, final String holidayName,
            final RepaymentRuleEntity repaymentRuleEntity) throws ApplicationException {

        this.holidayPK = new HolidayPK();

        if (holidayPK != null) {
            this.holidayPK.setOfficeId(holidayPK.getOfficeId());
            this.holidayPK.setHolidayFromDate(holidayPK.getHolidayFromDate());
        } else {
            throw new ApplicationException(HolidayConstants.HOLIDAY_CREATION_EXCEPTION);
        }
        this.holidayThruDate = holidayThruDate;
        this.holidayName = holidayName;
        this.repaymentRuleEntity = repaymentRuleEntity;
        this.holidayChangesAppliedFlag = YesNoFlag.NO.getValue();
    }

    public HolidayBO(final HolidayPK holidayPK, final Date holidayThruDate, final String holidayName, final short repaymentRuleId,
            final String lookupValueKey) throws ApplicationException {

        this.holidayPK = new HolidayPK();

        if (holidayPK != null) {
            this.holidayPK.setOfficeId(holidayPK.getOfficeId());
            this.holidayPK.setHolidayFromDate(holidayPK.getHolidayFromDate());
        } else {
            throw new ApplicationException(HolidayConstants.HOLIDAY_CREATION_EXCEPTION);
        }
        this.holidayThruDate = holidayThruDate;
        this.holidayName = holidayName;
        this.repaymentRuleEntity = new RepaymentRuleEntity(repaymentRuleId, lookupValueKey);
        this.holidayChangesAppliedFlag = YesNoFlag.NO.getValue();
    }

    public HolidayPK getHolidayPK() {
        return this.holidayPK;
    }

    public RepaymentRuleEntity getRepaymentRuleEntity() {
        return this.repaymentRuleEntity;
    }

    public Date getHolidayFromDate() {
        return this.holidayPK.getHolidayFromDate();
    }

    public Date getHolidayThruDate() {
        return this.holidayThruDate;
    }

    public String getHolidayName() {
        return this.holidayName;
    }

    public void setHolidayPK(final HolidayPK holidayPK) {
        this.holidayPK = holidayPK;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setRepaymentRuleEntity(final RepaymentRuleEntity repaymentRuleEntity) {
        this.repaymentRuleEntity = repaymentRuleEntity;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setHolidayFromDate(final Date holidayFromDate) {
        this.holidayPK.setHolidayFromDate(holidayFromDate);
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

    /**
     * @deprecated use {@link HolidayDao#save}
     */
    @Deprecated
    public void save() throws ApplicationException {
        if (this.getHolidayThruDate() == null) {
            this.setHolidayThruDate(this.getHolidayFromDate());
        }

        if (isValidationEnabled()) {
            this.validateFromDateAgainstCurrentDate(this.getHolidayFromDate());
            this.validateFromDateAgainstThruDate(this.getHolidayFromDate(), this.getHolidayThruDate());
        }

        new HolidayPersistence().createOrUpdate(this);
    }

    public void update(final HolidayPK holidayPK, final Date holidayThruDate, final String holidayName) throws ApplicationException {
        this.holidayName = holidayName;
        this.holidayPK.setOfficeId(holidayPK.getOfficeId());
        this.holidayPK.setHolidayFromDate(holidayPK.getHolidayFromDate());

        if (this.getHolidayThruDate() == null) {
            this.setHolidayThruDate(this.getHolidayFromDate());
        }

        if (isValidationEnabled()) {
            this.validateFromDateAgainstCurrentDate(this.getHolidayFromDate());
            this.validateFromDateAgainstThruDate(this.getHolidayFromDate(), this.getHolidayThruDate());
        }

        if (this.getRepaymentRuleEntity().getLookUpValue().equals(RepaymentRuleTypes.SAME_DAY.getValue()))
            this.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());

        new HolidayPersistence().createOrUpdate(this);

        // this block should not be here
        // HolidayUtils.rescheduleLoanRepaymentDates(this);
        // HolidayUtils.rescheduleSavingDates(this);
        // end of block
    }

    protected void validateHolidayState(final Short masterTypeId, final Short stateId, final boolean isCustomer)
            throws ApplicationException {
        Integer records;
        records = new HolidayPersistence().isValidHolidayState(masterTypeId, stateId, isCustomer);
        if (records.intValue() != 0) {
            throw new ApplicationException(HolidayConstants.EXCEPTION_STATE_ALREADY_EXIST);
        }
    }

    public String getRepaymentRule() {
        return repaymentRuleEntity.getLookUpValue();
    }

    public Short getRepaymentRuleId() {
        return repaymentRuleEntity.getId();
    }
    
    public RepaymentRuleTypes getRepaymentRuleType() {
        return RepaymentRuleTypes.fromOrd(repaymentRuleEntity.getId());
    }

    private void validateFromDateAgainstCurrentDate(final Date fromDate) throws ApplicationException {
        if (DateUtils.getDateWithoutTimeStamp(fromDate.getTime()).compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) <= 0) {
            throw new ApplicationException(HolidayConstants.INVALIDFROMDATE);
        }
    }

    private void validateFromDateAgainstThruDate(final Date fromDate, final Date thruDate) throws ApplicationException {
        if (DateUtils.getDateWithoutTimeStamp(fromDate.getTime()).compareTo(
                DateUtils.getDateWithoutTimeStamp(thruDate.getTime())) > 0) {
            throw new ApplicationException(HolidayConstants.INVALIDTHRUDATE);
        }
    }

    public boolean encloses(final Date date) {
        return date.compareTo(getDateWithoutTimeStamp(getHolidayFromDate().getTime())) >= 0
                && date.compareTo(getDateWithoutTimeStamp(getHolidayThruDate().getTime())) <= 0;
    }

    @Override
    public DateTime adjust(final DateTime day, final List<Days> workingDays, final ScheduledEvent scheduledEvent) {

        DateAdjustmentStrategy dateAdjustment = HolidayAdjustmentRuleFactory.createStrategy(workingDays,
                scheduledEvent, RepaymentRuleTypes.fromShort(repaymentRuleEntity.getId()));

        return dateAdjustment.adjust(day);
    }

}