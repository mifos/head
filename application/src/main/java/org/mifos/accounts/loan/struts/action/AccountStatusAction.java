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

package org.mifos.accounts.loan.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.struts.actionforms.AccountStatusActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.dto.domain.AccountUpdateStatus;
import org.mifos.dto.screen.ChangeAccountStatusDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class AccountStatusAction extends BaseAction {

    private LoanBusinessService loanService = ApplicationContextProvider.getBean(LoanBusinessService.class);

    public AccountStatusAction() {
        super();
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse httpservletresponse) throws Exception {
        cleanUp(form, request);

        ChangeAccountStatusDto activeBranchesAndLoanOfficers = this.loanAccountServiceFacade.retrieveAllActiveBranchesAndLoanOfficerDetails();

        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST, activeBranchesAndLoanOfficers.getActiveBranches(), request);
        SessionUtils.setCollectionAttribute(LoanConstants.LOAN_OFFICERS, activeBranchesAndLoanOfficers.getLoanOfficers(), request);

        return mapping.findForward(ActionForwards.changeAccountStatus_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward searchResults(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse httpservletresponse) throws Exception {
        AccountStatusActionForm accountStatusActionForm = (AccountStatusActionForm) form;

        List<LoanBO> searchResults = loanService.getSearchResults(accountStatusActionForm.getOfficeId(), accountStatusActionForm
        .getPersonnelId(), accountStatusActionForm.getCurrentStatus());
        if (searchResults.size() == 0) {
            return mapping.findForward(ActionForwards.noresultfound.toString());
        }

        SessionUtils.setCollectionAttribute(LoanConstants.SEARCH_RESULTS, searchResults, request);

        return mapping.findForward(ActionForwards.changeAccountStatusSearch_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward update(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse httpservletresponse) throws Exception {

        AccountStatusActionForm accountStatusActionForm = (AccountStatusActionForm) form;

        List<AccountUpdateStatus> accountsForUpdate = new ArrayList<AccountUpdateStatus>();
        List<AccountUpdateStatus> individualAccountsForUpdate = new ArrayList<AccountUpdateStatus>();
        for (String accountId : accountStatusActionForm.getAccountRecords()) {
            if (StringUtils.isNotBlank(accountId)) {
                Long accountIdValue = Long.parseLong(accountId);
                
                //GLIM
            	List<LoanBO> individualLoans = this.loanDao.findIndividualLoans(Integer.valueOf(accountId));
            	for(LoanBO indivdual : individualLoans) {
            		Short newStatusId = getShortValue(accountStatusActionForm.getNewStatus());
            		individualAccountsForUpdate.add(new AccountUpdateStatus(indivdual.getAccountId().longValue(), newStatusId, null, accountStatusActionForm.getComments()));
            	}
            	this.loanAccountServiceFacade.updateSeveralLoanAccountStatuses(individualAccountsForUpdate, null);

                Short newStatusId = getShortValue(accountStatusActionForm.getNewStatus());
                Short flagId = null;
                String comment = accountStatusActionForm.getComments();
                accountsForUpdate.add(new AccountUpdateStatus(accountIdValue, newStatusId, flagId, comment));
            }
        }
        
        List<String> accountNumbers = this.loanAccountServiceFacade.updateSeveralLoanAccountStatuses(accountsForUpdate, null);
        
        request.setAttribute(LoanConstants.ACCOUNTS_LIST, accountNumbers);

        return mapping.findForward(ActionForwards.changeAccountStatusConfirmation_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getLoanOfficers(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse httpservletresponse) throws Exception {
        AccountStatusActionForm accountStatusActionForm = (AccountStatusActionForm) form;
        Short officeId = Short.valueOf(accountStatusActionForm.getOfficeId());

        ChangeAccountStatusDto changeAccountStatusDto = this.loanAccountServiceFacade.retrieveLoanOfficerDetailsForBranch(officeId);

        SessionUtils.setCollectionAttribute(LoanConstants.LOAN_OFFICERS, changeAccountStatusDto.getLoanOfficers(), request);

        if (officeId != null) {
            AccountStateEntity accountStateEntity = legacyMasterDao.getPersistentObject(
                    AccountStateEntity.class, changeAccountStatusDto.getAccountState());
            SessionUtils.setAttribute(LoanConstants.LOANACCOUNTSTAES, accountStateEntity, request);
        }

        return mapping.findForward(ActionForwards.changeAccountStatus_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(final ActionMapping mapping, @SuppressWarnings("unused") final ActionForm form, final HttpServletRequest request,
            @SuppressWarnings("unused") final HttpServletResponse httpservletresponse) throws Exception {
        String method = (String) request.getAttribute("methodCalled");

        if (method.equalsIgnoreCase(Methods.searchResults.toString())) {
            return mapping.findForward(ActionForwards.changeAccountStatus_success.toString());
        }

        if (method.equalsIgnoreCase(Methods.update.toString())) {
            return mapping.findForward(ActionForwards.changeAccountStatusSearch_success.toString());
        }

        return null;
    }

    private void cleanUp(final ActionForm form, final HttpServletRequest request) {
        AccountStatusActionForm accountStatusActionForm = (AccountStatusActionForm) form;
        accountStatusActionForm.setAccountRecords(new ArrayList<String>());
        accountStatusActionForm.setComments("");
        accountStatusActionForm.setCurrentStatus("");
        accountStatusActionForm.setLoadOfficer("");
        accountStatusActionForm.setNewStatus("");
        accountStatusActionForm.setOfficeId("");
        accountStatusActionForm.setOfficeName("");
        accountStatusActionForm.setPersonnelId("");
        accountStatusActionForm.setType("");
        request.setAttribute("session", null);
    }
}