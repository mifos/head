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

import java.util.List;

import org.mifos.application.admin.servicefacade.AdminServiceFacade;
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
    private final AdminServiceFacade adminServiceFacade;
    
    @Autowired
    public LoanAccountStatusController(LoanAccountServiceFacade loanAccountServiceFacade, AdminServiceFacade adminServiceFacade) {
        this.loanAccountServiceFacade = loanAccountServiceFacade;
        this.adminServiceFacade = adminServiceFacade;
    }

    public void loadQuestionGroups(Integer productId, LoanAccountQuestionGroupFormBean loanAccountQuestionGroupFormBean) {
        List<QuestionGroupDetail> questionGroups = loanAccountServiceFacade.retrieveApplicableQuestionGroups(productId);
        loanAccountQuestionGroupFormBean.setQuestionGroups(questionGroups);
    }

    public void loadAccount(String globalAccountNum, LoanAccountStatusFormBean formBean) {
        LoanInformationDto loanInformation = loanAccountServiceFacade.retrieveLoanInformation(globalAccountNum);
        formBean.setLoanInformation(loanInformation);
        
        LoanApplicationStateDto loanApplicationState = loanAccountServiceFacade.retrieveLoanApplicationState();
        formBean.setLoanApplicationState(loanApplicationState);
// prepareFormBean(formBean);
    }
    
//    public void prepareFormBean(LoanAccountStatusFormBean formBean) {
//        LoanApplicationStateDto loanApplicationState = loanAccountServiceFacade.retrieveLoanApplicationState();
//        formBean.setLoanApplicationState(loanApplicationState);
//    }
    
    public void updateStatus(LoanAccountStatusFormBean formBean) {
        
    }
}
