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
package org.mifos.test.acceptance.framework.account;

public enum AccountStatus {

    LOAN_PARTIAL ("Partial Application", 1),
    LOAN_PENDING_APPROVAL ("Application Pending Approval", 2),
    LOAN_APPROVED("Application Approved", 3),
    LOAN_CLOSED_WRITTENOFF ("Closed- Written Off", 7),
    LOAN_CLOSED_RESHEDULED ("Closed- Rescheduled", 8),
    LOAN_CANCEL ("Cancel", 10),
    SAVINGS_PARTIAL ("Partial Application", 13),
    SAVINGS_PENDING_APPROVAL ("Application Pending Approval", 14),
    SAVINGS_CANCEL ("Cancel", 15),
    SAVINGS_ACTIVE ("Active", 16),
    SAVINGS_INACTIVE ("Inactive", 18);

    private final String statusText;
    private final Integer id;

    private AccountStatus(String statusText, Integer id) {
        this.statusText = statusText;
        this.id = id;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public Integer getId() {
        return this.id;
    }
}
