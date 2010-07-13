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

package org.mifos.ui.core.controller;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EQ_UNUSUAL", justification="using commons equals builder")
public class ViewOfficeHierarchyFormBean {

    private Boolean headOffice = false;

    private Boolean branchOffice = false;

    private Boolean regionalOffice = false;

    private Boolean subRegionalOffice = false;

    private Boolean areaOffice = false;

    public Boolean getHeadOffice() {
        return this.headOffice;
    }

    public void setHeadOffice(Boolean headOffice) {
        this.headOffice = headOffice;
    }

    public Boolean getBranchOffice() {
        return this.branchOffice;
    }

    public void setBranchOffice(Boolean branchOffice) {
        this.branchOffice = branchOffice;
    }

    public Boolean getRegionalOffice() {
        return this.regionalOffice;
    }

    public void setRegionalOffice(Boolean regionalOffice) {
        this.regionalOffice = regionalOffice;
    }

    public Boolean getSubRegionalOffice() {
        return this.subRegionalOffice;
    }

    public void setSubRegionalOffice(Boolean subRegionalOffice) {
        this.subRegionalOffice = subRegionalOffice;
    }

    public Boolean getAreaOffice() {
        return this.areaOffice;
    }

    public void setAreaOffice(Boolean areaOffice) {
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