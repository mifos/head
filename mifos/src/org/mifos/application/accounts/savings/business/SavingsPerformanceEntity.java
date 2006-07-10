package org.mifos.application.accounts.savings.business;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class SavingsPerformanceEntity extends PersistentObject {
	private Integer accountId;
    private Money totalDeposits;
    private Money totalWithdrawals;
    private Money totalInterestEarned;
    private Integer missedDeposits;
    private SavingsBO savings;
    
    
    public SavingsBO getSavings() {
		return savings;
	}

	public void setSavings(SavingsBO savings) {
		this.savings = savings;
	}

	public SavingsPerformanceEntity(){
       
	}

	public Integer getMissedDeposits() {
		return missedDeposits;
	}

	public void setMissedDeposits(Integer missedDeposits) {
		this.missedDeposits = missedDeposits;
	}

	public Money getTotalDeposits() {
		return totalDeposits;
	}

	public void setTotalDeposits(Money totalDeposits) {
		this.totalDeposits = totalDeposits;
	}

	public Money getTotalInterestEarned() {
		return totalInterestEarned;
	}

	public void setTotalInterestEarned(Money totalInterstEarned) {
		this.totalInterestEarned = totalInterstEarned;
	}

	public Money getTotalWithdrawals() {
		return totalWithdrawals;
	}

	public void setTotalWithdrawals(Money totalWithdrawals) {
		this.totalWithdrawals = totalWithdrawals;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	
	public void setPaymentDetails(Money totalAmount) {
		if(totalDeposits==null)
			totalDeposits = new Money();
		totalDeposits = totalDeposits.add(totalAmount);
	}

	public void setWithdrawDetails(Money totalAmount) {
		if(totalWithdrawals==null)
			totalWithdrawals = new Money();
		totalWithdrawals = totalWithdrawals.add(totalAmount);
	}
	
	public void setTotalInterestDetails(Money totalAmount) {
		if(totalInterestEarned==null)
			totalInterestEarned = new Money();
		totalInterestEarned = totalInterestEarned.add(totalAmount);
	}

	
}
