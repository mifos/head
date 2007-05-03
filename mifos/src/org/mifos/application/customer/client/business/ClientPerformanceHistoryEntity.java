package org.mifos.application.customer.client.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerPerformanceHistory;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.util.helpers.Money;

public class ClientPerformanceHistoryEntity extends CustomerPerformanceHistory {

	private final Integer id;

	private Set<LoanCounter> loanCounters;

	private Integer noOfActiveLoans;

	private Money lastLoanAmount;

	private Money delinquentPortfolio;

	private Money totalSavings;

	private final ClientBO client;

	public ClientPerformanceHistoryEntity(ClientBO client) {
		super();
		this.id = null;
		this.client = client;
		this.loanCounters = new HashSet<LoanCounter>();
		this.noOfActiveLoans = 0;
		this.lastLoanAmount = new Money();
		this.delinquentPortfolio = new Money();
		this.totalSavings = new Money();
	}

	protected ClientPerformanceHistoryEntity() {
		super();
		this.id = null;
		this.client = null;
		this.loanCounters = new HashSet<LoanCounter>();
		this.noOfActiveLoans = 0;
		this.lastLoanAmount = null;
		this.delinquentPortfolio = null;
		this.totalSavings = null;
	}

	public Integer getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private Money getDelinquentPortfolio() {
		return delinquentPortfolio;
	}

	@SuppressWarnings("unused")
	private void setDelinquentPortfolio(Money delinquentPortfolio) {
		this.delinquentPortfolio = delinquentPortfolio;
	}

	public Money getLastLoanAmount() {
		return lastLoanAmount;
	}

	public void setLastLoanAmount(Money lastLoanAmount) {
		this.lastLoanAmount = lastLoanAmount;
	}

	public Set<LoanCounter> getLoanCounters() {
		return loanCounters;
	}
	
	public void setLoanCounters(Set<LoanCounter> loanCounters) {
		this.loanCounters = loanCounters;
	}

	private void addLoanCounter(LoanCounter loanCounter) {
		this.loanCounters.add(loanCounter);
	}

	public Integer getNoOfActiveLoans() {
		return noOfActiveLoans;
	}

	public void setNoOfActiveLoans(Integer noOfActiveLoans) {
		this.noOfActiveLoans = noOfActiveLoans;
	}

	@SuppressWarnings("unused")
	private Money getTotalSavings() {
		return totalSavings;
	}

	public Money getTotalSavingsAmount() {
		return getClient().getSavingsBalance();
	}

	@SuppressWarnings("unused")
	private void setTotalSavings(Money totalSavings) {
		this.totalSavings = totalSavings;
	}

	public ClientBO getClient() {
		return client;
	}

	public void updateLoanCounter(LoanOfferingBO loanOfferingBO,
			YesNoFlag counterFlag) {
		boolean isCounterUpdated = false;
		if (loanCounters == null)
			loanCounters = new HashSet<LoanCounter>();
		for (LoanCounter loanCount : loanCounters) {
			if (loanCount.getLoanOffering().getPrdOfferingId().equals(
					loanOfferingBO.getPrdOfferingId())) {
				loanCount.updateLoanCounter(counterFlag);
				isCounterUpdated = true;
			}
		}
		if (!isCounterUpdated) {
			LoanCounter loanCounter = new LoanCounter(this, loanOfferingBO,
					counterFlag);
			addLoanCounter(loanCounter);
		}
	}

	public Integer getLoanCycleNumber() {
		Integer loanCount = 0;
		for (LoanCounter loanCounter : getLoanCounters()) {
			if (loanCounter.getLoanOffering().isIncludeInLoanCounter())
				loanCount += loanCounter.getLoanCycleCounter();
		}
		if(client.getHistoricalData() != null){
			if(client.getHistoricalData().getLoanCycleNumber() == null)
				client.getHistoricalData().setLoanCycleNumber(0);
			loanCount += client.getHistoricalData().getLoanCycleNumber();
		}
		return loanCount;
	}

	public Money getDelinquentPortfolioAmount() {
		Money amountOverDue = new Money();
		Money totalOutStandingAmount = new Money();
		for (AccountBO accountBO : client.getAccounts()) {
			if (accountBO.getType() == AccountTypes.LOAN_ACCOUNT
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
