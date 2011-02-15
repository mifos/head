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

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class ApplicableAccountFeeDto implements Serializable {

    private Integer feeId;
    private String amount;
    private boolean removed;
    private boolean weekly;
    private boolean monthly;
    private boolean periodic;
    private String feeName;
    private String feeSchedule;

    public ApplicableAccountFeeDto(Integer feeId, String feeName, String amount, boolean removed, boolean weekly, boolean monthly, boolean periodic, String feeSchedule) {
        this.feeId = feeId;
        this.amount = amount;
        this.feeName = feeName;
        this.removed = removed;
        this.weekly = weekly;
        this.monthly = monthly;
        this.periodic = periodic;
        this.feeSchedule = feeSchedule;
    }

    /**
     * used in action form for some reason.
     */
    public ApplicableAccountFeeDto() {
    }

    public Integer getFeeId() {
        return this.feeId;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setFeeRemoved(boolean isRemoved) {
        this.removed = isRemoved;
    }

    public boolean isFeeRemoved() {
        return this.removed;
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public boolean isWeekly() {
        return this.weekly;
    }

    public boolean isMonthly() {
        return this.monthly;
    }

    public boolean isPeriodic() {
        return this.periodic;
    }

    public void setRemoved(boolean isRemoved) {
        this.removed = isRemoved;
    }

    public String getFeeName() {
        return this.feeName;
    }

    public String getFeeSchedule() {
        return this.feeSchedule;
    }

    public void setFeeId(Integer feeId) {
        this.feeId = feeId;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setWeekly(boolean weekly) {
        this.weekly = weekly;
    }

    public void setMonthly(boolean monthly) {
        this.monthly = monthly;
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public void setFeeSchedule(String feeSchedule) {
        this.feeSchedule = feeSchedule;
    }
}