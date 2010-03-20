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

package org.mifos.reports.business.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.NumberUtils;

public class ReportsDataService {

    private LoanPrdBusinessService loanPrdBusinessService;

    private PersonnelBusinessService personnelBusinessService;

    private OfficeBusinessService officeBusinessService;

    private PersonnelBO personnel;

    private LoanPersistence loanPersistence;

    public ReportsDataService() {
        this.personnelBusinessService = new PersonnelBusinessService();
        this.officeBusinessService = new OfficeBusinessService();
        this.loanPrdBusinessService = new LoanPrdBusinessService();
        this.loanPersistence = new LoanPersistence();
    }

    public void initialize(Integer userId) throws ServiceException {
        this.personnel = personnelBusinessService.getPersonnel(NumberUtils.convertIntegerToShort(userId));
    }

    public List<OfficeBO> getActiveBranchesUnderUser() throws ServiceException {
        return officeBusinessService.getActiveBranchesUnderUser(personnel);
    }

    public List<PersonnelBO> getActiveLoanOfficers(Integer branchId) throws ServiceException {
        List<PersonnelBO> loanOfficers = new ArrayList<PersonnelBO>();
        if (personnel.isLoanOfficer()) {
            loanOfficers.add(personnel);
        } else {
            loanOfficers = personnelBusinessService.getActiveLoanOfficersUnderOffice(NumberUtils
                    .convertIntegerToShort(branchId));
            loanOfficers.add(PersonnelBO.ALL_PERSONNEL);
        }
        return loanOfficers;
    }

    public List<LoanOfferingBO> getAllLoanProducts() throws ServiceException {
        List<LoanOfferingBO> loanOffering = new ArrayList<LoanOfferingBO>();
        loanOffering = loanPrdBusinessService.getAllLoanOfferings(personnel.getLocaleId());
        loanOffering.add(LoanOfferingBO.ALL_LOAN_PRD);
        return loanOffering;
    }

    public List<LoanBO> getLoanAccountsInActiveBadStanding(Integer branchId, Integer loanOfficerId,
            Integer loanProductId) throws PersistenceException {
        return loanPersistence.getLoanAccountsInActiveBadStanding(NumberUtils.convertIntegerToShort(branchId),
                NumberUtils.convertIntegerToShort(loanOfficerId), NumberUtils.convertIntegerToShort(loanProductId));
    }

    public List<LoanBO> getActiveLoansBothInGoodAndBadStandingByLoanOfficer(Integer branchId, Integer loanOfficerId,
            Integer loanProductId) throws PersistenceException {
        return loanPersistence.getActiveLoansBothInGoodAndBadStandingByLoanOfficer(NumberUtils
                .convertIntegerToShort(branchId), NumberUtils.convertIntegerToShort(loanOfficerId), NumberUtils
                .convertIntegerToShort(loanProductId));
    }

    public BigDecimal getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(Integer branchId,
            Integer loanOfficerId, Integer loanProductId) throws PersistenceException {
        return loanPersistence.getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(NumberUtils
                .convertIntegerToShort(branchId), NumberUtils.convertIntegerToShort(loanOfficerId), NumberUtils
                .convertIntegerToShort(loanProductId));
    }

    void setLoanPrdBusinessService(LoanPrdBusinessService loanPrdBusinessService) {
        this.loanPrdBusinessService = loanPrdBusinessService;
    }

    void setPersonnelBusinessService(PersonnelBusinessService personnelBusinessService) {
        this.personnelBusinessService = personnelBusinessService;
    }

    void setOfficeBusinessService(OfficeBusinessService officeBusinessService) {
        this.officeBusinessService = officeBusinessService;
    }

    void setPersonnel(PersonnelBO personnel) {
        this.personnel = personnel;
    }

    public void setLoanPersistence(LoanPersistence loanPersistence) {
        this.loanPersistence = loanPersistence;
    }

    PersonnelBO getPersonnel() {
        return personnel;
    }

}
