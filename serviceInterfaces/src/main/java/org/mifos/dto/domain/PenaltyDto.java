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
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2", "NM_CONFUSING"}, justification="should disable at filter level and also for pmd - not important for us")
public class PenaltyDto implements Serializable {
    private String penaltyId;
    private String penaltyName;
    private PenaltyStatusDto status;
    private PenaltyCategoryDto categoryType;
    private PenaltyPeriodDto periodType;
    private String periodDuration;
    private String minimumLimit;
    private String maximumLimit;
    private PenaltyFrequencyDto penaltyFrequency;
    private GLCodeDto glCodeDto;

    private String amount;
    private Integer currencyId;

    private Double rate;
    private PenaltyFormulaDto penaltyFormula;

    public String getPenaltyId() {
        return penaltyId;
    }

    public void setPenaltyId(String penaltyId) {
        this.penaltyId = penaltyId;
    }

    public String getPenaltyName() {
        return penaltyName;
    }

    public void setPenaltyName(String penaltyName) {
        this.penaltyName = penaltyName;
    }

    public PenaltyStatusDto getStatus() {
        return status;
    }

    public void setStatus(PenaltyStatusDto status) {
        this.status = status;
    }

    public PenaltyCategoryDto getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(PenaltyCategoryDto categoryType) {
        this.categoryType = categoryType;
    }

    public PenaltyPeriodDto getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PenaltyPeriodDto periodType) {
        this.periodType = periodType;
    }

    public String getPeriodDuration() {
        return periodDuration;
    }

    public void setPeriodDuration(String periodDuration) {
        this.periodDuration = periodDuration;
    }

    public String getMinimumLimit() {
        return minimumLimit;
    }

    public void setMinimumLimit(String minimumLimit) {
        this.minimumLimit = minimumLimit;
    }

    public String getMaximumLimit() {
        return maximumLimit;
    }

    public void setMaximumLimit(String maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public PenaltyFrequencyDto getPenaltyFrequency() {
        return penaltyFrequency;
    }

    public void setPenaltyFrequency(PenaltyFrequencyDto penaltyFrequency) {
        this.penaltyFrequency = penaltyFrequency;
    }

    public GLCodeDto getGlCodeDto() {
        return glCodeDto;
    }

    public void setGlCodeDto(GLCodeDto glCodeDto) {
        this.glCodeDto = glCodeDto;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public PenaltyFormulaDto getPenaltyFormula() {
        return penaltyFormula;
    }

    public void setPenaltyFormula(PenaltyFormulaDto penaltyFormula) {
        this.penaltyFormula = penaltyFormula;
    }
}
