package org.mifos.application.util.helpers;

public enum TrxnTypes {
	
	loan_disbursement (Short.valueOf("1")), loan_repayment(Short.valueOf("2")), 
	savings_deposit(Short.valueOf("3")), savings_withdrawal(Short.valueOf("4")), fee(Short.valueOf("5"));
	
	Short value;

	TrxnTypes(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
