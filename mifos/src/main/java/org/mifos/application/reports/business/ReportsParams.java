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

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;

/**
 * This class encapsulates the Reports Parameters with Reports Datasource
 */
public class ReportsParams extends BusinessObject {
    private ReportsDataSource reportsDataSource;
    private int parameterId;
    private String name;
    private String type;
    private String classname;
    private String data;
    private String description;
    private int datasourceId;
    private boolean isInUse;

    public ReportsParams() {
        this.reportsDataSource = new ReportsDataSource();
    }

    public ReportsParams(UserContext userContext) {
        super(userContext);
    }

    public int getParameterId() {
        return parameterId;
    }

    public void setParameterId(int parameterId) {
        this.parameterId = parameterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(int datasourceId) {
        this.datasourceId = datasourceId;
    }

    public ReportsDataSource getReportsDataSource() {
        return reportsDataSource;
    }

    public void setReportsDataSource(ReportsDataSource reportsDataSource) {
        this.reportsDataSource = reportsDataSource;
    }

    public boolean isInUse() {
        return isInUse;
    }

    public void setIsInUse(boolean isInUse) {
        this.isInUse = isInUse;
    }

    public Short getEntityID() {
        return null;
    }

}
