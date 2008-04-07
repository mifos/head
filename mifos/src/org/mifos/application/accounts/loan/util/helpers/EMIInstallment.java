package org.mifos.application.accounts.loan.util.helpers;

import org.mifos.framework.util.helpers.Money;


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
