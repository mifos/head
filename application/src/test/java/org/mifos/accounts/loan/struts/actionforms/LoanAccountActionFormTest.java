/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounts.loan.struts.actionforms;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.mifos.accounts.loan.util.helpers.LoanAccountActionFormTestConstants.LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_100;
import static org.mifos.accounts.loan.util.helpers.LoanAccountActionFormTestConstants.LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_AND_PURPOSE_NULL;
import static org.mifos.accounts.loan.util.helpers.LoanAccountActionFormTestConstants.LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_AND_PURPOSE_NULL2;
import static org.mifos.accounts.loan.util.helpers.LoanAccountActionFormTestConstants.LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_NULL;
import static org.mifos.accounts.loan.util.helpers.LoanAccountActionFormTestConstants.LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_ZERO;
import static org.mifos.accounts.loan.util.helpers.LoanAccountActionFormTestConstants.LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_EMPTY;
import static org.mifos.accounts.loan.util.helpers.LoanAccountActionFormTestConstants.LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_NULL;
import static org.mifos.accounts.loan.util.helpers.LoanAccountActionFormTestConstants.LOAN_ACCOUNT_DETAILS_WITH_VALID_PURPOSE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.loan.struts.uihelpers.PaymentDataHtmlBean;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.productdefinition.business.AmountRange;
import org.mifos.accounts.productdefinition.business.LoanAmountSameForAllLoanBO;
import org.mifos.accounts.productdefinition.business.NoOfInstallSameForAllLoanBO;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoanAccountActionFormTest {

    private LoanAccountActionForm form;
    private PaymentDataHtmlBean paymentMock;
    private ActionErrors actionErrors;
    private static final String INTEREST_ERROR_KEY = "interest.invalid";
    private static final String AMOUNT_ERROR_KEY = "amount.invalid";
    private RepaymentScheduleInstallmentBuilder installmentBuilder;
    private Locale locale;
    private MifosCurrency rupee;

    @Before
    public void setUp() throws Exception {
        form = new LoanAccountActionForm();
        paymentMock = createMock(PaymentDataHtmlBean.class);
        expect(paymentMock.hasTotalAmount()).andReturn(true);
        actionErrors = new ActionErrors();
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
    }

    @Test
    public void testShouldAddErrorIfTransactionDateForAPaymentIsInFuture() throws Exception {
        expect(paymentMock.getTransactionDate()).andReturn(DateUtils.getDateFromToday(1));
        replay(paymentMock);
        ActionErrors errors = new ActionErrors();
        form.validateTransactionDate(errors, paymentMock, DateUtils.getDateFromToday(-3));
        verify(paymentMock);
        assertErrorKey(errors, LoanExceptionConstants.INVALIDTRANSACTIONDATEFORPAYMENT);
    }
    @Test
    public void testShouldNotAddErrorIfTransactionDateForAPaymentIsToday() {
        validateForNoErrorsOnDate(DateUtils.currentDate());
    }
    @Test
    public void testShouldNotAddErrorIfTransactionDateForAPaymentIsInPast() {
        validateForNoErrorsOnDate(DateUtils.getDateFromToday(-1));
    }
    @Test
    public void testShouldAddErrorWhenTransactionDateOnDisbursementDate() throws Exception {
        validateWhenTransactionDateInvalidForDisbursementDate(DateUtils.currentDate());
    }
    @Test
    public void testShouldAddErrorIfTransactionDateBeforeDisbursementDate() throws Exception {
        validateWhenTransactionDateInvalidForDisbursementDate(DateUtils.getDateFromToday(1));
    }
    @Test
    public void testShouldNotAddErrorIfTransactionDateAfterDisbursementDate() throws Exception {
        expect(paymentMock.getTransactionDate()).andReturn(DateUtils.currentDate()).anyTimes();
        replay(paymentMock);
        ActionErrors errors = new ActionErrors();
        form.validateTransactionDate(errors, paymentMock, DateUtils.getDateFromToday(-1));
        verify(paymentMock);
       Assert.assertTrue(errors.isEmpty());
    }

    private void validateForNoErrorsOnDate(Date transactionDate) {
            expect(paymentMock.getTransactionDate()).andReturn(transactionDate).anyTimes();
            replay(paymentMock);
            ActionErrors errors = new ActionErrors();
            form.validateTransactionDate(errors, paymentMock, DateUtils.getDateFromToday(-3));
            verify(paymentMock);
           Assert.assertTrue(errors.isEmpty());
    }

    private void assertErrorKey(ActionErrors errors, String expectedErrorKey) {
       Assert.assertEquals(1, errors.size());
       Assert.assertEquals(expectedErrorKey, ((ActionMessage) errors.get().next()).getKey());
    }

    private void validateWhenTransactionDateInvalidForDisbursementDate(Date disbursementDate) {
        expect(paymentMock.getTransactionDate()).andReturn(DateUtils.currentDate()).anyTimes();
        replay(paymentMock);
        ActionErrors errors = new ActionErrors();
        form.validateTransactionDate(errors, paymentMock, disbursementDate);
        verify(paymentMock);
        assertErrorKey(errors, LoanExceptionConstants.INVALIDTRANSACTIONDATE);
    }

    public void testShouldAddErrorIfInstallmentNotBetweenSpecifiedInstallments() throws Exception {
        assertForInterestError("4");
    }

    public void testShouldAddErrorIfInputValueIsNull() throws Exception {
        assertForInterestError(null);
    }

    public void testShouldAddErrorIfInputValueIsBlank() throws Exception {
        assertForInterestError(EMPTY);
    }

    private void assertForInterestError(String inputValue) {
        new LoanAccountActionForm().checkForMinMax(actionErrors, inputValue, new NoOfInstallSameForAllLoanBO((short) 1,
                (short) 3, (short) 2, null), INTEREST_ERROR_KEY);
       Assert.assertEquals(1, actionErrors.size());
        ActionMessage message = (ActionMessage) actionErrors.get(INTEREST_ERROR_KEY).next();
        Assert.assertNotNull(message);
       Assert.assertEquals(LoanExceptionConstants.INVALIDMINMAX, message.getKey());
    }

    public void testShouldNotErrorIfInstallmentBetweenSpecifiedValues() throws Exception {
        new LoanAccountActionForm().checkForMinMax(actionErrors, "2", new NoOfInstallSameForAllLoanBO((short) 1,
                (short) 3, (short) 2, null), INTEREST_ERROR_KEY);
       Assert.assertEquals(0, actionErrors.size());
    }

    public void testShouldAddErrorIfAmountNotBetweenSpecifiedLoanAmountRanges() throws Exception {
        assertForAmountError("4");
    }

    public void testShouldAddErrorIfInputAmountIsNull() throws Exception {
        assertForAmountError(null);
    }

    public void testShouldAddErrorIfInputAmountIsBlank() throws Exception {
        assertForAmountError(EMPTY);
    }

    private void assertForAmountError(String inputValue) {
        new LoanAccountActionForm().checkForMinMax(actionErrors, inputValue, new LoanAmountSameForAllLoanBO((double) 1,
                (double) 3, (double) 2, null), AMOUNT_ERROR_KEY);
       Assert.assertEquals(1, actionErrors.size());
        ActionMessage message = (ActionMessage) actionErrors.get(AMOUNT_ERROR_KEY).next();
        Assert.assertNotNull(message);
       Assert.assertEquals(LoanExceptionConstants.INVALIDMINMAX, message.getKey());
    }

    public void testShouldNotErrorIfAmountBetweenSpecifiedValues() throws Exception {
        new LoanAccountActionForm().checkForMinMax(actionErrors, "2", new LoanAmountSameForAllLoanBO((double) 1,
                (double) 3, (double) 2, null), AMOUNT_ERROR_KEY);
       Assert.assertEquals(0, actionErrors.size());
    }

    public void testShouldAddErrorIfNoMembersSelected() throws Exception {
        form.validateSelectedClients(actionErrors);
       Assert.assertEquals(1, actionErrors.size());
    }

    public void testShouldValidateIfMembersSelected() throws Exception {
        form.setClients(asList("1", "2"));
        form.validateSelectedClients(actionErrors);
       Assert.assertEquals(0, actionErrors.size());
    }

    public void testShouldAddErrorIfIndividualLoanAmountIsNull() throws Exception {
        form.setClientDetails(asList(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_NULL));
        form.setClients(asList("1"));
        form.validateSumOfTheAmountsSpecified(actionErrors);
       Assert.assertEquals(1, actionErrors.size());
    }

    public void testShouldValidateIfIndividualLoanAmountIsNotZero() throws Exception {
        AmountRange loanAmountRange = new LoanAmountSameForAllLoanBO();
        loanAmountRange.setMinLoanAmount(100d);
        loanAmountRange.setMaxLoanAmount(1000d);
        form.setClientDetails(asList(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_100));
        form.setLoanAmountRange(loanAmountRange);
        form.setClients(asList("1"));
        form.validateSumOfTheAmountsSpecified(actionErrors);
       Assert.assertEquals(0, actionErrors.size());
    }

    public void testShouldAddErrorIfIndividualLoanAmountIsZero() throws Exception {
        form.setClientDetails(asList(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_ZERO));
        form.setClients(asList("1"));
        form.validateSumOfTheAmountsSpecified(actionErrors);
       Assert.assertEquals(1, actionErrors.size());
    }

    public void testShouldAddErrorIfPurposeOfLoanIsNull() throws Exception {
        FieldConfigurationEntity fieldConfigMock = createMock(FieldConfigurationEntity.class);
        expect(fieldConfigMock.getFieldName()).andReturn(LoanConstants.PURPOSE_OF_LOAN);
        replay(fieldConfigMock);
        form.setClientDetails(asList(LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_NULL));
        form.setClients(asList("1"));
        form.validatePurposeOfLoanForGlim(actionErrors, asList(fieldConfigMock));
        Assert.assertEquals(1, actionErrors.size());
        verify(fieldConfigMock);
    }

    public void testShouldntAddErrorIfPurposeOfLoanIsNull() throws Exception {
        form.setClientDetails(asList(LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_NULL));
        form.setClients(asList("1"));
        form.validatePurposeOfLoanForGlim(actionErrors, Collections.<FieldConfigurationEntity>emptyList());
        Assert.assertEquals(0, actionErrors.size());
    }

    public void testShouldAddErrorIfPurposeOfLoanIsEmpty() throws Exception {
        FieldConfigurationEntity fieldConfigMock = createMock(FieldConfigurationEntity.class);
        expect(fieldConfigMock.getFieldName()).andReturn(LoanConstants.PURPOSE_OF_LOAN);
        replay(fieldConfigMock);
        form.setClientDetails(asList(LOAN_ACCOUNT_DETAILS_WITH_VALID_PURPOSE,
                LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_EMPTY));
        form.setClients(asList("1"));
        form.validatePurposeOfLoanForGlim(actionErrors, asList(fieldConfigMock));
        Assert.assertEquals(1, actionErrors.size());
        verify(fieldConfigMock);
    }

    public void testShouldntAddErrorIfPurposeOfLoanIsEmpty() throws Exception {
        form.setClientDetails(asList(LOAN_ACCOUNT_DETAILS_WITH_VALID_PURPOSE,
                LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_EMPTY));
        form.setClients(asList("1"));
        form.validatePurposeOfLoanForGlim(actionErrors, Collections.<FieldConfigurationEntity>emptyList());
        Assert.assertEquals(0, actionErrors.size());
    }

    public void testShouldValidateIfPurposeOfLoanIsValid() throws Exception {
        FieldConfigurationEntity fieldConfigMock = createMock(FieldConfigurationEntity.class);
        expect(fieldConfigMock.getFieldName()).andReturn(LoanConstants.PURPOSE_OF_LOAN);
        replay(fieldConfigMock);
        form.setClientDetails(asList(LOAN_ACCOUNT_DETAILS_WITH_VALID_PURPOSE));
        form.setClients(asList("1"));
        form.validatePurposeOfLoanForGlim(actionErrors, asList(fieldConfigMock));
        Assert.assertEquals(0, actionErrors.size());
        verify(fieldConfigMock);
    }

    public void testShouldRemoveClientDetailsIfNoMatchingEntryFoundInClients() throws Exception {
        form.setClientDetails(asList(LOAN_ACCOUNT_DETAILS_WITH_VALID_PURPOSE,
                LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_AND_PURPOSE_NULL,
                LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_AND_PURPOSE_NULL2));
        form.setClients(asList("1"));
        form.removeClientDetailsWithNoMatchingClients();
       Assert.assertEquals(1, form.getClientDetails().size());
    }

    public void testShouldGetSelectedClientIdsFromRequest() throws Exception {
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameterNames()).andReturn(
                Collections.enumeration(asList("clients[0]", "clients[1]", "clients[2]")));
        expect(requestMock.getParameter("clients[0]")).andReturn("1");
        expect(requestMock.getParameter("clients[1]")).andReturn("2");
        expect(requestMock.getParameter("clients[2]")).andReturn("3");
        replay(requestMock);
       Assert.assertEquals(asList("1", "2", "3"), form.getSelectedClientIdsFromRequest(requestMock));
        verify(requestMock);
    }

    public void testSetsEmptyStringForClientsNotMatchingInput() throws Exception {
        form.setClients(asList("1", "2", "3", "4"));
        form.setClientsNotPresentInInputToEmptyString(asList("1", "2", "3"));
       Assert.assertEquals(asList("1", "2", "3", ""), form.getClients());
    }

    public void testValidateLoanAmount()  {
        form.setLoanAmount("5000.0");
        ActionErrors errors = new ActionErrors();
        form.validateLoanAmount(errors, Locale.ENGLISH, TestUtils.RUPEE);
        Assert.assertEquals("No Error was expected",0, errors.size());
    }

    public void testValidateAdditionalFeesWithMultipleInstancesOfTheSameOneTimeFee() throws Exception {
        List<FeeDto> additionalFeeList = createDefaultFees();
        List<FeeDto> additionalFeeList2 = createDefaultFees();
        additionalFeeList2.add(additionalFeeList.get(0));
        FlowManager flowMgrMock = createMock(FlowManager.class);
        HttpSession sessionMock = createMock(HttpSession.class);
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getSession()).andReturn(sessionMock);
        expect(requestMock.getAttribute(Constants.CURRENTFLOWKEY)).andReturn("currentFlowKey");
        expect(sessionMock.getAttribute(Constants.FLOWMANAGER)).andReturn(flowMgrMock);
        expect(flowMgrMock.getFromFlow("currentFlowKey", LoanConstants.ADDITIONAL_FEES_LIST)).andReturn(additionalFeeList);
        ActionErrors errors = new ActionErrors();
        form.validateAdditionalFee(errors, Locale.ENGLISH, TestUtils.RUPEE, requestMock);
        Assert.assertEquals("There should be a 'Multiple instances of the same one-time fee are not allowed' error",
                1, errors.size());
        ActionMessage actionMessage = (ActionMessage)errors.get().next();
        Assert.assertEquals("There should be a 'Multiple instances of the same one-time fee are not allowed' error",
                ProductDefinitionConstants.MULTIPLE_ONE_TIME_FEES_NOT_ALLOWED, actionMessage.getKey());

    }

    /**
     * ignored cause failing when applicationConfiguration.custom.properties is changed.
     */
    @Ignore
    @Test
    public void testValidateDefaultFees()  {
        Short saveDigitsAfterDecimal = AccountingRules.getDigitsAfterDecimal();
        AccountingRules.setDigitsAfterDecimal(Short.valueOf("0"));
        form.setDefaultFees(createDefaultFees());
        ActionErrors errors = new ActionErrors();
        form.validateDefaultFee(errors, Locale.ENGLISH, TestUtils.RUPEE);
        Assert.assertEquals("No Error was expected",0, errors.size());
        AccountingRules.setDigitsAfterDecimal(saveDigitsAfterDecimal);
    }

    public void testValidatePaymentDatesOrdering(){
        ActionErrors actionErrors;

        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "49")).withTotalValue("50").withDueDateValue("01-Feb-2010").withTotalValue("522.0").withInstallment(1).build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "49")).withTotalValue("50").withDueDateValue("01-Mar-2010").withTotalValue("522.0").withInstallment(2).build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "49")).withTotalValue("50").withDueDateValue("01-Apr-2010").withTotalValue("522.0").withInstallment(3).build();
        RepaymentScheduleInstallment installment4 = installmentBuilder.reset(locale).withPrincipal(new Money(rupee, "49")).withTotalValue("50").withDueDateValue("01-May-2010").withTotalValue("522.0").withInstallment(4).build();

        PaymentDataHtmlBean paymentDataHtmlBean1 = new PaymentDataHtmlBean(locale, null, installment1);
        paymentDataHtmlBean1.setDate("01-Jan-2010");
        PaymentDataHtmlBean paymentDataHtmlBean2 = new PaymentDataHtmlBean(locale, null, installment2);
        paymentDataHtmlBean2.setDate("01-Mar-2010");
        PaymentDataHtmlBean paymentDataHtmlBean3 = new PaymentDataHtmlBean(locale, null, installment3);
        paymentDataHtmlBean3.setDate("01-Feb-2010");
        PaymentDataHtmlBean paymentDataHtmlBean4 = new PaymentDataHtmlBean(locale, null, installment4);
        paymentDataHtmlBean4.setDate("01-Apr-2010");

        List<PaymentDataHtmlBean> validPaymentBeans = asList(paymentDataHtmlBean1, paymentDataHtmlBean2,
                                                            paymentDataHtmlBean3, paymentDataHtmlBean4);

        actionErrors = new ActionErrors();
        form.validatePaymentDatesOrdering(validPaymentBeans, actionErrors);
        Assert.assertEquals(1, actionErrors.size());
        ActionMessage actionMessage = (ActionMessage) actionErrors.get().next();
        org.junit.Assert.assertEquals("3", actionMessage.getValues()[0]);

        paymentDataHtmlBean3.setDate("03-Mar-2010");
        actionErrors = new ActionErrors();
        form.validatePaymentDatesOrdering(validPaymentBeans, actionErrors);
        Assert.assertEquals(0, actionErrors.size());
    }

    public void testTransactionDateIsRequiredOnPayment() {
        Money amount = new Money(rupee, "10");
        RepaymentScheduleInstallment installment = new RepaymentScheduleInstallment(1, new Date(), amount, amount,
                amount, amount, amount);
        PaymentDataHtmlBean bean = new PaymentDataHtmlBean(null, null, installment);
        bean.setAmount("10");
        bean.setDate("");
        ActionErrors errors = new ActionErrors();
        form.validateTransactionDateOnPayment(errors, bean);
        Assert.assertEquals(1, errors.size());
        errors = new ActionErrors();
        bean.setDate("10/12/2010");
        form.validateTransactionDateOnPayment(errors, bean);
        Assert.assertEquals(0, errors.size());
    }


    private ArrayList <FeeDto> createDefaultFees() {
        AmountFeeBO amountFee = createMock(AmountFeeBO.class);
        expect(amountFee.getFeeId()).andReturn(Short.valueOf("1"));
        expect(amountFee.getFeeType()).andReturn(RateAmountFlag.AMOUNT).times(2);
        expect(amountFee.getFeeName()).andReturn("TestAmountFee");
        expect(amountFee.getFeeAmount()).andReturn(new Money(TestUtils.RUPEE,"5000.0")).times(2);
        expect(amountFee.isPeriodic()).andReturn(false).times(2);
        replay(amountFee);

        RateFeeBO rateFee = createMock(RateFeeBO.class);
        expect(rateFee.getFeeId()).andReturn(Short.valueOf("1"));
        expect(rateFee.getFeeType()).andReturn(RateAmountFlag.RATE).times(2);
        expect(rateFee.getFeeName()).andReturn("TestRateFee");
        expect(rateFee.getRate()).andReturn(2.12345);
        expect(rateFee.getFeeFormula()).andReturn(createFeeFormulaEntityMock());
        expect(rateFee.isPeriodic()).andReturn(false).times(2);
        replay(rateFee);

        UserContext userContext = createMock(UserContext.class);
        expect(userContext.getLocaleId()).andReturn(Short.valueOf("1")).times(2);
        replay(userContext);
        ArrayList <FeeDto> defaultFees = new ArrayList<FeeDto>();
        defaultFees.add(new FeeDto(userContext, amountFee));
        defaultFees.add(new FeeDto(userContext, rateFee));
        return defaultFees;
    }

    private FeeFormulaEntity createFeeFormulaEntityMock() {
        FeeFormulaEntity feeFormulaEntity = createMock(FeeFormulaEntity.class);
        expect(feeFormulaEntity.getFeeFormula()).andReturn(FeeFormula.INTEREST);
        expect(feeFormulaEntity.getFormulaString()).andReturn("FormulaString");
        replay(feeFormulaEntity);
        return feeFormulaEntity;
    }

    @Test
    public void shouldGetApplicableFees() {
        form.setDefaultFees(Arrays.asList(getFee("DefaultFee1"), getFee(null), getFee("DefaultFee3")));
        form.setAdditionalFees(Arrays.asList(getFee("AdditionalFee1"), getFee("")));
        List<FeeDto> applicableFees = form.getApplicableFees();
        Assert.assertNotNull(applicableFees);
        Assert.assertEquals(3, applicableFees.size());
        Assert.assertEquals("DefaultFee1", applicableFees.get(0).getFeeId());
        Assert.assertEquals("DefaultFee3", applicableFees.get(1).getFeeId());
        Assert.assertEquals("AdditionalFee1", applicableFees.get(2).getFeeId());
    }

    private FeeDto getFee(String feeId) {
        FeeDto feeDto = new FeeDto();
        feeDto.setFeeId(feeId);
        return feeDto;
    }
}
