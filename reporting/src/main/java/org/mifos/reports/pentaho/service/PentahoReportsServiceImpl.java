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
import java.net.URL;
import java.util.List;

import javax.annotation.PostConstruct;

import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.reports.business.ReportsJasperMap;
import org.mifos.reports.pentaho.PentahoReport;
import org.mifos.reports.pentaho.PentahoReportsServiceFacade;
import org.mifos.reports.pentaho.util.PentahoReportLocator;
import org.mifos.reports.persistence.ReportsPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.RTFReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.ExcelReportUtil;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import org.apache.commons.lang.StringUtils;

public class PentahoReportsServiceImpl implements PentahoReportsServiceFacade {

    private final static Logger logger = LoggerFactory.getLogger(PentahoReportsServiceFacade.class);

    private final static String PDF_CONTENT_TYPE = "application/pdf";
    private final static String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
    private final static String RTF_CONTENT_TYPE = "application/rtf";

    private final static String PDF_EXTENSTION = "pdf";
    private final static String XLS_EXTENSION = "xls";
    private final static String RTF_EXTENSION = "rtf";

    private ReportsPersistence reportsPersistence = new ReportsPersistence();

    @Override
    public PentahoReport getReport(Integer reportId, String outputType) {
        try {
            List<ReportsJasperMap> reports = this.reportsPersistence.findJasperOfReportId(reportId);
            if (!reports.isEmpty()) {
                String filename = reports.get(0).getReportJasper();
                return getReport(filename, outputType);
            } else {
                throw new MifosRuntimeException("Report doesn't contain a report file");
            }
        } catch (PersistenceException ex) {
            logger.error("Reports persistence exception", ex);
            throw new MifosRuntimeException(ex);
        }
    }

    @Override
    public PentahoReport getReport(String reportName, String outputType) {
        ByteArrayOutputStream baos = null;
        try {
            // load report definition
            ResourceManager manager = new ResourceManager();
            manager.registerDefaults();

            URL url = PentahoReportLocator.getURLForReport(reportName);

            Resource res = manager.createDirectly(url, MasterReport.class);
            MasterReport report = (MasterReport) res.getResource();

            PentahoReport result = new PentahoReport();

            baos = new ByteArrayOutputStream();
            if (outputType != null && "PDF".equals(outputType.toUpperCase())) {
                PdfReportUtil.createPDF(report, baos);
                result.setContentType(PDF_CONTENT_TYPE);
                result.setFileExtenstion(PDF_EXTENSTION);
            } else if (outputType != null && "XLS".equals(outputType.toUpperCase())) {
                ExcelReportUtil.createXLS(report, baos);
                result.setContentType(XLS_CONTENT_TYPE);
                result.setFileExtenstion(XLS_EXTENSION);
            } else {
                RTFReportUtil.createRTF(report, baos);
                result.setContentType(RTF_CONTENT_TYPE);
                result.setFileExtenstion(RTF_EXTENSION);
            }

            // report name
            if (StringUtils.isBlank(report.getName())) {
                result.setName(reportName.replace(".prpt", ""));
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
}
