/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.mifos.accounts.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.loan.business.LoanActivityDto;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanInformationDto;
import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.struts.action.LoanInstallmentDetailsDto;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.util.helpers.LoanDisbursalDto;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.customers.client.business.service.ClientBusinessService;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.mifos.platform.validations.Errors;
import org.mifos.security.util.UserContext;


public interface LoanServiceFacade {

    /**
     * @deprecated - unable at present to decouple Dto and ActionForm
     */
    @Deprecated
    LoanCreationLoanScheduleDetailsDto retrieveScheduleDetailsForRedoLoan(Integer customerId, DateTime disbursementDate, Short fundId, LoanAccountActionForm loanActionForm);

    /**
     * @deprecated - unable at present to decouple Dto and ActionForm
     */
    @Deprecated
    LoanCreationLoanScheduleDetailsDto retrieveScheduleDetailsForLoanCreation(Integer customerId, DateTime disbursementDate, Short fundId, LoanAccountActionForm loanActionForm);

    /**
     * @deprecated - unable at present to decouple LoanBO and ActionForm for redo functionality
     */
    @Deprecated
    LoanBO previewLoanRedoDetails(Integer customerId, LoanAccountActionForm loanAccountActionForm, DateTime disbursementDate);

    LoanCreationResultDto createLoan(UserContext userContext, Integer customerId, DateTime disbursementDate,
            FundBO fund, LoanAccountActionForm loanActionForm) throws ApplicationException;

    LoanCreationResultDto redoLoan(UserContext userContext, Integer customerId, DateTime disbursementDate,
            LoanAccountActionForm loanActionForm) throws ApplicationException;

    void checkIfProductsOfferingCanCoexist(Integer loanAccountId) throws ServiceException, PersistenceException,
            AccountException;

    LoanDisbursalDto getLoanDisbursalDto(Integer loanAccountId) throws ServiceException;

    List<LoanActivityDto> retrieveAllLoanAccountActivities(String globalAccountNum);

    LoanInstallmentDetailsDto retrieveInstallmentDetails(Integer accountId);

    boolean isTrxnDateValid(Integer loanAccountId, Date trxnDate) throws ApplicationException;

    LoanBO retrieveLoanRepaymentSchedule(UserContext userContext, Integer loanId, Date asOfDate);

    List<AccountStatusChangeHistoryEntity> retrieveLoanAccountStatusChangeHistory(UserContext userContext, String globalAccountNum);

    void makeEarlyRepayment(String globalAccountNum, String earlyRepayAmount, String receiptNumber,
                            java.sql.Date receiptDate, String paymentTypeId, Short id, boolean waiveInterest) throws AccountException;

    LoanInformationDto getLoanInformationDto(String globalAccountNum, UserContext userContext) throws ServiceException;

    List<LoanAccountDetailsDto> getLoanAccountDetailsViewList(LoanInformationDto loanInformationDto, List<BusinessActivityEntity> businessActEntity, ClientBusinessService clientBusinessService)
            throws ServiceException;

    RepayLoanDto getRepaymentDetails(String globalAccountNumber, Short localeId, AcceptedPaymentTypePersistence acceptedPaymentTypePersistence) throws PersistenceException;

    Errors validateInputInstallments(Date disbursementDate, VariableInstallmentDetailsBO variableInstallmentDetails, List<RepaymentScheduleInstallment> installments, Integer customerId);

    Errors validateInstallmentSchedule(List<RepaymentScheduleInstallment> installments, VariableInstallmentDetailsBO variableInstallmentDetailsBO);

    Errors validateCashFlowForInstallmentsForWarnings(LoanAccountActionForm loanActionForm, Short localeId) throws ServiceException;

    Errors validateCashFlowForInstallments(List<RepaymentScheduleInstallment> installments, CashFlowForm cashFlowForm, Double repaymentCapacity);

    OriginalScheduleInfoDto retrieveOriginalLoanSchedule(Integer accountId, Locale locale) throws PersistenceException;
}