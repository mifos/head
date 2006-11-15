package org.mifos.application.accounts.financial.util.helpers;

import org.hibernate.Hibernate;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.COAIDMapperEntity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class ChartOfAccountsCacheTest extends MifosTestCase {

	public static final Short EXCEED_SIZE=155;

	public void testAddAndGet() throws FinancialException {
		ChartOfAccountsCache.add(createMapperEntity());

		COABO coaAssets = ChartOfAccountsCache.get(CategoryConstants.ASSETS);
		assertEquals(1, coaAssets.getCategoryId().shortValue());
	}

	public void testExceedSize() {
		try {
			ChartOfAccountsCache.add(createMapperEntity());
			ChartOfAccountsCache.get(EXCEED_SIZE);
			fail();
		} catch (FinancialException expected) {
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
