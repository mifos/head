package org.mifos.application.productsmix.business.service;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;

import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class ProductMixBusinessServiceTest  extends MifosTestCase {
	
	private SavingsOfferingBO savingsOffering;
	private LoanOfferingBO loanOffering;
	private CustomerBO center;	
	private ProductMixBusinessService service;
	MeetingBO meetingIntPost;
	MeetingBO meetingIntCalc;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createSavingProduct();
		service = (ProductMixBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.PrdMix);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(loanOffering);
		TestObjectFactory.removeObject(savingsOffering);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetBusinessObject() throws ServiceException {
		assertNull(service.getBusinessObject(null));
	}
	
	public void testGetAllPrdOfferingsByType_Success() throws ServiceException {
		assertEquals(1, service.getAllPrdOfferingsByType(
				ProductType.SAVINGS.getValue().toString()).size());
		HibernateUtil.closeSession();

	}
	public void testGetAllowedPrdOfferingsForMixProduct_Success()
			throws ServiceException {
		assertEquals(1, service.getAllowedPrdOfferingsForMixProduct(
				savingsOffering.getPrdOfferingId().toString(),
				ProductType.SAVINGS.getValue().toString()).size());
		HibernateUtil.closeSession();
	}

	public void testGetAllPrdOfferingsByType_failure() throws ServiceException {
		assertEquals(0, service.getAllPrdOfferingsByType(
				ProductType.LOAN.getValue().toString()).size());
		HibernateUtil.closeSession();
	}
	
	
	public void testGetAllowedPrdOfferingsByType() throws ServiceException {
		assertEquals(1, service.getAllowedPrdOfferingsByType(
				savingsOffering.getPrdOfferingId().toString(),
				ProductType.SAVINGS.getValue().toString()).size());
		HibernateUtil.closeSession();

	}

	
	public void testGetNotAllowedPrdOfferingsForMixProduct()
			throws ServiceException {
		assertEquals(0, service.getNotAllowedPrdOfferingsForMixProduct(
				savingsOffering.getPrdOfferingId().toString(),
				ProductType.SAVINGS.getValue().toString()).size());

	}
	
	
	public void testGetNotAllowedPrdOfferingsByType_success() throws ServiceException {
		assertEquals(0, service.getNotAllowedPrdOfferingsByType(savingsOffering.getPrdOfferingId().toString()).size());
		HibernateUtil.closeSession();
	}

	private CenterBO createCenter() {
		return createCenter("Center_Active_test");
	}
	private CenterBO createCenter(String name) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getNewMeetingForToday(WEEKLY, EVERY_WEEK, CUSTOMER_MEETING));
		return TestObjectFactory.createCenter(name, meeting);
	}
	private void createSavingProduct() {
	Date startDate = new Date(System.currentTimeMillis());

	meetingIntCalc = TestObjectFactory
			.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
					EVERY_WEEK, CUSTOMER_MEETING));
	meetingIntPost = TestObjectFactory
			.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
					EVERY_WEEK, CUSTOMER_MEETING));

	center = createCenter();
	savingsOffering = 
		TestObjectFactory.createSavingsProduct("SavingPrd1", "S",
			startDate,
			meetingIntCalc, meetingIntPost);

	}

}
