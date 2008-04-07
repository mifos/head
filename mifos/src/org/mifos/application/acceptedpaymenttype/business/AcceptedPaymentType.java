package org.mifos.application.acceptedpaymenttype.business;

import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.framework.business.PersistentObject;

public class AcceptedPaymentType extends PersistentObject{
	
	private Short acceptedPaymentTypeId ;
	private TransactionTypeEntity transactionTypeEntity ;
	private PaymentTypeEntity paymentTypeEntity ;
		
	public Short getAcceptedPaymentTypeId() {
		return acceptedPaymentTypeId;
	}
	public void setAcceptedPaymentTypeId(Short acceptedPaymentTypeId) {
		this.acceptedPaymentTypeId = acceptedPaymentTypeId;
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
