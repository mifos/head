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

package org.mifos.customers.client.business.service;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.application.master.MessageLookup;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class WebTierClientDetailsServiceFacade implements ClientDetailsServiceFacade {

    CustomerBusinessService customerBusinessService;
    /* (non-Javadoc)
     * @see org.mifos.customers.client.business.service.ClientDetailsServiceFacade#getClientInformationDto(java.lang.String, java.lang.Short)
     */
    @Override
    public ClientInformationDto getClientInformationDto(String globalCustNum, Short levelId) throws ServiceException {
        ClientBO client = (ClientBO)getCustomerBusinessService().findBySystemId(globalCustNum, levelId);
        if (client == null) {
            throw new MifosRuntimeException("Client not found for globalCustNum, levelId: " + globalCustNum + "," + levelId);
        }
        String delinquentPortfolioAmountString;
        try {
            Money delinquentPortfolioAmount = client.getClientPerformanceHistory().getDelinquentPortfolioAmount();
            delinquentPortfolioAmountString = delinquentPortfolioAmount.toString();
        } catch(CurrencyMismatchException e) {
            delinquentPortfolioAmountString = localizedMessageLookup("errors.multipleCurrencies");
        }

        List<ClientLoanInformationDto> clientLoans = new ArrayList<ClientLoanInformationDto>();
        for (LoanBO loan : client.getOpenLoanAccounts()) {
            String totalAmountDueString;
            try {
                Money totalAmountDue = loan.getTotalAmountDue();
                totalAmountDueString = totalAmountDue.toString();
            } catch(CurrencyMismatchException e) {
                totalAmountDueString = localizedMessageLookup("errors.multipleCurrencies");
            }
            clientLoans.add(new ClientLoanInformationDto(totalAmountDueString, loan.getGlobalAccountNum(),
                loan.getLoanOffering().getPrdOfferingName(), loan.getAccountState().getId().toString(),
                loan.getAccountState().getName(), loan.getLoanSummary().getOutstandingBalance().toString()));
        }

        ClientInformationDto clientInformationDto = new ClientInformationDto(
                client.getCustomerStatus().getId(),
                client.getCustomerStatus().getName(), delinquentPortfolioAmountString,
                client.getDisplayName(), client.getGlobalCustNum(), clientLoans);
        return clientInformationDto;
    }

    public CustomerBusinessService getCustomerBusinessService() {
        if (customerBusinessService == null) {
            customerBusinessService = new CustomerBusinessService();
        }
        return this.customerBusinessService;
    }

    // allow for service injection
    public void setCustomerBusinessService(CustomerBusinessService customerBusinessService) {
        this.customerBusinessService = customerBusinessService;
    }

    protected String localizedMessageLookup(String key) {
        return MessageLookup.getInstance().lookup(key);
    }


}
