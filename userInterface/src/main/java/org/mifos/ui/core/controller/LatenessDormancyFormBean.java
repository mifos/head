/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
public class LatenessDormancyFormBean {

    private Integer latenessDays = Integer.valueOf(0);
    private Integer dormancyDays = Integer.valueOf(0);

    public Integer getLatenessDays() {
        return this.latenessDays;
    }

    public void setLatenessDays(Integer latenessDays) {
        this.latenessDays = latenessDays;
    }

    public Integer getDormancyDays() {
        return this.dormancyDays;
    }

    public void setDormancyDays(Integer dormancyDays) {
        this.dormancyDays = dormancyDays;
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