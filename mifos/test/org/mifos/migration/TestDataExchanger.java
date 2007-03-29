package org.mifos.migration;

import junit.framework.TestCase;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.application.customer.center.util.helpers.CenterSearchResults;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class TestDataExchanger extends TestCase {

	private DataExchanger dataExchanger = new DataExchanger();
	
	
	@Override
	public void setUp() { 		
		DatabaseSetup.initializeHibernate();
	}
		
	public void testReadandSaveValidXML() throws Exception {
		final String CENTER_NAME = "First Center";
		final String VALID_XML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<mifosDataExchange>\n" + 
			"    <center>\n" + 
			"        <name>" + CENTER_NAME + "</name>\n" + 			
			"        <officeId>3</officeId>\n" + 
			"        <loanOfficerId>3</loanOfficerId>\n" + 
			"        <monthlyMeeting>\n" + 
			"            <meetingWeekDay>MONDAY</meetingWeekDay>\n" + 
			"            <meetingWeekDayOccurence>SECOND</meetingWeekDayOccurence>\n" + 
			"            <monthsBetweenMeetings>1</monthsBetweenMeetings>\n" + 
			"            <location>Some Place</location>\n" + 
			"        </monthlyMeeting>\n" + 
			"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
			"        <address>\n" + 
			"            <address1>100 First St.</address1>\n" + 
			"            <address2>Suite 123</address2>\n" + 
			"            <address3>Attn: someone</address3>\n" + 
			"            <cityDistrict>Any Town</cityDistrict>\n" + 
			"            <state>State</state>\n" + 
			"            <country>Country</country>\n" + 
			"            <postalCode>12345</postalCode>\n" + 
			"            <telephone>1-123-123-1234</telephone>\n" + 
			"        </address>\n" + 
			"        <distanceFromBranchOffice>0</distanceFromBranchOffice>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";
		
		CenterPersistence centerPersistence = new CenterPersistence();
		assertFalse(centerPersistence.isCenterExists(CENTER_NAME));
		
		dataExchanger.importXML(VALID_XML, TestUtils.makeUser());
		HibernateUtil.flushAndCloseSession();
		
		assertTrue(centerPersistence.isCenterExists(CENTER_NAME));
		
		QueryResult queryResult = centerPersistence.search(CENTER_NAME, (short)1);
		CenterSearchResults centerSearchResults = (CenterSearchResults) queryResult.get(0, 1).get(0);
		CenterBO center = centerPersistence.findBySystemId(centerSearchResults.getCenterSystemId());
		
		String dumpedXML = dataExchanger.exportXML(center);
		
		assertEquals(VALID_XML, dumpedXML);
		
		centerPersistence.delete(center);
		HibernateUtil.commitTransaction();
		HibernateUtil.flushAndCloseSession();
		assertFalse(centerPersistence.isCenterExists(CENTER_NAME));		
	}
}

