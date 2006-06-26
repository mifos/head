package org.mifos.application.master.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.framework.business.PersistentObject;

public class TransactionTypeEntity extends PersistentObject{
	
	private Short transactionId;
	private String transactionName;
	private Set<SupportedModesEntity> supportedModesSet ;
	private List<PaymentTypeEntity>  paymentModesList;
	
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
	private Set<SupportedModesEntity> getSupportedModesSet() {
		return supportedModesSet;
	}
	private void setSupportedModesSet(Set<SupportedModesEntity> supportedModesSet) {
		this.supportedModesSet = supportedModesSet;
	}	
	public List<PaymentTypeEntity> getApplicablePaymentTypes(){		
		if(this.supportedModesSet!=null)
		{
			paymentModesList = new ArrayList<PaymentTypeEntity>();
			for (Iterator supportedModes = this.supportedModesSet.iterator(); supportedModes.hasNext();) {
				SupportedModesEntity supportedModesEntity = (SupportedModesEntity) supportedModes.next();								
				paymentModesList.add(supportedModesEntity.getPaymentTypeEntity());		
			}
		}
		return paymentModesList;
	}	
}
