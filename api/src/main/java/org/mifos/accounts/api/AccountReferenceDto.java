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

package org.mifos.accounts.api;

/**
 * The Class AccountReferenceDto is a Data Transfer Object
 * that holds a reference to an account.
 */
public class AccountReferenceDto {

    /** The account id. */
    private final int accountId;

    /**
     * Instantiates a new account reference dto.
     *
     * @param accountId the account id
     */
    public AccountReferenceDto(int accountId) {
        this.accountId = accountId;
    }

    /**
     * Gets the account id.
     *
     * @return the account id
     */
    public int getAccountId() {
        return this.accountId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.accountId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AccountReferenceDto other = (AccountReferenceDto) obj;
        if (this.accountId != other.accountId) {
            return false;
        }
        return true;
    }
}
