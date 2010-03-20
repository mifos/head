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

import org.mifos.framework.business.service.DataTransferObject;

/**
 *
 */
public class CenterInformationDto implements DataTransferObject {
    private final Integer numberOfGroups;
    private final Integer numberOfClients;
    private final String totalOutstandingPortfolio;
    private final String totalSavings;
    private final String portfolioAtRisk;
    private final String displayName;

    public CenterInformationDto(Integer numberOfGroups, Integer numberOfClients, String totalOutstandingPortfolio,
            String totalSavings, String portfolioAtRisk, String displayName) {
        this.numberOfGroups = numberOfGroups;
        this.numberOfClients = numberOfClients;
        this.totalOutstandingPortfolio = totalOutstandingPortfolio;
        this.totalSavings = totalSavings;
        this.portfolioAtRisk = portfolioAtRisk;
        this.displayName = displayName;
    }

    public Integer getNumberOfClients() {
        return numberOfClients;
    }

    public Integer getNumberOfGroups() {
        return numberOfGroups;
    }

    public String getTotalOutstandingPortfolio() {
        return totalOutstandingPortfolio;
    }

    public String getTotalSavings() {
        return totalSavings;
    }

    public String getPortfolioAtRisk() {
        return portfolioAtRisk;
    }

    public String getDisplayName() {
        return displayName;
    }
}
