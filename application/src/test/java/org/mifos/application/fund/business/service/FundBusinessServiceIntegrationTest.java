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

package org.mifos.application.fund.business.service;

import java.util.List;

import junit.framework.Assert;

import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FundBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    public FundBusinessServiceIntegrationTest() throws Exception {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetFundCodesForInvalidConnection() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
           Assert.assertEquals(5, new FundBusinessService().getFundCodes().size());
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testGetFundCodes() throws Exception {
        List<FundCodeEntity> funds = new FundBusinessService().getFundCodes();
       Assert.assertEquals(5, funds.size());
    }

    public void testGetSourcesOfFund() throws Exception {
        List<FundBO> funds = new FundBusinessService().getSourcesOfFund();
        Assert.assertNotNull(funds);
       Assert.assertEquals(5, funds.size());
    }

    public void testGetSourcesOfFundForInvalidConnection() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new FundBusinessService().getSourcesOfFund();
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testGetFund() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        FundBO fund = TestObjectFactory.createFund(fundCodeEntity, "Fund1");
       Assert.assertEquals("Fund1", new FundBusinessService().getFund("Fund1").getFundName());
        TestObjectFactory.removeObject(fund);
    }

    public void testGetFundForInvalidConnection() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        FundBO fund = TestObjectFactory.createFund(fundCodeEntity, "Fund1");
        TestObjectFactory.simulateInvalidConnection();
        try {
            new FundBusinessService().getFund("Fund1").getFundName();
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
        TestObjectFactory.removeObject(fund);
    }

    public void testGetFundById() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        FundBO fund = TestObjectFactory.createFund(fundCodeEntity, "Fund1");
        StaticHibernateUtil.closeSession();

        fund = new FundBusinessService().getFund(fund.getFundId());
        Assert.assertNotNull(fund);
       Assert.assertEquals("Fund1", fund.getFundName());
       Assert.assertEquals(1, fund.getFundCode().getFundCodeId().intValue());
        TestObjectFactory.removeObject(fund);
    }

    public void testGetFundByIdForInvalidConnection() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        FundBO fund = TestObjectFactory.createFund(fundCodeEntity, "Fund1");
        TestObjectFactory.simulateInvalidConnection();
        try {
            new FundBusinessService().getFund(fund.getFundId());
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
        TestObjectFactory.removeObject(fund);
    }
}
