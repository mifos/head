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

package org.mifos.application.master.business;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FUND_CODE")
public class FundCodeEntity implements Serializable {

    private Short fundCodeId;

    private String fundCodeValue;

    public FundCodeEntity(String fundCode) {
        this.fundCodeValue = fundCode;
    }

    protected FundCodeEntity() { }

    @Id
    @GeneratedValue
    @Column(name = "FUNDCODE_ID", nullable = false)
    public Short getFundCodeId() {
        return fundCodeId;
    }

    @Column(name = "FUNDCODE_VALUE")
    public String getFundCodeValue() {
        return fundCodeValue;
    }

    public void setFundCodeId(Short fundCodeId) {
        this.fundCodeId = fundCodeId;
    }

    public void setFundCodeValue(String fundCode) {
        this.fundCodeValue = fundCode;
    }
}
