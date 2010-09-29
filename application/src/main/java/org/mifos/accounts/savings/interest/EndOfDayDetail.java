package org.mifos.accounts.savings.interest;

import java.math.BigDecimal;

import org.joda.time.LocalDate;

public class EndOfDayDetail {

    private final LocalDate date;
    private final BigDecimal amount;

    public EndOfDayDetail(LocalDate date, BigDecimal amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return this.date;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

}
