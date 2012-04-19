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

import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.reports.business.ReportsBO;
import org.mifos.reports.business.ReportsJasperMap;
import org.mifos.reports.pentaho.PentahoReport;
import org.mifos.reports.pentaho.PentahoReportsServiceFacade;
import org.mifos.reports.pentaho.PentahoValidationError;
import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.mifos.reports.pentaho.util.PentahoOutputType;
import org.mifos.reports.pentaho.util.PentahoParamParser;
import org.mifos.reports.pentaho.util.PentahoReportLocator;
import org.mifos.reports.pentaho.util.ReflectionException;
import org.mifos.reports.persistence.ReportsPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.XmlTableReportUtil;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationMessage;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class PentahoReportsServiceImpl implements PentahoReportsServiceFacade {

    private final static Logger logger = LoggerFactory.getLogger(PentahoReportsServiceImpl.class);

    private final ReportsPersistence reportsPersistence = new ReportsPersistence();
    private PentahoParamParser paramParser = new PentahoParamParser();
    private RolesPermissionServiceFacade rolesPermissionService;

    @Autowired
    public void setRolesPermissionService(RolesPermissionServiceFacade rolesPermissionService) {
        this.rolesPermissionService = rolesPermissionService;
    }

    @Override
    public PentahoReport getReport(Integer reportId, Integer outputTypeId, Map<String, AbstractPentahoParameter> params) {
        ByteArrayOutputStream baos = null;

        if (!checkAccessToReport(reportId)) {
            throw new AccessDeniedException("Access denied");
        }

        try {
            String reportFileName = getReportFilename(reportId);
            // load report definition
            ResourceManager manager = new ResourceManager();
            manager.registerDefaults();

            URL url = PentahoReportLocator.getURLForReport(reportFileName);

            Resource res = manager.createDirectly(url, MasterReport.class);
            MasterReport report = (MasterReport) res.getResource();

            PentahoReport result = new PentahoReport();

            List<PentahoValidationError> errors = new ArrayList<PentahoValidationError>();
            try {
                addParametersToReport(report, params);
                validate(report, errors);
            } catch (ReflectionException ex) {
                errors.add(new PentahoValidationError(ex.getMessage()));
            }

            result.setErrors(errors);

            if (errors.isEmpty()) {
                baos = new ByteArrayOutputStream();
                PentahoOutputType outputType = PentahoOutputType.findById(outputTypeId);

                switch (outputType) {
                case XLS:
                    ExcelReportUtil.createXLS(report, baos);
                    break;
                case RTF:
                    RTFReportUtil.createRTF(report, baos);
                    break;
                case HTML:
                    HtmlReportUtil.createStreamHTML(report, baos);
                    break;
                case CSV:
                    CSVReportUtil.createCSV(report, baos, "UTF-8");
                    break;
                case XML:
                    XmlTableReportUtil.createFlowXML(report, baos);
                    break;
                default: // PDF
                    PdfReportUtil.createPDF(report, baos);
                    break;
                }

                result.setContentType(outputType.getContentType());
                result.setFileExtension(outputType.getFileExtension());

                result.setName(getReportName(reportId));
                result.setContent(baos.toByteArray());
            }
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
        if (!checkAccessToReport(reportId)) {
            throw new AccessDeniedException("Access denied");
        }

        String reportName = getReportFilename(reportId);
        MasterReport report = loadReport(reportName);

        return paramParser.parseReportParams(report);
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

    private void validate(MasterReport report, List<PentahoValidationError> errors) throws ReportProcessingException {
        ReportParameterDefinition paramDefinition = report.getParameterDefinition();
        ReportParameterValidator validator = paramDefinition.getValidator();
        ParameterContext paramContext = new DefaultParameterContext(report);

        ValidationResult validationResult = validator.validate(null, paramDefinition, paramContext);
        for (ValidationMessage msg : validationResult.getErrors()) {
            PentahoValidationError error = new PentahoValidationError(msg.getMessage());
            errors.add(error);
        }

        String[] properties = validationResult.getProperties();
        for (String prop : properties) {
            for (ValidationMessage msg : validationResult.getErrors(prop)) {
                PentahoValidationError error = new PentahoValidationError(prop, msg.getMessage());
                errors.add(error);
            }
        }
    }

    @Override
    public boolean checkAccessToReport(Integer reportId) {
        ReportsBO report = this.reportsPersistence.getReport(reportId.shortValue());
        boolean result = false;
        if (report != null) {
            Short activityID = report.getActivityId();
            try {
                result = this.rolesPermissionService.hasUserAccessForActivity(activityID);
            } catch (Exception ex) {
                result = false;
            }
        }
        return result;
    }
}
