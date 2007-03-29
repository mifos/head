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

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.migration.MifosValidationEventHandler;
import org.mifos.migration.generated.Center;
import org.mifos.migration.generated.MifosDataExchange;
import org.mifos.migration.generated.MonthlyMeeting;
import org.mifos.migration.generated.WeekDayChoice;
import org.mifos.migration.generated.WeeklyMeeting;

public class TestMeetingMapper extends TestCase {
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

	public void testWeekDayMapping() {
		for (WeekDay day : WeekDay.values()) {
			if (day == WeekDay.SUNDAY) {
				try {
					MeetingMapper.mapWeekDayToWeekDayChoice(day);
					fail("Expected illegal arg exception");
				} catch (IllegalArgumentException exception) {
					assertTrue(exception.getMessage().contains("SUNDAY"));
				}
			} else {
				assertTrue(day.compareTo(MeetingMapper.mapWeekDayChoiceToWeekDay(MeetingMapper.mapWeekDayToWeekDayChoice(day))) == 0);				
			}
		}
	}

	public void testWeekDayChoiceMapping() {
		for (WeekDayChoice day : WeekDayChoice.values()) {
			assertTrue(day.compareTo(MeetingMapper.mapWeekDayToWeekDayChoice(MeetingMapper.mapWeekDayChoiceToWeekDay(day))) == 0);				
		}
	}

	public void testMonthlyMeetingMapper() throws Exception {
		final String VALID_XML = 
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
			"</mifosDataExchange>\n" + 
			"";

		StringReader stringReader = new StringReader(VALID_XML);
		StringWriter writer = new StringWriter();

		MifosDataExchange mifosDataExchange = 
			(MifosDataExchange) unmarshaller.unmarshal(stringReader);
		Center center = mifosDataExchange.getCenter().get(0);
		MonthlyMeeting monthlyMeeting = center.getMonthlyMeeting();
		// The call to createMonthly meeting requires database access -- it shouldn't!			 
		MeetingBO meetingBO = MeetingMapper.mapMonthlyMeetingToMeetingBO(monthlyMeeting);

		MonthlyMeeting newMonthlyMeeting = MeetingMapper.mapMeetingBOToMonthlyMeeting(meetingBO);

		center.setMonthlyMeeting(newMonthlyMeeting);
		marshaller.marshal(mifosDataExchange, writer);

		assertEquals(VALID_XML, writer.toString());
	}

	public void testMonthlyMeetingMapper2() throws Exception {
		final String VALID_XML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<mifosDataExchange>\n" + 
			"    <center>\n" + 
			"        <name>First Center</name>\n" + 			
			"        <officeId>3</officeId>\n" + 
			"        <loanOfficerId>1234</loanOfficerId>\n" + 
			"        <monthlyMeeting>\n" + 
			"            <dayOfMonth>2</dayOfMonth>\n" + 
			"            <monthsBetweenMeetings>1</monthsBetweenMeetings>\n" + 
			"            <location>Some Place</location>\n" + 
			"        </monthlyMeeting>\n" + 
			"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
			"        <distanceFromBranchOffice>0</distanceFromBranchOffice>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";

		StringReader stringReader = new StringReader(VALID_XML);
		StringWriter writer = new StringWriter();

		MifosDataExchange mifosDataExchange = 
			(MifosDataExchange) unmarshaller.unmarshal(stringReader);
		Center center = mifosDataExchange.getCenter().get(0);
		MonthlyMeeting monthlyMeeting = center.getMonthlyMeeting();
		// The call to createMonthly meeting requires database access -- it shouldn't!			 
		MeetingBO meetingBO = MeetingMapper.mapMonthlyMeetingToMeetingBO(monthlyMeeting);

		MonthlyMeeting newMonthlyMeeting = MeetingMapper.mapMeetingBOToMonthlyMeeting(meetingBO);

		center.setMonthlyMeeting(newMonthlyMeeting);
		marshaller.marshal(mifosDataExchange, writer);

		assertEquals(VALID_XML, writer.toString());
	}

	public void testWeeklyMeetingMapper() throws Exception {
		final String VALID_XML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<mifosDataExchange>\n" + 
			"    <center>\n" + 
			"        <name>First Center</name>\n" + 			
			"        <officeId>3</officeId>\n" + 
			"        <loanOfficerId>1234</loanOfficerId>\n" + 
			"        <weeklyMeeting>\n" + 
			"            <weeksBetweenMeetings>1</weeksBetweenMeetings>\n" + 
			"            <meetingWeekDay>MONDAY</meetingWeekDay>\n" + 
			"            <location>Some Place</location>\n" + 
			"        </weeklyMeeting>\n" + 
			"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
			"        <distanceFromBranchOffice>0</distanceFromBranchOffice>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";

		StringReader stringReader = new StringReader(VALID_XML);
		StringWriter writer = new StringWriter();

		MifosDataExchange mifosDataExchange = 
			(MifosDataExchange) unmarshaller.unmarshal(stringReader);
		Center center = mifosDataExchange.getCenter().get(0);
		WeeklyMeeting weeklyMeeting = center.getWeeklyMeeting();
		// The call to createMonthly meeting requires database access -- it shouldn't!			 
		MeetingBO meetingBO = MeetingMapper.mapWeeklyMeetingToMeetingBO(weeklyMeeting);

		WeeklyMeeting newWeeklyMeeting = MeetingMapper.mapMeetingBOToWeeklyMeeting(meetingBO);

		center.setWeeklyMeeting(newWeeklyMeeting);
		marshaller.marshal(mifosDataExchange, writer);

		assertEquals(VALID_XML, writer.toString());
	}
	
}
