package org.mifos.platform.cashflow.ui.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.cashflow.CashFlowService;
import org.mifos.platform.cashflow.matchers.CashFlowFormMatcher;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CashFlowControllerTest {
    @Mock
    private CashFlowService cashFlowService;

    @Test
    public void prepareCashFlowForm() throws Exception {
        CashFlowDetail cashFlowDetail = new CashFlowDetail(new ArrayList<MonthlyCashFlowDetail>());
        Mockito.when(cashFlowService.cashFlowFor(2012, 1, 12)).thenReturn(cashFlowDetail);
        CashFlowController cashFlowController = new CashFlowController(cashFlowService);
        BigDecimal loanAmount = new BigDecimal("1234");
        Double indebtednessRatio = 12d;
        CashFlowForm cashFlowForm = cashFlowController.prepareCashFlowForm(2012, 1, 12, loanAmount, indebtednessRatio, true);
        CashFlowForm expectedCashFlowForm = new CashFlowForm(cashFlowDetail, true, loanAmount, indebtednessRatio);
        assertThat(cashFlowForm, new CashFlowFormMatcher(expectedCashFlowForm));
        Mockito.verify(cashFlowService).cashFlowFor(2012, 1, 12);
    }
}
