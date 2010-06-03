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

package org.mifos.customers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.domain.builders.PersonnelBuilder;

/**
 *
 */
public class GroupTransferToCenterTest {

    @Test
    public void transferingGroupToCenterInDifferentBranchCreatesNewCustomerMovementsForGroupAndChangesGroupStatusFromActiveToOnHold() throws Exception {

        // setup
        OfficeBO fromBranch = new OfficeBuilder().branchOffice().withName("fromBranch").withGlobalOfficeNum("xxx1").build();
        CenterBO fromCenter = new CenterBuilder().withName("fromCenter").with(fromBranch).build();
        GroupBO groupForTransfer = new GroupBuilder().withParentCustomer(fromCenter).active().build();

        OfficeBO receivingBranch = new OfficeBuilder().branchOffice().withName("receivingBranch").withGlobalOfficeNum("xxx2").build();
        CenterBO receivingCenter = new CenterBuilder().withName("receivingCenter").with(receivingBranch).build();

        // exercise test
        groupForTransfer.transferTo(receivingCenter);

        // verification
        assertThat(groupForTransfer.getStatus().isGroupActive(), is(false));
        assertThat(groupForTransfer.getStatus().isGroupOnHold(), is(true));
        assertThat(groupForTransfer.countOfCustomerMovements(), is(2));
    }

    @Test
    public void transferingGroupToCenterInSameBranchIncrementsMaxChildCountOfNewParentAndDoesNotDecrementsMaxChildCountOfOldParent() throws Exception {

        CenterBO fromCenter = new CenterBuilder().withName("fromCenter").build();
        GroupBO groupForTransfer = new GroupBuilder().withParentCustomer(fromCenter).active().build();
        fromCenter.addChild(groupForTransfer);

        CenterBO centerWithNoChildren = new CenterBuilder().withName("receivingCenter").build();

        // pre-verification
        assertThat(centerWithNoChildren.getMaxChildCount(), is(0));
        assertThat(centerWithNoChildren.getChildren().size(), is(0));
        assertThat(groupForTransfer.getParentCustomer().getMaxChildCount(), is(1));

        // exercise test
        groupForTransfer.transferTo(centerWithNoChildren);

        // verification
        assertThat(centerWithNoChildren.getMaxChildCount(), is(1));
        assertThat(fromCenter.getMaxChildCount(), is(1));
    }

    @Test
    public void transferingGroupToCenterInSameBranchCreatesActiveCustomerHierarchyBetweenGroupAndNewParent() throws Exception {

        CenterBO fromCenter = new CenterBuilder().withName("fromCenter").build();
        GroupBO groupForTransfer = new GroupBuilder().withParentCustomer(fromCenter).active().build();
        fromCenter.addChild(groupForTransfer);

        CenterBO receivingCenter = new CenterBuilder().withName("receivingCenter").build();

        // pre-verification
        assertThat(receivingCenter.getActiveCustomerHierarchy(), is(nullValue()));

        // exercise test
        groupForTransfer.transferTo(receivingCenter);

        // verification
        assertThat(groupForTransfer.getActiveCustomerHierarchy(), is(notNullValue()));
        assertThat(fromCenter.getActiveCustomerHierarchy(), is(nullValue()));
    }

    @Test
    public void transferingGroupToCenterInSameBranchShouldModifyGroupToHaveSameLoanOfficerAsReceivingCenter() throws Exception {

        PersonnelBO loanOfficer = new PersonnelBuilder().withName("old loan officer").build();
        CenterBO fromCenter = new CenterBuilder().withName("fromCenter").withLoanOfficer(loanOfficer).build();
        GroupBO groupForTransfer = new GroupBuilder().withParentCustomer(fromCenter).active().build();

        PersonnelBO newLoanOfficer = new PersonnelBuilder().withName("loan officer").build();
        CenterBO receivingCenter = new CenterBuilder().withName("receivingCenter").withLoanOfficer(newLoanOfficer).build();

        // pre-verification
        assertThat(receivingCenter.getPersonnel().getDisplayName(), is("loan officer"));
        assertThat(groupForTransfer.getPersonnel().getDisplayName(), is("old loan officer"));

        // exercise test
        groupForTransfer.transferTo(receivingCenter);

        // verification
        assertThat(receivingCenter.getPersonnel().getDisplayName(), is("loan officer"));
        assertThat(groupForTransfer.getPersonnel().getDisplayName(), is("loan officer"));
    }

}