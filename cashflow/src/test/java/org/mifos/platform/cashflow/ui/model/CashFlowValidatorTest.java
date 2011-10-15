package org.mifos.platform.cashflow.ui.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.matchers.MessageMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.validation.ValidationContext;

import java.math.BigDecimal;
import static java.util.Arrays.asList;
import static java.util.Collections.EMPTY_LIST;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class CashFlowValidatorTest {
    private CashFlowValidator cashFlowValidator;

    @Mock
    private ValidationContext validationContext;

    @Mock
    private MessageContext messageContext;

    @Before
    public void setUp() throws Exception {
        cashFlowValidator = new CashFlowValidator();
    }

    @Test
    public void validateCaptureCashFlow() throws Exception {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(EMPTY_LIST);
        cashFlowDetail.setTotalCapital(new BigDecimal(123d));
        cashFlowDetail.setTotalLiability(new BigDecimal(456d));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, true, null, 0d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext, never()).addMessage(Matchers.<MessageResolver>anyObject());
    }

    @Test
    public void doNotValidateTotalCapitalAndLiabilityIfCaptureCapitalLiabilityInfoIsNotSet() throws Exception {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(EMPTY_LIST);
        cashFlowDetail.setTotalCapital(null);
        cashFlowDetail.setTotalLiability(null);
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, false, null, 0d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext, never()).addMessage(Matchers.<MessageResolver>anyObject());
    }

    @Test
    public void validateCaptureCashFlowWhenTotalCapitalIsZero() throws Exception {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(EMPTY_LIST);
        cashFlowDetail.setTotalCapital(new BigDecimal(0));
        cashFlowDetail.setTotalLiability(new BigDecimal(0));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, true, null, 0d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.TOTAL_CAPITAL_SHOULD_BE_GREATER_THAN_ZERO)));
    }

    @Test
    public void validateCaptureCashFlowWhenTotalCapitalAndLiabilityIsNull() throws Exception {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(EMPTY_LIST);
        cashFlowDetail.setTotalCapital(null);
        cashFlowDetail.setTotalLiability(null);
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, true, null, 0d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.TOTAL_CAPITAL_SHOULD_NOT_BE_EMPTY)));
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.TOTAL_LIABILITY_SHOULD_NOT_BE_EMPTY)));
    }

    @Test
    public void validateCaptureCashFlowWhenTotalCapitalAndTotalLiabilityIsNegative() throws Exception {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(EMPTY_LIST);
        cashFlowDetail.setTotalCapital(new BigDecimal(-23));
        cashFlowDetail.setTotalLiability(new BigDecimal(-1));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, true, null, 0d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.TOTAL_CAPITAL_SHOULD_BE_GREATER_THAN_ZERO)));
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.TOTAL_LIABILITY_SHOULD_BE_NON_NEGATIVE)));
    }

    @Test
    public void validateForCalculatedIndebtednessRateGreaterThanOrEqualToIndebtednessRate() {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(EMPTY_LIST);
        cashFlowDetail.setTotalCapital(new BigDecimal(100d));
        cashFlowDetail.setTotalLiability(new BigDecimal(5d));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, true, new BigDecimal(1000d), 1000d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.INDEBTEDNESS_RATIO_MORE_THAN_ALLOWED)));
    }


    @Test
    public void indebtednessRateCalculationShouldNotThrowAnyExceptionsOnUnterminatedDecimalPlaceCalculation() {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(EMPTY_LIST);
        cashFlowDetail.setTotalCapital(new BigDecimal(33d));
        cashFlowDetail.setTotalLiability(new BigDecimal(5d));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, true, new BigDecimal(1000d), 1000d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.INDEBTEDNESS_RATIO_MORE_THAN_ALLOWED)));
    }

    @Test
    public void shouldNotValidateForCalculatedIndebtednessRateLessThanIndebtednessRate() {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(EMPTY_LIST);
        cashFlowDetail.setTotalCapital(new BigDecimal(1000d));
        cashFlowDetail.setTotalLiability(new BigDecimal(5d));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, true, new BigDecimal(1000d), 1000d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext, never()).addMessage(Matchers.<MessageResolver>anyObject());
    }

    @Test
    public void shouldComputeTotalExpenseAndTotalRevenueForAllMonthlyCashFlows() {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.hasErrorMessages()).thenReturn(false);
        MonthlyCashFlowDetail cashFlowDetail1 = new MonthlyCashFlowDetail(new DateTime(), new BigDecimal(12), new BigDecimal(12), "notes");
        MonthlyCashFlowDetail cashFlowDetail2 = new MonthlyCashFlowDetail(new DateTime(), new BigDecimal(23), new BigDecimal(34), "notes");
        MonthlyCashFlowDetail cashFlowDetail3 = new MonthlyCashFlowDetail(new DateTime(), new BigDecimal(20), new BigDecimal(30), "notes");
        CashFlowDetail cashFlowDetail = new CashFlowDetail(asList(cashFlowDetail1, cashFlowDetail2, cashFlowDetail3));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, false, new BigDecimal(1000), 10d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        assertThat(cashFlowForm.getTotalExpenses().doubleValue(), is(76d));
        assertThat(cashFlowForm.getTotalRevenues().doubleValue(), is(55d));
        verify(messageContext, times(2)).hasErrorMessages();
        verify(validationContext).getMessageContext();
    }

    @Test
    public void cumulativeCashFlowShouldBeGreaterThanZero() {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.hasErrorMessages()).thenReturn(false);
        DateTime may = new DateTime(2001, 5, 12, 0, 0, 0, 0);
        DateTime june = new DateTime(2001, 6, 12, 0, 0, 0, 0);
        DateTime july = new DateTime(2001, 7, 12, 0, 0, 0, 0);
        MonthlyCashFlowDetail cashFlowDetail1 = new MonthlyCashFlowDetail(may, new BigDecimal(12), new BigDecimal(13), "notes");
        MonthlyCashFlowDetail cashFlowDetail2 = new MonthlyCashFlowDetail(june, new BigDecimal(120), new BigDecimal(12), "notes");
        MonthlyCashFlowDetail cashFlowDetail3 = new MonthlyCashFlowDetail(july, new BigDecimal(1), new BigDecimal(108), "notes");
        CashFlowDetail cashFlowDetail = new CashFlowDetail(asList(cashFlowDetail1, cashFlowDetail2, cashFlowDetail3));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, false, new BigDecimal(1000), 10d);
        assertEquals(new BigDecimal("-1"), cashFlowForm.getMonthlyCashFlows().get(0).getCumulativeCashFlow());
        assertEquals(new BigDecimal("107"), cashFlowForm.getMonthlyCashFlows().get(1).getCumulativeCashFlow());
        assertEquals(new BigDecimal("0"), cashFlowForm.getMonthlyCashFlows().get(2).getCumulativeCashFlow());
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(messageContext, times(2)).hasErrorMessages();
        verify(validationContext).getMessageContext();
        verify(messageContext, times(2)).addMessage(argThat(new MessageMatcher(CashFlowConstants.CUMULATIVE_CASH_FLOW_FOR_MONTH_SHOULD_BE_GREATER_THAN_ZERO)));
    }

    @Test
    public void doNotValidateCumulativeCashIfRevenueOrExpenseNotGiven() {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.hasErrorMessages()).thenReturn(true);
        DateTime may = new DateTime(2001, 5, 12, 0, 0, 0, 0);
        DateTime june = new DateTime(2001, 6, 12, 0, 0, 0, 0);
        DateTime july = new DateTime(2001, 7, 12, 0, 0, 0, 0);
        MonthlyCashFlowDetail cashFlowDetail1 = new MonthlyCashFlowDetail(may, new BigDecimal(12), new BigDecimal(13), "notes");
        MonthlyCashFlowDetail cashFlowDetail2 = new MonthlyCashFlowDetail(june, null, null, "notes");
        CashFlowDetail cashFlowDetail = new CashFlowDetail(asList(cashFlowDetail1, cashFlowDetail2));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail, false, new BigDecimal(1000), 10d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(messageContext, times(2)).hasErrorMessages();
        verify(validationContext).getMessageContext();
        verify(messageContext, never()).addMessage(argThat(new MessageMatcher(CashFlowConstants.CUMULATIVE_CASH_FLOW_FOR_MONTH_SHOULD_BE_GREATER_THAN_ZERO)));
    }

}
