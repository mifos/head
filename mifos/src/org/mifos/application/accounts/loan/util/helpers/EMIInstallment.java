package org.mifos.application.accounts.loan.util.helpers;

import org.mifos.framework.util.helpers.Money;

/*
 * What does EMI stand for?  Equal Monthly Installment,
 * Estimated Monthly Installment?  In any case this seems
 * like a poor name since this class is used for both 
 * weekly and monthly installments.
 */
public class EMIInstallment
{

	private Money principal;
	private Money interest;

	public EMIInstallment() {
		this(new Money(), new Money());
	}

	public EMIInstallment(Money principal, Money interest) {
		this.principal = principal;
		this.interest = interest;
	}

	public void setPrincipal(Money principal)
	{
		this.principal= principal;
	}

	public void setInterest(Money interest)
	{
		this.interest = interest;
	}

	public Money getInterest()
	{
		return interest;
	}

	public Money getPrincipal()
	{
		return principal;
	}
}
