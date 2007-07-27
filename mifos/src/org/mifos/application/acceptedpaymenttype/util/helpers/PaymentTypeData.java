package org.mifos.application.acceptedpaymenttype.util.helpers;



import java.io.Serializable;

public class PaymentTypeData implements Serializable {

	private Short id = null;
	private String name;
	private Short acceptedPaymentTypeId = null;

	
	protected PaymentTypeData() {
		
	}
	
	public PaymentTypeData(Short id) {
		this.id =  id;
	}

	public Short getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public Short getAcceptedPaymentTypeId() {
		return acceptedPaymentTypeId;
	}

	public void setAcceptedPaymentTypeId(Short acceptedPaymentTypeId) {
		this.acceptedPaymentTypeId = acceptedPaymentTypeId;
	}

}


