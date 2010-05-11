package org.mifos.application.holiday.persistence;

import java.util.Date;

import org.hibernate.ejb.HibernatePersistence;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.helpers.DateUtils;

public class HolidayDetails {
    private String name;
    private Date fromDate;
    private Date thruDate;
    private RepaymentRuleTypes repaymentRuleType;
    private boolean disableValidation;
    private YesNoFlag holidayChangesAppliedFlag;

    public HolidayDetails(String name, Date fromDate, Date thruDate, RepaymentRuleTypes repaymentRuleType) {
        this.name = name;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.repaymentRuleType = repaymentRuleType;
        this.holidayChangesAppliedFlag = YesNoFlag.NO;
    }

    public HolidayDetails(String name, Date fromDate, Date thruDate, RepaymentRuleTypes repaymentRuleType,
            YesNoFlag holidayChangesAppliedFlag) {
        this.name = name;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.repaymentRuleType = repaymentRuleType;
        this.holidayChangesAppliedFlag = holidayChangesAppliedFlag;
    }

    public YesNoFlag getHolidayChangesAppliedFlag() {
        return this.holidayChangesAppliedFlag;
    }

    public String getName() {
        return name;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getThruDate() {
        if (thruDate == null) {
            return fromDate;
        }
        return thruDate;
    }

    public RepaymentRuleTypes getRepaymentRuleType() {
        return repaymentRuleType;
    }

    // this is only to support integration testing
    // re-factor test instead
    public void disableValidation(boolean disableValidation) {
        this.disableValidation = disableValidation;
    }

    public void validate() throws ValidationException {
        if (!disableValidation) {
            Date fromDate = DateUtils.getDateWithoutTimeStamp(this.fromDate);
            Date thruDate = DateUtils.getDateWithoutTimeStamp(getThruDate());
            Date today = DateUtils.getCurrentDateWithoutTimeStamp();
            if (fromDate.compareTo(today) <= 0) {
                throw new ValidationException(
                        "Holiday cannot start from today or a date in the past. Start date specified is " + fromDate);
            }
            if (thruDate.compareTo(fromDate) < 0) {
                throw new ValidationException("Holiday cannot end before start date. Start date specified is "
                        + fromDate + " and end date specified is " + thruDate);
            }
        }
    }
}
