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
package org.mifos.customers.util.helpers;

import java.util.Comparator;

import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.helpers.ChapterNum;

/**
 *
 */
public class CustomerDetailDto implements DataTransferObject {
    private Integer customerId;
    private String displayName;
    private String searchId;
    private String globalCustNum;

    public CustomerDetailDto() {
        // Default empty constructor for hibernate
    }

    public CustomerDetailDto(final Integer id, final String displayName, final String searchId, final String globalCustNum) {
        this.customerId = id;
        this.displayName = displayName;
        this.searchId = searchId;
        this.globalCustNum = globalCustNum;
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

    public static Comparator<CustomerDetailDto> searchIdComparator() {
        return new Comparator<CustomerDetailDto>() {
            public int compare(final CustomerDetailDto o1, final CustomerDetailDto o2) {
                return ChapterNum.compare(o1.getSearchId(), o2.getSearchId());
            }
        };
    }

}
