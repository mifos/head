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

package org.mifos.application.admin.servicefacade;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.hibernate.HibernateException;
import org.mifos.application.admin.system.SystemInfo;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.reports.struts.action.BirtReportsUploadAction;

/**
 *
 */
public class SystemInformationServiceFacadeWebTier implements SystemInformationServiceFacade {

    @Override
    public SystemInformationDto getSystemInformation(ServletContext context, Locale locale) {

        try {
            DatabaseMetaData metaData = StaticHibernateUtil.getSessionTL().connection().getMetaData();

            final SystemInfo systemInfo = new SystemInfo(metaData, context, locale, true);
            systemInfo.setCustomReportsDir(BirtReportsUploadAction.getCustomReportStorageDirectory());

            return new SystemInformationDto(
                    systemInfo.getApplicationServerInfo(),
                    systemInfo.getApplicationVersion(),
                    systemInfo.getBuildDate(),
                    systemInfo.getBuildNumber(),
                    systemInfo.getCommitIdentifier(),
                    systemInfo.getCustomReportsDir(),
                    systemInfo.getDatabaseName(),
                    systemInfo.getDatabasePort(),
                    systemInfo.getDatabaseServer(),
                    systemInfo.getDatabaseUser(),
                    systemInfo.getDatabaseVendor(),
                    systemInfo.getDatabaseVersion(),
                    systemInfo.getDriverName(),
                    systemInfo.getDriverVersion(),
                    systemInfo.getDateTimeString(),
                    systemInfo.getDateTimeStringIso8601(),
                    systemInfo.getInfoSource(),
                    systemInfo.getInfoURL(),
                    systemInfo.getJavaVendor(),
                    systemInfo.getJavaVersion(),
                    systemInfo.getOsArch(),
                    systemInfo.getOsName(),
                    systemInfo.getOsUser(),
                    systemInfo.getOsVersion(),
                    systemInfo.getReleaseName());

        } catch (HibernateException e) {
            throw new MifosRuntimeException(e);
        } catch (SQLException e) {
            throw new MifosRuntimeException(e);
        }
    }
}