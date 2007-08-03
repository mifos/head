package org.mifos.application.fund.business;

import org.mifos.application.fund.exception.FundException;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FundBOTest extends MifosTestCase {
	
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
		HibernateUtil.closeSession();
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
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		try{
			fundBO = new FundBO(fundCodeEntity,null);
			assertTrue(false);
		}catch(FundException fe) {
			assertTrue(true);
			assertEquals(FundConstants.INVALID_FUND_NAME,fe.getKey());
		}
	}
	
	public void testBuildFundWithDuplicateFundName() throws Exception{
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
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
		
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		TestObjectFactory.simulateInvalidConnection();
		try {
			fundBO = new FundBO(fundCodeEntity,"Fund-1");
			fundBO.save();
			fail();
		} catch (FundException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
		
				
		
	}
	
	public void testBuildFund() throws Exception{
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		fundBO = new FundBO(fundCodeEntity,"Fund-1");
		fundBO.save();
		HibernateUtil.commitTransaction();
		
		fundBO = (FundBO) TestObjectFactory.getObject(FundBO.class,fundBO.getFundId());
		assertEquals("Fund-1", fundBO.getFundName());
		assertNotNull(fundBO.getFundCode());
		assertEquals(fundCodeEntity.getFundCodeValue(),fundBO.getFundCode().getFundCodeValue());
	}
	
	public void testUpdateFundForNullFundName() throws Exception{
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		fundBO = createFund(fundCodeEntity,"Fund-1");
		HibernateUtil.closeSession();
		
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
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		fundBO = createFund(fundCodeEntity,"Fund-1");
		fund = createFund(fundCodeEntity,"Fund-2");
		HibernateUtil.closeSession();
		
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
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		fundBO = createFund(fundCodeEntity,"Fund-1");
		HibernateUtil.closeSession();
		
		fundBO = (FundBO) TestObjectFactory.getObject(FundBO.class,fundBO.getFundId());
		assertEquals("Fund-1", fundBO.getFundName());
		assertNotNull(fundBO.getFundCode());
		assertEquals(fundCodeEntity.getFundCodeValue(),fundBO.getFundCode().getFundCodeValue());
		fundBO.update("Fund-2");
		HibernateUtil.commitTransaction();
		assertEquals("Fund-2", fundBO.getFundName());
		assertNotNull(fundBO.getFundCode());
		assertEquals(fundCodeEntity.getFundCodeValue(),fundBO.getFundCode().getFundCodeValue());
	}
	
	private FundBO createFund(FundCodeEntity fundCodeEntity, String fundName) throws Exception {
		return TestObjectFactory.createFund(fundCodeEntity,fundName);
	}
}
