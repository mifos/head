package org.mifos.application.acceptedpaymenttype.persistence.helper;

import java.util.List;

import org.mifos.application.acceptedpaymenttype.business.AcceptedPaymentType;
import org.mifos.application.util.helpers.TrxnTypes;

public class TransactionAcceptedPaymentTypes {
	
	private TrxnTypes transactionType;
	private List<AcceptedPaymentType> acceptedPaymentTypes;
	
	
	public TrxnTypes getTransactionType()
	{
		return transactionType;
	}
	public void setTransactionType(TrxnTypes transactionType)
	{
		this.transactionType = transactionType;
	}
	
	public void setAcceptedPaymentTypes(List<AcceptedPaymentType> acceptedPaymentTypes)
	{
		this.acceptedPaymentTypes = acceptedPaymentTypes;
	}
	
	public List<AcceptedPaymentType> getAcceptedPaymentTypes()
	{
		return acceptedPaymentTypes;
	}
	
	

}
