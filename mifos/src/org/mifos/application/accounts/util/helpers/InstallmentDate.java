package org.mifos.application.accounts.util.helpers;

import java.util.Date;

public class InstallmentDate
{

	private Date installmentDueDate = null;
	private Short installmentId;

	private InstallmentDate(){}
	public InstallmentDate(Short installmentId , Date installmentDueDate)
	{
		this.installmentId = installmentId;
		this.installmentDueDate = installmentDueDate;
	}

	public void setInstallmentId(Short installmentId)
	{
		this.installmentId = installmentId;
	}

	public Short getInstallmentId()
	{
		return installmentId;
	}

	public Date getInstallmentDueDate()
	{
		return installmentDueDate;
	}

}

