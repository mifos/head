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

package org.mifos.accounts.fund.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.fund.util.helpers.FundConstants;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.service.BusinessRuleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class FundDaoHibernate implements FundDao {

    private GenericDao genericDao;

    @Autowired
    public FundDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
	public int countOfFundByName(String fundName) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(FundConstants.FUND_NAME, fundName);
        return ((Long) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.CHECK_FUND_NAME_EXIST, queryParameters)).intValue();
    }

    @Override
	@SuppressWarnings("unchecked")
    public List<FundCodeEntity> findAllFundCodes() {
        return (List<FundCodeEntity>) this.genericDao.executeNamedQuery(NamedQueryConstants.GET_FUND_CODES, null);
    }

    @Override
	@SuppressWarnings("unchecked")
    public List<FundBO> findAllFunds() {
        return (List<FundBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.PRDSRCFUNDS, null);
    }

    @Override
	public FundBO findByName(String fundName) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(FundConstants.FUND_NAME, fundName);
        return (FundBO) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.GET_FUND_FOR_GIVEN_NAME, queryParameters);
    }

    @Override
	public FundBO findById(Short fundId) {
        Assert.notNull(fundId, "fundId cannot be null.");
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("FUND_ID", fundId);

        return (FundBO) this.genericDao.executeUniqueResultNamedQuery("fund.findById", queryParameters);
    }

    @Override
    public void save(FundBO fund) {
        this.genericDao.createOrUpdate(fund);
    }

    @Override
    public void update(FundBO fund, String fundName) {
        fund.validateFundName(fundName);
        if (fund.isDifferent(fundName)) {
            if (countOfFundByName(fundName.trim()) > 0) {
                throw new BusinessRuleException(FundConstants.DUPLICATE_FUNDNAME_EXCEPTION);
            }
        }

        fund.setFundName(fundName);

        this.genericDao.createOrUpdate(fund);
    }
}
