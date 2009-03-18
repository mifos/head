package org.mifos.application.productsmix.persistence;


import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productsmix.business.ProductMixBO;
import org.mifos.application.productsmix.util.ProductMixTestHelper;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ProductMixPersistenceTest extends MifosIntegrationTest {

	public ProductMixPersistenceTest() throws SystemException, ApplicationException {
        super();
    }

    MeetingBO meeting;
	MeetingBO meeting1;
	SavingsOfferingBO saving1;
	SavingsOfferingBO saving2;
	ProductMixBO prdmix;
	ProductMixPersistence productMixPersistence = new ProductMixPersistence();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(prdmix);	
		TestObjectFactory.cleanUp(saving1);
		TestObjectFactory.cleanUp(saving2);
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testGetAllProductMix() throws PersistenceException {				
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		meeting1 = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		saving1 = ProductMixTestHelper.createSavingOffering("Savings Product1", "S1",meeting,meeting);
		saving2 = ProductMixTestHelper.createSavingOffering("Savings Product2", "S2",meeting1,meeting1);
		prdmix= TestObjectFactory.createAllowedProductsMix(saving1,saving2);		
		assertEquals(1, (productMixPersistence.getAllProductMix()).size());

	}	
	
	public void testGetNotAllowedProducts() throws PersistenceException {				
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		meeting1 = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		saving1 = ProductMixTestHelper.createSavingOffering("Savings Product1", "S1",meeting,meeting);
		saving2 = ProductMixTestHelper.createSavingOffering("Savings Product2", "S2",meeting1,meeting1);
		prdmix= TestObjectFactory.createAllowedProductsMix(saving1,saving2);		
		assertEquals(1, (productMixPersistence.getNotAllowedProducts(saving1.getPrdOfferingId())).size());

	}	
}
