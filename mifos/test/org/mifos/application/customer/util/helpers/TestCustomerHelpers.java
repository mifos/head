package org.mifos.application.customer.util.helpers;

import java.util.Locale;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.group.util.helpers.GroupSearchResults;
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

	public void testCustomerViewDefaultConstructor(){
		createCenter();
		CustomerView customerView = new CustomerView();
		customerView.setCustomerId(center.getCustomerId());
		customerView.setDisplayName(center.getDisplayName());
		customerView.setGlobalCustNum(center.getGlobalCustNum());
		customerView.setOfficeId(center.getOffice().getOfficeId());
		customerView.setStatusId(center.getStatus().getValue());
		customerView.setPersonnelId(center.getPersonnel().getPersonnelId());
		customerView.setCustomerLevelId(center.getLevel().getValue());
		customerView.setVersionNo(1);
		assertEquals(center.getCustomerId(),customerView.getCustomerId());
		assertEquals(center.getDisplayName(),customerView.getDisplayName());
		assertEquals(center.getGlobalCustNum(),customerView.getGlobalCustNum());
		assertEquals(center.getOffice().getOfficeId(),customerView.getOfficeId());
		assertEquals(center.getStatus().getValue(),customerView.getStatusId());
		assertEquals(center.getPersonnel().getPersonnelId(),customerView.getPersonnelId());
		assertEquals(center.getLevel().getValue(),customerView.getCustomerLevelId());
		assertEquals("1",customerView.getVersionNo().toString());
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

	public void testLoanCycleCounter() {
		LoanCycleCounter loanCycleCounter = new LoanCycleCounter();
		loanCycleCounter.setCounter(1);
		loanCycleCounter.setOfferingName("offeringName");
		assertEquals("value of counter", 1, loanCycleCounter.getCounter());
		assertEquals("value of offering name", "offeringName", loanCycleCounter
				.getOfferingName());
		loanCycleCounter = new LoanCycleCounter("offeringName");
		LoanCycleCounter loanCycleCounter1 = new LoanCycleCounter(
				"offeringName");
		LoanCycleCounter loanCycleCounter2 = new LoanCycleCounter(
				"offeringName1");
		assertTrue(loanCycleCounter.equals(loanCycleCounter1));
		assertFalse(loanCycleCounter.equals(loanCycleCounter2));
	}

	public void testCustomerRecentActivityView() throws Exception {
		java.sql.Date sampleDate = new java.sql.Date(System.currentTimeMillis());
		CustomerRecentActivityView customerRecentActivityView = new CustomerRecentActivityView(
				sampleDate, "description", "1000",
				"mifos");
		customerRecentActivityView.setLocale(new Locale("1"));
		assertEquals("date", sampleDate,
				customerRecentActivityView.getActivityDate());
		assertEquals("description",customerRecentActivityView.getDescription());
		assertEquals("1000",customerRecentActivityView.getAmount());
		assertEquals("mifos",customerRecentActivityView.getPostedBy());
		assertEquals("1",customerRecentActivityView.getLocale().toString());
	}

	public void testGroupSearchResults(){
		createCenter();
		GroupSearchResults groupSearchResults = new GroupSearchResults();
		groupSearchResults.setCenterName(center.getDisplayName());
		groupSearchResults.setGroupId(1);
		groupSearchResults.setGroupName("group1");
		groupSearchResults.setOfficeName(center.getOffice().getOfficeName());
		assertEquals("center name", center.getDisplayName(), groupSearchResults.getCenterName());
		assertEquals("group id", 1, groupSearchResults.getGroupId());
		assertEquals("group name", "group1", groupSearchResults.getGroupName());
		assertEquals("office name", center.getOffice().getOfficeName(), groupSearchResults.getOfficeName());
	}
	
	private void createCenter() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingForToday(1, 1, 4, 2));
		center = TestObjectFactory.createCenter("Center", meeting);
	}
}
