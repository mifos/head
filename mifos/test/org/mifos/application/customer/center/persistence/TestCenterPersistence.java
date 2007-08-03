package org.mifos.application.customer.center.persistence;

import java.util.GregorianCalendar;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.CenterTemplate;
import org.mifos.application.customer.center.CenterTemplateImpl;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.MeetingTemplateImpl;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeTemplate;
import org.mifos.application.office.business.OfficeTemplateImpl;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCenterPersistence extends MifosTestCase{
	private CustomerBO center;
    private OfficePersistence officePersistence;
    private CenterPersistence centerPersistence;

    @Override
	public void setUp() throws Exception {
        officePersistence = new OfficePersistence();
        centerPersistence = new CenterPersistence();
        initializeStatisticsService();
        super.setUp();
    }

    @Override
	public void tearDown() {
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
    }

    public void testCreateCenter()
            throws PersistenceException, OfficeException,
            MeetingException, CustomerException, ValidationException {
        UserContext userContext = TestUtils.makeUser();
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
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
            HibernateUtil.rollbackTransaction();
        }
        assertTrue(transactionCount == getStatisticsService().getSuccessfulTransactionCount());
    }

    public void testIsCenterExists_true() throws Exception {
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
		HibernateUtil.closeSession();
		assertTrue(new CenterPersistence().isCenterExists(centerName));
	}
	
	public void testIsCenterExists_false() throws PersistenceException {
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
		HibernateUtil.closeSession();
		assertFalse(new CenterPersistence().isCenterExists("NewCenter11"));
	}
	
	public void testGetCenter() throws Exception {
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,getMeeting());
		HibernateUtil.closeSession();
		center = new CenterPersistence().getCenter(center.getCustomerId());
		assertEquals(centerName, center.getDisplayName());
	}
	
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		meeting.setMeetingStartDate(new GregorianCalendar());
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
