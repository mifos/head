package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.application.branchreport.BranchReportBO;
import org.mifos.application.branchreport.BranchReportBOFixture;
import org.mifos.application.branchreport.BranchReportStaffingLevelSummaryBO;
import org.mifos.application.reports.business.service.BranchReportService;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;

public class BranchReportStaffingLevelSummaryHelperTest extends MifosTestCase {
	public static final Short BRANCH_ID = Short.valueOf("2");

	public void testPopulateStaffingLevelSummary() throws BatchJobException,
			ServiceException {
		BranchReportBO branchReport = BranchReportBOFixture.createBranchReport(
				Integer.valueOf(1), BRANCH_ID, DateUtils.currentDate());
		new BranchReportStaffingLevelSummaryHelper(branchReport,
				new BranchReportService()).populateStaffingLevelSummary();
		assertStaffingLevelSummaries(branchReport);
	}

	private void assertStaffingLevelSummaries(BranchReportBO branchReport)
			throws ServiceException {
		Set<BranchReportStaffingLevelSummaryBO> staffingLevelSummaries = branchReport
				.getStaffingLevelSummaries();
		List<RoleBO> roles = new RolesPermissionsBusinessService().getRoles();
		roles.add(new RoleBO() {
			@Override
			public String getName() {
				return BranchReportStaffingLevelSummaryBO.TOTAL_STAFF_ROLE_NAME;
			}
		});
		assertEquals(roles.size(), staffingLevelSummaries.size());
		ArrayList<String> retrievedRolenames = new ArrayList<String>();
		for (BranchReportStaffingLevelSummaryBO summaryBO : staffingLevelSummaries) {
			retrievedRolenames.add(summaryBO.getRolename());
		}
		for (RoleBO role : roles) {
			assertTrue(role.getName() + " not found", retrievedRolenames
					.contains(role.getName()));
		}
		
	}
}
