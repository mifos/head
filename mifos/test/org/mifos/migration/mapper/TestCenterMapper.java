package org.mifos.migration.mapper;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import junit.framework.TestCase;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.migration.MifosValidationEventHandler;
import org.mifos.migration.generated.Center;
import org.mifos.migration.generated.MifosDataExchange;
import org.mifos.migration.generated.MonthlyMeeting;
import org.mifos.migration.generated.WeekDayChoice;
import org.mifos.migration.generated.WeeklyMeeting;

public class TestCenterMapper extends TestCase {
	private static final String GENERATED_CLASS_PACKAGE = "org.mifos.migration.generated";
	private static final String MIFOS_DATA_EXCHANGE_SCHEMA_PATH = "src/org/mifos/migration/schemas/generated/MifosDataExchange.xsd";

	private JAXBContext  jaxbContext;
	private Marshaller   marshaller;
	private Unmarshaller unmarshaller;
	private MifosValidationEventHandler validationEventHandler;
	
	@Override
	public void setUp() throws Exception {
		// We really don't need or want to hit the database for these tests, 
		// but creating a MeetingBO object requires it.
		DatabaseSetup.initializeHibernate();

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

/*	TODO: add additional test cases for other combinations of center values
 * 		  and for fees and custom fields
	private static final String MULTI_CENTER_VALID_XML = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
		"<mifosDataExchange>\n" + 
		"    <center>\n" + 
		"        <name>First Center</name>\n" + 
		"        <officeId>3</officeId>\n" + 
		"        <loanOfficerId>1234</loanOfficerId>\n" + 
		"        <monthlyMeeting>\n" + 
		"            <meetingWeekDay>MONDAY</meetingWeekDay>\n" + 
		"            <meetingWeekDayOccurence>SECOND</meetingWeekDayOccurence>\n" + 
		"            <monthsBetweenMeetings>1</monthsBetweenMeetings>\n" + 
		"            <location>Some Place</location>\n" + 
		"        </monthlyMeeting>\n" + 
		"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
		"        <distanceFromBranchOffice>0</distanceFromBranchOffice>\n" + 
		"    </center>\n" + 
		"    <center>\n" + 
		"        <name>First Center</name>\n" + 
		"        <officeId>3</officeId>\n" + 
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
		"        <officeId>3</officeId>\n" + 
		"        <loanOfficerId>1234</loanOfficerId>\n" + 
		"        <weeklyMeeting>\n" + 
		"            <weeksBetweenMeetings>1</weeksBetweenMeetings>\n" + 
		"            <meetingWeekDay>MONDAY</meetingWeekDay>\n" + 
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

 */	
	public void testBasicCenter() throws Exception {
		final String VALID_XML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<mifosDataExchange>\n" + 
			"    <center>\n" + 
			"        <name>Center 1</name>\n" + 			
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

		StringReader stringReader = new StringReader(VALID_XML);
		StringWriter writer = new StringWriter();

		MifosDataExchange mifosDataExchange = 
			(MifosDataExchange) unmarshaller.unmarshal(stringReader);
		Center center = mifosDataExchange.getCenter().get(0);

		CenterBO centerBO = CenterMapper.mapCenterToCenterBO(center, TestUtils.makeUser());
		
		Center newCenter = CenterMapper.mapCenterBOToCenter(centerBO);
		
		mifosDataExchange.getCenter().clear();
		mifosDataExchange.getCenter().add(newCenter);
		
		marshaller.marshal(mifosDataExchange, writer);

		assertEquals(VALID_XML, writer.toString());
	}
	
}
