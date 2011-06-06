package org.mifos.clientportfolio.newloan.domain;

import org.joda.time.LocalDate;
import org.mifos.framework.util.helpers.Money;

public interface InterestCalculationForumula {

    Money calculate(Money principalOutstanding, Double interestRate, LocalDate interestPeriodStartDate, LocalDate interestPeriodEndDate);

}
