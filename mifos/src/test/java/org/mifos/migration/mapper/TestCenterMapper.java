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
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.migration.MifosValidationEventHandler;
import org.mifos.migration.generated.Center;
import org.mifos.migration.generated.MifosDataExchange;

public class TestCenterMapper extends TestCase {
	private static final String GENERATED_CLASS_PACKAGE = "org.mifos.migration.generated";
	private static final String MIFOS_DATA_EXCHANGE_SCHEMA_PATH = "src/org/mifos/migration/schemas/generated/MifosDataExchange.xsd";

	private JAXBContext  jaxbContext;
	private Marshaller   marshaller;
	private Unmarshaller unmarshaller;
	private MifosValidationEventHandler validationEventHandler;
	
	private FeeBO testFee1;
	private FeeBO testFee2;
	private FeeBO testFeeMonthly;
	
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
		
		HibernateUtil.getSessionTL();
		HibernateUtil.startTransaction();
		UserContext userContext = TestUtils.makeUser();
		testFee1 = TestObjectFactory.createOneTimeAmountFee("testFee1",
				FeeCategory.CENTER,"100",FeePayment.UPFRONT, userContext);
		testFee2 = TestObjectFactory.createOneTimeAmountFee("testFee2",
				FeeCategory.CENTER,"33",FeePayment.UPFRONT, userContext);
		testFeeMonthly = TestObjectFactory.createPeriodicAmountFee("testFeeMonthly", 
				FeeCategory.CENTER, "44", RecurrenceType.MONTHLY, (short)1, 
				userContext);
		HibernateUtil.commitTransaction();
		
	}
	
	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(testFee1);
		TestObjectFactory.cleanUp(testFee2);		
		TestObjectFactory.cleanUp(testFeeMonthly);		
	}

	private String singleCenterRoundTrip(String xmlString) throws Exception {
		StringReader stringReader = new StringReader(xmlString);
		StringWriter writer = new StringWriter();

		MifosDataExchange mifosDataExchange = 
			(MifosDataExchange) unmarshaller.unmarshal(stringReader);
		Center center = mifosDataExchange.getCenter().get(0);

		CenterBO centerBO = CenterMapper.mapCenterToCenterBO(center, TestUtils.makeUser());
		Center newCenter = CenterMapper.mapCenterBOToCenter(centerBO);
		
		mifosDataExchange.getCenter().clear();
		mifosDataExchange.getCenter().add(newCenter);
		
		marshaller.marshal(mifosDataExchange, writer);
		
		return writer.toString();
	}

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
			"        <customField>\n" + 
			"            <fieldId>5</fieldId>\n" + // meeting time 
			"            <stringValue>10 AM</stringValue>\n" + 
			"        </customField>\n" + 
			"        <customField>\n" + 
			"            <fieldId>6</fieldId>\n" + // distance from branch office 
			"            <numericValue>1</numericValue>\n" + 
			"        </customField>\n" + 
			"        <feeAmount>\n" +
			"            <feeId>" + testFee1.getFeeId() + "</feeId>\n" + 
			"            <amount>10.0</amount>\n" + 
			"        </feeAmount>\n" + 
			"        <feeAmount>\n" +
			"            <feeId>" + testFee2.getFeeId() + "</feeId>\n" + 
			"            <amount>99.0</amount>\n" + 
			"        </feeAmount>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";


		assertEquals(VALID_XML, singleCenterRoundTrip(VALID_XML));
	}

	public void testValidPeriodicFee() throws Exception {
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
			"        <customField>\n" + 
			"            <fieldId>5</fieldId>\n" + // meeting time 
			"            <stringValue>10 AM</stringValue>\n" + 
			"        </customField>\n" + 
			"        <customField>\n" + 
			"            <fieldId>6</fieldId>\n" + // distance from branch office 
			"            <numericValue>1</numericValue>\n" + 
			"        </customField>\n" + 
			"        <feeAmount>\n" +
			"            <feeId>" + testFeeMonthly.getFeeId() + "</feeId>\n" + 
			"            <amount>10.0</amount>\n" + 
			"        </feeAmount>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";

		assertEquals(VALID_XML, singleCenterRoundTrip(VALID_XML));
	}

	/**
	 * The meeting is weekly, but the fee is monthly, so there is a mismatch.
	 */
	public void testInValidPeriodicFee() throws Exception {
		final String VALID_XML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + 
			"<mifosDataExchange>\n" + 
			"    <center>\n" + 
			"        <name>Center 1</name>\n" + 			
			"        <officeId>3</officeId>\n" + 
			"        <loanOfficerId>3</loanOfficerId>\n" + 
			"        <weeklyMeeting>\n" + 
			"            <weeksBetweenMeetings>1</weeksBetweenMeetings>\n" + 
			"            <meetingWeekDay>MONDAY</meetingWeekDay>\n" + 
			"            <location>Some Place</location>\n" + 
			"        </weeklyMeeting>\n" + 
			"        <mfiJoiningDate>2005-12-01</mfiJoiningDate>\n" + 
			"        <customField>\n" + 
			"            <fieldId>5</fieldId>\n" + // meeting time 
			"            <stringValue>10 AM</stringValue>\n" + 
			"        </customField>\n" + 
			"        <customField>\n" + 
			"            <fieldId>6</fieldId>\n" + // distance from branch office 
			"            <numericValue>1</numericValue>\n" + 
			"        </customField>\n" + 
			"        <feeAmount>\n" +
			"            <feeId>" + testFeeMonthly.getFeeId() + "</feeId>\n" + 
			"            <amount>10.0</amount>\n" + 
			"        </feeAmount>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";

		try {
			singleCenterRoundTrip(VALID_XML);
			fail("Excpected meeting/fee periodicity mismatch error");
		} catch(Exception e) {
			assertTrue(true);
		}
	}

}
