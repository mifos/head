package org.mifos.clientportfolio.newloan.domain;

import java.util.List;

import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.framework.util.helpers.Money;

public interface LoanScheduleRounder {

    List<LoanScheduleEntity> round(List<LoanScheduleEntity> loanSchedules, List<LoanScheduleEntity> allLoanSchedules, GraceType graceType, Integer gracePeriodDuration, Money loanAmount, InterestType interestType);

}
