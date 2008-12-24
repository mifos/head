/**
 * 
 */
package org.mifos.application.master.business;

import org.mifos.framework.business.View;

public class PaymentTypeView extends View {

	private Short paymentTypeId;
	private String paymentTypeValue;
	
	public Short getPaymentTypeId() {
		return paymentTypeId;
	}
	public void setPaymentTypeId(Short paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}
	public String getPaymentTypeValue() {
		return paymentTypeValue;
	}
	public void setPaymentTypeValue(String paymentTypeValue) {
		this.paymentTypeValue = paymentTypeValue;
	}
	
}
