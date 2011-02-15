package org.mifos.clientportfolio.newloan.domain;

import java.math.BigDecimal;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;

public class TestMoneyUtil {

    public static final MifosCurrency RUPEE = new MifosCurrency((short) 2, "RUPEE", BigDecimal.valueOf(1.0), "INR");
    public static final MifosCurrency EURO = new MifosCurrency((short) 3, "EURO", BigDecimal.valueOf(0.5), "EUR");

    public static Money createMoney(final String loanAmount) {
        return new Money(RUPEE, loanAmount);
    }
}