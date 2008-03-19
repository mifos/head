package org.mifos.framework.util.helpers;

import java.math.BigDecimal;

public class MoneyFixture {

	public static Money createMoney(double amount) {
		return new Money(BigDecimal.valueOf(amount).toString());
	}

}
