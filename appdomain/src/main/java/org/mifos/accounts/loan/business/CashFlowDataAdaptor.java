package org.mifos.accounts.loan.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.mifos.accounts.loan.util.helpers.CashFlowDataDto;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.platform.cashflow.ui.model.MonthlyCashFlowForm;
import org.mifos.platform.util.CollectionUtils;

// TODO: Move this mapping logic to an appropriate layer - buddy/srikanth
public class CashFlowDataAdaptor {

    private List<MonthlyCashFlowForm> monthlyCashFlows;
    private List<RepaymentScheduleInstallment> installments;
    private BigDecimal loanAmount;
    private Date disbursementDate;
    private Locale locale;

    public void initialize(List<RepaymentScheduleInstallment> installments,
                           List<MonthlyCashFlowForm> monthlyCashFlows, BigDecimal loanAmount, Date disbursementDate, Locale locale, boolean addLoanAmountToCashFlow) {
        this.installments = installments;
        this.monthlyCashFlows = monthlyCashFlows;
        this.loanAmount = loanAmount;
        this.disbursementDate = disbursementDate;
        this.locale = locale;
        if(addLoanAmountToCashFlow) {
            addLoanAmountToRespectiveCashFlow();
        }
    }


    public List<CashFlowDataDto> getCashflowDataDtos() {
        List<CashFlowDataDto> cashflowDataDtos = null;
        if (hasMonthlyCashFlows()) {
            cashflowDataDtos = new ArrayList<CashFlowDataDto>();
            for (MonthlyCashFlowForm monthlyCashflowform : monthlyCashFlows) {
                CashFlowDataDto cashflowDataDto = getCashFlowDataDto(monthlyCashflowform);
                cashflowDataDtos.add(cashflowDataDto);
            }
        }

        return cashflowDataDtos;
    }

    private boolean hasMonthlyCashFlows() {
        return CollectionUtils.isNotEmpty(monthlyCashFlows);
    }


    private void addLoanAmountToRespectiveCashFlow() {
        for (MonthlyCashFlowForm monthlyCashFlowForm : monthlyCashFlows) {
            if(DateUtils.sameMonthYear(monthlyCashFlowForm.getDate(),disbursementDate)) {
                BigDecimal revenue = monthlyCashFlowForm.getRevenue();
                monthlyCashFlowForm.setRevenue((revenue == null)? loanAmount : loanAmount.add(revenue));
                break;
            }
        }
    }


    private CashFlowDataDto getCashFlowDataDto(MonthlyCashFlowForm monthlyCashflowform) {
        monthlyCashflowform.setLocale(locale);
        CashFlowDataDto cashflowDataDto = new CashFlowDataDto();
        cashflowDataDto.setMonth(monthlyCashflowform.getMonthInLocale());
        cashflowDataDto.setYear(String.valueOf(monthlyCashflowform.getYear()));
        cashflowDataDto.setCumulativeCashFlow(String.valueOf(monthlyCashflowform.getCumulativeCashFlow().setScale(2)));
        cashflowDataDto.setMonthYear(monthlyCashflowform.getDateTime().toDate());
        cashflowDataDto.setNotes(monthlyCashflowform.getNotes());

        String cumulativeCashflowAndInstallment = computeDiffBetweenCumulativeAndInstallment(monthlyCashflowform.getDateTime(), monthlyCashflowform.getCumulativeCashFlow());
        cashflowDataDto.setDiffCumulativeCashflowAndInstallment(cumulativeCashflowAndInstallment);
        String cashflowAndInstallmentPercent = computeDiffBetweenCumulativeAndInstallmentPercent(monthlyCashflowform.getDateTime(), monthlyCashflowform.getCumulativeCashFlow());
        cashflowDataDto.setDiffCumulativeCashflowAndInstallmentPercent(cashflowAndInstallmentPercent);

        return cashflowDataDto;
    }


    private String computeDiffBetweenCumulativeAndInstallment(DateTime dateOfCashFlow, BigDecimal cashflow) {
        BigDecimal totalInstallmentForMonth = cumulativeTotalForMonth(dateOfCashFlow);
        return String.valueOf(cashflow.subtract(totalInstallmentForMonth).setScale(2, RoundingMode.HALF_UP));
    }

    private String computeDiffBetweenCumulativeAndInstallmentPercent(DateTime dateOfCashFlow, BigDecimal cashflow) {
        BigDecimal totalInstallmentForMonth = cumulativeTotalForMonth(dateOfCashFlow);
        String value;
        if (cashflow.doubleValue() != 0) {
            value = String.valueOf(totalInstallmentForMonth.multiply(BigDecimal.valueOf(100)).divide(cashflow, 2, RoundingMode.HALF_UP));
        } else {
            value = "Infinity";
        }
        return value;
    }


    private BigDecimal cumulativeTotalForMonth(DateTime dateOfCashFlow) {
        BigDecimal value = new BigDecimal(0).setScale(2);
        for (RepaymentScheduleInstallment repaymentScheduleInstallment : installments) {
            Calendar dueDate = Calendar.getInstance();
            dueDate.setTime(repaymentScheduleInstallment.getDueDateValue());

            Calendar cashFlowDate = Calendar.getInstance();
            cashFlowDate.setTime(dateOfCashFlow.toDate());

            if (DateUtils.sameMonthYear(dueDate, cashFlowDate)) {
                value = value.add(repaymentScheduleInstallment.getTotalValue().getAmount());
            }
        }
        return value;
    }

}
