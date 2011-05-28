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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.dto.domain.FeeDto;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.dto.screen.CashFlowDataDto;
import org.mifos.dto.screen.LoanInstallmentsDto;
import org.mifos.platform.validations.ErrorEntry;
import org.mifos.platform.validations.Errors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.format.annotation.DateTimeFormat;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2" },
                                                  justification = "should disable at filter level and also for pmd - not important for us")
public class CashFlowSummaryFormBean implements BackdatedPaymentable {

    @Autowired
    private transient LoanAccountServiceFacade loanAccountServiceFacade;

    private Integer productId;
    private List<CashFlowDataDto> cashFlowDataDtos;

    private LoanInstallmentsDto loanInstallmentsDto;
    private List<MonthlyCashFlowDto> monthlyCashFlows;
    private Double repaymentCapacity;
    private BigDecimal cashFlowTotalBalance;

    // variable installments
    @DateTimeFormat(style = "S-")
    private List<DateTime> installments = new ArrayList<DateTime>();
    private List<Number> installmentAmounts = new ArrayList<Number>();
    // loans backdated with payments
    @DateTimeFormat(style = "S-")
    private List<DateTime> actualPaymentDates = new ArrayList<DateTime>();
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

    public CashFlowSummaryFormBean() {
        // constructor
    }

    public String parseInstallment(int index) {
        DateTimeFormatter formatter = org.joda.time.format.DateTimeFormat.forStyle("S-")
                .withLocale(Locale.getDefault());
        return formatter.print(this.installments.get(index));
    }

    public String parseActualPaymentDates(int index) {
        DateTimeFormatter formatter = org.joda.time.format.DateTimeFormat.forStyle("S-")
                .withLocale(Locale.getDefault());
        return formatter.print(this.actualPaymentDates.get(index));
    }

    /**
     * validateXXXX is invoked on transition from state
     */
    public void validateSummaryOfCashflow(ValidationContext context) {
        MessageContext messageContext = context.getMessageContext();

        DateTime firstInstallmentDueDate = installments.get(0);
        DateTime lastInstallmentDueDate = installments.get(installments.size() - 1);
        this.loanInstallmentsDto = new LoanInstallmentsDto(this.loanInstallmentsDto.getLoanAmount(),
                this.loanInstallmentsDto.getTotalInstallmentAmount(), firstInstallmentDueDate.toDate(),
                lastInstallmentDueDate.toDate());

        Errors warnings = loanAccountServiceFacade.validateCashFlowForInstallmentsForWarnings(cashFlowDataDtos,
                productId);
        Errors errors = loanAccountServiceFacade.validateCashFlowForInstallments(loanInstallmentsDto, monthlyCashFlows,
                repaymentCapacity, cashFlowTotalBalance);
        if (warnings.hasErrors()) {
            for (ErrorEntry fieldError : warnings.getErrorEntries()) {
                addErrorMessageToContext(messageContext, fieldError);
            }
        }

        if (errors.hasErrors()) {
            for (ErrorEntry fieldError : errors.getErrorEntries()) {
                addErrorMessageToContext(messageContext, fieldError);
            }
        }

        if (this.variableInstallmentsAllowed) {
            prevalidateTotalIsNonNull(messageContext);
            prevalidateAmountPaidIsNonNull(messageContext);

            recalculatePrincipalBasedOnTotalAmountForEachInstallmentWhileSettingInstallmentDate();
            Errors inputInstallmentsErrors = loanAccountServiceFacade.validateInputInstallments(disbursementDate,
                    minGapInDays, maxGapInDays, minInstallmentAmount, variableInstallments, customerId);
            Errors scheduleErrors = loanAccountServiceFacade.validateInstallmentSchedule(variableInstallments,
                    minInstallmentAmount);

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
        } else {
            prevalidateTotalIsNonNull(messageContext);
        }

        List<LoanRepaymentTransaction> loanRepaymentTransaction = new ArrayList<LoanRepaymentTransaction>();
        this.loanRepaymentPaidInstallmentsWithRunningBalance = new ArrayList<LoanRepaymentRunningBalance>();
        this.loanRepaymentFutureInstallments = new ArrayList<LoanRepaymentFutureInstallments>();
        // if any actual payment data exists, calculate
        validatePaymentsAndAmounts(messageContext, this.actualPaymentDates, this.actualPaymentAmounts);
        List<LoanInstallmentPostPayment> installmentsPostPayment = new ArrayList<LoanInstallmentPostPayment>();

        int paymentIndex = 0;
        for (Number actualPayment : this.actualPaymentAmounts) {

            BigDecimal remainingPayment = BigDecimal.valueOf(actualPayment.doubleValue());
            LocalDate paymentDate = new LocalDate(this.actualPaymentDates.get(paymentIndex));
            loanRepaymentTransaction.add(new LoanRepaymentTransaction(paymentDate, remainingPayment));

            int installmentIndex = 0;
            while (remainingPayment.doubleValue() > BigDecimal.ZERO.doubleValue()
                    && installmentIndex < this.actualPaymentAmounts.size()) {

                LoanCreationInstallmentDto installmentDetails = this.repaymentInstallments.get(installmentIndex);
                Double installmentTotalAmount = this.installmentAmounts.get(installmentIndex).doubleValue();
                LocalDate dueDate = new LocalDate(this.installments.get(installmentIndex));

                if (installmentsPostPayment.isEmpty() || installmentsPostPayment.size() <= installmentIndex) {

                    BigDecimal feesPaid = BigDecimal.valueOf(installmentDetails.getFees());
                    if (remainingPayment.doubleValue() >= installmentDetails.getFees()) {
                        remainingPayment = remainingPayment.subtract(feesPaid);
                    } else {
                        feesPaid = remainingPayment;
                        remainingPayment = remainingPayment.subtract(feesPaid);
                    }

                    BigDecimal interestPaid = BigDecimal.valueOf(installmentDetails.getInterest());
                    if (remainingPayment.doubleValue() >= installmentDetails.getInterest()) {
                        remainingPayment = remainingPayment.subtract(interestPaid);
                    } else {
                        interestPaid = remainingPayment;
                        remainingPayment = remainingPayment.subtract(interestPaid);
                    }

                    BigDecimal principalPaid = BigDecimal.valueOf(installmentDetails.getPrincipal());
                    if (remainingPayment.doubleValue() >= installmentDetails.getPrincipal()) {
                        remainingPayment = remainingPayment.subtract(principalPaid);
                    } else {
                        principalPaid = remainingPayment;
                        remainingPayment = remainingPayment.subtract(principalPaid);
                    }

                    BigDecimal totalInstallmentPaid = feesPaid.add(interestPaid).add(principalPaid);

                    LoanInstallmentPostPayment loanInstallmentPostPayment = new LoanInstallmentPostPayment(
                            installmentDetails.getInstallmentNumber(), dueDate, paymentDate, feesPaid, interestPaid,
                            principalPaid, totalInstallmentPaid, installmentTotalAmount);
                    installmentsPostPayment.add(loanInstallmentPostPayment);
                } else {
                    LoanInstallmentPostPayment paidInstallment = installmentsPostPayment.get(installmentIndex);
                    if (paidInstallment.isNotFullyPaid()) {
                        BigDecimal feesToBePaid = BigDecimal.valueOf(installmentDetails.getFees()).subtract(
                                paidInstallment.getFeesPaid());
                        if (remainingPayment.doubleValue() >= feesToBePaid.doubleValue()) {
                            remainingPayment = remainingPayment.subtract(feesToBePaid);
                        } else {
                            feesToBePaid = remainingPayment;
                            remainingPayment = remainingPayment.subtract(feesToBePaid);
                        }

                        BigDecimal interestToBePaid = BigDecimal.valueOf(installmentDetails.getInterest()).subtract(
                                paidInstallment.getInterestPaid());
                        if (remainingPayment.doubleValue() >= interestToBePaid.doubleValue()) {
                            remainingPayment = remainingPayment.subtract(interestToBePaid);
                        } else {
                            interestToBePaid = remainingPayment;
                            remainingPayment = remainingPayment.subtract(interestToBePaid);
                        }

                        BigDecimal principalToBePaid = BigDecimal.valueOf(installmentDetails.getPrincipal()).subtract(
                                paidInstallment.getPrincipalPaid());
                        if (remainingPayment.doubleValue() >= principalToBePaid.doubleValue()) {
                            remainingPayment = remainingPayment.subtract(principalToBePaid);
                        } else {
                            principalToBePaid = remainingPayment;
                            remainingPayment = remainingPayment.subtract(principalToBePaid);
                        }

                        BigDecimal totalInstallmentPaid = feesToBePaid.add(interestToBePaid).add(principalToBePaid);

                        paidInstallment.setLastPaymentDate(paymentDate);
                        paidInstallment.setFeesPaid(paidInstallment.getFeesPaid().add(feesToBePaid));
                        paidInstallment.setInterestPaid(paidInstallment.getInterestPaid().add(interestToBePaid));
                        paidInstallment.setPrincipalPaid(paidInstallment.getPrincipalPaid().add(principalToBePaid));
                        paidInstallment.setTotalInstallmentPaid(paidInstallment.getTotalInstallmentPaid().add(
                                totalInstallmentPaid));
                    }
                }
                installmentIndex++;
            }
            paymentIndex++;
        }

        // remaining running balance
        BigDecimal cumulativeFeesPaid = BigDecimal.ZERO;
        BigDecimal cumulativeInterestPaid = BigDecimal.ZERO;
        BigDecimal cumulativePrincipalPaid = BigDecimal.ZERO;
        BigDecimal cumulativeTotalInstallmentPaid = BigDecimal.ZERO;
        for (LoanInstallmentPostPayment installment : installmentsPostPayment) {

            cumulativeFeesPaid = cumulativeFeesPaid.add(installment.getFeesPaid());
            cumulativeInterestPaid = cumulativeInterestPaid.add(installment.getInterestPaid());
            cumulativePrincipalPaid = cumulativePrincipalPaid.add(installment.getPrincipalPaid());
            cumulativeTotalInstallmentPaid = cumulativeTotalInstallmentPaid.add(installment.getTotalInstallmentPaid());
            if (installment.isNotFullyPaid()) {
                BigDecimal remainingFees = this.totalLoanFees.subtract(cumulativeFeesPaid);
                BigDecimal remainingInterest = this.totalLoanInterest.subtract(cumulativeInterestPaid);
                BigDecimal remainingTotalInstallment = this.loanPrincipal.add(this.totalLoanFees)
                        .add(this.totalLoanInterest).subtract(cumulativeTotalInstallmentPaid);
                BigDecimal remainingPrincipal = remainingTotalInstallment.subtract(remainingInterest).subtract(
                        remainingFees);

                LoanCreationInstallmentDto installmentDetails = this.repaymentInstallments.get(installment
                        .getInstallmentNumber() - 1);

                LoanCreationInstallmentDto installmentPaidDetails = new LoanCreationInstallmentDto(
                        installment.getInstallmentNumber(), new LocalDate(installmentDetails.getDueDate()), installment
                                .getPrincipalPaid().doubleValue(), installment.getInterestPaid().doubleValue(),
                        installment.getFeesPaid().doubleValue(), BigDecimal.ZERO.doubleValue(), installment
                                .getTotalInstallmentPaid().doubleValue());
                this.loanRepaymentPaidInstallmentsWithRunningBalance.add(new LoanRepaymentRunningBalance(
                        installmentPaidDetails, installment.getTotalInstallmentPaid(), remainingPrincipal,
                        remainingInterest, remainingFees, remainingTotalInstallment, installment.getLastPaymentDate()));

                BigDecimal outstandingInstallmentPrincipal = BigDecimal.valueOf(installmentDetails.getPrincipal())
                        .subtract(installment.getPrincipalPaid());
                BigDecimal outstandingInstallmentInterest = BigDecimal.valueOf(installmentDetails.getInterest())
                        .subtract(installment.getInterestPaid());
                BigDecimal outstandingInstallmentFees = BigDecimal.valueOf(installmentDetails.getFees()).subtract(
                        installment.getFeesPaid());
                this.loanRepaymentFutureInstallments.add(new LoanRepaymentFutureInstallments(installmentDetails
                        .getInstallmentNumber(), installmentDetails.getDueDate(), outstandingInstallmentPrincipal,
                        outstandingInstallmentInterest, outstandingInstallmentFees, outstandingInstallmentPrincipal
                                .add(outstandingInstallmentInterest).add(outstandingInstallmentFees)));
            } else {
                BigDecimal remainingFees = this.totalLoanFees.subtract(cumulativeFeesPaid);
                BigDecimal remainingInterest = this.totalLoanInterest.subtract(cumulativeInterestPaid);
                BigDecimal remainingTotalInstallment = this.loanPrincipal.add(this.totalLoanFees)
                        .add(this.totalLoanInterest).subtract(cumulativeTotalInstallmentPaid);
                BigDecimal remainingPrincipal = remainingTotalInstallment.subtract(remainingInterest).subtract(
                        remainingFees);

                LoanCreationInstallmentDto installmentDetails = this.repaymentInstallments.get(installment
                        .getInstallmentNumber() - 1);
                this.loanRepaymentPaidInstallmentsWithRunningBalance.add(new LoanRepaymentRunningBalance(
                        installmentDetails, installment.getTotalInstallmentPaid(), remainingPrincipal,
                        remainingInterest, remainingFees, remainingTotalInstallment, installment.getLastPaymentDate()));
            }
        }

        int lastHandledFutureInstallmentNumber = loanRepaymentPaidInstallmentsWithRunningBalance.size();
        if (!this.loanRepaymentFutureInstallments.isEmpty()) {
            lastHandledFutureInstallmentNumber = this.loanRepaymentFutureInstallments.get(
                    this.loanRepaymentFutureInstallments.size() - 1).getInstallmentNumber();
        }

        for (LoanCreationInstallmentDto installmentDto : this.repaymentInstallments) {
            if (installmentDto.getInstallmentNumber() > lastHandledFutureInstallmentNumber) {
                this.loanRepaymentFutureInstallments.add(new LoanRepaymentFutureInstallments(installmentDto
                        .getInstallmentNumber(), installmentDto.getDueDate(), BigDecimal.valueOf(installmentDto
                        .getPrincipal()), BigDecimal.valueOf(installmentDto.getInterest()), BigDecimal
                        .valueOf(installmentDto.getFees()), BigDecimal.valueOf(installmentDto.getTotal())));
            }
        }
    }

    private void prevalidateTotalIsNonNull(MessageContext messageContext) {
        Integer installmentIndex = 1;
        for (Number totalAmount : this.installmentAmounts) {
            if (totalAmount == null) {
                String defaultMessage = "The total amount field for installment {0} was blank and has been defaulted to zero.";
                ErrorEntry fieldError = new ErrorEntry("installment.total.amount.blank.and.invalid",
                        "installmentAmounts", defaultMessage);
                fieldError.setArgs(Arrays.asList(installmentIndex.toString()));

                addErrorMessageToContext(messageContext, fieldError);
            }
            installmentIndex++;
        }
    }

    private void prevalidateAmountPaidIsNonNull(MessageContext messageContext) {

    }

    private void validatePaymentsAndAmounts(MessageContext messageContext, List<DateTime> actualPaymentDates,
            List<Number> actualPaymentAmounts) {
        int index = 0;
        LocalDate lastPaymentDate = null;
        BigDecimal totalPayment = BigDecimal.ZERO;

        for (Number actualPayment : actualPaymentAmounts) {
            String installment = Integer.valueOf(index + 1).toString();
            LocalDate paymentDate = new LocalDate(actualPaymentDates.get(index));

            if (paymentDate.isBefore(new LocalDate(this.disbursementDate))) {

                String defaultMessage = "The payment date cannot be before disbursement date";
                ErrorEntry fieldError = new ErrorEntry("paymentDate.before.disbursementDate.invalid",
                        "disbursementDate", defaultMessage);
                fieldError.setArgs(Arrays.asList(installment));

                addErrorMessageToContext(messageContext, fieldError);
            }

            if (paymentDate.isAfter(new LocalDate()) && actualPayment.doubleValue() > 0) {

                String defaultMessage = "The payment date cannot be in the future.";
                ErrorEntry fieldError = new ErrorEntry("paymentDate.is.future.date.invalid", "disbursementDate",
                        defaultMessage);
                fieldError.setArgs(Arrays.asList(installment));

                addErrorMessageToContext(messageContext, fieldError);
            }

            if (lastPaymentDate != null) {
                if (!paymentDate.isEqual(lastPaymentDate) && !paymentDate.isAfter(lastPaymentDate)) {
                    String defaultMessage = "The payment date cannot be before the previous payment date";
                    ErrorEntry fieldError = new ErrorEntry("paymentDate.before.lastPaymentDate.invalid",
                            "disbursementDate", defaultMessage);
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

        BigDecimal totalAllowedPayments = this.loanPrincipal.add(this.totalLoanFees).add(this.totalLoanInterest);
        if (totalPayment.doubleValue() > totalAllowedPayments.doubleValue()) {
            String defaultMessage = "Exceeds total payments allowed for loan.";
            ErrorEntry fieldError = new ErrorEntry("totalPayments.exceeded.invalid", "disbursementDate", defaultMessage);
            addErrorMessageToContext(messageContext, fieldError);
        }
    }

    private void recalculatePrincipalBasedOnTotalAmountForEachInstallmentWhileSettingInstallmentDate() {
        int index = 0;
        Double cumulativeNewInstallmentTotal = Double.valueOf("0.0");
        BigDecimal totalInterestAndFeesDue = BigDecimal.ZERO;
        for (LoanCreationInstallmentDto variableInstallment : this.variableInstallments) {
            totalInterestAndFeesDue = totalInterestAndFeesDue.add(BigDecimal.valueOf(variableInstallment.getInterest())
                    .add(BigDecimal.valueOf(variableInstallment.getFees())));
        }

        for (LoanCreationInstallmentDto variableInstallment : this.variableInstallments) {
            variableInstallment.setDueDate(new LocalDate(this.installments.get(index)));

            Double newTotal = Double.valueOf("0.0");
            Number newTotalEntry = this.installmentAmounts.get(index);
            if (newTotalEntry != null) {
                newTotal = newTotalEntry.doubleValue();
            } else {
                this.installmentAmounts.set(index, newTotal);
            }
            // adjust principal based on total and interest + fees
            if (index == this.variableInstallments.size() - 1) {
                // sum up all totals and make final total = loan principal + interest and fees due - sum of other
                // installment totals
                Double finalInstallmentTotal = this.loanPrincipal.add(totalInterestAndFeesDue)
                        .subtract(BigDecimal.valueOf(cumulativeNewInstallmentTotal)).doubleValue();
                Double finalInstallmentPrincipal = calculatePrincipalBasedOnNewTotal(variableInstallment,
                        finalInstallmentTotal);
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

    private void addErrorMessageToContext(MessageContext messageContext, ErrorEntry fieldError) {
        String[] errorCodes = new String[1];
        errorCodes[0] = fieldError.getErrorCode();
        List<Object> args = new ArrayList<Object>();
        if (fieldError.hasErrorArgs()) {
            args = new ArrayList<Object>(fieldError.getArgs());
        }
        MessageBuilder builder = new MessageBuilder().error().source(fieldError.getFieldName()).codes(errorCodes)
                .defaultText(fieldError.getDefaultMessage()).args(args.toArray());

        messageContext.addMessage(builder.build());
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public List<CashFlowDataDto> getCashFlowDataDtos() {
        return cashFlowDataDtos;
    }

    public void setCashFlowDataDtos(List<CashFlowDataDto> cashFlowDataDtos) {
        this.cashFlowDataDtos = cashFlowDataDtos;
    }

    public LoanInstallmentsDto getLoanInstallmentsDto() {
        return loanInstallmentsDto;
    }

    public void setLoanInstallmentsDto(LoanInstallmentsDto loanInstallmentsDto) {
        this.loanInstallmentsDto = loanInstallmentsDto;
    }

    public List<MonthlyCashFlowDto> getMonthlyCashFlows() {
        return monthlyCashFlows;
    }

    public void setMonthlyCashFlows(List<MonthlyCashFlowDto> monthlyCashFlows) {
        this.monthlyCashFlows = monthlyCashFlows;
    }

    public Double getRepaymentCapacity() {
        return repaymentCapacity;
    }

    public void setRepaymentCapacity(Double repaymentCapacity) {
        this.repaymentCapacity = repaymentCapacity;
    }

    public BigDecimal getCashFlowTotalBalance() {
        return cashFlowTotalBalance;
    }

    public void setCashFlowTotalBalance(BigDecimal cashFlowTotalBalance) {
        this.cashFlowTotalBalance = cashFlowTotalBalance;
    }

    @Override
    public boolean isVariableInstallmentsAllowed() {
        return variableInstallmentsAllowed;
    }

    @Override
    public void setVariableInstallmentsAllowed(boolean variableInstallmentsAllowed) {
        this.variableInstallmentsAllowed = variableInstallmentsAllowed;
    }

    public Integer getMinGapInDays() {
        return minGapInDays;
    }

    @Override
    public void setMinGapInDays(Integer minGapInDays) {
        this.minGapInDays = minGapInDays;
    }

    public Integer getMaxGapInDays() {
        return maxGapInDays;
    }

    @Override
    public void setMaxGapInDays(Integer maxGapInDays) {
        this.maxGapInDays = maxGapInDays;
    }

    public BigDecimal getMinInstallmentAmount() {
        return minInstallmentAmount;
    }

    @Override
    public void setMinInstallmentAmount(BigDecimal minInstallmentAmount) {
        this.minInstallmentAmount = minInstallmentAmount;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    @Override
    public void setDisbursementDate(Date disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    @Override
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<LoanCreationInstallmentDto> getVariableInstallments() {
        return variableInstallments;
    }

    @Override
    public void setVariableInstallments(List<LoanCreationInstallmentDto> variableInstallments) {
        this.variableInstallments = variableInstallments;
    }

    public List<Number> getInstallmentAmounts() {
        return installmentAmounts;
    }

    @Override
    public void setInstallmentAmounts(List<Number> installmentAmounts) {
        this.installmentAmounts = installmentAmounts;
    }

    public BigDecimal getLoanPrincipal() {
        return loanPrincipal;
    }

    @Override
    public void setLoanPrincipal(BigDecimal loanPrincipal) {
        this.loanPrincipal = loanPrincipal;
    }

    public List<DateTime> getInstallments() {
        return installments;
    }

    @Override
    public void setInstallments(List<DateTime> installments) {
        this.installments = installments;
    }

    public List<DateTime> getActualPaymentDates() {
        return actualPaymentDates;
    }

    @Override
    public void setActualPaymentDates(List<DateTime> actualPaymentDates) {
        this.actualPaymentDates = actualPaymentDates;
    }

    public List<Number> getActualPaymentAmounts() {
        return actualPaymentAmounts;
    }

    @Override
    public void setActualPaymentAmounts(List<Number> actualPaymentAmounts) {
        this.actualPaymentAmounts = actualPaymentAmounts;
    }

    public BigDecimal getTotalLoanInterest() {
        return totalLoanInterest;
    }

    @Override
    public void setTotalLoanInterest(BigDecimal totalLoanInterest) {
        this.totalLoanInterest = totalLoanInterest;
    }

    public BigDecimal getTotalLoanFees() {
        return totalLoanFees;
    }

    @Override
    public void setTotalLoanFees(BigDecimal totalLoanFees) {
        this.totalLoanFees = totalLoanFees;
    }

    public List<LoanCreationInstallmentDto> getRepaymentInstallments() {
        return repaymentInstallments;
    }

    @Override
    public void setRepaymentInstallments(List<LoanCreationInstallmentDto> repaymentInstallments) {
        this.repaymentInstallments = repaymentInstallments;
    }

    public List<LoanRepaymentRunningBalance> getLoanRepaymentPaidInstallmentsWithRunningBalance() {
        return loanRepaymentPaidInstallmentsWithRunningBalance;
    }

    public void setLoanRepaymentPaidInstallmentsWithRunningBalance(
            List<LoanRepaymentRunningBalance> loanRepaymentPaidInstallmentsWithRunningBalance) {
        this.loanRepaymentPaidInstallmentsWithRunningBalance = loanRepaymentPaidInstallmentsWithRunningBalance;
    }

    public List<LoanRepaymentFutureInstallments> getLoanRepaymentFutureInstallments() {
        return loanRepaymentFutureInstallments;
    }

    public void setLoanRepaymentFutureInstallments(List<LoanRepaymentFutureInstallments> loanRepaymentFutureInstallments) {
        this.loanRepaymentFutureInstallments = loanRepaymentFutureInstallments;
    }

    public List<FeeDto> getApplicableFees() {
        return applicableFees;
    }

    @Override
    public void setApplicableFees(List<FeeDto> applicableFees) {
        this.applicableFees = applicableFees;
    }
}