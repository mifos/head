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

package org.mifos.application.customer.center.business;

import java.io.Serializable;

import org.mifos.framework.util.helpers.Money;

public class CenterPerformanceHistory implements Serializable {

    private final Integer numberOfGroups;

    private final Integer numberOfClients;

    private final Money totalOutstandingPortfolio;

    private final Money totalSavings;

    private final Money portfolioAtRisk;

    public CenterPerformanceHistory(Integer numberOfGroups, Integer numberOfClients, Money totalOutstandingPortfolio,
            Money totalSavings, Money portfolioAtRisk) {
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

    public Money getTotalOutstandingPortfolio() {
        return totalOutstandingPortfolio;
    }

    public Money getTotalSavings() {
        return totalSavings;
    }

    public Money getPortfolioAtRisk() {
        return portfolioAtRisk;
    }
}
