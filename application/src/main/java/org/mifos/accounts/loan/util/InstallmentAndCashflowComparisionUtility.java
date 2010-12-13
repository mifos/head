package org.mifos.accounts.loan.util;

import org.joda.time.DateTime;
import org.mifos.accounts.loan.struts.uihelpers.CashFlowDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.cashflow.ui.model.MonthlyCashFlowForm;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// TODO: Move this mapping logic to an appropriate layer - buddy/srikanth
public class InstallmentAndCashflowComparisionUtility {

    private List<MonthlyCashFlowForm> monthlyCashFlows;
    private List<RepaymentScheduleInstallment> installments;


    public InstallmentAndCashflowComparisionUtility(List<RepaymentScheduleInstallment> installments,
                                                    List<MonthlyCashFlowForm> monthlyCashFlows) {
        this.installments = installments;
        this.monthlyCashFlows = monthlyCashFlows;
    }


    public List<CashFlowDataHtmlBean> mapToCashflowDataHtmlBeans() {
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = null;
        if (monthlyCashFlows != null && monthlyCashFlows.size() > 0) {
            cashflowDataHtmlBeans = new ArrayList<CashFlowDataHtmlBean>();
            for (MonthlyCashFlowForm monthlyCashflowform : monthlyCashFlows) {
                CashFlowDataHtmlBean cashflowDataHtmlBean = mapCashFlowDataHtmlBean(monthlyCashflowform);
                cashflowDataHtmlBeans.add(cashflowDataHtmlBean);
            }
        }
        return cashflowDataHtmlBeans;
    }

    private CashFlowDataHtmlBean mapCashFlowDataHtmlBean(MonthlyCashFlowForm monthlyCashflowform) {
        CashFlowDataHtmlBean cashflowDataHtmlBean = new CashFlowDataHtmlBean();
        cashflowDataHtmlBean.setMonth(monthlyCashflowform.getMonth());
        cashflowDataHtmlBean.setYear(String.valueOf(monthlyCashflowform.getYear()));
        cashflowDataHtmlBean.setCumulativeCashFlow(String.valueOf(monthlyCashflowform.getCumulativeCashFlow()));
        String cumulativeCashflowAndInstallment = computeDiffBetweenCumulativeAndInstallment(monthlyCashflowform.getDateTime(), monthlyCashflowform.getCumulativeCashFlow());
        cashflowDataHtmlBean.setDiffCumulativeCashflowAndInstallment(cumulativeCashflowAndInstallment);
        String cashflowAndInstallmentPercent = computeDiffBetweenCumulativeAndInstallmentPercent(monthlyCashflowform.getDateTime(), monthlyCashflowform.getCumulativeCashFlow());
        cashflowDataHtmlBean.setDiffCumulativeCashflowAndInstallmentPercent(cashflowAndInstallmentPercent);
        cashflowDataHtmlBean.setNotes(monthlyCashflowform.getNotes());
        cashflowDataHtmlBean.setMonthYear(monthlyCashflowform.getDateTime().toDate());
        return cashflowDataHtmlBean;
    }


    private String computeDiffBetweenCumulativeAndInstallment(DateTime dateOfCashFlow, BigDecimal cashflow) {
        BigDecimal totalInstallmentForMonth = cumulativeTotalForMonth(dateOfCashFlow);
        return String.valueOf(cashflow.subtract(totalInstallmentForMonth).setScale(2, RoundingMode.HALF_UP));
    }

    private String computeDiffBetweenCumulativeAndInstallmentPercent(DateTime dateOfCashFlow, BigDecimal cashflow) {
        BigDecimal totalInstallmentForMonth = cumulativeTotalForMonth(dateOfCashFlow);
        String value = "";
        if (cashflow.doubleValue() != 0) {
            value = String.valueOf(totalInstallmentForMonth.multiply(new BigDecimal(100)).divide(cashflow, 2, RoundingMode.HALF_UP));
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


            if ((dueDate.get(Calendar.MONTH) == cashFlowDate.get(Calendar.MONTH)) && (dueDate.get(Calendar.YEAR) == cashFlowDate.get(Calendar.YEAR))) {
                value = value.add(repaymentScheduleInstallment.getTotalValue().getAmount());
            }
        }
        return value;
    }

}
