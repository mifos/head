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
package org.mifos.reports.pentaho.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.reports.business.ReportsJasperMap;
import org.mifos.reports.pentaho.PentahoReport;
import org.mifos.reports.pentaho.PentahoReportsServiceFacade;
import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.mifos.reports.pentaho.util.PentahoOutputType;
import org.mifos.reports.pentaho.util.PentahoParamParser;
import org.mifos.reports.pentaho.util.PentahoReportLocator;
import org.mifos.reports.pentaho.util.ReflectionException;
import org.mifos.reports.persistence.ReportsPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import org.apache.commons.lang.StringUtils;

public class PentahoReportsServiceImpl implements PentahoReportsServiceFacade {

    private final static Logger logger = LoggerFactory.getLogger(PentahoReportsServiceImpl.class);

    private final ReportsPersistence reportsPersistence = new ReportsPersistence();
    private PentahoParamParser paramParser = new PentahoParamParser();

    @Override
    public PentahoReport getReport(Integer reportId, Integer outputTypeId, Map<String, AbstractPentahoParameter> params) {
        String filename = getReportFilename(reportId);
        return getReport(filename, outputTypeId, params);
    }

    @Override
    public PentahoReport getReport(String reportFileName, Integer outputTypeId, Map<String, AbstractPentahoParameter> params) {
        ByteArrayOutputStream baos = null;
        try {
            // load report definition
            ResourceManager manager = new ResourceManager();
            manager.registerDefaults();

            URL url = PentahoReportLocator.getURLForReport(reportFileName);

            Resource res = manager.createDirectly(url, MasterReport.class);
            MasterReport report = (MasterReport) res.getResource();
            addParametersToReport(report, params);

            PentahoReport result = new PentahoReport();
            baos = new ByteArrayOutputStream();

            PentahoOutputType outputType = PentahoOutputType.findById(outputTypeId);
            switch (outputType) {
            case XLS:
                ExcelReportUtil.createXLS(report, baos);
                break;
            case RTF:
                RTFReportUtil.createRTF(report, baos);
                break;
            default: // PDF
                PdfReportUtil.createPDF(report, baos);
                break;
            }

            result.setContentType(outputType.getContentType());
            result.setFileExtension(outputType.getFileExtension());

            // report name
            if (StringUtils.isBlank(report.getName())) {
                result.setName(reportFileName.replace(".prpt", ""));
            } else {
                result.setName(report.getName());
            }

            result.setContent(baos.toByteArray());
            return result;

        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        } finally {
            closeStream(baos);
        }
    }

    @PostConstruct
    public void init() {
        ClassicEngineBoot.getInstance().start();
    }

    private void closeStream(OutputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                logger.error("Error while closing stream", e);
            }
        }
    }

    @Override
    public String getReportName(Integer reportId) {
        return this.reportsPersistence.getReport((short) reportId.intValue()).getReportName();
    }

    @Override
    public Map<String, String> getReportOutputTypes() {
        Map<String, String> outputTypes = new HashMap<String, String>();
        for (PentahoOutputType outputType : PentahoOutputType.values()) {
            outputTypes.put(String.valueOf(outputType.getId()), outputType.getDisplayName());
        }
        return outputTypes;
    }

    @Override
    public List<AbstractPentahoParameter> getParametersForReport(Integer reportId) {
        List<AbstractPentahoParameter> result = new ArrayList<AbstractPentahoParameter>();

        String reportName = getReportFilename(reportId);
        MasterReport report = loadReport(reportName);

        ReportParameterDefinition paramsDefinition = report.getParameterDefinition();
        for (ParameterDefinitionEntry paramDefEntry : paramsDefinition.getParameterDefinitions()) {
            AbstractPentahoParameter param = paramParser.parseParam(paramDefEntry);
            if (param != null) {
                result.add(param);
            }
        }

        return result;
    }

    private MasterReport loadReport(String reportName) {
        ResourceManager manager = new ResourceManager();
        manager.registerDefaults();

        URL url = PentahoReportLocator.getURLForReport(reportName);
        try {
            Resource res = manager.createDirectly(url, MasterReport.class);
            MasterReport report = (MasterReport) res.getResource();
            return report;
        } catch (Exception e) {
            throw new MifosRuntimeException(e);
        }
    }

    private void addParametersToReport(MasterReport report, Map<String, AbstractPentahoParameter> params)
            throws ReflectionException {
        ReportParameterValues rptParamValues = report.getParameterValues();
        ReportParameterDefinition paramsDefinition = report.getParameterDefinition();

        for (ParameterDefinitionEntry paramDefEntry : paramsDefinition.getParameterDefinitions()) {
            String paramName = paramDefEntry.getName();
            AbstractPentahoParameter parameter = params.get(paramName);

            if (parameter != null) {
                Object val = this.paramParser.parseParamValue(parameter, paramDefEntry);
                if (val != null && (!val.getClass().isArray() || Array.getLength(val) > 0)) {
                    rptParamValues.put(paramName, val);
                }
            }
        }
    }

    private String getReportFilename(Integer reportId) {
        try {
            List<ReportsJasperMap> reports = this.reportsPersistence.findJasperOfReportId(reportId);
            if (reports.isEmpty()) {
                throw new MifosRuntimeException("Report doesn't contain a report file");
            } else {
                return reports.get(0).getReportJasper();
            }
        } catch (PersistenceException ex) {
            logger.error("Reports persistence exception", ex);
            throw new MifosRuntimeException(ex);
        }
    }
}
