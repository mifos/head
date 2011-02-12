/*
 * Copyright Grameen Foundation USA
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

import org.joda.time.LocalDate;

public class SavingsStatusChangeHistoryDto {

    private final String personnelName;
    private final String newStatusName;
    private final String oldStatusName;
    private final String userPrefferedTransactionDate;
    private final LocalDate createdDate;

    public SavingsStatusChangeHistoryDto(String personnelName, String newStatusName, String oldStatusName,
            String userPrefferedTransactionDate, LocalDate createdDate) {
        this.personnelName = personnelName;
        this.newStatusName = newStatusName;
        this.oldStatusName = oldStatusName;
        this.userPrefferedTransactionDate = userPrefferedTransactionDate;
        this.createdDate = createdDate;
    }

    public String getPersonnelName() {
        return this.personnelName;
    }

    public String getNewStatusName() {
        return this.newStatusName;
    }

    public String getOldStatusName() {
        return this.oldStatusName;
    }

    public String getUserPrefferedTransactionDate() {
        return this.userPrefferedTransactionDate;
    }

    public LocalDate getCreatedDate() {
        return this.createdDate;
    }
}