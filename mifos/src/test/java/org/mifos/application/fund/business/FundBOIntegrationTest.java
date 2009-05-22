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
 
package org.mifos.application.fund.business;

import org.mifos.application.fund.exception.FundException;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FundBOIntegrationTest extends MifosIntegrationTest {
	
	public FundBOIntegrationTest() throws SystemException, ApplicationException {
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
	
	public void testBuildFundWithoutFundCode() throws Exception{
		try{
			fundBO = new FundBO(null,"Fund-1");
			assertTrue(false);
		}catch(FundException fe) {
			assertTrue(true);
			assertEquals(FundConstants.INVALID_FUND_CODE,fe.getKey());
		}
	}
	
	public void testBuildFundWithoutFundName() throws Exception{
		FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		try{
			fundBO = new FundBO(fundCodeEntity,null);
			assertTrue(false);
		}catch(FundException fe) {
			assertTrue(true);
			assertEquals(FundConstants.INVALID_FUND_NAME,fe.getKey());
		}
	}
	
	public void testBuildFundWithDuplicateFundName() throws Exception{
		FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		fundBO = createFund(fundCodeEntity,"Fund-1");
		try{
			new FundBO(fundCodeEntity,"Fund-1");
			fail();
		}catch(FundException fe) {
			assertTrue(true);
			assertEquals(FundConstants.DUPLICATE_FUNDNAME_EXCEPTION,fe.getKey());
		}
	}
	
	public void testBuildFundForInvalidConnection() throws Exception{
		
		FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		TestObjectFactory.simulateInvalidConnection();
		try {
			fundBO = new FundBO(fundCodeEntity,"Fund-1");
			fundBO.save();
			fail();
		} catch (FundException e) {
			assertTrue(true);
		} finally {
			StaticHibernateUtil.closeSession();
		}
		
				
		
	}
	
	public void testBuildFund() throws Exception{
		FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		fundBO = new FundBO(fundCodeEntity,"Fund-1");
		fundBO.save();
		StaticHibernateUtil.commitTransaction();
		
		fundBO = (FundBO) TestObjectFactory.getObject(FundBO.class,fundBO.getFundId());
		assertEquals("Fund-1", fundBO.getFundName());
		assertNotNull(fundBO.getFundCode());
		assertEquals(fundCodeEntity.getFundCodeValue(),fundBO.getFundCode().getFundCodeValue());
	}
	
	public void testUpdateFundForNullFundName() throws Exception{
		FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		fundBO = createFund(fundCodeEntity,"Fund-1");
		StaticHibernateUtil.closeSession();
		
		fundBO = (FundBO) TestObjectFactory.getObject(FundBO.class,fundBO.getFundId());
		assertEquals("Fund-1", fundBO.getFundName());
		assertNotNull(fundBO.getFundCode());
		assertEquals(fundCodeEntity.getFundCodeValue(),fundBO.getFundCode().getFundCodeValue());
		try{
			fundBO.update("");
			assertTrue(false);
		}catch(FundException fe) {
			assertTrue(true);
			assertEquals(FundConstants.INVALID_FUND_NAME,fe.getKey());
		}
	}
	
	public void testUpdateFundForDuplicateFundName() throws Exception{
		FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		fundBO = createFund(fundCodeEntity,"Fund-1");
		fund = createFund(fundCodeEntity,"Fund-2");
		StaticHibernateUtil.closeSession();
		
		fundBO = (FundBO) TestObjectFactory.getObject(FundBO.class,fundBO.getFundId());
		assertEquals("Fund-1", fundBO.getFundName());
		assertNotNull(fundBO.getFundCode());
		assertEquals(fundCodeEntity.getFundCodeValue(),fundBO.getFundCode().getFundCodeValue());
		try{
			fundBO.update(fund.getFundName());
			assertTrue(false);
		}catch(FundException fe) {
			assertTrue(true);
			assertEquals(FundConstants.DUPLICATE_FUNDNAME_EXCEPTION,fe.getKey());
		}
		
	}
	
	public void testUpdateFund() throws Exception{
		FundCodeEntity fundCodeEntity = (FundCodeEntity) StaticHibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		fundBO = createFund(fundCodeEntity,"Fund-1");
		StaticHibernateUtil.closeSession();
		
		fundBO = (FundBO) TestObjectFactory.getObject(FundBO.class,fundBO.getFundId());
		assertEquals("Fund-1", fundBO.getFundName());
		assertNotNull(fundBO.getFundCode());
		assertEquals(fundCodeEntity.getFundCodeValue(),fundBO.getFundCode().getFundCodeValue());
		fundBO.update("Fund-2");
		StaticHibernateUtil.commitTransaction();
		assertEquals("Fund-2", fundBO.getFundName());
		assertNotNull(fundBO.getFundCode());
		assertEquals(fundCodeEntity.getFundCodeValue(),fundBO.getFundCode().getFundCodeValue());
	}
	
	private FundBO createFund(FundCodeEntity fundCodeEntity, String fundName) throws Exception {
		return TestObjectFactory.createFund(fundCodeEntity,fundName);
	}
}
