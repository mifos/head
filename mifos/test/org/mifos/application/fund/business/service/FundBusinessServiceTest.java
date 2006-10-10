package org.mifos.application.fund.business.service;

import java.util.List;

import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FundBusinessServiceTest extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testGetFundCodesForInvalidConnection() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			assertEquals(5, new FundBusinessService().getFundCodes().size());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}finally {
			HibernateUtil.closeSession();
		}
	}

	public void testGetFundCodes() throws Exception {
		List<FundCodeEntity> funds = new FundBusinessService().getFundCodes();
		assertEquals(5,funds.size());
	}
	
	public void testGetSourcesOfFund() throws Exception {
		List<FundBO> funds = new FundBusinessService().getSourcesOfFund();
		assertNotNull(funds);
		assertEquals(5, funds.size());
	}
	
	public void testGetSourcesOfFundForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			new FundBusinessService().getSourcesOfFund();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}
	
	public void testGetFund() throws Exception {
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		FundBO fund = TestObjectFactory.createFund(fundCodeEntity,"Fund1");
		assertEquals("Fund1", new FundBusinessService().getFund("Fund1").getFundName());
		TestObjectFactory.removeObject(fund);
	}
	
	public void testGetFundForInvalidConnection() throws Exception {
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		FundBO fund = TestObjectFactory.createFund(fundCodeEntity,"Fund1");
		TestObjectFactory.simulateInvalidConnection();
		try {
			new FundBusinessService().getFund("Fund1").getFundName();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
		TestObjectFactory.removeObject(fund);
	}
	
	public void testGetFundById() throws Exception {
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		FundBO fund = TestObjectFactory.createFund(fundCodeEntity,"Fund1");
		HibernateUtil.closeSession();

		fund = new FundBusinessService().getFund(fund.getFundId());
		assertNotNull(fund);
		assertEquals("Fund1", fund.getFundName());
		assertEquals(1, fund.getFundCode().getFundCodeId().intValue());
		TestObjectFactory.removeObject(fund);
	}
	
	public void testGetFundByIdForInvalidConnection() throws Exception {
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		FundBO fund = TestObjectFactory.createFund(fundCodeEntity,"Fund1");
		TestObjectFactory.simulateInvalidConnection();
		try {
			new FundBusinessService().getFund(fund.getFundId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
		TestObjectFactory.removeObject(fund);
	}
}
