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

package org.mifos.clientportfolio.newloan.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.clientportfolio.newloan.domain.IndividualLoanScheduleFactory;
import org.mifos.clientportfolio.newloan.domain.LoanInstallmentFactory;
import org.mifos.clientportfolio.newloan.domain.LoanInstallmentFactoryImpl;
import org.mifos.clientportfolio.newloan.domain.LoanInstallmentGenerator;
import org.mifos.clientportfolio.newloan.domain.LoanProductOverridenDetail;
import org.mifos.clientportfolio.newloan.domain.LoanSchedule;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleConfiguration;
import org.mifos.clientportfolio.newloan.domain.LoanScheduleFactory;
import org.mifos.clientportfolio.newloan.domain.RecurringScheduledEventFactory;
import org.mifos.clientportfolio.newloan.domain.RecurringScheduledEventFactoryImpl;
import org.mifos.customers.business.CustomerBO;
import org.springframework.beans.factory.annotation.Autowired;

public class LoanScheduleServiceDomain implements LoanScheduleService {

    private RecurringScheduledEventFactory scheduledEventFactory = new RecurringScheduledEventFactoryImpl(); 
    private LoanInstallmentFactory loanInstallmentFactory = new LoanInstallmentFactoryImpl(scheduledEventFactory);
    private LoanScheduleFactory loanScheduleFactory;
    
    @Autowired
    public LoanScheduleServiceDomain(FeeDao feeDao) {
        loanScheduleFactory = new IndividualLoanScheduleFactory(feeDao);
    }
    
    @Override
    public LoanSchedule generate(LoanOfferingBO loanProduct, CustomerBO customer, MeetingBO loanMeeting,
            LoanProductOverridenDetail overridenDetail, LoanScheduleConfiguration configuration, Short userBranchOfficeId, 
            List<AccountFeesEntity> accountFees, LocalDate disbursementDate) {
        
        List<DateTime> loanScheduleDates = generateScheduleDates(loanProduct, loanMeeting, overridenDetail, configuration, userBranchOfficeId);
        
        return loanScheduleFactory.create(disbursementDate, loanScheduleDates, loanProduct, customer, loanMeeting, overridenDetail.getLoanAmount(), 
                overridenDetail.getInterestRate(), configuration.getNumberOfInterestDays(), overridenDetail.getGraceDuration(), accountFees);
    }
    
    @Override
    public LoanSchedule generate(LoanOfferingBO loanProduct, CustomerBO customer, MeetingBO loanMeeting,LoanProductOverridenDetail overridenDetail, LoanScheduleConfiguration configuration, 
            List<AccountFeesEntity> accountFees, LocalDate disbursementDate, List<DateTime> loanScheduleDates) {
        
        return loanScheduleFactory.create(disbursementDate, loanScheduleDates, loanProduct, customer, loanMeeting, overridenDetail.getLoanAmount(), 
                overridenDetail.getInterestRate(), configuration.getNumberOfInterestDays(), overridenDetail.getGraceDuration(), accountFees);
    }

    private List<DateTime> generateScheduleDates(LoanOfferingBO loanProduct, MeetingBO loanMeeting,
            LoanProductOverridenDetail overridenDetail, LoanScheduleConfiguration configuration,
            Short userBranchOfficeId) {
        LoanInstallmentGenerator loanInstallmentGenerator = loanInstallmentFactory.create(loanMeeting, configuration.isLoanScheduleIndependentOfCustomerMeetingEnabled());
        
        List<InstallmentDate> installmentDates = loanInstallmentGenerator.generate(overridenDetail.getDisbursementDate(), overridenDetail.getNumberOfInstallments(), loanProduct.getGracePeriodType().asEnum(), overridenDetail.getGraceDuration(), userBranchOfficeId);
        List<DateTime> loanScheduleDates = new ArrayList<DateTime>();
        for (InstallmentDate installmentDate : installmentDates) {
            loanScheduleDates.add(new DateTime(installmentDate.getInstallmentDueDate()));
        }
        return loanScheduleDates;
    }
}