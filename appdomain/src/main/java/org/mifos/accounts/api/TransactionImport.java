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

package org.mifos.accounts.api;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.dto.domain.AccountTrxDto;
import org.mifos.dto.domain.ParseResultDto;
import org.mifos.dto.domain.UserReferenceDto;

/**
 * Service Provider Interface (SPI) for importing bank transactions.
 */
public abstract class TransactionImport {
    private AccountService accountService;
    private CustomerSearchService customerSearchService;
    private UserReferenceDto userReferenceDto;
    private AccountServiceFacade accountServiceFacade;

    /**
     * Parses transaction import data and return an object encapsulating parse
     * results. This method shall not write to the database, e.g., shall not
     * call {@link AccountService#makePayments(java.util.List)}.
     *
     * @return initialized {@link ParseResultDto} object. Never
     *         <code>null</code>.
     */
    public abstract ParseResultDto parse(final InputStream input);

    /**
     * Parses transaction import data and call the API to transactions in the
     * database. This method shall write to the database, e.g., may call
     * {@link AccountService#makePayments(java.util.List)}.
     */
    public abstract void store(final InputStream input) throws Exception;
    
    /**
     * Method created for undo full payments import used by Audi Plugin
     * Parses transaction import data and call the API to transactions in the
     * database. This method shall write to the database, e.g., may call
     * {@link AccountService#makePayments(java.util.List)}.
     */
    public abstract List<AccountTrxDto> storeForUndoImport(final InputStream input) throws Exception;

    /**
     * @return friendly name for this implementation
     */
    public abstract String getDisplayName();


    /**
     * @return properties to display in Admin "miscellaneous" section
     */
    public Map<String,String> getPropertiesForAdminDisplay() {
            return new HashMap<String, String>();
    }

    /**
     * Mifos will call this method to provide an {@link AccountService} for use
     * by the import plugin prior to calling {@link #parse} and {@link #store}.
     */
    public void setAccountService(final AccountService accountService) {
        this.accountService = accountService;
    }

    protected AccountService getAccountService() {
        return accountService;
    }

    /**
     * Injected by Mifos.
     */
    public void setCustomerSearchService(final CustomerSearchService customerSearchService) {
        this.customerSearchService = customerSearchService;
    }

    protected CustomerSearchService getCustomerSearchService() {
        return customerSearchService;
    }

    /**
     * Mifos will call this method to provide a {@link UserReferenceDto} for use
     * by the import plugin prior to calling {@link #parse} and {@link #store}.
     */
    public void setUserReferenceDto(final UserReferenceDto userReferenceDto) {
        this.userReferenceDto = userReferenceDto;
    }

    /**
     * Represents the user in the Mifos system responsible for creating the
     * transactions.
     */
    protected UserReferenceDto getUserReferenceDto() {
        return userReferenceDto;
    }

    /**
     * Expected number of transactions per import file row.
     *
     * See <a href="http://mifosforge.jira.com/browse/MIFOS-2909">MIFOS-2909</a>.
     *
     * @return numberOfTransactionsPerRow an integer
     */
    public int getSuccessfullyParsedRows() {
        return -1;
    }

    public AccountServiceFacade getAccountServiceFacade() {
        return accountServiceFacade;
    }

    public void setAccountServiceFacade(final AccountServiceFacade accountServiceFacade) {
        this.accountServiceFacade = accountServiceFacade;
    }
    
}
