package org.mifos.accounts.savings.interest;

import org.joda.time.LocalDate;

public class InterestCalculationRange {

    private final LocalDate lowerDate;
    private final LocalDate upperDate;

    public InterestCalculationRange(LocalDate lowerDate, LocalDate upperDate) {
        this.lowerDate = lowerDate;
        this.upperDate = upperDate;
    }

    public LocalDate getLowerDate() {
        return this.lowerDate;
    }

    public LocalDate getUpperDate() {
        return this.upperDate;
    }

}