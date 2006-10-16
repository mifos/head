package org.mifos.application.customer.center.business;

import org.mifos.framework.util.helpers.Money;

public class CenterPerformanceHistory {
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
