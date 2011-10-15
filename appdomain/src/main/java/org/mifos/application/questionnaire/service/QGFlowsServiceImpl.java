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

package org.mifos.application.questionnaire.service;

import java.util.List;

import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.QuestionGroupReference;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.platform.questionnaire.QGFlowsService;

public class QGFlowsServiceImpl implements QGFlowsService {

    @Override
    public void applyToAllLoanProducts(Integer questionGroupId) throws SystemException {
        LoanPrdBusinessService loanPrdBusinessService = new LoanPrdBusinessService();

        try {
            List<LoanOfferingBO> offerings = loanPrdBusinessService.getAllLoanOfferings((short)1);
            if (offerings.size() > 0) {
                QuestionGroupReference questionGroupReference = new QuestionGroupReference();
                questionGroupReference.setQuestionGroupId(questionGroupId);

                for (LoanOfferingBO offering : offerings) {
                    offering.getQuestionGroups().add(questionGroupReference);
                    offering.save();
                }

                StaticHibernateUtil.commitTransaction();
            }
        } catch (ServiceException e) {
            throw new SystemException(e);
        } catch (ProductDefinitionException e) {
            throw new SystemException(e);
        }
    }
}
