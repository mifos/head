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

package org.mifos.accounts.util.helpers;

import java.io.Serializable;

public class ApplicableCharge implements Serializable {

    private String feeId;

    private String feeName;

    private String amountOrRate;

    private String formula;

    private String periodicity;

    private String paymentType;
    
    private String isRateType;

    public ApplicableCharge() {

    }

    public ApplicableCharge(String feeId, String feeName, String amountOrRate, String formula, String periodicity,
            String paymentType) {
        this.feeId = feeId;
        this.feeName = feeName;
        this.amountOrRate = amountOrRate;
        this.formula = formula;
        this.periodicity = periodicity;
        this.paymentType = paymentType;
    }

    public String getIsRateType() {
        return this.isRateType;
    }

    public void setIsRateType(boolean isRateType) {
        if (isRateType) {
            this.isRateType = "1";
        } else {
            this.isRateType = "0";
        }
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

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getAmountOrRate() {
        return amountOrRate;
    }

    public void setAmountOrRate(String amountOrRate) {
        this.amountOrRate = amountOrRate;
    }

    public String getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(String periodicity) {
        this.periodicity = periodicity;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

}
