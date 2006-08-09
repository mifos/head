package org.mifos.application.customer.center.business;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingFrequency;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterBOTest extends MifosTestCase {
	private CenterBO center;

	private Short officeId = 1;

	private Short personnel = 3;

	private MeetingBO meeting;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}

	public void testCreateWithoutName() throws Exception {
		try {
			meeting = getMeeting();
			center = new CenterBO(TestObjectFactory.getUserContext(), "", null,
					null, null,  officeId, meeting, personnel);
			assertFalse("Center Created", true);
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_NAME);
		}
		TestObjectFactory.removeObject(meeting);
	}

	public void testCreateWithoutLO() throws Exception {
		try {
			meeting = getMeeting();
			center = new CenterBO(TestObjectFactory.getUserContext(), "Center",
					null, null, null, officeId, meeting, null);
			assertFalse("Center Created", true);
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_LOAN_OFFICER);
		}
		TestObjectFactory.removeObject(meeting);
	}


	public void testCreateWithoutMeeting() throws Exception {
		try {
			center = new CenterBO(TestObjectFactory.getUserContext(), "Center",
					null, null, null, officeId, meeting, personnel);
			assertFalse("Center Created", true);
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_MEETING);
		}
	}

	public void testCreateWithoutOffice() throws Exception {
		try {
			meeting = getMeeting();
			center = new CenterBO(TestObjectFactory.getUserContext(), "Center",
					null, null, null,  null, meeting, personnel);
			assertFalse("Center Created", true);
		} catch (CustomerException ce) {
			assertNull(center);
			assertEquals(ce.getKey(), CustomerConstants.INVALID_OFFICE);
		}
		TestObjectFactory.removeObject(meeting);
	}

	public void testSuccessfulCreateWithoutFeeAndCustomField() throws Exception {
		String name = "Center1";
		meeting = getMeeting();
		center = new CenterBO(TestObjectFactory.getUserContext(), name, null,
				null, null,  officeId, meeting, personnel);
		center.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		assertEquals(name, center.getDisplayName());
		assertEquals(officeId, center.getOffice().getOfficeId());
	}

	public void testSuccessfulCreateWithoutFee() throws Exception {
		String name = "Center1";
		meeting = getMeeting();
		center = new CenterBO(TestObjectFactory.getUserContext(), name, null,
				getCustomFields(), null,  officeId, meeting,
				personnel);
		center.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		assertEquals(name, center.getDisplayName());
		assertEquals(officeId, center.getOffice().getOfficeId());
		assertEquals(2, center.getCustomFields().size());
	}

	public void testSuccessfulCreate() throws Exception {
		try{
		String name = "Center1";
		meeting = getMeeting();
		List<FeeView> fees = getFees();
		center = new CenterBO(TestObjectFactory.getUserContext(), name, null,
				getCustomFields(), fees,  officeId, meeting,
				personnel);
		center.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		center = (CenterBO) TestObjectFactory.getObject(CenterBO.class, center
				.getCustomerId());
		assertEquals(name, center.getDisplayName());
		assertEquals(officeId, center.getOffice().getOfficeId());
		assertEquals(2, center.getCustomFields().size());
		assertEquals(AccountState.CUSTOMERACCOUNT_ACTIVE.getValue(), center
				.getCustomerAccount().getAccountState().getId());
		// check if values in account fees are entered.
		assertNotNull(center.getCustomerAccount().getAccountFees(
				fees.get(0).getFeeIdValue()));
		assertNotNull(center.getCustomerAccount().getAccountFees(
				fees.get(1).getFeeIdValue()));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}

	private List<CustomFieldView> getCustomFields() {
		List<CustomFieldView> fields = new ArrayList<CustomFieldView>();
		fields.add(new CustomFieldView(Short.valueOf("5"), "value1"));
		fields.add(new CustomFieldView(Short.valueOf("6"), "value2"));
		return fields;
	}

	private List<FeeView> getFees() {
		List<FeeView> fees = new ArrayList<FeeView>();
		AmountFeeBO fee1 = (AmountFeeBO) TestObjectFactory
				.createPeriodicAmountFee("PeriodicAmountFee",
						FeeCategory.CENTER, "200", MeetingFrequency.WEEKLY,
						Short.valueOf("2"));
		AmountFeeBO fee2 = (AmountFeeBO) TestObjectFactory
				.createOneTimeAmountFee("OneTimeAmountFee",
						FeeCategory.ALLCUSTOMERS, "100", FeePayment.UPFRONT);
		fees.add(new FeeView(fee1));
		fees.add(new FeeView(fee2));
		HibernateUtil.commitTransaction();
		return fees;
	}
}
