package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class SavingsAccountView extends View {

	private Integer accountId;

	private String depositAmountEntered;

	private String withDrawalAmountEntered;

	private List<AccountActionDateEntity> accountTrxnDetails;

	private Short accountType;

	private SavingsOfferingBO savingsOffering;

	private boolean isValidDepositAmountEntered;

	private boolean isValidWithDrawalAmountEntered;

	public SavingsAccountView(Integer accountId, Short accountType,
			SavingsOfferingBO savingsOffering) {
		this.accountId = accountId;
		this.accountType = accountType;
		this.savingsOffering = savingsOffering;
		accountTrxnDetails = new ArrayList<AccountActionDateEntity>();
		isValidDepositAmountEntered = true;
		isValidWithDrawalAmountEntered = true;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public List<AccountActionDateEntity> getAccountTrxnDetails() {
		return accountTrxnDetails;
	}

	public void addAccountTrxnDetail(AccountActionDateEntity accountTrxnDetail) {
		this.accountTrxnDetails.add(accountTrxnDetail);
	}

	public Short getAccountType() {
		return accountType;
	}

	public String getDepositAmountEntered() {
		return depositAmountEntered;
	}

	public void setDepositAmountEntered(String depositAmountEntered) {
		this.depositAmountEntered = depositAmountEntered;
	}

	public SavingsOfferingBO getSavingsOffering() {
		return savingsOffering;
	}

	public String getWithDrawalAmountEntered() {
		return withDrawalAmountEntered;
	}

	public void setWithDrawalAmountEntered(String withDrawalAmountEntered) {
		this.withDrawalAmountEntered = withDrawalAmountEntered;
	}

	public boolean isValidDepositAmountEntered() {
		return isValidDepositAmountEntered;
	}

	public void setValidDepositAmountEntered(boolean isValidDepositAmountEntered) {
		this.isValidDepositAmountEntered = isValidDepositAmountEntered;
	}

	public boolean isValidWithDrawalAmountEntered() {
		return isValidWithDrawalAmountEntered;
	}

	public void setValidWithDrawalAmountEntered(
			boolean isValidWithDrawalAmountEntered) {
		this.isValidWithDrawalAmountEntered = isValidWithDrawalAmountEntered;
	}

	public Double getTotalDepositDue() {
		Money totalDepositDue = new Money();
		if (accountTrxnDetails != null && accountTrxnDetails.size() > 0) {
			for (AccountActionDateEntity actionDates : accountTrxnDetails) {
				totalDepositDue = totalDepositDue.add(actionDates.getTotalDepositDue());
			}
		}
		return totalDepositDue.getAmountDoubleValue();
	}

}
