package org.mifos.application.acceptedpaymenttype.business;

import org.mifos.framework.business.PersistentObject;

public class TransactionTypeEntity extends PersistentObject{
	
	private Short transactionId;
	private String transactionName;
	
	
	public Short getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(Short transactionId) {
		this.transactionId = transactionId;
	}
	public String getTransactionName() {
		return transactionName;
	}
	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}
	
}
