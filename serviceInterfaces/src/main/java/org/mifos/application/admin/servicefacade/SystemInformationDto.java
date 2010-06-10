/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

@SuppressWarnings("PMD.TooManyFields")
public class SystemInformationDto {

    private final String applicationServerInfo;
    private final int    applicationVersion;
    private final String buildDate;
    private final String buildNumber;
    private final String commitIdentifier;
    private final String customReportsDirectory;
    private final String databaseName;
    private final String databasePort;
    private final String databaseServer;
    private final String databaseUser;
    private final String databaseVendor;
    private final String databaseVersion;
    private final String databaseDriverName;
    private final String databaseDriverVersion;
    private final String dateTimeString;
    private final String dateTimeStringIso8601;
    private final String infoSource;
    private final String infoURL;
    private final String javaVendor;
    private final String javaVersion;
    private final String osArch;
    private final String osName;
    private final String osUser;
    private final String osVersion;
    private final String releaseName;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public SystemInformationDto(String applicationServerInfo, int applicationVersion, String buildDate,
            String buildNumber, String commitIdentifier, String customReportsDirectory, String databaseName,
            String databasePort, String databaseServer, String databaseUser, String databaseVendor,
            String databaseVersion, String databaseDriverName, String databaseDriverVersion, String dateTimeString,
            String dateTimeStringIso8601, String infoSource, String infoURL, String javaVendor, String javaVersion,
            String osArch, String osName, String osUser, String osVersion, String releaseName) {
        super();
        this.applicationServerInfo = applicationServerInfo;
        this.applicationVersion = applicationVersion;
        this.buildDate = buildDate;
        this.buildNumber = buildNumber;
        this.commitIdentifier = commitIdentifier;
        this.customReportsDirectory = customReportsDirectory;
        this.databaseName = databaseName;
        this.databasePort = databasePort;
        this.databaseServer = databaseServer;
        this.databaseUser = databaseUser;
        this.databaseVendor = databaseVendor;
        this.databaseVersion = databaseVersion;
        this.databaseDriverName = databaseDriverName;
        this.databaseDriverVersion = databaseDriverVersion;
        this.dateTimeString = dateTimeString;
        this.dateTimeStringIso8601 = dateTimeStringIso8601;
        this.infoSource = infoSource;
        this.infoURL = infoURL;
        this.javaVendor = javaVendor;
        this.javaVersion = javaVersion;
        this.osArch = osArch;
        this.osName = osName;
        this.osUser = osUser;
        this.osVersion = osVersion;
        this.releaseName = releaseName;
    }

    public String getApplicationServerInfo() {
        return this.applicationServerInfo;
    }
    public int getApplicationVersion() {
        return this.applicationVersion;
    }
    public String getBuildDate() {
        return this.buildDate;
    }
    public String getBuildNumber() {
        return this.buildNumber;
    }
    public String getCommitIdentifier() {
        return this.commitIdentifier;
    }
    public String getCustomReportsDirectory() {
        return this.customReportsDirectory;
    }
    public String getDatabaseName() {
        return this.databaseName;
    }
    public String getDatabasePort() {
        return this.databasePort;
    }
    public String getDatabaseServer() {
        return this.databaseServer;
    }
    public String getDatabaseUser() {
        return this.databaseUser;
    }
    public String getDatabaseVendor() {
        return this.databaseVendor;
    }
    public String getDatabaseVersion() {
        return this.databaseVersion;
    }
    public String getDatabaseDriverName() {
        return this.databaseDriverName;
    }
    public String getDatabaseDriverVersion() {
        return this.databaseDriverVersion;
    }
    public String getDateTimeString() {
        return this.dateTimeString;
    }
    public String getDateTimeStringIso8601() {
        return this.dateTimeStringIso8601;
    }
    public String getInfoSource() {
        return this.infoSource;
    }
    public String getInfoURL() {
        return this.infoURL;
    }
    public String getJavaVendor() {
        return this.javaVendor;
    }
    public String getJavaVersion() {
        return this.javaVersion;
    }
    public String getOsArch() {
        return this.osArch;
    }
    public String getOsName() {
        return this.osName;
    }
    public String getOsUser() {
        return this.osUser;
    }
    public String getOsVersion() {
        return this.osVersion;
    }
    public String getReleaseName() {
        return this.releaseName;
    }

}

