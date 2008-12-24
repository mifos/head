package org.mifos.application.accounts.util.helpers;

public enum AccountStateFlag {
	LOAN_WITHDRAW (1), 
	LOAN_REJECTED (2),
	LOAN_OTHER (3),
	SAVINGS_WITHDRAW (4), 
	SAVINGS_REJECTED (5),
	SAVINGS_BLACKLISTED (6),
	LOAN_REVERSAL (7);
	
	Short value;

	private AccountStateFlag(int value) {
		this.value = (short)value;
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
