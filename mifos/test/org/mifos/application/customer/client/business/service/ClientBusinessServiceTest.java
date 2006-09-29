package org.mifos.application.customer.client.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
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
		HibernateUtil.closeSession();
		super.tearDown();		
	}
	
	public void testFailureRetrieveOfferings() throws Exception {
		savingsOffering1 = createSavingsOffering("Offering1","s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
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
		savingsOffering1 = createSavingsOffering("Offering1","s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		savingsOffering2 = createSavingsOffering("Offering2","s2", SavingsType.VOLUNTARY, PrdApplicableMaster.CLIENTS);
		savingsOffering3 = createSavingsOffering("Offering3","s3", SavingsType.MANDATORY, PrdApplicableMaster.GROUPS);
		savingsOffering4 = createSavingsOffering("Offering4","s4", SavingsType.VOLUNTARY, PrdApplicableMaster.CENTERS);
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

	private SavingsOfferingBO createSavingsOffering(String offeringName, String shortName, 
			SavingsType savingsTypeId,PrdApplicableMaster applicableTo) {
		MeetingBO meetingIntCalc = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		MeetingBO meetingIntPost = TestObjectFactory
				.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory
				.createSavingsOffering(offeringName, shortName, applicableTo.getValue(),
						new Date(System.currentTimeMillis()), Short
								.valueOf("2"), 300.0, Short.valueOf("1"), 24.0,
						200.0, 200.0, savingsTypeId.getValue(), InterestCalcType.MINIMUM_BALANCE.getValue(),
						meetingIntCalc, meetingIntPost);
	}

}
