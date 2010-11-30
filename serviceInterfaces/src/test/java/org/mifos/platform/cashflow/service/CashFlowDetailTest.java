package org.mifos.platform.cashflow.service;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CashFlowDetailTest {

    @Test
    public void shouldForValidateIndebtednessRate() {
        CashFlowDetail cashFlowDetail = new CashFlowDetail();
        cashFlowDetail.setTotalCapital(null);
        cashFlowDetail.setTotalLiability(BigDecimal.TEN);
        assertThat(cashFlowDetail.shouldForValidateIndebtednessRate(), is(false));

        cashFlowDetail.setTotalCapital(BigDecimal.ZERO);
        cashFlowDetail.setTotalLiability(BigDecimal.TEN);
        assertThat(cashFlowDetail.shouldForValidateIndebtednessRate(), is(false));

        cashFlowDetail.setTotalCapital(BigDecimal.TEN);
        cashFlowDetail.setTotalLiability(null);
        assertThat(cashFlowDetail.shouldForValidateIndebtednessRate(), is(false));

        cashFlowDetail.setTotalCapital(BigDecimal.TEN);
        cashFlowDetail.setTotalLiability(BigDecimal.TEN);
        assertThat(cashFlowDetail.shouldForValidateIndebtednessRate(), is(true));
    }
}
