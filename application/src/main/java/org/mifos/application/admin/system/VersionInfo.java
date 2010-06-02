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

package org.mifos.application.admin.system;

import java.io.IOException;
import java.util.Properties;

import org.springframework.core.io.ClassPathResource;

public class VersionInfo extends Properties {
    private static final String COMMIT_IDENTIFIER = "versioninfo.commitIdentifier";
    private static final String BUILD_NUMBER = "versioninfo.buildNumber";
    private static final String BUILD_DATE = "versioninfo.buildDate";

    public VersionInfo() {
        this("org/mifos/config/resources/versionInfo.properties");
    }

    public VersionInfo(String versionFile) {
        if (versionFile != null) {
            readVersionFile(versionFile);
        }
    }

    private void readVersionFile(String versionFile) {
        try {
            load(new ClassPathResource(versionFile).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCommitIdentifier() {
        return getProperty(COMMIT_IDENTIFIER);
    }

    public String getBuildNumber() {
        return getProperty(BUILD_NUMBER);
    }

    public String getBuildDate() {
        return getProperty(BUILD_DATE);
    }
}
