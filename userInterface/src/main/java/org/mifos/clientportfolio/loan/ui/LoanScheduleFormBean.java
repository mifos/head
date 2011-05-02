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
import java.util.Arrays;
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
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2", "DLS_DEAD_LOCAL_STORE"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanScheduleFormBean implements Serializable {

    @Autowired
    private transient LoanAccountServiceFacade loanAccountServiceFacade;
    
    private List<Date> installments = new ArrayList<Date>();
    private List<Date> actualPaymentDates = new ArrayList<Date>();
    private List<Number> installmentAmounts = new ArrayList<Number>();
    private List<Number> actualPaymentAmounts = new ArrayList<Number>();
    
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
    private BigDecimal totalLoanInterest;
    private BigDecimal totalLoanFees;

    private List<LoanCreationInstallmentDto> repaymentInstallments;
    private List<LoanRepaymentRunningBalance> loanRepaymentPaidInstallmentsWithRunningBalance;
    private List<LoanRepaymentFutureInstallments> loanRepaymentFutureInstallments;

    private boolean loanWithBackdatedPayments = true;

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
        
        List<LoanRepaymentTransaction> loanRepaymentTransaction = new ArrayList<LoanRepaymentTransaction>();
        this.loanRepaymentPaidInstallmentsWithRunningBalance = new ArrayList<LoanRepaymentRunningBalance>();
        this.loanRepaymentFutureInstallments = new ArrayList<LoanRepaymentFutureInstallments>();
        // if any actual payment data exists, calculate
        validatePaymentsAndAmounts(messageContext, this.actualPaymentDates, this.actualPaymentAmounts);
        int paymentIndex = 0;
        CumulativePaymentDetail cumulativePaymentDetail = new CumulativePaymentDetail();
        for (Number actualPayment : this.actualPaymentAmounts) {

            LocalDate paymentDate = new LocalDate(this.actualPaymentDates.get(paymentIndex));
            BigDecimal payment = BigDecimal.valueOf(actualPayment.doubleValue());
            cumulativePaymentDetail.setRemainingPayment(payment);  
            loanRepaymentTransaction.add(new LoanRepaymentTransaction(paymentDate, payment));
          
            int currentInstallmentIndex = paymentIndex;
            while (cumulativePaymentDetail.getRemainingPayment().doubleValue() > BigDecimal.ZERO.doubleValue() && currentInstallmentIndex < this.actualPaymentAmounts.size()-1) {
                LoanCreationInstallmentDto installmentDetails = this.repaymentInstallments.get(currentInstallmentIndex);
                cumulativePaymentDetail = handlePayment(currentInstallmentIndex, cumulativePaymentDetail, installmentDetails, paymentDate);
                currentInstallmentIndex++;
            }
            paymentIndex++;
        }
        
        int lastHandledFutureInstallmentNumber = loanRepaymentPaidInstallmentsWithRunningBalance.size();
        if (!this.loanRepaymentFutureInstallments.isEmpty()) {
            lastHandledFutureInstallmentNumber = this.loanRepaymentFutureInstallments.get(this.loanRepaymentFutureInstallments.size()-1).getInstallmentNumber();
        }

        for (LoanCreationInstallmentDto installmentDto : this.repaymentInstallments) {
            if (installmentDto.getInstallmentNumber() > lastHandledFutureInstallmentNumber) {
                this.loanRepaymentFutureInstallments.add(new LoanRepaymentFutureInstallments(installmentDto.getInstallmentNumber(), installmentDto.getDueDate(), BigDecimal.valueOf(installmentDto.getPrincipal()), 
                        BigDecimal.valueOf(installmentDto.getInterest()), 
                        BigDecimal.valueOf(installmentDto.getFees()), 
                        BigDecimal.valueOf(installmentDto.getTotal())));
            }
        }
        
        if (this.variableInstallmentsAllowed) {
            recalculatePrincipalBasedOnTotalAmountForEachInstallmentWhileSettingInstallmentDate();
            Errors inputInstallmentsErrors = loanAccountServiceFacade.validateInputInstallments(disbursementDate, minGapInDays, maxGapInDays, minInstallmentAmount, variableInstallments, customerId);
            Errors scheduleErrors = loanAccountServiceFacade.validateInstallmentSchedule(variableInstallments, minInstallmentAmount);
            
            handleErrors(messageContext, inputInstallmentsErrors, scheduleErrors);
        }
    }
    
    private void validatePaymentsAndAmounts(MessageContext messageContext, List<Date> actualPaymentDates, List<Number> actualPaymentAmounts) {
        int index = 0;
        LocalDate lastPaymentDate = null;
        BigDecimal totalPayment = BigDecimal.ZERO;
        
        for (Number actualPayment : actualPaymentAmounts) {
            String installment = Integer.valueOf(index+1).toString();
            LocalDate paymentDate = new LocalDate(actualPaymentDates.get(index));
            
            if (paymentDate.isBefore(new LocalDate(this.disbursementDate))) {
                
                String defaultMessage = "The payment date cannot be before disbursement date";
                ErrorEntry fieldError = new ErrorEntry("paymentDate.before.disbursementDate.invalid", "disbursementDate", defaultMessage);
                fieldError.setArgs(Arrays.asList(installment));
                
                addErrorMessageToContext(messageContext, fieldError);
            }
            
            if (paymentDate.isAfter(new LocalDate()) && actualPayment.doubleValue() > 0) {
                
                String defaultMessage = "The payment date cannot be in the future.";
                ErrorEntry fieldError = new ErrorEntry("paymentDate.is.future.date.invalid", "disbursementDate", defaultMessage);
                fieldError.setArgs(Arrays.asList(installment));
                
                addErrorMessageToContext(messageContext, fieldError);
            }
            
            if (lastPaymentDate != null) {
                if (!paymentDate.isAfter(lastPaymentDate)) {
                    String defaultMessage = "The payment date cannot be before the previous payment date";
                    ErrorEntry fieldError = new ErrorEntry("paymentDate.before.lastPaymentDate.invalid", "disbursementDate", defaultMessage);
                    fieldError.setArgs(Arrays.asList(installment));
                    
                    addErrorMessageToContext(messageContext, fieldError);
                }
            }
            
            BigDecimal payment = BigDecimal.valueOf(actualPayment.doubleValue());
            if (payment.doubleValue() > BigDecimal.ZERO.doubleValue()) {
                totalPayment = totalPayment.add(payment);
            }
            index++;
            lastPaymentDate = paymentDate;
        }
        
        BigDecimal totalAllowedPayments = this.loanPrincipal.add(this.totalLoanFees).add(this.totalLoanFees);
        if (totalPayment.doubleValue() > totalAllowedPayments.doubleValue()) {
            String defaultMessage = "Exceeds total payments allowed for loan.";
            ErrorEntry fieldError = new ErrorEntry("totalPayments.exceeded.invalid", "disbursementDate", defaultMessage);
            addErrorMessageToContext(messageContext, fieldError);
        }
    }

    private CumulativePaymentDetail handlePayment(int paymentIndex, CumulativePaymentDetail cumulativePaymentDetail, LoanCreationInstallmentDto installmentDetails, LocalDate paymentDate) {
        
        BigDecimal payment = BigDecimal.ZERO;
        BigDecimal cumulativeTotalInstallmentPaid = BigDecimal.ZERO;
        BigDecimal cumulativeFeesPaid = BigDecimal.ZERO;
        BigDecimal cumulativeInterestPaid = BigDecimal.ZERO;
        BigDecimal totalInstallmentDue = BigDecimal.valueOf(installmentAmounts.get(paymentIndex).doubleValue()); 
        if (cumulativePaymentDetail.getRemainingPayment().doubleValue() >= totalInstallmentDue.doubleValue()) {
            // pay off total installment amount
            payment = cumulativePaymentDetail.getRemainingPayment().subtract(totalInstallmentDue);
            // sum total amounts paid to date.
            cumulativeTotalInstallmentPaid = cumulativePaymentDetail.getCumulativeTotalPaid().add(totalInstallmentDue);
            cumulativeFeesPaid = cumulativePaymentDetail.getCumulativeFeesPaid().add(BigDecimal.valueOf(installmentDetails.getFees()));
            cumulativeInterestPaid = cumulativePaymentDetail.getCumulativeInterestPaid().add(BigDecimal.valueOf(installmentDetails.getInterest()));
            
            // remaining running balance
            BigDecimal remainingFees = this.totalLoanFees.subtract(cumulativeFeesPaid);
            BigDecimal remainingInterest = this.totalLoanInterest.subtract(cumulativeInterestPaid);
            BigDecimal remainingTotalInstallment = this.loanPrincipal.add(this.totalLoanFees).add(this.totalLoanInterest).subtract(cumulativeTotalInstallmentPaid);
            BigDecimal remainingPrincipal = remainingTotalInstallment.subtract(remainingInterest).subtract(remainingFees);
            
            this.loanRepaymentPaidInstallmentsWithRunningBalance.add(new LoanRepaymentRunningBalance(installmentDetails, BigDecimal.valueOf(installmentDetails.getTotal()), remainingPrincipal, remainingInterest, remainingFees, remainingTotalInstallment, paymentDate));
        } else {
            // pay off total installment amount
            BigDecimal feesPaid = BigDecimal.ZERO;
            BigDecimal interestPaid = BigDecimal.ZERO;
            payment = BigDecimal.ZERO;
            // sum total amounts paid to date.
            cumulativeTotalInstallmentPaid = cumulativePaymentDetail.getCumulativeTotalPaid().add(cumulativePaymentDetail.getRemainingPayment());
            
            if (cumulativePaymentDetail.getRemainingPayment().doubleValue() > installmentDetails.getFees()) {
                feesPaid = BigDecimal.valueOf(installmentDetails.getFees());
                cumulativeFeesPaid = cumulativePaymentDetail.getCumulativeFeesPaid().add(feesPaid);
                BigDecimal remainingPaymentAmount = cumulativePaymentDetail.getRemainingPayment().subtract(feesPaid);
                
                if (remainingPaymentAmount.doubleValue() > installmentDetails.getInterest()) {
                    interestPaid = BigDecimal.valueOf(installmentDetails.getInterest());
                    cumulativeInterestPaid = cumulativePaymentDetail.getCumulativeInterestPaid().add(interestPaid);
                } else {
                    interestPaid = remainingPaymentAmount;
                    cumulativeInterestPaid = cumulativePaymentDetail.getCumulativeInterestPaid().add(remainingPaymentAmount);
                }
            } else {
                feesPaid = cumulativePaymentDetail.getRemainingPayment();
                interestPaid = BigDecimal.ZERO;
                cumulativeFeesPaid = cumulativePaymentDetail.getCumulativeFeesPaid().add(feesPaid);
                cumulativeInterestPaid = cumulativePaymentDetail.getCumulativeInterestPaid().add(interestPaid);
            }
            
            // remaining running balance
            BigDecimal remainingFees = this.totalLoanFees.subtract(cumulativeFeesPaid);
            BigDecimal remainingInterest = this.totalLoanInterest.subtract(cumulativeInterestPaid);
            BigDecimal remainingTotalInstallment = this.loanPrincipal.add(this.totalLoanFees).add(this.totalLoanInterest).subtract(cumulativeTotalInstallmentPaid);
            BigDecimal remainingPrincipal = remainingTotalInstallment.subtract(remainingInterest).subtract(remainingFees);
            this.loanRepaymentPaidInstallmentsWithRunningBalance.add(new LoanRepaymentRunningBalance(installmentDetails, cumulativePaymentDetail.getRemainingPayment(), remainingPrincipal, remainingInterest, remainingFees, remainingTotalInstallment, paymentDate));
            
            BigDecimal outstandingInstallmentPrincipal = BigDecimal.valueOf(installmentDetails.getTotal()).subtract(cumulativePaymentDetail.getRemainingPayment());
            BigDecimal outstandingInstallmentInterest = BigDecimal.valueOf(installmentDetails.getInterest()).subtract(interestPaid);
            BigDecimal outstandingInstallmentFees = BigDecimal.valueOf(installmentDetails.getFees()).subtract(feesPaid);
            this.loanRepaymentFutureInstallments.add(new LoanRepaymentFutureInstallments(installmentDetails.getInstallmentNumber(), 
                    installmentDetails.getDueDate(), 
                    outstandingInstallmentPrincipal, 
                    outstandingInstallmentInterest, 
                    outstandingInstallmentFees, 
                    outstandingInstallmentPrincipal.add(outstandingInstallmentInterest).add(outstandingInstallmentFees)));
        }
        
        return new CumulativePaymentDetail(payment, cumulativeTotalInstallmentPaid, cumulativeInterestPaid, cumulativeFeesPaid);
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
        Double cumulativeNewInstallmentTotal = Double.valueOf("0.0");
        BigDecimal totalInterestAndFeesDue = BigDecimal.ZERO;
        for (LoanCreationInstallmentDto variableInstallment : this.variableInstallments) {
            totalInterestAndFeesDue = totalInterestAndFeesDue.add(BigDecimal.valueOf(variableInstallment.getInterest()).add(BigDecimal.valueOf(variableInstallment.getFees())));
        }
        
        for (LoanCreationInstallmentDto variableInstallment : this.variableInstallments) {
            variableInstallment.setDueDate(new LocalDate(this.installments.get(index)));
            
            Double newTotal = this.installmentAmounts.get(index).doubleValue();
            // adjust principal based on total and interest + fees
            if (index == this.variableInstallments.size()-1) {
                // sum up all totals and make final total = loan principal + interest and fees due - sum of other installment totals
                Double finalInstallmentTotal = this.loanPrincipal.add(totalInterestAndFeesDue).subtract(BigDecimal.valueOf(cumulativeNewInstallmentTotal)).doubleValue();
                Double finalInstallmentPrincipal = calculatePrincipalBasedOnNewTotal(variableInstallment, finalInstallmentTotal);
                variableInstallment.setTotal(finalInstallmentTotal);
                variableInstallment.setPrincipal(finalInstallmentPrincipal);
                this.installmentAmounts.set(index, finalInstallmentTotal);
            } else {
                variableInstallment.setTotal(newTotal);
                variableInstallment.setPrincipal(calculatePrincipalBasedOnNewTotal(variableInstallment, newTotal));
                cumulativeNewInstallmentTotal += newTotal;
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
    
    public List<Date> getActualPaymentDates() {
        return actualPaymentDates;
    }

    public void setActualPaymentDates(List<Date> actualPaymentDates) {
        this.actualPaymentDates = actualPaymentDates;
    }

    public List<Number> getActualPaymentAmounts() {
        return actualPaymentAmounts;
    }

    public void setActualPaymentAmounts(List<Number> actualPaymentAmounts) {
        this.actualPaymentAmounts = actualPaymentAmounts;
    }

    public BigDecimal getTotalLoanInterest() {
        return totalLoanInterest;
    }

    public void setTotalLoanInterest(BigDecimal totalLoanInterest) {
        this.totalLoanInterest = totalLoanInterest;
    }

    public BigDecimal getTotalLoanFees() {
        return totalLoanFees;
    }

    public void setTotalLoanFees(BigDecimal totalLoanFees) {
        this.totalLoanFees = totalLoanFees;
    }

    public List<LoanCreationInstallmentDto> getRepaymentInstallments() {
        return repaymentInstallments;
    }
    
    public void setRepaymentInstallments(List<LoanCreationInstallmentDto> repaymentInstallments) {
        this.repaymentInstallments = repaymentInstallments;
    }
    
    public List<LoanRepaymentFutureInstallments> getLoanRepaymentFutureInstallments() {
        return loanRepaymentFutureInstallments;
    }

    public void setLoanRepaymentFutureInstallments(List<LoanRepaymentFutureInstallments> loanRepaymentFutureInstallments) {
        this.loanRepaymentFutureInstallments = loanRepaymentFutureInstallments;
    }

    public List<LoanRepaymentRunningBalance> getLoanRepaymentPaidInstallmentsWithRunningBalance() {
        return loanRepaymentPaidInstallmentsWithRunningBalance;
    }

    public void setLoanRepaymentPaidInstallmentsWithRunningBalance(
            List<LoanRepaymentRunningBalance> loanRepaymentPaidInstallmentsWithRunningBalance) {
        this.loanRepaymentPaidInstallmentsWithRunningBalance = loanRepaymentPaidInstallmentsWithRunningBalance;
    }

    public boolean isLoanWithBackdatedPayments() {
        return loanWithBackdatedPayments;
    }
}