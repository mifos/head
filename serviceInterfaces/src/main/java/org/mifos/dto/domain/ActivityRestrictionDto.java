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
import java.math.BigDecimal;

/**
 * This class is DTO of RoleActivityRestrictionBO
 */
public class ActivityRestrictionDto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer activityRestrictionId;
    private Short roleId;
    private Short activityRestrictionTypeId;
    private BigDecimal amountValue;

    public ActivityRestrictionDto(Short roleId, Integer activityRestrictionId, Short activityRestrictionTypeId,
            BigDecimal amountValue) {
        this.roleId = roleId;
        this.activityRestrictionId = activityRestrictionId;
        this.activityRestrictionTypeId = activityRestrictionTypeId;
        this.amountValue = amountValue;
    }

    public ActivityRestrictionDto(Short roleId, Short activityRestrictionTypeId,
            BigDecimal amountValue) {
        this.roleId = roleId;
        this.activityRestrictionTypeId = activityRestrictionTypeId;
        this.amountValue = amountValue;
    }
    
    public ActivityRestrictionDto(Short activityRestrictionTypeId,
            BigDecimal amountValue) {
        this.activityRestrictionTypeId = activityRestrictionTypeId;
        this.amountValue = amountValue;
    }


    public Integer getActivityRestrictionId() {
        return activityRestrictionId;
    }

    public void setActivityRestrictionId(Integer activityRestrictionId) {
        this.activityRestrictionId = activityRestrictionId;
    }

    public Short getRoleId() {
        return roleId;
    }

    public void setRoleId(Short roleId) {
        this.roleId = roleId;
    }

    public Short getActivityRestrictionTypeId() {
        return activityRestrictionTypeId;
    }

    public void setActivityRestrictionTypeId(Short activityRestrictionTypeId) {
        this.activityRestrictionTypeId = activityRestrictionTypeId;
    }

    public BigDecimal getAmountValue() {
        return amountValue;
    }

    public void setAmountValue(BigDecimal amountValue) {
        this.amountValue = amountValue;
    }
    
}