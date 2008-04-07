package org.mifos.application.master.util.helpers;
import org.mifos.config.LocalizedTextLookup;



public enum PaymentTypes implements LocalizedTextLookup{
	CASH((short) 1), VOUCHER((short) 2), CHEQUE((short) 3);
	
	Short value;

	PaymentTypes(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	

	
	public static PaymentTypes getPaymentType(int value){
		for (PaymentTypes paymentType : PaymentTypes.values()) {
			if (paymentType.value == value) {
				return paymentType;
			}
		}
		throw new RuntimeException("can't find payment type " + value);
	}

	public PaymentTypes next() {
		if (this == CHEQUE) {
			return CASH;
		}
		return getPaymentType(value + 1);
	}

	public String getPropertiesKey() {
		return "PaymentTypes." + toString();
	}
	
	
}
