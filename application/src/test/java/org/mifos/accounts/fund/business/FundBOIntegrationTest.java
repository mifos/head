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

package org.mifos.accounts.fund.business;

import junit.framework.Assert;

import org.mifos.accounts.fund.exception.FundException;
import org.mifos.accounts.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FundBOIntegrationTest extends MifosIntegrationTestCase {

    public FundBOIntegrationTest() throws Exception {
        super();
    }

    private FundBO fundBO;
    private FundBO fund;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(fundBO);
        TestObjectFactory.cleanUp(fund);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testBuildFundWithoutFundCode() throws Exception {
        try {
            fundBO = new FundBO(null, "Fund-1");
           Assert.assertTrue(false);
        } catch (FundException fe) {
           Assert.assertTrue(true);
           Assert.assertEquals(FundConstants.INVALID_FUND_CODE, fe.getKey());
        }
    }

    public void testBuildFundWithoutFundName() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        try {
            fundBO = new FundBO(fundCodeEntity, null);
           Assert.assertTrue(false);
        } catch (FundException fe) {
           Assert.assertTrue(true);
           Assert.assertEquals(FundConstants.INVALID_FUND_NAME, fe.getKey());
        }
    }

    public void testBuildFundWithDuplicateFundName() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        fundBO = createFund(fundCodeEntity, "Fund-1");
        try {
            new FundBO(fundCodeEntity, "Fund-1");
            Assert.fail();
        } catch (FundException fe) {
           Assert.assertTrue(true);
           Assert.assertEquals(FundConstants.DUPLICATE_FUNDNAME_EXCEPTION, fe.getKey());
        }
    }

    public void testBuildFundForInvalidConnection() throws Exception {

        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        TestObjectFactory.simulateInvalidConnection();
        try {
            fundBO = new FundBO(fundCodeEntity, "Fund-1");
            fundBO.save();
            Assert.fail();
        } catch (FundException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }

    }

    public void testBuildFund() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        fundBO = new FundBO(fundCodeEntity, "Fund-1");
        fundBO.save();
        StaticHibernateUtil.commitTransaction();

        fundBO = (FundBO) TestObjectFactory.getObject(FundBO.class, fundBO.getFundId());
       Assert.assertEquals("Fund-1", fundBO.getFundName());
        Assert.assertNotNull(fundBO.getFundCode());
       Assert.assertEquals(fundCodeEntity.getFundCodeValue(), fundBO.getFundCode().getFundCodeValue());
    }

    public void testUpdateFundForNullFundName() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        fundBO = createFund(fundCodeEntity, "Fund-1");
        StaticHibernateUtil.closeSession();

        fundBO = (FundBO) TestObjectFactory.getObject(FundBO.class, fundBO.getFundId());
       Assert.assertEquals("Fund-1", fundBO.getFundName());
        Assert.assertNotNull(fundBO.getFundCode());
       Assert.assertEquals(fundCodeEntity.getFundCodeValue(), fundBO.getFundCode().getFundCodeValue());
        try {
            fundBO.update("");
           Assert.assertTrue(false);
        } catch (FundException fe) {
           Assert.assertTrue(true);
           Assert.assertEquals(FundConstants.INVALID_FUND_NAME, fe.getKey());
        }
    }

    public void testUpdateFundForDuplicateFundName() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        fundBO = createFund(fundCodeEntity, "Fund-1");
        fund = createFund(fundCodeEntity, "Fund-2");
        StaticHibernateUtil.closeSession();

        fundBO = (FundBO) TestObjectFactory.getObject(FundBO.class, fundBO.getFundId());
       Assert.assertEquals("Fund-1", fundBO.getFundName());
        Assert.assertNotNull(fundBO.getFundCode());
       Assert.assertEquals(fundCodeEntity.getFundCodeValue(), fundBO.getFundCode().getFundCodeValue());
        try {
            fundBO.update(fund.getFundName());
           Assert.assertTrue(false);
        } catch (FundException fe) {
           Assert.assertTrue(true);
           Assert.assertEquals(FundConstants.DUPLICATE_FUNDNAME_EXCEPTION, fe.getKey());
        }

    }

    public void testUpdateFund() throws Exception {
        FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class,
                (short) 1);
        fundBO = createFund(fundCodeEntity, "Fund-1");
        StaticHibernateUtil.closeSession();

        fundBO = (FundBO) TestObjectFactory.getObject(FundBO.class, fundBO.getFundId());
       Assert.assertEquals("Fund-1", fundBO.getFundName());
        Assert.assertNotNull(fundBO.getFundCode());
       Assert.assertEquals(fundCodeEntity.getFundCodeValue(), fundBO.getFundCode().getFundCodeValue());
        fundBO.update("Fund-2");
        StaticHibernateUtil.commitTransaction();
       Assert.assertEquals("Fund-2", fundBO.getFundName());
        Assert.assertNotNull(fundBO.getFundCode());
       Assert.assertEquals(fundCodeEntity.getFundCodeValue(), fundBO.getFundCode().getFundCodeValue());
    }

    private FundBO createFund(FundCodeEntity fundCodeEntity, String fundName) throws Exception {
        return TestObjectFactory.createFund(fundCodeEntity, fundName);
    }
}
