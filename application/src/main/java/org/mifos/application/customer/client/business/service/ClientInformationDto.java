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

package org.mifos.application.customer.client.business.service;

import org.mifos.framework.business.service.DataTransferObject;

/**
 * Initial data transfer object to hold data for display 
 * on the viewClientDetails page.
 *
 */
public class ClientInformationDto implements DataTransferObject {
    private final String delinquentPortfolioAmount;
    private final String displayName;

    
    public ClientInformationDto(String delinquentPortfolioAmount, String displayName) {
        this.delinquentPortfolioAmount = delinquentPortfolioAmount;
        this.displayName = displayName;
    }

    public String getDelinquentPortfolioAmount() {
        return this.delinquentPortfolioAmount;
    }

    public String getDisplayName() {
        return this.displayName;
    }        
        
}
