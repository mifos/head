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
package org.mifos.customers.util.helpers;

import org.mifos.framework.business.service.DataTransferObject;

/**
 *
 */
public class CenterPerformanceHistoryDto implements DataTransferObject {

    private final Integer numberOfGroups;
    private final Integer numberOfClients;
    private final String totalOutstandingPortfolio;
    private final String totalSavings;
    private final String portfolioAtRisk;

    public CenterPerformanceHistoryDto(final Integer numberOfGroups, final Integer numberOfClients,
            final String totalOutstandingPortfolio, final String totalSavings, final String portfolioAtRisk) {
        this.numberOfGroups = numberOfGroups;
        this.numberOfClients = numberOfClients;
        this.totalOutstandingPortfolio = totalOutstandingPortfolio;
        this.totalSavings = totalSavings;
        this.portfolioAtRisk = portfolioAtRisk;
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

}
