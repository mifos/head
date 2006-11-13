package org.mifos.application.customer.client.business.service;

import java.util.List;

import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientBusinessServiceTest extends MifosTestCase{

	private SavingsOfferingBO savingsOffering1;
	private SavingsOfferingBO savingsOffering2;
	private SavingsOfferingBO savingsOffering3;
	private SavingsOfferingBO savingsOffering4;
	private ClientBusinessService service;
	private ClientBO client;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = (ClientBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Client);
	}
	
	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(savingsOffering1);
		TestObjectFactory.removeObject(savingsOffering2);
		TestObjectFactory.removeObject(savingsOffering3);
		TestObjectFactory.removeObject(savingsOffering4);
		TestObjectFactory.cleanUp(client);
		HibernateUtil.closeSession();
		super.tearDown();		
	}
	
	public void testGetClient()throws Exception{
		client = createClient("abc");
		HibernateUtil.closeSession();
		client = service.getClient(client.getCustomerId());
		assertNotNull(client);
		assertEquals("abc", client.getClientName().getName().getFirstName());
		assertEquals("abc", client.getClientName().getName().getLastName());
	}
	
	public void testFailureGetClient() throws Exception {
		client = createClient("abc");
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			client = service.getClient(client.getCustomerId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}
	
	public void testFailureRetrieveOfferings() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsOffering("Offering1","s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.retrieveOfferingsApplicableToClient();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}
	
	public void testRetrieveOfferingsApplicableToClient()throws Exception{
		savingsOffering1 = TestObjectFactory.createSavingsOffering("Offering1","s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		savingsOffering2 = TestObjectFactory.createSavingsOffering("Offering2","s2", SavingsType.VOLUNTARY, PrdApplicableMaster.CLIENTS);
		savingsOffering3 = TestObjectFactory.createSavingsOffering("Offering3","s3", SavingsType.MANDATORY, PrdApplicableMaster.GROUPS);
		savingsOffering4 = TestObjectFactory.createSavingsOffering("Offering4","s4", SavingsType.VOLUNTARY, PrdApplicableMaster.CENTERS);
		HibernateUtil.closeSession();
		List<SavingsOfferingBO> offerings = service.retrieveOfferingsApplicableToClient();
		assertEquals(2,offerings.size());
		for(SavingsOfferingBO offering: offerings){
			if(offering.getPrdOfferingId().equals(savingsOffering1.getPrdOfferingId()))
				assertTrue(true);
			if(offering.getPrdOfferingId().equals(savingsOffering2.getPrdOfferingId()))
				assertTrue(true);
		}
		HibernateUtil.closeSession();
	}
	
	private ClientBO createClient(String clientName){
		return TestObjectFactory.createClient(clientName, null, 
			CustomerStatus.CLIENT_PARTIAL);
	}
}
