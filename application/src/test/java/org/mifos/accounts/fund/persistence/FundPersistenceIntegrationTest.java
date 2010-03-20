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

package org.mifos.accounts.fund.persistence;

import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.fund.business.FundBO;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FundPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public FundPersistenceIntegrationTest() throws Exception {
        super();
    }

    public void testGetFundNameCountWithoutData() throws PersistenceException {
       Assert.assertEquals(Long.valueOf("0"), new FundPersistence().getFundNameCount("Fund1"));
    }

    public void testGetFundNameCountWithDifferentName() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        FundBO fund = TestObjectFactory.createFund(fundCodeEntity, "Fund1");
       Assert.assertEquals(Long.valueOf("0"), new FundPersistence().getFundNameCount("Fund2"));
        TestObjectFactory.removeObject(fund);

    }

    public void testGetFundNameCountWithSameName() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        FundBO fund = TestObjectFactory.createFund(fundCodeEntity, "Fund1");
       Assert.assertEquals(Long.valueOf("1"), new FundPersistence().getFundNameCount("Fund1"));
        TestObjectFactory.removeObject(fund);
    }

    public void testGetFund() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        FundBO fund = TestObjectFactory.createFund(fundCodeEntity, "Fund1");
       Assert.assertEquals("Fund1", new FundPersistence().getFund("Fund1").getFundName());
        TestObjectFactory.removeObject(fund);
    }

    public void testGetFundCodes() throws Exception {
        List<FundCodeEntity> funds = new FundPersistence().getFundCodes();
       Assert.assertEquals(5, funds.size());
    }

    public void testGetSourcesOfFund() throws Exception {
        List<FundBO> funds = new FundPersistence().getSourcesOfFund();
        Assert.assertNotNull(funds);
       Assert.assertEquals(5, funds.size());
    }

    public void testGetFundById() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        FundBO fund = TestObjectFactory.createFund(fundCodeEntity, "Fund1");
        StaticHibernateUtil.closeSession();

        fund = new FundPersistence().getFund(fund.getFundId());
        Assert.assertNotNull(fund);
       Assert.assertEquals("Fund1", fund.getFundName());
       Assert.assertEquals(1, fund.getFundCode().getFundCodeId().intValue());
        TestObjectFactory.removeObject(fund);
    }
}
