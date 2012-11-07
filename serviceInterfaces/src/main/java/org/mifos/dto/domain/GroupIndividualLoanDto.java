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

import java.math.BigDecimal;

public class GroupIndividualLoanDto implements Comparable<GroupIndividualLoanDto>{
    private final String globalAccountNum;
    private final BigDecimal defaultAmount;
    private final Integer accountId;
    
    
    public GroupIndividualLoanDto(String globalAccountNum, BigDecimal defaultAmount, Integer accountId) {
        this.globalAccountNum = globalAccountNum;
        this.defaultAmount = defaultAmount;
        this.accountId = accountId;
    }


    public String getGlobalAccountNum() {
        return globalAccountNum;
    }


    public BigDecimal getDefaultAmount() {
        return defaultAmount;
    }


    public Integer getAccountId() {
        return accountId;
    }

    @Override
    public int compareTo(GroupIndividualLoanDto o) {
        return this.getAccountId().compareTo(o.getAccountId());
    }
    
    @Override
    public boolean equals(Object obj) {
        return (this == obj);
    }
    
    @Override
    public int hashCode() {
        return Integer.valueOf(accountId).hashCode();
    }
}
