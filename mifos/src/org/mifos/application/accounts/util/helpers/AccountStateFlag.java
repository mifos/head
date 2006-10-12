package org.mifos.application.accounts.util.helpers;

public enum AccountStateFlag {
	LOAN_WITHDRAW (Short.valueOf("1")), LOAN_REJECTED (Short.valueOf("2")),LOAN_OTHER (Short.valueOf("3")),
	SAVINGS_WITHDRAW (Short.valueOf("4")), SAVINGS_REJECTED (Short.valueOf("5")),SAVINGS_BLACKLISTED (Short.valueOf("6"));
	
	Short value;

	AccountStateFlag(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	
	public static AccountStateFlag getStatusFlag(Short value){
		for(AccountStateFlag statusFlag : AccountStateFlag.values())
			if(statusFlag.getValue().equals(value))
				return statusFlag;
		return null;
	}
}
