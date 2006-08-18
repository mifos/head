package org.mifos.application.accounts.loan.util.helpers;

import org.mifos.framework.util.helpers.Money;


public class EMIInstallment
{

	private Money principal = new Money();
	private Money interest = new Money();
	

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
