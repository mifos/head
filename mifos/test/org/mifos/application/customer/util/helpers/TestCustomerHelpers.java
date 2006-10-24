package org.mifos.application.customer.util.helpers;

import java.sql.Date;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCustomerHelpers extends MifosTestCase {

	private CustomerBO center;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testCustomerPositionView() {
		createCenter();
		CustomerPositionView customerPositionView = new CustomerPositionView();
		customerPositionView.setCustomerId(center.getCustomerId());
		customerPositionView.setCustomerName(center.getDisplayName());
		customerPositionView.setPositionId(1);
		customerPositionView.setPositionName("position");
		assertEquals(center.getCustomerId(), customerPositionView
				.getCustomerId());
		assertEquals(center.getDisplayName(), customerPositionView
				.getCustomerName());
		assertEquals(Integer.valueOf(1), customerPositionView.getPositionId());
		assertEquals("position", customerPositionView.getPositionName());
	}

	public void testCustomerView() {
		createCenter();
		CustomerView customerView = new CustomerView(center.getCustomerId(),
				center.getDisplayName(), center.getGlobalCustNum(), center
						.getStatus().getValue());
		assertEquals(center.getCustomerId(), customerView.getCustomerId());
		assertEquals(center.getDisplayName(), customerView.getDisplayName());
		assertEquals(center.getGlobalCustNum(), customerView.getGlobalCustNum());
		assertEquals(center.getStatus().getValue(), customerView.getStatusId());
		customerView = new CustomerView(center.getCustomerId(), center
				.getDisplayName(), center.getGlobalCustNum(), center
				.getStatus().getValue(), center.getLevel().getValue(), center
				.getVersionNo(), center.getOffice().getOfficeId(), center
				.getPersonnel().getPersonnelId());
		assertEquals(center.getCustomerId(), customerView.getCustomerId());
		assertEquals(center.getDisplayName(), customerView.getDisplayName());
		assertEquals(center.getGlobalCustNum(), customerView.getGlobalCustNum());
		assertEquals(center.getStatus().getValue(), customerView.getStatusId());
		assertEquals(center.getLevel().getValue(), customerView
				.getCustomerLevelId());
		assertEquals(center.getVersionNo(), customerView.getVersionNo());
		assertEquals(center.getOffice().getOfficeId(), customerView
				.getOfficeId());
		assertEquals(center.getPersonnel().getPersonnelId(), customerView
				.getPersonnelId());
	}

	public void testPerformanceHistoryView() {
		PerformanceHistoryView performanceHistoryView = new PerformanceHistoryView();
		performanceHistoryView.setNumberOfClients(10);
		performanceHistoryView.setNumberOfGroups(10);
		performanceHistoryView.setTotalOutstandingPortfolio(10);
		performanceHistoryView.setTotalSavings(10);
		assertEquals(10, performanceHistoryView.getNumberOfClients());
		assertEquals(10, performanceHistoryView.getNumberOfGroups());
		assertEquals(10.0, performanceHistoryView
				.getTotalOutstandingPortfolio());
		assertEquals(10.0, performanceHistoryView.getTotalSavings());
	}

	public void testIdGenerator() {
		createCenter();
		IdGenerator idGenerator = new IdGenerator();
		assertEquals("TestBranchOffice-000000003", idGenerator
				.generateSystemId(center.getOffice().getOfficeName(), 2));
		assertEquals("TestBranchOffice-000000002", idGenerator
				.generateSystemIdForCustomer(
						center.getOffice().getOfficeName(), 2));
	}

	private void createCenter() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
	}
}
