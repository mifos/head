package org.mifos.application.accounts.business;

import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class ViewInstallmentDetails extends View{
	
	private Money principal;
	private Money interest;
	private Money fees;
	private Money penalty;
	private Money subTotal;	
	
	public ViewInstallmentDetails(Money principal, Money interest, Money fees, Money penalty){
		this.principal = principal;
		this.interest = interest;
		this.fees = fees;
		this.penalty = penalty;	
	}
	
	public Money getFees() {
		return fees;
	}
	private void setFees(Money fees) {
		this.fees = fees;
	}
	public Money getInterest() {
		return interest;
	}
	private void setInterest(Money interest) {
		this.interest = interest;
	}
	public Money getPenalty() {
		return penalty;
	}
	private void setPenalty(Money penalty) {
		this.penalty = penalty;
	}
	public Money getPrincipal() {
		return principal;
	}
	private void setPrincipal(Money principal) {
		this.principal = principal;
	}
	public Money getSubTotal() {
		this.subTotal = new Money();
		this.subTotal=this.subTotal.add(this.principal).add(this.interest).add(this.fees).add(this.penalty);
		return this.subTotal;
	}
	
}
