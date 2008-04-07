package org.mifos.framework.util.helpers;

import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.MifosTestCase;

public class MifosSelectHelperTest extends MifosTestCase {

	public void testGetInstance() {
		MifosSelectHelper mifosSelectHelper = MifosSelectHelper.getInstance();
		assertNotNull(mifosSelectHelper);
	}

	public void testGetValue() {
		MifosSelectHelper mifosSelectHelper = MifosSelectHelper.getInstance();
		String[] prdCarStatusIds = mifosSelectHelper.getValue(ProductDefinitionConstants.PRODUCTCATEGORYSTATUSID);
		assertEquals("org.mifos.application.productdefinition.business.PrdStatusEntity", prdCarStatusIds[0]);
		assertEquals(ProductDefinitionConstants.PRODUCTCATEGORYSTATUSID, prdCarStatusIds[1]);
	}

}
