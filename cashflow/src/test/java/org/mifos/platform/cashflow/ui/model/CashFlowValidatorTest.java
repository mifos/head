package org.mifos.platform.cashflow.ui.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.matchers.MessageMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.validation.ValidationContext;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

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
        CashFlowDetail cashFlowDetail = new CashFlowDetail(Collections.EMPTY_LIST);
        cashFlowDetail.setTotalCapital(new BigDecimal(123d));
        cashFlowDetail.setTotalLiability(new BigDecimal(456d));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail,true,null, 0d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext,never()).addMessage(Matchers.<MessageResolver>anyObject());
    }

    @Test
    public void doNotValidateTotalCapitalAndLiabilityIfCaptureCapitalLiabilityInfoIsNotSet() throws Exception {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(Collections.EMPTY_LIST);
        cashFlowDetail.setTotalCapital(null);
        cashFlowDetail.setTotalLiability(null);
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail,false,null, 0d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext,never()).addMessage(Matchers.<MessageResolver>anyObject());
    }

    @Test
    public void validateCaptureCashFlowWhenTotalCapitalIsZero() throws Exception {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(Collections.EMPTY_LIST);
        cashFlowDetail.setTotalCapital(new BigDecimal(0));
        cashFlowDetail.setTotalLiability(new BigDecimal(0));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail,true,null, 0d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.TOTAL_CAPITAL_SHOULD_BE_GREATER_THAN_ZERO)));
    }
    
    @Test
    public void validateCaptureCashFlowWhenTotalCapitalAndLiabilityIsNull() throws Exception {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(Collections.EMPTY_LIST);
        cashFlowDetail.setTotalCapital(null);
        cashFlowDetail.setTotalLiability(null);
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail,true,null, 0d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.TOTAL_CAPITAL_SHOULD_NOT_BE_EMPTY)));
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.TOTAL_LIABILITY_SHOULD_NOT_BE_EMPTY)));
    }

    @Test
    public void validateCaptureCashFlowWhenTotalCapitalAndTotalLiabilityIsNegative() throws Exception {
        when(validationContext.getMessageContext()).thenReturn(messageContext);
        CashFlowDetail cashFlowDetail = new CashFlowDetail(Collections.EMPTY_LIST);
        cashFlowDetail.setTotalCapital(new BigDecimal(-23));
        cashFlowDetail.setTotalLiability(new BigDecimal(-1));
        CashFlowForm cashFlowForm = new CashFlowForm(cashFlowDetail,true,null, 0d);
        cashFlowValidator.validateCaptureCashFlow(cashFlowForm, validationContext);
        verify(validationContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.TOTAL_CAPITAL_SHOULD_BE_NON_NEGATIVE)));
        verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.TOTAL_LIABILITY_SHOULD_BE_NON_NEGATIVE)));
    }
}
