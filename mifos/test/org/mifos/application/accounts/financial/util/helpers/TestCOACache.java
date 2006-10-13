package org.mifos.application.accounts.financial.util.helpers;

import org.hibernate.Hibernate;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.COAIDMapperEntity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestConstants;

public class TestCOACache extends MifosTestCase {

	public void testCOACache() throws FinancialException {

		COACache.addToCache(createMapperEntity());

		COABO coaAssets = COACache.getCOA(CategoryConstants.ASSETS);
		assertEquals(coaAssets.getCategoryId().shortValue(), 1);

	}

	public void testCOACacheException() {
		try {
			COACache.addToCache(createMapperEntity());
			COACache.getCOA(TestConstants.COACACHE_EXCEED_SIZE);
			assertFalse(true);
		} catch (FinancialException fin) {
			assertTrue(true);
		}

	}

	private COAIDMapperEntity createMapperEntity() {

		COABO coaAssets = (COABO) HibernateUtil.getSessionTL().get(COABO.class,
				new Short("1"));
		Hibernate.initialize(coaAssets);
		Hibernate.initialize(coaAssets.getCOAHead());
		Hibernate.initialize(coaAssets.getAssociatedGlcode());
		Hibernate.initialize(coaAssets.getSubCategory());
		COAIDMapperEntity mapperEntity = new COAIDMapperEntity(
				CategoryConstants.ASSETS, coaAssets);
		return mapperEntity;

	}

}
