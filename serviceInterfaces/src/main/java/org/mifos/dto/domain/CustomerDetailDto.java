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

import java.io.Serializable;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="should disable at filter level and also for pmd - not important for us")
public class CustomerDetailDto implements Serializable {

    private Integer customerId;
    private String displayName;
    private String searchId;
    private String globalCustNum;
    private Short loanOfficerId;
    private String externalId;
    private AddressDto address;

    /*
     * requires public constructor as using hibernates aliasToBean transformer
     */
    public CustomerDetailDto() {
        // Default empty constructor for hibernate
        this.loanOfficerId = null;
        this.externalId = null;
        this.address = null;
    }

    public CustomerDetailDto(final Integer id, final String displayName, final String searchId, final String globalCustNum) {
        this.customerId = id;
        this.displayName = displayName;
        this.searchId = searchId;
        this.globalCustNum = globalCustNum;
        this.loanOfficerId = null;
        this.externalId = null;
        this.address = null;
    }

    public CustomerDetailDto(final Integer id, final String displayName, final String searchId,
            final String globalCustNum, Short loanOfficerId, String externalId, AddressDto address) {
        this.customerId = id;
        this.displayName = displayName;
        this.searchId = searchId;
        this.globalCustNum = globalCustNum;
        this.loanOfficerId = loanOfficerId;
        this.externalId = externalId;
        this.address = address;
    }

    public Integer getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSearchId() {
        return this.searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public String getGlobalCustNum() {
        return this.globalCustNum;
    }

    public void setGlobalCustNum(String globalCustNum) {
        this.globalCustNum = globalCustNum;
    }

    public Short getLoanOfficerId() {
        return this.loanOfficerId;
    }

    public String getLoanOfficerIdAsString() {
        if (this.loanOfficerId != null) {
            return this.loanOfficerId.toString();
        }
        return null;
    }

    public String getExternalId() {
        return this.externalId;
    }

    public AddressDto getAddress() {
        return this.address;
    }
}