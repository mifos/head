package org.mifos.platform.cashflow.ui.model;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mifos.platform.cashflow.matchers.MonthlyCashFlowFormMatcher;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SuppressWarnings("unchecked")
public class CashFlowFormTest {

    @Test
    public void testGetMonthlyCashFlows() throws Exception {
        MonthlyCashFlowDetail cashFlowDetail1 = new MonthlyCashFlowDetail(new DateTime(), new BigDecimal(12), new BigDecimal(12), "notes");
        MonthlyCashFlowDetail cashFlowDetail2 = new MonthlyCashFlowDetail(new DateTime(), new BigDecimal(23), new BigDecimal(34), "notes");
        ArrayList<MonthlyCashFlowDetail> monthlyCashFlowDetails = new ArrayList<MonthlyCashFlowDetail>();
        monthlyCashFlowDetails.add(cashFlowDetail1);
        monthlyCashFlowDetails.add(cashFlowDetail2);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(monthlyCashFlowDetails);
        CashFlowForm cashFlowForm= new CashFlowForm(cashFlowDetail, false, null, 0d);
        List<MonthlyCashFlowForm> actual = cashFlowForm.getMonthlyCashFlows();
        List<MonthlyCashFlowForm> expected = new ArrayList<MonthlyCashFlowForm>();
        expected.add(new MonthlyCashFlowForm(cashFlowDetail1));
        expected.add(new MonthlyCashFlowForm(cashFlowDetail2));
        assertThat(actual.get(0),new MonthlyCashFlowFormMatcher(expected.get(0)));
        assertThat(actual.get(1),new MonthlyCashFlowFormMatcher(expected.get(1)));
    }

    @Test
    public void shouldForValidateIndebtednessRate() {
        CashFlowDetail cashFlowDetail = new CashFlowDetail(Collections.EMPTY_LIST);
        cashFlowDetail.setTotalCapital(BigDecimal.TEN);
        cashFlowDetail.setTotalLiability(BigDecimal.TEN);
        CashFlowForm cashFlowForm;
        cashFlowForm = new CashFlowForm(cashFlowDetail, true, new BigDecimal(123), null);
        assertThat(cashFlowForm.shouldForValidateIndebtednessRate(), is(false));
        cashFlowForm = new CashFlowForm(cashFlowDetail, true, new BigDecimal(123), 0d);
        assertThat(cashFlowForm.shouldForValidateIndebtednessRate(), is(false));
        cashFlowForm = new CashFlowForm(cashFlowDetail, true, null, 123d);
        assertThat(cashFlowForm.shouldForValidateIndebtednessRate(), is(false));
        cashFlowForm = new CashFlowForm(null, true, new BigDecimal(123), 123d);
        assertThat(cashFlowForm.shouldForValidateIndebtednessRate(), is(false));
        cashFlowForm = new CashFlowForm(cashFlowDetail, false, new BigDecimal(123), 123d);
        assertThat(cashFlowForm.shouldForValidateIndebtednessRate(), is(false));
        cashFlowForm = new CashFlowForm(cashFlowDetail, true, new BigDecimal(123), 123d);
        assertThat(cashFlowForm.shouldForValidateIndebtednessRate(), is(true));
    }

    @Test
    public void shouldComputeRepaymentCapacity() {
        CashFlowDetail cashFlowDetail = new CashFlowDetail(Collections.EMPTY_LIST);
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, false, new BigDecimal(1000), 10d);
        cashFlowForm.setTotalExpenses(BigDecimal.valueOf(76));
        cashFlowForm.setTotalRevenues(BigDecimal.valueOf(55));
        BigDecimal repaymentCapacity = cashFlowForm.computeRepaymentCapacity(BigDecimal.valueOf(60));
        assertThat(repaymentCapacity, is(notNullValue()));
        assertThat(repaymentCapacity.doubleValue(), is(1631.67));
    }
}