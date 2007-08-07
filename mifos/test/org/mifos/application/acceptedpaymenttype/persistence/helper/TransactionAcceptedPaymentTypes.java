package org.mifos.application.acceptedpaymenttype.persistence.helper;

import org.mifos.application.util.helpers.TrxnTypes;
import org.mifos.application.acceptedpaymenttype.business.AcceptedPaymentType;
import java.util.List;

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
