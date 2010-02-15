/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.application.reports.business;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
/**
 * This class encapsulates the Reports Parameters Map with Reports Parameter
 */
public class ReportsParamsMapValue implements Serializable {
    private int parameterId;
    private int reportId;
    private int mapId;

    public ReportsParamsMapValue() {
    }

    public ReportsParamsMapValue(int reportId, int parameterId) {
        this.reportId = reportId;
        this.parameterId = parameterId;
    }

    @Id
    @GeneratedValue
    @Column(name = "map_id", nullable = false)
    public int getMapId() {
        return this.mapId;
    }

    public void setMapId(int mapId) {
        this.mapId = mapId;
    }

    @Column(name = "report_id")
    public int getReportId() {
        return this.reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    @Column(name = "parameter_id")
    public int getParameterId() {
        return this.parameterId;
    }

    public void setParameterId(int parameterId) {
        this.parameterId = parameterId;
    }

}
