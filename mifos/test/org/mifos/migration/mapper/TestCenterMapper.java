package org.mifos.migration.mapper;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;

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
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.migration.MifosValidationEventHandler;
import org.mifos.migration.generated.Center;
import org.mifos.migration.generated.FeeAmount;
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
		testFee1 = TestObjectFactory.createOneTimeAmountFee("testFee1",FeeCategory.CENTER,"100",FeePayment.UPFRONT, userContext);
		testFee2 = TestObjectFactory.createOneTimeAmountFee("testFee2",FeeCategory.CENTER,"33",FeePayment.UPFRONT, userContext);
		HibernateUtil.commitTransaction();
		
	}
	
	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(testFee1);
		TestObjectFactory.cleanUp(testFee2);		
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
			"        <meetingTime>10 AM</meetingTime>\n" + 
			"        <distanceFromBranchOffice>1</distanceFromBranchOffice>\n" + 
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

		StringReader stringReader = new StringReader(VALID_XML);
		StringWriter writer = new StringWriter();

		MifosDataExchange mifosDataExchange = 
			(MifosDataExchange) unmarshaller.unmarshal(stringReader);
		Center center = mifosDataExchange.getCenter().get(0);

		CenterBO centerBO = CenterMapper.mapCenterToCenterBO(center, TestUtils.makeUser());
		Center newCenter = CenterMapper.mapCenterBOToCenter(centerBO);

		sortFeeAmountsByFeeId(newCenter);
		
		mifosDataExchange.getCenter().clear();
		mifosDataExchange.getCenter().add(newCenter);
		
		marshaller.marshal(mifosDataExchange, writer);

		assertEquals(VALID_XML, writer.toString());
	}

	/* Sort the FeeAmount list by feeId to make sure we get the fees back
	 * in the same order they were created 
	 */
	private void sortFeeAmountsByFeeId(Center center) {
		final class FeeAmountFeeIdComparator implements Comparator<FeeAmount> {
			public int compare(FeeAmount fee1, FeeAmount fee2) {
				return fee1.getFeeId() - fee2.getFeeId();
			}		
		}
		Collections.sort(center.getFeeAmount(), new FeeAmountFeeIdComparator());
	}
	
}
