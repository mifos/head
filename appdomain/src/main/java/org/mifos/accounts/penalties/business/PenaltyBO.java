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

package org.mifos.accounts.penalties.business;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.penalties.exceptions.PenaltyException;
import org.mifos.accounts.penalties.util.helpers.PenaltyConstants;
import org.mifos.accounts.penalties.util.helpers.PenaltyFrequency;
import org.mifos.accounts.penalties.util.helpers.PenaltyStatus;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.AccountingRules;
import org.mifos.dto.domain.PenaltyDto;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;

public abstract class PenaltyBO extends AbstractBusinessObject {
    private final Short penaltyId;
    private String penaltyName;
    private PenaltyStatusEntity status;
    private final PenaltyCategoryEntity categoryType;
    private PenaltyPeriodEntity periodType;
    private Integer periodDuration;
    private Double minimumLimit;
    private Double maximumLimit;
    private PenaltyFrequencyEntity penaltyFrequency;
    private GLCodeEntity glCode;
    
    protected PenaltyBO(final UserContext userContext, final String name, final PenaltyCategoryEntity categoryEntity,
            final PenaltyPeriodEntity periodEntity, final Integer duration, final Double min, final Double max,
            final PenaltyFrequencyEntity frequencyEntity, final GLCodeEntity glCodeEntity) throws PenaltyException {
        
        validateFields(name, categoryEntity, periodEntity, min, max, frequencyEntity, glCodeEntity);
        
        setUserContext(userContext);
        setCreateDetails();
        
        this.penaltyId = null;
        this.penaltyName = name;
        this.status = new PenaltyStatusEntity(PenaltyStatus.ACTIVE);
        this.categoryType = categoryEntity;
        this.periodType = periodEntity;
        this.periodDuration = duration;
        this.minimumLimit = min;
        this.maximumLimit = max;
        this.penaltyFrequency = frequencyEntity;
        this.glCode = glCodeEntity;
    }

    protected PenaltyBO() {
        this.penaltyId = null;
        this.penaltyName = null;
        this.status = null;
        this.categoryType = null;
        this.periodType = null;
        this.periodDuration = null;
        this.minimumLimit = null;
        this.maximumLimit = null;
        this.penaltyFrequency = null;
        this.glCode = null;
    }
    
    public String getPenaltyName() {
        return penaltyName;
    }

    public void setPenaltyName(String penaltyName) {
        this.penaltyName = penaltyName;
    }

    public PenaltyStatusEntity getStatus() {
        return status;
    }

    public void setStatus(PenaltyStatusEntity status) {
        this.status = status;
    }

    public PenaltyPeriodEntity getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PenaltyPeriodEntity periodType) {
        this.periodType = periodType;
    }

    public Integer getPeriodDuration() {
        return periodDuration;
    }

    public void setPeriodDuration(Integer periodDuration) {
        this.periodDuration = periodDuration;
    }

    public Double getMinimumLimit() {
        return minimumLimit;
    }

    public void setMinimumLimit(Double minimumLimit) {
        this.minimumLimit = minimumLimit;
    }

    public Double getMaximumLimit() {
        return maximumLimit;
    }

    public void setMaximumLimit(Double maximumLimit) {
        this.maximumLimit = maximumLimit;
    }

    public PenaltyFrequencyEntity getPenaltyFrequency() {
        return penaltyFrequency;
    }

    public void setPenaltyFrequency(PenaltyFrequencyEntity penaltyFrequency) {
        this.penaltyFrequency = penaltyFrequency;
    }

    public GLCodeEntity getGlCode() {
        return glCode;
    }

    public void setGlCode(GLCodeEntity glCode) {
        this.glCode = glCode;
    }

    public Short getPenaltyId() {
        return penaltyId;
    }

    public PenaltyCategoryEntity getCategoryType() {
        return categoryType;
    }
    
    public boolean isOneTime() {
        return getPenaltyFrequency().isOneTime();
    }
    
    public boolean isDailyTime() {
        return getPenaltyFrequency().isDailyTime();
    }
    
    public boolean isWeeklyTime() {
        return getPenaltyFrequency().isWeeklyTime();
    }
    
    public boolean isMonthlyTime() {
        return getPenaltyFrequency().isMonthlyTime();
    }

    public PenaltyDto toDto() {
        PenaltyDto dto = new PenaltyDto();
        dto.setPenaltyId(Short.toString(this.penaltyId));
        dto.setPenaltyName(this.penaltyName);
        dto.setStatus(this.status.toDto());
        dto.setCategoryType(this.categoryType.toDto());
        dto.setPeriodType(this.periodType.toDto());
        dto.setPeriodDuration(this.periodDuration == null ? null : Integer.toString(this.periodDuration));
        dto.setMinimumLimit(Double.toString(this.minimumLimit));
        dto.setMaximumLimit(Double.toString(this.maximumLimit));
        dto.setPenaltyFrequency(this.penaltyFrequency.toDto());
        dto.setGlCodeDto(this.glCode.toDto());

        if (this instanceof AmountPenaltyBO) {
            Money amount = ((AmountPenaltyBO) this).getAmount();
            dto.setCurrencyId(amount.getCurrency().getCurrencyId().intValue());
            dto.setAmount(amount.toString(AccountingRules.getDigitsAfterDecimal()));
            dto.setRateBasedPenalty(false);
        } else {
            RatePenaltyBO rate = (RatePenaltyBO) this;
            dto.setRate(rate.getRate());
            dto.setPenaltyFormula(rate.getFormula().toDto());
            dto.setRateBasedPenalty(true);
        }
        
        return dto;
    }
    
    public void save() throws PenaltyException {
        try {
            ApplicationContextProvider.getBean(LegacyAccountDao.class).createOrUpdate(this);
        } catch (PersistenceException he) {
            throw new PenaltyException(PenaltyConstants.PENALTY_CREATE_ERROR, he);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        PenaltyBO rhs = (PenaltyBO) obj;
        return new EqualsBuilder().append(this.penaltyId, rhs.penaltyId).append(this.penaltyName, rhs.penaltyName).isEquals();
    }

    @Override
    public int hashCode() {
        int initialNonZeroOddNumber = 7;
        int multiplierNonZeroOddNumber = 7;
        return new HashCodeBuilder(initialNonZeroOddNumber, multiplierNonZeroOddNumber).append(this.penaltyId).append(this.penaltyName).hashCode();
    }

    @Override
    public String toString() {
        return new StringBuilder().append(this.penaltyId).append(" : ").append(this.penaltyName).toString();
    }
    
    protected void validateFields(String name, PenaltyCategoryEntity categoryEntity, PenaltyPeriodEntity periodEntity,
            Double min, Double max, PenaltyFrequencyEntity frequencyEntity, GLCodeEntity glCodeEntity) throws PenaltyException {
        validateName(name);
        validateCategory(categoryEntity);
        validatePeriod(periodEntity);
        validateMinAndMax(min, max);
        validateFrequency(frequencyEntity);
        validateGLCode(glCodeEntity);
    }
    
    private void validateName(final String name) throws PenaltyException {
        if (StringUtils.isBlank(name)) {
            throw new PenaltyException(PenaltyConstants.INVALID_PENALTY_NAME);
        }
    }

    private void validateCategory(final PenaltyCategoryEntity category) throws PenaltyException {
        if (category == null) {
            throw new PenaltyException(PenaltyConstants.INVALID_PENALTY_CATEGORY);
        }
    }
    
    private void validatePeriod(final PenaltyPeriodEntity periodEntity) throws PenaltyException {
        if (periodEntity == null) {
            throw new PenaltyException(PenaltyConstants.INVALID_PENALTY_PERIOD);
        }
    }
    
    private void validateMinAndMax(final Double min, final Double max) throws PenaltyException {
        if (min == null) {
            throw new PenaltyException(PenaltyConstants.INVALID_PENALTY_MINIMUM);
        }
        
        if (max == null) {
            throw new PenaltyException(PenaltyConstants.INVALID_PENALTY_MAXIMUM);
        }
        
        if (min.compareTo(max) > 0) {
            throw new PenaltyException(PenaltyConstants.INVALID_MAX_GREATER_MIN);
        }
    }
    
    private void validateFrequency(final PenaltyFrequencyEntity frequencyEntity) throws PenaltyException {
        if (frequencyEntity == null) {
            throw new PenaltyException(PenaltyConstants.INVALID_PENALTY_FREQUENCY);
        }
    }
    
    private void validateGLCode(final GLCodeEntity glCode) throws PenaltyException {
        if (glCode == null) {
            throw new PenaltyException(PenaltyConstants.INVALID_GLCODE);
        }
    }
}
