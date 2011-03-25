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

package org.mifos.ui.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.clientportfolio.loan.ui.LoanAccountQuestionGroupFormBean;
import org.mifos.clientportfolio.loan.ui.LoanAccountStatusFormBean;
import org.mifos.clientportfolio.newloan.applicationservice.LoanApplicationStateDto;
import org.mifos.dto.screen.LoanInformationDto;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LoanAccountStatusController {

    private final LoanAccountServiceFacade loanAccountServiceFacade;
    
    @Autowired
    public LoanAccountStatusController(LoanAccountServiceFacade loanAccountServiceFacade) {
        this.loanAccountServiceFacade = loanAccountServiceFacade;
    }

    public void loadQuestionGroups(Integer loanStatus, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean) {
        List<QuestionGroupDetail> questionGroups = loanAccountServiceFacade.retrieveLoanStatusUpdateQuestionGroups(loanStatus);
        loanAccountQuestionGroupFormBean.setQuestionGroups(questionGroups);
    }

    public void loadAccount(String globalAccountNum, LoanAccountStatusFormBean formBean) {

        LoanInformationDto loanInformation = loanAccountServiceFacade.retrieveLoanInformation(globalAccountNum);
        formBean.setLoanInformation(loanInformation);

        // TODO store and display current account status
        // what does this do? have to get a list of account status and then grab the flag message text (AFAIK, only cancel has them)
//        AccountStatusDto accountStatuses = this.loanAccountServiceFacade.retrieveAccountStatuses(loanInformation.getAccountId().longValue());
//        formBean.setCurrentAccountStatus();
        
        LoanApplicationStateDto loanApplicationState = loanAccountServiceFacade.retrieveLoanApplicationState();
        formBean.setLoanApplicationState(loanApplicationState);
        
        Map<String, String> accountStatusOptions = getAccountStatusOptions();
        formBean.setAccountStatusOptions(accountStatusOptions);
        
        Map<String, String> cancelOptions = getCancelOptions();
        formBean.setCancelOptions(cancelOptions);
    }
    
    public void updateStatus(LoanAccountStatusFormBean formBean) {
        // TODO
    }
    
    private Map<String, String> getCancelOptions() {
        // TODO
        return new HashMap<String, String>();
    }
    
    private Map<String, String> getAccountStatusOptions() {
        LoanApplicationStateDto loanApplicationState = loanAccountServiceFacade.retrieveLoanApplicationState();
        Map<String, String> options = new HashMap<String, String>();
        options.put(loanApplicationState.getApprovedApplicationId().toString(), "Approved - TODO i18n");
        options.put(loanApplicationState.getPartialApplicationId().toString(), "Partial - TODO i18n");
        options.put(loanApplicationState.getCancelledApplicationId().toString(), "Cancelled - TODO i18n");
        return options;
    }
}
