package org.mifos.application.customer.client.business;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountTypes;
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
	
	public Integer getId() {
		return id;
	}

	private Money getDelinquentPortfolio() {
		return delinquentPortfolio;
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
		return getClient().getSavingsBalance();
	}

	private void setTotalSavings(Money totalSavings) {
		this.totalSavings = totalSavings;
	}

	public ClientBO getClient() {
		return client;
	}
	
	public Money getDelinquentPortfolioAmount() {
		Money amountOverDue = new Money();
		Money totalOutStandingAmount = new Money();
		for (AccountBO accountBO : client.getAccounts()) {
			if (accountBO.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue())
					&& ((LoanBO) accountBO).isAccountActive()) {
				amountOverDue = amountOverDue.add(((LoanBO) accountBO)
						.getTotalPrincipalAmountInArrears());
				totalOutStandingAmount = totalOutStandingAmount
						.add(((LoanBO) accountBO).getLoanSummary()
								.getOriginalPrincipal());
			}
		}
		if (totalOutStandingAmount.getAmountDoubleValue() != 0.0)
			return new Money(String.valueOf(amountOverDue
					.getAmountDoubleValue()
					/ totalOutStandingAmount.getAmountDoubleValue()));
		return new Money();
	}
}
