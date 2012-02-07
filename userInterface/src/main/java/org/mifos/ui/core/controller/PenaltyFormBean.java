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

package org.mifos.ui.core.controller;

import org.mifos.dto.domain.PenaltyDto;

public class PenaltyFormBean {
    private String id;
    private String name;
    private String oldName;
    private String statusId;
    private String categoryTypeId;
    private String periodTypeId;
    private String duration;
    private String min;
    private String max;
    private String amount;
    private String currencyId;
    private String rate;
    private String formulaId;
    private String frequencyId;
    private String glCodeId;
    private boolean showAmount;
    
    public PenaltyFormBean() {
        frequencyId = "1";
        statusId = "1";
        showAmount = false;
    }

    public PenaltyFormBean(PenaltyDto dto) {
        id = dto.getPenaltyId();
        oldName = dto.getPenaltyName();
        name = oldName;
        
        categoryTypeId = Short.toString(dto.getCategoryType().getId());
        
        statusId = Short.toString(dto.getStatus().getId());
        
        periodTypeId = Short.toString(dto.getPeriodType().getId());
        
        duration = dto.getPeriodDuration();
        min = dto.getMinimumLimit();
        max = dto.getMaximumLimit();
        
        amount = dto.getAmount() == null ? "" : dto.getAmount();
        currencyId = dto.getCurrencyId() == null ? null : Integer.toString(dto.getCurrencyId());
        
        if(dto.getRate() != null) {
            rate = Double.toString(dto.getRate());
        
            formulaId = Short.toString(dto.getPenaltyFormula().getId());
        }
        
        frequencyId = Short.toString(dto.getPenaltyFrequency().getId());
        
        glCodeId = Short.toString(dto.getGlCodeDto().getGlcodeId());
        
        showAmount = !amount.equalsIgnoreCase("");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getCategoryTypeId() {
        return categoryTypeId;
    }

    public void setCategoryTypeId(String categoryTypeId) {
        this.categoryTypeId = categoryTypeId;
    }

    public String getPeriodTypeId() {
        return periodTypeId;
    }

    public void setPeriodTypeId(String periodTypeId) {
        this.periodTypeId = periodTypeId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration.trim();
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min.trim();
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max.trim();
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount.trim();
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate.trim();
    }

    public String getFormulaId() {
        return formulaId;
    }

    public void setFormulaId(String formulaId) {
        this.formulaId = formulaId;
    }

    public String getFrequencyId() {
        return frequencyId;
    }

    public void setFrequencyId(String frequencyId) {
        this.frequencyId = frequencyId;
    }

    public String getGlCodeId() {
        return glCodeId;
    }

    public void setGlCodeId(String glCodeId) {
        this.glCodeId = glCodeId;
    }

    public boolean isShowAmount() {
        return showAmount;
    }

    public void setShowAmount(boolean showAmount) {
        this.showAmount = showAmount;
    }
    
}
