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

package org.mifos.dto.screen;

import org.joda.time.LocalDate;

public class ClosedAccountDto {

    private final Integer accountId;
    private final String globalAccountNum;
    private final Integer accountType;
    private final Integer accountState;
    private final LocalDate closedDate;

    public ClosedAccountDto(Integer accountId, String globalAccountNum, Integer accountType, Integer accountState, LocalDate closedDate) {
        this.accountId = accountId;
        this.globalAccountNum = globalAccountNum;
        this.accountType = accountType;
        this.accountState = accountState;
        this.closedDate = closedDate;
    }

    public Integer getAccountId() {
        return this.accountId;
    }

    public String getGlobalAccountNum() {
        return this.globalAccountNum;
    }

    public Integer getAccountType() {
        return this.accountType;
    }

    public Integer getAccountState() {
        return this.accountState;
    }

    public LocalDate getClosedDate() {
        return this.closedDate;
    }
}