/*
 * Copyright Grameen Foundation USA
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

package org.mifos.ui.core.controller;

import java.io.Serializable;
import java.util.List;

import org.mifos.dto.screen.ListElement;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="required for spring web flow storage at a minimum - should disable at filter level and also for pmd")
public class OfficeHierarchyFormBean implements Serializable {

    private List<BranchOnlyHierarchyBean> branchOnlyOfficeHierarchy;
    private List<ListElement> nonBranches;

    public List<ListElement> getNonBranches() {
        return this.nonBranches;
    }

    public void setNonBranches(List<ListElement> nonBranches) {
        this.nonBranches = nonBranches;
    }

    public List<BranchOnlyHierarchyBean> getBranchOnlyOfficeHierarchy() {
        return this.branchOnlyOfficeHierarchy;
    }

    public void setBranchOnlyOfficeHierarchy(List<BranchOnlyHierarchyBean> branchOnlyOfficeHierarchy) {
        this.branchOnlyOfficeHierarchy = branchOnlyOfficeHierarchy;
    }
}