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
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.loan.service.CreateLoanSchedule;
import org.mifos.clientportfolio.loan.service.MonthlyOnDayOfMonthSchedule;
import org.mifos.clientportfolio.loan.service.MonthlyOnWeekOfMonthSchedule;
import org.mifos.clientportfolio.loan.service.RecurringSchedule;
import org.mifos.clientportfolio.loan.service.WeeklySchedule;
import org.mifos.clientportfolio.newloan.applicationservice.CreateGlimLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.GroupMemberAccountDto;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.clientportfolio.newloan.applicationservice.LoanApplicationStateDto;
import org.mifos.dto.domain.CashFlowDto;
import org.mifos.dto.domain.CreateAccountFeeDto;
import org.mifos.dto.domain.CustomerSearchDto;
import org.mifos.dto.domain.CustomerSearchResultDto;
import org.mifos.dto.domain.FeeDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.dto.screen.CashFlowDataDto;
import org.mifos.dto.screen.CustomerSearchResultsDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.dto.screen.LoanCreationProductDetailsDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanInstallmentsDto;
import org.mifos.dto.screen.LoanScheduleDto;
import org.mifos.dto.screen.SearchDetailsDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.springframework.beans.factory.annotation.Autowired;
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

	public CustomerSearchResultsDto searchCustomers(CustomerSearchFormBean formBean) {

        // Search result cap. This is needed until ajax search is implemented.
        Integer searchCap = 1000;

    	CustomerSearchDto customerSearchDto = new CustomerSearchDto(formBean.getSearchString(), Integer.valueOf(1), searchCap);
    	List<CustomerSearchResultDto> pagedDetails = this.loanAccountServiceFacade.retrieveCustomersThatQualifyForLoans(customerSearchDto);

    	int firstResult = formBean.getPage() * formBean.getPageSize() - (formBean.getPageSize()-1);

    	// FIXME selected pageNumber and pageSize info should be passed in on bean.
		SearchDetailsDto searchDetails = new SearchDetailsDto(pagedDetails.size(),  1, 1, searchCap);
        return new CustomerSearchResultsDto(searchDetails, pagedDetails);
    }

    public LoanCreationProductDetailsDto retrieveLoanProducts(int customerId) {
    	return this.loanAccountServiceFacade.retrieveGetProductDetailsForLoanAccountCreation(customerId);
    }

    @SuppressWarnings("PMD")
    public LoanCreationLoanDetailsDto retrieveLoanCreationDetails(int customerId, int productId, LoanAccountFormBean formBean) {

        MandatoryHiddenFieldsDto mandatoryHidden = this.adminServiceFacade.retrieveHiddenMandatoryFields();
    	LoanCreationLoanDetailsDto dto = this.loanAccountServiceFacade.retrieveLoanDetailsForLoanAccountCreation(customerId, Integer.valueOf(productId).shortValue());

    	formBean.setProductId(productId);
    	formBean.setCustomerId(dto.getCustomerDetailDto().getCustomerId());
    	formBean.setRepaymentScheduleIndependentOfCustomerMeeting(dto.isRepaymentIndependentOfMeetingEnabled());

    	if (dto.isRepaymentIndependentOfMeetingEnabled()) {

    	    Integer recursEvery = dto.getCustomerMeetingDetail().getMeetingDetailsDto().getEvery();
    	    Integer dayOfMonth = dto.getCustomerMeetingDetail().getMeetingDetailsDto().getRecurrenceDetails().getDayNumber();
    	    Integer weekOfMonth = dto.getCustomerMeetingDetail().getMeetingDetailsDto().getRecurrenceDetails().getWeekOfMonth();
    	    Integer dayOfWeek = dto.getCustomerMeetingDetail().getMeetingDetailsDto().getRecurrenceDetails().getDayOfWeek();
    	    if (dayOfMonth != null && dayOfMonth > 0) {
    	        formBean.setDayOfMonthDetails(dayOfMonth, recursEvery);
    	    } else if (weekOfMonth != null){
    	        formBean.setWeekOfMonthDetails(weekOfMonth, dayOfWeek, recursEvery);
    	    } else if (dayOfWeek != null) {
    	        formBean.setWeeklyDetails(dayOfWeek, recursEvery);
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
    	    formBean.setClientLoanPurposeId(new Integer[clientData.size()]);

    	} else {
    	    formBean.setAmount(dto.getDefaultLoanAmount());
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

    	LocalDate possibleDisbursementDate = dto.getNextPossibleDisbursementDate();
    	formBean.setDisbursalDateDay(possibleDisbursementDate.getDayOfMonth());
    	formBean.setDisbursalDateMonth(possibleDisbursementDate.getMonthOfYear());
    	formBean.setDisbursalDateYear(possibleDisbursementDate.getYearOfEra());

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

    	Number[] selectedFeeId = new Number[3];
		formBean.setSelectedFeeId(selectedFeeId);

		Number[] selectedFeeAmount = new Number[3];
		formBean.setSelectedFeeAmount(selectedFeeAmount);

    	return dto;
    }

    public void loadQuestionGroups(Integer productId, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean) {

        List<QuestionGroupDetail> questionGroups = loanAccountServiceFacade.retrieveApplicableQuestionGroups(productId);
        loanAccountQuestionGroupFormBean.setQuestionGroups(questionGroups);
    }

    public boolean isCompareForCashFlowEnabled(Integer productId) {
        return this.loanAccountServiceFacade.isCompareWithCashFlowEnabledOnProduct(productId);
    }

    public LoanScheduleDto retrieveLoanSchedule(int customerId, int productId, LoanAccountFormBean formBean, LoanScheduleFormBean loanScheduleFormBean) {

        LocalDate disbursementDate = translateDisbursementDateToLocalDate(formBean);

        RecurringSchedule recurringSchedule = determineRecurringSchedule(formBean);
        List<CreateAccountFeeDto> accountFees = translateToAccountFeeDtos(formBean);
        List<CreateAccountFeeDto> additionalAccountFees = translateToAdditionalAccountFeeDtos(formBean);
        accountFees.addAll(additionalAccountFees);

        CreateLoanSchedule createLoanAccount = new CreateLoanSchedule(customerId, productId, BigDecimal.valueOf(formBean.getAmount().doubleValue()), formBean.getInterestRate().doubleValue(), disbursementDate,
                formBean.getNumberOfInstallments().intValue(), formBean.getGraceDuration().intValue(), formBean.isRepaymentScheduleIndependentOfCustomerMeeting(), recurringSchedule, accountFees);

        LoanScheduleDto loanSchedule = loanAccountServiceFacade.createLoanSchedule(createLoanAccount);

        List<Date> installments = new ArrayList<Date>();
        for (LoanCreationInstallmentDto installment :loanSchedule.getInstallments()) {
            installments.add(installment.getDueDate());
        }
        loanScheduleFormBean.setInstallments(installments);

        // variable installments related
        loanScheduleFormBean.setVariableInstallmentsAllowed(formBean.isVariableInstallmentsAllowed());
        if (loanScheduleFormBean.isVariableInstallmentsAllowed()) {
            loanScheduleFormBean.setMinGapInDays(formBean.getMinGapInDays());
            loanScheduleFormBean.setMaxGapInDays(formBean.getMaxGapInDays());
            loanScheduleFormBean.setMinInstallmentAmount(formBean.getMinInstallmentAmount());

            loanScheduleFormBean.setDisbursementDate(disbursementDate.toDateMidnight().toDate());
            loanScheduleFormBean.setCustomerId(formBean.getCustomerId());
            loanScheduleFormBean.setVariableInstallments(loanSchedule.getInstallments());
        }

        return loanSchedule;
    }

    private LocalDate translateDisbursementDateToLocalDate(LoanAccountFormBean formBean) {
        return new DateTime().withDate(formBean.getDisbursalDateYear().intValue(), formBean.getDisbursalDateMonth().intValue(), formBean.getDisbursalDateDay().intValue()).toLocalDate();
    }

    private RecurringSchedule determineRecurringSchedule(LoanAccountFormBean formBean) {
        RecurringSchedule recurringSchedule = null;
        if (formBean.isMonthly()) {
            if (formBean.isMonthlyDayOfMonthOptionSelected()) {
                recurringSchedule = new MonthlyOnDayOfMonthSchedule(formBean.getRepaymentRecursEvery(), formBean.getRepaymentDayOfMonth());
            } else if (formBean.isMonthlyWeekOfMonthOptionSelected()) {
                recurringSchedule = new MonthlyOnWeekOfMonthSchedule(formBean.getRepaymentRecursEvery(), formBean.getRepaymentWeekOfMonth(), formBean.getRepaymentDayOfWeek());
            }
        } else if (formBean.isWeekly()) {
            recurringSchedule = new WeeklySchedule(formBean.getRepaymentRecursEvery(), formBean.getRepaymentDayOfWeek());
        }
        return recurringSchedule;
    }

    public CashFlowDto retrieveCashFlowSettings(LoanScheduleDto loanScheduleDto, int productId) {
        DateTime firstInstallment = loanScheduleDto.firstInstallment();
        DateTime lastInstallment = loanScheduleDto.lastInstallment();
        return loanAccountServiceFacade.retrieveCashFlowSettings(firstInstallment, lastInstallment, productId, BigDecimal.valueOf(loanScheduleDto.getLoanAmount()));
    }

    public List<CashFlowDataDto> retrieveCashflowSummaryDetails(CashFlowSummaryFormBean formBean, CashFlowDto cashFlowDto,
            List<MonthlyCashFlowDto> monthlyCashFlow, LoanScheduleDto loanScheduleDto, int productId, LoanAccountFormBean loanAccountFormBean) {

        List<CashFlowDataDto> cashFlowDataDtos = this.loanAccountServiceFacade.retrieveCashFlowSummary(monthlyCashFlow, loanScheduleDto);
        formBean.setCashFlowDataDtos(cashFlowDataDtos);
        formBean.setProductId(productId);

        BigDecimal loanAmount = BigDecimal.valueOf(loanScheduleDto.getLoanAmount());
        BigDecimal totalInstallmentAmount = BigDecimal.ZERO;
        Date firstInstallmentDueDate = new Date();
        Date lastInstallmentDueDate = new Date();
        List<Date> installments = new ArrayList<Date>();
        if (!loanScheduleDto.getInstallments().isEmpty()) {
            firstInstallmentDueDate = loanScheduleDto.firstInstallment().toDate();
            lastInstallmentDueDate = loanScheduleDto.lastInstallment().toDate();

            for (LoanCreationInstallmentDto installment : loanScheduleDto.getInstallments()) {
                totalInstallmentAmount = totalInstallmentAmount.add(BigDecimal.valueOf(installment.getTotal()));
                installments.add(installment.getDueDate());
            }
        }
        formBean.setInstallments(installments);
        LoanInstallmentsDto loanInstallmentsDto = new LoanInstallmentsDto(loanAmount, totalInstallmentAmount, firstInstallmentDueDate, lastInstallmentDueDate);

        BigDecimal cashFlowTotalBalance = BigDecimal.ZERO;
        for (MonthlyCashFlowDto monthlyCashFlowDto : monthlyCashFlow) {
            cashFlowTotalBalance = cashFlowTotalBalance.add(monthlyCashFlowDto.calculateRevenueMinusExpenses());
        }

        formBean.setMonthlyCashFlows(monthlyCashFlow);
        formBean.setLoanInstallmentsDto(loanInstallmentsDto);
        formBean.setCashFlowTotalBalance(cashFlowTotalBalance);
        formBean.setRepaymentCapacity(cashFlowDto.getRepaymentCapacity());

        // variable installments related
        formBean.setVariableInstallmentsAllowed(loanAccountFormBean.isVariableInstallmentsAllowed());
        if (loanAccountFormBean.isVariableInstallmentsAllowed()) {
            formBean.setMinGapInDays(loanAccountFormBean.getMinGapInDays());
            formBean.setMaxGapInDays(loanAccountFormBean.getMaxGapInDays());
            formBean.setMinInstallmentAmount(loanAccountFormBean.getMinInstallmentAmount());

            LocalDate disbursementDate = translateDisbursementDateToLocalDate(loanAccountFormBean);

            formBean.setDisbursementDate(disbursementDate.toDateMidnight().toDate());
            formBean.setCustomerId(loanAccountFormBean.getCustomerId());
            formBean.setVariableInstallments(loanScheduleDto.getInstallments());
        }

        return cashFlowDataDtos;
    }

    public LoanCreationResultDto saveLoanApplicationForLater(LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean) {

        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();

        return submitLoanApplication(applicationState.getPartialApplicationId(), formBean, loanAccountQuestionGroupFormBean, loanAccountCashFlow, cashFlowSummaryFormBean, loanScheduleFormBean);
    }

    public LoanCreationResultDto submitLoanApplication(LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean) {

        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();

        return submitLoanApplication(applicationState.getConfiguredApplicationId(), formBean, loanAccountQuestionGroupFormBean, loanAccountCashFlow, cashFlowSummaryFormBean, loanScheduleFormBean);
    }

    private LoanCreationResultDto submitLoanApplication(Integer accountState, LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean,
            LoanAccountCashFlow loanAccountCashFlow, CashFlowSummaryFormBean cashFlowSummaryFormBean, LoanScheduleFormBean loanScheduleFormBean) {

        LocalDate disbursementDate = translateDisbursementDateToLocalDate(formBean);
        RecurringSchedule recurringSchedule = determineRecurringSchedule(formBean);
        List<CreateAccountFeeDto> accountFees = translateToAccountFeeDtos(formBean);
        List<CreateAccountFeeDto> additionalAccountFees = translateToAdditionalAccountFeeDtos(formBean);
        accountFees.addAll(additionalAccountFees);

        CreateLoanAccount loanAccountDetails = new CreateLoanAccount(formBean.getCustomerId(),
                formBean.getProductId(), accountState, BigDecimal.valueOf(formBean.getAmount().doubleValue()),
                formBean.getInterestRate().doubleValue(), disbursementDate, formBean.getNumberOfInstallments()
                        .intValue(), formBean.getGraceDuration().intValue(), formBean.getFundId(),
                formBean.getLoanPurposeId(), formBean.getCollateralTypeId(), formBean.getCollateralNotes(),
                formBean.getExternalId(), formBean.isRepaymentScheduleIndependentOfCustomerMeeting(), recurringSchedule, accountFees);

        LoanCreationResultDto loanCreationResultDto = null;

        if (formBean.isGlimApplicable()) {

            List<GroupMemberAccountDto> memberAccounts = createGroupMemberAccounts(formBean);
            BigDecimal totalLoanAmount = BigDecimal.valueOf(formBean.getAmount().doubleValue());

            CreateGlimLoanAccount createGroupLoanAccount = new CreateGlimLoanAccount(memberAccounts, totalLoanAmount, loanAccountDetails);

            loanCreationResultDto = loanAccountServiceFacade.createGroupLoanWithIndividualMonitoring(createGroupLoanAccount, loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow);
        } else {

            if (formBean.isVariableInstallmentsAllowed()) {
                List<Date> installmentDates = cashFlowSummaryFormBean.getInstallments();
                if (installmentDates.isEmpty()) {
                    installmentDates = loanScheduleFormBean.getInstallments();
                }
                loanCreationResultDto = loanAccountServiceFacade.createLoan(loanAccountDetails,
                        loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow, installmentDates);
            } else {
                loanCreationResultDto = loanAccountServiceFacade.createLoan(loanAccountDetails,
                        loanAccountQuestionGroupFormBean.getQuestionGroups(), loanAccountCashFlow);
            }
        }
        return loanCreationResultDto;
    }

    private List<CreateAccountFeeDto> translateToAdditionalAccountFeeDtos(LoanAccountFormBean formBean) {
        List<CreateAccountFeeDto> accountFees = new ArrayList<CreateAccountFeeDto>();

        int index = 0;
        for (Number feeId : formBean.getSelectedFeeId()) {
            if (feeId != null) {
                Number feeAmountOrRate = formBean.getSelectedFeeAmount()[index];
                CreateAccountFeeDto accountFee = new CreateAccountFeeDto(feeId.intValue(), feeAmountOrRate.toString());
                accountFees.add(accountFee);
            }
            index++;
        }

        return accountFees;
    }

    private List<CreateAccountFeeDto> translateToAccountFeeDtos(LoanAccountFormBean formBean) {
        List<CreateAccountFeeDto> accountFees = new ArrayList<CreateAccountFeeDto>();
        Number[] defaultFeeIds = formBean.getDefaultFeeId();
        if (defaultFeeIds != null) {
            int feeIndex = 0;
            for (Number feeId : defaultFeeIds) {
                Boolean removeDefaultFeeSelected = formBean.getDefaultFeeSelected()[feeIndex];
                if (removeDefaultFeeSelected == null || !removeDefaultFeeSelected) {
                    String amount = formBean.getDefaultFeeAmountOrRate()[feeIndex].toString();
                    CreateAccountFeeDto accountFee = new CreateAccountFeeDto(feeId.intValue(), amount);
                    accountFees.add(accountFee);
                }
                feeIndex++;
            }
        }
        return accountFees;
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