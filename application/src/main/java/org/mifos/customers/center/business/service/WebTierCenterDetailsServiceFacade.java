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

package org.mifos.customers.center.business.service;

import org.mifos.application.master.MessageLookup;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.business.CenterPerformanceHistory;
import org.mifos.framework.exceptions.ServiceException;

/**
 *
 */
public class WebTierCenterDetailsServiceFacade implements CenterDetailsServiceFacade {

    CustomerBusinessService customerBusinessService;
    /* (non-Javadoc)
     * @see org.mifos.customers.client.business.service.ClientDetailsServiceFacade#getClientInformationDto(java.lang.String, java.lang.Short)
     */
    @Override
    public CenterInformationDto getCenterInformationDto(String globalCustNum, Short levelId) throws ServiceException {
        CenterBO center = (CenterBO)getCustomerBusinessService().findBySystemId(globalCustNum, levelId);
        if (center == null) {
            throw new MifosRuntimeException("Client not found for globalCustNum, levelId: " + globalCustNum + "," + levelId);
        }

        CenterPerformanceHistory centerPerformanceHistory = customerBusinessService.getCenterPerformanceHistory(
                center.getSearchId(), center.getOffice().getOfficeId());

        CenterInformationDto clientInformationDto = new CenterInformationDto(centerPerformanceHistory.getNumberOfGroups(),
                centerPerformanceHistory.getNumberOfClients(), centerPerformanceHistory.getTotalOutstandingPortfolio(),
                centerPerformanceHistory.getTotalSavings(), centerPerformanceHistory.getPortfolioAtRisk(), center.getDisplayName());
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
