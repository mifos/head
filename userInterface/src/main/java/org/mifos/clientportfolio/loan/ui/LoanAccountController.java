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
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.admin.servicefacade.AdminServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.loan.service.CreateLoanSchedule;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.LoanApplicationStateDto;
import org.mifos.dto.domain.CustomerSearchDto;
import org.mifos.dto.domain.CustomerSearchResultDto;
import org.mifos.dto.domain.MandatoryHiddenFieldsDto;
import org.mifos.dto.screen.CustomerSearchResultsDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.dto.screen.LoanCreationProductDetailsDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanScheduleDto;
import org.mifos.dto.screen.SearchDetailsDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
        
    	CustomerSearchDto customerSearchDto = new CustomerSearchDto(formBean.getSearchString(), formBean.getPage(), formBean.getPageSize());
    	List<CustomerSearchResultDto> pagedDetails = this.loanAccountServiceFacade.retrieveCustomersThatQualifyForLoans(customerSearchDto);
    	
    	int firstResult = formBean.getPage() * formBean.getPageSize() - (formBean.getPageSize()-1);
		SearchDetailsDto searchDetails = new SearchDetailsDto(pagedDetails.size(), firstResult, formBean.getPage(), formBean.getPageSize());
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
    	formBean.setAmount(dto.getDefaultLoanAmount());
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
    	
    	String[] selectedFeeId = new String[3];
    	selectedFeeId[0] = "";
    	selectedFeeId[1] = "";
    	selectedFeeId[2] = "";
    	
		formBean.setSelectedFeeId(selectedFeeId);
		
		String[] selectedFeeAmount = new String[3];
		selectedFeeAmount[0] = "";
		selectedFeeAmount[1] = "";
		selectedFeeAmount[2] = "";
		formBean.setSelectedFeeAmount(selectedFeeAmount);
    	
    	return dto;
    }
    
    public void loadQuestionGroups(Integer productId, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean) {
        
        List<QuestionGroupDetail> questionGroups = loanAccountServiceFacade.retrieveApplicableQuestionGroups(productId);
        loanAccountQuestionGroupFormBean.setQuestionGroups(questionGroups);
    }
    
    public LoanScheduleDto retrieveLoanSchedule(int customerId, int productId, LoanAccountFormBean formBean) {
        
        LocalDate disbursementDate = new LocalDate().withDayOfMonth(formBean.getDisbursalDateDay().intValue())
                                                    .withMonthOfYear(formBean.getDisbursalDateMonth().intValue())
                                                    .withYearOfEra(formBean.getDisbursalDateYear().intValue());
        
        CreateLoanSchedule createLoanAccount = new CreateLoanSchedule(customerId, productId, BigDecimal.valueOf(formBean.getAmount().doubleValue()), formBean.getInterestRate().doubleValue(), disbursementDate, formBean.getNumberOfInstallments().intValue(), formBean.getGraceDuration().intValue());
        
        return loanAccountServiceFacade.createLoanSchedule(createLoanAccount);
    }
    
    public LoanCreationResultDto saveLoanApplicationForLater(LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean) {
        
        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();
        
        return submitLoanApplication(applicationState.getPartialApplicationId(), formBean, loanAccountQuestionGroupFormBean);
    }

    public LoanCreationResultDto submitLoanApplication(LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean) {
        
        LoanApplicationStateDto applicationState = loanAccountServiceFacade.retrieveLoanApplicationState();
        
        return submitLoanApplication(applicationState.getConfiguredApplicationId(), formBean, loanAccountQuestionGroupFormBean);
    }

    private LoanCreationResultDto submitLoanApplication(Integer accountState, LoanAccountFormBean formBean, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean) {

        LocalDate disbursementDate = new LocalDate().withDayOfMonth(formBean.getDisbursalDateDay().intValue())
                                                    .withMonthOfYear(formBean.getDisbursalDateMonth().intValue())
                                                     .withYearOfEra(formBean.getDisbursalDateYear().intValue());
        
        CreateLoanAccount createLoanAccount = new CreateLoanAccount(formBean.getCustomerId(), formBean.getProductId(), accountState, 
                                                                                    BigDecimal.valueOf(formBean.getAmount().doubleValue()), 
                                                                                    formBean.getInterestRate().doubleValue(), 
                                                                                    disbursementDate, 
                                                                                    formBean.getNumberOfInstallments().intValue(),
                                                                                    formBean.getGraceDuration().intValue(),
                                                                                    formBean.getFundId(),
                                                                                    formBean.getLoanPurposeId(),
                                                                                    formBean.getCollateralTypeId(),
                                                                                    formBean.getCollateralNotes(),
                                                                                    formBean.getExternalId());

        return loanAccountServiceFacade.createLoan(createLoanAccount, loanAccountQuestionGroupFormBean.getQuestionGroups());
    }
}