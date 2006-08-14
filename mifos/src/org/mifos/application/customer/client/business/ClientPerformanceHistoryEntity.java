package org.mifos.application.customer.client.business;

import org.mifos.application.customer.business.CustomerPerformanceHistory;
import org.mifos.framework.util.helpers.Money;

public class ClientPerformanceHistoryEntity extends CustomerPerformanceHistory {

	private final Integer id;

	private Integer loanCycleNumber;

	private Integer noOfActiveLoans;

	private Money lastLoanAmount;

	private Money delinquentPortfolio;

	private Money totalSavings;

	private final ClientBO client;

	protected ClientPerformanceHistoryEntity() {
		super();
		this.id = null;
		this.client = null;
		this.loanCycleNumber = 0;
		this.noOfActiveLoans = 0;
		this.lastLoanAmount = null;
		this.delinquentPortfolio = null;
		this.totalSavings = null;
	}
	
	public ClientPerformanceHistoryEntity(ClientBO client) {
		super();
		this.id = null;
		this.client = client;
		this.loanCycleNumber = 0;
		this.noOfActiveLoans = 0;
		this.lastLoanAmount = new Money();
		this.delinquentPortfolio = new Money();
		this.totalSavings = new Money();
	}

	public ClientPerformanceHistoryEntity(ClientBO client,
			Integer loanCycleNumber, Integer noOfActiveLoans,
			Money lastLoanAmount, Money delinquentPortfolio, Money totalSavings) {
		super();
		this.id = null;
		this.client = client;
		this.loanCycleNumber = loanCycleNumber;
		this.noOfActiveLoans = noOfActiveLoans;
		this.lastLoanAmount = lastLoanAmount;
		this.delinquentPortfolio = delinquentPortfolio;
		this.totalSavings = totalSavings;
	}

	public Integer getId() {
		return id;
	}

	private Money getDelinquentPortfolio() {
		return delinquentPortfolio;
	}

	public Money getDelinquentPortfolioAmount() {
		if (getClient() != null)
			return getClient().getDelinquentPortfolioAmount();
		return new Money();
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

	private Money getTotalSavings() {
		return totalSavings;
	}

	public Money getTotalSavingsAmount() {
		if (getClient() != null)
			return getClient().getSavingsBalance();
		return new Money();
	}

	private void setTotalSavings(Money totalSavings) {
		this.totalSavings = totalSavings;
	}

	public ClientBO getClient() {
		return client;
	}
}
