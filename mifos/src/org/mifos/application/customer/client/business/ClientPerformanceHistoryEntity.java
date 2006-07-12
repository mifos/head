package org.mifos.application.customer.client.business;


import org.mifos.application.customer.business.CustomerPerformanceHistory;
import org.mifos.framework.util.helpers.Money;

public class ClientPerformanceHistoryEntity extends CustomerPerformanceHistory{

	private Integer id;

	private Integer loanCycleNumber;

	private Integer noOfActiveLoans;

	private Money lastLoanAmount;

	private Money delinquentPortfolio;

	private Money totalSavings;

	private ClientBO client;
	
	public ClientPerformanceHistoryEntity() {}
	
	public ClientPerformanceHistoryEntity(Integer loanCycleNumber,Integer noOfActiveLoans,Money lastLoanAmount,Money delinquentPortfolio,Money totalSavings) {
		this.loanCycleNumber = loanCycleNumber;
		this.noOfActiveLoans = noOfActiveLoans;
		this.lastLoanAmount = lastLoanAmount;
		this.delinquentPortfolio = delinquentPortfolio;
		this.totalSavings = totalSavings;
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	private Money getDelinquentPortfolio() {
		return delinquentPortfolio;
	}
	
	public Money getDelinquentPortfolioAmount() {
		return getClient().getDelinquentPortfolioAmount();
	}

	private void setDelinquentPortfolio(Money delinquentPortfolio) {
		this.delinquentPortfolio = delinquentPortfolio;
	}

	public Money getLastLoanAmount() {
		return lastLoanAmount;
	}

	public void setLastLoanAmount(Money lastLoanAmount) {
		this.lastLoanAmount = lastLoanAmount;
	}

	public Integer getLoanCycleNumber() {
		return loanCycleNumber;
	}

	public void setLoanCycleNumber(Integer loanCycleNumber) {
		this.loanCycleNumber = loanCycleNumber;
	}

	public Integer getNoOfActiveLoans() {
		return noOfActiveLoans;
	}

	public void setNoOfActiveLoans(Integer noOfActiveLoans) {
		this.noOfActiveLoans = noOfActiveLoans;
	}

	public Money getTotalSavings() {
		return totalSavings;
	}

	public void setTotalSavings(Money totalSavings) {
		this.totalSavings = totalSavings;
	}

	public ClientBO getClient() {
		return client;
	}

	public void setClient(ClientBO client) {
		this.client = client;
	}
	

}
