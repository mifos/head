package org.mifos.framework.util.helpers;

public class MoneyUtils {

	public static Money add(Money firstAmount, Money secondAmount) {
		Money sum = firstAmount == null ? secondAmount : firstAmount
				.add(secondAmount);
		return sum;
	}

	public static Double getMoneyDoubleValue(Money money) {
		return money == null ? null : money.getAmountDoubleValue();
	}

	public static final Money ZERO = new Money("0.00");

}
