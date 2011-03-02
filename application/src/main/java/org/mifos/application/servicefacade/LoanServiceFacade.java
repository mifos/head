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
import java.util.Locale;

import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.dto.domain.MonthlyCashFlowDto;
import org.mifos.dto.screen.LoanInstallmentsDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.platform.validations.Errors;

/**
 * @deprecated - do not use. please add functionality to {@link LoanAccountServiceFacade} instead.
 */
@Deprecated
public interface LoanServiceFacade {

    Errors validateInputInstallments(Date disbursementDate, VariableInstallmentDetailsBO variableInstallmentDetails, List<RepaymentScheduleInstallment> installments, Integer customerId);

    Errors validateInstallmentSchedule(List<RepaymentScheduleInstallment> installments, VariableInstallmentDetailsBO variableInstallmentDetailsBO);

    public Errors validateCashFlowForInstallments(LoanInstallmentsDto loanInstallmentsDto, List<MonthlyCashFlowDto> monthlyCashFlows, 
            Double repaymentCapacity, BigDecimal cashFlowTotalBalance);

    OriginalScheduleInfoDto retrieveOriginalLoanSchedule(Integer accountId, Locale locale) throws PersistenceException;
}