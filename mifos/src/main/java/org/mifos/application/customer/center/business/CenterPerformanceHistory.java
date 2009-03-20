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
	private Integer numberOfGroups;

	private Integer numberOfClients;

	private Money totalOutstandingPortfolio;

	private Money totalSavings;

	private Money portfolioAtRisk;

	public CenterPerformanceHistory(){	}
	
	public Integer getNumberOfClients() {
		return numberOfClients;
	}

	void setNumberOfClients(Integer numberOfClients) {
		this.numberOfClients = numberOfClients;
	}

	public Integer getNumberOfGroups() {
		return numberOfGroups;
	}

	void setNumberOfGroups(Integer numberOfGroups) {
		this.numberOfGroups = numberOfGroups;
	}

	public Money getTotalOutstandingPortfolio() {
		return totalOutstandingPortfolio;
	}

	void setTotalOutstandingPortfolio(Money totalOutstandingPortfolio) {
		this.totalOutstandingPortfolio = totalOutstandingPortfolio;
	}

	public Money getTotalSavings() {
		return totalSavings;
	}

	void setTotalSavings(Money totalSavings) {
		this.totalSavings = totalSavings;
	}

	public Money getPortfolioAtRisk() {
		return portfolioAtRisk;
	}

	void setPortfolioAtRisk(Money portfolioAtRisk) {
		this.portfolioAtRisk = portfolioAtRisk;
	}

	public void setPerformanceHistoryDetails(Integer numberOfGroups,
			Integer numberOfClients, Money totalOutstandingPortfolio,
			Money totalSavings, Money portfolioAtRisk) {
		setNumberOfGroups(numberOfGroups);
		setNumberOfClients(numberOfClients);
		setTotalOutstandingPortfolio(totalOutstandingPortfolio);
		setTotalSavings(totalSavings);
		setPortfolioAtRisk(portfolioAtRisk);

	}

}
