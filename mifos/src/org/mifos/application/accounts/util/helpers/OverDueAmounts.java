/**
 * 
 */
package org.mifos.application.accounts.util.helpers;

import org.mifos.framework.util.helpers.Money;



/**
 * This class acts as wrapper class for overdue amounts.
 * @author ashishsm
 *
 */
public class OverDueAmounts  {
	private Money principalOverDue ;
	
	private Money interestOverdue ;
	
	private Money penaltyOverdue ;
	
	private Money feesOverdue ;

	private Money totalPrincipalPaid;

	public Money getFeesOverdue() {
		return feesOverdue;
	}

	public void setFeesOverdue(Money feesOverdue) {
		this.feesOverdue = feesOverdue;
	}

	public Money getInterestOverdue() {
		return interestOverdue;
	}

	public void setInterestOverdue(Money interestOverdue) {
		this.interestOverdue = interestOverdue;
	}

	public Money getPenaltyOverdue() {
		return penaltyOverdue;
	}

	public void setPenaltyOverdue(Money penaltyOverdue) {
		this.penaltyOverdue = penaltyOverdue;
	}

	public Money getPrincipalOverDue() {
		return principalOverDue;
	}

	public void setPrincipalOverDue(Money principalOverDue) {
		this.principalOverDue = principalOverDue;
	}

	public void setTotalPrincipalPaid(Money principalPaid) {
		this.totalPrincipalPaid = principalPaid;
		
	}

	public Money getTotalPrincipalPaid() {
		
		return this.totalPrincipalPaid;
	}

	public void add(OverDueAmounts dueAmounts) {
		this.principalOverDue = new Money().add(principalOverDue).add(dueAmounts.getPrincipalOverDue()) ;
		this.interestOverdue = new Money().add(interestOverdue).add(dueAmounts.getInterestOverdue());
		this.penaltyOverdue = new Money().add(penaltyOverdue).add(dueAmounts.getPenaltyOverdue());
		this.feesOverdue = new Money().add(feesOverdue).add(dueAmounts.getFeesOverdue());
				
	}
}
