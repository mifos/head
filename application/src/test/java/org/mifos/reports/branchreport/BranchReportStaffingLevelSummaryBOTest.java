/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.reports.branchreport;

import static org.mifos.reports.branchreport.BranchReportStaffingLevelSummaryBO.IS_TOTAL;
import static org.mifos.reports.branchreport.BranchReportStaffingLevelSummaryBO.IS_NOT_TOTAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

public class BranchReportStaffingLevelSummaryBOTest extends TestCase {

    public void testCompareToReturnsAnyBOGreaterThanTotalSummaryBO() {
        Assert.assertEquals(-1, new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "A", 1).compareTo(new BranchReportStaffingLevelSummaryBO(IS_TOTAL, "A", 1)));
        Assert.assertEquals(1, new BranchReportStaffingLevelSummaryBO(IS_TOTAL, "A", 1).compareTo(new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "A", 1)));
    }

    public void testCompareTo() throws Exception {
        Assert.assertEquals(-1, new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "A", 1).compareTo(new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "B", 1)));
        Assert.assertEquals(1, new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "B", 1).compareTo(new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "A", 1)));
        Assert.assertEquals(0, new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "A", 1).compareTo(new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "A", 1)));
    }

    public void testSortingBasedOnComparator() throws Exception {
        BranchReportStaffingLevelSummaryBO one = new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "A", 1);
        BranchReportStaffingLevelSummaryBO two = new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "B", 1);
        BranchReportStaffingLevelSummaryBO three = new BranchReportStaffingLevelSummaryBO(IS_NOT_TOTAL, "C", 1);
        BranchReportStaffingLevelSummaryBO total = new BranchReportStaffingLevelSummaryBO(IS_TOTAL, "A", 1);
        List<BranchReportStaffingLevelSummaryBO> list = new ArrayList<BranchReportStaffingLevelSummaryBO>();
        list.add(three);
        list.add(one);
        list.add(total);
        list.add(two);
        Collections.sort(list);
       Assert.assertEquals(0, one.compareTo(list.get(0)));
       Assert.assertEquals(0, two.compareTo(list.get(1)));
       Assert.assertEquals(0, three.compareTo(list.get(2)));
       Assert.assertEquals(0, total.compareTo(list.get(3)));
    }
}
