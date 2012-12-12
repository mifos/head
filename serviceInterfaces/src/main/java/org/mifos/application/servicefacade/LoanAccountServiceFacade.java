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

package org.mifos.application.servicefacade;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.clientportfolio.loan.service.CreateLoanSchedule;
import org.mifos.clientportfolio.newloan.applicationservice.CreateGlimLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.CreateLoanAccount;
import org.mifos.clientportfolio.newloan.applicationservice.LoanAccountCashFlow;
import org.mifos.clientportfolio.newloan.applicationservice.LoanApplicationStateDto;
import org.mifos.clientportfolio.newloan.applicationservice.LoanDisbursementDateValidationServiceFacade;
import org.mifos.clientportfolio.newloan.applicationservice.VariableInstallmentsFeeValidationServiceFacade;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountStatusDto;
import org.mifos.dto.domain.AccountUpdateStatus;
import org.mifos.dto.domain.ApplicableCharge;
import org.mifos.dto.domain.CashFlowDto;
import org.mifos.dto.domain.CreateAccountNote;
import org.mifos.dto.domain.CreateLoanRequest;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.CustomerSearchDto;
import org.mifos.dto.domain.CustomerSearchResultDto;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.LoanCreationInstallmentDto;
import org.mifos.dto.domain.LoanInstallmentDetailsDto;
import org.mifos.dto.domain.LoanPaymentDto;
import org.mifos.dto.domain.LoanRepaymentScheduleItemDto;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.dto.domain.OriginalScheduleInfoDto;
import org.mifos.dto.domain.OverpaymentDto;
import org.mifos.dto.screen.AccountPaymentDto;
import org.mifos.dto.screen.CashFlowDataDto;
import org.mifos.dto.screen.ChangeAccountStatusDto;
import org.mifos.dto.screen.ExpectedPaymentDto;
import org.mifos.dto.screen.LoanAccountDetailDto;
import org.mifos.dto.screen.LoanCreationLoanDetailsDto;
import org.mifos.dto.screen.LoanCreationPreviewDto;
import org.mifos.dto.screen.LoanCreationProductDetailsDto;
import org.mifos.dto.screen.LoanCreationResultDto;
import org.mifos.dto.screen.LoanDisbursalDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.dto.screen.LoanInstallmentsDto;
import org.mifos.dto.screen.LoanScheduleDto;
import org.mifos.dto.screen.MultipleLoanAccountDetailsDto;
import org.mifos.dto.screen.RepayLoanDto;
import org.mifos.dto.screen.RepayLoanInfoDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.validations.Errors;
import org.springframework.security.access.prepost.PreAuthorize;

public interface LoanAccountServiceFacade extends LoanDisbursementDateValidationServiceFacade, VariableInstallmentsFeeValidationServiceFacade {
    
    @PreAuthorize("isFullyAuthenticated()")
    AccountStatusDto retrieveAccountStatuses(Long loanAccountId);

    @PreAuthorize("isFullyAuthenticated() and hasPermission(#updateStatus, 'MAX_LOAN_AMOUNT_FOR_APPROVE')")
    String updateLoanAccountStatus(AccountUpdateStatus updateStatus, Date transactionDate);

    @PreAuthorize("isFullyAuthenticated()")
    LoanAccountDetailDto retrieveLoanAccountNotes(Long loanAccountId);

    @PreAuthorize("isFullyAuthenticated()")
    void addNote(CreateAccountNote accountNote);
    
    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_REDO_CAN_CREATE_BACKDATED_LOANS', 'ROLE_CAN_CREATE_NEW_LOAN_IN_SAVE_FOR_LATER_STATE', 'ROLE_CAN_CREATE_NEW_LOAN_IN_SUBMIT_FOR_APPROVAL_STATE')")
    List<CustomerSearchResultDto> retrieveCustomersThatQualifyForLoans(CustomerSearchDto customerSearchDto, boolean isNewGLIMCreation);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_REDO_CAN_CREATE_BACKDATED_LOANS', 'ROLE_CAN_CREATE_NEW_LOAN_IN_SAVE_FOR_LATER_STATE', 'ROLE_CAN_CREATE_NEW_LOAN_IN_SUBMIT_FOR_APPROVAL_STATE')")
    LoanCreationProductDetailsDto retrieveGetProductDetailsForLoanAccountCreation(Integer customerId);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_REDO_CAN_CREATE_BACKDATED_LOANS', 'ROLE_CAN_CREATE_NEW_LOAN_IN_SAVE_FOR_LATER_STATE', 'ROLE_CAN_CREATE_NEW_LOAN_IN_SUBMIT_FOR_APPROVAL_STATE')")
    LoanCreationLoanDetailsDto retrieveLoanDetailsForLoanAccountCreation(Integer customerId, Short productId, boolean isLoanWithBackdatedPayments);

    @PreAuthorize("isFullyAuthenticated()")
    LoanCreationPreviewDto previewLoanCreationDetails(Integer customerId, List<LoanAccountDetailsDto> accountDetails, List<String> selectedClientIds);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_CREATE_NEW_LOAN_IN_SAVE_FOR_LATER_STATE', 'ROLE_CAN_CREATE_NEW_LOAN_IN_SUBMIT_FOR_APPROVAL_STATE')")
    LoanCreationResultDto createLoan(CreateLoanAccount createLoanAccount, List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow);
    
    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_CREATE_NEW_LOAN_IN_SAVE_FOR_LATER_STATE', 'ROLE_CAN_CREATE_NEW_LOAN_IN_SUBMIT_FOR_APPROVAL_STATE')")
    LoanCreationResultDto createLoan(CreateLoanAccount createLoanAccount, List<QuestionGroupDetail> questionGroups, 
            LoanAccountCashFlow loanAccountCashFlow, List<DateTime> installments, List<Number> totalInstallmentAmounts);
    
    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_CREATE_NEW_LOAN_IN_SAVE_FOR_LATER_STATE', 'ROLE_CAN_CREATE_NEW_LOAN_IN_SUBMIT_FOR_APPROVAL_STATE')")
    LoanCreationResultDto createGroupLoanWithIndividualMonitoring(CreateGlimLoanAccount createLoanAccount, List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow);

    /**
     * create a backdated loan and provide loan schedule dates and amounts.
     * Will automatically approve/disburse and make payments.
     */
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_REDO_CAN_CREATE_BACKDATED_LOANS')")
    LoanCreationResultDto createBackdatedLoan(CreateLoanAccount loanAccountDetails,
            List<LoanPaymentDto> backdatedLoanPayments, List<QuestionGroupDetail> questionGroups,
            LoanAccountCashFlow loanAccountCashFlow, List<DateTime> installmentDates,
            List<Number> totalInstallmentAmounts);
    
    /**
     * create a backdated loan (loan schedule dates and amounts authomatically generated).
     * Will automatically approve/disburse and make payments.
     */
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_REDO_CAN_CREATE_BACKDATED_LOANS')")
    LoanCreationResultDto createBackdatedLoan(CreateLoanAccount loanAccountDetails,
            List<LoanPaymentDto> backdatedLoanPayments, List<QuestionGroupDetail> questionGroups,
            LoanAccountCashFlow loanAccountCashFlow);
    
    /**
     * create a backdated group loan with individual monitoring and automatically approve/disburse and make payments
     */
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_REDO_CAN_CREATE_BACKDATED_LOANS')")
    LoanCreationResultDto createBackdatedGroupLoanWithIndividualMonitoring(
            CreateGlimLoanAccount glimLoanAccount, List<LoanPaymentDto> backdatedLoanPayments,
            List<QuestionGroupDetail> questionGroups, LoanAccountCashFlow loanAccountCashFlow);
    
    LoanScheduleDto createLoanSchedule(CreateLoanSchedule createLoanSchedule);
    
    LoanScheduleDto createLoanSchedule(CreateLoanSchedule createLoanSchedule, List<DateTime> installments, List<Number> installmentAmounts);

    @PreAuthorize("isFullyAuthenticated()")
    List<LoanActivityDto> retrieveAllLoanAccountActivities(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated()")
    LoanInstallmentDetailsDto retrieveInstallmentDetails(Integer accountId);

    @PreAuthorize("isFullyAuthenticated()")
    List<LoanRepaymentScheduleItemDto> retrieveLoanRepaymentSchedule(String globalAccountNum, Date viewDate);

    @PreAuthorize("isFullyAuthenticated()")
    OriginalScheduleInfoDto retrieveOriginalLoanSchedule(String globalAccountNum);
    
    @PreAuthorize("isFullyAuthenticated()")
    boolean isTrxnDateValid(Integer loanAccountId, Date trxnDate);

    @PreAuthorize("isFullyAuthenticated()")
    void makeEarlyRepayment(RepayLoanInfoDto repayLoanInfoDto);

    @PreAuthorize("isFullyAuthenticated()")
    void makeEarlyRepaymentFromSavings(RepayLoanInfoDto repayLoanInfoDto, String savingsAccGlobalNum);

    @PreAuthorize("isFullyAuthenticated()")
    LoanInformationDto retrieveLoanInformation(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated()")
    RepayLoanDto retrieveLoanRepaymentDetails(String globalAccountNum);
    
    @PreAuthorize("isFullyAuthenticated()")
	ExpectedPaymentDto retrieveExpectedPayment(String loanGlobalAccountNumber, LocalDate paymentDueAsOf);
    
    @PreAuthorize("isFullyAuthenticated()")
	void applyLoanRepayment(String loanGlobalAccountNumber,
			LocalDate paymentDate, BigDecimal repaymentAmount, String receiptId, LocalDate receiptDate, Short modeOfPayment);

    @PreAuthorize("isFullyAuthenticated()")
    List<LoanAccountDetailsDto> retrieveLoanAccountDetails(LoanInformationDto loanInformationDto);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_DISBURSE_LOAN')")
    LoanDisbursalDto retrieveLoanDisbursalDetails(Integer loanAccountId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_DISBURSE_LOAN')")
    void disburseLoan(AccountPaymentParametersDto loanDisbursement, Short paymentTypeId);

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_APPROVE_LOANS_IN_BULK', 'ROLE_CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS')")
    ChangeAccountStatusDto retrieveAllActiveBranchesAndLoanOfficerDetails();

    @PreAuthorize("isFullyAuthenticated() and hasAnyRole('ROLE_CAN_APPROVE_LOANS_IN_BULK', 'ROLE_CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS')")
    ChangeAccountStatusDto retrieveLoanOfficerDetailsForBranch(Short officeId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_APPROVE_LOANS_IN_BULK') and hasPermission(#accountsForUpdate, 'MAX_LOAN_AMOUNT_FOR_APPROVE')")
    List<String> updateSeveralLoanAccountStatuses(List<AccountUpdateStatus> accountsForUpdate, Date transactionDate);
    
    @PreAuthorize("isFullyAuthenticated() and hasPermission(#accountForUpdate, 'MAX_LOAN_AMOUNT_FOR_APPROVE')")
    String updateSingleLoanAccountStatus(AccountUpdateStatus accountForUpdate, Date transactionDate);
    
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_REVERSE_LOAN_DISBURSAL')")
    List<LoanActivityDto> retrieveLoanPaymentsForReversal(String globalAccountNum);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_REVERSE_LOAN_DISBURSAL')")
    void reverseLoanDisbursal(String globalAccountNum, String note);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS')")
    List<CustomerDto> retrieveActiveGroupingAtTopOfCustomerHierarchyForLoanOfficer(Short loanOfficerId, Short officeId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS')")
    MultipleLoanAccountDetailsDto retrieveMultipleLoanAccountDetails(String searchId, Short branchId, Integer productId);

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS')")
    List<String> createMultipleLoans(List<CreateLoanRequest> createMultipleLoans);

    @PreAuthorize("isFullyAuthenticated()")
    LoanApplicationStateDto retrieveLoanApplicationState();

    @PreAuthorize("isFullyAuthenticated()")
    List<QuestionGroupDetail> retrieveApplicableQuestionGroups(Integer productId);

    @PreAuthorize("isFullyAuthenticated()")
    CashFlowDto retrieveCashFlowSettings(DateTime firstInstallment, DateTime lastInstallment, Integer productId, BigDecimal loanAmount);

    @PreAuthorize("isFullyAuthenticated()")
    List<CashFlowDataDto> retrieveCashFlowSummary(List<MonthlyCashFlowDto> monthlyCashFlow, LoanScheduleDto loanScheduleDto);
    
    @PreAuthorize("isFullyAuthenticated()")
    Errors validateCashFlowForInstallmentsForWarnings(List<CashFlowDataDto> cashFlowDataDtos, Integer productId);
    
    @PreAuthorize("isFullyAuthenticated()")
    Errors validateCashFlowForInstallments(LoanInstallmentsDto loanInstallmentsDto, List<MonthlyCashFlowDto> monthlyCashFlows, 
            Double repaymentCapacity, BigDecimal cashFlowTotalBalance);
    
    @PreAuthorize("isFullyAuthenticated()")
    boolean isCompareWithCashFlowEnabledOnProduct(Integer productId);
    
    @PreAuthorize("isFullyAuthenticated()")
    Errors validateInputInstallments(Date disbursementDate, Integer minGapInDays, Integer maxGapInDays, 
            BigDecimal minInstallmentAmount, List<LoanCreationInstallmentDto> installments, Integer customerId);
    
    @PreAuthorize("isFullyAuthenticated()")
    Errors validateInstallmentSchedule(List<LoanCreationInstallmentDto> installments, BigDecimal minInstallmentAmount);

    @PreAuthorize("isFullyAuthenticated()")
	void makeEarlyRepaymentWithCommit(RepayLoanInfoDto repayLoanInfoDto);

    @PreAuthorize("isFullyAuthenticated()")
    OverpaymentDto retrieveOverpayment(String overpaymentId);

    @PreAuthorize("isFullyAuthenticated()")
    void applyOverpaymentClear(String overpaymentId, BigDecimal overpaymentAmount);
    
    void putLoanBusinessKeyInSession(String globalAccountNum, HttpServletRequest request);

    @PreAuthorize("isFullyAuthenticated()")
    void removeLoanPenalty(Integer loanId, Short penaltyId);

    @PreAuthorize("isFullyAuthenticated()")
    List<ApplicableCharge> getApplicablePenalties(Integer accountId);

    @PreAuthorize("isFullyAuthenticated()")
    List<AccountPaymentDto> getLoanAccountPayments(String globalAccountNum);
    
    Integer getGroupLoanType(String globalAccountNum);
    
    @PreAuthorize("isFullyAuthenticated()")
    void makeEarlyGroupRepayment(RepayLoanInfoDto repayLoanInfoDto, Map<String, Double> memberNumWithAmount);
    
    @PreAuthorize("isFullyAuthenticated()")
    BigDecimal calculateInterestDueForCurrentInstalmanet(RepayLoanInfoDto repayLoanInfoDto);
}