package org.mifos.accounts.loan.util;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.struts.uihelpers.CashFlowDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.cashflow.ui.model.MonthlyCashFlowForm;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InstallmentAndCashflowComparisionUtilityTest {

    @Mock
    RepaymentScheduleInstallment repaymentScheduleInstallment;
    @Mock
    RepaymentScheduleInstallment repaymentScheduleInstallment1;

    MifosCurrency currency=new MifosCurrency((short) 1,"",new BigDecimal(0),"");

    @Mock
    MonthlyCashFlowForm monthlyCashFlowForm;

    @Mock
    MonthlyCashFlowDetail monthlyCashFlowDetail;

    @Test
    public void shouldGetCorrectValueFromGetCashflowDataHtmlBeansForPositiveCashflow() {
        Date date = new Date();

        when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,100.00));
        when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
        when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(150));
        when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
        when(monthlyCashFlowForm.getDate()).thenReturn(date);

        ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(repaymentScheduleInstallment);
        ArrayList<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        monthlyCashFlows.add(monthlyCashFlowForm);

        InstallmentAndCashflowComparisionUtility utility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows, BigDecimal.ZERO, date);
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = utility.mapToCashflowDataHtmlBeans();
        Assert.assertNotNull(cashflowDataHtmlBeans);
        Assert.assertEquals(1,cashflowDataHtmlBeans.size());
        Assert.assertEquals("50.00",cashflowDataHtmlBeans.get(0).getDiffCumulativeCashflowAndInstallment());
    }

    @Test
    public void shouldGetCorrectValueFromGetCashflowDataHtmlBeansForPositiveCashflowWithDecimal() {
        Date date = new Date();

        when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,100.56));
        when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
        when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(150));
        when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
        when(monthlyCashFlowForm.getDate()).thenReturn(date);

        ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(repaymentScheduleInstallment);
        ArrayList<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        monthlyCashFlows.add(monthlyCashFlowForm);

        InstallmentAndCashflowComparisionUtility utility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows,  BigDecimal.ZERO, date);
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = utility.mapToCashflowDataHtmlBeans();
        Assert.assertNotNull(cashflowDataHtmlBeans);
        Assert.assertEquals(1,cashflowDataHtmlBeans.size());
        Assert.assertEquals("49.44",cashflowDataHtmlBeans.get(0).getDiffCumulativeCashflowAndInstallment());
    }

    @Test
    public void shouldGetCorrectValueFromGetCashflowDataHtmlBeansForEqualCashflow() {
        Date date = new Date();

        when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,150.00));
        when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
        when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(150));
        when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
        when(monthlyCashFlowForm.getDate()).thenReturn(date);

        ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(repaymentScheduleInstallment);
        ArrayList<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        monthlyCashFlows.add(monthlyCashFlowForm);

        InstallmentAndCashflowComparisionUtility utility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows , BigDecimal.ZERO, date);
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = utility.mapToCashflowDataHtmlBeans();
        Assert.assertNotNull(cashflowDataHtmlBeans);
        Assert.assertEquals(1,cashflowDataHtmlBeans.size());
        Assert.assertEquals("0.00",cashflowDataHtmlBeans.get(0).getDiffCumulativeCashflowAndInstallment());
    }

    @Test
    public void shouldGetCorrectValueFromGetCashflowDataHtmlBeansForNegativeCashFlow() {
        Date date = new Date();

        when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,100.00));
        when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
        when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(50));
        when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
        when(monthlyCashFlowForm.getDate()).thenReturn(date);

        ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(repaymentScheduleInstallment);
        ArrayList<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        monthlyCashFlows.add(monthlyCashFlowForm);

        InstallmentAndCashflowComparisionUtility utility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows,  BigDecimal.ZERO, date);
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = utility.mapToCashflowDataHtmlBeans();
        Assert.assertNotNull(cashflowDataHtmlBeans);
        Assert.assertEquals(1,cashflowDataHtmlBeans.size());
        Assert.assertEquals("-50.00",cashflowDataHtmlBeans.get(0).getDiffCumulativeCashflowAndInstallment());
    }

    @Test
    public void shouldGetCorrectValueFromGetCashflowDataHtmlBeansForMultipleInstallmentsInSameMonth() {
        Date date = new Date(2010,10,1);
        Date date1 = new Date(2010,10,10);

        when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,100.00));
        when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
        when(repaymentScheduleInstallment1.getTotalValue()).thenReturn(new Money(currency,150.00));
        when(repaymentScheduleInstallment1.getDueDateValue()).thenReturn(date1);
        when(monthlyCashFlowForm.getDate()).thenReturn(date);

        when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(350));
        when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));

        ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(repaymentScheduleInstallment);
        installments.add(repaymentScheduleInstallment1);

        ArrayList<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        monthlyCashFlows.add(monthlyCashFlowForm);

        InstallmentAndCashflowComparisionUtility utility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows,  BigDecimal.ZERO, date);
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = utility.mapToCashflowDataHtmlBeans();
        Assert.assertNotNull(cashflowDataHtmlBeans);
        Assert.assertEquals(1,cashflowDataHtmlBeans.size());
        Assert.assertEquals("100.00",cashflowDataHtmlBeans.get(0).getDiffCumulativeCashflowAndInstallment());
    }

    @Test
    public void shouldGetCorrectPercentFromGetCashflowDataHtmlBeansForPositiveCashflow() {
        Date date = new Date();

        when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,100.00));
        when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
        when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(150));
        when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
        when(monthlyCashFlowForm.getDate()).thenReturn(date);

        ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(repaymentScheduleInstallment);
        ArrayList<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        monthlyCashFlows.add(monthlyCashFlowForm);

        InstallmentAndCashflowComparisionUtility utility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows,  BigDecimal.ZERO, date);
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = utility.mapToCashflowDataHtmlBeans();
        Assert.assertNotNull(cashflowDataHtmlBeans);
        Assert.assertEquals(1,cashflowDataHtmlBeans.size());
        Assert.assertEquals("66.67",cashflowDataHtmlBeans.get(0).getDiffCumulativeCashflowAndInstallmentPercent());
    }

    @Test
    public void shouldReturnInfinityForDivideByZero() {
        Date date = new Date();

        when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,0.0));
        when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
        when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(0));
        when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
        when(monthlyCashFlowForm.getDate()).thenReturn(date);

        ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(repaymentScheduleInstallment);
        ArrayList<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        monthlyCashFlows.add(monthlyCashFlowForm);

        InstallmentAndCashflowComparisionUtility utility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows,  BigDecimal.ZERO, date);
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = utility.mapToCashflowDataHtmlBeans();
        Assert.assertNotNull(cashflowDataHtmlBeans);
        Assert.assertEquals(1,cashflowDataHtmlBeans.size());
        Assert.assertEquals("Infinity",cashflowDataHtmlBeans.get(0).getDiffCumulativeCashflowAndInstallmentPercent());
    }



    @Test
    public void shouldGetCorrectPercentFromGetCashflowDataHtmlBeansForEqualCashflow() {
        Date date = new Date();

        when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,150.00));
        when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
        when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(150));
        when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
        when(monthlyCashFlowForm.getDate()).thenReturn(date);

        ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(repaymentScheduleInstallment);
        ArrayList<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        monthlyCashFlows.add(monthlyCashFlowForm);

        InstallmentAndCashflowComparisionUtility utility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows,  BigDecimal.ZERO, date);
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = utility.mapToCashflowDataHtmlBeans();
        Assert.assertNotNull(cashflowDataHtmlBeans);
        Assert.assertEquals(1,cashflowDataHtmlBeans.size());
        Assert.assertEquals("100.00",cashflowDataHtmlBeans.get(0).getDiffCumulativeCashflowAndInstallmentPercent());
    }

    @Test
    public void shouldGetCorrectPercentFromGetCashflowDataHtmlBeansForNegativeCashFlow() {
        Date date = new Date();

        when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,100.00));
        when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
        when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(50));
        when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
        when(monthlyCashFlowForm.getDate()).thenReturn(date);

        ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(repaymentScheduleInstallment);
        ArrayList<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        monthlyCashFlows.add(monthlyCashFlowForm);

        InstallmentAndCashflowComparisionUtility utility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows,  BigDecimal.ZERO, date);
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = utility.mapToCashflowDataHtmlBeans();
        Assert.assertNotNull(cashflowDataHtmlBeans);
        Assert.assertEquals(1,cashflowDataHtmlBeans.size());
        Assert.assertEquals("200.00",cashflowDataHtmlBeans.get(0).getDiffCumulativeCashflowAndInstallmentPercent());
    }

    @Test
    public void shouldGetCorrectPercentFromGetCashflowDataHtmlBeansForMultipleInstallmentsInSameMonth() {
        Date date = new Date(2010,10,1);
        Date date1 = new Date(2010,10,10);
        BigDecimal loanAmount = BigDecimal.ZERO;

        when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,100.00));
        when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
        when(repaymentScheduleInstallment1.getTotalValue()).thenReturn(new Money(currency,150.00));
        when(repaymentScheduleInstallment1.getDueDateValue()).thenReturn(date1);

        when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(350));
        when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
        when(monthlyCashFlowForm.getDate()).thenReturn(date);

        ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
        installments.add(repaymentScheduleInstallment);
        installments.add(repaymentScheduleInstallment1);

        ArrayList<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
        monthlyCashFlows.add(monthlyCashFlowForm);


        InstallmentAndCashflowComparisionUtility utility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows,  loanAmount, date);
        List<CashFlowDataHtmlBean> cashflowDataHtmlBeans = utility.mapToCashflowDataHtmlBeans();
        Assert.assertNotNull(cashflowDataHtmlBeans);
        Assert.assertEquals(1,cashflowDataHtmlBeans.size());
        Assert.assertEquals("71.43",cashflowDataHtmlBeans.get(0).getDiffCumulativeCashflowAndInstallmentPercent());
    }

    @Test
    public void shouldLoanAmountAddedToCashFlowForTheCorrespondingMonth() {

    List<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
    BigDecimal loanAmount = BigDecimal.TEN;
    Date date = new Date(2010,10,1);
    Date date1 = new Date(2010,10,10);

    when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,100.00));
    when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
    when(repaymentScheduleInstallment1.getTotalValue()).thenReturn(new Money(currency,150.00));
    when(repaymentScheduleInstallment1.getDueDateValue()).thenReturn(date1);

    when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(350));
    BigDecimal REVENUE_FOR_MONTH = BigDecimal.TEN;
    when(monthlyCashFlowForm.getRevenue()).thenReturn(REVENUE_FOR_MONTH);
    when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
    when(monthlyCashFlowForm.getDate()).thenReturn(date);

    monthlyCashFlows.add(monthlyCashFlowForm);


    ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
    installments.add(repaymentScheduleInstallment);
    installments.add(repaymentScheduleInstallment1);

    InstallmentAndCashflowComparisionUtility installmentAndCashflowComparisionUtility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows, loanAmount, date);
    installmentAndCashflowComparisionUtility.addLoanAmountToRespectiveCashFlow();
    Mockito.verify(monthlyCashFlowForm).setRevenue(loanAmount.add(REVENUE_FOR_MONTH));

    }


    @Test
    public void shouldLoanAmountAddedToCashFlowForOnlyTheCorrespondingMonth() {

    List<MonthlyCashFlowForm> monthlyCashFlows = new ArrayList<MonthlyCashFlowForm>();
    BigDecimal loanAmount = BigDecimal.TEN;
    Date date = new Date(2010,10,1);
    Date date1 = new Date(2010,10,10);
    Date date2 = new Date(2010,11,10);

    when(repaymentScheduleInstallment.getTotalValue()).thenReturn(new Money(currency,100.00));
    when(repaymentScheduleInstallment.getDueDateValue()).thenReturn(date);
    when(repaymentScheduleInstallment1.getTotalValue()).thenReturn(new Money(currency,150.00));
    when(repaymentScheduleInstallment1.getDueDateValue()).thenReturn(date1);

    when(monthlyCashFlowForm.getCumulativeCashFlow()).thenReturn(new BigDecimal(350));
    BigDecimal REVENUE_FOR_MONTH = BigDecimal.TEN;
    when(monthlyCashFlowForm.getRevenue()).thenReturn(REVENUE_FOR_MONTH);
    when(monthlyCashFlowForm.getDateTime()).thenReturn(new DateTime(date.getTime()));
    when(monthlyCashFlowForm.getDate()).thenReturn(date);

    MonthlyCashFlowForm monthlyCashFlowForm2 = Mockito.mock(MonthlyCashFlowForm.class);
    when(monthlyCashFlowForm2.getCumulativeCashFlow()).thenReturn(new BigDecimal(350));
    when(monthlyCashFlowForm2.getRevenue()).thenReturn(REVENUE_FOR_MONTH);
    when(monthlyCashFlowForm2.getDateTime()).thenReturn(new DateTime(date.getTime()));
    when(monthlyCashFlowForm2.getDate()).thenReturn(date2);

    monthlyCashFlows.add(monthlyCashFlowForm2);
    monthlyCashFlows.add(monthlyCashFlowForm);


    ArrayList<RepaymentScheduleInstallment> installments = new ArrayList<RepaymentScheduleInstallment>();
    installments.add(repaymentScheduleInstallment);
    installments.add(repaymentScheduleInstallment1);

    InstallmentAndCashflowComparisionUtility installmentAndCashflowComparisionUtility = new InstallmentAndCashflowComparisionUtility(installments, monthlyCashFlows, loanAmount, date);
    installmentAndCashflowComparisionUtility.addLoanAmountToRespectiveCashFlow();
    Mockito.verify(monthlyCashFlowForm).setRevenue(loanAmount.add(REVENUE_FOR_MONTH));
    Mockito.verify(monthlyCashFlowForm2, Mockito.never()).setRevenue(Mockito.any(BigDecimal.class));

    }

}
