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

package org.mifos.accounts.fees.business;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingHelper;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.View;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.LocalizationConverter;

public class FeeView extends View {
    private String feeId;

    private String feeName;

    private String amount;

    private Short currencyId;

    private boolean periodic;

    private String feeSchedule;

    private Short feeRemoved;

    private String feeFormula;

    private Short localeId;

    private RateAmountFlag feeType;

    private RecurrenceType frequencyType;

    public FeeView() {
    }

    public FeeView(UserContext userContext, FeeBO fee) {
        if (userContext != null)
            localeId = userContext.getLocaleId();
        this.feeId = fee.getFeeId().toString();
        this.feeType = fee.getFeeType();
        this.feeName = fee.getFeeName();
        if (fee.getFeeType().equals(RateAmountFlag.AMOUNT)) {
            this.amount = ((AmountFeeBO) fee).getFeeAmount().toString();
            this.setCurrencyId(((AmountFeeBO) fee).getFeeAmount().getCurrency().getCurrencyId());
            this.feeFormula = "";
        } else {
            this.amount = ((RateFeeBO) fee).getRate().toString();
            FeeFormulaEntity feeEntity = ((RateFeeBO) fee).getFeeFormula();
            this.feeFormula = feeEntity.getFormulaString();
        }
        this.periodic = fee.isPeriodic();
        if (fee.isPeriodic()) {
            MeetingBO feeMeeting = fee.getFeeFrequency().getFeeMeetingFrequency();
            this.feeSchedule = new MeetingHelper().getMessageWithFrequency(feeMeeting, userContext);
            if (feeMeeting.isMonthly())
                this.frequencyType = RecurrenceType.MONTHLY;
            else if (feeMeeting.isWeekly())
                this.frequencyType = RecurrenceType.WEEKLY;
            else
                this.frequencyType = RecurrenceType.DAILY;
        }
        this.feeRemoved = YesNoFlag.NO.getValue();
    }

    public String getFeeSchedule() {
        return feeSchedule;
    }

    public String getAmount() {
        return amount;
    }

    public Double getAmountMoney() {
        return new LocalizationConverter().getDoubleValueForCurrentLocale(amount);
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isPeriodic() {
        return periodic;
    }

    public void setCurrencyId(Short currencyId) {
        this.currencyId = currencyId;
    }

    public Short getCurrencyId() {
        return currencyId;
    }

    public String getFeeId() {
        return feeId;
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public String getFeeName() {
        return feeName;
    }

    public Short getFeeRemoved() {
        return feeRemoved;
    }

    public void setFeeRemoved(Short feeRemoved) {
        this.feeRemoved = feeRemoved;
    }

    public boolean isRemoved() {
        return feeRemoved.equals(YesNoFlag.YES.getValue());
    }

    public Short getFeeIdValue() {
        return StringUtils.isNotBlank(feeId) ? Short.valueOf(feeId) : null;
    }

    public String getFeeFormula() {
        return feeFormula;
    }

    public Short getLocaleId() {
        return localeId;
    }

    public RateAmountFlag getFeeType() {
        return feeType;
    }

    public RecurrenceType getFrequencyType() {
        return frequencyType;
    }

    public void setFrequencyType(RecurrenceType frequencyType) {
        this.frequencyType = frequencyType;
    }

    public boolean isValidForCurrency(Short currencyId){
     //  Rate fees do not have currency hence the currencyId will be null for them,
     //  when fee has a currency  then it should match loan account currency id
       return (getCurrencyId()== null || getCurrencyId().equals(currencyId));
    }

}
