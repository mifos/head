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

package org.mifos.accounts.loan.persistance;

import java.util.List;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.framework.fileupload.domain.LoanFileEntity;

public interface LoanDao {

    LoanBO findById(Integer accountId);

    LoanBO findByGlobalAccountNum(String globalAccountNum);

    void save(LoanBO loanAccount);

    List<SurveyDto> getAccountSurveyDto(Integer accountId);

    List<LoanBO> findIndividualLoans(Integer accountId);
    
    List<LoanBO> findAllBadStandingLoans(int position,int noOfObjects);
    
    List<LoanBO> findBadStandingLoansUnderLoanOfficer(int position,int noOfObjects,Short loanOfficerId);
    
    int countAllBadStandingLoans();
    
    int countBadStandingLoansUnderLoanOfficer(Short loanOfficerId);
    
    List<LoanBO> findAllLoansWaitingForApproval(int position,int noOfObjects);
    
    List<LoanBO> findLoansWaitingForApprovalUnderLoanOfficer(int position,int noOfObjects,Short loanOfficerId);
    
    int countAllLoansWaitingForApproval();
    
    int countLoansWaitingForApprovalUnderLoanOfficer(Short loanOfficerId);
    
    List<LoanBO> findLoansToBePaidCurrentWeek(int position,int noOfObjects);
    
    List<LoanBO> findLoansToBePaidCurrentWeekUnderLoanOfficer(int position,int noOfObjects,Short loanOfficerId);
    
    int countLoansToBePaidCurrentWeek();
    
    int countLoansToBePaidCurrentWeekUnderLoanOfficer(Short loanOfficerId);
    
    LoanFileEntity getUploadedFile(Long fileId);
    
    List<LoanFileEntity> getLoanAllUploadedFiles(Integer accountId);
    
    LoanFileEntity getLoanUploadedFileByName(Integer accountId, String fileName);
}
