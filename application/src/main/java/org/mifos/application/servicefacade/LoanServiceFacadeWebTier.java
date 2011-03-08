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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.OriginalLoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.business.service.validators.InstallmentValidationContext;
import org.mifos.accounts.loan.business.service.validators.InstallmentsValidator;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.platform.validations.Errors;
import org.springframework.beans.factory.annotation.Autowired;

public class LoanServiceFacadeWebTier implements LoanServiceFacade {

    private final CustomerDao customerDao;
    private final LoanDao loanDao;
    private final InstallmentsValidator installmentsValidator;
    private final LoanBusinessService loanBusinessService;
    private final HolidayServiceFacade holidayServiceFacade;

    @Autowired
    public LoanServiceFacadeWebTier(final CustomerDao customerDao,
                                    final LoanDao loanDao,
                                    InstallmentsValidator installmentsValidator,
                                    LoanBusinessService loanBusinessService,
                                    HolidayServiceFacade holidayServiceFacade) {
        this.customerDao = customerDao;
        this.loanDao = loanDao;
        this.installmentsValidator = installmentsValidator;
        this.loanBusinessService = loanBusinessService;
        this.holidayServiceFacade = holidayServiceFacade;
    }

    @Override
	public Errors validateInputInstallments(Date disbursementDate, VariableInstallmentDetailsBO variableInstallmentDetails,
                                            List<RepaymentScheduleInstallment> installments, Integer customerId) {
        Short officeId = customerDao.findCustomerById(customerId).getOfficeId();
        InstallmentValidationContext context = new InstallmentValidationContext(disbursementDate, variableInstallmentDetails, holidayServiceFacade, officeId);
        return installmentsValidator.validateInputInstallments(installments, context);
    }

    @Override
    public Errors validateInstallmentSchedule(List<RepaymentScheduleInstallment> installments, VariableInstallmentDetailsBO variableInstallmentDetailsBO) {
        return installmentsValidator.validateInstallmentSchedule(installments, variableInstallmentDetailsBO);
    }
    
    @Override
    public OriginalScheduleInfoDto retrieveOriginalLoanSchedule(Integer accountId, Locale locale) throws PersistenceException {
        List<OriginalLoanScheduleEntity> loanScheduleEntities = loanBusinessService.retrieveOriginalLoanSchedule(accountId);
        ArrayList<RepaymentScheduleInstallment> repaymentScheduleInstallments = new ArrayList<RepaymentScheduleInstallment>();
        for (OriginalLoanScheduleEntity loanScheduleEntity : loanScheduleEntities) {
              repaymentScheduleInstallments.add(loanScheduleEntity.toDto(locale));
        }

        LoanBO loan = this.loanDao.findById(accountId);
        return new OriginalScheduleInfoDto(loan.getLoanAmount().toString(),loan.getDisbursementDate(),repaymentScheduleInstallments);
    }
}