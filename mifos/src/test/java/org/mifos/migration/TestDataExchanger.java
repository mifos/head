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
 
package org.mifos.migration;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.application.customer.center.util.helpers.CenterSearchResults;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;

public class TestDataExchanger extends MifosIntegrationTest {
	public TestDataExchanger() throws SystemException, ApplicationException {
        super();
    }

    private DataExchanger dataExchanger = new DataExchanger();
		
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
			"        <customField>\n" + 
			"            <fieldId>5</fieldId>\n" + // meeting time 
			"            <stringValue>10 AM</stringValue>\n" + 
			"        </customField>\n" + 
			"        <customField>\n" + 
			"            <fieldId>6</fieldId>\n" + // distance from branch office 
			"            <numericValue>1</numericValue>\n" + 
			"        </customField>\n" + 
			"    </center>\n" + 
			"</mifosDataExchange>\n" + 
			"";
		
		CenterPersistence centerPersistence = new CenterPersistence();
		assertFalse(centerPersistence.isCenterExists(CENTER_NAME));
		
		dataExchanger.importXML(VALID_XML, TestUtils.makeUser());
		StaticHibernateUtil.flushAndCloseSession();
		
		assertTrue(centerPersistence.isCenterExists(CENTER_NAME));
		
		QueryResult queryResult = centerPersistence.search(CENTER_NAME, (short)1);
		CenterSearchResults centerSearchResults = (CenterSearchResults) queryResult.get(0, 1).get(0);
		CenterBO center = centerPersistence.findBySystemId(centerSearchResults.getCenterSystemId());
				
		assertEquals(VALID_XML, dataExchanger.exportXML(center));
		
		centerPersistence.delete(center);
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.flushAndCloseSession();
		assertFalse(centerPersistence.isCenterExists(CENTER_NAME));		
	}
}

