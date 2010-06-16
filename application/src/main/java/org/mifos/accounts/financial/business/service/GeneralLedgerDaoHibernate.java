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

package org.mifos.accounts.financial.business.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;

public class GeneralLedgerDaoHibernate implements GeneralLedgerDao {

    private final GenericDao genericDao;

    public GeneralLedgerDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public List<GLCodeEntity> retreiveGlCodesBy(FinancialActionConstants financialTransactionType, FinancialConstants debitOrCredit) throws FinancialException {

        List<GLCodeEntity> glCodeList = new ArrayList<GLCodeEntity>();
        Set<COABO> applicableCategory = null;
        FinancialActionTypeEntity finActionFees = FinancialActionCache.getFinancialAction(financialTransactionType);
        if (debitOrCredit.equals(FinancialConstants.DEBIT)) {
            applicableCategory = finActionFees.getApplicableDebitCharts();
        } else if (debitOrCredit.equals(FinancialConstants.CREDIT)) {
            applicableCategory = finActionFees.getApplicableCreditCharts();
        }

        for (COABO chartOfAccounts : applicableCategory) {
            glCodeList.add(chartOfAccounts.getAssociatedGlcode());
        }
        return glCodeList;
    }

    @Override
    public GLCodeEntity findGlCodeById(Short glCodeId) {

        Map<String, Short> queryParameters = new HashMap<String, Short>();
        queryParameters.put("glcodeId", glCodeId);

        return (GLCodeEntity) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GL_CODE_BY_ID, queryParameters);
    }
}
