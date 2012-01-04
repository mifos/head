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

package org.mifos.dto.screen;

import java.io.Serializable;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class AccountFeesDto implements Serializable {

    private final Short feeFrequencyTypeId;
    private final Short feePaymentTypeId;
    private final Short feeStatus;
    private final String feeName;
    private final String accountFeeAmount;
    private final String meetingRecurrence;
    private final Short feeId;

    public AccountFeesDto(Short feeFrequencyTypeId, Short feePaymentTypeId, Short feeStatus, String feeName, String accountFeeAmount,
            String meetingRecurrence, Short feeId) {
        super();
        this.feeFrequencyTypeId = feeFrequencyTypeId;
        this.feePaymentTypeId = feePaymentTypeId;
        this.feeStatus = feeStatus;
        this.feeName = feeName;
        this.accountFeeAmount = accountFeeAmount;
        this.meetingRecurrence = meetingRecurrence;
        this.feeId = feeId;
    }

    public Short getFeeFrequencyTypeId() {
        return this.feeFrequencyTypeId;
    }

    public Short getFeePaymentTypeId() {
        return feePaymentTypeId;
    }

    public Short getFeeStatus() {
        return this.feeStatus;
    }

    public String getFeeName() {
        return this.feeName;
    }

    public String getAccountFeeAmount() {
        return this.accountFeeAmount;
    }

    public String getMeetingRecurrence() {
        return this.meetingRecurrence;
    }

    public Short getFeeId() {
        return this.feeId;
    }
}