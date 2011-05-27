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
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class FeeFrequencyDto implements Serializable {

    private String type;
    private String payment;
    private Integer paymentId;
    private String recurAfterPeriod;
    private boolean weekly;
    private boolean monthly;
    private boolean oneTime;

    public boolean isOneTime() {
        return oneTime;
    }

    public void setOneTime(boolean oneTime) {
        this.oneTime = oneTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setRecurAfterPeriod(String recurAfterPeriod) {
        this.recurAfterPeriod = recurAfterPeriod;
    }

    public String getRecurAfterPeriod() {
        return recurAfterPeriod;
    }

    public void setWeekly(boolean weekly) {
        this.weekly = weekly;
    }

    public boolean isWeekly() {
        return weekly;
    }

    public void setMonthly(boolean monthly) {
        this.monthly = monthly;
    }

    public boolean isMonthly() {
        return monthly;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getPayment() {
        return payment;
    }
    
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }
}