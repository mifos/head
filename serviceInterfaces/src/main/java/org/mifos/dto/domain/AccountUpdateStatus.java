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

public class AccountUpdateStatus {

    private final Long savingsId;
    private final Short newStatusId;
    private final Short flagId;
    private final String comment;

    public AccountUpdateStatus(Long savingsId, Short newStatusId, Short flagId, String comment) {
        this.savingsId = savingsId;
        this.newStatusId = newStatusId;
        this.flagId = flagId;
        this.comment = comment;
    }

    public Long getSavingsId() {
        return this.savingsId;
    }

    public Short getNewStatusId() {
        return this.newStatusId;
    }

    public Short getFlagId() {
        return this.flagId;
    }

    public String getComment() {
        return this.comment;
    }

}