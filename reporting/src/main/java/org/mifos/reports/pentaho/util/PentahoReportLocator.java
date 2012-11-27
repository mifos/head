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
package org.mifos.reports.pentaho.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.core.MifosRuntimeException;

public class PentahoReportLocator {

    public static URL getURLForReport(String reportFileName) {
        if (StringUtils.isBlank(reportFileName)) {
            throw new IllegalArgumentException("Report name not specified");
        }

        URL url = getReportURLFromUploads(reportFileName);
        if (url == null) {
            url = getReportURLFromClasspath(reportFileName);
        }
        return url;
    }

    public static URL getReportURLFromClasspath(String reportFileName) {
        String reportPath = "pentaho/" + reportFileName;
        return getClassLoader().getResource(reportPath);
    }

    public static URL getReportURLFromUploads(String reportName) {
        String reportPath = getPathToUploadedReport(reportName);
        try {
            URL url = new URL(reportPath);
            return (new File(url.getPath()).exists()) ? url : null;
        } catch (MalformedURLException ex) {
            throw new MifosRuntimeException(ex);
        }
    }

    private static String getPathToUploadedReport(String reportFileName) {
        String uploadsDir = MifosConfigurationManager.getInstance().getString("GeneralConfig.UploadStorageDirectory",
                "$HOME/.mifos/uploads");
        StringBuilder sb;
        if (File.separatorChar == '\\') { // windows platform
            uploadsDir = uploadsDir.replaceAll("/", "\\\\");
            sb = new StringBuilder("file:/");
        }
        else {
            sb = new StringBuilder("file://");
        }

        uploadsDir = uploadsDir.replace("$HOME", System.getProperty("user.home"));
        sb.append(uploadsDir);

        if (!uploadsDir.endsWith(File.separator)) {
            sb.append(File.separator);
        }

        sb.append("report").append(File.separator).append(reportFileName);

        return sb.toString();
    }

    private static ClassLoader getClassLoader() {
        return PentahoReportLocator.class.getClassLoader();
    }
}
