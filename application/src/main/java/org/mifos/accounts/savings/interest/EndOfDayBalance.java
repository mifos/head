package org.mifos.accounts.savings.interest;

import org.joda.time.LocalDate;
import org.mifos.framework.util.helpers.Money;

public class EndOfDayBalance {

    private LocalDate date;

    private Money balance;

    public EndOfDayBalance(LocalDate date, Money balance) {
        super();
        this.date = date;
        this.balance = balance;
    }

    public Money getBalance() {
        return this.balance;
    }

    public LocalDate getDate() {
        return this.date;
    }
}
