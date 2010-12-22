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

package org.mifos.dto.domain;

@SuppressWarnings("PMD")
public class CreateLoanRequest {

    private final Integer centerId;
    private final Short loanProductId;
    private final Short accountStateId;
    private final Integer clientId;
    private final String loanAmount;
    private final Short defaultNoOfInstall;
    private final String maxLoanAmount;
    private final String minLoanAmount;
    private final Short maxNoOfInstall;
    private final Short minNoOfInstall;
    private final Integer loanPurpose;

    public CreateLoanRequest(Integer centerId, Short loanProductId, Short accountStateId, Integer clientId,
            String loanAmount, Short defaultNoOfInstall, String maxLoanAmount, String minLoanAmount, Short maxNoOfInstall,
            Short minNoOfInstall, Integer loanPurpose) {
                this.centerId = centerId;
                this.loanProductId = loanProductId;
                this.accountStateId = accountStateId;
                this.clientId = clientId;
                this.loanAmount = loanAmount;
                this.defaultNoOfInstall = defaultNoOfInstall;
                this.maxLoanAmount = maxLoanAmount;
                this.minLoanAmount = minLoanAmount;
                this.maxNoOfInstall = maxNoOfInstall;
                this.minNoOfInstall = minNoOfInstall;
                this.loanPurpose = loanPurpose;
    }

    public Integer getCenterId() {
        return this.centerId;
    }

    public Short getLoanProductId() {
        return this.loanProductId;
    }

    public Short getAccountStateId() {
        return this.accountStateId;
    }

    public Integer getClientId() {
        return this.clientId;
    }

    public String getLoanAmount() {
        return this.loanAmount;
    }

    public Short getDefaultNoOfInstall() {
        return this.defaultNoOfInstall;
    }

    public String getMaxLoanAmount() {
        return this.maxLoanAmount;
    }

    public String getMinLoanAmount() {
        return this.minLoanAmount;
    }

    public Short getMaxNoOfInstall() {
        return this.maxNoOfInstall;
    }

    public Short getMinNoOfInstall() {
        return this.minNoOfInstall;
    }

    public Integer getLoanPurpose() {
        return this.loanPurpose;
    }
}