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
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.loan.service.CreateLoanSchedule;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.newloan.applicationservice.CreateGlimLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.GroupMemberAccountDto;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.clientportfolio.newloan.applicationservice.LoanApplicationStateDto;
import org.mifos.dto.domain.CashFlowDto;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CreateAccountPenaltyDto;
import org.mifos.dto.domain.CustomerSearchDto;
import org.mifos.dto.domain.CustomerSearchResultDto;
import org.mifos.dto.domain.FeeDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.dto.domain.PenaltyDto;
import org.mifos.dto.screen.CashFlowDataDto;
import org.mifos.dto.screen.CustomerSearchResultsDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.dto.screen.LoanCreationProductDetailsDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanInstallmentsDto;
import org.mifos.dto.screen.LoanScheduleDto;
import org.mifos.dto.screen.SearchDetailsDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.validations.ErrorEntry;
import org.mifos.service.BusinessRuleException;
import org.mifos.ui.core.controller.util.helpers.LoanCreationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.stereotype.Controller;

@SuppressWarnings("PMD")
@Controller
public class LoanAccountController {

	private final LoanAccountServiceFacade loanAccountServiceFacade;
    private final AdminServiceFacade adminServiceFacade;

	@Autowired
    public LoanAccountController(LoanAccountServiceFacade loanAccountServiceFacade, AdminServiceFacade adminServiceFacade) {
		this.loanAccountServiceFacade = loanAccountServiceFacade;
        this.adminServiceFacade = adminServiceFacade;
    }
	
	public void markAsRedoLoanAccount(CustomerSearchFormBean formBean) {
	    formBean.setRedoLoanAccount(true);
	}
	
	public void markAsRedoLoanAccount(LoanAccountFormBean formBean) {
        formBean.setRedoLoanAccount(true);
    }

	public CustomerSearchResultsDto searchCustomers(CustomerSearchFormBean formBean, boolean isNewGLIMCreation) {

        // Search result cap. This is needed until ajax search is implemented.
        Integer searchCap = 1000;

    	CustomerSearchDto customerSearchDto = new CustomerSearchDto(formBean.getSearchString(), Integer.valueOf(1), searchCap);
    	List<CustomerSearchResultDto> pagedDetails = this.loanAccountServiceFacade.retrieveCustomersThatQualifyForLoans(customerSearchDto, isNewGLIMCreation);

    	//int firstResult = formBean.getPage() * formBean.getPageSize() - (formBean.getPageSize()-1);

    	// FIXME selected pageNumber and pageSize info should be passed in on bean.
		SearchDetailsDto searchDetails = new SearchDetailsDto(pagedDetails.size(),  1, 1, searchCap);
        return new CustomerSearchResultsDto(searchDetails, pagedDetails);
    }

    public LoanCreationProductDetailsDto retrieveLoanProducts(int customerId, org.springframework.binding.message.MessageContext messageContext) {
        LoanCreationProductDetailsDto loanProductDetails = this.loanAccountServiceFacade.retrieveGetProductDetailsForLoanAccountCreation(customerId);
        
        if (loanProductDetails.getErrors().hasErrors()) {
          
            ErrorEntry entry = loanProductDetails.getErrors().getErrorEntries().get(0);
            MessageBuilder builder = new MessageBuilder().error().source(entry.getFieldName())
            .codes(Arrays.asList(entry.getErrorCode()).toArray(new String[1]))
            .defaultText(entry.getDefaultMessage());

            messageContext.addMessage(builder.build());
        }
        return loanProductDetails;
    }

    @SuppressWarnings("PMD")
    public LoanCreationLoanDetailsDto retrieveLoanCreationDetails(int customerId, int productId, LoanAccountFormBean formBean) {

        MandatoryHiddenFieldsDto mandatoryHidden = this.adminServiceFacade.retrieveHiddenMandatoryFieldsToRead();
    	LoanCreationLoanDetailsDto dto = this.loanAccountServiceFacade.retrieveLoanDetailsForLoanAccountCreation(customerId, Integer.valueOf(productId).shortValue(), formBean.isRedoLoanAccount());

    	formBean.setLocale(Locale.getDefault());
    	
    	formBean.setDigitsBeforeDecimalForInterest(dto.getAppConfig().getDigitsBeforeDecimalForInterest());
    	formBean.setDigitsAfterDecimalForInterest(dto.getAppConfig().getDigitsAfterDecimalForInterest());
    	formBean.setDigitsBeforeDecimalForMonetaryAmounts(dto.getAppConfig().getDigitsBeforeDecimalForMonetaryAmounts());
    	formBean.setDigitsAfterDecimalForMonetaryAmounts(dto.getAppConfig().getDigitsAfterDecimalForMonetaryAmounts());
    	
    	formBean.setProductId(productId);
    	formBean.setCustomerId(dto.getCustomerDetailDto().getCustomerId());
    	formBean.setRepaymentScheduleIndependentOfCustomerMeeting(dto.isRepaymentIndependentOfMeetingEnabled());
    	formBean.setGraceDuration(dto.getGracePeriodInInstallments());
    	formBean.setMaxGraceDuration(dto.getGracePeriodInInstallments());
    	
    	if (dto.isRepaymentIndependentOfMeetingEnabled()) {

    	    // use loan product to default meeting details
    	    Integer recursEvery = dto.getLoanOfferingMeetingDetail().getMeetingDetailsDto().getEvery();
            Integer dayOfMonth = dto.getLoanOfferingMeetingDetail().getMeetingDetailsDto().getRecurrenceDetails().getDayNumber();
            Integer weekOfMonth = dto.getLoanOfferingMeetingDetail().getMeetingDetailsDto().getRecurrenceDetails().getWeekOfMonth();
            Integer dayOfWeek = dto.getLoanOfferingMeetingDetail().getMeetingDetailsDto().getRecurrenceDetails().getDayOfWeek();
            Integer recurrenceType = dto.getLoanOfferingMeetingDetail().getMeetingDetailsDto().getRecurrenceTypeId();
            
            Integer customerRecurrenceType = dto.getCustomerMeetingDetail().getMeetingDetailsDto().getRecurrenceTypeId();
    	    if (recurrenceType.equals(customerRecurrenceType)) {
    	        // if customer and product meeting frequencies are the same e.g. weekly or monthly, then default to customer details except for recurrence details
                dayOfMonth = dto.getCustomerMeetingDetail().getMeetingDetailsDto().getRecurrenceDetails().getDayNumber();
                weekOfMonth = dto.getCustomerMeetingDetail().getMeetingDetailsDto().getRecurrenceDetails().getWeekOfMonth();
                dayOfWeek = dto.getCustomerMeetingDetail().getMeetingDetailsDto().getRecurrenceDetails().getDayOfWeek();
    	    }
    	    
    	    // sometimes it seems customer meeting information can be setup wrong (i.e. has a day of month even though its weekly)
            if (recurrenceType == 1) {
                if (dateInformationIsAvailable(dayOfWeek)) {
                    formBean.setWeeklyDetails(dayOfWeek, recursEvery);
                }
            } else if (recurrenceType == 2) {
                if (dateInformationIsAvailable(weekOfMonth) && dateInformationIsAvailable(dayOfWeek)) {
                    formBean.setWeekOfMonthDetails(weekOfMonth, dayOfWeek, recursEvery);
                } else if (dateInformationIsAvailable(dayOfMonth)) {
                    formBean.setDayOfMonthDetails(dayOfMonth, recursEvery);
                }
            }
    	}

    	formBean.setVariableInstallmentsAllowed(dto.isVariableInstallmentsAllowed());
    	if (dto.isVariableInstallmentsAllowed()) {
    	    formBean.setMinGapInDays(dto.getMinGapInDays());
    	    formBean.setMaxGapInDays(dto.getMaxGapInDays());
    	    formBean.setMinInstallmentAmount(dto.getMinInstallmentAmount());
    	}

    	formBean.setGlimApplicable(dto.isGlimApplicable());
    	if (dto.isGlimApplicable()) {
    	    List<LoanAccountDetailsDto> clientData = dto.getClientDetails();
    	    String[] clientGlobalIdArray = new String[clientData.size()];
    	    int index = 0;
    	    for (LoanAccountDetailsDto loanAccountDetailsDto : clientData) {
                clientGlobalIdArray[index] = loanAccountDetailsDto.getClientId();
                index++;
            }
    	    formBean.setClientGlobalId(clientGlobalIdArray);

    	    formBean.setClientSelectForGroup(new Boolean[clientData.size()]);
    	    formBean.setClientAmount(new Number[clientData.size()]);
    	    Integer[] loanPurpose = new Integer[clientData.size()];
    	    formBean.setClientLoanPurposeId(loanPurpose);

    	} else {
    	    formBean.setAmount(Double.valueOf(dto.getDefaultLoanAmount().toPlainString()));
    	}

    	formBean.setMinAllowedAmount(dto.getMinLoanAmount());
    	formBean.setMaxAllowedAmount(dto.getMaxLoanAmount());

    	formBean.setInterestRate(dto.getDefaultInterestRate());
    	formBean.setMinAllowedInterestRate(dto.getMinInterestRate());
    	formBean.setMaxAllowedInterestRate(dto.getMaxInterestRate());

    	formBean.setNumberOfInstallments(dto.getDefaultNumberOfInstallments());
    	formBean.setMinNumberOfInstallments(dto.getMinNumberOfInstallments());
    	formBean.setMaxNumberOfInstallments(dto.getMaxNumberOfInstallments());

    	formBean.setSourceOfFundsMandatory(mandatoryHidden.isMandatoryLoanSourceOfFund());
    	formBean.setPurposeOfLoanMandatory(mandatoryHidden.isMandatoryLoanAccountPurpose());
    	formBean.setCollateralTypeAndNotesHidden(mandatoryHidden.isHideSystemCollateralTypeNotes());
    	formBean.setExternalIdHidden(mandatoryHidden.isHideSystemExternalId());
    	if (!mandatoryHidden.isHideSystemExternalId()) {
    	    formBean.setExternalIdMandatory(mandatoryHidden.isMandatorySystemExternalId());
    	}

    	LocalDate possibleDisbursementDate = dto.getNextPossibleDisbursementDate();
    	if (possibleDisbursementDate != null) {
        	formBean.setDisbursementDateDD(possibleDisbursementDate.getDayOfMonth());
        	formBean.setDisbursementDateMM(possibleDisbursementDate.getMonthOfYear());
        	formBean.setDisbursementDateYY(possibleDisbursementDate.getYearOfEra());
    	}
    	
    	formBean.setCollateralNotes("");

    	Number[] defaultFeeId = new Number[dto.getDefaultFees().size()];
    	Number[] defaultFeeAmountOrRate = new Number[dto.getDefaultFees().size()];
    	int index=0;
    	for (FeeDto defaultFee : dto.getDefaultFees()) {
    	    if (defaultFee.isRateBasedFee()) {
    	        defaultFeeAmountOrRate[index] = defaultFee.getRate();
    	    } else {
    	        defaultFeeAmountOrRate[index] = defaultFee.getAmountAsNumber();
    	    }
    	    defaultFeeId[index] = Long.valueOf(defaultFee.getId());
    	    index++;
    	}
    	formBean.setDefaultFeeAmountOrRate(defaultFeeAmountOrRate);
    	formBean.setDefaultFeeId(defaultFeeId);
    	formBean.setDefaultFeeSelected(new Boolean[dto.getDefaultFees().size()]);
    	formBean.setDefaultFees(dto.getDefaultFees());
    	
        Number[] defaultPenaltyId = new Number[dto.getDefaultPenalties().size()];
        Number[] defaultPenaltyAmountOrRate = new Number[dto.getDefaultPenalties().size()];
        int idx=0;
        for (PenaltyDto defaultPenalty : dto.getDefaultPenalties()) {
            if (defaultPenalty.isRateBasedPenalty()) {
                defaultPenaltyAmountOrRate[idx] = defaultPenalty.getRate();
            } else {
                defaultPenaltyAmountOrRate[idx] = defaultPenalty.getAmountAsNumber();
            }
            defaultPenaltyId[idx] = Long.valueOf(defaultPenalty.getPenaltyId());
            idx++;
        }
        formBean.setDefaultPenaltyAmountOrRate(defaultPenaltyAmountOrRate);
        formBean.setDefaultPenaltyId(defaultPenaltyId);
        formBean.setDefaultPenaltySelected(new Boolean[dto.getDefaultPenalties().size()]);
        formBean.setDefaultPenalties(dto.getDefaultPenalties());

    	Number[] selectedFeeId = new Number[3];
		formBean.setSelectedFeeId(selectedFeeId);

		Number[] selectedFeeAmount = new Number[3];
		formBean.setSelectedFeeAmount(selectedFeeAmount);
        formBean.setAdditionalFees(dto.getAdditionalFees());

    	return dto;
    }

    private boolean dateInformationIsAvailable(Integer dayOfWeek) {
        return dayOfWeek != null && dayOfWeek > 0;
    }

    public void loadQuestionGroups(Integer productId, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean) {

        List<QuestionGroupDetail> questionGroups = loanAccountServiceFacade.retrieveApplicableQuestionGroups(productId);
        loanAccountQuestionGroupFormBean.setQuestionGroups(questionGroups);
    }

    public boolean isCompareForCashFlowEnabled(Integer productId) {
        return this.loanAccountServiceFacade.isCompareWithCashFlowEnabledOnProduct(productId);
    }

    public LoanScheduleDto retrieveLoanSchedule(int customerId, int productId, LoanAccountFormBean formBean, BackdatedPaymentable loanScheduleFormBean, boolean resetRedoLoanAccountDetails) {

        LocalDate disbursementDate =  LoanCreationHelper.translateDisbursementDateToLocalDate(formBean);

        RecurringSchedule recurringSchedule = LoanCreationHelper.determineRecurringSchedule(formBean);
        List<CreateAccountFeeDto> accountFees = LoanCreationHelper.translateToAccountFeeDtos(formBean);
        List<CreateAccountFeeDto> additionalAccountFees = LoanCreationHelper.translateToAdditionalAccountFeeDtos(formBean);
        accountFees.addAll(additionalAccountFees);

        CreateLoanSchedule createLoanAccount = new CreateLoanSchedule(customerId, productId, BigDecimal.valueOf(formBean.getAmount().doubleValue()), formBean.getInterestRate().doubleValue(), disbursementDate,
                formBean.getNumberOfInstallments().intValue(), formBean.getGraceDuration().intValue(), formBean.isRepaymentScheduleIndependentOfCustomerMeeting(), recurringSchedule, accountFees);

        LoanScheduleDto loanSchedule = null;
        if (formBean.isVariableInstallmentsAllowed() && !loanScheduleFormBean.getInstallments().isEmpty()) {
            loanSchedule = loanAccountServiceFacade.createLoanSchedule(createLoanAccount, loanScheduleFormBean.getInstallments(), loanScheduleFormBean.getInstallmentAmounts());
        } else {
            loanSchedule = loanAccountServiceFacade.createLoanSchedule(createLoanAccount);
        }
            
        populateFormBeanFromDto(customerId, productId, formBean, loanScheduleFormBean, disbursementDate, loanSchedule, resetRedoLoanAccountDetails);

        return loanSchedule;
    }

    private void populateFormBeanFromDto(int customerId, int productId, LoanAccountFormBean formBean,
            BackdatedPaymentable loanScheduleFormBean, LocalDate disbursementDate, LoanScheduleDto loanSchedule, boolean resetActualPaymentDatesAndAmountsForRedoLoan) {
        List<DateTime> installments = new ArrayList<DateTime>();
        List<DateTime> actualPaymentDates = new ArrayList<DateTime>();
        List<Number> installmentAmounts = new ArrayList<Number>();
        List<Number> actualPaymentAmounts = new ArrayList<Number>();
        List<Short> actualPaymentTypes = new ArrayList<Short>();

        BigDecimal totalLoanInterest = BigDecimal.ZERO;
        BigDecimal totalLoanFees = BigDecimal.ZERO;
        for (LoanCreationInstallmentDto installment :loanSchedule.getInstallments()) {
            
            totalLoanInterest = totalLoanInterest.add(BigDecimal.valueOf(installment.getInterest()));
            totalLoanFees = totalLoanFees.add(BigDecimal.valueOf(installment.getFees()));
            
            installments.add(new DateTime(installment.getDueDate()));
            actualPaymentDates.add(new DateTime(installment.getDueDate()));
            installmentAmounts.add(installment.getTotal());
            if (new LocalDate(installment.getDueDate()).isBefore(new LocalDate().plusDays(1))) {
                actualPaymentAmounts.add(installment.getTotal());
            } else {
                actualPaymentAmounts.add(Double.valueOf("0.0"));
            }
            actualPaymentTypes.add(null);
        }
        loanScheduleFormBean.setInstallments(installments);
        loanScheduleFormBean.setVariableInstallments(loanSchedule.getInstallments());
        loanScheduleFormBean.setInstallmentAmounts(installmentAmounts);
        if (resetActualPaymentDatesAndAmountsForRedoLoan) {
            loanScheduleFormBean.setActualPaymentDates(actualPaymentDates);
            loanScheduleFormBean.setActualPaymentAmounts(actualPaymentAmounts);
            loanScheduleFormBean.setActualPaymentTypes(actualPaymentTypes);
        }
        
        loanScheduleFormBean.setLoanPrincipal(BigDecimal.valueOf(formBean.getAmount().doubleValue()));
        loanScheduleFormBean.setTotalLoanInterest(totalLoanInterest);
        loanScheduleFormBean.setTotalLoanFees(totalLoanFees);
        loanScheduleFormBean.setRepaymentInstallments(loanSchedule.getInstallments());
        if (disbursementDate != null) {
            loanScheduleFormBean.setDisbursementDate(disbursementDate.toDateMidnight().toDate());            
        }
        
        // variable installments related
        loanScheduleFormBean.setVariableInstallmentsAllowed(formBean.isVariableInstallmentsAllowed());
        if (loanScheduleFormBean.isVariableInstallmentsAllowed()) {
            loanScheduleFormBean.setMinGapInDays(formBean.getMinGapInDays());
            loanScheduleFormBean.setMaxGapInDays(formBean.getMaxGapInDays());
            loanScheduleFormBean.setMinInstallmentAmount(formBean.getMinInstallmentAmount());
            
            loanScheduleFormBean.setCustomerId(formBean.getCustomerId());
            loanScheduleFormBean.setLoanAccountFormBean(formBean);
        }

        List<FeeDto> applicableFees = new ArrayList<FeeDto>();
        LoanCreationLoanDetailsDto dto = this.loanAccountServiceFacade.retrieveLoanDetailsForLoanAccountCreation(customerId, Integer.valueOf(productId).shortValue(), formBean.isRedoLoanAccount());
        int feeIndex = 0;
        for (Boolean defaultFeeSelectedForRemoval : formBean.getDefaultFeeSelected()) {
            if (defaultFeeSelectedForRemoval == null || !defaultFeeSelectedForRemoval) {
                Integer feeId = formBean.getDefaultFeeId()[feeIndex].intValue();
                BigDecimal amountOrRate = BigDecimal.valueOf(formBean.getDefaultFeeAmountOrRate()[feeIndex].doubleValue());
                applicableFees.add(findFeeById(dto.getDefaultFees(), feeId, amountOrRate));
            }
            feeIndex++;
        }
        
        List<PenaltyDto> applicablePenalties = new ArrayList<PenaltyDto>();
        int penaltyIndex = 0;
        for (Boolean defaultPenaltySelectedForRemoval : formBean.getDefaultPenaltySelected()) {
            if (defaultPenaltySelectedForRemoval == null || !defaultPenaltySelectedForRemoval) {
                Integer penaltyId = formBean.getDefaultPenaltyId()[penaltyIndex].intValue();
                BigDecimal amountOrRate = BigDecimal.valueOf(formBean.getDefaultPenaltyAmountOrRate()[penaltyIndex].doubleValue());
                applicablePenalties.add(findPenaltyById(dto.getDefaultPenalties(), penaltyId, amountOrRate));
            }
            penaltyIndex++;
        }
        
        feeIndex = 0;
        Number[] additionalFeesSelected = formBean.getSelectedFeeId();
        if (additionalFeesSelected != null) {
            for (Number additionalFee : additionalFeesSelected) {
                if (additionalFee != null) {
                    BigDecimal amountOrRate = BigDecimal.valueOf(formBean.getSelectedFeeAmount()[feeIndex].doubleValue());
                    applicableFees.add(findFeeById(dto.getAdditionalFees(), additionalFee.intValue(), amountOrRate));
                }
                feeIndex++;
            }
        }
        
        loanScheduleFormBean.setApplicableFees(applicableFees);
        loanScheduleFormBean.setApplicablePenalties(applicablePenalties);
    }

    private FeeDto findFeeById(final List<FeeDto> defaultFees, final Integer feeId, final BigDecimal amountOrRate) {
        FeeDto found = null;
        
        for (FeeDto feeDto : defaultFees) {
            if (Integer.valueOf(feeDto.getId()).equals(feeId)) {
                feeDto.setAmount(amountOrRate.toPlainString());
                if (feeDto.isRateBasedFee()) {
                    feeDto.setRate(amountOrRate.doubleValue());
                }
                found = feeDto;
            }
        }
        
        return found;
    }
    
    private PenaltyDto findPenaltyById(final List<PenaltyDto> defaultPenalties, final Integer penaltyId, final BigDecimal amountOrRate) {
        PenaltyDto found = null;
        
        for (PenaltyDto penaltyDto : defaultPenalties) {
            if (Integer.valueOf(penaltyDto.getPenaltyId()).equals(penaltyId)) {
                penaltyDto.setAmount(amountOrRate.toPlainString());
                if (penaltyDto.isRateBasedPenalty()) {
                    penaltyDto.setRate(amountOrRate.doubleValue());
                }
                found = penaltyDto;
            }
        }
        
        return found;
    }





    public CashFlowDto retrieveCashFlowSettings(LoanScheduleDto loanScheduleDto, int productId) {
        DateTime firstInstallment = loanScheduleDto.firstInstallment();
        DateTime lastInstallment = loanScheduleDto.lastInstallment();
        return loanAccountServiceFacade.retrieveCashFlowSettings(firstInstallment, lastInstallment, productId, BigDecimal.valueOf(loanScheduleDto.getLoanAmount()));
    }

    public List<CashFlowDataDto> recalculateCashflowSummaryDetails(CashFlowSummaryFormBean formBean, CashFlowDto cashFlowDto,
            List<MonthlyCashFlowDto> monthlyCashFlow, LoanScheduleDto loanScheduleDto, int productId) {
        
        List<CashFlowDataDto> cashFlowDataDtos = this.loanAccountServiceFacade.retrieveCashFlowSummary(monthlyCashFlow, loanScheduleDto);
        formBean.setCashFlowDataDtos(cashFlowDataDtos);
        formBean.setProductId(productId);
        
        BigDecimal cashFlowTotalBalance = BigDecimal.ZERO;
        for (MonthlyCashFlowDto monthlyCashFlowDto : monthlyCashFlow) {
            cashFlowTotalBalance = cashFlowTotalBalance.add(monthlyCashFlowDto.calculateRevenueMinusExpenses());
        }

        formBean.setMonthlyCashFlows(monthlyCashFlow);
        formBean.setCashFlowTotalBalance(cashFlowTotalBalance);
        formBean.setRepaymentCapacity(cashFlowDto.getRepaymentCapacity());
        
        return cashFlowDataDtos;
    }
    
    public List<CashFlowDataDto> retrieveCashflowSummaryDetails(CashFlowSummaryFormBean formBean, CashFlowDto cashFlowDto,
            List<MonthlyCashFlowDto> monthlyCashFlow, LoanScheduleDto loanScheduleDto, int productId, 
            LoanAccountFormBean loanAccountFormBean) {

        List<CashFlowDataDto> cashFlowDataDtos = this.loanAccountServiceFacade.retrieveCashFlowSummary(monthlyCashFlow, loanScheduleDto);
        formBean.setCashFlowDataDtos(cashFlowDataDtos);
        formBean.setProductId(productId);

        BigDecimal loanAmount = BigDecimal.valueOf(loanScheduleDto.getLoanAmount());
        BigDecimal totalInstallmentAmount = BigDecimal.ZERO;
        DateTime firstInstallmentDueDate = new DateTime();
        DateTime lastInstallmentDueDate = new DateTime();
        List<DateTime> installments = new ArrayList<DateTime>();
        List<Number> installmentAmounts = new ArrayList<Number>();
        if (!loanScheduleDto.getInstallments().isEmpty()) {
            firstInstallmentDueDate = loanScheduleDto.firstInstallment();
            lastInstallmentDueDate = loanScheduleDto.lastInstallment();

            for (LoanCreationInstallmentDto installment : loanScheduleDto.getInstallments()) {
                totalInstallmentAmount = totalInstallmentAmount.add(BigDecimal.valueOf(installment.getTotal()));
                installments.add(new DateTime(installment.getDueDate()));
                installmentAmounts.add(installment.getTotal());
            }
        }
        
        formBean.setInstallments(installments);
        formBean.setInstallmentAmounts(installmentAmounts);
        formBean.setLoanPrincipal(loanAmount);
        
        LoanInstallmentsDto loanInstallmentsDto = new LoanInstallmentsDto(loanAmount, totalInstallmentAmount, firstInstallmentDueDate.toDate(), lastInstallmentDueDate.toDate());

        BigDecimal cashFlowTotalBalance = BigDecimal.ZERO;
        for (MonthlyCashFlowDto monthlyCashFlowDto : monthlyCashFlow) {
            cashFlowTotalBalance = cashFlowTotalBalance.add(monthlyCashFlowDto.calculateRevenueMinusExpenses());
        }

        formBean.setMonthlyCashFlows(monthlyCashFlow);
        formBean.setLoanInstallmentsDto(loanInstallmentsDto);
        formBean.setCashFlowTotalBalance(cashFlowTotalBalance);
        formBean.setRepaymentCapacity(cashFlowDto.getRepaymentCapacity());
        
        LocalDate disbursementDate = LoanCreationHelper.translateDisbursementDateToLocalDate(loanAccountFormBean);
        populateFormBeanFromDto(loanAccountFormBean.getCustomerId(), productId, loanAccountFormBean, formBean, disbursementDate, loanScheduleDto, true);

        return cashFlowDataDtos;
    }

    public LoanCreationResultDto saveLoanApplicationForLater(LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean) {

        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();

        return submitLoanApplication(applicationState.getPartialApplicationId(), formBean, loanAccountQuestionGroupFormBean, loanAccountCashFlow, cashFlowSummaryFormBean, loanScheduleFormBean);
    }

    public LoanCreationResultDto submitLoanApplication(LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean,
            MessageContext messageContext) {

        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();

        try {
            return submitLoanApplication(applicationState.getConfiguredApplicationId(), formBean, loanAccountQuestionGroupFormBean, loanAccountCashFlow, cashFlowSummaryFormBean, loanScheduleFormBean);
        }
        catch (BusinessRuleException e) {
            MessageBuilder builder = new MessageBuilder()
                    .error()
                    .codes(Arrays.asList(e.getMessageKey()).toArray(
                            new String[1])).defaultText(e.getMessage())
                    .args(e.getMessageValues());

            messageContext.addMessage(builder.build());
            throw e;
        }
    }
    
    public LoanCreationResultDto openLoanWithBackdatedPayments(LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean, MessageContext messageContext) {

        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();

            try {
            	return submitLoanWithBackdatedPaymentsApplication(applicationState.getPartialApplicationId(), formBean, loanAccountQuestionGroupFormBean, loanAccountCashFlow, cashFlowSummaryFormBean, loanScheduleFormBean);
            }
            catch (BusinessRuleException e) {
                MessageBuilder builder = new MessageBuilder()
                        .error()
                        .codes(Arrays.asList(e.getMessageKey()).toArray(
                                new String[1])).defaultText(e.getMessage())
                        .args(e.getMessageValues());

                messageContext.addMessage(builder.build());
                throw e;
            }
    }
    
    private LoanCreationResultDto submitLoanWithBackdatedPaymentsApplication(Integer accountState, LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean) {

        LocalDate disbursementDate = LoanCreationHelper.translateDisbursementDateToLocalDate(formBean);
        RecurringSchedule recurringSchedule = LoanCreationHelper.determineRecurringSchedule(formBean);
        List<CreateAccountFeeDto> accountFees = LoanCreationHelper.translateToAccountFeeDtos(formBean);
        List<CreateAccountPenaltyDto> accountPenalties = LoanCreationHelper.translateToAccountPenaltyDtos(formBean);
        List<CreateAccountFeeDto> additionalAccountFees = LoanCreationHelper.translateToAdditionalAccountFeeDtos(formBean);
        accountFees.addAll(additionalAccountFees);
        
        BigDecimal loanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());
        BigDecimal minAllowedLoanAmount = BigDecimal.valueOf(formBean.getMinAllowedAmount().doubleValue());
        BigDecimal maxAllowedLoanAmount = BigDecimal.valueOf(formBean.getMaxAllowedAmount().doubleValue());

        CreateLoanAccount loanAccountDetails = new CreateLoanAccount(formBean.getCustomerId(),
                formBean.getProductId(), accountState, loanAmount, minAllowedLoanAmount, maxAllowedLoanAmount,
                formBean.getInterestRate().doubleValue(), disbursementDate, formBean.getDisbursalPaymentTypeId(), formBean.getNumberOfInstallments().intValue(),
                formBean.getMinNumberOfInstallments().intValue(), formBean.getMaxNumberOfInstallments().intValue(),
                formBean.getGraceDuration().intValue(), formBean.getFundId(),
                formBean.getLoanPurposeId(), formBean.getCollateralTypeId(), formBean.getCollateralNotes(),
                formBean.getExternalId(), formBean.isRepaymentScheduleIndependentOfCustomerMeeting(), recurringSchedule, accountFees, accountPenalties);
        
        List<LoanPaymentDto> backdatedLoanPayments = new ArrayList<LoanPaymentDto>();
        int index = 0;
        List<Number> actualPaymentAmountDetails = cashFlowSummaryFormBean.getActualPaymentAmounts();
        if (actualPaymentAmountDetails.isEmpty()) {
            actualPaymentAmountDetails = loanScheduleFormBean.getActualPaymentAmounts();
        }
        for (Number actualPaymentAmount : actualPaymentAmountDetails) {
            if (actualPaymentAmount.doubleValue() > 0) {
                LocalDate transactionDate = new LocalDate(loanScheduleFormBean.getActualPaymentDates().get(index));
                backdatedLoanPayments.add(new LoanPaymentDto(actualPaymentAmount.toString(), transactionDate,
                        loanScheduleFormBean.getActualPaymentTypes().get(index), null));
            }
            index++;
        }
        
        LoanCreationResultDto loanCreationResultDto = null;
        if (formBean.isGlimApplicable()) {

            List<GroupMemberAccountDto> memberAccounts = createGroupMemberAccounts(formBean);
            BigDecimal totalLoanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());

            CreateGlimLoanAccount createGroupLoanAccount = new CreateGlimLoanAccount(memberAccounts, totalLoanAmount, loanAccountDetails);

            loanCreationResultDto = loanAccountServiceFacade.createBackdatedGroupLoanWithIndividualMonitoring(createGroupLoanAccount, backdatedLoanPayments, 
                    loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow);
            
        } else if (formBean.isVariableInstallmentsAllowed()) {
            List<DateTime> installmentDates = cashFlowSummaryFormBean.getInstallments();
            List<Number> installmentPrincipalAmounts = cashFlowSummaryFormBean.getInstallmentAmounts();
            if (installmentDates.isEmpty()) {
                installmentDates = loanScheduleFormBean.getInstallments();
                installmentPrincipalAmounts = loanScheduleFormBean.getInstallmentAmounts();
            }
            // api for creating loan with premade loan schedule
            loanCreationResultDto = loanAccountServiceFacade.createBackdatedLoan(loanAccountDetails, backdatedLoanPayments, loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow, installmentDates, installmentPrincipalAmounts);
        } else {
            loanCreationResultDto = loanAccountServiceFacade.createBackdatedLoan(loanAccountDetails, backdatedLoanPayments, loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow);
        }

        return loanCreationResultDto;
    }
    
    private LoanCreationResultDto submitLoanApplication(Integer accountState, LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean) {

        LocalDate disbursementDate =  LoanCreationHelper.translateDisbursementDateToLocalDate(formBean);
        RecurringSchedule recurringSchedule =  LoanCreationHelper.determineRecurringSchedule(formBean);
        List<CreateAccountFeeDto> accountFees =  LoanCreationHelper.translateToAccountFeeDtos(formBean);
        List<CreateAccountFeeDto> additionalAccountFees =  LoanCreationHelper.translateToAdditionalAccountFeeDtos(formBean);
        List<CreateAccountPenaltyDto> accountPenalties = LoanCreationHelper.translateToAccountPenaltyDtos(formBean);
        accountFees.addAll(additionalAccountFees);
        
        BigDecimal loanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());
        BigDecimal minAllowedLoanAmount = BigDecimal.valueOf(formBean.getMinAllowedAmount().doubleValue());
        BigDecimal maxAllowedLoanAmount = BigDecimal.valueOf(formBean.getMaxAllowedAmount().doubleValue());

        CreateLoanAccount loanAccountDetails = new CreateLoanAccount(formBean.getCustomerId(),
                formBean.getProductId(), accountState, loanAmount, minAllowedLoanAmount, maxAllowedLoanAmount,
                formBean.getInterestRate().doubleValue(), disbursementDate, null, formBean.getNumberOfInstallments().intValue(),
                formBean.getMinNumberOfInstallments().intValue(), formBean.getMaxNumberOfInstallments().intValue(),
                formBean.getGraceDuration().intValue(), formBean.getFundId(),
                formBean.getLoanPurposeId(), formBean.getCollateralTypeId(), formBean.getCollateralNotes(),
                formBean.getExternalId(), formBean.isRepaymentScheduleIndependentOfCustomerMeeting(), recurringSchedule, accountFees, accountPenalties);

        LoanCreationResultDto loanCreationResultDto = null;

        if (formBean.isGlimApplicable()) {

            List<GroupMemberAccountDto> memberAccounts = createGroupMemberAccounts(formBean);
            BigDecimal totalLoanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());

            CreateGlimLoanAccount createGroupLoanAccount = new CreateGlimLoanAccount(memberAccounts, totalLoanAmount, loanAccountDetails);

            loanCreationResultDto = loanAccountServiceFacade.createGroupLoanWithIndividualMonitoring(createGroupLoanAccount, loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow);
        } else {

            if (formBean.isVariableInstallmentsAllowed()) {
                List<DateTime> installmentDates = cashFlowSummaryFormBean.getInstallments();
                List<Number> totalInstallmentAmounts = cashFlowSummaryFormBean.getInstallmentAmounts();
                if (installmentDates.isEmpty()) {
                    installmentDates = loanScheduleFormBean.getInstallments();
                    totalInstallmentAmounts = loanScheduleFormBean.getInstallmentAmounts();
                }
                // api for creating loan with premade loan schedule
                
                loanCreationResultDto = loanAccountServiceFacade.createLoan(loanAccountDetails,
                        loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow, installmentDates, totalInstallmentAmounts);
            } else {
                loanCreationResultDto = loanAccountServiceFacade.createLoan(loanAccountDetails,
                        loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow);
            }
        }
        return loanCreationResultDto;
    }

    @SuppressWarnings("PMD")
    private List<GroupMemberAccountDto> createGroupMemberAccounts(LoanAccountFormBean formBean) {

        List<GroupMemberAccountDto> memberAccounts = new ArrayList<GroupMemberAccountDto>();

        int index = 0;
        for (Boolean clientSelected : formBean.getClientSelectForGroup()) {
            if (clientSelected != null && clientSelected.booleanValue()) {
                String globalId = formBean.getClientGlobalId()[index];
                BigDecimal loanAmount = BigDecimal.valueOf(formBean.getClientAmount()[index].doubleValue());
                Integer loanPurposeId = formBean.getClientLoanPurposeId()[index];
                GroupMemberAccountDto memberAccount = new GroupMemberAccountDto(globalId, loanAmount, loanPurposeId);

                memberAccounts.add(memberAccount);
            }
            index++;
        }

        return memberAccounts;
    }
}