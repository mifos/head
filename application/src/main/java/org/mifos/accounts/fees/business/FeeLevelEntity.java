/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.fees.business;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.mifos.accounts.fees.util.helpers.FeeLevel;

@Entity
@Table(name = "FEELEVEL")
public class FeeLevelEntity implements Serializable {
    private Short feeLevelId;
    private Short levelId;
    private FeeBO fee;

    public FeeLevelEntity(FeeBO fee, FeeLevel feeLevel) {
        this.fee = fee;
        levelId = feeLevel.getValue();
    }

    protected FeeLevelEntity() {
        fee = null;
    }

    @Id
    @GeneratedValue
    @Column(name = "FEELEVEL_ID", nullable = false)
    protected Short getFeeLevelId() {
        return feeLevelId;
    }

    @Column(name = "LEVEL_ID")
    public Short getLevelId() {
        return levelId;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "FEE_ID")
    public FeeBO getFee() {
        return fee;
    }

    protected void setFeeLevelId(Short feeLevelId) {
        this.feeLevelId = feeLevelId;
    }

    protected void setLevelId(Short levelId) {
        this.levelId = levelId;
    }

    protected void setFee(FeeBO fee) {
        this.fee = fee;
    }

}
