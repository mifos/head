package org.mifos.application.customer.client.util.valueobjects;

import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

public class ClientPerformanceHistory extends ValueObject {

	private Integer id;

	private Integer loanCycleNumber;

	private Integer noOfActiveLoans;

	private Money lastLoanAmount;

	private Money delinquentPortfolio;

	private Money totalSavings;

	private Client client;
	
	public ClientPerformanceHistory() {}
	
	public ClientPerformanceHistory(Integer loanCycleNumber,Integer noOfActiveLoans,Money lastLoanAmount,Money delinquentPortfolio,Money totalSavings) {
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

	public Money getDelinquentPortfolio() {
		return delinquentPortfolio;
	}

	public void setDelinquentPortfolio(Money delinquentPortfolio) {
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

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}
