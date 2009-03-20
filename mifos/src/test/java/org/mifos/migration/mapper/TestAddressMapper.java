/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
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

import org.mifos.migration.MifosValidationEventHandler;
import org.mifos.migration.generated.Address;
import org.mifos.migration.generated.Center;
import org.mifos.migration.generated.MifosDataExchange;

public class TestAddressMapper extends TestCase {
	private static final String GENERATED_CLASS_PACKAGE = "org.mifos.migration.generated";
	private static final String MIFOS_DATA_EXCHANGE_SCHEMA_PATH = "src/org/mifos/migration/schemas/generated/MifosDataExchange.xsd";

	private JAXBContext  jaxbContext;
	private Marshaller   marshaller;
	private Unmarshaller unmarshaller;
	private MifosValidationEventHandler validationEventHandler;
	
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

	private String roundTripAddressMap(String xml) throws Exception {
		StringReader stringReader = new StringReader(xml);
		StringWriter writer = new StringWriter();

		MifosDataExchange mifosDataExchange = 
			(MifosDataExchange) unmarshaller.unmarshal(stringReader);
		Center center = mifosDataExchange.getCenter().get(0);
		Address address = center.getAddress();
		org.mifos.framework.business.util.Address mifosAddress = AddressMapper.mapXMLAddressToMifosAddress(address);
		Address newAddress = AddressMapper.mapMifosAddressToXMLAddress(mifosAddress);

		center.setAddress(newAddress);
		marshaller.marshal(mifosDataExchange, writer);

		return writer.toString();
		
	}
	
	private static String BEFORE_ADDRESS_XML_FRAGMENT =
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
		"";

	private static String AFTER_ADDRESS_XML_FRAGMENT = 
		"        <customField>\n" + 
		"            <fieldId>6</fieldId>\n" + // distance from branch office 
		"            <numericValue>1</numericValue>\n" + 
		"        </customField>\n" + 
		"    </center>\n" + 
		"</mifosDataExchange>\n" + 
		"";
	
	public void testFullAddress() throws Exception {
		final String VALID_XML =
			BEFORE_ADDRESS_XML_FRAGMENT +
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
			AFTER_ADDRESS_XML_FRAGMENT;
		
		assertEquals(VALID_XML, roundTripAddressMap(VALID_XML));
	}

	public void testPartialAddress() throws Exception {
		final String VALID_XML = 
			BEFORE_ADDRESS_XML_FRAGMENT +
			"        <address>\n" + 
			"            <cityDistrict>Any Town</cityDistrict>\n" + 
			"            <state>State</state>\n" + 
			"            <country>Country</country>\n" + 
			"            <postalCode>12345</postalCode>\n" + 
			"        </address>\n" + 
			AFTER_ADDRESS_XML_FRAGMENT;

		assertEquals(VALID_XML, roundTripAddressMap(VALID_XML));
	}

	public void testNoAddress() throws Exception {
		final String VALID_XML = 
			BEFORE_ADDRESS_XML_FRAGMENT +
			AFTER_ADDRESS_XML_FRAGMENT;

		assertEquals(VALID_XML, roundTripAddressMap(VALID_XML));
	}
	
}

