package org.mifos.migration;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import junit.framework.TestCase;

import org.mifos.migration.generated.Address;
import org.mifos.migration.generated.Center;
import org.mifos.migration.generated.Fee;
import org.mifos.migration.generated.MifosDataExchange;
import org.mifos.migration.generated.MonthlyMeeting;
import org.mifos.migration.generated.WeeklyMeeting;

public class TestMifosDataExchange extends TestCase {
	private static final String GENERATED_CLASS_PACKAGE = 
		"org.mifos.migration.generated";
	private static final String MIFOS_DATA_EXCHANGE_SCHEMA_PATH = 
		"src/org/mifos/migration/schemas/generated/MifosDataExchange.xsd";

	private JAXBContext  jaxbContext;
	private Marshaller   marshaller;
	private Unmarshaller unmarshaller;
	private MifosValidationEventHandler validationEventHandler;
	
	private static final String MULTI_CENTER_VALID_XML = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
		"<mifosDataExchange>\n" + 
		"    <center>\n" + 
		"        <name>First Center</name>\n" + 
		"        <loanOfficerId>1234</loanOfficerId>\n" + 
		"        <monthlyMeeting>\n" + 
		"            <meetingWeekDay>Monday</meetingWeekDay>\n" + 
		"            <meetingWeekDayOccurence>Second</meetingWeekDayOccurence>\n" + 
		"            <monthsBetweenMeetings>1</monthsBetweenMeetings>\n" + 
		"            <location>Some Place</location>\n" + 
		"        </monthlyMeeting>\n" + 
		"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
		"        <distanceFromBranchOffice>0</distanceFromBranchOffice>\n" + 
		"    </center>\n" + 
		"    <center>\n" + 
		"        <name>First Center</name>\n" + 
		"        <loanOfficerId>1234</loanOfficerId>\n" + 
		"        <monthlyMeeting>\n" + 
		"            <dayOfMonth>2</dayOfMonth>\n" + 
		"            <monthsBetweenMeetings>1</monthsBetweenMeetings>\n" + 
		"            <location>Some Place</location>\n" + 
		"        </monthlyMeeting>\n" + 
		"        <externalId>07-01-01</externalId>\n" + 
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
		"        <meetingTime>10 AM</meetingTime>\n" + 
		"        <distanceFromBranchOffice>1</distanceFromBranchOffice>\n" + 
		"        <fee id=\"12234\">\n" + 
		"            <amount>10</amount>\n" + 
		"        </fee>\n" + 
		"    </center>\n" + 
		"    <center>\n" + 
		"        <name>First Center</name>\n" + 
		"        <loanOfficerId>1234</loanOfficerId>\n" + 
		"        <weeklyMeeting>\n" + 
		"            <weeksBetweenMeetings>1</weeksBetweenMeetings>\n" + 
		"            <meetingWeekDay>Monday</meetingWeekDay>\n" + 
		"            <location>Some Place</location>\n" + 
		"        </weeklyMeeting>\n" + 
		"        <externalId>07-01-01</externalId>\n" + 
		"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
		"        <address>\n" + 
		"            <cityDistrict>City</cityDistrict>\n" + 
		"            <country>Country</country>\n" + 
		"        </address>\n" + 
		"        <meetingTime>10 AM</meetingTime>\n" + 
		"        <distanceFromBranchOffice>2</distanceFromBranchOffice>\n" + 
		"        <fee id=\"12234\">\n" + 
		"            <amount>10</amount>\n" + 
		"        </fee>\n" + 
		"        <fee id=\"3333\">\n" + 
		"            <amount>99</amount>\n" + 
		"        </fee>\n" + 
		"    </center>\n" + 
		"</mifosDataExchange>\n" + 
		"";
	
	@Override
	public void setUp() throws Exception {
        jaxbContext = JAXBContext.newInstance( GENERATED_CLASS_PACKAGE );
        
        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
  			  Boolean.TRUE);
        
        unmarshaller = jaxbContext.createUnmarshaller();
        SchemaFactory schemaFactory =
        	SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema =
        	schemaFactory.newSchema(new File(MIFOS_DATA_EXCHANGE_SCHEMA_PATH));
        unmarshaller.setSchema(schema);  
        
        validationEventHandler = new MifosValidationEventHandler();
        unmarshaller.setEventHandler(validationEventHandler);
	}
	
	private void assignAll(MifosDataExchange fromData, MifosDataExchange toData) {
		List<Center> centers = fromData.getCenter();
		
		List<Center> toCenters = toData.getCenter();

		for (Center center : centers) {
			Center toCenter = new Center();
			toCenter.setName(center.getName());
			toCenter.setLoanOfficerId(center.getLoanOfficerId());
			
			if (center.getMonthlyMeeting() != null) {
				MonthlyMeeting meeting = center.getMonthlyMeeting();
				MonthlyMeeting toMeeting = new MonthlyMeeting();

				if (meeting.getDayOfMonth() != null) {
					toMeeting.setDayOfMonth(meeting.getDayOfMonth());
				} else {
					toMeeting.setMeetingWeekDay(meeting.getMeetingWeekDay());
					toMeeting.setMeetingWeekDayOccurence(meeting.getMeetingWeekDayOccurence());
				}
				toMeeting.setMonthsBetweenMeetings(meeting.getMonthsBetweenMeetings());
				toMeeting.setLocation(meeting.getLocation());
				
				toCenter.setMonthlyMeeting(toMeeting);
			} else if (center.getWeeklyMeeting() != null) {
				WeeklyMeeting weeklyMeeting = center.getWeeklyMeeting();
				WeeklyMeeting toWeeklyMeeting = new WeeklyMeeting();
				
				toWeeklyMeeting.setWeeksBetweenMeetings(weeklyMeeting.getWeeksBetweenMeetings());
				toWeeklyMeeting.setMeetingWeekDay(weeklyMeeting.getMeetingWeekDay());
				toWeeklyMeeting.setLocation(weeklyMeeting.getLocation());
				
				toCenter.setWeeklyMeeting(toWeeklyMeeting);
			}
			
			toCenter.setExternalId(center.getExternalId());
			toCenter.setMfiJoiningDate(center.getMfiJoiningDate());
			
			if (center.getAddress() != null) {
				Address address = center.getAddress();
				Address toAddress = new Address();
				toAddress.setAddress1(address.getAddress1());
				toAddress.setAddress2(address.getAddress2());
				toAddress.setAddress3(address.getAddress3());
				toAddress.setCityDistrict(address.getCityDistrict());
				toAddress.setState(address.getState());
				toAddress.setCountry(address.getCountry());
				toAddress.setPostalCode(address.getPostalCode());
				toAddress.setTelephone(address.getTelephone());			
				
				toCenter.setAddress(toAddress);
			}
			toCenter.setMeetingTime(center.getMeetingTime());
			toCenter.setDistanceFromBranchOffice(center.getDistanceFromBranchOffice());

			List<Fee> toFees = toCenter.getFee();
			for (Fee fee : center.getFee()) {
				Fee toFee = new Fee();
				toFee.setAmount(fee.getAmount());
				toFee.setId(fee.getId());
				toFees.add(toFee);
			}			
			toCenters.add(toCenter);
		}		
	}

	private String validationErrors() {
		return "\n" + 
			"Found " + validationEventHandler.getErrorCount() + " error(s):"
			+ validationEventHandler.getErrorString();
	}
		
	public void testReadWriteValidXML() throws Exception {
		StringReader stringReader = new StringReader(MULTI_CENTER_VALID_XML);
		StringWriter writer = new StringWriter();

		MifosDataExchange mifosDataExchange = 
			(MifosDataExchange) unmarshaller.unmarshal(stringReader);
		marshaller.marshal(mifosDataExchange, writer);
		assertEquals(MULTI_CENTER_VALID_XML, writer.toString());
	}

	public void testReadAssignWriteValidXML() throws Exception {
		StringReader stringReader = new StringReader(MULTI_CENTER_VALID_XML);
		StringWriter writer = new StringWriter();

		MifosDataExchange mifosDataExchange = 
			(MifosDataExchange) unmarshaller.unmarshal(stringReader);
		MifosDataExchange targetMifosDataExchange = new MifosDataExchange();	
		assignAll(mifosDataExchange, targetMifosDataExchange);
		marshaller.marshal(targetMifosDataExchange, writer);
		assertEquals(MULTI_CENTER_VALID_XML, writer.toString());
	}

	public void testMissingElementInValidXML() {
		final String MISSING_NAME_ELEMENT_INVALID_XML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<mifosDataExchange>\n" + 
			"    <center>\n" + 
			"        <loanOfficerId>1234</loanOfficerId>\n" + 
			"        <monthlyMeeting>\n" + 
			"            <meetingWeekDay>Monday</meetingWeekDay>\n" + 
			"            <meetingWeekDayOccurence>Second</meetingWeekDayOccurence>\n" + 
			"            <monthsBetweenMeetings>1</monthsBetweenMeetings>\n" + 
			"            <location>Some Place</location>\n" + 
			"        </monthlyMeeting>\n" + 
			"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
			"        <distanceFromBranchOffice>0</distanceFromBranchOffice>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";

		StringReader stringReader = new StringReader(MISSING_NAME_ELEMENT_INVALID_XML);

		try {
			unmarshaller.unmarshal(stringReader);
			fail("Invalid XML should have thrown an exception" + 
				validationErrors());
		}
		catch (JAXBException e) {
			assertEquals(
				"Message is cvc-complex-type.2.4.a: " +
				"Invalid content was found starting with " +
				"element 'loanOfficerId'. One of '{\"\":name}' is expected.\n" +
				"    Column is 24 at line number 4\n", 
				validationEventHandler.getErrorString());
			assertTrue(validationEventHandler.getErrorCount() == 1);
		}
	}
	
	public void testMisspelledElementInValidXML() {
		final String MISSPELLED_ELEMENT_INVALID_XML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<mifosDataExchange>\n" + 
			"    <center>\n" + 
			"        <name>First Center</name>\n" + 
			"        <loanOfficerId>1234</loanOfficerId>\n" + 
			"        <monthlyxMeeting>\n" + 
			"            <meetingWeekDay>Monday</meetingWeekDay>\n" + 
			"            <meetingWeekDayOccurence>Second</meetingWeekDayOccurence>\n" + 
			"            <monthsBetweenMeetings>1</monthsBetweenMeetings>\n" + 
			"            <location>Some Place</location>\n" + 
			"        </monthlyMeeting>\n" + 
			"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
			"        <distanceFromBranchOffice>0</distanceFromBranchOffice>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";

		StringReader stringReader = new StringReader(MISSPELLED_ELEMENT_INVALID_XML);

		try {
			unmarshaller.unmarshal(stringReader);
			fail("Invalid XML should have thrown an exception" + 
				validationErrors());
		}
		catch (JAXBException e) {
			assertEquals(
				"Message is cvc-complex-type.2.4.a: " +
				"Invalid content was found starting with element " +
				"'monthlyxMeeting'. " +
				"One of '{\"\":weeklyMeeting, \"\":monthlyMeeting}' " +
				"is expected.\n" +
				"    Column is 26 at line number 6\n", 
				validationEventHandler.getErrorString());
			assertTrue(validationEventHandler.getErrorCount() == 1);
		}
	}

	public void testBadMonthsBetweenElementInValidXML() {
		final String BAD_MONTHS_BETWEEN_INVALID_XML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<mifosDataExchange>\n" + 
			"    <center>\n" + 
			"        <name>First Center</name>\n" + 
			"        <loanOfficerId>1234</loanOfficerId>\n" + 
			"        <monthlyMeeting>\n" + 
			"            <meetingWeekDay>Monday</meetingWeekDay>\n" + 
			"            <meetingWeekDayOccurence>Second</meetingWeekDayOccurence>\n" + 
			"            <monthsBetweenMeetings>0</monthsBetweenMeetings>\n" + 
			"            <location>Some Place</location>\n" + 
			"        </monthlyMeeting>\n" + 
			"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
			"        <distanceFromBranchOffice>0</distanceFromBranchOffice>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";

		StringReader stringReader = new StringReader(BAD_MONTHS_BETWEEN_INVALID_XML);

		try {
			unmarshaller.unmarshal(stringReader);
			fail("Invalid XML should have thrown an exception" +
				validationErrors());
		}
		catch (JAXBException je) {
			assertEquals(
				"Message is cvc-minInclusive-valid: " +
				"Value '0' is not facet-valid " +
				"with respect to minInclusive '1' for type 'null'.\n" +
				"    Column is 61 at line number 9\n", 
				validationEventHandler.getErrorString());
			assertTrue(validationEventHandler.getErrorCount() == 1);
		}
	}

	public void testWeeklyAndMonthlyElementsInValidXML() {
		final String WEEKLY_AND_MONTHLY_ELEMENTS_INVALID_XML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<mifosDataExchange>\n" + 
			"    <center>\n" + 
			"        <name>First Center</name>\n" + 
			"        <loanOfficerId>1234</loanOfficerId>\n" + 
			"        <monthlyMeeting>\n" + 
			"            <meetingWeekDay>Monday</meetingWeekDay>\n" + 
			"            <meetingWeekDayOccurence>Second</meetingWeekDayOccurence>\n" + 
			"            <monthsBetweenMeetings>1</monthsBetweenMeetings>\n" + 
			"            <location>Some Place</location>\n" + 
			"        </monthlyMeeting>\n" + 
			"        <weeklyMeeting>\n" + 
			"            <weeksBetweenMeetings>1</weeksBetweenMeetings>\n" + 
			"            <meetingWeekDay>Monday</meetingWeekDay>\n" + 
			"            <location>Some Place</location>\n" + 
			"        </weeklyMeeting>\n" + 
			"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
			"        <distanceFromBranchOffice>0</distanceFromBranchOffice>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";

		StringReader stringReader = new StringReader(WEEKLY_AND_MONTHLY_ELEMENTS_INVALID_XML);

		try {
			unmarshaller.unmarshal(stringReader);
			fail("Invalid XML should have thrown an exception" +
				validationErrors());
		}
		catch (JAXBException je) {
			assertEquals(
				"Message is cvc-complex-type.2.4.a: " +
				"Invalid content was found starting with " +
				"element 'weeklyMeeting'. " +
				"One of '{\"\":externalId, \"\":mfiJoiningDate}' " +
				"is expected.\n" +
				"    Column is 24 at line number 12\n", 
				validationEventHandler.getErrorString());
			assertTrue(validationEventHandler.getErrorCount() == 1);
		}
	}
	
}
