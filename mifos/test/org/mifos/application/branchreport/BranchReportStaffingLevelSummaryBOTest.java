package org.mifos.application.branchreport;

import static org.mifos.application.branchreport.BranchReportBOFixture.*;
import static org.mifos.application.branchreport.BranchReportStaffingLevelSummaryBO.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

public class BranchReportStaffingLevelSummaryBOTest extends TestCase {

	public void testCompareToReturnsAnyBOGreaterThanTotalSummaryBO() {
		assertEquals(-1, createStaffingLevelBO(1).compareTo(
				createStaffingLevelBO(TOTAL_STAFF_ROLE_ID)));
		assertEquals(1, createStaffingLevelBO(TOTAL_STAFF_ROLE_ID).compareTo(
				createStaffingLevelBO(1)));
	}

	public void testCompareTo() throws Exception {
		assertEquals(1, createStaffingLevelBO(2).compareTo(
				createStaffingLevelBO(1)));
		assertEquals(-1, createStaffingLevelBO(1).compareTo(
				createStaffingLevelBO(2)));
		assertEquals(0, createStaffingLevelBO(1).compareTo(
				createStaffingLevelBO(1)));
	}
	
	public void testSortingBasedOnComparator() throws Exception {
		BranchReportStaffingLevelSummaryBO one = createStaffingLevelBO(1);
		BranchReportStaffingLevelSummaryBO two = createStaffingLevelBO(2);
		BranchReportStaffingLevelSummaryBO three = createStaffingLevelBO(3);
		BranchReportStaffingLevelSummaryBO total = createStaffingLevelBO(TOTAL_STAFF_ROLE_ID);
		List<BranchReportStaffingLevelSummaryBO> list = new ArrayList<BranchReportStaffingLevelSummaryBO>();
		list.add(three);
		list.add(one);
		list.add(total);
		list.add(two);
		Collections.sort(list);
		assertEquals(0, one.compareTo(list.get(0)));
		assertEquals(0, two.compareTo(list.get(1)));
		assertEquals(0, three.compareTo(list.get(2)));
		assertEquals(0, total.compareTo(list.get(3)));
	}
}
