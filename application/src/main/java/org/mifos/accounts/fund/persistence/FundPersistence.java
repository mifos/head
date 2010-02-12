/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.accounts.fund.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class FundPersistence extends Persistence {
    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.FUNDLOGGER);

    public Long getFundNameCount(String fundName) throws PersistenceException {
        logger.debug("getting the fund name count for :" + fundName);
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(FundConstants.FUND_NAME, fundName);
        return (Long) execUniqueResultNamedQuery(NamedQueryConstants.CHECK_FUND_NAME_EXIST, queryParameters);
    }

    public List<FundCodeEntity> getFundCodes() throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.GET_FUND_CODES, null);
    }

    public List<FundBO> getSourcesOfFund() throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.PRDSRCFUNDS, null);
    }

    public FundBO getFund(String fundName) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(FundConstants.FUND_NAME, fundName);
        return (FundBO) execUniqueResultNamedQuery(NamedQueryConstants.GET_FUND_FOR_GIVEN_NAME, queryParameters);
    }

    public FundBO getFund(Short fundId) throws PersistenceException {
        return (FundBO) getPersistentObject(FundBO.class, fundId);
    }
}
