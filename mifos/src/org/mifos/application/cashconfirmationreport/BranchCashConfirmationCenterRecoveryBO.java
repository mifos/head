package org.mifos.application.cashconfirmationreport;

import org.mifos.framework.util.helpers.Money;

public class BranchCashConfirmationCenterRecoveryBO extends BranchCashConfirmationSubReport {
	
	private Integer recoveryId;
	private String productOffering;
	private Money due;
	private Money actual;
	private Money arrears;

	protected BranchCashConfirmationCenterRecoveryBO() {
	}

	public BranchCashConfirmationCenterRecoveryBO(String productOffering,
			Money due, Money actual, Money arrears) {
		this.productOffering = productOffering;
		this.due = due;
		this.actual = actual;
		this.arrears = arrears;
	}

	public Money getDue() {
		return due;
	}

	public String getProductOffering() {
		return productOffering;
	}

	public Money getActual() {
		return actual;
	}

	public Money getArrears() {
		return arrears;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((actual == null) ? 0 : actual.hashCode());
		result = PRIME * result + ((arrears == null) ? 0 : arrears.hashCode());
		result = PRIME * result + ((due == null) ? 0 : due.hashCode());
		result = PRIME * result + ((productOffering == null) ? 0 : productOffering.hashCode());
		result = PRIME * result + ((recoveryId == null) ? 0 : recoveryId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BranchCashConfirmationCenterRecoveryBO other = (BranchCashConfirmationCenterRecoveryBO) obj;
		if (actual == null) {
			if (other.actual != null)
				return false;
		}
		else if (!actual.equals(other.actual))
			return false;
		if (arrears == null) {
			if (other.arrears != null)
				return false;
		}
		else if (!arrears.equals(other.arrears))
			return false;
		if (due == null) {
			if (other.due != null)
				return false;
		}
		else if (!due.equals(other.due))
			return false;
		if (productOffering == null) {
			if (other.productOffering != null)
				return false;
		}
		else if (!productOffering.equals(other.productOffering))
			return false;
		if (recoveryId == null) {
			if (other.recoveryId != null)
				return false;
		}
		else if (!recoveryId.equals(other.recoveryId))
			return false;
		return true;
	}
}
