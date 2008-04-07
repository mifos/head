package org.mifos.framework.util.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.mifos.application.master.business.MifosCurrency;


public class MoneyUtils {

	public static Money add(Money firstAmount, Money secondAmount) {
		Money sum = firstAmount == null ? secondAmount : firstAmount
				.add(secondAmount);
		return sum;
	}

	public static Double getMoneyDoubleValue(Money money) {
		return money == null ? null : money.getAmountDoubleValue();
	}

	public static BigDecimal getMoneyAmount(Money money) {
		BigDecimal amount = money.getAmount();
		MifosCurrency currency = money.getCurrency();
		if (amount == null)
			return null;
		if (currency == null)
			return amount;
		return amount.setScale(currency.getDefaultDigitsAfterDecimal(),
				RoundingMode.HALF_UP);
	}
}
