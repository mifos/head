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

package org.mifos.spi;

import java.io.BufferedReader;
import java.util.List;

import org.mifos.api.accounts.AccountService;
import org.mifos.api.accounts.UserReferenceDto;

/**
 * Service Provider Interface (SPI) for importing bank transactions.
 */
public abstract class TransactionImport {
    private AccountService accountService;
    private UserReferenceDto userReferenceDTO;

    /**
     * Parse transaction import data and return errors encountered or an empty
     * list.
     * 
     * @return error messages, if any, but never <code>null</code>
     */
    public abstract List<String> parseTransactions(final BufferedReader input);

    /**
     * @return friendly name for this implementation
     */
    public abstract String getDisplayName();

    /**
     * Mifos will call this method to provide an {@link AccountService} for use
     * by the import plugin prior to calling parseTransactions.
     */
    public void setAccountService(final AccountService accountService) {
        this.accountService = accountService;
    }
    
    protected AccountService getAccountService() {
        return accountService;
    }

    /**
     * Mifos will call this method to provide a {@link UserReferenceDto} for use
     * by the import plugin prior to calling parseTransactions.
     */
    public void setUserReferenceDTO(final UserReferenceDto userReferenceDTO) {
        this.userReferenceDTO = userReferenceDTO;
    }
    
    protected UserReferenceDto getUserReferenceDTO() {
        return userReferenceDTO;
    }
}
