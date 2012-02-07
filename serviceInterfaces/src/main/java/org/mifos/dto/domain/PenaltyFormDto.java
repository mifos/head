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

public class PenaltyFormDto {
    private Short id;
    private Short categoryType;
    private Short penaltyStatus;
    private Short penaltyPeriod;
    private Short penaltyFrequency;
    private Short glCode;
    private Short penaltyFormula;
    private String penaltyName;
    private boolean ratePenalty;
    private Short currencyId;
    private Double rate;
    private String amount;
    private Integer duration;
    private Double max;
    private Double min;

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Short getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Short categoryType) {
        this.categoryType = categoryType;
    }

    public Short getPenaltyStatus() {
        return penaltyStatus;
    }

    public void setPenaltyStatus(Short penaltyStatus) {
        this.penaltyStatus = penaltyStatus;
    }

    public Short getPenaltyPeriod() {
        return penaltyPeriod;
    }

    public void setPenaltyPeriod(Short penaltyPeriod) {
        this.penaltyPeriod = penaltyPeriod;
    }

    public Short getPenaltyFrequency() {
        return penaltyFrequency;
    }

    public void setPenaltyFrequency(Short penaltyFrequency) {
        this.penaltyFrequency = penaltyFrequency;
    }

    public Short getGlCode() {
        return glCode;
    }

    public void setGlCode(Short glCode) {
        this.glCode = glCode;
    }

    public Short getPenaltyFormula() {
        return penaltyFormula;
    }

    public void setPenaltyFormula(Short penaltyFormula) {
        this.penaltyFormula = penaltyFormula;
    }

    public String getPenaltyName() {
        return penaltyName;
    }

    public void setPenaltyName(String penaltyName) {
        this.penaltyName = penaltyName;
    }

    public boolean isRatePenalty() {
        return ratePenalty;
    }

    public void setRatePenalty(boolean ratePenalty) {
        this.ratePenalty = ratePenalty;
    }

    public Short getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Short currencyId) {
        this.currencyId = currencyId;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

}
