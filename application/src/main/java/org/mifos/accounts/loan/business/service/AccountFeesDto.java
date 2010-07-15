package org.mifos.accounts.loan.business.service;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.helpers.Money;

public class AccountFeesDto  implements DataTransferObject {

    private final Short feeFrequencyTypeId;
    private final Short feeStatus;
    private final String feeName;
    private final Money accountFeeAmount;
    private final String meetingRecurrence;
    private final Short feeId;

    public AccountFeesDto(Short feeFrequencyTypeId, Short feeStatus, String feeName, Money accountFeeAmount,
            String meetingRecurrence, Short feeId) {
        super();
        this.feeFrequencyTypeId = feeFrequencyTypeId;
        this.feeStatus = feeStatus;
        this.feeName = feeName;
        this.accountFeeAmount = accountFeeAmount;
        this.meetingRecurrence = meetingRecurrence;
        this.feeId = feeId;
    }

    public Short getFeeFrequencyTypeId() {
        return this.feeFrequencyTypeId;
    }

    public Short getFeeStatus() {
        return this.feeStatus;
    }

    public String getFeeName() {
        return this.feeName;
    }

    public Money getAccountFeeAmount() {
        return this.accountFeeAmount;
    }

    public String getMeetingRecurrence() {
        return this.meetingRecurrence;
    }

    public Short getFeeId() {
        return this.feeId;
    }
}
