package org.mifos.ui.core.controller.util.helpers;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.clientportfolio.loan.service.DailySchedule;
import org.mifos.clientportfolio.loan.service.MonthlyOnDayOfMonthSchedule;
import org.mifos.clientportfolio.loan.service.MonthlyOnWeekOfMonthSchedule;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.loan.service.WeeklySchedule;
import org.mifos.clientportfolio.loan.ui.LoanAccountFormBean;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountPenaltyDto;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
public class LoanCreationHelper {
    
    public static LocalDate translateDisbursementDateToLocalDate(LoanAccountFormBean formBean) {
        return new DateTime().withDate(formBean.getDisbursementDateYY().intValue(), formBean.getDisbursementDateMM().intValue(), formBean.getDisbursementDateDD().intValue()).toLocalDate();
    }

    public static RecurringSchedule determineRecurringSchedule(LoanAccountFormBean formBean) {
        RecurringSchedule recurringSchedule = null;
        if (formBean.isMonthly()) {
            if (formBean.isMonthlyDayOfMonthOptionSelected()) {
                recurringSchedule = new MonthlyOnDayOfMonthSchedule(formBean.getRepaymentRecursEvery(), formBean.getRepaymentDayOfMonth());
            } else if (formBean.isMonthlyWeekOfMonthOptionSelected()) {
                recurringSchedule = new MonthlyOnWeekOfMonthSchedule(formBean.getRepaymentRecursEvery(), formBean.getRepaymentWeekOfMonth(), formBean.getRepaymentDayOfWeek());
            }
        } else if (formBean.isWeekly()) {
            recurringSchedule = new WeeklySchedule(formBean.getRepaymentRecursEvery(), formBean.getRepaymentDayOfWeek());
        } else {
            recurringSchedule = new DailySchedule(formBean.getRepaymentRecursEvery());
        }
        return recurringSchedule;
    }
    
    public static List<CreateAccountFeeDto> translateToAccountFeeDtos(LoanAccountFormBean formBean) {
        List<CreateAccountFeeDto> accountFees = new ArrayList<CreateAccountFeeDto>();
        Number[] defaultFeeIds = formBean.getDefaultFeeId();
        if (defaultFeeIds != null) {
            int feeIndex = 0;
            CreateAccountFeeDto accountFee = null;
            for (Number feeId : defaultFeeIds) {
                Boolean removeDefaultFeeSelected = formBean.getDefaultFeeSelected()[feeIndex];
                if (removeDefaultFeeSelected == null || !removeDefaultFeeSelected) {
                    String amount = formBean.getDefaultFeeAmountOrRate()[feeIndex].toString();
                    accountFee = new CreateAccountFeeDto(feeId.intValue(), amount);
                    accountFees.add(accountFee);
                }
                feeIndex++;
            }
        }
        return accountFees;
    }
    
    public static List<CreateAccountFeeDto> translateToAdditionalAccountFeeDtos(LoanAccountFormBean formBean) {
        List<CreateAccountFeeDto> accountFees = new ArrayList<CreateAccountFeeDto>();

        int index = 0;
        CreateAccountFeeDto accountFee = null;
        for (Number feeId : formBean.getSelectedFeeId()) {
            if (feeId != null) {
                Number feeAmountOrRate = formBean.getSelectedFeeAmount()[index];
                accountFee = new CreateAccountFeeDto(feeId.intValue(), feeAmountOrRate.toString());
                accountFees.add(accountFee);
            }
            index++;
        }

        return accountFees;
    }

    public static List<CreateAccountPenaltyDto> translateToAccountPenaltyDtos(LoanAccountFormBean formBean) {
        List<CreateAccountPenaltyDto> accountPenalties = new ArrayList<CreateAccountPenaltyDto>();
        Number[] defaultPenaltyIds = formBean.getDefaultPenaltyId();
        if (defaultPenaltyIds != null) {
            int penaltyIndex = 0;
            CreateAccountPenaltyDto accountPenalty = null;
            for (Number penaltyId : defaultPenaltyIds) {
                Boolean removeDefaultPenaltySelected = formBean.getDefaultPenaltySelected()[penaltyIndex];
                if (removeDefaultPenaltySelected == null || !removeDefaultPenaltySelected) {
                    String amount = formBean.getDefaultPenaltyAmountOrRate()[penaltyIndex].toString();
                    accountPenalty = new CreateAccountPenaltyDto(penaltyId.intValue(), amount);
                    accountPenalties.add(accountPenalty);
                }
                penaltyIndex++;
            }
        }
        return accountPenalties;
    }
}
