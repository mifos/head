package org.mifos.application.customer.client.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class ClientPerformanceHistoryEntity extends PersistentObject {

	private Integer customerId;

	private Integer loanCycleNumber;

	private Integer noOfActiveLoans;

	private Money lastLoanAmount;

	private Money delinquentPortfolio;

	private Money totalSavings;

	private ClientBO client;

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
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

	public ClientBO getClient() {
		return client;
	}

	public void setClient(ClientBO client) {
		this.client = client;
	}

	public void setPerformanceHistoryDetails(Integer loanCycleNumber,
			Integer noOfActiveLoans, Money lastLoanAmount,
			Money delinquentPortfolio, Money totalSavings) {
		this.loanCycleNumber = loanCycleNumber;
		this.noOfActiveLoans = noOfActiveLoans;
		this.lastLoanAmount = lastLoanAmount;
		this.delinquentPortfolio = delinquentPortfolio;
		this.totalSavings = totalSavings;
	}
}
