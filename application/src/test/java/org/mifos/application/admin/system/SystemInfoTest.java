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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.util.DateTimeService;

import servletunit.ServletContextSimulator;

public class SystemInfoTest {

    private SystemInfo info;

    @Before
    public void setUp() throws Exception {
        ServletContext servletContext = new ServletContextSimulator();
        MockDatabaseMetaData metaData = new MockDatabaseMetaData();
        info = new SystemInfo(metaData, servletContext, Locale.US, false);
        info.setJavaVendor("Sun");
        info.setJavaVersion("1.5");
        info.setBuildInformation(new MockSvnRevision());
    }

    @Test
    public void testDatabaseDetails() throws Exception {
        Assert.assertEquals("vendorName", info.getDatabaseVendor());
        Assert.assertEquals("1.0", info.getDatabaseVersion());
        Assert.assertEquals("driverName", info.getDriverName());
        Assert.assertEquals("2.0", info.getDriverVersion());
    }

    @Test
    public void testDatabaseInfos() throws Exception {
        String infoSourceValue = "test";
        info.setInfoSource(infoSourceValue);
        Assert.assertEquals(info.getInfoSource(), infoSourceValue);
        URI full = new URI("jdbc:mysql://localhost:3305/mifos?useUnicode=true&characterEncoding=UTF-8");
        URI mysqlSpecific = new URI(full.getSchemeSpecificPart());
        info.setInfoURL(mysqlSpecific);
        Assert.assertEquals("localhost", info.getDatabaseServer());
        Assert.assertEquals("mifos", info.getDatabaseName());
        Assert.assertEquals("3305", info.getDatabasePort());
        info.setDatabaseUser("mysql");
        Assert.assertEquals("mysql", info.getDatabaseUser());
    }

    @Test
    public void testJava() throws Exception {
        Assert.assertEquals("Sun", info.getJavaVendor());
        Assert.assertEquals("1.5", info.getJavaVersion());
    }

    @Test
    public void testApplicationServer() throws Exception {
        Assert.assertEquals("MockServletEngine/1.9.5", info.getApplicationServerInfo());
    }

    @Test
    public void testGetVersionInfoFromMissingFile() throws Exception {
        try {
            info.setBuildInformation(new VersionInfo("non-existant.file"));
            Assert.fail("Expected RuntimeException.");
        } catch (RuntimeException e) {
            // expected
        }
    }

    @Test
    public void testGetDateTime() {
        DateTime referenceDateTime = new DateTime(2008, 12, 5, 1, 10, 0, 0);
        DateTimeService dateTimeService = new DateTimeService();
        try {
            // set a fixed datetime which is what SystemInfo should get back
            dateTimeService.setCurrentDateTimeFixed(referenceDateTime);
            Assert.assertEquals("System info date time should be from the DateTimeService", referenceDateTime, info
                    .getDateTime());
        } finally {
            dateTimeService.resetToCurrentSystemDateTime();
        }
    }

    @Test
    public void testFriendlyDatabaseVersion() {
        List<Integer> appliedUpgrades = new ArrayList<Integer>();
        appliedUpgrades.add(1277604232);
        appliedUpgrades.add(1277604243);
        appliedUpgrades.add(1277604267);

        List<Integer> releaseUpgrades = new ArrayList<Integer>();
        releaseUpgrades.add(1277604232);
        releaseUpgrades.add(1277604243);
        releaseUpgrades.add(1277604267);

        String releaseSchemaName = "mifos-v1.7.0-schema";

        String databaseVersion = info.getApplicationVersion(appliedUpgrades, releaseUpgrades, releaseSchemaName);
        Assert.assertEquals(releaseSchemaName, databaseVersion);

    }
}
