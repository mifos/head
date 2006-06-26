package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.List;

import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class CustomerAccountView extends View {

	private Integer accountId;

	private String customerAccountAmountEntered;

	private List<AccountActionDateEntity> accountActionDates;

	private boolean isValidCustomerAccountAmountEntered;

	public CustomerAccountView(Integer accountId) {
		this.accountId = accountId;
		customerAccountAmountEntered = "0.0";
		accountActionDates = new ArrayList<AccountActionDateEntity>();
		isValidCustomerAccountAmountEntered = true;
	}

	public List<AccountActionDateEntity> getAccountActionDates() {
		return accountActionDates;
	}

	public void setAccountActionDates(
			List<AccountActionDateEntity> accountActionDates) {
		this.accountActionDates = accountActionDates;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public String getCustomerAccountAmountEntered() {
		return customerAccountAmountEntered;
	}

	public void setCustomerAccountAmountEntered(
			String customerAccountAmountEntered) {
		this.customerAccountAmountEntered = customerAccountAmountEntered;
	}

	public boolean isValidCustomerAccountAmountEntered() {
		return isValidCustomerAccountAmountEntered;
	}

	public void setValidCustomerAccountAmountEntered(
			boolean isValidCustomerAccountAmountEntered) {
		this.isValidCustomerAccountAmountEntered = isValidCustomerAccountAmountEntered;
	}

	public Money getTotalAmountDue() {
		Money totalAmount = new Money();
		if (accountActionDates != null && accountActionDates.size() > 0)
			for (AccountActionDateEntity accountAction : accountActionDates)
				totalAmount = totalAmount.add(accountAction
						.getTotalDueWithFees());
		return totalAmount;
	}

}
