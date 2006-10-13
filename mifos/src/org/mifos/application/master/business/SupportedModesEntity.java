package org.mifos.application.master.business;

import org.mifos.framework.business.PersistentObject;

public class SupportedModesEntity extends PersistentObject{
	
	private Short modeId ;
	private TransactionTypeEntity transactionTypeEntity ;
	private PaymentTypeEntity paymentTypeEntity ;
		
	public Short getModeId() {
		return modeId;
	}
	public void setModeId(Short modeId) {
		this.modeId = modeId;
	}
	public PaymentTypeEntity getPaymentTypeEntity() {
		return paymentTypeEntity;
	}
	public void setPaymentTypeEntity(PaymentTypeEntity paymentTypeEntity) {
		this.paymentTypeEntity = paymentTypeEntity;
	}
	public TransactionTypeEntity getTransactionTypeEntity() {
		return transactionTypeEntity;
	}
	public void setTransactionTypeEntity(TransactionTypeEntity transactionTypeEntity) {
		this.transactionTypeEntity = transactionTypeEntity;
	}
		
}
