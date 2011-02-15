package org.mifos.clientportfolio.newloan.domain;

import java.math.BigDecimal;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

public class TestMoneyUtil {

    public static final MifosCurrency RUPEE = new MifosCurrency((short) 2, "RUPEE", BigDecimal.valueOf(1.0), "INR");
    public static final MifosCurrency EURO = new MifosCurrency((short) 3, "EURO", BigDecimal.valueOf(0.5), "EUR");

    public static Money createMoney(final String amount) {
        return new Money(RUPEE, amount);
    }

    public static String moneyOf(Money money) {
        return money.toString(Short.valueOf("1"));
    }

    public static String moneyOf(String amount) {
        Money money = TestMoneyUtil.createMoney(amount);
        return money.toString(Short.valueOf("1"));
    }
}