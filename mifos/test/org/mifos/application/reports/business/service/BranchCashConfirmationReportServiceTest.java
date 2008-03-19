package org.mifos.application.reports.business.service;

import static org.mifos.framework.util.helpers.MoneyFactory.ZERO;

import java.util.ArrayList;
import java.util.List;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.expect;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationDisbursementBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationInfoBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationIssueBO;
import org.mifos.application.cashconfirmationreport.BranchCashConfirmationReportBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.report.branchcashconfirmation.persistence.BranchCashConfirmationReportPersistence;

public class BranchCashConfirmationReportServiceTest extends
		BranchReportTestCase {

	private BranchCashConfirmationReportPersistence persistenceMock;
	private BranchCashConfirmationReportService service;

	public void testGetCenterIssues() throws ServiceException {
		BranchCashConfirmationReportBO reportBO = new BranchCashConfirmationReportBO(
				BRANCH_ID_SHORT, RUN_DATE);
		BranchCashConfirmationInfoBO issueBO = new BranchCashConfirmationIssueBO(
				"SOME PRODUCT", ZERO);
		reportBO.addCenterIssue(issueBO);
		BranchCashConfirmationInfoBO anotherIssue = new BranchCashConfirmationIssueBO(
				"SOMEMORE", ZERO);
		reportBO.addCenterIssue(anotherIssue);
		Session session = HibernateUtil.getSessionTL();
		Transaction transaction = session.beginTransaction();
		session.save(reportBO);
		List<BranchCashConfirmationInfoBO> centerIssues = ReportServiceFactory
				.getBranchCashConfirmationReportService(null).getCenterIssues(
						BRANCH_ID, RUN_DATE_STR);
		assertNotNull(centerIssues);
		assertEquals(2, centerIssues.size());
		assertTrue(centerIssues.contains(issueBO));
		assertTrue(centerIssues.contains(anotherIssue));
		transaction.rollback();
	}

	public void testGetDisbursements() throws Exception {
		expect(persistenceMock.getDisbursements(BRANCH_ID_SHORT, RUN_DATE))
				.andReturn(
						new ArrayList<BranchCashConfirmationDisbursementBO>());
		replay(persistenceMock);
		service.getDisbursements(BRANCH_ID, RUN_DATE_STR);
		verify(persistenceMock);
	}

	@Override
	protected void setUp() throws Exception {
		persistenceMock = createMock(BranchCashConfirmationReportPersistence.class);
		service = new BranchCashConfirmationReportService(persistenceMock,
				new OfficeBusinessService());
	}
}
