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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EQ_UNUSUAL", justification="using commons equals builder")
public class ViewOfficeHierarchyFormBean {

    private boolean headOffice = false;
    private boolean regionalOffice = false;
    private boolean subRegionalOffice = false;
    private boolean areaOffice = false;
    private boolean branchOffice = false;

    public boolean isHeadOffice() {
        return this.headOffice;
    }

    public void setHeadOffice(boolean headOffice) {
        this.headOffice = headOffice;
    }

    public boolean isBranchOffice() {
        return this.branchOffice;
    }

    public void setBranchOffice(boolean branchOffice) {
        this.branchOffice = branchOffice;
    }

    public boolean isRegionalOffice() {
        return this.regionalOffice;
    }

    public void setRegionalOffice(boolean regionalOffice) {
        this.regionalOffice = regionalOffice;
    }

    public boolean isSubRegionalOffice() {
        return this.subRegionalOffice;
    }

    public void setSubRegionalOffice(boolean subRegionalOffice) {
        this.subRegionalOffice = subRegionalOffice;
    }

    public boolean isAreaOffice() {
        return this.areaOffice;
    }

    public void setAreaOffice(boolean areaOffice) {
        this.areaOffice = areaOffice;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().reflectionHashCode(this);
    }
}