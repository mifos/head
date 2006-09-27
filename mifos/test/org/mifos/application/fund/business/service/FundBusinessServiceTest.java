package org.mifos.application.fund.business.service;

import java.util.List;

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
	
	public void testGetFundCodesForInvalidConnection() {
		TestObjectFactory.simulateInvalidConnection();
		try {
			assertEquals(5, new FundBusinessService().getFundCodes().size());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}

	public void testGetFundCodes() throws Exception {
		List<FundCodeEntity> funds = new FundBusinessService().getFundCodes();
		assertEquals(5,funds.size());
	}

}
