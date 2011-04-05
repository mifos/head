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
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.clientportfolio.newloan.applicationservice.LoanDisbursementDateValidationServiceFacade;
import org.mifos.platform.validation.MifosBeanValidator;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageCriteria;
import org.springframework.binding.validation.ValidationContext;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanAccountFormBean implements Serializable {

    @Autowired
    private transient MifosBeanValidator validator;
    
    @Autowired
    private transient LoanDisbursementDateValidationServiceFacade loanDisbursementDateValidationServiceFacade;
    
    private transient DateValidator dateValidator;

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
    private Number disbursalDateDay;
    private Number disbursalDateMonth;
    private Number disbursalDateYear;
    
    private Number graceDuration = Integer.valueOf(0);
    
    private boolean sourceOfFundsMandatory;
    private Integer fundId;
    
    private boolean purposeOfLoanMandatory;
    private Integer loanPurposeId;
    
    private Integer collateralTypeId;
    private String collateralNotes;
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
    private Integer[] clientLoanPurposeId = new Integer[1];
    
    // fees
    private Boolean[] defaultFeeSelected;
    private Number[] defaultFeeAmountOrRate;
    private Number[] defaultFeeId;

    private Number[] selectedFeeId;
    private Number[] selectedFeeAmount;

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

                        if (clientAmount == null || exceedsMax(clientAmount, this.maxAllowedAmount)) {
                            String defaultErrorMessage = "Please specify valid Amount.";
                            rejectGlimClientAmountField(index + 1, errors, defaultErrorMessage);
                        }

                        // check error message of loan purpose for each client when its mandatory..
                        Integer clientLoanPurposeId = this.clientLoanPurposeId[index];
                        if (this.purposeOfLoanMandatory && isInvalidSelection(clientLoanPurposeId)) {
                            errors.rejectValue("clientLoanPurposeId", "loanAccountFormBean.glim.purposeOfLoan.invalid", new Object[] {index+1}, "Please specify loan purpose.");
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
        
        if (this.interestRate == null || exceedsMinOrMax(this.interestRate, this.minAllowedInterestRate, this.maxAllowedInterestRate)) {
            String defaultErrorMessage = "Please specify valid Interest rate.";
            rejectInterestRateField(errors, defaultErrorMessage);
        }
        
        if (this.numberOfInstallments == null || exceedsMinOrMax(this.numberOfInstallments, this.minNumberOfInstallments, this.maxNumberOfInstallments)) {
            String defaultErrorMessage = "Please specify valid number of installments.";
            rejectNumberOfInstallmentsField(errors, defaultErrorMessage);
        }

        if (dateValidator == null) {
            dateValidator = new DateValidator();
        }
        if (!dateValidator.formsValidDate(this.disbursalDateDay, this.disbursalDateMonth, this.disbursalDateYear)) {
            String defaultErrorMessage = "Please specify valid disbursal date.";
            rejectDisbursementDateField(errors, defaultErrorMessage, "loanAccountFormBean.DisbursalDate.invalid");
        } else {
            LocalDate validDate = new DateTime().withDate(disbursalDateYear.intValue(), disbursalDateMonth.intValue(), disbursalDateDay.intValue()).toLocalDate();
            try {
                loanDisbursementDateValidationServiceFacade.validateLoanDisbursementDate(validDate, customerId, productId);
            } catch (BusinessRuleException e) {
                String defaultErrorMessage = "The disbursal date is invalid.";
                rejectDisbursementDateField(errors, defaultErrorMessage, "loanAccountFormBean.DisbursalDate.validButNotAllowed");
            }
        }
        
        if (this.sourceOfFundsMandatory && isInvalidSelection(this.fundId)) {
            errors.rejectValue("fundId", "loanAccountFormBean.SourceOfFunds.invalid", "Please specify source of funds.");
        }
        
        if (!this.glimApplicable && this.purposeOfLoanMandatory && isInvalidSelection(this.loanPurposeId)) {
            errors.rejectValue("loanPurposeId", "loanAccountFormBean.PurposeOfLoan.invalid", "Please specify loan purpose.");
        }
        
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
        }
        
        // validate additional fees not duplicated
        Set<Integer> feeSet = new HashSet<Integer>();
        if (this.selectedFeeId != null) {
            for (Number feeId : this.selectedFeeId) {
                boolean noDuplicateExists = feeSet.add(feeId.intValue());
                if (!noDuplicateExists) {
                    errors.rejectValue("selectedFeeId", "loanAccountFormBean.additionalfees.invalid", "Multiple instances of the same fee are not allowed.");
                    break;
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
        errors.rejectValue("disbursalDateDay", "loanAccountFormBean.glim.totalAmount.invalid", new Object[] {this.minAllowedAmount, this.maxAllowedAmount}, defaultErrorMessage);
    }

    private void rejectGroupLoanWithTooFewClients(Errors errors, String defaultErrorMessage) {
        errors.rejectValue("disbursalDateDay", "loanAccountFormBean.glim.notEnoughClients", defaultErrorMessage);
    }

    private void rejectUnselectedGlimClientAmountField(int clientIndex, Errors errors, String defaultErrorMessage) {
        errors.rejectValue("clientAmount", "loanAccountFormBean.glim.client.notselected", new Object[] {clientIndex}, defaultErrorMessage);
    }

    private void rejectGlimClientAmountField(int clientIndex, Errors errors, String defaultErrorMessage) {
        errors.rejectValue("clientAmount", "loanAccountFormBean.glim.clientAmount.invalid", new Object[] {clientIndex, this.minAllowedAmount, this.maxAllowedAmount}, defaultErrorMessage);
    }

    private void rejectDisbursementDateField(Errors errors, String defaultErrorMessage, String errorCode) {
        errors.rejectValue("disbursalDateDay", errorCode, defaultErrorMessage);
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
        
        if (isAnyOf("disbursalDateDay", "disbursalDateMonth", "disbursalDateYear", message.getSource())) {
            rejectDisbursementDateField(errors, message.getText(), "loanAccountFormBean.DisbursalDate.invalid");
        }
    }

    private boolean isAnyOf(String field1, String field2, String field3, Object sourceField) {
        return (field1.equals(sourceField) || field2.equals(sourceField) || field3.equals(sourceField));
    }

    private boolean isInvalidSelection(Integer selectionId) {
        return selectionId == null;
    }

    private boolean exceedsMinOrMax(Number defaultValue, Number minValue, Number maxValue) {
        return defaultValue.doubleValue() > maxValue.doubleValue() || defaultValue.doubleValue() < minValue.doubleValue();
    }
    
    private boolean exceedsMax(Number defaultValue, Number maxValue) {
        return defaultValue.doubleValue() > maxValue.doubleValue();
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

	public Number getDisbursalDateDay() {
		return disbursalDateDay;
	}

	public void setDisbursalDateDay(Number disbursalDateDay) {
		this.disbursalDateDay = disbursalDateDay;
	}

	public Number getDisbursalDateMonth() {
		return disbursalDateMonth;
	}

	public void setDisbursalDateMonth(Number disbursalDateMonth) {
		this.disbursalDateMonth = disbursalDateMonth;
	}

	public Number getDisbursalDateYear() {
		return disbursalDateYear;
	}

	public void setDisbursalDateYear(Number disbursalDateYear) {
		this.disbursalDateYear = disbursalDateYear;
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
}