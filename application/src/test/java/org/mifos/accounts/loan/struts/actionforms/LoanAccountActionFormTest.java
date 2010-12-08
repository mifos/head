/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.accounts.productdefinition.business.AmountRange;
import org.mifos.accounts.productdefinition.business.LoanAmountSameForAllLoanBO;
import org.mifos.accounts.productdefinition.business.NoOfInstallSameForAllLoanBO;
import org.mifos.accounts.util.helpers.PaymentDataTemplate;
import org.mifos.config.AccountingRules;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntity;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;


public class LoanAccountActionFormTest extends TestCase {

    private LoanAccountActionForm form;
    private PaymentDataTemplate paymentMock;
    private ActionErrors actionErrors;
    private static final String INTEREST_ERROR_KEY = "interest.invalid";
    private static final String AMOUNT_ERROR_KEY = "amount.invalid";

    public void testShouldAddErrorIfTransactionDateForAPaymentIsInFuture() throws InvalidDateException {
        expect(paymentMock.getTransactionDate()).andReturn(DateUtils.getDateFromToday(1));
        replay(paymentMock);
        ActionErrors errors = new ActionErrors();
        form.validateTransactionDate(errors, paymentMock, DateUtils.getDateFromToday(-3));
        verify(paymentMock);
        assertErrorKey(errors, LoanExceptionConstants.INVALIDTRANSACTIONDATEFORPAYMENT);
    }

    public void testShouldNotAddErrorIfTransactionDateForAPaymentIsToday() throws InvalidDateException {
        validateForNoErrorsOnDate(DateUtils.currentDate());
    }

    public void testShouldNotAddErrorIfTransactionDateForAPaymentIsInPast() throws InvalidDateException {
        validateForNoErrorsOnDate(DateUtils.getDateFromToday(-1));
    }

    public void testShouldAddErrorWhenTransactionDateOnDisbursementDate() throws Exception {
        validateWhenTransactionDateInvalidForDisbursementDate(DateUtils.currentDate());
    }

    public void testShouldAddErrorIfTransactionDateBeforeDisbursementDate() throws Exception {
        validateWhenTransactionDateInvalidForDisbursementDate(DateUtils.getDateFromToday(1));
    }

    public void testShouldNotAddErrorIfTransactionDateAfterDisbursementDate() throws Exception {
        expect(paymentMock.getTransactionDate()).andReturn(DateUtils.currentDate()).anyTimes();
        replay(paymentMock);
        ActionErrors errors = new ActionErrors();
        form.validateTransactionDate(errors, paymentMock, DateUtils.getDateFromToday(-1));
        verify(paymentMock);
       Assert.assertTrue(errors.isEmpty());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        form = new LoanAccountActionForm();
        paymentMock = createMock(PaymentDataTemplate.class);
        expect(paymentMock.getTotalAmount()).andReturn(new Money(TestUtils.RUPEE));
        actionErrors = new ActionErrors();

    }

    private void validateForNoErrorsOnDate(Date transactionDate) throws InvalidDateException {
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

    private void validateWhenTransactionDateInvalidForDisbursementDate(Date disbursementDate) throws InvalidDateException {
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
        form.setClients(Arrays.asList("1", "2"));
        form.validateSelectedClients(actionErrors);
       Assert.assertEquals(0, actionErrors.size());
    }

    public void testShouldAddErrorIfIndividualLoanAmountIsNull() throws Exception {
        form.setClientDetails(Arrays.asList(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_NULL));
        form.setClients(Arrays.asList("1"));
        form.validateSumOfTheAmountsSpecified(actionErrors);
       Assert.assertEquals(1, actionErrors.size());
    }

    public void testShouldValidateIfIndividualLoanAmountIsNotZero() throws Exception {
        AmountRange loanAmountRange = new LoanAmountSameForAllLoanBO();
        loanAmountRange.setMinLoanAmount(100d);
        loanAmountRange.setMaxLoanAmount(1000d);
        form.setClientDetails(Arrays.asList(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_100));
        form.setLoanAmountRange(loanAmountRange);
        form.setClients(Arrays.asList("1"));
        form.validateSumOfTheAmountsSpecified(actionErrors);
       Assert.assertEquals(0, actionErrors.size());
    }

    public void testShouldAddErrorIfIndividualLoanAmountIsZero() throws Exception {
        form.setClientDetails(Arrays.asList(LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_ZERO));
        form.setClients(Arrays.asList("1"));
        form.validateSumOfTheAmountsSpecified(actionErrors);
       Assert.assertEquals(1, actionErrors.size());
    }

    public void testShouldAddErrorIfPurposeOfLoanIsNull() throws Exception {
        FieldConfigurationEntity fieldConfigMock = createMock(FieldConfigurationEntity.class);
        expect(fieldConfigMock.getFieldName()).andReturn(LoanConstants.PURPOSE_OF_LOAN);
        replay(fieldConfigMock);
        form.setClientDetails(Arrays.asList(LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_NULL));
        form.setClients(Arrays.asList("1"));
        form.validatePurposeOfLoanForGlim(actionErrors, Arrays.asList(fieldConfigMock));
        Assert.assertEquals(1, actionErrors.size());
        verify(fieldConfigMock);
    }

    public void testShouldntAddErrorIfPurposeOfLoanIsNull() throws Exception {
        form.setClientDetails(Arrays.asList(LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_NULL));
        form.setClients(Arrays.asList("1"));
        form.validatePurposeOfLoanForGlim(actionErrors, Collections.<FieldConfigurationEntity>emptyList());
        Assert.assertEquals(0, actionErrors.size());
    }

    public void testShouldAddErrorIfPurposeOfLoanIsEmpty() throws Exception {
        FieldConfigurationEntity fieldConfigMock = createMock(FieldConfigurationEntity.class);
        expect(fieldConfigMock.getFieldName()).andReturn(LoanConstants.PURPOSE_OF_LOAN);
        replay(fieldConfigMock);
        form.setClientDetails(Arrays.asList(LOAN_ACCOUNT_DETAILS_WITH_VALID_PURPOSE,
                LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_EMPTY));
        form.setClients(Arrays.asList("1"));
        form.validatePurposeOfLoanForGlim(actionErrors, Arrays.asList(fieldConfigMock));
        Assert.assertEquals(1, actionErrors.size());
        verify(fieldConfigMock);
    }

    public void testShouldntAddErrorIfPurposeOfLoanIsEmpty() throws Exception {
        form.setClientDetails(Arrays.asList(LOAN_ACCOUNT_DETAILS_WITH_VALID_PURPOSE,
                LOAN_ACCOUNT_DETAILS_WITH_PURPOSE_EMPTY));
        form.setClients(Arrays.asList("1"));
        form.validatePurposeOfLoanForGlim(actionErrors, Collections.<FieldConfigurationEntity>emptyList());
        Assert.assertEquals(0, actionErrors.size());
    }

    public void testShouldValidateIfPurposeOfLoanIsValid() throws Exception {
        FieldConfigurationEntity fieldConfigMock = createMock(FieldConfigurationEntity.class);
        expect(fieldConfigMock.getFieldName()).andReturn(LoanConstants.PURPOSE_OF_LOAN);
        replay(fieldConfigMock);
        form.setClientDetails(Arrays.asList(LOAN_ACCOUNT_DETAILS_WITH_VALID_PURPOSE));
        form.setClients(Arrays.asList("1"));
        form.validatePurposeOfLoanForGlim(actionErrors, Arrays.asList(fieldConfigMock));
        Assert.assertEquals(0, actionErrors.size());
        verify(fieldConfigMock);
    }

    public void testShouldRemoveClientDetailsIfNoMatchingEntryFoundInClients() throws Exception {
        form.setClientDetails(Arrays.asList(LOAN_ACCOUNT_DETAILS_WITH_VALID_PURPOSE,
                LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_AND_PURPOSE_NULL,
                LOAN_ACCOUNT_DETAILS_WITH_LOAN_AMOUNT_AND_PURPOSE_NULL2));
        form.setClients(Arrays.asList("1"));
        form.removeClientDetailsWithNoMatchingClients();
       Assert.assertEquals(1, form.getClientDetails().size());
    }

    public void testShouldGetSelectedClientIdsFromRequest() throws Exception {
        HttpServletRequest requestMock = createMock(HttpServletRequest.class);
        expect(requestMock.getParameterNames()).andReturn(
                Collections.enumeration(Arrays.asList("clients[0]", "clients[1]", "clients[2]")));
        expect(requestMock.getParameter("clients[0]")).andReturn("1");
        expect(requestMock.getParameter("clients[1]")).andReturn("2");
        expect(requestMock.getParameter("clients[2]")).andReturn("3");
        replay(requestMock);
       Assert.assertEquals(Arrays.asList("1", "2", "3"), form.getSelectedClientIdsFromRequest(requestMock));
        verify(requestMock);
    }

    public void testSetsEmptyStringForClientsNotMatchingInput() throws Exception {
        form.setClients(Arrays.asList("1", "2", "3", "4"));
        form.setClientsNotPresentInInputToEmptyString(Arrays.asList("1", "2", "3"));
       Assert.assertEquals(Arrays.asList("1", "2", "3", ""), form.getClients());
    }

    public void testValidateLoanAmount()  {
        form.setLoanAmount("5000.0");
        ActionErrors errors = new ActionErrors();
        form.validateLoanAmount(errors, Locale.ENGLISH, TestUtils.RUPEE);
        Assert.assertEquals("No Error was expected",0, errors.size());
    }

    public void testValidateDefaultFees()  {
        Short saveDigitsAfterDecimal = AccountingRules.getDigitsAfterDecimal();
        AccountingRules.setDigitsAfterDecimal(Short.valueOf("0"));
        form.setDefaultFees(createDefaultFees());
        ActionErrors errors = new ActionErrors();
        form.validateDefaultFee(errors, Locale.ENGLISH, TestUtils.RUPEE);
        Assert.assertEquals("No Error was expected",0, errors.size());
        AccountingRules.setDigitsAfterDecimal(saveDigitsAfterDecimal);
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
        expect(feeFormulaEntity.getFormulaString()).andReturn("FormulaString");
        replay(feeFormulaEntity);
        return feeFormulaEntity;
    }
}
