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

package org.mifos.clientportfolio.loan.ui;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.dto.domain.FeeDto;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.platform.validations.ErrorEntry;
import org.mifos.platform.validations.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanScheduleFormBean implements Serializable {

    @Autowired
    private transient LoanAccountServiceFacade loanAccountServiceFacade;
    
    private List<Date> installments = new ArrayList<Date>();
    private List<Number> installmentAmounts = new ArrayList<Number>();
    
    // variable installments only for validation purposes
    private boolean variableInstallmentsAllowed;
    private Integer minGapInDays;
    private Integer maxGapInDays;
    private BigDecimal minInstallmentAmount;
    
    private Date disbursementDate;
    private Integer customerId;
    private List<LoanCreationInstallmentDto> variableInstallments = new ArrayList<LoanCreationInstallmentDto>();

    private List<FeeDto> applicableFees = new ArrayList<FeeDto>();

    private BigDecimal loanPrincipal;

    public LoanScheduleFormBean() {
        // constructor
    }
    
    private void addErrorMessageToContext(MessageContext messageContext, ErrorEntry fieldError) {
        String[] errorCodes = new String[1];
        errorCodes[0] = fieldError.getErrorCode();
        List<Object> args = new ArrayList<Object>();
        if (fieldError.hasErrorArgs()) {
            args = new ArrayList<Object>(fieldError.getArgs());
        }
        MessageBuilder builder = new MessageBuilder().error().source(fieldError.getFieldName())
                                              .codes(errorCodes)
                                              .defaultText(fieldError.getDefaultMessage()).args(args.toArray());
        
        messageContext.addMessage(builder.build());
    }
    
    /**
     * validateXXXX is invoked on transition from state 
     */
    public void validateReviewLoanSchedule(ValidationContext context) {
        validateCalculateAndReviewLoanSchedule(context);
    }
    
    /**
     * validateXXXX is invoked on transition from state 
     */
    public void validateCalculateAndReviewLoanSchedule(ValidationContext context) {
        MessageContext messageContext = context.getMessageContext();
        
        if (this.variableInstallmentsAllowed) {
            recalculatePrincipalBasedOnTotalAmountForEachInstallmentWhileSettingInstallmentDate();
            Errors inputInstallmentsErrors = loanAccountServiceFacade.validateInputInstallments(disbursementDate, minGapInDays, maxGapInDays, minInstallmentAmount, variableInstallments, customerId);
            Errors scheduleErrors = loanAccountServiceFacade.validateInstallmentSchedule(variableInstallments, minInstallmentAmount);
            
            handleErrors(messageContext, inputInstallmentsErrors, scheduleErrors);
        }
    }
    
    private void handleErrors(MessageContext messageContext, Errors inputInstallmentsErrors, Errors scheduleErrors) {
        if (inputInstallmentsErrors.hasErrors()) {
            for (ErrorEntry fieldError : inputInstallmentsErrors.getErrorEntries()) {
                addErrorMessageToContext(messageContext, fieldError);
            }
        }
        
        if (scheduleErrors.hasErrors()) {
            for (ErrorEntry fieldError : scheduleErrors.getErrorEntries()) {
                addErrorMessageToContext(messageContext, fieldError);
            }
        }
    }

    private void recalculatePrincipalBasedOnTotalAmountForEachInstallmentWhileSettingInstallmentDate() {
        int index=0;
        Double cumulativeNewTotal = Double.valueOf("0.0");
        for (LoanCreationInstallmentDto variableInstallment : this.variableInstallments) {
            variableInstallment.setDueDate(new LocalDate(this.installments.get(index)));
            
            Double newTotal = this.installmentAmounts.get(index).doubleValue();
            // adjust principal based on total and interest + fees
            if (index == this.variableInstallments.size()-1) {
                // sum up all totals and make final total = loan principal - sum of other totals
                Double finalInstallmentTotal = this.loanPrincipal.subtract(BigDecimal.valueOf(cumulativeNewTotal)).doubleValue();
                Double finalInstallmentPrincipal = calculatePrincipalBasedOnNewTotal(variableInstallment, finalInstallmentTotal);
                variableInstallment.setTotal(finalInstallmentTotal);
                variableInstallment.setPrincipal(finalInstallmentPrincipal);
                this.installmentAmounts.set(index, finalInstallmentTotal);
            } else {
                variableInstallment.setTotal(newTotal);
                variableInstallment.setPrincipal(calculatePrincipalBasedOnNewTotal(variableInstallment, newTotal));
                cumulativeNewTotal += newTotal;
            }
            index++;
        }
    }

    private Double calculatePrincipalBasedOnNewTotal(LoanCreationInstallmentDto variableInstallment, Double newTotal) {
        BigDecimal fees = BigDecimal.valueOf(variableInstallment.getFees());
        BigDecimal interest = BigDecimal.valueOf(variableInstallment.getInterest());
        
        return BigDecimal.valueOf(newTotal).subtract(fees.add(interest)).doubleValue();
    }

    public List<Date> getInstallments() {
        return installments;
    }

    public void setInstallments(List<Date> installments) {
        this.installments = installments;
    }

    public boolean isVariableInstallmentsAllowed() {
        return variableInstallmentsAllowed;
    }

    public void setVariableInstallmentsAllowed(boolean variableInstallmentsAllowed) {
        this.variableInstallmentsAllowed = variableInstallmentsAllowed;
    }

    public Integer getMinGapInDays() {
        return minGapInDays;
    }

    public void setMinGapInDays(Integer minGapInDays) {
        this.minGapInDays = minGapInDays;
    }

    public Integer getMaxGapInDays() {
        return maxGapInDays;
    }

    public void setMaxGapInDays(Integer maxGapInDays) {
        this.maxGapInDays = maxGapInDays;
    }

    public BigDecimal getMinInstallmentAmount() {
        return minInstallmentAmount;
    }

    public void setMinInstallmentAmount(BigDecimal minInstallmentAmount) {
        this.minInstallmentAmount = minInstallmentAmount;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(Date disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<LoanCreationInstallmentDto> getVariableInstallments() {
        return variableInstallments;
    }

    public void setVariableInstallments(List<LoanCreationInstallmentDto> variableInstallments) {
        this.variableInstallments = variableInstallments;
    }
    
    public List<FeeDto> getApplicableFees() {
        return applicableFees;
    }

    public void setApplicableFees(List<FeeDto> applicableFees) {
        this.applicableFees = applicableFees;
    }
    
    public List<Number> getInstallmentAmounts() {
        return installmentAmounts;
    }

    public void setInstallmentAmounts(List<Number> installmentAmounts) {
        this.installmentAmounts = installmentAmounts;
    }
    
    public BigDecimal getLoanPrincipal() {
        return loanPrincipal;
    }

    public void setLoanPrincipal(BigDecimal loanPrincipal) {
        this.loanPrincipal = loanPrincipal;
    }
}