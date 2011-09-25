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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.clientportfolio.newloan.applicationservice.LoanDisbursementDateValidationServiceFacade;
import org.mifos.clientportfolio.newloan.applicationservice.VariableInstallmentWithFeeValidationResult;
import org.mifos.clientportfolio.newloan.applicationservice.VariableInstallmentsFeeValidationServiceFacade;
import org.mifos.dto.domain.FeeDto;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mifos.platform.validations.ErrorEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageCriteria;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2", "DLS_DEAD_LOCAL_STORE"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanAccountFormBean implements Serializable {

    @Autowired
    private transient MifosBeanValidator validator;
    
    @Autowired
    private transient LoanDisbursementDateValidationServiceFacade loanDisbursementDateValidationServiceFacade;
    
    @Autowired
    private transient VariableInstallmentsFeeValidationServiceFacade variableInstallmentsFeeValidationServiceFacade;
    
    private transient DateValidator dateValidator;
    
    private boolean redoLoanAccount = false;

    // only used for validating business rule on disbursement
    private Integer customerId;

    @NotNull
    private Integer productId;

    // custom validation
    private Number amount;
    private Number minAllowedAmount;
    private Number maxAllowedAmount;
    private Number interestRate;
    private Number minAllowedInterestRate;
    private Number maxAllowedInterestRate;
    private Number numberOfInstallments;
    private Number minNumberOfInstallments;
    private Number maxNumberOfInstallments;
    private Number disbursementDateDD;
    private Number disbursementDateMM;
    private Number disbursementDateYY;
    
    private Number graceDuration = Integer.valueOf(0);
    private Number maxGraceDuration = Integer.valueOf(0);

    private boolean sourceOfFundsMandatory;
    private Integer fundId;
    
    private boolean purposeOfLoanMandatory;
    private Integer loanPurposeId;
    
    private boolean collateralTypeAndNotesHidden;
    private Integer collateralTypeId;
    private String collateralNotes;
    private boolean externalIdHidden;
    private boolean externalIdMandatory;
    private String externalId;

    // only when LSIM is turned on.
    private boolean repaymentScheduleIndependentOfCustomerMeeting = false;
    private Integer repaymentRecursEvery;
    private Integer repaymentDayOfWeek;
    private Integer repaymentDayOfMonth;
    private Integer repaymentWeekOfMonth;
    private boolean monthly;
    private boolean monthlyDayOfMonthOptionSelected;
    private boolean monthlyWeekOfMonthOptionSelected;
    private String montlyOption = "weekOfMonth";
    private boolean weekly;

    // variable installments only for validation purposes
    private boolean variableInstallmentsAllowed;
    private Integer minGapInDays;
    private Integer maxGapInDays;
    private BigDecimal minInstallmentAmount;
    
    // GLIM specific
    private boolean glimApplicable;
    private Boolean[] clientSelectForGroup = new Boolean[1];
    private String[] clientGlobalId = new String[1];
    private Number[] clientAmount = new Number[1];
    private Integer[] clientLoanPurposeId = new Integer[] {0};
    
    // fees
    private List<FeeDto> defaultFees;
    private List<FeeDto> additionalFees;
    private Boolean[] defaultFeeSelected;
    private Number[] defaultFeeAmountOrRate;
    private Number[] defaultFeeId;

    private Number[] selectedFeeId;
    private Number[] selectedFeeAmount;
    
    // validation of interest field
    private int digitsBeforeDecimalForInterest;
    private int digitsAfterDecimalForInterest;
    private int digitsBeforeDecimalForMonetaryAmounts;
    private int digitsAfterDecimalForMonetaryAmounts;

    private Locale locale;

    public Locale getLocale() {
        return locale;
    }

    public void validateEditAccountDetailsStep(ValidationContext context) {
        validateEnterAccountDetailsStep(context);
    }
    
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SIC_INNER_SHOULD_BE_STATIC_ANON"}, justification="")
    public void validateEnterAccountDetailsStep(ValidationContext context) {
        MessageContext messageContext = context.getMessageContext();

        Errors errors = validator.checkConstraints(this);
        
        // handle data binding errors that may of occurred
        if (messageContext.hasErrorMessages()) {
            Message[] errorMessages = messageContext.getMessagesByCriteria(new MessageCriteria() {
                
                @Override
                public boolean test(@SuppressWarnings("unused") Message message) {
                    return true;
                }
            });
            messageContext.clearMessages();
            
            for (Message message : errorMessages) {
                handleDataMappingError(errors, message);
            }
        }
        
        if (this.glimApplicable) {
            int index = 0;
            int selectedCount = 0;
                for (Boolean clientSelected : this.clientSelectForGroup) {
                    if (clientSelected != null && clientSelected.booleanValue()) {

                        Number clientAmount = this.clientAmount[index];

                        if (clientAmount == null || exceedsMinOrMax(clientAmount, Integer.valueOf(1), this.maxAllowedAmount)) {
                            String defaultErrorMessage = "Please specify valid Amount.";
                            rejectGlimClientAmountField(index + 1, errors, defaultErrorMessage);
                        }
                        
                        if (clientAmount != null) {
                            BigDecimal amountAsDecimal = new BigDecimal(clientAmount.toString()).stripTrailingZeros();
                            int places = amountAsDecimal.scale();
                            if (places > this.digitsAfterDecimalForMonetaryAmounts) {
                                String defaultErrorMessage = "The number of digits after the decimal separator exceeds the allowed number.";
                                rejectInterestRateFieldValue(errors, defaultErrorMessage,
                                        "loanAccountFormBean.client.amount.digitsAfterDecimal.invalid",
                                        new Object[] {index+1, this.digitsAfterDecimalForMonetaryAmounts });
                            }
                            
                            int digitsBefore = amountAsDecimal.toBigInteger().toString().length();
                            if (digitsBefore > this.digitsBeforeDecimalForMonetaryAmounts) {
                                String defaultErrorMessage = "The number of digits before the decimal separator exceeds the allowed number.";
                                rejectInterestRateFieldValue(errors, defaultErrorMessage,
                                        "loanAccountFormBean.client.amount.digitsBeforeDecimal.invalid",
                                        new Object[] {index+1, this.digitsBeforeDecimalForMonetaryAmounts});
                            }
                        }

                        // check error message of loan purpose for each client when its mandatory..
                        Integer clientLoanPurposeId = this.clientLoanPurposeId[index];
                        if (this.purposeOfLoanMandatory && isInvalidSelection(clientLoanPurposeId)) {
                            errors.rejectValue("clientLoanPurposeId", "loanAccountFormBean.glim.purposeOfLoan.invalid", new Object[] {index+1}, "Please specify loan purpose.");
                            this.clientLoanPurposeId[index] = 0;
                        } else {
                            // needed so attempt in freemarker(ftl) to display loan purpose doesnt fall over.
                            if (clientLoanPurposeId == null) {
                                this.clientLoanPurposeId[index] = 0;
                            }
                        }

                        selectedCount++;
                    } else {

                        Number clientAmount = this.clientAmount[index];
                        Integer clientLoanPurposeId = this.clientLoanPurposeId[index];
                        if (clientAmount != null || clientLoanPurposeId != null) {
                            String defaultErrorMessage = "You have entered details for a member you have not selected. Select the checkbox in front of the member's name in order to include him or her in the loan.";
                            rejectUnselectedGlimClientAmountField(index + 1, errors, defaultErrorMessage);
                        }
                    }

                    index++;
                }

                if (selectedCount < 2) {
                    String defaultErrorMessage = "Not enough clients for group loan.";
                    rejectGroupLoanWithTooFewClients(errors, defaultErrorMessage);
                }
        }
        
        if (this.amount == null || exceedsMinOrMax(this.amount, this.minAllowedAmount, this.maxAllowedAmount)) {
            String defaultErrorMessage = "Please specify valid Amount.";
            if (glimApplicable) {
                defaultErrorMessage = "The sum of the amounts specified for each member is outside the allowable total amount for this loan product.";
                rejectGlimTotalAmountField(errors, defaultErrorMessage);
            } else {
                rejectAmountField(errors, defaultErrorMessage);
            }
        }
        
        if (this.amount != null) {
            BigDecimal amountAsDecimal = new BigDecimal(this.amount.toString()).stripTrailingZeros();
            int places = amountAsDecimal.scale();
            if (places > this.digitsAfterDecimalForMonetaryAmounts) {
                String defaultErrorMessage = "The number of digits after the decimal separator exceeds the allowed number.";
                rejectInterestRateFieldValue(errors, defaultErrorMessage,
                        "loanAccountFormBean.amount.digitsAfterDecimal.invalid",
                        new Object[] { this.digitsAfterDecimalForMonetaryAmounts });
            }
            
            int digitsBefore = amountAsDecimal.toBigInteger().toString().length();
            if (digitsBefore > this.digitsBeforeDecimalForMonetaryAmounts) {
                String defaultErrorMessage = "The number of digits before the decimal separator exceeds the allowed number.";
                rejectInterestRateFieldValue(errors, defaultErrorMessage,
                        "loanAccountFormBean.amount.digitsBeforeDecimal.invalid",
                        new Object[] { this.digitsBeforeDecimalForMonetaryAmounts});
            }
        }
        
        if (this.interestRate == null || exceedsMinOrMax(this.interestRate, this.minAllowedInterestRate, this.maxAllowedInterestRate)) {
            String defaultErrorMessage = "Please specify valid Interest rate.";
            rejectInterestRateField(errors, defaultErrorMessage);
        }
        
        if (this.interestRate != null) {
            BigDecimal interestRateAsDecimal = new BigDecimal(this.interestRate.toString()).stripTrailingZeros();
            int places = interestRateAsDecimal.scale();
            if (places > this.digitsAfterDecimalForInterest) {
                String defaultErrorMessage = "The number of digits after the decimal separator exceeds the allowed number.";
                rejectInterestRateFieldValue(errors, defaultErrorMessage,
                        "loanAccountFormBean.digitsAfterDecimalForInterest.invalid",
                        new Object[] { this.digitsAfterDecimalForInterest });
            }

            int digitsBefore = interestRateAsDecimal.toBigInteger().toString().length();
            if (digitsBefore > this.digitsBeforeDecimalForInterest) {
                String defaultErrorMessage = "The number of digits before the decimal separator exceeds the allowed number.";
                rejectInterestRateFieldValue(errors, defaultErrorMessage,
                        "loanAccountFormBean.digitsBeforeDecimalForInterest.invalid",
                        new Object[] { this.digitsBeforeDecimalForInterest });
            }
        }
        
        if (this.numberOfInstallments == null || exceedsMinOrMax(this.numberOfInstallments, this.minNumberOfInstallments, this.maxNumberOfInstallments)) {
            String defaultErrorMessage = "Please specify valid number of installments.";
            rejectNumberOfInstallmentsField(errors, defaultErrorMessage);
        }

        String graceDurationUserInput = (String)context.getUserValue("graceDuration");
        if (graceDurationUserInput != null && !graceDurationUserInput.trim().matches("^[0-9]*$")) {
            this.graceDuration = null;
        }

        if (this.graceDuration == null || this.graceDuration.intValue() < 0) {
            String defaultErrorMessage = "Please specify a valid Grace period. Only non-negative integer numbers are allowed.";
            errors.rejectValue("graceDuration", "loanAccountFormBean.gracePeriodDuration.emptyOrIncorrect.invalid", defaultErrorMessage);
        } else {
            if (this.graceDuration.intValue() > this.maxGraceDuration.intValue()) {
                String defaultErrorMessage = "The Grace period cannot be greater than in loan product definition.";
                errors.rejectValue("graceDuration", "loanAccountFormBean.gracePeriodDuration.invalid", defaultErrorMessage);
            }

            if (this.numberOfInstallments != null
                    && (this.graceDuration.intValue() >= this.numberOfInstallments.intValue())) {
                String defaultErrorMessage = "Grace period for repayments must be less than number of loan installments.";
                errors.rejectValue("graceDuration",
                        "loanAccountFormBean.gracePeriodDurationInRelationToInstallments.invalid", defaultErrorMessage);
            }
        }

        if (errors.hasFieldErrors("graceDuration")) {
            this.graceDuration = null;
        }

        if (dateValidator == null) {
            dateValidator = new DateValidator();
        }
        if (!dateValidator.formsValidDate(this.disbursementDateDD, this.disbursementDateMM, this.disbursementDateYY)) {
            String defaultErrorMessage = "Please specify valid disbursal date.";
            rejectDisbursementDateField(errors, defaultErrorMessage, "disbursementDateDD", "loanAccountFormBean.DisbursalDate.invalid", "");
        } else {
            LocalDate validDate = new DateTime().withDate(disbursementDateYY.intValue(), disbursementDateMM.intValue(), disbursementDateDD.intValue()).toLocalDate();
            
            org.mifos.platform.validations.Errors disbursementDateErrors = new org.mifos.platform.validations.Errors();
            if (this.redoLoanAccount) {
                disbursementDateErrors = loanDisbursementDateValidationServiceFacade.validateLoanWithBackdatedPaymentsDisbursementDate(validDate, customerId, productId);
            } else {
                disbursementDateErrors = loanDisbursementDateValidationServiceFacade.validateLoanDisbursementDate(validDate, customerId, productId);
            }
            for (ErrorEntry entry : disbursementDateErrors.getErrorEntries()) {
                String defaultErrorMessage = "The disbursal date is invalid.";
                rejectDisbursementDateField(errors, defaultErrorMessage, "disbursementDateDD", entry.getErrorCode(), entry.getArgs().get(0));
            }
        }
        
        if (this.sourceOfFundsMandatory && isInvalidSelection(this.fundId)) {
            errors.rejectValue("fundId", "loanAccountFormBean.SourceOfFunds.invalid", "Please specify source of funds.");
        }
        
        if (this.externalIdMandatory && StringUtils.isBlank(this.externalId)) {
            errors.rejectValue("externalId", "loanAccountFormBean.externalid.invalid", "Please specify required external id.");
        }
        
        if (!this.glimApplicable && this.purposeOfLoanMandatory && isInvalidSelection(this.loanPurposeId)) {
            errors.rejectValue("loanPurposeId", "loanAccountFormBean.PurposeOfLoan.invalid", "Please specify loan purpose.");
        }
        
        validateAdministrativeAndAdditionalFees(errors);
                
        if (this.repaymentScheduleIndependentOfCustomerMeeting) {
            if (isInvalidRecurringFrequency(this.repaymentRecursEvery)) {
                errors.rejectValue("repaymentRecursEvery", "loanAccountFormBean.repaymentDay.recursEvery.invalid", "Please specify a valid recurring frequency for repayment day.");
            }
            if (this.weekly) {
                if (isInvalidDayOfWeekSelection()) {
                    errors.rejectValue("repaymentDayOfWeek", "loanAccountFormBean.repaymentDay.weekly.dayOfWeek.invalid", "Please select a day of the week for repayment day.");
                }
            } else if (this.monthly) {
                if (this.monthlyDayOfMonthOptionSelected) {
                    if (isInvalidDayOfMonthEntered()) {
                        errors.rejectValue("repaymentDayOfMonth", "loanAccountFormBean.repaymentDay.monthly.dayOfMonth.invalid", "Please select a day of the month for repayment day.");
                    }
                } else if (this.monthlyWeekOfMonthOptionSelected) {
                    if (isInvalidWeekOfMonthSelection()) {
                        errors.rejectValue("repaymentWeekOfMonth", "loanAccountFormBean.repaymentDay.monthly.weekOfMonth.invalid", "Please select a week of the month for repayment day.");
                    }
                    if (isInvalidDayOfWeekSelection()) {
                        errors.rejectValue("repaymentDayOfWeek", "loanAccountFormBean.repaymentDay.monthly.dayOfWeek.invalid", "Please select a day of the week for repayment day.");
                    }
                }
            }
            
            if (this.variableInstallmentsAllowed) {
                if (this.selectedFeeId != null) {
                    for (Number feeId : this.selectedFeeId) {
                        if (feeId != null) {
                            VariableInstallmentWithFeeValidationResult result = variableInstallmentsFeeValidationServiceFacade.validateFeeCanBeAppliedToVariableInstallmentLoan(feeId.longValue());
                            if (!result.isFeeCanBeAppliedToVariableInstallmentLoan()) {
                                errors.rejectValue("selectedFeeId", "loanAccountFormBean.additionalfees.variableinstallments.invalid", new String[] {result.getFeeName()}, "This type of fee cannot be applied to loan with variable installments.");
                            }
                        }
                    }
                }
                
                int defaultFeeIndex = 0;
                if (this.defaultFeeId != null) {
                    for (Number feeId : this.defaultFeeId) {
                        if (feeId != null) {
                            Boolean feeSelectedForRemoval = this.defaultFeeSelected[defaultFeeIndex];
                            if (feeSelectedForRemoval == null || !feeSelectedForRemoval) {
                                VariableInstallmentWithFeeValidationResult result = variableInstallmentsFeeValidationServiceFacade.validateFeeCanBeAppliedToVariableInstallmentLoan(feeId.longValue());
                                if (!result.isFeeCanBeAppliedToVariableInstallmentLoan()) {
                                    errors.rejectValue("selectedFeeId", "loanAccountFormBean.defaultfees.variableinstallments.invalid", new String[] {result.getFeeName()}, "This type of fee cannot be applied to loan with variable installments.");
                                }
                            }
                        }
                        defaultFeeIndex++;
                    }
                }
            }
        }
        
        if (errors.hasErrors()) {
            for (FieldError fieldError : errors.getFieldErrors()) {
                MessageBuilder builder = new MessageBuilder().error().source(fieldError.getField())
                                                      .codes(fieldError.getCodes())
                                                      .defaultText(fieldError.getDefaultMessage()).args(fieldError.getArguments());
                
                messageContext.addMessage(builder.build());
            }
        }
    }

    private void validateAdministrativeAndAdditionalFees(Errors errors) {
        Set<Integer> feeSet = new HashSet<Integer>();
        if (this.selectedFeeId != null) {
            boolean noDuplicateExists = true;
            for (Number feeId : this.selectedFeeId) {
                if (feeId != null) {
                    noDuplicateExists = feeSet.add(feeId.intValue());
                    if (!noDuplicateExists) {
                        errors.rejectValue("selectedFeeId", "loanAccountFormBean.additionalfees.invalid", "Multiple instances of the same fee are not allowed.");
                        break;
                    }
                }
            }
        }
        
        int defaultFeeIndex = 0;
        if (this.defaultFeeId != null) {
            for (Number feeId : this.defaultFeeId) {
                if (feeId != null) {

                    Boolean feeSelectedForRemoval = this.defaultFeeSelected[defaultFeeIndex];
                    if (feeSelectedForRemoval == null || !feeSelectedForRemoval) {

                        Number amountOrRate = this.defaultFeeAmountOrRate[defaultFeeIndex];
                        if (amountOrRate == null) {
                            errors.rejectValue(
                                    "defaultFeeId",
                                    "loanAccountFormBean.defaultfees.amountOrRate.invalid",
                                    "Please specify fee amount for administrative fee "
                                            + Integer.valueOf(defaultFeeIndex + 1).toString());
                        } else {
                            FeeDto selectedFee = findDefaultFee(feeId.intValue());
                            if (selectedFee.isRateBasedFee()) {
                                // maybe check based on 'interest rate' decimals?
                            } else {
                                BigDecimal feeAmountAsDecimal = new BigDecimal(amountOrRate.toString())
                                        .stripTrailingZeros();
                                int places = feeAmountAsDecimal.scale();
                                if (places > this.digitsAfterDecimalForMonetaryAmounts) {
                                    errors.rejectValue(
                                            "selectedFeeId",
                                            "loanAccountFormBean.defaultfees.amountOrRate.digits.after.decimal.invalid",
                                            new Object[] { Integer.valueOf(defaultFeeIndex + 1),
                                                    this.digitsAfterDecimalForMonetaryAmounts },
                                            "Please specify fee amount for additional fee "
                                                    + Integer.valueOf(defaultFeeIndex + 1).toString());
                                }

                                int digitsBefore = feeAmountAsDecimal.toBigInteger().toString().length();
                                if (digitsBefore > this.digitsBeforeDecimalForMonetaryAmounts) {
                                    errors.rejectValue(
                                            "selectedFeeId",
                                            "loanAccountFormBean.defaultfees.amountOrRate.digits.before.decimal.invalid",
                                            new Object[] { Integer.valueOf(defaultFeeIndex + 1),
                                                    this.digitsAfterDecimalForMonetaryAmounts },
                                            "Please specify fee amount for additional fee "
                                                    + Integer.valueOf(defaultFeeIndex + 1).toString());
                                }

                            }
                        }
                    }
                }
                defaultFeeIndex++;
            }
        }

        int additionalFeeIndex = 0;
        if (this.selectedFeeId != null) {
            for (Number feeId : this.selectedFeeId) {
                if (feeId != null) {
                    Number amountOrRate = this.selectedFeeAmount[additionalFeeIndex];
                    if (amountOrRate == null) {
                        errors.rejectValue("selectedFeeId", "loanAccountFormBean.additionalfees.amountOrRate.invalid", "Please specify fee amount for additional fee " + Integer.valueOf(additionalFeeIndex+1).toString());
                    } else {
                        int digitsAllowedBefore, digitsAllowedAfter;
                        if (findAdditionalFee(feeId.intValue()).isRateBasedFee()) {
                            digitsAllowedBefore = this.digitsBeforeDecimalForInterest;
                            digitsAllowedAfter = this.digitsAfterDecimalForInterest;
                        } else {
                            digitsAllowedBefore = this.digitsBeforeDecimalForMonetaryAmounts;
                            digitsAllowedAfter = this.digitsAfterDecimalForMonetaryAmounts;
                        }

                        BigDecimal feeAmountAsDecimal = new BigDecimal(amountOrRate.toString())
                                .stripTrailingZeros();
                        int places = feeAmountAsDecimal.scale();
                        if (places > digitsAllowedAfter) {
                            errors.rejectValue(
                                    "selectedFeeId",
                                    "loanAccountFormBean.additionalfees.amountOrRate.digits.after.decimal.invalid",
                                    new Object[] { Integer.valueOf(additionalFeeIndex + 1),
                                            digitsAllowedAfter },
                                    "Please specify fee amount for additional fee "
                                            + Integer.valueOf(additionalFeeIndex + 1).toString());
                        }

                        int digitsBefore = feeAmountAsDecimal.toBigInteger().toString().length();
                        if (digitsBefore > digitsAllowedBefore) {
                            errors.rejectValue(
                                    "selectedFeeId",
                                    "loanAccountFormBean.additionalfees.amountOrRate.digits.before.decimal.invalid",
                                    new Object[] { Integer.valueOf(additionalFeeIndex + 1),
                                            digitsAllowedBefore },
                                    "Please specify fee amount for additional fee "
                                            + Integer.valueOf(additionalFeeIndex + 1).toString());
                        }
                    }
                }
                additionalFeeIndex++;
            }
        }
    }

    private FeeDto findDefaultFee(Integer feeId) {
        FeeDto found = null;
        for (FeeDto fee :this.defaultFees) {
            if (fee.getId().equals(feeId.toString())) {
                found = fee;
            }
        }
        return found;
    }

    private FeeDto findAdditionalFee(Integer feeId) {
        FeeDto found = null;
        for (FeeDto fee :this.additionalFees) {
            if (fee.getId().equals(feeId.toString())) {
                found = fee;
            }
        }
        return found;
    }

    private boolean isInvalidDayOfMonthEntered() {
        return this.repaymentDayOfMonth == null || this.repaymentDayOfMonth < 1 || this.repaymentDayOfMonth > 31;
    }

    private boolean isInvalidWeekOfMonthSelection() {
        return this.repaymentWeekOfMonth == null;
    }

    private boolean isInvalidDayOfWeekSelection() {
        return this.repaymentDayOfWeek == null;
    }

    private boolean isInvalidRecurringFrequency(Integer recursEvery) {
        return (recursEvery == null || recursEvery < 1);
    }

    private void rejectGlimTotalAmountField(Errors errors, String defaultErrorMessage) {
        errors.rejectValue("disbursementDateDD", "loanAccountFormBean.glim.totalAmount.invalid", new Object[] {this.minAllowedAmount, this.maxAllowedAmount}, defaultErrorMessage);
    }

    private void rejectGroupLoanWithTooFewClients(Errors errors, String defaultErrorMessage) {
        errors.rejectValue("disbursementDateDD", "loanAccountFormBean.glim.notEnoughClients", defaultErrorMessage);
    }

    private void rejectUnselectedGlimClientAmountField(int clientIndex, Errors errors, String defaultErrorMessage) {
        errors.rejectValue("clientAmount", "loanAccountFormBean.glim.client.notselected", new Object[] {clientIndex}, defaultErrorMessage);
    }

    private void rejectGlimClientAmountField(int clientIndex, Errors errors, String defaultErrorMessage) {
        errors.rejectValue("clientAmount", "loanAccountFormBean.glim.clientAmount.invalid", new Object[] {clientIndex, this.minAllowedAmount, this.maxAllowedAmount}, defaultErrorMessage);
    }

    private void rejectDisbursementDateField(Errors errors, String defaultErrorMessage, String field, String errorCode, String args) {
        errors.rejectValue(field, errorCode, new Object[] {args}, defaultErrorMessage);
    }

    private void rejectNumberOfInstallmentsField(Errors errors, String defaultErrorMessage) {
        errors.rejectValue("numberOfInstallments", "loanAccountFormBean.NumberOfInstallments.invalid", new Object[] {this.minNumberOfInstallments, this.maxNumberOfInstallments}, defaultErrorMessage);
    }

    private void rejectAmountField(Errors errors, String defaultErrorMessage) {
        errors.rejectValue("amount", "loanAccountFormBean.Amount.invalid", new Object[] {this.minAllowedAmount, this.maxAllowedAmount}, defaultErrorMessage);
    }
    
    private void rejectInterestRateField(Errors errors, String defaultErrorMessage) {
        errors.rejectValue("interestRate", "loanAccountFormBean.InterestRate.invalid", new Object[] {this.minAllowedInterestRate, this.maxAllowedInterestRate}, defaultErrorMessage);
    }
    
    private void rejectInterestRateFieldValue(Errors errors, String defaultErrorMessage, String errorCode, Object[] args) {
        errors.rejectValue("interestRate", errorCode, args, defaultErrorMessage);
    }

    private void handleDataMappingError(Errors errors, Message message) {
        if ("amount".equals(message.getSource())) {
            rejectAmountField(errors, message.getText());
        }
        
        if ("interestRate".equals(message.getSource())) {
            rejectInterestRateField(errors, message.getText());
        }
        
        if ("numberOfInstallments".equals(message.getSource())) {
            rejectNumberOfInstallmentsField(errors, message.getText());
        }
        
        if ("disbursementDateDD".equals(message.getSource())) {
            rejectDisbursementDateField(errors, message.getText(), message.getSource().toString(), "loanAccountFormBean.DisbursalDate.dd.invalid", "");
        }
        
        if ("disbursementDateMM".equals(message.getSource())) {
            rejectDisbursementDateField(errors, message.getText(), message.getSource().toString(), "loanAccountFormBean.DisbursalDate.mm.invalid", "");
        }
        
        if ("disbursementDateYY".equals(message.getSource())) {
            rejectDisbursementDateField(errors, message.getText(), message.getSource().toString(), "loanAccountFormBean.DisbursalDate.yyyy.invalid", "");
        }
        
        if (message.getSource().toString().startsWith("selectedFeeAmount")) {
            rejectAdditionalOrDefaultFeeField(errors, message.getText(), "loanAccountFormBean.additionalfee.invalid");
        }
        
        String messageSource = message.getSource().toString();
        if (messageSource.startsWith("defaultFeeAmountOrRate")) {
            int first = messageSource.indexOf("[");
            int last = messageSource.indexOf("]");
            Integer feeIndex = Integer.valueOf(messageSource.substring(first+1, last));
            Boolean feeSelectedForRemoval = this.defaultFeeSelected[feeIndex]; 
            if (feeSelectedForRemoval == null || !feeSelectedForRemoval) {
                rejectAdditionalOrDefaultFeeField(errors, message.getText(), "loanAccountFormBean.additionalfee.invalid");
            }
        }
    }

    private void rejectAdditionalOrDefaultFeeField(Errors errors, String defaultErrorMessage, String errorCode) {
        errors.rejectValue("selectedFeeAmount[0]", errorCode, defaultErrorMessage);
    }

    private boolean isInvalidSelection(Integer selectionId) {
        return selectionId == null;
    }

    private boolean exceedsMinOrMax(Number defaultValue, Number minValue, Number maxValue) {
        return defaultValue.doubleValue() > maxValue.doubleValue() || defaultValue.doubleValue() < minValue.doubleValue();
    }
    
    public Number[] getSelectedFeeId() {
		return selectedFeeId;
	}

	public void setSelectedFeeId(Number[] selectedFeeId) {
		this.selectedFeeId = selectedFeeId;
	}

	public Number[] getSelectedFeeAmount() {
		return selectedFeeAmount;
	}

	public void setSelectedFeeAmount(Number[] selectedFeeAmount) {
		this.selectedFeeAmount = selectedFeeAmount;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public String getCollateralNotes() {
		return collateralNotes;
	}

	public void setCollateralNotes(String collateralNotes) {
		this.collateralNotes = collateralNotes;
	}

	public Integer getLoanPurposeId() {
		return loanPurposeId;
	}

	public void setLoanPurposeId(Integer loanPurposeId) {
		this.loanPurposeId = loanPurposeId;
	}

	public Integer getCollateralTypeId() {
		return collateralTypeId;
	}

	public void setCollateralTypeId(Integer collateralTypeId) {
		this.collateralTypeId = collateralTypeId;
	}

	public Integer getFundId() {
		return fundId;
	}

	public void setFundId(Integer fundId) {
		this.fundId = fundId;
	}

	public Number getGraceDuration() {
		return graceDuration;
	}

	public void setGraceDuration(Number graceDuration) {
		this.graceDuration = graceDuration;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Number getAmount() {
		return amount;
	}

	public void setAmount(Number amount) {
		this.amount = amount;
	}

	public Number getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Number interestRate) {
		this.interestRate = interestRate;
	}

	public Number getNumberOfInstallments() {
		return numberOfInstallments;
	}

	public void setNumberOfInstallments(Number numberOfInstallments) {
		this.numberOfInstallments = numberOfInstallments;
	}

	public Number getDisbursementDateDD() {
		return disbursementDateDD;
	}

	public void setDisbursementDateDD(Number disbursalDateDay) {
		this.disbursementDateDD = disbursalDateDay;
	}

	public Number getDisbursementDateMM() {
		return disbursementDateMM;
	}

	public void setDisbursementDateMM(Number disbursalDateMonth) {
		this.disbursementDateMM = disbursalDateMonth;
	}

	public Number getDisbursementDateYY() {
		return disbursementDateYY;
	}

	public void setDisbursementDateYY(Number disbursalDateYear) {
		this.disbursementDateYY = disbursalDateYear;
	}
	
    public Number getMinAllowedAmount() {
        return minAllowedAmount;
    }

    public void setMinAllowedAmount(Number minAllowedAmount) {
        this.minAllowedAmount = minAllowedAmount;
    }

    public Number getMaxAllowedAmount() {
        return maxAllowedAmount;
    }

    public void setMaxAllowedAmount(Number maxAllowedAmount) {
        this.maxAllowedAmount = maxAllowedAmount;
    }
    
    public Number getMinAllowedInterestRate() {
        return minAllowedInterestRate;
    }

    public void setMinAllowedInterestRate(Number minAllowedInterestRate) {
        this.minAllowedInterestRate = minAllowedInterestRate;
    }

    public Number getMaxAllowedInterestRate() {
        return maxAllowedInterestRate;
    }

    public void setMaxAllowedInterestRate(Number maxAllowedInterestRate) {
        this.maxAllowedInterestRate = maxAllowedInterestRate;
    }

    public Number getMinNumberOfInstallments() {
        return minNumberOfInstallments;
    }

    public void setMinNumberOfInstallments(Number minNumberOfInstallments) {
        this.minNumberOfInstallments = minNumberOfInstallments;
    }

    public Number getMaxNumberOfInstallments() {
        return maxNumberOfInstallments;
    }

    public void setMaxNumberOfInstallments(Number maxNumberOfInstallments) {
        this.maxNumberOfInstallments = maxNumberOfInstallments;
    }
    
    public boolean isSourceOfFundsMandatory() {
        return sourceOfFundsMandatory;
    }

    public void setSourceOfFundsMandatory(boolean sourceOfFundsMandatory) {
        this.sourceOfFundsMandatory = sourceOfFundsMandatory;
    }

    public boolean isPurposeOfLoanMandatory() {
        return purposeOfLoanMandatory;
    }

    public void setPurposeOfLoanMandatory(boolean purposeOfLoanMandatory) {
        this.purposeOfLoanMandatory = purposeOfLoanMandatory;
    }
    
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    
    public void setValidator(MifosBeanValidator validator) {
        this.validator = validator;
    }

    public void setDateValidator(DateValidator dateValidator) {
        this.dateValidator = dateValidator;
    }
    
    public void setLoanDisbursementDateValidationServiceFacade(
            LoanDisbursementDateValidationServiceFacade loanDisbursementDateValidationServiceFacade) {
        this.loanDisbursementDateValidationServiceFacade = loanDisbursementDateValidationServiceFacade;
    }
    
    public boolean isRepaymentScheduleIndependentOfCustomerMeeting() {
        return repaymentScheduleIndependentOfCustomerMeeting;
    }

    public void setRepaymentScheduleIndependentOfCustomerMeeting(boolean repaymentScheduleIndependentOfCustomerMeeting) {
        this.repaymentScheduleIndependentOfCustomerMeeting = repaymentScheduleIndependentOfCustomerMeeting;
    }

    public Integer getRepaymentRecursEvery() {
        return repaymentRecursEvery;
    }

    public void setRepaymentRecursEvery(Integer repaymentRecursEvery) {
        this.repaymentRecursEvery = repaymentRecursEvery;
    }

    public Integer getRepaymentDayOfWeek() {
        return repaymentDayOfWeek;
    }

    public void setRepaymentDayOfWeek(Integer repaymentDayOfWeek) {
        this.repaymentDayOfWeek = repaymentDayOfWeek;
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
    
    public Integer[] getClientLoanPurposeId() {
        return this.clientLoanPurposeId;
    }

    public void setClientLoanPurposeId(Integer[] clientLoanPurposeId) {
        this.clientLoanPurposeId = clientLoanPurposeId;
    }

    public Number[] getClientAmount() {
        return clientAmount;
    }

    public void setClientAmount(Number[] clientAmount) {
        this.clientAmount = clientAmount;
    }
    
    public boolean isGlimApplicable() {
        return glimApplicable;
    }

    public void setGlimApplicable(boolean glimApplicable) {
        this.glimApplicable = glimApplicable;
    }
    
    public Boolean[] getClientSelectForGroup() {
        return clientSelectForGroup;
    }

    public void setClientSelectForGroup(Boolean[] clientSelectForGroup) {
        this.clientSelectForGroup = clientSelectForGroup;
    }
    
    public boolean isClientSelected(Integer index) {
        Boolean result = this.clientSelectForGroup[index];
        boolean clientSelected = false;
        if (result != null) {
            clientSelected = result.booleanValue();
        }
        return clientSelected;
    }
    
    public String[] getClientGlobalId() {
        return clientGlobalId;
    }

    public void setClientGlobalId(String[] clientGlobalId) {
        this.clientGlobalId = clientGlobalId;
    }
    
    public Integer getRepaymentDayOfMonth() {
        return repaymentDayOfMonth;
    }

    public void setRepaymentDayOfMonth(Integer repaymentDayOfMonth) {
        this.repaymentDayOfMonth = repaymentDayOfMonth;
    }
    
    public Integer getRepaymentWeekOfMonth() {
        return repaymentWeekOfMonth;
    }

    public void setRepaymentWeekOfMonth(Integer repaymentWeekOfMonth) {
        this.repaymentWeekOfMonth = repaymentWeekOfMonth;
    }
    
    public boolean isMonthly() {
        return monthly;
    }

    public void setMonthly(boolean monthly) {
        this.monthly = monthly;
    }

    public boolean isMonthlyDayOfMonthOptionSelected() {
        return monthlyDayOfMonthOptionSelected;
    }

    public void setMonthlyDayOfMonthOptionSelected(boolean monthlyDayOfMonthOptionSelected) {
        this.monthlyDayOfMonthOptionSelected = monthlyDayOfMonthOptionSelected;
    }

    public boolean isMonthlyWeekOfMonthOptionSelected() {
        return monthlyWeekOfMonthOptionSelected;
    }

    public void setMonthlyWeekOfMonthOptionSelected(boolean monthlyWeekOfMonthOptionSelected) {
        this.monthlyWeekOfMonthOptionSelected = monthlyWeekOfMonthOptionSelected;
    }

    public boolean isWeekly() {
        return weekly;
    }

    public void setWeekly(boolean weekly) {
        this.weekly = weekly;
    }
    
    public String getMontlyOption() {
        return montlyOption;
    }

    public void setMontlyOption(String montlyOption) {
        this.montlyOption = montlyOption;
        if (this.montlyOption.equalsIgnoreCase("weekOfMonth")) {
            this.monthlyDayOfMonthOptionSelected = false;
            this.monthlyWeekOfMonthOptionSelected = true;
        } else if (this.montlyOption.equalsIgnoreCase("dayOfMonth")) {
            this.monthlyDayOfMonthOptionSelected = true;
            this.monthlyWeekOfMonthOptionSelected = true;
        }
    }

    public void setDayOfMonthDetails(Integer dayOfMonth, Integer recursEvery) {
        this.weekly = false;
        this.monthly = true;
        this.monthlyDayOfMonthOptionSelected = true;
        this.monthlyWeekOfMonthOptionSelected = false;
        this.repaymentDayOfMonth = dayOfMonth;
        this.repaymentWeekOfMonth = null;
        this.repaymentDayOfWeek = null;
        this.repaymentRecursEvery = recursEvery;
        this.montlyOption = "dayOfMonth";
    }

    public void setWeekOfMonthDetails(Integer weekOfMonth, Integer dayOfWeek, Integer recursEvery) {
        this.weekly = false;
        this.monthly = true;
        this.monthlyDayOfMonthOptionSelected = false;
        this.monthlyWeekOfMonthOptionSelected = true;
        this.repaymentDayOfMonth = null;
        this.repaymentWeekOfMonth = weekOfMonth;
        this.repaymentDayOfWeek = dayOfWeek;
        this.repaymentRecursEvery = recursEvery;
        this.montlyOption = "weekOfMonth";
    }

    public void setWeeklyDetails(Integer dayOfWeek, Integer recursEvery) {
        this.weekly = true;
        this.monthly = false;
        this.monthlyDayOfMonthOptionSelected = false;
        this.monthlyWeekOfMonthOptionSelected = false;
        this.repaymentDayOfMonth = null;
        this.repaymentWeekOfMonth = null;
        this.repaymentDayOfWeek = dayOfWeek;
        this.repaymentRecursEvery = recursEvery;
    }
    
    public Boolean[] getDefaultFeeSelected() {
        return defaultFeeSelected;
    }

    public void setDefaultFeeSelected(Boolean[] defaultFeeSelected) {
        this.defaultFeeSelected = defaultFeeSelected;
    }
    
    public Number[] getDefaultFeeAmountOrRate() {
        return defaultFeeAmountOrRate;
    }

    public void setDefaultFeeAmountOrRate(Number[] defaultFeeAmountOrRate) {
        this.defaultFeeAmountOrRate = defaultFeeAmountOrRate;
    }
    
    public Number[] getDefaultFeeId() {
        return defaultFeeId;
    }

    public void setDefaultFeeId(Number[] defaultFeeId) {
        this.defaultFeeId = defaultFeeId;
    }
    
    public Number getMaxGraceDuration() {
        return maxGraceDuration;
    }

    public void setMaxGraceDuration(Number maxGraceDuration) {
        this.maxGraceDuration = maxGraceDuration;
    }

    public int getDigitsAfterDecimalForInterest() {
        return digitsAfterDecimalForInterest;
    }

    public void setDigitsAfterDecimalForInterest(int digitsAfterDecimalForInterest) {
        this.digitsAfterDecimalForInterest = digitsAfterDecimalForInterest;
    }
    
    public int getDigitsBeforeDecimalForInterest() {
        return digitsBeforeDecimalForInterest;
    }

    public void setDigitsBeforeDecimalForInterest(int digitsBeforeDecimalForInterest) {
        this.digitsBeforeDecimalForInterest = digitsBeforeDecimalForInterest;
    }
    
    public int getDigitsAfterDecimalForMonetaryAmounts() {
        return digitsAfterDecimalForMonetaryAmounts;
    }

    public void setDigitsAfterDecimalForMonetaryAmounts(int digitsAfterDecimalForMonetaryAmounts) {
        this.digitsAfterDecimalForMonetaryAmounts = digitsAfterDecimalForMonetaryAmounts;
    }
    
    public int getDigitsBeforeDecimalForMonetaryAmounts() {
        return digitsBeforeDecimalForMonetaryAmounts;
    }

    public void setDigitsBeforeDecimalForMonetaryAmounts(int digitsBeforeDecimalForMonetaryAmounts) {
        this.digitsBeforeDecimalForMonetaryAmounts = digitsBeforeDecimalForMonetaryAmounts;
    }
    
    public String getNumberFormatForMonetaryAmounts() {
        StringBuilder numberFormat = new StringBuilder("0.");
        for(int i=0; i<this.digitsAfterDecimalForMonetaryAmounts; i++) {
            numberFormat.append("#");
        }
        
        if (this.digitsAfterDecimalForMonetaryAmounts == 0) {
            numberFormat = new StringBuilder("0");
        }
        
        return numberFormat.toString();
    }
    
    public boolean isRedoLoanAccount() {
        return redoLoanAccount;
    }

    public void setRedoLoanAccount(boolean redoLoanAccount) {
        this.redoLoanAccount = redoLoanAccount;
    }
    
    public boolean isCollateralTypeAndNotesHidden() {
        return collateralTypeAndNotesHidden;
    }

    public void setCollateralTypeAndNotesHidden(boolean collateralTypeAndNotesHidden) {
        this.collateralTypeAndNotesHidden = collateralTypeAndNotesHidden;
    }

    public boolean isExternalIdHidden() {
        return externalIdHidden;
    }

    public void setExternalIdHidden(boolean externalIdHidden) {
        this.externalIdHidden = externalIdHidden;
    }

    public boolean isExternalIdMandatory() {
        return externalIdMandatory;
    }

    public void setExternalIdMandatory(boolean externalIdMandatory) {
        this.externalIdMandatory = externalIdMandatory;
    }
    
    public List<FeeDto> getDefaultFees() {
        return defaultFees;
    }

    public void setDefaultFees(List<FeeDto> defaultFees) {
        this.defaultFees = defaultFees;
    }

    public List<FeeDto> getAdditionalFees() {
        return additionalFees;
    }

    public void setAdditionalFees(List<FeeDto> additionalFees) {
        this.additionalFees = additionalFees;
    }

    public void setLocale(Locale default1) {
        this.locale = default1;
    }
}