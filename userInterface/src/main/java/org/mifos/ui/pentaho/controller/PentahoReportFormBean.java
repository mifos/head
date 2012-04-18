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
package org.mifos.ui.pentaho.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.reports.pentaho.params.PentahoDateParameter;
import org.mifos.reports.pentaho.params.PentahoMultiSelectParameter;
import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.mifos.reports.pentaho.params.PentahoInputParameter;
import org.mifos.reports.pentaho.params.PentahoSingleSelectParameter;

public class PentahoReportFormBean implements Serializable {

    private static final long serialVersionUID = 1460298190140336560L;

    private Integer reportId;
    private String outputType;
    private Map<String, String> allowedOutputTypes;

    private List<PentahoDateParameter> reportDateParams = new ArrayList<PentahoDateParameter>();
    private List<PentahoInputParameter> reportInputParams = new ArrayList<PentahoInputParameter>();
    private List<PentahoSingleSelectParameter> reportSingleSelectParams = new ArrayList<PentahoSingleSelectParameter>();
    private List<PentahoMultiSelectParameter> reportMultiSelectParams = new ArrayList<PentahoMultiSelectParameter>();

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public Map<String, String> getAllowedOutputTypes() {
        return allowedOutputTypes;
    }

    public void setAllowedOutputTypes(Map<String, String> allowedOutputTypes) {
        this.allowedOutputTypes = allowedOutputTypes;
    }

    public List<PentahoDateParameter> getReportDateParams() {
        return reportDateParams;
    }

    public void setReportDateParams(List<PentahoDateParameter> reportDateParams) {
        this.reportDateParams = reportDateParams;
    }

    public List<PentahoInputParameter> getReportInputParams() {
        return reportInputParams;
    }

    public void setReportInputParams(List<PentahoInputParameter> reportInputParams) {
        this.reportInputParams = reportInputParams;
    }

    public List<PentahoSingleSelectParameter> getReportSingleSelectParams() {
        return reportSingleSelectParams;
    }

    public void setReportSingleSelectParams(List<PentahoSingleSelectParameter> reportSingleSelectParams) {
        this.reportSingleSelectParams = reportSingleSelectParams;
    }

    public List<PentahoMultiSelectParameter> getReportMultiSelectParams() {
        return reportMultiSelectParams;
    }

    public void setReportMultiSelectParams(List<PentahoMultiSelectParameter> reportMultiSelectParams) {
        this.reportMultiSelectParams = reportMultiSelectParams;
    }

    public void setReportParameters(List<AbstractPentahoParameter> params) {
        this.reportDateParams.clear();
        this.reportInputParams.clear();
        this.reportSingleSelectParams.clear();
        this.reportMultiSelectParams.clear();
        for (AbstractPentahoParameter param : params) {
            if (param instanceof PentahoDateParameter) {
                this.reportDateParams.add((PentahoDateParameter) param);
            } else if (param instanceof PentahoInputParameter) {
                this.reportInputParams.add((PentahoInputParameter) param);
            } else if (param instanceof PentahoSingleSelectParameter) {
                this.reportSingleSelectParams.add((PentahoSingleSelectParameter) param);
            } else if (param instanceof PentahoMultiSelectParameter) {
                this.reportMultiSelectParams.add((PentahoMultiSelectParameter) param);
            }
        }
    }

    public Map<String, AbstractPentahoParameter> getAllParameteres() {
        List<AbstractPentahoParameter> allParams = new ArrayList<AbstractPentahoParameter>();

        allParams.addAll(this.reportDateParams);
        allParams.addAll(this.reportInputParams);
        allParams.addAll(this.reportSingleSelectParams);
        allParams.addAll(this.reportMultiSelectParams);

        Map<String, AbstractPentahoParameter> result = new HashMap<String, AbstractPentahoParameter>();
        for (AbstractPentahoParameter param : allParams) {
            result.put(param.getParamName(), param);
        }

        return result;
    }
}
