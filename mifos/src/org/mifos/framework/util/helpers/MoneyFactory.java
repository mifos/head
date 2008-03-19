package org.mifos.framework.util.helpers;

import java.math.BigDecimal;

import org.mifos.application.master.business.MifosCurrency;

public class MoneyFactory {

	public static Money createMoney(MifosCurrency currency, BigDecimal amount) {
		return new Money(currency, amount);
	}

	public static Money zero(MifosCurrency currency) {
		return new Money(currency, BigDecimal.ZERO);
	}

	public static final Money ZERO = new Money();

}
