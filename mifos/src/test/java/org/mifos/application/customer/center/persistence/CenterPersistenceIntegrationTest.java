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
 
package org.mifos.application.customer.center.persistence;

import java.util.Date;
import java.util.GregorianCalendar;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.CenterTemplate;
import org.mifos.application.customer.center.CenterTemplateImpl;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.meeting.MeetingTemplateImpl;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeTemplate;
import org.mifos.application.office.business.OfficeTemplateImpl;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterPersistenceIntegrationTest extends MifosIntegrationTest{
	public CenterPersistenceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private CustomerBO center;
    private OfficePersistence officePersistence;
    private CenterPersistence centerPersistence;

    @Override
	public void setUp() throws Exception {
        super.setUp();
        officePersistence = new OfficePersistence();
        centerPersistence = new CenterPersistence();
        initializeStatisticsService();
    }

    @Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(center);
		StaticHibernateUtil.closeSession();
		super.tearDown();
    }

    public void testCreateCenter()
            throws PersistenceException, OfficeException,
            MeetingException, CustomerException, ValidationException {
        UserContext userContext = TestUtils.makeUser();
        long beforeTransactionCount = getStatisticsService().getSuccessfulTransactionCount();
        OfficeTemplate template =
                OfficeTemplateImpl.createNonUniqueOfficeTemplate(OfficeLevel.BRANCHOFFICE);
        try {
            OfficeBO office = getOfficePersistence().createOffice(userContext, template);
            MeetingBO meeting = new MeetingBO(MeetingTemplateImpl.createWeeklyMeetingTemplate());
            CenterTemplate centerTemplate = new CenterTemplateImpl(meeting, office.getOfficeId());
            CenterBO center = getCenterPersistence().createCenter(userContext, centerTemplate);

            assertNotNull(center.getCustomerId());
            assertTrue(center.isActive());
        }
        finally {
            StaticHibernateUtil.rollbackTransaction();
        }
        long afterTransactionCount = getStatisticsService().getSuccessfulTransactionCount();
        assertEquals(beforeTransactionCount, afterTransactionCount);
    }

    public void testIsCenterExists_true() throws Exception {
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
		StaticHibernateUtil.closeSession();
		assertTrue(new CenterPersistence().isCenterExists(centerName));
	}
	
	public void testIsCenterExists_false() throws PersistenceException {
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
		StaticHibernateUtil.closeSession();
		assertFalse(new CenterPersistence().isCenterExists("NewCenter11"));
	}
	
	public void testGetCenter() throws Exception {
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
		StaticHibernateUtil.closeSession();
		center = new CenterPersistence().getCenter(center.getCustomerId());
		assertEquals(centerName, center.getDisplayName());
	}
	
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		meeting.setMeetingStartDate(new Date());
		return meeting;
	}
	public void testSearch() throws Exception{
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
	   QueryResult queryResult=	new CenterPersistence().search(center.getDisplayName(),Short.valueOf("1"));
	   assertNotNull(queryResult);
	   assertEquals(1,queryResult.getSize());
	   assertEquals(1,queryResult.get(0,10).size());
	}

    OfficePersistence getOfficePersistence() {
        return officePersistence;
    }

    CenterPersistence getCenterPersistence() {
        return centerPersistence;
    }
}
