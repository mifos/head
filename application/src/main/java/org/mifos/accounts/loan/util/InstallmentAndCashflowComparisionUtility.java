package org.mifos.accounts.loan.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.accounts.loan.struts.uihelpers.CashflowDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.cashflow.ui.model.MonthlyCashFlowForm;

public class InstallmentAndCashflowComparisionUtility {

    private List<MonthlyCashFlowForm> monthlyCashFlows;
    private List<RepaymentScheduleInstallment> installments;


    public InstallmentAndCashflowComparisionUtility(List<RepaymentScheduleInstallment> installments,
            List<MonthlyCashFlowForm> monthlyCashFlows) {
        this.installments = installments;
        this.monthlyCashFlows = monthlyCashFlows;
    }


    public void setMonthlyCashFlows(List<MonthlyCashFlowForm> monthlyCashFlows) {
        this.monthlyCashFlows = monthlyCashFlows;
    }


    public List<MonthlyCashFlowForm> getMonthlyCashFlows() {
        return monthlyCashFlows;
    }

    public void setInstallments(List<RepaymentScheduleInstallment> installments) {
        this.installments = installments;
    }


    public List<RepaymentScheduleInstallment> getInstallments() {
        return installments;
    }

    public List<CashflowDataHtmlBean> getCashflowDataHtmlBeans(){
         List<CashflowDataHtmlBean> cashflowDataHtmlBeans = null;

         if(getMonthlyCashFlows() != null) {
             cashflowDataHtmlBeans = new ArrayList<CashflowDataHtmlBean>();
             for(MonthlyCashFlowForm monthlyCashflowform : getMonthlyCashFlows()) {
                 CashflowDataHtmlBean cashflowDataHtmlBean = new CashflowDataHtmlBean();
                 cashflowDataHtmlBean.setMonth(monthlyCashflowform.getMonth());
                 cashflowDataHtmlBean.setYear(String.valueOf(monthlyCashflowform.getYear()));
                 cashflowDataHtmlBean.setCumulativeCashFlow(String.valueOf(monthlyCashflowform.getCumulativeCashFlow()));
                 cashflowDataHtmlBean.setDiffCumulativeCashflowAndInstallment(computeDiffBetweenCumulativeAndInstallment(monthlyCashflowform.getDateTime(),monthlyCashflowform.getCumulativeCashFlow()));
                 cashflowDataHtmlBean.setDiffCumulativeCashflowAndInstallmentPercent(computeDiffBetweenCumulativeAndInstallmentPercent(monthlyCashflowform.getDateTime(),monthlyCashflowform.getCumulativeCashFlow()));
                 cashflowDataHtmlBean.setNotes(monthlyCashflowform.getNotes());
                 cashflowDataHtmlBeans.add(cashflowDataHtmlBean);
             }
         }


         return cashflowDataHtmlBeans;
     }


     private String computeDiffBetweenCumulativeAndInstallment(DateTime dateOfCashFlow, BigDecimal cashflow) {
         BigDecimal totalInstallmentForMonth = cumulativeTotalForMonth(dateOfCashFlow);
         return String.valueOf(cashflow.subtract(totalInstallmentForMonth).setScale(2,RoundingMode.HALF_UP));
     }

     private String computeDiffBetweenCumulativeAndInstallmentPercent(DateTime dateOfCashFlow, BigDecimal cashflow) {
         BigDecimal totalInstallmentForMonth = cumulativeTotalForMonth(dateOfCashFlow);
         String value= "";
         if(totalInstallmentForMonth.doubleValue()!=0) {
            value = String.valueOf(totalInstallmentForMonth.multiply(new BigDecimal(100)).divide(cashflow, 2, RoundingMode.HALF_UP));
         }
         return value;
     }


     private BigDecimal cumulativeTotalForMonth(DateTime dateOfCashFlow) {
         BigDecimal value = new BigDecimal(0).setScale(2);
         for(RepaymentScheduleInstallment repaymentScheduleInstallment:getInstallments()) {
             Calendar dueDate = Calendar.getInstance();
             dueDate.setTime(repaymentScheduleInstallment.getDueDateValue());

             Calendar cashFlowDate = Calendar.getInstance();
             cashFlowDate.setTime(dateOfCashFlow.toDate());


             if((dueDate.get(Calendar.MONTH) == cashFlowDate.get(Calendar.MONTH))&&(dueDate.get(Calendar.YEAR) == cashFlowDate.get(Calendar.YEAR))){
                 value = value.add(repaymentScheduleInstallment.getTotalValue().getAmount());
             }
         }
         return value;
     }

}
