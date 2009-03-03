package org.mifos.application.productsmix.business;

import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ProductMixBOTest extends MifosIntegrationTest {
	

	public ProductMixBOTest() throws SystemException, ApplicationException {
        super();
    }

    private SavingsOfferingBO savingsOffering;
	private SavingsTestHelper helper = new SavingsTestHelper();
	private ProductMixBO prdMix; 
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		savingsOffering = helper.createSavingsOffering("Eddikhar", "Edd");
	}

	@Override
	protected void tearDown() throws Exception {

		TestObjectFactory.removeObject(prdMix);
		TestObjectFactory.removeObject(savingsOffering);
		
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	
	public void testUpdate() throws PersistenceException, ProductDefinitionException {
		prdMix = new ProductMixBO(savingsOffering,savingsOffering);
		prdMix.update();
		assertEquals(savingsOffering.getPrdOfferingId(),prdMix.getPrdOfferingId().getPrdOfferingId());
		assertEquals(savingsOffering.getPrdOfferingId(),prdMix.getPrdOfferingNotAllowedId().getPrdOfferingId());
	}
	
	public void testDelete() throws PersistenceException, ProductDefinitionException {
		prdMix = new ProductMixBO(savingsOffering,savingsOffering);
		prdMix.update();		
		assertEquals(savingsOffering.getPrdOfferingId(),prdMix.getPrdOfferingId().getPrdOfferingId());
		prdMix.delete();
		
	}
}
