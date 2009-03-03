package org.mifos.application.fund.persistence;

import java.util.List;

import org.mifos.application.fund.business.FundBO;
import org.mifos.application.master.business.FundCodeEntity;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class FundPersistenceTest extends MifosIntegrationTest {

	public FundPersistenceTest() throws SystemException, ApplicationException {
        super();
    }

    public void testGetFundNameCountWithoutData() throws PersistenceException {
		assertEquals(Long.valueOf("0"), new FundPersistence().getFundNameCount("Fund1"));
	}

	public void testGetFundNameCountWithDifferentName()	throws Exception {
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		FundBO fund = TestObjectFactory.createFund(fundCodeEntity,"Fund1");
		assertEquals(Long.valueOf("0"),new FundPersistence().getFundNameCount("Fund2"));
		TestObjectFactory.removeObject(fund);

	}

	public void testGetFundNameCountWithSameName() throws Exception {
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		FundBO fund = TestObjectFactory.createFund(fundCodeEntity,"Fund1");
		assertEquals(Long.valueOf("1"), new FundPersistence().getFundNameCount("Fund1"));
		TestObjectFactory.removeObject(fund);
	}
	
	public void testGetFund() throws Exception {
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		FundBO fund = TestObjectFactory.createFund(fundCodeEntity,"Fund1");
		assertEquals("Fund1", new FundPersistence().getFund("Fund1").getFundName());
		TestObjectFactory.removeObject(fund);
	}
	
	public void testGetFundCodes() throws Exception{
		List<FundCodeEntity> funds = new FundPersistence().getFundCodes();
		assertEquals(5,funds.size());
	}
	
	public void testGetSourcesOfFund() throws Exception {
		List<FundBO> funds = new FundPersistence().getSourcesOfFund();
		assertNotNull(funds);
		assertEquals(5, funds.size());
	}
	
	public void testGetFundById() throws Exception {
		FundCodeEntity fundCodeEntity = (FundCodeEntity) HibernateUtil.getSessionTL().get(FundCodeEntity.class, (short) 1);
		FundBO fund = TestObjectFactory.createFund(fundCodeEntity,"Fund1");
		HibernateUtil.closeSession();

		fund = new FundPersistence().getFund(fund.getFundId());
		assertNotNull(fund);
		assertEquals("Fund1", fund.getFundName());
		assertEquals(1, fund.getFundCode().getFundCodeId().intValue());
		TestObjectFactory.removeObject(fund);
	}
}
