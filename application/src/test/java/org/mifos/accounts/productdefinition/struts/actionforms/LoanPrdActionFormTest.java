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

package org.mifos.accounts.productdefinition.struts.actionforms;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyEntity;
import org.mifos.accounts.fees.business.RateFeeBO;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.RateAmountFlag;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.MeetingDetailsEntity;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class LoanPrdActionFormTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private FlowManager flowManager;

    @Mock
    private ActionErrors errors;


    @Mock
    RateFeeBO periodicFeeRate;

    @Mock
    FeeBO periodicFeeAmount;


    FeeBO nonPeriodicFeeRate = Mockito.mock(RateFeeBO.class);

    @Mock
    FeeBO oneTimeFee;

    @Mock
    FeeFormulaEntity feeFormulaEntity;

    private LoanPrdActionForm loanPrdActionForm;
    private static final String FLOW_KEY = "FlowKey";

    @Before
    public void setUp() {
        loanPrdActionForm = new LoanPrdActionForm();
    }

    @Test
    public void testApplicableMaster() throws Exception {
        loanPrdActionForm.setPrdApplicableMaster("" + ApplicableTo.CLIENTS.getValue());
        Assert.assertEquals(ApplicableTo.CLIENTS, loanPrdActionForm.getPrdApplicableMasterEnum());
    }

    @Test
    public void testSetFromEnum() throws Exception {
        loanPrdActionForm.setPrdApplicableMaster(ApplicableTo.ALLCUSTOMERS);
        Assert.assertEquals(ApplicableTo.ALLCUSTOMERS, loanPrdActionForm.getPrdApplicableMasterEnum());
    }

    @Test
    public void shouldSetSelectedQuestionGroupsOnSession() throws PageExpiredException {
        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        Flow flow = new Flow();
        when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        List<QuestionGroupDetail> questionGroupDetails = asList(getQuestionGroupDetail(1, "QG1"), getQuestionGroupDetail(2, "QG2"),
                                                            getQuestionGroupDetail(3, "QG3"), getQuestionGroupDetail(4, "QG4"));
        when(flowManager.getFromFlow(FLOW_KEY, ProductDefinitionConstants.SRCQGLIST)).thenReturn(questionGroupDetails);
        loanPrdActionForm.setLoanOfferingQGs(new String[] {"1", "4"});
        loanPrdActionForm.setSelectedQuestionGroups(request);
        List<QuestionGroupDetail> selectedQGDetails = (List<QuestionGroupDetail>) flow.getObjectFromSession(ProductDefinitionConstants.SELECTEDQGLIST);
        assertThat(selectedQGDetails, is(notNullValue()));
        assertThat(selectedQGDetails.get(0).getId(), is(1));
        assertThat(selectedQGDetails.get(0).getTitle(), is("QG1"));
        assertThat(selectedQGDetails.get(1).getId(), is(4));
        assertThat(selectedQGDetails.get(1).getTitle(), is("QG4"));
    }

    @Test
    public void shouldSetSelectedQuestionGroupsOnSessionForNoQGs() throws PageExpiredException {
        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        Flow flow = new Flow();
        when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        List<QuestionGroupDetail> questionGroupDetails = asList(getQuestionGroupDetail(1, "QG1"), getQuestionGroupDetail(2, "QG2"),
                                                            getQuestionGroupDetail(3, "QG3"), getQuestionGroupDetail(4, "QG4"));
        when(flowManager.getFromFlow(FLOW_KEY, ProductDefinitionConstants.SRCQGLIST)).thenReturn(questionGroupDetails);
        loanPrdActionForm.setLoanOfferingQGs(null);
        loanPrdActionForm.setSelectedQuestionGroups(request);
        List<QuestionGroupDetail> selectedQGDetails = (List<QuestionGroupDetail>) flow.getObjectFromSession(ProductDefinitionConstants.SELECTEDQGLIST);
        assertThat(selectedQGDetails, is(notNullValue()));
        assertThat(selectedQGDetails.isEmpty(), is(true));
    }

    private QuestionGroupDetail getQuestionGroupDetail(int id, String title) {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail();
        questionGroupDetail.setId(id);
        questionGroupDetail.setTitle(title);
        return questionGroupDetail;
    }

    @Test
    public void onlyDecliningInterestTypeShouldBeSelectedForVariableInstallmentLoanProduct() {
        ActionMessageMatcher actionMessageMatcher = new ActionMessageMatcher(ProductDefinitionConstants.INVALID_INTEREST_TYPE_FOR_VARIABLE_INSTALLMENT);

        loanPrdActionForm.setCanConfigureVariableInstallments(true);
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verify(errors).add(Mockito.anyString(), Mockito.argThat(actionMessageMatcher));
        Mockito.reset(errors);

        loanPrdActionForm.setInterestTypes(InterestType.FLAT.getValueAsString());
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verify(errors).add(Mockito.anyString(), Mockito.argThat(actionMessageMatcher));
        Mockito.reset(errors);

        loanPrdActionForm.setInterestTypes(InterestType.DECLINING.getValueAsString());
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verifyZeroInteractions(errors);
        Mockito.reset(errors);

        loanPrdActionForm.setInterestTypes(InterestType.COMPOUND.getValueAsString());
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verify(errors).add(Mockito.anyString(), Mockito.argThat(actionMessageMatcher));
        Mockito.reset(errors);

        loanPrdActionForm.setInterestTypes(InterestType.DECLINING_EPI.getValueAsString());
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verify(errors).add(Mockito.anyString(), Mockito.argThat(actionMessageMatcher));
        Mockito.reset(errors);

        loanPrdActionForm.setInterestTypes(InterestType.DECLINING_PB.getValueAsString());
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verify(errors).add(Mockito.anyString(), Mockito.argThat(actionMessageMatcher));
        Mockito.reset(errors);
    }

    @Test
    public void shouldValidateForGracePeriodWithDIPBInterestTypeAndVariableInstallments() {
        ActionMessageMatcher actionMessageMatcher = new ActionMessageMatcher(ProductDefinitionConstants.INVALID_INTEREST_TYPE_FOR_GRACE_PERIODS);

        loanPrdActionForm.setGracePeriodType(GraceType.PRINCIPALONLYGRACE.getValueAsString());
        loanPrdActionForm.setCanConfigureVariableInstallments(true);
        loanPrdActionForm.setInterestTypes(InterestType.FLAT.getValueAsString());
        loanPrdActionForm.validateInterestTypeForGracePeriods(errors, Locale.getDefault());
        Mockito.verify(errors).add(Mockito.anyString(), Mockito.argThat(actionMessageMatcher));
        Mockito.reset(errors);

        loanPrdActionForm.setGracePeriodType(GraceType.PRINCIPALONLYGRACE.getValueAsString());
        loanPrdActionForm.setCanConfigureVariableInstallments(false);
        loanPrdActionForm.setInterestTypes(InterestType.DECLINING_PB.getValueAsString());
        loanPrdActionForm.validateInterestTypeForGracePeriods(errors, Locale.getDefault());
        Mockito.verify(errors).add(Mockito.anyString(), Mockito.argThat(actionMessageMatcher));
        Mockito.reset(errors);

        loanPrdActionForm.setGracePeriodType(GraceType.GRACEONALLREPAYMENTS.getValueAsString());
        loanPrdActionForm.setCanConfigureVariableInstallments(true);
        loanPrdActionForm.setInterestTypes(InterestType.COMPOUND.getValueAsString());
        loanPrdActionForm.validateInterestTypeForGracePeriods(errors, Locale.getDefault());
        Mockito.verify(errors).add(Mockito.anyString(), Mockito.argThat(actionMessageMatcher));
        Mockito.reset(errors);

        loanPrdActionForm.setGracePeriodType(GraceType.NONE.getValueAsString());
        loanPrdActionForm.setCanConfigureVariableInstallments(false);
        loanPrdActionForm.setInterestTypes(InterestType.DECLINING_EPI.getValueAsString());
        loanPrdActionForm.validateInterestTypeForGracePeriods(errors, Locale.getDefault());
        Mockito.verifyZeroInteractions(errors);
        Mockito.reset(errors);
    }

    @Test
    public void shouldNotAllowPeriodicFeeForVariableInstallmentLoanProduct() {
        String PERIODIC_FEE_2 = "2";
        String NON_PERIODIC_FEE = "3";

        when(periodicFeeRate.isPeriodic()).thenReturn(true);
        when(periodicFeeRate.getFeeType()).thenReturn(RateAmountFlag.RATE);
        when(periodicFeeRate.getFeeId()).thenReturn(Short.valueOf(PERIODIC_FEE_2));
        when(periodicFeeRate.getFeeName()).thenReturn("periodic fee2");


        when(nonPeriodicFeeRate.isPeriodic()).thenReturn(false);
        when(nonPeriodicFeeRate.getFeeType()).thenReturn(RateAmountFlag.RATE);
        when(nonPeriodicFeeRate.getFeeId()).thenReturn(Short.valueOf(NON_PERIODIC_FEE));
        when(nonPeriodicFeeRate.getFeeName()).thenReturn("non Periodic fee");
        when(((RateFeeBO)nonPeriodicFeeRate).getFeeFormula()).thenReturn(feeFormulaEntity);
        when(feeFormulaEntity.getFeeFormula()).thenReturn(FeeFormula.INTEREST);


        List<FeeBO> allPrdFees = new ArrayList<FeeBO>();
        allPrdFees.add(periodicFeeRate);
        allPrdFees.add(nonPeriodicFeeRate);


        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        when(session.getAttribute(ProductDefinitionConstants.LOANPRDFEE)).thenReturn(allPrdFees);

        Flow flow = new Flow();
        try {
            when(flowManager.getFromFlow(Mockito.anyString(),Mockito.anyString())).thenReturn(allPrdFees);
            when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        } catch (PageExpiredException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        ActionMessageMatcher actionMessageMatcher = new ActionMessageMatcher(ProductDefinitionConstants.PERIODIC_FEE_NOT_APPLICABLE);

        loanPrdActionForm.setCanConfigureVariableInstallments(true);
        loanPrdActionForm.setPrdOfferinFees(new String[] {PERIODIC_FEE_2, NON_PERIODIC_FEE});

        loanPrdActionForm.validateSelectedFeeForVariableInstallment(request, errors);
        Mockito.verify(errors, Mockito.times(1)).add(Mockito.anyString(), Mockito.argThat(actionMessageMatcher));
    }

    @Test
    public void shouldAllowPeriodicFeeForNonVariableInstallmentLoanProduct() {
        String PERIODIC_FEE_2 = "2";
        String NON_PERIODIC_FEE = "3";



        final FeeDto feeDto = Mockito.mock(FeeDto.class);
        loanPrdActionForm = new LoanPrdActionForm() {
            @Override
            FeeDto getFeeDto(@SuppressWarnings("unused") HttpServletRequest request, @SuppressWarnings("unused") FeeBO fee) {
                return feeDto;
            }
        };

        FeeFrequencyEntity feeFrequencyEntity = Mockito.mock(FeeFrequencyEntity.class);
        MeetingBO meetingBo = Mockito.mock(MeetingBO.class);
        MeetingDetailsEntity meetingDetailsEntity = Mockito.mock(MeetingDetailsEntity.class);

        when(periodicFeeRate.isPeriodic()).thenReturn(true);
        when(periodicFeeRate.getFeeType()).thenReturn(RateAmountFlag.RATE);
        when(periodicFeeRate.getFeeId()).thenReturn(Short.valueOf(PERIODIC_FEE_2));
        when(periodicFeeRate.getFeeName()).thenReturn("periodic fee2");
        when((periodicFeeRate).getFeeFormula()).thenReturn(feeFormulaEntity);
        when(feeFormulaEntity.getFeeFormula()).thenReturn(FeeFormula.INTEREST);
        when(periodicFeeRate.getFeeFrequency()).thenReturn(feeFrequencyEntity);

        when(nonPeriodicFeeRate.isPeriodic()).thenReturn(false);
        when(nonPeriodicFeeRate.getFeeType()).thenReturn(RateAmountFlag.RATE);
        when(nonPeriodicFeeRate.getFeeId()).thenReturn(Short.valueOf(NON_PERIODIC_FEE));
        when(nonPeriodicFeeRate.getFeeName()).thenReturn("non Periodic fee");
        when(((RateFeeBO)nonPeriodicFeeRate).getFeeFormula()).thenReturn(feeFormulaEntity);
        when(feeFormulaEntity.getFeeFormula()).thenReturn(FeeFormula.INTEREST);


        List<FeeBO> allPrdFees = new ArrayList<FeeBO>();
        allPrdFees.add(periodicFeeRate);
        allPrdFees.add(nonPeriodicFeeRate);


        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        when(session.getAttribute(ProductDefinitionConstants.LOANPRDFEE)).thenReturn(allPrdFees);

        Flow flow = new Flow();
        try {
            when(flowManager.getFromFlow(Mockito.anyString(),Mockito.anyString())).thenReturn(allPrdFees);
            when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        } catch (PageExpiredException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        loanPrdActionForm.setCanConfigureVariableInstallments(false);
        loanPrdActionForm.setPrdOfferinFees(new String[] { PERIODIC_FEE_2, NON_PERIODIC_FEE});

        loanPrdActionForm.validateSelectedFeeForVariableInstallment(request, errors);
        Mockito.verifyZeroInteractions(errors);
    }

    @Test
    public void anyInterestTypeCanBeSelectedForNonVariableInstallmentProductTypes() {
        String FLAT = "1";
        String DECLINING = "2";
        String COMPOUND = "3";
        String DECLINING_EPI = "4";
        String DECLINING_PB = "5";

        loanPrdActionForm.setCanConfigureVariableInstallments(false);
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verifyZeroInteractions(errors);
        Mockito.reset(errors);

        loanPrdActionForm.setInterestTypes(FLAT);
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verifyZeroInteractions(errors);
        Mockito.reset(errors);

        loanPrdActionForm.setInterestTypes(DECLINING);
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verifyZeroInteractions(errors);
        Mockito.reset(errors);

        loanPrdActionForm.setInterestTypes(COMPOUND);
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verifyZeroInteractions(errors);
        Mockito.reset(errors);

        loanPrdActionForm.setInterestTypes(DECLINING_EPI);
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verifyZeroInteractions(errors);
        Mockito.reset(errors);

        loanPrdActionForm.setInterestTypes(DECLINING_PB);
        loanPrdActionForm.validateInterestTypeForVariableInstallment(errors, Locale.getDefault());
        Mockito.verifyZeroInteractions(errors);
        Mockito.reset(errors);
    }

    @Test
    public void shouldAbleToRetrieveMaxAndMinValues() {
        Assert.assertNotNull(loanPrdActionForm.getMaxCashFlowThreshold());
        Assert.assertNotNull(loanPrdActionForm.getMinCashFlowThreshold());
        Assert.assertNotNull(loanPrdActionForm.getMaxIndebtednessRatio());
        Assert.assertNotNull(loanPrdActionForm.getMinIndebtednessRatio());
        Assert.assertNotNull(loanPrdActionForm.getMaxRepaymentCapacity());
        Assert.assertNotNull(loanPrdActionForm.getMinRepaymentCapacity());
    }


    @Test
    public void shouldReturnConsistentValuesForPropertiesWhichHaveBothValuesAndStringRepresentationFunctions() {
        String cashflowValue = "100";
        loanPrdActionForm.setCashFlowThreshold(cashflowValue);
        Assert.assertEquals(cashflowValue, loanPrdActionForm.getCashFlowThreshold());
        Assert.assertEquals(100d,loanPrdActionForm.getCashFlowThresholdValue());

        String emptyValue = " ";
        loanPrdActionForm.setCashFlowThreshold(emptyValue);
        Assert.assertEquals(emptyValue, loanPrdActionForm.getCashFlowThreshold());
        Assert.assertEquals(null,loanPrdActionForm.getCashFlowThresholdValue());
    }


    private class ActionMessageMatcher extends TypeSafeMatcher<ActionMessage> {
        private String errorCode;

        public ActionMessageMatcher(String errorCode) {
            this.errorCode = errorCode;
        }

        @Override
        public boolean matchesSafely(ActionMessage actionMessage) {
            return actionMessage != null && StringUtils.equals(actionMessage.getKey(), errorCode);
        }

        @Override
        public void describeTo(Description arg0) {
            arg0.appendText("ActionMessage error code should be " + errorCode);
        }
    }
}
