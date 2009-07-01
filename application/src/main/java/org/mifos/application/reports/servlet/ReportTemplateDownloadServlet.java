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

package org.mifos.application.reports.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.application.admindocuments.business.AdminDocumentBO;
import org.mifos.application.reports.business.ReportsBO;
import org.mifos.framework.util.helpers.StringUtils;

public class ReportTemplateDownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String realPath = request.getParameter("realPath");
        boolean isReportAnAdminDocument = false;
        if (StringUtils.isNullOrEmpty(realPath))
            realPath = "report";
        else
            isReportAnAdminDocument = true;

        File dir = new File(getServletContext().getRealPath("/") + realPath);
        String reportFileName = "";
        if (isReportAnAdminDocument)
            reportFileName = ((AdminDocumentBO) request.getSession().getAttribute("reportsBO"))
                    .getAdminDocumentIdentifier();
        else
            reportFileName = ((ReportsBO) request.getSession().getAttribute("reportsBO")).getReportsJasperMap()
                    .getReportJasper();

        File file = new File(dir, reportFileName);

        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
        response.setContentType("application/x-msdownload;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + reportFileName);
        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = is.read(buffer, 0, 4096)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        is.close();
        request.getSession().setAttribute("reportsBO", null);
    }
}
